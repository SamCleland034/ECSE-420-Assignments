package ca.mcgill.ecse420.a3.q2;

import java.util.Random;
import java.util.stream.IntStream;

public class FineGrainedAlgorithm implements Runnable {

	static Random rand = new Random();
	public static volatile Node head = new Node(null);
	public static volatile int size = 1;

	public static void populateList() {
		System.out.println("Generated List: ");
		IntStream.range(0, 10).forEach(i -> add(new Node(null), size));
	}

	public static void printList() {
		Node current = head;
		StringBuilder sb = new StringBuilder();
		sb.append("List from thread " + Thread.currentThread().getId() + " POV\n");

		while(current != null) {
			sb.append(current.getKey() + "\t");
			current = current.getNext();
		}

		sb.append(" ");
		System.out.println(sb.toString());
	}

	public static boolean remove(Node o) {
		Node current = head;
		Node next;

		if(current.equals(o)) {
			head = current.getNext();
			return true;
		}

		while(current.getNext() != null) {
			synchronized (current) {
				synchronized (next = current.getNext()) {
					if (next.equals(o)) {
						current.setNext(next.getNext());
						next.setNext(null);
						size--;
						return true;
					}

					current = current.getNext();
				}
			}
		}

		return false;
	}

	public static boolean add(Node node, int index) {
		Node current;
		current = head;
		int count = 0;
		if (index == 0) {
			node.setNext(current);
			head = node;
			return true;
		}

		while (count <= size && current != null) {
			synchronized (current) {
				if (count + 1 == index) {
					Node next = current.getNext();
					current.setNext(node);
					node.setNext(next);
					size++;
					return true;
				}

				current = current.getNext();
				count++;
			}
		}

		return false;
	}

	public static boolean contains(Node o) {
		Node current = head;
		do {
			synchronized(current) {
				if(current.equals(o)) {
					return true;
				}

				current = current.getNext();
			}
		} while(current != null);

		return false;
	}

	@Override
	public void run() {
		int random = Math.abs(rand.nextInt() % 3);
		if(random == 0) {
			int value = getRandomValue();
			Node current = new Node(null);
			System.out.println("Adding new node at position " + value + " with key " + current.getKey() + " from thread " + Thread.currentThread().getId());
			add(current, value);
		} else if (random == 1) {
			Node current = head;
			int value;
			do {
				value = getRandomValue();
				for (int i = 0; i < value; i++) {
					if (current == null) {
						value = getRandomValue();
						current = head;
					}

					current = current.getNext();
				}
			} while (current == null);

			System.out.println("Removing node at position " + value + " with key " + current.getKey() + " from thread " + Thread.currentThread().getId());
			remove(current);
		} else {
			Node current = head;
			int value;
			do {
				value = getRandomValue();
				for (int i = 0; i < value; i++) {
					if (current == null) {
						value = getRandomValue();
						current = head;
					}

					current = current.getNext();
				}
			} while (current == null);

			System.out.println("Contains on node with key " + current.getKey() + " from thread " + Thread.currentThread().getId() + " = " + contains(current));
		}

		printList();
	}

	private int getRandomValue() {
		return Math.abs(rand.nextInt() % size);
	}
}
