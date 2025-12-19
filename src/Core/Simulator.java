package Core;
import java.util.ArrayList;
import java.util.List;
import physics.Drone;
import Control.*;

public class Simulator {
private double dt;
private double totalTime;
private List<Drone> drones;
private Vector3 gravity;
private Controller controller;
private FormationManager formationManager;
private CollisionAvoidance collisionAvoidance;
private CommunicationModule communication;
private Logger logger;
private List<Obstacle> obstacles;
private ObstacleManager obstacleManager;
private Config config;
private CSVExporter csvExporter;

public Simulator(Controller controller, Config config){
    this.dt = config.dt;
    this.totalTime = config.totalTime;
    this.gravity = config.gravity;
    this.drones = new ArrayList<>();
    this.controller = controller;
    this.formationManager =new FormationManager(config.formationKPos,config.formationKVel,config.formationSpacing);
    this.collisionAvoidance =new CollisionAvoidance(config.collisionSafeDistance,config.collisionStrength);
    this.communication = new CommunicationModule();
    this.logger = new Logger();
    this.obstacles = new ArrayList<>();
    this.obstacleManager =new ObstacleManager(config.obstacleStrength);
    this.csvExporter = new CSVExporter("output.csv");
}

public void addDrone(Drone d){
    if (d == null) {
        throw new IllegalArgumentException("drone cannot be null");
    }
    this.drones.add(d);
}

public void setDt(double dt) {
    if (dt <= 0) {
        throw new IllegalArgumentException("dt must be positive");
    }
    this.dt = dt;
}

public void setTotalTime(double t) {
    if (t <= 0) {
        throw new IllegalArgumentException("total time must be positive");
    }
    this.totalTime = t;
}

public void setGravity(Vector3 g) {
    if (g == null) {
        throw new IllegalArgumentException("gravity cannot be null");
    }
    this.gravity = g;
}

public void setController(Controller c) {
    if (c == null) {
        throw new IllegalArgumentException("controller cannot be null");
    }   
    this.controller = c;
}

public void setFormationManager(FormationManager fm) {
    if (fm == null) {
        throw new IllegalArgumentException("formation manager cannot be null");
    }
    this.formationManager = fm;
}

public double getDt() {
    return this.dt;
}

public double getTotalTime() {
    return this.totalTime;
}

public Vector3 getGravity() {
    return this.gravity;
}

public Controller getController() {
    return this.controller;
}

public FormationManager getFormationManager() {
    return this.formationManager;
}

public CollisionAvoidance getCollisionAvoidance() {
    return this.collisionAvoidance;
}

public CommunicationModule getCommunication() {
    return this.communication;
}

public List<Drone> getDrones() {
    return this.drones;
}
public Logger getLogger() {
    return this.logger;
}
public List<Obstacle> getObstacles() {
    return obstacles;
}
public CSVExporter getCsvExporter() {
    return csvExporter;
}

public void run() {
int steps = (int)(totalTime / dt);
logger.log("simulation started");

for (int s = 0; s < steps; s++) {
    
    if (s % 50 == 0) {
        communication.sendMessage("status update tick " + s);
        logger.log("step " + s);
    }

    for (Drone d : drones) {
        String msg = communication.readMessage();
        if (msg != null && !msg.isEmpty()) {
            d.applyForce(new Vector3(0, 0, 0));
        }

        Vector3 obsF = obstacleManager.computeObstacleForce(d, obstacles);
        d.applyForce(obsF);

        Vector3 avoidF = collisionAvoidance.computeAvoidanceForce(d, drones);
        d.applyForce(avoidF);

        Vector3 formationF = formationManager.computeFormationForce(d, drones);
        d.applyForce(formationF);

        Vector3 thrust = controller.computeThrust(d, d.getTarget(), gravity);
        d.applyForce(thrust);

        d.update(dt);

        csvExporter.writeRow(s,d.getId(),d.getPosition());
    }

    if (s % 20 == 0 && !drones.isEmpty()) {
        for (int i = 0; i < drones.size(); i++) {
            System.out.println("Step " + s + " Drone " + i);
            drones.get(i).display();
        }
    }
}
logger.log("simulation ended");

}
}