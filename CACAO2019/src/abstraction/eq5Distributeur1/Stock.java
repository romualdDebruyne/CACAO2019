package abstraction.eq5Distributeur1;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
//import java.util.Set;

import abstraction.eq7Romu.produits.Chocolat;
import abstraction.fourni.IActeur;
import abstraction.fourni.Indicateur;
import abstraction.fourni.Monde;

public class Stock {
	private HashMap<Chocolat, Double> stock;
	private HashMap<Chocolat, Indicateur> indicateurs;
	private HashMap<Chocolat, Double> stockDebutStep;

	/** @author Estelle BONNET */
	public Stock() {
		this.stock = new HashMap<Chocolat, Double>();
		this.indicateurs = new HashMap<Chocolat, Indicateur>();
		this.stockDebutStep = new HashMap<Chocolat, Double>();
	}

	/**
	 * Si produit figure deja dans le stock en vente, actualise la quantite du produit a stock.
	 * Sinon, ajoute qu'il y a une quantite stock de produit dans le stock en vente
	 * Si l'indicateur existe deja, on actualise sa valeur.
	 * Sinon, on ajoute le nouvel indicateure
	 * @param produit
	 * @param quantite mise en vente du produit
	 * @param acteur
	 */

	/** @authors Erine DUPONT & Estelle BONNET 
	 * V2 @author Estelle Bonnet
	 */
	public void ajouter(Chocolat produit, Double quantite, IActeur acteur) {
		if (quantite<0.0) {
			throw new IllegalArgumentException("Appel de ajouter(produit, quantite) de Stock avec quantite<0.0 (=="+quantite+")");
		} else {
			this.stock.put(produit, this.get(produit) + quantite);
			if (this.getIndicateur(produit)!=null) {
				this.getIndicateur(produit).ajouter(acteur, quantite);;
			}
			else {
				this.indicateurs.put(produit, new Indicateur("EQ5 stock"+" "+produit.name(), acteur, quantite));
				Monde.LE_MONDE.ajouterIndicateur(this.getIndicateur(produit));
			}			
		}
	}
	
	/** @autho Estelle BONNET 
	 */
	public void initialisationDebutStep(HashMap<Chocolat, Double> stock) {
		this.stockDebutStep=stock;
	}
	
	/** @autho Estelle BONNET 
	 */
	public void perenniteStock(HashMap<Chocolat, Double> stock, IActeur acteur) {
		List<Chocolat> produit = this.getProduitsEnVente();
		double exces = 0 ;
		for (Chocolat chocolat:produit) {
			exces = 0;
			exces = stock.get(chocolat) - this.stockDebutStep.get(chocolat);
			if (exces>0) {
				this.enlever(chocolat, exces*0.1, acteur);
			}
		}
	}
	
	/** @authors Erine DUPONT & Estelle BONNET 
	 * V2 @author Estelle Bonnet
	 */
	public void enlever(Chocolat produit, Double quantite, IActeur acteur) {
		if (quantite<0.0) {
			throw new IllegalArgumentException("Appel de enlever(produit, quantite) de Stock "
					+ "avec quantite<0.0 (=="+quantite+")");
		} else if (this.get(produit) < quantite) {
			throw new IllegalArgumentException("Appel de enlever(produit, quantite) de Stock "
					+ "avec stock (=="+ this.get(produit) + ") < quantite (==" + quantite + ")");
		} else {
			this.stock.put(produit, this.get(produit)- quantite);
			this.getIndicateur(produit).retirer(acteur, quantite);
		}
	}
	

	/** @author Estelle BONNET */
	public List<Chocolat> getProduitsEnVente() {
		ArrayList<Chocolat> produits=new ArrayList<Chocolat>();
		produits.addAll(this.stock.keySet());
		return produits;
	}
	
	/** @author Estelle BONNET */
	public HashMap<Chocolat,Double> getStock() {
		return this.stock;
	}
	
	/** @author Estelle BONNET */
	public HashMap<Chocolat,Double> getStockDebutStep() {
		return this.stockDebutStep;
	}

	/** @author Estelle BONNET */
	public Double get(Chocolat produit) {
		return (this.stock.containsKey(produit)? this.stock.get(produit) : 0.0) ;
	}
	
	/** @author Estelle BONNET */
	public Double getStockTotal() {
		double stocktotal =0 ;
		for (Chocolat produit : this.getProduitsEnVente()) {
			stocktotal += (this.stock.containsKey(produit)? this.stock.get(produit) : 0.0);
		}
		return stocktotal;
	}
	
	/** @author Estelle BONNET */
	public Indicateur getIndicateur(Chocolat produit) {
		if (this.indicateurs.containsKey(produit)) {
			return this.indicateurs.get(produit);
		}
		return null;
	}

	/** @author Estelle BONNET */
	public String toString() {
		return this.stock.toString();
	}
	
	/** @author Estelle BONNET */
	public String toHtml() {
		String res = "";
		for (Chocolat produit : this.stock.keySet()) {
			res+=produit+":"+String.format("%.3f",this.stock.get(produit))+"<br>";
		}
		return res;
	}

	/*
	public static void main(String[] args) {
		StockEnVente<Chocolat> s = new StockEnVente<Chocolat>();
		s.ajouter(Chocolat.MG_E_SHP, 120.0);
		s.ajouter(Chocolat.MG_NE_SHP, 200.0);
		System.out.println(s);
		s.ajouter(Chocolat.MG_E_SHP, 300.0);
		System.out.println(s);
	}
	*/
}