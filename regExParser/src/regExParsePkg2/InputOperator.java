package regExParsePkg2;

import java.util.concurrent.*;
import java.io.*;

public class InputOperator implements Runnable {

	BufferedReader inputFile;
	ConcurrentLinkedQueue<String> outputQ;
	
	public InputOperator(BufferedReader i, ConcurrentLinkedQueue<String> o) {
		inputFile = i;
		outputQ = o;
	}
	
	public void run() {
		try {
			while (true) {
				String next = inputFile.readLine();
				if (next == null) { // EOF reached
					outputQ.add("EOF");
					// notify();
					return;
				}
				outputQ.add(next);
				// notify();
			}
		} catch(IOException e) {
			e.printStackTrace();
		}
	}

}
