package ca.mcgill.ecse420.a1;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ForkJoinTask;

public class MatrixMultiplication {
	
	private static final int NUMBER_THREADS = 100;
	private static final int MATRIX_SIZE = 1000;
	private static final int portion = MATRIX_SIZE / NUMBER_THREADS;
	private static double[][] a;
	private static double[][] b;
	private static double[][] result;
	
        public static void main(String[] args) {
		
		// Generate two random matrices, same size
        a = generateRandomMatrix(MATRIX_SIZE, MATRIX_SIZE);
		b = generateRandomMatrix(MATRIX_SIZE, MATRIX_SIZE);
		result = new double[MATRIX_SIZE][MATRIX_SIZE];
		try {
			timeMultiplication();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		//sequentialMultiplyMatrix(a, b);
		//parallelMultiplyMatrix(a, b);	
	}
	
	private static void timeMultiplication() throws InterruptedException {
			long current = System.currentTimeMillis();
			//sequentialMultiplyMatrix(a, b);
			parallelMultiplyMatrix(a, b);
			long difference = System.currentTimeMillis() - current;
			System.out.println("Time: " + difference + " ms");
		}

	/**
	 * Returns the result of a sequential matrix multiplication
	 * The two matrices are randomly generated
	 * @param a is the first matrix
	 * @param b is the second matrix
	 * @return the result of the multiplication
	 * */
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
	 * Returns the result of a concurrent matrix multiplication
	 * The two matrices are randomly generated
	 * @param a is the first matrix
	 * @param b is the second matrix
	 * @return the result of the multiplication
	 * @throws InterruptedException 
	 * */
        public static double[][] parallelMultiplyMatrix(double[][] a, double[][] b) throws InterruptedException {
            ExecutorService executor = Executors.newFixedThreadPool(NUMBER_THREADS);
        	List<MultiplyTask> tasks = new ArrayList<MultiplyTask>();
        	CountDownLatch latch = new CountDownLatch(NUMBER_THREADS);
        	for(int i = 0; i < NUMBER_THREADS; i++) {
        		tasks.add(new MultiplyTask(i, latch));
        	}

        	for(MultiplyTask task : tasks) {
        		executor.execute(task);
        	}

        	latch.await();
        	return result;
		
	}
        
        /**
         * Populates a matrix of given size with randomly generated integers between 0-10.
         * @param numRows number of rows
         * @param numCols number of cols
         * @return matrix
         */
        private static double[][] generateRandomMatrix (int numRows, int numCols) {
             double matrix[][] = new double[numRows][numCols];
        for (int row = 0 ; row < numRows ; row++ ) {
            for (int col = 0 ; col < numCols ; col++ ) {
                matrix[row][col] = (double) ((int) (Math.random() * 10.0));
            }
        }
        return matrix;
    }
        
		private static class MultiplyTask implements Runnable {
        	
			private int index;
			private CountDownLatch latch;
        	public MultiplyTask(int index, CountDownLatch latch) {
        		this.index = index * portion;
        		this.latch = latch;
        	}

			@Override
			public void run() {
        		int aRows = a.length;
        		int bColumns = b[0].length;

        		for(int i = index; i < index + portion ; i++) {
        			for(int j = 0; j < aRows; j++) {
        				for(int k = 0; k < bColumns; k++) {
                            result[i][j] = a[i][k] * b[k][j];
        				}
        			}        			
        		}
        		
        		latch.countDown();
			}
        }
}
