package ca.mcgill.ecse420.a3.q3;

import java.util.Scanner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.IntStream;

public class TestBoundedArrayQueue {

	public static void main(String[] args) {
		Scanner userInput = new Scanner(System.in);
		System.out.println("Enter 1 for Lock free, anything else for normal");
		ExecutorService service = Executors.newCachedThreadPool();
		String input = userInput.nextLine();
		userInput.close();
		if(input.trim().equals("1")) {
			System.out.println("Lock Free Bounded Queue");
			LockFreeBoundedArrayQueue.populateSlots();
			IntStream.range(0, 50).forEach(i -> service.execute(new LockFreeBoundedArrayQueue()));
		} else {
			System.out.println("Normal Bounded Queue");
			BoundedArrayQueue.populateSlots();
			IntStream.range(0, 50).forEach(i -> service.execute(new BoundedArrayQueue()));
		}

		service.shutdown();
	}

}
