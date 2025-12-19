package Control;
import physics.*;
import Core.*;

public class Controller {
public double kp = 8.0;
public double kd = 3.0;

public Vector3 computeThrust(Drone d, Vector3 target, Vector3 gravity){
    Vector3 ePos = target.sub(d.getPosition());
    Vector3 eVel = d.getVelocity().scale(-1);
    Vector3 accDesired = ePos.scale(kp).add(eVel.scale(kd)).add(gravity);
    // compensate gravity fully
    accDesired = accDesired.add(new Vector3(0,0,9.81));
    return accDesired.scale(d.getMass());
}


}
