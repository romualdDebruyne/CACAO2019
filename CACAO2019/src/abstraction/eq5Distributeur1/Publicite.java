package abstraction.eq5Distributeur1;

import abstraction.eq7Romu.produits.Chocolat;

public class Publicite {
	private double budget;
	private Chocolat produit;
	private String impact;
	
	/**
	 * @author Erine DUPONT
	 */
	public Publicite (Chocolat produit, double budget, String impact) throws IllegalArgumentException {
		this.produit = produit;
		this.impact = impact;
		if (budget < 0) {
			throw new IllegalArgumentException("Le budget est négatif: " + budget);
		} else {
			this.budget = budget;
		}
	}
	
	public Chocolat getProduit() {
		return this.produit;
	}
	
	public double getBudget() {
		return this.budget;
	}
	
	public String getImpact() {
		return this.impact;
	}
	
	public void setBudget(double b) {
		this.budget = b;
	}
	
	public void setImpact(String i) {
		this.impact = i;
	}
	
	public String toString() {
		return "Produit : " + this.getProduit() + " - Budget : " + this.getBudget() + " € - Impact : " + this.getImpact();
	}
}
