package ca.mcgill.ecse420.a1;

import java.util.HashSet;

public class Deadlock {

	private static HashSet<String> set1 = new HashSet<String>();
	private static HashSet<String> set2 = new HashSet<String>();

	public static void main(String[] args) {

		set1.add("Lamborghini");
		set2.add("Porsche");
		set1.add("Ford");
		set2.add("Audi");

		new Thread("Set1") {
			@Override
			public void run() {
				while (true) {
					synchronized (set1) {
						synchronized (set2) {
							System.out.println("Set 1 items:");
							set1.stream().forEach((value) -> {
								System.out.println(value);
							});
						}
					}
				}
			}
		}.start();

		new Thread("Set2") {
			@Override
			public void run() {
				while (true) {
					synchronized (set2) {
						synchronized (set1) {
							System.out.println("Set 2 items:");
							set2.stream().forEach((value) -> {
								System.out.println(value);
							});
						}
					}
				}
			}
		}.start();
	}
}
