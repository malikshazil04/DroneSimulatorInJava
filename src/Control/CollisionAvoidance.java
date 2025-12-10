package Control;
import java.util.List;
import Core.Vector3;
import Physics.Drone;

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

        if (other.getId() == d.getId()) {
            continue;
        }

        double dist = d.getPosition().distance(other.getPosition());

        if (dist >= safeDistance) {
            continue;
        }

        Vector3 direction = d.getPosition().sub(other.getPosition());

        double scale = (safeDistance - dist) * strength;

        Vector3 repulsion = direction.normalize().scale(scale);

        total = total.add(repulsion);
    }

    return total;
}


}
