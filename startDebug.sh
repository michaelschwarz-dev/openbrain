#!/bin/bash

java -jar target/openbrain.jar -D-agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=*:5005
