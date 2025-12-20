package physics;
import Core.*;

public class Drone {
private static int nextId = 1;
private int id;
private Vector3 position;
private Vector3 velocity;
private Vector3 acceleration;
private double mass;
private Vector3 target;

public Drone() {
    this.id = nextId++;
    this.position = new Vector3(0,0,0);
    this.velocity = new Vector3(0,0,0);
    this.acceleration = new Vector3(0,0,0);
    this.mass = 1.0;
    this.target = new Vector3(0,0,0);
}

public Drone(Vector3 position, double mass, Vector3 target) {
    this.id = nextId++;
    setPosition(position);
    setMass(mass);
    this.velocity = new Vector3(0,0,0);
    this.acceleration = new Vector3(0,0,0);
    setTarget(target);
}

public int getId() {
    return id;
}

public Vector3 getPosition() {
    return position;
}

public void setPosition(Vector3 p) {
    if (p == null) {
        throw new IllegalArgumentException("position cannot be null");
    }
    this.position = p;
}

public Vector3 getVelocity() {
    return velocity;
}

public void setVelocity(Vector3 v) {
    if (v == null) {
        throw new IllegalArgumentException("velocity cannot be null");
}
    this.velocity = v;
}

public Vector3 getAcceleration() {
    return acceleration;
}

public void setAcceleration(Vector3 a) {
    if (a == null) {
        throw new IllegalArgumentException("acceleration cannot be null");
}
    this.acceleration = a;
}

public double getMass() {
    return mass;
}
public void setMass(double m) {
    if (m <= 0) {
        throw new IllegalArgumentException("mass must be positive");
}
    this.mass = m;
}

public Vector3 getTarget() {
    return target;
}

public void setTarget(Vector3 t) {
    if (t == null) {
    throw new IllegalArgumentException("target cannot be null");
}
    this.target = t;
}

public void applyForce(Vector3 force) {
    if (force == null) {
        throw new IllegalArgumentException("force cannot be null");
}
    Vector3 a = force.scale(1.0 / mass);
    this.acceleration = this.acceleration.add(a);
}

public void update(double dt) {
if (dt <= 0) {
throw new IllegalArgumentException("dt must be positive");
}
this.velocity = this.velocity.add(this.acceleration.scale(dt));
this.position = this.position.add(this.velocity.scale(dt));
this.acceleration = new Vector3(0, 0, 0);
}

public void resetAcceleration() {
this.acceleration = new Vector3(0, 0, 0);
}

public void display() {
System.out.println("Drone ID: " + id);
System.out.println("Position: " + position.toString());
System.out.println("Velocity: " + velocity.toString());
System.out.println("Acceleration: " + acceleration.toString());
System.out.println("Target: " + target.toString());
}



}
