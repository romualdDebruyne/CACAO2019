package abstraction.eq3Transformateur1;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import abstraction.eq3Transformateur1.Stock;
import abstraction.eq3Transformateur1.Marge;
import abstraction.eq7Romu.produits.Chocolat;
import abstraction.eq7Romu.produits.Feve;
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

public class Transformateur1 implements IActeur, IAcheteurContratCadre<Feve>, IVendeurContratCadre<Chocolat>  {
	
 	private Indicateur iStockFeves;
 	private Indicateur iStockChocolat;
    private Indicateur soldeBancaire;
	private Journal journal;

	//begin Raph
	private HashMap<Chocolat, Double> PRIX_VENTE_PAR_DEFAUT = new HashMap<Chocolat, Double>();
	//End Raph
	
	//Begin Kevin
	private static final double stockLim = 10000.0;
	//End Kevin
	

	//begin sacha
	private List<ContratCadre<Chocolat>> contratsChocolatEnCours;
	private List<ContratCadre<Feve>> contratsFeveEnCours;
	//end sacha
	//begin Raphael
	private Marge margeChocolats;
	private CoutEnFeves coutEnFeves;
	//end Raphael
	
	// begin eve
	private Stock<Chocolat> stockChocolat;
	private Stock<Feve> stockFeves;
	// end eve
	
	private List<Chocolat> peutEtreProduit;
	
	
	
	public Transformateur1() {

		//Begin Kevin
		this.peutEtreProduit = new ArrayList<Chocolat>();
		// produits specifies dans le cahier des charges
		this.peutEtreProduit.add(Chocolat.MG_NE_HP);
		this.peutEtreProduit.add(Chocolat.MG_NE_SHP);
		this.peutEtreProduit.add(Chocolat.MG_E_SHP);
		//End Kevin
		// --------------------------------- begin eve


		// stock de feves
		ArrayList<Feve> feves = new ArrayList<Feve>();
		for (Feve f: Arrays.asList(Feve.values())) {
			if (f.getGamme() == Gamme.MOYENNE) {
				feves.add(f);
			}
		}
		this.stockFeves = new Stock<Feve>(feves);
		for (Feve f: feves) {
			this.stockFeves.addQuantiteEnStock(f, 1000);
		}
		this.iStockFeves = new Indicateur("EQ3 stock feves", this, feves.size()*1000);


		// stock de chocolat
		ArrayList<Chocolat> chocolats = new ArrayList<Chocolat>();
		chocolats.add(Chocolat.MG_NE_HP);
		chocolats.add(Chocolat.MG_NE_SHP);
		chocolats.add(Chocolat.MG_E_SHP);
		this.stockChocolat = new Stock<Chocolat>(chocolats);
		for (Chocolat c: chocolats) {
			this.stockChocolat.addQuantiteEnStock(c, 1000000);
		}
		this.iStockChocolat = new Indicateur("EQ3 stock chocolat", this, chocolats.size()*1000000);

		// --------------------------------- end eve

		// --------------------------------- begin Raph
		//Feves utilisees pour la production des différents chocolats




		this.coutEnFeves = new CoutEnFeves(chocolats,feves);


		this.coutEnFeves.setCoutEnFeves(Chocolat.MG_NE_HP, Feve.FORASTERO_MG_NEQ, 0.5);
		this.coutEnFeves.setCoutEnFeves(Chocolat.MG_NE_HP, Feve.MERCEDES_MG_NEQ, 0.5);
		this.coutEnFeves.setCoutEnFeves(Chocolat.MG_NE_HP, Feve.TRINITARIO_MG_NEQ, 0.5);
		this.coutEnFeves.setCoutEnFeves(Chocolat.MG_NE_SHP, Feve.FORASTERO_MG_NEQ, 75.0/150);
		this.coutEnFeves.setCoutEnFeves(Chocolat.MG_NE_SHP, Feve.MERCEDES_MG_NEQ, 75.0/150);
		this.coutEnFeves.setCoutEnFeves(Chocolat.MG_NE_SHP, Feve.TRINITARIO_MG_NEQ, 75.0/150);
		this.coutEnFeves.setCoutEnFeves(Chocolat.MG_E_SHP, Feve.FORASTERO_MG_EQ, 12.0/20);
		this.coutEnFeves.setCoutEnFeves(Chocolat.MG_E_SHP, Feve.MERCEDES_MG_EQ, 12.0/20);
		this.coutEnFeves.setCoutEnFeves(Chocolat.MG_E_SHP, Feve.TRINITARIO_MG_EQ, 12.0/20); 


		// Marges sur chocolat
		this.margeChocolats = new Marge(chocolats);
		this.margeChocolats.setMargeBrute(Chocolat.MG_NE_HP, 5.);
		this.margeChocolats.setCoutProd(Chocolat.MG_NE_HP, 3.);
		this.margeChocolats.setMargeBrute(Chocolat.MG_NE_SHP, 10);
		this.margeChocolats.setCoutProd(Chocolat.MG_NE_SHP, 4.33);
		this.margeChocolats.setMargeBrute(Chocolat.MG_E_SHP, 10);
		this.margeChocolats.setCoutProd(Chocolat.MG_E_SHP, 4.5);

		this.PRIX_VENTE_PAR_DEFAUT.put(Chocolat.MG_NE_HP,40.);
		this.PRIX_VENTE_PAR_DEFAUT.put(Chocolat.MG_NE_SHP,40.);
		this.PRIX_VENTE_PAR_DEFAUT.put(Chocolat.MG_E_SHP,40.);
		this.PRIX_VENTE_PAR_DEFAUT.put(Chocolat.HG_E_SHP,1000.);
		

		// --------------------------------- end Raph
		
		
		this.soldeBancaire=new Indicateur("EQ3 solde bancaire", this, 2000000);
		this.journal = new Journal ("Journal EQ3");
		Monde.LE_MONDE.ajouterJournal(this.journal);
		System.out.println("ajout du journal jEq3");
		Monde.LE_MONDE.ajouterIndicateur(this.iStockFeves);
		Monde.LE_MONDE.ajouterIndicateur(this.soldeBancaire);
		Monde.LE_MONDE.ajouterIndicateur(this.iStockChocolat);
		
		//begin sacha
		this.contratsChocolatEnCours = new ArrayList<ContratCadre<Chocolat>>();
		this.contratsFeveEnCours = new ArrayList<ContratCadre<Feve>>();
		//end sacha
		

	}
	
	// -------------------------------------------------------------------------------------------
	// 			GETTERS & SETTERS
	// -------------------------------------------------------------------------------------------
	
	public String getNom() {
		return "EQ3";
	}
	
	
	// -------------------------------------------------------------------------------------------
	// 			STEPS
	// -------------------------------------------------------------------------------------------
		
	public void initialiser() {
	}

	public void next() {
		// -------------------------- begin eve
		
		// feves en stock = utilisables
		
		ArrayList<Feve> aDisposition = this.stockFeves.getProduitsEnStock();
		
		for (Feve f: aDisposition) {
			
			// on ne transforme que si on a assez de stock
			if (this.stockFeves.getQuantiteEnStock(f) > 100) {
				
				// on garde 10% des feves en cas de penurie
				double fevesUtilisees = this.stockFeves.getQuantiteEnStock(f)*0.9;
				
				// chocolats qu'on peut produire avec cette feve
				ArrayList<Chocolat> aProduire = new ArrayList<Chocolat>();
				for (Chocolat c: this.peutEtreProduit) {
					if (this.coutEnFeves.getCoutEnFeves(c, f)>0.0) {
						aProduire.add(c);
						System.out.println("c'est le prix" + this.getPrix(c, 1000.0));
					}
				}
				
				// on partage les feves entre les differents types de chocolat
				double fevesParProduit = fevesUtilisees/aProduire.size();
				for (Chocolat c: aProduire) {
					double nouveauChocolat = fevesParProduit/this.coutEnFeves.getCoutEnFeves(c, f);
					double coutProduction = nouveauChocolat*this.margeChocolats.getCoutProd(c);
					if (coutProduction<this.soldeBancaire.getValeur()) {
						// update solde bancaire
						this.soldeBancaire.retirer(this, nouveauChocolat*this.margeChocolats.getCoutProd(c));
						// updater stocks chocolat
						this.stockChocolat.addQuantiteEnStock(c, nouveauChocolat);
						this.iStockChocolat.ajouter(this, nouveauChocolat);
						// updater stocks feves
						this.stockFeves.removeQuantiteEnStock(f, fevesParProduit);
						this.journal.ajouter("Transformation de " + fevesParProduit + " de feves pour " + c.toString());
						this.iStockFeves.retirer(this, fevesParProduit);
					}
				}
			}
		}
		retireVieuxContrats();
		// -------------------------- end eve 
	}
	
	public void retireVieuxContrats() {
		//Begin Kevin
		List<ContratCadre<Feve>> aEnlever = new ArrayList<ContratCadre<Feve>>();
		for (ContratCadre<Feve> c : this.contratsFeveEnCours) {
			if (c.getQuantiteRestantALivrer()<=0.0 && c.getMontantRestantARegler()<=0.0) {
				aEnlever.add(c);
			}
		}
		for (ContratCadre<Feve> c : aEnlever) {
			this.contratsFeveEnCours.remove(c);
		}
		List<ContratCadre<Chocolat>> aEnleverC = new ArrayList<ContratCadre<Chocolat>>();
		for (ContratCadre<Chocolat> c : this.contratsChocolatEnCours) {
			if (c.getQuantiteRestantALivrer()<=0.0 && c.getMontantRestantARegler()<=0.0) {
				aEnleverC.add(c);
			}
		}
		for (ContratCadre<Chocolat> c : aEnleverC) {
			this.contratsChocolatEnCours.remove(c);
		}
		//End Kevin
	}
	
	
	// -------------------------------------------------------------------------------------------
	// 			ACHETEUR
	// -------------------------------------------------------------------------------------------

	
	
	// -------------------------- begin eve
	public double quantiteDesiree(double quantiteEnVente, double prix) {
		double possible = Math.max(0.0, soldeBancaire.getValeur()/prix);
		double desiree= Math.min(possible,  quantiteEnVente); // achete le plus possible
		return desiree;
	}
	// -------------------------- end eve
	
	
	@Override
	public ContratCadre<Feve> getNouveauContrat() {
		// begin eve
		ContratCadre<Feve> res=null;
		ArrayList<Feve> toutesFeves = new ArrayList<Feve>(Arrays.asList(Feve.values()));
		ArrayList<Feve> choixFeve = new ArrayList<Feve>();
		for (Feve f: toutesFeves) {
			if (f.getGamme() == Gamme.MOYENNE) {
				choixFeve.add(f);
			}
		}
		Feve f = choixFeve.get(((int) (Math.random()*choixFeve.size())));
		// on determine combien il resterait sur le compte si on soldait tous les contrats en cours.
		double solde = this.soldeBancaire.getValeur();
		this.journal.ajouter("Determination du solde une fois tous les contrats en cours payes");
		this.journal.ajouter("- solde="+solde);
		
		//Begin Kevin 
		//On calcule le cout de production du chocolat à ce step
		//C'est très similaire au next car je ne savais pas comment accéder à l'information du cout de production (calculé dans le next)
		//autrement qu'en faisant le calcul ici. C'est dû au fait que ce soit le superviseur qui appel les fonctions.
		ArrayList<Feve> aDisposition = this.stockFeves.getProduitsEnStock();
		for (Feve fe: aDisposition) {
			// on ne transforme que si on a assez de stock
			if (this.stockFeves.getQuantiteEnStock(fe) > 100) {
				
				// on garde 10% des feves en cas de penurie
				double fevesUtilisees = this.stockFeves.getQuantiteEnStock(fe)*0.9;
				
				// chocolats qu'on peut produire avec cette feve
				ArrayList<Chocolat> aProduire = new ArrayList<Chocolat>();
				for (Chocolat c: this.peutEtreProduit) {
					if (this.coutEnFeves.getCoutEnFeves(c, fe)>0.0) {
						aProduire.add(c);
					}
				}
				
				// on partage les feves entre les differents types de chocolat
				double fevesParProduit = fevesUtilisees/aProduire.size();
				for (Chocolat c: aProduire) {
					double nouveauChocolat = fevesParProduit/this.coutEnFeves.getCoutEnFeves(c, fe);
					double coutProduction = nouveauChocolat*this.margeChocolats.getCoutProd(c);
					solde = solde - coutProduction ;
					this.journal.ajouter("solde intermédiaire =" + solde);
				}
			}
		}
		//End Kevin
		
		for (ContratCadre<Feve> cc : this.contratsFeveEnCours) {
//			this.journal.ajouter("- contrat #"+cc.getNumero()+" restant a regler ="+cc.getMontantRestantARegler());
			solde = solde - cc.getMontantRestantARegler();
		}
		this.journal.ajouter("--> solde =" + solde);

		if (solde>10000.0) { // On ne cherche pas a etablir d'autres contrats d'achat si le compte bancaire est trop bas
			List<IVendeurContratCadre<Feve>> vendeurs = new ArrayList<IVendeurContratCadre<Feve>>();
			this.journal.ajouter("  recherche vendeur de "+f);
			for (IActeur acteur : Monde.LE_MONDE.getActeurs()) {
				if (acteur instanceof IVendeurContratCadre) {
					IVendeurContratCadre <Feve> vacteur = (IVendeurContratCadre<Feve>)acteur;
					StockEnVente<Feve> stock = vacteur.getStockEnVente();
					if (stock.get(f)>=1000.0) {// on souhaite faire des contrats d'au moins 1 tonne
						this.journal.ajouter("   "+(acteur.getNom())+" vend "+stock.get(f)+" de "+f);
						vendeurs.add((IVendeurContratCadre<Feve>)vacteur);
					} else {
//						this.journal.ajouter("   "+(acteur.getNom())+" ne vend que "+stock.toHtml());
					}
				}

			}
			if (vendeurs.size()>=1) {
				
				IVendeurContratCadre<Feve> vendeur = vendeurs.get( (int)( Math.random()*vendeurs.size()));// ici tire au hasard plutot que de tenir compte des stocks en vente et des prix
				double quantitetot  = vendeur.getStockEnVente().get(f);
				for (IVendeurContratCadre<Feve> v : vendeurs) {
					if (v.getStockEnVente().get(f) < quantitetot) {
						vendeur = v ;
						this.journal.ajouter("Le vendeurs" + vendeur + "vend plus de fèves");
					}
				
				}
				// On determine la quantite qu'on peut esperer avec le reste de notre solde bancaire
//				this.journal.ajouter(" Determination de la quantite achetable avec une somme de "+String.format("%.3f",solde)); 
//				this.journal.ajouter(" "); 
				double quantite = 1000.0; // On ne cherche pas a faire de contrat pour moins de 1 tonne
				double prix = vendeur.getPrix(f, quantite);
//				this.journal.ajouter("prix total = "+prix*quantite+" solde = "+solde);
				if (prix*quantite<solde) {

					while (!Double.isNaN(prix) && prix*quantite<solde ) {
						quantite=quantite*1.2;
						prix = vendeur.getPrix(f,  quantite);
//						this.journal.ajouter(" quantite "+String.format("%.3f",quantite)+" --> "+String.format("%.3f",prix*quantite));
					}
					quantite = quantite/1.5;
					res = new ContratCadre<Feve>(this, vendeur, f, quantite);
//					this.journal.ajouter("vendeur de "+f+" trouve: quantite = "+quantite);
				}
				else {
//					this.journal.ajouter("solde = "+solde+" insuffisant pour un contrat cadre de plus de 1 tonne");
				}
			} else {
//				this.journal.ajouter("   Aucun vendeur trouve --> pas de nouveau contrat a ce step"); 
			}

		} else {
//			this.journal.ajouter("   Il ne reste que "+solde+" une fois tous les contrats payes donc nous ne souhaitons pas en creer d'autres pour l'instant");
		}
		return res;
	}
	//end eve
	

	@Override
	public void proposerEcheancierAcheteur(ContratCadre<Feve> cc) {
		//Begin Kevin

		if (cc.getEcheancier()==null) { // il n'y a pas encore eu de contre-proposition de la part du vendeur 
			cc.ajouterEcheancier(new Echeancier(Monde.LE_MONDE.getStep(), 12, cc.getQuantite()/12)); 
		} 
		else { 
			if ((this.contratsFeveEnCours.isEmpty())&&(this.stockFeves.getQuantiteEnStock(cc.getProduit()) < stockLim)) { // On accepte forcément la proposition si on a pas de contrat cadre en cours et que le stock est inférieur à une quantité arbitraire 
				cc.ajouterEcheancier(new Echeancier(cc.getEcheancier())); 
			} 

			else if (Math.random() < 0.33) { 
				cc.ajouterEcheancier(new Echeancier(cc.getEcheancier())); //1 chance sur 3 d'accepter l'échéancier (si la première condition n'est pas remplie) 
			}

			else { // 2 chance sur 3 de proposer un echeancier etalant sur un step de plus 
				cc.ajouterEcheancier(new Echeancier(cc.getEcheancier().getStepDebut(),
						cc.getEcheancier().getNbEcheances()+1,
						cc.getQuantite()/(cc.getEcheancier().getNbEcheances()+1))); 
			}
			
		}
		//End Kevin
	}

	@Override
	public void proposerPrixAcheteur(ContratCadre<Feve> cc) {
		//begin raphael et eve

		double prixVendeur = cc.getListePrixAuKilo().get(0);
		int nbAchatsMoyenne=Math.min(10,cc.getListePrixAuKilo().size());//Nombre d'achats pris en compte pour le calcul de la moyenne (au plus 10)
		double moyenneDerniersAchats=0;
		for(int i=0;i<nbAchatsMoyenne;i++) {//Calcul de la moyenne des derniers prix d'achat
			moyenneDerniersAchats+=cc.getListePrixAuKilo().get(i);
		}
		moyenneDerniersAchats=moyenneDerniersAchats/nbAchatsMoyenne;
		if (prixVendeur<moyenneDerniersAchats*1.1) { // On accepte les prix inférieurs à 110% du prix moyen des derniers achats
			cc.ajouterPrixAuKilo(cc.getPrixAuKilo());
		} else {
			cc.ajouterPrixAuKilo(moyenneDerniersAchats); // Sinon on propose un achat au prix moyen d'achat des dernièrs achats
		}
		//end raphael et eve
	}
	
	@Override
	public void notifierAcheteur(ContratCadre<Feve> cc) {
		// begin sacha
		this.contratsFeveEnCours.add(cc);
		//end sachaaa
	}

	@Override
	public void receptionner(Feve produit, double quantite, ContratCadre<Feve> cc) {

		// begin sacha et eve

		this.journal.ajouter("Receptionner " + produit + ", quantite = " + quantite);
		ArrayList<Feve> produitsEnStock = this.stockFeves.getProduitsEnStock();
		if (produit==null || !(produitsEnStock.contains(produit))) {
			throw new IllegalArgumentException("Appel de la methode receptionner de Transformateur1 avec un produit ne correspondant pas aux feves achetees par le transformateur");

		}
				

		if (quantite<=0.0) {
			throw new IllegalArgumentException("Appel de la methode receptionner de Transformateur1 avec une quantite egale a "+quantite);
		}
		this.stockFeves.addQuantiteEnStock(produit, quantite);
		this.iStockFeves.ajouter(this, quantite);

	}
//end sacha et eve
	
	

	@Override
	public double payer(double montant, ContratCadre<Feve> cc) {
		// begin sacha
		if (montant<=0.0) {
			throw new IllegalArgumentException("Appel de la methode payer de Transformateur1 avec un montant negatif = "+montant);
		}
		double paiement = Math.min(montant,  this.soldeBancaire.getValeur()); // on peut avoir sans pb un retard de paiement
		this.soldeBancaire.retirer(this,  paiement);
		return paiement;
	}
	
	// -------------------------------------------------------------------------------------------
	// 			VENDEUR
	// -------------------------------------------------------------------------------------------

	public StockEnVente<Chocolat> getStockEnVente() {
		// ---------------- begin eve
		StockEnVente<Chocolat> res = new StockEnVente<Chocolat>();
		HashMap<Chocolat, Double> temp = new HashMap<Chocolat, Double>();
		// ajout des stocks effectifs
		for (Chocolat choco: this.stockChocolat.getProduitsEnStock()) {
			temp.put(choco, this.stockChocolat.getQuantiteEnStock(choco));
		}
		// retrait des quantites que l'on aura a livrer
		for (ContratCadre<Chocolat> cc : this.contratsChocolatEnCours) {
			Chocolat choco = cc.getProduit();
			double quantite = this.stockChocolat.getQuantiteEnStock(choco) - cc.getQuantiteRestantALivrer();
			temp.put(choco, Math.max(0.0, quantite));
		}
		// mise dans stock en vente si quantite non nulle
		for (Chocolat choco : temp.keySet()) {
			if (temp.get(choco)>0.0) {
				res.ajouter(choco, temp.get(choco));
			}
		}
		return res;
		// ---------------- end eve
	}
	
	@Override
	public double getPrix(Chocolat chocolat, Double quantite) {
		//Begin Raph/Kevin
		double prix=0.;
		
		if (chocolat==null || quantite<=0.0 || this.getStockEnVente().get(chocolat)<quantite) {
			return Double.NaN;
			
		}
		if (this.contratsFeveEnCours.size()==0) {
			return PRIX_VENTE_PAR_DEFAUT.get(chocolat);
		}
		else {
			HashMap<Feve,Double> prixMoyenFeves = new HashMap<Feve,Double>();
			HashMap<Feve, Double> qttTotaleFeves = new HashMap<Feve,Double>();
			
			for (ContratCadre<Feve> cc : this.contratsFeveEnCours) {
				prixMoyenFeves.put(cc.getProduit(), cc.getQuantiteRestantALivrer()*cc.getPrixAuKilo());
				qttTotaleFeves.put(cc.getProduit(), cc.getQuantiteRestantALivrer());
			}
			for(Feve feve : qttTotaleFeves.keySet()) {
				prixMoyenFeves.put(feve,prixMoyenFeves.get(feve)/qttTotaleFeves.get(feve));
				prix+=prixMoyenFeves.get(feve)*coutEnFeves.getCoutEnFeves(chocolat,feve);
			
					
				}
			}
		// begin Sacha ___ évolution du prix en fonction du stock en chocolat
		if (this.iStockChocolat.getValeur() < 60000000.0) {
			return prix + this.margeChocolats.getCoutProd(chocolat)+this.margeChocolats.getMargeBrute(chocolat);
			
		}
		else {
			
			return prix + this.margeChocolats.getCoutProd(chocolat) +this.margeChocolats.getMargeBrute(chocolat)/(2*this.iStockChocolat.getValeur()/60000000.0) ;
		}
		// end Sacha
	}
	
		//End Raph/Kevin
		
	

	@Override
	public void proposerEcheancierVendeur(ContratCadre<Chocolat> cc) {
		//Begin Kevin
		if (Math.random()<0.5) { // une chance sur deux d'accepter l'echeancier
			cc.ajouterEcheancier(new Echeancier(cc.getEcheancier())); // on accepte la proposition de l'acheteur car on a la quantite en stock 
		} else {
			if ((Math.random() < 0.5) && (cc.getEcheancier().getNbEcheances() > 1)) {
				cc.ajouterEcheancier(new Echeancier(cc.getEcheancier().getStepDebut(), cc.getEcheancier().getNbEcheances()-1, cc.getQuantite()/(cc.getEcheancier().getNbEcheances()-1)));
			    // une chance sur deux de proposer  un echeancier etalant sur un step de moins quand c'est possible
			}
			else {
				cc.ajouterEcheancier(new Echeancier(cc.getEcheancier().getStepDebut(), cc.getEcheancier().getNbEcheances()+1, cc.getQuantite()/(cc.getEcheancier().getNbEcheances()+1)));
				// une chance sur deux de proposer un echeancier etalant sur un step de plus
			}
		}
		//End Kevin
	}

	@Override
	public void proposerPrixVendeur(ContratCadre<Chocolat> cc) {
		//Begin Raphael
		
		if (cc.getListePrixAuKilo().size()==0) {
			cc.ajouterPrixAuKilo(getPrix(cc.getProduit(), cc.getQuantite()));
		} else {
			double prixVendeur = cc.getListePrixAuKilo().get(0);
			double prixAcheteur = cc.getPrixAuKilo();
			if (prixAcheteur>=0.75*prixVendeur) { // on ne fait une proposition que si l'acheteur ne demande pas un prix trop bas.
				if (Math.random()<0.25) { // probabilite de 25% d'accepter
					cc.ajouterPrixAuKilo(cc.getPrixAuKilo());
				} else {
					cc.ajouterPrixAuKilo((prixVendeur*(0.9+Math.random()*0.1))); // rabais de 10% max
				}
			}
		}
		// end Raphael
	}

	public void notifierVendeur(ContratCadre<Chocolat> cc) {
		//Begin Kevin
		this.contratsChocolatEnCours.add(cc);
		//End Kevin.
	}


	
	
	public double livrer(Chocolat produit, double quantite, ContratCadre<Chocolat> cc) {
		//Begin Raph/Kevin
		this.journal.ajouter("demande de livraison de " + produit + " avec quantite " + quantite);
		if (produit==null || !stockChocolat.getProduitsEnStock().contains(produit)) {
			this.journal.ajouter("Livraison " + produit + "rien");
			return 0.0 ;
		}
		else if (!stockChocolat.estEnStock(produit)) {
			return 0.0;
		}
		else {
			double livraison = Math.min(quantite, this.stockChocolat.getQuantiteEnStock(produit));
			this.stockChocolat.removeQuantiteEnStock(produit, livraison);
			this.iStockChocolat.retirer(this, livraison);
			this.journal.ajouter("Livraison " + produit + ", quantite = " + livraison + ", stock restant = " + this.stockChocolat.getQuantiteEnStock(produit));
			return livraison;
		}
		//End Raph/Kevin
		
	}

	
	public void encaisser(double montant, ContratCadre<Chocolat> cc) {

		//Begin Raph
		if (montant<0.0) {
			throw new IllegalArgumentException("Appel de la methode encaisser de Transformateur1 avec un montant negatif");
		}
		this.soldeBancaire.ajouter(this,  montant);
		
		//End Raph


		
		
	}
	
	
}
