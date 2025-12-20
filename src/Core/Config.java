package Core;
import java.io.*;
public class Config {

public double dt;
public double totalTime;
public Vector3 gravity;
public double formationSpacing;
public double formationKPos;
public double formationKVel;
public double collisionSafeDistance;
public double collisionStrength;
public double obstacleStrength;
public double dragK ;
public double commRange;
public double pLoss;
public int logEvery;


public Config() {

    dt = 0;
    totalTime = 0;

    gravity = new Vector3(0, 0, 0);

    formationSpacing = 0;
    formationKPos = 0;
    formationKVel = 0;

    collisionSafeDistance = 0;
    collisionStrength = 0;

    obstacleStrength = 0;

    commRange = 0.0;
    pLoss = 0;
    logEvery = 0;
}
    public void loadFromFile(String filePath) {
        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {

            String line;
            while ((line = br.readLine()) != null) {

                if (line.trim().isEmpty() || !line.contains("=")) continue;

                String[] parts = line.split("=");
                String key = parts[0].trim();
                double value = Double.parseDouble(parts[1].trim());

                switch (key) {
                    case "dt": dt = value; break;
                    case "totalTime": totalTime = value; break;
                    case "dragK": dragK = value; break;
                    case "commRange": commRange = value; break;
                    case "pLoss": pLoss = value; break;
                    case "formationSpacing": formationSpacing = value; break;
                    case "collisionSafeDistance": collisionSafeDistance = value; break;
                }
            }

        } catch (Exception e) {
            throw new RuntimeException("Failed to load config file");
        }
    }
}
