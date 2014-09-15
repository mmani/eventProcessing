package regExParsePkg2;

import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.concurrent.ConcurrentLinkedQueue;

public class DFAOperatorSpecial implements Runnable {
	
	ConcurrentLinkedQueue<String> inputQ;
	ConcurrentLinkedQueue<String> outputQ;
	Automaton2 aut;
	State currState;
	int window;
	
	public DFAOperatorSpecial(ConcurrentLinkedQueue<String> i, ConcurrentLinkedQueue<String> o, Automaton2 a, int window) {
		inputQ = i;
		outputQ = o;
		aut = a;
		currState = a.startSt;
		this.window = window;
	}

	public String extendSym(String s) {
		String retVal = "";
		
		Iterator<String> it = aut.sortedAlph.iterator();
/*		{
			String t = it.next();
			if (s.equals(t)) retVal = retVal + t;
			else retVal = retVal + "!" + t;
		}
		
*/		while (it.hasNext()) {
			String t = it.next();
			if (s.equals(t)) retVal = retVal + t + ":";
			else retVal = retVal + "!" + t + ":";
		}
		// System.out.println ("extended " + s + " to " + retVal);
		return retVal;
	}

	//@Override               //??????
	
	//LinkedHashSet<Buffer> bufferSet = new LinkedHashSet<Buffer>();
	LinkedHashMap<String,LinkedList<String>> bufferSet = new LinkedHashMap<String,LinkedList<String>>();
	LinkedHashSet<String> doneList = new LinkedHashSet<String>();
	LinkedHashMap<State,LinkedList<String>> stateActionOrder = new LinkedHashMap<State,LinkedList<String>>(); // has order of buffer actions for the state been determined??
	
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
			
			Iterator<State> stIt = aut.transitions.get(currState).get(event).iterator();
			if (!stIt.hasNext()) { // no transitions defined.. should not happen
				System.err.println ("no transitions defined for state " + currState.num + " on seeing symbol " + event);
				outputQ.add("EOF");
				break;
			}

			// System.out.print("Transitioning from " + currState.num + " on " + event + " to ");
			currState = stIt.next();
			// System.out.println(currState.num);
			
			LinkedHashSet<Buffer> curBufferSet = aut.stateBufferLink.get(currState);
			if ((curBufferSet == null) || (curBufferSet.isEmpty())) { // no buffers for the state -- so continue
				boolean clear = false;
				{
					State s = currState;
					if (s.num.equals("s7:4")) clear = true;
					else if (s.num.equals("s7:5")) clear = true;
					else if (s.num.equals("s7:8")) clear = true;
					else if (s.num.equals("s7:9")) clear = true;
					else if (s.num.equals("s7:12")) clear = true;
					else if (s.num.equals("s7:13")) clear = true;
					else if (s.num.equals("s7:16")) clear = true;
					else if (s.num.equals("s7:17")) clear = true;
					else if (s.num.equals("s7:19")) clear = true;
					else if (s.num.equals("s7:20")) clear = true;
					else if (s.num.equals("s7:23")) clear = true;
					else if (s.num.equals("s7:24")) clear = true;
				}
				if (clear) bufferSet.get("b1:1").clear();
				continue;
			}
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
					destBuffer.add(s);
					if (curBuffer.num == aut.finalBuf.num) outputQ.add(s);
					doneList.add(curBuffer.num);
				}
				LinkedHashSet<String> curActions = curBuffer.actions;
				Iterator<String> it1 = curActions.iterator();
				while (it1.hasNext()) {
					String curAction = it1.next();
					if (curAction.equals("prev event")) continue;

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
						LinkedList<String> events1 = bufferSet.get(b1);
						LinkedList<String> events2;
						Iterator<String> it2 = events1.iterator();
						while (it2.hasNext()) {
							String el1 = it2.next();
							String [] el1Arr = el1.split(":");
							events2 = bufferSet.get(b2);
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
			// System.out.println ("final buffer = " + bufferSet.get(aut.finalBuf.num));
			boolean clear = false;
			{
				State s = currState;
				if (s.num.equals("s7:4")) clear = true;
				else if (s.num.equals("s7:5")) clear = true;
				else if (s.num.equals("s5:8")) clear = true;
				else if (s.num.equals("s5:9")) clear = true;
				else if (s.num.equals("s7:12")) clear = true;
				else if (s.num.equals("s5:13")) clear = true;
				else if (s.num.equals("s5:16")) clear = true;
				else if (s.num.equals("s7:17")) clear = true;
				else if (s.num.equals("s5:19")) clear = true;
				else if (s.num.equals("s5:20")) clear = true;
				else if (s.num.equals("s5:23")) clear = true;
				else if (s.num.equals("s5:24")) clear = true;
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
}