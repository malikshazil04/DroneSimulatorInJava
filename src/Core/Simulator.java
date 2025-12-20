package Core;

import java.util.ArrayList;
import java.util.List;

import Control.CSVExporter;
import Control.CollisionAvoidance;
import Control.CommunicationModule;
import Control.Controller;
import Control.FormationManager;
import Control.Logger;
import Control.ObstacleManager;
import physics.Drone;

public class Simulator {

    private double dt;
    private double totalTime;

    private List<Drone> drones;
    private List<Obstacle> obstacles;

    private Vector3 gravity;

    private Controller controller;
    private FormationManager formationManager;
    private CollisionAvoidance collisionAvoidance;
    private ObstacleManager obstacleManager;
    private CommunicationModule communication;
    private boolean running = false;
    private boolean paused = false;

    private int currentStep = 0;
    private int totalSteps = 0;

    private Logger logger;
    private CSVExporter csvExporter;

    private Config config;

    public Simulator(Controller controller, Config config) {

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
        this.obstacles = new ArrayList<>();

        this.controller = controller;

        this.formationManager = new FormationManager(
                config.formationKPos,
                config.formationKVel,
                config.formationSpacing
        );

        this.collisionAvoidance = new CollisionAvoidance(
                config.collisionSafeDistance,
                config.collisionStrength
        );

        this.obstacleManager = new ObstacleManager(config.obstacleStrength);

        this.communication = new CommunicationModule(config.commRange, config.pLoss);

        this.logger = new Logger();
        this.csvExporter = new CSVExporter(logger.getDirectory());
    }

    public void addDrone(Drone d) {
        if (d == null) throw new IllegalArgumentException("drone cannot be null");
        drones.add(d);
    }

    public List<Drone> getDrones() {
        return drones;
    }

    public List<Obstacle> getObstacles() {
        return obstacles;
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

    private int computeCollisionCount() {
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

    private double computeAverageSpacing() {
        if (drones.size() < 2) return 0.0;

        double sum = 0.0;
        int pairs = 0;

        for (int i = 0; i < drones.size(); i++) {
            for (int j = i + 1; j < drones.size(); j++) {
                sum += drones.get(i).getPosition().distance(drones.get(j).getPosition());
                pairs++;
            }
        }

        return sum / pairs;
    }

    public void startSim() {
        if (dt <= 0) throw new IllegalStateException("dt must be > 0");
        if (totalTime <= 0) throw new IllegalStateException("totalTime must be > 0");

        running = true;
        paused = false;
        currentStep = 0;

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

    public void setTotalTime(double totalTime) {
        this.totalTime = totalTime;
    }

    public void stopSim() {

        if (running) {
            running = false;
            paused = false;
            finalizeSimulation();
        }
    }

    public void setDt(double dt) {
        this.dt = dt;
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

            // integrate
            d.update(dt);

            csvExporter.writeRow(currentStep, d, thrust.z);
        }
        if (config.logEvery > 0 && currentStep % config.logEvery == 0) {
            logger.log("step " + currentStep);
        }

        currentStep++;
        if (currentStep >= totalSteps) {
            running = false;
            finalizeSimulation();
        }

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
