package Control;
import physics.*;
import Core.*;

public class Controller {
public double kp = 8.0;
public double kd = 3.0;
private double kYaw = 2.0;
private double kDamp = 0.6;

public void setKp(double kp) {
    this.kp = kp;
}
public void setKd(double kd) {
    this.kd = kd;
}
public void setkYaw(double kYaw) {
    this.kYaw = kYaw;
}
public void setkDamp(double kDamp) {
    this.kDamp = kDamp;
}


public Vector3 computeThrust(Drone d, Vector3 target, Vector3 gravity) {
    Vector3 ePos = target.sub(d.getPosition());     // e_p
    Vector3 eVel = d.getVelocity().scale(-1);       // e_v  (v_target = 0)
    // CEP: a_d = kp*e_p + kd*e_v + g   where g = [0,0,-9.81]
    Vector3 g = (gravity != null) ? gravity : new Vector3(0, 0, -9.81);
    Vector3 accDesired = ePos.scale(kp).add(eVel.scale(kd)).add(g);
    // thrust in world frame: T = m * a_d
    return accDesired.scale(d.getMass());
}
public Vector3 computeTorque(Drone d) {
    // minimal: try to rotate drone toward target direction

    Vector3 dir = d.getTarget().sub(d.getPosition());
    if (dir.magnitude() < 1e-9) return new Vector3(0, 0, 0);
    dir = dir.normalize();

    // forward axis in world = R*(1,0,0)
    Vector3 forward = d.getRotation().multiply(new Vector3(1, 0, 0)).normalize();

    // axis error = forward x dir
    Vector3 axisErr = forward.cross(dir);

    // PD torque (simple)
    double k = 2.0;
    double kd = 0.6;

    return axisErr.scale(k).sub(d.getOmega().scale(kd));
}
}
