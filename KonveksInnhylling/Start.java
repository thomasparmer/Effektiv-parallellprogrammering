import java.io.*;

class Start {
    public static void main(String[] args) {
	if (args.length != 1) {
	    System.out.println("USAGE: java Start [N]");
	    System.exit(1);
	}
	int n = Integer.parseInt(args[0]);
	long start, end;
	double finalt;
		
	start = System.nanoTime();
	Sequential s = new Sequential(n);
	s.sequential();
	end = System.nanoTime();
	finalt = (double)(end-start)/1000000.0;
	System.out.println("Time used Sequential: " + finalt);

	start = System.nanoTime();
	Parallellv2 p = new Parallellv2(n);
	p.parallel();
	end = System.nanoTime();
	finalt = (double)(end-start)/1000000.0;
	System.out.println("Time used parallelv2: " + finalt);
    }
}
