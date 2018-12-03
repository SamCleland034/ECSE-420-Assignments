package ca.mcgill.ecse420.a3.q4;

import java.util.concurrent.Callable;
import java.util.concurrent.Future;
import java.util.stream.IntStream;

/**
 * Calculates and puts result of a single row/column entry in matrix and row
 * entry in vector into an n by n array to prepare for the parallel addition
 *
 * @author Sam Cleland
 *
 */
public class MultiplicationCalculationTask implements Callable<Object> {

	private double result;
	private int row;
	private int start;
	private int end;
	private int level;
	private double[][] matrix = MatrixVectorMultiplication.matrix;
	private double[] vector = MatrixVectorMultiplication.vector;
	private int cores = MatrixVectorMultiplication.cores;

	public MultiplicationCalculationTask(int row, int start, int end, int level) {
		this.row = row;
		this.start = start;
		this.end = end;
		this.level = level;
	}

	@Override
	public Double call() throws Exception {
		if(start == end) {
			MatrixVectorMultiplication.preResult[row][start] = matrix[row][start] * vector[start];
			return null;
		}

		if(level < Math.log10(cores) / Math.log10(2)) {
			Future<Object>[] tasks = new Future[2];
			int floor = (int) Math.floor((start + end) / 2);
			int ceiling = (int) Math.ceil((start + end) / 2);
			tasks[0] = MatrixVectorMultiplication.service.submit(new MultiplicationCalculationTask(row, start, floor, level + 1));
			tasks[1] = MatrixVectorMultiplication.service.submit(new MultiplicationCalculationTask(row, ceiling == floor ? ceiling + 1 : ceiling, end, level + 1));
			tasks[0].get();
			tasks[1].get();
			return null;
		}

		IntStream.range(start, end + 1).forEach(i ->
		MatrixVectorMultiplication.preResult[row][i] = matrix[row][i] * vector[i]);
		return null;
	}

}
