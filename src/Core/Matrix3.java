package Core;

public class Matrix3 {

    private final double[][] m;

    public Matrix3() {
        m = new double[3][3];
    }

    public Matrix3(double[][] values) {
        if (values == null || values.length != 3 || values[0].length != 3 || values[1].length != 3 || values[2].length != 3) {
            throw new IllegalArgumentException("Matrix3 must be 3x3");
        }
        m = new double[3][3];
        for (int i = 0; i < 3; i++) {
            System.arraycopy(values[i], 0, m[i], 0, 3);
        }
    }

    public static Matrix3 identity() {
        Matrix3 I = new Matrix3();
        I.m[0][0] = 1; I.m[0][1] = 0; I.m[0][2] = 0;
        I.m[1][0] = 0; I.m[1][1] = 1; I.m[1][2] = 0;
        I.m[2][0] = 0; I.m[2][1] = 0; I.m[2][2] = 1;
        return I;
    }

    public static Matrix3 skew(Vector3 v) {
        Matrix3 S = new Matrix3();
        S.m[0][0] = 0;     S.m[0][1] = -v.z;  S.m[0][2] =  v.y;
        S.m[1][0] =  v.z;  S.m[1][1] = 0;     S.m[1][2] = -v.x;
        S.m[2][0] = -v.y;  S.m[2][1] =  v.x;  S.m[2][2] = 0;
        return S;
    }

    public Matrix3 multiply(Matrix3 b) {
        Matrix3 r = new Matrix3();
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                double sum = 0;
                for (int k = 0; k < 3; k++) {
                    sum += this.m[i][k] * b.m[k][j];
                }
                r.m[i][j] = sum;
            }
        }
        return r;
    }
    public Matrix3 add(Matrix3 b) {
        Matrix3 r = new Matrix3();
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                r.m[i][j] = this.m[i][j] + b.m[i][j];
            }
        }
        return r;
    }

    public Matrix3 scale(double s) {
        Matrix3 r = new Matrix3();
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                r.m[i][j] = this.m[i][j] * s;
            }
        }
        return r;
    }

    // Exp(omega_dt) using Rodrigues formula
    public static Matrix3 expSO3(Vector3 omegaDt) {
        double theta = omegaDt.magnitude();

        Matrix3 I = identity();

        // small-angle safe approximation
        if (theta < 1e-9) {
            // Exp(w) ≈ I + skew(w)
            return I.add(skew(omegaDt));
        }

        Vector3 a = omegaDt.scale(1.0 / theta); // unit axis
        Matrix3 K = skew(a);

        double sin = Math.sin(theta);
        double cos = Math.cos(theta);

        // R = I + sin(theta)K + (1-cos(theta))K^2
        Matrix3 K2 = K.multiply(K);
        return I.add(K.scale(sin)).add(K2.scale(1.0 - cos));
    }

    public double[][] raw() {
        return m;
    }
    public Matrix3 orthonormalize() {
        Vector3 c0 = new Vector3(m[0][0], m[1][0], m[2][0]).normalize();
        Vector3 c1 = new Vector3(m[0][1], m[1][1], m[2][1]);
        c1 = c1.sub(c0.scale(c0.dot(c1))).normalize();
        Vector3 c2 = c0.cross(c1);

        Matrix3 R = new Matrix3();
        R.m[0][0]=c0.x; R.m[1][0]=c0.y; R.m[2][0]=c0.z;
        R.m[0][1]=c1.x; R.m[1][1]=c1.y; R.m[2][1]=c1.z;
        R.m[0][2]=c2.x; R.m[1][2]=c2.y; R.m[2][2]=c2.z;
        return R;
    }
    public Matrix3 transpose() {
        double[][] t = new double[3][3];
        for (int i=0;i<3;i++) for (int j=0;j<3;j++) t[i][j] = m[j][i];
        return new Matrix3(t);
    }
    public Vector3 multiply(Vector3 v) {
        if (v == null) throw new IllegalArgumentException("vector cannot be null");

        double x = m[0][0] * v.x + m[0][1] * v.y + m[0][2] * v.z;
        double y = m[1][0] * v.x + m[1][1] * v.y + m[1][2] * v.z;
        double z = m[2][0] * v.x + m[2][1] * v.y + m[2][2] * v.z;

        return new Vector3(x, y, z);
    }
}

