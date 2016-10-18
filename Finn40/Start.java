import java.util.Arrays;
import java.util.Random;

class Start {
	
	private final static int MAX = 100000001; //000
	private final static int RUNS = 9;

	private double para[][] = new double[6][9];
	private double seq[][] = new double[6][9];
	private boolean scorrect[] = {false, false, false, false, false, false};
	private boolean pcorrect[] = {false, false, false, false, false, false};

	private int pcnt = 0;
	private int scnt = 0;

	private int prcnt = 0;
	private int srcnt = 0;



	void start() {
		Sequential s;
		Para p;

		int cnt = 0;

		Random r = new Random(7361);
		//Random r = new Random(7860);

		for (int i = 1000; i < MAX; i*=10) {
			int[] a = new int[i];
			int[] b = new int[i];
			for (int j = 0; j < a.length; j++) {
				a[j] = r.nextInt();
				b[j] = a[j];
			}
			Arrays.sort(b);

			for (int j = 0; j < 9; j++) {
				p = new Para(i, a, b);
				double temp = p.parallel();
				addPTime(temp);
				pcorrect[cnt] = true && p.getCorrect();
			}

			for (int j = 0; j < 9; j++) {
				s = new Sequential(i, a, b);
				double temp = s.sequential();
				addSTime(temp);
				scorrect[cnt] = true && s.getCorrect();
			}

			cnt++;
		}


		for (double[] inner : seq) {
			Arrays.sort(inner);
		}

		for (double[] inner : para) {
			Arrays.sort(inner);
		}

		for (int i = 0; i < 6; i++) {
			System.out.println("mean sequential with size 1000 * 10^" + i + ":\t" + seq[i][5] + " ms and order = " + scorrect[i]);
			System.out.println("mean on Parallel with size 1000 * 10^" + i + ":\t" + para[i][5] + " ms and order = " + pcorrect[i] + "\n");
		}
	}


	public void addPTime(double time) {
		if (pcnt >= 9) {
			pcnt = 0;
			prcnt++;
		}
		para[prcnt][pcnt++] = time;
	}
	

	public void addSTime(double time) {
		if (scnt >= 9) {
			scnt = 0;
			srcnt++;
		}
		seq[srcnt][scnt++] = time; ////System.out.println("mean:\t" + duration + " ms");
	}
}