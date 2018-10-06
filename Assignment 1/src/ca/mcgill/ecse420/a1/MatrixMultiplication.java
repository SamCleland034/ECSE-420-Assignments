package ca.mcgill.ecse420.a1;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Level;
import java.util.logging.Logger;

public class MatrixMultiplication {

	private static final int NUMBER_THREADS = 2;
	private static final int MATRIX_SIZE = 500;
	private static final int portion = MATRIX_SIZE / NUMBER_THREADS;
	private static double[][] a;
	private static double[][] b;
	private static double[][] result;
	private static Logger logger = Logger.getLogger(MatrixMultiplication.class.getName());

	public static void main(String[] args) {

		// Generate two random matrices, same size
		a = generateRandomMatrix(MATRIX_SIZE, MATRIX_SIZE);
		b = generateRandomMatrix(MATRIX_SIZE, MATRIX_SIZE);
		result = new double[MATRIX_SIZE][MATRIX_SIZE];
		try {
			timeMultiplication();
		} catch (InterruptedException ix) {
			logger.log(Level.SEVERE, ix.getMessage());
		}

		System.exit(0);
	}

	private static void timeMultiplication() throws InterruptedException {
		System.out.println("Starting sequential matrix multiplication...");
		long current = System.currentTimeMillis();
		sequentialMultiplyMatrix(a, b);
		long difference = System.currentTimeMillis() - current;
		System.out.println("Sequential time: " + difference + " ms");
		System.out.println("Starting parallel matrix multiplication with " + NUMBER_THREADS + " threads...");
		current = System.currentTimeMillis();
		parallelMultiplyMatrix(a, b);
		long secondDifference = System.currentTimeMillis() - current;
		System.out.println("Parallel time: " + secondDifference + " ms");
		System.out.println("Speedup = " + (float) difference / secondDifference);
	}

	/**
	 * Returns the result of a sequential matrix multiplication The two matrices are
	 * randomly generated
	 *
	 * @param a is the first matrix
	 * @param b is the second matrix
	 * @return the result of the multiplication
	 */
	public static double[][] sequentialMultiplyMatrix(double[][] a, double[][] b) {
		int aRows = a.length;
		int aColumns = a[0].length;
		int bRows = b.length;
		int bColumns = b[0].length;

		if (aColumns != bRows) {
			throw new IllegalArgumentException("A:Rows: " + aColumns + " did not match B:Columns " + bRows + ".");
		}

		double[][] c = new double[aRows][bColumns];

		for (int i = 0; i < aRows; i++) {
			for (int j = 0; j < bColumns; j++) {
				for (int k = 0; k < aColumns; k++) {
					c[i][j] = a[i][k] * b[k][j];
				}
			}
		}

		return c;
	}

	/**
	 * Returns the result of a concurrent matrix multiplication The two matrices are
	 * randomly generated
	 *
	 * @param a is the first matrix
	 * @param b is the second matrix
	 * @return the result of the multiplication
	 * @throws InterruptedException
	 */
	public static double[][] parallelMultiplyMatrix(double[][] a, double[][] b) throws InterruptedException {
		ExecutorService executor = Executors.newFixedThreadPool(NUMBER_THREADS);

		// latch to cause main thread to wait for other threads to finish
		CountDownLatch latch = new CountDownLatch(NUMBER_THREADS);
		for (int i = 0; i < NUMBER_THREADS; i++) {

			// execute a new multiply task
			executor.execute(new MultiplyTask(i, latch));
		}

		// pause execution here until threads countdown the latch
		latch.await();
		return result;

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

	private static class MultiplyTask implements Runnable {

		private int index;
		private CountDownLatch latch;

		public MultiplyTask(int index, CountDownLatch latch) {
			// threads only take a portion of the work
			this.index = index * portion;
			this.latch = latch;
		}

		@Override
		public void run() {
			int aRows = a.length;
			int bColumns = b[0].length;

			// only take portion of the rows based on the thread's index
			// no race conditions since the threads are updating different parts of the
			// arrays
			for (int i = index; i < index + portion; i++) {
				for (int j = 0; j < aRows; j++) {
					for (int k = 0; k < bColumns; k++) {
						result[i][j] = a[i][k] * b[k][j];
					}
				}
			}

			latch.countDown();
		}
	}
}
