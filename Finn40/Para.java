import java.util.Random;
import java.util.Arrays;
import java.lang.Object;
import java.util.concurrent.*;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.locks.*;

class Para {
	private final static int BEGIN = 40; 
	private final static int THREADS = 10; //Runtime.getRuntime().availableProcessors();
	private int done = 0;
	private int pri = 0;
	private int size;
	private int[] a;
	private int[] c;
	private Lock sortedLock;
	private CyclicBarrier b;
	private int[] best40;
	private int bestCnt = 0;
	private boolean correct = false;

	public Para(int size, int[] aa, int[] cc) {
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




	double parallel() {
		double duration = -1;
		
		sortedLock = new ReentrantLock();
		b = new CyclicBarrier(THREADS+1);
		Thread t;
		Sort s;

		best40 = new int[(THREADS)*40];

		//System.out.println("THREADS: " + THREADS);
		long startTime = System.nanoTime();
		
		int beg = 0;
		int last = size/THREADS;

		for (int i = 0; i < THREADS; i++) {
			s = new Sort(beg, last);
			beg = last;
			last += size/THREADS;

			if (i == THREADS-1)
				last = size;

			t = new Thread(s);
			t.start();
		}
		try {
			b.await();
			s = new Sort(0, best40.length-1);
			//s.insertSort(best40, 0, best40.length-1);
			Arrays.sort(best40);

			//s.compare(best40, 40, best40.length);
			long endTime = System.nanoTime();
			duration = (endTime - startTime)/(1000000.0);
			correct = order();
			return duration;
		} catch(InterruptedException ex) {
			System.out.println("line 75");
			ex.printStackTrace();
		} catch (BrokenBarrierException e) {
			System.out.println("line 77");
			e.printStackTrace();
		}
		return -200;
	}


	private boolean order() {
		boolean o = true;
		for (int i = 0; i < BEGIN; i++) {
			if (best40[best40.length-1-i] != c[size-1-i]) {
				o = false;
			}
		}
		return o;
	}

	private class Sort implements Runnable {
		private int from;
		private int to;

		public Sort(int from, int to) {
			this.from = from;
			this.to = to;
		}


		synchronized void fill(int st, int en) {
			for (int i = st; i < en; i++) {
				best40[bestCnt++] = a[i];
			}
			//System.out.println("bestCnt = : " + bestCnt);
		}

		public void run() {
			try {
				insertSort(a, from, from+40);
				compare(a, from+40, to);


				sortedLock.lock();
				try {
					fill(from, from+40);
				} finally {
					sortedLock.unlock();
				}



				b.await();
			} catch(InterruptedException ex) {
				ex.printStackTrace();
				System.out.println("line 119");
			} catch (BrokenBarrierException e) {
				System.out.println("line 121");
				e.printStackTrace();
			}
		}

		private void insertSort (int [] tmpArray, int start, int end) {
			int i, t;
			for (int k = start; k < end; k++) {
				t = tmpArray[k+1];
				i = k;
				while (i >= start && tmpArray[i] < t) {
					tmpArray[i+1] = tmpArray[i];
					i--;
				}
				tmpArray[i+1] = t;
			} 
		} 



		private void compare(int [] tmpArray, int start, int end) {
			for (int i = start; i < end; i++) {
				if (tmpArray[start-1] < tmpArray[i]) {
					int temp = tmpArray[i];
					tmpArray[i] = tmpArray[start-1];
					tmpArray[start-1] = temp;
					insertSort(tmpArray, from, from+40);
				}
			}
		}
	}
}