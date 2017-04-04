package rmi;

import java.io.File;
import java.io.IOException;
import java.rmi.RemoteException;
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
    
    private HashMap<String, Numero> annuaire;
    
    protected Annuaire() throws RemoteException {
	super();
	
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
            doc = docBuilder.parse(new File("DataStore/Annuaire.xml"));
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
    }

    @Override
    public Numero get(String abonne) {
	return annuaire.get(abonne);
    }
    
}
