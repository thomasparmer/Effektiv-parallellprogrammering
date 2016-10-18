import java.util.Random;
import java.util.Arrays;
import java.util.concurrent.*;


class SortedOrder {
 private static final int THREADS = 8;

	private CyclicBarrier sync;
	private int size;
	private int[] a;
	private int[] b;
	private int[] count;
	private int shift;
	private int mask;


	public SortedOrder(int[] a, int[] b, int[] count, int shift, int mask) {
		this.a = a;
		this.b = b;
		this.size = a.length;
		this.count = count;
		this.shift = shift;
		this.mask = mask;
	}

	void sortedOrder() {
		int end;
		Thread t; 
		SetOrder x;

		sync = new CyclicBarrier(THREADS+1);
		for (int i = 0; i < size; i = end) {
			if (i+(2*(size/THREADS)-1) >= size)
				end = size;
			else
				end = i+size/THREADS;
			x = new SetOrder(i, end);
			t = new Thread(x);
			t.start();
		}


		try {
			sync.await();
		}	catch (Exception e) {
			e.printStackTrace();
		}
	}

	private class SetOrder implements Runnable {
		private int start;
		private int stop;

		public SetOrder(int start, int stop) {
			this.start = start;
			this.stop = stop;
		}


		public void run() {
			try {
				for (int i = start; i < stop; i++) {
					b[count[(a[i]>>>shift) & mask]++] = a[i];
				}

				sync.await();
			} catch (Exception e) {
				e.printStackTrace();
			}	
		}
	}
}