package abstraction.eq4Transformateur2;

/**
 * @author kelia
 * Cette classe contient toutes les constantes nécessaires au fonctionnement de notre acteur.
 */
public class ConfigEQ4 {
	// Constantes générales 
	protected static final int STEPS_PAR_ANNEE = 24;
	
	// Quantités initiales
	protected static final double SOLDE_INITIAL = 100_000;
	protected static final double STOCK_INITIAL_QTE_PAR_CHOCOLAT = 0; //10e3; // en kg
	
	// Constantes relatives à la production
	protected static final double QTE_PRODUCTION_MIN = 10.0; // Quantité minimale produite par transformation, en kg
	protected static final double FONDS_ROULEMENT_CHOCOLAT = 1000;
	protected static final double MAX_PRODUCTION_PAR_STEP = 10e3; // Production max. de chocolats par step, en kg
	
	// Constantes utilisées par l'estimateur de demande
	protected static final int MEMOIRE_ESTIMATEUR_ANNEES = 5; // On utilise les n dernières années pour estimer les demandes futures
	protected static final int STEPS_ESTIMATION_DEMANDE_FUTURE = 12; // On estime la demande pour les n prochains steps

	// Constantes utilisées pour les contrats cadres d'achat de fèves
	protected static final double POIDS_MIN_CONTRAT_ACHAT = 300.0; // Poids min d'un contrat cadre de fèves, en kg.
	protected static final double DEPENSE_MAX_PAR_CC = 0.75; // Proportion maximale de notre solde que l'on s'autorise à dépenser pour un contrat.
	
	// Constantes utilisées pour les contrats cadres de ventes de chocolats
	protected static final double MARGE_VISEE = 0.6; // Marge que l'on tente de faire par rapport au coût de production
	protected static final double SEUIL_ACCEPTATION_FORCEE = 0.05; // Si l'écart relatif entre le prix proposé par l'acheteur et notre prix est inférieur à cette constante, on accepte
	protected static final double SEUIL_REFUS_FORCE = 0.80; // Si le prix proposé est inférieur à 80% du coût de production, on abandonne les négociations
	
}
