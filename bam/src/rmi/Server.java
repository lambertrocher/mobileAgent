package rmi;

import java.rmi.AlreadyBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public class Server {
    
    public static void main(String[] args) {
	
	if (System.getSecurityManager() == null) {
	    System.setSecurityManager(new SecurityManager());
	}
	
	// Valeur par défaut du port et du nombre de chaines d'hôtels
	int port = 1099;
	int nombreChaines = 4;
	
	if (args.length != 2) {
	    System.out.println("Server <port du registry> <nombre de chaines>");
	    System.exit(1);
	}
	try {
	    port = Integer.parseInt(args[0]);
	    nombreChaines = Integer.parseInt(args[1]);
	} catch (Exception e) {
	    e.printStackTrace();
	}
	
	try {
	    // On crée le registry
	    Registry registry = LocateRegistry.createRegistry(port);
	    // On ajoute une référence pour chaque chaîne
	    for (int i = 1; i <= nombreChaines; i++) {
		String nomChaine = "Hotels" + i;
		Chaine chaine = new Chaine(nomChaine);
		registry.bind("Chaine" + i, chaine);
		System.out.println("Référence à la chaine" + i + " : OK");
	    }
	    // On ajoute une référence à l'annuaire
	    	Annuaire annuaire = new Annuaire();
	    	registry.bind("Annuaire", annuaire);
	    	System.out.println("Référence à l'annuaire : OK");
	} catch (RemoteException | AlreadyBoundException e) {
	    e.printStackTrace();
	}
	
	

    }

}
