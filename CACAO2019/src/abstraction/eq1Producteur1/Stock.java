package abstraction.eq1Producteur1;

import java.util.HashMap;

import abstraction.eq7Romu.produits.Feve;
import abstraction.fourni.IActeur;
import abstraction.fourni.Indicateur;
import static abstraction.eq1Producteur1.Producteur1Interne.*;
//BEGIN Nas
public class Stock{
	private Indicateur ind;
	private HashMap<Integer, Double> stock; //clé=step de stockage, objet=quantité
	private IActeur act;
	private int stepBorneInf=-(40-1)*unAnEnSteps; //borne inf à partir de laquelle on explore (si rien n'a été stocké pour un step<stepCourant, rien n'y sera plus jamais planté)
	
	
	public Stock(Feve feve,IActeur act,int stockDepart) {
		this.act=act;
		ind=new Indicateur("EQ1 stock "+feve.getVariete(), act, stockDepart);
		stock=new HashMap<Integer, Double>();
		for (int an=0;an<40;an++) {
			stock.put(-an*unAnEnSteps, (double)stockDepart/40);//stock initial avant le step de départ
		}
		
	}
	
	public Stock(Feve feve,IActeur act) {
		this(feve,act,1000);
		
	}
	
	public void depot(int step,double quantite) {
		getStock().put(step, quantite); //maj du stock
		getInd().ajouter(getAct(), quantite); //maj de l'indicateur
	}
	
	public double retrait(int stepCourant,double quantite) {
		double quantiteAEnlever=quantite;
		int stepAExplorer=getStepBorneInf();
		while (quantiteAEnlever>0 && stepAExplorer <=stepCourant) { //maj du stock en retirant les feves les plus agées d'abord
			if (getStock().getOrDefault(stepAExplorer,(double)0)<quantiteAEnlever) {
				if (getStock().get(stepAExplorer)!=null) {
					quantiteAEnlever=quantiteAEnlever-getStock().get(stepAExplorer);
					getStock().put(stepAExplorer,(double) 0);
				}
				setStepBorneInf(stepAExplorer); //maj de la borne inf d'exploration seulement si toutes les feves du step exploré sont consommées
			} else {// cas où le stock du step exploré peut fournir toute la quantité à enlever 
			
				getStock().put(stepAExplorer, getStock().get(stepAExplorer)-quantiteAEnlever);
				quantiteAEnlever=0;
			}
			stepAExplorer++;
			
		}
		double quantiteRetire=quantite-quantiteAEnlever;
		getInd().retirer(getAct(), quantiteRetire); //maj de l'indicateur
		return quantiteRetire;
		
	}
	
	public void retraitPerime(int stepCourant) {
		int stepPerime=stepCourant-dureeDeVieFeve;
		if (getStock().get(stepPerime)!=null) {
			double stockPerime=getStock().get(stepPerime);
			getStock().put(stepPerime, (double)0);
			getInd().retirer(getAct(), stockPerime);
		} 
	}
	
	public void updateStock(int stepCourant,double recolte) { //méthode appelée à chaque next
		depot(stepCourant,recolte); 
		retraitPerime(stepCourant);
	}
	
	public Indicateur getInd() {
		return ind;
	}

	public HashMap<Integer, Double> getStock() {
		return stock;
	}

	public int getStepBorneInf() {
		return stepBorneInf;
	}
	//Begin Pauline
	public double getStock(int step) {
		return stock.get(step);
	}
	//End Pauline
	

	public IActeur getAct() {
		return act;
	}

	public void setStepBorneInf(int stepBorneInf) { 
		this.stepBorneInf = stepBorneInf;
	}

	public static void main(String[] args) {
		System.out.println("EQ1 stock "+Feve.TRINITARIO_MG_NEQ.getVariete());
		System.out.println(new Integer(-1));
		HashMap<Integer, Integer> toast = new HashMap<Integer, Integer>();
		System.out.println(toast.get(0));
	}

}
//END Nas
