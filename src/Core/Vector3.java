package Core;
public class Vector3 {
public double x;
public double y;
public double z;

public Vector3() {
    this(0,0,0);
}

public Vector3(double x, double y, double z) {
    this.x = x;
    this.y = y;
    this.z = z;
}

public Vector3 add(Vector3 other) {
    return new Vector3(this.x + other.x, this.y + other.y, this.z + other.z);
}

public Vector3 sub(Vector3 other) {
    return new Vector3(this.x - other.x, this.y - other.y, this.z - other.z);
}

public Vector3 scale(double s) {
    return new Vector3(this.x * s, this.y * s, this.z * s);
}

public double dot(Vector3 other) {
    return this.x * other.x + this.y * other.y + this.z * other.z;
}

public Vector3 cross(Vector3 other) {
    double cx = this.y * other.z - this.z * other.y;
    double cy = this.z * other.x - this.x * other.z;
    double cz = this.x * other.y - this.y * other.x;
    return new Vector3(cx, cy, cz);
}

public double magnitude() {
    return Math.sqrt(this.x*this.x + this.y*this.y + this.z*this.z);
}

public Vector3 normalize() {
    double mag = magnitude();
    if (mag == 0) return new Vector3(0,0,0);
    return scale(1.0 / mag);
}

public double distance(Vector3 other) {
    return this.sub(other).magnitude();
}

public String toString() {
    return String.format("Vector3(%.3f, %.3f, %.3f)", x, y, z);
}


}
