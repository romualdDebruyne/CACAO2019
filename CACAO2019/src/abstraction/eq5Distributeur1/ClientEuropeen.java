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
import abstraction.eq5Distributeur1.Publicite;

public class ClientEuropeen implements IActeur {
	private Journal journal;
	private int quantiteParStep;
	private String region;
	public static final int NOVEMBRE1 = 21;
	public static final int NOVEMBRE2 = 22;
	public static final int DECEMBRE1 = 23;
	public static final int DECEMBRE2 = 24;

	/** @author Erwann DEFOY */
	public String getNom() {
		return "Client "+ getRegion();
	}

	/** @author Erwann DEFOY */
	public void initialiser() {

	}

	/** @author Erwann DEFOY */
	public ClientEuropeen(int quantiteParStep, String region) {
		this.quantiteParStep = quantiteParStep;
		this.region = region;
		this.journal = new Journal("Journal "+this.getNom());
		Monde.LE_MONDE.ajouterJournal(this.journal);
	}

	/** @author Erwann DEFOY */
	public void next() {
		this.journal.ajouter(" ");
		this.journal.ajouter("Step "+Monde.LE_MONDE.getStep()+" : Tentative d'achat de "+quantiteParStep+" du meilleur chocolat ____________");
		double quantiteAchetee = 0.0;
		Chocolat produitQ = null;
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
					for (Chocolat c : s.getProduitsEnVente()) {
						quantiteEnVente = s.get(c);
						this.journal.ajouter("Step "+Monde.LE_MONDE.getStep()+" : "+((IActeur)dist).getNom()+" vend "+ c +" a la quantite de "+quantiteEnVente+" a "+dist.getPrix(c));
						if (quantiteEnVente>0.0) { // dist vend le chocolat recherche
							double noteQualite = getNoteQualite(dist,c);
							if (acteur instanceof IPublicitaire) {
								IPublicitaire pubActeur = (IPublicitaire)acteur;
								for (Publicite pub : pubActeur.getPubEnCours()) {
									if (pub.getProduit()==c && pub.getBudget()>0 && this.region == pub.getImpact()) {
										if (pub.getBudget() < 5000) {
											noteQualite = getNoteQualite(dist,c) + 1;
											this.journal.ajouter("-----PUB-----");
											this.journal.ajouter("Step "+Monde.LE_MONDE.getStep()+" : "+"La qualite du chocolat "+c+" de "+((IActeur)dist).getNom()+" passe de "+getNoteQualite(dist,c)+" à "+noteQualite);
										} else if (pub.getBudget() >= 5000 && pub.getBudget() < 7500) {
											noteQualite = getNoteQualite(dist,c) + 2;
											this.journal.ajouter("-----PUB-----");
											this.journal.ajouter("Step "+Monde.LE_MONDE.getStep()+" : "+"La qualite du chocolat "+c+" de "+((IActeur)dist).getNom()+" passe de "+getNoteQualite(dist,c)+" à "+noteQualite);
										} else if (pub.getBudget() >= 7500 && pub.getBudget() < 10000) {
											noteQualite = getNoteQualite(dist,c) + 3;
											this.journal.ajouter("-----PUB-----");
											this.journal.ajouter("Step "+Monde.LE_MONDE.getStep()+" : "+"La qualite du chocolat "+c+" de "+((IActeur)dist).getNom()+" passe de "+getNoteQualite(dist,c)+" à "+noteQualite);
										} else if (pub.getBudget() >= 10000) {
											noteQualite = getNoteQualite(dist,c) + 4;
											this.journal.ajouter("-----PUB-----");
											this.journal.ajouter("Step "+Monde.LE_MONDE.getStep()+" : "+"La qualite du chocolat "+c+" de "+((IActeur)dist).getNom()+" passe de "+getNoteQualite(dist,c)+" à "+noteQualite);
										}
									}
								}
							}
							if ((distributeurDeQualite==null || noteQualite>meilleureQualite) && dist.getPrix(c) < 70 ) { // recherche si le produit est de meilleur qualité
								distributeurDeQualite = dist;
								produitQ = c;
								quantiteEnVenteMeilleur = quantiteEnVente;
								meilleureQualite = noteQualite;
								meilleurPrix = dist.getPrix(c);
							} else if ((distributeurDeQualite==null || (getNoteQualite(dist, c) == meilleureQualite 
									&& dist.getPrix(c) < meilleurPrix)) && dist.getPrix(c) < 70) { // prend le meilleur prix si qualité identique
								distributeurDeQualite = dist;
								produitQ = c;
								quantiteEnVenteMeilleur = quantiteEnVente;
								meilleureQualite = noteQualite;
								meilleurPrix = dist.getPrix(c);
							}
						}
					}
				}
			}
			if (quantiteAchetee<this.quantiteParStep && distributeurDeQualite!=null) {
				double quantiteCommandee = Math.min(this.quantiteParStep-quantiteAchetee, quantiteEnVenteMeilleur);
				double quantiteVendue = distributeurDeQualite.vendre(produitQ, quantiteCommandee);
				quantiteAchetee+=quantiteVendue;
				this.journal.ajouter(" ");
				this.journal.ajouter("Step "+Monde.LE_MONDE.getStep()+" : Achat de "+ produitQ + " a la quantite de "+quantiteVendue+" chez "+((IActeur)distributeurDeQualite).getNom()+" au prix de "+meilleurPrix);
			}
		} while (quantiteAchetee<this.quantiteParStep && distributeurDeQualite!=null);
	}

	/** @author Erwann DEFOY */
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
		return N;
	}

	/** @author Erwann DEFOY */
	public double getNoteQualite (IDistributeurChocolat dist, Chocolat c) {
		return NoteQualite (c);
	}


	private String getRegion() {
		return this.region ;
	}

}