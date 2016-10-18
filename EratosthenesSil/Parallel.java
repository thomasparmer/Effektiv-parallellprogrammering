import java.util.Random;
import java.util.Arrays;
import java.lang.Object;
import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.locks.*;

class Parallel {
  private final static int THREADS = Runtime.getRuntime().availableProcessors();
  private CyclicBarrier b;


  byte[] bitArr;
  int maxNum;
  int rootMax;
  Thread t;
  ArrayList <Long> fakt;
  int[][] factors;




  public Parallel(int maxNum) {
    this.maxNum = maxNum;
    bitArr = new byte[(maxNum/16)+1];
    b = new CyclicBarrier(THREADS+1);

  }

  byte[] parallel() {
    System.out.println("THREADS: " + THREADS);
    long startTime = System.nanoTime();
    setAllPrime();
    //createThreads();

    splitArea(); //test

    long endTime = System.nanoTime();
    double duration = (endTime-startTime)/1000000.0;


    startTime = System.nanoTime();
    //factorThreads();
    fastFactor();
    endTime = System.nanoTime();


    System.out.println("Time used parallel sieve of eratosthenes: " + duration);
    duration = (endTime-startTime)/1000000.0;
    System.out.println("Time used parallel factorize: " + duration);


      //printAllPrimes();
      //printbyte();

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
    bitArr[i/16] &= ~(1 << ((i%16)/2));
  }


  boolean isPrime (int i) {
    if (i == 2)
      return false;
    if (i%2 == 0)
      return false;
    if ((bitArr[i/16] & 1<<(i%16)/2) != 0)
      return true;
    return false;
  }


  int nextPrime(int i) {
      //if (i > maxNum)
      //  System.out.println(" " + i);
    boolean next = false;
    while (next == false) {
      if ((bitArr[i/16] & 1<<(i%16)/2) != 0)
        next = true;
      else
        i += 2;
    }
    return i;
  }

  void printAllPrimes(){
    for ( int i = 3; i <= maxNum; i++)
      if (isPrime(i)) System.out.println(" "+i);

  }

    void generatePrimesByEratosthenes(int root) { //noe galt her, rootmax ikke brukt
      crossOut(1);

      for (int x = 3; x <= Math.sqrt(maxNum); x +=2) {
        if (isPrime(x*x)) {
                //System.out.println("isprime x*x x: " + x);
          for (int i = x*x; i < maxNum; i += 2*x) { //root
            crossOut(i);
          }
        }
      }
    }

    int fromNextByte(int baseNumber, int num) {
      int next = baseNumber + num - 1;
      next -= (next % num);
      return next; 
    }


    void splitArea() {
      int last = 0;  
      int baseNumber;
      int multiple;
      SievePortion s;
      rootMax = (int)Math.sqrt(maxNum);
      //System.out.println("rootmax: " + rootMax);
      int next = rootMax/THREADS;
      int root = fromNextByte((int)Math.sqrt(rootMax), 16);
      //System.out.println(last + " " + root);

      generatePrimesByEratosthenes(root);
      last = root;
      last = fromNextByte(last, 16);
      next = fromNextByte(rootMax/THREADS, 16);

      for (int x = 0; x < THREADS; x++) {
	  //System.out.println(last + " " + next);
        s = new SievePortion(last, next); //Sieveportion
        last = next;


        if (x == THREADS-2)
          next = rootMax;
        else
          next = fromNextByte(next+(rootMax/THREADS), 16);

        t = new Thread(s);
        t.start();
      }
      try {
       b.await();
     } catch(InterruptedException ex) {
       ex.printStackTrace();
     } catch (BrokenBarrierException e) {
       e.printStackTrace();
     }

   }





   void createThreads() {
    int last = 0;  
    Sieve s;
    rootMax = (int)Math.sqrt(maxNum);
    System.out.println("rootmax: " + rootMax);
    int next = rootMax/THREADS;
    int root = (int)Math.sqrt(rootMax);

    // System.out.println(last + " " + root);
    generatePrimesByEratosthenes(root);
    last = root+1;

    for (int x = 0; x < THREADS; x++) {
	//	System.out.println(last + " " + next);
      s = new Sieve(last, next);
      last = next;

      if (x == THREADS-2)
        next = rootMax;
      else
        next = next+(rootMax/THREADS);

      t = new Thread(s);
      t.start();
    }
    try {
     b.await();
   } catch(InterruptedException ex) {
     ex.printStackTrace();
   } catch (BrokenBarrierException e) {
     e.printStackTrace();
   }
 }





 synchronized void addToFactorList(long num) {
   fakt.add(num);
 }

 void factorThreads() {
   Factorize f;

   for (long i = (long)maxNum*(long)maxNum-100; i <= (long)maxNum*(long)maxNum; i++) {
     b = new CyclicBarrier(THREADS+1);
     fakt = new ArrayList <Long>();
     int last = 2;
     int next = maxNum/THREADS;

     for (int x = 0; x < THREADS; x++) {
        //System.out.println(last + " " + next + " " + i);
      f = new Factorize(last, next, i);
      last = next+1;

      if (x == THREADS-2)
        next = maxNum;
      else
        next = next+(maxNum/THREADS);

      t = new Thread(f);
      t.start();
    }
    try {
      b.await();

      if (fakt.size() > 0) {
        long temp = i;
        System.out.printf(i + " = ");
        for (int j = 0; j < fakt.size(); j++) {
          if (j == fakt.size()-1)
           System.out.print(" " + fakt.get(j));
         else
           System.out.print(fakt.get(j) + " * ");
         temp /= fakt.get(j);
       }
       if (temp != 1)
        System.out.print(" * " + temp + " ");
      System.out.println();
    }

		//Arrays.sort(fakt);



  } catch(Exception e) {
    e.printStackTrace();
  } /*catch(InterruptedException ex) {
    ex.printStackTrace();
  } catch (BrokenBarrierException e) {
    e.printStackTrace();
  }*/
}
}  

synchronized void fastaddToFactorList(long num, int index) {

}


void fastFactor() {
 FastFactorize f;
 long i = (long)maxNum*(long)maxNum;
 factors = new int[100][50];
 b = new CyclicBarrier(THREADS+1);
 Monitor m = new Monitor();

 int last = 2;
 int next = maxNum/THREADS;

 for (int x = 0; x < THREADS; x++) {
  f = new FastFactorize(last, next, i, m);
  last = next+1;

  if (x == THREADS-2)
    next = maxNum;
  else
    next = next+(maxNum/THREADS);
  t = new Thread(f);
  t.start();
}
try {
  b.await();
  m.print();
} catch(Exception e) {
  e.printStackTrace();
}
}

private class Factorize implements Runnable {
 private int from;
 private int to;
 private long num;


 public Factorize(int from, int to, long num) {
   this.from = from;
   this.to = to;
   this.num = num;
 }

 public void run() {
   try {
        //System.out.println("from: " + from + " to: " + to);
    int x = from;
    if (x == 2) {
      while (num % x == 0) {
        fastaddToFactorList(x, 1);
        num /= x;
      }
    }

    if (x % 2 == 0)
      x++;

    x = nextPrime(x);
    while (x < to && x < Math.sqrt(num)) {
      while (num % x == 0) {
        fastaddToFactorList(x, 1);
        num /= x;
      }
      x += 2;
      x = nextPrime(x);
    }
    b.await();
  } catch(Exception e) {
    e.printStackTrace();
  }
}
}

class Monitor {
  long[][]answers = new long[100][50];
  int[] cnt = new int[100];

  synchronized void monitor(long[][] ans) {
    for (int i = 0; i < 100; i++) {
      for (int j = 0; j < 50; j++) {
        if (ans[i][j] == 0)
          break;
        answers[i][cnt[i]++] = ans[i][j];
      }
    }
  }

  public void print() {
    long check;
    for (int i = 0; i < 100; i++) {
      check = (long)maxNum*(long)maxNum-i;
      System.out.print(check + " = ");
      for (int j = 0; j < 50; j++) {
        if (answers[i][j] == 0) {
          if (check > 1)
            System.out.print(check);
          break;
        }
        System.out.print(answers[i][j] + " * ");
        check /= answers[i][j];
      }
      System.out.println();
    }
  }
}


private class FastFactorize implements Runnable {
 private int from;
 private int to;
 private long num;
 private Monitor m;
 long[][] answers = new long[100][100];
 long[] last = new long[100];


 public FastFactorize(int from, int to, long num, Monitor m) {
   this.from = from;
   this.to = to;
   this.num = num;
   this.m = m;
 }

 public void run() {
   try {
    int[] cnt = new int[100];
    if (from == 2) {
      for (int x = 99; x >= 0; x--) {
        last[x] = num -x;
        while (last[x] % 2 == 0) {
          answers[x][cnt[x]++] = 2;
          last[x] /= 2;
        }
      }
      from++;
    } else {
      for (int x = 99; x >= 0; x--) {
        last[x] = num -x;
      }

      if (from % 2 == 0)
          from++;
    }

    int i = from;
    while (i < to) {
      i = nextPrime(i);
      for (int x = 99; x >= 0; x--) {
        while (last[x] % i == 0) {
          answers[x][cnt[x]++] = i;
          last[x] /= i;
        }
      }
      i += 2;
    }

    m.monitor(answers);
    b.await();

  } catch(Exception e) {
    e.printStackTrace();
  }
}
}


private class Sieve implements Runnable {
 private int from;
 private int to;
 private int step = 2;

 public Sieve(int from, int to) {
   this.from = from;
   this.to = to;
 }

 public void run() {
   try {
		//System.out.println("from: " + from + " to: " + to);
    if (from % 2 == 0)
      from++;

    int temp;
    int x = from;
    while (x < to) {
      x = nextPrime(x);
      for (int i = x*x; i < maxNum; i += 2*x) {
       crossOut(i);
     }
     x += 2;
   }

   b.await();
 } catch(InterruptedException ex) {
  ex.printStackTrace();
} catch (BrokenBarrierException e) {
  e.printStackTrace();
}
}
}



private class SievePortion implements Runnable {
 private int from;
 private int to;
 private int step;

 public SievePortion(int from, int to) {
   this.from = from;
   this.to = to;
 }

 public void run() {
   try {
    //System.out.println("from: " + from + " to: " + to);
    if (from % 2 == 0)
      from++;

    int top = (int)Math.sqrt(maxNum);
    if (top > Math.sqrt(to))
      top = (int)Math.sqrt(to);

    int temp;
    for (int x = 3; x <= top; x +=2) {
      if (isPrime(x)) {
        if (x*x > from)
          from = x*x;
        step = fromNextByte(from, x);
        if (step % 2 == 0)
         step += x;
      //System.out.println("Thread " + Thread.currentThread().getId() + " step: " + step + " prime: " + x);
          for (int i = step; i < to; i += 2*x) { //root
            crossOut(i);
          }
        }
      }
      
      b.await();
    } catch(InterruptedException ex) {
      ex.printStackTrace();
    } catch (BrokenBarrierException e) {
      e.printStackTrace();
    }
  }
}
}