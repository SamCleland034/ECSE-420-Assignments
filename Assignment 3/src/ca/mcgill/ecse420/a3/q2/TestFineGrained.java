package ca.mcgill.ecse420.a3.q2;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.IntStream;

public class TestFineGrained {

	public static void main(String[] args) {
		ExecutorService service = Executors.newCachedThreadPool();
		FineGrainedAlgorithm.populateList();
		FineGrainedAlgorithm.printList();
		IntStream.range(0, 50).forEach(i -> service.execute(new FineGrainedAlgorithm()));
		service.shutdown();
	}

}
