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
	private static final double POIDS_MIN_CONTRAT_ACHAT = 300.0; // poids min d'un contrat d'achat de fèves.
	private static final double DEPENSE_MAX_PAR_CC = 0.75; // on ne dépense pas plus de 60% de notre solde par CC
	
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
		// Calcul du solde que l'on peut dépenser en fonction des contrats sortants et entrants
		double solde = t2.soldeBancaire.getValeur();
		for(ContratCadre<Feve> cc : t2.contratsFevesEnCours)
			solde -= cc.getMontantRestantARegler();
		for(ContratCadre<Chocolat> cc : t2.contratsChocolatEnCours)
			solde += cc.getMontantRestantARegler();

		// On souhaite créer un contrat pour le type de fèves dont on a le plus besoin
		HashMap<Feve, Echeancier> echeanciers = creerEcheanciersPourPlanning();
		// On trie les fèves par quantité à acheter
		List<Feve> triParQte = new ArrayList<Feve>();
		for(Feve f : echeanciers.keySet())
			triParQte.add(f);
		triParQte.sort((f1, f2) -> {
			return (int) (echeanciers.get(f2).getQuantiteTotale() - echeanciers.get(f1).getQuantiteTotale());
		});
		
		t2.journal.ajouter("Il manque " + echeanciers.get(triParQte.get(0)).getQuantiteTotale() + " kg de " + triParQte.get(0));
		
		// On prend la première fève (dans l'ordre décroissant des quantités manquantes) pour laquelle on trouve un vendeur
		int idx = 0;
		IVendeurContratCadre<Feve> vendeur = null;
		while(idx < triParQte.size() && vendeur == null) {
			Feve f = triParQte.get(idx);
			double qte = echeanciers.get(f).getQuantiteTotale();
			if(qte != 0.0) {
				vendeur = choisirVendeur(f, qte);
				if(vendeur == null)
					t2.journal.ajouter("Pas trouvé de vendeur pour acheter " + qte + " kg de " + f + ".");
			}
			idx++;
		}
		idx--;
		
		if(vendeur == null)
			return null;
		
		t2.journal.ajouter("Vendeur trouvé pour " + triParQte.get(idx) + ": " + vendeur);
		
		Feve feve = triParQte.get(idx);
		double qte = echeanciers.get(feve).getQuantiteTotale();
		
		if(qte == 0.0 || qte < POIDS_MIN_CONTRAT_ACHAT)
			return null;
		
		// On réduit la quantité achetée tant que le prix est supérieur à un pourcentage fixé de notre solde
		double prix = vendeur.getPrix(feve, qte);
		while(qte > POIDS_MIN_CONTRAT_ACHAT && qte * prix > solde * DEPENSE_MAX_PAR_CC) {
			qte *= 0.8;
			prix = vendeur.getPrix(feve, qte);
		}

		t2.journal.ajouter("Nouveau CC fève : " + vendeur + ", " + feve + ", " + qte + " kg");
		return new ContratCadre<Feve>(t2, vendeur, feve, qte);
		
		
		/*List<Pair<IVendeurContratCadre<Feve>, List<Feve>>> vendeurs = trouverVendeursInteressants();
		
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
			return null;*/
	}
	
	@Override
	public void proposerEcheancierAcheteur(ContratCadre<Feve> cc) {
		if(cc.getEcheancier() == null) { // il n'y a pas encore eu de contre-proposition de la part du vendeur
			cc.ajouterEcheancier(creerEcheancierPourPlanning(cc.getProduit()));
		} 
		else {
			Echeancier echCible = creerEcheancierPourPlanning(cc.getProduit());
			
			cc.ajouterEcheancier(moyenneEcheancier(echCible, cc.getEcheancier()));
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
			cc.ajouterPrixAuKilo((prixVendeur - prixVendeur * Math.random() * REDUCTION_MAX));
		}
	}
	
	// Kelian
	/** Pour chaque fève, crée un échéancier des quantités que l'on doit acheter pour satisfaire le planning de stock */	
	private HashMap<Feve, Echeancier> creerEcheanciersPourPlanning() {
		HashMap<Feve, Echeancier> echeanciers = new HashMap<Feve, Echeancier>();
		for(Feve f : t2.FEVES_ACHAT)
			echeanciers.put(f, creerEcheancierPourPlanning(f));
		return echeanciers;
	}	
	
	/** Pour une fève donnée, crée un échéancier des quantités que l'on doit acheter pour satisfaire le planning de stock,
	 * en prenant en compte le stock actuel et les contrats cadres entrants */
	private Echeancier creerEcheancierPourPlanning(Feve f) {
		Echeancier e = new Echeancier(Monde.LE_MONDE.getStep());
		double qte = t2.stockFeves.getQuantiteTotale(f);
		for(int i = 0; i <= Transformateur2.STEPS_ESTIMATION_DEMANDE_FUTURE; i++) {
			int step = Monde.LE_MONDE.getStep() + i;
			double qteManquante = t2.planningStockFeves.getQuantite(f, step);
			// On prend un maximum de fèves dans le stock actuel
			double aPrendreDansStock = Math.min(qte, qteManquante);
			qteManquante -= aPrendreDansStock;
			qte -= aPrendreDansStock;
			
			for(ContratCadre<Feve> cc : t2.contratsFevesEnCours) {
				if(cc.getProduit().equals(f))
					qteManquante -= cc.getEcheancier().getQuantite(step);
			}
			
			// Si l'on a un overflow de fèves pour ce step, on considère qu'on les aura en stock au step précédent
			if(qteManquante < 0)
				qte += -qteManquante;
			
			e.set(step, Math.max(0, qteManquante));
		}
		if(e.getQuantiteTotale() != 0.0)
			t2.journal.ajouter("Qte manquante pour " + f + " : " + e.getQuantiteTotale() + " kg.");
		return e;
	}
	
	// Kelian
	/** Renvoie le vendeur le plus "adapté" pour acheter une quantité donnée d'un type de fève donné */
	private IVendeurContratCadre<Feve> choisirVendeur(Feve feve, double qte) {
		// On attribue un score à chaque vendeur en fonction de la qte vendue et du prix
		double maxScore = 0.0;
		IVendeurContratCadre<Feve> maxVendeur = null;
		for(IActeur a : Monde.LE_MONDE.getActeurs()) {
			if(a instanceof IVendeurContratCadre<?>) {
				List<Object> produitsEnVente = ((IVendeurContratCadre) a).getStockEnVente().getProduitsEnVente();
				// On vérifie que le vendeur vend bien des fèves
				if(produitsEnVente.size() == 0 || !(produitsEnVente.get(0) instanceof Feve))
					continue;
				
				@SuppressWarnings("unchecked") // A ce stade, on est sûrs qu'il s'agit bien d'un IVendeurContratCadre<Feve>
				IVendeurContratCadre<Feve> vendeur = (IVendeurContratCadre<Feve>) a;
				StockEnVente<Feve> sev = vendeur.getStockEnVente();
				
				if(sev.getProduitsEnVente().contains(feve)) {
					double qteEnVente = sev.get(feve);
					double prixAuKilo = vendeur.getPrix(feve, Math.min(qte, qteEnVente));
					double score = Math.min(qte, qteEnVente) / qte - 0.15 * prixAuKilo;
					if(score > maxScore) {
						maxScore = score;
						maxVendeur = vendeur;
					}
				}
				
			}
		}
		return maxVendeur;
	}
	

	// Kelian
	/** Regroupe tous les vendeurs vérifiant les conditions souhaitées (type de fèves vendues, poids min) et les fèves qu'ils vendent
	 *  On utilise une liste de paires et non une HashMap pour pouvoir récupérer un élément par indice
	 */
	/*private List<Pair<IVendeurContratCadre<Feve>, List<Feve>>> trouverVendeursInteressants() {
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
	}*/
	
	// Kelian
	/** Calcule une moyenne entre deux échéanciers */
	private Echeancier moyenneEcheancier(Echeancier e1, Echeancier e2) {
		int duree = Math.max(e1.getNbEcheances(), e2.getNbEcheances());
		Echeancier e3 = new Echeancier(Monde.LE_MONDE.getStep());
		for(int i = 0; i < duree; i++) {
			int step = Monde.LE_MONDE.getStep() + i;
			e3.set(step, (e1.getQuantite(step) + e2.getQuantite(step)) / 2);
		}
		return e3;
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
		
		t2.journal.ajouter("Réception de " + quantite + " kg de " + produit);
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