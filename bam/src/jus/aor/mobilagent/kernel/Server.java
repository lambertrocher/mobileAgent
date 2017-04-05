/**
 * J<i>ava</i> U<i>tilities</i> for S<i>tudents</i>
 */
package jus.aor.mobilagent.kernel;

import java.io.ObjectOutputStream;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.net.InetAddress;
import java.net.Socket;
import java.net.URI;
import java.net.URL;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import jus.aor.mobilagent.kernel.BAMAgentClassLoader;
import jus.aor.mobilagent.kernel._Agent;

/**
 * Le serveur principal permettant le lancement d'un serveur d'agents mobiles et les fonctions permettant de déployer des services et des agents.
 * @author     Morat
 */
public final class Server implements _Server {
	/** le nom logique du serveur */
	protected String name;
	/** le port où sera ataché le service du bus à agents mobiles. Pafr défaut on prendra le port 10140 */
	protected int port=10140;
	/** le server d'agent démarré sur ce noeud */
	protected AgentServer agentServer;
	/** le nom du logger */
	protected String loggerName;
	/** le logger de ce serveur */
	protected Logger logger=null;
	/**
	 * Démarre un serveur de type mobilagent 
	 * @param port le port d'écuote du serveur d'agent 
	 * @param name le nom du serveur
	 */
	public Server(final int port, final String name){
		this.name=name;
		try {
			this.port=port;
			/* mise en place du logger pour tracer l'application */
			loggerName = "jus/aor/mobilagent/"+InetAddress.getLocalHost().getHostName()+"/"+this.name;
			logger=Logger.getLogger(loggerName);
			/* démarrage du server d'agents mobiles attaché à cette machine */
			agentServer = new AgentServer(this.port, this.name);
			/* temporisation de mise en place du server d'agents */
			Thread.sleep(1000);
		}catch(Exception ex){
			logger.log(Level.FINE," erreur durant le lancement du serveur"+this,ex);
			return;
		}
	}
	/**
	 * Ajoute le service caractérisé par les arguments
	 * @param name nom du service
	 * @param classeName classe du service
	 * @param codeBase codebase du service
	 * @param args arguments de construction du service
	 */
	public final void addService(String name, String classeName, String codeBase, Object... args) {
		try {
			//TODO
		}catch(Exception ex){
			logger.log(Level.FINE," erreur durant le lancement du serveur"+this,ex);
			return;
		}
	}
	/**	
	 * deploie l'agent caractérisé par les arguments sur le serveur
	 * @param classeName classe du service
	 * @param args arguments de construction de l'agent
	 * @param codeBase codebase du service
	 * @param etapeAddress la liste des adresse des étapes
	 * @param etapeAction la liste des actions des étapes
	 */
	public final void deployAgent(String classeName, Object[] args, String codeBase, List<String> etapeAddress, List<String> etapeAction) {
		Agent agent = null;
		try {
			//creation du classLoader
			BAMAgentClassLoader agentClassLoader = new BAMAgentClassLoader(new URI(codeBase).getPath(),this.getClass().getClassLoader());
			Class<?> agentclass = Class.forName(classeName, true, agentClassLoader);

			//creation du constructeur qui permet de construire l'agent
			Constructor<?> Constructor = agentclass.getConstructor(Object[].class);

			//creation de l'agent
			agent = (Agent) Constructor.newInstance(new Object[] { args });
			agent.init(this.agentServer, this.name);


			Iterator<String> addressIt = etapeAddress.iterator();
			Iterator<String> actionIt = etapeAction.iterator();

			//ajoute toutes les etapes
			while(addressIt.hasNext() && actionIt.hasNext()) {
				Field champ = agentclass.getDeclaredField(actionIt.next());
				//on a besoin de changer les pamametres d'accessibilite
				champ.setAccessible(true);
				agent.addEtape(new Etape(new URI(addressIt.next()), (_Action) champ.get(agent))); 
			}
			this.startAgent(agent, agentClassLoader);
		}catch(Exception ex){
			logger.log(Level.FINE," erreur durant le lancement du serveur"+this,ex);
			ex.printStackTrace();
			return;
		}
	}
	/**
	 * Primitive permettant de "mover" un agent sur ce serveur en vue de son exécution
	 * immédiate.
	 * @param agent l'agent devant être exécuté
	 * @param loader le loader à utiliser pour charger les classes.
	 * @throws Exception
	 */
	protected void startAgent(_Agent agent, BAMAgentClassLoader loader) throws Exception {
		URI AgentServerSite = this.agentServer.site();
		Socket Sock = new Socket(AgentServerSite.getHost(), AgentServerSite.getPort());

		java.io.OutputStream os = Sock.getOutputStream();
		ObjectOutputStream oos = new ObjectOutputStream(os);
		ObjectOutputStream oss = new ObjectOutputStream(os);
		
		// extraie le jar
		Jar BaseCode = loader.extractCode();

		//envoie le chemin du jar et l'agent
		oos.writeObject(BaseCode);
		oss.writeObject(agent);

		oos.close();
		oss.close();
		Sock.close();
	}

	public String toString() {
		return name;
	}
}
