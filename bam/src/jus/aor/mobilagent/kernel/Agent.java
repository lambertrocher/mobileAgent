package jus.aor.mobilagent.kernel;

public class Agent implements _Agent{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Override
	public void run() {
		System.out.println("Agent : run");
		
	}

	@Override
	public void init(AgentServer agentServer, String serverName) {
		System.out.println("Agent : init");
		
	}

	@Override
	public void reInit(AgentServer server, String serverName) {
		System.out.println("Agent : reinit");
		
	}

	@Override
	public void addEtape(Etape etape) {
		System.out.println("Agent : addEtape");
		
	}

	protected _Action retour() {
		System.out.println("Agent : retour");
		return null;
	}

}
