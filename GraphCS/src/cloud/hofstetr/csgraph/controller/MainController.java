package cloud.hofstetr.csgraph.controller;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.JMenuItem;
import javax.swing.WindowConstants;

import cloud.hofstetr.csgraph.model.ContentStore;
import cloud.hofstetr.csgraph.view.ConnectDialog;
import cloud.hofstetr.csgraph.view.ContentFrame;
import cloud.hofstetr.csgraph.view.ContentMenu;
import cloud.hofstetr.csgraph.view.ProgressDialog;

public class MainController {
	private ContentFrame contentFrame;
	private ContentMenu contentMenu;
	private ContentStore contentStore;
	private ConnectDialog connectDialog;
	private ProgressDialog progressDialog;
	private String dispatcher = "http://localhost:9300/p2pd/servlet/dispatch";
	private String namespace = "ldap";
	private String userid = "cognos";
	private String password = "password";
	
	public void start() {
		contentFrame = new ContentFrame();
		contentMenu = new ContentMenu();
		contentMenu.addMenuListener(new MenuHandler());
		contentMenu.init();
		contentFrame.addMenu(contentMenu);
		contentStore = new ContentStore();
		contentFrame.addTree(contentStore.getRoot());
		contentFrame.loadFrame();
	}
	
	class MenuHandler implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			JMenuItem menuClicked = (JMenuItem)e.getSource();
			if (menuClicked.getText() == "Exit") {
				System.exit(0);
			}

			if (menuClicked.getText() == "Connect") {
				connectDialog = new ConnectDialog();
				connectDialog.addButtonListener(new ButtonHandler());
				connectDialog.init(dispatcher, namespace, userid, password);
			}
		}
	}
	
	class ButtonHandler implements ActionListener {
		public void actionPerformed(ActionEvent event) {
			String cmd = event.getActionCommand();
			if (cmd.compareToIgnoreCase("okay") == 0) {
				dispatcher = connectDialog.getDispatcher();
				namespace = connectDialog.getNamespace();
				userid = connectDialog.getUserID();
				password = connectDialog.getPassword();
				connectDialog.dispose();
				connectDialog = null;
				progressDialog = new ProgressDialog();
				progressDialog.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
                progressDialog.getBar().setIndeterminate(true);
				ContentStore cs = new ContentStore(dispatcher, namespace, userid, password);
				cs.addPropertyChangeListener(new PropertyChangeListener() {
	                @Override
	                public void propertyChange(PropertyChangeEvent evt) {
                        ContentStore worker = (ContentStore) evt.getSource();
	                    if ("state".equalsIgnoreCase(evt.getPropertyName())) {
	                        switch (worker.getState()) {
	                            case DONE:
	                				progressDialog.dispose();
	                				progressDialog = null;
	                				contentFrame.addTree(cs.getRoot());
	                                break;
							case PENDING:
								break;
							case STARTED:
								break;
							default:
								break;
	                        }
	                    } else if ("progress".equalsIgnoreCase(evt.getPropertyName())) {
	                        progressDialog.getBar().setValue((Integer) evt.getNewValue());
	                        progressDialog.getBar().setString(cs.getCurrentCount() + " / " + cs.getChildCount());
	                    }
	                }
	            });
            	progressDialog.getBar().setIndeterminate(false);
                progressDialog.getBar().setStringPainted(true);
				cs.execute();
			}
			else {
				connectDialog.dispose();
				connectDialog = null;
			}
		}
	}
}
