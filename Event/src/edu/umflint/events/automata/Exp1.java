package edu.umflint.events.automata;

import java.util.concurrent.*;
import java.io.*;

public class Exp1 {

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
			reg.setRegExp("a, b"); 
			reg.buildParseTree();
			
			Automaton a = reg.constructAut(reg.expTree.root);
			a = Automaton.removeEps(a);
			a = Automaton.determinize(a); // use this to make it deterministic
			a.getAlphabet();
			
			InputOperator ioper = new InputOperator(r, q1);
			DFAOperator doper = new DFAOperator(q1, q2, a); // for DFA
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

			System.out.println ("time taken -- deterministic -- a, b: " + elapsedTimeMillis);
			
		} catch (Exception e) {
			e.printStackTrace();
		}

/*		try {
			ConcurrentLinkedQueue<String> q1 = new ConcurrentLinkedQueue<String>();
			ConcurrentLinkedQueue<String> q2 = new ConcurrentLinkedQueue<String>();
			BufferedReader r = new BufferedReader(new FileReader("input.txt"));
			BufferedWriter w = new BufferedWriter(new FileWriter("output.txt"));

			RegExp reg = new RegExp();
			reg.setRegExp("a, b, c"); 
			reg.buildParseTree();
			
			Automaton a = reg.constructAut(reg.expTree.root);
			a = Automaton.removeEps(a);
			a = Automaton.determinize(a); // use this to make it deterministic
			a.getAlphabet();
			
			InputOperator ioper = new InputOperator(r, q1);
			DFAOperator doper = new DFAOperator(q1, q2, a); // for DFA
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

			System.out.println ("time taken -- deterministic -- a, b, c: " + elapsedTimeMillis);
			
		} catch (Exception e) {
			e.printStackTrace();
		}

		try {
			ConcurrentLinkedQueue<String> q1 = new ConcurrentLinkedQueue<String>();
			ConcurrentLinkedQueue<String> q2 = new ConcurrentLinkedQueue<String>();
			BufferedReader r = new BufferedReader(new FileReader("input.txt"));
			BufferedWriter w = new BufferedWriter(new FileWriter("output.txt"));

			RegExp reg = new RegExp();
			reg.setRegExp("a, b, c, a"); 
			reg.buildParseTree();
			
			Automaton a = reg.constructAut(reg.expTree.root);
			a = Automaton.removeEps(a);
			a = Automaton.determinize(a); // use this to make it deterministic
			a.getAlphabet();
			
			InputOperator ioper = new InputOperator(r, q1);
			DFAOperator doper = new DFAOperator(q1, q2, a); // for DFA
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

			System.out.println ("time taken -- deterministic -- a, b, c, a: " + elapsedTimeMillis);
			
		} catch (Exception e) {
			e.printStackTrace();
		}

		try {
			ConcurrentLinkedQueue<String> q1 = new ConcurrentLinkedQueue<String>();
			ConcurrentLinkedQueue<String> q2 = new ConcurrentLinkedQueue<String>();
			BufferedReader r = new BufferedReader(new FileReader("input.txt"));
			BufferedWriter w = new BufferedWriter(new FileWriter("output.txt"));

			RegExp reg = new RegExp();
			reg.setRegExp("a, b, c, a, b"); 
			reg.buildParseTree();
			
			Automaton a = reg.constructAut(reg.expTree.root);
			a = Automaton.removeEps(a);
			a = Automaton.determinize(a); // use this to make it deterministic
			a.getAlphabet();
			
			InputOperator ioper = new InputOperator(r, q1);
			DFAOperator doper = new DFAOperator(q1, q2, a); // for DFA
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

			System.out.println ("time taken -- deterministic -- a, b, c, a, b: " + elapsedTimeMillis);
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		try {
			ConcurrentLinkedQueue<String> q1 = new ConcurrentLinkedQueue<String>();
			ConcurrentLinkedQueue<String> q2 = new ConcurrentLinkedQueue<String>();
			BufferedReader r = new BufferedReader(new FileReader("input.txt"));
			BufferedWriter w = new BufferedWriter(new FileWriter("output.txt"));

			RegExp reg = new RegExp();
			reg.setRegExp("a, b, c, a, b, c"); 
			reg.buildParseTree();
			
			Automaton a = reg.constructAut(reg.expTree.root);
			a = Automaton.removeEps(a);
			a = Automaton.determinize(a); // use this to make it deterministic
			a.getAlphabet();
			
			InputOperator ioper = new InputOperator(r, q1);
			DFAOperator doper = new DFAOperator(q1, q2, a); // for DFA
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

			System.out.println ("time taken -- deterministic -- a, b, c, a, b, c: " + elapsedTimeMillis);
			
		} catch (Exception e) {
			e.printStackTrace();
		}

		try {
			ConcurrentLinkedQueue<String> q1 = new ConcurrentLinkedQueue<String>();
			ConcurrentLinkedQueue<String> q2 = new ConcurrentLinkedQueue<String>();
			BufferedReader r = new BufferedReader(new FileReader("input.txt"));
			BufferedWriter w = new BufferedWriter(new FileWriter("output.txt"));

			RegExp reg = new RegExp();
			reg.setRegExp("a, b"); 
			reg.buildParseTree();
			
			Automaton a = reg.constructAut(reg.expTree.root);
			a = Automaton.removeEps(a);
			a = Automaton.extendAllAlph(a); // use this to keep it NFA
			a.getAlphabet();
			
			InputOperator ioper = new InputOperator(r, q1);
			NFAOperator doper = new NFAOperator(q1, q2, a); // for NFA
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

			System.out.println ("time taken -- non-deterministic -- a, b: " + elapsedTimeMillis);
			
		} catch (Exception e) {
			e.printStackTrace();
		}

		try {
			ConcurrentLinkedQueue<String> q1 = new ConcurrentLinkedQueue<String>();
			ConcurrentLinkedQueue<String> q2 = new ConcurrentLinkedQueue<String>();
			BufferedReader r = new BufferedReader(new FileReader("input.txt"));
			BufferedWriter w = new BufferedWriter(new FileWriter("output.txt"));

			RegExp reg = new RegExp();
			reg.setRegExp("a, b, c"); 
			reg.buildParseTree();
			
			Automaton a = reg.constructAut(reg.expTree.root);
			a = Automaton.removeEps(a);
			a = Automaton.extendAllAlph(a); // use this to keep it NFA
			a.getAlphabet();
			
			InputOperator ioper = new InputOperator(r, q1);
			NFAOperator doper = new NFAOperator(q1, q2, a); // for NFA
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

			System.out.println ("time taken -- non-deterministic -- a, b, c: " + elapsedTimeMillis);
			
		} catch (Exception e) {
			e.printStackTrace();
		}

		try {
			ConcurrentLinkedQueue<String> q1 = new ConcurrentLinkedQueue<String>();
			ConcurrentLinkedQueue<String> q2 = new ConcurrentLinkedQueue<String>();
			BufferedReader r = new BufferedReader(new FileReader("input.txt"));
			BufferedWriter w = new BufferedWriter(new FileWriter("output.txt"));

			RegExp reg = new RegExp();
			reg.setRegExp("a, b, c, a"); 
			reg.buildParseTree();
			
			Automaton a = reg.constructAut(reg.expTree.root);
			a = Automaton.removeEps(a);
			a = Automaton.extendAllAlph(a); // use this to keep it NFA
			a.getAlphabet();
			
			InputOperator ioper = new InputOperator(r, q1);
			NFAOperator doper = new NFAOperator(q1, q2, a); // for NFA
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

			System.out.println ("time taken -- non-deterministic -- a, b, c, a: " + elapsedTimeMillis);
			
		} catch (Exception e) {
			e.printStackTrace();
		}

		try {
			ConcurrentLinkedQueue<String> q1 = new ConcurrentLinkedQueue<String>();
			ConcurrentLinkedQueue<String> q2 = new ConcurrentLinkedQueue<String>();
			BufferedReader r = new BufferedReader(new FileReader("input.txt"));
			BufferedWriter w = new BufferedWriter(new FileWriter("output.txt"));

			RegExp reg = new RegExp();
			reg.setRegExp("a, b, c, a, b");
			reg.buildParseTree();
			
			Automaton a = reg.constructAut(reg.expTree.root);
			a = Automaton.removeEps(a);
			a = Automaton.extendAllAlph(a); // use this to keep it NFA
			a.getAlphabet();
			
			InputOperator ioper = new InputOperator(r, q1);
			NFAOperator doper = new NFAOperator(q1, q2, a); // for NFA
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

			System.out.println ("time taken -- non-deterministic -- a, b, c, a, b: " + elapsedTimeMillis);
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		try {
			ConcurrentLinkedQueue<String> q1 = new ConcurrentLinkedQueue<String>();
			ConcurrentLinkedQueue<String> q2 = new ConcurrentLinkedQueue<String>();
			BufferedReader r = new BufferedReader(new FileReader("input.txt"));
			BufferedWriter w = new BufferedWriter(new FileWriter("output.txt"));

			RegExp reg = new RegExp();
			reg.setRegExp("a, b, c, a, b, c"); 
			reg.buildParseTree();
			
			Automaton a = reg.constructAut(reg.expTree.root);
			a = Automaton.removeEps(a);
			a = Automaton.extendAllAlph(a); // use this to keep it NFA
			a.getAlphabet();
			
			InputOperator ioper = new InputOperator(r, q1);
			NFAOperator doper = new NFAOperator(q1, q2, a); // for NFA
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

			System.out.println ("time taken -- non-deterministic -- a, b, c, a, b, c: " + elapsedTimeMillis);
			
		} catch (Exception e) {
			e.printStackTrace();
		}*/



	}

}
