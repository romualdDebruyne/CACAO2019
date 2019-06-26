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

	private Transformateur2 t2;
	
	/** Stockage du dernier prix d'achat de chaque fève. Utilisé dans notre stratégie de négociation des prix. */
	private HashMap<Feve, Double> dernierPrixAchat;
	
	public Transformateur2AcheteurCC(Transformateur2 trans2) {
		this.t2 = trans2;
		
		LogiqueTransformations.t2 = t2;
		
		// Adrien et Guillaume
		dernierPrixAchat = new HashMap<Feve,Double>();
		for (Feve f : t2.getFEVES_ACHAT())
			dernierPrixAchat.put(f, 0.0);
	}
	
	
	// Kelian
	@Override
	public ContratCadre<Feve> getNouveauContrat() {
		// Calcul du solde que l'on peut dépenser en fonction des contrats sortants et entrants
		double solde = t2.getSoldeBancaire().getValeur();
		for(ContratCadre<Feve> cc : t2.getContratsFevesEnCours())
			solde -= cc.getMontantRestantARegler();
		for(ContratCadre<Chocolat> cc : t2.getContratsChocolatEnCours())
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
		
		for(Feve f : triParQte) {
			if(echeanciers.get(f).getQuantiteTotale() != 0.0)
				t2.getJournal().ajouter("Il manque " + echeanciers.get(f).getQuantiteTotale() + " kg de " + f);
		}
		
		// On prend la première fève (dans l'ordre décroissant des quantités manquantes) pour laquelle on trouve un vendeur
		int idx = 0;
		IVendeurContratCadre<Feve> vendeur = null;
		while(idx < triParQte.size() && vendeur == null) {
			Feve f = triParQte.get(idx);
			double qte = echeanciers.get(f).getQuantiteTotale();
			if(qte != 0.0) {
				vendeur = choisirVendeur(f, qte);
				if(vendeur == null)
					t2.getJournal().ajouter("[IMPORTANT] Pas trouvé de vendeur pour acheter " + qte + " kg de " + f + ".");
			}
			idx++;
		}
		idx--;
		
		if(vendeur == null)
			return null;
		
		Feve feve = triParQte.get(idx);
		double qte = echeanciers.get(feve).getQuantiteTotale();
		
		if(qte == 0.0 || qte < ConfigEQ4.POIDS_MIN_CONTRAT_ACHAT)
			return null;
		
		// On réduit la quantité achetée tant que le prix est supérieur à un pourcentage fixé de notre solde
		double prix = vendeur.getPrix(feve, qte);
		while(qte > ConfigEQ4.POIDS_MIN_CONTRAT_ACHAT && qte * prix > solde * ConfigEQ4.DEPENSE_MAX_PAR_CC) {
			qte *= 0.8;
			prix = vendeur.getPrix(feve, qte);
		}

		t2.getJournal().ajouter("Début négociation nouveau CC fève : " + feve + " " + qte + " kg  -  " + vendeur);
		return new ContratCadre<Feve>(t2, vendeur, feve, qte);
	}
	
	@Override
	public void proposerEcheancierAcheteur(ContratCadre<Feve> cc) {
		if(cc.getEcheancier() == null) { // il n'y a pas encore eu de contre-proposition de la part du vendeur
			cc.ajouterEcheancier(creerEcheancierPourPlanning(cc.getProduit(), cc.getQuantite()));
		} 
		else {
			t2.getJournal().ajouter("Proposition nouvel échéancier");
			Echeancier echCible = creerEcheancierPourPlanning(cc.getProduit(), cc.getQuantite());
			
			cc.ajouterEcheancier(moyenneEcheancier(echCible, cc.getEcheancier()));
		}
	}

	@Override
	public void proposerPrixAcheteur(ContratCadre<Feve> cc) {
		t2.getJournal().ajouter("Négo prix " + cc.getPrixAuKilo());
		
		double prixPropose = cc.getPrixAuKilo();
		
		Feve f = cc.getProduit();
		double prixLimite = getDernierPrixAchat(f) == 0 ? Double.MAX_VALUE : getDernierPrixAchat(f) * 1.25;
		double prixARenvoyer;
		
		if(prixPropose <= prixLimite) {
			double prixCible;
			// Au début des négos, on tente de diminuer le prix par rapport à la dernière fois
			if(cc.getListePrixAuKilo().size() <= 1)
				prixCible = getDernierPrixAchat(f) * 0.90;
			// Après, on se contente d'éssayer de diminuer un peu
			else
				prixCible = prixPropose * 0.90;

			prixARenvoyer = (prixPropose + prixCible) / 2;
		}
		else {
			// Au début des négos, on tente de diminuer le prix légèrement par rapport à la dernière fois
			if(cc.getListePrixAuKilo().size() <= 1)
				prixARenvoyer = getDernierPrixAchat(f) * 0.95;
			// Après, on fait la moyenne entre le dernier prix et le prix proposé
			else
				prixARenvoyer = (getDernierPrixAchat(f) + prixPropose) / 2;
		}
		if((prixARenvoyer - prixPropose) / prixPropose < ConfigEQ4.SEUIL_MODIFICATION_PRIX)
			cc.ajouterPrixAuKilo(prixPropose);
		else
			cc.ajouterPrixAuKilo(prixARenvoyer);
	}
	
	// Kelian
	/** Pour chaque fève, crée un échéancier des quantités que l'on doit acheter pour satisfaire le planning de stock */	
	private HashMap<Feve, Echeancier> creerEcheanciersPourPlanning() {
		HashMap<Feve, Echeancier> echeanciers = new HashMap<Feve, Echeancier>();
		for(Feve f : t2.getFEVES_ACHAT())
			echeanciers.put(f, creerEcheancierPourPlanning(f, -1));
		return echeanciers;
	}	
	
	/** Pour une fève donnée, crée un échéancier des quantités que l'on doit acheter pour satisfaire le planning de stock,
	 * en prenant en compte le stock actuel et les contrats cadres entrants 
	 * @param qteTotaleEcheancier */
	private Echeancier creerEcheancierPourPlanning(Feve f, double qteTotaleEcheancier) {
		Echeancier e = new Echeancier(Monde.LE_MONDE.getStep());
		double qte = t2.getStockFeves().getQuantiteTotale(f);
		double qteDansEcheancier = 0.0;
		
		for(int i = 0; i <= ConfigEQ4.STEPS_ESTIMATION_DEMANDE_FUTURE; i++) {
			int step = Monde.LE_MONDE.getStep() + i;
			double qteManquante = t2.getPlanningStockFeves().getQuantite(f, step);

			// On prend un maximum de fèves dans le stock actuel
			double aPrendreDansStock = Math.min(qte, qteManquante);
			qteManquante -= aPrendreDansStock;
			qte -= aPrendreDansStock;
			
			for(ContratCadre<Feve> cc : t2.getContratsFevesEnCours()) {
				if(cc.getProduit().equals(f))
					qteManquante -= cc.getEcheancier().getQuantite(step);
			}
			
			// Si l'on a un overflow de fèves pour ce step, on considère qu'on les aura en stock au step suivant
			if(qteManquante < 0) {
				qte += -qteManquante;
				qteManquante = 0;
			}
			
			// Si la quantité totale de l'échéancier vaut -1, c'est à nous de spécifier la quantité, on est donc libres d'ajouter autant de fèves que l'on souhaite
			if(qteTotaleEcheancier != -1 && qteDansEcheancier + qteManquante > qteTotaleEcheancier) {
				e.set(step, qteTotaleEcheancier - qteDansEcheancier);
				return e;
			}
			else {
				e.set(step, qteManquante);
				qteDansEcheancier += qteManquante;
			}	
		}

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
	/** Calcule une moyenne entre deux échéanciers */
	private Echeancier moyenneEcheancier(Echeancier e1, Echeancier e2) {
		int duree = Math.max(e1.getNbEcheances(), e2.getNbEcheances());
		Echeancier e3 = new Echeancier(Monde.LE_MONDE.getStep());
		for(int i = 0; i <= duree; i++) {
			int step = Monde.LE_MONDE.getStep() + i;
			e3.set(step, (e1.getQuantite(step) + e2.getQuantite(step)) / 2);
		}
		return e3;
	}
 
	@Override
	public void notifierAcheteur(ContratCadre<Feve> cc) { 
		t2.getContratsFevesEnCours().add(cc);
		dernierPrixAchat.put(cc.getProduit(), cc.getPrixAuKilo());
	}

	// Kelian
	@Override
	public void receptionner(Feve produit, double quantite, ContratCadre<Feve> cc) {
		if(quantite <= 0.0)
			throw new IllegalArgumentException("Appel de la méthode réceptionner de Transformateur2 avec une quantité égale à " + quantite);
		
		t2.getJournal().ajouter("Réception de " + quantite + " kg de " + produit);
		t2.getStockFeves().ajouterTas(produit, new TasProduit<Feve>(quantite, cc.getPrixAuKilo()));		
		t2.getIndicateurStockFeves().ajouter(t2, quantite);
	}

	// Kelian
	@Override
	public double payer(double montant, ContratCadre<Feve> cc) {
		if(montant <= 0.0)
			throw new IllegalArgumentException("Appel de la methode payer de Transformateur2 avec un montant negatif = " + montant);
		
		double paiement = Math.min(montant,  t2.getSoldeBancaire().getValeur());
		t2.getSoldeBancaire().retirer(t2,  paiement);
		return paiement;
	}
	
	// Guillaume
	public double getDernierPrixAchat(Feve f) {
		return dernierPrixAchat.get(f);
	}
}