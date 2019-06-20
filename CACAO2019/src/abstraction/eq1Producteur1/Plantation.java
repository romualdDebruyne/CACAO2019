package abstraction.eq1Producteur1;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.Map.Entry;

import abstraction.eq7Romu.produits.Feve;
import abstraction.eq7Romu.produits.Variete;
import abstraction.eq7Romu.ventesContratCadre.ContratCadre;
import abstraction.eq7Romu.ventesContratCadre.Echeancier;
import abstraction.fourni.IActeur;
import abstraction.fourni.Indicateur;
import static abstraction.eq1Producteur1.Producteur1Interne.*;


//BEGIN Nas et Anti
public class Plantation {
	private Indicateur ind;
	private HashMap<Integer, Double> plantation;//clé=step de stockage, objet=quantité
	private IActeur act;
	private int stepBorneInf=-(40-1)*unAnEnSteps;
	protected HashMap<Integer, ContratCadre<Feve>> historiqueContrats;
	private Feve feve;
	
	public Plantation(Feve feve,IActeur act,int plantationDepart) {
		this.act=act;
		this.feve=feve;
		ind=new Indicateur("EQ1 plantation "+feve.getVariete(), act, plantationDepart);
		plantation=new HashMap<Integer, Double>();
		historiqueContrats=((Producteur1Interne) act).getHistoriqueContrats();
		//System.out.println("init a "+historiqueContrats);
		for (int an=0;an<40;an++) {
			plantation.put(-an*unAnEnSteps, (double)plantationDepart/40);//plantation initial avant le step de départ
		}	
	}
	
	public Plantation(Feve feve,IActeur act) {
		this(feve,act,1000);
		
	}
	
	
	
	public Indicateur getInd() {
		return ind;
	}

	public HashMap<Integer, Double> getPlantation() {
		return plantation;
	}

	public IActeur getAct() {
		return act;
	}

	public int getStepBorneInf() {
		return stepBorneInf;
	}

	public void planter(int step,double quantite){
		getPlantation().put(step, quantite); //maj de la plantation
		getInd().ajouter(getAct(), quantite); //maj de l'indicateur
	}
	
	public void retraitArbresAges(int stepCourant) {
		int stepArbresAges=stepCourant-dureeDeVieCacaoyer;
		if (getPlantation().get(stepArbresAges)!=null) {
			double ArbresAges=getPlantation().get(stepArbresAges);
			getPlantation().put(stepArbresAges, (double)0);
			getInd().retirer(getAct(), ArbresAges);
		} 
	}

	public double getRecolte(int stepCourant){

		int stepAExplorer=getStepBorneInf();
		double recolte=0;
		while (stepAExplorer <=stepCourant-troisAnsEnSteps) { 
			recolte += getPlantation().getOrDefault(stepAExplorer, (double)0);
			stepAExplorer++;
			
		}
		return recolte;
	}
	

	
	
	public Feve getFeve() {
		return feve;
	}

	public HashMap<Integer, ContratCadre<Feve>> getHistoriqueContrats() {
		return this.historiqueContrats;
	}



	
	public Double moyenneDemande(){

		Double moyenne = 0.0;
		//System.out.println(getHistoriqueContrats());//ROMU
		Set<Entry<Integer, ContratCadre<Feve>>> setHisto= getHistoriqueContrats().entrySet();
		Iterator<Entry<Integer, ContratCadre<Feve>>> it = setHisto.iterator();
		while(it.hasNext()) {
			Entry<Integer, ContratCadre<Feve>> e = it.next();
			if (e.getValue().getProduit().getVariete() == getFeve().getVariete()) {
			List<Echeancier> echeanciers = e.getValue().getEcheanciers();
			
			for(int i=0; i<echeanciers.size(); i++) {
				Echeancier echeancier = echeanciers.get(i);
				int stepDebut = echeancier.getStepDebut();
				if (stepDebut <cinqAnsEnSteps) {
					int stepFin = echeancier.getStepFin();
					if (stepFin > cinqAnsEnSteps) {
						stepFin = cinqAnsEnSteps;
					}
					moyenne += echeancier.getQuantiteJusquA(stepFin);

		}
		
	}}
			 }
		return moyenne/5 ;}
	

	
	
	
	public void updatePlantation(int stepCourant,double plantation) {
		planter(stepCourant,plantation); 
		retraitArbresAges(stepCourant);
	}
	
	
}
//END Nas et Anti
