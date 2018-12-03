package ca.mcgill.ecse420.a3.q4;

import java.util.concurrent.Callable;
import java.util.concurrent.Future;
import java.util.stream.IntStream;

/**
 * Breaks down the matrix until there is only 1 row and then calls MultiplicationCalculationTask
 * to calculate the elements that will be in that row in the preResult matrix
 *
 * @author Sam Cleland
 *
 */
public class MatrixBreakdownTask implements Callable<Object> {
	private int level;
	private int startMatrix;
	private int endMatrix;
	private int cores = MatrixVectorMultiplication.cores;
	private double[][] matrix = MatrixVectorMultiplication.matrix;
	private double[] vector = MatrixVectorMultiplication.vector;

	public MatrixBreakdownTask(int startMatrix, int endMatrix, int level) {
		this.startMatrix = startMatrix;
		this.endMatrix = endMatrix;
		this.level = level;
	}

	@Override
	public Object call() throws Exception {
		// Base case, perform computation for 1 element of the preResult matrix to get ready for parallel addition afterwards
		if (startMatrix == endMatrix) {
			MatrixVectorMultiplication.service.submit(new MultiplicationCalculationTask(startMatrix, 0, matrix.length - 1, level)).get();
			return null;
		}

		// Parallel portion, dictated by how many threads are used
		if (level < Math.log10(cores) / Math.log10(2)) {
			Future<Object>[] tasks = new Future[2];
			int ceilingMatrix = (int) Math.ceil((startMatrix + endMatrix) / 2);
			int floorMatrix = (int) Math.floor((startMatrix + endMatrix) / 2);
			tasks[0] = MatrixVectorMultiplication.service
					.submit(new MatrixBreakdownTask(startMatrix, floorMatrix, level + 1));
			tasks[1] = MatrixVectorMultiplication.service
					.submit(new MatrixBreakdownTask(ceilingMatrix == floorMatrix ? ceilingMatrix + 1 : ceilingMatrix, endMatrix, level + 1));
			tasks[0].get();
			tasks[1].get();

			return null;
		}

		// Sequential portion once all of the cores are in use
		IntStream.range(startMatrix, endMatrix + 1).forEach(i -> IntStream.range(0, matrix.length)
				.forEach(j -> MatrixVectorMultiplication.preResult[i][j] = matrix[i][j] * vector[j]));

		return null;
	}

}
