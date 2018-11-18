package ca.mcgill.ecse420.a3.q4;

import java.util.concurrent.Callable;
import java.util.concurrent.Future;
import java.util.stream.IntStream;

public class MatrixBreakdownTask implements Callable<Object> {
	private int level;
	private int start;
	private int end;
	private int cores = MatrixVectorMultiplication.cores;
	private double[][] matrix = MatrixVectorMultiplication.matrix;
	private double[] vector = MatrixVectorMultiplication.vector;

	public MatrixBreakdownTask(int start, int end, int level) {
		this.start = start;
		this.end = end;
		this.level = level;
	}

	@Override
	public Object call() throws Exception {
		if (start == end) {
			MatrixVectorMultiplication.result[start] = MatrixVectorMultiplication.service
					.submit(new CalculationTask(start, 0, vector.length - 1, 0)).get();

			return null;
		}

		if (level < (int) Math.log10(cores) / Math.log(2)) {
			Future<Object>[] tasks = new Future[2];
			tasks[0] = MatrixVectorMultiplication.service
					.submit(new MatrixBreakdownTask(start, (start + end) / 2, level + 1));
			tasks[1] = MatrixVectorMultiplication.service
					.submit(new MatrixBreakdownTask((start + end) / 2, end, level + 1));
			tasks[0].get();
			tasks[1].get();
			return null;

		}

		IntStream.range(start, end).forEach(i -> IntStream.range(0, vector.length)
				.forEach(j -> MatrixVectorMultiplication.result[i] += matrix[i][j] * vector[j]));

		return null;
	}

}
