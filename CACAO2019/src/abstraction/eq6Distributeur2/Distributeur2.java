package abstraction.eq6Distributeur2;


import java.util.HashMap;
import java.util.ArrayList;
import java.util.List;

import abstraction.eq7Romu.distributionChocolat.IDistributeurChocolat;
import abstraction.eq7Romu.produits.Chocolat;
import abstraction.eq7Romu.produits.Gamme;
import abstraction.eq7Romu.ventesContratCadre.ContratCadre;
import abstraction.eq7Romu.ventesContratCadre.Echeancier;
import abstraction.eq7Romu.ventesContratCadre.IAcheteurContratCadre;
import abstraction.eq7Romu.ventesContratCadre.IVendeurContratCadre;
import abstraction.eq7Romu.ventesContratCadre.StockEnVente;
import abstraction.fourni.IActeur;
import abstraction.fourni.Indicateur;
import abstraction.fourni.Journal;
import abstraction.fourni.Monde;


public class Distributeur2 implements IActeur, IAcheteurContratCadre<Chocolat>, IDistributeurChocolat {


	private List<ContratCadre<Chocolat>> contratsEnCours;
	private HashMap<Chocolat,Double> margeParProduit;
	private Indicateur soldeBancaire;

	private Indicateur stockMG_E_SHP;
	private Indicateur stockMG_NE_SHP;
	private Indicateur stockMG_NE_HP;
	private Indicateur stockHG_E_SHP;

	private Indicateur prixMG_E_SHP;
	private Indicateur prixMG_NE_SHP;
	private Indicateur prixMG_NE_HP;;
	private Indicateur prixHG_E_SHP;

	private Journal journal;

	private HashMap<Chocolat,Double> prixParProduit;
	private StockEnVente<Chocolat> stockEnVente; 


	public Distributeur2() {

		//NORDIN et Caroline


		// Partie se référant au journal
		this.journal = new Journal ("Marché du Chocolat");
		this.soldeBancaire = new Indicateur("EQ6 Solde Bancaire", this, 100000);

		Monde.LE_MONDE.ajouterIndicateur(this.soldeBancaire);

		//Chnager par nom du chocolat pour que le getNom de indcateur renvoie le type chocolat
		this.stockMG_E_SHP = new Indicateur("EQ6 stcok" + Chocolat.MG_E_SHP.toString(), this, 5000);
		Monde.LE_MONDE.ajouterIndicateur(this.stockMG_E_SHP);
		this.stockMG_NE_SHP = new Indicateur("EQ6 stock " + Chocolat.MG_NE_SHP.toString(), this,5000);
		Monde.LE_MONDE.ajouterIndicateur(this.stockMG_NE_SHP);
		this.stockMG_NE_HP = new Indicateur("EQ6 stock " + Chocolat.MG_NE_HP.toString(), this, 5000);
		Monde.LE_MONDE.ajouterIndicateur(this.stockMG_NE_HP);
		this.stockHG_E_SHP = new Indicateur("EQ6 stock "+ Chocolat.HG_E_SHP.toString(), this, 5000);
		Monde.LE_MONDE.ajouterIndicateur(this.stockHG_E_SHP);

		this.prixMG_E_SHP = new Indicateur("EQ6 " + Chocolat.MG_E_SHP.toString(), this, 50);
		Monde.LE_MONDE.ajouterIndicateur(this.prixMG_E_SHP);
		this.prixMG_NE_SHP = new Indicateur("EQ6 " + Chocolat.MG_NE_SHP.toString(), this, 50);
		Monde.LE_MONDE.ajouterIndicateur(this.prixMG_NE_SHP);
		this.prixMG_NE_HP = new Indicateur("EQ6 "+ Chocolat.MG_NE_HP.toString(), this, 100);
		Monde.LE_MONDE.ajouterIndicateur(this.prixMG_NE_HP);
		this.prixHG_E_SHP = new Indicateur("EQ6 " + Chocolat.HG_E_SHP.toString(), this, 100);
		Monde.LE_MONDE.ajouterIndicateur(this.prixHG_E_SHP);

		this.journal = new Journal("Journal EQ6");
		Monde.LE_MONDE.ajouterJournal(this.journal);

		this.stockEnVente = new StockEnVente<Chocolat>();
		this.stockEnVente.ajouter(Chocolat.HG_E_SHP, this.getStockHG_E_SHP().getValeur());
		this.stockEnVente.ajouter(Chocolat.MG_E_SHP, this.getStockMG_E_SHP().getValeur());
		this.stockEnVente.ajouter(Chocolat.MG_NE_SHP,this.getStockMG_NE_SHP().getValeur());
		this.stockEnVente.ajouter(Chocolat.MG_NE_HP, this.getStockMG_NE_HP().getValeur());

		this.contratsEnCours = new ArrayList<ContratCadre<Chocolat>>();
		this.margeParProduit = new HashMap<Chocolat, Double>();
		this.margeParProduit.put(Chocolat.HG_E_SHP, 1.5);
		this.margeParProduit.put(Chocolat.MG_E_SHP, 1.5);
		this.margeParProduit.put(Chocolat.MG_NE_SHP,1.5);
		this.margeParProduit.put(Chocolat.MG_NE_HP, 1.5);

		//Caroline
		this.prixParProduit =  new HashMap<Chocolat,Double>();
		this.prixParProduit.put(Chocolat.HG_E_SHP, this.getPrixHG_E_SHP().getValeur());
		this.prixParProduit.put(Chocolat.MG_E_SHP, this.getPrixMG_E_SHP().getValeur());
		this.prixParProduit.put(Chocolat.MG_NE_SHP,this.getPrixMG_NE_SHP().getValeur());
		this.prixParProduit.put(Chocolat.MG_NE_HP, this.getPrixMG_NE_HP().getValeur());
		//Monde.LE_MONDE.ajouterActeur(new Client1( 6.5, 100, 1.0 , 100.0, Chocolat.MG_NE_SHP));// ROMU
	}


	/**
	 * @return the soldeBancaire
	 */
	public Indicateur getSoldeBancaire() {
		return soldeBancaire;
	}


	/**
	 * @return the prixMG_E_SHP
	 */
	public Indicateur getPrixMG_E_SHP() {
		return prixMG_E_SHP;
	}

	/**
	 * @return the prixMG_NE_SHP
	 */
	public Indicateur getPrixMG_NE_SHP() {
		return prixMG_NE_SHP;
	}

	/**
	 * @return the prixMG_NE_HP
	 */
	public Indicateur getPrixMG_NE_HP() {
		return prixMG_NE_HP;
	}

	/**
	 * @return the prixHG_E_SHP
	 */
	public Indicateur getPrixHG_E_SHP() {
		return prixHG_E_SHP;
	}


	/**
	 * @return the stockMG_E_SHP
	 */
	public Indicateur getStockMG_E_SHP() {
		return stockMG_E_SHP;
	}

	/**
	 * @return the stockMG_NE_SHP
	 */
	public Indicateur getStockMG_NE_SHP() {
		return stockMG_NE_SHP;
	}

	/**
	 * @return the stockMG_NE_HP
	 */
	public Indicateur getStockMG_NE_HP() {
		return stockMG_NE_HP;
	}

	/**
	 * @return the stockHG_E_SHP
	 */
	public Indicateur getStockHG_E_SHP() {
		return stockHG_E_SHP;
	}
	//Nordin et Caro
//*
  	public Indicateur getIndicateurStock(Chocolat c) {
 
		if (c.getGamme()==Gamme.MOYENNE && (c.isEquitable()) && (c.isSansHuileDePalme())) {
			return this.stockMG_E_SHP;
		}
		if (c.getGamme()==Gamme.MOYENNE && !(c.isEquitable()) && (c.isSansHuileDePalme())) {
			return this.stockMG_NE_SHP;
		}
		if (c.getGamme()==Gamme.MOYENNE && !(c.isEquitable()) && !(c.isSansHuileDePalme())){ 
			return this.stockMG_NE_HP;
		}
		if (c.getGamme()==Gamme.HAUTE && (c.isEquitable()) && (c.isSansHuileDePalme())){
			System.out.println("getStock->indicateur "+this.stockHG_E_SHP.getNom());
			return this.stockHG_E_SHP;
		}
		else {
			return null;
		}
	}//*/
	//Nordin et Caro
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

	public List<ContratCadre<Chocolat>> getContratsEnCours() {
		return this.contratsEnCours;
	}

	public HashMap<Chocolat,Double> getPrixParProduit () {	
		return this.prixParProduit;
	}

	public StockEnVente<Chocolat> getStockEnVente() {
		//NORDIN
		return this.stockEnVente;
	}

	private double getMargeParProduit(Chocolat c) {
		if  (!getPrixParProduit().containsKey(c)) {
			return 0.0;
		}
		return (this.margeParProduit.containsKey(c)? this.margeParProduit.get(c) : 0.0);
	}

	public String getNom() {
		return "Walmart";

	}

	public void initialiser() {
	}

	public void next() {
System.out.println("Prix par produit : "+prixParProduit);
	}

	//Nordin
	public double getPrix(Chocolat c) {
		if (!getPrixParProduit().containsKey(c)) {
			return 0.0;
		}
		/*this.prixParProduit==null ? Double.MAX_VALUE */ 
		return	(getPrixParProduit().containsKey(c)? getPrixParProduit().get(c) : 0.0);

	}
	public int getArrondi(double d) {
		double d_2 = d*100;
		int i = (int) d_2;
		return i/100;
	}
	

	//NORDIN
	public double vendre(Chocolat c, double quantite) {
		List<String> chocolatsdisponibles = new ArrayList<String>();
		for (Chocolat chocolat : this.getStockEnVente().getProduitsEnVente()) {
			if( c.equals(chocolat)) {
				Double q = Math.min(this.getStockEnVente().get(c), quantite);
				Double stockenvente = this.getStockEnVente().get(c) - q;
				this.getStockEnVente().ajouter(c, stockenvente);
				System.out.println("vente de "+c+" quantite "+q);
				this.getIndicateurStock(c).retirer(this, q);
				this.getSoldeBancaire().ajouter(this, this.getPrix(c)*q);
				this.journal.ajouter("Vente de "+getArrondi(q)+ " kilos à " +getArrondi(this.getPrix(c)) + " euros pour le chocolat " +c);
				return q;
			}
			else {chocolatsdisponibles.add(""+chocolat);}
		}

		for (String i : chocolatsdisponibles) {
			this.journal.ajouter("Vente de 0.0 (produit demande = "+c+ " vs produit dispo = "+i+")");
		}

		return 0.0;
	}


	/** 
	 * Retire de la liste des contrats en cours les contrats pour lesquels la quantite a livrer 
	 * est nulle et le montant a regler est egalement nul (toutes les livraisons et tous les paiements
	 * ont ete effectues).
	 */

	public void retireVieuxContrats() {
		List<ContratCadre<Chocolat>> aEnlever = new ArrayList<ContratCadre<Chocolat>>();
		for (ContratCadre<Chocolat> c : this.contratsEnCours) {
			if (c.getQuantiteRestantALivrer()<=0.0 && c.getMontantRestantARegler()<=0.0) {
				aEnlever.add(c);
			}
		}
		for (ContratCadre<Chocolat> c : aEnlever) {
			this.contratsEnCours.remove(c);
		}
	}

	// Caroline 

	private HashMap<Chocolat, Double> derniere_vente () {

		HashMap<Chocolat, Double> vente_produit= new HashMap<Chocolat, Double>();
		double vente_stockMG_E_SHP = 0;
		double vente_stockMG_NE_HP = 0;
		double vente_stockMG_NE_SHP = 0;
		double vente_stockHG_E_SHP = 0;

		if (Monde.LE_MONDE.getStep()>0) {
			for (ContratCadre<Chocolat> c  : this.getContratsEnCours()) {
				Chocolat ch = (Chocolat) c.getProduit();
				if (ch.equals(Chocolat.MG_E_SHP)) {
					vente_stockMG_E_SHP +=c.getEcheancier().getQuantite(Monde.LE_MONDE.getStep()-1);
				}
				if (ch.equals(Chocolat.MG_NE_HP)) {
					vente_stockMG_NE_HP +=c.getEcheancier().getQuantite(Monde.LE_MONDE.getStep()-1);
				}
				if (ch.equals(Chocolat.MG_NE_SHP)) {
					vente_stockMG_NE_SHP +=c.getEcheancier().getQuantite(Monde.LE_MONDE.getStep()-1);
				}
				if (ch.equals(Chocolat.HG_E_SHP)) {
					vente_stockHG_E_SHP +=c.getEcheancier().getQuantite(Monde.LE_MONDE.getStep()-1);
				}
			}
		}
		if ( stockMG_E_SHP.getHistorique().getTaille()  > 2 ) {
			vente_stockMG_E_SHP += stockMG_E_SHP.getHistorique().get(stockMG_E_SHP.getHistorique().getTaille() -2).getValeur() - stockMG_E_SHP.getValeur();

		} else {
			vente_stockMG_E_SHP=0; }
		
		if ( stockMG_NE_SHP.getHistorique().getTaille()  > 2 ) {
			vente_stockMG_NE_SHP += stockMG_NE_SHP.getHistorique().get(stockMG_NE_SHP.getHistorique().getTaille() -2).getValeur() - stockMG_NE_SHP.getValeur();

		} else {
			vente_stockMG_NE_SHP=0;}

		if ( stockMG_NE_HP.getHistorique().getTaille()  > 2 ) {
			vente_stockMG_NE_HP += stockMG_NE_HP.getHistorique().get(stockMG_NE_HP.getHistorique().getTaille() -2).getValeur() - stockMG_NE_HP.getValeur();

		} else {
			vente_stockMG_NE_HP=0;}

		if ( stockHG_E_SHP.getHistorique().getTaille()  > 2 ) {
			vente_stockHG_E_SHP += stockHG_E_SHP.getHistorique().get(stockHG_E_SHP.getHistorique().getTaille() -2).getValeur() - stockHG_E_SHP.getValeur();

		} else {
			vente_stockHG_E_SHP=0;}


		vente_produit.put(Chocolat.MG_E_SHP, vente_stockMG_E_SHP);
		vente_produit.put(Chocolat.MG_NE_HP, vente_stockMG_NE_HP);
		vente_produit.put(Chocolat.MG_NE_SHP, vente_stockMG_NE_SHP);
		vente_produit.put(Chocolat.HG_E_SHP, vente_stockHG_E_SHP);

		return vente_produit;
	}

	//Caroline
	// À améliorer avec temporalité pour avoir des prévisions de ventes plus valables sur 5 steps environ

	private HashMap<Chocolat, Double> prevision_variation_stock_sur_5_STEPS () {
		HashMap<Chocolat, Double> variations_produit= new HashMap<Chocolat, Double>();

		//PREVISION stockMG_E_SHP

		double variation_stockMG_E_SHP = 0;
		double recevoir_stockMG_E_SHP = 0;
		double variation_stockMG_NE_HP = 0;
		double recevoir_stockMG_NE_HP = 0;
		double variation_stockHG_E_SHP = 0;
		double recevoir_stockHG_E_SHP = 0;
		double variation_stockMG_NE_SHP = 0;
		double recevoir_stockMG_NE_SHP = 0;

		for (ContratCadre<Chocolat> c  : this.getContratsEnCours()) {
			Chocolat ch = (Chocolat) c.getProduit();
			if (ch.equals(Chocolat.MG_E_SHP)) {
				recevoir_stockMG_E_SHP += c.getQuantiteRestantALivrer();}
			if (ch.equals(Chocolat.MG_NE_SHP)) {
				recevoir_stockMG_NE_SHP += c.getQuantiteRestantALivrer(); }
			if (ch.equals(Chocolat.MG_NE_HP)) {
				recevoir_stockMG_NE_HP += c.getQuantiteRestantALivrer(); }
			if (ch.equals(Chocolat.HG_E_SHP)) {
				recevoir_stockHG_E_SHP += c.getQuantiteRestantALivrer(); }
		}
		variation_stockMG_E_SHP = -5*this.derniere_vente().get(Chocolat.MG_E_SHP) + recevoir_stockMG_E_SHP;
		variations_produit.put(Chocolat.MG_E_SHP, variation_stockMG_E_SHP);

		variation_stockMG_NE_SHP = -5*this.derniere_vente().get(Chocolat.MG_NE_SHP)  + recevoir_stockMG_NE_SHP;
		variations_produit.put(Chocolat.MG_NE_SHP, variation_stockMG_NE_SHP);

		variation_stockMG_NE_HP = -5*this.derniere_vente().get(Chocolat.MG_NE_HP) + recevoir_stockMG_NE_HP;
		variations_produit.put(Chocolat.MG_NE_HP, variation_stockMG_NE_HP);

		variation_stockHG_E_SHP = -5*this.derniere_vente().get(Chocolat.HG_E_SHP) + recevoir_stockHG_E_SHP;
		variations_produit.put(Chocolat.HG_E_SHP, variation_stockHG_E_SHP);

		return variations_produit;
	}

	//Caroline
	private HashMap<Chocolat, Double> stockIdeal () {
		//HashMap<Chocolat, Double> historique_vente = historique_vente() ;

		//ETUDE DE MARCHÉ : Prendre en compte l'avis des clients les plus fidèles sur leur avis de produit ainsi que l'historique de leur demande 
		/*
				for (IActeur acteur : Monde.LE_MONDE.getActeurs()) {
					if (acteur instanceof Client1) {
						Client1 c = (Client1)acteur;
						c.Offres(Monde.LE_MONDE.getStep());
					} 
				}
		 
*/
		//Pour l'instant avec 4 clients qui veulent chaquun un produit different avec 7500 par step on prend : 

		HashMap<Chocolat, Double> stockIdeal= new HashMap<Chocolat, Double>();
		stockIdeal.put(Chocolat.MG_E_SHP, 15000.0);
		stockIdeal.put(Chocolat.MG_NE_SHP, 15000.0);
		stockIdeal.put(Chocolat.MG_NE_HP, 15000.0);
		stockIdeal.put(Chocolat.HG_E_SHP, 15000.0);
		//Il serait mieux de voir la quantite reçue par step afin de combler les écarts avec de nouveaux contrats 

		//Travail sur le stock idéal par rapport aux ventes précédentes
		
		for (Chocolat c : stockIdeal.keySet() ) {
			
			if (derniere_vente().get(c) < 5000) {
				stockIdeal.put(c, 5000.0);
			}
			if (derniere_vente().get(c) > 10000) {
				stockIdeal.put(c, 20000.0);
			}
		}
		 


		return stockIdeal;
	}


	public ContratCadre<Chocolat> getNouveauContrat() { //ILIAS et Caroline


		ContratCadre<Chocolat> res=null;

		double solde = this.getSoldeBancaire().getValeur();
		for (ContratCadre<Chocolat> cc : this.getContratsEnCours()) {
			solde = solde - cc.getMontantRestantARegler();
		}

		//Choix du produit 
		HashMap<Chocolat, Double> variations_produit = this.prevision_variation_stock_sur_5_STEPS ();

		Chocolat produit =  Chocolat.MG_NE_SHP;
		//Écart entre stock ideal et stock supposé dans 5 steps
		double max_ecart = Math.max(this.stockIdeal().get(produit) - (variations_produit.get(produit)+this.getStockEnVente().get(produit)),0.0);

		for (Chocolat c : variations_produit.keySet()) {
			if (this.stockIdeal().get(c) -  (variations_produit.get(c)+this.getStockEnVente().get(c)) > max_ecart) {
				max_ecart = Math.max(this.stockIdeal().get(c) -  (variations_produit.get(c)+this.getStockEnVente().get(c)),0.0);
				produit = c;
			}
		}
		System.out.println("produit selectionne = "+produit);
		System.out.println("stock ideal = "+this.stockIdeal().get(produit));

		//QUANTITE
		double quantite;
		if (variations_produit.get(produit) + this.getStockEnVente().get(produit) > this.stockIdeal().get(produit)) {
			quantite = 0;
		}

		else 
		{   quantite =  Math.max(this.stockIdeal().get(produit) - (variations_produit.get(produit)+this.getStockEnVente().get(produit)),0.0);
System.out.println("quantite voulue = "+quantite+" solde = "+solde+" stock="+this.getStockEnVente().get(produit));
		if (quantite <1000) {
				quantite = 0;
			}
		}

		retireVieuxContrats();
		
		if (solde >10000 && quantite > 1000 ) 
		{

			List<IVendeurContratCadre<Chocolat>> vendeurs = new ArrayList<IVendeurContratCadre<Chocolat>>();
			for (IActeur acteur : Monde.LE_MONDE.getActeurs()) {
				if (acteur instanceof IVendeurContratCadre<?>) {
					IVendeurContratCadre<Chocolat> vacteur = (IVendeurContratCadre<Chocolat>)acteur;
					StockEnVente<Chocolat> stock = vacteur.getStockEnVente();
					System.out.println(" acteur "+acteur+" vend "+stock.get(produit)+" quantite min="+(0.7*quantite));
					if (stock.get(produit)>quantite*0.7) {// on souhaite faire des contrats d'au moins 100kg
						vendeurs.add((IVendeurContratCadre<Chocolat>)vacteur);
					} 
				}
			}


			//VENDEUR
			double meilleurprix = 5000000;
			IVendeurContratCadre<Chocolat> vendeur = null;
			for (IVendeurContratCadre<Chocolat> v : vendeurs) 
			{
				System.out.println("vendeur = "+v+" prix ="+v.getPrix(produit, Math.min(v.getStockEnVente().get(produit),quantite)));
				if (v.getPrix(produit, Math.min(v.getStockEnVente().get(produit),quantite)) < meilleurprix) 
				{
					vendeur = v;
				}
			}
System.out.println("meilleur vendeur = "+vendeur);
			if (vendeur != null & produit != null && quantite != 0) 
			{
				res = new ContratCadre<Chocolat>(this, vendeur, produit, Math.min(vendeur.getStockEnVente().get(produit),quantite));
				this.journal.ajouter("Pour le step " +Monde.LE_MONDE.getStep()+ 
						" nouveau contrat non signé : " + res.getNumero()  +" Produit : "+ produit +
					" Quantité = " +  getArrondi(Math.min(vendeur.getStockEnVente().get(produit),quantite)) + " kilos "
					+ " vendeur= " + vendeur);
			}
			else 
			{ res = null;
			}

		}
		if (quantite > 1000) {this.journal.ajouter("La quantité de stock est suffisante "+res);}
		else {this.journal.ajouter(" Il ne reste que "+getArrondi(solde)+" euros une"
				+ " fois tous les contrats payes donc nous ne souhaitons pas en creer d'autres pour l'instant");}
		return res;

	}

	@Override
	//Caroline 
	//A ameliorer selon la quantite demandée dans contrat, la quantite nécessaire aux prochains steps selon la demande en prévoyant des futurs coûrts de stockage
	public void proposerEcheancierAcheteur(ContratCadre<Chocolat> cc) {
		if (cc!=null) {

			if (cc.getEcheancier()==null) { // il n'y a pas encore eu de contre-proposition de la part du vendeur
				cc.ajouterEcheancier(new Echeancier(Monde.LE_MONDE.getStep(), 5, cc.getQuantite()/5));

			}   

			else {
				cc.ajouterEcheancier(new Echeancier(cc.getEcheancier())); // on accepte la contre-proposition du vendeur 
				this.journal.ajouter("Contrat n° " + cc.getNumero() + " avec " + cc.getEcheancier().getNbEcheances()+ " échéances");
			}
		}
	}

	//Caroline
	// il faudra prendre en compte le prix de stockage 
	public boolean satisfaitParPrixContratCadre (ContratCadre<Chocolat> cc) {
		boolean satisfait = true;
		Chocolat produit = cc.getProduit();

		double dernierprixpropose = cc.getPrixAuKilo();
		double notreprix = this.getPrix(produit);

		if (notreprix/dernierprixpropose >= this.getMargeParProduit(cc.getProduit())) {
			satisfait = true;
		}else {
			satisfait = false;
		}

		return satisfait;
	}



	@Override
	//Caroline
	public void proposerPrixAcheteur(ContratCadre<Chocolat> cc) {
		//Si le dernier prix de la liste nous satisfait => proposer le même prix
		//Sinon, le dernier prix nous satisfait pas :
		//Si le vendeur propose 2 fois le même prix et pas satisfait => ne pas ajouter de prix
		// Sinon proposer un nouveau prix 

		if (cc!=null && 25 > cc.getListePrixAuKilo().size()) {
			if (satisfaitParPrixContratCadre (cc)) {
				cc.ajouterPrixAuKilo(cc.getPrixAuKilo());
				this.getIndicateurPrix(cc.getProduit()).ajouter(this,cc.getPrixAuKilo());
				this.journal.ajouter("Accord sur Prix sur contrat n° " + cc.getNumero());
			} else {
				if (cc.getListePrixAuKilo().size() >= 2) {
					if (cc.getListePrixAuKilo().get(cc.getListePrixAuKilo().size() -2)*1.02 < this.prixParProduit.get(cc.getProduit())) {
						cc.ajouterPrixAuKilo(cc.getListePrixAuKilo().get(cc.getListePrixAuKilo().size() -2)*1.02);
					} 
				}else {
					cc.ajouterPrixAuKilo(cc.getVendeur().getPrix(cc.getProduit(), cc.getQuantite())*0.8);

				}	}}}

	@Override//Caroline
	public void notifierAcheteur(ContratCadre<Chocolat> cc) {
		if (cc!=null) {
			this.journal.ajouter("Le contrat n° " + cc.getNumero()+ " est signé");
			this.getContratsEnCours().add(cc);
		}
	}

	@Override//Caroline
	public void receptionner(Chocolat produit, double quantite, ContratCadre<Chocolat> cc) {

		this.journal.ajouter("Réception du produit " + produit.toString() +
				" en quantité " + getArrondi(quantite) + " kilos "+ " provenant du contrat n° " + cc.getNumero());

		if (cc != null && quantite >0 && cc.getProduit().equals(produit)) {
			double quantiteajoutee= this.getStockEnVente().get(produit)+quantite;
			this.getStockEnVente().ajouter(produit, quantiteajoutee);
			this.getIndicateurStock(produit).ajouter(this, quantite);
			System.out.println("stock de "+produit+" augmente de "+quantite);

		}
	}

	@Override//Caroline et Nordin
	public double payer(double montant, ContratCadre<Chocolat> cc) {
		double montantpaye = 0;
		double solde = getSoldeBancaire().getValeur();
		if (cc!=null | montant ==0.0 ) {
			return 0.0;
		}

		if (montant<0.0) {
			throw new IllegalArgumentException("Appel de la methode payer avec un montant negatif");
		}

		if (solde - montant > -5000) {
			montantpaye = montant;
			this.soldeBancaire.retirer(this, montantpaye);
		} 
		else   {
			montantpaye = solde+5000;
			this.soldeBancaire.retirer(this, montantpaye);
		} 


		this.journal.ajouter(montantpaye + "€ sur le contrat n° " + cc.getNumero());

		return montantpaye;
	}




}
