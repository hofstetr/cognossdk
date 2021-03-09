package cloud.hofstetr.csgraph.view;

import java.awt.event.ActionListener;

import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;

public class ContentMenu extends JMenuBar {
	private static final long serialVersionUID = 8727700150613116574L;
	private ActionListener menuListener;
	
	public ContentMenu() {
	}
	
	public void addMenuListener(ActionListener listener) {
		this.menuListener = listener;
	}
	
	public void init() {

		//declare menuItems
		JMenuItem exit;
		JMenuItem connect;
		JMenuItem about;

		//Add and populate the File menu. 
		JMenu fileMenu = new JMenu("File");
		this.add(fileMenu);

		connect = new JMenuItem("Connect");
		fileMenu.add(connect);
		connect.addActionListener(menuListener);

		exit = new JMenuItem("Exit");
		fileMenu.add(exit);
		exit.addActionListener(menuListener);

		//Add and populate the Help menu.
		JMenu helpMenu = new JMenu("Help");
		this.add(helpMenu);

		about = new JMenuItem("About");
		helpMenu.add(about);
		about.addActionListener(menuListener);
	}
}
