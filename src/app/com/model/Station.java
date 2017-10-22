package app.com.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Elle contient 3 attributs privés : nom qui prend la valeur du nom de la
 * station, numero : qui prend la valeur du numero correspondant au nom et la
 * liste des années.
 *
 * @version 1.2
 * @author M'SILI Fatah.
 */
public class Station implements Serializable {

	private static final long serialVersionUID = 1L;
	private String nom;
	private String numero;
	private List<Annee> listAnnees = new ArrayList<Annee>();

	public Station(String nom, String numero) {
		super();
		this.nom = nom;
		this.numero = numero;
	}

	/**
	 * Elle renvoie une Annee, elle prend comme paramètre @param aValue afin de
	 * vérifier si le jour existe dans listAnnees. Si ce dernier existe on le
	 * retourne sinon on en crée un nouveau avec la valeur @param aValue et on
	 * l'ajoute à listAnnees et celà évite de recréer un jour existant dans la
	 * liste.
	 * 
	 * @param aValue
	 *            c'est la valeur de l'année.
	 * @return le type de retour est une Annee.
	 * @see Annee
	 */
	public Annee creatAnnee(int aValue) {
		Annee annee = null;
		for (Annee a : listAnnees) {
			if (a.getValue() == aValue) {
				annee = a;
			}
		}
		if (annee == null) {
			annee = new Annee(aValue);
			listAnnees.add(annee);
		}
		return annee;
	}

	/**
	 * Cette méthode permet de récupérer la moyenne des relevés du jour d'une
	 * année particulière.
	 * 
	 * @param annee
	 *            valeur de l'année
	 * @param mois
	 *            valeur du mois
	 * @param jour
	 *            valeur du jour
	 * @return une liste de relevés
	 */
	public List<Releve> getListeReleveJour(int annee, int mois, int jour) {
		for (Annee a : this.getListAnnees()) {
			if (a.getValue() == annee) {
				return a.getListeReleveJour(mois, jour);
			}
		}
		return null;
	}

	/**
	 * Cette méthode permet de récupérer la liste des relevés du mois d'une
	 * année particulière.
	 * 
	 * @param annee
	 *            valeur de l'année
	 * @param mois
	 *            valeur du mois
	 * @param param
	 *            si c'est 1 liste de relevés sinon liste des ecart-types.
	 * @return une liste de Relevés
	 * @see Releve
	 */
	public List<Releve> getListeReleveMois(int annee, int mois, int param) {
		for (Annee a : this.getListAnnees()) {
			if (a.getValue() == annee) {
				return a.getListeReleveMois(mois, param);
			}
		}
		return null;
	}

	/**
	 * Cette méthode permet de récupérer la moyenne des relevés ou écart-types
	 * d'une année particuliere.
	 * 
	 * @param annee
	 *            valeur de l'année
	 * @param param
	 *            si c'est 1 liste de relevés sinon liste des ecart-types.
	 * @return une liste de Relevés
	 * @see Releve
	 */
	public List<Releve> getListeReleveAnnee(int annee, int param) {
		for (Annee a : this.getListAnnees()) {
			if (a.getValue() == annee) {
				return a.getListeMoyenneReleveAnnee(param);
			}
		}
		return null;
	}

	public String getNom() {
		return nom;
	}

	public void setNom(String nom) {
		this.nom = nom;
	}

	public String getNumero() {
		return numero;
	}

	public void setNumero(String numero) {
		this.numero = numero;
	}

	public List<Annee> getListAnnees() {
		return listAnnees;
	}

	public void setListAnnees(List<Annee> listAnnees) {
		this.listAnnees = listAnnees;
	}

}
