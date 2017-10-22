package app.com.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Elle contient deux attributs privés : value qui prend la valeur de du mois
 * (exemple : 1 pour janvier) et une liste de jour listJours.
 * 
 * @version 1.2
 * @author M'SILI Fatah.
 */
public class Mois implements Serializable {

	private static final long serialVersionUID = 1L;
	private int value;
	private List<Jour> listJours = new ArrayList<Jour>();

	public Mois(int value) {
		super();
		this.value = value;
	}

	/**
	 * Elle retourne un Jour, elle prend comme paramètre @param jValue afin de
	 * vérifier si le jour existe dans listJours. Si ce dernier existe on le
	 * retourne sinon on en crée un nouveau avec la valeur @param jValue et on
	 * l'ajoute à listJours et celà évite de recréer un jour existant dans la
	 * liste.
	 * 
	 * @param jValue
	 *            c'est la valeur du Jour.
	 * @return le type retourné est un Jour.
	 * @see Jour
	 */
	public Jour createJour(int jValue) {
		Jour jour = null;
		for (Jour j : listJours) {
			if (j.getValue() == jValue) {
				jour = j;
			}
		}
		if (jour == null) {
			jour = new Jour(jValue);
			listJours.add(jour);
		}
		return jour;
	}

	/**
	 * Cette méthode permet de récupèrer une liste de relevés d'un jour
	 * particulier.
	 * 
	 * @param jour
	 *            valeur du jour
	 * @return une liste de relevés
	 * @see Releve
	 */
	public List<Releve> getListeReleveJour(int jour) {
		for (Jour j : this.getListJours()) {
			if (j.getValue() == jour) {
				return j.getListReleves();
			}
		}
		return null;
	}

	/**
	 * Cette méthode permet de récupérer la moyenne des relevés ou écart-type du
	 * mois
	 * 
	 * @param param
	 *            si c'est 1 liste de relevés sinon liste des ecart-types.
	 * @return une liste de relevés
	 */
	public List<Releve> getMoyenneRelevesMois(int param) {
		List<Releve> moyenneJourDuMois = new ArrayList<Releve>();
		if (param == 1) {
			for (Jour j : this.getListJours()) {
				moyenneJourDuMois.add(j.calculeMoyenneJour());
			}
		} else {
			for (Jour j : this.getListJours()) {
				moyenneJourDuMois.add(j.getEcartype());
			}
		}

		return moyenneJourDuMois;
	}

	/**
	 * Elle renvoie une liste de relevés, Cette méthode fait appelle au
	 * getEcartype() de la classe Jour. C'est à dire elle récupère l'ecart-type
	 * de chaque jour du mois.
	 * 
	 * @return le type de retour est une Liste de Releve.
	 * @see Releve
	 */
	public List<Releve> getEcartype() {
		List<Releve> listReleve = new ArrayList<Releve>();
		for (Jour j : listJours) {
			listReleve.add(j.getEcartype());
		}
		return listReleve;
	}

	/**
	 * Elle renvoie un relevé, cette méthode permet de calculer la moyenne des
	 * relevés du mois.
	 * 
	 * @return le type de retour est un Releve.
	 * @see Releve
	 *
	 */
	public Releve calculeMoyenneMois() {
		Releve releve = new Releve(value);
		// variables temoraires
		float temp = 0, hum = 0, neb = 0;
		/*
		 * compteur sur le nombre d'elts mesuré dans le csv (ceux != mq -- la
		 * valeur réelle correspondante à mq est -1 --)
		 */

		int nbrtemp = 0, nbrneb = 0, nbrhum = 0;

		for (Releve r : this.getMoyenneRelevesMois(1)) {
			if (r.getTemperature() != -1) {
				temp = temp + r.getTemperature();
				nbrtemp++;
			}
			if (r.getNebulosite() != -1) {
				neb = neb + r.getNebulosite();
				nbrneb++;
			}
			if (r.getHumidite() != -1) {
				hum = hum + r.getHumidite();
				nbrhum++;
			}
		}
		if (nbrtemp != 0) {
			temp = temp / nbrtemp;
		} else {
			temp = -1;
		}
		if (nbrhum != 0) {
			hum = hum / nbrhum;
		} else {
			hum = -1;
		}
		if (nbrneb != 0) {
			neb = neb / nbrneb;
		} else {
			neb = -1;
		}
		releve.setTemperature(Math.round(temp));
		releve.setHumidite(Math.round(hum));
		releve.setNebulosite(Math.round(neb));

		return releve;
	}

	/**
	 * Elle renvoie un Releve, cette méthode permet de calculer la moyenne des
	 * ecart-types d'un mois sur l'ensemble de ces jours.
	 * 
	 * @return le type de retour est un Releve.
	 * @see Releve
	 */
	public Releve calculeMoyenneEcartype() {
		Releve releve = new Releve(value);
		float temp = 0, hum = 0, neb = 0;
		int nbrtemp = 0, nbrneb = 0, nbrhum = 0;
		for (Releve r : this.getEcartype()) {
			if (r.getTemperature() != -1) {
				temp = temp + r.getTemperature();
				nbrtemp++;
			}
			if (r.getNebulosite() != -1) {
				neb = neb + r.getNebulosite();
				nbrneb++;
			}
			if (r.getHumidite() != -1) {
				hum = hum + r.getHumidite();
				nbrhum++;
			}
		}
		if (nbrtemp != 0) {
			temp = temp / nbrtemp;
		} else {
			temp = -1;
		}
		if (nbrhum != 0) {
			hum = hum / nbrhum;
		} else {
			hum = -1;
		}
		if (nbrneb != 0) {
			neb = neb / nbrneb;
		} else {
			neb = -1;
		}
		releve.setTemperature(Math.round(temp));
		releve.setHumidite(Math.round(hum));
		releve.setNebulosite(Math.round(neb));

		return releve;
	}

	public int getValue() {
		return value;
	}

	public void setValue(int value) {
		this.value = value;
	}

	public List<Jour> getListJours() {
		return listJours;
	}

	public void setListJours(List<Jour> listJours) {
		this.listJours = listJours;
	}

}
