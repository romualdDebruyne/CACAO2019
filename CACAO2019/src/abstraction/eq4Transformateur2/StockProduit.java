package abstraction.eq4Transformateur2;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

/**
 * @author Kelian
 * Stocke une file de tas de fèves (ou de chocolat) pour chaque type de fèves (ou de chocolat)
 */
public class StockProduit<T> {
	private HashMap<T , LinkedList<TasProduit<T>>> stocks;
	
	public StockProduit(List<T> stockables) {
		// Initialisation des files
		stocks = new HashMap<T, LinkedList<TasProduit<T>>>();
		for(int i = 0; i < stockables.size(); i++)
			stocks.put(stockables.get(i), new LinkedList<TasProduit<T>>());
	}
	
	public void ajouterTas(T type, TasProduit<T> t) {
		stocks.get(type).add(t);
	}
	
	public double getQuantiteTotale(T sousType) {
		// On récupère les tas de fèves/chocolat correspondant à ce sous-type
		LinkedList<TasProduit<T>> tas = stocks.get(sousType);
		double qty = 0;
		// On additionne les quantités de produit de chaque tas
		for(int i = 0; i < tas.size(); i++)
			qty += tas.get(i).getQuantité();
		return qty;
	}
	
	/** Renvoie le prix correspondant à la quantité demandée (ou 0 si l'on n'a pas cette quantité) */
	public double getPrix(T sousType, double qty) {
		// On récupère les tas correspondant à ce sous-type
		LinkedList<TasProduit<T>> tas = stocks.get(sousType);
		
		if(tas.isEmpty() || qty > getQuantiteTotale(sousType))
			return 0;
		else {
			double prix = 0;
			int i = 0;
			while(qty > 0 && i < tas.size()) {
				TasProduit<T> t = tas.get(i); // prochain tas à vider
				double qteAPrendre = Math.min(qty, t.getQuantité());
				qty -= qteAPrendre;
				prix += qteAPrendre * t.getPrixAuKilo();
				i++;
			}
			return prix;
		}
	}
	
	/** Récupère les fèves demandées dans la file. Renvoie le prix total (ou 0 s'il n'y a pas assez de fèves) */
	public double prendreProduits(T sousType, double qty) {
		// On récupère les tas correspondant à ce sous-type
		LinkedList<TasProduit<T>> tas = stocks.get(sousType);
		
		if(tas.isEmpty() || qty > getQuantiteTotale(sousType))
			return 0;
		else {
			double prix = 0;
			while(qty > 0) {
				TasProduit<T> t = tas.peek(); // prochain tas à vider
				double qteAPrendre = Math.min(qty, t.getQuantité());
				t.prendre(qty);
				qty -= qteAPrendre;
				prix += qteAPrendre * t.getPrixAuKilo();
				// On supprime le tas s'il est vide
				if(t.getQuantité() == 0)
					tas.pop();
			}
			return prix;
		}
	}
	
	/** Pour un sous-type donné, renvoie le prochain tas qui périme */
	public TasProduit<T> getProchainTasPerime(T sousType) {
		LinkedList<TasProduit<T>> tas = stocks.get(sousType);
		
		int minDatePeremption = 0;
		TasProduit<T> minTas = tas.get(0);
		for(TasProduit<T> t : tas) {
			if(t.getDatePeremption() < minDatePeremption) {
				minDatePeremption = t.getDatePeremption();
				minTas = t;
			}
		}
		return minTas;
	}
}
