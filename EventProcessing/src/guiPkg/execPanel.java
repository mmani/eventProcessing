package guiPkg;

import java.awt.*;
import javax.swing.JPanel;

public class execPanel extends JPanel {

	private static final long serialVersionUID = 1L;
	private InputPanel execInputPanel;

	/**
	 * This is the default constructor
	 */
	public execPanel() {
		super();
		initialize();
	}

	/**
	 * This method initializes this
	 * 
	 * @return void
	 */
	private void initialize() {
		this.setSize(400, 250);
		this.setLayout(new BorderLayout());
		this.setName("Execute");
		
		execInputPanel = new InputPanel();
		this.add(execInputPanel, BorderLayout.LINE_START);
	}

}
