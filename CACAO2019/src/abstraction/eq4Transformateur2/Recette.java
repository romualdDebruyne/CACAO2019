package abstraction.eq4Transformateur2;

import java.util.ArrayList;
import java.util.List;

import abstraction.eq7Romu.produits.Chocolat;
import abstraction.eq7Romu.produits.Feve;

// Guillaume & Adrien
public enum Recette {
	// Définition des recettes
	HauteQ(Feve.CRIOLLO_HG_EQ, 		1, 5.0, 100.0, Chocolat.HG_E_SHP),
	MG_E_F(Feve.FORASTERO_MG_EQ, 	1, 3.0, 50.0,  Chocolat.MG_E_SHP),
	MG_NE_F(Feve.FORASTERO_MG_NEQ, 	1, 2.0, 50.0,  Chocolat.MG_NE_SHP),
	MG_E_M(Feve.MERCEDES_MG_EQ, 	1, 3.0, 60.0,  Chocolat.MG_E_SHP),
	MG_NE_M(Feve.MERCEDES_MG_NEQ, 	1, 2.0, 50.0,  Chocolat.MG_NE_SHP),
	MG_E_T(Feve.TRINITARIO_MG_EQ, 	1, 3.0, 65.0,  Chocolat.MG_E_SHP),
	MG_NE_T(Feve.TRINITARIO_MG_NEQ, 1, 2.0, 75.0,  Chocolat.MG_NE_SHP);
	
	private Feve inputFeve;
	private Chocolat output;
	private double inputParKgProduit; // kg de fèves par kg de chocolat
	private double coutParKgProduit; // coût en € pour produire 1 kg de chocolat (hors coûts fixes)
	private double coutFixe; // coût fixe en €
	
	Recette(Feve inputFeve, double inputParKgProduit, double coutParKgProduit, double coutFixe, Chocolat output) {
		this.inputFeve = inputFeve;
		this.inputParKgProduit = inputParKgProduit;
		this.coutParKgProduit = coutParKgProduit;
		this.coutFixe = coutFixe;
		this.output = output;
	}
	
	// Kelian
	/** Renvoie la liste des recettes utilisant un type de fève donné. */
	public static List<Recette> getRecettes(Feve inputFeve) {
		List<Recette> recettes = new ArrayList<Recette>();
		for(Recette r : values()) {
			if(r.getInputFeve().equals(inputFeve))
				recettes.add(r);
		}
		return recettes;
	}

	// Kelian
	/** Renvoie la liste des recettes produisant un type de chocolat donné. */
	public static List<Recette> getRecettes(Chocolat output) {
		List<Recette> recettes = new ArrayList<Recette>();
		for(Recette r : values()) {
			if(r.getOutput().equals(output))
				recettes.add(r);
		}
		return recettes;
	}
	
	public Feve getInputFeve() {
		return inputFeve;
	}

	public double getInputParKgProduit() {
		return inputParKgProduit;
	}

	public Chocolat getOutput() {
		return output;
	}
	
	public double getCoutParKgProduit() {
		return coutParKgProduit;
	}
	
	public double getCoutFixe() {
		return coutFixe;
	}
	
	public double calculCoutTransformation(double qte) {
		return getCoutParKgProduit() * qte + getCoutFixe();
	}
	
	// Kelian
	/** Renvoie la quantité que l'on peut produire avec cette recette pour un coût donné */
	public double getQteProductible(double cout) {
		 return Math.max(0, (cout - getCoutFixe()) / getCoutParKgProduit());
	}

}