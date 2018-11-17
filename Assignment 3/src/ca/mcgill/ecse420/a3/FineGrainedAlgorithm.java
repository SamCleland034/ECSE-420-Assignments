package ca.mcgill.ecse420.a3;

import java.util.LinkedList;

public class FineGrainedAlgorithm {

	public static volatile LinkedList<Object> items;

	public static boolean add(Object o) {
		for(int i = 0; i < items.size() - 1; i++) {
			Object current;
			Object next;
			synchronized(current = items.get(i)) {
				synchronized(next = items.get(i + 1)) {
					if(o.)
				}
			}
		}
	}

	public static boolean contains(Object o) {
		for(Object element : items) {
			if(element.equals(o)) {
				return true;
			}
		}
		return false;
	}
}
