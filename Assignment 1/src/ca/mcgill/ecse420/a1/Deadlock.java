package ca.mcgill.ecse420.a1;

import java.util.HashSet;
import java.util.concurrent.CountDownLatch;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Deadlock {

	private static HashSet<String> set1 = new HashSet<String>();
	private static HashSet<String> set2 = new HashSet<String>();
	private static Logger logger = Logger.getLogger(Deadlock.class.getName());

	public static void main(String[] args) {

		CountDownLatch doneSignal = new CountDownLatch(20);
		set1.add("Lamborghini");
		set2.add("Porsche");
		set1.add("Ford");
		set2.add("Audi");

		new Thread("Set1") {
			@Override
			public void run() {
				while (doneSignal.getCount() > 0) {
					synchronized (set1) {
						synchronized (set2) {
							System.out.println("Set 1 items:");
							set1.stream().forEach((value) -> {
								System.out.println(value);
							});

							doneSignal.countDown();
							sleepFor(500);
						}
					}
				}
			}

		}.start();

		new Thread("Set2") {
			@Override
			public void run() {
				while (doneSignal.getCount() > 0) {
					synchronized (set2) {
						synchronized (set1) {
							System.out.println("Set 2 items:");
							set2.stream().forEach((value) -> {
								System.out.println(value);
							});

							doneSignal.countDown();
							sleepFor(500);
						}
					}
				}
			}
		}.start();

		new Thread("Signal") {
			@Override
			public void run() {
				while (doneSignal.getCount() > 0) {
					System.out.println("Current Count: " + (int) (20 - doneSignal.getCount()));
					sleepFor(2000);
				}
			}
		}.start();
	}

	private static void sleepFor(int duration) {
		try {
			Thread.sleep(duration);
		} catch (InterruptedException e) {
			logger.log(Level.SEVERE, e.getMessage());
		}
	}
}
