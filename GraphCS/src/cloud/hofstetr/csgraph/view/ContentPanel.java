package cloud.hofstetr.csgraph.view;

import java.text.DecimalFormat;
import org.apache.log4j.*;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTree;
import javax.swing.ToolTipManager;
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
	static Logger logger = Logger.getLogger(ContentPanel.class.getName());
	
	public ContentPanel(ContentFrame frame) {
		logger.debug("Creating main content panel");
		SplitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
		logger.debug("Adding split pane to main content panel");
		SplitPane.setContinuousLayout(true);
	    SplitPane.setOneTouchExpandable(true);
		frame.setContentPane(SplitPane);
		logger.debug("Finished creating main content panel");
	}
	
	public void addTreePanel(ContentItem root) {
		logger.debug("Creating Tree panel");
		tree = new JTree(root);
		tree.setCellRenderer(new TreeRenderer());
		ToolTipManager.sharedInstance().registerComponent(tree);
		tree.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
		tree.addTreeSelectionListener(new TreeSelectionListener() {
		    public void valueChanged(TreeSelectionEvent e) {
		    	logger.debug("Tree item selected");
		        ContentItem node = (ContentItem)tree.getLastSelectedPathComponent();
		        logger.debug(node.getDefaultName() + " was selected");
		        
		        /* if nothing is selected */ 
		        if (node != null) {
			        // update the pie chart to the selected tree node
			        updatePiePanel(node);
		        }
		    }
		});
		SplitPane.setLeftComponent(new JScrollPane(tree,
			      JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
			      JScrollPane.HORIZONTAL_SCROLLBAR_NEVER));
		updateDividerLocation();
		logger.debug("Finished creating Tree panel");
	}
	
	public void addPiePanel() {
		JPanel pie = new JPanel();
		SplitPane.setRightComponent(pie);
	}
	
	public void updatePiePanel(ContentItem node) {
		logger.info("Updating Pie chart");
		DefaultPieDataset dataset=new DefaultPieDataset();
		
		for(int i=0; i<node.getChildCount(); i++) {
			ContentItem child = (ContentItem) node.getChildAt(i);
			
			// Only add pie slices for nodes that actually have data
			if (child.getDataSize() > 0) {
				logger.debug("Adding content item " + child.getDefaultName() + " to Pie chart");
				dataset.setValue(child.getDefaultName(), child.getDataSize());
			}
			
			// The dataset is the size of the children under the selected node
			logger.info("There are " + dataset.getItemCount() + " slices");
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
		logger.info("Finished updating Pie chart");
	}
	
	public void updateDividerLocation() {
		logger.debug("Updating divider position");
		int loc = (int) ((SplitPane.getBounds().getWidth() - SplitPane.getDividerSize()) / 4);
		SplitPane.setDividerLocation(loc);
		logger.debug("Finished updating divider position");
	}
}
