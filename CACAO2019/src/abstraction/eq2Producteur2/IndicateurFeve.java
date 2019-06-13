package abstraction.eq2Producteur2;

import java.util.HashMap;

import abstraction.fourni.IActeur;
import abstraction.fourni.Indicateur;

public class IndicateurFeve extends HashMap<String,Indicateur>{
	
	public IndicateurFeve(IActeur acteur) {
		this.put("Stock",new Indicateur("Stock",acteur,0));
		this.put("PrixVente", new Indicateur("PrixVente",acteur,0));
		this.put("ProductionParStep", new Indicateur("ProductionParStep",acteur,0));
	}
	
	
	public double getStock(){	return this.get("Stock").getValeur();	}
	public double getPrixVente() {	return this.get("PrixVente").getValeur();	}
	public double getProductionParStep() {	return this.get("ProductionParStep").getValeur();	}
	public Indicateur getStockIndicateur(){		return this.get("Stock");	}
	public Indicateur getPrixIndicateur() {		return this.get("PrixVente");	}
	public Indicateur getProductionIndicateur() {		return this.get("ProductionParStep");	}
	
	public void setStockIndicateur(Indicateur stockIndicateur) {	this.put("Stock", stockIndicateur);	}
	public void setPrixIndicateur(Indicateur prixIndicateur) {	this.put("prixVente", prixIndicateur);	}
	public void setproductionIndicateur(Indicateur productionIndicateur) {	this.put("ProductionParStep", productionIndicateur);	}
	public void setStock(IActeur acteur, double stock) {	this.getStockIndicateur().setValeur(acteur, stock);	}
	public void setPrix(IActeur acteur, double prix) {	this.getPrixIndicateur().setValeur(acteur, prix);	}
	public void setProduction(IActeur acteur, double production) {	this.getProductionIndicateur().setValeur(acteur, production);	}
}
