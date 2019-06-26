package abstraction.eq4Transformateur2;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import abstraction.eq7Romu.produits.Chocolat;
import abstraction.eq7Romu.produits.Feve;
import abstraction.eq7Romu.ventesContratCadre.ContratCadre;
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
	private Indicateur iStockFeves;
	private Indicateur iStockChocolat;
	private Indicateur soldeBancaire;

	// Listes des produits échangés
	private List<Chocolat> CHOCOLATS_VENTE;
	private List<Feve> FEVES_ACHAT;
	
	// Stocks
	private StockProduit<Chocolat> stocksChocolat;
	private StockProduit<Feve> stockFeves;
	
	// Implémentations des échanges par contrats cadres
	private Transformateur2AcheteurCC acheteurCC;
	private Transformateur2VendeurCC vendeurCC;
	
	/// Stockage des contrats en cours
	private List<ContratCadre<Feve>> contratsFevesEnCours;
	private List<ContratCadre<Chocolat>> contratsChocolatEnCours;
	
	// Archivage des contrats terminés
	private List<ContratCadre<Feve>> archiveContratsFeves;
	private List<ContratCadre<Chocolat>> archiveContratsChocolat;
	
	// Historique des demandes (utilisée pour définir notre stratégie d'achat de fèves)
	private HistoriqueDemande historiqueDemande;
	
	// Planning des stocks (il s'agit d'objectifs que l'on souhaite atteindre, estimés à l'aide de l'historique des demandes
	private PlanningStock<Chocolat> planningStockChocolats;
	private PlanningStock<Feve> planningStockFeves;
	
	public Transformateur2() {
		// Initialisation et ajout des indicateurs
		this.iStockFeves = new Indicateur("EQ4 stock feves", this, 0);
		this.iStockChocolat = new Indicateur("EQ4 stock chocolat", this, 0);
		this.soldeBancaire = new Indicateur("EQ4 solde bancaire", this, ConfigEQ4.SOLDE_INITIAL);
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
			stocksChocolat.ajouterTas(c, new TasProduit<Chocolat>(ConfigEQ4.STOCK_INITIAL_QTE_PAR_CHOCOLAT, 12.0));
			iStockChocolat.ajouter(this, ConfigEQ4.STOCK_INITIAL_QTE_PAR_CHOCOLAT);
		}
			
		// Initialisation de l'historique des demandes
		historiqueDemande = new HistoriqueDemande();
		
		// Initialisation des planning objectifs de stocks
		planningStockChocolats = new PlanningStock<Chocolat>();
		planningStockFeves = new PlanningStock<Feve>();
	}

	public void next() {
		journal.ajouter("====================================== STEP " + Monde.LE_MONDE.getStep() + " ======================================");
		
		/** Archivage des contrats terminés */
		archiverContratsTerminés(contratsFevesEnCours, archiveContratsFeves);
		archiverContratsTerminés(contratsChocolatEnCours, archiveContratsChocolat);
		
	
		/** ---- Transformations ---- (Kelian) */
		double qteTransformee = 0.0;
		double lastQteTransformee = 0.0;
		
		// TODO Ne pas s'arrêter dès qu'une quantité est nulle (laisser essayer d'autres chocolats)
		
		do {
			lastQteTransformee = LogiqueTransformations.effectuerTransformation(); 
			qteTransformee += lastQteTransformee;
		} while(lastQteTransformee != 0.0 && qteTransformee < ConfigEQ4.MAX_PRODUCTION_PAR_STEP);
		
		/** Prévision des stocks */ // TODO Ne pas le faire à chaque step si jamais le temps d'éxécution est trop grand
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
	
		str = "";
		for(Feve f : FEVES_ACHAT)
			str += f + " = " + planningStockFeves.getQuantite(f, Monde.LE_MONDE.getStep() + 1) + "kg ; ";
		journal.ajouter("Planning stock fèves au prochain step : " + str);
		
		journal.ajouter("=====================================================================================");
		
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
	/** Estime les quantités de chocolat à stocker sur les prochains steps en utilisant des estimations calculées à partir des années précédentes. */
	private void estimerPlanningStockChocolat() {
		int step = Monde.LE_MONDE.getStep();
		for(Chocolat c : CHOCOLATS_VENTE) {
			for(int i = 1; i <= ConfigEQ4.STEPS_ESTIMATION_DEMANDE_FUTURE; i++) {
				double demandeEstimee = historiqueDemande.estimerDemande(i, c);
				if(demandeEstimee == -1) // Pas encore d'échantillon pour pouvoir estimer
					planningStockChocolats.setQuantite(c, step + i, 0);
				else
					planningStockChocolats.setQuantite(c, step + i, demandeEstimee);
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
			
			for(int i = 1; i <= ConfigEQ4.STEPS_ESTIMATION_DEMANDE_FUTURE; i++) {
				// On additionne les quantités (pour ce step) associées à tous les contrats cadres pour ce type de chocolat
				double qteRequise = 0;
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
			double qteChocolatEnStock = stocksChocolat.getQuantiteTotale(c) - ConfigEQ4.FONDS_ROULEMENT_CHOCOLAT;
			Recette r = Recette.getRecetteMoindreCout(c, this);
			planningStockFeves.reset(r.getInputFeve());
			
			for(int i = 1; i <= ConfigEQ4.STEPS_ESTIMATION_DEMANDE_FUTURE; i++) {
				double qteChocolatNecessaire = planningStockChocolats.getQuantite(c, step + i);
				
				// Prise en compte du fonds de roulement à satisfaire
				//if(i == 1 && qteChocolatEnStock < ConfigEQ4.FONDS_ROULEMENT_CHOCOLAT)
				//	qteChocolatNecessaire += (ConfigEQ4.FONDS_ROULEMENT_CHOCOLAT- qteChocolatEnStock);
				
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
	/** Renvoie le step actuel dans l'année en cours */
	public static int getCurrentStepInAnnee() {
		return Monde.LE_MONDE.getStep() % ConfigEQ4.STEPS_PAR_ANNEE;
	}
	
	/** Fonctions relatives à IAcheteurContratCadre<Feve> */
	public Transformateur2AcheteurCC getAcheteurCC() {
		return acheteurCC;
	}
	public ContratCadre<Feve> getNouveauContrat() {return acheteurCC.getNouveauContrat();}
	public void proposerEcheancierAcheteur(ContratCadre<Feve> cc) {acheteurCC.proposerEcheancierAcheteur(cc);}
	public void proposerPrixAcheteur(ContratCadre<Feve> cc) {acheteurCC.proposerPrixAcheteur(cc);}
	public void notifierAcheteur(ContratCadre<Feve> cc) {acheteurCC.notifierAcheteur(cc);}
	public void receptionner(Feve p, double qte, ContratCadre<Feve> cc) {acheteurCC.receptionner(p, qte, cc);}
	public double payer(double montant, ContratCadre<Feve> cc) {return acheteurCC.payer(montant, cc);}
	
	/** Fonctions relatives à IVendeurContratCadre<Chocolat> */
	public Transformateur2VendeurCC getVendeurCC() {
		return vendeurCC;
	}
	public List<Chocolat> getProduitsEnVente() {return vendeurCC.getProduitsEnVente();}
	public StockEnVente<Chocolat> getStockEnVente() {return vendeurCC.getStockEnVente();}
	public double getPrix(Chocolat produit, Double quantite) {return vendeurCC.getPrix(produit, quantite);}
	public void proposerEcheancierVendeur(ContratCadre<Chocolat> cc) {vendeurCC.proposerEcheancierVendeur(cc);}
	public void proposerPrixVendeur(ContratCadre<Chocolat> cc) {vendeurCC.proposerPrixVendeur(cc);}
	public void notifierVendeur(ContratCadre<Chocolat> cc) {vendeurCC.notifierVendeur(cc);}
	public double livrer(Chocolat p, double qte, ContratCadre<Chocolat> cc) {return vendeurCC.livrer(p, qte, cc);}
	public void encaisser(double montant, ContratCadre<Chocolat> cc) {vendeurCC.encaisser(montant, cc);}
	
	/** Getters */
	public Journal getJournal() {
		return journal;
	}
	protected Indicateur getSoldeBancaire() {
		return soldeBancaire;
	}
	protected Indicateur getIndicateurStockFeves() {
		return iStockFeves;
	}
	protected Indicateur getIndicateurStockChocolat() {
		return iStockChocolat;
	}
	protected StockProduit<Feve> getStockFeves() {
		return stockFeves;
	}
	protected StockProduit<Chocolat> getStocksChocolat() {
		return stocksChocolat;
	}
	protected List<ContratCadre<Chocolat>> getContratsChocolatEnCours() {
		return contratsChocolatEnCours;
	}
	protected List<ContratCadre<Feve>> getContratsFevesEnCours() {
		return contratsFevesEnCours;
	}
	protected List<Chocolat> getCHOCOLATS_VENTE() {
		return CHOCOLATS_VENTE;
	}
	protected List<Feve> getFEVES_ACHAT() {
		return FEVES_ACHAT;
	}
	protected HistoriqueDemande getHistoriqueDemande() {
		return historiqueDemande;
	}
	protected PlanningStock<Chocolat> getPlanningStockChocolats() {
		return planningStockChocolats;
	}
	protected PlanningStock<Feve> getPlanningStockFeves() {
		return planningStockFeves;
	}
}