package abstraction.eq5Distributeur1;

import java.util.ArrayList;
import java.util.List;

import abstraction.eq7Romu.acteurs.ProducteurRomu;
import abstraction.eq7Romu.distributionChocolat.IDistributeurChocolat;
import abstraction.eq7Romu.produits.Chocolat;
import abstraction.eq7Romu.produits.Feve;
import abstraction.eq7Romu.produits.Gamme;
import abstraction.eq7Romu.ventesContratCadre.ContratCadre;
import abstraction.eq7Romu.ventesContratCadre.Echeancier;
import abstraction.eq7Romu.ventesContratCadre.IAcheteurContratCadre;
import abstraction.eq7Romu.ventesContratCadre.IVendeurContratCadre;
import abstraction.eq7Romu.ventesContratCadre.StockEnVente;
import abstraction.eq7Romu.ventesContratCadre.SuperviseurVentesContratCadre;
import abstraction.fourni.IActeur;
import abstraction.fourni.Indicateur;
import abstraction.fourni.Journal;
import abstraction.fourni.Monde;

public class Distributeur1 implements IActeur, IAcheteurContratCadre, IDistributeurChocolat, IPublicitaire {
	private Journal journal;
	private Stock stock;
	private CompteBancaire soldeBancaire;
	private Double marge;
	private Indicateur indicateursolde;
	private List<ContratCadre<Chocolat>> contratsEnCours;

	private List<Publicite> publicites;
	private double coutfixe;
	private double coutsdestockage;
	private double soldeDebutStep;

	public static final int JANVIER1 = 1;
	public static final int JANVIER2 = 2;
	public static final int FEVRIER1 = 3;
	public static final int FEVRIER2 = 4;
	public static final int MARS1 = 5;
	public static final int MARS2 = 6;
	public static final int AVRIL1 = 7;
	public static final int AVRIL2 = 8;
	public static final int MAI1 = 9;
	public static final int MAI2 = 10;
	public static final int JUIN1 = 11;
	public static final int JUIN2 = 12;
	public static final int JUILLET1 = 13;
	public static final int JUILLET2 = 14;
	public static final int AOUT1 = 15;
	public static final int AOUT2 = 16;
	public static final int SEPTEMBRE1 = 17;
	public static final int SEPTEMBRE2 = 18;
	public static final int OCTOBRE1 = 19;
	public static final int OCTOBRE2 = 20;
	public static final int NOVEMBRE1 = 21;
	public static final int NOVEMBRE2 = 22;
	public static final int DECEMBRE1 = 23;
	public static final int DECEMBRE2 = 24;

	/**
	 * @author Erine DUPONT & Estelle BONNET
	 */
	public Distributeur1() {
		this(0.6, 1000000.0); // La marge doit être en pourcentage !!! 5% > 0.05
	}

	/**
	 * @author Erine DUPONT & Estelle BONNET
	 */
	public Distributeur1(double marge, Double soldeInitial) {

		this.marge = marge;   // La marge doit être en pourcentage !!! 5% > 0.05
		this.coutfixe = 0.17;	
		this.coutsdestockage = 0.1;
		this.stock = new Stock();
		stock.ajouter(Chocolat.HG_E_SHP, 150000.0, this);
		stock.ajouter(Chocolat.MG_E_SHP, 55000.0, this);
		stock.ajouter(Chocolat.MG_NE_HP, 55000.0, this);
		stock.ajouter(Chocolat.MG_NE_SHP, 55000.0, this);
		publicites = new ArrayList<Publicite>();

		this.soldeBancaire = new CompteBancaire(this.getNom(), this, soldeInitial);
		this.indicateursolde = new Indicateur ("EQ5 solde bancaire",this, soldeBancaire.getCompteBancaire());
		this.soldeDebutStep= this.soldeBancaire.getCompteBancaire();
		Monde.LE_MONDE.ajouterIndicateur(indicateursolde);
		this.journal = new Journal("Journal "+this.getNom());
		Monde.LE_MONDE.ajouterJournal(this.journal);
		this.contratsEnCours = new ArrayList<ContratCadre<Chocolat>>();
		Monde.LE_MONDE.ajouterActeur(new ClientEuropeen(1000, "Europe"));
		Monde.LE_MONDE.ajouterActeur(new ClientEuropeen(1000, "Asie"));
		Monde.LE_MONDE.ajouterActeur(new ClientFidele(this, Chocolat.MG_E_SHP, 1000));
		Monde.LE_MONDE.ajouterActeur(new ClientFidele(this, Chocolat.MG_NE_HP, 1000));
		Monde.LE_MONDE.ajouterActeur(new ClientFidele(this, Chocolat.MG_NE_SHP, 1000));
		Monde.LE_MONDE.ajouterActeur(new ClientFidele(this, Chocolat.HG_E_SHP, 1000));
	}

	public String getNom() {
		return "EQ5";
	}


	/**
	 * @author Estelle Bonnet
	 */
	public void initialiser() {
		this.soldeDebutStep = this.soldeBancaire.getCompteBancaire();
		stock.initialisationDebutStep(stock.getStock());
	}

	/**
	 * @author Estelle Bonnet > couts fixes, couts variables
	 * @author Erine DUPONT > pub
	 */
	public void next() {
		//Prise en compte de coût fixe
		this.soldeBancaire.retirer(this, ((this.soldeBancaire.getCompteBancaire()-this.soldeDebutStep)*this.coutfixe));
		this.indicateursolde.retirer(this, ((this.soldeBancaire.getCompteBancaire()-this.soldeDebutStep)*this.coutfixe));
		//Prise en compte de la pérénité des stocks
		this.stock.perenniteStock(this.stock.getStock(), this);
		//Prise en compte du coût du stock

		//------------------ Publicité -----------------------------------------------------
		//Janvier 	Step 1 à 4					Juillet 	Step 25 à 28
		//Février 	Step 5 à 8					Août		Step 29 à 32
		//Mars 		Step 9 à 12					Septembre	Step 33 à 36
		//Avril		Step 13 à 16				Octobre		Step 37 à 40
		//Mai 		Step 17 à 20				Novembre	Step 41 à 44
		//Juin		Step 21 à 24				Décembre	Step 45 à 48
		int step_actuel = Monde.LE_MONDE.getStep();
		int stepDansLAnnee = step_actuel%24;
		publicites.clear();
		if (stepDansLAnnee == JANVIER2 || stepDansLAnnee == FEVRIER1) {
			Publicite pub1 = new Publicite(Chocolat.HG_E_SHP, 10000, "Europe");
			this.journal.ajouter("-------------------------------------------- PUB -------------------------------------------------------------------------------------------");
			this.journal.ajouter("Publicité de la Saint-Valentin:");
			this.journal.ajouter(pub1.toString());
			publicites.clear();
			publicites.add(pub1);
		}
		if (stepDansLAnnee == MARS1 || stepDansLAnnee == MARS2 || stepDansLAnnee == AVRIL1) {
			Publicite pub1 = new Publicite(Chocolat.HG_E_SHP, 7000, "Europe");
			Publicite pub2 = new Publicite(Chocolat.MG_E_SHP, 5000, "Europe");
			Publicite pub3 = new Publicite(Chocolat.MG_NE_SHP, 3000, "Europe");
			this.journal.ajouter("-------------------------------------------- PUB -------------------------------------------------------------------------------------------");
			this.journal.ajouter("Publicité de Pâques:");
			this.journal.ajouter(pub1.toString());
			this.journal.ajouter(pub2.toString());
			this.journal.ajouter(pub3.toString());
			publicites.clear();
			publicites.add(pub1);
			publicites.add(pub2);
			publicites.add(pub3);
		}
		if (stepDansLAnnee == NOVEMBRE1 || stepDansLAnnee == NOVEMBRE2 || stepDansLAnnee == DECEMBRE1 
				|| stepDansLAnnee == DECEMBRE2) {
			Publicite pub1 = new Publicite(Chocolat.HG_E_SHP,7000,"Europe");
			Publicite pub2 = new Publicite(Chocolat.MG_E_SHP,5000,"Europe");
			Publicite pub3 = new Publicite(Chocolat.MG_NE_HP,5000,"Europe");
			Publicite pub4 = new Publicite(Chocolat.MG_NE_SHP,5000,"Europe");
			this.journal.ajouter("-------------------------------------------- PUB -------------------------------------------------------------------------------------------");
			this.journal.ajouter("Publicité de Noël:");
			this.journal.ajouter(pub1.toString());
			this.journal.ajouter(pub2.toString());
			this.journal.ajouter(pub3.toString());
			this.journal.ajouter(pub4.toString());
			publicites.clear();
			publicites.add(pub1);
			publicites.add(pub2);
			publicites.add(pub3);
			publicites.add(pub4);
		}
	}

	// ------------------------------------------------------------------------------------------------------
	// ACHETEUR
	// ------------------------------------------------------------------------------------------------------ 

	@Override
	/**
	 * @author Erine DUPONT
	 */
	public ContratCadre<Chocolat> getNouveauContrat() {
		/* -----------------------------------------------------------------------------------------
		 * V1 ERINE
		// On va créer un nouveau contrat cadre 
		ContratCadre<Chocolat> ncc = null;
		// Au préalable, il faut identifier produit, quantité, vendeur, acheteur

		// On détermine combien il resterait sur le compte si on soldait tous les contrats en cours.
		double solde = this.soldeBancaire.getCompteBancaire();
		for (ContratCadre<Chocolat> cc : this.contratsEnCours) {
			solde = solde - cc.getMontantRestantARegler();
		}

		// On ne cherche pas a établir d'autres contrats d'achat si le compte bancaire est trop bas
		if (solde>5000.0) { 

			//Choix du produit : on choisit un produit au hasard parmi tous les produits
			ArrayList<Chocolat> produits = new ArrayList<Chocolat>();
			produits.add(Chocolat.HG_E_SHP);
			produits.add(Chocolat.MG_E_SHP);
			produits.add(Chocolat.MG_NE_HP);
			produits.add(Chocolat.MG_NE_SHP);
			Chocolat produit = produits.get((int) Math.random()*produits.size());


			//Choix quantité : on choisit le vendeur ayant la plus grande quantité du produit
			//Choix acteur
			List<IVendeurContratCadre<Chocolat>> vendeurs = new ArrayList<IVendeurContratCadre<Chocolat>>();
			for (IActeur acteur : Monde.LE_MONDE.getActeurs()) {
				if (acteur instanceof IVendeurContratCadre) {
					IVendeurContratCadre vacteur = (IVendeurContratCadre) acteur;
					StockEnVente<Chocolat> stock = vacteur.getStockEnVente();
					if (stock.get(produit)>100.0) { // on souhaite faire des contrats d'au moins 100kg
						vendeurs.add((IVendeurContratCadre<Chocolat>)vacteur);
					}
				}
			}
			if (vendeurs.size()>1) { // On choisit le vendeur ayant le plus gros stock de produit
				IVendeurContratCadre<Chocolat> vendeur_choisi = vendeurs.get(0); 
				for (IVendeurContratCadre<Chocolat> vendeur : vendeurs) {
					double stock = vendeur_choisi.getStockEnVente().get(produit);
					if (vendeur.getStockEnVente().get(produit) > stock) {
						stock = vendeur.getStockEnVente().get(produit);
						vendeur_choisi = vendeur;
					}
				}
				double quantite = vendeur_choisi.getStockEnVente().get(produit)*0.65; // On prend 65% de sa production
				ncc = new ContratCadre<Chocolat>(this, vendeur_choisi, produit, quantite);
				this.journal.ajouter("Nouveau contrat cadre signé avec " + vendeur_choisi + 
						". Chocolat: "+ produit+ "/ Quantité: "+ quantite);
			} else {
				this.journal.ajouter("   Il ne reste que "+solde+" une fois tous les contrats payes donc nous ne souhaitons "
						+ "pas en créer d'autres pour l'instant");
			}
		}
		//Création Contrat
		return ncc; 
		 */
		/* --------------------------------------------------------------------------------------------------------
		 * V2 ERINE
		 */
		// On va créer un nouveau contrat cadre 
		ContratCadre<Chocolat> ncc = null;
		this.journal.ajouter("------------------------------------- Démarrage d'un nouveau contrat cadre ------------------------------------------------");
		// Au préalable, il faut identifier produit, quantité, vendeur, acheteur

		// On détermine combien il resterait sur le compte si on soldait tous les contrats en cours.
		double solde = this.soldeBancaire.getCompteBancaire();
		this.journal.ajouter("Le solde actuel est de " + solde + " €");
		for (ContratCadre<Chocolat> cc : this.contratsEnCours) {
			solde = solde - cc.getMontantRestantARegler();
		}
		this.journal.ajouter("Le solde une fois tous les contrats payés est de " + solde + " €");
		// On ne cherche pas a établir d'autres contrats d'achat si le compte bancaire est trop bas
		if (solde > 100000.0) { 
			this.journal.ajouter("On négocie donc un nouveau contrat cadre");
			//Choix du produit : on choisit le produit pour lequel on a le moins de stock
			Chocolat produit = this.stock.getProduitsEnVente().get(0);
			for (Chocolat nouvproduit : this.stock.getProduitsEnVente()) {
				if (this.stock.get(nouvproduit)<this.stock.get(produit)) {
					produit = nouvproduit;
				}
				if (this.stock.get(nouvproduit) == this.stock.get(produit)) { 
					/*Si deux produits ont le moins de stock, on en choisit un des 2 au hasard */
					ArrayList<Chocolat> liste = new ArrayList<Chocolat>();
					liste.add(produit);
					liste.add(nouvproduit);
					produit = liste.get((int)Math.random()*2);
				}
			}
			this.journal.ajouter("Choix du produit: "+ produit);

			//Choix acteur : on choisit le vendeur ayant le meilleur rapport quantité/prix du produit
			List<IVendeurContratCadre<Chocolat>> vendeurs = new ArrayList<IVendeurContratCadre<Chocolat>>();
			for (IActeur acteur : Monde.LE_MONDE.getActeurs()) {
				if (acteur instanceof IVendeurContratCadre) {
					IVendeurContratCadre vacteur = (IVendeurContratCadre) acteur;
					StockEnVente<Chocolat> stock = vacteur.getStockEnVente();
					if (stock.get(produit)>0.0) { // on souhaite faire des contrats d'au moins 100kg
						vendeurs.add((IVendeurContratCadre<Chocolat>)vacteur);
					}
				}
			}
			this.journal.ajouter("La liste des vendeurs possibles est : " + vendeurs);

			if (vendeurs.size()>=1) { 
				IVendeurContratCadre<Chocolat> vendeur_choisi = vendeurs.get(0); 
				double stock_vendeur = vendeur_choisi.getStockEnVente().get(produit);
				double prix_vendeur = vendeur_choisi.getPrix(produit, stock_vendeur);
				this.journal.ajouter("Le prix de " + vendeur_choisi +" est de " + prix_vendeur + " €");
				for (IVendeurContratCadre<Chocolat> vendeur : vendeurs) {
					double stock = vendeur.getStockEnVente().get(produit);
					double prix = vendeur.getPrix(produit, stock);
					this.journal.ajouter("Le prix de " + vendeur + " est de " + prix + " €");;
					if (prix < prix_vendeur) {
						vendeur_choisi = vendeur;
						prix_vendeur = prix;
						this.journal.ajouter("Le prix est plus bas, le vendeur est donc " + vendeur_choisi);
					} else {
						this.journal.ajouter("Le prix est plus élevé, le vendeur reste " + vendeur_choisi);
					}
				}

				// Choix quantité
				double quantite = 0.0;
				/* V2 Erine
				 if (this.stock.get(produit) <= 50000) {
					if (vendeur_choisi.getStockEnVente().get(produit) >= 40000) {
						quantite = 40000;
					} else {
						quantite = vendeur_choisi.getStockEnVente().get(produit);
					}
				} else {
					if (vendeur_choisi.getStockEnVente().get(produit) >= 20000) {
						quantite = 20000;
					} else {
						quantite = vendeur_choisi.getStockEnVente().get(produit);
					}
				}*/
				/* V3 Erine */
				double qrestante = 0.0;
				for (ContratCadre<Chocolat> cc : this.contratsEnCours) {
					if (cc.getProduit() == produit) {
						qrestante += cc.getQuantiteRestantALivrer();
					}
				}
				if (qrestante >= this.stock.get(produit)) {
					if (vendeur_choisi.getStockEnVente().get(produit) >= qrestante + 50000) {
						quantite = qrestante + 50000;
					} else if (vendeur_choisi.getStockEnVente().get(produit) >= qrestante + 20000){
						quantite = vendeur_choisi.getStockEnVente().get(produit) + 20000;
					} else {
						quantite = vendeur_choisi.getStockEnVente().get(produit);
					}
				} else {
					if (this.stock.get(produit) <= 50000) {
						if (vendeur_choisi.getStockEnVente().get(produit) >= 40000) {
							quantite = 40000;
						} else {
							quantite = vendeur_choisi.getStockEnVente().get(produit);
						}
					} else {
						if (vendeur_choisi.getStockEnVente().get(produit) >= 20000) {
							quantite = 20000;
						} else {
							quantite = vendeur_choisi.getStockEnVente().get(produit);
						}
					}
				}
				this.journal.ajouter("La quantité demandée est " + quantite);
				ncc = new ContratCadre<Chocolat>(this, vendeur_choisi, produit, quantite);
			}  
		} else {
			this.journal.ajouter("   Il ne reste moins de 100000 € une fois tous les contrats payes donc nous ne souhaitons "
					+ "pas en créer d'autres pour l'instant"); 
		}

		//Création Contrat
		return ncc; 
	}

	/**
	 * @author Imane ZRIAA
	 */
	public void proposerEcheancierAcheteur(ContratCadre C) {
		/*if (C!=null) {
			Echeancier e = C.getEcheancier() ;
			if (e==null ) {//pas de contre-proposition
				C.ajouterEcheancier(new Echeancier(Monde.LE_MONDE.getStep(), 5, C.getQuantite()/5));
			} else {
				if( e.getQuantiteTotale() > C.getQuantite() ) {
					C.ajouterEcheancier(new Echeancier(C.getEcheancier())); 
				}	
				this.journal.ajouter("Contrat n° " + C.getNumero() + " avec " + C.getEcheancier().getNbEcheances()+ " échéances");
			}
		}
		 */
		/* -------------------------------------------------------------------------- 
		 * V2 ERINE
		 */
		if (C != null) {
			Echeancier e_vendeur = C.getEcheancier();
			Object produit = C.getProduit();
			Double stock = this.getStockEnVente().get((Chocolat) produit);
			Double quantite = C.getQuantite();
			//On regarde la quantité de produit à vendre avec les autres contrats cadres
			double qcc = 0.0;
			for (ContratCadre cc : this.contratsEnCours) {
				qcc += cc.getQuantiteRestantALivrer();
			}
			this.journal.ajouter("La quantité de " + produit + " restant à livrer pour les autres contrats est " + qcc + " kg");
			double qtot = qcc + quantite;
			if (qcc + quantite <= 0.25*stock) {
				this.journal.ajouter("La quantité demandée ajoutée à la quantité restant à livrer (" + qtot +") est inférieure "
						+ "à 25% du stock ("+ stock + ")");
				ArrayList<Integer> e1 = new ArrayList<Integer>();
				e1.add(1);
				e1.add(2);
				e1.add(3);
				e1.add(4);
				e1.add(5);
				int echeance = e1.get((int)Math.random()*5);
				Echeancier e_possible = new Echeancier(Monde.LE_MONDE.getStep(), echeance, 
						C.getQuantite()/echeance);
				if (e_vendeur != null && e_vendeur.getNbEcheances() >= e_possible.getNbEcheances()) {
					this.journal.ajouter("L'échéancier possible est: " + e_possible.getNbEcheances() + " échéances");
					this.journal.ajouter("L'échéancier vendeur est: " + e_vendeur.getNbEcheances() + " échéances" );
					C.ajouterEcheancier(e_vendeur);
					this.journal.ajouter("On accepte l'échéancier du vendeur");
				} else if (e_vendeur != null && e_vendeur.getNbEcheances() < e_possible.getNbEcheances()) {
					this.journal.ajouter("L'échéancier possible est: " + e_possible.getNbEcheances() + " échéances");
					this.journal.ajouter("L'échéancier vendeur est: " + e_vendeur.getNbEcheances() + " échéances" );
					C.ajouterEcheancier(e_possible);
					this.journal.ajouter("L'échéancier proposé par le vendeur est trop court, on propose " 
							+ e_possible.toString());
				} else {
					C.ajouterEcheancier(e_possible);
					this.journal.ajouter("On propose "+ e_possible.toString());
				}
			} else if (quantite + qcc <= 0.5*stock) {
				this.journal.ajouter("La quantité demandée ajoutée à la quantité restant à livrer " + qtot + " est inférieure "
						+ "à 50% du stock ("+ stock + ")");
				ArrayList<Integer> e2 = new ArrayList<Integer>();
				e2.add(6);
				e2.add(7);
				e2.add(8);
				e2.add(9);
				e2.add(10);
				int echeance = e2.get((int)Math.random()*5);
				Echeancier e_possible = new Echeancier(Monde.LE_MONDE.getStep(), echeance, 
						C.getQuantite()/echeance);
				if (e_vendeur != null && e_vendeur.getNbEcheances() >= e_possible.getNbEcheances()) {
					this.journal.ajouter("L'échéancier possible est: " + e_possible.getNbEcheances() + " échéances");
					this.journal.ajouter("L'échéancier vendeur est: " + e_vendeur.getNbEcheances() + " échéances" );
					C.ajouterEcheancier(e_vendeur);
					this.journal.ajouter("On accepte l'échéancier du vendeur");
				} else if (e_vendeur != null && e_vendeur.getNbEcheances() < e_possible.getNbEcheances()){
					this.journal.ajouter("L'échéancier possible est: " + e_possible.getNbEcheances() + " échéances");
					this.journal.ajouter("L'échéancier vendeur est: " + e_vendeur.getNbEcheances() + " échéances" );
					C.ajouterEcheancier(e_possible);
					this.journal.ajouter("L'échéancier proposé par le vendeur est trop court, on propose " 
							+ e_possible.toString());
				} else {
					C.ajouterEcheancier(e_possible);
					this.journal.ajouter("On propose "+ e_possible.toString());
				}
			} else {
				this.journal.ajouter("La quantité demandée ajoutée à la quantité restant à livrer (" + qtot + ") est supérieure "
						+ "à 50% du stock (" + stock + ")");
				ArrayList<Integer> e3 = new ArrayList<Integer>();
				e3.add(8);
				e3.add(9);
				e3.add(10);
				e3.add(11);
				e3.add(12);
				int echeance = e3.get((int)Math.random()*5);
				Echeancier e_possible = new Echeancier(Monde.LE_MONDE.getStep(), echeance, 
						C.getQuantite()/echeance);
				if (e_vendeur != null && e_vendeur.getNbEcheances() >= e_possible.getNbEcheances()) {
					this.journal.ajouter("L'échéancier possible est: " + e_possible.getNbEcheances() + " échéances");
					this.journal.ajouter("L'échéancier vendeur est: " + e_vendeur.getNbEcheances() + " échéances" );
					C.ajouterEcheancier(e_vendeur);
					this.journal.ajouter("On accepte l'échéancier du vendeur");
				} else if (e_vendeur != null && e_vendeur.getNbEcheances() >= e_possible.getNbEcheances()){
					this.journal.ajouter("L'échéancier possible est: " + e_possible.getNbEcheances() + " échéances");
					this.journal.ajouter("L'échéancier vendeur est: " + e_vendeur.getNbEcheances() + " échéances" );
					C.ajouterEcheancier(e_possible);
					this.journal.ajouter("L'échéancier proposé par le vendeur est trop court, on propose " 
							+ e_possible.toString());
				} else {
					C.ajouterEcheancier(e_possible);
					this.journal.ajouter("On propose "+ e_possible.toString());
				}
			}
		}
	}



	/**
	 * @author Imane ZRIAA
	 * @author2 Erine DUPONT
	 */
	public void proposerPrixAcheteur(ContratCadre cc) {
		double prixVendeur = cc.getPrixAuKilo();
		/* ------------------------------------------------------------------------------------------
		 * VERSION IMANE
		 * if (Math.random()<0.30) { 
			cc.ajouterPrixAuKilo(cc.getPrixAuKilo());
		} else {
			cc.ajouterPrixAuKilo((prixVendeur*(0.9+Math.random()*0.1))); // Rabais de 10% max
		}*/
		/* ------------------------------------------------------------------------------------------
		 * V1 ERINE
		 if (5 < prixVendeur && prixVendeur < 10 && stock.get((Chocolat) cc.getProduit())<1000) {
			cc.ajouterPrixAuKilo(prixVendeur*0.8);
			this.journal.ajouter("Nous proposons un prix de " + prixVendeur*0.8);
		} else if (5 < prixVendeur && prixVendeur < 10 && stock.get((Chocolat) cc.getProduit())>=1000) {
			cc.ajouterPrixAuKilo(prixVendeur*0.6);
			this.journal.ajouter("Nous proposons un prix de " + prixVendeur*0.6);
		} else if (prixVendeur <= 5) {
			cc.ajouterPrixAuKilo(prixVendeur);
			this.journal.ajouter("Nous proposons un prix de " + prixVendeur);
		} else {
			this.journal.ajouter("Nous refusons le prix de " + prixVendeur);
		} */
		/* ------------------------------------------------------------------------------------------
		 V2 ERINE
		 */
		if (cc.getProduit().equals(Chocolat.HG_E_SHP)) {
			if (20.0 < prixVendeur && prixVendeur <= 70.0 && stock.get((Chocolat) cc.getProduit()) < 10000) {
				cc.ajouterPrixAuKilo(prixVendeur*0.95);
				this.journal.ajouter("Nous proposons un prix de " + prixVendeur*0.95 + " €");
			} else if (20.0 < prixVendeur && prixVendeur <= 70.0 && stock.get((Chocolat) cc.getProduit())>=10000) {
				cc.ajouterPrixAuKilo(prixVendeur*0.8);
				this.journal.ajouter("Nous proposons un prix de " + prixVendeur*0.8 + " €");
			} else if (prixVendeur <= 20.0) {
				cc.ajouterPrixAuKilo(prixVendeur);
				this.journal.ajouter("Nous proposons un prix de " + prixVendeur + " €");
			} else {
				this.journal.ajouter("Nous refusons le prix de " + prixVendeur + " €" );
			}	
		} else if (cc.getProduit().equals(Chocolat.MG_E_SHP) || cc.getProduit().equals(Chocolat.MG_NE_SHP)) {
			if (10.0 < prixVendeur && prixVendeur <= 50.0 && stock.get((Chocolat) cc.getProduit()) < 10000) {
				cc.ajouterPrixAuKilo(prixVendeur*0.8);
				this.journal.ajouter("Nous proposons un prix de " + prixVendeur*0.8 + " €");
			} else if (10.0 < prixVendeur && prixVendeur <= 50.0 && stock.get((Chocolat) cc.getProduit())>=10000) {
				cc.ajouterPrixAuKilo(prixVendeur*0.7);
				this.journal.ajouter("Nous proposons un prix de " + prixVendeur*0.6 + " €");
			} else if (prixVendeur <= 10.0) {
				cc.ajouterPrixAuKilo(prixVendeur);
				this.journal.ajouter("Nous proposons un prix de " + prixVendeur + " €");
			} else {
				this.journal.ajouter("Nous refusons le prix de " + prixVendeur + " €");
			}	
		} else if (cc.getProduit().equals(Chocolat.MG_NE_HP)) {
			if (10.0 < prixVendeur && prixVendeur <= 40.0 && stock.get((Chocolat) cc.getProduit()) < 10000) {
				cc.ajouterPrixAuKilo(prixVendeur*0.8);
				this.journal.ajouter("Nous proposons un prix de " + prixVendeur*0.8 + " €");
			} else if (10.0 < prixVendeur && prixVendeur <= 40.0 && stock.get((Chocolat) cc.getProduit())>=10000) {
				cc.ajouterPrixAuKilo(prixVendeur*0.6);
				this.journal.ajouter("Nous proposons un prix de " + prixVendeur*0.6 + " €");
			} else if (prixVendeur <= 10.0) {
				cc.ajouterPrixAuKilo(prixVendeur);
				this.journal.ajouter("Nous proposons un prix de " + prixVendeur + " €");
			} else {
				this.journal.ajouter("Nous refusons le prix de " + prixVendeur + " €");
			}	
		}
	}

	/** 
	 * @author Erine DUPONT
	 * @author2 Imane : Ajout du journal 
	 */
	public void notifierAcheteur(ContratCadre cc) {
		if (cc!=null) {
			this.contratsEnCours.add(cc);
			this.journal.ajouter("Le contrat n° " + cc.getNumero()+ " est signé");
		}
	}

	/**@author Erine DUPONT / Imane ZRIAA 
	 */
	public void receptionner(Object produit, double quantite, ContratCadre cc) {
		if (produit==null || !produit.equals(cc.getProduit())) {
			throw new IllegalArgumentException("Appel de la methode receptionner de Distributeur1 avec un produit ne correspondant pas au produit distribue par le distributeur");
		}
		if (quantite<=0.0) {
			throw new IllegalArgumentException("Appel de la methode receptionner de Distributeur1 avec une quantite egale a "+quantite);
		}
		if (cc.getProduit().equals(produit)) { 
			this.stock.ajouter((Chocolat) produit, quantite, this);
		}
		this.journal.ajouter("----------------------------------------- RECEPTION ------------------------------------------------------------------------------------");
		this.journal.ajouter("Réception de "+ quantite + " kg de" + produit);
	}

	/**
	 * @author Erwann DEFOY
	 * @author2 Erine DUPONT : ajout du journal
	 */
	public double payer(double montant, ContratCadre cc) {
		if (montant<=0.0) {
			throw new IllegalArgumentException("Appel de la methode payer de Distributeur1 avec un montant negatif = "+montant);
		}
		double quantitepaye = soldeBancaire.Payer((IActeur)(cc.getVendeur()), montant);
		this.indicateursolde.retirer(this, quantitepaye);
		this.journal.ajouter("------------------------------------------ PAIEMENT -------------------------------------------------------------------------------------");
		this.journal.ajouter("Paiement de " + montant + " €");
		return quantitepaye;
	}

	// ---------------------------------------------------------------------------------------------------------
	// VENDEUR CLIENT
	// ---------------------------------------------------------------------------------------------------------

	/**
	 * @author Estelle BONNET
	 */
	public StockEnVente<Chocolat> getStockEnVente() {
		StockEnVente<Chocolat> res = new StockEnVente<Chocolat>();
		List<Chocolat> produits = this.stock.getProduitsEnVente();
		for (int i =0; i< produits.size(); i++) {
			res.ajouter(produits.get(i), stock.get(produits.get(i)));
		}
		return res;
	}

	/**
	 * @author Estelle BONNET
	 * @author2 Erine DUPONT
	 */
	public double getPrix(Chocolat c) {
		boolean vendu = false;
		List<Chocolat> produits =this.stock.getProduitsEnVente();
		for (int i=0; i<produits.size();i++) {
			if (c.equals(produits.get(i))) {
				vendu = true;
			}
		}
		if (!vendu) {
			return Double.MAX_VALUE;
		}
		if (this.contratsEnCours.size()==0) {
			if (c == Chocolat.HG_E_SHP) {
				return 60;
			}
			if (c == Chocolat.MG_E_SHP) {
				return 10;
			}
			if (c == Chocolat.MG_NE_SHP || c == Chocolat.MG_NE_HP) {
				return 7;
			}
			else {
				return 50;
			}
		}
		else {
			double somme = 0;
			int nbproduits = 0;
			for (ContratCadre<Chocolat> cc : this.contratsEnCours) {
				if (cc.getProduit()==c) {
					somme += cc.getPrixAuKilo()*cc.getQuantite();
					nbproduits += cc.getQuantite();
				}
			}
			if (nbproduits == 0) {
				if (c == Chocolat.HG_E_SHP) {
					return 60;
				}
				if (c == Chocolat.MG_E_SHP) {
					return 10;
				}
				if (c == Chocolat.MG_NE_SHP || c == Chocolat.MG_NE_HP) {
					return 7;
				}
				else {
					return 50;
				}
			}
			else {
				double prixMoyen = somme/ nbproduits;
				double prixVente = prixMoyen*(1+this.marge);
				/*
				if (prixVente <60 & c == Chocolat.HG_E_SHP) {
					return 60;
				}
				if (prixVente <7 & (c == Chocolat.MG_E_SHP|| c == Chocolat.MG_NE_SHP || c == Chocolat.MG_NE_HP)) {
					return 7;
				}
				else {
					return prixVente;
				}
				 */
				return prixMoyen*(1.0+this.marge);
			}
		}
	}
	/**
	 * @author Erine DUPONT 
	 */
	public double vendre(Chocolat chocolat, double quantite) {
		double stock = this.getStockEnVente().get(chocolat);
		if (quantite < 0.0) {
			throw new IllegalArgumentException("Appel de vendre(chocolat, quantité) de "
					+ "Distributeur1 avec quantité<0.0 (=="+quantite+")");
		} else {
			double quantitevendue = Math.min(stock, quantite);
			soldeBancaire.RecevoirPaiement(this, quantitevendue*getPrix(chocolat));
			this.indicateursolde.ajouter(this, quantitevendue*getPrix(chocolat));
			//this.journal.ajouter("vendre : solde bancaire affecte a "+quantitevendue*getPrix(chocolat)+" getprix="+getPrix(chocolat));
			this.stock.enlever(chocolat, quantitevendue, this);
			this.journal.ajouter("------------------------------------------ VENTE ------------------------------------------------------------------------------------------");
			this.journal.ajouter("La quantité de " + chocolat + " vendue est : "+ quantite + " kg"+"au prix de "+getPrix(chocolat));
			return quantitevendue;
		}
	}	
	/**
	 * @author Erwann DEFOY
	 */
	@Override
	public List<Publicite> getPubEnCours() {
		return publicites;
	}
}

