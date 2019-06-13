package abstraction.eq2Producteur2;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import abstraction.eq7Romu.produits.Feve;
import abstraction.fourni.IActeur;

public class GestionnaireFeve extends HashMap<Feve, IndicateurFeve>{
	
	public GestionnaireFeve(IActeur acteur) {
		this.put(Feve.FORASTERO_MG_NEQ, new IndicateurFeve(acteur));
		this.put(Feve.FORASTERO_MG_EQ, new IndicateurFeve(acteur));
		this.put(Feve.MERCEDES_MG_EQ, new IndicateurFeve(acteur));
		this.put(Feve.MERCEDES_MG_NEQ, new IndicateurFeve(acteur));
	}	
	

	
	public double getStock(Feve feve){	return this.get(feve).getStock();	}
	public double getPrixVente(Feve feve) {	return this.get(feve).getPrixVente();	}
	public double getProductionParStep(Feve feve) {	return this.get(feve).getProductionParStep();	}
	

	public void setStock(IActeur acteur,Feve feve, double stock) {	this.get(feve).setStock(acteur, stock);	}
	public void setPrix(IActeur acteur,Feve feve, double prix) { this.get(feve).setPrix(acteur, prix);	}
	public void setProduction(IActeur acteur,Feve feve, double production) {	this.get(feve).setProduction(acteur, production);	}
	
	public List<Feve> getFeves(){
		List<Feve> list=new LinkedList<Feve>();
		list.add(Feve.FORASTERO_MG_EQ);
		list.add(Feve.FORASTERO_MG_NEQ);
		list.add(Feve.MERCEDES_MG_EQ);
		list.add(Feve.MERCEDES_MG_NEQ);
		return list;
	}
}
