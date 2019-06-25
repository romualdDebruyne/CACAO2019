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

public class ClientFidele implements IActeur {
	private static int NB_CLIENT = 0;

	private int numero;
	private Journal journal;
	private int quantiteParStep;
	Chocolat uniqueProduit;
	IDistributeurChocolat dist;

	/** @author Erwann DEFOY */
	public String getNom() {
		return "Client Fidele "+this.numero;
	}

	/** @author Erwann DEFOY */
	public void initialiser() {
	}

	/** @author Erwann DEFOY */
	public ClientFidele(IDistributeurChocolat dist, Chocolat uniqueProduit, int quantiteParStep) {
		NB_CLIENT++;
		this.numero = NB_CLIENT;
		this.quantiteParStep = quantiteParStep;
		this.uniqueProduit = uniqueProduit;
		this.dist = dist;
		this.journal = new Journal("Journal "+this.getNom());
		Monde.LE_MONDE.ajouterJournal(this.journal);
	}

	/** @author Erwann DEFOY */
	public void next() {
		this.journal.ajouter("Step "+Monde.LE_MONDE.getStep()+" : Tentative d'achat de "+quantiteParStep+" de chocolat a "+((IActeur)dist).getNom());
		double quantiteAchetee = 0.0;
		double quantiteEnVente = 0.0;
		double quantiteAVendre = 0.0;
		double achetteACeStep = 0.0;
		do {
			quantiteAVendre = 0.0;
			achetteACeStep = 0.0;
			StockEnVente<Chocolat> s = dist.getStockEnVente();
			if (s.getProduitsEnVente().contains(this.uniqueProduit)) {
				quantiteEnVente = s.get(this.uniqueProduit);
				this.journal.ajouter("Step "+Monde.LE_MONDE.getStep()+" : "+((IActeur)dist).getNom()+" vend la quantite de "+quantiteEnVente+" a "+dist.getPrix(this.uniqueProduit));
				if (quantiteEnVente>0.0) { // dist vend le chocolat recherche
					if (dist.getPrix(this.uniqueProduit)<60) {
						quantiteAVendre = quantiteEnVente;
					}
				} 
			}
			if (quantiteAchetee<this.quantiteParStep) {
				double quantiteCommandee = Math.min(this.quantiteParStep-quantiteAchetee, quantiteAVendre);
				double quantiteVendue = dist.vendre(this.uniqueProduit, quantiteCommandee);
				quantiteAchetee+=quantiteVendue;
				achetteACeStep = quantiteVendue;
				this.journal.ajouter("Step "+Monde.LE_MONDE.getStep()+" : Achat de "+uniqueProduit+" a la quantite de "+quantiteVendue+" chez "+((IActeur)dist).getNom()+" au prix de "+dist.getPrix(this.uniqueProduit));
			}
			//			Clavier.lireString();
		} while (achetteACeStep > 0.0 && quantiteAchetee<this.quantiteParStep);
	}
}
