package Core;
import java.util.ArrayList;
import java.util.List;
import Physics.*;

public class Simulator {
public double dt = 0.05;
public double totalTime = 10.0;
public List<Drone> drones = new ArrayList<>();

public void run() {
    int steps = (int)(totalTime / dt);
    for (int s = 0; s < steps; s++) {
        // placeholder for full loop
        for (Drone d : drones) {
            d.update(dt);
        }
        // simple print for now
        if (s % 20 == 0) {
            System.out.println("step " + s + " drone0 " + drones.get(0).position);
        }
    }
}

}
