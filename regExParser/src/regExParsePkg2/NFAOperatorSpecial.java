package regExParsePkg2;

import java.util.*;
import java.util.concurrent.ConcurrentLinkedQueue;

public class NFAOperatorSpecial implements Runnable {
	
	ConcurrentLinkedQueue<String> inputQ;
	ConcurrentLinkedQueue<String> outputQ;
	Automaton2 aut;
	HashSet<State> currStateSet = new HashSet<State>();
	int window;

	public NFAOperatorSpecial(ConcurrentLinkedQueue<String> i, ConcurrentLinkedQueue<String> o, Automaton2 a, int window) {
		inputQ = i;
		outputQ = o;
		aut = a;
		currStateSet.add (a.startSt);
		this.window = window;
	}
		
	public String extendSym(String s) {
		String retVal = "";

		Iterator<String> it = aut.sortedAlph.iterator();
		while (it.hasNext()) {
			String t = it.next();
			if (s.equals(t)) retVal = retVal + t + ":";
			else retVal = retVal + "!" + t + ":";
		}
		// System.out.println ("extended " + s + " to " + retVal);
		return retVal;
	}

	//LinkedHashSet<Buffer> bufferSet = new LinkedHashSet<Buffer>();
	LinkedHashMap<String,LinkedList<String>> bufferSet = new LinkedHashMap<String,LinkedList<String>>();
	LinkedHashSet<String> doneList = new LinkedHashSet<String>();
	LinkedHashMap<State,LinkedList<String>> stateActionOrder = new LinkedHashMap<State,LinkedList<String>>(); // has order of buffer actions for the state been determined??

	// assume that the input first has the symbol and then space and then attributes
	public void run() {
		while (true) { // do as long as there are elements in inputQ -- they may come in future..
			if (inputQ.isEmpty()) {
				continue;
			}
			String next = inputQ.remove();
			if (next.equals("EOF")) { // EOF indicates end of input
				outputQ.add("EOF");
				break; // done with the loop
			}
			// System.out.println(next);
			String event1 = next.substring(0, next.indexOf(' '));
			String event = extendSym(event1);

			doneList.clear();
			
			HashSet<State> nextState = new HashSet<State>();
			// for each state in currState, get the set of transitions.
			epsClosure(currStateSet);
			Iterator<State> currIt = currStateSet.iterator();
			while (currIt.hasNext()) {
				State st1 = currIt.next();
				if (aut.transitions.get(st1).get(event) == null) continue;
				Iterator<State> stIt = aut.transitions.get(st1).get(event).iterator();
				while (stIt.hasNext()) {
					State st = stIt.next();
					if (!nextState.contains(st)) nextState.add(st);
				}
			}
			epsClosure(nextState);
			if (nextState.size() == 0) { // no transitions defined.. should not happen
				System.err.println ("no transitions defined for state on seeing symbol " + event);
				outputQ.add("EOF");
				// notify();
				return;
			}

			currStateSet = nextState;
			
			HashSet<String> doneActions = new HashSet<String>();
			
			for (State currState: currStateSet) {
				LinkedHashSet<Buffer> curBufferSet = aut.stateBufferLink.get(currState);
				if ((curBufferSet == null) || (curBufferSet.isEmpty())) continue; // no buffers for the state -- so continue
				
				LinkedList<Buffer> curBufferList = new LinkedList<Buffer>();
				curBufferList.addAll(curBufferSet);
				Collections.sort(curBufferList);
				
				Iterator<Buffer> bufferIt = curBufferList.iterator();  //iterate through the buffers given by the automaton for the state
				Buffer curBuffer;
				while (bufferIt.hasNext()) // fill all prev event buffers first
				{
					curBuffer = bufferIt.next();
					if (!bufferSet.containsKey(curBuffer.num))
					//if (newBuffer == true)                                           // add the automaton buffer to the actual buffers
					{
						LinkedList<String> newLinkedList = new LinkedList<String>();
						String newString = curBuffer.num;
						bufferSet.put(newString, newLinkedList);
					}
					LinkedList<String> destBuffer = bufferSet.get(curBuffer.num);

					//fetch the actual buffer and carry out the actions
					// LinkedList<String> curRealBuffer = bufferSet.get(curBuffer.num);
					if (curBuffer.actions.contains("prev event"))
					{
						String s = next.substring(next.indexOf("id1 = ") + 6);
						clearBuffer(destBuffer, s);
						if (!doneActions.contains("prev event")) destBuffer.add(s);
						doneActions.add("prev event");
						doneList.add(curBuffer.num);
					}
					LinkedHashSet<String> curActions = curBuffer.actions;
					Iterator<String> it1 = curActions.iterator();
					while (it1.hasNext()) {
						String curAction = it1.next();
						if (curAction.equals("prev event")) continue;
						if (doneActions.contains(curAction)) continue;
						doneActions.add(curAction);

						if (!curAction.contains("*")) {
							// if (!doneList.contains(curAction)) System.out.println ("UNEXPECTED NOT DONE BUFFER ENCOUNTERED -- 1");
							LinkedList<String> srcBuffer = bufferSet.get(curAction);
							if (srcBuffer == null) continue;
							Iterator<String> it2 = srcBuffer.iterator();
							while (it2.hasNext()) {
								String item = it2.next();
								if (!destBuffer.contains(item)) {
									clearBuffer(destBuffer, item);
									boolean flag = checkWindow(item);
									if (flag) {
										destBuffer.add(item);
										if (curBuffer.num == aut.finalBuf.num) outputQ.add(item);
									}
								}
							}
						}
						else { // it is *
							String b1 = curAction.substring(0, curAction.indexOf(" * "));
							String b2 = curAction.substring(curAction.indexOf(" * ") + 3);
							// if (!doneList.contains(b1)) System.out.println ("UNEXPECTED NOT DONE BUFFER ENCOUNTERED -- 2");
							if (!b2.equals(curBuffer.num) && (!doneList.contains(b2))) System.out.println ("UNEXPECTED NOT DONE BUFFER ENCOUNTERED -- 3");
							LinkedList<String> events1 = (LinkedList<String>)bufferSet.get(b1).clone();
							LinkedList<String> events2;
							Iterator<String> it2 = events1.iterator();
							while (it2.hasNext()) {
								String el1 = it2.next();
								String [] el1Arr = el1.split(":");
								events2 = (LinkedList<String>)bufferSet.get(b2).clone();
								Iterator<String> it3 = events2.iterator();
								while (it3.hasNext()) {
									String el2 = it3.next();
									boolean formRes = true;
									
									// find the latest timestamp among elements in el1Arr = t1
									// find the earliest timeStamp among elements in el2Arr = t2
									// form a result only when t1 < t2
									// note this also ensures the same event is not in el1Arr and el2Arr

									String pattern = "[^0-9]+";
									String s[] = el1.split(pattern);
									int max = 0, min = 0, max2 = 0, curr;
									for (String s1: s){
										if (s1.equals("")) continue;
										curr = Integer.parseInt(s1);
										max = (curr > max ? curr : max);
									}
									String s2[] = el2.split(pattern);
									min = 0;
									for (String s1: s2){
										if (s1.equals("")) continue;
										curr = Integer.parseInt(s1);
										if (min == 0) { min = curr; }
										min = (curr < min ? curr : min);
										max2 = (curr > max2 ? curr : max2);									
									}
									if (max >= min) formRes = false;
									
									// also check max2 has timestamp = current event (called next) timestamp
									String s3[] = next.split(pattern);
									int currTime = Integer.parseInt(s3[1]);
									if (currTime > max2) formRes = false;
									
									if (formRes) {
										String item = el1 + ":" + el2;
										clearBuffer(destBuffer, item);
										// destBuffer.add(item);
										boolean flag = checkWindow(item);
										if (flag) {
											destBuffer.add(item);
											if (curBuffer.num == aut.finalBuf.num) outputQ.add(item);
										}
									}
								}
							}
						}
					}
					doneList.add(curBuffer.num);
				}
			}
			// do the custom code here..
			// if nextState has one of 5:2, 5:4, 5:5, 5:7 clear buffer b1:1
			boolean clear = false;
			for (State s : nextState) {
				if (s.num.equals("s5:2")) clear = true;
				else if (s.num.equals("s5:3")) clear = true;
				else if (s.num.equals("s5:5")) clear = true;
				else if (s.num.equals("s5:6")) clear = true;
			}
			if (clear) bufferSet.get("b1:1").clear();
		}
	}
	
	public void clearBuffer(LinkedList<String> buffer, String item) {
		String pattern = "[^0-9]+";
		String s[] = item.split(pattern);
		int max = 0, min = 0, curr;
		for (String s1: s){
			if (s1.equals("")) continue;
			curr = Integer.parseInt(s1);
			max = (curr > max ? curr : max);
		}
		LinkedList<String> bClone = (LinkedList<String>) buffer.clone();
		for (String bItem: bClone) {
			String s2[] = bItem.split(pattern);
			min = 0;
			for (String s1: s2){
				if (s1.equals("")) continue;
				curr = Integer.parseInt(s1);
				if (min == 0) { min = curr; }
				min = (curr < min ? curr : min);
			}
			if ((max - min) > window) buffer.remove();
		}
	}
	
	public boolean checkWindow(String item) {
		String pattern = "[^0-9]+";
		String s[] = item.split(pattern);
		int max = 0, min = 0, curr;
		for (String s1: s){
			if (s1.equals("")) continue;
			curr = Integer.parseInt(s1);
			if (max == 0) { max = curr; min = curr; }
			max = (curr > max ? curr : max);
			min = (curr < min ? curr : min);
		}
		return (!((max - min) > window));
	}

	public void epsClosure(HashSet<State> stateSet) {
		boolean found = false;
		if (stateSet.isEmpty()) return;
		HashSet<State> stateSet1 = (HashSet<State>) stateSet.clone();
		for (State s: stateSet1) {
			HashSet<State> destSts = aut.transitions.get(s).get("EPSILON");
			if ((destSts == null) || (destSts.isEmpty())) continue;
			else {
				for (State s1: destSts) {
					if (stateSet.contains(s1)) continue;
					else {
						stateSet.add(s1);
						found = true;
					}
				}
			}
		}
		if (found) epsClosure(stateSet);
		return;
	}
}
