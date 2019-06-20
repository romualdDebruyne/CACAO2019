package abstraction.eq4Transformateur2;

import java.util.HashMap;

import abstraction.eq7Romu.ventesContratCadre.Echeancier;

/**
 * @author Kelian
 * Classe permettant de mémoriser un planning des stocks que l'on souhaite avoir à disposition à chaque step.
 * Le planning est rempli à l'aide d'estimations obtenues en utilisant l'historique des demandes précédentes.
 */
public class PlanningStock<T> {
	// Pour chaque sous-type de produit, on stocke un échéancier
	private HashMap<T, Echeancier> plannings;
	
	public PlanningStock() {
		plannings = new HashMap<T, Echeancier>();
	}
	
	public void setQuantite(T sousType, int step, double qte) {
		if(!plannings.containsKey(sousType))
			plannings.put(sousType, new Echeancier(0));
		plannings.get(sousType).set(step, qte);
	}
	
	public void addQuantite(T sousType, int step, double qte) {
		setQuantite(sousType, step, getQuantite(sousType, step) + qte);
	}
	
	public double getQuantite(T sousType, int step) {
		if(!plannings.containsKey(sousType))
			return 0;
		return plannings.get(sousType).getQuantite(step);
	}

	/** Réinitialise le planning pour un sous-type donné */
	public void reset(T sousType) {
		plannings.put(sousType, new Echeancier(0));
	}
}
