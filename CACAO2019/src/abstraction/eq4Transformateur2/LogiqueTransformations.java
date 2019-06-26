package abstraction.eq4Transformateur2;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import abstraction.eq7Romu.produits.Chocolat;
import abstraction.eq7Romu.produits.Feve;
import abstraction.eq7Romu.ventesContratCadre.ContratCadre;
import abstraction.eq7Romu.ventesContratCadre.Echeancier;
import abstraction.fourni.Monde;

/**
 * Cette classe contient les fonctions utilisées pour transformer le cacao en chocolat.
 */
public class LogiqueTransformations {
	protected static Transformateur2 t2;
	
	// Kelian
	/** Effectue une transformation. Renvoie la quantité de chocolat produit */
	protected static double effectuerTransformation() {
		// On vérifie d'abord s'il y a des fèves que l'on doit absolument utiliser
		/** Les dates de péremption n'étant pas implémentées, ce comportement est désactivé */
		/*
		Feve f = getFeveCritique();
		if(f != null) {
			Recette r = Recette.getRecettes(f).get(0); // la liste ne contient normalement qu'un élément
			TasProduit<Feve> tas = stockFeves.getProchainTasPerime(f);
			double qte = 0;
			if(r.calculCoutTransformation(tas.getQuantité()) < soldeBancaire.getValeur() * 0.3)
				qte = tas.getQuantité(); // on transforme tout le tas
			else
				qte = r.getQteProductible(soldeBancaire.getValeur() * 0.3); // on transforme le plus possible jusqu'à 30% de notre solde
			
			if(qte < QTE_PRODUCTION_MIN)
				return 0.0;
			executerRecette(r, qte);
			return qte;
		}*/
		
		// On s'intéresse ensuite au chocolat que l'on doit produire en priorité pour satisfaire les échéances des CC en cours
		Pair<Chocolat, Double> c = getChocolatCritique();
		if(c != null && c.getX() != null) {
			List<Recette> recettes = Recette.getRecettes(c.getX());
			
			// On prend la recette pour laquelle on a le plus de stock de fèves
			double maxStock = 0;
			Recette maxRecette = null;
			for(Recette r : recettes) {
				double qteStock = t2.getStockFeves().getQuantiteTotale(r.getInputFeve());
				if(qteStock >= maxStock) {
					maxStock = qteStock;
					maxRecette = r;
				}
			}
			
			// à ce stade maxRecette ne devrait pas être null, sauf si on nous demande de produire un chocolat qui n'a pas de recette associée.
			
			double soldeMaxADepenser = t2.getSoldeBancaire().getValeur();
			double qte = 0;
			if(maxRecette.calculCoutTransformation(c.getY()) <=  soldeMaxADepenser)
				qte = c.getY();
			else
				qte = maxRecette.getQteProductible(soldeMaxADepenser); // on transforme le plus possible jusqu'à 60% de notre sold

			if(qte < ConfigEQ4.QTE_PRODUCTION_MIN)
				return 0.0;
			
			return executerRecette(maxRecette, qte);
		}
		
		return 0.0;
	}
	
	// Kelian
	/** Exécute une recette (actualisation des stocks, du solde, ...). Renvoie true si la recette a bien été executée (false sinon; manque de stock par exemple) */
	private static double executerRecette(Recette r, double qte) {
		t2.getJournal().ajouter("On veut produire " + qte + " kg de " + r.getOutput());
		
		double productionMax = qte;
		
		// Vérification stock
		double fevesNecessaires = r.getInputParKgProduit() * qte;
		if(t2.getStockFeves().getQuantiteTotale(r.getInputFeve()) < fevesNecessaires)
			productionMax = Math.min(productionMax, t2.getStockFeves().getQuantiteTotale(r.getInputFeve()) / r.getInputParKgProduit());
		
		// Vérification solde
		if(t2.getSoldeBancaire().getValeur() * ConfigEQ4.DEPENSE_MAX_PAR_PROD < r.calculCoutTransformation(qte))
			productionMax = Math.min(productionMax, r.getQteProductible(t2.getSoldeBancaire().getValeur() * ConfigEQ4.DEPENSE_MAX_PAR_PROD));
		
		qte = productionMax;
		fevesNecessaires = r.getInputParKgProduit() * qte;
		
		if(qte != 0.0 && fevesNecessaires <= t2.getStockFeves().getQuantiteTotale(r.getInputFeve())) {
			// Exécution stock
			double coutTransfo = r.calculCoutTransformation(qte);
			double coutMatieresPremieres = t2.getStockFeves().getPrix(r.getInputFeve(), fevesNecessaires);
			t2.getStockFeves().prendreProduits(r.getInputFeve(), fevesNecessaires);
			t2.getIndicateurStockFeves().retirer(t2, fevesNecessaires);
			t2.getStocksChocolat().ajouterTas(r.getOutput(), new TasProduit<Chocolat>(qte, qte / (coutMatieresPremieres + coutTransfo)));
			t2.getIndicateurStockChocolat().ajouter(t2, qte);
			
			// Exécution solde
			t2.getSoldeBancaire().retirer(t2, coutTransfo);
			
			
			t2.getJournal().ajouter("Production de " + qte + " kg de " + r.getOutput() + " effectuée.");
		}
		
		return qte;
	}
	
	// Kelian
	/** Renvoie la fève que l'on doit utiliser en priorité pour ne pas en perdre à cause de péremption */
	private static Feve getFeveCritique() {
		// On cherche si un tas périme bientôt
		/** Désactivé tant que les dates de péremption ne sont pas implémentées
		for(Feve f : FEVES_ACHAT) {
			TasProduit<Feve> prochainPerime = stockFeves.getProchainTasPerime(f);
			if(prochainPerime.getDatePeremption() <= Monde.LE_MONDE.getStep() + 2)
				return f;
		}
		*/
		return null;
	}
	
	// Kelian
	/** Renvoie le chocolat que l'on doit produire en priorité pour satisfaire les échéances et le fonds de roulement, et la quantité associée */
	private static Pair<Chocolat, Double> getChocolatCritique() {
		int step = Monde.LE_MONDE.getStep();
		
		// On construit une HashMap qui contient, pour chaque CC en cours, le nombre d'échéances que l'on peut satisfaire avec le stock actuel
		HashMap<ContratCadre<Chocolat>, Integer> derniereEcheanceSatisfiable = new HashMap<ContratCadre<Chocolat>, Integer>();
		
		HashMap<Chocolat, Double> stocks = new HashMap<Chocolat, Double>();
		for(Chocolat c : t2.getCHOCOLATS_VENTE()) 
			stocks.put(c, t2.getStocksChocolat().getQuantiteTotale(c));
		for(ContratCadre<Chocolat> cc : t2.getContratsChocolatEnCours()) {
			Chocolat c = cc.getProduit();
			Echeancier e = cc.getEcheancier();
			double qteDejaLivree = e.getQuantiteJusquA(step - 1);
			
			int i = 0;
			// Tant que l'on n'est pas arrivé au bout de l'échéancier et que l'on a assez de stock
			while(i < e.getNbEcheances() && e.getQuantiteJusquA(step + i) - qteDejaLivree <= stocks.get(c)) {
				stocks.put(c, stocks.get(c) - e.getQuantite(step + i));
				i++;
			}
			
			if(i != e.getNbEcheances())
				derniereEcheanceSatisfiable.put(cc, i-1);
		}

		if(derniereEcheanceSatisfiable.isEmpty()) { // Tous les CC en cours sont satisfiables par notre stock actuel
			// On éssaye alors de satisfaire le fonds de roulement : on cherche le chocolat que l'on possède en plus faible qté
			Chocolat minChocolat = null;
			double minStock = Double.POSITIVE_INFINITY;
			for(Chocolat c : t2.getCHOCOLATS_VENTE()) {
				double s = t2.getStocksChocolat().getQuantiteTotale(c);
				if(s < minStock) {
					minChocolat = c;
					minStock = s;
				}
			}
			// On vérifie ensuite si notre stock pour ce chocolat est inférieur au fonds de roulement
			if(minStock < ConfigEQ4.FONDS_ROULEMENT_CHOCOLAT) {
				double manquant = ConfigEQ4.FONDS_ROULEMENT_CHOCOLAT - minStock;
				t2.getJournal().ajouter("Tous les CC sont satisfaits par notre stock - Tentative de production de " + manquant + " kg de " + minChocolat + " pour le fonds de roulement");
				return new Pair<Chocolat, Double>(minChocolat, manquant);
			}
			else
				return null; // les CCs sont satisfaits et le fonds de roulement est atteint
		}
		
		// On va ensuite chercher le CC pour lequel on peut satisfaire le moins d'échéances
		int minEcheances = Integer.MAX_VALUE;
		ContratCadre<Chocolat> minContrat = null;
		for(Map.Entry<ContratCadre<Chocolat>, Integer> entry : derniereEcheanceSatisfiable.entrySet()) {
		    if(entry.getValue() <= minEcheances) {
		    	minEcheances = entry.getValue();
		    	minContrat = entry.getKey();
		    }
		}

		Chocolat c = minContrat.getProduit();
		
		/* Attention : produire toute la qté nécessaire pour le CC d'un coup n'est pas forcément une bonne idée vis à vis de la péremption. */
		double qteAProduire = minContrat.getQuantiteRestantALivrer() - getStockDisponible(c);
		return new Pair<Chocolat, Double>(c, qteAProduire); 
	}
	
	// Kelian
	/** Renvoie le stock disponible pour un chocolat donné, en prenant en copte les contrats en cours */
	private static double getStockDisponible(Chocolat c) {
		double q = t2.getStocksChocolat().getQuantiteTotale(c);
		for(ContratCadre<Chocolat> cc : t2.getContratsChocolatEnCours()) {
			if(cc.getProduit().equals(c))
				q -= cc.getQuantiteRestantALivrer();
		}
		return q;
	}
}
