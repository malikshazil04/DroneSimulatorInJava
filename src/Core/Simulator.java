package Core;

import java.util.ArrayList;
import java.util.List;

import Control.*;
import physics.Drone;

public class Simulator {

    private double dt;
    private double totalTime;
    private List<Drone> drones;
    private Vector3 gravity;
    private Controller controller;
    private FormationManager formationManager;
    private CollisionAvoidance collisionAvoidance;
    private CommunicationModule communication;
    private boolean running = false;
    private boolean paused = false;
    private int currentStep = 0;
    private int totalSteps = 0;
    private Logger logger;
    private CSVExporter csvExporter;
    private Config config;
    private double areaWidth = 120.0;
    private double areaLength = 120.0;
    private double wallBounce = 0.6;
    private double elapsedTime = 0.0;
    private int collisionCountStep = 0; // collisions in the current step (optional)
    private int collisionCountTotal = 0; // total collisions across sim (optional)
    private double targetTolerance = 0.8;
    private ObstacleManager obstacleManager;
    public List<Obstacle> obstacles;

    public Simulator(Controller controller, Config config, java.io.File outputDir) {

        if (controller == null) {
            throw new IllegalArgumentException("controller cannot be null");
        }
        if (config == null) {
            throw new IllegalArgumentException("config cannot be null");
        }

        this.config = config;

        this.dt = config.dt;
        this.totalTime = config.totalTime;
        this.gravity = config.gravity;

        this.drones = new ArrayList<>();

        this.controller = controller;

        this.formationManager = new FormationManager(
                config.formationKPos,
                config.formationKVel,
                config.formationSpacing);
        this.formationManager.setCommRange(config.commRange);

        this.collisionAvoidance = new CollisionAvoidance(
                config.collisionSafeDistance,
                config.collisionStrength);
        this.communication = new CommunicationModule(config.commRange, config.pLoss);

        this.logger = new Logger(outputDir);
        this.csvExporter = new CSVExporter(logger.getDirectory());
        this.obstacleManager = new ObstacleManager(config.obstacleStrength);
        obstacles = new ArrayList<>();
    }

    public double getDt() {
        return dt;
    }

    public ObstacleManager getObstacleManager() {
        return obstacleManager;
    }

    public void setObstacleManager(ObstacleManager obstacleManager) {
        this.obstacleManager = obstacleManager;
    }

    public List<Obstacle> getObstacles() {
        return obstacles;
    }

    public void setObstacles(List<Obstacle> obstacles) {
        this.obstacles = obstacles;
    }

    public double getAreaWidth() {
        return areaWidth;
    }

    public double getAreaLength() {
        return areaLength;
    }

    public void setAreaWidth(double w) {
        if (w <= 0)
            throw new IllegalArgumentException("areaWidth must be > 0");
        this.areaWidth = w;
    }

    public void setAreaLength(double l) {
        if (l <= 0)
            throw new IllegalArgumentException("areaLength must be > 0");
        this.areaLength = l;
    }

    public void setWallBounce(double b) {
        if (b < 0 || b > 1)
            throw new IllegalArgumentException("wallBounce must be 0..1");
        this.wallBounce = b;
    }

    public double getElapsedTime() {
        return elapsedTime;
    }

    public void setTargetTolerance(double tol) {
        if (tol <= 0)
            throw new IllegalArgumentException("tol must be > 0");
        this.targetTolerance = tol;
    }

    private void enforceBoundaryBounce(Drone d) {
        double halfW = areaWidth / 2.0;
        double halfL = areaLength / 2.0;

        Vector3 p = d.getPosition();
        Vector3 v = d.getVelocity();

        double x = p.x, y = p.y, z = p.z;
        double vx = v.x, vy = v.y, vz = v.z;

        boolean hit = false;

        if (x < -halfW) {
            x = -halfW;
            vx = Math.abs(vx) * wallBounce;
            hit = true;
        } else if (x > halfW) {
            x = halfW;
            vx = -Math.abs(vx) * wallBounce;
            hit = true;
        }

        if (y < -halfL) {
            y = -halfL;
            vy = Math.abs(vy) * wallBounce;
            hit = true;
        } else if (y > halfL) {
            y = halfL;
            vy = -Math.abs(vy) * wallBounce;
            hit = true;
        }

        if (hit) {
            d.setPosition(new Vector3(x, y, z));
            d.setVelocity(new Vector3(vx, vy, vz));
        }
    }

    public void addDrone(Drone d) {
        if (d == null)
            throw new IllegalArgumentException("drone cannot be null");
        d.setMaxSpeed(config.maxSpeed);
        drones.add(d);
    }

    public void setMaxSpeed(double s) {
        config.maxSpeed = s;
        for (Drone d : drones) {
            d.setMaxSpeed(s);
        }
    }

    public List<Drone> getDrones() {
        return drones;
    }

    public FormationManager getFormationManager() {
        return formationManager;
    }

    public CollisionAvoidance getCollisionAvoidance() {
        return collisionAvoidance;
    }

    public CommunicationModule getCommunication() {
        return communication;
    }

    public Logger getLogger() {
        return logger;
    }

    public int computeCollisionCount() {
        int count = 0;

        for (int i = 0; i < drones.size(); i++) {
            for (int j = i + 1; j < drones.size(); j++) {
                double dist = drones.get(i).getPosition().distance(drones.get(j).getPosition());
                if (dist < config.collisionSafeDistance) {
                    count++;
                }
            }
        }
        return count;
    }

    public double computeAverageSpacing() {
        if (drones.size() < 2)
            return 0.0;

        double sum = 0.0;
        int pairs = 0;

        for (int i = 0; i < drones.size(); i++) {
            for (int j = i + 1; j < drones.size(); j++) {
                sum += drones.get(i).getPosition().distance(drones.get(j).getPosition());
                pairs++;
            }
        }

        return pairs == 0 ? 0.0 : sum / pairs;
    }

    public double getCollisionPercentage() {
        int n = drones.size();
        if (n < 2)
            return 0.0;

        int pairs = n * (n - 1) / 2;
        int collisions = 0;

        double safe = config.collisionSafeDistance;

        for (int i = 0; i < n; i++) {
            for (int j = i + 1; j < n; j++) {
                double dist = drones.get(i).getPosition().distance(drones.get(j).getPosition());
                if (dist < safe)
                    collisions++;
            }
        }

        return (collisions * 100.0) / pairs;
    }

    private boolean allReachedTargets() {
        for (Drone d : drones) {
            double dist = d.getPosition().distance(d.getTarget());
            if (dist > targetTolerance)
                return false;
        }
        return true;
    }

    public void startSim() {
        if (dt <= 0)
            throw new IllegalStateException("dt must be > 0");
        if (totalTime <= 0)
            throw new IllegalStateException("totalTime must be > 0");

        running = true;
        paused = false;
        currentStep = 0;
        elapsedTime = 0.0;

        totalSteps = (int) Math.ceil(totalTime / dt);

        logger.log("Simulation started");
        logger.log("totalSteps=" + totalSteps + " dt=" + dt + " totalTime=" + totalTime);
    }

    public void pauseSim() {
        paused = true;
        logger.log("Simulation paused");
    }

    public void resumeSim() {
        paused = false;
        logger.log("Simulation resumed");
    }

    public Config getConfig() {
        return config;
    }

    public void stopSim() {

        if (running) {
            running = false;
            paused = false;
            finalizeSimulation();
        }
    }

    public Controller getController() {
        return controller;
    }

    public void setDt(double dt) {
        if (dt <= 0)
            throw new IllegalArgumentException("dt must be > 0");
        this.dt = dt;
        if (running)
            totalSteps = (int) Math.ceil(totalTime / dt);
    }

    public void setTotalTime(double totalTime) {
        if (totalTime <= 0)
            throw new IllegalArgumentException("totalTime must be > 0");
        this.totalTime = totalTime;
        if (running)
            totalSteps = (int) Math.ceil(totalTime / dt);
    }

    public boolean isRunning() {
        return running;
    }

    public boolean isPaused() {
        return paused;
    }

    public void stepOnce() {
        if (!running || paused)
            return;
        if (currentStep >= totalSteps) {
            stopSim();
            return;
        }

        communication.exchangeStates(drones);

        for (Drone d : drones) {

            if (d.getMass() <= 0) {
                throw new IllegalStateException("Drone mass is zero for id=" + d.getId());
            }
            d.resetAcceleration();
            Vector3 obsF = obstacleManager.computeObstacleForce(d, obstacles);
            d.applyForce(obsF);
            // collision avoidance
            Vector3 avoidF = collisionAvoidance.computeAvoidanceForce(d, drones);
            d.applyForce(avoidF);

            // formation
            Vector3 formF = formationManager.computeFormationForce(d, drones);
            d.applyForce(formF);

            // aerodynamic drag
            Vector3 dragF = d.getVelocity().scale(-config.dragK);
            d.applyForce(dragF);

            // controller thrust
            Vector3 thrust = controller.computeThrust(d, d.getTarget(), gravity);
            d.applyForce(thrust);

            // applying torque
            d.applyTorque(controller.computeTorque(d));

            // integrate
            d.update(dt);
            enforceBoundaryBounce(d);

            double thrustBodyZ = d.getRotation().transpose().multiply(thrust).z;
            csvExporter.writeRow(currentStep, d, thrustBodyZ);
        }

        // Increment elapsed time once per step, not per drone
        elapsedTime += dt;
        if (config.logEvery > 0 && currentStep % config.logEvery == 0) {
            logger.log("step " + currentStep);
        }

        currentStep++;
        if (currentStep >= totalSteps) {
            running = false;
            finalizeSimulation();
        } else if (checkAllDronesFinished()) {
            logger.log("All drones reached target! Stopping simulation.");
            stopSim();
        }
    }

    private boolean checkAllDronesFinished() {
        if (drones.isEmpty())
            return false;
        for (Drone d : drones) {
            if (d.getPosition().distance(d.getTarget()) > 1.0) {
                return false;
            }
            // Also check if valid velocity is low enough (settled)
            if (d.getVelocity().magnitude() > 0.1) {
                return false;
            }
        }
        return true;
    }

    private void finalizeSimulation() {

        int collisions = computeCollisionCount();
        double avgSpacing = computeAverageSpacing();
        double commSuccess = communication.getSuccessRate();

        logger.writeMetric("Final Metrics Summary");
        logger.writeMetric("Total Drones: " + drones.size());
        logger.writeMetric("Collisions (dist < safeDistance): " + collisions);
        logger.writeMetric("Average Pairwise Distance: " + avgSpacing);
        logger.writeMetric("Communication Attempts: " + communication.getAttempts());
        logger.writeMetric("Communication Successes: " + communication.getSuccesses());
        logger.writeMetric("Communication Success Rate: " + commSuccess);

        logger.log("simulation ended");

        csvExporter.close();
        logger.close();
    }

}
