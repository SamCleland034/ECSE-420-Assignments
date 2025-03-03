package ca.mcgill.ecse420.a3.q3;

import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.IntStream;

/**
 * Lock free version of Bounded Array Queue
 * @author Sam Cleland
 *
 */
public class LockFreeBoundedArrayQueue implements Runnable {
	public static AtomicReference<Integer>[] slots = new AtomicReference[10];
	public static volatile AtomicInteger tail = new AtomicInteger();
	public static volatile AtomicInteger head = new AtomicInteger();
	static Random rand = new Random();

	public static void populateSlots() {
		IntStream.range(0, slots.length).forEach(i -> slots[i] = new AtomicReference<Integer>(i));
		tail.set(slots.length);
		System.out.println("Initial Slots: " + printSlots());
	}

	public static String printSlots() {
		StringBuilder sb = new StringBuilder();
		for(int index = head.get(); index < tail.get(); index++) {
			sb.append(" " + slots[index % slots.length].get());
		}

		return sb.toString();
	}

	public static boolean enqueue(int value) {
		if(tail.get() - head.get() == slots.length) {
			return false;
		}

		// Get the current tail value
		int tailVal = tail.get();
		AtomicReference<Integer> next = slots[(tailVal) % (slots.length)];
		int currentVal = next.get();
		// Check if anything has changed, if nothing changed operation was successful and continue, else have to back off.
		if(tail.compareAndSet(tailVal, tailVal + 1)) {
			while(!next.compareAndSet(currentVal, value));
			System.out.println("Enqueued " + value + " from Thread " + Thread.currentThread().getId() + " POV");
			return true;
		}

		return false;
	}

	public static int dequeue() {
		if(tail.get() - head.get() == 0) {
			return -1;
		}

		int headVal = head.get();
		int currentVal = slots[headVal % slots.length].get();
		// check if value of the head changed, if so then have to back off
		if(head.compareAndSet(headVal, headVal + 1)) {
			System.out.println("Dequeued " + currentVal + " from Thread " + Thread.currentThread().getId());
			return currentVal;
		}

		return -1;
	}

	@Override
	public void run() {
		int value = rand.nextInt(2);
		if(value == 0) {
			value = rand.nextInt(100);
			int counter = 1;
			while(!enqueue(value)) {
				// Exponential Backoff if it fails
				sleepFor((int) Math.pow(2, counter++));
			}
		} else {
			int counter = 1;
			while(dequeue() == -1) {
				// Exponential Backoff if it fails
				sleepFor((int) Math.pow(2, counter++));
			}
		}

		System.out.println("Values in the queue from Thread " + Thread.currentThread().getId() + ":\n" + printSlots());
	}

	private void sleepFor(int i) {
		try {
			Thread.sleep(i);
		} catch (InterruptedException ix) {
			ix.printStackTrace();
		}
	}
}
