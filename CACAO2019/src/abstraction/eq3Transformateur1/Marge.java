package abstraction.eq3Transformateur1;

import java.util.ArrayList;
import java.util.HashMap;
import abstraction.eq7Romu.produits.Chocolat;
/** 
 * 
 * @author Raph
 *
 */
public class Marge {

private HashMap<Chocolat, Double> coutProd;
private HashMap<Chocolat, Double> margeBrute;
	
	public Marge(ArrayList<Chocolat> produits) {
		this.coutProd = new HashMap<Chocolat, Double>();
		this.margeBrute = new HashMap<Chocolat, Double>();
		for (Chocolat p: produits) { 
			this.coutProd.put(p, 0.);
			this.margeBrute.put(p, 0.);
		}
	}
	public Marge() {
		this(new ArrayList<Chocolat> ());
	}
	
	// -----------------------------------------------------------
	//          GETTERS & SETTERS
	// -----------------------------------------------------------

	public double getCoutProd(Chocolat produit) {
		if(coutProd.containsKey(produit)) {
			return this.coutProd.get(produit);
		}else {
			return 0.;
		}
	}
	public double getMargeBrute(Chocolat produit) {
		if(coutProd.containsKey(produit)) {
			return this.margeBrute.get(produit);
		}else {
			return 0.;
		}
	}
	public void setCoutProd(Chocolat produit, double cout) {
		if (cout >= 0.) {
			this.coutProd.put(produit, cout);
		} else {
			this.coutProd.put(produit, 0.);
		}
	}
	public void setMargeBrute(Chocolat produit, double marge) {
		if (marge >= 0.) {
			this.margeBrute.put(produit, marge);
		} else {
			this.margeBrute.put(produit, 0.);
		}
	}
}