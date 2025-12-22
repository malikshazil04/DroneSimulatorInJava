package Core;
public class Obstacle {
    private Vector3 position;
    private double radius;

    public Obstacle(Vector3 position, double radius) {

        if (position == null || radius <= 0) {
            throw new IllegalArgumentException("invalid obstacle");
        }
        this.position = position;
        this.radius = radius;
    }
    public Vector3 getPosition() {
        return position;
    }
    public double getRadius() {
        return radius;
    }
}