package abstraction.eq4Transformateur2;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import abstraction.eq7Romu.produits.Chocolat;
import abstraction.eq7Romu.produits.Feve;
import abstraction.eq7Romu.ventesContratCadre.ContratCadre;
import abstraction.eq7Romu.ventesContratCadre.Echeancier;
import abstraction.eq7Romu.ventesContratCadre.IAcheteurContratCadre;
import abstraction.eq7Romu.ventesContratCadre.IVendeurContratCadre;
import abstraction.eq7Romu.ventesContratCadre.StockEnVente;
import abstraction.fourni.IActeur;
import abstraction.fourni.Indicateur;
import abstraction.fourni.Journal;
import abstraction.fourni.Monde;


public class Transformateur2 implements IActeur, IAcheteurContratCadre<Feve>, IVendeurContratCadre<Chocolat> {
	private Journal journal;

	// Indicateurs
	protected Indicateur iStockFeves;
	protected Indicateur iStockChocolat;
	protected Indicateur soldeBancaire;

	// Listes des produits échangés
	protected List<Chocolat> CHOCOLATS_VENTE;
	protected List<Feve> FEVES_ACHAT;
	
	// Stocks
	protected StockProduit<Chocolat> stocksChocolat;
	protected StockProduit<Feve> stockFeves;
	
	// Implémentations des échanges par contrats cadres
	private Transformateur2AcheteurCC acheteurCC;
	private Transformateur2VendeurCC vendeurCC;
	
	/// Stockage des contrats en cours
	protected List<ContratCadre<Feve>> contratsFevesEnCours;
	protected List<ContratCadre<Chocolat>> contratsChocolatEnCours;

	protected StockEnVente<Chocolat> stockEnVente;
	
	// Historique des demandes (utilisée pour définir notre stratégie d'achat de fèves)
	public HistoriqueDemande historiqueDemande;
	
	// Constantes
	public static final int STEPS_PAR_ANNEE = 24;
	private static final double MAX_PRODUCTION_PAR_STEP = 10e3; // Production max. de chocolats par step, en kg
	
	public Transformateur2() {
		// Initialisation et ajout des indicateurs
		this.iStockFeves = new Indicateur("EQ4 stock feves", this, 0);
		this.iStockChocolat = new Indicateur("EQ4 stock chocolat", this, 0);
		this.soldeBancaire = new Indicateur("EQ4 solde bancaire", this, 100000);
		Monde.LE_MONDE.ajouterIndicateur(this.iStockFeves);
		Monde.LE_MONDE.ajouterIndicateur(this.soldeBancaire);
		Monde.LE_MONDE.ajouterIndicateur(this.iStockChocolat);
	}
	
	public String getNom() {
		return "EQ4";	
	}

	public void initialiser() {
		// Initialisation du journal
		this.journal = new Journal("jEq4");
		Monde.LE_MONDE.ajouterJournal(this.journal);
		this.journal.ajouter("Initialisation du transformateur 2 (Eq4).");
		
		CHOCOLATS_VENTE = new ArrayList<Chocolat>();
		CHOCOLATS_VENTE.add(Chocolat.HG_E_SHP);
		CHOCOLATS_VENTE.add(Chocolat.MG_E_SHP);
		CHOCOLATS_VENTE.add(Chocolat.MG_NE_SHP);

		FEVES_ACHAT = new ArrayList<Feve>();
		FEVES_ACHAT.add(Feve.CRIOLLO_HG_EQ);
		FEVES_ACHAT.add(Feve.FORASTERO_MG_EQ);
		FEVES_ACHAT.add(Feve.FORASTERO_MG_NEQ);
		FEVES_ACHAT.add(Feve.MERCEDES_MG_EQ);
		FEVES_ACHAT.add(Feve.MERCEDES_MG_NEQ);
		FEVES_ACHAT.add(Feve.TRINITARIO_MG_EQ);
		FEVES_ACHAT.add(Feve.TRINITARIO_MG_NEQ);
		
		contratsFevesEnCours = new ArrayList<ContratCadre<Feve>>();
		contratsChocolatEnCours = new ArrayList<ContratCadre<Chocolat>>();
		
		acheteurCC = new Transformateur2AcheteurCC(this);
		vendeurCC = new Transformateur2VendeurCC(this);
		
		// Initialisation des stocks
		stocksChocolat = new StockProduit<Chocolat>(CHOCOLATS_VENTE);
		stockFeves = new StockProduit<Feve>(FEVES_ACHAT);
		
		// Initialisation de l'historique des demandes
		historiqueDemande = new HistoriqueDemande();
	}

	public void next() {
		/** ---- Transformations ---- (Kelian) */
		double qteTransformee = 0.0;
		double lastQteTransformee = 0.0;
		do {
			lastQteTransformee = effectuerTransformation(); 
			qteTransformee += lastQteTransformee;
		} while(lastQteTransformee != 0 && qteTransformee < MAX_PRODUCTION_PAR_STEP);
			
		/*double quantiteTransformee = Math.random()*Math.min(100, this.iStockFeves.getValeur()); // on suppose qu'on a un stock infini de sucre
		this.iStockFeves.retirer(this, quantiteTransformee);
		this.iStockChocolat.ajouter(this, (2*quantiteTransformee));// 50% cacao, 50% sucre
		this.soldeBancaire.retirer(this, quantiteTransformee*1.0234); // sucre, main d'oeuvre, autres frais*/
		
	}

	// Kelian
	/** Effectue une transformation. Renvoie true si une transformation a été effectuée, false sinon */
	private double effectuerTransformation() {
		// On vérifie d'abord s'il y a  des fèves qu'on doit absolument utiliser
		Feve f = getFeveCritique();
		if(f != null) {
			Recette r = Recette.getRecettes(f).get(0); // la liste ne contient normalement qu'un élément
			TasProduit<Feve> tas = stockFeves.getProchainTasPerime(f);
			double qte = 0;
			// TODO Prendre en compte coût fixe
			if(r.getCoutTransformation() * tas.getQuantité() < soldeBancaire.getValeur() * 0.3)
				qte = tas.getQuantité(); // on transforme tout le tas
			else
				qte = (soldeBancaire.getValeur() * 0.3) / r.getCoutTransformation(); // on transforme le plus possible
			
			executerRecette(r, qte);
			return qte;
		}
		
		// On s'intéresse ensuite au chocolat que l'on doit produire en priorité pour satisfaire les échéances des CC en cours
		Pair<Chocolat, Double> c = getChocolatCritique();
		if(c != null && c.getX() != null) {
			List<Recette> recettes = Recette.getRecettes(c.getX());
			
			// On prend la recette pour laquelle on a le plus de stock de feves
			double maxStock = 0;
			Recette maxRecette = null;
			for(Recette r : recettes) {
				double qteStock = stockFeves.getQuantiteTotale(r.getInputFeve());
				if(qteStock >= maxStock) {
					maxStock = qteStock;
					maxRecette = r;
				}
			}
			
			// à ce stade maxRecette ne devrait pas être null, sauf si on nous demande de produire un chocolat qui n'a pas de recette associée.
			
			double qte = 0;
			// TODO Prendre en compte coût fixe
			if(maxRecette.getCoutTransformation() * c.getY() < soldeBancaire.getValeur() * 0.6)
				qte = c.getY();
			else
				qte = (soldeBancaire.getValeur() * 0.6) / maxRecette.getCoutTransformation(); // on transforme le plus possible
			
			executerRecette(maxRecette, qte);
			return qte;
		}
		
		return 0;
	}
	
	// Kelian
	/** Exécute une recette (actualisation des stocks, du solde, ...). Renvoie true si la recette a bien été executée (false sinon; manque de stock par exemple) */
	private boolean executerRecette(Recette r, double qte) {
		double fevesNecessaires = r.getInputQteParKilo() * qte;
		
		// Vérification stock
		if(stockFeves.getQuantiteTotale(r.getInputFeve()) < fevesNecessaires)
			return false;
		
		// Vérification solde
		if(soldeBancaire.getValeur() < r.calculCout(qte))
			return false;
		
		// Exécution stock
		double coutTotal = stockFeves.getPrix(r.getInputFeve(), fevesNecessaires) + r.calculCout(qte);
		stockFeves.prendreProduits(r.getInputFeve(), fevesNecessaires);
		iStockFeves.retirer(this, fevesNecessaires);
		stocksChocolat.ajouterTas(r.getOutput(), new TasProduit<Chocolat>(qte, qte / coutTotal));
		iStockChocolat.ajouter(this, qte);
		
		// Exécution solde
		soldeBancaire.retirer(this, r.calculCout(qte));
		
		return true;
	}
	
	// Kelian
	/** Renvoie la fève que l'on doit utiliser en priorité pour ne pas en perdre à cause de péremption */
	private Feve getFeveCritique() {
		// On cherche si un tas périme bientôt
		/** Désactivé tant que les dates de péremption ne sont pas implémentées
		for(Feve f : FEVES_ACHAT) {
			TasProduit<Feve> prochainPerime = stockFeves.getProchainTasPerime(f);
			if(prochainPerime.getDatePeremption() <= Monde.LE_MONDE.getStep() + 2)
				return f;
		}
		*/
		return null;
	}
	
	// Kelian
	/** Renvoie le chocolat que l'on doit produire en priorité pour satisfaire les échéances, et la quantité associée */
	private Pair<Chocolat, Double> getChocolatCritique() {
		// On construit une HashMap qui contient, pour chaque CC en cours, le nombre d'échéances que l'on peut satisfaire avec le stock actuel
		HashMap<ContratCadre<Chocolat>, Integer> echeancesSatisfiables = new HashMap<ContratCadre<Chocolat>, Integer>();
		for(ContratCadre<Chocolat> cc : contratsChocolatEnCours) {
			Echeancier e = cc.getEcheancier();
			double qteDejaLivree = e.getQuantiteJusquA(Monde.LE_MONDE.getStep() - e.getStepDebut());
			int i = 0;
			// Tant que l'on n'est pas arrivé au bout de l'échéancier et que l'on a assez de stock
			while(i < e.getNbEcheances() && e.getQuantiteJusquA(i) - qteDejaLivree < stocksChocolat.getQuantiteTotale(cc.getProduit()));
				i++;
			echeancesSatisfiables.put(cc, i-1);
		}
		
		if(echeancesSatisfiables.isEmpty()) // Tous les CC en cours sont satisfiables par notre stock actuel
			return null; // On ne produit pas plus
		
		// On va ensuite chercher le CC pour lequel on peut satisfaire le moins d'échéances
		int minEcheances = Integer.MAX_VALUE;
		ContratCadre<Chocolat> minContrat = null;
		for(Map.Entry<ContratCadre<Chocolat>, Integer> entry : echeancesSatisfiables.entrySet()) {
		    if(entry.getValue() <= minEcheances) {
		    	minEcheances = entry.getValue();
		    	minContrat = entry.getKey();
		    }
		}

		Chocolat c = minContrat.getProduit();
		double qteAProduire = minContrat.getQuantiteRestantALivrer() - stocksChocolat.getQuantiteTotale(c);
		return new Pair<Chocolat, Double>(c, qteAProduire); 
		/* TODO Attention : produire toute la qté nécessaire pour le CC d'un coup n'est pas forcément une bonne idée vis à vis de la péremption */
	}
	
	// Kelian
	/** Renvoie le step actuel dans l'année en cours */
	public static int getCurrentStepInAnnee() {
		return Monde.LE_MONDE.getStep() % STEPS_PAR_ANNEE;
	}
	
	
	/** Fonctions relatives à IAcheteurContratCadre<Feve> */
	public ContratCadre<Feve> getNouveauContrat() {return acheteurCC.getNouveauContrat();}
	public void proposerEcheancierAcheteur(ContratCadre<Feve> cc) {acheteurCC.proposerEcheancierAcheteur(cc);}
	public void proposerPrixAcheteur(ContratCadre<Feve> cc) {acheteurCC.proposerPrixAcheteur(cc);}
	public void notifierAcheteur(ContratCadre<Feve> cc) {acheteurCC.notifierAcheteur(cc);}
	public void receptionner(Feve p, double qte, ContratCadre<Feve> cc) {acheteurCC.receptionner(p, qte, cc);}
	public double payer(double montant, ContratCadre<Feve> cc) {return acheteurCC.payer(montant, cc);}
	
	/** Fonctions relatives à IVendeurContratCadre<Chocolat> */
	public List<Chocolat> getProduitsEnVente() {return vendeurCC.getProduitsEnVente();}
	public StockEnVente<Chocolat> getStockEnVente() {return vendeurCC.getStockEnVente();}
	public double getPrix(Chocolat produit, Double quantite) {return vendeurCC.getPrix(produit, quantite);}
	public void proposerEcheancierVendeur(ContratCadre<Chocolat> cc) {vendeurCC.proposerEcheancierVendeur(cc);}
	public void proposerPrixVendeur(ContratCadre<Chocolat> cc) {vendeurCC.proposerPrixVendeur(cc);}
	public void notifierVendeur(ContratCadre<Chocolat> cc) {vendeurCC.notifierVendeur(cc);}
	public double livrer(Chocolat p, double qte, ContratCadre<Chocolat> cc) {return vendeurCC.livrer(p, qte, cc);}
	public void encaisser(double montant, ContratCadre<Chocolat> cc) {vendeurCC.encaisser(montant, cc);}
	
	

}