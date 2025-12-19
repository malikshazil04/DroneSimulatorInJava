package Core;
public class Config {

public double dt;
public double totalTime;
public Vector3 gravity;
public double formationSpacing;
public double formationKPos;
public double formationKVel;
public double collisionSafeDistance;
public double collisionStrength;
public double obstacleStrength;

public Config() {

    dt = 0.05;
    totalTime = 10.0;

    gravity = new Vector3(0, 0, -9.81);

    formationSpacing = 3.0;
    formationKPos = 0.2;
    formationKVel = 0.1;

    collisionSafeDistance = 2.0;
    collisionStrength = 0.8;

    obstacleStrength = 1.0;
}
public Config(double dt, double totalTime,double formationSpacing,double formationKPos,double formationKVel, double collisionSafeDistance,double collisionStrength, double obstacleStrength) {
    if (dt <= 0 || totalTime <= 0) {
        throw new IllegalArgumentException("time values must be positive");
    }
    this.dt = dt;
    this.totalTime = totalTime;
    this.gravity = new Vector3(0, 0, -9.81);

    this.formationSpacing = formationSpacing;
    this.formationKPos = formationKPos;
    this.formationKVel = formationKVel;

    this.collisionSafeDistance = collisionSafeDistance;
    this.collisionStrength = collisionStrength;

    this.obstacleStrength = obstacleStrength;
}
}
