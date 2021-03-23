package cloud.hofstetr.csgraph.view;

import javax.swing.JDialog;
import javax.swing.JProgressBar;

public class ProgressDialog extends JDialog {
	private static final long serialVersionUID = 8727700150613116574L;
	private JProgressBar Bar;
	
	public ProgressDialog() {
		Bar = new JProgressBar();
		Bar.setValue(0);
	}
	
	public JProgressBar getBar() {
		return Bar;
	}
}
