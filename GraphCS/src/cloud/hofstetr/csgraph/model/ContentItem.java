package cloud.hofstetr.csgraph.model;

import java.util.Collections;

import javax.swing.tree.*;

import org.apache.log4j.Logger;

import com.cognos.developer.schemas.bibus._3.BaseClass;
import com.cognos.developer.schemas.bibus._3.ContentManagerService_PortType;
import com.cognos.developer.schemas.bibus._3.Model;
import com.cognos.developer.schemas.bibus._3.OrderEnum;
import com.cognos.developer.schemas.bibus._3.Output;
import com.cognos.developer.schemas.bibus._3.PropEnum;
import com.cognos.developer.schemas.bibus._3.QueryOptions;
import com.cognos.developer.schemas.bibus._3.Report;
import com.cognos.developer.schemas.bibus._3.SearchPathMultipleObject;
import com.cognos.developer.schemas.bibus._3.Sort;

public class ContentItem extends DefaultMutableTreeNode implements Comparable<ContentItem>{
	private static final long serialVersionUID = 8727700150613116574L;
	private String defaultName;
	private String searchPath;
	private String objectType;
	private double dataSize;
	static Logger logger = Logger.getLogger(ContentItem.class.getName());

	public ContentItem(String name, String type, String path, double size) {
		this.setDefaultName(name);
		this.setObjectType(type);
		this.setSearchPath(path);
		this.setDataSize(size);
	}
     
	public double loadChildren(ContentManagerService_PortType cmService) {
		logger.debug("Loading children of " + getDefaultName());
		// Search properties: we need the defaultName, searchPath, type of object and size of data.
		PropEnum[] properties = { PropEnum.defaultName, PropEnum.searchPath, PropEnum.objectClass, PropEnum.dataSize, PropEnum.specification, PropEnum.model };
	
		// Sort options: ascending sort on the defaultName property.
		Sort[] sortBy = { new Sort()};
		sortBy[0].setOrder(OrderEnum.ascending);
		sortBy[0].setPropName(PropEnum.defaultName);
	
		// Query options; use the defaults.
		QueryOptions options = new QueryOptions();
	     
		try {
			// Make the query.
			BaseClass[] siblings = cmService.query(new SearchPathMultipleObject(getSearchPath() + "/*"), properties, sortBy, options);
			logger.debug("There are " + siblings.length + " children");
			// Build results for this level.
			for (int i = 0; i < siblings.length; i++) {
				String theDefaultName = siblings[i].getDefaultName().getValue();
				String theSearchPath = siblings[i].getSearchPath().getValue();
				String theType = siblings[i].getObjectClass().getValue().toString();
				double dataSize = 0;
				
				// Get the output size if this is an output object
				if (siblings[i] instanceof Output) {
					Output reportOutput = (Output)siblings[i];
					dataSize = reportOutput.getDataSize().getValue().doubleValue();
					logger.debug("Output size of " + theDefaultName + " with type of " + theType + " is " + dataSize);
	        	} else
				// Get the report specification size if this is a report object
				if (siblings[i] instanceof Report) {
					Report report = (Report)siblings[i];
					if (report.getSpecification() != null) {
						dataSize = report.getSpecification().getValue().length();
					}
					logger.debug("Specification length of " + theDefaultName + " with type of " + theType + " is " + dataSize);
	        	} else
				// Get the model specification size if this is a model object
				if (siblings[i] instanceof Model) {
					Model model = (Model)siblings[i];
					if (model.getModel() != null) {
						dataSize = model.getModel().getValue().length();
					}
					logger.debug("Model length of " + theDefaultName + " with type of " + theType + " is " + dataSize);
	        	}
				ContentItem item = new ContentItem(theDefaultName, theType, theSearchPath, dataSize);
				addSize(item.loadChildren(cmService));
				this.add(item);
	        }
			
			// This object will persist in the tree so free up space
			siblings = null;
	    }
		catch(Exception e) {
			logger.debug(e);
		}
	    logger.debug("The total size of " + getDefaultName() + " is " + getDataSize());
	    logger.debug("Finished loading children of " + getDefaultName());
		return getDataSize();
	}
	
	void addSize(double size) {
		dataSize += size;
	}
	
	public double getDataSize() {
		return dataSize;
	}
	
	public void setDataSize(double size) {
		this.dataSize = size;
	}

	public String getDefaultName() {
		return defaultName;
	}

	public void setDefaultName(String name) {
		this.defaultName = name;
	}

	public String getSearchPath() {
		return searchPath;
	}

	public void setSearchPath(String path) {
		this.searchPath = path;
	}

	public String getObjectType() {
		return objectType;
	}

	public void setObjectType(String objectType) {
		this.objectType = objectType;
	}
	
	public String toString() {
		return getDefaultName();
	}
	
	@Override
	public int compareTo(ContentItem o) {
		return this.defaultName.compareTo(o.getDefaultName());
	}
	
	@SuppressWarnings("unchecked")
	@Override
    public void add(MutableTreeNode newChild) {
        super.add(newChild);
        Collections.sort(this.children);
    }
}