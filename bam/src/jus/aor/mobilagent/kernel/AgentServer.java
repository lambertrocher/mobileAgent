package jus.aor.mobilagent.kernel;

import java.io.IOException;
import java.io.InputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;


public class AgentServer implements Runnable {

    protected int port;
    protected String name;
    protected Map<String,_Service<?>> services;
    protected BAMAgentClassLoader serverClassLoader;
    protected ServerSocket serverSocket;

    AgentServer(int port, String name) {
	this.port = port;
	this.name = name;
	this.services = new HashMap<String, _Service<?>>();
    }
	
    public void run() {
	try {
	    // Création socket serveur
	    this.serverSocket = new ServerSocket(this.port);
	    while (true){
		Socket socketClient = this.serverSocket.accept();
		_Agent agent = this.getAgent(socketClient);
		new Thread(agent).start();
		socketClient.close();
	    }
	} catch (IOException e) {
	    // TODO Auto-generated catch block
	    e.printStackTrace();
	}	
    }

    private _Agent getAgent(Socket socketClient) {
	// TODO Auto-generated method stub
	return null;
    }
	
    _Service<?> getService(String s) {
	return this.services.get(s);
    }
	
    void addService(String s, _Service<?> service) {
	this.services.put(s, service);
    }

    public URI site() {
	try {
	    return new URI("//localhost:" + this.port);
	} catch (URISyntaxException e) {
	    e.printStackTrace();
	}
	return null;
    }
	
    public String toString() {
	//TODO
	return null;
    }
}
