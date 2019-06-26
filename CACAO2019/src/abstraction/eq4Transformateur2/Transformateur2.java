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
	protected Journal journal;

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
	
	// Archivage des contrats terminés
	private List<ContratCadre<Feve>> archiveContratsFeves;
	private List<ContratCadre<Chocolat>> archiveContratsChocolat;
	
	protected StockEnVente<Chocolat> stockEnVente;
	
	// Historique des demandes (utilisée pour définir notre stratégie d'achat de fèves)
	protected HistoriqueDemande historiqueDemande;
	
	// Planning des stocks (il s'agit d'objectifs que l'on souhaite atteindre, estimés à l'aide de l'historique des demandes
	protected PlanningStock<Chocolat> planningStockChocolats;
	protected PlanningStock<Feve> planningStockFeves;
	
	// Constantes
	public static final int STEPS_PAR_ANNEE = 24;
	private static final double MAX_PRODUCTION_PAR_STEP = 10e3; // Production max. de chocolats par step, en kg
	protected static final int STEPS_ESTIMATION_DEMANDE_FUTURE = 12; // Le nombre de steps dans le futur pour lesquels on estime la demande
	private static final double MARGE_STOCK_CHOCOLAT = 0.05; // La marge de chocolat que l'on produit en plus de la demande estimée
	private static final double QTE_PRODUCTION_MIN = 10.0;
	
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
		this.journal = new Journal("Journal EQ4");
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
		
		// Initialisation des listes de contrats
		contratsFevesEnCours = new ArrayList<ContratCadre<Feve>>();
		contratsChocolatEnCours = new ArrayList<ContratCadre<Chocolat>>();
		archiveContratsFeves = new ArrayList<ContratCadre<Feve>>();
		archiveContratsChocolat = new ArrayList<ContratCadre<Chocolat>>();
		
		acheteurCC = new Transformateur2AcheteurCC(this);
		vendeurCC = new Transformateur2VendeurCC(this);
		
		// Initialisation des stocks
		stocksChocolat = new StockProduit<Chocolat>(CHOCOLATS_VENTE);
		stockFeves = new StockProduit<Feve>(FEVES_ACHAT);
		
		for(Chocolat c : CHOCOLATS_VENTE) {
			stocksChocolat.ajouterTas(c, new TasProduit<Chocolat>(10e3, 12.0));
			iStockChocolat.ajouter(this, 10e3);
		}
			
		// Initialisation de l'historique des demandes
		historiqueDemande = new HistoriqueDemande();
		
		// Initialisation des planning objectifs de stocks
		planningStockChocolats = new PlanningStock<Chocolat>();
		planningStockFeves = new PlanningStock<Feve>();
	}

	public void next() {
		journal.ajouter("=================== STEP " + Monde.LE_MONDE.getStep() + " ===================");
		
		/** Archivage des contrats terminés */
		archiverContratsTerminés(contratsFevesEnCours, archiveContratsFeves);
		archiverContratsTerminés(contratsChocolatEnCours, archiveContratsChocolat);
		
	
		/** ---- Transformations ---- (Kelian) */
		double qteTransformee = 0.0;
		double lastQteTransformee = 0.0;
		do {;
			lastQteTransformee = effectuerTransformation(); 
			qteTransformee += lastQteTransformee;
		} while(lastQteTransformee != 0.0 && qteTransformee < MAX_PRODUCTION_PAR_STEP);
		
		/** Prévision des stocks */ // TODO Ne pas le faire à chaque step
		estimerPlanningStockChocolat();
		verifierPlanningStockChocolat();
		calculerPlanningStockFeves();
		
		String str = "";
		for(Chocolat c : CHOCOLATS_VENTE)
			str += c + " = " + stocksChocolat.getQuantiteTotale(c) + "kg ; ";
		journal.ajouter("Stock chocolat : " + str);
		
		str = "";
		for(Feve f : FEVES_ACHAT)
			str += f + " = " + stockFeves.getQuantiteTotale(f) + "kg ; ";
		journal.ajouter("Stock fèves : " + str);
		
		str = "";
		for(Chocolat c : CHOCOLATS_VENTE)
			str += c + " = " + planningStockChocolats.getQuantite(c, Monde.LE_MONDE.getStep() + 1) + "kg ; ";
		journal.ajouter("Planning stock chocolat au prochain step : " + str);
	
		
	}

	// Kelian
	/** Archive les contrats terminés */
	private <T> void archiverContratsTerminés(List<ContratCadre<T>> contrats, List<ContratCadre<T>> archive) {
		List<ContratCadre<T>> aArchiver = new ArrayList<ContratCadre<T>>();
		for(ContratCadre<T> cc : contrats) {
			if(cc.getQuantiteRestantALivrer() <= 0.0 && cc.getMontantRestantARegler() <= 0.0)
				aArchiver.add(cc);
		}
		for(ContratCadre<T> cc : aArchiver) {
			archive.add(cc);
			contrats.remove(cc);
		}
	}
	
	// Kelian
	/** Effectue une transformation. Renvoie la quantité de chocolat produit */
	private double effectuerTransformation() {
		// On vérifie d'abord s'il y a des fèves que l'on doit absolument utiliser
		/** Les dates de péremption n'étant pas implémentées, il n'y aura jamais de telles fèves
		Feve f = getFeveCritique();
		if(f != null) {
			Recette r = Recette.getRecettes(f).get(0); // la liste ne contient normalement qu'un élément
			TasProduit<Feve> tas = stockFeves.getProchainTasPerime(f);
			double qte = 0;
			if(r.calculCoutTransformation(tas.getQuantité()) < soldeBancaire.getValeur() * 0.3)
				qte = tas.getQuantité(); // on transforme tout le tas
			else
				qte = r.getQteProductible(soldeBancaire.getValeur() * 0.3); // on transforme le plus possible jusqu'à 30% de notre solde
			
			if(qte < QTE_PRODUCTION_MIN)
				return 0.0;
			executerRecette(r, qte);
			return qte;
		}*/
		
		// On s'intéresse ensuite au chocolat que l'on doit produire en priorité pour satisfaire les échéances des CC en cours
		Pair<Chocolat, Double> c = getChocolatCritique();
		if(c != null && c.getX() != null) {
			List<Recette> recettes = Recette.getRecettes(c.getX());
			
			// On prend la recette pour laquelle on a le plus de stock de fèves
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
			
			double soldeMaxADepenser = soldeBancaire.getValeur();
			double qte = 0;
			if(maxRecette.calculCoutTransformation(c.getY()) <=  soldeMaxADepenser)
				qte = c.getY();
			else
				qte = maxRecette.getQteProductible(soldeMaxADepenser); // on transforme le plus possible jusqu'à 60% de notre sold

			if(qte < QTE_PRODUCTION_MIN)
				return 0.0;
			executerRecette(maxRecette, qte);
			return qte;
			
		}
		
		return 0;
	}
	
	// Kelian
	/** Exécute une recette (actualisation des stocks, du solde, ...). Renvoie true si la recette a bien été executée (false sinon; manque de stock par exemple) */
	private boolean executerRecette(Recette r, double qte) {
		double fevesNecessaires = r.getInputParKgProduit() * qte;
		
		// Vérification stock
		if(stockFeves.getQuantiteTotale(r.getInputFeve()) < fevesNecessaires)
			return false;
		
		// Vérification solde
		if(soldeBancaire.getValeur() < r.calculCoutTransformation(qte))
			return false;
		
		// Exécution stock
		double coutTransfo = r.calculCoutTransformation(qte);
		double coutTotal = stockFeves.getPrix(r.getInputFeve(), fevesNecessaires);
		stockFeves.prendreProduits(r.getInputFeve(), fevesNecessaires);
		iStockFeves.retirer(this, fevesNecessaires);
		stocksChocolat.ajouterTas(r.getOutput(), new TasProduit<Chocolat>(qte, qte / coutTotal));
		iStockChocolat.ajouter(this, qte);
		
		// Exécution solde
		soldeBancaire.retirer(this, coutTransfo);
		
		journal.ajouter("Production de " + qte + " kg de " + r.getOutput());
		
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
		int step = Monde.LE_MONDE.getStep();
		// On construit une HashMap qui contient, pour chaque CC en cours, le nombre d'échéances que l'on peut satisfaire avec le stock actuel
		HashMap<ContratCadre<Chocolat>, Integer> echeancesSatisfiables = new HashMap<ContratCadre<Chocolat>, Integer>();
		for(ContratCadre<Chocolat> cc : contratsChocolatEnCours) {
			Echeancier e = cc.getEcheancier();
			double qteDejaLivree = e.getQuantiteJusquA(step);
			double stock = stocksChocolat.getQuantiteTotale(cc.getProduit());
			int i = 1;
			// Tant que l'on n'est pas arrivé au bout de l'échéancier et que l'on a assez de stock
			while(i < e.getNbEcheances() && e.getQuantiteJusquA(step + i) - qteDejaLivree <= stock) {
				i++;
				stock -= e.getQuantite(step + i);
			}
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
	/** Estime les quantités de chocolat à stocker sur les prochains steps en utilisant des estimations calculées à partir des années précédentes. */
	private void estimerPlanningStockChocolat() {
		int step = Monde.LE_MONDE.getStep();
		for(Chocolat c : CHOCOLATS_VENTE) {
			for(int i = 1; i <= STEPS_ESTIMATION_DEMANDE_FUTURE; i++) {
				double demandeEstimee = historiqueDemande.estimerDemande(i, c);
				if(demandeEstimee == -1) // Pas encore d'échantillon pour pouvoir estimer
					planningStockChocolats.setQuantite(c, step + i, 0);
				else
					planningStockChocolats.setQuantite(c, step + i, demandeEstimee * (1 + MARGE_STOCK_CHOCOLAT));
			}
		}
	}
	
	// Kelian et Guillaume
	/** Cette fonction adapte le stock de chocolat préalablement calculé pour s'assurer qu'il satisfait tous les contrats en cours. */
	private void verifierPlanningStockChocolat() {
		int step = Monde.LE_MONDE.getStep();
		// On commence par regrouper les contrats en cours par type de chocolat
		HashMap<Chocolat, List<ContratCadre<Chocolat>>> contrats = new HashMap<Chocolat, List<ContratCadre<Chocolat>>>();
		for(ContratCadre<Chocolat> cc : contratsChocolatEnCours) {
			if(!contrats.containsKey(cc.getProduit()))
				contrats.put(cc.getProduit(), new ArrayList<ContratCadre<Chocolat>>());
			contrats.get(cc.getProduit()).add(cc);
		}
		
		for(Chocolat c : CHOCOLATS_VENTE) {
			if(!contrats.containsKey(c))
				continue;
			
			for(int i = 1; i <= STEPS_ESTIMATION_DEMANDE_FUTURE; i++) {
				// On additionne les quantités (pour ce step) associées à tous les contrats cadres pour ce type de chocolat
				double qteRequise = 0.0;
				for(ContratCadre<Chocolat> cc : contrats.get(c))
					qteRequise += cc.getEcheancier().getQuantite(step + i);
			
				if(qteRequise > planningStockChocolats.getQuantite(c, step + i))					
					planningStockChocolats.setQuantite(c, step + i, qteRequise);
			}	
		}
	}
	
	// Kelian
	/** Utilise le planning de stock de chocolats pour définir les quantités de fèves à stocker sur les prochains steps */
	private void calculerPlanningStockFeves() {
		int step = Monde.LE_MONDE.getStep();
		
		for(Chocolat c : CHOCOLATS_VENTE) {
			double qteChocolatEnStock = stocksChocolat.getQuantiteTotale(c);
			Recette r = getRecetteMoindreCout(c);
			planningStockFeves.reset(r.getInputFeve());
			
			for(int i = 1; i <= STEPS_ESTIMATION_DEMANDE_FUTURE; i++) {
				double qteChocolatNecessaire = planningStockChocolats.getQuantite(c, step + i);
				// Prise en compte du chocolat déjà présent dans notre stock
				double utilisationStock = Math.min(qteChocolatEnStock, qteChocolatNecessaire);
				qteChocolatEnStock -= utilisationStock;
				qteChocolatNecessaire -= utilisationStock;
				
				double qteFevesNecessaire = r.getInputParKgProduit() * qteChocolatNecessaire;
				planningStockFeves.addQuantite(r.getInputFeve(), step + i, qteFevesNecessaire);
			}
		}
	}
	
	// Kelian
	/** Renvoie la recette que l'on doit utiliser pour produire un type de chocolat au moindre coût, à l'aide du dernier prix d'achat de chaque fève. */
	private Recette getRecetteMoindreCout(Chocolat c) {
		List<Recette> recettes = Recette.getRecettes(c);
		double minPrix = Double.MAX_VALUE;
		Recette minPrixRecette = null;
		for(Recette r : recettes) {
			double prix = r.getInputParKgProduit() * acheteurCC.getDernierPrixAchat(r.getInputFeve());
			if(prix < minPrix) {
				minPrix = prix;
				minPrixRecette = r;
			}
		}
		return minPrixRecette;
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