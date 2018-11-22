package ca.mcgill.ecse420.a3.q3;

import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.stream.IntStream;

public class BoundedArrayQueue implements Runnable {

	public static int[] slots = new int[10];
	public static volatile AtomicInteger tail = new AtomicInteger();
	public static volatile AtomicInteger head = new AtomicInteger();
	static Lock enq = new ReentrantLock(true);
	static Condition enqCondition = enq.newCondition();
	static Lock deq = new ReentrantLock(true);
	static Condition deqCondition = deq.newCondition();
	static Random rand = new Random();

	public static void populateSlots() {
		IntStream.range(0, 10).forEach(i -> enqueue(i));
		System.out.println("Initial Slots: " + printSlots());
	}

	public static String printSlots() {
		StringBuilder sb = new StringBuilder();
		for(int index = head.get(); index < tail.get(); index++) {
			sb.append(" " + slots[index % slots.length]);
		}

		return sb.toString();
	}

	public static void enqueue(int value) {
		enq.lock();
		while (tail.get() - head.get() == slots.length) {
			try {
				enqCondition.await();
			} catch (InterruptedException ix) {
				ix.printStackTrace();
			}
		}

		System.out.println("Enqueuing " + value + " from Thread " + Thread.currentThread().getId() + " POV");
		slots[tail.get() % slots.length] = value;
		if(tail.getAndIncrement() - head.get() == 0) {
			deq.lock();
			deqCondition.signalAll();
			deq.unlock();
		}

		enq.unlock();
	}

	public void dequeue() {
		deq.lock();
		while (head.get() - tail.get() == 0) {
			try {
				deqCondition.await();
			} catch (InterruptedException ix) {
				ix.printStackTrace();
			}
		}

		System.out.println("Dequeuing from Thread " + Thread.currentThread().getId());
		int value = slots[head.get() % slots.length];
		if(tail.get() - head.getAndIncrement() == slots.length) {
			enq.lock();
			enqCondition.signalAll();
			enq.unlock();
		}

		System.out.println("Dequeued " + value + " from Thread " + Thread.currentThread().getId());
		deq.unlock();
	}

	@Override
	public void run() {
		int value = rand.nextInt(2);
		if(value == 0) {
			value = rand.nextInt(100);
			enqueue(value);
		} else {
			dequeue();
		}

		System.out.println("Values in the queue from Thread " + Thread.currentThread().getId() + ":\n" + printSlots());
	}
}
