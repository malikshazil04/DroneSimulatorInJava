package Core;
import java.util.ArrayList;
import java.util.List;
import Physics.Drone;
import Control.Controller;

public class Simulator {
private double dt;
private double totalTime;
private List<Drone> drones;
private Vector3 gravity;
private Controller controller;

public Simulator(Controller controller) {
    this.dt = 0.05;
    this.totalTime = 10.0;
    this.gravity = new Vector3(0, 0, -9.81);
    this.drones = new ArrayList<>();
    this.controller = controller;
}

public void addDrone(Drone d){
    if (d == null) {
        throw new IllegalArgumentException("drone cannot be null");
    }
    this.drones.add(d);
}

public void run() {
    int steps = (int)(totalTime / dt);

    for (int s = 0; s < steps; s++) {

        for (Drone d : drones) {
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