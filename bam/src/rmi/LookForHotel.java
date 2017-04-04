/**
 * J<i>ava</i> U<i>tilities</i> for S<i>tudents</i>
 */

package rmi;

import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

/**
 * Représente un client effectuant une requête lui permettant d'obtenir les numéros de téléphone des hôtels répondant à son critère de choix.
 * @author  Morat
 */
public class LookForHotel {
    
    /** le critère de localisaton choisi */
    private String localisation;
    /** le nombre de chaîne d'hotels */
    private int nombreChaines;
    /** port pour le registre */
    private int port;
    /** Hashmap contenat les correspondances entre une chaîne d'hôtels et un numéro */
    private HashMap<String, Numero> numberList;
    
    /**
     * Définition de l'objet représentant l'interrogation.
     * @param args les arguments n'en comportant qu'un seul qui indique le critère
     *          de localisation
     */
    public LookForHotel(String... args){
	port = 1099;
	nombreChaines = 4;
	
	// On vérifie que la localisation est en argument
	if (args.length != 1) {
	    System.out.println("Client <localisation>");
	    System.exit(1);
	}
	localisation = args[0];
	    
    }
	
    /**
     * réalise une interrogation
     * @return la durée de l'interrogation
     * @throws RemoteException
     */
    public long call() throws RemoteException {
	    
	// Heure au début de l'interrogation
	long startTime = System.currentTimeMillis();
	    
	try {
		
	    Registry registry = LocateRegistry.getRegistry(port);
	    _Annuaire refAnnuaire = (_Annuaire) registry.lookup("Annuaire");
	    List<Hotel> hotels = new LinkedList<Hotel>();
		
	    // Interrogation des chaînes d'hôtels
	    for (int i = 1; i <= nombreChaines; i++) {
		String chaineName = "Chaine" + i;
		_Chaine refChaine = (_Chaine) registry.lookup(chaineName);
		hotels.addAll(refChaine.get(localisation));
	    }
		
	    // On récupère les numéros des hôtels
	    numberList = new HashMap<String, Numero>();
	    Iterator<Hotel> iterator = hotels.iterator();
	    while (iterator.hasNext()) {
		String hotelName = iterator.next().name;
		numberList.put(hotelName, refAnnuaire.get(hotelName));
	    }
	} catch (NotBoundException e) {
	    e.printStackTrace();
	}
	
	// Heure à la fin de l'interrogation
	long endTime = System.currentTimeMillis();
	    
	return endTime - startTime;
    }
	
    public static void main(String[] args) {
	    
	if (System.getSecurityManager() == null) {
	    System.setSecurityManager(new SecurityManager());
	}
	long executionTime = 0;
	    
	LookForHotel client = new LookForHotel(args[0]);
	System.out.println("Localisation choisie : " + args[0]);
	    
	try {
	    System.out.println("Début de l'interrogation");
	    executionTime = client.call();
	    System.out.println("Fin de l'interrogation");
	} catch (RemoteException e) {
	    e.printStackTrace();
	}
	System.out.println("Temps d'exécution de l'interrogation : " + executionTime + " ms");
    }
    
}
