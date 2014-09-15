package regExParsePkg2;

import java.io.*;
import java.util.concurrent.*;

public class OutputOperator implements Runnable {
	BufferedWriter outputFile;
	ConcurrentLinkedQueue<String> inputQ;
	
	public OutputOperator(ConcurrentLinkedQueue<String> i, BufferedWriter o) {
		inputQ = i;
		outputFile = o;
	}
	
	public void run() {
		try {
			while (true) {
				if (inputQ.isEmpty()) {
					continue;
				}
				String next = inputQ.remove();
				if (next.equals("EOF")) {
					// we are done..
					try {
						// outputFile.close();
					} catch (Exception e) {
						e.printStackTrace();
					}
					return;
				}
				/* outputFile.write(next);
				outputFile.newLine(); */
			}
		} catch(Exception e) {
			e.printStackTrace();
		} 
	}
}
