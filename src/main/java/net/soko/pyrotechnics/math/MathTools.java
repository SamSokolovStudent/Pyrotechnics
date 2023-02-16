package net.soko.pyrotechnics.math;

import java.util.ArrayList;

public class MathTools {


    public static double gaussianModel(double x, double y, double variance) {
        return (1 / (2 * Math.PI * Math.pow(variance, 2)))
                * Math.exp(-((Math.pow(x, 2) + Math.pow(y, 2)) / (2 * Math.pow(variance, 2))));
    }


    public static double[][] generateWeightMatrix(int diameter, double variance) {
        double[][] weightMatrix = new double[diameter][diameter];
        double sum = 0;
        for (int i = 0; i < weightMatrix.length; i++) {
            for (int j = 0; j < weightMatrix[i].length; j++) {
                weightMatrix[i][j] = gaussianModel(i - diameter / 2, j - diameter / 2, variance);
                sum += weightMatrix[i][j];
            }
        }

        for (int i = 0; i < weightMatrix.length; i++) {
            for (int j = 0; j < weightMatrix[i].length; j++) {
                weightMatrix[i][j] /= sum;
            }
        }
        return weightMatrix;
    }


    public static float convolveMatrixGaussian(float[][] inputMatrix) {
        if (inputMatrix.length % 2 == 0) {
            throw new IllegalArgumentException("Input matrix must have an odd number of rows and columns");
        }

        float[][] weightMatrix = {
                {0.014418818362460818f, 0.02808402335634917f, 0.035072700805593486f, 0.02808402335634917f, 0.014418818362460818f},
                {0.02808402335634917f, 0.054700208300935874f, 0.06831229327078019f, 0.054700208300935874f, 0.02808402335634917f},
                {0.035072700805593486f, 0.06831229327078019f, 0.08531173019012506f, 0.06831229327078019f, 0.035072700805593486f},
                {0.02808402335634917f, 0.054700208300935874f, 0.06831229327078019f, 0.054700208300935874f, 0.02808402335634917f},
                {0.014418818362460818f, 0.02808402335634917f, 0.035072700805593486f, 0.02808402335634917f, 0.014418818362460818f}};

        float sum = 0;
        for (int x = 0; x < 5; x++) {
            for (int z = 0; z < 5; z++) {
                double smoothedValue = weightMatrix[x][z] * inputMatrix[x][z];
                sum += smoothedValue;
            }
        }

        return sum;
    }


    public static double standardDeviation(ArrayList<Double> values) {
        double mean = 0;
        for (double value : values) {
            mean += value;
        }
        mean /= values.size();

        double sum = 0;
        for (double value : values) {
            sum += Math.pow(value - mean, 2);
        }
        return Math.sqrt(sum / values.size());
    }

    /**
     *
     * Maps one range of numbers to another. Incredibly useful function for lazy people like me.
     * @param fromMin The minimum of the range you're mapping from.
     * @param fromMax The maximum of the range you're mapping from.
     * @param toMin The minimum of the range you're mapping to.
     * @param toMax The maximum of the range you're mapping to.
     * @param value The value you're mapping.
     * @return The value, mapped to the second range.
     * @author birse/cappin
     * @apiNote Taken from Cappin's code. "MathUtils" class. Thanks Cappin!
     */
    public static float mapRange(float fromMin, float fromMax, float toMin, float toMax, float value) {
        return toMin + (((value - fromMin) * (toMax - toMin))/(fromMax - fromMin));
    }
}
