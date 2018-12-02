package ca.mcgill.ecse420.a3.q4;

import java.util.concurrent.Callable;
import java.util.concurrent.Future;
import java.util.stream.IntStream;

public class MatrixBreakdownTask implements Callable<Object> {
	private int level;
	private int startMatrix;
	private int endMatrix;
	private int cores = MatrixVectorMultiplication.cores;
	private double[][] matrix = MatrixVectorMultiplication.matrix;
	private double[] vector = MatrixVectorMultiplication.vector;
	private int startVector;
	private int endVector;

	public MatrixBreakdownTask(int startMatrix, int endMatrix, int startVector, int endVector, int level) {
		this.startMatrix = startMatrix;
		this.endMatrix = endMatrix;
		this.startVector = startVector;
		this.endVector = endVector;
		this.level = level;
	}

	@Override
	public Object call() throws Exception {
		// Base case, perform computation for 1 element of the preResult matrix to get ready for parallel addition afterwards
		if (startMatrix == endMatrix && startVector == endVector) {
			MatrixVectorMultiplication.preResult[startMatrix][startVector] = matrix[startMatrix][startVector] * vector[startVector];
			return null;
		}

		// Parallel portion, dictated by how many threads are used
		if (level < Math.log10(cores) / Math.log10(2)) {
			Future<Object>[] tasks = new Future[4];
			tasks[0] = MatrixVectorMultiplication.service
					.submit(new MatrixBreakdownTask(startMatrix, (int) Math.floor((startMatrix + endMatrix) / 2), startVector, (int) Math.floor((startVector + endVector) / 2), level + 1));
			tasks[1] = MatrixVectorMultiplication.service
					.submit(new MatrixBreakdownTask(startMatrix, (int) Math.floor((startMatrix + endMatrix) / 2), (int) Math.ceil((startVector + endVector) / 2), endVector, level + 1));
			tasks[2] = MatrixVectorMultiplication.service
					.submit(new MatrixBreakdownTask((int) Math.ceil((startMatrix + endMatrix) / 2), endMatrix, startVector, (int) Math.floor((startVector + endVector) / 2), level + 1));
			tasks[3] = MatrixVectorMultiplication.service
					.submit(new MatrixBreakdownTask((int) Math.ceil((startMatrix + endMatrix) / 2), endMatrix, (int) Math.ceil((startVector + endVector) / 2), endVector, level + 1));
			tasks[0].get();
			tasks[1].get();
			tasks[2].get();
			tasks[3].get();
			return null;
		}

		// Sequential portion once all of the cores are in use
		IntStream.range(startMatrix, endMatrix + 1).forEach(i -> IntStream.range(startVector, endVector + 1)
				.forEach(j -> MatrixVectorMultiplication.preResult[i][j] = matrix[i][j] * vector[j]));

		return null;
	}

}
