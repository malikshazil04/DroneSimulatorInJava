package Control;

import java.util.List;
import Core.Vector3;
import Physics.Drone;

public class FormationManager {
private double kPos;
private double kVel;
private double desiredSpacing;

public FormationManager(double kPos, double kVel, double spacing) {
    this.kPos = kPos;
    this.kVel = kVel;
    this.desiredSpacing = spacing;
}

public void setSpacing(double s) {
    if (s <= 0) {
        throw new IllegalArgumentException("spacing must be positive");
    }
    this.desiredSpacing = s;
}

public void setKPos(double k) {
    this.kPos = k;
}

public void setKVel(double k) {
    this.kVel = k;
}

public Vector3 computeFormationForce(Drone d, List<Drone> all) {
    Vector3 total = new Vector3(0, 0, 0);

    for (Drone other : all) {

        if (other.getId() == d.getId()) {
            continue;
        }

        double dist = d.getPosition().distance(other.getPosition());

        if (dist > desiredSpacing * 2) {
            continue;
        }

        Vector3 diffPos = d.getPosition().sub(other.getPosition());
        Vector3 diffVel = d.getVelocity().sub(other.getVelocity());

        Vector3 f = diffPos.scale(kPos).add(diffVel.scale(kVel));

        total = total.add(f);
    }

    return total;
}


}

