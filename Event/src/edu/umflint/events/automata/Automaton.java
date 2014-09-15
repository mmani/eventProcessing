/**
 * 
 */
package edu.umflint.events.automata;

/**
 * @author mmani
 *
 */

// This class to deal with automaton construction, as well as automaton execution. Make it multi-thread safe during execution.
// It must support deterministic and non-deterministic automaton.

import java.util.*;

// This class specifies a state.
class State {
	public int num;	// the state number
}

interface TreeNodeType {
	static final int SEQ = 1;
	static final int UNION = 2;
	static final int STAR = 3;
	static final int NOT = 4;
	static final int SYMBOL = 5;
	static final int INTERNAL = 6;
	static final int AND = 7;
	static final int UNDEFINED = 8;
}

class TreeNode {
	public int nodeType; // one of the types.
	public String nodeVal; // only if type is SYMBOL
	public LinkedHashSet<TreeNode> children; // set of children
	
	public boolean done; // to be used in automaton construction
	// SEQ can have 1 or more children; UNION can have 1 or more children; STAR has 1 child; NOT has 1 child; INTERNAL has 1 child; SYMBOL has no child
	
	public TreeNode() {
		nodeType = TreeNodeType.UNDEFINED;
		nodeVal = null;
		children = new LinkedHashSet<TreeNode>();
	}
	
	public void displayTreeNode(TreeNode n) {
		if (n.children == null) {
			System.out.print (" " + n.nodeVal + " ");
			return;
		}
		TreeNode [] childArr = new TreeNode [n.children.size()];
		Iterator <TreeNode> it = n.children.iterator();
		for (int i = 0; i < childArr.length; i++) {
			childArr[i] = it.next();
		}
		for (int i = 0; i < childArr.length; i++) {
			displayTreeNode(childArr[i]);
			System.out.print (" " + n.nodeType + " ");
		}
	}
}

class RegExParseOp {
	public int operatorType; // one of the types
	public LinkedHashSet<String> operands;
	
	public RegExParseOp() {
		operatorType = TreeNodeType.UNDEFINED;
		operands = new LinkedHashSet<String>();
	}
}

class Tree {
	public TreeNode root;
	
	public Tree() {
		root = new TreeNode();
	}
}

class RegExp {
	public String exp;
	public Tree expTree;
	
	public RegExp() {
		exp = null;
		expTree = new Tree();
	}
	
	public RegExp(String s) {
		exp = s;
		expTree = new Tree();
	}
	
	public void setRegExp (String e){
		exp = e;
	}
	
	public String getRegExp() {
		return exp;
	}
	
	public void buildParseTree() {
		expTree = buildParseTreeString(exp);
	}
	
	public Tree buildParseTreeString(String s) {
		Tree t = new Tree();
		RegExParseOp outer = determineOuterMostOp(s);
		t.root.nodeType = outer.operatorType;
		
		String [] childArray = new String[outer.operands.size()];
		// System.out.println (childArray.length);

		Iterator<String> it = outer.operands.iterator();
		for (int i = 0; i < childArray.length; i++) {
			childArray[i] = (String) it.next();
		}
		
		
		switch (outer.operatorType) {
		case TreeNodeType.SYMBOL:
			t.root.nodeVal = childArray[0];
			t.root.children = null;
			break;
		case TreeNodeType.STAR:
			t.root.nodeVal = null;
			Tree t1 = buildParseTreeString(childArray[0]);
			t.root.children.add(t1.root);
			break;
		case TreeNodeType.NOT:
			t.root.nodeVal = null;
			t1 = buildParseTreeString(childArray[0]);
			t.root.children.add(t1.root);
			break;
		case TreeNodeType.INTERNAL:
			t.root.nodeVal = null;
			t1 = buildParseTreeString(childArray[0]);
			t.root.children.add(t1.root);
			break;
		case TreeNodeType.SEQ:
			t.root.nodeVal = null;
			for (int i = 0; i < childArray.length; i++) {
				t1 = buildParseTreeString(childArray[i]);
				t.root.children.add(t1.root);
			}
			break;
		case TreeNodeType.AND:
			t.root.nodeVal = null;
			t1 = buildParseTreeString(childArray[0]);
			t.root.children.add (t1.root);
			t1 = buildParseTreeString(childArray[1]);
			t.root.children.add (t1.root);
			break;
		case TreeNodeType.UNION:
			t.root.nodeVal = null;
			for (int i = 0; i < childArray.length; i++) {
				t1 = buildParseTreeString(childArray[i]);
				t.root.children.add(t1.root);
			}
			break;
		default:
			System.err.println ("Should not come here");

		}
		
		// set root's value and children.
		return t;
	}

	protected void findAllOperands(String s, char c, LinkedHashSet<String> hs) {
		int count = 0;
		int startIndex = 0;
		int endIndex = 0;
		for (int i = 0; i < s.length(); i++) {
			if (s.charAt(i) == ' ') continue;
			if (s.charAt(i) == '(') { 
				if (count == 0) startIndex = i; 
				++count; 
				for (i++; i < s.length(); i++) {			
					if (s.charAt(i) == '(') { 
						count++;
					}
					else if (s.charAt(i) == ')') { 
						--count; 
						if (count == 0) {
							endIndex = i; 
							hs.add(s.substring(startIndex, endIndex + 1));
							int pos = s.indexOf(c, endIndex + 1);
							if (pos == -1) return;
							findAllOperands(s.substring(pos + 1), c, hs); 
							return; 
						}
					}
				}
			}
			else { // we find a symbol
				int pos = s.indexOf(c);
				if (pos == -1) {
					hs.add (s.trim());
					return;
				}
				else {
					hs.add ((s.substring(0, pos)).trim());
					findAllOperands(s.substring(pos + 1), c, hs);
				}
			}
		}
	}
	
	protected RegExParseOp determineOuterMostOp(String s) {
		
		RegExParseOp op = new RegExParseOp();
		for (int i = 0; i < s.length(); i++) {
			if (s.charAt(i) == ' ') continue;
			if (s.charAt(i) == '(') { // find matching ')'. then skip till we see one of {, + * EOL} If we see *, determine whether we have {, +}
				int count = 1; 
				int startIndex = i;
				int endIndex = 0;
				for (i++; i < s.length(); i++) {
					if (s.charAt(i) == ' ') continue;
					if (s.charAt(i) == '(') { count++; continue; }
					if (s.charAt(i) == ')') { 
						--count; 
						if (count == 0) {
							endIndex = i;
							// count = 0. i.e., we have found the matching ')' and the next token
							for (i++; i < s.length(); i++) {
								if (s.charAt(i) == ',') { 
									op.operatorType = TreeNodeType.SEQ;
									op.operands.add(s.substring(startIndex, endIndex + 1));
									findAllOperands(s.substring(i+1), ',', op.operands);
									return op;
								};
								if (s.charAt(i) == '&') {
									op.operatorType = TreeNodeType.AND;
									op.operands.add(s.substring(startIndex, endIndex + 1));
									op.operands.add(s.substring(i + 1).trim()); // assume only 2 operands for &
									return op;
								}
								if (s.charAt(i) == '+') { 
									op.operatorType = TreeNodeType.UNION;
									op.operands.add(s.substring(startIndex, endIndex + 1));
									findAllOperands(s.substring(i+1), '+', op.operands);
									return op;
								};
								if (s.charAt(i) == '*') {
									// we do not allow (a, b)*, c -- it should be written as ((a, b)*), c
									op.operatorType = TreeNodeType.STAR;
									op.operands.add(s.substring(startIndex, endIndex + 1));
									return op;
								}
							}
							// string ends -- so the exp is of the form "(...) "
							op.operatorType = TreeNodeType.INTERNAL;
							op.operands.add((s.substring(startIndex + 1, endIndex)).trim());
							return op;
						}
					}
				}
				System.err.println("Should never come here");
			}
			else if (s.charAt(i) == '!') {
				// so the root must be NOT node. So we do not allow (!b, c). it must be (!b), c
				op.operatorType = TreeNodeType.NOT;
				op.operands.add((s.substring(i + 1)).trim());
				return op;
			}
			else { // we see a symbol
				// skip till we see one of {, + * EOL} Again, we do not allow a*, b -- this must be written as (a*), b
				int startIndex = i;
				for (i++; i < s.length(); i++) {
					if (s.charAt(i) == ' ') continue;
					if (s.charAt(i) == ',') { // root operator is ,
						op.operatorType = TreeNodeType.SEQ;
						op.operands.add((s.substring(startIndex, i)).trim());
						findAllOperands(s.substring(i+1), ',', op.operands);
						return op;
					}
					if (s.charAt(i) == '&') {
						op.operatorType = TreeNodeType.AND;
						op.operands.add(s.substring(startIndex, i).trim());
						op.operands.add(s.substring(i+1).trim()); // assume only 2 operands for &
						return op;
					}
					if (s.charAt(i) == '+') { // root operator is +
						op.operatorType = TreeNodeType.UNION;
						op.operands.add((s.substring(startIndex, i)).trim());
						findAllOperands(s.substring(i+1), '+', op.operands);
						return op;					
					}
					if (s.charAt(i) == '*') { // root operator is *
						// we do not allow (a, b)*, c -- it should be written as ((a, b)*), c
						op.operatorType = TreeNodeType.STAR;
						op.operands.add((s.substring(startIndex, i)).trim());
						return op;
					}
				}
				// it was only a symbol
				op.operatorType = TreeNodeType.SYMBOL;
				op.operands.add(s.trim());
				return op;
			}
		}
		return null;
	}
	
	public String neg(String s) {
		if (s.charAt(0) == '!') return s.substring(1);
		else return "!" + s;
	}
	
	public Automaton constructAut(TreeNode t) {
		Automaton aut = new Automaton();
		switch (t.nodeType) {
		case TreeNodeType.SYMBOL: {
			State s1 = new State();
			s1.num = 1;
			State s2 = new State();
			s2.num = 2;
			aut.addState(s1);
			aut.addState(s2);
			aut.addTransition(s1, t.nodeVal, s2);
			aut.addTransition(s2, neg(t.nodeVal), s1);
			aut.addTransition(s2, t.nodeVal, s2);
			aut.addTransition(s1, neg(t.nodeVal), s1);
			aut.startSt = s1;
			aut.finalSt.add(s2);
			aut.smallestNum = s1;
			aut.largestNum = s2;
			t.done = true;
			aut.deterministic = true;
			return aut;
		}
		case TreeNodeType.INTERNAL: {
			Iterator<TreeNode> it = (Iterator<TreeNode>) t.children.iterator();
			TreeNode t1 = it.next();
			t.done = true;
			return (constructAut(t1));
		}
		case TreeNodeType.NOT: {
			Iterator<TreeNode> it = (Iterator<TreeNode>) t.children.iterator();
			TreeNode t1 = it.next();
			Automaton aut1 = (constructAut(t1));
			// add loops to every final state, determinize the automaton, minimize, switch final and non-final states, minimize.
			if (aut1.deterministic == false) {
				aut1 = Automaton.removeEps(aut1);
			}
			Iterator<State> it1 = aut1.finalSt.iterator();
			while (it1.hasNext()) {
				State st = it1.next();
				HashSet<State> starStates = aut1.transitions.get(st).get("STAR");
				if (starStates == null) aut1.addTransition(st, "STAR", st);
				else {
					if (!starStates.contains(st)) starStates.add(st);
				}
			}
			aut1.deterministic = false;

			aut1 = Automaton.determinize(aut1);

			int [] newFinSt;
			int numNewFin = aut1.states.size() - aut1.finalSt.size();
			newFinSt = new int[numNewFin];
			int counter = -1;
			for (int i = 0; i < aut1.states.size(); i++) {
				State currSt = aut1.getState(i+1);
				if (!aut1.finalSt.contains(currSt)) newFinSt[++counter] = i + 1;
			}
			aut1.finalSt.clear();
			for (int i = 0; i < newFinSt.length; i++) {
				aut1.finalSt.add(aut1.getState(newFinSt[i]));
			}
			
			
			return aut1;
		}
		case TreeNodeType.STAR: {
			// add an epsilon transition from every final state to start state
			Iterator<TreeNode> it = (Iterator<TreeNode>) t.children.iterator();
			TreeNode t1 = it.next();
			Automaton aut1 = (constructAut(t1));
			Iterator<State> it1 = aut1.finalSt.iterator();
			State s1 = aut1.startSt;
			while (it1.hasNext()) {
				State s2 = it1.next();
				aut1.addTransition(s2, "EPSILON", s1);
			}
			aut1.deterministic = false;
			return aut1;
			// remove EPSILSON TRANSITIONS??
		}
		case TreeNodeType.UNION: {
			// add a new start state, add epsilon transitions from the new start state to start state of every child. remember to renumber states
			State s = new State();
			s.num = 1;
			aut.addState(s);
			int smallest = s.num;
			Iterator<TreeNode> it = t.children.iterator();
			while (it.hasNext()) {
				Automaton aut1 = constructAut(it.next());
				Automaton aut2 = Automaton.renumber(aut1, smallest + 1);
				smallest = aut2.largestNum.num;
				aut = Automaton.add(aut, aut2); // this should maintain states, transitions and final states, no start states, smallestNum, largestNum
				aut.addTransition(aut.getState(1), "EPSILON", aut.getState(aut2.startSt.num));
			}
			aut.startSt = s;
			aut.smallestNum = s;
			aut.largestNum = aut.getState(smallest);
			aut.deterministic = false;
			return aut;
		}
		case TreeNodeType.SEQ: {
			// add an epsilon transition from every final state to start state of the next.. remember to renumber
			Iterator<TreeNode> it = t.children.iterator();
			Automaton prev = constructAut(it.next());
			int smallest = prev.largestNum.num;
			while (it.hasNext()) {
				Automaton aut1 = constructAut(it.next());
				Automaton aut2 = Automaton.renumber(aut1, smallest + 1);
				aut = Automaton.add(prev, aut2);
				smallest = aut2.largestNum.num;
				// remove prev final states from aut and add transition from every final state of prev to start state of aut2
				Iterator<State> it1 = prev.finalSt.iterator();
				while (it1.hasNext()) {
					State s = it1.next();
					State autSt = aut.getState(s.num);
					aut.addTransition(autSt, "EPSILON", aut.getState(aut2.startSt.num));
					aut.finalSt.remove(autSt);
				}
				aut.largestNum = aut.getState(aut2.largestNum.num);
				prev = aut;
				// System.out.println("aut");
			}
			aut.startSt = aut.getState(1); // start state will have number 1
			aut.smallestNum = aut.getState(1); // smallest state will have number 1
			aut.deterministic = false;
			return aut;
		}
		case TreeNodeType.AND: {
			Iterator<TreeNode> it = t.children.iterator();
			Automaton aut1 = constructAut(it.next()); // has 2 child nodes
			Automaton aut2 = constructAut(it.next());
			aut = Automaton.intersect(aut1, aut2);
			return aut;
		}
		}
		return null;
	}
}

public class Automaton {

	// This class captures for a particular state, the transitions. key is a symbol, value is the set of states to which you can go on seeing the symbol
	class AutoTrans extends HashMap<String, LinkedHashSet<State>> {
		
		static final long serialVersionUID = 1;
		
		public LinkedHashSet<State> getTransitions(String s) {
			return (LinkedHashSet<State>)super.get(s);
		}
		
		public void addTransition(String s, State t) {
			if (super.containsKey(s)) {
				LinkedHashSet<State> hs = (LinkedHashSet<State>) super.get(s);
				if (!hs.contains(t)) hs.add(t);
			}
			else {
				LinkedHashSet<State> hs = new LinkedHashSet<State>();
				hs.add(t);
				super.put(s, hs);
			}
		}
	};
	
	public LinkedHashSet<State> states; // set of states
	public HashMap<State, AutoTrans> transitions; // transitions. key is state, value is another HashMap of type AutoTrans
	public State startSt; // start state
	public LinkedHashSet<State> finalSt; // set of final states
	public LinkedHashSet<Integer> finalStInt; // set of final states as integer (used to speed up checking at execution).
	public State smallestNum; // used in automaton merging
	public State largestNum; // used in automaton merging
	public boolean deterministic;
	public LinkedHashSet<String> sortedAlph = new LinkedHashSet<String>();
	
	public Automaton () {
		states = new LinkedHashSet<State>();
		transitions = new HashMap<State,AutoTrans>();
		startSt = null;
		finalSt = new LinkedHashSet<State>();
		smallestNum = null;
		largestNum = null;
		deterministic = false;
	}
	
	public boolean isDeterministic() {
		return deterministic;
	}
	
	public void setDeterministic() {
		deterministic = true;
	}
	
	public void setNonDeterministic() {
		deterministic = false;
	}
	
	public void addState(State s) {
		states.add(s);
		AutoTrans hm = new AutoTrans();
		transitions.put(s, hm);
	}
	
	public void addTransition(State s1, String symbol, State s2) { 
		// state must already be in states, and a transition for that must already be added
		AutoTrans hm = (AutoTrans) transitions.get(s1);
		hm.addTransition(symbol, s2);
	}
	
	public State getState(int num) {
		Iterator<State> it = states.iterator();
		while (it.hasNext()) {
			State s = it.next();
			if (s.num == num) return s;
		}
		return null;
	}
	
	// return the states (as a HashSet) to which we can transition from s on seeing sym
	public HashSet<State> getTransitionStates(State s, String sym) {
		HashSet<State> hs = new HashSet<State>();
		AutoTrans at = transitions.get(s);
		Iterator<State> it1 = at.get(sym).iterator();
		while (it1.hasNext()) {
			State s1 = it1.next();
			if (!hs.contains(s1)) hs.add(s1);
		}
		return hs;
	}

	public void getAlphabet() { // fills up the member variable sortedAlph
		// look up transitions. if the symbols already have &, then tokenize.. Do not put the ! symbols, STAR, EPSILON into the alphabet..
		Iterator<State> it1 = transitions.keySet().iterator();
		while (it1.hasNext()) {
			AutoTrans at = transitions.get(it1.next());
			Iterator<String> it2 = at.keySet().iterator();
			while (it2.hasNext()) {
				String sym = it2.next();
				if (sym.equals("EPSILON")) continue;
				if (sym.equals("STAR")) continue;
				// the symbol could have &
				StringTokenizer tokenizer = new StringTokenizer (sym, " &");
				while (tokenizer.hasMoreTokens()) {
					String sym1 = tokenizer.nextToken();
					if (sym1.charAt(0) == '!') sym1 = sym1.substring(1);
					if (!sortedAlph.contains(sym1)) sortedAlph.add(sym1);
				}
			}
		}
	}
	
	public static Automaton renumber(Automaton a, int num) { // fix states, transitions, start state, final states, smallest num, largest num, deterministic
		Automaton aut = new Automaton();
		int diff = num - a.smallestNum.num;
		Iterator<State> it = a.states.iterator();
		while (it.hasNext()) {
			State s = new State();
			s.num = it.next().num + diff;
			aut.addState(s);
		}
		it = a.transitions.keySet().iterator();
		while (it.hasNext()) {
			State s = it.next();
			AutoTrans at = a.transitions.get(s);
			State newSt = aut.getState(s.num + diff);
			Iterator<String> itsym = at.keySet().iterator();
			while (itsym.hasNext()) {
				String s1 = itsym.next();
				Iterator<State> itst = at.get(s1).iterator();
				while (itst.hasNext()) {
					aut.addTransition(newSt, s1, aut.getState(itst.next().num + diff));
				}
			}
		}
		it = a.finalSt.iterator();
		while (it.hasNext()) {
			aut.finalSt.add(aut.getState(it.next().num + diff));
		}
		aut.startSt = aut.getState(a.startSt.num + diff);
		aut.smallestNum = aut.getState(a.smallestNum.num + diff);
		aut.largestNum = aut.getState(a.largestNum.num + diff);
		aut.deterministic = a.deterministic;
		
		return aut;
	}
	
	public static Automaton add(Automaton a1, Automaton a2) {
		Automaton aut = new Automaton();
		// add a1 and a2 states to aut
		Iterator<State> it = a1.states.iterator();
		while (it.hasNext()) {
			State s = new State();
			s.num = it.next().num;
			aut.addState(s);
		}
		it = a2.states.iterator();
		while (it.hasNext()) {
			State s = new State();
			s.num = it.next().num;
			aut.addState(s);
		}
		// add a1 and a2 transitions to aut
		it = a1.transitions.keySet().iterator();
		while (it.hasNext()) {
			State s = it.next();
			AutoTrans at = a1.transitions.get(s);
			State newSt = aut.getState(s.num);
			Iterator<String> itsym = at.keySet().iterator();
			while (itsym.hasNext()) {
				String s1 = itsym.next();
				Iterator<State> itst = at.get(s1).iterator();
				while (itst.hasNext()) {
					aut.addTransition(newSt, s1, aut.getState(itst.next().num));
				}
			}
		}
		it = a2.transitions.keySet().iterator();
		while (it.hasNext()) {
			State s = it.next();
			AutoTrans at = a2.transitions.get(s);
			State newSt = aut.getState(s.num);
			Iterator<String> itsym = at.keySet().iterator();
			while (itsym.hasNext()) {
				String s1 = itsym.next();
				Iterator<State> itst = at.get(s1).iterator();
				while (itst.hasNext()) {
					aut.addTransition(newSt, s1, aut.getState(itst.next().num));
				}
			}
		}
		// add a1 and a2 final states
		it = a1.finalSt.iterator();
		while (it.hasNext()) {
			aut.finalSt.add(aut.getState(it.next().num));
		}
		it = a2.finalSt.iterator();
		while (it.hasNext()) {
			aut.finalSt.add(aut.getState(it.next().num));
		}
		
		return aut;
	}
	
	public static Automaton removeEps(Automaton a) {
		if (a.deterministic) return a;
		HashMap<State, HashSet<State>> epsClos = new HashMap<State, HashSet<State>>();
		Iterator<State> it = a.transitions.keySet().iterator();
		boolean modified = false;
		while (it.hasNext()) { // first look at transitions and add..
			State s = it.next();
			HashSet<State> hs = new HashSet<State>();	// every state has at least itself in its epsilon closure
			hs.add(s);
			epsClos.put(s, hs);
			AutoTrans at = a.transitions.get(s);
			Set<String> stSet = at.keySet();
			if (!stSet.contains("EPSILON")) continue;
			Iterator<State> it1 = at.get("EPSILON").iterator();
			while (it1.hasNext()) {
				State toAdd = it1.next();
				if (hs.contains(toAdd)) continue;
				hs.add(toAdd); // else add state and set modified
				modified = true;
			}
		}
		while (modified) { // use only epsClos data structure
			modified = false;
			it = epsClos.keySet().iterator();
			while (it.hasNext()) {
				State s = it.next();
				HashSet<State> hs = epsClos.get(s);
				Iterator<State> it1 = hs.iterator();
				while (it1.hasNext()) {
					State s1 = it1.next();
					Iterator<State> it2 = epsClos.get(s1).iterator();
					while(it2.hasNext()) {
						State s2 = it2.next();
						if (hs.contains(s2)) continue;
						hs.add(s2);
						modified = true;
					}
				}
			}
		}
		// now we have the complete epsilon closure
		/* System.out.println("EPSILON CLOSURE\n------------------");
		it = epsClos.keySet().iterator();
		while (it.hasNext()) {
			State s = it.next();
			System.out.print(s.num + " : ");
			HashSet<State> hs = epsClos.get(s);
			Iterator<State> it1 = hs.iterator();
			while (it1.hasNext()) {
				System.out.print(it1.next().num + " , ");
			}
			System.out.print("\n");
		} */
		
		// for each state, check its epsilon closure.
		//    for each state in the epsilon closure, add a transition symbol if not present and is not epsilon
		//       get the transitions for that symbol from that state and add the eps closure of each of those states..
		
		it = a.transitions.keySet().iterator();
		while (it.hasNext()) { // first look at transitions and add..
			State s = it.next();
			AutoTrans at = a.transitions.get(s);
			if (at.keySet().contains("EPSILON")) at.remove("EPSILON"); // remove epsilon from transitions for s
			// loop over epsilon closure of s
			Iterator<State> it1 = epsClos.get(s).iterator();
			while (it1.hasNext()) {
				State s1 = it1.next(); // s1 is in epsClos of s
				AutoTrans at1 = a.transitions.get(s1); // transitions of s1
				Iterator<String> it2 = at1.keySet().iterator();
				while (it2.hasNext()) {
					String s2 = it2.next();
					if (s2.equals("EPSILON")) continue; // skip EPSILON transitions
					Iterator<State> it3 = at1.get(s2).iterator(); // transitions of s1 for string s2
					while (it3.hasNext()) {
						State s3 = it3.next();
						Iterator<State> it4 = epsClos.get(s3).iterator(); // loop over epsClos of s3 and add those states
						while (it4.hasNext()) {
							State s4 = it4.next();
							HashSet<State> hs = at.get(s2);
							if (hs == null) {
								at.addTransition(s2, s4);
							}
							else if (!hs.contains(s4)) hs.add(s4);
						}
					}
				}
			}
		}
		
		// fix final states...
		it = a.states.iterator();
		while (it.hasNext()) {
			State s = it.next();
			if (a.finalSt.contains(s)) continue;	// already a final state
			Iterator<State> it1 = epsClos.get(s).iterator();
			while (it1.hasNext()) {
				State s1 = it1.next();
				if (a.finalSt.contains(s1)) {
					// make s a final state
					if (!a.finalSt.contains(s)) // really no need to check.. s should not be there..
						a.finalSt.add(s);
					break;	// go to the next state..
				}
			}
		}
		return a;
	}
	
	public static Automaton intersect (Automaton a1, Automaton a2) { // assume that a1, a2 are numbered correctly.. i.e. start state = 1, etc...
		
		a1.getAlphabet();
		a2.getAlphabet();
		
		LinkedHashSet<String> extendedAlph = Automaton.mergeAlphabet(a1.sortedAlph, a2.sortedAlph);
		
		Automaton aut1 = Automaton.extendAlph(a1, extendedAlph);
		Automaton aut2 = Automaton.extendAlph(a2, extendedAlph);
		return Automaton.intersect1(aut1, aut2);
	}
	
	public static LinkedHashSet<String> mergeAlphabet(LinkedHashSet<String> set1, LinkedHashSet<String> set2) {
		LinkedHashSet<String> retSet = new LinkedHashSet<String>();
		Iterator<String> it1 = set1.iterator();
		while (it1.hasNext()) {
			String s = it1.next();
			if (!retSet.contains(s)) retSet.add(s);
		}
		it1 = set2.iterator();
		while (it1.hasNext()) {
			String s = it1.next();
			if (!retSet.contains(s)) retSet.add(s);
		}
		return retSet;
	}
	
	public static Automaton extendAlph (Automaton a, LinkedHashSet<String> alph) {

		Automaton aut = new Automaton();
		aut.deterministic = a.deterministic;
		Iterator<State> it = a.states.iterator();
		while (it.hasNext()) {
			State s = new State();
			s.num = it.next().num;
			aut.addState(s);
		}
		// go through transitions in a
		it = a.transitions.keySet().iterator();
		while (it.hasNext()) {
			State currSt = it.next();
			AutoTrans at = a.transitions.get(currSt);
			Iterator<String> symSet = at.keySet().iterator();

			while (symSet.hasNext()) { // first get what is there and then extend..
				String sym = symSet.next();
				// if sym == STAR, then add dest states for all symbols..
				if (sym.equals ("STAR")) {
					aut.deterministic = false;
					int numMissTokens = alph.size();
					for (int i = 0; i < (int) Math.pow(2, numMissTokens); i++) {
						String curr = "";
						int temp = i;
						Iterator<String> extAlphIt = alph.iterator();
						{ // take care of the first symbol
							String extSym = extAlphIt.next();
							if ((temp & 1) == 1) curr = curr + "!" + extSym;
							else curr = curr + extSym;
							temp = temp >> 1;
						}
						
						while (extAlphIt.hasNext()) {
							String extSym = extAlphIt.next();
							if ((temp & 1) == 1) curr = curr + " & !" + extSym;
							else curr = curr + " & " + extSym;
							temp = temp >> 1;
						}
						// add dest states
						Iterator<State> destIt = at.get(sym).iterator();
						while (destIt.hasNext()) {
							State s2 = destIt.next();
							aut.addTransition(aut.getState(currSt.num), curr, aut.getState(s2.num));
						}
					}
					continue;
				}
				
				LinkedHashSet<String> symHashSet = new LinkedHashSet<String>();
				StringTokenizer tok1 = new StringTokenizer(sym, "& ");
				while (tok1.hasMoreTokens()) {
					symHashSet.add(tok1.nextToken());
				}
				// now extend..
				int numMissTokens = alph.size() - symHashSet.size();
				for (int i = 0; i < (int) Math.pow(2, numMissTokens); i++) {
					String curr = "";
					int temp = i;
					Iterator<String> extAlphIt = alph.iterator();
					{ // take care of the first symbol
						String extSym = extAlphIt.next();
						boolean found = false;
						Iterator<String> symIt = symHashSet.iterator();
						while (symIt.hasNext()) {
							String s = symIt.next();
							if (s.equals(extSym)) {
								curr = curr + s;
								found = true;
								break;
							}
							if (s.equals("!" + extSym)) {
								curr = curr + s;
								found = true;
								break;
							}
						}
						if (!found) {
							if ((temp & 1) == 1) curr = curr + "!" + extSym;
							else curr = curr + extSym;
							temp = temp >> 1;
						}
					}
					
					while (extAlphIt.hasNext()) {
						String extSym = extAlphIt.next();
						boolean found = false;
						Iterator<String> symIt = symHashSet.iterator();
						while (symIt.hasNext()) {
							String s = symIt.next();
							if (s.equals(extSym)) {
								curr = curr + " & " + s;
								found = true;
								break;
							}
							if (s.equals("!" + extSym)) {
								curr = curr + " & " + s;
								found = true;
								break;
							}
						}
						if (!found) {
							if ((temp & 1) == 1) curr = curr + " & !" + extSym;
							else curr = curr + " & " + extSym;
							temp = temp >> 1;
						}
					}
					// add dest states
					Iterator<State> destIt = at.get(sym).iterator();
					while (destIt.hasNext()) {
						State s2 = destIt.next();
						aut.addTransition(aut.getState(currSt.num), curr, aut.getState(s2.num));
					}
				}
			}
		}
		// set startSt, finalSt, smallestNum, largestNum
		aut.startSt = aut.getState(1);
		aut.smallestNum = aut.getState(1);
		aut.largestNum = aut.getState(a.largestNum.num);
		it = a.finalSt.iterator();
		while (it.hasNext()) {
			aut.finalSt.add(aut.getState(it.next().num));
		}
		return aut;
	}

	public static Automaton intersect1 (Automaton a1, Automaton a2) { // assume that a1, a2 are numbered correctly.. i.e. start state = 1, etc...
		Automaton aut = new Automaton();
		// state [x, y] becomes (x - 1) * n2 + y (n2 = number of states in a2
		Iterator<State> it1 = a1.states.iterator();
		int n1 = a1.states.size();
		int n2 = a2.states.size();
		while (it1.hasNext()) {
			int num1 = it1.next().num;
			Iterator<State> it2 = a2.states.iterator();
			while (it2.hasNext()) {
				State s1 = new State();
				s1.num = (num1 - 1) * n2 + it2.next().num;
				aut.addState(s1);
			}
		}
		// now get the transitions
		it1 = a1.states.iterator();
		while (it1.hasNext()) {
			State s1 = it1.next();
			AutoTrans at1 = a1.transitions.get(s1);
			Iterator<String> it2 = at1.keySet().iterator();
			while (it2.hasNext()) {
				String sym1 = it2.next();
				HashSet<State> hs1 = at1.get(sym1);
				Iterator<State> it3 = a2.states.iterator();
				while (it3.hasNext()) {
					State s2 = it3.next();
					HashSet<State> hs2 = a2.getTransitionStates(s2, sym1);
					Iterator<State> it4 = hs1.iterator();
					while (it4.hasNext()) {
						State d1 = it4.next();
						Iterator<State> it5 = hs2.iterator();
						while (it5.hasNext()) {
							State d2 = it5.next();
							// add transition in aut from [s1, s2] on sym1 to [d1, d2]
							State src = aut.getState((s1.num - 1) * n2 + s2.num);
							State dst = aut.getState((d1.num - 1) * n2 + d2.num);
							aut.addTransition(src, sym1, dst);
						}
					}
				}
			}
		}
		
		// now fix the start state, final states, smallest num, largest num and deterministic
		aut.startSt = aut.getState(1);
		aut.smallestNum = aut.getState(1);
		aut.largestNum = aut.getState((n1 - 1) * n2 + n2);
		if (a1.deterministic && a2.deterministic) aut.deterministic = true;
		else aut.deterministic = false;
		it1 = a1.finalSt.iterator();
		while (it1.hasNext()) {
			State s1 = it1.next();
			Iterator<State> it2 = a2.finalSt.iterator();
			while (it2.hasNext()) {
				State s2 = it2.next();
				// [s1, s2] is final state in aut
				aut.finalSt.add(aut.getState((s1.num - 1) * n2 + s2.num));
			}
		}
		return aut;
	}
	
	public static Automaton extendAllAlph (Automaton a) {
		Automaton aut = new Automaton();
		a.getAlphabet();

		Iterator<State> it = a.states.iterator();
		while (it.hasNext()) {
			State s = new State();
			s.num = it.next().num;
			aut.addState(s);
		}

		it = a.transitions.keySet().iterator();
		while (it.hasNext()) {
			State srcSt = it.next();
			State srcStNew = aut.getState(srcSt.num);
			Iterator<String> it1 = a.transitions.get(srcSt).keySet().iterator();
			while (it1.hasNext()) {
				String sym = it1.next();
				if (sym.equals("STAR")) {
					// WRITE THE CODE..
					int expLimit = (int) Math.pow(2, a.sortedAlph.size());
					for (int i = 0; i < expLimit; i++) {
						int temp = i;
						String curr = "";
						Iterator<String> it2 = a.sortedAlph.iterator();
						{
							String alph = it2.next();
							if ((temp & 1) == 1) curr = curr + "!" + alph;
							else curr = curr + alph;
							temp = temp >> 1;
						}
						while (it2.hasNext()) {
							String alph = it2.next();
							if ((temp & 1) == 1) curr = curr + " & !" + alph;
							else curr = curr + " & " + alph;
							temp = temp >> 1;
						}
						Iterator<State> destIt = a.transitions.get(srcSt).get(sym).iterator();
						while (destIt.hasNext()) {
							aut.addTransition(srcStNew, curr, aut.getState(destIt.next().num));
						}
					}
					continue;
				}
				// tokenize sym
				StringTokenizer tok = new StringTokenizer(sym, "& ");
				LinkedHashSet<String> symSet = new LinkedHashSet<String>();
				while (tok.hasMoreTokens()) {
					symSet.add(tok.nextToken());
				}
				int expLimit = (int) Math.pow(2, a.sortedAlph.size() - symSet.size());
				for (int i = 0; i < expLimit; i++) {
					int temp = i;
					String curr = "";
					Iterator<String> it2 = a.sortedAlph.iterator();
					{
						String alph = it2.next();
						if (symSet.contains(alph)) {
							curr = curr + alph;
						}
						else if (symSet.contains("!" + alph)) {
							curr = curr + "!" + alph;
						}
						else {
							if ((temp & 1) == 1) curr = curr + "!" + alph;
							else curr = curr + alph;
							temp = temp >> 1;
						}
					}
					while (it2.hasNext()) {
						String alph = it2.next();
						if (symSet.contains(alph)) {
							curr = curr + " & " + alph;
							continue;
						}
						if (symSet.contains("!" + alph)) {
							curr = curr + " & !" + alph;
							continue;
						}
						if ((temp & 1) == 1) curr = curr + " & !" + alph;
						else curr = curr + " & " + alph;
						temp = temp >> 1;
					}
					Iterator<State> destIt = a.transitions.get(srcSt).get(sym).iterator();
					while (destIt.hasNext()) {
						aut.addTransition(srcStNew, curr, aut.getState(destIt.next().num));
					}
				}
			}
		}
		
		aut.startSt = aut.getState(a.startSt.num);
		aut.smallestNum = aut.getState(a.smallestNum.num);
		aut.largestNum = aut.getState(a.largestNum.num);
		aut.deterministic = false;
		// fix final states..
		it = a.finalSt.iterator();
		while (it.hasNext()) {
			aut.finalSt.add(aut.getState(it.next().num));
		}
		return aut;
	}
	
	public static Automaton determinize (Automaton a) {
		if (a.deterministic == true) return a;
		Automaton aut = new Automaton();
		a.getAlphabet();
		// handle if transitions already have symbols such as a & b
		// determinize alphabet and transitions
		// build a matrix such that M[state, symbol] = set of states
		
		// get sorted alphabet into an array..
		String [] sortedAlphArr = new String[a.sortedAlph.size()];
		Iterator<String> it = a.sortedAlph.iterator();
		int x = -1;
		while (it.hasNext()) {
			sortedAlphArr[++x] = it.next();
		}
		
		int detAlphSize = (int) Math.pow(2, a.sortedAlph.size());
		int numStates = (int) Math.pow(2, a.states.size()) - 1;
		Object [] hmArr = new Object [a.states.size()];
		for (int i = 0; i < a.states.size(); i++) {
			HashMap<String, HashSet<Integer>> hm = new HashMap<String, HashSet<Integer>>();
			// if this state has a STAR transition, then add the states in the STAR transition to all symbols.
			HashSet<State> starStates = a.transitions.get(a.getState(i+1)).get("STAR");
			for (int j = 0; j < detAlphSize; j++) {
				HashSet<Integer> hs = new HashSet<Integer>();
				// get the symbol, which is basically the binary representation of j; and whichever is 1 the corresponding alph symbol is a !
				String sym = "";
				int temp = j;
				if ((temp & 1) == 1) sym = sym + "!" + sortedAlphArr[0];
				else sym = sym + sortedAlphArr[0];
				temp = temp >> 1;
				for (int k = 1; k < a.sortedAlph.size(); k++) {
					if ((temp & 1) == 1) sym = sym + " & !" + sortedAlphArr[k];
					else sym = sym + " & " + sortedAlphArr[k];
					temp = temp >> 1;
				}
				if (starStates != null) {
					Iterator<State> it1 = starStates.iterator();
					while (it1.hasNext()) {
						hs.add(new Integer(it1.next().num));
					}
				}
				hm.put(sym, hs);
			}
			hmArr[i] = hm;
		}
		
		// go through the transitions. For each transition, which symbol/(s) it matches.. then add 
		Iterator<State> itSt = a.transitions.keySet().iterator();
		while (itSt.hasNext()) {
			State st = itSt.next();
			AutoTrans at = a.transitions.get(st);
			Iterator<String> sset = at.keySet().iterator();
			while (sset.hasNext()) {
				String currSym = sset.next();

				if (currSym.equals("STAR")) continue;
				// get the state numbers into an integer array and sort them..
				Iterator<State> it1 = at.get(currSym).iterator();
				int [] temp;
				temp = new int [at.get(currSym).size()];
				int tempIndex = -1;
				while (it1.hasNext()) {
					temp[++tempIndex] = it1.next().num;
				}
				// sort the temp array
				Arrays.sort(temp);

				StringTokenizer stokens = new StringTokenizer(currSym, "& ");
				int numTokens = stokens.countTokens();
				String [] currSymArr = new String[numTokens];
				x = -1;
				while (stokens.hasMoreTokens()) {
					currSymArr[++x] = stokens.nextToken();
				}
				int numMissSyms = a.sortedAlph.size() - numTokens;
				int [] missingSyms = new int[numMissSyms];
				x = -1;
				for (int i = 0; i < a.sortedAlph.size(); i++) {
					String currToCheck = sortedAlphArr [i];
					boolean present = false;
					for (int j = 0; j < numTokens; j++) {
						String currTok = currSymArr[j];
						if (currTok.charAt(0) == '!') currTok = currTok.substring(1);
						if (currToCheck.equals(currTok)) {
							present = true; break;
						}
					}
					if (present) continue;
					missingSyms[++x] = i;
				}
				int expLimit = (int) Math.pow(2, numMissSyms);
				for (int i = 0; i < expLimit; i++) {
					int binrep = i;
					int currIndexMissing = 0;
					String curr = "";
					if (missingSyms.length >= 1 && missingSyms[currIndexMissing] == 0) { // curr sym is missing from the transition
						if ((binrep & 1) == 1) curr = curr + "!" + sortedAlphArr[0];
						else curr = curr + sortedAlphArr[0];
						binrep = binrep >> 1;
						currIndexMissing++;
					}
					else { // curr sym is present in the transition; find it in currSymArr
						for (int k = 0; k < currSymArr.length; k++) {
							String toCheck = currSymArr[k];
							if (toCheck.charAt(0) == '!') toCheck = toCheck.substring(1);
							if (sortedAlphArr[0].equals(toCheck)) { 
								curr = curr + currSymArr[k];
								break;
							}
						}
					}
					
					for (int j = 1; j < a.sortedAlph.size(); j++) {
						if (missingSyms.length >= (currIndexMissing + 1) && missingSyms[currIndexMissing] == j) { // curr sym is missing from the transition
							if ((binrep & 1) == 1) curr = curr + " & !" + sortedAlphArr[j];
							else curr = curr + " & " + sortedAlphArr[j];
							binrep = binrep >> 1;
							currIndexMissing++;
						}
						else { // curr sym is present in the transition; find it in currSymArr
							for (int k = 0; k < currSymArr.length; k++) {
								String toCheck = currSymArr[k];
								if (toCheck.charAt(0) == '!') toCheck = toCheck.substring(1);
								if (sortedAlphArr[j].equals(toCheck)) { 
									curr = curr + " & " + currSymArr[k];
									break;
								}
							}
						}
					}
			
					// add temp array calculated before to the hash set
					HashSet<Integer> hs1 = ((HashMap<String, HashSet<Integer>>) hmArr[st.num - 1]).get(curr);
					for (int j = 0; j < temp.length; ++j) {
						hs1.add(new Integer(temp[j]));
					}
				}
			}
		}
		
		
		
		// display the filled up table
		/* for (int i = 0; i < a.states.size(); i++) {
			System.out.println ("State : " + (i + 1));
			HashMap<String, HashSet<Integer>> hm = (HashMap<String, HashSet<Integer>>) hmArr[i];
			Iterator<String> sset = hm.keySet().iterator();
			while (sset.hasNext()) {
				String sym = sset.next();
				System.out.print(sym + " : ");
				Iterator<Integer> it1 = hm.get(sym).iterator();
				while (it1.hasNext()) {
					System.out.print(it1.next().intValue() + " , ");
				}
				System.out.print("\n");
			}
		} */
		
		for (int i = 0; i < numStates; i++) {
			State s = new State();
			s.num = (i + 1);
			aut.addState(s);
		}
		int [] currNFASet;
		currNFASet = new int[1];
		currNFASet[0] = 1;
		for (int i = 0; i < numStates; i++) {
			if (currNFASet == null) {
				System.out.println ("looping too much");
				break;
			}
			State currDFAState = aut.getState(i + 1); // current state in DFA
			// go through all the alphabet symbols
			for (int j = 0; j < detAlphSize; j++) {
				String currAlph = "";
				int temp = j;
				if ((temp & 1) == 1) currAlph = currAlph + "!" + sortedAlphArr[0];
				else currAlph = currAlph + sortedAlphArr[0];
				temp = temp >> 1;
				for (int k = 1; k < sortedAlphArr.length; k++) {
					if ((temp & 1) == 1) currAlph = currAlph + " & !" + sortedAlphArr[k];
					else currAlph = currAlph + " & " + sortedAlphArr[k];
					temp = temp >> 1;
				}
				// now we have the current alphabet symbol, get all the transitions for every state in currNFASet
				HashSet<Integer> stSet = new HashSet<Integer> ();
				for (int k = 0; k < currNFASet.length; k++) {
					HashMap<String, HashSet<Integer>> hm = (HashMap<String, HashSet<Integer>>) hmArr[currNFASet[k] - 1];
					Iterator<Integer> it1 = hm.get(currAlph).iterator();
					while (it1.hasNext()) {
						Integer curr = it1.next();
						if (!stSet.contains(curr)) stSet.add(curr);
					}
				}
				// add all the Integers in stSet to an integer array, and then sort them..
				int [] stSet1;
				stSet1 = new int[stSet.size()];
				Iterator<Integer> it1 = stSet.iterator();
				int tempC = -1;
				while (it1.hasNext()) {
					stSet1[++tempC] = it1.next().intValue();
				}
				Arrays.sort(stSet1);
				LinkedHashSet<Integer> stSet2 = new LinkedHashSet<Integer> ();
				for (int k = 0; k < stSet1.length; k++) {
					stSet2.add(new Integer(stSet1[k]));
				}
				int destState = a.determineDFAState(a.states.size(), stSet2);
				aut.addTransition(currDFAState, currAlph, aut.getState(destState));
				
				// if any state in currNFASet is a final state, then make currDFAState a final state..
				boolean isFinal = false;
				for (int k = 0; k < currNFASet.length; k++) {
					Iterator<State> finStIt = a.finalSt.iterator();
					while (finStIt.hasNext()) {
						State currFin = finStIt.next();
						if (currNFASet[k] == currFin.num) {
							isFinal = true; break;
						}
					}
					if (isFinal) break;
				}
				if (isFinal) aut.finalSt.add(aut.getState(i + 1));				
			}
			currNFASet = a.nextStateSet(a.states.size(), currNFASet);
		}
		// fix final states, startSt, smallestNum, largestNum
		aut.startSt = aut.getState(1);
		aut.smallestNum = aut.getState(1);
		aut.largestNum = aut.getState(numStates);
		
		aut.deterministic = true;
		return aut;
	}
	
	// n = total number of states in the NFA; numSet is sorted list of states for which we need the corresponding DFA state
	public int determineDFAState (int n, LinkedHashSet<Integer> stateSet) {
		int numEls = stateSet.size();
		int [] stateArr = new int[numEls];
		Iterator<Integer> it = stateSet.iterator();
		int x1 = -1;
		while (it.hasNext()) {
			stateArr[++x1] = it.next().intValue();
		}
		if (numEls == 1) return stateArr[0];
		if (numEls == n) return ((int) Math.pow(2, n) - 1);
		int retVal = 0;
		for (int i = 1; i < numEls; i++) {
			retVal += cval(n, i);
		}
		for (int i = 1; i < numEls; i++) {
			int cbase = numEls - i;
			int numTerms = 0;
			if (i == 1) numTerms = stateArr[0] - 1;
			else numTerms = stateArr[i-1] - stateArr[i-2] - 1;
			int startNum = 0;
			if (i == 1) startNum = n - 1;
			else startNum = n - stateArr[i-2] - 1;
			for (int j = 0; j < numTerms; j++) {
				retVal += cval(startNum - j, cbase);
			}
		}
		// add (last el - last but 1 el) to retval
		retVal += (stateArr[numEls - 1] - stateArr[numEls - 2]);
		return retVal;
	}

	public int [] nextStateSet (int n, int [] currStateSet) {
		int [] retArr;
		int currLen = currStateSet.length;
		if (currLen == n) return null; // no next defined..
		if (currStateSet[0] == (n - currLen + 1)) { // should we increase the length of state set??
			retArr = new int[currLen + 1];
			for (int i = 0; i <= currLen; i++) {
				retArr[i] = i + 1;
			}
			return retArr;
		}
		retArr = new int[currLen]; // no increase in length of state set
		int lastIncreasableIndex = -1;
		for (int i = 0; i < currLen; i++) {
			if (currStateSet[i] < (n - currLen + i + 1)) lastIncreasableIndex = i;
		}
		for (int i = 0; i < lastIncreasableIndex; i++) { // copy currStateSet into retArr
			retArr [i] = currStateSet[i];
		}
		retArr[lastIncreasableIndex] = currStateSet[lastIncreasableIndex] + 1;
		for (int i = lastIncreasableIndex + 1; i < currLen; i++) {
			retArr[i] = retArr[i-1] + 1;
		}
		return retArr;
	}
	
	private int cval (int n, int d) {
		int retVal = 1;
		if (d > n/2) return cval(n, (n - d));
		for (int i = 0; i < d; i++)
			retVal *= (n - i);
		for (int i = 2; i <= d; i++)
			retVal /= i;
		return retVal;
	}

	public void display() {
		System.out.println ("------------------------------------------");
		Iterator<State> it = states.iterator();
		while (it.hasNext()) {
			State s = (State) it.next();
			System.out.println ("State : " + s.num);
			AutoTrans at = (AutoTrans) transitions.get(s);
			Set<String> keySet = at.keySet();
			Iterator<String> it1 = keySet.iterator();
			while (it1.hasNext()) {
				String key = (String) it1.next();
				System.out.print(key + " : ");
				LinkedHashSet<State> hs = (LinkedHashSet<State>) at.get(key);
				Iterator<State> it2 = hs.iterator();
				while (it2.hasNext()) {
					State s1 = (State) it2.next();
					System.out.print (s1.num + " ");
				}
				System.out.print("\n");
			}
		}
		System.out.println ("Start State : " + startSt.num);
		it = finalSt.iterator();
		System.out.print ("final states : ");
		while (it.hasNext()) {
			State s = (State) it.next();
			System.out.print (s.num + " ");
		}
		System.out.println ("\ndeterministic : " + deterministic);
		System.out.println ("smallest number : " + smallestNum.num);
		System.out.println ("largest number : " + largestNum.num);
		System.out.println ("\n\n-------------------------------------------");
	}

	public static void main (String args[]) {

		RegExp r = new RegExp();
		// r.setRegExp ("!a"); // produces 3 states
		// r.setRegExp ("b & c"); // produces 4 states
		// r.setRegExp ("(!b) & c"); // produces 6 states
		// r.setRegExp("a, (b & c)"); // produces 63 states
		r.setRegExp("a, b"); // produces 255 states
		r.buildParseTree();
		r.expTree.root.displayTreeNode(r.expTree.root);
		
		Automaton a = r.constructAut(r.expTree.root);
		a.display();
		a = Automaton.removeEps(a);
		a.display();

		a = Automaton.determinize(a);
		a.display();
	}
}
