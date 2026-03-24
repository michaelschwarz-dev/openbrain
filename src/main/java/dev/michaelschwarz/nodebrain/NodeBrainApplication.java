package dev.michaelschwarz.nodebrain;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import dev.michaelschwarz.nodebrain.llm.ChatContext;
import dev.michaelschwarz.nodebrain.llm.OllamaClient;
import dev.michaelschwarz.nodebrain.tools.TerminalExecutor;
import dev.michaelschwarz.nodebrain.tools.ToolDefinitionFactory;
import dev.michaelschwarz.nodebrain.tui.ConsoleUi;
import jakarta.inject.Inject;
import org.fusesource.jansi.AnsiConsole;
import picocli.CommandLine;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

@CommandLine.Command(
        name = "nodebrain",
        mixinStandardHelpOptions = true, // Fügt automatisch -h, --help und -V, --version hinzu
        version = "nodebrain 0.1.0",
        description = "NodeBrain AI Assistent"
)
public class NodeBrainApplication implements Runnable {

    private final ConsoleUi ui;
    private final TerminalExecutor executor;
    private final OllamaClient client;
    private final ChatContext context;
    private final ArrayNode tools;

    @Inject
    public NodeBrainApplication(ConsoleUi ui, TerminalExecutor executor, OllamaClient client, ChatContext context, ToolDefinitionFactory toolDefinitionFactory) {
        this.ui = ui;
        this.executor = executor;
        this.client = client;
        this.context = context;
        this.tools = toolDefinitionFactory.createRunTerminalTool();

        initSystemPrompt();
    }

    private void initSystemPrompt() {
        try {
            var prompt = Files.readString(Path.of("src/main/resources/systemprompt.md"));
            context.addSystemMessage(prompt);
        } catch (IOException e) {
            ui.printError("Systemprompt konnte nicht geladen werden: " + e.getMessage());
            System.exit(1);
        }
    }

    @Override
    public void run() {
        AnsiConsole.systemInstall();
        while (true) {
            var userInput = ui.readUserInput();

            if (userInput == null || userInput.equalsIgnoreCase("exit")) {
                break;
            }
            if (userInput.trim().isEmpty()) {
                continue;
            }

            context.addUserMessage(userInput);
            runAgenticLoop();
        }
        AnsiConsole.systemUninstall();
        ui.close();
    }

    private void runAgenticLoop() {
        var taskCompleted = false;

        while (!taskCompleted) {
            try {
                // Client aufrufen und Callback für den Stream übergeben
                var result = client.sendChatRequest(context, tools, ui::printAgentChunk);
                if (!result.content().isEmpty()) {
                    ui.printNewLine();
                }

                // Antwort in den Kontext aufnehmen
                context.addAssistantMessage(result.content(), result.toolCalls());

                if (result.hasToolCalls()) {
                    handleTools(result.toolCalls());
                } else {
                    taskCompleted = true; // Keine Tools mehr? Loop beenden.
                }

            } catch (Exception e) {
                ui.printError(e.getMessage());
                taskCompleted = true; // Bei Fehler sicherheitshalber abbrechen
            }
        }
    }

    private void handleTools(JsonNode toolCalls) {
        for (var toolCall : toolCalls) {
            var function = toolCall.get("function");
            var functionName = function.get("name").asText();

            if ("run_terminal".equals(functionName)) {
                var command = function.get("arguments").get("command").asText();
                ui.printSystem("🛠️ " + command);
                ui.printNewLine();

                var terminalResult = executor.execute(command);

                context.addToolResultMessage(functionName, terminalResult);
            }
        }
    }
}