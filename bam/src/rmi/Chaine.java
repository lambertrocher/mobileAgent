package rmi;

import java.io.File;
import java.io.IOException;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public class Chaine extends UnicastRemoteObject implements _Chaine {
    
    private static final long serialVersionUID = 1L;
    
    private List<Hotel> hotels;

    protected Chaine(String nomChaine) throws RemoteException {
	super();
	
	hotels = new LinkedList<Hotel>();
	
	/* Chargement du XML */
        DocumentBuilder docBuilder = null;
        Document doc=null;
        try {
	    docBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
	} catch (ParserConfigurationException e) {
	    e.printStackTrace();
	}
        try {
            String chaine = "DataStore/" + nomChaine + ".xml";
	    doc = docBuilder.parse(new File(chaine));
	} catch (SAXException | IOException e) {
	    e.printStackTrace();
	}
    
        // On récupère les hotêls et on les ajoute dans la liste
        String name, localisation;
        NodeList list = doc.getElementsByTagName("Hotel");
        NamedNodeMap attrs;
        /* acquisition de toutes les entrées de la base d'hôtels */
        for(int i =0; i<list.getLength();i++) {
            attrs = list.item(i).getAttributes();
            name=attrs.getNamedItem("name").getNodeValue();
            localisation=attrs.getNamedItem("localisation").getNodeValue();
            hotels.add(new Hotel(name,localisation));
        }
        

    }

    /**
     * Restitue la liste des hotels situés dans la localisation.
     * @param localisation le lieu où l'on recherche des hotels
     * @return la liste des hotels trouvés
     */
    public List<Hotel> get(String localisation) throws RemoteException {
	
	// Itération sur la tous les hôtels de la chaine
	List<Hotel> listeHotels = new LinkedList<Hotel>();
	Iterator<Hotel> iterator = hotels.iterator();
	while(iterator.hasNext()) {
	    Hotel hotel = iterator.next();
	    if (hotel.localisation.equals(localisation)) {
		listeHotels.add(hotel);
	    }
	}
	return listeHotels;
    }
    
}
