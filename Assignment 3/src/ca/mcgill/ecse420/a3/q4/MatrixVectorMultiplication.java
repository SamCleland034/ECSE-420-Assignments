package ca.mcgill.ecse420.a3.q4;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.IntStream;

/**
 * Test program to test the functionality of sequential and parallel matrix vector multiplication
 *
 * @author Sam Cleland
 *
 */
public class MatrixVectorMultiplication {

	public static ExecutorService service;
	public static double[] result;
	public static double[][] matrix;
	public static double[] vector;
	public static double[][] preResult;
	public static int cores = 1;
	public static Logger logger = Logger.getLogger(MatrixVectorMultiplication.class.getName());

	public static void main(String[] args) throws InterruptedException {
		service = Executors.newCachedThreadPool();
		int num = 2000;
		matrix = generateRandomMatrix(num, num);
		vector = generateRandomVector(num);
		result = new double[num];
		preResult = new double[num][num];
		long start = System.currentTimeMillis();
		paraMultiply();
		long diff1 = System.currentTimeMillis() - start;
		System.out.println("Sequential: " + diff1 + " milliseconds");
		result = new double[num];
		cores = 2;
		long start2 = System.currentTimeMillis();
		paraMultiply();
		long diff2 = System.currentTimeMillis() - start2;
		System.out.println("Parallel with " + cores + " cores: " + diff2 + " milliseconds");
		System.out.println("Speedup with " + cores + " cores: " + diff1 / (double) diff2);
		service.shutdown();

	}

	private static void printResult() {
		IntStream.range(0, result.length).forEach(i ->
		System.out.print(result[i] + " "));
		System.out.println();
	}

	public static double[] generateRandomVector(int numRows) {
		double vector[] = new double[numRows];
		IntStream.range(0, numRows).forEach(i -> vector[i] = (int) (Math.random() * 10.0));
		return vector;
	}

	/**
	 * Populates a matrix of given size with randomly generated integers between
	 * 0-10.
	 *
	 * @param numRows number of rows
	 * @param numCols number of cols
	 * @return matrix
	 */
	private static double[][] generateRandomMatrix(int numRows, int numCols) {
		double matrix[][] = new double[numRows][numCols];
		for (int row = 0; row < numRows; row++) {
			for (int col = 0; col < numCols; col++) {
				matrix[row][col] = ((int) (Math.random() * 10.0));
			}
		}

		return matrix;
	}

	public static void paraMultiply() {
		try {
			Future<?> value = service.submit(new MatrixBreakdownTask(0, matrix.length - 1, 0));
			value.get();
			Future<?> calculation = service.submit(new RowAdditionCalculationTask(0, matrix.length - 1, 0));
			calculation.get();
		} catch (InterruptedException intx) {
			logger.log(Level.SEVERE, intx.getMessage());
			intx.printStackTrace();
		} catch (ExecutionException exx) {
			logger.log(Level.SEVERE, exx.getMessage());
			exx.printStackTrace();
		}
	}

	public static void seqMultiply() {
		int rows = matrix.length;
		int columns = matrix[0].length;
		for (int i = 0; i < rows; i++) {
			double sum = 0;
			for (int j = 0; j < columns; j++) {
				sum += matrix[i][j] * vector[j];
			}

			result[i] = sum;
		}
	}

	public static void seqMultiplyStream() {
		IntStream.range(0, vector.length).forEach(i -> IntStream.range(0, vector.length)
				.forEach(j -> result[i] += matrix[i][j]*vector[j]));
	}
}
