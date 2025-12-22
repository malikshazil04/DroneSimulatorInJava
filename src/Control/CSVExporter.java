package Control;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import Core.Vector3;
import physics.Drone;

public class CSVExporter {
    private final File file;

    public CSVExporter(File directory) {
        if (directory == null) {
            throw new IllegalArgumentException("directory cannot be null");
        }
        if (!directory.exists()) {
            directory.mkdirs();
        }

        this.file = new File(directory, "positions.csv");
        try {
            String header = "step,droneId,px,py,pz,vx,vy,vz,thrustZ\n";
            Files.write(file.toPath(), header.getBytes(),
                    StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
        } catch (IOException e) {
            System.err.println("CRITICAL: Cannot create positions.csv: " + e.getMessage());
        }
    }

    public void writeRow(int step, Drone d, double thrustZ) {
        try {
            Vector3 p = d.getPosition();
            Vector3 v = d.getVelocity();

            String row = String.format("%d,%d,%.4f,%.4f,%.4f,%.4f,%.4f,%.4f,%.4f\n",
                    step, d.getId(), p.x, p.y, p.z, v.x, v.y, v.z, thrustZ);

            Files.write(file.toPath(), row.getBytes(),
                    StandardOpenOption.CREATE, StandardOpenOption.APPEND);

        } catch (IOException e) {
            System.err.println("CSV write failed: " + e.getMessage());
        }
    }

    public void close() {
        // No persistent writers to close
    }
}