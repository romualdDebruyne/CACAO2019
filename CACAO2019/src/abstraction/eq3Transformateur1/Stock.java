package abstraction.eq3Transformateur1;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Arrays;

import abstraction.eq7Romu.produits.Chocolat;
import abstraction.eq7Romu.produits.Feve;

/** 
 * 
 * @author eve
 *
 */
public class Stock<T> {

	// T est le chocolat ou la feve ; lui est associe une liste de lots
	HashMap<T, Collection<Lot>> stock;
	int numLot;
	int peremptionFeve;
	int peremptionChocolat;
	
	public Stock(ArrayList<T> produits) {
		this.stock = new HashMap<T, Collection<Lot>>();
	
		for (T p: produits) { 
			this.stock.put(p, new ArrayList<Lot>());
		}
		this.peremptionFeve = 120;
		this.peremptionChocolat = 60;
	}
	public Stock() { }
	
	// -----------------------------------------------------------
	//          GETTERS
	// -----------------------------------------------------------
	
	
	
	public double getQuantiteEnStock(T produit) {
		try { 
			double result = 0.;
			for (Lot l: this.stock.get(produit)) {
				if (l.getQuantite() > 1.) {
					result += l.getQuantite();
				}
			}
			return result;
		}
		catch (NullPointerException e) { return 0.; }
	}
	
	public double getQuantiteEnStockTotale() {
		double result = 0.;
		for (T t : this.stock.keySet()) {
			result = result + this.getQuantiteEnStock(t);
		}
		return result;
	}
	
	
	public boolean estEnStock(T produit) {
		return this.stock.containsKey(produit) && (this.getQuantiteEnStock(produit) > 1.);
	}
	
	public ArrayList<T> getProduitsEnStock() {
		ArrayList<T> resultat = new ArrayList<T>();
		for (T p: this.stock.keySet()) {
				if (this.getQuantiteEnStock(p) > 1.) {
					resultat.add(p);
				}
		}
		return resultat;
	}
	
	public int getNombreDeLots(ArrayList<T> produits) {
		int nbLots = 0;
		for (T p: produits) {
			nbLots = nbLots + this.stock.get(p).size();
		}
		return nbLots;
	}
	
	
	// -----------------------------------------------------------
	//          SETTERS
	// -----------------------------------------------------------
	
	private void nouveauLot(T produit, double quantite, int date) {
		try { 	this.stock.get(produit).add(new Lot(this.numLot, quantite, date));
				this.incrLot(); }
		catch (IllegalArgumentException e) { 
			this.stock.put(produit, new ArrayList<Lot>());
			this.stock.get(produit).add(new Lot(this.numLot, quantite, date));
			this.incrLot();
		}
	}
	
	private Lot getLotLePlusVieux(T produit) {
		Lot result = new Lot();
		result.setDate(10000000); // arbitrairement grand
		for (Lot l: this.stock.get(produit)) {
			if (l.getDate() < result.getDate()) {
				result = l;
			}
		}
		return result;
	}
	
	public void addQuantiteEnStock(T produit, double quantite)
			throws IllegalArgumentException {
		if (quantite>=0.) {
			// chocolat ou feve = pas meme date de peremption
			if (produit instanceof Chocolat) { this.nouveauLot(produit, quantite, peremptionChocolat); }
			else if (produit instanceof Feve) { this.nouveauLot(produit, quantite, peremptionFeve); }
			else { this.nouveauLot(produit, quantite, 1000); }
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
				double quantiteRestante = quantite;
				Lot lot = new Lot();
				while (quantiteRestante > 10e-8) {
//					System.out.println("dans le while");
					lot = this.getLotLePlusVieux(produit);
					this.stock.get(produit).remove(lot);
					if (lot.getQuantite() > quantiteRestante) { lot.retirerQuantiteLot(quantiteRestante);
																quantiteRestante = 0.;
																this.stock.get(produit).add(lot); }
//																System.out.println("lot 1 : " + lot.getQuantite());}
					else { 	quantiteRestante = quantiteRestante - lot.getQuantite(); }
//							System.out.println("lot 2 : " + lot.getQuantite()); }
//					System.out.println("reste " + quantiteRestante);
				}
			}
		}
		else {
			throw new IllegalArgumentException("Appel de removeQuantiteEnStock avec quantite positive. Utiliser plutot addQuantiteEnStock");
		}
	}
	
	// -----------------------------------------------------------
	//          METHODS
	// -----------------------------------------------------------
	
	private void incrLot() {
		this.numLot ++;
	}
	
	public void decrDate() {
		for (T produit: this.stock.keySet()) {
			ArrayList<Lot> lotsAModifier = (ArrayList<Lot>) ((ArrayList<Lot>) this.stock.get(produit)).clone();
			for (Lot lot: lotsAModifier) {
				lot.setDate(lot.getDate() - 1);
				if (lot.getDate() == 0) {
					this.stock.get(produit).remove(lot);
//					System.out.println("lot " + lot.getnLot() + " de " + produit + " retiré, quantité " + lot.getQuantite());
				}
			}
		}
	}
	
	
	// -----------------------------------------------------------
	//          TESTS
	// -----------------------------------------------------------
	
/*	public static void main(String [] args) {
		Stock<Chocolat> test = new Stock<Chocolat>( new ArrayList(Arrays.asList(Chocolat.values())) );
//		test.removeQuantiteEnStock(Chocolat.MG_E_SHP, 1000);
		test.addQuantiteEnStock(Chocolat.MG_E_SHP, 10);
//		test.removeQuantiteEnStock(Chocolat.MG_E_SHP, 1000);
		test.addQuantiteEnStock(Chocolat.MG_E_SHP, 100000);
		test.removeQuantiteEnStock(Chocolat.MG_E_SHP, 1000);
		System.out.println(test.getQuantiteEnStock(Chocolat.MG_E_SHP));
	}
*/
	
}
