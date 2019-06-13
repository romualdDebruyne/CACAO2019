package abstraction.eq4Transformateur2;

import java.util.ArrayList;
import java.util.HashMap;//Minh Tri
import java.util.List;
import java.util.Random;

import abstraction.eq7Romu.produits.Chocolat;
import abstraction.eq7Romu.ventesContratCadre.ContratCadre;
import abstraction.eq7Romu.ventesContratCadre.Echeancier;
import abstraction.eq7Romu.ventesContratCadre.IVendeurContratCadre;
import abstraction.eq7Romu.ventesContratCadre.StockEnVente;

public class Transformateur2VendeurCC implements IVendeurContratCadre<Chocolat> {
	private Transformateur2 t2;
	protected HashMap<Chocolat,Double> catalogueChocolat;//Minh tri
	
	// Initialise Transformateur2VendeurCC avec un catalogue vide
	public Transformateur2VendeurCC(Transformateur2 trans2) {
		this.t2 = trans2;
		this.catalogueChocolat = new HashMap<Chocolat,Double>(); //Minh Tri
	}
	
	
	public List<Chocolat> getProduitsEnVente() {
		ArrayList<Chocolat> chocolat = new ArrayList<Chocolat>();
		chocolat.addAll(t2.stockEnVente.keySet());
		return chocolat;
	}

	@Override
	public StockEnVente<Chocolat> getStockEnVente() {
		StockEnVente<Chocolat> sev = new StockEnVente<Chocolat>();
		sev.ajouter(Chocolat.HG_E_SHP, 12000.0);
		return sev;
	}

	//Minh Tri
	@Override
	public double getPrix(Chocolat produit, Double quantite) {
		if(!catalogueChocolat.containsKey(produit)) {
			System.out.println(" getPrix avec chocolat pas au catalogue "+produit);
			System.out.println(" catalogue : "+catalogueChocolat);
			return Double.MAX_VALUE;
		}
		return catalogueChocolat.get(produit);//*quantite;
	}

	@Override
	public void proposerEcheancierVendeur(ContratCadre<Chocolat> cc) {
		//*
		if (Math.random()<0.4) { // 40% de chances d'accepter l'échéancier
			cc.ajouterEcheancier(new Echeancier(cc.getEcheancier())); // on accepte la proposition de l'acheteur car on a la quantite en stock 
		} else { // 60% de chance de proposer un echeancier etalant sur un ou deux step de plus, de façon aléatoire
			Random r = new Random();
			cc.ajouterEcheancier(new Echeancier(cc.getEcheancier().getStepDebut(), cc.getEcheancier().getNbEcheances()+(r.nextInt(1)+1), cc.getQuantite()/(cc.getEcheancier().getNbEcheances()+(r.nextInt(1)+1))));
		}
		//*/
	}
	@Override
	public void proposerPrixVendeur(ContratCadre<Chocolat> cc) {
		if (cc.getListePrixAuKilo().size()==0) {
			cc.ajouterPrixAuKilo(getPrix(cc.getProduit(), cc.getQuantite()));
		} else {
			double prixVendeur = cc.getListePrixAuKilo().get(0);
			double prixAcheteur = cc.getPrixAuKilo();
			if (prixAcheteur>=0.75*prixVendeur) { // on ne fait une proposition que si l'acheteur ne demande pas un prix trop bas.
				if (Math.random()<0.25) { // probabilite de 25% d'accepter
					cc.ajouterPrixAuKilo(cc.getPrixAuKilo());
				} if (prixAcheteur>=0.9*prixVendeur) { // si l'acheteur propose 90% de notre prix on accepte
					cc.ajouterPrixAuKilo(cc.getPrixAuKilo());
				} else {
					final double RABAIS_MAX = 0.1;
					cc.ajouterPrixAuKilo((prixVendeur-prixVendeur*Math.random()*RABAIS_MAX)); // rabais de 10% max
				}
			}
		}
	}

	@Override
	public void notifierVendeur(ContratCadre<Chocolat> cc) {
		t2.contratsChocolatEnCours.add(cc);
	}

	@Override
	public double livrer(Chocolat produit, double quantite, ContratCadre<Chocolat> cc) {
		if (produit==null || //t2.
				getStockEnVente().get(produit) == 0) {
			System.out.println(cc.getAcheteur());
			throw new IllegalArgumentException("Appel de la methode livrer de Transformateur2 avec un produit ne correspondant pas au chocolat produit");
		}
		double livraison = Math.min(quantite, t2.iStockChocolat.getValeur());
		t2.iStockChocolat.retirer(t2, livraison);
		return livraison;

	}

	@Override
	public void encaisser(double montant, ContratCadre<Chocolat> cc) {
		if (montant<0.0) {
			throw new IllegalArgumentException("Appel de la methode encaisser de Transformateur2 avec un montant negatif");
		}
		t2.soldeBancaire.ajouter(t2,  montant);
	}
}
