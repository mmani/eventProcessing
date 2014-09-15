package edu.umflint.events.automata;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.util.*;

public class StreamGenerator {

	public static void main (String[] argv) {
		try {
			BufferedWriter w = new BufferedWriter(new FileWriter("input.txt"));
			Random ran = new Random();
			
			int limit = Integer.parseInt(argv[0]);
			
			for (int i = 1; i <= limit; i++) {
				int curr = ran.nextInt(5);
				char c = (char) ('a' + curr);
				w.write(c);
				w.write(" id = " + i);
				w.write(" id1 = " + c + i);
				w.newLine();
			}
			w.close();
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}
	
}
