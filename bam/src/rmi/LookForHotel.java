/**
 * J<i>ava</i> U<i>tilities</i> for S<i>tudents</i>
 */

package rmi;

import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

/**
 * Représente un client effectuant une requête lui permettant d'obtenir les numéros de téléphone des hôtels répondant à son critère de choix.
 * @author  Morat
 */
public class LookForHotel extends Thread {
    
	/** le critère de localisaton choisi */
	private static String localisation;
	// ...
	/**
	 * Définition de l'objet représentant l'interrogation.
	 * @param args les arguments n'en comportant qu'un seul qui indique le critère
	 *          de localisation
	 */
	public LookForHotel(String... args){
	    localisation = args[0];
	}
	
	/**
	 * réalise une interrogation
	 * @return la durée de l'interrogation
	 * @throws RemoteException
	 */
	public long call() throws RemoteException {
	    
	    long startTime = System.currentTimeMillis();
	    
	    try {
		// Interrogation des chaînes d'hôtels
		_Chaine refChaine = (_Chaine) Naming.lookup("Chaine");
		List<Hotel> hotels = refChaine.get(localisation);
		
		// Interrogation de l'annuaire
		_Annuaire refAnnuaire = (_Annuaire) Naming.lookup("Annuaire");
		Iterator<Hotel> iterator = hotels.iterator();
		
		// On récupère les numéros des hôtels
		List<Numero> numeros = new LinkedList<Numero>();
		while (iterator.hasNext()) {
		    numeros.add(refAnnuaire.get(iterator.next().name));
		}
		 printNum(numeros);
		
	    } catch (MalformedURLException | NotBoundException e) {
		e.printStackTrace();
	    }
	    
	    long endTime = System.currentTimeMillis();
	    
	   
	    
	    return endTime - startTime;
	}
	
	public void printNum(List<Numero> numeros) {
	    Iterator<Numero> iterator = numeros.iterator();
	    if (!iterator.hasNext()) {
		System.out.println("Pas de numéros");
	    }
	    while (iterator.hasNext()) {
		System.out.println(iterator.next().toString());
	    }
	}
	
	public void run() {
	    System.out.println("localisation : " + localisation);
	    long executionTime = 0;
	    try {
		executionTime = call();
	    } catch (RemoteException e) {
		e.printStackTrace();
	    }
	    System.out.println(executionTime);
	}	
}
