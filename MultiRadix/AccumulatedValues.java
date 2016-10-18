import java.util.Random;
import java.util.Arrays;
import java.util.concurrent.*;

class AccumulatedValues {

	private static final int THREADS = 8;

	private CyclicBarrier b;
	private CyclicBarrier sync;
	private CyclicBarrier done;
	private int size;
	private int[] count;
	private int[] topValues;
	private int[][] startStop;
	private int mask;

	public AccumulatedValues(int[] count, int mask) {
		this.count = count;
		this.size = count.length;
		this.mask = mask;
	}

	void accumulatedValues() {
		int end = 0;
		Thread t; 
		Accumulate x;
		int tNum = 0;
		b = new CyclicBarrier(THREADS+1);
		sync = new CyclicBarrier(THREADS+1);
		done = new CyclicBarrier(THREADS+1);
		topValues = new int[THREADS];
		startStop = new int[THREADS][2];
		
		System.out.println("Threads: " + THREADS);
		//System.out.println("mask: " + mask);
		//if(tnr < n%ant_t)
		//	m++;

		for (int i = 0; i < mask; i = end) {
			if (i+(2*(mask/THREADS)-1) >= mask)
				end = mask+1;
			else {
				end = i+(mask/THREADS);
			}

			//System.out.println("i: " + i + " end: " + end + " i+mask/THREADS: " + (i+mask/THREADS) + " tNum: " + tNum);
			try {
				startStop[tNum][0] = i;
				startStop[tNum][1] = end;
			} catch (Exception e) {
				System.out.println("tnum: " + tNum);
				e.printStackTrace();
			}
			x = new Accumulate(i, end, tNum++);
			t = new Thread(x);
			t.start();
		}

		try {
			b.await();
		}	catch (Exception e) {
			e.printStackTrace();
		}
		
		for (int i = 1; i < THREADS; i++) {
			topValues[i] += topValues[i-1];
		}

		try {
			sync.await();
		}	catch (Exception e) {
			e.printStackTrace();
		}
		
		try {
			done.await();
		}	catch (Exception e) {
			e.printStackTrace();
		}

		/*System.out.println(topValues[7]);
		for (int i = 1; i < THREADS; i++) {
			for (int j = startStop[i][0]; j < startStop[i][1]; j++) {
				count[j] += topValues[i-1];
			}
		}*/
	}

	private class Accumulate implements Runnable {
		private int start;
		private int stop;
		private int[] counting;
		private int tNum;

		public Accumulate(int start, int stop, int tNum) {
			this.start = start;
			this.stop = stop;
			this.tNum = tNum;
			counting = count;

		}


		public void run() {
			try {
				int j;
				int tmp2 = count[stop-1];
				int acumVal = 0;

				for (int i = start; i < stop; i++) {
					j = count[i];
					count[i] = acumVal;
					acumVal += j;
				}

				topValues[tNum] = count[stop-1] + tmp2;
				b.await();				
			} catch (Exception e) {
				e.printStackTrace();
			}

			try {
				sync.await();
			}	catch (Exception e) {
				e.printStackTrace();
			}
			if (tNum > 0) {
				for (int j = start; j < stop; j++) {
					count[j] += topValues[tNum-1];
				}
			}
			try {
				done.await();
			}	catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
}