import java.util.Random;
import java.util.Arrays;
import java.util.concurrent.*;

class ValueFrequency {

	private static final int THREADS = 8;

	private CyclicBarrier sync;
	private int size;
	private int[] a;

	private int[][] allCount;
	private int[] sumCount;
	private int shift;
	private int numSif;


	public ValueFrequency(int[] a, int shift, int numSif) {
		this.a = a;
		this.size = a.length;
		this.shift = shift;
		this.numSif = numSif;
	}

	int[] valueFrequency() {
		int end;
		Thread t; 
		Frequency x;
		Combine y;

		sync = new CyclicBarrier(THREADS+1);
		allCount = new int[THREADS][];
		sumCount = new int[numSif+1];


		int tNum = 0;
		for (int i = 0; i < size; i = end) {
			if (i+(2*(size/THREADS)-1) >= size)
				end = size;
			else
				end = i+size/THREADS;
			x = new Frequency(i, end, tNum++);
			t = new Thread(x);
			t.start();
		}


		try {
			sync.await();
		} catch (InterruptedException ex) {
			ex.printStackTrace();
		} catch (BrokenBarrierException ex) {
			ex.printStackTrace();
		}



		sync = new CyclicBarrier(THREADS+1);
		tNum = 0;
		end = 0;
		for (int i = 0; i < numSif; i+= numSif/THREADS) {
			if (i+numSif/THREADS >= numSif)
				end = numSif;
			else
				end = i+numSif/THREADS;

			y = new Combine(i, end, tNum++);
			t = new Thread(y);
			t.start();
		}
		return sumCount;
	}

	private class Frequency implements Runnable {
		private int start;
		private int stop;
		private int tNum;
		private int[] count;


		public Frequency(int start, int stop, int tNum) {
			this.start = start;
			this.stop = stop;
			this.tNum = tNum;
			count = new int[numSif+1];
		}


		public void run() {
			try {
				for (int i = start; i < stop; i++) {
					count[(a[i]>>> shift) & numSif]++;
				}
				allCount[tNum] = count;

				sync.await();
			} catch (Exception e) {
				e.printStackTrace();
			}	
		}
	}

	private class Combine implements Runnable {
		private int start;
		private int stop;
		private int tNum;
		
		public Combine(int start, int stop, int tNum) {
			this.start = start;
			this.stop = stop;
			this.tNum = tNum;
		}


		public void run() {
			try {
				for (int j = start; j < stop; j++) {
					for (int i = 0; i < THREADS; i++) {
						sumCount[j] += allCount[i][j];
					}
				}
				sync.await();
			} catch (Exception e) {
				e.printStackTrace();
			}	
		}
	}
}