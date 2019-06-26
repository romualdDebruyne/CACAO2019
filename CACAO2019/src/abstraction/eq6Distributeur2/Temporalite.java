package abstraction.eq6Distributeur2;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import abstraction.eq7Romu.produits.Chocolat;
import abstraction.eq7Romu.produits.Gamme;
import abstraction.fourni.Monde;



public class Temporalite {
	// Nordin
    private Distributeur2 nous;
    private List<Integer> noel ;
    private List<Integer> paques ;
    private List<Integer> saintValentin ;
    private HashMap<Integer,Double> quantiteVendueMG_NE_SHP; // Nous donne pour un mois donnée la quantité maximale vendue 
    private HashMap<Integer,Double> quantiteVendueMG_NE_HP; 
    private HashMap<Integer,Double> quantiteVendueMG_E_SHP; 
    private HashMap<Integer,Double> quantiteVendueHG_E_SHP; 
    
    
    
	public Temporalite(Distributeur2 dist) {
		nous =dist;
		noel= new ArrayList<Integer>();
		paques= new ArrayList<Integer>();
		saintValentin= new ArrayList<Integer>();
		noel.add(11);
		noel.add(12);
		saintValentin.add(2);
		paques.add(3);
		paques.add(4);
		quantiteVendueHG_E_SHP = new HashMap<>();
		quantiteVendueMG_E_SHP = new HashMap<>();
		quantiteVendueMG_NE_HP = new HashMap<>();
		quantiteVendueMG_NE_SHP = new HashMap<>();
		
		for(int i = 1; i <= 24; i++){
			quantiteVendueHG_E_SHP.put(i, 0.0);
			quantiteVendueMG_E_SHP.put(i, 0.0);
			quantiteVendueMG_NE_HP.put(i, 0.0);
			quantiteVendueMG_NE_SHP.put(i, 0.0);
		}
		
		}
	
	
	public void setQuantiteVendue(Chocolat c, int step, double q) {
			if (c.getGamme()==Gamme.MOYENNE && (c.isEquitable()) && (c.isSansHuileDePalme())) {
				 if (quantiteVendueMG_E_SHP.get(getPeriode(step))<q){
					quantiteVendueMG_E_SHP.put(getPeriode(step), q);
				}
				}
			if (c.getGamme()==Gamme.MOYENNE && !(c.isEquitable()) && (c.isSansHuileDePalme())) {
				 if (quantiteVendueMG_NE_SHP.get(getPeriode(step))<q) {
					quantiteVendueMG_NE_SHP.put(getPeriode(step), q);
				}
				}
			if (c.getGamme()==Gamme.MOYENNE && !(c.isEquitable()) && !(c.isSansHuileDePalme())){ 
				 if (quantiteVendueMG_NE_HP.get(getPeriode(step))<q){
					quantiteVendueMG_NE_HP.put(getPeriode(step), q);
				}
				}
			if (c.getGamme()==Gamme.HAUTE && (c.isEquitable()) && (c.isSansHuileDePalme())){
				 if (quantiteVendueHG_E_SHP.get(getPeriode(step))<q) {
					quantiteVendueHG_E_SHP.put(getPeriode(step), q);
				}
				}
		
	}



	
	public int getPeriode (int step) {
		 int modulostep = step%24;
		 if (modulostep ==0) {
			 return 24;
		 }
		 return modulostep; 
		}
	
	public int getMois (int step) {
		 int modulostep = getPeriode(step); 
		 double moisActuel_ = modulostep/2;
		 int mois= (int)moisActuel_;
		 if (mois != moisActuel_) {
			 mois = mois +1;
		 }
		 return mois; 
		}
	
	public double getFacteur (int step, Chocolat c) {

		double demandetotalechorsperiode = 0;
		double demandetotalecnoel = 0;
		double demandetotalecstvalen = 0;
		double demandetotalecpaques = 0;

		int stepschorsperiode = 0;
		int stepscnoel = 0;
		int stepscstvalen = 0;
		int stepscpaques = 0;

		ArrayList<Double> historique = nous.getHistoriqueVente(c);

		if (Monde.LE_MONDE.getStep() >= 72) {
			for (int k=0 ; k<historique.size(); k++) {
				if (getPeriode (step) ==11 || getPeriode (step) ==12) {
					demandetotalecnoel+=historique.get(k);
					stepscnoel+=1;
				}
				if (getPeriode (step) ==2) {
					demandetotalecstvalen+=historique.get(k);
					stepscstvalen+=1;
				}
				if (getPeriode (step) ==3 || getPeriode (step) ==4) {
					demandetotalecpaques+=historique.get(k);
					stepscpaques+=1;
				}
				else {
					demandetotalechorsperiode+=historique.get(k);
					stepschorsperiode+=1;
				}
			}

			//Moyenne sur les steps non noel, paques ou st valentin 
			double moyennehorsperiode = demandetotalechorsperiode/stepschorsperiode;

			//Moyenne sur noel et facteur de différence
			double moyennenoel = demandetotalecnoel/stepscnoel;
			double facteurnoel = moyennenoel/moyennehorsperiode;

			//Moyenne sur paques
			double moyennestval = demandetotalecstvalen/stepscstvalen;
			double facteurstval = moyennestval/moyennehorsperiode;

			//Moyenne sur st valentin 
			double moyennepaques = demandetotalecpaques/stepscpaques;
			double facteurpaques = moyennepaques/moyennehorsperiode;

			if (getPeriode (step) ==11 || getPeriode (step) ==12) {
				return facteurnoel;
			}
			if (getPeriode (step) ==2) {
				return facteurstval;
			}
			if (getPeriode (step) ==3 || getPeriode (step) ==4) {
				return facteurpaques;
			}
			else {
				return 1;
			}

		}
		else {
			if (getPeriode (step) ==11 || getPeriode (step) ==12) {
				return 4;
			}
			if (getPeriode (step) ==2) {
				return 3;
			}
			if (getPeriode (step) ==3 || getPeriode (step) ==4) {
				return 5;
			}
			else {
				return 1;
			}}
	}
	
	// si on est dans la première elle renvoie 0 sinon elle renvoie le meilleur mois de l'année précedente 
	public int moisInteressant(Chocolat c ) {
		if (getPeriode(Monde.LE_MONDE.getStep()) == 24 ) {
		// Méthode qui renvoie le mois de l'année la plus intéressante basée sur les ventes précédente 
		double qmax = 0.0;
		double q =0.0;
		int meilleurmois = 0;
		int moisactuel=	0;
		if (c.getGamme()==Gamme.MOYENNE && (c.isEquitable()) && (c.isSansHuileDePalme())) {
			for (int i = 2; i <= 24; i++) {
				if (getMois(i)== moisactuel) {
					 q=quantiteVendueMG_E_SHP.get(i)+quantiteVendueMG_E_SHP.get(i-1);
					
				}
				else {q=0;}
            	if (q > qmax) {
            		qmax=q;
            		meilleurmois = getMois(i);
            		
            	}
            	moisactuel = getMois(i);
            }
		}
		if (c.getGamme()==Gamme.MOYENNE && !(c.isEquitable()) && (c.isSansHuileDePalme())) {
			for (int i = 2; i <= 24; i++) {
				if (getMois(i)== moisactuel) {
					 q=quantiteVendueMG_NE_SHP.get(i)+quantiteVendueMG_NE_SHP.get(i-1);
				}
				else {q=0;}
            	if (q > qmax) {
            		qmax=q;
            		meilleurmois = getMois(i);
            		
            	}
            	moisactuel = getMois(i);
            }
		}
		if (c.getGamme()==Gamme.MOYENNE && !(c.isEquitable()) && !(c.isSansHuileDePalme())){ 
			for (int i = 2; i <= 24; i++) {
				if (getMois(i)== moisactuel) {
					 q=quantiteVendueMG_NE_HP.get(i)+quantiteVendueMG_NE_HP.get(i-1);
				}
				else {q=0;}
            	if (q > qmax) {
            		qmax=q;
            		meilleurmois = getMois(i);
            		
            	}
            	moisactuel = getMois(i);
            }
		}
		if (c.getGamme()==Gamme.HAUTE && (c.isEquitable()) && (c.isSansHuileDePalme())){
			for (int i = 2; i <= 24; i++) {
				if (getMois(i)== moisactuel) {
					 q=quantiteVendueHG_E_SHP.get(i)+quantiteVendueHG_E_SHP.get(i-1);
				}
				else {q=0;}
            	if (q > qmax) {
            		qmax=q;
            		meilleurmois = getMois(i);
            		
            	}
            	moisactuel = getMois(i);
            }
		}
		return meilleurmois;
		}
		else {
			return 0;
		}
	}
	

}
