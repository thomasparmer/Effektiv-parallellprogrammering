import java.util.Random;

class Start {
	
	public static void main(String[] args) {

		if (args.length != 1) {
			System.out.println("USAGE: java Start [N]");
			System.exit(1);
		}
		int size = Integer.parseInt(args[0]);

		int[] a;
		Random r = new Random();
		a = new int[(int)size];
		for (int i = 0; i < size; i++) {
			a[i] = r.nextInt(1000);
		}

		Radix radix = new Radix();
		radix.radixMulti(a);

		Parallel parallel = new Parallel();
		parallel.radixMulti(a);

	}
}