package ca.mcgill.ecse420.a1;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Level;
import java.util.logging.Logger;

public class DiningPhilosophers {

	private static Chopstick[] chopsticks;
	private static int numberOfPhilosophers;
	private static final Logger logger = Logger.getLogger(DiningPhilosophers.class.getName());

	public static void main(String[] args) {

		numberOfPhilosophers = 5;
		Philosopher[] philosophers = new Philosopher[numberOfPhilosophers];
		chopsticks = new Chopstick[numberOfPhilosophers];
		ExecutorService executor = Executors.newFixedThreadPool(numberOfPhilosophers);
		for (int i = 0; i < numberOfPhilosophers; i++) {
			philosophers[i] = new Philosopher(i);
			chopsticks[i] = new Chopstick();
		}

		for (Philosopher philosopher : philosophers) {
			executor.execute(philosopher);
		}
	}

	public static class Philosopher implements Runnable {

		private int number;

		public Philosopher(int number) {
			this.number = number;
		}

		@Override
		public void run() {
			while (true) {
				Chopstick leftChopstick = chopsticks[(number - 1) % numberOfPhilosophers];
				Chopstick rightChopstick = chopsticks[(number + 1) % numberOfPhilosophers];
				try {
					leftChopstick.wait();
					synchronized (leftChopstick) {
						rightChopstick.wait();
						synchronized (rightChopstick) {

						}
					}
				} catch (InterruptedException ix) {
					logger.log(Level.SEVERE, ix.getMessage());
				}
			}
		}
	}

	public static class Chopstick {

		private boolean isFree;

		public Chopstick() {
			this.isFree = true;
		}

		public boolean isFree() {
			return isFree;
		}

		public void setFree(boolean isFree) {
			this.isFree = isFree;
		}

	}

}
