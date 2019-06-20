package abstraction.eq4Transformateur2;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import abstraction.eq7Romu.produits.Chocolat;
import abstraction.fourni.Monde;

public class HistoriqueDemande {
	private HashMap<Integer, HashMap<Chocolat,TasProduit<Chocolat>>> historique;
	
	// Mémoire de notre estimateur de demande (en années). On utilise les n dernières années pour estimer les demandes futures
	private static final int MEMOIRE_ESTIMATEUR_ANNEES = 5; 
	
	public HistoriqueDemande() {
		historique = new HashMap<Integer, HashMap<Chocolat, TasProduit<Chocolat>>>();
	}
	
	// Guillaume
	public void ajouterDemande(int step, TasProduit<Chocolat> tas, Chocolat type) {
		if(!historique.containsKey(step))
			historique.put(step, new HashMap<Chocolat, TasProduit<Chocolat>>());
		
		HashMap<Chocolat, TasProduit<Chocolat>> demandeParProduit = historique.get(step);
		// Si on a déjà enregistré une demande de ce type de chocolat à ce step, on la fusionne avec la nouvelle demande
		if(demandeParProduit.containsKey(type))
			demandeParProduit.put(type, fusionTasProduit(tas, demandeParProduit.get(type)));
		else
			demandeParProduit.put(type, tas);
	}
	
	// Kelian
	/** (polymorphisme) Renvoie la demande pour un type de chocolat donné, à un certain step d'un nombre d'année donné dans le passé */
	public TasProduit<Chocolat> getDemande(int yearsBack, int stepInYear, Chocolat type) {
		return getDemande(yearsBack * Transformateur2.STEPS_PAR_ANNEE - stepInYear, type);
	}	
	
	/** Renvoie la demande pour un type de chocolat donné à un nombre de steps donné dans le passé */
	public TasProduit<Chocolat> getDemande(int stepsBack, Chocolat type) {
		int currentStep = Monde.LE_MONDE.getStep();
		int targetStep = currentStep - stepsBack; 
		if(targetStep < 0 || targetStep > currentStep)
			return null;
		HashMap<Chocolat, TasProduit<Chocolat>> demandeAncienne = historique.get(targetStep);
		if(demandeAncienne == null || demandeAncienne.get(type) == null)
			return new TasProduit<>(0, 0);
		else
			return demandeAncienne.get(type);
	}	
	
	// Adrien et Guillaume
	/** Fusionne deux TasProduit<Chocolat> en additionnant les quantités et en faisant une moyenne pondérée des prix au kilo. */
	private TasProduit<Chocolat> fusionTasProduit(TasProduit<Chocolat> tas1, TasProduit<Chocolat> tas2) {
		double qteTotale = tas1.getQuantité() + tas2.getQuantité();
		double prixPondere = (tas1.getPrixAuKilo() * tas1.getQuantité() + tas2.getPrixAuKilo() * tas2.getQuantité()) / qteTotale;
		return new TasProduit<Chocolat>(qteTotale, prixPondere);
	}
	
	// Kelian
	public double estimerDemande(int stepsInFuture, Chocolat type) {
		// On commence par récupérer les données des années précédentes
		List<TasProduit<Chocolat>> echantillons = new ArrayList<TasProduit<Chocolat>>();
		for(int i = 1; i <= MEMOIRE_ESTIMATEUR_ANNEES && Monde.LE_MONDE.getStep() > i*Transformateur2.STEPS_PAR_ANNEE; i++) {
			TasProduit<Chocolat> e = getDemande(i * Transformateur2.STEPS_PAR_ANNEE, type);
			echantillons.add(e);
		}
		
		double n = echantillons.size();
		
		if(n == 0)
			return -1; // Estimation impossible
		
		// Moyenne des demandes des années précédentes
		double m = 0.0;
		for(int i = 0; i < n; i++)
			m += echantillons.get(i).getQuantité() / n;
		return m;
		
		/* 
		double moyenneEmpirique = 0, varEmpiriqueModifiee = 0;
		for(int i = 0; i < n; i++)
			moyenneEmpirique += echantillons.get(i).getQuantité() / n;
		for(int i = 0; i < n; i++)
			varEmpiriqueModifiee += Math.pow(echantillons.get(i).getQuantité() - moyenneEmpirique, 2) / (n-1);
			
		// Estimation utilisée : borne haute de l'intervalle de confiance à 95% pour la moyenne
		*/

	}
}




