package abstraction.eq4Transformateur2;

import java.util.ArrayList;
import java.util.List;

import abstraction.eq7Romu.produits.Chocolat;
import abstraction.eq7Romu.produits.Feve;
import abstraction.eq7Romu.ventesContratCadre.ContratCadre;
import abstraction.eq7Romu.ventesContratCadre.Echeancier;
import abstraction.eq7Romu.ventesContratCadre.IAcheteurContratCadre;
import abstraction.eq7Romu.ventesContratCadre.IVendeurContratCadre;
import abstraction.eq7Romu.ventesContratCadre.StockEnVente;
import abstraction.fourni.IActeur;
import abstraction.fourni.Monde;

public class Transformateur2AcheteurCC implements IAcheteurContratCadre<Feve> {
	private Transformateur2 t2;
	
	public Transformateur2AcheteurCC(Transformateur2 trans2) {
		this.t2 = trans2;
	}
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	public ContratCadre<Feve> getNouveauContrat() {
		//return null;/*
		// Calcul du solde que l'on peut dépenser en fonction des contrats sortants et entrants
		double solde = t2.soldeBancaire.getValeur();
		for(ContratCadre<Feve> cc : t2.contratsFevesEnCours)
			solde -= cc.getMontantRestantARegler();
		for(ContratCadre<Chocolat> cc : t2.contratsChocolatEnCours)
			solde += cc.getMontantRestantARegler();

		List<IVendeurContratCadre> vendeurs = new ArrayList<IVendeurContratCadre>();
		
		// Liste des fèves que l'on souhaite acheter 
		List<Feve> fevesAchetees = new ArrayList<Feve>();
		fevesAchetees.add(Feve.CRIOLLO_HG_EQ);
		fevesAchetees.add(Feve.FORASTERO_MG_EQ);
		fevesAchetees.add(Feve.MERCEDES_MG_EQ);
		fevesAchetees.add(Feve.TRINITARIO_MG_EQ);
		
		final double POIDS_MIN_CONTRAT = 150.0; // pas de contrats de moins de 150 kg
		
		// Choix du vendeur : on regroupe d'abord tous les vendeurs vérifiant les conditions souhaitées
		for(IActeur a : Monde.LE_MONDE.getActeurs()) {
			if(a instanceof IVendeurContratCadre) {
				StockEnVente sev = ((IVendeurContratCadre) a).getStockEnVente();
				
				List<Object> produits = sev.getProduitsEnVente();
				for(Object o : produits) {
					if(fevesAchetees.contains(o) && sev.get(o) >= POIDS_MIN_CONTRAT)
						vendeurs.add((IVendeurContratCadre) a);
				}
			}
		}
		
		// On choisit ensuite l'un des vendeurs, si possible
		if(vendeurs.size() > 0) {
			IVendeurContratCadre vendeur = vendeurs.get((int) (Math.random() * vendeurs.size()));
			
			// Construction d'une liste des produits que l'on est susceptibles d'acheter à ce vendeur
			List<Feve> produitsInteressants = new ArrayList<Feve>();
			for(Object o : vendeur.getStockEnVente().getProduitsEnVente()) {
				if(fevesAchetees.contains(o) && vendeur.getStockEnVente().get(o) >= POIDS_MIN_CONTRAT)
					produitsInteressants.add((Feve) o);
			}
			// Choix du produit à acheter (pour l'instant : le produit que l'on a en quantité la plus faible)
			double minStock = 0;
			Feve minProduit = produitsInteressants.get(0);
			for(Feve f : produitsInteressants) {
				double stock = t2.stockFeves.get(f).getQuantiteTotale();
				if(stock < minStock) {
					minStock = stock;
					minProduit = f;
				}
			}
			
			double qté = Math.min(20e3, vendeur.getStockEnVente().get(minProduit)); // on achete au maximum 20 tonnes
			double prix = vendeur.getPrix(minProduit, qté);			
			
			// On réduit la quantité achetée tant que le prix est supérieur à 60% de notre solde
			while(qté * prix > solde * 0.60) {
				qté *= 0.8;
				prix = vendeur.getPrix(minProduit, qté);
			}

			//System.out.println("Nouveau contrat cadre - " + vendeur.toString() + " " + minProduit.name() + " " + qté);
			return new ContratCadre<Feve>(t2, vendeur, minProduit, qté);	
		}
		else
			return null;
			
		//	*/
	}

	@Override
	public void proposerEcheancierAcheteur(ContratCadre<Feve> cc) {
		//*
		if(cc.getEcheancier() == null) { // il n'y a pas encore eu de contre-proposition de la part du vendeur
			// On répartit la quantité sur 12 steps (3 mois)
			cc.ajouterEcheancier(new Echeancier(Monde.LE_MONDE.getStep(), 12, cc.getQuantite() / 12));
		} 
		else {
			Echeancier ech = cc.getEcheancier();
			int duree = ech.getNbEcheances();
			final int DUREE_MAX = 4*6; // on n'accepte pas d'échéancier sur plus de 6 mois : on diminue plutôt la quantité totale
			if(duree > DUREE_MAX)
				cc.ajouterEcheancier(new Echeancier(Monde.LE_MONDE.getStep(), DUREE_MAX, (cc.getQuantite()*0.75)/DUREE_MAX));
			else {
				// On essaye de se rapprocher d'un échéancier de 12 steps
				if(duree > 12)
					cc.ajouterEcheancier(new Echeancier(ech.getStepDebut(), ech.getNbEcheances()-1, cc.getQuantite()/(ech.getNbEcheances()-1)));
				else if(duree < 12) 
					cc.ajouterEcheancier(new Echeancier(ech.getStepDebut(), ech.getNbEcheances()+1, cc.getQuantite()/(ech.getNbEcheances()+1)));
			}
		}//*/
	}

	@Override
	public void proposerPrixAcheteur(ContratCadre<Feve> cc) {
		// TODO Stocker le prix du dernier achat de ce produit et l'utiliser comme référence 
		// (éviter d'acheter plus de 10% plus haut que l'achat le moins cher de ce produit, par exemple)
		double prixVendeur = cc.getListePrixAuKilo().get(0);
		if (Math.random() < 0.25) // probabilite de 25% d'accepter
			cc.ajouterPrixAuKilo(cc.getPrixAuKilo());
		else {
			// On essaye de diminuer le prix (de 15% max)
			final double REDUCTION_MAX = 0.15;
			cc.ajouterPrixAuKilo((prixVendeur - prixVendeur*Math.random()*REDUCTION_MAX));
		}
	}

	@Override
	public void notifierAcheteur(ContratCadre<Feve> cc) { 
		t2.contratsFevesEnCours.add(cc);
	}

	@Override
	public void receptionner(Feve produit, double quantite, ContratCadre<Feve> cc) {
		if(quantite <= 0.0)
			throw new IllegalArgumentException("Appel de la méthode réceptionner de Transformateur2 avec une quantité égale à " + quantite);
		
		t2.stockFeves.get(produit).ajouterTas(new TasFeve(quantite, cc.getPrixAuKilo()));		
		
	}

	@Override
	public double payer(double montant, ContratCadre<Feve> cc) {
		if(montant <= 0.0)
			throw new IllegalArgumentException("Appel de la methode payer de Transformateur2 avec un montant negatif = " + montant);
		
		double paiement = Math.min(montant,  t2.soldeBancaire.getValeur());
		t2.soldeBancaire.retirer(t2,  paiement);
		return paiement;
	}
}
