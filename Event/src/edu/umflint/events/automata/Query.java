package edu.umflint.events.automata;

import java.util.concurrent.*;
import java.io.*;

public class Query {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		try {
			ConcurrentLinkedQueue<String> q1 = new ConcurrentLinkedQueue<String>();
			ConcurrentLinkedQueue<String> q2 = new ConcurrentLinkedQueue<String>();
			BufferedReader r = new BufferedReader(new FileReader("input.txt"));
			BufferedWriter w = new BufferedWriter(new FileWriter("output.txt"));

			RegExp reg = new RegExp();
			reg.setRegExp("a, ((!b) & c)"); // produces 255 states
			reg.buildParseTree();
			
			Automaton a = reg.constructAut(reg.expTree.root);
			a = Automaton.removeEps(a);
			a = Automaton.determinize(a); // use this to make it deterministic
			//a = Automaton.extendAllAlph(a); // use this to keep it NFA
			a.getAlphabet();
			
			InputOperator ioper = new InputOperator(r, q1);
			DFAOperator doper = new DFAOperator(q1, q2, a); // for DFA
			// NFAOperator doper = new NFAOperator(q1, q2, a); // for NFA
			OutputOperator ooper = new OutputOperator(q2, w);
			
			Thread t1 = new Thread(ioper);
			Thread t2 = new Thread(doper);
			Thread t3 = new Thread(ooper);

			long start = System.currentTimeMillis(); 

			t1.start();
			t2.start();
			t3.start();
			t1.join();
			t2.join();
			t3.join();
			
			long elapsedTimeMillis = System.currentTimeMillis()-start; 

			System.out.println ("time taken : " + elapsedTimeMillis);
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		

	}

}
