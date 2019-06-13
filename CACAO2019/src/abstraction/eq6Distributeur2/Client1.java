package abstraction.eq6Distributeur2;


import java.util.ArrayList;
import java.util.List;

import abstraction.eq7Romu.distributionChocolat.IDistributeurChocolat;
import abstraction.eq7Romu.produits.Chocolat;
import abstraction.eq7Romu.produits.Gamme;
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
	private double preferencequalite;
	private double preferencequantite;
	private double quantitemoyenne ;
	private List<IDistributeurChocolat> Distributeurs;
	private Chocolat unique;
	private Journal journal ;
	private ArrayList<Double> temporalite ;
	
	
	public Client1(double preferenceprix, double preferencequantite, double preferencequalite , double quantitemoyenne , Chocolat unique) {
		NB_CLIENT++;
		this.numero = NB_CLIENT;
		this.preferenceprix = preferenceprix;
		this.preferencequantite = preferencequantite ;
		this.preferencequalite= preferencequalite;
		this.quantitemoyenne = quantitemoyenne;
		this.unique = unique ;
		this.Distributeurs = new ArrayList<IDistributeurChocolat>();
		this.journal = new Journal("Journal "+this.getNom());
		Monde.LE_MONDE.ajouterJournal(this.journal);
		
		ArrayList<Double> temporalite = new ArrayList<Double>();
		int i = 0 ;
		while (i<24) {
			if (i==0 || i==4 || i==23||i==24) {
				temporalite.add(1.5);
			}
			else {
				temporalite.add(1.0);
			}
			
			i+=1;
			
		}
	}
	
	
	
	//Nazim
		public double getQualite(Chocolat c) {
			int N = 0;
			if (c.isEquitable()) {
				N = N+1 ;
			}
			if (c.isSansHuileDePalme()) {
				N = N+1 ;
			}
			if (c.getGamme() == Gamme.HAUTE ) {
				N= N+2 ;
			} else if (c.getGamme() == Gamme.MOYENNE) {
				N = N+1 ;
			}
			return 10*N/4;
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


	public ArrayList<Double> evaluation_produit ( IDistributeurChocolat D, Chocolat c) {
		ArrayList<Double> L = new ArrayList<Double>(); 
		L.add(D.getPrix(c));
		L.add(D.getStockEnVente().get(c));
		L.add(getQualite(c));
		return L;
		
	}
	

	
//Implémentation de la liste de distributeurs comportant le produit désiré par le client en stock 
//avec une quantité supérieure ou égale à la quantité recherché par le client qui vaut quantitemoyenne*temporalité.get(step)
//*	
	public void setDistributeurs(int step) {
		
	for (IActeur D : Monde.LE_MONDE.getActeurs()) {
		if (D instanceof IDistributeurChocolat ) {
			IDistributeurChocolat dist = (IDistributeurChocolat)D;
			StockEnVente<Chocolat> s = dist.getStockEnVente();
			if (s.getProduitsEnVente().contains(this.unique) & s.get(unique)>= quantitemoyenne* temporalite.get(step) );
				Distributeurs.add((IDistributeurChocolat)D);
			}
		}
	}
	
	//*/
	
	 
    // Liste des notes sur 10 du chocolat désiré par le client selon le distributeur. Ces notes sont basées sur les préférences du client en terme de prix/quantité/qualité
//*	
	public List<Double> Offres (int step){
	List<Double> K  = new ArrayList<Double>();
	this.setDistributeurs(step); 
	Double pmax= 0.0 ;
	Double qmax= 0.0;
	
	for ( IDistributeurChocolat D: Distributeurs) {
		if (evaluation_produit (D,unique).get(0)> pmax) {
			pmax=evaluation_produit (D,unique).get(0);
		}
		if (evaluation_produit (D,unique).get(1)> qmax) {
			qmax=evaluation_produit (D,unique).get(1);
		}
	}
		
	for ( IDistributeurChocolat H: Distributeurs) {
		Double N = ((10 - evaluation_produit (H,unique).get(0)*10/pmax) * preferenceprix + (evaluation_produit (H,unique).get(1)* 10/qmax)*preferencequantite 
		+ (evaluation_produit (H,unique).get(2))*preferencequalite)/3;
		
		K.add(N);
		
	}
	
	return K ;
	
	}	
	
	
	//*/
	//Evaluation du  distributeur à choisir à un step donné : on multiplie les elements de la  liste de notes par un facteur de fidélité calculé grâce aux occurences des distributeurs
	// dans le journal.
	//On obtient alors une liste de notes finales et on cherche la note maximale : le distributeur correspondant au chocolat atteignant ce maximum sera choisi par le client

	
	public void initialiser() {
	}

	
	public void next() {
		System.out.println("next client1");
		/*
		IDistributeurChocolat D = null ;
		int step = Monde.LE_MONDE.getStep();
		double notemax =0.0;
		List<Double> Notesfinales = new ArrayList<Double>();
		List<Double> Notes = this.Offres(step);
		for (IDistributeurChocolat V: Distributeurs) {
			//for ( ArrayList<String> x: Commandes){
				double t =0 ;
				int i = 0 ;
				if ("V" == x.get(1)) {
					if (i==0) {
					     t = Notes.get(Distributeurs.indexOf(V)) *1.1  ; 
					}
					else {
						t = t*1.1;
					}
				}
			Notesfinales.add(t);	
			}
		}
		for ( double n : Notesfinales) {
			if (n>notemax) {
				notemax=n;
			}
		}
		
		D= Distributeurs.get(Notesfinales.indexOf(notemax));
		D.vendre(this.unique, quantitemoyenne);
		
		this.journal.ajouter(""+step);
		this.journal.ajouter(""+ D);
		this.journal.ajouter("" +numero);
		this.journal.ajouter(""+quantitemoyenne*temporalite.get(step));
	//*/
	}

}
	
	
	
