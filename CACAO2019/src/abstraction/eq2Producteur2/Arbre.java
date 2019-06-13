package abstraction.eq2Producteur2;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import abstraction.eq7Romu.produits.Feve;

public class Arbre {
	private HashMap<Feve, List<Integer >> nbArbres;
	private HashMap<Feve, Integer> nbtot;
	
	
	
	public Arbre() {
		nbArbres = new  HashMap<Feve, List<Integer >>();
		nbtot = new  HashMap<Feve, Integer >();
	}
	
	public void initialise() {
		 this.nbArbres.put(Feve.FORASTERO_MG_NEQ, new ArrayList<Integer>());
		 this.nbArbres.put(Feve.FORASTERO_MG_EQ, new ArrayList<Integer>());
		 this.nbArbres.put(Feve.MERCEDES_MG_EQ, new ArrayList<Integer>());
		 this.nbArbres.put(Feve.MERCEDES_MG_NEQ, new ArrayList<Integer>());
		 
		 this.nbtot.put(Feve.FORASTERO_MG_NEQ, 0);
		 this.nbtot.put(Feve.FORASTERO_MG_EQ, 0);
		 this.nbtot.put(Feve.MERCEDES_MG_EQ, 0);
		 this.nbtot.put(Feve.MERCEDES_MG_NEQ, 0);
		 
		 for (Feve f : this.nbArbres.keySet()) {
			 for(int i=0;i<37;i++) {
				 this.nbArbres.get(f).add(18225);
				 int t=this.nbtot.get(f)+18225;
				 this.nbtot.replace(f, t);
	
			 }
			 for(int i=0;i<3;i++) {
				 this.nbArbres.get(f).add(18225);}
			
		 }
	}
	
	
	public void actualise(HashMap<Feve, Integer > nouveaux) {
		 for (Feve f : this.nbArbres.keySet()) {
			 int arbreMort=this.nbArbres.get(f).remove(0);
			 int nouveauTot=this.nbtot.get(f)-arbreMort+this.nbArbres.get(f).get(36);
			 this.nbtot.replace(f, nouveauTot);
			 this.nbArbres.get(f).add(nouveaux.get(f));
		 }
		
	}
	
	public void actualise() {		
		 for (Feve f : this.nbArbres.keySet()) {
			 int arbreMort=this.nbArbres.get(f).remove(0);
			 int nouveauTot=this.nbtot.get(f)-arbreMort+this.nbArbres.get(f).get(36);
			 this.nbtot.replace(f, nouveauTot);
			 this.nbArbres.get(f).add(18225);
		 }
		
	}
	
	public Integer getNbArbres(Feve feve) {
		return this.nbtot.get(feve);
	}
	
}
