package abstraction.eq7Romu.acteurs;

import java.util.ArrayList;
import java.util.List;

import abstraction.eq7Romu.distributionChocolat.IDistributeurChocolat;
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

public class DistributeurRomu implements IActeur, IAcheteurContratCadre<Chocolat>, IDistributeurChocolat {
	private static int NB_DIST = 0;

	private int numero;
	private Indicateur stock;
	private Indicateur soldeBancaire;
	private Journal journal;
	private Chocolat uniqueProduit;
	private Double marge;
	private List<ContratCadre<Chocolat>> contratsEnCours;


	public DistributeurRomu(Chocolat uniqueProduit, 
			Double marge, 
			Double stockInitial, 
			Double soldeInitial) {
		NB_DIST++;
		this.numero = NB_DIST;
		this.uniqueProduit = uniqueProduit;
		this.marge = marge;
		this.stock = new Indicateur(this.getNom()+" Stock", this, stockInitial);
		Monde.LE_MONDE.ajouterIndicateur(this.stock);
		this.soldeBancaire = new Indicateur(this.getNom()+" Solde", this, soldeInitial);
		Monde.LE_MONDE.ajouterIndicateur(this.soldeBancaire);
		this.journal = new Journal("Journal "+this.getNom());
		Monde.LE_MONDE.ajouterJournal(this.journal);
		this.contratsEnCours = new ArrayList<ContratCadre<Chocolat>>();
	}

	public String getNom() {
		return "DI"+this.numero+"Romu";
	}

	public void initialiser() {
	}

	public void next() {
	}

	public StockEnVente<Chocolat> getStockEnVente() {
		StockEnVente<Chocolat> res = new StockEnVente<Chocolat>();
		res.ajouter(uniqueProduit, this.stock.getValeur());
		return res;
	}

	public double getPrix(Chocolat c) {
		if (!c.equals(this.uniqueProduit)) {
			return Double.NaN;
		}
		if (this.contratsEnCours.size()==0) {
			return 40;
		} else {
			double prixMoyen = 0;
			for (ContratCadre<Chocolat> cc : this.contratsEnCours) {
				prixMoyen+=cc.getPrixAuKilo();
			}
			prixMoyen = prixMoyen/ this.contratsEnCours.size();
			return prixMoyen *(1.0+this.marge);
		}
	}

	public double vendre(Chocolat c, double quantite) {
		if (!c.equals(this.uniqueProduit)) {
			this.journal.ajouter("vente de 0.0 (produit demande = "+c+ " vs produit dispo = "+this.uniqueProduit+")");
			return 0.0;
		} else {
			Double q = Math.min(this.stock.getValeur(), quantite);
			this.stock.retirer(this, q);
			this.soldeBancaire.ajouter(this, this.getPrix(c)*q);
			this.journal.ajouter("Vente de "+q+" a "+this.getPrix(c));
			return q;
		}
	}
	/** 
	 * Retire de la liste des contrats en cours les contrats pour lesquels la quantite a livrer 
	 * est nulle et le montant a regler est egalement nul (toutes les livraisons et tous les paiements
	 * ont ete effectues).
	 */
	public void retireVieuxContrats() {
		List<ContratCadre<Chocolat>> aEnlever = new ArrayList<ContratCadre<Chocolat>>();
		for (ContratCadre<Chocolat> c : this.contratsEnCours) {
			if (c.getQuantiteRestantALivrer()<=0.0 && c.getMontantRestantARegler()<=0.0) {
				aEnlever.add(c);
			}
		}
		for (ContratCadre<Chocolat> c : aEnlever) {
			this.contratsEnCours.remove(c);
		}
	}


	public ContratCadre<Chocolat> getNouveauContrat() {
		ContratCadre<Chocolat> res=null;
        // on determine combien il resterait sur le compte si on soldait tous les contrats en cours.
		double solde = this.soldeBancaire.getValeur();
		this.journal.ajouter("Determination du solde une fois tous les contrats en cours payes");
		this.journal.ajouter("- solde="+solde);
		for (ContratCadre<Chocolat> cc : this.contratsEnCours) {
			solde = solde - cc.getMontantRestantARegler();
		}
		this.journal.ajouter("--> solde="+solde);

		if (solde>10000.0) { // On ne cherche pas a etablir d'autres contrats d'achat si le compte bancaire est trop bas
			List<IVendeurContratCadre<Chocolat>> vendeurs = new ArrayList<IVendeurContratCadre<Chocolat>>();
	           this.journal.ajouter("  recherche vendeur d'au moins 100 de "+this.uniqueProduit);
			for (IActeur acteur : Monde.LE_MONDE.getActeurs()) {
				if (acteur instanceof IVendeurContratCadre) {
					IVendeurContratCadre vacteur = (IVendeurContratCadre)acteur;
					StockEnVente stock = vacteur.getStockEnVente();
					if (stock.get(this.uniqueProduit)>100.0) {// on souhaite faire des contrats d'au moins 100kg
						this.journal.ajouter("   "+(acteur.getNom())+" vend "+stock.get(this.uniqueProduit)+" de "+this.uniqueProduit);
						vendeurs.add((IVendeurContratCadre<Chocolat>)vacteur);
					} else {
						this.journal.ajouter("   "+(acteur.getNom())+" ne vend que "+stock.toHtml());
					}
				}
			}
			if (vendeurs.size()>=1) {
				IVendeurContratCadre<Chocolat> vendeur = vendeurs.get( (int)( Math.random()*vendeurs.size())); // ici tire au hasard plutot que de tenir compte des stocks en vente et des prix
				// On determine la quantite qu'on peut esperer avec le tiers du reste de notre solde bancaire
                this.journal.ajouter(" Determination de la quantite achetable avec une somme de "+String.format("%.3f",solde/3.0));
				double quantite = 100.0; // on souhaite faire des contrats d'au moins 100 kg
				double prix = vendeur.getPrix(this.uniqueProduit, quantite);
				this.journal.ajouter("prix="+prix+" pour quantite="+quantite+" --> "+(quantite*prix));
				while (!Double.isNaN(prix) && prix*quantite<solde/3.0 ) {
					quantite=quantite*1.5;
					prix = vendeur.getPrix(this.uniqueProduit,  quantite);
					this.journal.ajouter("prix="+prix+" pour quantite="+quantite+" --> "+(quantite*prix));
				}
				quantite = quantite/1.5;
				res = new ContratCadre<Chocolat>(this, vendeur, this.uniqueProduit, quantite);
			}else {
				this.journal.ajouter("   Aucun vendeur trouve --> pas de nouveau contrat a ce step");
			}
		} else {
			this.journal.ajouter("   Il ne reste que "+solde+" une fois tous les contrats payes donc nous ne souhaitons pas en creer d'autres pour l'instant");
		}
		return res;
	}

	public void proposerEcheancier(ContratCadre<Chocolat> cc) {
		if (cc.getEcheancier()==null) { // il n'y a pas encore eu de contre-proposition de la part du vendeur
			cc.ajouterEcheancier(new Echeancier(Monde.LE_MONDE.getStep(), 10, cc.getQuantite()/10));
		} else {
			cc.ajouterEcheancier(new Echeancier(cc.getEcheancier())); // on accepte la contre-proposition du vendeur 
		}
	}

	public void proposerEcheancierAcheteur(ContratCadre<Chocolat> cc) {
		if (cc.getEcheancier()==null) { // il n'y a pas encore eu de contre-proposition de la part du vendeur
			cc.ajouterEcheancier(new Echeancier(Monde.LE_MONDE.getStep(), 10, cc.getQuantite()/10));
		} else {
			if (Math.random()<0.5) { // une chance sur deux d'accepter l'echeancier
				cc.ajouterEcheancier(new Echeancier(cc.getEcheancier())); // on accepte la proposition de l'acheteur car on a la quantite en stock 
			} else { // une chance sur deux de proposer un echeancier etalant sur un step de plus
				cc.ajouterEcheancier(new Echeancier(cc.getEcheancier().getStepDebut(), cc.getEcheancier().getNbEcheances()+1, cc.getQuantite()/(cc.getEcheancier().getNbEcheances()+1)));
			}
		}
	}

	public void proposerPrixAcheteur(ContratCadre<Chocolat> cc) {
		double prixVendeur = cc.getListePrixAuKilo().get(0);
		if (Math.random()<0.25) { // probabilite de 25% d'accepter
			cc.ajouterPrixAuKilo(cc.getPrixAuKilo());
		} else {
			cc.ajouterPrixAuKilo((prixVendeur*(0.9+Math.random()*0.1))); // rabais de 10% max
		}
	}

	public void notifierAcheteur(ContratCadre<Chocolat> cc) {
		this.contratsEnCours.add(cc);
	}

	public void receptionner(Chocolat produit, double quantite, ContratCadre<Chocolat> cc) {
		if (produit==null || !produit.equals(this.uniqueProduit)) {
			throw new IllegalArgumentException("Appel de la methode receptionner de DistributeurRomu avec un produit ne correspondant pas au produit distribue par le distributeur");
		}
		if (quantite<=0.0) {
			throw new IllegalArgumentException("Appel de la methode receptionner de DistributeurRomu avec une quantite egale a "+quantite);
		}
		this.stock.ajouter(this, quantite);
	}

	public double payer(double montant, ContratCadre<Chocolat> cc) {
		if (montant<=0.0) {
			throw new IllegalArgumentException("Appel de la methode payer de DistributeurRomu avec un montant negatif = "+montant);
		}
		double paiement = Math.min(montant,  this.soldeBancaire.getValeur());
		this.soldeBancaire.retirer(this,  paiement);
		return paiement;
	}

}
