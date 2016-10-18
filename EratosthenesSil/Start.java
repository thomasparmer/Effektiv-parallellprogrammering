class Start {
	public static void main(String[] args) {
	    if (args.length != 1) {
	    	System.out.println("USAGE: java Start [N]");
	    	System.exit(1);
	    }
	    int size = Integer.parseInt(args[0]);

	    boolean equal = true;

	    EratosthenesSil sil = new EratosthenesSil(size);
	    byte[] bitArrSerial = sil.eratosthenesSil();
	    
	    Parallel par = new Parallel(size);
	    byte[] bitArrPara = par.parallel();
	    
	    for (int i = 0; i < bitArrSerial.length; i++)
		if (bitArrSerial[i] != bitArrPara[i])
		    equal = false;

	    System.out.println("implementation: " + equal);

	}
}