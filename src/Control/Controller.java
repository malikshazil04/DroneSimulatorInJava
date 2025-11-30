package Control;
import Physics.*;
import Core.*;

public class Controller {
public double kp = 8.0;
public double kd = 3.0;

public Vector3 computeThrust(Drone d, Vector3 target, Vector3 gravity){
    Vector3 ePos = target.sub(d.position);
    Vector3 eVel = d.velocity.scale(-1);
    Vector3 accDesired = ePos.scale(kp).add(eVel.scale(kd)).add(gravity);
    // compensate gravity fully
    accDesired = accDesired.add(new Vector3(0,0,9.81));
    return accDesired.scale(d.mass);
}


}
