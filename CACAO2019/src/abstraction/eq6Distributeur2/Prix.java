package abstraction.eq6Distributeur2;


import java.util.ArrayList;
import java.util.HashMap;

import abstraction.eq7Romu.produits.Chocolat;
import abstraction.eq7Romu.produits.Gamme;
import abstraction.fourni.Indicateur;
import abstraction.fourni.Monde;

public class Prix {
	// Nordin
    private HashMap<Chocolat,Double> prixachatParProduit;
    private HashMap<Chocolat,Double> margeParProduit;
    private double massesalariale = 100000; //à harmoniser avec le sscond distributeur
    private double coutstructure = 10000 ; //à harmoniser avec le sscond distributeur
    private double tva = 1.2 ;
    private Distributeur2 nous;

    private Indicateur prixMG_E_SHP;
    private Indicateur prixMG_NE_SHP;
    private Indicateur prixMG_NE_HP;;
    private Indicateur prixHG_E_SHP;
    

	public Prix(Distributeur2 distributeur) {
		
		nous = distributeur; 
		this.prixMG_E_SHP = new Indicateur("EQ6 " + Chocolat.MG_E_SHP.toString(), nous, 20);
        Monde.LE_MONDE.ajouterIndicateur(this.prixMG_E_SHP);
        this.prixMG_NE_SHP = new Indicateur("EQ6 " + Chocolat.MG_NE_SHP.toString(), nous, 50);
        Monde.LE_MONDE.ajouterIndicateur(this.prixMG_NE_SHP);
        this.prixMG_NE_HP = new Indicateur("EQ6 "+ Chocolat.MG_NE_HP.toString(), nous, 30);
        Monde.LE_MONDE.ajouterIndicateur(this.prixMG_NE_HP);
        this.prixHG_E_SHP = new Indicateur("EQ6 " + Chocolat.HG_E_SHP.toString(), nous, 50);
        Monde.LE_MONDE.ajouterIndicateur(this.prixHG_E_SHP);
        
        this.margeParProduit = new HashMap<Chocolat, Double>();
        this.margeParProduit.put(Chocolat.HG_E_SHP, 1.25);
        this.margeParProduit.put(Chocolat.MG_E_SHP, 1.25);
        this.margeParProduit.put(Chocolat.MG_NE_SHP,1.25);
        this.margeParProduit.put(Chocolat.MG_NE_HP, 1.25);

        
        this.prixachatParProduit =  new HashMap<Chocolat,Double>();
        this.prixachatParProduit.put(Chocolat.HG_E_SHP, this.getPrixHG_E_SHP().getValeur());
        this.prixachatParProduit.put(Chocolat.MG_E_SHP, this.getPrixMG_E_SHP().getValeur());
        this.prixachatParProduit.put(Chocolat.MG_NE_SHP,this.getPrixMG_NE_SHP().getValeur());
        this.prixachatParProduit.put(Chocolat.MG_NE_HP, this.getPrixMG_NE_HP().getValeur());
	}
	
    public Indicateur getIndicateurPrix (Chocolat c) {

        if (c.getGamme()==Gamme.MOYENNE && (c.isEquitable()) && (c.isSansHuileDePalme())) {
                return this.prixMG_E_SHP;
        }
        if (c.getGamme()==Gamme.MOYENNE && !(c.isEquitable()) && (c.isSansHuileDePalme())) {
                return this.prixMG_NE_SHP;
        }
        if (c.getGamme()==Gamme.MOYENNE && !(c.isEquitable()) && !(c.isSansHuileDePalme())){ 
                return this.prixMG_NE_HP;
        }
        if (c.getGamme()==Gamme.HAUTE && (c.isEquitable()) && (c.isSansHuileDePalme())){
                return this.prixHG_E_SHP;
        }
        else {
                return null;
        	}

    }

    public void setMargeParProduit(Chocolat c, double marge) {
    	 if  (!this.margeParProduit.containsKey(c)) {
             return ;
    	 }
     this.margeParProduit.put(c, marge);
    }
    
    
    public void setPrixachatParProduit(Chocolat c, double prix) {
   	 if  (!this.prixachatParProduit.containsKey(c)) {
            return ;
   	 }
    this.prixachatParProduit.put(c, prix);
   }
    
    
    
    public double getMargeParProduit(Chocolat c) {
        if  (!this.margeParProduit.containsKey(c)) {
                return Double.NaN;
        }
        return (this.margeParProduit.containsKey(c)? this.margeParProduit.get(c) : 0.0);
    }
    public double getPrixachatParProduit(Chocolat c) {
        if  (!this.prixachatParProduit.containsKey(c)) {
                return Double.NaN;
        }
        return this.prixachatParProduit.get(c);
}

    
    public double cout (Chocolat choco) { // cout de structure a pendre en compte lors de la vente d'un produit
    	int i = Monde.LE_MONDE.getStep()%24 - 1;
    	int t = Monde.LE_MONDE.getStep();
    	/*double qv=0;
    	double c = 0.25;
    	if (i != 0) {
    		for (int j = 24*i; j <24*i+24 ; j++) {
    			qv = qv + nous.getIndicateurStock(choco).getHistorique().get(nous.getIndicateurStock(choco).getHistorique().getTaille() -t+j).getValeur();
    			}
    		c = (coutstructure+massesalariale)*.25/qv;

    	}
    	else { c = 0.25; } */
    	
    	return 1.25;
    }
	
    public void ajustementMarge(ArrayList<Double> historique, Chocolat c ) {
        // je récupère le chocoalt et l'historique des variations 
        int n = historique.size();
        int t = nous.getIndicateurStock(c).getHistorique().getTaille();
        if (n>3 && t>3) {
        		double stockprecedent = nous.getIndicateurStock(c).getHistorique().get(t-1).getValeur();
        		double stock_2 = nous.getIndicateurStock(c).getHistorique().get(t-2).getValeur();
        		double stock_3=nous.getIndicateurStock(c).getHistorique().get(t-3).getValeur();
        		if (stockprecedent != 0 && stock_2!=0 && stock_3!=0) {
        			if ( historique.get(n-1)/stockprecedent < 0.1 
                            && historique.get(n-2)/stock_2 < 0.1 
                            &&historique.get(n-3)/stock_3 < 0.1 ) {
                    double nouvellemarge = this.getMargeParProduit(c)*0.95;
                    setMargeParProduit(c, nouvellemarge);
            }
        			if ( historique.get(n-1)/stockprecedent > 0.9 
                            && historique.get(n-2)/stock_2 > 0.9 
                            &&historique.get(n-3)/stock_3 > 0.9 ) {
                    double nouvellemarge = this.getMargeParProduit(c)*1.05;
                    setMargeParProduit(c, nouvellemarge);
        		}		
        }
        }

}
    public double getPrixParProduit(Chocolat c) {
    	return getIndicateurPrix(c).getValeur();
    }
    public void setPrixParProduit(Chocolat c ) {
    	double prix = getPrixachatParProduit(c)*getMargeParProduit(c)*this.cout(c)*tva;
    	this.getIndicateurPrix(c).setValeur(nous, prix);
    }
    
        
    
	public Indicateur getPrixMG_E_SHP() {
		return prixMG_E_SHP;
	}

	public Indicateur getPrixMG_NE_SHP() {
		return prixMG_NE_SHP;
	}

	public Indicateur getPrixMG_NE_HP() {
		return prixMG_NE_HP;
	}

	public Indicateur getPrixHG_E_SHP() {
		return prixHG_E_SHP;
	}
    
   
}
