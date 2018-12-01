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
		if (startMatrix == endMatrix && startVector == endVector) {
			MatrixVectorMultiplication.result[startMatrix] += matrix[startMatrix][startVector] * vector[startVector];

			return null;
		}

		if (level < (int) Math.log10(cores) / Math.log(2)) {
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

		IntStream.range(startMatrix, endMatrix + 1).forEach(i -> IntStream.range(startVector, endVector + 1)
				.forEach(j -> MatrixVectorMultiplication.result[i] += matrix[i][j] * vector[j]));

		return null;
	}

}
