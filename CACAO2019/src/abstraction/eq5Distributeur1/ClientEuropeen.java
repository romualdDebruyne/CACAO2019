/**
 * @author Erwann DEFOY
 */

package abstraction.eq5Distributeur1;

import java.util.List;

import abstraction.eq7Romu.distributionChocolat.IDistributeurChocolat;
import abstraction.eq7Romu.produits.Chocolat;
import abstraction.eq7Romu.produits.Gamme;
import abstraction.eq7Romu.ventesContratCadre.StockEnVente;
import abstraction.fourni.IActeur;
import abstraction.fourni.Journal;
import abstraction.fourni.Monde;

public class ClientEuropeen implements IActeur {
	private static int NB_CLIENT = 0;
	
	private int numero;
	private Journal journal;
	private Chocolat uniqueProduit;
	private int quantiteParStep;
	
	public String getNom() {
		return "CL"+this.numero;
	}

	public void initialiser() {
	}

	public ClientEuropeen(Chocolat uniqueProduit, int quantiteParStep) {
		System.out.println("creation client europeen");
		NB_CLIENT++;
		this.numero = NB_CLIENT;
		this.uniqueProduit = uniqueProduit;
		this.quantiteParStep = quantiteParStep;
		this.journal = new Journal("Journal"+this.getNom());
		Monde.LE_MONDE.ajouterJournal(this.journal);
	}


	public void next() {
		this.journal.ajouter("Step "+Monde.LE_MONDE.getStep()+" : tentative d'achat de "+quantiteParStep+" de "+this.uniqueProduit+" ____________");
		double quantiteAchetee = 0.0;
		IDistributeurChocolat distributeurDeQualite = null;
		double meilleureQualite = 0.0;
		double meilleurPrix = Double.MAX_VALUE;
		double quantiteEnVente = 0.0;
		double quantiteEnVenteMeilleur = 0.0;
		do {
			distributeurDeQualite = null;
			quantiteEnVenteMeilleur = 0.0;
			for (IActeur acteur : Monde.LE_MONDE.getActeurs()) { // recherche des distributeurs avec la meilleur qualité de chocolat
				if (acteur instanceof IDistributeurChocolat) { // recherche des distributeurs
					IDistributeurChocolat dist = (IDistributeurChocolat)acteur;
					StockEnVente<Chocolat> s = dist.getStockEnVente();
					if (s.getProduitsEnVente().contains(this.uniqueProduit)) { // recherche si le produit est dans le stock du distributeur sélectionné
						quantiteEnVente = s.get(this.uniqueProduit);
						this.journal.ajouter("Step "+Monde.LE_MONDE.getStep()+" : "+((IActeur)dist).getNom()+" vend la quantite de "+quantiteEnVente+" a "+dist.getPrix(this.uniqueProduit));
						if (quantiteEnVente>0.0) { // dist vend le chocolat recherche
							if (distributeurDeQualite==null || getNoteQualite(dist, this.uniqueProduit)>meilleureQualite) { // recherche si le produit est de meilleur qualité
								distributeurDeQualite = dist;
								quantiteEnVenteMeilleur = quantiteEnVente;
								meilleureQualite = getNoteQualite(dist, this.uniqueProduit);
								meilleurPrix = dist.getPrix(this.uniqueProduit);
							} else if (distributeurDeQualite==null || (getNoteQualite(dist, this.uniqueProduit) == meilleureQualite && dist.getPrix(this.uniqueProduit)<meilleurPrix)) { // prend le meilleur prix si qualité identique
								distributeurDeQualite = dist;
								quantiteEnVenteMeilleur = quantiteEnVente;
								meilleureQualite = getNoteQualite(dist, this.uniqueProduit);
								meilleurPrix = dist.getPrix(this.uniqueProduit);
							}
						}
					}
				}
			}
			if (quantiteAchetee<this.quantiteParStep && distributeurDeQualite!=null) {
				double quantiteCommandee = Math.min(this.quantiteParStep-quantiteAchetee, quantiteEnVenteMeilleur);
				double quantiteVendue = distributeurDeQualite.vendre(this.uniqueProduit, quantiteCommandee);
				quantiteAchetee+=quantiteVendue;
				this.journal.ajouter("Step "+Monde.LE_MONDE.getStep()+" : Achat de "+quantiteVendue+" chez "+((IActeur)distributeurDeQualite).getNom()+" au prix de "+meilleurPrix);
			}
		} while (quantiteAchetee<this.quantiteParStep && distributeurDeQualite!=null);
	}
	
	public double NoteQualite(Chocolat c) {
		int N = 0;
		if (c.isEquitable()) {
			N = N+1 ;
		}
		if (c.isSansHuileDePalme()) {
			N = N+1 ;
		}
		if (c.getGamme() == Gamme.HAUTE) {
			N= N+2 ;
		} else if (c.getGamme() == Gamme.MOYENNE) {
			N = N+1 ;
		}
		return 10*N/4;
	}
	
	public double getNoteQualite (IDistributeurChocolat dist, Chocolat c) {
		return NoteQualite (c);
		
	}

}
