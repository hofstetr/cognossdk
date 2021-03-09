package cloud.hofstetr.csgraph.view;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JFrame;
import cloud.hofstetr.csgraph.model.ContentItem;

public class ContentFrame extends JFrame {
	private static final long serialVersionUID = 8727700150613116574L;
	private ContentPanel contentPanel;
	
	public ContentFrame () {
		super("Content Store Grapher");
		addWindowListener(new WindowAdapter() {
	         public void windowClosing(WindowEvent windowEvent){
	            System.exit(0);
	         }        
	      }); 
		contentPanel = new ContentPanel(this);
		contentPanel.addPiePanel();
	}
	
	public void loadFrame() {
		this.setSize(500, 500);
		this.setVisible(true);
		this.setResizable(true);
	}
	
	public void addMenu(ContentMenu menu) {
		this.setJMenuBar(menu);
	}
	
	public void addTree(ContentItem root) {
		contentPanel.addTreePanel(root);
	}
}
