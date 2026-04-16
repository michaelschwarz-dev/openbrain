# Knotenhirn

Knotenhirn is a Model Context Protocol (MCP) server that gives LLMs a persistent, structured long-term memory backed by a Neo4j graph
database.

It exposes two MCP tools — `storeRecords` and `queryMemory` — allowing an LLM to write knowledge into a typed graph and retrieve it via
Cypher queries with fulltext search.

## Architecture & Tech Stack

* **Framework:** Java / Quarkus (uber-jar)
* **Database:** Neo4j 5 with APOC plugin
* **API Protocol:** Model Context Protocol (MCP) via `quarkus-mcp` (Streamable HTTP)
* **Search:** Neo4j fulltext indexes

## Data Model

### Node Types

| Label        | Properties      | Purpose                                  |
|--------------|-----------------|------------------------------------------|
| `Topic`      | `name`, `id`    | Category / namespace (singleton by name) |
| `Fact`       | `content`, `id` | Objective, verifiable data               |
| `Principle`  | `content`, `id` | Rules, guidelines, preferences           |
| `Experience` | `content`, `id` | Lessons learned, solutions to problems   |

### Relationships

| Type         | Direction                               | Purpose                         |
|--------------|-----------------------------------------|---------------------------------|
| `BELONGS_TO` | `Fact\|Principle\|Experience` → `Topic` | Assigns a record to a topic     |
| `RELATED_TO` | `Any` → `Any`                           | Cross-reference between records |
| `SUPERSEDES` | `Any` → `Any`                           | Marks a node as replaced by a newer one |

### Indexes (auto-created on startup)

| Index     | Type     | Target                                                               |
|-----------|----------|----------------------------------------------------------------------|
| `content` | Fulltext | `Principle\|Experience\|Fact` → `content`                            |
| `topic`   | Fulltext | `Topic` → `name`                                                     |
| —         | Standard | `Topic.name`, `Topic.id`, `Fact.id`, `Principle.id`, `Experience.id` |

## MCP Tools

### `storeRecords`

Stores one or more knowledge records. Each record requires:

| Field          | Type         | Description                                             |
|----------------|--------------|---------------------------------------------------------|
| `topic`           | String       | Topic name (auto-created via MERGE, case-sensitive)                     |
| `nodeType`        | Enum         | `FACT`, `PRINCIPLE`, or `EXPERIENCE`                                    |
| `content`         | String       | The knowledge text                                                      |
| `relatedNodes`    | List\<UUID\> | Optional IDs of existing nodes to link via `RELATED_TO`                 |
| `supersededNodes` | List\<UUID\> | Optional IDs of nodes this record replaces — creates `SUPERSEDES` edges |

Returns a list of UUID strings for the created nodes.

**Rule:** Always call `queryMemory` first to avoid duplicates and retrieve IDs for `relatedNodes`.

### `queryMemory`

Executes any Cypher query against the graph and returns newline-separated results.

**Common queries:**

```cypher
// All topics
MATCH (t:Topic) RETURN t.name

// All records on a topic
MATCH (t:Topic {name: "Java"})<-[:BELONGS_TO]-(n)
RETURN labels(n)[0] AS type, n.content, n.id

// Fulltext search across records
CALL db.index.fulltext.queryNodes("content", "search term") YIELD node, score
WHERE score > 0.8
MATCH (node)-[:BELONGS_TO]->(t:Topic)
RETURN node.content, t.name, score

// Fulltext search across topics
CALL db.index.fulltext.queryNodes("topic", "search term") YIELD node, score
WHERE score > 0.8
RETURN node.name
```

## Local Development

### Prerequisites

* Java 21+
* Docker & Docker Compose

### 1. Start Neo4j

```bash
docker compose up -d
```

Neo4j Browser: `http://localhost:7474` (user: `neo4j`, password: `knotenhirn`)

### 2. Run the Application

```bash
mvn quarkus:dev
```

The MCP server listens on port `8080`.

### 3. Build an Uber-JAR

```bash
mvn package
```
