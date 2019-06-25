package abstraction.eq5Distributeur1;

import abstraction.fourni.IActeur;
import abstraction.fourni.Indicateur;

public class CompteBancaire extends Indicateur {
	private double compte;
	
	//Cette classe sert à gérer notre compte bancaire pour y imposer des règles propres au compte
	/** @author Estelle BONNET */
	public CompteBancaire(String nom , IActeur createur, double compte) {
		super(nom, createur, compte);
	}
	
	/** @author Estelle BONNET */
	public CompteBancaire(String nom, IActeur createur) {
		super(nom, createur);
	}
	
	/** @author Estelle BONNET */
	public double getCompteBancaire() {
		return this.getValeur();
	}
	
	/**
	 * @author Erine DUPONT & Estelle BONNET
	 */
	public double Payer (IActeur auteur, double paiement) {
		double nouveausolde = this.getValeur() - paiement;
		if (paiement<0.0){
			throw new IllegalArgumentException("Appel de Payer(compte, paiement) de CompteBancaire avec paiement<0.0 (=="+paiement+")");
		}	else if (nouveausolde <0.0) {
			this.setValeur(auteur, 0);
			return paiement + nouveausolde;
		} else {
			this.setValeur(auteur, nouveausolde);
			return paiement;
		}
	}
	
	/** 
	 * @author Erine DUPONT & Estelle BONNET 
	 * */
	public void RecevoirPaiement (IActeur auteur, double paiement) {
		double nouveausolde = this.getValeur() + paiement*0.8;
		if (paiement <0.0) {
			throw new IllegalArgumentException("Appel de RecevoirPaiement(compte, paiement) de CompteBancaire avec paiement<0.0 (=="+paiement+")");
		} else {
			this.setValeur(auteur, nouveausolde);
		}
	}
}
