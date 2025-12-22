package Control;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import Core.Config;

public class Logger {
    private final File directory;
    private final File logFile;
    private final File metricsFile;

    public Logger(File directory) {
        if (directory == null) {
            throw new IllegalArgumentException("directory cannot be null");
        }
        this.directory = directory;
        if (!directory.exists()) {
            directory.mkdirs();
        }
        this.logFile = new File(directory, "simulation.txt");
        this.metricsFile = new File(directory, "metrics.txt");

        // Clear metrics file on start
        try {
            Files.write(metricsFile.toPath(), new byte[0], StandardOpenOption.CREATE,
                    StandardOpenOption.TRUNCATE_EXISTING);
        } catch (IOException e) {
            System.err.println("Failed to initialize metrics file: " + e.getMessage());
        }
    }

    public File getDirectory() {
        return directory;
    }

    public void log(String message) {
        if (message == null)
            return;

        System.out.println(message);

        try {
            Files.write(logFile.toPath(), (message + "\n").getBytes(),
                    StandardOpenOption.CREATE, StandardOpenOption.APPEND);
        } catch (IOException e) {
            System.err.println("Logger write failed: " + e.getMessage());
        }
    }

    public void writeMetric(String line) {
        if (line == null)
            return;

        try {
            Files.write(metricsFile.toPath(), (line + "\n").getBytes(),
                    StandardOpenOption.CREATE, StandardOpenOption.APPEND);
        } catch (IOException e) {
            System.err.println("Metrics write failed: " + e.getMessage());
        }
    }

    public void writeParameters(Config config) {
        writeMetric("        PARAMETERS USED       ");
        writeMetric("dt=" + config.dt);
        writeMetric("totalTime=" + config.totalTime);
        writeMetric("dragK=" + config.dragK);
        writeMetric("commRange=" + config.commRange);
        writeMetric("pLoss=" + config.pLoss);
        writeMetric("formationSpacing=" + config.formationSpacing);
        writeMetric("collisionSafeDistance=" + config.collisionSafeDistance);
    }

    public void close() {
        // No persistent writers to close with Files.write approach
    }
}