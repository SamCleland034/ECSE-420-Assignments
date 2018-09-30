package ca.mcgill.ecse420.a1;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.locks.ReentrantLock;
import java.util.logging.Level;
import java.util.logging.Logger;

public class DiningPhilosophersDeadlock {

	private static ReentrantLock[] chopsticks;
	private static int numberOfPhilosophers = 10;
	private static final Logger logger = Logger.getLogger(DiningPhilosophers.class.getName());

	public static void main(String[] args) {

		Philosopher[] philosophers = new Philosopher[numberOfPhilosophers];
		chopsticks = new ReentrantLock[numberOfPhilosophers];
		ExecutorService executor = Executors.newFixedThreadPool(numberOfPhilosophers);

		for (int i = 0; i < numberOfPhilosophers; i++) {
			chopsticks[i] = new ReentrantLock(true);
		}

		for (int i = 0; i < numberOfPhilosophers; i++) {
			philosophers[i] = new Philosopher(i);
		}

		for (Philosopher philosopher : philosophers) {
			executor.execute(philosopher);
		}

		executor.shutdown();
	}

	public static class Philosopher implements Runnable {

		private ReentrantLock leftChopstick;
		private ReentrantLock rightChopstick;
		private String philosopherName;

		public Philosopher(int number) {
			this.philosopherName = "Philosopher " + number;
			this.leftChopstick = chopsticks[number - 1 < 0 ? numberOfPhilosophers - 1 : number - 1];
			this.rightChopstick = chopsticks[number];
		}

		@Override
		public void run() {
			while (true) {
				try {
					leftChopstick.lock();
					// Deadlocks immediately for sleeping after locking (All philosophers will get
					// left chopstick)
					System.out.println(philosopherName + " picked up left chopstick...");
					sleepFor(50);
					rightChopstick.lock();
					System.out.println(philosopherName + " is eating...");
					sleepFor(3000);
				} finally {
					if (rightChopstick.isHeldByCurrentThread()) {
						rightChopstick.unlock();
					}

					if (leftChopstick.isHeldByCurrentThread()) {
						leftChopstick.unlock();
					}

					sleepFor(1000);
				}
			}
		}

		private void sleepFor(int duration) {
			try {
				Thread.sleep(duration);
			} catch (InterruptedException ix) {
				logger.log(Level.SEVERE, ix.getMessage());
			}
		}
	}
}
