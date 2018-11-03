package ca.mcgill.ecse420.a2;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;

/*
 * Exemplifies the Filter lock that was described in class
 * @author Sam Cleland and Wiam El Ouadi
 */
public class Filter implements Lock {
	// level[i] for thread i
	volatile int[] level;
	// victim[L] for level L
	volatile int[] victim;
	// number of elements
	int number;
	// current thread that is running (0- (n-1))
	int currentThread;

	/*
	 * Constructs a filter with n-1 levels and n victim slots
	 */
	public Filter(int n) {
		level = new int[n];
		victim = new int[n];
		number = n;
		for (int i = 1; i < n; i++) {
			level[i] = 0;
		}
	}

	/*
	 * Exemplifies the filter lock algorithm described in class
	 */
	@Override
	public void lock() {
		int id = (int) (Thread.currentThread().getId() % victim.length);
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

	/*
	 * Unlocks the thread by making its level 0, no interest in getting in
	 */
	@Override
	public void unlock() {
		level[currentThread] = 0;
	}
}
