package com.umflint.edu.regExParsePkg.test;

public class Test {

	public void clearBuffer(String item) {
		String pattern = "[^0-9]+";
		String s[] = item.split(pattern);
		for (String s1: s)
			System.out.println("item = " + s1);
		int max = 0, min = 0, curr;
		for (String s1: s){
			if (s1.equals("")) continue;
			curr = Integer.parseInt(s1);
			if (max == 0) { max = curr; min = curr; }
			max = (curr > max ? curr : max);
			min = (curr < min ? curr : min);
		}
		System.out.println ("max = " + max);
		System.out.println ("min = " + min);		
	}
	public static void main(String[] args) {
		Test t = new Test();
		t.clearBuffer("a4:b5");
		
	}

}
