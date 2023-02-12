package net.soko.pyrotechnics.math;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

public class SokoMath {


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


    public static int[] @NotNull [] convolveMatrixGaussian(int[][] inputMatrix, double variance) {
        if (inputMatrix.length % 2 == 0) {
            throw new IllegalArgumentException("Input matrix must have an odd number of rows and columns");
        }

        double[][] weightMatrix = generateWeightMatrix(inputMatrix.length, variance);

        int[][] smoothedFieriness = new int[5][5];
        for (int x = 0; x < 5; x++) {
            for (int z = 0; z < 5; z++) {
                double smoothedValue = weightMatrix[x][z] * inputMatrix[x][z];
                smoothedFieriness[x][z] = (int) smoothedValue;
            }
        }
        return smoothedFieriness;
    }


    public static int getMatrixSum(int[] @NotNull [] inputMatrix) {
        int sum = 0;
        for (int[] row : inputMatrix) {
            for (int ints : row) {
                sum += ints;
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
}
