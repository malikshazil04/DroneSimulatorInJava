package Control;

import java.util.List;
import Core.Vector3;
import physics.Drone;

public class FormationManager {
    private double kPos;
    private double kVel;
    private double desiredSpacing;
    private double commRange = 0;

    public FormationManager(double kPos, double kVel, double spacing) {
        this.kPos = kPos;
        this.kVel = kVel;
        this.desiredSpacing = spacing;
    }

    public void setCommRange(double commRange) {
        this.commRange = commRange;
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

    public Vector3 computeFormationForce(Drone me, List<Drone> drones) {
        Vector3 total = new Vector3(0, 0, 0);

        for (Drone other : drones) {
            if (other == me) continue;

            double dist = me.getPosition().distance(other.getPosition());

            // ✅ CEP: neighbors set Ni = only within comm range
            if (commRange > 0 && dist > commRange) continue;

            // avoid zero divide
            if (dist < 1e-9) continue;

            // ✅ minimal "spacing" (desired relative position magnitude)
            Vector3 diff = me.getPosition().sub(other.getPosition());  // (pi - pj)
            Vector3 velDiff = me.getVelocity().sub(other.getVelocity()); // (vi - vj)

            // convert distance error into a vector (push/pull along line)
            Vector3 posErrVec = diff.normalize().scale(dist - desiredSpacing);

            // ✅ CEP sign: negative feedback
            Vector3 fij = posErrVec.scale(-kPos).add(velDiff.scale(-kVel));

            total = total.add(fij);
        }

        return total;


    }
}

