package ca.mcgill.ecse420.a3.q3;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.IntStream;

public class TestBoundedArrayQueue {

	public static void main(String[] args) {
		ExecutorService service = Executors.newCachedThreadPool();
		BoundedArrayQueue.populateSlots();
		IntStream.range(0, 50).forEach(i -> service.execute(new BoundedArrayQueue()));
		service.shutdown();
	}

}
