package ca.mcgill.ecse420.a2;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.locks.Lock;

public class MutalExclusion {
	public static int counter = 0;
	public static int size = 100;

	public static void main(String[] args) {
		Filter filter = new Filter(size);
		Bakery bakery = new Bakery(size);
		testMutex(filter, size);
		counter = 0;
		testMutex(bakery, size);
	}

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

		System.out.println(lock.getClass().getSimpleName() + " Counter value = " + counter);
	}

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
			lock.lock();
			counter++;
			lock.unlock();
			latch.countDown();
		}
	}
}
