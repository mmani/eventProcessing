package com.umflint.edu.regExParsePkg;

import java.util.LinkedHashSet;

public class Buffer implements Comparable<Buffer> {
	public String num; // the buffer number
	public LinkedHashSet<String> actions = new LinkedHashSet<String>(); // how to populate the buffer
	public LinkedHashSet<String> tag=new LinkedHashSet<String>(); // this is the list of variables/tags associated with the buffer... do we need this???
	public Buffer clone()
	{
		Buffer newBuf = new Buffer();
		newBuf.num = this.num;
		newBuf.actions = (LinkedHashSet<String>) this.actions.clone();
		newBuf.tag = (LinkedHashSet<String>) this.tag.clone();
		return newBuf;
	}

	public String toString() {
		return this.num;
	}
	
	@Override
	public int compareTo(Buffer o) {
		// TODO Auto-generated method stub
		String s1, s2;
		double num1, num2;
		s1 = this.num.substring(1, this.num.indexOf(':'));
		s1 += "." + this.num.substring(this.num.indexOf(':') + 1);
		// System.out.println ("s1 = " + s1);

		s2 = o.num.substring(1, o.num.indexOf(':'));
		s2 += "." + o.num.substring(o.num.indexOf(':') + 1);
		// System.out.println ("s2 = " + s2);

		num1 = Double.parseDouble(s1);
		num2 = Double.parseDouble(s2);
		
		if (num1 == num2) return 0;
		if (num1 > num2) return 1;
		if (num1 < num2) return -1;
		return 0;
	}

}
