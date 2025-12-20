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
    public double dragK;
    public double commRange;
    public double pLoss;
    public int logEvery;
    public double maxSpeed;

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
        maxSpeed = 20.0;
    }

    public void loadFromFile(String fileName) {
        // Try multiple possible paths
        String[] possiblePaths = {
                fileName, // CWD relative
                "src/" + fileName, // Inside src (if running from root)
                "../" + fileName, // Parent (if running from src/out)
                "DroneSimulatorInJava/" + fileName // Absolute-ish fallback
        };

        File fileToLoad = null;
        for (String path : possiblePaths) {
            File f = new File(path);
            if (f.exists() && !f.isDirectory()) {
                fileToLoad = f;
                System.out.println("Config loaded from: " + f.getAbsolutePath());
                break;
            }
        }

        if (fileToLoad == null) {
            System.err.println("CRITICAL ERROR: Could not find config file '" + fileName + "'");
            System.err.println("Searched in:");
            for (String p : possiblePaths) {
                System.err.println(" - " + new File(p).getAbsolutePath());
            }
            // fallback to defaults or throw
            throw new RuntimeException("Config file not found: " + fileName);
        }

        try (BufferedReader br = new BufferedReader(new FileReader(fileToLoad))) {

            String line;
            while ((line = br.readLine()) != null) {

                if (line.trim().isEmpty() || !line.contains("="))
                    continue;

                String[] parts = line.split("=");
                String key = parts[0].trim();
                double value = Double.parseDouble(parts[1].trim());

                switch (key) {
                    case "dt":
                        dt = value;
                        break;
                    case "totalTime":
                        totalTime = value;
                        break;
                    case "dragK":
                        dragK = value;
                        break;
                    case "commRange":
                        commRange = value;
                        break;
                    case "pLoss":
                        pLoss = value;
                        break;
                    case "formationSpacing":
                        formationSpacing = value;
                        break;
                    case "collisionSafeDistance":
                        collisionSafeDistance = value;
                        break;
                    case "formationKPos":
                        formationKPos = value;
                        break;
                    case "formationKVel":
                        formationKVel = value;
                        break;
                    case "collisionStrength":
                        collisionStrength = value;
                        break;
                    case "obstacleStrength":
                        obstacleStrength = value;
                        break;
                    case "logEvery":
                        logEvery = (int) value;
                        break;
                    case "maxSpeed":
                        maxSpeed = value;
                        break;
                }
            }

        } catch (Exception e) {
            throw new RuntimeException("Failed to load config file: " + e.getMessage());
        }
    }
}
