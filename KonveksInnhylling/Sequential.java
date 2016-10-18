import java.io.*;
import java.util.*;


class Sequential {
    int[] x;
    int[] y;
    int n;
    int MAX_X;
    int MAX_Y;
    int globalCnt = 10;
    int counter = 0;
    private NPunkter p;

    public Sequential(int n) {
	this.n = n;
	x = new int[n];
	y = new int[n];
	p = new NPunkter(n);
    }

    void sequential() {
	p.fyllArrayer(x, y);
	int[] maxxminx = maxminX();
	findMaxY();
	IntList m = new IntList(n);
	for (int i = 0; i < n; i++)
	    m.add(i);
	IntList kohyll = new IntList();

	kohyll.add(maxxminx[0]);
	//System.out.println("added: "+maxxminx[0]);
	//kohyll.add(MAX_Y);

	int[] line = findLine(maxxminx[0], maxxminx[1]);
	int p3 = findFurthest(line, maxxminx[0], maxxminx[1], m);
	IntList tmp = possiblePoints(line, m);
	//System.out.println("minx: " + maxxminx[0] + " maxx: " + maxxminx[1]);
	//System.out.println("toppunkt: " + p3);
	sekvRek(maxxminx[1], maxxminx[0], p3, tmp, kohyll);
	kohyll.add(maxxminx[1]);
	//System.out.println("added: "+maxxminx[1]);

	line = findLine(maxxminx[1], maxxminx[0]);
	p3 = findFurthest(line, maxxminx[1], maxxminx[0], m);
	tmp = possiblePoints(line, m);
	sekvRek(maxxminx[0], maxxminx[1], p3, tmp, kohyll);

	for ( int i = 0; i < kohyll.size(); i++) {
	    System.out.print(" " + kohyll.get(i));
	}
	System.out.println();
	//TegnUt tu = new TegnUt(this, kohyll, "Konveks innhylling");
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
	d = (a * x[point] + b * y[point] + c);
	return d;
    }


    int findFurthest(int[] line, int p1, int p2, IntList m) { //fiks 
	int furthest = 0;
	int furthestIndex = -1;
	int tmp;
	int val;
	for (int i = 0; i < m.size(); i++) {
	    val = m.get(i);
	    tmp = findDistance(line[0], line[1], line[2], val);

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
		furthestIndex = val; //val
	    }
	}
	return furthestIndex;
    }

    IntList possiblePoints(int[] line, IntList m) {
	IntList list = new IntList();
	int tmp;
	int val;
	for (int i = 0; i < m.size(); i++) {
	    val = m.get(i);
	    tmp = findDistance(line[0], line[1], line[2], val);
	    if (tmp >= 0)
		list.add(val);
	}
	return list;
    }


    void sekvRek(int p1, int p2, int p3, IntList m, IntList kohyll) {
	int[] line = findLine(p2, p3);
	IntList tmp = possiblePoints(line, m);
	
	int furthestIndex = findFurthest(line, p2, p3, tmp);


	if (furthestIndex != -1) { //evt 1
	    sekvRek(p3, p2, furthestIndex, tmp, kohyll);
	}
	kohyll.add(p3);
	//System.out.println("added: "+p3);
	
	line = findLine(p3, p1);
	tmp = possiblePoints(line, m);
	furthestIndex = findFurthest(line, p3, p1, tmp);
	
	if (furthestIndex != -1) {
	    sekvRek(p1, p3, furthestIndex, tmp, kohyll);
	} 
    }
}
