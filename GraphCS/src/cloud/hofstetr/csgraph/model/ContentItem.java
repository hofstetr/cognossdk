package cloud.hofstetr.csgraph.model;

import javax.swing.tree.*;

import com.cognos.developer.schemas.bibus._3.BaseClass;
import com.cognos.developer.schemas.bibus._3.ContentManagerService_PortType;
import com.cognos.developer.schemas.bibus._3.OrderEnum;
import com.cognos.developer.schemas.bibus._3.Output;
import com.cognos.developer.schemas.bibus._3.PropEnum;
import com.cognos.developer.schemas.bibus._3.QueryOptions;
import com.cognos.developer.schemas.bibus._3.SearchPathMultipleObject;
import com.cognos.developer.schemas.bibus._3.Sort;

public class ContentItem extends DefaultMutableTreeNode {
	private static final long serialVersionUID = 8727700150613116574L;
	private String defaultName;
	private String searchPath;
	private String objectType;
	private double dataSize;

	public ContentItem(String name, String type, String path, double size) {
		this.setDefaultName(name);
		this.setObjectType(type);
		this.setSearchPath(path);
		this.setDataSize(size);
	}
     
	public double loadChildren(ContentManagerService_PortType cmService) {
		// Search properties: we need the defaultName, searchPath, type of object and size of data.
		PropEnum[] properties = { PropEnum.defaultName, PropEnum.searchPath, PropEnum.objectClass, PropEnum.dataSize };
	
		// Sort options: ascending sort on the defaultName property.
		Sort[] sortBy = { new Sort()};
		sortBy[0].setOrder(OrderEnum.ascending);
		sortBy[0].setPropName(PropEnum.defaultName);
	
		// Query options; use the defaults.
		QueryOptions options = new QueryOptions();
	     
		try {
			// Make the query.
			BaseClass[] siblings = cmService.query(new SearchPathMultipleObject(getSearchPath() + "/*"), properties, sortBy, options);
	
			// Build results for this level.
			for (int i = 0; i < siblings.length; i++) {
				String theDefaultName = siblings[i].getDefaultName().getValue();
				String theSearchPath = siblings[i].getSearchPath().getValue();
				String theType = siblings[i].getObjectClass().toString();
				double dataSize = 0;
	             
				// Get the output size if this is an output object
				if (siblings[i] instanceof Output) {
					Output reportOutput = (Output)siblings[i];
					dataSize = reportOutput.getDataSize().getValue().doubleValue();
					addSize(dataSize);
	        	}
				ContentItem item = new ContentItem(theDefaultName, theType, theSearchPath, dataSize);
				addSize(item.loadChildren(cmService));
				this.add(item);
	        }
	    }
		catch(Exception e) {
			e.printStackTrace();
		}
	    System.out.println("The size of " + getDefaultName() + " is " + getDataSize());
		return getDataSize();
	}
	
	public void loadTeamContent(ContentManagerService_PortType cmService) {
		// No extra processing required for team content
		loadChildren(cmService);
	}
	
	public void loadPersonalContent(ContentManagerService_PortType cmService) {
		// Need to fetch each personal my folder and then load the children
		PropEnum[] properties = { PropEnum.defaultName, PropEnum.searchPath, PropEnum.objectClass };
	
		// Sort options: ascending sort on the defaultName property.
		Sort[] sortBy = { new Sort()};
		sortBy[0].setOrder(OrderEnum.ascending);
		sortBy[0].setPropName(PropEnum.defaultName);
	
		// Query options; use the defaults.
		QueryOptions options = new QueryOptions();
	     
		try {
			// Make the query.
			BaseClass[] folders = cmService.query(new SearchPathMultipleObject(getSearchPath()), properties, sortBy, options);
	
			// Build results for this level.
			for (int i = 0; i < folders.length; i++) {
				String theDefaultName = folders[i].getDefaultName().getValue();
				String theSearchPath = folders[i].getSearchPath().getValue();
				String theType = folders[i].getObjectClass().toString();
				double dataSize = 0; 
				ContentItem item = new ContentItem(theDefaultName, theType, theSearchPath, dataSize);
				addSize(item.loadChildren(cmService));
				this.add(item);
	        }
	    }
		catch(Exception e) {
			e.printStackTrace();
		}
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
}