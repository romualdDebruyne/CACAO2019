package abstraction.eq1Producteur1;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.Map.Entry;

import abstraction.eq1Producteur1.ventesCacaoAleatoires.SuperviseurVentesCacaoAleatoires;
import abstraction.eq7Romu.produits.Feve;
import abstraction.eq7Romu.produits.Variete;
import abstraction.eq7Romu.ventesContratCadre.ContratCadre;
import abstraction.fourni.IActeur;
import abstraction.fourni.Indicateur;
import abstraction.fourni.Journal;
import abstraction.fourni.Monde;
import abstraction.eq7Romu.ventesContratCadre.Echeancier;
import static abstraction.fourni.Monde.*;


public class Producteur1Interne implements IActeur /* , IVendeurCacaoAleatoire */ {

	public static int COUT_FIXE = 1000;
	public static int COUT_VARIABLE_STOCK = 5;
	
	protected Indicateur stockFeves;
	protected Stock stockCriollo=new Stock(Feve.CRIOLLO_HG_EQ,this);
	protected Stock stockForastero=new Stock(Feve.FORASTERO_MG_NEQ,this);
	protected Stock stockTrinitario=new Stock(Feve.TRINITARIO_MG_NEQ,this);
	protected double recolteCriollo ;
	protected double recolteForastero ;
	protected double recolteTrinitario ;
    protected List<ContratCadre<Feve>> contratEnCours;  //
    protected List<Double>historiqueSoldeBancaire;

	//BEGIN ANTI 
	protected Indicateur plantationCriolloI;
	protected Indicateur plantationForasteroI;
	protected Indicateur plantationTrinitarioI;
	protected HashMap<Integer, Integer> plantationCriollo;
	protected HashMap<Integer, Integer> plantationForastero;
	protected HashMap<Integer, Integer> plantationTrinitario; 
	protected int compteurSteps = 0 ;
	public static int dureeDeVieCacaoyer = 960 ; 
	protected int criolloPlante ; 
	protected int forasteroPlante ; 
	protected int trinitarioPlante ;
	public static int unAnEnSteps = 24 ; 
	public static int deuxAnsEnSteps = 48 ;
	public static int troisAnsEnSteps = 72 ; 
	public static int quatreAnsEnSteps = 96 ;
	public static int cinqAnsEnSteps = 120 ;
	public static int dureeDeVieFeve = unAnEnSteps; // durée de vie en nexts
//END ANTI

	protected int compteurRecolte = 0;
	protected int stepRecolteExceptionnellementReduite;


	protected Indicateur soldeBancaire;
	// BEGIN ANTI
	// private StockEnVente<Feve> stockEnVente;
	// END ANTI
	// BEGIN Manon
	protected Journal journal1;
	// END MANON
	// BEGIN Pauline
	protected HashMap<Feve, Double> prixAuKilo;
	// END Pauline
	// BEGIN ANTI
	protected HashMap<Integer, ContratCadre<Feve>> historiqueContrats=new HashMap<Integer, ContratCadre<Feve>>();
	// END ANTI

	public Producteur1Interne() {
		this.stockFeves = new Indicateur("EQ1 stock feves", this, 3000);  
		this.contratEnCours= new ArrayList<ContratCadre<Feve>> ();
		this.historiqueSoldeBancaire= new ArrayList<Double> ();
		Random r=new Random();
		stepRecolteExceptionnellementReduite=r.nextInt(unAnEnSteps);
		//BEGIN ANTI 
		this.plantationCriolloI = new Indicateur("EQ1 plantation criollo", this, 80);
		this.plantationForasteroI = new Indicateur("EQ1 plantation forastero", this, 80);
		this.plantationTrinitarioI = new Indicateur("EQ1 plantation trinitario", this, 80);
		this.plantationCriollo = new HashMap<Integer, Integer>();
		this.plantationForastero = new HashMap<Integer, Integer>(); 
		this.plantationTrinitario = new HashMap<Integer, Integer>(); 
		
		
		for (int next = 0; next < dureeDeVieCacaoyer - 1; next++) {
			if ( next%unAnEnSteps == 0 ) {
				plantationCriollo.put(next, 2);
				plantationForastero.put(next, 2);
				plantationTrinitario.put(next, 2);
			} else {
				plantationCriollo.put(next, 0);
				plantationForastero.put(next, 0);
				plantationTrinitario.put(next, 0);
			}
		}
		//END ANTI 
		this.soldeBancaire = new Indicateur("EQ1 solde bancaire", this, 100000);
		Monde.LE_MONDE.ajouterIndicateur(this.stockFeves);
		Monde.LE_MONDE.ajouterIndicateur(this.getStockI(Feve.CRIOLLO_HG_EQ));
		Monde.LE_MONDE.ajouterIndicateur(this.getStockI(Feve.FORASTERO_MG_NEQ));
		Monde.LE_MONDE.ajouterIndicateur(this.getStockI(Feve.TRINITARIO_MG_NEQ));
		Monde.LE_MONDE.ajouterIndicateur(this.soldeBancaire);
		Monde.LE_MONDE.ajouterIndicateur(this.plantationCriolloI);
		Monde.LE_MONDE.ajouterIndicateur(this.plantationForasteroI);
		Monde.LE_MONDE.ajouterIndicateur(this.plantationTrinitarioI);
		//Monde.LE_MONDE.ajouterActeur(new SuperviseurVentesCacaoAleatoires());
		// BEGIN Manon
		this.journal1 = new Journal("JEQ1");
		Monde.LE_MONDE.ajouterJournal(this.journal1);
		System.out.println(" ajout du journal...");
		// END Manon

	}
	// BEGIN Anti
	public HashMap<Integer, ContratCadre<Feve>> getHistoriqueContrats() {
		return this.historiqueContrats;
	}
	// END Anti

	// BEGIN Nas
	public double getRecolte(Feve feve) {
		
		
		if (feve.getVariete() == Variete.CRIOLLO) {
			return stepRecolteExceptionnellementReduite==LE_MONDE.getStep()%unAnEnSteps ? recolteCriollo*Math.random() :recolteCriollo;
		} else if (feve.getVariete() == Variete.FORASTERO) {
			return stepRecolteExceptionnellementReduite==LE_MONDE.getStep()%unAnEnSteps ? recolteForastero*Math.random() :recolteForastero;
		} else if (feve.getVariete() == Variete.TRINITARIO) {
			return stepRecolteExceptionnellementReduite==LE_MONDE.getStep()%unAnEnSteps ? recolteTrinitario*Math.random() :recolteTrinitario;
		}
		
		return Double.NaN;
	}

	
	public void genereAlea() {
		if (compteurRecolte<unAnEnSteps) {
			compteurRecolte++;
		} else {
			compteurRecolte=0;
			Random r=new Random();
			stepRecolteExceptionnellementReduite=r.nextInt(unAnEnSteps);
		}
	}
	
	// END Nas

	public Indicateur getSoldeBancaire() {
		return this.soldeBancaire;

	}

	public String getNom() {
		return "EQ1";
	}

	public void initialiser() {
	}

	public void next() {
		// BEGIN Nas
		updateStock();
		this.soldeBancaire.retirer(this, COUT_FIXE + COUT_VARIABLE_STOCK * stockFeves.getValeur());
		// END Nas
		//BEGIN ANTI 
		updatePlantation();
		//END ANTI
		//BEGINMANON
		this.historiqueSoldeBancaire.add(this.getSoldeBancaire().getValeur());
		for(Feve feve:this.getFeve()) {
			this.journal1.ajouter("Prix de Vente"+ this.getPrixAuKilo().get(feve));}

	}

	public HashMap<Feve, Double> getPrixAuKilo() {
		// BEGIN Pauline
		HashMap<Feve, Double> mapPrix = new HashMap<Feve, Double>();
		mapPrix.put(Feve.CRIOLLO_HG_EQ, 3.5);
		mapPrix.put(Feve.FORASTERO_MG_EQ, 2.5);
		mapPrix.put(Feve.FORASTERO_MG_NEQ, 2.0);
		mapPrix.put(Feve.TRINITARIO_MG_EQ, 2.2);
		mapPrix.put(Feve.TRINITARIO_MG_NEQ, 1.5);
		return mapPrix;
		// END Pauline
	}
	//BEGIN ANTI 

	public Double moyenneDemandeCriollo(){
		List<Double> moyenne = new ArrayList<Double>() ; 
		for (int i=0; i<5; i++) {
			moyenne.add(0.0);
		}
		Set<Entry<Integer, ContratCadre<Feve>>> setHisto= historiqueContrats.entrySet();
		Iterator<Entry<Integer, ContratCadre<Feve>>> it = setHisto.iterator();
		while(it.hasNext()) {
			Entry<Integer, ContratCadre<Feve>> e = it.next();
			if (e.getValue().getProduit().getVariete() == Variete.CRIOLLO) {
			List<Echeancier> echeanciers = e.getValue().getEcheanciers();
			
			for(int i=0; i<echeanciers.size(); i++) {
				Echeancier echeancier = echeanciers.get(i);
				int stepDebut = echeancier.getStepDebut();
				if (stepDebut <cinqAnsEnSteps) {
					int stepFin = echeancier.getStepFin();
					if (stepFin - stepDebut > cinqAnsEnSteps) {
						stepFin = cinqAnsEnSteps; }
					
					if (stepDebut < unAnEnSteps) {
						moyenne.set(0, moyenne.get(0)+echeancier.getQuantiteJusquA(unAnEnSteps));
						stepDebut = unAnEnSteps ; 
						
					}
					if (stepFin > unAnEnSteps && stepDebut < deuxAnsEnSteps ) {
						moyenne.set(1, moyenne.get(1)+echeancier.getQuantiteJusquA(deuxAnsEnSteps)- echeancier.getQuantiteJusquA(stepDebut));
						stepDebut = deuxAnsEnSteps ; 
						
					}
					if (stepFin > deuxAnsEnSteps && stepDebut < troisAnsEnSteps ) {
						moyenne.set(2, moyenne.get(2)+echeancier.getQuantiteJusquA(troisAnsEnSteps) - echeancier.getQuantiteJusquA(stepDebut));
						stepDebut = troisAnsEnSteps ;
					}
					if (stepFin > troisAnsEnSteps && stepDebut < quatreAnsEnSteps ) {
						moyenne.set(3, moyenne.get(3)+echeancier.getQuantiteJusquA(quatreAnsEnSteps) - echeancier.getQuantiteJusquA(stepDebut));
						stepDebut = quatreAnsEnSteps ; 
				}
					if (stepFin > quatreAnsEnSteps && stepDebut < cinqAnsEnSteps ) {
						moyenne.set(4, moyenne.get(4)+echeancier.getQuantiteJusquA(cinqAnsEnSteps) - echeancier.getQuantiteJusquA(stepDebut));
						stepDebut = cinqAnsEnSteps ; 
				}
				
				
			}
			
			
		}
		
	}
		}
		double total = 0 ; 
		for (Double qte : moyenne) {
			total += qte ;
		}
		return total / moyenne.size() ;}
	
	public Double moyenneDemandeForastero(){
		List<Double> moyenne = new ArrayList<Double>() ; 
		for (int i=0; i<5; i++) {
			moyenne.add(0.0);
		}
		Set<Entry<Integer, ContratCadre<Feve>>> setHisto= historiqueContrats.entrySet();
		Iterator<Entry<Integer, ContratCadre<Feve>>> it = setHisto.iterator();
		while(it.hasNext()) {
			Entry<Integer, ContratCadre<Feve>> e = it.next();
			if (e.getValue().getProduit().getVariete() == Variete.FORASTERO) {
			List<Echeancier> echeanciers = e.getValue().getEcheanciers();
			
			for(int i=0; i<echeanciers.size(); i++) {
				Echeancier echeancier = echeanciers.get(i);
				int stepDebut = echeancier.getStepDebut();
				if (stepDebut <cinqAnsEnSteps) {
					int stepFin = echeancier.getStepFin();
					if (stepFin - stepDebut > cinqAnsEnSteps) {
						stepFin = cinqAnsEnSteps; }
					
					if (stepDebut < unAnEnSteps) {
						moyenne.set(0, moyenne.get(0)+echeancier.getQuantiteJusquA(unAnEnSteps));
						stepDebut = unAnEnSteps ; 
						
					}
					if (stepFin > unAnEnSteps && stepDebut < deuxAnsEnSteps ) {
						moyenne.set(1, moyenne.get(1)+echeancier.getQuantiteJusquA(deuxAnsEnSteps)- echeancier.getQuantiteJusquA(stepDebut));
						stepDebut = deuxAnsEnSteps ; 
						
					}
					if (stepFin > deuxAnsEnSteps && stepDebut < troisAnsEnSteps ) {
						moyenne.set(2, moyenne.get(2)+echeancier.getQuantiteJusquA(troisAnsEnSteps) - echeancier.getQuantiteJusquA(stepDebut));
						stepDebut = troisAnsEnSteps ;
					}
					if (stepFin > troisAnsEnSteps && stepDebut < quatreAnsEnSteps ) {
						moyenne.set(3, moyenne.get(3)+echeancier.getQuantiteJusquA(quatreAnsEnSteps) - echeancier.getQuantiteJusquA(stepDebut));
						stepDebut = quatreAnsEnSteps ; 
				}
					if (stepFin > quatreAnsEnSteps && stepDebut < cinqAnsEnSteps ) {
						moyenne.set(4, moyenne.get(4)+echeancier.getQuantiteJusquA(cinqAnsEnSteps) - echeancier.getQuantiteJusquA(stepDebut));
						stepDebut = cinqAnsEnSteps ; 
				}
				
				
			}
			
			
		}
		
	}
		}
		double total = 0 ; 
		for (Double qte : moyenne) {
			total += qte ;
		}
		return total / moyenne.size() ;}
	
	public Double moyenneDemandeTrinitario(){
		List<Double> moyenne = new ArrayList<Double>() ; 
		for (int i=0; i<5; i++) {
			moyenne.add(0.0);
		}
		Set<Entry<Integer, ContratCadre<Feve>>> setHisto= historiqueContrats.entrySet();
		Iterator<Entry<Integer, ContratCadre<Feve>>> it = setHisto.iterator();
		while(it.hasNext()) {
			Entry<Integer, ContratCadre<Feve>> e = it.next();
			if (e.getValue().getProduit().getVariete() == Variete.TRINITARIO) {
			List<Echeancier> echeanciers = e.getValue().getEcheanciers();
			
			for(int i=0; i<echeanciers.size(); i++) {
				Echeancier echeancier = echeanciers.get(i);
				int stepDebut = echeancier.getStepDebut();
				if (stepDebut <cinqAnsEnSteps) {
					int stepFin = echeancier.getStepFin();
					if (stepFin - stepDebut > cinqAnsEnSteps) {
						stepFin = cinqAnsEnSteps; }
					
					if (stepDebut < unAnEnSteps) {
						moyenne.set(0, moyenne.get(0)+echeancier.getQuantiteJusquA(unAnEnSteps));
						stepDebut = unAnEnSteps ; 
						
					}
					if (stepFin > unAnEnSteps && stepDebut < deuxAnsEnSteps ) {
						moyenne.set(1, moyenne.get(1)+echeancier.getQuantiteJusquA(deuxAnsEnSteps)- echeancier.getQuantiteJusquA(stepDebut));
						stepDebut = deuxAnsEnSteps ; 
						
					}
					if (stepFin > deuxAnsEnSteps && stepDebut < troisAnsEnSteps ) {
						moyenne.set(2, moyenne.get(2)+echeancier.getQuantiteJusquA(troisAnsEnSteps) - echeancier.getQuantiteJusquA(stepDebut));
						stepDebut = troisAnsEnSteps ;
					}
					if (stepFin > troisAnsEnSteps && stepDebut < quatreAnsEnSteps ) {
						moyenne.set(3, moyenne.get(3)+echeancier.getQuantiteJusquA(quatreAnsEnSteps) - echeancier.getQuantiteJusquA(stepDebut));
						stepDebut = quatreAnsEnSteps ; 
				}
					if (stepFin > quatreAnsEnSteps && stepDebut < cinqAnsEnSteps ) {
						moyenne.set(4, moyenne.get(4)+echeancier.getQuantiteJusquA(cinqAnsEnSteps) - echeancier.getQuantiteJusquA(stepDebut));
						stepDebut = cinqAnsEnSteps ; 
				}
				
				
			}
			
			
		}
		
	}
		}
		double total = 0 ; 
		for (Double qte : moyenne) {
			total += qte ;
		}
		return total / moyenne.size() ;}
		
	//END ANTI
	//BEGIN ANTI
	

	
	public void updatePlantation() {
		
	
		HashMap<Integer, Integer> plantationCriolloOld = new HashMap<Integer,Integer>(plantationCriollo);
		HashMap<Integer, Integer> plantationForasteroOld = new HashMap<Integer, Integer>(plantationForastero);
		HashMap<Integer, Integer> plantationTrinitarioOld = new HashMap<Integer, Integer>(plantationTrinitario);
		
		for (int next = 0; next < dureeDeVieCacaoyer - 1; next++) {
			recolteCriollo = 0;
			recolteTrinitario = 0;
			recolteForastero = 0;
			plantationCriollo.put(next + 1 , plantationCriolloOld.get(next));
			plantationForastero.put(next + 1 , plantationForasteroOld.get(next));
			plantationTrinitario.put( next + 1 , plantationTrinitarioOld.get(next));
			if (next > troisAnsEnSteps - 1) {
			recolteCriollo += plantationCriollo.get(next);
			recolteForastero += plantationForastero.get(next);
			recolteTrinitario += plantationTrinitario.get(next);
			}
			
					
		}
		criolloPlante = (int) Math.ceil(moyenneDemandeCriollo()*1/40);
		forasteroPlante = (int) Math.ceil(moyenneDemandeForastero()*1/40);
		trinitarioPlante = (int) Math.ceil(moyenneDemandeTrinitario()*1/40);
		
		if (compteurSteps%unAnEnSteps == 0  ) {
			plantationCriollo.put(0, criolloPlante);
			plantationForastero.put(0, forasteroPlante);
			plantationTrinitario.put(0, trinitarioPlante);
			
		}
		plantationCriolloI.setValeur(this,  0);
		plantationForasteroI.setValeur(this,  0);
		plantationTrinitarioI.setValeur(this, 0);
		
		for (int next = 0 ; next < dureeDeVieCacaoyer; next++) {
			plantationCriolloI.ajouter(this, plantationCriollo.get(next));
			plantationForasteroI.ajouter(this, plantationForastero.get(next));
			plantationTrinitarioI.ajouter(this, plantationTrinitario.get(next));
			
		}
	}
	// END ANTI 

	// BEGIN Nas
	private void updateStock() {
		stockFeves.setValeur(this, 0);
		for (Feve feve:getFeve()) {
			getStock(feve).updateStock(LE_MONDE.getStep(),(int)getRecolte(feve));
			stockFeves.ajouter(this,getStockI(feve).getValeur());
		}
		
		genereAlea();		
	}
	
	protected Indicateur getStockI(Feve feve) {
		return getStock(feve).getInd();
		
	}
	
	protected Stock getStock(Feve feve){
		if (feve.getVariete()== Variete.CRIOLLO) {
	    	  
	    	  return stockCriollo;
	      }
	      if (feve.getVariete()== Variete.FORASTERO) {
	    	  
	    	  return stockForastero;
	      }
	      if (feve.getVariete()== Variete.TRINITARIO) {
	    	  
	    	  return stockTrinitario;
	      }
		return null;
	}
	// END Nass
	
	//Begin Manon
	public List<Feve> getFeve(){
		ArrayList<Feve> typeFeve=new ArrayList<Feve>();
		typeFeve.add(Feve.CRIOLLO_HG_EQ);
		typeFeve.add(Feve.FORASTERO_MG_NEQ);
		typeFeve.add(Feve.TRINITARIO_MG_NEQ);
		return typeFeve;
		
	}
	//END MANON
	public int getCOUT_FIXE() {
		return COUT_FIXE;
	}
	public void setCOUT_FIXE(int cOUT_FIXE) {
		COUT_FIXE = cOUT_FIXE;
	}
	public int getCOUT_VARIABLE_STOCK() {
		return COUT_VARIABLE_STOCK;
	}
	public  void setCOUT_VARIABLE_STOCK(int cOUT_VARIABLE_STOCK) {
		COUT_VARIABLE_STOCK = cOUT_VARIABLE_STOCK;
	}
	public Indicateur getStockFeves() {
		return stockFeves;
	}
	public void setStockFeves(Indicateur stockFeves) {
		this.stockFeves = stockFeves;
	}
	/*public Indicateur getStockCriolloI() {
		return stockCriolloI;
	}
	public void setStockCriolloI(Indicateur stockCriolloI) {
		this.stockCriolloI = stockCriolloI;
	}
	public Indicateur getStockForasteroI() {
		return stockForasteroI;
	}
	public void setStockForasteroI(Indicateur stockForasteroI) {
		this.stockForasteroI = stockForasteroI;
	}
	public Indicateur getStockTrinitarioI() {
		return stockTrinitarioI;
	}
	public void setStockTrinitarioI(Indicateur stockTrinitarioI) {
		this.stockTrinitarioI = stockTrinitarioI;
	}
	public HashMap<Integer, Double> getStockCriollo() {
		return stockCriollo;
	}
	public void setStockCriollo(HashMap<Integer, Double> stockCriollo) {
		this.stockCriollo = stockCriollo;
	}
	public HashMap<Integer, Double> getStockForastero() {
		return stockForastero;
	}
	public void setStockForastero(HashMap<Integer, Double> stockForastero) {
		this.stockForastero = stockForastero;
	}
	public HashMap<Integer, Double> getStockTrinitario() {
		return stockTrinitario;
	}
	public void setStockTrinitario(HashMap<Integer, Double> stockTrinitario) {
		this.stockTrinitario = stockTrinitario;
	}*/
	
	public List<ContratCadre<Feve>> getContratEnCours() {
		return contratEnCours;
	}
	public void setContratEnCours(List<ContratCadre<Feve>> contratEnCours) {
		this.contratEnCours = contratEnCours;
	}
	public List<Double> getHistoriqueSoldeBancaire() {
		return historiqueSoldeBancaire;
	}
	public void setHistoriqueSoldeBancaire(List<Double> historiqueSoldeBancaire) {
		this.historiqueSoldeBancaire = historiqueSoldeBancaire;
	}
	public Indicateur getPlantationCriolloI() {
		return plantationCriolloI;
	}
	public void setPlantationCriolloI(Indicateur plantationCriolloI) {
		this.plantationCriolloI = plantationCriolloI;
	}
	public Indicateur getPlantationForasteroI() {
		return plantationForasteroI;
	}
	public void setPlantationForasteroI(Indicateur plantationForasteroI) {
		this.plantationForasteroI = plantationForasteroI;
	}
	public Indicateur getPlantationTrinitarioI() {
		return plantationTrinitarioI;
	}
	public void setPlantationTrinitarioI(Indicateur plantationTrinitarioI) {
		this.plantationTrinitarioI = plantationTrinitarioI;
	}
	public HashMap<Integer, Integer> getPlantationCriollo() {
		return plantationCriollo;
	}
	public void setPlantationCriollo(HashMap<Integer, Integer> plantationCriollo) {
		this.plantationCriollo = plantationCriollo;
	}
	public HashMap<Integer, Integer> getPlantationForastero() {
		return plantationForastero;
	}
	public void setPlantationForastero(HashMap<Integer, Integer> plantationForastero) {
		this.plantationForastero = plantationForastero;
	}
	public HashMap<Integer, Integer> getPlantationTrinitario() {
		return plantationTrinitario;
	}
	public void setPlantationTrinitario(HashMap<Integer, Integer> plantationTrinitario) {
		this.plantationTrinitario = plantationTrinitario;
	}
	public int getCompteurSteps() {
		return compteurSteps;
	}
	public void setCompteurSteps(int compteurSteps) {
		this.compteurSteps = compteurSteps;
	}
	public static int getDureeDeVieCacaoyer() {
		return dureeDeVieCacaoyer;
	}
	public static void setDureeDeVieCacaoyer(int dureeDeVieCacaoyer) {
		Producteur1Interne.dureeDeVieCacaoyer = dureeDeVieCacaoyer;
	}
	public int getCriolloPlante() {
		return criolloPlante;
	}
	public void setCriolloPlante(int criolloPlante) {
		this.criolloPlante = criolloPlante;
	}
	public int getForasteroPlante() {
		return forasteroPlante;
	}
	public void setForasteroPlante(int forasteroPlante) {
		this.forasteroPlante = forasteroPlante;
	}
	public int getTrinitarioPlante() {
		return trinitarioPlante;
	}
	public void setTrinitarioPlante(int trinitarioPlante) {
		this.trinitarioPlante = trinitarioPlante;
	}
	public static int getUnAnEnSteps() {
		return unAnEnSteps;
	}
	public static void setUnAnEnSteps(int unAnEnSteps) {
		Producteur1Interne.unAnEnSteps = unAnEnSteps;
	}
	public static int getDeuxAnsEnSteps() {
		return deuxAnsEnSteps;
	}
	public static void setDeuxAnsEnSteps(int deuxAnsEnSteps) {
		Producteur1Interne.deuxAnsEnSteps = deuxAnsEnSteps;
	}
	public static int getTroisAnsEnSteps() {
		return troisAnsEnSteps;
	}
	public static void setTroisAnsEnSteps(int troisAnsEnSteps) {
		Producteur1Interne.troisAnsEnSteps = troisAnsEnSteps;
	}
	public static int getQuatreAnsEnSteps() {
		return quatreAnsEnSteps;
	}
	public static void setQuatreAnsEnSteps(int quatreAnsEnSteps) {
		Producteur1Interne.quatreAnsEnSteps = quatreAnsEnSteps;
	}
	public static int getCinqAnsEnSteps() {
		return cinqAnsEnSteps;
	}
	public static void setCinqAnsEnSteps(int cinqAnsEnSteps) {
		Producteur1Interne.cinqAnsEnSteps = cinqAnsEnSteps;
	}
	public static int getDureeDeVieFeve() {
		return dureeDeVieFeve;
	}
	public static void setDureeDeVieFeve(int dureeDeVieFeve) {
		Producteur1Interne.dureeDeVieFeve = dureeDeVieFeve;
	}
	public int getcompteurRecolte() {
		return compteurRecolte;
	}
	public void setcompteurRecolte(int compteurRecolte) {
		this.compteurRecolte = compteurRecolte;
	}
	/*public int getAlea() {
		return alea;
	}
	public void setAlea(int alea) {
		this.alea = alea;
	}*/
	public Journal getJournal1() {
		return journal1;
	}
	public void setJournal1(Journal journal1) {
		this.journal1 = journal1;
	}
	public void setSoldeBancaire(Indicateur soldeBancaire) {
		this.soldeBancaire = soldeBancaire;
	}
	public void setPrixAuKilo(HashMap<Feve, Double> prixAuKilo) {
		this.prixAuKilo = prixAuKilo;
	}
	public void setHistoriqueContrats(HashMap<Integer, ContratCadre<Feve>> historiqueContrats) {
		this.historiqueContrats = historiqueContrats;
	}
	
	
	

}
