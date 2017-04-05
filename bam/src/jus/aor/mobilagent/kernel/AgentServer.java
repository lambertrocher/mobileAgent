package jus.aor.mobilagent.kernel;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
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
	protected BAMServerClassLoader serverClassLoader;
	protected ServerSocket serverSocket;

	public AgentServer(BAMServerClassLoader bamServerClassLoader, int port, String name) {
		this.port = port;
		this.name = name;
		services = new HashMap<String, _Service<?>>();
		serverClassLoader = bamServerClassLoader;
	}

	public AgentServer(int port, String name) {
		this.port = port;
		this.name = name;
		services = new HashMap<String, _Service<?>>();
	}

	@Override
	public void run() {
		try {
			// Création socket serveur
			this.serverSocket = new ServerSocket(this.port);
			while (true){
				Socket socketClient = this.serverSocket.accept();
				_Agent agent;
				try {
					agent = this.getAgent(socketClient);
				
				new Thread(agent).start();
				socketClient.close();
				} catch (ClassNotFoundException e) {
					e.printStackTrace();
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}	
	}

	private _Agent getAgent(Socket socketClient) throws IOException, ClassNotFoundException {
		
		BAMAgentClassLoader agentClassLoader = new BAMAgentClassLoader(this.getClass().getClassLoader());

		
		InputStream inputStream = socketClient.getInputStream();
		AgentInputStream agentInputStream = new AgentInputStream(inputStream, agentClassLoader);
		ObjectInputStream objectInputStream = new ObjectInputStream(inputStream);

		Jar wJar = (Jar) objectInputStream.readObject();
		agentClassLoader.integrateCode(wJar);

		_Agent wAgent = (_Agent) agentInputStream.readObject();

		objectInputStream.close();
		agentInputStream.close();
		inputStream.close();

		return wAgent;
	}

	_Service<?> getService(String s) {
		return this.services.get(s);
	}

	void addService(String s, _Service<?> service) {
		this.services.put(s, service);
	}

	public URI site() {
		URI uri = null;
		try {
			uri =  new URI("mobilagent://localhost:"+Integer.toString(port) + "/");
		} catch (URISyntaxException e) {
			e.printStackTrace();
		}

		return uri;
	}

	public String toString() {
		return  name;
	}
}
