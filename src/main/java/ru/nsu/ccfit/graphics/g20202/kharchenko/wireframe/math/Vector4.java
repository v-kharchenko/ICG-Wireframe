package ru.nsu.ccfit.graphics.g20202.kharchenko.wireframe.math;

/**
 * Describes 4-component vector with specific functionality for 3D graphics.
 */
public class Vector4 {
    public double x;
    public double y;
    public double z;
    public double w;

    public Vector4(double x, double y, double z, double w) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.w = w;
    }
    public Vector4(double[] arrayVector) {
        if (arrayVector.length < 4)
            return;

        x = arrayVector[0];
        y = arrayVector[1];
        z = arrayVector[2];
        w = arrayVector[3];
    }
    public Vector4() {}

    public Vector4 multiply(Matrix4x4 matrix4x4, boolean wCorrect) {
        Vector4 resultVector = new Vector4();
        double[][] matrix = matrix4x4.matrix;

        resultVector.x = this.x * matrix[0][0] + this.y * matrix[1][0] + this.z * matrix[2][0] + this.w * matrix[3][0];
        resultVector.y = this.x * matrix[0][1] + this.y * matrix[1][1] + this.z * matrix[2][1] + this.w * matrix[3][1];
        resultVector.z = this.x * matrix[0][2] + this.y * matrix[1][2] + this.z * matrix[2][2] + this.w * matrix[3][2];
        resultVector.w = this.x * matrix[0][3] + this.y * matrix[1][3] + this.z * matrix[2][3] + this.w * matrix[3][3];

        if (wCorrect) {
            resultVector.correctW();
        }

        return resultVector;
    }

    /**
     * Changes vector's length to 1 by dividing each component by current length.
     */
    public void normalize() {
        double divider = Math.sqrt(x*x + y*y + z*z);
        x /= divider;
        y /= divider;
        z /= divider;
    }

    /**
     * Divides each component by w. In 3D graphics the vector represents the vertex correctly, if the 4th component w is equal to 1.
     */
    public void correctW() {
        if (this.w != 1.0) {
            this.x /= this.w;
            this.y /= this.w;
            this.z /= this.w;
            this.w = 1.0;
        }
    }

    /**
     * Convert vector's coordinates to array.
     * @return double[] {x, y, z, w}
     */
    public double[] getAsArray() {
        return new double[] {x, y, w, z};
    }

    @Override
    public String toString() {
        return "(" + x + ", " + y + ", " + z + ", " + w + ")";
    }
}
