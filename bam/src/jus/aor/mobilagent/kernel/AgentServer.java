package jus.aor.mobilagent.kernel;

import java.io.IOException;
import java.io.InputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;

import com.sun.org.apache.xerces.internal.util.URI;
import com.sun.org.apache.xerces.internal.util.URI.MalformedURIException;

public class AgentServer implements _Agent{

	private static final long serialVersionUID = 1L;
	protected int port;
	protected String name;
	protected Map<String,_Service<?>> services;
	protected BAMAgentClassLoader serverClassLoader;
	protected ServerSocket serverSocket;

	protected AgentServer(int port, String name) {
		this.port = port;
		this.name = name;
		this.services = new HashMap<String, _Service<?>>();
	}
	
	@Override
	public void run() {
		try {
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

	@Override
	public void init(AgentServer agentServer, String serverName) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void reInit(AgentServer server, String serverName) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void addEtape(Etape etape) {
		// TODO Auto-generated method stub
		
	}
	
	public _Service<?> getService(String s) {
		return this.services.get(s);
	}
	
	protected void addService(String s, _Service<?> service) {
		this.services.put(s, service);
	}

	public URI site() throws MalformedURIException {
			return new URI("//localhost:" + this.port);
	}
}
