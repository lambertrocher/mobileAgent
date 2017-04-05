package jus.aor.mobilagent.kernel;

public class BAMAgentClassLoader extends ClassLoader {

	protected Jar jar;

	BAMAgentClassLoader(String jarName,ClassLoader classLoader) {
		super(classLoader);
	}

	BAMAgentClassLoader(ClassLoader classLoader) {
		super(classLoader);
	}

	void integrateCode(Jar jar) {
		this.jar = jar;
	}

	private String className(String s) {
		//TODO
		return null;
	}

	Jar extractCode() {
		return this.jar;
	}

	public String toString() {
		return null;
		//TODO
	}


}
