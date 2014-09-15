// Hopefully there's no place that I need a deep copy and instead got a pointer -_- not that used to Java yet



package regExParsePkg2;

import javax.swing.tree.DefaultMutableTreeNode;
import java.util.*;

//This class specifies a state.
class State {
	public String num; // the state number
	public LinkedHashSet<String> tag=new LinkedHashSet<String>(); // this is the list of buffers associated with a state
}

class Buffer {
	public String num; // the buffer number
	public LinkedHashSet<String> actions = new LinkedHashSet<String>(); // how to populate the buffer
	public LinkedHashSet<String> tag=new LinkedHashSet<String>(); // this is the list of variables/tags associated with the buffer... do we need this???
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
	                                          // this has not been maintained, it should be done at the end of the process

	public LinkedHashSet<Buffer> buffers; // set of buffers
	public LinkedHashSet<Buffer> startBuf; // set of start buffers
	public Buffer finalBuf; // final buffer
	public HashMap<State, LinkedHashSet<Buffer>> stateBufferLink; // buffers to be modified when we reach a state
	
	public boolean deterministic = false; // is automaton deterministic?
	public LinkedHashSet<String> symbolSet = new LinkedHashSet<String>(); // set of symbols for the automaton
	public LinkedHashSet<String> alphabet = new LinkedHashSet<String>(); // alphabet used on transitions (transitions need to be fixed whenever this is changed
	                                                                     // and this will be changed whenever a new symbol is added.  
	
	public static int stateNum = 1; // a class variable for numbering states
	public static int bufferNum = 1; // a class variable for numbering buffers
	
	public Automaton2 () {
		states = new LinkedHashSet<State>();
		transitions = new HashMap<State,Transition>();
		startSt = null;
		finalSt = new LinkedHashSet<State>();
		
		buffers = new LinkedHashSet<Buffer>();
		startBuf = new LinkedHashSet<Buffer>();
		finalBuf = null;
		stateBufferLink = new HashMap<State,LinkedHashSet<Buffer>>();
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
		Transition at = transitions.get(s);
		return (at.get(sym));
	}

	public Automaton2 constructAut(DefaultMutableTreeNode n) {
		
		Automaton2 aut = new Automaton2(); // this is set to non-deterministic by default. unless we specify otherwise
		String s = n.getUserObject().toString();
		
		//My random debugging (Kunji)
		System.out.print(s + "\n");
		//System.out.println ("\n");
		//aut.display();

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
			
			// add symbols from aut1 and aut2 to aut
			{
				Iterator<String> it = aut2.symbolSet.iterator();
				while (it.hasNext()) {
					String symbol = it.next();
					//System.out.print(symbol);
					if (!aut.symbolSet.contains(symbol))
						{
							aut.symbolSet.add(symbol);
							// fix alphabet
							{
								if (aut.alphabet.isEmpty())
								{
									String newLetter1 = symbol;
									String newLetter2 = "!" + symbol;
									aut.alphabet.add(newLetter1);
									aut.alphabet.add(newLetter2);
								}
								else
								{
									LinkedHashSet<String> newAlphabet = new LinkedHashSet<String>();
									Iterator<String> alphabetIterator = aut.alphabet.iterator();
									while(alphabetIterator.hasNext())
									{
										String replacedLetter = alphabetIterator.next();
										String newLetter1 = replacedLetter + symbol;
										String newLetter2 = replacedLetter + "!" + symbol;
										newAlphabet.add(newLetter1);
										newAlphabet.add(newLetter2);
									}
									aut.alphabet = newAlphabet;
								}
							}
							// fix transitions to use alphabet
							{
								//System.out.print(aut.transitions.toString() + "\n");
								HashMap<State, Transition> newTransitions = new HashMap<State, Transition>();
								Iterator<State> transitionsIterator = aut.transitions.keySet().iterator();
								while(transitionsIterator.hasNext())
								{
									State curTransitionFrom = transitionsIterator.next();
									Transition newTransition = new Transition();
									Transition curTransition = aut.transitions.get(curTransitionFrom);
									Iterator<String> curTransitionIterator = curTransition.keySet().iterator();
									while(curTransitionIterator.hasNext())
									{
										String curSymbol = curTransitionIterator.next();
										LinkedHashSet<State> curTransitionTo = aut.transitions.get(curTransitionFrom).getTransitions(curSymbol);
										Iterator<State> curTransitionToIterator = curTransitionTo.iterator();
										while (curTransitionToIterator.hasNext())
										{
											State curTransitionToState = curTransitionToIterator.next();
											if (curSymbol == "EPSILON")
												{
												newTransition.addTransition("EPSILON", curTransitionToState); 
												//continue;
												}
											else
											{
												//System.out.print("curSymbol: " + curSymbol + "   " + "symbol: " + symbol + "\n");
												String newCurSymbol1 = curSymbol;
												if (curSymbol.contains(symbol))
												{
													newTransition.addTransition(curSymbol, curTransitionToState);
													//continue;
												}
												else
												{
													newCurSymbol1 = curSymbol + symbol;
													if(!(aut.alphabet.contains(newCurSymbol1)))
														{
															newCurSymbol1 = symbol + curSymbol;
														}
													String newCurSymbol2 = curSymbol + "!" + symbol;
													if(!(aut.alphabet.contains(newCurSymbol2)))
													{
														newCurSymbol2 = "!" + symbol + curSymbol;
													}
													//System.out.print("adding transition: " + newCurSymbol1 + "\n");
													//System.out.print("adding transition: " + newCurSymbol2 + "\n");
													newTransition.addTransition(newCurSymbol1, curTransitionToState);
													newTransition.addTransition(newCurSymbol2, curTransitionToState);
												}
											}
											newTransitions.put(curTransitionFrom, newTransition);
											//System.out.print(newTransitions.toString() + "\n");
										}
									}
								}
								//System.out.print("Symbol Added, RESULTS: " + "\n");
								//System.out.print(aut.transitions.toString() + "\n");
								aut.transitions = newTransitions;
								//System.out.print(aut.transitions.toString() + "\n");
							}
						}
				}
			}
			{
				Iterator<String> it = aut1.symbolSet.iterator();
				while (it.hasNext()) {
					String symbol = it.next();
					if (!aut.symbolSet.contains(symbol))
						{
							aut.symbolSet.add(symbol);
							// fix alphabet
							{
								if (aut.alphabet.isEmpty())
								{
									String newLetter1 = symbol;
									String newLetter2 = "!" + symbol;
									aut.alphabet.add(newLetter1);
									aut.alphabet.add(newLetter2);
								}
								else
								{
									LinkedHashSet<String> newAlphabet = new LinkedHashSet<String>();
									Iterator<String> alphabetIterator = aut.alphabet.iterator();
									while(alphabetIterator.hasNext())
									{
										String replacedLetter = alphabetIterator.next();
										String newLetter1 = replacedLetter + symbol;
										String newLetter2 = replacedLetter + "!" + symbol;
										newAlphabet.add(newLetter1);
										newAlphabet.add(newLetter2);
									}
									aut.alphabet = newAlphabet;
								}
							}
							// fix transitions to use alphabet
							{
								HashMap<State, Transition> newTransitions = new HashMap<State, Transition>();
								Iterator<State> transitionsIterator = aut.transitions.keySet().iterator();
								while(transitionsIterator.hasNext())
								{
									State curTransitionFrom = transitionsIterator.next();
									Transition newTransition = new Transition();
									Transition curTransition = aut.transitions.get(curTransitionFrom);
									Iterator<String> curTransitionIterator = curTransition.keySet().iterator();
									while(curTransitionIterator.hasNext())
									{
										String curSymbol = curTransitionIterator.next();
										LinkedHashSet<State> curTransitionTo = aut.transitions.get(curTransitionFrom).getTransitions(curSymbol);
										Iterator<State> curTransitionToIterator = curTransitionTo.iterator();
										while (curTransitionToIterator.hasNext())
										{
											State curTransitionToState = curTransitionToIterator.next();
											if (curSymbol == "EPSILON")
												{
												newTransition.addTransition("EPSILON", curTransitionToState); 
												}
											else
											{
												String newCurSymbol1 = curSymbol;
												if (curSymbol.contains(symbol))
												{
													newTransition.addTransition(curSymbol, curTransitionToState);
												}
												else
												{
													newCurSymbol1 = curSymbol + symbol;
													if(!(aut.alphabet.contains(newCurSymbol1)))
														{
															newCurSymbol1 = symbol + curSymbol;
														}
													String newCurSymbol2 = curSymbol + "!" + symbol;
													if(!(aut.alphabet.contains(newCurSymbol2)))
													{
														newCurSymbol2 = "!" + symbol + curSymbol;
													}
													newTransition.addTransition(newCurSymbol1, curTransitionToState);
													newTransition.addTransition(newCurSymbol2, curTransitionToState);
												}
											}
											newTransitions.put(curTransitionFrom, newTransition);
										}
									}
								}
								aut.transitions = newTransitions;
							}
						}
				}
			}
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
				Iterator<String> it = aut2.symbolSet.iterator();
				while (it.hasNext()) {
					String symbol = it.next();
					if (!aut.symbolSet.contains(symbol))
						{
							aut.symbolSet.add(symbol);
							// fix alphabet
							{
								if (aut.alphabet.isEmpty())
								{
									String newLetter1 = symbol;
									String newLetter2 = "!" + symbol;
									aut.alphabet.add(newLetter1);
									aut.alphabet.add(newLetter2);
								}
								else
								{
									LinkedHashSet<String> newAlphabet = new LinkedHashSet<String>();
									Iterator<String> alphabetIterator = aut.alphabet.iterator();
									while(alphabetIterator.hasNext())
									{
										String replacedLetter = alphabetIterator.next();
										String newLetter1 = replacedLetter + symbol;
										String newLetter2 = replacedLetter + "!" + symbol;
										newAlphabet.add(newLetter1);
										newAlphabet.add(newLetter2);
									}
									aut.alphabet = newAlphabet;
								}
							}
							// fix transitions to use alphabet
							{
								HashMap<State, Transition> newTransitions = new HashMap<State, Transition>();
								Iterator<State> transitionsIterator = aut.transitions.keySet().iterator();
								while(transitionsIterator.hasNext())
								{
									State curTransitionFrom = transitionsIterator.next();
									Transition newTransition = new Transition();
									Transition curTransition = aut.transitions.get(curTransitionFrom);
									Iterator<String> curTransitionIterator = curTransition.keySet().iterator();
									while(curTransitionIterator.hasNext())
									{
										String curSymbol = curTransitionIterator.next();
										LinkedHashSet<State> curTransitionTo = aut.transitions.get(curTransitionFrom).getTransitions(curSymbol);
										Iterator<State> curTransitionToIterator = curTransitionTo.iterator();
										while (curTransitionToIterator.hasNext())
										{
											State curTransitionToState = curTransitionToIterator.next();
											if (curSymbol == "EPSILON")
												{
												newTransition.addTransition("EPSILON", curTransitionToState); 
												}
											else
											{
												String newCurSymbol1 = curSymbol;
												if (curSymbol.contains(symbol))
												{
													newTransition.addTransition(curSymbol, curTransitionToState);
												}
												else
												{
													newCurSymbol1 = curSymbol + symbol;
													if(!(aut.alphabet.contains(newCurSymbol1)))
														{
															newCurSymbol1 = symbol + curSymbol;
														}
													String newCurSymbol2 = curSymbol + "!" + symbol;
													if(!(aut.alphabet.contains(newCurSymbol2)))
													{
														newCurSymbol2 = "!" + symbol + curSymbol;
													}
													newTransition.addTransition(newCurSymbol1, curTransitionToState);
													newTransition.addTransition(newCurSymbol2, curTransitionToState);
												}
											}
											newTransitions.put(curTransitionFrom, newTransition);
										}
									}
								}
								aut.transitions = newTransitions;
							}
						}
				}
			}
			{
				Iterator<String> it = aut1.symbolSet.iterator();
				while (it.hasNext()) {
					String symbol = it.next();
					if (!aut.symbolSet.contains(symbol)) 
						{
							aut.symbolSet.add(symbol);
							// fix alphabet
							{
								if (aut.alphabet.isEmpty())
								{
									String newLetter1 = symbol;
									String newLetter2 = "!" + symbol;
									aut.alphabet.add(newLetter1);
									aut.alphabet.add(newLetter2);
								}
								else
								{
									LinkedHashSet<String> newAlphabet = new LinkedHashSet<String>();
									Iterator<String> alphabetIterator = aut.alphabet.iterator();
									while(alphabetIterator.hasNext())
									{
										String replacedLetter = alphabetIterator.next();
										String newLetter1 = replacedLetter + symbol;
										String newLetter2 = replacedLetter + "!" + symbol;
										newAlphabet.add(newLetter1);
										newAlphabet.add(newLetter2);
									}
									aut.alphabet = newAlphabet;
								}
							}
							// fix transitions to use alphabet
							{
								HashMap<State, Transition> newTransitions = new HashMap<State, Transition>();
								Iterator<State> transitionsIterator = aut.transitions.keySet().iterator();
								while(transitionsIterator.hasNext())
								{
									State curTransitionFrom = transitionsIterator.next();
									Transition newTransition = new Transition();
									Transition curTransition = aut.transitions.get(curTransitionFrom);
									Iterator<String> curTransitionIterator = curTransition.keySet().iterator();
									while(curTransitionIterator.hasNext())
									{
										String curSymbol = curTransitionIterator.next();
										LinkedHashSet<State> curTransitionTo = aut.transitions.get(curTransitionFrom).getTransitions(curSymbol);
										Iterator<State> curTransitionToIterator = curTransitionTo.iterator();
										while (curTransitionToIterator.hasNext())
										{
											State curTransitionToState = curTransitionToIterator.next();
											if (curSymbol == "EPSILON")
												{
												newTransition.addTransition("EPSILON", curTransitionToState); 
												}
											else
											{
												String newCurSymbol1 = curSymbol;
												if (curSymbol.contains(symbol))
												{
													newTransition.addTransition(curSymbol, curTransitionToState);
												}
												else
												{
													newCurSymbol1 = curSymbol + symbol;
													if(!(aut.alphabet.contains(newCurSymbol1)))
														{
															newCurSymbol1 = symbol + curSymbol;
														}
													String newCurSymbol2 = curSymbol + "!" + symbol;
													if(!(aut.alphabet.contains(newCurSymbol2)))
													{
														newCurSymbol2 = "!" + symbol + curSymbol;
													}
													newTransition.addTransition(newCurSymbol1, curTransitionToState);
													newTransition.addTransition(newCurSymbol2, curTransitionToState);
												}
											}
											newTransitions.put(curTransitionFrom, newTransition);
										}
									}
								}
								aut.transitions = newTransitions;
							}
						}
				}
			}
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
				Iterator<String> it = aut1.symbolSet.iterator();
				while (it.hasNext()) {
					String symbol = it.next();
					if (!aut.symbolSet.contains(symbol))
						{
							aut.symbolSet.add(symbol);
							// fix alphabet
							{
								if (aut.alphabet.isEmpty())
								{
									String newLetter1 = symbol;
									String newLetter2 = "!" + symbol;
									aut.alphabet.add(newLetter1);
									aut.alphabet.add(newLetter2);
								}
								else
								{
									LinkedHashSet<String> newAlphabet = new LinkedHashSet<String>();
									Iterator<String> alphabetIterator = aut.alphabet.iterator();
									while(alphabetIterator.hasNext())
									{
										String replacedLetter = alphabetIterator.next();
										String newLetter1 = replacedLetter + symbol;
										String newLetter2 = replacedLetter + "!" + symbol;
										newAlphabet.add(newLetter1);
										newAlphabet.add(newLetter2);
									}
									aut.alphabet = newAlphabet;
								}
							}
							// fix transitions to use alphabet
							{
								HashMap<State, Transition> newTransitions = new HashMap<State, Transition>();
								Iterator<State> transitionsIterator = aut.transitions.keySet().iterator();
								while(transitionsIterator.hasNext())
								{
									State curTransitionFrom = transitionsIterator.next();
									Transition newTransition = new Transition();
									Transition curTransition = aut.transitions.get(curTransitionFrom);
									Iterator<String> curTransitionIterator = curTransition.keySet().iterator();
									while(curTransitionIterator.hasNext())
									{
										String curSymbol = curTransitionIterator.next();
										LinkedHashSet<State> curTransitionTo = aut.transitions.get(curTransitionFrom).getTransitions(curSymbol);
										Iterator<State> curTransitionToIterator = curTransitionTo.iterator();
										while (curTransitionToIterator.hasNext())
										{
											State curTransitionToState = curTransitionToIterator.next();
											if (curSymbol == "EPSILON")
												{
												newTransition.addTransition("EPSILON", curTransitionToState); 
												}
											else
											{
												String newCurSymbol1 = curSymbol;
												if (curSymbol.contains(symbol))
												{
													newTransition.addTransition(curSymbol, curTransitionToState);
												}
												else
												{
													newCurSymbol1 = curSymbol + symbol;
													if(!(aut.alphabet.contains(newCurSymbol1)))
														{
															newCurSymbol1 = symbol + curSymbol;
														}
													String newCurSymbol2 = curSymbol + "!" + symbol;
													if(!(aut.alphabet.contains(newCurSymbol2)))
													{
														newCurSymbol2 = "!" + symbol + curSymbol;
													}
													newTransition.addTransition(newCurSymbol1, curTransitionToState);
													newTransition.addTransition(newCurSymbol2, curTransitionToState);
												}
											}
											newTransitions.put(curTransitionFrom, newTransition);
										}
									}
								}
								aut.transitions = newTransitions;
							}
						}
				}
			}
		}
		else if (s.startsWith("NOT")) {
			Automaton2 aut1 = aut.constructAut(((DefaultMutableTreeNode) n.getChildAt(0)));  // This one gets negated (i.e. complemented)
			Automaton2 aut2 = aut.constructAut(((DefaultMutableTreeNode) n.getChildAt(1)));  // 
			
			
			//add state to aut1
			//add transitions from all final states to new state on every member of the alphabet
			//also transitions from the new state to the new state
			//make finalState the new state we added
			
			aut1.deterministic = false;
			
			// Add new final state
			State newSt = new State();
			newSt.num = "s" + stateNum + ":1";
			aut1.addState(newSt);
			
			// set aut1 final states = aut1 and aut2 final states
			{
				Iterator<State> it = aut1.finalSt.iterator();
				while (it.hasNext()) {
					State s1 = it.next();
					Iterator<String> alphaIt = aut1.alphabet.iterator();
					while (alphaIt.hasNext()){
						aut1.addTransition(s1, alphaIt.next(), newSt);
					}
				}
				Iterator<String> alphaIt = aut1.alphabet.iterator();
				while (alphaIt.hasNext()){
					aut1.addTransition(newSt, alphaIt.next(), newSt);
				}
			}
			aut1.finalSt.clear();
			aut1.finalSt.add(newSt);
			
			System.out.println("After adding the new state");
			aut1.display();
			
			
			//Determinize
			aut1 = aut1.determinize(aut1);
			
			
			// Take complement of aut1
			LinkedHashSet<State> prevFinalStates = (LinkedHashSet<State>) aut1.finalSt.clone();
			aut1.finalSt.clear();
			Iterator<State> statesIterator = aut1.states.iterator();
			while (statesIterator.hasNext())
			{
				State curState = statesIterator.next();
				if (!prevFinalStates.contains(curState))
				{
					aut1.finalSt.add(curState);
				}
			}
			
			System.out.println("After swapping states");
			aut1.display();
			
			aut.copy(aut1);
			aut.copy(aut2);
			
			aut1.display();
			aut2.display();
			
			// Take Cartesian product of the states, and of the final states
			// State renaming for Cartesian product?
			
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
			aut.startSt = aut1.getState(aut1.startSt.num);

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
			
			// add symbols from aut1 and aut2 to aut
			{
				Iterator<String> it = aut2.symbolSet.iterator();
				while (it.hasNext()) {
					String symbol = it.next();
					//System.out.print(symbol);
					if (!aut.symbolSet.contains(symbol))
						{
							aut.symbolSet.add(symbol);
							// fix alphabet
							{
								if (aut.alphabet.isEmpty())
								{
									String newLetter1 = symbol;
									String newLetter2 = "!" + symbol;
									aut.alphabet.add(newLetter1);
									aut.alphabet.add(newLetter2);
								}
								else
								{
									LinkedHashSet<String> newAlphabet = new LinkedHashSet<String>();
									Iterator<String> alphabetIterator = aut.alphabet.iterator();
									while(alphabetIterator.hasNext())
									{
										String replacedLetter = alphabetIterator.next();
										String newLetter1 = replacedLetter + symbol;
										String newLetter2 = replacedLetter + "!" + symbol;
										newAlphabet.add(newLetter1);
										newAlphabet.add(newLetter2);
									}
									aut.alphabet = newAlphabet;
								}
							}
							// fix transitions to use alphabet
							{
								//System.out.print(aut.transitions.toString() + "\n");
								HashMap<State, Transition> newTransitions = new HashMap<State, Transition>();
								Iterator<State> transitionsIterator = aut.transitions.keySet().iterator();
								while(transitionsIterator.hasNext())
								{
									State curTransitionFrom = transitionsIterator.next();
									Transition newTransition = new Transition();
									Transition curTransition = aut.transitions.get(curTransitionFrom);
									Iterator<String> curTransitionIterator = curTransition.keySet().iterator();
									while(curTransitionIterator.hasNext())
									{
										String curSymbol = curTransitionIterator.next();
										LinkedHashSet<State> curTransitionTo = aut.transitions.get(curTransitionFrom).getTransitions(curSymbol);
										Iterator<State> curTransitionToIterator = curTransitionTo.iterator();
										while (curTransitionToIterator.hasNext())
										{
											State curTransitionToState = curTransitionToIterator.next();
											if (curSymbol == "EPSILON")
												{
												newTransition.addTransition("EPSILON", curTransitionToState); 
												//continue;
												}
											else
											{
												//System.out.print("curSymbol: " + curSymbol + "   " + "symbol: " + symbol + "\n");
												String newCurSymbol1 = curSymbol;
												if (curSymbol.contains(symbol))
												{
													newTransition.addTransition(curSymbol, curTransitionToState);
													//continue;
												}
												else
												{
													newCurSymbol1 = curSymbol + symbol;
													if(!(aut.alphabet.contains(newCurSymbol1)))
														{
															newCurSymbol1 = symbol + curSymbol;
														}
													String newCurSymbol2 = curSymbol + "!" + symbol;
													if(!(aut.alphabet.contains(newCurSymbol2)))
													{
														newCurSymbol2 = "!" + symbol + curSymbol;
													}
													//System.out.print("adding transition: " + newCurSymbol1 + "\n");
													//System.out.print("adding transition: " + newCurSymbol2 + "\n");
													newTransition.addTransition(newCurSymbol1, curTransitionToState);
													newTransition.addTransition(newCurSymbol2, curTransitionToState);
												}
											}
											newTransitions.put(curTransitionFrom, newTransition);
											//System.out.print(newTransitions.toString() + "\n");
										}
									}
								}
								//System.out.print("Symbol Added, RESULTS: " + "\n");
								//System.out.print(aut.transitions.toString() + "\n");
								aut.transitions = newTransitions;
								//System.out.print(aut.transitions.toString() + "\n");
							}
						}
				}
			}
			{
				Iterator<String> it = aut1.symbolSet.iterator();
				while (it.hasNext()) {
					String symbol = it.next();
					if (!aut.symbolSet.contains(symbol))
						{
							aut.symbolSet.add(symbol);
							// fix alphabet
							{
								if (aut.alphabet.isEmpty())
								{
									String newLetter1 = symbol;
									String newLetter2 = "!" + symbol;
									aut.alphabet.add(newLetter1);
									aut.alphabet.add(newLetter2);
								}
								else
								{
									LinkedHashSet<String> newAlphabet = new LinkedHashSet<String>();
									Iterator<String> alphabetIterator = aut.alphabet.iterator();
									while(alphabetIterator.hasNext())
									{
										String replacedLetter = alphabetIterator.next();
										String newLetter1 = replacedLetter + symbol;
										String newLetter2 = replacedLetter + "!" + symbol;
										newAlphabet.add(newLetter1);
										newAlphabet.add(newLetter2);
									}
									aut.alphabet = newAlphabet;
								}
							}
							// fix transitions to use alphabet
							{
								HashMap<State, Transition> newTransitions = new HashMap<State, Transition>();
								Iterator<State> transitionsIterator = aut.transitions.keySet().iterator();
								while(transitionsIterator.hasNext())
								{
									State curTransitionFrom = transitionsIterator.next();
									Transition newTransition = new Transition();
									Transition curTransition = aut.transitions.get(curTransitionFrom);
									Iterator<String> curTransitionIterator = curTransition.keySet().iterator();
									while(curTransitionIterator.hasNext())
									{
										String curSymbol = curTransitionIterator.next();
										LinkedHashSet<State> curTransitionTo = aut.transitions.get(curTransitionFrom).getTransitions(curSymbol);
										Iterator<State> curTransitionToIterator = curTransitionTo.iterator();
										while (curTransitionToIterator.hasNext())
										{
											State curTransitionToState = curTransitionToIterator.next();
											if (curSymbol == "EPSILON")
												{
												newTransition.addTransition("EPSILON", curTransitionToState); 
												}
											else
											{
												String newCurSymbol1 = curSymbol;
												if (curSymbol.contains(symbol))
												{
													newTransition.addTransition(curSymbol, curTransitionToState);
												}
												else
												{
													newCurSymbol1 = curSymbol + symbol;
													if(!(aut.alphabet.contains(newCurSymbol1)))
														{
															newCurSymbol1 = symbol + curSymbol;
														}
													String newCurSymbol2 = curSymbol + "!" + symbol;
													if(!(aut.alphabet.contains(newCurSymbol2)))
													{
														newCurSymbol2 = "!" + symbol + curSymbol;
													}
													newTransition.addTransition(newCurSymbol1, curTransitionToState);
													newTransition.addTransition(newCurSymbol2, curTransitionToState);
												}
											}
											newTransitions.put(curTransitionFrom, newTransition);
										}
									}
								}
								aut.transitions = newTransitions;
							}
						}
				}
			}
		}
		/*
		else if (s.startsWith("NOT")) {
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
				Iterator<String> it = aut1.symbolSet.iterator();
				while (it.hasNext()) {
					String symbol = it.next();
					if (!aut.symbolSet.contains(symbol))
						{
							aut.symbolSet.add(symbol);
							// fix alphabet
							{
								if (aut.alphabet.isEmpty())
								{
									String newLetter1 = symbol;
									String newLetter2 = "!" + symbol;
									aut.alphabet.add(newLetter1);
									aut.alphabet.add(newLetter2);
								}
								else
								{
									LinkedHashSet<String> newAlphabet = new LinkedHashSet<String>();
									Iterator<String> alphabetIterator = aut.alphabet.iterator();
									while(alphabetIterator.hasNext())
									{
										String replacedLetter = alphabetIterator.next();
										String newLetter1 = replacedLetter + symbol;
										String newLetter2 = replacedLetter + "!" + symbol;
										newAlphabet.add(newLetter1);
										newAlphabet.add(newLetter2);
									}
									aut.alphabet = newAlphabet;
								}
							}
							// fix transitions to use alphabet
							{
								HashMap<State, Transition> newTransitions = new HashMap<State, Transition>();
								Iterator<State> transitionsIterator = aut.transitions.keySet().iterator();
								while(transitionsIterator.hasNext())
								{
									State curTransitionFrom = transitionsIterator.next();
									Transition newTransition = new Transition();
									Transition curTransition = aut.transitions.get(curTransitionFrom);
									Iterator<String> curTransitionIterator = curTransition.keySet().iterator();
									while(curTransitionIterator.hasNext())
									{
										String curSymbol = curTransitionIterator.next();
										LinkedHashSet<State> curTransitionTo = aut.transitions.get(curTransitionFrom).getTransitions(curSymbol);
										Iterator<State> curTransitionToIterator = curTransitionTo.iterator();
										while (curTransitionToIterator.hasNext())
										{
											State curTransitionToState = curTransitionToIterator.next();
											if (curSymbol == "EPSILON")
												{
												newTransition.addTransition("EPSILON", curTransitionToState); 
												}
											else
											{
												String newCurSymbol1 = curSymbol;
												if (curSymbol.contains(symbol))
												{
													newTransition.addTransition(curSymbol, curTransitionToState);
												}
												else
												{
													newCurSymbol1 = curSymbol + symbol;
													if(!(aut.alphabet.contains(newCurSymbol1)))
														{
															newCurSymbol1 = symbol + curSymbol;
														}
													String newCurSymbol2 = curSymbol + "!" + symbol;
													if(!(aut.alphabet.contains(newCurSymbol2)))
													{
														newCurSymbol2 = "!" + symbol + curSymbol;
													}
													newTransition.addTransition(newCurSymbol1, curTransitionToState);
													newTransition.addTransition(newCurSymbol2, curTransitionToState);
												}
											}
											newTransitions.put(curTransitionFrom, newTransition);
										}
									}
								}
								aut.transitions = newTransitions;
							}
						}
				}
			}
		}*/
		else { // symbol
			String symbol = s.substring(0, s.indexOf(':'));
			State s1 = new State();
			s1.num = "s" + stateNum + ":1";
			State s2 = new State();
			s2.num = "s" + stateNum + ":2";
			aut.addState(s1);
			aut.addState(s2);
			aut.addTransition(s1, symbol, s2);
			aut.addTransition(s2, symbol, s2);
			aut.addTransition(s1, neg(symbol), s1);
			aut.addTransition(s2, neg(symbol), s1);
			
			aut.startSt = s1;
			aut.finalSt.add(s2);

			Buffer b = new Buffer();
			b.num = "b" + bufferNum + ":1";
			aut.addBuffer(b);
			aut.addStateBufferLink(s2, b, "prev event");
			aut.startBuf.add(b);
			aut.finalBuf = b;
			
			aut.deterministic = true;
			System.out.print("Deterministic (isSymbol): ");
			System.out.print(aut.deterministic);
			System.out.print("\n");
			aut.symbolSet.add(symbol); // add symbol to symbolSet
			// fix alphabet
			{
				if (aut.alphabet.isEmpty())
				{
					String newLetter1 = symbol;
					String newLetter2 = "!" + symbol;
					aut.alphabet.add(newLetter1);
					aut.alphabet.add(newLetter2);
				}
				else
				{
					LinkedHashSet<String> newAlphabet = new LinkedHashSet<String>();
					Iterator<String> alphabetIterator = aut.alphabet.iterator();
					while(alphabetIterator.hasNext())
					{
						String replacedLetter = alphabetIterator.next();
						String newLetter1 = replacedLetter + symbol;
						String newLetter2 = replacedLetter + "!" + symbol;
						newAlphabet.add(newLetter1);
						newAlphabet.add(newLetter2);
					}
					aut.alphabet = newAlphabet;
				}
			}
			// fix transitions to use alphabet
			{
				HashMap<State, Transition> newTransitions = new HashMap<State, Transition>();
				Iterator<State> transitionsIterator = aut.transitions.keySet().iterator();
				while(transitionsIterator.hasNext())
				{
					State curTransitionFrom = transitionsIterator.next();
					Transition newTransition = new Transition();
					Transition curTransition = aut.transitions.get(curTransitionFrom);
					Iterator<String> curTransitionIterator = curTransition.keySet().iterator();
					while(curTransitionIterator.hasNext())
					{
						String curSymbol = curTransitionIterator.next();
						LinkedHashSet<State> curTransitionTo = aut.transitions.get(curTransitionFrom).getTransitions(curSymbol);
						Iterator<State> curTransitionToIterator = curTransitionTo.iterator();
						while (curTransitionToIterator.hasNext())
						{
							State curTransitionToState = curTransitionToIterator.next();
							if (curSymbol == "EPSILON")
								{
								newTransition.addTransition("EPSILON", curTransitionToState); 
								}
							else
							{
								String newCurSymbol1 = curSymbol;
								if (curSymbol.contains(symbol))
								{
									newTransition.addTransition(curSymbol, curTransitionToState);
								}
								else
								{
									newCurSymbol1 = curSymbol + symbol;
									if(!(aut.alphabet.contains(newCurSymbol1)))
										{
											newCurSymbol1 = symbol + curSymbol;
										}
									String newCurSymbol2 = curSymbol + "!" + symbol;
									if(!(aut.alphabet.contains(newCurSymbol2)))
									{
										newCurSymbol2 = "!" + symbol + curSymbol;
									}
									System.out.print(newCurSymbol1 + "\n");
									System.out.print(newCurSymbol2 + "\n");
									newTransition.addTransition(newCurSymbol1, curTransitionToState);
									newTransition.addTransition(newCurSymbol2, curTransitionToState);
								}
							}
							newTransitions.put(curTransitionFrom, newTransition);
						}
					}
				}
				aut.transitions = newTransitions;
			}
		}
		
		// Cleanup numbering of states and buffers (moved here to reduce code redundancy)
		stateNum ++;
		bufferNum ++;
		
		// Convert to DFA
		//aut.removeEpsilon();
		//aut.determinize(aut);
		
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
	

	
	// return the epsilon closure of the states in a -- all objects are actually states within automaton a
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
		epsClState.add(s);
		boolean modified = true;
		while (modified) {
			modified = false;
			Iterator<State> it = epsClState.iterator();
			while (it.hasNext()) {
				State s1 = it.next();
				// check the transitions of s1 and see if there are any EPSILON transitions
				HashMap<String, LinkedHashSet<State>> trans = a.transitions.get(s1);
				if (!trans.containsKey("EPSILON")) continue;
				LinkedHashSet<State> dest = trans.get("EPSILON");
				Iterator<State> it1 = dest.iterator();
				while (it1.hasNext()) {
					State s2 = it1.next();
					if (!epsClState.contains(s2)) {
						epsClState.add(s2);
						modified = true; // no need to break here because we are not accessing iterator it
					}
				}
				if (modified) break; // iterator it may not work correctly -- I think it still will not matter, but let us not risk it.
			}
		}
		
		return epsClState;
	}
	
	public void display() {
		System.out.print("DISPLAY: " + "\n");
		System.out.print("Symbols: ");
		System.out.print(symbolSet.toString());
		/*Iterator<String> symbolIterator = symbolSet.iterator();
		while(symbolIterator.hasNext())
		{
			System.out.print(symbolIterator.next().toString());
		}*/
		System.out.print("\n");
		System.out.print("Alphabet: ");
		/*Iterator<String> alphabetIterator = alphabet.iterator();
		while(alphabetIterator.hasNext())
		{
			System.out.print(alphabetIterator.next().toString() + "\n");
		}*/
		System.out.print(alphabet.toString());
		System.out.print("\n");
		System.out.print("Deterministic: " + deterministic);
		System.out.print ("\n");
		System.out.print("States : ");
		{
			Iterator<State> it = states.iterator();
			while (it.hasNext()) {
				State st = it.next();
				System.out.print (st.num + " :: ");
			}
			System.out.println ("\n");
		}
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
		
//		System.out.println ("Start State : " + startSt.num);
		
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
			System.out.println ("\n");
		}
		
		System.out.print ("Start Buffers : ");
		{
			Iterator<Buffer> it = startBuf.iterator();
			while (it.hasNext()) {
				Buffer b = it.next();
				System.out.print (b.num + " :: ");
			}
			System.out.println ("\n");
		}

//		System.out.println ("Final Buffer : " + finalBuf.num);
		
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
		System.out.print("END DISPLAY\n");
	}
	
	
	public Automaton2 determinize (Automaton2 a) 
	{
		if (a.deterministic == true)
		{
			return a;
		}
		Automaton2 a2 = new Automaton2();
		LinkedHashSet<LinkedHashSet<State>> stateQueue = new LinkedHashSet<LinkedHashSet<State>>();        // A state may comprise of several states during the 
		LinkedHashSet<LinkedHashSet<State>> stateQueueMarked = new LinkedHashSet<LinkedHashSet<State>>();  // process of determinization
		LinkedHashSet<State> nextStateSet = new LinkedHashSet<State>();                                       //
		
		nextStateSet.add(a.startSt);                                                                          // Start with the start state
		stateQueue.add(nextStateSet);                                                                         // Start with having the start state in the queue
		a2.states.add(a.startSt);                                                                          // Add this to the states of the determinized automaton
		HashMap<State,LinkedHashSet<State>> epsCl = a.epsilonClosure(a);                                   // used to find epsilon closure of states of automaton a
		
		while(!stateQueue.isEmpty())
		{
			System.out.print("\n Queue Contains: \n");
			Iterator<LinkedHashSet<State>> stateQueueIterator = stateQueue.iterator();
			while (stateQueueIterator.hasNext())
			{
				System.out.print("Set: \n");
				LinkedHashSet<State> currentStateSet = stateQueueIterator.next();
				Iterator<State> currentStateIterator = currentStateSet.iterator();
				while (currentStateIterator.hasNext())
				{
					State currentState = currentStateIterator.next();
					System.out.print("      " + currentState.num + "\n");
				}
			}
			System.out.print("\n Queue Marked Contains: \n");
			Iterator<LinkedHashSet<State>> stateQueueMarkedIterator = stateQueueMarked.iterator();
			while (stateQueueMarkedIterator.hasNext())
			{
				System.out.print("Set: \n");
				LinkedHashSet<State> currentStateSet = stateQueueMarkedIterator.next();
				Iterator<State> currentStateIterator = currentStateSet.iterator();
				while (currentStateIterator.hasNext())
				{
					State currentState = currentStateIterator.next();
					System.out.print("      " + currentState.num + "\n");
				}
			}
			System.out.print("\n");
			
			stateQueue.remove(nextStateSet);
			stateQueueMarked.add(nextStateSet);
			
			LinkedHashSet<State> epsCl1 = new LinkedHashSet<State>();
			LinkedHashSet<State> epsClTemp = new LinkedHashSet<State>();
			Iterator<State> curStateIterator = nextStateSet.iterator();
			while (curStateIterator.hasNext())
			{
				State curState = curStateIterator.next();
				epsClTemp = epsCl.get(curState);
				Iterator<State> epsClTempIterator = epsClTemp.iterator();
				while (epsClTempIterator.hasNext())
				{
					State epsClCurrentState = epsClTempIterator.next();
					if (!epsCl1.contains(epsClCurrentState))                          // Double Check this "contains" is working later
					{
						epsCl1.add(epsClCurrentState);
					}
				}
			}
			
			
			Iterator<String> alphSymbols = a.alphabet.iterator();
			while (alphSymbols.hasNext())
			{
				LinkedHashSet<State> unionofTransitions = new LinkedHashSet<State>();
				LinkedHashSet<State> finalState = new LinkedHashSet<State>();
				String curSymbol = alphSymbols.next();
				Iterator<State> epsCl1Iterator = epsCl1.iterator();
				while (epsCl1Iterator.hasNext())
				{
					State stateToTransFrom = epsCl1Iterator.next();
					Transition curTransition = a.transitions.get(stateToTransFrom);
					//System.out.print(stateToTransFrom.num);
					//System.out.print(curTransition);
					//System.out.print("State: " + stateToTransFrom.num + "\n");
					//System.out.print("curSymbol: " + curSymbol + "\n");
					LinkedHashSet<State> statesTransitionedTo = curTransition.getTransitions(curSymbol);
					Iterator<State> statesTransitionedToIterator = statesTransitionedTo.iterator();
					while (statesTransitionedToIterator.hasNext())
					{
						State curStateTransitionedTo = statesTransitionedToIterator.next();
						if (!unionofTransitions.contains(curStateTransitionedTo))
						{
							unionofTransitions.add(curStateTransitionedTo);
							//System.out.print(curStateTransitionedTo.num + " :: ");
						}
					}
					Iterator<State> unionofTransitionsIterator = unionofTransitions.iterator();
					while (unionofTransitionsIterator.hasNext())
					{
						State epsClThisState = unionofTransitionsIterator.next();
						LinkedHashSet<State> epsCl2 = epsCl.get(epsClThisState);
						Iterator<State> epsCl2Iterator = epsCl2.iterator();
						while (epsCl2Iterator.hasNext())
						{
							State epsCl2State = epsCl2Iterator.next();
							if (!finalState.contains(epsCl2State))
							{
								finalState.add(epsCl2State);
							}
						}
					}
				}
				Iterator<State> finalStateIterator = finalState.iterator();
				String newStateNum = "";
				LinkedHashSet<State> newStateSet = new LinkedHashSet<State>();
				while (finalStateIterator.hasNext())
				{
					State finalStateNumState = finalStateIterator.next();
					String finalStateNum = finalStateNumState.num;
					if (newStateNum.compareTo(finalStateNum) < 0)
					{
						newStateNum = newStateNum + finalStateNum;
					}
					else
					{
						newStateNum = finalStateNum + newStateNum;
					}
					newStateSet.add(finalStateNumState);
				}
				State newState = new State();
				newState.num = newStateNum;
				
				Iterator<State> findStateWithNewStateNumIterator = a2.states.iterator();
				while (findStateWithNewStateNumIterator.hasNext())
				{
					State curState = findStateWithNewStateNumIterator.next();
					if (newState.num.equals(curState.num))
					{
						newState = curState;
					}
				}
				
				if (!stateQueue.contains(newStateSet) && !stateQueueMarked.contains(newStateSet))
				{
					stateQueue.add(newStateSet);
					a2.states.add(newState);
					System.out.print("Added State " + newState.num + " to the queue\n");
					Iterator<State> newStateSetIterator = newStateSet.iterator();
					while (newStateSetIterator.hasNext())
					{
						State curNewState = newStateSetIterator.next();
						if (a.finalSt.contains(curNewState))
						{
							a2.finalSt.add(newState);
						}
					}
				}
				
				// Add new transitions
				Transition newTransition = new Transition();
				newTransition.addTransition(curSymbol, newState);
				String nextStateSetStates = "";
				Iterator<State> nextStateSetIterator = nextStateSet.iterator();
				while (nextStateSetIterator.hasNext())
				{
					State addState = nextStateSetIterator.next();
					nextStateSetStates = nextStateSetStates + addState.num;
				}
				//State fromThisState = new State();
				//fromThisState.num = nextStateSetStates;
				
				State fromThisState = new State();
				fromThisState.num = nextStateSetStates;
				Iterator<State> findStateWithNumIterator = a2.states.iterator();
				while (findStateWithNumIterator.hasNext())
				{
					State curFromThisState = findStateWithNumIterator.next();
					if (curFromThisState.num.equals(nextStateSetStates))
					{
						fromThisState = curFromThisState;
					}
				}
				if (!a2.transitions.containsKey(fromThisState))
				{
					a2.transitions.put(fromThisState, newTransition);
				}
				else
				{
					Transition curTransition = a2.transitions.get(fromThisState);
					curTransition.addTransition(curSymbol, newState);
				}
			}
			
			Iterator<LinkedHashSet<State>> nextStatePicker = stateQueue.iterator();
			if (nextStatePicker.hasNext())
			{
				nextStateSet = nextStatePicker.next();
			}			
		}
		
		// fix state/buffer mapping
		Iterator<State> a2StatesIterator = a2.states.iterator();
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
				if (a2State.num.contains(linkedState.num))
				{
					LinkedHashSet<Buffer> linkedBuffers = a.stateBufferLink.get(linkedState);
					Iterator<Buffer> linkedBuffersIterator = linkedBuffers.iterator();
					while (linkedBuffersIterator.hasNext())
					{
						Buffer curBuffer = linkedBuffersIterator.next();
						allLinkedBuffers.add(curBuffer);
					}
				}
			}
			a2.stateBufferLink.put(a2State, allLinkedBuffers);
		}
		
		//a2.finalStInt = a.finalStInt;  // Is not being maintained, so we don't need to copy it here
		//a2.bufferNum = a.bufferNum;    // not sure that they would need to be copied, 
		//a2.stateNum = a.stateNum;      // also get a warning saying they should be accessed in a static way
		a2.symbolSet = (LinkedHashSet<String>) a.symbolSet.clone();
		a2.alphabet = (LinkedHashSet<String>) a.alphabet.clone();
		a2.buffers = (LinkedHashSet<Buffer>) a.buffers.clone();
		a2.startBuf = (LinkedHashSet<Buffer>) a.startBuf.clone();
		a2.finalBuf = a.finalBuf;  // is this a deep copy?
		//a2.finalBuf.num = a.finalBuf.num;
		//a2.finalBuf.actions = (LinkedHashSet<String>) a.finalBuf.actions.clone();
		//a2.finalBuf.tag = (LinkedHashSet<String>) a.finalBuf.tag.clone();
		a2.deterministic = true;

		
		a2 = a2.fixStatesForFutureUse(a2);
		//...need to fix transitions...again, nope, all fixed now, fixing the states to keep using the same ones fixed this as well
		a2.display();
		return a2;
	}
	
	
	
	public Automaton2 fixStatesForFutureUse (Automaton2 a) 
	{
		int length = 0;
		Iterator<State> statesIterator = a.states.iterator();
		while (statesIterator.hasNext())
		{
			State curState = statesIterator.next();
			String curString = curState.num;
			int i = 0;
			int j = 0;
			while (curString.indexOf('s', j) != -1)
			{
				j = curString.indexOf('s', j) + 1;
				i++;
			}
			if (i > length)
			{
				length = i;
			}
			//System.out.print(i);
		}
		statesIterator = a.states.iterator();
		while (statesIterator.hasNext())
		{
			State curState = statesIterator.next();
			String curString = curState.num;
			String searchString = curString;
			int i = 0;
			int j = 0;
			while (curString.indexOf('s', j) != -1)
			{
				j = curString.indexOf('s', j) + 1;
				i++;
			}
			while (i < length)
			{
				curString = "s0:0" + curString;
				i++;
			}
			curState.num = curString;
			//System.out.print(curString);
		}		
		return a;
	}
}
