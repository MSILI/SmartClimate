package app.com.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Elle contient deux attributs privés : value qui prend la valeur de du Jour
 * (exemple : 1 pour le premier jour du mois) et une liste de relevés
 * listReleves.
 * 
 * @version 1.2
 * @author M'SILI Fatah.
 */
public class Jour implements Serializable {

	private static final long serialVersionUID = 1L;
	private int value;
	private List<Releve> listReleves = new ArrayList<Releve>();

	public Jour(int value) {
		super();
		this.value = value;
	}

	public int getValue() {
		return value;
	}

	public void setValue(int value) {
		this.value = value;
	}

	public List<Releve> getListReleves() {
		return listReleves;
	}

	public void setListReleves(List<Releve> listReleves) {
		this.listReleves = listReleves;
	}

	/**
	 * Elle permet d'ajouter un relvé dans la liste des relevés d'un jour.
	 * 
	 * @param releve
	 *            le relvé qu'on en souhaite ajouter à la liste des relvés.
	 */
	public void addReleves(Releve releve) {
		listReleves.add(releve);
	}

	/**
	 * Elle renvoie un Releve, cette méthode permet de calculer l'ecartype sur
	 * l'ensemble des relevés d'une journée.
	 * 
	 * @return le type retourné est un Releve.
	 * @see Releve
	 */
	public Releve getEcartype() {
		Releve releve = new Releve(value);
		float tempEcart = 0, nebEcart = 0, humEcart = 0;
		int nbrtemp = 0, nbrneb = 0, nbrhum = 0;
		for (Releve r : listReleves) {
			if (r.getTemperature() != -1) {
				nbrtemp++;
				tempEcart = tempEcart
						+ (float) (Math.pow(r.getTemperature() - calculeMoyenneJour().getTemperature(), 2));
			}
			if (r.getHumidite() != -1) {
				nbrhum++;
				humEcart = humEcart + (float) (Math.pow(r.getHumidite() - calculeMoyenneJour().getHumidite(), 2));
			}
			if (r.getNebulosite() != -1) {
				nbrneb++;
				nebEcart = nebEcart + (float) (Math.pow(r.getNebulosite() - calculeMoyenneJour().getNebulosite(), 2));
			}
		}

		if (nbrtemp != 0) {
			tempEcart = (float) Math.sqrt(tempEcart / nbrtemp);
		} else {
			tempEcart = -1;
		}
		if (nbrhum != 0) {
			humEcart = (float) Math.sqrt(humEcart / nbrhum);
		} else {
			humEcart = -1;
		}
		if (nbrneb != 0) {
			nebEcart = (float) Math.sqrt(nebEcart / nbrneb);
		} else {
			nebEcart = -1;
		}
		releve.setTemperature(Math.round(tempEcart));
		releve.setHumidite(Math.round(humEcart));
		releve.setNebulosite(Math.round(nebEcart));
		return releve;
	}

	/**
	 * Elle retourne un releve, cette méthode calcule la moyenne des relevés
	 * d'un Jour.
	 * 
	 * @return le type retourné est un Releve.
	 * @see Releve
	 */
	public Releve calculeMoyenneJour() {
		Releve releve = new Releve(value);
		int nbrtemp = 0, nbrneb = 0, nbrhum = 0;
		float temp = 0, hum = 0, neb = 0;

		for (Releve r : listReleves) {
			// si ce n'est pas une valeur mq dans le csv
			if (r.getTemperature() != -1) {
				temp = temp + r.getTemperature();
				nbrtemp++;
			}
			if (r.getHumidite() != -1) {
				hum = hum + r.getHumidite();
				nbrhum++;
			}
			if (r.getNebulosite() != -1) {
				neb = neb + r.getNebulosite();
				nbrneb++;
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
		releve = new Releve(value);
		releve.setTemperature(Math.round(temp));
		releve.setHumidite(Math.round(hum));
		releve.setNebulosite(Math.round(neb));
		return releve;
	}

}
