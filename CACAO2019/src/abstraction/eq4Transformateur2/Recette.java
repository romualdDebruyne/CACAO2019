package abstraction.eq4Transformateur2;

import java.util.ArrayList;
import java.util.List;

import abstraction.eq7Romu.produits.Chocolat;
import abstraction.eq7Romu.produits.Feve;

// Guillaume & Adrien
public enum Recette {
	// Définition des recettes
	HauteQ(Feve.CRIOLLO_HG_EQ, 1, 5,Chocolat.HG_E_SHP),
	MG_E_F(Feve.FORASTERO_MG_EQ, 1, 3, Chocolat.MG_E_SHP),
	MG_NE_F(Feve.FORASTERO_MG_NEQ, 1, 2, Chocolat.MG_NE_SHP),
	MG_E_M(Feve.MERCEDES_MG_EQ, 1, 3, Chocolat.MG_E_SHP),
	MG_NE_M(Feve.MERCEDES_MG_NEQ, 1, 2, Chocolat.MG_NE_SHP),
	MG_E_T(Feve.TRINITARIO_MG_EQ, 1, 3, Chocolat.MG_E_SHP),
	MG_NE_T(Feve.TRINITARIO_MG_NEQ, 1, 2, Chocolat.MG_NE_SHP);
	
	private Feve inputFeve;
	private Chocolat output;
	private double inputQteParKilo; // kg de feve par kg de chocolat
	private double coutTransformation; // coût en € pour produire 1 kg de chocolat (hors coûts fixes)
	
	Recette(Feve inputFeve, double inputQte, double coutTransformation, Chocolat output) {
		this.inputFeve = inputFeve;
		this.inputQteParKilo = inputQte;
		this.coutTransformation = coutTransformation;
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

	public double getInputQteParKilo() {
		return inputQteParKilo;
	}

	public double getCoutTransformation() {
		return coutTransformation;
	}

	public Chocolat getOutput() {
		return output;
	}
	
	public double calculCout(double qte) {
		return getCoutTransformation() * qte; // TODO ajouter coût fixe
		// TODO URGENT prendre en compte le prix d'achat des feves ici ?
	}

	
}