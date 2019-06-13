package abstraction.eq4Transformateur2;

/** 
 * @author Kelian
 * Classe utilitaire permettant de stocker deux valeurs (utilisée par plusieurs fonctions de notre acteur)
 */
public class Pair<X, Y> {
	private X x;
	private Y y;
	
	public Pair() {
		this(null, null);
	}
	
	public Pair(X x, Y y) {
		this.x = x;
		this.y = y;
	}
	
	public X getX() {
		return x;
	}
	 
	public Y getY() {
		return y;
	}
}
