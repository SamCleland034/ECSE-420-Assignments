package ca.mcgill.ecse420.a2;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.locks.Lock;

/**
 * Class to test the Bakery and Filter algorithms implemented
 *
 * @author Sam Cleland
 *
 */
public class MutalExclusion {
	public static int counter = 0;
	public static int size = 5;

	public static void main(String[] args) {
		Filter filter = new Filter(size);
		Bakery bakery = new Bakery(size);
		testMutex(filter, size);
		counter = 0;
		testMutex(bakery, size);
		counter = 0;
		testMutex(null, size);
	}

	/**
	 * Creates a number of threads to then test the mutual exclusion of the locks
	 *
	 * @param lock type of lock that will be tested
	 * @param size number of threads that will be created by the service
	 */
	private static void testMutex(Lock lock, int size) {
		CountDownLatch latch = new CountDownLatch(size);
		ExecutorService execute = Executors.newFixedThreadPool(size);
		for (int i = 0; i < size; i++) {
			execute.execute(new MutexTask(lock, latch, i));
		}

		execute.shutdown();
		try {
			latch.await();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		if (lock != null) {
			System.out.println(lock.getClass().getSimpleName() + " counter value = " + counter);
		} else {
			System.out.println("No lock counter value = " + counter);
		}
	}

	/**
	 * Describes the task of sharing a counter variable to prove it is mutual
	 * exclusive or not
	 *
	 * @author Sam Cleland
	 *
	 */
	public static class MutexTask implements Runnable {

		Lock lock;
		CountDownLatch latch;
		int id;

		MutexTask(Lock lock, CountDownLatch latch, int id) {
			this.lock = lock;
			this.latch = latch;
			this.id = id;
		}

		@Override
		public void run() {
			if (lock == null) {
				for (int i = 0; i < 20; i++) {
					increment();
				}
			} else {
				for (int i = 0; i < 20; i++) {
					lock.lock();
					increment();
					lock.unlock();
				}
			}

			latch.countDown();
		}

		/*
		 * Increments the counter variable with a sleep to exemplify mutual exclusion
		 */
		private void increment() {
			int number = counter;
			try {
				Thread.sleep(1);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			number += 1;
			counter = number;
		}
	}
}
