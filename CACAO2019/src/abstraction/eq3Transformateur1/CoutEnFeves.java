package abstraction.eq3Transformateur1;

import java.util.ArrayList;
import java.util.HashMap;

import abstraction.eq7Romu.produits.Chocolat;
import abstraction.eq7Romu.produits.Feve;
/** 
 * 
 * @author eve
 *
 */
public class CoutEnFeves {
	
	private HashMap<Chocolat, HashMap<Feve, Double>> cout;
	
	public CoutEnFeves(ArrayList<Chocolat> chocolats, ArrayList<Feve> feves) {
		this.cout = new HashMap<Chocolat, HashMap<Feve, Double>>();
		for (Chocolat c: chocolats) { 
			this.cout.put(c, new HashMap<Feve, Double>());
			for(Feve f:feves) {
				this.cout.get(c).put(f, 0.);
			}	
		}
	}
	
	// -----------------------------------------------------------
	//          GETTERS
	// -----------------------------------------------------------
	
	public double getCoutEnFeves(Chocolat chocolat, Feve feve) {
		if(!this.cout.containsKey(chocolat)) {
			return 0.;
		}else if(!this.cout.get(chocolat).containsKey(feve)){
			return 0.;
		}else {
			return this.cout.get(chocolat).get(feve);
		}
	}

	// -----------------------------------------------------------
	//          SETTERS
	// -----------------------------------------------------------
	
	public void setCoutEnFeves(Chocolat chocolat, Feve feve, double cout) {
		if(!this.cout.containsKey(chocolat)) {
			this.cout.put(chocolat, new HashMap<Feve, Double>());
		}
		if (cout >= 0.) {
			this.cout.get(chocolat).put(feve, cout);
		} else {
			this.cout.get(chocolat).put(feve, 0.);
		}
	}
}
