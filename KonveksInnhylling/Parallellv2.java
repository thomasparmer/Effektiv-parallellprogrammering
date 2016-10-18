import java.io.*;
import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.concurrent.BrokenBarrierException;

class Parallellv2 {
    private static final int THREADS = 2;
    private int createdThreads;
    private CyclicBarrier b;
    private CyclicBarrier c;
    IntList[] kohyll;
    IntList hyll;
    int[] x;
    int[] y;
    int n;
    Lock lock1;
    Lock lock2;

    int MAX_X;
    int MAX_Y;
    int globalCnt = 10;
    int counter = 0;
    private NPunkter p;

    synchronized int getCreatedThreads() {
	return --createdThreads;
    }


    public Parallellv2(int n) {
	this.n = n;
	x = new int[n];
	y = new int[n];
	p = new NPunkter(n);
	b = new CyclicBarrier(THREADS+1);
	c = new CyclicBarrier(4+1);
	lock1 = new ReentrantLock();
	lock2 = new ReentrantLock();
	createdThreads = THREADS-2;
    }

    void parallel() {
	p.fyllArrayer(x, y);
	int[] maxxminx = maxminX();
	findMaxY();
	IntList m = new IntList(n);
	for (int i = 0; i < n; i++)
	    m.add(i);

		
	kohyll = new IntList[THREADS];
	for (int i = 0; i < THREADS; i++) {
	    kohyll[i] = new IntList();
	}
	Recursive r;
	Thread t;
	int tnum = 0;
	int tnum2 = THREADS/2;


	kohyll[tnum].add(maxxminx[0]);

	int[] line = findLine(maxxminx[0], maxxminx[1]);
	IntList tmp = possiblePoints(line, m);//fungerer



	int p3 = findFurthest(line, maxxminx[0], maxxminx[1], tmp);

	r = new Recursive(maxxminx[1], maxxminx[0], p3, tmp, tnum++);
	t = new Thread(r);
	t.start();

	kohyll[tnum].add(maxxminx[1]);
	line = findLine(maxxminx[1], maxxminx[0]);
	tmp = possiblePoints(line, m);
	p3 = findFurthest(line, maxxminx[1], maxxminx[0], tmp); //m
		
	r = new Recursive(maxxminx[0], maxxminx[1], p3, tmp, tnum); //m
	t = new Thread(r);
	t.start();
	try {
	    b.await();
	} catch(Exception e) {
	    e.printStackTrace();
	}

	hyll = new IntList();
	for (int i = 0; i < kohyll.length; i++) {
	    for (int j = 0; j < kohyll[i].size(); j++) {
		hyll.add(kohyll[i].get(j));
	    }
	}
	//TegnUt tu = new TegnUt(this, hyll, "Konveks innhylling");
    }

    int[] maxminX() {
	int[] maxxminx = {0, 0};
	for (int i = 1; i < n; i++) {
	    if (x[maxxminx[1]] < x[i]) {
		maxxminx[1] = i;
	    }
	    else if (x[maxxminx[0]] > x[i]) {
		maxxminx[0] = i;
	    }
	} 
	MAX_X = x[maxxminx[1]];
	return maxxminx;
    }

    int[] findLine(int p1, int p2) { //p1, p2

    	int[] line = new int[3];
    	line[0] = y[p1] - y[p2];
    	line[1] = x[p2] - x[p1];
    	line[2] = y[p2] * x[p1] - y[p1] * x[p2];
    	return line;
    }

    void findMaxY() {
    	int maxy = 0;
    	for (int i = 0; i < n; i++) {
	    if (y[maxy] < y[i])
		maxy = i;
    	}
    	MAX_Y = y[maxy];
    }

    int findDistance(int a, int b, int c, int point) {
    	int d;
	d = (a * x[point] + b * y[point] + c); // / (int)Math.sqrt(a*a + b*b);
	return d;
    }


    int findFurthest(int[] line, int p1, int p2, IntList m) { //fiks
    	int furthest = 0;
    	int furthestIndex = -1;
    	int tmp;
    	int val;
    	for (int i = 0; i < m.size(); i++) {
	    val = m.get(i);
	    tmp = findDistance(line[0], line[1], line[2], val); //Math.abs

	    if (tmp == 0) {
	    	if (y[p1] == y[val] && y[p2] == y[val]) {
		    if (x[p1] > x[val] && x[p2] < x[val]) {
			return val;
		    }
		    if (x[p2] > x[val] && x[p1] < x[val]) {
			return val;
		    }
	    	} 
	    	if(x[p1] == x[val] && x[p2] == x[val]) {
		    if (y[p1] > y[val] && y[p2] < y[val]) {
			return val;
		    }
		    if (y[p2] > y[val] && y[p1] < y[val]) {
			return val;
		    }
	    	}
	    }

	    if (furthest < tmp) {
	    	furthest = tmp;
	    	furthestIndex = val;
	    }
	}
	return furthestIndex;
    }

    IntList possiblePoints(int[] line, IntList m) {
	IntList list = new IntList();
	int tmp;
	int val;
	for (int i = m.size() -1; i >= 0; i--) {
	    val = m.get(i);
	    tmp = findDistance(line[0], line[1], line[2], val);
	    if (tmp >= 0)
		list.add(val);
	}
	return list;
    }


    void sekvRek(int p1, int p2, int p3, IntList m, int tnum) {
	int[] line = findLine(p2, p3);
	IntList tmp = possiblePoints(line, m);
	
	int furthestIndex = findFurthest(line, p2, p3, tmp);


	if (furthestIndex != -1) { 
	    sekvRek(p3, p2, furthestIndex, tmp, tnum);
	}
	kohyll[tnum].add(p3);
	//System.out.println("added: " + p3);
	line = findLine(p3, p1);
	tmp = possiblePoints(line, m);
	furthestIndex = findFurthest(line, p3, p1, tmp);
	
	if (furthestIndex != -1) {
	    sekvRek(p1, p3, furthestIndex, tmp, tnum);
	} 
    }


    private class Recursive implements Runnable {
	int tnum;
	int p1, p2, p3;
	IntList m;

	public Recursive(int p1, int p2, int p3, IntList m, int tnum) {
	    this.p1 = p1;
	    this.p2 = p2;
	    this.p3 = p3;
	    this.m = m;
	    this.tnum = tnum;
	}

	public void run() {
	    Thread t;
	    Recursive r;
	    int nextTnum = tnum;

	    int[] line = findLine(p2, p3);
	    IntList tmp = possiblePoints(line, m);
	    int furthestIndex = findFurthest(line, p2, p3, tmp);

	    if (furthestIndex != -1) {
		sekvRek(p3, p2, furthestIndex, tmp, nextTnum);
	    }

	    boolean exists = false;
	    for (int i = 0; i < kohyll.length; i++) {
		if (kohyll[i].contains(p3)) {
		    exists = true;
		}
	    }
	    if (!exists) {
		kohyll[tnum].add(p3); //sortere paa avstand?
	    }
	    //System.out.println("added: "+p3);

	    line = findLine(p3, p1);
	    tmp = possiblePoints(line, m);
	    furthestIndex = findFurthest(line, p3, p1, tmp);


	    if (furthestIndex != -1) {
		sekvRek(p1, p3, furthestIndex, tmp, nextTnum);
	    } 

	    try {
		//System.out.println("await: " + tnum);
		b.await();
	    } catch(Exception e) {
		e.printStackTrace();
	    }
	}
    }

    private class Points implements Runnable {
	private int start, stop;
	private IntList m;
	private IntList newList;
	private int[] line;
	private Lock lock;


	Points(int start, int stop, IntList m, IntList newList, int[] line, Lock lock) {
	    this.start = start;
	    this.stop = stop;
	    this.m = m;
	    this.newList = newList;
	    this.line = line;
	    this.lock = lock;
	}
	

	public void run() {
	    int tmp;
	    int val;
	    //System.out.println("Start: " + start + " stop: " + stop);
	    for (int i = start; i < stop; i++) {
		val = m.get(i);
		tmp = findDistance(line[0], line[1], line[2], val);
		if (tmp >= 0) {
		    try {
			lock.lock();
			newList.add(val);
		    } finally {
			lock.unlock();
		    }

		}
	    }
	    try {
		c.await();
	    } catch(InterruptedException | BrokenBarrierException e) {
		e.printStackTrace();
	    }

	}

    }

}
