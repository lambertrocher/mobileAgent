package jus.aor.mobilagent.kernel;

import java.net.URL;
import java.net.URLClassLoader;

public class BAMServerClassLoader extends URLClassLoader{

    BAMServerClassLoader(URL[] urls, ClassLoader classLoader) {
	super(urls, classLoader);
	for (URL url : urls) {
	    this.addURL(url);
	}
    }
    
    public void addURL(URL urls) {
	//TODO
    }
    
    public String toString() {
	return null;
	//TODO
    }

}
