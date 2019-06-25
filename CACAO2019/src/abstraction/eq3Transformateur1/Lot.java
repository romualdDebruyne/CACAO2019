package abstraction.eq3Transformateur1;

/** 
 * 
 * @author kevin
 *
 */

public class Lot {
	private int nLot;
	private double quantite;
	private int date;
	
	public Lot (int nLot,double quantite,int date) {
		this.nLot = nLot;
		this.quantite = quantite;
		this.date = date;
	}
	public Lot() {
		this(0,0,0);
	}

	public int getnLot() {
		return nLot;
	}

	public void setnLot(int nLot) {
		this.nLot = nLot;
	}

	public double getQuantite() {
		return quantite;
	}

	public void setQuantite(double quantite) {
		this.quantite = quantite;
	}

	public int getDate() {
		return date;
	}

	public void setDate(int date) {
		this.date = date;
	}
	
	public void retirerQuantiteLot (double qteARetirer) {
		if (qteARetirer > this.quantite) {
			this.quantite = 0;
		}
		else {
			this.quantite = this.quantite - qteARetirer;
		}
	}
}
