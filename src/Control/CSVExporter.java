package Control;

import java.io.FileWriter;
import java.io.IOException;

import Core.Vector3;

public class CSVExporter {

private FileWriter writer;

public CSVExporter(String filePath) {

    try {
        writer = new FileWriter(filePath);
        writer.write("step,droneId,x,y,z\n");
        writer.flush();
    } catch (IOException e) {
        throw new RuntimeException("cannot create csv file");
    }
}

public void writeRow(int step,int droneId,Vector3 position) {
    try {
        writer.write(step + ", " +droneId + ", " + position.toString() + "\n");
        writer.flush();
    } catch (IOException e) {
        System.out.println("csv write failed");
    }
}


}