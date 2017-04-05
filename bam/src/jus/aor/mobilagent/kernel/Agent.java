package jus.aor.mobilagent.kernel;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.net.URI;

import com.sun.media.jfxmedia.logging.Logger;

public abstract class Agent implements _Agent {

	private static final long serialVersionUID = 1L;
	private transient AgentServer agentServer;
	// Nom du server initial
	private String serverName;
	private Route route;

	public Agent() {
		agentServer = null;
		serverName = null;
		route = null;
	}

	@Override
	public final void run() {
		if (this.route.hasNext()) {
			//on avance sur la route meme si il y eu du doute
			Etape NextStep = this.route.next();
			//execute laction 
			NextStep.action.execute();

			if (this.route.hasNext()) {
				// envoie lagent au prochain AgentServer
				this.move();
			} else {
				// revient au debut
				this.move(this.route.retour.server);
			}
		} else {
			this.route.retour.action.execute();
		}

	}

	@Override
	public void init(AgentServer agentServer, String serverName) {
		this.agentServer = agentServer;
		this.serverName = serverName;
		route = new Route(new Etape(agentServer.site(), _Action.NIHIL));
	}

	@Override
	public void reInit(AgentServer server, String serverName) {
		agentServer = server;
		this.serverName = serverName;
	}

	@Override
	public final void addEtape(Etape etape) {
		route.add(etape);
	}

	protected abstract _Action retour();

	protected _Service<?> getService(String serviceName) {
		return agentServer.getService(serviceName);
	}

	protected String route() {
		return route.toString();
	}  

	// Permet de déplacer l'agent vers un autre serveur
	protected void move(URI uri) {
		try {
			Socket destsocket = new Socket(uri.getHost(), uri.getPort());

			OutputStream os = destsocket.getOutputStream();
			ObjectOutputStream oos = new ObjectOutputStream(os);

			BAMAgentClassLoader AgentClassLoader = (BAMAgentClassLoader) this.getClass().getClassLoader();
			Jar BaseCode = AgentClassLoader.extractCode();

			oos.writeObject(BaseCode);

			oos.writeObject(this);

			oos.close();
			destsocket.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void move() {
		move(route.next().server);
	}

	public final String toString() {
		return "Je suis un agent";
	}



}
