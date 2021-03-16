package cloud.hofstetr.csgraph.view;

import java.text.DecimalFormat;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTree;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.TreeSelectionModel;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.labels.PieSectionLabelGenerator;
import org.jfree.chart.labels.StandardPieSectionLabelGenerator;
import org.jfree.chart.plot.PiePlot;
import org.jfree.data.general.DefaultPieDataset;

import cloud.hofstetr.csgraph.model.ContentItem;

public class ContentPanel extends JPanel {
	private static final long serialVersionUID = 8727700150613116574L;
	private JSplitPane SplitPane;
	private JTree tree;

	
	public ContentPanel(ContentFrame frame) {
		SplitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
		SplitPane.setContinuousLayout(true);
	    SplitPane.setOneTouchExpandable(true);
		frame.setContentPane(SplitPane);
	}
	
	public void addTreePanel(ContentItem root) {
		tree = new JTree(root);
		tree.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
		tree.addTreeSelectionListener(new TreeSelectionListener() {
		    public void valueChanged(TreeSelectionEvent e) {
		        ContentItem node = (ContentItem)tree.getLastSelectedPathComponent();

		        /* if nothing is selected */ 
		        if (node == null) return;
		        
		        // update the pie chart to the selected tree node
		        updatePiePanel(node);
		    }
		});
		SplitPane.setLeftComponent(new JScrollPane(tree,
			      JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
			      JScrollPane.HORIZONTAL_SCROLLBAR_NEVER));
		updateDividerLocation();
	}
	
	public void addPiePanel() {
		JPanel pie = new JPanel();
		SplitPane.setRightComponent(pie);
	}
	
	public void updatePiePanel(ContentItem node) {
		DefaultPieDataset dataset=new DefaultPieDataset();
		
		// The dataset is the size of the children under the selected node
		System.out.println("There are " + node.getChildCount() + " slices.");
		for(int i=0; i<node.getChildCount(); i++) {
			ContentItem child = (ContentItem) node.getChildAt(i);
			
			// Only add pie slices for nodes that actually have data
			if (child.getDataSize() > 0) {
				dataset.setValue(child.getDefaultName(), child.getDataSize());
			}
		}
		JFreeChart chart = ChartFactory.createPieChart3D(node.getDefaultName(), dataset, false, true, false);  
			  
		//Format Labels
		PieSectionLabelGenerator labelGenerator = new StandardPieSectionLabelGenerator(  
				"{0} : ({2})", new DecimalFormat("0"), new DecimalFormat("0%")); 
		
		// Get the pie plot and override some defaults
		final PiePlot plot = ( PiePlot ) chart.getPlot( );             
		plot.setStartAngle( 270 );             
		plot.setForegroundAlpha( 0.60f );             
		plot.setInteriorGap( 0.02 );             
		plot.setLabelGenerator(labelGenerator);  
			      
		// Create Panel  
		ChartPanel panel = new ChartPanel(chart);
		SplitPane.setRightComponent(panel);
		updateDividerLocation();
	}
	
	public void updateDividerLocation() {
		int loc = (int) ((SplitPane.getBounds().getWidth() - SplitPane.getDividerSize()) / 4);
		SplitPane.setDividerLocation(loc);
	}
}
