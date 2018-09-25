package ca.mcgill.ecse420.a1;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;
import java.util.logging.Level;
import java.util.logging.Logger;

public class DiningPhilosophers {

	private static ChopStick[] chopsticks;
	private static int numberOfPhilosophers = 5;
	private static final Logger logger = Logger.getLogger(DiningPhilosophers.class.getName());

	public static void main(String[] args) {

		Philosopher[] philosophers = new Philosopher[numberOfPhilosophers];
		chopsticks = new ChopStick[numberOfPhilosophers];
		ExecutorService executor = Executors.newFixedThreadPool(numberOfPhilosophers);

		for (int i = 0; i < numberOfPhilosophers; i++) {
			chopsticks[i] = new ChopStick(true);
		}

		for (int i = 0; i < numberOfPhilosophers; i++) {
			philosophers[i] = new Philosopher(i);
		}

		for (Philosopher philosopher : philosophers) {
			executor.execute(philosopher);
		}

		executor.shutdown();
	}

	public static class ChopStick extends ReentrantLock {

		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;

		public ChopStick(boolean fairness) {
			super(fairness);
		}

		private boolean free = true;

		private Condition condition = this.newCondition();

		public Condition getCondition() {
			return condition;
		}

		public boolean isFree() {
			return free;
		}

		public void setFree(boolean free) {
			this.free = free;
			if (free) {
				condition.signalAll();
			}
		}
	}

	public static class Philosopher implements Runnable {

		private ChopStick leftChopstick;
		private ChopStick rightChopstick;
		private String philosopherName;

		public Philosopher(int number) {
			this.philosopherName = "Philosopher " + number;
			this.leftChopstick = chopsticks[number - 1 < 0 ? numberOfPhilosophers - 1 : number - 1];
			this.rightChopstick = chopsticks[number];
		}

		@Override
		public void run() {
			Condition left = leftChopstick.getCondition();

			while (true) {
				try {
					leftChopstick.lock();
					while (!rightChopstick.isFree()) {
						left.await();
					}

					rightChopstick.lock();
					rightChopstick.setFree(false);

					System.out.println(philosopherName + " is eating...");
					sleepFor(3000);
				} catch (InterruptedException ix) {
					logger.log(Level.SEVERE, ix.getMessage());
				} finally {
					if (rightChopstick.isHeldByCurrentThread()) {
						rightChopstick.setFree(true);
						rightChopstick.unlock();
					}

					if (leftChopstick.isHeldByCurrentThread()) {
						leftChopstick.setFree(true);
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
