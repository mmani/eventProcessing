package guiPkg;

import java.awt.*;
import javax.swing.JPanel;

public class demoPanel extends JPanel {

	private static final long serialVersionUID = 1L;
	private InputPanel demoInputPanel;
	private AutomatonPanel automatonPanel;
	private PlanPanel planPanel;

	/**
	 * This is the default constructor
	 */
	public demoPanel() {
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
		this.setName("Demo");
		
		demoInputPanel = new InputPanel();
		this.add(demoInputPanel, BorderLayout.LINE_START);
		
		automatonPanel = new AutomatonPanel();
		this.add(automatonPanel, BorderLayout.CENTER);
		
		planPanel = new PlanPanel();
		this.add(planPanel, BorderLayout.LINE_END);
		
	}

}
