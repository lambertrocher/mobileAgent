package jus.aor.mobilagent.kernel;

import java.io.IOException;
import java.net.Socket;
import java.net.URI;

public abstract class Agent implements _Agent {

    private static final long serialVersionUID = 1L;
    private AgentServer agentServer;
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
	System.out.println("Agent : run");
    
    
    }

    @Override
    public void init(AgentServer agentServer, String serverName) {
	System.out.println("Agent : init");
	this.agentServer = agentServer;
	this.serverName = serverName;
	route = new Route(new Etape(agentServer.site(), _Action.NIHIL));	
    }

    @Override
    public void reInit(AgentServer server, String serverName) {
	System.out.println("Agent : reinit");
	agentServer = server;
	this.serverName = serverName;
    }

    @Override
    public final void addEtape(Etape etape) {
	System.out.println("Agent : addEtape");
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
	    // Création d'une socket pour la connexion avec le serveur suivant
	    Socket server = new Socket(uri.getHost(), uri.getPort());
	    
	} catch (IOException e) {
	    e.printStackTrace();
	}
    }
    
    private void move() {
	move(route.next().server);
    }
    
    public final String toString() {
	return null;
	//TODO
    }
    
    
    
}
