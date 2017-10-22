package app.com.model;

import java.io.Serializable;

/**
 * Elle contient quatres attributs priv�s : value qui prend la valeur de du
 * relev� (exemple : 21 pour le relv� de 21h), temperature qui prend la valeur
 * de la temp�rature en degr�s Kelvin, humdite qui prend la valeur du %
 * l'humidit� et n�bulosit� qui prend la valeur en pourcentage de la n�bulosit�.
 *
 * @version 1.2
 * @author M'SILI Fatah.
 */
public class Releve implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private int value;
	private float temperature;
	private float humidite;
	private float nebulosite;

	public Releve(int value) {
		super();
		this.value = value;
	}

	public Releve(int value, float temperature, float humidite, float nebulosite) {
		super();
		this.value = value;
		this.temperature = temperature;
		this.humidite = humidite;
		this.nebulosite = nebulosite;
	}

	public int getValue() {
		return value;
	}

	public void setValue(int value) {
		this.value = value;
	}

	public float getTemperature() {
		return temperature;
	}

	public void setTemperature(float temperature) {
		this.temperature = temperature;
	}

	public float getHumidite() {
		return humidite;
	}

	public void setHumidite(float humidite) {
		this.humidite = humidite;
	}

	public float getNebulosite() {
		return nebulosite;
	}

	public void setNebulosite(float nebulosite) {
		this.nebulosite = nebulosite;
	}

}
