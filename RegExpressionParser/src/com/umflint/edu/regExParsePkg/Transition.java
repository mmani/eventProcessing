package com.umflint.edu.regExParsePkg;

import java.util.HashMap;
import java.util.LinkedHashSet;

public class Transition extends HashMap<String, LinkedHashSet<State>> {
	
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
