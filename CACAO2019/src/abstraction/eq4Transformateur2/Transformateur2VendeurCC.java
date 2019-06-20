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
	// On tente de faire une marge de 30%.
	private static final double MARGE_VISEE = 0.3;
	// On ne propose pas un prix si la différence avec le prix proposé est inférieure à 5% (on accepte)
	private static final double SEUIL_ACCEPTATION_FORCEE = 0.05;
	// On ne continue pas les négociations de prix si le prix proposé est inférieur à 80% du coût de production
	private static final double SEUIL_REFUS_FORCE = 0.80;
	
	private Transformateur2 t2;
	
	// Initialise Transformateur2VendeurCC avec un catalogue vide
	public Transformateur2VendeurCC(Transformateur2 trans2) {
		this.t2 = trans2;
	}
	
	// Guillaume
	public List<Chocolat> getProduitsEnVente() {
		ArrayList<Chocolat> chocolat = new ArrayList<Chocolat>();
		chocolat.addAll(t2.stockEnVente.getProduitsEnVente());
		return chocolat;
	}

	@Override
	public StockEnVente<Chocolat> getStockEnVente() {
		StockEnVente<Chocolat> sev = new StockEnVente<Chocolat>();
		for(Chocolat c : t2.CHOCOLATS_VENTE)
			sev.ajouter(c, t2.stocksChocolat.getQuantiteTotale(c));	
		t2.stockEnVente = sev;
		return sev;
	}

	// Minh Tri
	@Override
	public double getPrix(Chocolat produit, Double qte) {
		//System.out.println("getPrix(" + produit + ", " + qte + ") = " + (t2.stocksChocolat.getPrix(produit, qte) * (1.0 + MARGE_VISEE) / qte));
		
		// Si l'on ne vend pas ce type de chocolat, on renvoie +infini
		if(!t2.CHOCOLATS_VENTE.contains(produit) || qte == Double.POSITIVE_INFINITY)
			return Double.MAX_VALUE;
		// Quantité réelle de production de la qté de chocolat demandée + une marge (on re-divise par la quantité pour obtenir le prix au kg)
		return t2.stocksChocolat.getPrix(produit, qte) * (1.0 + MARGE_VISEE) / qte;
	}

	//Adrien
	@Override
	public void proposerEcheancierVendeur(ContratCadre<Chocolat> cc) {
		if(cc.getQuantite() == Double.POSITIVE_INFINITY)
			return;
		
		if (Math.random() < 0.4) { // 40% de chances d'accepter l'échéancier
			cc.ajouterEcheancier(new Echeancier(cc.getEcheancier())); // on accepte la proposition de l'acheteur car on a la quantite en stock 
		} else { // 60% de chance de proposer un echeancier etalant sur un ou deux step de plus, de façon aléatoire
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
		
		double coutProduction = t2.stocksChocolat.getPrix(cc.getProduit(), cc.getQuantite()) / cc.getQuantite();
		double prixAcheteur = cc.getPrixAuKilo();
		double marge = (prixAcheteur - coutProduction) / coutProduction;
		
		// On ne fait une proposition que si l'acheteur ne demande pas un prix trop bas.
		if(prixAcheteur >= SEUIL_REFUS_FORCE * coutProduction) { 
			// Si le prix proposé nous permet de faire une marge, probabilité d'accepter dépendant de cette marge
			if(marge > 0 && Math.random() < 2 * marge) // Ex : marge de 10% => probabilité de 20% d'accepter directement
				cc.ajouterPrixAuKilo(cc.getPrixAuKilo());
			else {
				double prixSouhaite = coutProduction * MARGE_VISEE;
				if(prixAcheteur >= prixSouhaite) // Si le prix est suffisant pour la marge que l'on souhaite, on accepte
					cc.ajouterPrixAuKilo(cc.getPrixAuKilo()); 
				else {
					double prixIntermediaire = (prixAcheteur + prixSouhaite) / 2;
					// Si la différence de prix entre le prix de l'acheteur et le prix que l'on veut proposer est inférieure au seuil, on accepte
					if((prixIntermediaire - prixAcheteur) / prixAcheteur < SEUIL_ACCEPTATION_FORCEE) 
						cc.ajouterPrixAuKilo(cc.getPrixAuKilo());
					else // Sinon, on propose un prix intermédiaire
						cc.ajouterPrixAuKilo(prixIntermediaire);
				}
			}
		}
	}

	@Override
	public void notifierVendeur(ContratCadre<Chocolat> cc) {
		t2.contratsChocolatEnCours.add(cc);

		System.out.println("------ CONTRAT VALIDé ------" + cc.getPrixAuKilo());
		
		// Ajout de la demande à l'historique (Minh Tri)
		Echeancier e = cc.getEcheancier();
		for(int s = e.getStepDebut(); s <= e.getStepFin(); s++) {
			TasProduit<Chocolat> tas = new TasProduit<>(e.getQuantite(s), cc.getPrixAuKilo());
			t2.historiqueDemande.ajouterDemande(Monde.LE_MONDE.getStep() + s, tas, cc.getProduit());
		}
	}

	// Kelian
	@Override
	public double livrer(Chocolat produit, double quantite, ContratCadre<Chocolat> cc) {
		System.out.println("Livraison de " + quantite + " kg de " + produit);
		if (produit == null || !t2.CHOCOLATS_VENTE.contains(produit))
			throw new IllegalArgumentException("Appel de la methode livrer de Transformateur2 avec un produit ne correspondant pas au chocolat produit");
		double livraison = Math.min(quantite, t2.stocksChocolat.getQuantiteTotale(produit));
		t2.stocksChocolat.prendreProduits(produit, livraison);
		t2.iStockChocolat.retirer(t2, livraison);
		
		return livraison;
	}

	// Kelian
	@Override
	public void encaisser(double montant, ContratCadre<Chocolat> cc) {
		if (montant < 0.0) {
			throw new IllegalArgumentException("Appel de la methode encaisser de Transformateur2 avec un montant negatif");
		}
		t2.soldeBancaire.ajouter(t2,  montant);
	}
}
