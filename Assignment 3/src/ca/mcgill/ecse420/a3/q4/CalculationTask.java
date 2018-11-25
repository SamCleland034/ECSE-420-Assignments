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
		if (start == end) {
			return matrix[row][start] * vector[start];
		}

		if (level < (int) Math.log10(cores) / Math.log(2)) {
			Future<Double>[] values = new Future[2];
			values[0] = MatrixVectorMultiplication.service
					.submit(new CalculationTask(row, start, (int) Math.floor((start + end) / 2), level + 1));
			values[1] = MatrixVectorMultiplication.service
					.submit(new CalculationTask(row, (int) Math.ceil((start + end) / 2), end, level + 1));
			return values[0].get() + values[1].get();
		}

		rowValue = Double.valueOf(0d);
		IntStream.range(start, end + 1).forEach(i -> rowValue += matrix[row][i] * vector[i]);
		return rowValue;
	}

}
