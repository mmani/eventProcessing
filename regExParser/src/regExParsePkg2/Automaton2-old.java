package regExParsePkg2;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeNode;

import java.util.*;

//This class specifies a state.
class State {
	public String num; // the state number
	public LinkedHashSet<String> tag=new LinkedHashSet<String>(); 
	// this is the list of buffers associated with a state
}

class Buffer {
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
}

class Transition extends HashMap<String, LinkedHashSet<State>> {
		
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
}

public class Automaton2 {
	
	public LinkedHashSet<State> states; // set of states
	public HashMap<State, Transition> transitions; // transitions. key is state, value is another HashMap of type AutoTrans
	public State startSt; // start state
	public LinkedHashSet<State> finalSt; // set of final states
	public LinkedHashSet<Integer> finalStInt; // set of final states as integer (used to speed up checking at execution).
	public State smallestStNum; // smallest state number used in automaton merging
	public State largestStNum; // largest state number used in automaton merging

	public LinkedHashSet<Buffer> buffers; // set of buffers
	public LinkedHashSet<Buffer> startBuf; // set of start buffers
	public Buffer finalBuf; // final buffer
	public Buffer smallestBufNum; // smallest buffer number used in automaton merging
	public Buffer largestBufNum; // largest buffer number used in automaton merging
	public HashMap<State, LinkedHashSet<Buffer>> stateBufferLink; // buffers to be modified when we reach a state
	
	public boolean deterministic = false; // is automaton deterministic?
	public LinkedHashSet<String> sortedAlph = new LinkedHashSet<String>(); // alphabet for the automaton
	
	public static int stateNum = 1; // a class variable for numbering states
	public static int bufferNum = 1; // a class variable for numbering buffers
	
	public Automaton2 () {
		states = new LinkedHashSet<State>();
		transitions = new HashMap<State,Transition>();
		startSt = null;
		finalSt = new LinkedHashSet<State>();
		smallestStNum = null;
		largestStNum = null;
		
		buffers = new LinkedHashSet<Buffer>();
		startBuf = new LinkedHashSet<Buffer>();
		finalBuf = null;
		smallestBufNum = null;
		largestBufNum = null;
		stateBufferLink = new HashMap<State,LinkedHashSet<Buffer>>();
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
		Transition hm = new Transition();
		transitions.put(s, hm);
		LinkedHashSet<Buffer> bufs = new LinkedHashSet<Buffer>();
		stateBufferLink.put(s, bufs);
	}

	public void addTransition(State s1, String symbol, State s2) { 
		// state must already be in states, and a transition for that must already be added
		Transition hm = (Transition) transitions.get(s1);
		hm.addTransition(symbol, s2);
	}
	
	public void addBuffer(Buffer b) {
		buffers.add(b);
	}
	
	public void addStateBufferLink (State s, Buffer b, String action) {
		LinkedHashSet<Buffer> bufs = stateBufferLink.get(s);
		if (!bufs.contains(b)) bufs.add(b);
		if (!b.actions.contains(action)) b.actions.add(action);
	}

	public void addStateBufferLink (State s, Buffer b) { // w/o action used in copying automaton
		LinkedHashSet<Buffer> bufs = stateBufferLink.get(s);
		if (!bufs.contains(b)) bufs.add(b);
	}

	public State getState(String num) {
		Iterator<State> it = states.iterator();
		while (it.hasNext()) {
			State s = it.next();
			if (s.num.equals (num)) return s;
		}
		return null;
	}

	public Buffer getBuffer(String num) {
		Iterator<Buffer> it = buffers.iterator();
		while (it.hasNext()) {
			Buffer b = it.next();
			if (b.num.equals (num)) return b;
		}
		return null;
	}

	// return the states (as a HashSet) to which we can transition from s on seeing sym
	public HashSet<State> getTransitionStates(State s, String sym) {
		// HashSet<State> hs = new HashSet<State>();
		Transition at = transitions.get(s);
		return (at.get(sym));
		/* Iterator<State> it1 = at.get(sym).iterator();
		while (it1.hasNext()) {
			State s1 = it1.next();
			if (!hs.contains(s1)) hs.add(s1);
		}
		return hs; */
	}
	
	public void getAlphabet() { // fills up the member variable sortedAlph
		// look up transitions. if the symbols already have &, then tokenize.. Do not put the ! symbols, STAR, EPSILON into the alphabet..
		Iterator<State> it1 = transitions.keySet().iterator();
		while (it1.hasNext()) {
			Transition at = transitions.get(it1.next());
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
	
	// can we give numbers in such a way that we will not have to renumber the states or the buffers..
	// I think we can.. so let me skip renumbering.. so not need for smallest and largest state and buffer numbers..
	// trick is to use some numbering scheme that uses some kind of global number, 
	// so state number and buffer number must be a string??

	public Automaton2 constructAut(DefaultMutableTreeNode n) {
		
		Automaton2 aut = new Automaton2(); // this is set to non-deterministic by default. unless we specify otherwise
		String s = n.getUserObject().toString();

		if (s.startsWith("CONCAT")) {
			Automaton2 aut1 = aut.constructAut(((DefaultMutableTreeNode) n.getChildAt(0)));
			Automaton2 aut2 = aut.constructAut(((DefaultMutableTreeNode) n.getChildAt(1)));
			aut.copy(aut1);
			aut.copy(aut2);
			
			// add a new buffer -- this will be the new final buffer
			Buffer b = new Buffer ();
			b.num = "b" + bufferNum + ":1";
			aut.addBuffer(b);
			
			// in aut, add EPSILON transition from every final state of aut1 to start state of aut2
			{
				Iterator<State> it = aut1.finalSt.iterator();
				while (it.hasNext()) {
					State s1 = it.next();
					aut.addTransition(aut.getState(s1.num), "EPSILON", aut.getState(aut2.startSt.num));
				}
			}

			// set aut start state = aut1 start state
			aut.startSt = aut.getState(aut1.startSt.num);

			// set aut final states = aut2 final states
			{
				Iterator<State> it = aut2.finalSt.iterator();
				while (it.hasNext()) {
					State s1 = it.next();
					aut.finalSt.add(aut.getState(s1.num));
				}
			}
			
			// set aut start buffers = aut1 start buffers
			{
				Iterator<Buffer> it = aut1.startBuf.iterator();
				while (it.hasNext()) {
					Buffer b1 = it.next();
					aut.startBuf.add(aut.getBuffer(b1.num));
				}
			}

			// set aut final buffer = new buffer created
			aut.finalBuf = b;
			
			// add link from final states of aut2 to new buffer created.
			{
				Iterator<State> it = aut2.finalSt.iterator();
				while (it.hasNext()) {
					State s1 = it.next();
					String action = aut1.finalBuf.num + " * " + aut2.finalBuf.num;
					aut.addStateBufferLink(aut.getState(s1.num), b, action);
				}
			}
			
			// add alphabet from aut1 and aut2 to aut
			{
				Iterator<String> it = aut1.sortedAlph.iterator();
				while (it.hasNext()) {
					String s1 = it.next();
					if (!aut.sortedAlph.contains(s1)) aut.sortedAlph.add(s1);
				}
			}
			{
				Iterator<String> it = aut2.sortedAlph.iterator();
				while (it.hasNext()) {
					String s1 = it.next();
					if (!aut.sortedAlph.contains(s1)) aut.sortedAlph.add(s1);
				}
			}
			
			stateNum++;
			bufferNum++;

			return aut;
		}
		else if (s.startsWith("CHOICE")) {
			Automaton2 aut1 = aut.constructAut(((DefaultMutableTreeNode) n.getChildAt(0)));
			Automaton2 aut2 = aut.constructAut(((DefaultMutableTreeNode) n.getChildAt(1)));
			aut.copy(aut1);
			aut.copy(aut2);
			
			// add 1 new start state to aut
			State newSt = new State();
			newSt.num = "s" + stateNum + ":1";
			aut.addState(newSt);
			
			// add a new buffer -- this will be the new final buffer
			Buffer newBuf = new Buffer ();
			newBuf.num = "b" + bufferNum + ":1";
			aut.addBuffer(newBuf);
			
			// in aut, add EPSILON transition from new start state to start state in aut1 and aut2
			aut.addTransition(newSt, "EPSILON",aut.getState(aut1.startSt.num));
			aut.addTransition(newSt, "EPSILON",aut.getState(aut2.startSt.num));

			// set aut start state = new start state
			aut.startSt = newSt;

			// set aut final states = aut1 and aut2 final states
			{
				Iterator<State> it = aut1.finalSt.iterator();
				while (it.hasNext()) {
					State s1 = it.next();
					aut.finalSt.add(aut.getState(s1.num));
				}
			}
			{
				Iterator<State> it = aut2.finalSt.iterator();
				while (it.hasNext()) {
					State s1 = it.next();
					aut.finalSt.add(aut.getState(s1.num));
				}
			}
			
			// set aut start buffers = start buffers in aut1 and aut2
			{
				Iterator<Buffer> it = aut1.startBuf.iterator();
				while (it.hasNext()) {
					Buffer b1 = it.next();
					aut.startBuf.add(aut.getBuffer(b1.num));
				}
			}
			{
				Iterator<Buffer> it = aut2.startBuf.iterator();
				while (it.hasNext()) {
					Buffer b1 = it.next();
					aut.startBuf.add(aut.getBuffer(b1.num));
				}
			}

			// set aut final buffer = new buffer created
			aut.finalBuf = newBuf;
			
			// add link from final states of aut1 and aut2 to new buffer created.
			{
				Iterator<State> it = aut1.finalSt.iterator();
				while (it.hasNext()) {
					State s1 = it.next();
					String action = aut1.finalBuf.num;
					aut.addStateBufferLink(aut.getState(s1.num), newBuf, action);
				}
			}
			{
				Iterator<State> it = aut2.finalSt.iterator();
				while (it.hasNext()) {
					State s1 = it.next();
					String action = aut2.finalBuf.num;
					aut.addStateBufferLink(aut.getState(s1.num), newBuf, action);
				}
			}

			// add alphabet from aut1 and aut2 to aut
			{
				Iterator<String> it = aut1.sortedAlph.iterator();
				while (it.hasNext()) {
					String s1 = it.next();
					if (!aut.sortedAlph.contains(s1)) aut.sortedAlph.add(s1);
				}
			}
			{
				Iterator<String> it = aut2.sortedAlph.iterator();
				while (it.hasNext()) {
					String s1 = it.next();
					if (!aut.sortedAlph.contains(s1)) aut.sortedAlph.add(s1);
				}
			}
			
			stateNum++;
			bufferNum++;

			return aut;	
		}
		else if (s.startsWith("PLUS")) {
			Automaton2 aut1 = aut.constructAut(((DefaultMutableTreeNode) n.getChildAt(0)));
			aut.copy(aut1);

			// add a new buffer -- this will be the new final buffer
			Buffer newBuf = new Buffer ();
			newBuf.num = "b" + bufferNum + ":1";
			aut.addBuffer(newBuf);

			// in aut, add EPSILON transitions from final states of aut1 to start state of aut1
			{
				Iterator<State> it = aut1.finalSt.iterator();
				while (it.hasNext()) {
					State s1 = it.next();
					aut.addTransition(aut.getState(s1.num), "EPSILON", aut.getState(aut1.startSt.num));
				}
			}

			// set aut start state = aut1 start state
			aut.startSt = aut.getState(aut1.startSt.num);

			// set aut final states = aut1 final states
			{
				Iterator<State> it = aut1.finalSt.iterator();
				while (it.hasNext()) {
					State s1 = it.next();
					aut.finalSt.add(aut.getState(s1.num));
				}
			}
			
			// set aut start buffers = start buffers in aut1
			{
				Iterator<Buffer> it = aut1.startBuf.iterator();
				while (it.hasNext()) {
					Buffer b1 = it.next();
					aut.startBuf.add(aut.getBuffer(b1.num));
				}
			}

			// set aut final buffer = new buffer created
			aut.finalBuf = newBuf;
			
			// add link from final states of aut1 to new buffer created.
			{
				Iterator<State> it = aut1.finalSt.iterator();
				while (it.hasNext()) {
					State s1 = it.next();
					String action = aut1.finalBuf.num;
					aut.addStateBufferLink(aut.getState(s1.num), newBuf, action);
					action = aut1.finalBuf.num + " * " + aut.finalBuf.num;
					aut.addStateBufferLink(aut.getState(s1.num), newBuf, action);
				}
			}

			// add alphabet from aut1 to aut
			{
				Iterator<String> it = aut1.sortedAlph.iterator();
				while (it.hasNext()) {
					String s1 = it.next();
					if (!aut.sortedAlph.contains(s1)) aut.sortedAlph.add(s1);
				}
			}
			
			stateNum++;
			bufferNum++;

			return aut;
		}
		else if (s.startsWith("NOT")) {
			Automaton2 aut1 = aut.constructAut(((DefaultMutableTreeNode) n.getChildAt(0)));  // This one gets negated (i.e. complemented)
			Automaton2 aut2 = aut.constructAut(((DefaultMutableTreeNode) n.getChildAt(1)));  // 

			aut1.display();
			aut2.display();

			//add state to aut1 -- we will not create a new automaton !!! we will modify aut1.. be careful..
			//add transitions from all final states to new state on every member of sortedAlph and every negated member of sortedAlph
			//also transitions from the new state to the new state on every member of sortedAlph and every negated member of sortedAlph
			//make finalState the new state we added
			//aut1 also becomes non-deterministic
			//remember a state has numbering as s#1:#2.. we get #1 from aut1. we will iterate through all #2s in aut1 and choose next number
		
			State newSt = new State();

			// make this into a separate function as: String findNextNum (Automaton2) that returns newStNum 
			// iterate through aut1 states to find the new number.
			// first find #1
			String exStNum = "";
			{
				String s1 = aut1.startSt.num;
				int i1 = s1.indexOf(":");
				exStNum = s1.substring(1,i1);
			}
			// now iterate through states to find max existing numbers to determine #2
			int newNum = 1;
			{
				Iterator<State> it1 = aut1.states.iterator();
				int maxNum = 0;
				while (it1.hasNext()) {
					String s1 = it1.next().num;
					// System.out.println ("#2 = " + s1.substring(s1.indexOf(":") + 1));
					int currNum =  new Integer(s1.substring(s1.indexOf(":") + 1)).intValue();
					maxNum = (currNum > maxNum)? currNum : maxNum;
				}
				newNum = maxNum + 1;
			}
			String newStNum = "s" + exStNum + ":" + newNum;
			// System.out.println ("newStNum = " + newStNum);
			newSt.num = newStNum;
			
			aut1.addState(newSt);

			// add transitions from every final state for every member of sortedalph and negated sortedalph
			// also add transitions from newSt to newSt for every member of sortedalph and negated sortedalph
			// assuming sortedAlph has no negations -- NECESSARY !!! CHECK IF ASSUMPTION IS TRUE!!!
			{
				Iterator<State> it = aut1.finalSt.iterator();
				while (it.hasNext()) {
					State s1 = it.next();
					Iterator<String> alphaIt = aut1.sortedAlph.iterator();
					while (alphaIt.hasNext()){
						String currAlph = alphaIt.next(); 
						aut1.addTransition(s1, currAlph + ":", newSt);
						aut1.addTransition(s1, neg(currAlph) + ":", newSt);
					}
				}
				Iterator<String> alphaIt = aut1.sortedAlph.iterator();
				while (alphaIt.hasNext()){
					String currAlph = alphaIt.next(); 
					aut1.addTransition(newSt, currAlph + ":", newSt);
					aut1.addTransition(newSt, neg(currAlph) + ":", newSt);
				}
			}

			// make newSt the final state of aut1
			aut1.finalSt.clear();
			aut1.finalSt.add(newSt);
			
			aut1.deterministic = false;
			
			// System.out.println ("Automaton after adding new state");
			aut1.display();
			
			aut1 = aut1.determinize(aut1);

			// intersect aut1 and aut2
			Automaton2 aut3 = intersect(aut1,aut2);
			
		}
		else { // symbol
			String symbol = s.substring(0, s.indexOf(':'));
			State s1 = new State();
			s1.num = "s" + stateNum + ":1";
			State s2 = new State();
			s2.num = "s" + stateNum + ":2";
			aut.addState(s1);
			aut.addState(s2);
			aut.addTransition(s1, symbol + ":", s2);
			aut.addTransition(s2, symbol + ":", s2);
			aut.addTransition(s1, neg(symbol) + ":", s1);
			aut.addTransition(s2, neg(symbol) + ":", s1);
			
			aut.startSt = s1;
			aut.finalSt.add(s2);

			Buffer b = new Buffer();
			b.num = "b" + bufferNum + ":1";
			aut.addBuffer(b);
			aut.addStateBufferLink(s2, b, "prev event");
			aut.startBuf.add(b);
			aut.finalBuf = b;
			
			aut.deterministic = true;
			aut.sortedAlph.add(symbol); // add symbol to alphabet

			stateNum ++;
			bufferNum ++;
			return aut;
		}
		return aut;
	}
	
	public String neg(String s) {
		if (s.charAt(0) == '!') return s.substring(1);
		else return "!" + s;
	}

	public void copy(Automaton2 a) { // copy states, transitions, buffers, buffer actions and state buffer links
									// no need to copy start state, final state, start buffer, final buffer
									// assumption: no "conflicts" between this and a
		{ // copy states
			Iterator<State> it = a.states.iterator();
			while (it.hasNext()) {
				State s = it.next();
				State s1 = new State();
				this.addState(s1);
				s1.num = s.num;
			}
		}
		{ // copy transitions
			Iterator<State> it1 = a.transitions.keySet().iterator();
			while (it1.hasNext()) {
				State st = it1.next();
				Transition t = a.transitions.get(st);
				Iterator<String> it2 = t.keySet().iterator();
				while (it2.hasNext()) {
					String s = it2.next();
					Iterator<State> it3 = t.get(s).iterator();
					while (it3.hasNext()) {
						State dest = it3.next();
						addTransition(getState(st.num),s,getState(dest.num));
					}
				}
			}
		}
		{ // copy buffers and actions
			Iterator<Buffer> it = a.buffers.iterator();
			while (it.hasNext()) {
				Buffer b = it.next();
				Buffer b1 = new Buffer();
				this.addBuffer(b1);
				b1.num = b.num;
				Iterator<String> it1 = b.actions.iterator();
				while (it1.hasNext()) {
					String s = it1.next();
					b1.actions.add(s);
				}
			}
		}
		{ // copy state-buffer-links
			Iterator<State> it = a.stateBufferLink.keySet().iterator();
			while (it.hasNext()) {
				State s = it.next();
				Iterator<Buffer> it1 = a.stateBufferLink.get(s).iterator();
				while (it1.hasNext()) {
					Buffer b = it1.next();
					addStateBufferLink(getState(s.num), getBuffer(b.num));
				}
			}
		}
	}
	
	// construct a deterministic automaton for a
	public Automaton2 determinize (Automaton2 a) {

		System.out.println("determinize");
		if (a.deterministic == true) return a;

		int numSyms = (int) Math.pow(2, sortedAlph.size());
		System.out.println ("num symbols = " + numSyms);
		Object[] alphStrs = sortedAlph.toArray();
		
		Automaton2 aut = new Automaton2();
		
		// initializing the NFA table
		HashMap<String, HashMap<String, LinkedHashSet<State>>> nfaTable = new HashMap<String, HashMap<String, LinkedHashSet<State>>>();
		{
			Iterator<State> it = a.states.iterator();
			while (it.hasNext()) {
				State s = it.next();
				String sNum = s.num;
				HashMap<String, LinkedHashSet<State>> hm = new HashMap<String, LinkedHashSet<State>>();
				nfaTable.put(sNum, hm);
				for (int i = 0; i < numSyms; i++) {
					String alph = "";
					for (int j = 0; j < sortedAlph.size(); j++) {
						int bitMask = (int) Math.pow(2, j);
						// add : as separator between individual symbols
						if ((i & bitMask) > 0) alph = (String) alphStrs[j] + ":" + alph;
						else alph = "!" + (String)alphStrs[j] + ":" + alph;
					}
					System.out.println ("alph = " + alph);
					LinkedHashSet<State> s1 = new LinkedHashSet<State> ();
					hm.put(alph, s1);
				}
			}		
		}
		
		// fix the NFA transitions to use expanded alphabet
		{
			Iterator<State> it = transitions.keySet().iterator();
			while (it.hasNext()) {
				Transition currTr = transitions.get(it.next());
				
				Transition dupCurr = (Transition) currTr.clone();
				
				Iterator<String> it1 = dupCurr.keySet().iterator();
				while (it1.hasNext()) {
					String sym = it1.next();
					if (sym.equals("EPSILON")) continue;
					for (int i = 0; i < numSyms; i++) {
						String alph = "";
						boolean conf = false;
						for (int j = 0; j < sortedAlph.size(); j++) {
							int bitMask = (int) Math.pow(2, j);
							if ((i & bitMask) > 0) {
								alph = (String) alphStrs[j] + ":" + alph;
								if (sym.contains("!" + (String) alphStrs[j])) conf = true;
							}
							else {
								alph = "!" + (String)alphStrs[j] + ":" + alph;
								int t1 = sym.indexOf((String)alphStrs[j]);
								if (t1 != -1) {
									if (t1 == 0) conf = true;
									else if (sym.charAt(t1 - 1) != '!') conf = true; 
								}
							}
						}
						if (conf == true) continue;
						// System.out.println ("not a conflict: " + sym + "::" + alph);
						currTr.remove(sym); // remove transition using old symbols and add transitions using new alph
						Iterator<State> it2 = dupCurr.get(sym).iterator();
						while (it2.hasNext()) {
							State cs = it2.next();
							currTr.addTransition(alph, cs);
						}
					}
				}
			}
		}
		
		a.display();
		
		HashMap<State,LinkedHashSet<State>> epsCl = epsilonClosure(a);
		HashMap<State,HashSet<State>> mapping = new HashMap<State,HashSet<State>> (); // this will hold the mapping from states in aut -> set of states in a
		LinkedList<State> statesToDo = new LinkedList<State>(); // states for which we need to add transitions
		LinkedList<State> statesDone = new LinkedList<State>(); // list of states that have been added

		{ // add start state to aut
			int n = 1;
			State s = new State();
			s.num = "s" + Automaton2.stateNum + ":" + n++; // this is the start state in aut
			aut.addState(s);
			aut.startSt = s;
			// s corresponds to the epsilonClosure of start state in a, create that mapping
			mapping.put(s, epsCl.get(a.startSt));
			statesToDo.add(s);
			while (!statesToDo.isEmpty()) {
				State currDetSt = statesToDo.remove();
				statesDone.add(currDetSt);
				HashSet<State> currNDSt = mapping.get(currDetSt);

				// loop over all det symbols and get set of ND states to which currDetSt transitions to
				for (int i = 0; i < numSyms; i++) {
					String alph = "";
					for (int j = 0; j < sortedAlph.size(); j++) {
						int bitMask = (int) Math.pow(2, j);
						// add : as separator between individual symbols
						if ((i & bitMask) > 0) alph = (String) alphStrs[j] + ":" + alph;
						else alph = "!" + (String)alphStrs[j] + ":" + alph;
					}
					System.out.println ("alph = " + alph);
					Iterator<State> it1 = currNDSt.iterator();
					HashSet<State> currNDDestSt = new HashSet<State>();
					while (it1.hasNext()) {
						State s1 = it1.next();
						HashSet<State> currDest = a.getTransitionStates(s1, alph);
						{
							Iterator<State> curState = currDest.iterator();
							while (curState.hasNext())
							{
								currNDDestSt.addAll(epsCl.get(curState.next()));
							}
							
						}
						
					}
					if (currNDDestSt.isEmpty()) continue;
					
					// check if mapping already exists with currNDDest as the range.. we have to anyway loop to get the key..
					Set<State> keySet = mapping.keySet();
					boolean found = false;
					State newDetSt = null;
					Iterator<State> it2 = keySet.iterator();
					while (it2.hasNext()) {
						State detSt = it2.next();
						HashSet<State> ndSt = mapping.get(detSt);
						if (currNDDestSt.equals(ndSt)) {
							newDetSt = detSt;
							found = true;
						}
					}
					if (found) {
						aut.addTransition(currDetSt, alph, newDetSt);
						if (!statesDone.contains(newDetSt) && !statesToDo.contains(newDetSt))
						{
							statesToDo.add(newDetSt);
						}
					}
					else {
						State s1 = new State();
						s1.num = "s" + Automaton2.stateNum + ":" + n++;
						aut.addState(s1);
						mapping.put(s1, currNDDestSt);
						statesToDo.add(s1);
						aut.addTransition(currDetSt, alph, s1);
					}
				}
			}
		}
		{
			// set deterministic to true
			aut.deterministic = true;
			// fix final states for aut as: a state is a final state if the mapping for that state has atleast one final state for a
			Set<State> keySet = mapping.keySet();
			Iterator<State> it2 = keySet.iterator();
			while (it2.hasNext()) {
				State detSt = it2.next();
				HashSet<State> ndSt = mapping.get(detSt);
				Iterator<State> it3 = ndSt.iterator();
				while (it3.hasNext()) {
					if (a.finalSt.contains(it3.next())) {
						aut.finalSt.add(detSt);
						break;
					}
				}
			}
		}
		{
			// fix alphabet for aut = alphabet for a
			Iterator<String> it2 = a.sortedAlph.iterator();
			while (it2.hasNext()) {
				aut.sortedAlph.add(it2.next());
			}
		}
		{
			Iterator<Buffer> bufferIt = a.buffers.iterator();
					while (bufferIt.hasNext())
					{
						aut.buffers.add(bufferIt.next().clone());
					}
			
			// fix state/buffer mapping
			Iterator<State> a2StatesIterator = mapping.keySet().iterator();
			while (a2StatesIterator.hasNext())
			{
				State a2State = a2StatesIterator.next();
				LinkedHashSet<Buffer> allLinkedBuffers = new LinkedHashSet<Buffer>();
				//System.out.print("curState" + a2State.num + "\n");
				Iterator<State> stateBufferLinkIterator = a.stateBufferLink.keySet().iterator();
				while (stateBufferLinkIterator.hasNext())
				{
					State linkedState = stateBufferLinkIterator.next();
					//System.out.print("Compare: " + a2State.num + " " + linkedState.num + "\n");
					if (mapping.get(a2State).contains(linkedState))
					{
						LinkedHashSet<Buffer> linkedBuffers = a.stateBufferLink.get(linkedState);
						Iterator<Buffer> linkedBuffersIterator = linkedBuffers.iterator();
						while (linkedBuffersIterator.hasNext())
						{
							Buffer curBuffer = linkedBuffersIterator.next();
							allLinkedBuffers.add(aut.getBuffer(curBuffer.num));
						}
					}
				}
				aut.stateBufferLink.put(a2State, allLinkedBuffers);
			}


			Iterator<Buffer> bufIt = a.startBuf.iterator();
			while (bufIt.hasNext())
			{
				aut.startBuf.add(aut.getBuffer(bufIt.next().num));
			}

			aut.finalBuf = aut.getBuffer(a.finalBuf.num);
		}
				
		{
			System.out.println ("Done some determinization");
			System.out.println ("Mapping is");
			Set<State> keySet = mapping.keySet();
			Iterator<State> it2 = keySet.iterator();
			while (it2.hasNext()) {
				State detSt = it2.next();
				String s = "";
				HashSet<State> ndSt = mapping.get(detSt);
				Iterator<State> it3 = ndSt.iterator();
				while (it3.hasNext()) {
					s = s + it3.next().num + "::";
				}
				System.out.println (detSt.num + " => " + s);
			}
		}
		
		aut.display();
		
		{
			Iterator<State> it = a.states.iterator();
			while (it.hasNext()) {
				State s = it.next();
				String sNum = s.num;
				System.out.println ("state num = " + sNum);
				HashMap<String, LinkedHashSet<State>> currTr = transitions.get(s);
				Iterator<String> it1 = currTr.keySet().iterator();
				while (it1.hasNext()) {
					String sym = it1.next();
					if (sym.equals("EPSILON")) continue;
					LinkedHashSet<State> completeStates = new LinkedHashSet<State> ();
					
					LinkedHashSet<State> startingStates = new LinkedHashSet<State> ();
					LinkedHashSet<State> stStCl = epsCl.get(s);
					
					{
						Iterator<State> it2 = stStCl.iterator();
						while (it2.hasNext()) {
							State cSt = it2.next();
							LinkedHashSet<State> cStTr = a.transitions.get(cSt).get(sym);
							Iterator<State> it3 = cStTr.iterator();
							while (it3.hasNext()) {
								LinkedHashSet<State> cStTrCl = epsCl.get(it3.next());
								Iterator<State> it4 = cStTrCl.iterator();
								while (it4.hasNext()) {
									State s1 = it4.next();
									if (!completeStates.contains(s1)) completeStates.add(s1);
								}
							}
						}
					}
					{
						Iterator<State> it2 = completeStates.iterator();
						while (it2.hasNext()) {
							nfaTable.get(s.num).get(sym).add(it2.next());
						}
					}
				}
			}	
		}
		
		/*
		HashMap<State,LinkedHashSet<State>> epsCl = epsilonClosure(a); // remember that all objects are actually states within automaton a
		HashMap<State,LinkedHashSet<State>> mapping = new HashMap<State,LinkedHashSet<State>> (); // this will hold the mapping from states in aut -> set of states in a
		
		{ // add start state to aut
			State s = new State();
			s.num = "s" + aut.stateNum + ":1"; // this is the start state in aut
			aut.addState(s);
			// s corresponds to the epsilonClosure of start state in a, create that mapping
			mapping.put(s, epsCl.get(a.startSt));
		}		
		*/
		
		Automaton2.stateNum ++;
		Automaton2.bufferNum ++;
		
		return aut;
	}
	
	public Automaton2 intersect(Automaton2 aut1, Automaton2 aut2)
	{
		Automaton2 a = new Automaton2();

		aut1 = aut1.determinize(aut1);
		aut2 = aut2.determinize(aut2);

		a.sortedAlph.addAll(aut1.sortedAlph);
		a.sortedAlph.addAll(aut2.sortedAlph);
		aut1.sortedAlph.addAll(a.sortedAlph);
		aut2.sortedAlph.addAll(a.sortedAlph);
		// fix transitions to use alphabet

		// fix the NFA transitions to use expanded alphabet
		{
			int numSyms = (int) Math.pow(2, a.sortedAlph.size());
			Object[] alphaStrs = a.sortedAlph.toArray();
			Iterator<State> transIt = aut1.transitions.keySet().iterator();
			while (transIt.hasNext()) {
				Transition currTr = aut1.transitions.get(transIt.next());

				Transition dupCurr = (Transition) currTr.clone();

				Iterator<String> it1 = dupCurr.keySet().iterator();
				while (it1.hasNext()) {
					String sym = it1.next();
					if (sym.equals("EPSILON")) continue;
					for (int i = 0; i < numSyms; i++) {
						String alph = "";
						boolean conf = false;
						for (int j = 0; j < a.sortedAlph.size(); j++) {
							int bitMask = (int) Math.pow(2, j);
							if ((i & bitMask) > 0) {
								alph = (String) alphaStrs[j] + ":" + alph;
								if (sym.contains("!" + (String) alphaStrs[j])) conf = true;
							}
							else {
								alph = "!" + (String)alphaStrs[j] + ":" + alph;
								int t1 = sym.indexOf((String)alphaStrs[j]);
								if (t1 != -1) {
									if (t1 == 0) conf = true;
									else if (sym.charAt(t1 - 1) != '!') conf = true; 
								}
							}
						}
						if (conf == true) continue;
						// System.out.println ("not a conflict: " + sym + "::" + alph);
						currTr.remove(sym); // remove transition using old symbols and add transitions using new alph
						Iterator<State> it2 = dupCurr.get(sym).iterator();
						while (it2.hasNext()) {
							State cs = it2.next();
							currTr.addTransition(alph, cs);
						}
					}
				}
			}
		}
		{
			int numSyms = (int) Math.pow(2, a.sortedAlph.size());
			Object[] alphaStrs = a.sortedAlph.toArray();
			Iterator<State> transIt = aut2.transitions.keySet().iterator();
			while (transIt.hasNext()) {
				Transition currTr = aut2.transitions.get(transIt.next());

				Transition dupCurr = (Transition) currTr.clone();

				Iterator<String> it1 = dupCurr.keySet().iterator();
				while (it1.hasNext()) {
					String sym = it1.next();
					if (sym.equals("EPSILON")) continue;
					for (int i = 0; i < numSyms; i++) {
						String alph = "";
						boolean conf = false;
						for (int j = 0; j < a.sortedAlph.size(); j++) {
							int bitMask = (int) Math.pow(2, j);
							if ((i & bitMask) > 0) {
								alph = (String) alphaStrs[j] + ":" + alph;
								if (sym.contains("!" + (String) alphaStrs[j])) conf = true;
							}
							else {
								alph = "!" + (String)alphaStrs[j] + ":" + alph;
								int t1 = sym.indexOf((String)alphaStrs[j]);
								if (t1 != -1) {
									if (t1 == 0) conf = true;
									else if (sym.charAt(t1 - 1) != '!') conf = true; 
								}
							}
						}
						if (conf == true) continue;
						// System.out.println ("not a conflict: " + sym + "::" + alph);
						currTr.remove(sym); // remove transition using old symbols and add transitions using new alph
						Iterator<State> it2 = dupCurr.get(sym).iterator();
						while (it2.hasNext()) {
							State cs = it2.next();
							currTr.addTransition(alph, cs);
						}
					}
				}
			}
		}
		System.out.print("Testing Intersection!: \n");
		aut1.display();
		aut2.display();
		// Alphabet is now fixed

		System.out.print("in the State?: " + aut1.states.contains(aut1.startSt) + "\n");
		
		HashMap<State,LinkedList<State>> mapping = new HashMap<State,LinkedList<State>> (); // this will hold the mapping from states in aut1 and aut2 -> set of states in a
		LinkedList<State> statesToDo = new LinkedList<State>(); // states for which we need to add transitions
		LinkedList<State> statesDone = new LinkedList<State>(); // list of states that have been added
		LinkedList<State> listStates = new LinkedList<State>();  // create LinkedList to hold start states -- order is impt
		listStates.add(aut1.startSt); // add the start state from aut1
		listStates.add(aut2.startSt); // add the start state from aut2
		System.out.print("in the hashSet?: " + listStates.contains(aut1.startSt) + "\n");
		State newState = new State(); // create the new state to be the start state of a
		newState.num = "s" + stateNum + ":1"; // give it the first number
		a.addState(newState); // add it to a
		a.startSt = newState; // set it as the start state
		mapping.put(newState, listStates); // add the mapping of the start states
		// System.out.print("in the mapping?: " + mapping.get(newState).contains(aut1.startSt) + "\n");
		// System.out.print("hashSet in mapping?: " + listStates.equals(mapping.get(newState)));
		int n = 2; // initialize numbering for subsequent states in a
		statesToDo.add(newState); // place the start state as the beginning of our queue for finding other states
		while (!statesToDo.isEmpty())
		{
			State curState = statesToDo.remove(); // take out the first state
			statesDone.add(curState); // we have to do it here.. otherwise we might end up adding curState again in statesToDo
			// System.out.print("is newState = curState?:" + newState.equals(curState) + "\n");
			System.out.print("Working on State: " + curState.num + "\n"); // output where we are
			LinkedList<State> tempList = mapping.get(curState); // use the mapping to get the states that make up the state in a from aut1 and aut2
			State s1 = tempList.get(0);
			State s2 = tempList.get(1);			
            
			// System.out.print("is aut1.startSt = s2?:" + aut1.startSt.equals(s2) + "\n");
			// System.out.print("is aut2.startSt = s1?:" + aut2.startSt.equals(s1) + "\n");
			// System.out.print(s1);
			// System.out.print(s2);
			// System.out.print("Transitions: " + aut1.transitions + "\n");
			Transition trans1 = aut1.transitions.get(s1); // get the transitions from these states in aut1 and aut2
			// System.out.print("Trans1: " + trans1);
			Transition trans2 = aut2.transitions.get(s2);
			// System.out.print("Keyset: " + trans1.keySet().toArray() + "\n");
			Iterator<String> symbolsIt = trans1.keySet().iterator(); // iterate over all the symbols
			while (symbolsIt.hasNext())
			{
				String curSymbol = symbolsIt.next();
				// System.out.print("Working on Symbol: " + curSymbol + "\n");
				HashSet<State> destStates1 = trans1.get(curSymbol); //get states the transitions go to on that symbol
				HashSet<State> destStates2 = trans2.get(curSymbol);
				LinkedList<State> destSet = new LinkedList<State>(); // set to collect the resulting sets of state into
				Iterator<State> destStateIt1 = destStates1.iterator();
				while(destStateIt1.hasNext())
				{
					State destState1 = destStateIt1.next();
					Iterator<State> destStateIt2 = destStates2.iterator();
					while (destStateIt2.hasNext())
					{
						State destState2 = destStateIt2.next();
						destSet.clear();
						destSet.add(destState1);
						destSet.add(destState2);
						Iterator<State> intersectIt = mapping.keySet().iterator();
						boolean flag = false;
						State intersectState = new State();
						while (intersectIt.hasNext())
						{
							State tempState = intersectIt.next();
							if (destSet.equals(mapping.get(tempState)))
							{
								flag = true;
								intersectState = tempState;
							}
						}
						if (flag == false)
						{
							intersectState.num = "s" + Automaton2.stateNum + ":" + n;
							n++;
							a.addState(intersectState);
							mapping.put(intersectState, destSet);
							System.out.println ("adding to mapping " + intersectState.num + " => " +
									destSet.get(0).num + "::" + destSet.get(1).num);
						}
						a.addTransition(curState, curSymbol, intersectState);
						if (!statesDone.contains(intersectState) && !statesToDo.contains(intersectState))
						{
							statesToDo.add(intersectState);
						}
					}
				}
			}
		}

		// TO DO: Fix final states, buffers, startbuf and final buf
		
		a.display();
		
		Automaton2.stateNum++;
		Automaton2.bufferNum++;
		return a;
	}
	
	// return the epsilonclosure of the states in a -- all objects are actually states within automaton a
	public HashMap<State,LinkedHashSet<State>> epsilonClosure (Automaton2 a) {
		
		HashMap<State,LinkedHashSet<State>> epsCl = new HashMap<State,LinkedHashSet<State>> ();
		Iterator<State> it = a.states.iterator();
		while (it.hasNext()) {
			State s = it.next();
			LinkedHashSet<State> epsClState = epsilonClosureState(a, s);
			epsCl.put(s, epsClState);
		}
		return epsCl;
	}

	// return the epsilon closure of the state s in a -- objects returned are actually objects within a
	public LinkedHashSet<State> epsilonClosureState(Automaton2 a, State s) {

		LinkedHashSet<State> epsClState = new LinkedHashSet<State> ();
		LinkedList<State> statesToDo = new LinkedList<State>();
		epsClState.add(s);
		statesToDo.add(s);
		while (!statesToDo.isEmpty()) {
			State s1 = statesToDo.remove();
			HashMap<String, LinkedHashSet<State>> trans = a.transitions.get(s1);
			if (!trans.containsKey("EPSILON")) continue;
			LinkedHashSet<State> dest = trans.get("EPSILON");
			Iterator<State> it1 = dest.iterator();
			while (it1.hasNext()) {
				State s2 = it1.next();
				if (!epsClState.contains(s2)) {
					epsClState.add(s2);
					statesToDo.add(s2);
				}
			}
		}
		
		System.out.println ("epsilon closure of state " + s.num);
		{
			Iterator<State> it = epsClState.iterator();
			while (it.hasNext()) {
				System.out.print(it.next().num + "::");
			}
			System.out.println ("");
		}
		return epsClState;
	}
	
	public void display() {
		System.out.print("States : ");
		{
			Iterator<State> it = states.iterator();
			while (it.hasNext()) {
				State st = it.next();
				System.out.print (st.num + " :: ");
			}
			System.out.println ("");
		}
		
		System.out.print("sorted Alph : [ ");
		{
			Iterator<String> it = sortedAlph.iterator();
			while (it.hasNext()) {
				String st = it.next();
				System.out.print (st + " ");
			}
			System.out.println ("]");
		}

		System.out.println ("deterministic = " + deterministic);
		
		System.out.println ("Transitions : ");
		{
			Iterator<State> it1 = transitions.keySet().iterator();
			while (it1.hasNext()) {
				State st = it1.next();
				Transition t = transitions.get(st);
				Iterator<String> it2 = t.keySet().iterator();
				while (it2.hasNext()) {
					String s = it2.next();
					Iterator<State> it3 = t.get(s).iterator();
					System.out.print ("  " + st.num + "(" + s + ")" + " -> ");
					while (it3.hasNext()) {
						State dest = it3.next();
						System.out.print(dest.num + " :: ");
					}
					System.out.print ("\n");
				}
				System.out.print ("\n");
			}
			System.out.print("\n");
		}
		
		System.out.println ("Start State : " + startSt.num);
		
		System.out.print ("Final States : ");
		{
			Iterator<State> it = finalSt.iterator();
			while (it.hasNext()) {
				State st = it.next();
				System.out.print (st.num + " :: ");
			}
			System.out.println ("\n");
		}

		System.out.print ("Buffers : ");
		{
			Iterator<Buffer> it = buffers.iterator();
			while (it.hasNext()) {
				Buffer b = it.next();
				System.out.print (b.num + " :: ");
			}
			System.out.println ("");
		}
		
		System.out.print ("Start Buffers : ");
		{
			Iterator<Buffer> it = startBuf.iterator();
			while (it.hasNext()) {
				Buffer b = it.next();
				System.out.print (b.num + " :: ");
			}
			System.out.println ("");
		}

		System.out.println ("Final Buffer : " + finalBuf.num);
		
		System.out.println ("State-Buffer-Link : ");
		{
			Iterator<State> it = stateBufferLink.keySet().iterator();
			while (it.hasNext()) {
				State s = it.next();
				Iterator<Buffer> it1 = stateBufferLink.get(s).iterator();
				while (it1.hasNext()) {
					Buffer b = it1.next();
					Iterator<String> it2 = b.actions.iterator();
					String act = "";
					while (it2.hasNext()) {
						act = act + it2.next() + "::";
					}
					System.out.println ("  " + s.num + "->" + b.num + " :: " + act);
				}
			}
		}
		System.out.println ("");
	}
}
