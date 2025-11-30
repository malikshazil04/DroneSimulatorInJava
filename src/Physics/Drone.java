package Physics;
import Core.*;

public class Drone {
public int id;
public Vector3 position;
public Vector3 velocity;
public Vector3 acceleration;
public double mass;

public Drone(int id, Vector3 position, double mass) {
    this.id = id;
    this.position = position;
    this.velocity = new Vector3();
    this.acceleration = new Vector3();
    this.mass = mass;
}

public void applyForce(Vector3 force) {
    Vector3 a = force.scale(1.0 / mass);
    this.acceleration = this.acceleration.add(a);
}

public void update(double dt) {
    this.velocity = this.velocity.add(this.acceleration.scale(dt));
    this.position = this.position.add(this.velocity.scale(dt));
    this.acceleration = new Vector3();
}


}
