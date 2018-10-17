package ca.mcgill.ecse420.a2;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;

public class Filter implements Lock {
	volatile int[] level; // level[i] for thread i
	volatile int[] victim; // victim[L] for level L
	int number;
	AtomicInteger counter = new AtomicInteger();
	int currentThread;

	public Filter(int n) {
		level = new int[n];
		victim = new int[n];
		number = n;
		for (int i = 1; i < n; i++) {
			level[i] = 0;
		}
	}

	@Override
	public void lock() {
		int id = counter.getAndIncrement();
		for (int L = 1; L < number; L++) {
			level[id] = L;
			victim[L] = id;
			for (int k = 0; k < number; k++) {
				while ((k != id && level[k] >= L) && victim[L] == id) {
					continue;
				}
			}
		}

		currentThread = id;
	}

	@Override
	public void lockInterruptibly() throws InterruptedException {

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
	public boolean tryLock(long arg0, TimeUnit arg1) throws InterruptedException {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void unlock() {
		level[currentThread] = 0;
	}
}
