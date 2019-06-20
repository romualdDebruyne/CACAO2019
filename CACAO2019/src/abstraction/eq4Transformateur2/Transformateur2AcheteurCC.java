package abstraction.eq4Transformateur2;

import java.util.ArrayList;
import java.util.HashMap;
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
	private static final double POIDS_MIN_CONTRAT_ACHAT = 10000.0; // poids min d'un contrat d'achat de fèves.
	private static final double DEPENSE_MAX_PAR_CC = 0.60; // on ne dépense pas plus de 60% de notre solde par CC
	
	private Transformateur2 t2;
	
	private HashMap<Feve,Double> dernierPrixAchat;
	
	public Transformateur2AcheteurCC(Transformateur2 trans2) {
		this.t2 = trans2;
		
		// Adrien et Guillaume
		dernierPrixAchat = new HashMap<Feve,Double>();
		for (Feve f : t2.FEVES_ACHAT)
			dernierPrixAchat.put(f, 0.0);
	}
	
	
	// Kelian
	@Override
	public ContratCadre<Feve> getNouveauContrat() {
		//return null;/*
		// Calcul du solde que l'on peut dépenser en fonction des contrats sortants et entrants
		double solde = t2.soldeBancaire.getValeur();
		for(ContratCadre<Feve> cc : t2.contratsFevesEnCours)
			solde -= cc.getMontantRestantARegler();
		for(ContratCadre<Chocolat> cc : t2.contratsChocolatEnCours)
			solde += cc.getMontantRestantARegler();

		List<Pair<IVendeurContratCadre<Feve>, List<Feve>>> vendeurs = trouverVendeursInteressants();
		
		// On choisit ensuite l'un des vendeurs, si possible
		if(vendeurs.size() > 0) {
			// TODO Choix moins arbitraire du vendeur
			Pair<IVendeurContratCadre<Feve>, List<Feve>> randomPair = vendeurs.get((int) (Math.random() * vendeurs.size()));
			IVendeurContratCadre<Feve> vendeur = randomPair.getX();
			// Liste des produits que l'on est susceptibles d'acheter à ce vendeur
			List<Feve> produitsInteressants = randomPair.getY();
			
			// Choix du produit à acheter (pour l'instant : le produit que l'on a en quantité la plus faible)
			double minStock = 0;
			Feve minProduit = produitsInteressants.get(0);
			for(Feve f : produitsInteressants) {
				double stock = t2.stockFeves.getQuantiteTotale(f);
				if(stock < minStock) {
					minStock = stock;
					minProduit = f;
				}
			}
			
			double qté = vendeur.getStockEnVente().get(minProduit);
			double prix = vendeur.getPrix(minProduit, qté);			
			
			// On réduit la quantité achetée tant que le prix est supérieur à un pourcentage fixé de notre solde
			while(qté > POIDS_MIN_CONTRAT_ACHAT && qté * prix > solde * DEPENSE_MAX_PAR_CC) {
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

	// Kelian
	/** Regroupe tous les vendeurs vérifiant les conditions souhaitées (type de fèves vendues, poids min) et les fèves qu'ils vendent
	 *  On utilise une liste de paires et non une HashMap pour pouvoir récupérer un élément par indice
	 */
	private List<Pair<IVendeurContratCadre<Feve>, List<Feve>>> trouverVendeursInteressants() {
		List<Pair<IVendeurContratCadre<Feve>, List<Feve>>> vendeurs = new ArrayList<Pair<IVendeurContratCadre<Feve>, List<Feve>>>();
		for(IActeur a : Monde.LE_MONDE.getActeurs()) {
			if(a instanceof IVendeurContratCadre<?>) {
				List<Object> produitsEnVente = ((IVendeurContratCadre) a).getStockEnVente().getProduitsEnVente();
				// On vérifie que le vendeur vend bien des fèves
				if(produitsEnVente.size() == 0 || !(produitsEnVente.get(0) instanceof Feve))
					continue;
				
				@SuppressWarnings("unchecked") // A ce stade, on est sûrs qu'il s'agit bien d'un IVendeurContratCadre<Feve>
				IVendeurContratCadre<Feve> vendeur = (IVendeurContratCadre<Feve>) a;
				StockEnVente<Feve> sev = vendeur.getStockEnVente();

				List<Feve> produitsInteressants = new ArrayList<Feve>();
				for(Feve f : sev.getProduitsEnVente()) {
					if(t2.FEVES_ACHAT.contains(f) && sev.get(f) >= POIDS_MIN_CONTRAT_ACHAT)
						produitsInteressants.add(f);
				}
				if(!produitsInteressants.isEmpty())
					vendeurs.add(new Pair<IVendeurContratCadre<Feve>, List<Feve>>(vendeur, produitsInteressants));
			}
		}
		return vendeurs;
	}
	
	@Override
	public void proposerEcheancierAcheteur(ContratCadre<Feve> cc) {
		//*
		if(cc.getEcheancier() == null) { // il n'y a pas encore eu de contre-proposition de la part du vendeur
			// On répartit la quantité sur 12 steps
			cc.ajouterEcheancier(new Echeancier(Monde.LE_MONDE.getStep(), 12, cc.getQuantite() / 12));
		} 
		else {
			Echeancier ech = cc.getEcheancier();
			int duree = ech.getNbEcheances();
			
			// On essaye de se rapprocher d'un échéancier de 12 steps
			if(duree > 12)
				cc.ajouterEcheancier(new Echeancier(ech.getStepDebut(), ech.getNbEcheances()-1, cc.getQuantite()/(ech.getNbEcheances()-1)));
			else if(duree < 12) 
				cc.ajouterEcheancier(new Echeancier(ech.getStepDebut(), ech.getNbEcheances()+1, cc.getQuantite()/(ech.getNbEcheances()+1)));
		}
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
		dernierPrixAchat.put(cc.getProduit(), cc.getPrixAuKilo());
	}

	// Kelian
	@Override
	public void receptionner(Feve produit, double quantite, ContratCadre<Feve> cc) {
		if(quantite <= 0.0)
			throw new IllegalArgumentException("Appel de la méthode réceptionner de Transformateur2 avec une quantité égale à " + quantite);
		
		t2.stockFeves.ajouterTas(produit, new TasProduit<Feve>(quantite, cc.getPrixAuKilo()));		
		t2.iStockFeves.ajouter(t2, quantite);
	}

	// Kelian
	@Override
	public double payer(double montant, ContratCadre<Feve> cc) {
		if(montant <= 0.0)
			throw new IllegalArgumentException("Appel de la methode payer de Transformateur2 avec un montant negatif = " + montant);
		
		double paiement = Math.min(montant,  t2.soldeBancaire.getValeur());
		t2.soldeBancaire.retirer(t2,  paiement);
		return paiement;
	}
	
	// Guillaume
	public double getDernierPrixAchat(Feve f) {
		return dernierPrixAchat.get(f);
	}
}
