package com.umflint.edu.regExParsePkg.test;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

public class SortTest implements Comparable<SortTest> {

	public String num;
	
	public String toString() {
		return this.num;
	}

	public void split(String x) {
		String s = x.substring(x.indexOf("id1 = ") + 6);
		System.out.println ("s = " + s);
	}
	
	@Override
	public int compareTo(SortTest o) {
		// TODO Auto-generated method stub
		
		String s1, s2;
		double num1, num2;
		s1 = this.num.substring(1, this.num.indexOf(':'));
		s1 += "." + this.num.substring(this.num.indexOf(':') + 1);
		System.out.println ("s1 = " + s1);

		s2 = o.num.substring(1, o.num.indexOf(':'));
		s2 += "." + o.num.substring(o.num.indexOf(':') + 1);
		System.out.println ("s2 = " + s2);

		num1 = Double.parseDouble(s1);
		num2 = Double.parseDouble(s2);
		
		if (num1 == num2) return 0;
		if (num1 > num2) return 1;
		if (num1 < num2) return -1;
		return 0;
	}

	public static void main(String args[]) {
		SortTest t = new SortTest();
		t.split("a id = 12 id1 = a12");
		t.num = "b1:101";
		SortTest t1 = new SortTest();
		t1.num = "b11:1";
		
		LinkedList<SortTest> listT = new LinkedList<SortTest>();
		listT.add(t);
		listT.add(t1);
		Collections.sort(listT);
		System.out.println (listT);
		
	}
	
}
