/**
 * J<i>ava</i> U<i>tilities</i> for S<i>tudents</i>
 */
package jus.aor.mobilagent.kernel;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.URL;
import java.rmi.RMISecurityManager;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * @author Morat 
 */
public class Starter {
	/** le document xml en cours */
	protected Document doc;
	/** le logger pour ce code */
	protected Logger logger;
	/** le server associé à ce starter */
	protected jus.aor.mobilagent.kernel._Server server;
	/** le Loader utilisé */
	protected BAMServerClassLoader loader;
	/** la classe du server : jus.aor.mobilagent.kernel.Server */
	protected Class<jus.aor.mobilagent.kernel.Server> classe;
	/**
	 * 
	 * @param args
	 */
	public Starter(String... args){
		// récupération du niveau de log
		java.util.logging.Level level;
		try {
			level = Level.FINE;
		}catch(NullPointerException e) {
			level=java.util.logging.Level.OFF;
		}catch(IllegalArgumentException e) {
			level=java.util.logging.Level.SEVERE;
		}
		try {
			/* Mise en place du logger pour tracer l'application */
			String loggerName = "jus/aor/mobilagent/"+InetAddress.getLocalHost().getHostName()+"/"+args[1];
			logger = Logger.getLogger(loggerName);
			//			logger.setUseParentHandlers(false);
			logger.addHandler(new IOHandler());
			logger.setLevel(level);
			/* Récupération d'informations de configuration */
			DocumentBuilder docBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
			doc = docBuilder.parse(new File(args[0]));
			int port=Integer.parseInt(doc.getElementsByTagName("port").item(0).getAttributes().getNamedItem("value").getNodeValue());
			// Création du serveur
			createServer(port,args[1]);
			// ajout des services
			addServices();
			// déploiement d'agents
			deployAgents();
		}catch(Exception ex){
			logger.log(Level.FINE,"Ce programme necessite un argument : <conf file> <name server>",ex);
			return;
		}
	}
	@SuppressWarnings("unchecked")
	protected void createServer(int port, String name) throws MalformedURLException, ClassNotFoundException, InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException {
		loader = new BAMServerClassLoader(new URL[]{new URL("file:///.../MobilagentServer.jar")},this.getClass().getClassLoader());
		classe = (Class<jus.aor.mobilagent.kernel.Server>)Class.forName("jus.aor.mobilagent.kernel.Server",true,loader);
		server = classe.getConstructor(int.class,String.class).newInstance(port,name);
		logger.log(Level.FINE, "\""+name+"\" created at port "+port, "");
	}
	/**
	 * Ajoute les services définis dans le fichier de configuration
	 */
	protected void addServices() {
		NamedNodeMap attrs;
		Object[] args;
		String codeBase, classeName, name;
		for(Node item : iterable(doc,"service")) {
			attrs = item.getAttributes();
			codeBase = attrs.getNamedItem("codebase").getNodeValue();
			classeName = attrs.getNamedItem("class").getNodeValue();
			args = attrs.getNamedItem("args").getNodeValue().split(" ");
			name = attrs.getNamedItem("name").getNodeValue();
			addService(name, classeName, codeBase, args);
		}
	}
	/**
	 * Ajoute un service
	 * @param name le nom du service
	 * @param classeName la classe du service
	 * @param codeBase le code du service
	 * @param args les arguments de la construction du service
	 */
	protected void addService(String name, String classeName, String codeBase, Object... args) {
		try{
			server.addService(name,classeName,codeBase,args);
			logger.log(Level.FINE, "Service added: "+name, "");
		}catch(Exception e){
			logger.log(Level.FINE," erreur durant l'ajout d'un service",e);
		}
	}
	/**
	 * déploiement les agents définis dans le fichier de configuration
	 */
	protected void deployAgents() {
		NamedNodeMap attrsAgent, attrsEtape;
		Object[] args=null;
		String codeBase;
		String classeName;
		List<String> serverAddress=new LinkedList<String>(), serverAction=new LinkedList<String>();

		for(Node  item1 : iterable(doc,"agent")) {
			attrsAgent = item1.getAttributes();
			codeBase = attrsAgent.getNamedItem("codebase").getNodeValue();
			classeName = attrsAgent.getNamedItem("class").getNodeValue();
			args = attrsAgent.getNamedItem("args").getNodeValue().split(" ");
			for(Node item2 : iterable((Element)item1,"etape")) {
				attrsEtape = item2.getAttributes();
				serverAction.add(attrsEtape.getNamedItem("action").getNodeValue());
				serverAddress.add(attrsEtape.getNamedItem("server").getNodeValue());
			}
			deployAgent(classeName, args, codeBase,serverAddress, serverAction);
		}
	}
	/**
	 * Déploie un agent
	 * @param classeName la classe de l'agent
	 * @param args les arguments de la construction de l'agent
	 * @param codeBase le code de l'agent
	 * @param serverAddress la liste des serveurs des étapes
	 * @param serverAction la liste des actions des étapes
	 */
	protected void deployAgent(String classeName, Object[] args, String codeBase, List<String> serverAddress, List<String> serverAction) {
		try{
			server.deployAgent(classeName,args,codeBase,serverAddress,serverAction);
			logger.log(Level.FINE, "Agent deployed: "+classeName, "");
		}catch(Exception e){
			logger.log(Level.FINE," erreur durant le déploiement de l'agent",e);
		}
	}
	private static Iterable<Node> iterable(final Node racine, final String element){
		return new Iterable<Node>() {
			@Override
			public Iterator<Node> iterator(){
				return new Iterator<Node>() {
					NodeList nodelist;
					int current = 0, length;
					{ //init
						try{
							nodelist = ((Document)racine).getElementsByTagName(element);
						}catch(ClassCastException e){
							nodelist = ((Element)racine).getElementsByTagName(element);
						}
						length = nodelist.getLength();
					}
					@Override
					public boolean hasNext(){return current<length;}
					@Override
					public Node next(){return nodelist.item(current++);}
					@Override
					public void remove(){}
				};
			}
		};
	}


	/**
	 * Application starter
	 * @param args
	 */
	public static void main(String... args) {
		System.setProperty("java.security.policy","file:./security.policy");
		if(System.getSecurityManager() == null)System.setSecurityManager(new SecurityManager());
		new Starter(args);
	}
}

