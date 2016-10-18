import java.util.Random;
import java.util.Arrays;
import java.lang.Object;
import java.util.concurrent.*;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.locks.*;

class Parallel {
	private final static int BEGIN = 40; 
	private final static int THREADS = 2; //Runtime.getRuntime().availableProcessors();
	private int done = 0;
	private int pri = 0;
	private int size;
	private int[] a;
	private int[] c;
	private Lock sortedLock;
	private CyclicBarrier b;
	private boolean correct = false;

	public Parallel(int size, int[] aa, int[] cc) {
		this.size = size;
		a = new int[size];
		c = new int[size];

		for (int i = 0; i < a.length; i++) {
			a[i] = aa[i];
			c[i] = cc[i];
		}
	}

	public boolean getCorrect() {
		return correct;
	}

	private boolean order() {
		boolean o = true;
		for (int i = 0; i < BEGIN; i++) {
			if (a[i] != c[size-1-i]) {
				o = false;
				//System.out.println("a[i]: " + a[i] + " i: " + i + " b[size-1-i]: " + c[size-1-i] + "a[i-1]: " + "før feil: " + a[i-1] + " "  + a[i-2]);
			}
		}
		System.out.println("ORDER is : " + o);
		return o;
	}


	double parallel() {
		double duration = -1;
		
		sortedLock = new ReentrantLock();
		b = new CyclicBarrier(THREADS+1);
		Thread t;

		//Random r = new Random(7361);
		//Random r = new Random();
		//a = new int[size];
		
		long startTime = System.nanoTime();
		Sort st = new Sort(0, BEGIN);
		//t = new Thread(st);
		//t.start();

		st.insertSort(a, 0, BEGIN);


		Sort s = new Sort(BEGIN, size/THREADS);
		t = new Thread(s);
		t.start();
		
		int last = size/THREADS;
		int cnt = 0;

		for (int i = 0; i <= THREADS-2; i++) {
			s = new Sort(last, last+(size/THREADS));
			if (last+(size/THREADS) >= size)
				last = size;
			else
				last = last+(size/THREADS);
			t = new Thread(s);

			t.start();
		}
		try {
			b.await();
			long endTime = System.nanoTime();
			duration = (endTime - startTime)/(1000000.0);
			correct = order();
			return duration;
		} catch(InterruptedException ex) {
			ex.printStackTrace();
		} catch (BrokenBarrierException e) {
			e.printStackTrace();
		}
		return -200;
	}


	private class Sort implements Runnable {
		private int from;
		private int to;

		public Sort(int from, int to) {
			this.from = from;
			this.to = to;
		}


		public void run() {
			try {
				compare(a, from, to);
				b.await();
			} catch(InterruptedException ex) {
				ex.printStackTrace();
			} catch (BrokenBarrierException e) {
				e.printStackTrace();
			}
		}


		synchronized void insertSort (int[] a, int start, int end) {
			int i, t;
			for (int k = start; k < end; k++) {
				t = a[k+1];
				i = k;
				while (i >= start && a[i] < t) {
					a[i+1] = a[i];
					i--;
				}
				a[i+1] = t;
			} 
		} 



		synchronized void compare(int [] a, int start, int end) {
			for (int i = start; i < end; i++) {

				if (a[BEGIN-1] < a[i]) {
						sortedLock.lock();
					try {
						if (a[BEGIN-1] < a[i]) {
							int temp = a[i];
							a[i] = a[BEGIN-1];
							a[BEGIN-1] = temp;
							insertSort(a, 0, BEGIN);

						}
					} finally {
						sortedLock.unlock();
					}
				}
			}
		}
	}
}