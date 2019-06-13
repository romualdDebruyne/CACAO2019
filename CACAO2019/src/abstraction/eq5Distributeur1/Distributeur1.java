package abstraction.eq5Distributeur1;

import java.util.ArrayList;
import java.util.List;

import abstraction.eq7Romu.distributionChocolat.IDistributeurChocolat;
import abstraction.eq7Romu.produits.Chocolat;
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

public class Distributeur1 implements IActeur, IAcheteurContratCadre, IDistributeurChocolat {
	private Journal journal;
	private Stock stock;
	private int numero;
	private CompteBancaire soldeBancaire;
	private Double marge;
	private Indicateur indicateurstock;
	private Indicateur indicateursolde;
	private List<ContratCadre<Chocolat>> contratsEnCours;



	public Distributeur1() {
		this(5.0, 100000.0);
	}

	public Distributeur1(double marge, Double soldeInitial) {
		this.numero =1 ;
		this.marge = marge;
		this.stock = new Stock();
		stock.ajouter(Chocolat.HG_E_SHP, 150000.0);
		stock.ajouter(Chocolat.MG_E_SHP, 0.0);
		stock.ajouter(Chocolat.MG_NE_HP, 0.0);
		stock.ajouter(Chocolat.MG_NE_SHP, 0.0);
		this.soldeBancaire = new CompteBancaire(this.getNom(), this, soldeInitial);
		this.indicateursolde = new Indicateur ("EQ5 solde bancaire",this);
		indicateursolde.setValeur(this, soldeBancaire.getCompteBancaire());
		Monde.LE_MONDE.ajouterIndicateur(indicateursolde);
		this.indicateurstock = new Indicateur ("EQ5 stock", this);
		for (int i=0; i<stock.getProduitsEnVente().size(); i++) {
			indicateurstock.ajouter(this, stock.get(stock.getProduitsEnVente().get(i)));
		}
		Monde.LE_MONDE.ajouterIndicateur(indicateurstock);
		this.journal = new Journal("Journal "+this.getNom());
		Monde.LE_MONDE.ajouterJournal(this.journal);
		this.contratsEnCours = new ArrayList<ContratCadre<Chocolat>>();
	}

	public String getNom() {
		return "EQ5";
	}

	public void initialiser() {
	}

	public void next() {
	}

	// ------------------------------------------------------------------------------------------------------
	// ACHETEUR
	// ------------------------------------------------------------------------------------------------------ 

	@Override
	/**
	 * @author Erine DUPONT
	 */
	public ContratCadre<Chocolat> getNouveauContrat() {
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

			} else {
				this.journal.ajouter("   Il ne reste que "+solde+" une fois tous les contrats payes donc nous ne souhaitons pas en creer d'autres pour l'instant");
			}


		}
		//Création Contrat
		return ncc;
	}

	/**
	 * @author Imane ZRIAA
	 */
	@Override
	public void proposerEcheancierAcheteur(ContratCadre C) {
		if (C.getEcheancier()==null) {//pas de contre-proposition
			C.ajouterEcheancier(new Echeancier(Monde.LE_MONDE.getStep(), 20, C.getQuantite()/20));
		} else {
			C.ajouterEcheancier(new Echeancier(C.getEcheancier())); // accepter la contre-proposition
		}
	}

	@Override
	/**
	 * @author Imane ZRIAA
	 * @author2 Erine DUPONT
	 */
	public void proposerPrixAcheteur(ContratCadre cc) {
		double prixVendeur = cc.getPrixAuKilo();
		journal.ajouter("proposerPrixAcheteur : prix sur contrat = "+prixVendeur);
		/*if (Math.random()<0.25) { // probabilite de 25% d'accepter
			cc.ajouterPrixAuKilo(cc.getPrixAuKilo());
		} else {
			cc.ajouterPrixAuKilo((prixVendeur*(0.9+Math.random()*0.1))); // Rabais de 10% max
		}*/
		if (5 < prixVendeur && prixVendeur < 10 && stock.get((Chocolat) cc.getProduit())<1000) {
			cc.ajouterPrixAuKilo(prixVendeur*0.8);
		} else if (5 < prixVendeur && prixVendeur < 10 && stock.get((Chocolat) cc.getProduit())>=1000) {
			cc.ajouterPrixAuKilo(prixVendeur*0.6);
		} else if (prixVendeur <= 5) {
			cc.ajouterPrixAuKilo(prixVendeur);
		}
	}
	/** @author Erine DUPONT
	 */
	@Override
	public void notifierAcheteur(ContratCadre cc) {
		this.contratsEnCours.add(cc);
		System.out.println("CONTRAT EQ5 : "+cc);
	}

	/**@author Erine DUPONT
	 */
	@Override
	public void receptionner(Object produit, double quantite, ContratCadre cc) {
		if (produit==null || !produit.equals(cc.getProduit())) {
			throw new IllegalArgumentException("Appel de la methode receptionner de DistributeurRomu avec un produit ne correspondant pas au produit distribue par le distributeur");
		}
		if (quantite<=0.0) {
			throw new IllegalArgumentException("Appel de la methode receptionner de DistributeurRomu avec une quantite egale a "+quantite);
		}
		this.stock.ajouter((Chocolat) produit, quantite);
	}

	
	/**
	 * @author Erwann DEFOY
	 */
	@Override
	public double payer(double montant, ContratCadre cc) {
		if (montant<=0.0) {
			throw new IllegalArgumentException("Appel de la methode payer de Distributeur1 avec un montant negatif = "+montant);
		}
		double quantitepaye = soldeBancaire.Payer((IActeur)(cc.getVendeur()), montant);
		//this.indicateursolde.retirer(this, quantitepaye);
		return quantitepaye;
	}

	// ---------------------------------------------------------------------------------------------------------
	// VENDEUR CLIENT
	// ---------------------------------------------------------------------------------------------------------

	/**
	 * @author Estelle
	 */
	@Override
	public StockEnVente<Chocolat> getStockEnVente() {
		StockEnVente<Chocolat> res = new StockEnVente<Chocolat>();
		List<Chocolat> produits = this.stock.getProduitsEnVente();
		for (int i =0; i< produits.size(); i++) {
			res.ajouter(produits.get(i), stock.get(produits.get(i)));
		}
		return res;
	}

	/**
	 * @author Estelle
	 */
	@Override
	public double getPrix(Chocolat c) {
		boolean vendu = false;
		List<Chocolat> produits =this.stock.getProduitsEnVente();
		for (int i=0; i<produits.size();i++) {
			if (c.equals(produits.get(i))) {
				vendu = true;
			}
		}
		if (!vendu) {
			return Double.NaN;
		}

		if (this.contratsEnCours.size()==0) {
			return 50;
		} else {

			double prixMoyen = 0;
			for (ContratCadre<Chocolat> cc : this.contratsEnCours) {
				if (cc.getProduit()==c) {
					prixMoyen+=cc.getPrixAuKilo();
				}
			}
			prixMoyen = prixMoyen/ this.contratsEnCours.size();
			return prixMoyen *(1.0+this.marge);
		}
	}
	
	/**
	 * @author Erine DUPONT 
	 */
	@Override
	public double vendre(Chocolat chocolat, double quantite) {
		double stock = this.getStockEnVente().get(chocolat);
		if (quantite < 0.0) {
			throw new IllegalArgumentException("Appel de vendre(chocolat, quantité) de "
					+ "Distributeur1 avec quantité<0.0 (=="+quantite+")");
		} else if (stock < quantite) {
			throw new IllegalArgumentException("Appel de vendre(chocolat, quantité) de "
					+ "Distributeur1 avec stock ( ==" + stock +") < quantité (=="+quantite+")");
		} else {
			this.stock.enlever(chocolat, quantite);
			this.indicateurstock.retirer(this, quantite);
			soldeBancaire.RecevoirPaiement(this, quantite*getPrix(chocolat));
			this.indicateursolde.ajouter(this, quantite*getPrix(chocolat));
			return this.stock.get(chocolat);
		}
	}
}