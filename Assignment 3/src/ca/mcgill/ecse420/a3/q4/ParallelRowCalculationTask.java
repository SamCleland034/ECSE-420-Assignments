package ca.mcgill.ecse420.a3.q4;

import java.util.concurrent.Callable;
import java.util.concurrent.Future;
import java.util.stream.IntStream;

public class ParallelRowCalculationTask implements Callable<Object> {
	private int start;
	private int end;
	private int cores = MatrixVectorMultiplication.cores;
	private int level;

	public ParallelRowCalculationTask(int startRow, int endRow, int level) {
		this.start = startRow;
		this.end = endRow;
		this.level = level;
	}

	@Override
	public Object call() throws Exception {
		// Start merging at that row
		if(start == end) {
			Future<Double> result = MatrixVectorMultiplication.service.submit(new MergeCalculationTask(start, 0, MatrixVectorMultiplication.matrix.length - 1, level));
			MatrixVectorMultiplication.result[start] = result.get();
			return null;
		}

		// Divide rows by 2
		if(level < Math.log10(cores) / Math.log10(2)) {
			Future<Object>[] tasks = new Future[2];
			int floor = (int) Math.floor((start + end) / 2);
			int ceiling = (int) Math.ceil((start + end) / 2);
			tasks[0] = MatrixVectorMultiplication.service.submit(new ParallelRowCalculationTask(start, floor, level + 1));
			tasks[1] = MatrixVectorMultiplication.service.submit(new ParallelRowCalculationTask(ceiling == floor ? ceiling + 1 : ceiling, end, level + 1));
			tasks[0].get();
			tasks[1].get();
			return null;
		}

		// Sequential Implementation
		IntStream.range(start, end + 1).forEach(i ->
		IntStream.range(0, MatrixVectorMultiplication.vector.length).forEach(j ->
		MatrixVectorMultiplication.result[i] += MatrixVectorMultiplication.preResult[i][j]));
		return null;
	}
}
