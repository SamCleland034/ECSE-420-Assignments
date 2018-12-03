package ca.mcgill.ecse420.a3.q2;

import java.util.Random;
import java.util.stream.IntStream;

public class FineGrainedAlgorithm implements Runnable {

	static Random rand = new Random();
	public static volatile Node head = new Node(null);
	public static volatile int size = 1;
	private static int startingSize = 50;
	private static volatile int removeCounter = 49;
	private static volatile int containsCounter = 49;

	public static void populateList() {
		System.out.println("Generated List: ");
		IntStream.range(0, startingSize).forEach(i -> add(new Node(null), size));
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

		if (current.equals(o)) {
			// Just make head point to the next node
			synchronized (current) {
				head = current.getNext();
			}

			return true;
		}

		// Hand over hand locking until we get to the node we want to remove
		while(current.getNext() != null) {
			synchronized (current) {
				synchronized (next = current.getNext()) {
					if (next.equals(o)) {
						// Switch the pointers of the node
						System.out.println("Removed node with key " + next.getKey() + " from thread " + Thread.currentThread().getId());

						current.setNext(next.getNext());
						next.setNext(null);
						size--;
						return true;
					}

					// Go to next node
					current = current.getNext();
				}
			}
		}

		return false;
	}

	public static boolean add(Node node, int index) {
		// Start at head
		Node current;
		current = head;
		int count = 0;
		// If the node we want to add is at the head, don't have to lock the next node.
		// Just make head the new node
		if (index == 0) {
			node.setNext(current);
			head = node;
			return true;
		}

		// Traverse the list, hand over hand locking in this case
		while (count <= size && current != null) {
			synchronized (current) {
				// If we find the node we are looking for
				if (count + 1 == index) {
					Node next = current.getNext();
					current.setNext(node);
					node.setNext(next);
					size++;
					return true;
				}

				// Go to next node
				current = current.getNext();
				count++;
			}
		}

		return false;
	}

	public static boolean contains(Node o) {
		System.out.println("Contains on key " + o.getKey());
		Node current = head;
		do {
			// Traverse the list from the head 1 node at a time
			// Locking each node using the synchronized keyword
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
		// Generate random value to correspond to a random task
		int random = rand.nextInt(2) + 1;
		if (random == 1) {
			Node current = head;
			int value;
			do {
				value = removeCounter;
				for (int i = 0; i < value; i++) {
					// If the node was modified, try again
					if (current == null) {
						i = 0;
						value = removeCounter;
						current = head;
						continue;
					}

					current = current.getNext();
				}
			} while (current == null);

			removeCounter--;
			System.out.println("Removing node at position " + value + " with key " + current.getKey() + " from thread " + Thread.currentThread().getId());
			remove(current);
		} else {
			Node current = head;
			int value;
			do {
				value = containsCounter;
				for (int i = 0; i < value; i++) {
					if (current == null) {
						i = 0;
						value = containsCounter;
						current = head;
						continue;
					}

					current = current.getNext();
				}
			} while (current == null);
			containsCounter--;
			System.out.println("Contains on node with key " + current.getKey() + " from thread " + Thread.currentThread().getId() + " = " + contains(current));
		}

		// Print the state of the list
		printList();
	}

	private int getRandomValue() {
		return rand.nextInt(size - 1);
	}
}
