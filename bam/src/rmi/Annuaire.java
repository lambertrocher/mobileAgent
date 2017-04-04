package rmi;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.rmi.AlreadyBoundException;
import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.server.UnicastRemoteObject;
import java.util.HashMap;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public class Annuaire extends UnicastRemoteObject implements _Annuaire {
    
    private static final long serialVersionUID = 1L;
    
    private static HashMap<String, Numero> annuaire;
    
    protected Annuaire() throws RemoteException {
	super();
    }

    @Override
    public Numero get(String abonne) {
	if (abonne == null) throw new IllegalArgumentException("abonne est null"); 
	return annuaire.get(abonne);
    }
    
    public static void main(String[] args[]) {
	
	annuaire = new HashMap<String, Numero>();
	
        /* On récupère l'annuaire dans le XML */
        DocumentBuilder docBuilder = null;
        Document doc = null;
        try {
    	docBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
        } catch (ParserConfigurationException e1) {
    	e1.printStackTrace();
        }
        try {
    	doc = docBuilder.parse(new File("dataStore/Annuaire.xml"));
        } catch (SAXException | IOException e) {
    	e.printStackTrace();
        }
        
        String name, numero;
        NodeList list = doc.getElementsByTagName("Telephone");
        NamedNodeMap attrs;
        /* acquisition de toutes les entrées de l'annuaire */
        for(int i =0; i<list.getLength();i++) {
        	attrs = list.item(i).getAttributes();
        	name=attrs.getNamedItem("name").getNodeValue();
        	numero=attrs.getNamedItem("numero").getNodeValue();
        	annuaire.put(name, new Numero(numero));
        }
    
        Annuaire serveurAnnuaire = null;
        
	try {
	    serveurAnnuaire = new Annuaire();
	    LocateRegistry.createRegistry(1099);
	    Naming.bind("Annuaire", serveurAnnuaire);
	} catch (RemoteException | MalformedURLException | AlreadyBoundException e) {
	    e.printStackTrace();
	}
	
    }
}
