package regExParsePkg;

public class regExParserTest {

	/**
	 * @param args
	 */
	public static void main(String[] args) throws ParseException {
		// TODO Auto-generated method stub
		// regExpGrammar parser = new regExpGrammar(System.in);
		java.io.StringReader sr = new java.io.StringReader( "!(a,b),c" );
		java.io.Reader r = new java.io.BufferedReader( sr );
		regExpGrammar parser = new regExpGrammar(r);
		SimpleNode n = parser.Start();
		n.dump(">");
		regExParserTest t = new regExParserTest();
	    String s = t.recursivePrintValue (n, 0);
	    System.out.println ("expr = " + s);
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
