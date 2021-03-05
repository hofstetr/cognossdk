package cloud.hofstetr.csgraph;

import cloud.hofstetr.csgraph.model.ContentStore;

public class Main {

	public static void main(String[] args) {
		String dispatcher = "http://localhost:9300/p2pd/servlet/dispatch";
		String namespace = "ldap";
		String userid = "username";
		String password = "password";
		
		ContentStore test = new ContentStore(dispatcher, namespace, userid, password);
		

	}

}
