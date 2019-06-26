package abstraction.eq4Transformateur2;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import abstraction.eq7Romu.produits.Chocolat;
import abstraction.eq7Romu.ventesContratCadre.ContratCadre;
import abstraction.eq7Romu.ventesContratCadre.Echeancier;
import abstraction.eq7Romu.ventesContratCadre.IVendeurContratCadre;
import abstraction.eq7Romu.ventesContratCadre.StockEnVente;
import abstraction.fourni.Monde;

public class Transformateur2VendeurCC implements IVendeurContratCadre<Chocolat> {
	private Transformateur2 t2;
	
	// Initialise Transformateur2VendeurCC avec un catalogue vide
	public Transformateur2VendeurCC(Transformateur2 trans2) {
		this.t2 = trans2;
	}
	
	// Guillaume
	public List<Chocolat> getProduitsEnVente() {
		ArrayList<Chocolat> chocolat = new ArrayList<Chocolat>();
		chocolat.addAll(t2.getStockEnVente().getProduitsEnVente());
		return chocolat;
	}

	@Override
	public StockEnVente<Chocolat> getStockEnVente() {
		StockEnVente<Chocolat> sev = new StockEnVente<Chocolat>();
		for(Chocolat c : t2.getCHOCOLATS_VENTE())
			sev.ajouter(c, t2.getStocksChocolat().getQuantiteTotale(c));	
		return sev;
	}

	// Minh Tri
	@Override
	public double getPrix(Chocolat produit, Double qte) {
		//System.out.println("getPrix(" + produit + ", " + qte + ") = " + (t2.stocksChocolat.getPrix(produit, qte) * (1.0 + MARGE_VISEE) / qte));
		
		// Si l'on ne vend pas ce type de chocolat, on renvoie +infini
		if(!t2.getCHOCOLATS_VENTE().contains(produit) || qte == Double.POSITIVE_INFINITY)
			return Double.MAX_VALUE;
		// Quantité réelle de production de la qté de chocolat demandée + une marge (on re-divise par la quantité pour obtenir le prix au kg)
		return t2.getStocksChocolat().getPrix(produit, qte) * (1.0 + ConfigEQ4.MARGE_VISEE) / qte;
	}

	// Adrien
	@Override
	public void proposerEcheancierVendeur(ContratCadre<Chocolat> cc) {
		if(cc.getQuantite() == Double.POSITIVE_INFINITY)
			return;
		
		if (Math.random() < 0.3) { // 30% de chances d'accepter l'échéancier
			cc.ajouterEcheancier(new Echeancier(cc.getEcheancier())); // on accepte la proposition de l'acheteur car on a la quantite en stock 
		} else { // 70% de chance de proposer un echeancier etalant sur un ou deux step de plus, de façon aléatoire
			Random r = new Random();
			cc.ajouterEcheancier(new Echeancier(cc.getEcheancier().getStepDebut(), cc.getEcheancier().getNbEcheances()+(r.nextInt(1)+1), cc.getQuantite()/(cc.getEcheancier().getNbEcheances()+(r.nextInt(1)+1))));
		}
		//*/
	}
	@Override
	// Guillaume et Kelian
	public void proposerPrixVendeur(ContratCadre<Chocolat> cc) {
		if(cc.getListePrixAuKilo().size() == 0) {
			cc.ajouterPrixAuKilo(getPrix(cc.getProduit(), cc.getQuantite()));
			return;
		}
		
		double coutProduction = t2.getStocksChocolat().getPrix(cc.getProduit(), cc.getQuantite()) / cc.getQuantite();
		double prixAcheteur = cc.getPrixAuKilo();
		double marge = (prixAcheteur - coutProduction) / coutProduction;
		
		// On ne fait une proposition que si l'acheteur ne demande pas un prix trop bas.
		if(prixAcheteur >= ConfigEQ4.SEUIL_REFUS_FORCE * coutProduction) { 
			// Si le prix proposé nous permet de faire une marge, probabilité d'accepter dépendant de cette marge
			if(marge > 0 && Math.random() < 2 * marge) // Ex : marge de 10% => probabilité de 20% d'accepter directement
				cc.ajouterPrixAuKilo(cc.getPrixAuKilo());
			else {
				double prixSouhaite = coutProduction * ConfigEQ4.MARGE_VISEE;
				if(prixAcheteur >= prixSouhaite) // Si le prix est suffisant pour la marge que l'on souhaite, on accepte
					cc.ajouterPrixAuKilo(cc.getPrixAuKilo()); 
				else {
					double prixIntermediaire = (prixAcheteur + prixSouhaite) / 2;
					// Si la différence de prix entre le prix de l'acheteur et le prix que l'on veut proposer est inférieure au seuil, on accepte
					if((prixIntermediaire - prixAcheteur) / prixAcheteur < ConfigEQ4.SEUIL_ACCEPTATION_FORCEE) 
						cc.ajouterPrixAuKilo(cc.getPrixAuKilo());
					else // Sinon, on propose un prix intermédiaire
						cc.ajouterPrixAuKilo(prixIntermediaire);
				}
			}
		}
	}

	@Override
	public void notifierVendeur(ContratCadre<Chocolat> cc) {
		t2.getContratsChocolatEnCours().add(cc);

		// Ajout de la demande à l'historique (Minh Tri)
		Echeancier e = cc.getEcheancier();
		for(int s = e.getStepDebut(); s <= e.getStepFin(); s++) {
			TasProduit<Chocolat> tas = new TasProduit<>(e.getQuantite(s), cc.getPrixAuKilo());
			t2.getHistoriqueDemande().ajouterDemande(Monde.LE_MONDE.getStep() + s, tas, cc.getProduit());
		}
	}

	// Kelian
	@Override
	public double livrer(Chocolat produit, double quantite, ContratCadre<Chocolat> cc) {
		if (produit == null || !t2.getCHOCOLATS_VENTE().contains(produit))
			throw new IllegalArgumentException("Appel de la methode livrer de Transformateur2 avec un produit ne correspondant pas à un chocolat produit");
		double livraison = Math.min(quantite, t2.getStocksChocolat().getQuantiteTotale(produit));
		t2.getJournal().ajouter("Livraison de " + livraison + " kg de " + produit + " à " + cc.getAcheteur() + " (demande : " + quantite + " kg)");
		t2.getStocksChocolat().prendreProduits(produit, livraison);
		t2.getIndicateurStockChocolat().retirer(t2, livraison);
		
		return livraison;
	}

	// Kelian
	@Override
	public void encaisser(double montant, ContratCadre<Chocolat> cc) {
		if (montant < 0.0) {
			throw new IllegalArgumentException("Appel de la methode encaisser de Transformateur2 avec un montant negatif");
		}
		t2.getSoldeBancaire().ajouter(t2,  montant);
	}
}
