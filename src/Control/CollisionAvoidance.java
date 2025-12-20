package Control;
import java.util.List;
import Core.Vector3;
import physics.Drone;

public class CollisionAvoidance {

private double safeDistance;
private double strength;

public CollisionAvoidance() {
    this.safeDistance = 0;
    this.strength = 0;
}

public CollisionAvoidance(double safeDistance, double strength) {
    this.safeDistance = safeDistance;
    this.strength = strength;
}

public double getSafeDistance() {
    return this.safeDistance;
}

public double getStrength() {
    return this.strength;
}

public void setSafeDistance(double d) {
    if (d <= 0) {
    throw new IllegalArgumentException("safe distance must be positive");
}
    this.safeDistance = d;
}

public void setStrength(double s) {
    this.strength = s;
}

public Vector3 computeAvoidanceForce(Drone d, List<Drone> all) {

    Vector3 total = new Vector3(0, 0, 0);
    for (Drone other : all) {
        if (other == d) continue;

        double dist = d.getPosition().distance(other.getPosition());
        if (dist < 1e-9) continue;

        if (dist < safeDistance) {
            Vector3 dir = d.getPosition().sub(other.getPosition()).normalize();
            double mag = (safeDistance - dist) * strength;
            total = total.add(dir.scale(mag));
        }
    }
    return total;
}
}
