package abstraction.eq6Distributeur2;



import java.util.ArrayList;
import java.util.List;

import abstraction.eq7Romu.distributionChocolat.IDistributeurChocolat;
import abstraction.eq7Romu.produits.Chocolat;
import abstraction.eq7Romu.ventesContratCadre.StockEnVente;
import abstraction.fourni.IActeur;
import abstraction.fourni.Journal;
import abstraction.fourni.Monde;
import java.util.HashMap;

//Nazim

public class Client1 implements IActeur{

	private static int NB_CLIENT = 0;
//Le numéro permet d'identitifer le client . Les 3 indicateurs de préférences( 0<p<1) permettent de jauger quelle pondération apporter aux différents critères prix/quantité/qualité
//lors du calcul ensuite de la note du chocolat selon le distributeur .  Le client veut acheter un produit unique de chocolat qu'on notera unique.
// Il est nécessaire de connaître la quantitémoyenne dans l'année que veut le client afin de vérifier quels distributeurs ont des stocks suffisants pour satisfaire ses besoins.
// Nous parlons de quantitemoyenne car durant l'année en fonction de la période ( noel / paques .. )  la quantité voulue sera le produit de la quantitémoyenne par un facteur de temporalité.
// Enfin le journal permet de garder un historique pour les clients de ces échanges avec les distributeurs ce qui vient introduire un facteur de fidélité : un client aura tendance 
// à chosir à la step n+1 un distributeur chez qui il a l'habitude d'acheter même si le chocolat d'un concurrent est un peu mieux noté.
	
	private int numero;
	private double preferenceprix;
	private double preferencequantite;
	private double quantitemoyenne ;
	private List<IDistributeurChocolat> Distributeurs;
	private Chocolat unique;
	private Journal journal ;
	private HashMap<IDistributeurChocolat, Double> hist;
	private ArrayList<Double> temporalite ;
	
	
	public Client1(double preferenceprix, double preferencequantite , double quantitemoyenne , Chocolat unique, ArrayList<Double> temporalite ) {
		NB_CLIENT++;
		this.numero = NB_CLIENT;
		this.preferenceprix = preferenceprix;
		this.preferencequantite = preferencequantite ;
		this.quantitemoyenne = quantitemoyenne;
		this.unique = unique ;
		this.temporalite = temporalite ;
		this.hist= new HashMap< IDistributeurChocolat, Double>();
		this.Distributeurs = new ArrayList<IDistributeurChocolat>();
		this.journal = new Journal("Journal "+this.getNom());
		Monde.LE_MONDE.ajouterJournal(this.journal);
		
		
	}
	
	public ArrayList<Double> getTemporalite (){
		return temporalite;
	}
	
	public HashMap<IDistributeurChocolat, Double> getHist() {
		return hist;
	}


	public void setHist(IDistributeurChocolat D, Double oc) {
	
	this.hist.put(D, oc);
		
	}

	public String getNom() {
		return "CL"+this.numero;
	
	}

	
	public int getNumero() {
		return numero;
	}


	public void setNumero(int numero) {
		this.numero = numero;
	}


	public List<IDistributeurChocolat> getDistributeurs() {
		return Distributeurs;
	}


	public void setDistributeurs(List<IDistributeurChocolat> distributeurs) {
		Distributeurs = distributeurs;
	}


	public Chocolat getUnique() {
		return unique;
	}


	public void setUnique(Chocolat unique) {
		this.unique = unique;
	}


	public Journal getJournal() {
		return journal;
	}


	public void setJournal(Journal journal) {
		this.journal = journal;
	}



	
//Implémentation de la liste de distributeurs comportant le produit désiré par le client en stock 
//avec une quantité supérieure ou égale à la quantité recherché par le client qui vaut quantitemoyenne*temporalité.get(step)
	
	public void setDistributeurs() {
	for (IActeur D : Monde.LE_MONDE.getActeurs()) {
		if (D instanceof IDistributeurChocolat ) {
			IDistributeurChocolat dist = (IDistributeurChocolat)D;
			StockEnVente<Chocolat> s = dist.getStockEnVente();
			if (s.getProduitsEnVente().contains(this.unique) == true && (s.get(unique) > 0) ) {
				Distributeurs.add((IDistributeurChocolat)D);
			}
		}
	}
	}
	
	
	
	 
    // Liste des notes sur 10 du chocolat désiré par le client selon le distributeur. Ces notes sont basées sur les préférences du client en terme de prix/quantité
	
	public List<Double> Offres (){
	List<Double> K  = new ArrayList<Double>();
	this.setDistributeurs(); 
	double pmax =0.0;
	double qmax=0.0;
	
	for ( IDistributeurChocolat D: Distributeurs) {
		if (D.getPrix(unique)> pmax) {
			pmax=D.getPrix(unique);
		}
		if (D.getStockEnVente().get(unique)> qmax) {
			qmax=D.getStockEnVente().get(unique);
		}
	} 
		
	for ( IDistributeurChocolat H: Distributeurs) {
		Double N = ((H.getPrix(unique)) * preferenceprix*10/pmax  +   (H.getStockEnVente().get(unique)   *preferencequantite )*10/qmax)	/ 2;
		K.add(N);
		
		
	}
	return K ;
	}	
	
	public IDistributeurChocolat choix (List<Double> Notes) {
		ArrayList<Double> Notesfinales = new ArrayList();
		for (IDistributeurChocolat V: Distributeurs) {
			if (this.hist.containsKey(V)) {
				double facteurfidelite= getHist().get(V);
				Notesfinales.add(Notes.get(Distributeurs.indexOf(V))* facteurfidelite);
			}	
			else {
				Notesfinales.add(Notes.get(Distributeurs.indexOf(V)));
			}
		}
		
		Double notemax= Notesfinales.get(0);
		for (Double x: Notesfinales) {
			if (x> notemax) {
				notemax = x  ;
				
			}
		}

		return Distributeurs.get(Notesfinales.indexOf(notemax));
		
		
	}
	
	//Evaluation du  distributeur à choisir à un step donné : on multiplie les elements de la  liste de notes par un facteur de fidélité calculé grâce aux occurences des distributeurs
	// dans le journal.
	//On obtient alors une liste de notes finales et on cherche la note maximale : le distributeur correspondant au chocolat atteignant ce maximum sera choisi par le client

	
	public void initialiser() {
	}


	// Amelioration  de la V1 à la V2:  prise en compte de la fidélité d'achat , de la temporalité  influant sur la quantité demandée par le client et enfin l'achat de 
	// quantitépartielles chez les distributeurs dans l'ordre décroissant de leur note relative au client et à son chocolat jusqu'à obtention de la quantité voulue de manière à 
	// être plus réaliste.
		
	
	public void next() {
		
		IDistributeurChocolat D = null ;
		int step = Monde.LE_MONDE.getStep();	
		this.setDistributeurs();
		System.out.println (Distributeurs);
		System.out.println(Distributeurs);
		List<Double> Notes = this.Offres();
		double quantitevoulue = quantitemoyenne * temporalite.get(step);
		
		double quantiteachetee=0.0 ;
		double quantiterestante = quantitevoulue-quantiteachetee ;	
		
		
		while ( quantiteachetee != quantitevoulue && Distributeurs.size()!= 0) {
			
			IDistributeurChocolat T = choix ( Notes);
			if (hist.containsKey(T)) {
				hist.put( T , hist.get(T)+0.02);
			}
			else {
				hist.put( T , 1.02);	
			}
			this.journal.ajouter(""+step);
			this.journal.ajouter("Distributeur "+ T.toString());
			this.journal.ajouter("Client " +numero);
	
			
				
			if ( T.getStockEnVente().get(this.unique) >= quantiterestante  ) {
				T.vendre(this.unique, quantiterestante);
				this.journal.ajouter(""+quantiterestante); 
				hist.put( T , hist.get(T)+0.02);
			}
			
			else {
				T.vendre(this.unique, T.getStockEnVente().get(this.unique));
				
				System.out.println (Distributeurs);
				System.out.println ( "le distributeur est "+T);
				System.out.println  ( " l'indice est"+Distributeurs.indexOf(T));
				Notes.remove(Distributeurs.indexOf(T));
				Distributeurs.remove(T);
				this.journal.ajouter(""+T.getStockEnVente().get(this.unique));	
			}	
			

		}		
				
			}
			
		
		}
		
		


	