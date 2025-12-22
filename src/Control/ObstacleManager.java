package Control;
import java.util.List;
import Core.Vector3;
import Core.Obstacle;
import physics.Drone;
public class ObstacleManager {
    private double strength;

    public ObstacleManager(double strength) {
        this.strength = strength;
    }
    public void setStrength(double strength) {
        this.strength = strength;
    }
    public Vector3 computeObstacleForce(Drone d, List<Obstacle> obstacles) {
        Vector3 total = new Vector3(0, 0, 0);
        for (Obstacle o : obstacles) {
            double dist = d.getPosition().distance(o.getPosition());
            double minDist = o.getRadius();
            if (dist >= minDist) {
                continue;
            }

            Vector3 dir = d.getPosition().sub(o.getPosition());
            Vector3 repulse = dir.normalize().scale((minDist - dist) * strength);

            total = total.add(repulse);
        }


        return total;
    }
}