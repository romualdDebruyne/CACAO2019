package abstraction.fourni;

import java.util.ArrayList;
import java.util.Observable;

import abstraction.eq1Producteur1.Producteur1;
import abstraction.eq2Producteur2.Producteur2;
import abstraction.eq3Transformateur1.Transformateur1;
import abstraction.eq4Transformateur2.Transformateur2;
import abstraction.eq5Distributeur1.Distributeur1;
import abstraction.eq6Distributeur2.Distributeur2;
import abstraction.eq7Romu.acteurs.CreateurRomu;



/**
 * Classe modelisant "le monde" vu comme un regroupement d'acteurs, 
 * d'indicateurs et de journaux. 
 * 
 * Les acteurs/indicateurs/journaux que vous creerez devront etre
 * ajoutes a l'unique instance de cette classe designee par la 
 * variable LE_MONDE. Le bon endroit pour creer les instances 
 * d'acteurs et les ajouter au monde est dans la methode peupler(),
 * laquelle est appelee juste apres la creation de l'unique instance
 * du Monde.
 *
 * @author Romuald Debruyne
 */
public class Monde extends Observable {

	// Il n'y aura qu'une seule instance de la classe Monde.
	// Pour acceder facilement a cette unique
	// instance de Monde vous pouvez utiliser la reference 
	// statique LE_MONDE.
	public static Monde LE_MONDE;
	
	private int step;                         // Le numero d'etape en cours
	private ArrayList<IActeur> acteurs;        // La liste des acteurs
	private ArrayList<Indicateur> indicateurs;// La liste des indicateurs
	private ArrayList<Journal> journaux;      // La liste des journaux
	
	/**
	 * Initialise le monde de sorte que le numero d'etape soit 0, 
	 * et qu'il n'y ait pour l'heure aucun acteur/indicateur/journal.
	 */
	public Monde() {
		this.step=0;
		this.acteurs=new ArrayList<IActeur>();
		this.indicateurs=new ArrayList<Indicateur>();
		this.journaux=new ArrayList<Journal>();
	}
	public void peupler() {
		this.ajouterActeur(new CreateurRomu());
//		this.ajouterActeur(new Producteur1());
		this.ajouterActeur(new Producteur2());
//		this.ajouterActeur(new Transformateur1());
//		this.ajouterActeur(new Transformateur2());
//		this.ajouterActeur(new Distributeur1());
//		this.ajouterActeur(new Distributeur2());
		
		for (IActeur a : this.acteurs) {
			a.initialiser();
		}
	}

	/**
	 * @return Retourne le numero de l'etape en cours.
	 */
	public int getStep() {
		return this.step;
	}
	
	/**
	 * Ajoute l'acteur ac au monde
	 * @param ac, l'acteur a ajouter
	 */
	public void ajouterActeur(IActeur ac) {
		if (this.getActeur(ac.getNom())==null) {
			this.acteurs.add(ac);
		} else {
			System.out.println("ajouterActeur(\""+ac.getNom()+"\") : il existe deja un acteur de ce nom dans le monde...");
		}
	}
	
	/**
	 * Ajoute l'indicateur i au monde
	 * @param i l'idicateur a ajouter
	 */
	public void ajouterIndicateur(Indicateur i) {
		this.indicateurs.add(i);
	}
	
	/**
	 * Ajoute le journal j au monde
	 * @param j le journal a ajouter
	 */
	public void ajouterJournal(Journal j) {
		this.journaux.add(j);
	}
	
	/**
	 * @return Retourne la liste des acteurs du monde
	 */
	public ArrayList<IActeur> getActeurs() {
		return this.acteurs;
	}
	
	/**
	 * @param nom Le nom de l'acteur a retourner
	 * @return Si il existe dans le monde un acteur de nom nom, retourne cet acteur.
	 * Sinon, returne null. 
	 */
	public IActeur getActeur(String nom) {
		int i=0; 
		while (i<this.acteurs.size() && !this.acteurs.get(i).getNom().equals(nom)) {
			i++;
		}
		if (i<this.acteurs.size()) {
			return this.acteurs.get(i);
		} else {
			return null;
		}
	}
	
	/** 
	 * @return Retourne la liste des indicateurs du monde
	 */
	public ArrayList<Indicateur> getIndicateurs() {
		return this.indicateurs;
	}
	
	/**
	 * @param nom le nom de l'indicateur a retourner
	 * @return Si il existe dans le Monde un indicateur de nom nom
	 * retourne cet indicateur. Sinon, affiche un message d'alerte 
	 * et retourne null.
	 */
	public Indicateur getIndicateur(String nom) {
		int i=0; 
		while (i<this.indicateurs.size() && !this.indicateurs.get(i).getNom().equals(nom)) {
			i++;
		}
		if (i<this.indicateurs.size()) {
			return this.indicateurs.get(i);
		} else {
			return null;
		}
	}
	
	/**
	 * @return Retourne la liste des journaux du monde
	 */
	public ArrayList<Journal> getJournaux() {
		return this.journaux;
	}
	
	/**
	 * Methode appelee lorsque l'utilisateur clique sur le bouton NEXT de l'interface graphique.
	 * Cette methode incremente le numero d'etape puis appelle la methode next() de chaque acteur du monde.
	 */
	public void next() {
		this.step++;
		this.setChanged();
		this.notifyObservers("step");
		for (IActeur a : this.acteurs) {
			//System.out.println("Step "+this.getStep()+" : "+a.getNom()+".next()");
			a.next();
			//System.out.println("Step "+this.getStep()+" : "+a.getNom()+".next() termine");
		}
		//System.out.println("Step "+this.getStep()+" : appels a next() effectues");
		for (Journal j : journaux) {
			j.notifyObservers("endNext");
		}
	}
}
