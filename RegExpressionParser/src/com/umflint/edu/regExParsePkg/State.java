package com.umflint.edu.regExParsePkg;

import java.util.LinkedHashSet;

public class State {
	public String num; // the state number
	public LinkedHashSet<String> tag=new LinkedHashSet<String>(); 
	// this is the list of buffers associated with a state
}
