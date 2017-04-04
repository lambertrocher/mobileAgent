package jus.aor.mobilagent.kernel;

import java.net.URL;
import java.net.URLClassLoader;

public class BAMServerClassLoader extends URLClassLoader{

	public BAMServerClassLoader(URL[] urls, ClassLoader classLoader) {
		super(urls, classLoader);
		for (URL url : urls) {
			this.addURL(url);
		}
	}

}
