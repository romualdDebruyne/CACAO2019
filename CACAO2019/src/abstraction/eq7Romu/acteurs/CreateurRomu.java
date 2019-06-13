package abstraction.eq7Romu.acteurs;

import abstraction.eq7Romu.produits.Chocolat;
import abstraction.eq7Romu.produits.Feve;
import abstraction.eq7Romu.ventesContratCadre.SuperviseurVentesContratCadre;
import abstraction.fourni.IActeur;
import abstraction.fourni.Monde;

/**
 * Acteur creant et ajoutant au monde les acteurs qui ne sont pas le fruit des 6 equipes.
 * @author R. Debruyne
 *
 */
public class CreateurRomu implements IActeur {

	public CreateurRomu() {
		Monde.LE_MONDE.ajouterActeur(new SuperviseurVentesContratCadre());
//////		Monde.LE_MONDE.ajouterActeur(new ProducteurRomu(Feve.CRIOLLO_HG_EQ,
//////				10000, 100000.0, 100000.0));
//		Monde.LE_MONDE.ajouterActeur(new ProducteurRomu(Feve.CRIOLLO_HG_EQ,
//				100000, 400000.0, 100000.0));
//		Monde.LE_MONDE.ajouterActeur(new ProducteurRomu(Feve.TRINITARIO_MG_NEQ,
//				150000, 40000.0, 100000.0));
//		Monde.LE_MONDE.ajouterActeur(new ProducteurRomu(Feve.TRINITARIO_MG_EQ,
//				15000, 5000.0, 100000.0));
//		Monde.LE_MONDE.ajouterActeur(new ProducteurRomu(Feve.FORASTERO_MG_NEQ,
//				15000, 5000.0, 100000.0));
//		Monde.LE_MONDE.ajouterActeur(new ProducteurRomu(Feve.FORASTERO_MG_EQ,
//				15000, 5000.0, 100000.0));
//		Monde.LE_MONDE.ajouterActeur(new TransformateurRomu(Feve.TRINITARIO_MG_NEQ, 
//				Chocolat.MG_NE_HP, 10000, 1.5, 5000.0, 5000.0, 100000.0, 0.15));
//		Monde.LE_MONDE.ajouterActeur(new TransformateurRomu(Feve.TRINITARIO_MG_NEQ, 
//				Chocolat.MG_NE_HP, 5000, 1.8, 5000.0, 5000.0, 100000.0, 0.20));
		Monde.LE_MONDE.ajouterActeur(new TransformateurRomu(Feve.FORASTERO_MG_NEQ, 
				Chocolat.HG_E_SHP, 2000, 1.1, 2000.0, 2000.0, 100000.0, 0.25));
///		Monde.LE_MONDE.ajouterActeur(new TransformateurRomu(Feve.FORASTERO_MG_EQ, 
///				Chocolat.HG_E_SHP, 2000, 1.1, 2000.0, 2000.0, 100000.0, 0.25));
///		Monde.LE_MONDE.ajouterActeur(new TransformateurRomu(Feve.CRIOLLO_HG_EQ, 
///				Chocolat.HG_E_SHP, 2000, 1.1, 2000.0, 2000.0, 100000.0, 0.25));
//		Monde.LE_MONDE.ajouterActeur(new DistributeurRomu(Chocolat.MG_NE_HP, 
//				0.25, 200.0, 100000.0));
//////		Monde.LE_MONDE.ajouterActeur(new DistributeurRomu(Chocolat.HG_E_SHP, 
//////				0.35, 200.0, 100000.0));
////		Monde.LE_MONDE.ajouterActeur(new DistributeurRomu(Chocolat.MG_NE_HP, 
////				0.20, 200.0, 100000.0));
//		Monde.LE_MONDE.ajouterActeur(new DistributeurRomu(Chocolat.HG_E_SHP, 
//				0.30, 200.0, 100000.0));
////		Monde.LE_MONDE.ajouterActeur(new DistributeurRomu(Chocolat.MG_E_SHP, 
////				0.30, 200.0, 100000.0));
//		Monde.LE_MONDE.ajouterActeur(new DistributeurRomu(Chocolat.HG_E_SHP, 
//	0.30, 200.0, 100000.0));
///		Monde.LE_MONDE.ajouterActeur(new ClientFinalRomu(Chocolat.HG_E_SHP, 
///				7500.0));
//		Monde.LE_MONDE.ajouterActeur(new ClientFinalRomu(Chocolat.MG_NE_HP, 
//				7500.0));
	}
	public String getNom() {
		return "CreateurROMU";
	}

	public void initialiser() {
	}

	public void next() {
	}
}
