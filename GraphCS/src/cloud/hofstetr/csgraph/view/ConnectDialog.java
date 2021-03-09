package cloud.hofstetr.csgraph.view;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.event.ActionListener;

import javax.swing.GroupLayout;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;

public class ConnectDialog extends JDialog {
	private static final long serialVersionUID = 8727700150613116574L;
	private ActionListener buttonListener;
	private JTextField dispatcherField;
	private JTextField namespaceField;
	private JTextField useridField;
	private JPasswordField passwordField;
	
	public ConnectDialog() {
	}
	
	public void addButtonListener(ActionListener listener) {
		this.buttonListener = listener;
	}
	
	public void init(String dispatcher, String namespace, String userid, String password) {
		JPanel connectPanel = new JPanel();
		
		GroupLayout groupLayout = new GroupLayout(connectPanel);
		groupLayout.setAutoCreateGaps(true);
		connectPanel.setLayout(groupLayout);
		
		// Create the dispatcher text field
		dispatcherField = new JTextField(50);
		dispatcherField.setText(dispatcher);
		JLabel dispatcherLabel = new JLabel("Dispatcher URL:");
		dispatcherLabel.setLabelFor(dispatcherField);
		
		// Create the name space text field
		namespaceField = new JTextField(50);
		namespaceField.setText(namespace);
		JLabel namespaceLabel = new JLabel("Name Space ID:");
		namespaceLabel.setLabelFor(namespaceField);
		
		// Create the user id text field
		useridField = new JTextField(50);
		useridField.setText(userid);
		JLabel useridLabel = new JLabel("Super User ID:");
		useridLabel.setLabelFor(useridField);
		
		// Create the password field
		passwordField = new JPasswordField(50);
		passwordField.setText(password);
		JLabel passwordLabel = new JLabel("Password:");
		dispatcherLabel.setLabelFor(passwordField);
		
		JPanel buttonPanel = new JPanel();

		JButton okayButton = new JButton("OK");
		JButton cancelButton = new JButton("Cancel");

		okayButton.setActionCommand("okay");
		cancelButton.setActionCommand("cancel");
		okayButton.addActionListener(buttonListener);
		cancelButton.addActionListener(buttonListener);

		groupLayout.setHorizontalGroup(groupLayout.createSequentialGroup()
		        .addGroup(groupLayout.createParallelGroup(GroupLayout.Alignment.TRAILING)
		                .addComponent(dispatcherLabel)
		                .addComponent(namespaceLabel)
		                .addComponent(useridLabel)
		                .addComponent(passwordLabel)
		                .addComponent(okayButton))
		        .addGroup(groupLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
		                .addComponent(dispatcherField)
		                .addComponent(namespaceField)
		                .addComponent(useridField)
		                .addComponent(passwordField)
		                .addComponent(cancelButton)));
		
		groupLayout.setVerticalGroup(groupLayout.createSequentialGroup()
		        .addGroup(groupLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
		                .addComponent(dispatcherLabel)
		                .addComponent(dispatcherField))
		        .addGroup(groupLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
		                .addComponent(namespaceLabel)
		                .addComponent(namespaceField))
		        .addGroup(groupLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
		                .addComponent(useridLabel)
		                .addComponent(useridField))
		        .addGroup(groupLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
		                .addComponent(passwordLabel)
		                .addComponent(passwordField))
		        .addGroup(groupLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
		                .addComponent(okayButton)
		                .addComponent(cancelButton)));
		
		this.setTitle("Connection Details");
		Container connectPane = this.getContentPane();
		connectPane.add(connectPanel, BorderLayout.CENTER);
		connectPane.add(buttonPanel, BorderLayout.SOUTH);
		this.pack();
		this.setResizable(true);
		this.setModal(true);
		this.setVisible(true);
	}
	
	public String getDispatcher() {
		return dispatcherField.getText();
	}
	
	public String getNamespace() {
		return namespaceField.getText();
	}
	
	public String getUserID() {
		return useridField.getText();
	}
	
	public String getPassword() {
		return passwordField.getPassword().toString();
	}
	
}
