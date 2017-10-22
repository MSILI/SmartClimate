package app.com.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Elle contient 3 attributs priv�s : nom qui prend la valeur du nom de la
 * station, numero : qui prend la valeur du numero correspondant au nom et la
 * liste des ann�es.
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
	 * Elle renvoie une Annee, elle prend comme param�tre @param aValue afin de
	 * v�rifier si le jour existe dans listAnnees. Si ce dernier existe on le
	 * retourne sinon on en cr�e un nouveau avec la valeur @param aValue et on
	 * l'ajoute � listAnnees et cel� �vite de recr�er un jour existant dans la
	 * liste.
	 * 
	 * @param aValue
	 *            c'est la valeur de l'ann�e.
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
	 * Cette m�thode permet de r�cup�rer la moyenne des relev�s du jour d'une
	 * ann�e particuli�re.
	 * 
	 * @param annee
	 *            valeur de l'ann�e
	 * @param mois
	 *            valeur du mois
	 * @param jour
	 *            valeur du jour
	 * @return une liste de relev�s
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
	 * Cette m�thode permet de r�cup�rer la liste des relev�s du mois d'une
	 * ann�e particuli�re.
	 * 
	 * @param annee
	 *            valeur de l'ann�e
	 * @param mois
	 *            valeur du mois
	 * @param param
	 *            si c'est 1 liste de relev�s sinon liste des ecart-types.
	 * @return une liste de Relev�s
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
	 * Cette m�thode permet de r�cup�rer la moyenne des relev�s ou �cart-types
	 * d'une ann�e particuliere.
	 * 
	 * @param annee
	 *            valeur de l'ann�e
	 * @param param
	 *            si c'est 1 liste de relev�s sinon liste des ecart-types.
	 * @return une liste de Relev�s
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
