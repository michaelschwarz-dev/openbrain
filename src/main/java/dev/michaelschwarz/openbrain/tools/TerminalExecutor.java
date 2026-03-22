package dev.michaelschwarz.openbrain.tools;

import jakarta.enterprise.context.ApplicationScoped;
import org.apache.commons.exec.CommandLine;
import org.apache.commons.exec.DefaultExecutor;
import org.apache.commons.exec.ExecuteWatchdog;
import org.apache.commons.exec.PumpStreamHandler;

import java.io.ByteArrayOutputStream;
import java.nio.charset.StandardCharsets;

@ApplicationScoped
public class TerminalExecutor {
    private static final int TIMEOUT_MS = 100_000; // 100 Sekunden

    public String execute(String command) {
        try {
            var cmdLine = buildCommandLine(command);
            return runCommand(cmdLine);
        } catch (Exception e) {
            return "Kritischer Fehler bei der Befehlsausführung: " + e.getMessage();
        }
    }

    private CommandLine buildCommandLine(String command) {
        CommandLine cmdLine;
        if (System.getProperty("os.name").toLowerCase().contains("win")) {
            cmdLine = CommandLine.parse("cmd.exe");
            cmdLine.addArgument("/c");
        } else {
            cmdLine = new CommandLine("bash");
            cmdLine.addArgument("-c");
        }
        cmdLine.addArgument(command, false);
        return cmdLine;
    }

    private String runCommand(CommandLine cmdLine) throws Exception {
        var executor = new DefaultExecutor();
        executor.setWatchdog(new ExecuteWatchdog(TIMEOUT_MS));

        var outputStream = new ByteArrayOutputStream();
        var errorStream = new ByteArrayOutputStream();
        executor.setStreamHandler(new PumpStreamHandler(outputStream, errorStream));

        var exitValue = executor.execute(cmdLine);
        return extractResult(outputStream, errorStream, exitValue);
    }

    private String extractResult(ByteArrayOutputStream outputStream, ByteArrayOutputStream errorStream, int exitValue) {
        var result = outputStream.toString(StandardCharsets.UTF_8).trim();
        var error = errorStream.toString(StandardCharsets.UTF_8).trim();

        if (!error.isEmpty() && exitValue != 0) {
            return "Fehler-Output (Exit Code " + exitValue + "): " + error;
        }
        return result.isEmpty() ? "Befehl erfolgreich ausgeführt, aber keine Ausgabe." : result;
    }
}