package edu.umflint.events.automata;

import java.util.concurrent.*;
import java.io.*;

public class Exp2Q2 {

	/**
	 * @param args
	 */
	public static void main(String[] args) {

		try {
			ConcurrentLinkedQueue<String> q1 = new ConcurrentLinkedQueue<String>();
			ConcurrentLinkedQueue<String> q2 = new ConcurrentLinkedQueue<String>();
			BufferedReader r = new BufferedReader(new FileReader("input.txt"));
			BufferedWriter w = new BufferedWriter(new FileWriter("output.txt"));

			RegExp reg = new RegExp();
			reg.setRegExp("a, ((!b) & c), ((!b) & c)");
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

			System.out.println ("time taken : 1 DFA " + elapsedTimeMillis);
			r.close();
			w.close();
			
		} catch (Exception e) {
			e.printStackTrace();
		}

		try {
			ConcurrentLinkedQueue<String> q1 = new ConcurrentLinkedQueue<String>();
			ConcurrentLinkedQueue<String> q2 = new ConcurrentLinkedQueue<String>();
			BufferedReader r = new BufferedReader(new FileReader("input.txt"));
			BufferedWriter w = new BufferedWriter(new FileWriter("output.txt"));

			RegExp reg = new RegExp();
			reg.setRegExp("a, ((!b) & c), ((!b) & c)");
			reg.buildParseTree();
			
			Automaton a = reg.constructAut(reg.expTree.root);
			a = Automaton.removeEps(a);
			// a = Automaton.determinize(a); // use this to make it deterministic
			a = Automaton.extendAllAlph(a); // use this to keep it NFA
			a.getAlphabet();
			
			InputOperator ioper = new InputOperator(r, q1);
			// DFAOperator doper = new DFAOperator(q1, q2, a); // for DFA
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

			System.out.println ("time taken : 1 NFA " + elapsedTimeMillis);
			r.close();
			w.close();
			
		} catch (Exception e) {
			e.printStackTrace();
		}

		try {
			ConcurrentLinkedQueue<String> iq1 = new ConcurrentLinkedQueue<String>();
			ConcurrentLinkedQueue<String> iq2 = new ConcurrentLinkedQueue<String>();
			ConcurrentLinkedQueue<String> iq3 = new ConcurrentLinkedQueue<String>();
			ConcurrentLinkedQueue<String> iq4 = new ConcurrentLinkedQueue<String>();
			ConcurrentLinkedQueue<String> iq5 = new ConcurrentLinkedQueue<String>();

			ConcurrentLinkedQueue<String> aq1 = new ConcurrentLinkedQueue<String>();
			ConcurrentLinkedQueue<String> aq2 = new ConcurrentLinkedQueue<String>();
			ConcurrentLinkedQueue<String> aq3 = new ConcurrentLinkedQueue<String>();
			ConcurrentLinkedQueue<String> aq4 = new ConcurrentLinkedQueue<String>();
			ConcurrentLinkedQueue<String> aq5 = new ConcurrentLinkedQueue<String>();
			
			ConcurrentLinkedQueue<String> jq1 = new ConcurrentLinkedQueue<String>();
			ConcurrentLinkedQueue<String> jq2 = new ConcurrentLinkedQueue<String>();

			BufferedReader r1 = new BufferedReader(new FileReader("input.txt"));
			BufferedReader r2 = new BufferedReader(new FileReader("input.txt"));
			BufferedReader r3 = new BufferedReader(new FileReader("input.txt"));
			BufferedReader r4 = new BufferedReader(new FileReader("input.txt"));
			BufferedReader r5 = new BufferedReader(new FileReader("input.txt"));
			BufferedWriter w = new BufferedWriter(new FileWriter("output.txt"));

			RegExp reg = new RegExp();
			reg.setRegExp("a, ((!b) & c), ((!b) & c)"); // produces 255 states
			reg.buildParseTree();
			
			Automaton a1 = reg.constructAut(reg.expTree.root);
			a1 = Automaton.removeEps(a1);
			a1 = Automaton.determinize(a1); // use this to make it deterministic
			// a1 = Automaton.extendAllAlph(a1); // use this to keep it NFA
			a1.getAlphabet();

			InputOperator ioper1 = new InputOperator(r1, iq1);
			InputOperator ioper2 = new InputOperator(r2, iq2);
			InputOperator ioper3 = new InputOperator(r3, iq3);
			InputOperator ioper4 = new InputOperator(r4, iq4);
			InputOperator ioper5 = new InputOperator(r5, iq5);

			// DFAOperator doper = new DFAOperator(q1, q2, a); // for DFA
			DFAOperator doper1 = new DFAOperator(iq1, aq1, a1); // for NFA
			DummyOperator doper2 = new DummyOperator(iq2, aq2); // for NFA
			DummyOperator doper3 = new DummyOperator(iq3, aq3); // for NFA
			DummyOperator doper4 = new DummyOperator(iq4, aq4); // for NFA
			DummyOperator doper5 = new DummyOperator(iq5, aq5); // for NFA
			
			JoinOperator joper1 = new JoinOperator(aq1, aq2, aq3, jq1);
			JoinOperator joper2 = new JoinOperator(jq1, aq4, aq5, jq2);
			
			OutputOperator ooper = new OutputOperator(jq2, w);
			
			Thread t11 = new Thread(ioper1);
			Thread t12 = new Thread(ioper2);
			Thread t13 = new Thread(ioper3);
			Thread t14 = new Thread(ioper4);
			Thread t15 = new Thread(ioper5);
			
			Thread t21 = new Thread(doper1);
			Thread t22 = new Thread(doper2);
			Thread t23 = new Thread(doper3);
			Thread t24 = new Thread(doper4);
			Thread t25 = new Thread(doper5);

			Thread t31 = new Thread(joper1);
			Thread t32 = new Thread(joper2);

			Thread ot = new Thread(ooper);

			long start = System.currentTimeMillis(); 

			t11.start();
			t12.start();
			t13.start();
			t14.start();
			t15.start();

			t21.start();
			t22.start();
			t23.start();
			t24.start();
			t25.start();

			t31.start();
			t32.start();
			
			ot.start();
			
			t11.join();
			t12.join();
			t13.join();
			t14.join();
			t15.join();

			t21.join();
			t22.join();
			t23.join();
			t24.join();
			t25.join();

			t31.join();
			t32.join();
			
			ot.join();
			
			long elapsedTimeMillis = System.currentTimeMillis()-start; 

			System.out.println ("time taken : DFA + dummy " + elapsedTimeMillis);
			r1.close();
			r2.close();
			r3.close();
			r4.close();
			r5.close();
			w.close();
			
		} catch (Exception e) {
			e.printStackTrace();
		}

		try {
			ConcurrentLinkedQueue<String> iq1 = new ConcurrentLinkedQueue<String>();
			ConcurrentLinkedQueue<String> iq2 = new ConcurrentLinkedQueue<String>();
			ConcurrentLinkedQueue<String> iq3 = new ConcurrentLinkedQueue<String>();
			ConcurrentLinkedQueue<String> iq4 = new ConcurrentLinkedQueue<String>();
			ConcurrentLinkedQueue<String> iq5 = new ConcurrentLinkedQueue<String>();

			ConcurrentLinkedQueue<String> aq1 = new ConcurrentLinkedQueue<String>();
			ConcurrentLinkedQueue<String> aq2 = new ConcurrentLinkedQueue<String>();
			ConcurrentLinkedQueue<String> aq3 = new ConcurrentLinkedQueue<String>();
			ConcurrentLinkedQueue<String> aq4 = new ConcurrentLinkedQueue<String>();
			ConcurrentLinkedQueue<String> aq5 = new ConcurrentLinkedQueue<String>();
			
			ConcurrentLinkedQueue<String> jq1 = new ConcurrentLinkedQueue<String>();
			ConcurrentLinkedQueue<String> jq2 = new ConcurrentLinkedQueue<String>();

			BufferedReader r1 = new BufferedReader(new FileReader("input.txt"));
			BufferedReader r2 = new BufferedReader(new FileReader("input.txt"));
			BufferedReader r3 = new BufferedReader(new FileReader("input.txt"));
			BufferedReader r4 = new BufferedReader(new FileReader("input.txt"));
			BufferedReader r5 = new BufferedReader(new FileReader("input.txt"));
			BufferedWriter w = new BufferedWriter(new FileWriter("output.txt"));

			RegExp reg = new RegExp();
			reg.setRegExp("a, ((!b) & c), ((!b) & c)"); // produces 255 states
			reg.buildParseTree();
			
			Automaton a1 = reg.constructAut(reg.expTree.root);
			a1 = Automaton.removeEps(a1);
			// a1 = Automaton.determinize(a1); // use this to make it deterministic
			a1 = Automaton.extendAllAlph(a1); // use this to keep it NFA
			a1.getAlphabet();

			InputOperator ioper1 = new InputOperator(r1, iq1);
			InputOperator ioper2 = new InputOperator(r2, iq2);
			InputOperator ioper3 = new InputOperator(r3, iq3);
			InputOperator ioper4 = new InputOperator(r4, iq4);
			InputOperator ioper5 = new InputOperator(r5, iq5);

			// DFAOperator doper = new DFAOperator(q1, q2, a); // for DFA
			NFAOperator doper1 = new NFAOperator(iq1, aq1, a1); // for NFA
			DummyOperator doper2 = new DummyOperator(iq2, aq2); // for NFA
			DummyOperator doper3 = new DummyOperator(iq3, aq3); // for NFA
			DummyOperator doper4 = new DummyOperator(iq4, aq4); // for NFA
			DummyOperator doper5 = new DummyOperator(iq5, aq5); // for NFA
			
			JoinOperator joper1 = new JoinOperator(aq1, aq2, aq3, jq1);
			JoinOperator joper2 = new JoinOperator(jq1, aq4, aq5, jq2);
			
			OutputOperator ooper = new OutputOperator(jq2, w);
			
			Thread t11 = new Thread(ioper1);
			Thread t12 = new Thread(ioper2);
			Thread t13 = new Thread(ioper3);
			Thread t14 = new Thread(ioper4);
			Thread t15 = new Thread(ioper5);
			
			Thread t21 = new Thread(doper1);
			Thread t22 = new Thread(doper2);
			Thread t23 = new Thread(doper3);
			Thread t24 = new Thread(doper4);
			Thread t25 = new Thread(doper5);

			Thread t31 = new Thread(joper1);
			Thread t32 = new Thread(joper2);

			Thread ot = new Thread(ooper);

			long start = System.currentTimeMillis(); 

			t11.start();
			t12.start();
			t13.start();
			t14.start();
			t15.start();

			t21.start();
			t22.start();
			t23.start();
			t24.start();
			t25.start();

			t31.start();
			t32.start();
			
			ot.start();
			
			t11.join();
			t12.join();
			t13.join();
			t14.join();
			t15.join();

			t21.join();
			t22.join();
			t23.join();
			t24.join();
			t25.join();

			t31.join();
			t32.join();
			
			ot.join();
			
			long elapsedTimeMillis = System.currentTimeMillis()-start; 

			System.out.println ("time taken : NFA + dummy " + elapsedTimeMillis);
			r1.close();
			r2.close();
			r3.close();
			r4.close();
			r5.close();
			w.close();
			
		} catch (Exception e) {
			e.printStackTrace();
		}

		
		try {
			ConcurrentLinkedQueue<String> iq1 = new ConcurrentLinkedQueue<String>();
			ConcurrentLinkedQueue<String> iq2 = new ConcurrentLinkedQueue<String>();
			ConcurrentLinkedQueue<String> iq3 = new ConcurrentLinkedQueue<String>();
			ConcurrentLinkedQueue<String> iq4 = new ConcurrentLinkedQueue<String>();
			ConcurrentLinkedQueue<String> iq5 = new ConcurrentLinkedQueue<String>();

			ConcurrentLinkedQueue<String> aq1 = new ConcurrentLinkedQueue<String>();
			ConcurrentLinkedQueue<String> aq2 = new ConcurrentLinkedQueue<String>();
			ConcurrentLinkedQueue<String> aq3 = new ConcurrentLinkedQueue<String>();
			ConcurrentLinkedQueue<String> aq4 = new ConcurrentLinkedQueue<String>();
			ConcurrentLinkedQueue<String> aq5 = new ConcurrentLinkedQueue<String>();
			
			ConcurrentLinkedQueue<String> jq1 = new ConcurrentLinkedQueue<String>();
			ConcurrentLinkedQueue<String> jq2 = new ConcurrentLinkedQueue<String>();

			BufferedReader r1 = new BufferedReader(new FileReader("input.txt"));
			BufferedReader r2 = new BufferedReader(new FileReader("input.txt"));
			BufferedReader r3 = new BufferedReader(new FileReader("input.txt"));
			BufferedReader r4 = new BufferedReader(new FileReader("input.txt"));
			BufferedReader r5 = new BufferedReader(new FileReader("input.txt"));
			BufferedWriter w = new BufferedWriter(new FileWriter("output.txt"));

			RegExp reg = new RegExp();
			reg.setRegExp("a"); // produces 255 states
			reg.buildParseTree();
			
			Automaton a1 = reg.constructAut(reg.expTree.root);
			a1 = Automaton.removeEps(a1);
			a1 = Automaton.determinize(a1); // use this to make it deterministic
			// a1 = Automaton.extendAllAlph(a1); // use this to keep it NFA
			a1.getAlphabet();

			reg.setRegExp("b"); // produces 255 states
			reg.buildParseTree();
			
			Automaton a2 = reg.constructAut(reg.expTree.root);
			a2 = Automaton.removeEps(a2);
			a2 = Automaton.determinize(a2); // use this to make it deterministic
			// a2 = Automaton.extendAllAlph(a2); // use this to keep it NFA
			a2.getAlphabet();

			reg.setRegExp("c"); // produces 255 states
			reg.buildParseTree();
			
			Automaton a3 = reg.constructAut(reg.expTree.root);
			a3 = Automaton.removeEps(a3);
			a3 = Automaton.determinize(a3); // use this to make it deterministic
			// a3 = Automaton.extendAllAlph(a3); // use this to keep it NFA
			a3.getAlphabet();

			reg.setRegExp("b"); // produces 255 states
			reg.buildParseTree();
			
			Automaton a4 = reg.constructAut(reg.expTree.root);
			a4 = Automaton.removeEps(a4);
			a4 = Automaton.determinize(a4); // use this to make it deterministic
			// a4 = Automaton.extendAllAlph(a4); // use this to keep it NFA
			a4.getAlphabet();

			reg.setRegExp("c"); // produces 255 states
			reg.buildParseTree();
			
			Automaton a5 = reg.constructAut(reg.expTree.root);
			a5 = Automaton.removeEps(a5);
			a5 = Automaton.determinize(a5); // use this to make it deterministic
			// a5 = Automaton.extendAllAlph(a5); // use this to keep it NFA
			a5.getAlphabet();
			
			InputOperator ioper1 = new InputOperator(r1, iq1);
			InputOperator ioper2 = new InputOperator(r2, iq2);
			InputOperator ioper3 = new InputOperator(r3, iq3);
			InputOperator ioper4 = new InputOperator(r4, iq4);
			InputOperator ioper5 = new InputOperator(r5, iq5);

			// DFAOperator doper = new DFAOperator(q1, q2, a); // for DFA
			DFAOperator doper1 = new DFAOperator(iq1, aq1, a1); // for NFA
			DFAOperator doper2 = new DFAOperator(iq2, aq2, a2); // for NFA
			DFAOperator doper3 = new DFAOperator(iq3, aq3, a3); // for NFA
			DFAOperator doper4 = new DFAOperator(iq4, aq4, a4); // for NFA
			DFAOperator doper5 = new DFAOperator(iq5, aq5, a5); // for NFA
			
			JoinOperator joper1 = new JoinOperator(aq1, aq2, aq3, jq1);
			JoinOperator joper2 = new JoinOperator(jq1, aq4, aq5, jq2);
			
			OutputOperator ooper = new OutputOperator(jq2, w);
			
			Thread t11 = new Thread(ioper1);
			Thread t12 = new Thread(ioper2);
			Thread t13 = new Thread(ioper3);
			Thread t14 = new Thread(ioper4);
			Thread t15 = new Thread(ioper5);
			
			Thread t21 = new Thread(doper1);
			Thread t22 = new Thread(doper2);
			Thread t23 = new Thread(doper3);
			Thread t24 = new Thread(doper4);
			Thread t25 = new Thread(doper5);

			Thread t31 = new Thread(joper1);
			Thread t32 = new Thread(joper2);

			Thread ot = new Thread(ooper);

			long start = System.currentTimeMillis(); 

			t11.start();
			t12.start();
			t13.start();
			t14.start();
			t15.start();

			t21.start();
			t22.start();
			t23.start();
			t24.start();
			t25.start();

			t31.start();
			t32.start();
			
			ot.start();
			
			t11.join();
			t12.join();
			t13.join();
			t14.join();
			t15.join();

			t21.join();
			t22.join();
			t23.join();
			t24.join();
			t25.join();

			t31.join();
			t32.join();
			
			ot.join();
			
			long elapsedTimeMillis = System.currentTimeMillis()-start; 

			System.out.println ("time taken : 5 DFAs + 2 joins " + elapsedTimeMillis);
			r1.close();
			r2.close();
			r3.close();
			r4.close();
			r5.close();
			w.close();
			
		} catch (Exception e) {
			e.printStackTrace();
		}


		
		
		try {
			ConcurrentLinkedQueue<String> iq1 = new ConcurrentLinkedQueue<String>();
			ConcurrentLinkedQueue<String> iq2 = new ConcurrentLinkedQueue<String>();
			ConcurrentLinkedQueue<String> iq3 = new ConcurrentLinkedQueue<String>();
			ConcurrentLinkedQueue<String> iq4 = new ConcurrentLinkedQueue<String>();
			ConcurrentLinkedQueue<String> iq5 = new ConcurrentLinkedQueue<String>();

			ConcurrentLinkedQueue<String> aq1 = new ConcurrentLinkedQueue<String>();
			ConcurrentLinkedQueue<String> aq2 = new ConcurrentLinkedQueue<String>();
			ConcurrentLinkedQueue<String> aq3 = new ConcurrentLinkedQueue<String>();
			ConcurrentLinkedQueue<String> aq4 = new ConcurrentLinkedQueue<String>();
			ConcurrentLinkedQueue<String> aq5 = new ConcurrentLinkedQueue<String>();
			
			ConcurrentLinkedQueue<String> jq1 = new ConcurrentLinkedQueue<String>();
			ConcurrentLinkedQueue<String> jq2 = new ConcurrentLinkedQueue<String>();

			BufferedReader r1 = new BufferedReader(new FileReader("input.txt"));
			BufferedReader r2 = new BufferedReader(new FileReader("input.txt"));
			BufferedReader r3 = new BufferedReader(new FileReader("input.txt"));
			BufferedReader r4 = new BufferedReader(new FileReader("input.txt"));
			BufferedReader r5 = new BufferedReader(new FileReader("input.txt"));
			BufferedWriter w = new BufferedWriter(new FileWriter("output.txt"));

			RegExp reg = new RegExp();
			reg.setRegExp("a"); // produces 255 states
			reg.buildParseTree();
			
			Automaton a1 = reg.constructAut(reg.expTree.root);
			a1 = Automaton.removeEps(a1);
			// a = Automaton.determinize(a); // use this to make it deterministic
			a1 = Automaton.extendAllAlph(a1); // use this to keep it NFA
			a1.getAlphabet();

			reg.setRegExp("b"); // produces 255 states
			reg.buildParseTree();
			
			Automaton a2 = reg.constructAut(reg.expTree.root);
			a2 = Automaton.removeEps(a2);
			// a = Automaton.determinize(a); // use this to make it deterministic
			a2 = Automaton.extendAllAlph(a2); // use this to keep it NFA
			a2.getAlphabet();

			reg.setRegExp("c"); // produces 255 states
			reg.buildParseTree();
			
			Automaton a3 = reg.constructAut(reg.expTree.root);
			a3 = Automaton.removeEps(a3);
			// a = Automaton.determinize(a); // use this to make it deterministic
			a3 = Automaton.extendAllAlph(a3); // use this to keep it NFA
			a3.getAlphabet();

			reg.setRegExp("b"); // produces 255 states
			reg.buildParseTree();
			
			Automaton a4 = reg.constructAut(reg.expTree.root);
			a4 = Automaton.removeEps(a4);
			// a = Automaton.determinize(a); // use this to make it deterministic
			a4 = Automaton.extendAllAlph(a4); // use this to keep it NFA
			a4.getAlphabet();

			reg.setRegExp("c"); // produces 255 states
			reg.buildParseTree();
			
			Automaton a5 = reg.constructAut(reg.expTree.root);
			a5 = Automaton.removeEps(a5);
			// a = Automaton.determinize(a); // use this to make it deterministic
			a5 = Automaton.extendAllAlph(a5); // use this to keep it NFA
			a5.getAlphabet();

			
			InputOperator ioper1 = new InputOperator(r1, iq1);
			InputOperator ioper2 = new InputOperator(r2, iq2);
			InputOperator ioper3 = new InputOperator(r3, iq3);
			InputOperator ioper4 = new InputOperator(r4, iq4);
			InputOperator ioper5 = new InputOperator(r5, iq5);

			// DFAOperator doper = new DFAOperator(q1, q2, a); // for DFA
			NFAOperator doper1 = new NFAOperator(iq1, aq1, a1); // for NFA
			NFAOperator doper2 = new NFAOperator(iq2, aq2, a2); // for NFA
			NFAOperator doper3 = new NFAOperator(iq3, aq3, a3); // for NFA
			NFAOperator doper4 = new NFAOperator(iq4, aq4, a4); // for NFA
			NFAOperator doper5 = new NFAOperator(iq5, aq5, a5); // for NFA
			
			JoinOperator joper1 = new JoinOperator(aq1, aq2, aq3, jq1);
			JoinOperator joper2 = new JoinOperator(jq1, aq4, aq5, jq2);
			
			OutputOperator ooper = new OutputOperator(jq2, w);
			
			Thread t11 = new Thread(ioper1);
			Thread t12 = new Thread(ioper2);
			Thread t13 = new Thread(ioper3);
			Thread t14 = new Thread(ioper4);
			Thread t15 = new Thread(ioper5);
			
			Thread t21 = new Thread(doper1);
			Thread t22 = new Thread(doper2);
			Thread t23 = new Thread(doper3);
			Thread t24 = new Thread(doper4);
			Thread t25 = new Thread(doper5);

			Thread t31 = new Thread(joper1);
			Thread t32 = new Thread(joper2);

			Thread ot = new Thread(ooper);

			long start = System.currentTimeMillis(); 

			t11.start();
			t12.start();
			t13.start();
			t14.start();
			t15.start();

			t21.start();
			t22.start();
			t23.start();
			t24.start();
			t25.start();

			t31.start();
			t32.start();
			
			ot.start();
			
			t11.join();
			t12.join();
			t13.join();
			t14.join();
			t15.join();

			t21.join();
			t22.join();
			t23.join();
			t24.join();
			t25.join();

			t31.join();
			t32.join();
			
			ot.join();
			
			long elapsedTimeMillis = System.currentTimeMillis()-start; 

			System.out.println ("time taken : 5 NFAs + 2 joins " + elapsedTimeMillis);
			r1.close();
			r2.close();
			r3.close();
			r4.close();
			r5.close();
			w.close();
			
		} catch (Exception e) {
			e.printStackTrace();
		}


	}

}
