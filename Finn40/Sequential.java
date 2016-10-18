import java.util.Random;
import java.util.Arrays;

class Sequential {
	private static int cnt = 0;
	private int size;
	private boolean correct = false;
	int[] a;
	int[] b;
	int[] c;


	public Sequential(int size, int[] aa, int[] bb) {
		this.size = size;
		a = new int[size];
		b = new int[size];
		c = new int[size];
		

		for (int i = 0; i < size; i++) {
			a[i] = aa[i];
			b[i] = bb[i];
			c[i] = aa[i];

		}
	}


	public double sequential() {

		double temp = sort(a, b);
		correct = order();
		return temp;
	}

	public boolean getCorrect() {
		return correct;
	}


	private double sort(int[] a, int[] b) {
		long startTime = System.nanoTime();
		insertSort(a, 0, 40);
		compare(a, 40, size);

		long endTime = System.nanoTime();
		double duration = (endTime - startTime)/(1000000.0);

		/*
		startTime = System.nanoTime();
		Arrays.sort(c);
		endTime = System.nanoTime();
		double duration1 = (endTime - startTime)/(1000000.0);
		System.out.println("Javas ArraySort on " + size + " elements used:\t" + duration1 + " ms");
		*/
		return duration;
		
	}



	private boolean order() {
		int siz = 40;
		boolean o = true;
		for (int i = 0; i < siz; i++) {
			if (a[i] != b[size-1-i]) {
				o = false;
			}
		}
		//System.out.println("Sequential order is " + o);
		return o;
	}



	private void insertSort (int [] a, int start, int end) {
		int i, t;
		for (int k = start; k < end; k++) {
			t = a [k+1];
			i = k;
			while (i >= start && a[i] < t) {
				a [i+1] = a[i];
				i--;
			}
			a[i+1] = t;
		} 
	} 



	private void compare(int [] a, int start, int end) {
		for (int i = start; i < end; i++) {
			if (a[start-1] < a[i]) {
				int temp = a[i];
				a[i] = a[start-1];
				a[start-1] = temp;
				insertSort(a, 0, start);
			}
		}
	}
}