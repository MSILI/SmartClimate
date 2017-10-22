package app.com.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * 
 * 
 * Elle contient deux attributs priv�s : value qui prend la valeur de l'ann�e
 * (exemple : 2017) et une liste de mois listMois
 * 
 * @version 1.2
 * @author M'SILI Fatah.
 */
public class Annee implements Serializable {

	private static final long serialVersionUID = 1L;
	private int value;
	private List<Mois> listMois = new ArrayList<Mois>();

	public Annee(int value) {
		super();
		this.value = value;
	}

	/**
	 * Elle retourne un Mois, elle prend comme param�tre @param mValue afin de
	 * v�rifier si le mois existe dans listMois. Si ce dernier existe on le
	 * retourne sinon on en cr�e un nouveau avec la valeur @param mValue et on
	 * l'ajoute � listMois et cel� �vite de recr�er un mois existant dans la
	 * liste.
	 * 
	 * @param mValue
	 *            valeur du Mois (01 pour le premier mois -janvier- de l'ann�e).
	 * @return le type renvoy� est un Mois.
	 * @see Mois
	 */
	public Mois createMois(int mValue) {
		Mois mois = null;
		for (Mois m : listMois) {
			// si le mois existe
			if (m.getValue() == mValue) {
				mois = m;
			}
		}
		// si le mois n'existe pas
		if (mois == null) {
			mois = new Mois(mValue);
			listMois.add(mois);
		}
		return mois;
	}

	/**
	 * Cette m�thode permet de r�cup�rer la liste des relev�s du jour de d'un
	 * mois particulier de l'ann�e.
	 * 
	 * @param mois
	 *            valeur du mois
	 * @param jour
	 *            valeur du jour
	 * @return une liste de relev�s
	 * @see Releve
	 */
	public List<Releve> getListeReleveJour(int mois, int jour) {
		for (Mois m : this.getListMois()) {
			if (m.getValue() == mois) {
				return m.getListeReleveJour(jour);
			}
		}
		return null;
	}

	/**
	 * Cette m�thode permet de r�cup�rer la liste des relv�s d'un mois
	 * particulier.
	 * 
	 * @param mois
	 *            valeur du mois
	 * @param param
	 *            si c'est 1 liste de relev�s sinon liste des ecart-types.
	 * @return une liste de relev�s
	 * @see Releve
	 */
	public List<Releve> getListeReleveMois(int mois, int param) {
		for (Mois m : this.getListMois()) {
			if (m.getValue() == mois) {
				return m.getMoyenneRelevesMois(param);
			}
		}
		return null;
	}

	/**
	 * Cette m�thode permet de r�cup�rer la moyenne des relev� ou ecart-types de l'ann�e.
	 * 
	 * @param param
	 *            si c'est 1 liste de relev�s sinon liste des ecart-types.
	 * @return une liste de relev�s
	 * @see Releve
	 */
	public List<Releve> getListeMoyenneReleveAnnee(int param) {
		List<Releve> moyenneMoisAnnee = new ArrayList<Releve>();
		if (param == 1) {
			for (Mois m : this.getListMois()) {
				moyenneMoisAnnee.add(m.calculeMoyenneMois());
			}
		} else {
			for (Mois m : this.getListMois()) {
				moyenneMoisAnnee.add(m.calculeMoyenneEcartype());
			}
		}
		return moyenneMoisAnnee;
	}

	
	public int getValue() {
		return value;
	}

	public void setValue(int value) {
		this.value = value;
	}

	public List<Mois> getListMois() {
		return listMois;
	}

	public void setListMois(List<Mois> listMois) {
		this.listMois = listMois;
	}

}
