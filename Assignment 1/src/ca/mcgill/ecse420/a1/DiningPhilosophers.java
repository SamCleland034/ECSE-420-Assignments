package ca.mcgill.ecse420.a1;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.logging.Level;
import java.util.logging.Logger;

public class DiningPhilosophers {

	private static Lock[] chopsticks;
	private static int numberOfPhilosophers = 5;
	private static final Logger logger = Logger.getLogger(DiningPhilosophers.class.getName());

	public static void main(String[] args) {

		Philosopher[] philosophers = new Philosopher[numberOfPhilosophers];
		chopsticks = new Lock[numberOfPhilosophers];
		ExecutorService executor = Executors.newFixedThreadPool(numberOfPhilosophers);

		for (int i = 0; i < numberOfPhilosophers; i++) {
			chopsticks[i] = new ReentrantLock();
		}

		for (int i = 0; i < numberOfPhilosophers; i++) {
			philosophers[i] = new Philosopher(i);
		}

		for (Philosopher philosopher : philosophers) {
			executor.execute(philosopher);
		}
	}

	public static class Philosopher implements Runnable {

		private ReentrantLock leftChopstick;
		private ReentrantLock rightChopstick;
		private String philosopherName;

		public Philosopher(int number) {
			this.philosopherName = "Philosopher " + number;
			this.leftChopstick = (ReentrantLock) chopsticks[number - 1 < 0 ? numberOfPhilosophers - 1 : number - 1];
			this.rightChopstick = (ReentrantLock) chopsticks[number];
		}

		@Override
		public void run() {
			while (true) {
				try {
					System.out.println(philosopherName + " is thinking...");
					leftChopstick.lock();
					if (rightChopstick.tryLock(1, TimeUnit.SECONDS)) {
						System.out.println(philosopherName + " is eating...");
						sleepFor(3000);
					} else {
						System.out.println(philosopherName + " couldn't acquire both forks...releasing....");
					}
				} catch (InterruptedException ix) {
					logger.log(Level.SEVERE, ix.getMessage());
				} finally {
					leftChopstick.unlock();
					if (rightChopstick.isHeldByCurrentThread()) {
						rightChopstick.unlock();
					}

					sleepFor(2000);
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
