package abstraction.eq2Producteur2;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


// import org.graalvm.compiler.core.amd64.AMD64ArithmeticLIRGenerator.Maths;

import abstraction.eq7Romu.produits.Feve;

public class Arbre {

	private static int NB_PROD = 2;
	
	public static int surface_F_NEQ = 18225;   
	public static int surface_F_EQ = 2025;
	public static int surface_M_NEQ = 675;
	public static int surface_M_EQ = 75;

	private HashMap<Feve, List<Integer>> nbArbres;
	private HashMap<Feve, Integer> nbtot;
	private GestionnaireFeve gestFeves;


	public Arbre() {
		nbArbres = new HashMap<Feve, List<Integer>>();
		nbtot = new HashMap<Feve, Integer>();
		
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

		for (int i = 0; i < 37; i++) {

			this.nbArbres.get(Feve.FORASTERO_MG_EQ).add(this.surface_F_EQ);
			this.nbArbres.get(Feve.FORASTERO_MG_NEQ).add(this.surface_F_NEQ);   
			this.nbArbres.get(Feve.MERCEDES_MG_EQ).add(this.surface_M_EQ);
			this.nbArbres.get(Feve.MERCEDES_MG_NEQ).add(this.surface_M_NEQ);

			int surface_F_EQ = this.nbtot.get(Feve.FORASTERO_MG_EQ) + this.surface_F_EQ;
			int surface_F_NEQ = this.nbtot.get(Feve.FORASTERO_MG_NEQ) + this.surface_F_NEQ;
			int surface_M_EQ = this.nbtot.get(Feve.MERCEDES_MG_EQ) + this.surface_M_EQ;
			int surface_M_NEQ = this.nbtot.get(Feve.MERCEDES_MG_NEQ) + this.surface_M_NEQ;

			this.nbtot.put(Feve.FORASTERO_MG_EQ, surface_F_EQ);// replace(f, t);
			this.nbtot.put(Feve.FORASTERO_MG_NEQ, surface_F_NEQ);// replace(f, t);
			this.nbtot.put(Feve.MERCEDES_MG_EQ, surface_M_EQ);// replace(f, t);
			this.nbtot.put(Feve.MERCEDES_MG_NEQ, surface_M_NEQ);// replace(f, t);

		}
		for (int i = 0; i < 3; i++) {
			this.nbArbres.get(Feve.FORASTERO_MG_EQ).add(this.surface_F_EQ);
			this.nbArbres.get(Feve.FORASTERO_MG_NEQ).add(this.surface_F_NEQ);
			this.nbArbres.get(Feve.MERCEDES_MG_EQ).add(this.surface_M_EQ);
			this.nbArbres.get(Feve.MERCEDES_MG_NEQ).add(this.surface_M_NEQ);			
		}
	}

	public void actualise(HashMap<Feve, Integer> nouveaux) {
		for (Feve f : this.nbArbres.keySet()) {
			int arbreMort = this.nbArbres.get(f).remove(0);
			int nouveauTot = this.nbtot.get(f) - arbreMort + this.nbArbres.get(f).get(36);
			this.nbtot.put(f, nouveauTot); // replace(f, nouveauTot)
			this.nbArbres.get(f).add(nouveaux.get(f));
		}
	}

	public void actualise() {
		int evolDebut = 0;
		//for (Feve f : this.nbArbres.keySet()) {
		//	int arbreMort = this.nbArbres.get(f).remove(0);
		//	int nouveauTot = this.nbtot.get(f) - arbreMort + this.nbArbres.get(f).get(36);
		//	this.nbtot.put(f, nouveauTot);
		//	evolDebut += (gestFeves.get(f).get("Stock").getHistorique().get(an).getValeur() - gestFeves.get(f).get("Stock").getHistorique().get(0).getValeur())/gestFeves.get(f).get("Stock").getHistorique().get(0).getValeur();
		//}

		//int qteArbreAn = 21000;
		
		//if (evolDebut > 0.3) {
		//	qteArbreAn = (int)(qteArbreAn - 0.2 * qteArbreAn) ;	
	
		//}if(evolDebut <0) {
		//	qteArbreAn = (int)(qteArbreAn + 0.2*qteArbreAn);
		//}else {
		//	qteArbreAn =(int)(qteArbreAn);
		//}
		
		//int a = (int)(qteArbreAn * 0.002);
		//int b = (int)(qteArbreAn * 0.008);
		//int c = (int)(qteArbreAn * 0.90);
		//int d = (int)(qteArbreAn * 0.09); 
		
		//this.nbArbres.get(Feve.FORASTERO_MG_EQ).add(c);
		//this.nbArbres.get(Feve.FORASTERO_MG_NEQ).add(d);
		//this.nbArbres.get(Feve.MERCEDES_MG_EQ).add(a);
		//this.nbArbres.get(Feve.MERCEDES_MG_NEQ).add(b);
		

		this.nbArbres.get(Feve.FORASTERO_MG_EQ).add(this.surface_F_EQ);
		this.nbArbres.get(Feve.FORASTERO_MG_NEQ).add(this.surface_F_NEQ);
		this.nbArbres.get(Feve.MERCEDES_MG_EQ).add(this.surface_M_EQ);
		this.nbArbres.get(Feve.MERCEDES_MG_NEQ).add(this.surface_M_NEQ);
	}
	
	public Integer getNbArbres(Feve feve) {
		return this.nbtot.get(feve);
	}


	public double getPrixParStep(Feve f) {

		double surfaceTotale = this.nbtot.get(f);
		return surfaceTotale * 32; // en dollar

	}
}
