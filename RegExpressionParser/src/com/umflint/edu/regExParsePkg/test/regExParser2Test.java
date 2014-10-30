package com.umflint.edu.regExParsePkg.test;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.concurrent.ConcurrentLinkedQueue;

import com.umflint.edu.regExParsePkg.Automaton2;
import com.umflint.edu.regExParsePkg.DFAOperator;
import com.umflint.edu.regExParsePkg.DFAOperatorSpecial;
import com.umflint.edu.regExParsePkg.InputOperator;
import com.umflint.edu.regExParsePkg.NFAOperator;
import com.umflint.edu.regExParsePkg.NFAOperatorSpecial;
import com.umflint.edu.regExParsePkg.OutputOperator;
import com.umflint.edu.regExParsePkg.RegExpTree;
import com.umflint.edu.regExParsePkg.generated.SimpleNode;
import com.umflint.edu.regExParsePkg.generated.regExpGrammar2;

public class regExParser2Test {

	/**
	 * @param args
	 */
	
	public static void processDFAString(String s) throws Exception {
		for (int i = 0; i < 1; i++) {
			long start = System.currentTimeMillis(); 

			java.io.StringReader sr = new java.io.StringReader(s);
			java.io.Reader r1 = new java.io.BufferedReader( sr );
			regExpGrammar2 parser = new regExpGrammar2(r1);
			SimpleNode n = parser.Start();
			RegExpTree tree = new RegExpTree(n);
			// tree.displayTree();
			Automaton2 a = new Automaton2();
			a = a.constructAut(tree.rootNode);
			a.fixAlphabet();
			// a.display();
			a = a.determinize(a);
			a.display();

			long elapsedTimeMillis = System.currentTimeMillis()-start; 
			System.out.println ("time taken constructing DFA  for " + s + " : " + elapsedTimeMillis);

			ConcurrentLinkedQueue<String> q1 = new ConcurrentLinkedQueue<String>();
			ConcurrentLinkedQueue<String> q2 = new ConcurrentLinkedQueue<String>();
			BufferedReader r = new BufferedReader(new FileReader("input.txt"));
			BufferedWriter w = new BufferedWriter(new FileWriter("output.txt"));
			// BufferedWriter w = null;
			int window = 5;

			InputOperator ioper = new InputOperator(r, q1);
			DFAOperator doper = new DFAOperator(q1, q2, a, window); // for DFA
			// NFAOperator doper = new NFAOperator(q1, q2, a2, window); // for NFA
			OutputOperator ooper = new OutputOperator(q2, w);

			Thread t1 = new Thread(ioper);
			Thread t2 = new Thread(doper);
			Thread t3 = new Thread(ooper);

			start = System.currentTimeMillis(); 

			t1.start();
			t2.start();
			t3.start();
			t1.join();
			t2.join();
			t3.join();

			elapsedTimeMillis = System.currentTimeMillis()-start; 

			System.out.println ("time taken using DFA  for " + s + " : " + elapsedTimeMillis);
		}
	}

	public static void processDFAStringSpecial(String s) throws Exception {
		for (int i = 0; i < 1; i++) {
			long start = System.currentTimeMillis(); 

			java.io.StringReader sr = new java.io.StringReader(s);
			java.io.Reader r1 = new java.io.BufferedReader( sr );
			regExpGrammar2 parser = new regExpGrammar2(r1);
			SimpleNode n = parser.Start();
			RegExpTree tree = new RegExpTree(n);
			// tree.displayTree();
			Automaton2 a = new Automaton2();
			a = a.constructAut(tree.rootNode);
			a.fixAlphabet();
			// a.display();
			a = a.determinize(a);
			// a.display();

			long elapsedTimeMillis = System.currentTimeMillis()-start; 
			System.out.println ("time taken constructing DFA  for " + s + " : " + elapsedTimeMillis);

			ConcurrentLinkedQueue<String> q1 = new ConcurrentLinkedQueue<String>();
			ConcurrentLinkedQueue<String> q2 = new ConcurrentLinkedQueue<String>();
			BufferedReader r = new BufferedReader(new FileReader("input.txt"));
			BufferedWriter w = new BufferedWriter(new FileWriter("output.txt"));
			int window = 5;

			InputOperator ioper = new InputOperator(r, q1);
			DFAOperatorSpecial doper = new DFAOperatorSpecial(q1, q2, a, window); // for DFA
			// NFAOperator doper = new NFAOperator(q1, q2, a2, window); // for NFA
			OutputOperator ooper = new OutputOperator(q2, w);

			Thread t1 = new Thread(ioper);
			Thread t2 = new Thread(doper);
			Thread t3 = new Thread(ooper);

			start = System.currentTimeMillis(); 

			t1.start();
			t2.start();
			t3.start();
			t1.join();
			t2.join();
			t3.join();

			elapsedTimeMillis = System.currentTimeMillis()-start; 

			System.out.println ("time taken using DFA  for " + s + " : " + elapsedTimeMillis);
		}
	}
	
	public static void processNFAString(String s) throws Exception {
		for (int i = 0; i < 1; i++) {
			long start = System.currentTimeMillis(); 

			java.io.StringReader sr = new java.io.StringReader(s);
			java.io.Reader r1 = new java.io.BufferedReader( sr );
			regExpGrammar2 parser = new regExpGrammar2(r1);
			SimpleNode n = parser.Start();
			n.dump("test");
			RegExpTree tree = new RegExpTree(n);
			tree.displayTree();
			Automaton2 a = new Automaton2();
			a = a.constructAut(tree.rootNode);
			a.fixAlphabet();
			// a.display();
			// Automaton2 a3 = a2.determinize(a2);
			// a3.display();

			long elapsedTimeMillis = System.currentTimeMillis()-start; 
			System.out.println ("time taken constructing NFA  for " + s + " : " + elapsedTimeMillis);
			
			ConcurrentLinkedQueue<String> q1 = new ConcurrentLinkedQueue<String>();
			ConcurrentLinkedQueue<String> q2 = new ConcurrentLinkedQueue<String>();
			BufferedReader r = new BufferedReader(new FileReader("input.txt"));
			BufferedWriter w = new BufferedWriter(new FileWriter("output.txt"));
			// BufferedWriter w = null;
			int window = 5;

			InputOperator ioper = new InputOperator(r, q1);
			// DFAOperator doper = new DFAOperator(q1, q2, a3, window); // for DFA
			NFAOperator doper = new NFAOperator(q1, q2, a, window); // for NFA
			OutputOperator ooper = new OutputOperator(q2, w);
		
			Thread t1 = new Thread(ioper);
			Thread t2 = new Thread(doper);
			Thread t3 = new Thread(ooper);

			start = System.currentTimeMillis(); 

			t1.start();
			t2.start();
			t3.start();
			t1.join();
			t2.join();
			t3.join();

			elapsedTimeMillis = System.currentTimeMillis()-start; 

			System.out.println ("time taken using NFA  for " + s + " : " + elapsedTimeMillis);
		}
	}

	public static void processNFAStringSpecial(String s) throws Exception {
		for (int i = 0; i < 1; i++) {
			long start = System.currentTimeMillis(); 

			java.io.StringReader sr = new java.io.StringReader(s);
			java.io.Reader r1 = new java.io.BufferedReader( sr );
			regExpGrammar2 parser = new regExpGrammar2(r1);
			SimpleNode n = parser.Start();
			RegExpTree tree = new RegExpTree(n);
			// tree.displayTree();
			Automaton2 a = new Automaton2();
			a = a.constructAut(tree.rootNode);
			a.fixAlphabet();
			// a.display();
			// Automaton2 a3 = a2.determinize(a2);
			// a3.display();

			long elapsedTimeMillis = System.currentTimeMillis()-start; 
			System.out.println ("time taken constructing NFA  for " + s + " : " + elapsedTimeMillis);
			
			ConcurrentLinkedQueue<String> q1 = new ConcurrentLinkedQueue<String>();
			ConcurrentLinkedQueue<String> q2 = new ConcurrentLinkedQueue<String>();
			BufferedReader r = new BufferedReader(new FileReader("input.txt"));
			BufferedWriter w = new BufferedWriter(new FileWriter("output.txt"));
			int window = 5;

			InputOperator ioper = new InputOperator(r, q1);
			// DFAOperator doper = new DFAOperator(q1, q2, a3, window); // for DFA
			NFAOperatorSpecial doper = new NFAOperatorSpecial(q1, q2, a, window); // for NFA
			OutputOperator ooper = new OutputOperator(q2, w);
		
			Thread t1 = new Thread(ioper);
			Thread t2 = new Thread(doper);
			Thread t3 = new Thread(ooper);

			start = System.currentTimeMillis(); 

			t1.start();
			t2.start();
			t3.start();
			t1.join();
			t2.join();
			t3.join();

			elapsedTimeMillis = System.currentTimeMillis()-start; 

			System.out.println ("time taken using NFA  for " + s + " : " + elapsedTimeMillis);
		}
	}
	
	public static void main(String[] args) throws Exception {
		
		// concatenation
		/*processDFAString("a");
		processNFAString("a");
		
		processDFAString("a,b");
		processNFAString("a,b");

		processDFAString("a,b,c");
		processNFAString("a,b,c");

		processDFAString("a,b,c,d");
		processNFAString("a,b,c,d");

		processDFAString("a,b,c,d,e");
		processNFAString("a,b,c,d,e"); */

		// choice -- note that the first pattern here is the same as for concatentation
		/* processDFAString("a");
		processNFAString("a");
		
		processDFAString("a|b");
		processNFAString("a|b");

		processDFAString("a|b|c");
		processNFAString("a|b|c");

		processDFAString("a|b|c|d");
		processNFAString("a|b|c|d");

		processDFAString("a|b|c|d|e");
		processNFAString("a|b|c|d|e"); */

		// Kleene-* -- not sure whether our parser handles (a,b)+ -- 
		// how big a difference will this be for larger patterns given our window of 6 events..??
		// I expect the results will not be that different between the 5 symbol pattern with + and the 5 symbol pattern with only concat, 
		// but processing costs might be very different?? given the extra buffers, and the copying..??
		/* processDFAString("a+");
		processNFAString("a+");
		
		processDFAString("a+,b+");
		processNFAString("a+,b+");

		processDFAString("a+,b+,c+");
		processNFAString("a+,b+,c+");

		processDFAString("a+,b+,c+,d+");
		processNFAString("a+,b+,c+,d+");

		processDFAString("a+,b+,c+,d+,e+");
		processNFAString("a+,b+,c+,d+,e+"); */
		
		//processNFAString("!a,!c,b");
		processNFAString("!a,(b,c,d)");

		// negation -- we can only do one pattern !! probably different sizes??
		/* processDFAStringSpecial("a,(!c,b)");
		processNFAStringSpecial("a,(!c,b)"); */
		
	}

	public String recursivePrintValue (SimpleNode n, int c) {
		// System.out.print("c = " + c + ":");
		String s = "";
		for (int i = 0; i < c; i++) {
			System.out.print (" ");
		}
		String val = n.jjtGetValue().toString();
		System.out.println (val);
		if (n.jjtGetNumChildren() == 0) return val; // it must be a constant
		for (int i = 0; i < n.jjtGetNumChildren(); i++) {
			s = s + recursivePrintValue((SimpleNode) n.jjtGetChild(i), c + 1);
			if (n.jjtGetNumChildren() > (i + 1)) { // more children are there
				if (val.equals("CONCAT")) s = s + ",";
				else if (val.equals("CHOICE")) s = s + "|";
				else if (val.equals("ALIAS")) s = s + ":";
				else if (val.equals("NOT")) s = s + ",";
			} else {
				if (val.equals("PLUS")) return s + "+";
				else if (val.equals("UNARY")) return s;
				else if (val.equals("NOT")) return "!" + s;
			}
		}
		if (val.equals("ALIAS")) return s;
		else if (val.equals ("START")) return s;
		else return ("(" + s + ")");
	}

}
