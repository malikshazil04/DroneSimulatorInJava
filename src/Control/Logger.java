package Control;
import javax.swing.JFileChooser;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class Logger {

private FileWriter writer;

public Logger() {

    JFileChooser chooser = new JFileChooser();
    chooser.setDialogTitle("Select directory to save log file");
    chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);

    int result = chooser.showSaveDialog(null);

    if (result != JFileChooser.APPROVE_OPTION) {
        throw new RuntimeException("No directory selected");
    }

    File directory = chooser.getSelectedFile();

    File logFile = new File(directory, "simulation.txt");

    try {
        writer = new FileWriter(logFile, true);
    } catch (IOException e) {
        throw new RuntimeException("Cannot create log file");
    }
}

public void log(String message) {

    if (message == null) {
        return;
    }

    System.out.println(message);

    try {
        writer.write(message + "\n");
        writer.flush();
    } catch (IOException e) {
        System.out.println("Error writing log");
    }
}


}
