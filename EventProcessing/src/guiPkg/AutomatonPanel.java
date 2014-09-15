package guiPkg;

import java.awt.GridBagLayout;
import javax.swing.*;

public class AutomatonPanel extends JPanel {

	private static final long serialVersionUID = 1L;

	/**
	 * This is the default constructor
	 */
	public AutomatonPanel() {
		super();
		initialize();
	}

	/**
	 * This method initializes this
	 * 
	 * @return void
	 */
	private void initialize() {
		this.setSize(300, 200);
		this.setLayout(new GridBagLayout());
		this.add(new JLabel("Automaton with Buffers"));
	}

}
