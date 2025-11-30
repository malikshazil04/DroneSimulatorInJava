package Control;
import Physics.*;
import Core.*;

public class Controller {
public double kp = 2.0;
public double kd = 1.0;

public Vector3 computeThrust(Drone d, Vector3 target, Vector3 gravity) {
    Vector3 ePos = target.sub(d.position);
    Vector3 eVel = d.velocity.scale(-1);
    Vector3 accDesired = ePos.scale(kp).add(eVel.scale(kd)).add(gravity);
    Vector3 thrust = accDesired.scale(d.mass);
    return thrust;
}


}
