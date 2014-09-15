package guiPkg;

import java.awt.*;
import javax.swing.*;

public class InputPanel extends JPanel {

	private static final long serialVersionUID = 1L;
	private JTextArea queryText;
	private JTextField inputText;
	private JTextField outputText;
	
	private JButton submitBtn;

	/**
	 * This is the default constructor
	 */
	public InputPanel() {
		super();
		initialize();
	}

	/**
	 * This method initializes this
	 * 
	 * @return void
	 */
	private void initialize() {
		this.setSize(600, 300);
		this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		
		queryText = new JTextArea("Enter your query", 4, 40);
		JScrollPane jScrollPane = new JScrollPane(queryText);
		this.add(jScrollPane);
		
		inputText = new JTextField("Stream Src", 40);
		this.add(inputText);

		outputText = new JTextField("Output Loc", 40);
		this.add(outputText);
		
		submitBtn = new JButton("Submit");
		// submitBtn.setMaximumSize(new Dimension(1,1));
		submitBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
		this.add(submitBtn);
		
	}

}
