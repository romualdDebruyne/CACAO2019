package abstraction.fourni;

import java.util.Observable;
/**
 * Classe modelisant un indicateur. 
 * Un indicateur est une variable numerique dont la valeur 
 * pourra etre historisee et visualisee.
 * 
 * Vous aurez a creer des instances de cette classe (potentiellement 
 * partagees entre plusieurs acteurs) et a faire evoluer leurs valeurs.
 *
 * @author Romuald Debruyne
 */
public class Indicateur extends Observable {
	private String nom;           // nom designant l'indicateur
	private IActeur createur;      // acteur a l'origine de la creation de l'indicateur
	private Courbe courbe;        // ensemble des couples (etape, valeur). Exploite par la classe Graphique
	private Historique historique;// memorise l'historique des changements de valeur depuis la creation (premier element)
	                              // a la valeur actuelle (dernier element), en precisant pour chaque changement
	                              // le nom de l'acteur a l'origine de la modification, l'etape a laquelle le 
	                              // changement intervient et bien sur la nouvelle valeur.
	
	/**
	 * 
	 * @param nom identifiant designant l'indicateur
	 * @param createur l'acteur qui est a l'origine de la creation de l'indicateur
	 * @param valInit la valeur initiale de l'indicateur
	 */
	public Indicateur(String nom, IActeur createur,  double valInit) {
		this.nom = nom;
		this.createur = createur;
		this.historique = new Historique();
		this.historique.ajouter(createur, Monde.LE_MONDE.getStep(), valInit);
		this.courbe = new Courbe(this.nom);
		this.courbe.ajouter(Monde.LE_MONDE.getStep(), valInit);
	}
	/**
	 * Par defaut, la valeur initiale est fixee a 0.0
	 */
	public Indicateur(String nom, IActeur createur) {
		this(nom, createur, 0.0);
	}
	/**
	 * @return Retourne le nom de l'indicateur
	 */
	public String getNom() {
		return this.nom;
	}
	/**
	 * @return Retourne l'acteur a l'origine de la creation de l'indicateur
	 */
	public IActeur getCreateur() {
		return this.createur;
	}
	/**
	 * @return Retourne l'historique des changements de valeur
	 */
	public Historique getHistorique() {
		return this.historique;
	}
	/**
	 * @return Retourne la Courbe (utilise principalement pour l'affichage du graphique correspondant)
	 */
	public Courbe getCourbe() {
		return this.courbe;
	}
	/**
	 * @return Retourne la valeur actuelle de l'indicateur (donc la derniere valeur indiquee dans l'historique)
	 */
	public double getValeur() {
		return this.historique.getValeur();
	}
	/**
	 * Affecte la valeur valeur a l'indicateur en precisant
	 * que c'est auteur qui est a l'origine de ce changement.
	 * @param auteur Acteur a l'origine de la modification de valeur
	 * @param valeur La nouvelle valeur
	 */
	public void setValeur(IActeur auteur, double valeur) {
		this.historique.ajouter(auteur, Monde.LE_MONDE.getStep(), valeur);
		if (this.getNom().contains("HG_E_SHP")) System.out.println(" historique ajout de "+auteur+" - "+Monde.LE_MONDE.getStep()+" - "+valeur);
		this.courbe.ajouter(Monde.LE_MONDE.getStep(), this.getValeur());
		this.setChanged();
		this.notifyObservers("setValeur");
		if (this.getNom().contains("HG_E_SHP")) System.out.println(" notify "+this.getValeur());
		if (this.getNom().contains("HG_E_SHP")) System.out.println("indicateur "+this.getNom()+" notify setvaleur "+valeur);
	}
	
	/**
	 * Ajoute montant a la valeur de l'indicateur
	 * @param auteur
	 * @param delta>0
	 */
	public void ajouter(IActeur auteur, double delta) {
		if (this.getNom().contains("HG_E_SHP")) System.out.println(" indicateur "+this.getNom()+" ajouter :"+this.getValeur()+" --> "+( this.getValeur()+delta));
		this.setValeur(auteur, this.getValeur()+delta);
	}
	
	/**
	 * Retire montant a la valeur de l'indicateur
	 * @param auteur
	 * @param delta>0
	 */
	public void retirer(IActeur auteur, double delta) {
		this.setValeur(auteur, this.getValeur()-delta);
	}
}
