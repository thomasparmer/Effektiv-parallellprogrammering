///--------------------------------------------------------
//
//     File: EratosthenesSil.java for INF2440-2016
//     implements bit-array (Boolean) for prime numbers
//     written by:  Arne Maus , Univ of Oslo,
//
//--------------------------------------------------------
import java.util.*;
/**
* Implements the bitArray of length 'maxNum' [0..maxNum/16 ]
*   1 - true (is prime number)
*   0 - false
*  can be used up to 2 G Bits (integer range)
*  16 numbers, i.e. 8 odd numbers per byte (bitArr[0] represents 1,3,5,7,9,11,13,15 )
*
*/
public class EratosthenesSil {
	byte[] bitArr;           // bitArr[0] represents the 8 integers:  1,3,5,...,15, and so on
	int  maxNum;               // all primes in this bit-array is <= maxNum
	final int[] bitMask = {1,2,4,8,16,32,64,128};  // kanskje trenger du denne
	final int[] bitMask2 = {255-1,255-2,255-4,255-8,255-16,255-32,255-64, 255-128}; // kanskje trenger du denne


	EratosthenesSil (int maxNum) {
		this.maxNum = maxNum;
		bitArr = new byte [(maxNum/16)+1];

		

      } // end konstruktor ErathostenesSil
    
    byte[] eratosthenesSil() {
	long startTime = System.nanoTime();
	
	setAllPrime();
	generatePrimesByEratosthenes();
	long endTime = System.nanoTime();
	double duration = (endTime-startTime)/1000000.0;
	
	startTime = System.nanoTime();

	startFactor();
	endTime = System.nanoTime();
	
	System.out.println("Time used serial sieve of eratosthenes: " + duration);
	duration = (endTime-startTime)/1000000.0;
	System.out.println("Time used serial factorize: " + duration);
	

	//printbyte();
	//printAllPrimes();
	
	return bitArr;
    }


      void printbyte() {
      	for (byte x : bitArr) {
      		System.out.println(Integer.toBinaryString(x & 255 | 256).substring(1));
      	}
      }

      void setAllPrime() {
      	for (int i = 0; i < bitArr.length; i++) {
      		bitArr[i] = (byte)255;
      	}
      }

      void crossOut(int i) {
       // set as not prime- cross out (set to 0)  bit represening 'int i'
      	bitArr[i/16] &= ~(1 << ((i%16)/2));


	   }

	   boolean isPrime (int i) {
	   	if (i == 2)
	   		return false;
	   	if (i%2 == 0)
	   		return false;
	   	if ((bitArr[i/16] & 1<<(i%16)/2) != 0)
	   		return true;


          // <din kode her, husk å teste særskilt for 2 (primtall) og andre partall først
          // før du slår opp i bitArr som jo bare representerer oddetallene>
	   	return false;
	   }

	   void startFactor() {
	   	for (long i = (long)maxNum*(long)maxNum-100; i < (long)maxNum*(long)maxNum; i++) {
	   		ArrayList<Long> fakt = factorize(i);
	   		System.out.printf(i + " = ");
	   		for (int j = 0; j < fakt.size(); j++) {
	   			if (j == fakt.size()-1)
	   				System.out.print(" " + fakt.get(j));
	   			else
	   				System.out.print(fakt.get(j) + " *");
	   		}
	   		System.out.println();
	   	}
	   }	

	   ArrayList<Long> factorize (long num) {
	   	ArrayList <Long> fakt = new ArrayList <Long>();
	   	long temp = num;
	   	long primeFactor = 2; 
	   	while (primeFactor < Math.sqrt(temp)) {
	   		while (temp % primeFactor == 0) {
	   			temp /= primeFactor;
	   			fakt.add(primeFactor);
	   		}

	   		if (primeFactor == 2)
	   			primeFactor++;
	   		else
	   			primeFactor += 2;

	   		primeFactor = nextPrime((int)primeFactor);
	   	}
	   	if (temp > 1)
	   		fakt.add(temp);

	   	long tmp = 1;
	   	for (int j = 0; j < fakt.size(); j++) {
	   		tmp *= fakt.get(j);
	   	}

	   	return fakt;
	  } // end factorize


	  int nextPrime(int i) {
	  	boolean next = false;
	  	while (next == false) {
	  		if ((bitArr[i/16] & 1<<(i%16)/2) != 0)
	  			next = true;
	  		else
	  			i += 2;
	  	}

	   // returns next prime number after number 'i'
          // <din kode her>
	  	return i;
	  } // end nextTrue


	  void printAllPrimes(){
	  	for ( int i = 3; i <= maxNum; i++)
	  		if (isPrime(i)) System.out.println(" "+i);

	  }

	  void generatePrimesByEratosthenes() {
		  // krysser av alle  oddetall i 'bitArr[]' som ikke er primtall (setter de =0)
		       crossOut(1);      // 1 er ikke et primtall

		       for (int x = 3; x < Math.sqrt(maxNum); x +=2) {
		       	if (isPrime(x*x)) {
		       		for (int i = x*x; i < maxNum; i += 2*x) {
		       			crossOut(i);
		       		}
		       	} else {

		       	}
		       }
		       
		       
		       // < din Kode her, kryss ut multipla av alle primtall <= sqrt(maxNum),
		       // og start avkryssingen av neste primtall p med p*p>

	 	 } // end generatePrimesByEratosthenes


} // end class EratosthenesSil