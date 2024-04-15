import java.util.Random;
import java.util.concurrent.locks.ReentrantLock;

public class MathUtils {
    public static void generateMatrix(float[][] matrix) {
        for (float[] floats : matrix) {
            generateVector(floats);
        }
    }

    public static void generateVector(float[] vector) {
        Random r = new Random();
        for (int i = 0; i < vector.length; i++) {
            vector[i] = 4 + r.nextFloat() * 5;
        }
    }

    public static float[] addVectors(float[] A, float[] B) {
        float[] result = new float[A.length];
        for (int i = 0; i < A.length; i++) {
            result[i] = A[i] + B[i];
        }
        return result;
    }

    public static float[] multiplyMatrixOnVector(float[][] MA, float[] A) {
        ReentrantLock lock = new ReentrantLock(true);
        int rows = MA[0].length;
        int cols = A.length;
        float[] result = new float[rows];
        for (int i = 0; i < rows; i++) {
            float sum = 0;
            for (int j = 0; j < cols; j++) {
                lock.lock();
                try {
                    sum += MA[i][j] * A[j];
                } finally {
                    lock.unlock();
                }
            }
            result[i] = sum;
        }
        return result;
    }
    public static void multiplyMatrices(float[][] A, float[][] B, float[][] result, int start, int end) {
        ReentrantLock lock = new ReentrantLock(true);
        for (int i = 0; i < A.length; i++) {
            for (int j = start; j < end; j++) {
                for (int k = 0; k < B.length; k++) {
                    lock.lock();
                    try {
                        result[i][j] = KahanAlgorithm(new float[] { result[i][j], A[i][k] * B[k][j] });
                    } finally {
                        lock.unlock();
                    }
                }
            }
        }
    }

    public static void multiplyMatrixByValue(float[][] A, float a) {
        ReentrantLock lock = new ReentrantLock(true);
        for (int i = 0; i < A.length; i++) {
            for (int j = 0; j < A[i].length; j++) {
                lock.lock();
                try {
                    A[i][j] = a * A[i][j];
                } finally {
                    lock.unlock();
                }
            }
        }
    }

    public static float findMinValue(float[] A) {
        float minValue = A[0];
        for (int i = 1; i < A.length; i++) {
            minValue = Math.min(minValue, A[i]);
        }
        return minValue;
    }

    public static void addMatrices(float[][] A, float[][] B, float[][] C) {
        ReentrantLock lock = new ReentrantLock(true);
        for (int i = 0; i < A.length; i++) {
            for (int j = 0; j < A[i].length; j++) {
                lock.lock();
                try {
                    C[i][j] = A[i][j] + B[i][j];
                } finally {
                    lock.unlock();
                }
            }
        }
    }

    public static float KahanAlgorithm(float[] obj) {
        float sum = 0.0f;
        float err = 0.0f;
        for (float item : obj) {
            float y = item - err;
            float t = sum + y;
            err = (t - sum) - y;
            sum = t;
        }
        return sum;
    }
}
