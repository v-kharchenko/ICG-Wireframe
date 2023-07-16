package ru.nsu.ccfit.graphics.g20202.kharchenko.wireframe.math;

import java.util.Arrays;

import static java.lang.Math.*;

/**
 * An implementation of 4x4 matrices with specific functionality for 3D graphics.
 */
public class Matrix4x4 {

    // Matrix of values
    public double[][] matrix = new double[4][4];

    // Identity matrix
    public Matrix4x4() {
        matrix = new double[][] {{1, 0, 0, 0},
                {0, 1, 0, 0},
                {0, 0, 1, 0},
                {0, 0, 0, 1}};
    }

    // Matrix constructor based on array
    public Matrix4x4(double[][] matrix) {
        if (matrix.length != 4 || matrix[0].length != 4)
            return;

        for (int i = 0; i < 4; i++) {
            this.matrix[i] = Arrays.copyOf(matrix[i], 4);
        }
    }

    /**
     * Multiply each element by value.
     * @param value - value to multiply the matrix by.
     * @return new Matrix4x4
     */
    public Matrix4x4 multiply(double value) {
        double[][] newMatrix = new double[4][4];
        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 4; j++) {
                newMatrix[i][j] = this.matrix[i][j] * value;
            }
        }

        return new Matrix4x4(newMatrix);
    }

    /**
     * Multiply by Vector4
     * @param vector vector to multiply this matrix by
     * @param wCorrect true if the resulting vector should be corrected by the 4th component w, false otherwise.
     * @return new vector - result of multiplication
     */
    public Vector4 multiply(Vector4 vector, boolean wCorrect) {
        Vector4 resultVector = new Vector4();

        resultVector.x = vector.x * matrix[0][0] + vector.y * matrix[0][1] + vector.z * matrix[0][2] + vector.w * matrix[0][3];
        resultVector.y = vector.x * matrix[1][0] + vector.y * matrix[1][1] + vector.z * matrix[1][2] + vector.w * matrix[1][3];
        resultVector.z = vector.x * matrix[2][0] + vector.y * matrix[2][1] + vector.z * matrix[2][2] + vector.w * matrix[2][3];
        resultVector.w = vector.x * matrix[3][0] + vector.y * matrix[3][1] + vector.z * matrix[3][2] + vector.w * matrix[3][3];

        if (wCorrect) {
            resultVector.correctW();
        }

        return resultVector;
    }

    /**
     * Multiply this matrix by another matrix.
     * @param other the matrix to multiply this matrix by
     * @return new matrix - result of multiplication
     */
    public Matrix4x4 multiply(Matrix4x4 other) {
        // Copy matrix
        double[][] newMatrixArray = new double[4][4];
        for (int i = 0; i < 4; i++) {
            Arrays.fill(newMatrixArray[i], 0);
        }

        // Multiply matrices
        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 4; j++) {
                for (int k = 0; k < 4; k++) {
                    newMatrixArray[i][k] += this.matrix[i][j] * other.matrix[j][k];
                }
            }
        }

        return new Matrix4x4(newMatrixArray);
    }

    // Affine operations
    public Matrix4x4 translate(double dx, double dy, double dz) {
        Matrix4x4 translationMatrix = Matrix4x4.getTranslationMatrix(dx, dy, dz);

        return translationMatrix.multiply(this);
    }
    public Matrix4x4 rotate(Vector4 axis, double angle) {
        Matrix4x4 rotationMatrix = Matrix4x4.getRotationMatrix(axis, angle);

        return rotationMatrix.multiply(this);
    }
    public Matrix4x4 scale(double xScale, double yScale, double zScale) {
        Matrix4x4 scaleMatrix = Matrix4x4.getScaleMatrix(xScale, yScale, zScale);

        return scaleMatrix.multiply(this);
    }

    // Matrices that describe the affine operations
    private static Matrix4x4 getTranslationMatrix(double dx, double dy, double dz) {
        return new Matrix4x4(new double[][] {{1, 0, 0, dx},
                {0, 1, 0, dy},
                {0, 0, 1, dz},
                {0, 0, 0, 1}});
    }
    public static Matrix4x4 getScaleMatrix(double xScale, double yScale, double zScale) {
        return new Matrix4x4(new double[][] {{xScale, 0, 0, 0},
                {0, yScale, 0, 0},
                {0, 0, zScale, 0},
                {0, 0, 0, 1}});
    }
    public static Matrix4x4 getRotationMatrix(Vector4 axis, double angleDegrees) {
        axis.normalize();
        double x = axis.x;
        double y = axis.y;
        double z = axis.z;
        double angle = angleDegrees * 2 * PI / 360;
        return new Matrix4x4(new double[][] {{cos(angle) + (1 - cos(angle)) * x * x, (1 - cos(angle)) * x * y - sin(angle) * z, (1 - cos(angle)) * x * z + sin(angle) * y, 0},
                                            {(1 - cos(angle)) * x * y + sin(angle) * z, cos(angle) + (1 - cos(angle)) * y * y, (1 - cos(angle)) * y * z - sin(angle) * x, 0},
                                            {(1 - cos(angle)) * x * z - sin(angle) * y, (1 - cos(angle)) * y * z + sin(angle) * x, cos(angle) + (1 - cos(angle)) * z * z, 0},
                                            {0                                      , 0                                          , 0                                    , 1}});
    }

    @Override
    public String toString() {
        StringBuilder matrixString = new StringBuilder();
        for (int i = 0; i < 4; i++) {
            matrixString.append(Arrays.toString(matrix[i]));
            matrixString.append("\n");
        }
        return matrixString.toString();
    }
}
