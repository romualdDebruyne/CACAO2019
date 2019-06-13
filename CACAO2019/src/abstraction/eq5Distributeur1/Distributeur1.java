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

public class Distributeur1 implements IActeur, IAcheteurContratCadre, IDistributeurChocolat {
	private Journal journal;
	private Stock stock;
	private CompteBancaire soldeBancaire;
	private Double marge;
	private Indicateur indicateursolde;
	private List<ContratCadre<Chocolat>> contratsEnCours;


	/**
	 * @author Erine DUPONT & Estelle BONNET
	 */
	public Distributeur1() {
		this(0.1, 100000.0); // La marge doit être en pourcentage !!! 5% > 0.05
	}

	/**
	 * @author Erine DUPONT & Estelle BONNET
	 */
	public Distributeur1(double marge, Double soldeInitial) {
		this.marge = marge;   // La marge doit être en pourcentage !!! 5% > 0.05
		this.stock = new Stock();
		stock.ajouter(Chocolat.HG_E_SHP, 150000.0, this);
		stock.ajouter(Chocolat.MG_E_SHP, 150000.0, this);
		stock.ajouter(Chocolat.MG_NE_HP, 150000.0, this);
		stock.ajouter(Chocolat.MG_NE_SHP, 150000.0, this);
		this.soldeBancaire = new CompteBancaire(this.getNom(), this, soldeInitial);
		this.indicateursolde = new Indicateur ("EQ5 solde bancaire",this, soldeBancaire.getCompteBancaire());
		Monde.LE_MONDE.ajouterIndicateur(indicateursolde);
		this.journal = new Journal("Journal "+this.getNom());
		Monde.LE_MONDE.ajouterJournal(this.journal);
		this.contratsEnCours = new ArrayList<ContratCadre<Chocolat>>();
		Monde.LE_MONDE.ajouterActeur(new ClientEuropeen(Chocolat.HG_E_SHP, 100));
		Monde.LE_MONDE.ajouterActeur(new ClientEuropeen(Chocolat.MG_E_SHP, 100));
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
		this.journal.ajouter("Démarrage d'un nouveau contrat cadre");
		// Au préalable, il faut identifier produit, quantité, vendeur, acheteur

		// On détermine combien il resterait sur le compte si on soldait tous les contrats en cours.
		double solde = this.soldeBancaire.getCompteBancaire();
		this.journal.ajouter("Le solde actuel est de " + solde);
		for (ContratCadre<Chocolat> cc : this.contratsEnCours) {
			solde = solde - cc.getMontantRestantARegler();
		}

		// On ne cherche pas a établir d'autres contrats d'achat si le compte bancaire est trop bas
		if (solde>5000.0) { 
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
				double meilleur_rapport_qp = stock_vendeur/prix_vendeur;
				this.journal.ajouter("Le rapport quantité/prix initial est de " + meilleur_rapport_qp);
				for (IVendeurContratCadre<Chocolat> vendeur : vendeurs) {
					double stock = vendeur.getStockEnVente().get(produit);
					double prix = vendeur.getPrix(produit, stock);
					double rapport_qp = stock/prix;
					this.journal.ajouter("Le rapport quantité/prix de " + vendeur + " est de " + rapport_qp);;
					if (rapport_qp > meilleur_rapport_qp) {
						vendeur_choisi = vendeur;
						meilleur_rapport_qp = rapport_qp;
						this.journal.ajouter("Le rapport est meilleur, le vendeur est donc " + vendeur_choisi);
					} else {
						this.journal.ajouter("Le rapport est moins bon, le vendeur reste " + vendeur_choisi);
					}
				}

				// Choix quantité
				double quantite = 0.0;
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
				this.journal.ajouter("La quantité demandée est " + quantite);
				ncc = new ContratCadre<Chocolat>(this, vendeur_choisi, produit, quantite);
			}  
		} else {
			this.journal.ajouter("   Il ne reste que "+solde+" une fois tous les contrats payes donc nous ne souhaitons "
					+ "pas en créer d'autres pour l'instant");
		}

		//Création Contrat
		return ncc; 
	}

	/**
	 * @author Imane ZRIAA
	 */
	public void proposerEcheancierAcheteur(ContratCadre C) {
		if (C!=null) {
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
			if (20 < prixVendeur && prixVendeur <= 70 && stock.get((Chocolat) cc.getProduit()) < 10000) {
				cc.ajouterPrixAuKilo(prixVendeur*0.95);
				this.journal.ajouter("Nous proposons un prix de " + prixVendeur*0.95);
			} else if (20 < prixVendeur && prixVendeur <= 70 && stock.get((Chocolat) cc.getProduit())>=10000) {
				cc.ajouterPrixAuKilo(prixVendeur*0.8);
				this.journal.ajouter("Nous proposons un prix de " + prixVendeur*0.8);
			} else if (prixVendeur <= 20) {
				cc.ajouterPrixAuKilo(prixVendeur);
				this.journal.ajouter("Nous proposons un prix de " + prixVendeur);
			} else {
				this.journal.ajouter("Nous refusons le prix de " + prixVendeur);
			}	
		} else if (cc.getProduit().equals(Chocolat.MG_E_SHP) || cc.getProduit().equals(Chocolat.MG_NE_SHP)) {
			if (10 < prixVendeur && prixVendeur <= 50 && stock.get((Chocolat) cc.getProduit()) < 10000) {
				cc.ajouterPrixAuKilo(prixVendeur*0.8);
				this.journal.ajouter("Nous proposons un prix de " + prixVendeur*0.8);
			} else if (10 < prixVendeur && prixVendeur <= 50 && stock.get((Chocolat) cc.getProduit())>=10000) {
				cc.ajouterPrixAuKilo(prixVendeur*0.6);
				this.journal.ajouter("Nous proposons un prix de " + prixVendeur*0.6);
			} else if (prixVendeur <= 10) {
				cc.ajouterPrixAuKilo(prixVendeur);
				this.journal.ajouter("Nous proposons un prix de " + prixVendeur);
			} else {
				this.journal.ajouter("Nous refusons le prix de " + prixVendeur);
			}	
		} else if (cc.getProduit().equals(Chocolat.MG_NE_HP)) {
			if (10 < prixVendeur && prixVendeur <= 40 && stock.get((Chocolat) cc.getProduit()) < 10000) {
				cc.ajouterPrixAuKilo(prixVendeur*0.8);
				this.journal.ajouter("Nous proposons un prix de " + prixVendeur*0.8);
			} else if (10 < prixVendeur && prixVendeur <= 40 && stock.get((Chocolat) cc.getProduit())>=10000) {
				cc.ajouterPrixAuKilo(prixVendeur*0.6);
				this.journal.ajouter("Nous proposons un prix de " + prixVendeur*0.6);
			} else if (prixVendeur <= 10) {
				cc.ajouterPrixAuKilo(prixVendeur);
				this.journal.ajouter("Nous proposons un prix de " + prixVendeur);
			} else {
				this.journal.ajouter("Nous refusons le prix de " + prixVendeur);
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
			throw new IllegalArgumentException("Appel de la methode receptionner de DistributeurRomu avec un produit ne correspondant pas au produit distribue par le distributeur");
		}
		if (quantite<=0.0) {
			throw new IllegalArgumentException("Appel de la methode receptionner de DistributeurRomu avec une quantite egale a "+quantite);
		}
		if (cc.getProduit().equals(produit)) { 
			this.stock.ajouter((Chocolat) produit, quantite, this);
		}
		this.journal.ajouter("Réception de "+ quantite + "kg de" + produit);
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
		this.journal.ajouter("Paiement de " + montant);
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
			return 50;
		} else {

			double somme = 0;
			int nbproduits = 0;
			for (ContratCadre<Chocolat> cc : this.contratsEnCours) {
				if (cc.getProduit()==c) {
					somme += cc.getPrixAuKilo();
					nbproduits += 1;
				}
			}
			if (nbproduits == 0) {
				return 50;
			}
			else {
				double prixMoyen = somme/ nbproduits;
				return prixMoyen *(1.0+this.marge);
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
			this.journal.ajouter("La quantité de " + chocolat + " vendue est : "+ quantite);
			return quantitevendue;
		}
	}	
}