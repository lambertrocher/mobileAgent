package jus.aor.mobilagent.kernel;

import java.net.URL;
import java.net.URLClassLoader;

public class BAMServerClassLoader extends URLClassLoader{

	BAMServerClassLoader(URL[] urls, ClassLoader classLoader) {
		super(urls);
	}

	@Override
	public void addURL(URL url) {
		super.addURL(url);
	}

	@Override
	public String toString() {
		return super.toString();
	}

}
