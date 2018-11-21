package ca.mcgill.ecse420.a3.q2;

public class Node extends Object {

	private volatile Node next;
	private int key;
	private static volatile int count = 0;

	public Node(Node next) {
		key = count++;
		this.next = next;
	}

	public Node getNext() {
		return next;
	}

	public void setNext(Node next) {
		this.next = next;
	}

	public int getKey() {
		return key;
	}





}
