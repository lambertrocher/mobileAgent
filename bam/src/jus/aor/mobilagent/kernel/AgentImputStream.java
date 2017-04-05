package jus.aor.mobilagent.kernel;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectStreamClass;

/**
 * ObjectInputStream spécifique au bus à agents mobiles. Il permet d'utiliser le loader de l'agent.
 * @author   Morat
 */
class AgentInputStream extends ObjectInputStream {
	/**
	 * le classLoader à utiliser
	 */
	private BAMAgentClassLoader loader;

	public AgentInputStream ( InputStream inputStream, BAMAgentClassLoader bamAgentClassLoader) throws IOException {
		super(inputStream);
		this.loader=bamAgentClassLoader;
	}

	protected Class<?> resolveClass(ObjectStreamClass desc) throws IOException, ClassNotFoundException {
		return loader.loadClass(desc.getName());

	}
}