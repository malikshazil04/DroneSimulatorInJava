package Core;
import java.util.ArrayList;
import java.util.List;
import Physics.Drone;
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

public Simulator(Controller controller){
    this.dt = 0.05;
    this.totalTime = 10.0;
    this.gravity = new Vector3(0, 0, -9.81);
    this.drones = new ArrayList<>();
    this.controller = controller;
    this.formationManager = new FormationManager(0.2, 0.1, 3.0);
    this.collisionAvoidance = new CollisionAvoidance(2.0, 0.8);
    this.communication = new CommunicationModule();
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

public void run() {
int steps = (int)(totalTime / dt);

for (int s = 0; s < steps; s++) {

    if (s % 50 == 0) {
        communication.sendMessage("status update tick " + s);
    }

    for (Drone d : drones) {

        String msg = communication.readMessage();
        if (msg != null && !msg.isEmpty()) {
            d.applyForce(new Vector3(0, 0, 0));
        }

        Vector3 avoidF = collisionAvoidance.computeAvoidanceForce(d, drones);
        d.applyForce(avoidF);

        Vector3 formationF = formationManager.computeFormationForce(d, drones);
        d.applyForce(formationF);

        Vector3 thrust = controller.computeThrust(d, d.getTarget(), gravity);
        d.applyForce(thrust);

        d.update(dt);
    }

    if (s % 20 == 0 && !drones.isEmpty()) {
        for (int i = 0; i < drones.size(); i++) {
            System.out.println("Step " + s + " Drone " + i);
            drones.get(i).display();
        }
    }
}


}
}