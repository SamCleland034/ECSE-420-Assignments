package ca.mcgill.ecse420.a3.q4;

import java.util.concurrent.Callable;
import java.util.concurrent.Future;
import java.util.stream.IntStream;

public class MergeCalculationTask implements Callable<Double> {

	private int row;
	private int start;
	private int end;
	private int cores = MatrixVectorMultiplication.cores;
	private int level;
	private double result;
	private double[][] preResult = MatrixVectorMultiplication.preResult;

	public MergeCalculationTask(int row, int start, int end, int level) {
		this.row = row;
		this.start = start;
		this.end = end;
		this.level = level;
	}

	@Override
	public Double call() throws Exception {
		// Base cases
		if(end - start == 1) {
			return preResult[row][start] + preResult[row][end];
		}

		if(end == start) {
			return preResult[row][start];
		}

		// Divide until we get to 2 elements next to each other or an element by itself
		if (level < Math.log10(cores) / Math.log10(2)) {
			Future<Double>[] tasks = new Future[2];
			int floor = (int) Math.floor((start + end) / 2);
			int ceiling = (int) Math.ceil((start + end) / 2);
			tasks[0] = MatrixVectorMultiplication.service.submit(new MergeCalculationTask(row, start, floor, level + 1));
			// Have to check if ceiling variable is odd or even so we don't add the same value twice
			tasks[1] = MatrixVectorMultiplication.service.submit(new MergeCalculationTask(row, ceiling == floor ? ceiling + 1: ceiling, end, level + 1));
			return tasks[0].get() + tasks[1].get();
		}

		// Sequential Implementation
		result = 0;
		IntStream.range(start, end + 1).forEach(i ->
		result += preResult[row][i]);
		return result;
		}
}
