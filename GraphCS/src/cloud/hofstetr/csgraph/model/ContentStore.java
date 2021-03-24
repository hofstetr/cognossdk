package cloud.hofstetr.csgraph.model;

import java.net.MalformedURLException;
import java.rmi.RemoteException;
import javax.swing.SwingWorker;
import javax.xml.namespace.QName;
import javax.xml.rpc.ServiceException;

import org.apache.log4j.Logger;

import com.cognos.developer.schemas.bibus._3.BaseClass;
import com.cognos.developer.schemas.bibus._3.BiBusHeader;
import com.cognos.developer.schemas.bibus._3.ContentManagerService_PortType;
import com.cognos.developer.schemas.bibus._3.ContentManagerService_ServiceLocator;
import com.cognos.developer.schemas.bibus._3.Folder;
import com.cognos.developer.schemas.bibus._3.OrderEnum;
import com.cognos.developer.schemas.bibus._3.PropEnum;
import com.cognos.developer.schemas.bibus._3.QueryOptions;
import com.cognos.developer.schemas.bibus._3.SearchPathMultipleObject;
import com.cognos.developer.schemas.bibus._3.Sort;
import com.cognos.developer.schemas.bibus._3.XmlEncodedXML;
import com.cognos.org.apache.axis.client.Stub;
import com.cognos.org.apache.axis.message.SOAPHeaderElement;

public class ContentStore extends SwingWorker<Object, Object> {
	private ContentManagerService_ServiceLocator cmServiceLocator = null;
	private ContentManagerService_PortType cmService = null;
	private ContentItem Root = new ContentItem("Content Store", "Folder", "/", 100);
	private ContentItem TeamContent;
	private ContentItem PersonalContent;
	private String BiBus_NS = "http://developer.cognos.com/schemas/bibus/3/";
	private String BiBus_H = "biBusHeader";
	static Logger logger = Logger.getLogger(ContentStore.class.getName());
	private int ChildCount = 0;
	
    public ContentItem getRoot() {
		return Root;
	}
    
	public ContentStore() {
	}
	
    public ContentStore(String dispatcher, String namespace, String userid, String password) {
		// First connect to Cognos and authenticate
		cmServiceLocator = new ContentManagerService_ServiceLocator();
		
		try {
			java.net.URL serverURL = new java.net.URL(dispatcher);
			logger.debug("Connecting to Cognos at: " + dispatcher);
			cmService = cmServiceLocator.getcontentManagerService(serverURL);
			
			if (userid == null) {
				logger.debug("Logging on anonymously");
				logonWithAnonymous();
			}
			else {
				logger.debug("Authenticating to Cognos as " + userid);
				logonWithCreds(namespace, userid, password);
			}
			
			// Create team content and personal content nodes
			TeamContent = new ContentItem("Team Content", "Folder", "/content/*", 0);
			PersonalContent = new ContentItem("Personal Content", "Folder", "CAMID(\"" + namespace + "\")//account/folder[@name='My Folders']", 0);
			ChildCount = length(TeamContent.getSearchPath()) + length(PersonalContent.getSearchPath());

		} catch (MalformedURLException e) {
			logger.info("Invalid URL encountered");
		} catch (ServiceException e) {
			logger.info("Failed to connect to Cognos");
		}
	}
	
	public boolean logonWithCreds(String namespace, String userid, String password) {
		StringBuffer credentialXML = new StringBuffer();
			
		credentialXML.append("<credential>");
		credentialXML.append("<namespace>").append(namespace).append("</namespace>");
		credentialXML.append("<username>").append(userid).append("</username>");
		credentialXML.append("<password>").append(password).append("</password>");
		credentialXML.append("</credential>");
	
		String encodedCredentials = credentialXML.toString();
		XmlEncodedXML xmlCredentials = new XmlEncodedXML();
		xmlCredentials.set_value(encodedCredentials);
		try {
			((Stub)cmService).clearHeaders();
			cmService.logon(xmlCredentials, null);
			getSetHeaders();
		} catch (RemoteException e) {
			logger.info("Failed to log on as " + userid);
			return false;
		}
		return true;
	 }
	 
	 public boolean logonWithAnonymous() {
		 try {
			((Stub)cmService).clearHeaders();
			cmService.query(new SearchPathMultipleObject("~"), null, null, null);
		} catch (RemoteException e) {
			logger.info("Failed to log on anonymously.");
			return false;
		}
		return true;
	 }
	 
	 public void getSetHeaders() {
		 BiBusHeader CMbibus = null;
		 SOAPHeaderElement temp = ((Stub)cmService).getResponseHeader(BiBus_NS, BiBus_H);
		 try	{
			 CMbibus = (BiBusHeader)temp.getValueAsType(new QName(BiBus_NS, BiBus_H));
		 }
		 catch (Exception e)	{
			 logger.info("Failed to preserve authentication header");
		 }
	
		 if (CMbibus != null) {
			 ((Stub)cmService).setHeader(BiBus_NS, BiBus_H, CMbibus);
		 }
	 }
	 
	 public void loadTeamContent() {
		logger.debug("Loading Team Content");
		
		// Search properties: we need the defaultName, searchPath, type of object and size of data.
		PropEnum[] properties = { PropEnum.defaultName, PropEnum.searchPath, PropEnum.objectClass};
	
		// Sort options: ascending sort on the defaultName property.
		Sort[] sortBy = { new Sort()};
		sortBy[0].setOrder(OrderEnum.ascending);
		sortBy[0].setPropName(PropEnum.defaultName);
	
		// Query options; use the defaults.
		QueryOptions options = new QueryOptions();
	     
		try {
			// Make the query.
			BaseClass[] folders = cmService.query(new SearchPathMultipleObject(TeamContent.getSearchPath()), properties, sortBy, options);
			
			// Iterate over team content folders and crawl their children
			for (int i = 0; i < folders.length; i++) {
				Folder teamFolder = ((Folder)folders[i]);
				String theSearchPath = teamFolder.getSearchPath().getValue();
				String theDefaultName = teamFolder.getDefaultName().getValue();
				logger.debug("Found folder " + theDefaultName);
				String theType = teamFolder.getObjectClass().toString();
				ContentItem item = new ContentItem(theDefaultName, theType, theSearchPath, 0);
				TeamContent.add(item);
				logger.debug("Getting children of folder " + theDefaultName);
				double size = item.loadChildren(cmService);
				TeamContent.addSize(size);
				logger.debug("The total size of " + item.getDefaultName() + " is " + item.getDataSize());
				this.setProgress(this.getProgress() + 1);
			}
		}
		catch(Exception e) {
			logger.debug(e);
		}
		finally {
			logger.debug("Finished loading Team Content");
		}
	}
	
	public void loadPersonalContent() {
		logger.debug("Loading Personal Content");
		
		// Create alphabetical grouping nodes so that a single pie chart will have a manageable number of slices
		String alphabet = "abcdefghijklmnopqrstuvwxyz";
		for (int i=0; i<alphabet.length(); i++) {
			ContentItem ci = new ContentItem(alphabet.substring(i,i+1), "Folder", alphabet.substring(i,i+1), 0);
			PersonalContent.add(ci);
		}
		
		// Need to fetch each personal my folder and then load the children
		PropEnum[] properties = { PropEnum.defaultName, PropEnum.searchPath, PropEnum.objectClass };
	
		// Sort options: ascending sort on the defaultName property.
		Sort[] sortBy = { new Sort()};
	
		// Query options; use the defaults.
		QueryOptions options = new QueryOptions();
	     
		try {
			// Make the query.
			BaseClass[] folders = cmService.query(new SearchPathMultipleObject(PersonalContent.getSearchPath()), properties, sortBy, options);
			logger.debug("There are " + folders.length + " children");
			
			// Iterate over personal folders and group them alphabetically
			for (int i = 0; i < folders.length; i++) {
				Folder myFolder = ((Folder)folders[i]);
				String theSearchPath = myFolder.getSearchPath().getValue();
				
				String parentSearchPath = theSearchPath.substring(0, theSearchPath.length() - 27);
				logger.debug("Getting account for " + theSearchPath);
				BaseClass[] parent = cmService.query(new SearchPathMultipleObject(parentSearchPath), properties, sortBy, options);
				String theDefaultName = parent[0].getDefaultName().getValue();
				logger.debug("Found account " + theDefaultName);
				String theType = myFolder.getObjectClass().toString();
				ContentItem item = new ContentItem(theDefaultName, theType, theSearchPath, 0);
			
				// Figure out which alphabet grouping to add this account to by first letter
				int nodeIndex = alphabet.indexOf(theDefaultName.substring(0,1).toLowerCase());
				ContentItem alphabetNode = ((ContentItem)PersonalContent.getChildAt(nodeIndex));
				logger.debug("Adding account " + theDefaultName + " to alphabetical group " + alphabetNode.getDefaultName());
				alphabetNode.add(item);
				
				logger.debug("Getting children of account " + theDefaultName);
				double size = item.loadChildren(cmService);
				alphabetNode.addSize(size);
				PersonalContent.addSize(size);
				logger.debug("The total size of " + item.getDefaultName() + " is " + item.getDataSize());
				this.setProgress(this.getProgress() + 1);
	        }
			
			// This object will persist in the tree so free up space
			folders = null;
	    }
		catch(Exception e) {
			logger.debug(e);
		}
		finally {
			logger.debug("Finished loading Personal Content");
		}
	}
	
	public int length(String searchPath) {
		int count=0;
		// The length is the count of children
		PropEnum[] properties = { PropEnum.defaultName, PropEnum.searchPath, PropEnum.objectClass };
	
		// Sort options: none.
		Sort[] sortBy = { new Sort()};
	
		// Query options; use the defaults.
		QueryOptions options = new QueryOptions();
	     
		try {
			// Make the query.
			BaseClass[] folders = cmService.query(new SearchPathMultipleObject(searchPath), properties, sortBy, options);
			count = folders.length;
			folders = null;
	    }
		catch(Exception e) {
			logger.debug(e);
		}

		return count;
	}
		
	@Override
	protected Object doInBackground() throws Exception {
		// Load the children
		loadTeamContent();
		Root.add(TeamContent);
		
		loadPersonalContent();
		Root.add(PersonalContent);
		
		logger.debug("Logging off");
		cmService.logoff();
		return null;
	}

	public int getChildCount() {
		return ChildCount;
	}
}
