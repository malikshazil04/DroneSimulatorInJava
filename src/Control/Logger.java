package Control;

import Core.Config;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class Logger {
    private final File directory;
    private final FileWriter logWriter;
    private final FileWriter metricsWriter;

    public Logger(File directory) {
        if (directory == null) {
            throw new IllegalArgumentException("directory cannot be null");
        }
        this.directory = directory;
        if (!directory.exists()) {
            directory.mkdirs();
        }
        try {
            logWriter = new FileWriter(new File(directory, "simulation.txt"), true);
            metricsWriter = new FileWriter(new File(directory, "metrics.txt"), false);
        } catch (IOException e) {
            throw new RuntimeException("Cannot create output files");
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
            logWriter.write(message + "\n");
            logWriter.flush();
        } catch (IOException e) {
            System.out.println("Logger write failed");
        }
    }

    public void writeMetric(String line) {
        if (line == null)
            return;

        try {
            metricsWriter.write(line + "\n");
            metricsWriter.flush();
        } catch (IOException e) {
            System.out.println("metrics write failed");
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
        try {
            if (logWriter != null)
                logWriter.close();
            if (metricsWriter != null)
                metricsWriter.close();
        } catch (IOException e) {
            System.out.println("logger close failed");
        }
    }
}