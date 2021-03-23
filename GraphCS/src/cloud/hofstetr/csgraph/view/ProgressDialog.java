package cloud.hofstetr.csgraph.view;

import java.awt.BorderLayout;

import javax.swing.GroupLayout;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JProgressBar;

public class ProgressDialog extends JDialog {
	private static final long serialVersionUID = 8727700150613116574L;
	private JProgressBar Bar;
	
	public ProgressDialog() {
		JPanel progressPanel = new JPanel();
		GroupLayout groupLayout = new GroupLayout(progressPanel);
		groupLayout.setAutoCreateGaps(true);
		progressPanel.setLayout(groupLayout);
		
		Bar = new JProgressBar();
		Bar.setValue(0);
		Bar.setMaximum(100);
		
		groupLayout.setHorizontalGroup(groupLayout.createSequentialGroup()
		        .addGroup(groupLayout.createParallelGroup(GroupLayout.Alignment.TRAILING)
		                .addComponent(Bar)));
		
		groupLayout.setVerticalGroup(groupLayout.createSequentialGroup()
		        .addGroup(groupLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
		                .addComponent(Bar)));
		
		this.setTitle("Progress");
		this.getContentPane().add(progressPanel, BorderLayout.CENTER);
		this.pack();
		//this.setModal(true);
		this.setVisible(true);
	}
	
	public JProgressBar getBar() {
		return Bar;
	}
}
