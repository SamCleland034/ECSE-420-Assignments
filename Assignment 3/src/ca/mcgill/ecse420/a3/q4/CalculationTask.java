package ca.mcgill.ecse420.a3.q4;

import java.util.concurrent.Callable;
import java.util.concurrent.Future;
import java.util.stream.IntStream;

public class CalculationTask implements Callable<Double> {

	private int level;
	private int start;
	private int end;
	private int row;
	private static double[][] matrix = MatrixVectorMultiplication.matrix;
	private static double[] vector = MatrixVectorMultiplication.vector;
	private static int cores = MatrixVectorMultiplication.cores;
	Double rowValue;

	public CalculationTask(int row, int start, int end, int level) {
		this.row = row;
		this.start = start;
		this.end = end;
		this.level = level;
	}

	@Override
	public Double call() throws Exception {
		if(start == end) {
			return matrix[row][start] * vector[start];
		}

		if(level < Math.log(cores) / Math.log(2)) {
			Future<Double>[] values = new Future[2];
			values[0] = MatrixVectorMultiplication.service.submit(new CalculationTask(row, start, (start + end) / 2, 0));
			values[1] = MatrixVectorMultiplication.service.submit(new CalculationTask(row, (start + end) / 2, end, 0));
			return values[0].get() + values[1].get();
		}

		rowValue = Double.valueOf(0d);
		IntStream.range(start, end).forEach(i -> rowValue += matrix[row][i] * vector[i]);
		return rowValue;
	}

}
