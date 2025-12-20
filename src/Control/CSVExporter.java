package Control;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import Core.Vector3;
import physics.Drone;
public class CSVExporter {
    private final FileWriter writer;
    public CSVExporter(File directory) {
        if (directory == null) {
            throw new IllegalArgumentException("directory cannot be null");
        }
        if (!directory.exists()) {
            directory.mkdirs();
        }

        File file = new File(directory, "positions.csv");
        try {
            writer = new FileWriter(file);
            writer.write("step,droneId,px,py,pz,vx,vy,vz,thrustZ\n");
            writer.flush();
        } catch (IOException e) {
            throw new RuntimeException("cannot create positions.csv");
        }
    }

    public void writeRow(int step, Drone d, double thrustZ) {
        try {
            Vector3 p = d.getPosition();
            Vector3 v = d.getVelocity();

            writer.write(
                    step + "," + d.getId() + "," +  p.x + "," + p.y + "," + p.z + "," +  v.x + "," + v.y + "," + v.z + "," +  thrustZ + "\n"
            );
            writer.flush();

        } catch (IOException e) {
            System.out.println("csv write failed");
        }
    }

    public void close() {
        try {
            if (writer != null) writer.close();
        } catch (IOException e) {
            System.out.println("csv close failed");
        }
    }
}