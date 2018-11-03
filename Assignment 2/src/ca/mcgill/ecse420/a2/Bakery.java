package ca.mcgill.ecse420.a2;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;

/**
 * Implements the Bakery Lock described in class
 *
 * @author Sam Cleland and Wiam El Ouadi
 *
 */
public class Bakery implements Lock {
	volatile boolean[] flag;
	volatile List<Integer> label;
	int currentThread;

	public Bakery(int n) {
		flag = new boolean[n];
		label = new ArrayList<Integer>(n);
		for (int i = 0; i < n; i++) {
			flag[i] = false;
			label.add(0);
		}
	}

	/**
	 * Exemplifies the bakery algorithm shown in class, using an atomic integer to
	 * keep track of threads. If there is a tie with a label then using the
	 * lexiographic ordering
	 */
	@Override
	public void lock() {
		int id = (int) (Thread.currentThread().getId() % flag.length);
		flag[id] = true;
		label.set(id, Collections.max(label) + 1);
		for (int k = 0; k < label.size(); k++) {
			while (flag[k] && (label.get(id) > label.get(k) || (label.get(id) == label.get(k) && id > k))) {
				continue;
			}
		}

		currentThread = id;
	}

	@Override
	public void lockInterruptibly() throws InterruptedException {
		// TODO Auto-generated method stub

	}

	@Override
	public Condition newCondition() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean tryLock() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean tryLock(long time, TimeUnit unit) throws InterruptedException {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void unlock() {
		flag[currentThread] = false;
	}
}