package cloud.hofstetr.csgraph.view;

import java.awt.Component;
import java.awt.Toolkit;

import javax.swing.ImageIcon;
import javax.swing.JTree;
import javax.swing.tree.DefaultTreeCellRenderer;

import cloud.hofstetr.csgraph.model.ContentItem;

public class TreeRenderer extends DefaultTreeCellRenderer {
	private static final long serialVersionUID = 8727700150613116574L;
	private ImageIcon MyFolder = new ImageIcon(Toolkit.getDefaultToolkit().getImage("icon_myfolder.gif"));
	private ImageIcon TeamFolder = new ImageIcon(Toolkit.getDefaultToolkit().getImage("icon_publicfolder.gif"));
	private ImageIcon Folder = new ImageIcon(Toolkit.getDefaultToolkit().getImage("icon_folder.gif"));
	private ImageIcon Model = new ImageIcon(Toolkit.getDefaultToolkit().getImage("icon_model.gif"));
	private ImageIcon Report = new ImageIcon(Toolkit.getDefaultToolkit().getImage("icon_report.gif"));
	
	public Component getTreeCellRendererComponent(
            JTree tree,
            Object value,
            boolean sel,
            boolean expanded,
            boolean leaf,
            int row,
            boolean hasFocus) {

		super.getTreeCellRendererComponent(
		            tree, value, sel,
		            expanded, leaf, row,
		            hasFocus);
		
		String itemType = ((ContentItem)value).getObjectType();
		switch(itemType) {
			case "MyFolder":
	            setIcon(MyFolder);
	            break;
	        case "TeamFolder":
	            setIcon(TeamFolder);
	            break;
	        case "Folder":
	            setIcon(Folder);
	            break;
	        case "Report":
	            setIcon(Report);
	            break;
	        case "Model":
	        case "Package":
	            setIcon(Model);
	            break;
	        default: 
	            break;
		}
		return this;
	}
	
}
