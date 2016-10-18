import java.util.Random;
import java.util.Arrays;
import java.util.concurrent.*;

class MaxVal {

    private static final int THREADS = 8;

	private CyclicBarrier b;
	private int size;
	private int[] a;
	private int maximum;

	public MaxVal(int[] a) {
		this.a = a;
		this.size = a.length;
	}

	int findMax() {
		int end;
		Thread t; 
		Max x;
		//System.out.println("hey");
		b = new CyclicBarrier(THREADS+1);
		for (int i = 0; i < size; i = end) {
			if (i+(2*(size/THREADS)-1) >= size)
				end = size;
			else
				end = i+size/THREADS;
			x = new Max(i, end);
			t = new Thread(x);
			t.start();
		}


		try {
			b.await();
		}	catch (Exception e) {
			e.printStackTrace();
		}
		return maximum;
	}

	private class Max implements Runnable {
		private int start;
		private int stop;

		public Max(int start, int stop) {
			this.start = start;
			this.stop = stop;
		}

		synchronized void globalMax(int max) {
			if (max > maximum)
				maximum = max;
		}


		public void run() {
		    //System.out.println("from: " + start + " to: " + stop);
			try {
				int max = a[start];

				for (int i = start+1; i < stop; i++) {
					if (a[i] > max) {
						max = a[i];
					}
				}

				globalMax(max);
				b.await();
			} catch (Exception e) {
				e.printStackTrace();
			}	
		}
	}
}