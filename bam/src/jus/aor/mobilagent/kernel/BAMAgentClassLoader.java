package jus.aor.mobilagent.kernel;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Hashtable;
import java.util.Map;
import java.util.jar.JarEntry;
import java.util.jar.JarException;
import java.util.jar.JarOutputStream;

public class BAMAgentClassLoader extends ClassLoader {
	
	// Hashtable contenant les classes lues
	Hashtable<String, byte[]> table;


	public BAMAgentClassLoader(String jarPath, ClassLoader classLoaderGenerique) throws JarException, IOException {
		super(classLoaderGenerique);
		table = new Hashtable<String, byte[]>();
		Jar jar = new Jar(jarPath);
		integrateCode(jar);
	}

	public BAMAgentClassLoader(ClassLoader classLoaderGenerique) {
		super(classLoaderGenerique);
		table = new Hashtable<String, byte[]>();
	}

	
	// remplis la table de hash
	void integrateCode(Jar jar) {
		for (Map.Entry<String, byte[]> entry : jar) {

			String tmpClassName = className(entry.getKey());
			table.put(tmpClassName, entry.getValue());

			Class<?> tmpclass = defineClass(tmpClassName, entry.getValue(), 0, entry.getValue().length);
			super.resolveClass(tmpclass);
		}
	}

	private String className(String s) {
		return s.replace(".class", "").replace("/", ".");
	}

	
	// renvoie le le Jar
	protected Jar extractCode() throws JarException, IOException {
		File jarFile = new File("LeJar.jar");

		try {
			// on crée un stream pour ecrire les donnees comprises dans le jar
			JarOutputStream jarStream = new JarOutputStream(new FileOutputStream(jarFile));
			for (String className : table.keySet()) {
				jarStream.putNextEntry(new JarEntry(className));
				jarStream.write(table.get(className));
			}
			jarStream.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		Jar jarObject = new Jar(jarFile.getPath());
		jarFile.delete();
		return jarObject;
	}

}
