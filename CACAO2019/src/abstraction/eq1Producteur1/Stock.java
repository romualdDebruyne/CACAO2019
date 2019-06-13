package abstraction.eq1Producteur1;

import java.util.HashMap;

import abstraction.eq7Romu.produits.Feve;
import abstraction.fourni.IActeur;
import abstraction.fourni.Indicateur;
import static abstraction.eq1Producteur1.Producteur1Interne.*;

public class Stock{
	private Indicateur ind;
	private HashMap<Integer, Double> stock;
	private IActeur act;
	private int nextBorneInf=-(40-1)*unAnEnSteps;
	
	
	public Stock(Feve feve,IActeur act,int stockDepart) {
		this.act=act;
		ind=new Indicateur("EQ1 stock "+feve.getVariete(), act, stockDepart);
		stock=new HashMap<Integer, Double>();
		for (int an=0;an<40;an++) {
			stock.put(-an*unAnEnSteps, (double)1000/40);
		}
		
	}
	
	public Stock(Feve feve,IActeur act) {
		this(feve,act,1000);
		
	}
	
	public void depot(int next,double quantite) {
		stock.put(next, quantite);
		ind.ajouter(act, quantite);
	}
	
	public double retrait(int nextCourant,double quantite) {
		double quantiteAEnlever=quantite;
		int nextAExplorer=nextBorneInf;
		while (quantiteAEnlever>0 && nextAExplorer <=nextCourant) {
			if (stock.getOrDefault(nextAExplorer,(double)0)<quantiteAEnlever) {
				if (stock.get(nextAExplorer)!=null) {
					quantiteAEnlever=quantiteAEnlever-stock.get(nextAExplorer);
					stock.put(nextAExplorer,(double) 0);
				}
				nextBorneInf=nextAExplorer;
			} else {
			
				stock.put(nextAExplorer, stock.get(nextAExplorer)-quantiteAEnlever);
				quantiteAEnlever=0;
			}
			nextAExplorer++;
			
		}
		double quantiteRetire=quantite-quantiteAEnlever;
		ind.retirer(act, quantiteRetire);
		return quantiteRetire;
		
	}
	
	public void retraitPerime(int nextCourant) {
		int nextPerime=nextCourant-dureeDeVieFeve;
		if (stock.get(nextPerime)!=null) {
			double stockPerime=stock.get(nextPerime);
			stock.put(nextPerime, (double)0);
			ind.retirer(act, stockPerime);
		} 
	}
	
	public void updateStock(int nextCourant,double recolte) {
		depot(nextCourant,recolte);
		retraitPerime(nextCourant);
	}
	
	public Indicateur getInd() {
		return ind;
	}

	public HashMap<Integer, Double> getStock() {
		return stock;
	}

	public static void main(String[] args) {
		System.out.println("EQ1 stock "+Feve.TRINITARIO_MG_NEQ.getVariete());
		System.out.println(new Integer(-1));
		HashMap<Integer, Integer> toast = new HashMap<Integer, Integer>();
		System.out.println(toast.get(0));
	}

}
