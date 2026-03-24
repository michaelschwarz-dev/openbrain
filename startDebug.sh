#!/bin/bash

java -jar target/nodebrain.jar -D-agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=*:5005
