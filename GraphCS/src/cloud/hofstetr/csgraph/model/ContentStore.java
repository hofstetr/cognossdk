package cloud.hofstetr.csgraph.model;

import java.net.MalformedURLException;
import java.rmi.RemoteException;

import javax.xml.namespace.QName;
import javax.xml.rpc.ServiceException;

import com.cognos.developer.schemas.bibus._3.BiBusHeader;
import com.cognos.developer.schemas.bibus._3.ContentManagerService_PortType;
import com.cognos.developer.schemas.bibus._3.ContentManagerService_ServiceLocator;
import com.cognos.developer.schemas.bibus._3.SearchPathMultipleObject;
import com.cognos.developer.schemas.bibus._3.XmlEncodedXML;
import com.cognos.org.apache.axis.client.Stub;
import com.cognos.org.apache.axis.message.SOAPHeaderElement;

public class ContentStore {
	private ContentManagerService_ServiceLocator cmServiceLocator = null;
	private ContentManagerService_PortType cmService = null;
	private ContentItem Root = new ContentItem("Content Store", "Folder", "/", 100);
	private String BiBus_NS = "http://developer.cognos.com/schemas/bibus/3/";
	private String BiBus_H = "biBusHeader";
	
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
			cmService = cmServiceLocator.getcontentManagerService(serverURL);
			logonWithCreds(namespace, userid, password);
			
			// Create and load team content structure
			ContentItem TeamContent = new ContentItem("Team Content", "Folder", "/content/*", 0);
			TeamContent.loadTeamContent(cmService);
			Root.add(TeamContent);
			ContentItem PersonalContent = new ContentItem("Personal Content", "Folder", "CAMID(\"" + namespace + "\")//account/folder[@name='My Folders']", 0);
			PersonalContent.loadPersonalContent(cmService);
			Root.add(PersonalContent);
			cmService.logoff();
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (ServiceException e) {
			e.printStackTrace();
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
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
			System.out.println("Failed to log on as " + userid + " using " + password);
			e.printStackTrace();
			return false;
		}
		return true;
	 }
	 
	 public boolean logonWithAnonymous() {
		 try {
			((Stub)cmService).clearHeaders();
			cmService.query(new SearchPathMultipleObject("~"), null, null, null);
		} catch (RemoteException e) {
			System.out.println("Failed to log on.");
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
			 System.out.println("Failed to log on.");
		 }
	
		 if (CMbibus != null) {
			 ((Stub)cmService).setHeader(BiBus_NS, BiBus_H, CMbibus);
		 }
	 }
}
