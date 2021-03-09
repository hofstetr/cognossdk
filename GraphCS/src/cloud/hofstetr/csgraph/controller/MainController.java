package cloud.hofstetr.csgraph.controller;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JMenuItem;

import cloud.hofstetr.csgraph.model.ContentStore;
import cloud.hofstetr.csgraph.view.ConnectDialog;
import cloud.hofstetr.csgraph.view.ContentFrame;
import cloud.hofstetr.csgraph.view.ContentMenu;

public class MainController {
	private ContentFrame contentFrame;
	private ContentMenu contentMenu;
	private ContentStore contentStore;
	private ConnectDialog connectDialog;
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
				ContentStore cs = new ContentStore(dispatcher, namespace, userid, password);
				contentFrame.addTree(cs.getRoot());
			}
			else {
				connectDialog.dispose();
				connectDialog = null;
			}
		}
	}
}
