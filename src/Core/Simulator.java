package Core;

import java.util.ArrayList;
import java.util.List;
import Physics.*;
import Control.*;

public class Simulator {

    public double dt = 0.05;
    public double totalTime = 10.0;
    public List<Drone> drones = new ArrayList<>();

    // These are the controller and simulation parameters
    private Controller controller;
    private Vector3 gravity;

    // Constructor to set up controller and environment
    public Simulator(Controller controller,  Vector3 gravity) {
        this.controller = controller;
        this.gravity = gravity;
    }

    public void run() {
        int steps = (int)(totalTime / dt);

        for (int s = 0; s < steps; s++) {
            for (Drone d : drones) {
                // Compute thrust for this drone
                Vector3 thrust = controller.computeThrust(d, d.target, gravity);
                d.applyForce(thrust);
                d.update(dt);
            }
            if (s % 20 == 0 && !drones.isEmpty()) {
               for (int i = 0; i < drones.size(); i++) {
                    System.out.println("step " + s + " drone " + i + " " + drones.get(i).position);
               }
            }
        }
    }
}

