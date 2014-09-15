package queryParsePkg;

import java.util.Vector;
//import regExParsePkg.SimpleNode;
//import regExParsePkg.regExpGrammar;

public class queryParseTest {

	/**
	 * @param args
	 */
	public static void main(String[] args) throws Exception {
		// TODO Auto-generated method stub
		Vector<String> selectVector = new Vector<String>();
		StringBuilder inputStream = new StringBuilder();
		StringBuilder patternString = new StringBuilder();
		Vector<String> predVector = new Vector<String>();
		StringBuilder windowSize = new StringBuilder();
		StringBuilder windowType = new StringBuilder();
		
		java.io.StringReader sr = new java.io.StringReader( "SELECT abc, xbs1, * from stream1.xml as pattern (a1, b, (d, e+ : z)) where ab != cv1 and cd1 = kv and tc > fgj and tm < tpq and tm >= jkj and tr <= kpl WITHIN 10 events" );
		java.io.Reader r = new java.io.BufferedReader( sr );
		queryGrammar parser = new queryGrammar (r);
		parser.Start(selectVector, inputStream, patternString, predVector, windowSize, windowType);
		
		for (int i = 0; i < selectVector.size(); i++) {
			System.out.println (selectVector.elementAt(i));
		}

		System.out.println ("input stream = " + inputStream.toString());
		
		System.out.println ("pattern string = " + patternString.toString());
		
		for (int i = 0; i < predVector.size(); i++) {
			System.out.println (predVector.elementAt(i));
		}
		
		System.out.println ("window size = " + windowSize.toString());
		System.out.println ("window type = " + windowType.toString());
	}

}
