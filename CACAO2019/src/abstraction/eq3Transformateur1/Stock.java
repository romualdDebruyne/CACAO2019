package abstraction.eq3Transformateur1;

import java.util.ArrayList;
import java.util.HashMap;

/** 
 * 
 * @author eve
 *
 */
public class Stock<T> {

	// T est le chocolat ou la feve ; lui est associe une quantite en stock, en kg
	private HashMap<T, Double> stock;
	
	public Stock(ArrayList<T> produits) {
		this.stock = new HashMap<T, Double>();
		for (T p: produits) { 
			this.stock.put(p, 0.);
		}
	}
	public Stock() { }
	
	// -----------------------------------------------------------
	//          GETTERS
	// -----------------------------------------------------------
	
	public double getQuantiteEnStock(T produit) {
		try { 
			if (this.stock.get(produit) > 1.) {
				return this.stock.get(produit); 
			}
			else { return 0.; }
		}
		catch (NullPointerException e) { return 0.; }
	}
	
	public boolean estEnStock(T produit) {
		return this.stock.containsKey(produit) && (this.stock.get(produit) > 1.);
	}
	
	public ArrayList<T> getProduitsEnStock() {
		ArrayList<T> resultat = new ArrayList<T>();
		for (T p: this.stock.keySet()) {
				if (this.getQuantiteEnStock(p) > 0.) {
					resultat.add(p);
				}
		}
		return resultat;
	}
	
	
	// -----------------------------------------------------------
	//          SETTERS
	// -----------------------------------------------------------
	
	public void addQuantiteEnStock(T produit, double quantite)
			throws IllegalArgumentException {
		if (quantite>=0.) {
			double newQuantiteEnStock = getQuantiteEnStock(produit) + quantite;
			this.stock.put(produit, newQuantiteEnStock);
		}
		else {
			throw new IllegalArgumentException("Appel de addQuantiteEnStock avec quantite negative. Utiliser plutot removeQuantiteEnStock");
		}
	}
	
	public void removeQuantiteEnStock(T produit, double quantite)
			throws IllegalArgumentException {
		if (quantite>=0.) {
			double newQuantiteEnStock = getQuantiteEnStock(produit) - quantite;
			if (newQuantiteEnStock<0.) {
				throw new IllegalArgumentException("Quantite retiree trop grande !");
			}
			else {
				this.stock.put(produit, newQuantiteEnStock);
			}
		}
		else {
			throw new IllegalArgumentException("Appel de removeQuantiteEnStock avec quantite positive. Utiliser plutot addQuantiteEnStock");
		}
	}

}
