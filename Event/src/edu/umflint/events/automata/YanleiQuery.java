package edu.umflint.events.automata;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.concurrent.ConcurrentLinkedQueue;

public class YanleiQuery {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		
		try {
			ConcurrentLinkedQueue<String> iq1 = new ConcurrentLinkedQueue<String>();
			ConcurrentLinkedQueue<String> iq2 = new ConcurrentLinkedQueue<String>();
			ConcurrentLinkedQueue<String> iq3 = new ConcurrentLinkedQueue<String>();

			ConcurrentLinkedQueue<String> aq1 = new ConcurrentLinkedQueue<String>();
			ConcurrentLinkedQueue<String> aq2 = new ConcurrentLinkedQueue<String>();
			ConcurrentLinkedQueue<String> aq3 = new ConcurrentLinkedQueue<String>();
			ConcurrentLinkedQueue<String> q2 = new ConcurrentLinkedQueue<String>();
			BufferedReader r1 = new BufferedReader(new FileReader("input.txt"));
			BufferedReader r2 = new BufferedReader(new FileReader("input.txt"));
			BufferedReader r3 = new BufferedReader(new FileReader("input.txt"));
			BufferedWriter w = new BufferedWriter(new FileWriter("output1.txt"));

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

			InputOperator ioper1 = new InputOperator(r1, iq1);
			InputOperator ioper2 = new InputOperator(r2, iq2);
			InputOperator ioper3 = new InputOperator(r3, iq3);

			// DFAOperator doper = new DFAOperator(q1, q2, a); // for DFA
			NFAOperator doper1 = new NFAOperator(iq1, aq1, a1); // for NFA
			NFAOperator doper2 = new NFAOperator(iq2, aq2, a2); // for NFA
			NFAOperator doper3 = new NFAOperator(iq3, aq3, a3); // for NFA
			JoinOperator joper = new JoinOperator(aq1, aq2, aq3, q2);
			OutputOperator ooper = new OutputOperator(q2, w);
			
			Thread t11 = new Thread(ioper1);
			Thread t12 = new Thread(ioper2);
			Thread t13 = new Thread(ioper3);
			Thread t2 = new Thread(doper1);
			Thread t3 = new Thread(doper2);
			Thread t4 = new Thread(doper3);
			Thread t5 = new Thread(joper);
			Thread t6 = new Thread(ooper);

			long start = System.currentTimeMillis(); 

			t11.start();
			t12.start();
			t13.start();

			t2.start();
			t3.start();
			t4.start();
			t5.start();
			t6.start();

			t11.join();
			t12.join();
			t13.join();

			t2.join();
			t3.join();
			t4.join();
			t5.join();
			t6.join();
			
			long elapsedTimeMillis = System.currentTimeMillis()-start; 

			System.out.println ("time taken : " + elapsedTimeMillis);
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
