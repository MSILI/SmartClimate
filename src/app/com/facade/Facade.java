package app.com.facade;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import app.com.model.Annee;
import app.com.model.Jour;
import app.com.model.Mois;
import app.com.model.Releve;
import app.com.model.Station;
import app.com.utils.Utils;

/**
 * Elle contient l'ensemble des traitements �chang�s entre le model (domaine) et
 * les controleurs.
 *
 * @version 1.4
 * @author M'SILI Fatah.
 */
public class Facade {

	private List<Station> listStations = new ArrayList<Station>();
	private Utils util;
	private boolean erreur = false;
	private final String savePath = "C:\\smartClimateDepot";

	public Facade() {
		util = new Utils();
	}

	/**
	 * Elle permet de cr�er et ajouter une station dans listStations si elle
	 * n'existe pas ou de la retourner si elle existe. Cette m�thode permet
	 * d'�viter de r�p�ter la cr�ation d'une station si elle existe d�j�.
	 * 
	 * @param sNum
	 *            c'est le num�ro de la station.
	 * @param sNom
	 *            c'est le nom de la station
	 * @return le type retourn� de cette m�thode est une Station.
	 * @see Station
	 */
	public Station createStation(String sNum, String sNom) {
		Station station = null;
		for (Station s : listStations) {
			if (s.getNumero().equals(sNum)) {
				station = s;
			}
		}
		if (station == null) {
			station = new Station(sNom, sNum);
			listStations.add(station);
		}
		return station;
	}

	/**
	 * Cette methode permet de v�rifier si une station existe dans la liste des
	 * stations.
	 * 
	 * @param stationName
	 *            nom de la station.
	 * @return un bool�en
	 */
	public boolean isStationExists(String stationName) {
		if (!this.getListStations().isEmpty()) {
			for (Station station : this.getListStations()) {
				if (station.getNom().equals(stationName)) {
					return true;
				}
			}
		}
		return false;
	}

	/**
	 * Cette m�thode permet de de r�cuperer la liste des relv�s d'un jour au
	 * choix pour une station particuli�re : si la station existe on cherche
	 * v�rifie si la liste des relev�s du jour existe on les r�cup�res
	 * directement sinon on lit les donn�es � partir du csv du mois puis on
	 * r�cup�re les donn�es � partir de la liste des stations. la m�thode qui
	 * permet de lire le fichier csv est :
	 * {@link #getDataDayAndMonthFromCSV(String, String)}.
	 * 
	 * @see {@link Releve}
	 * @param stationName
	 *            nom de la station
	 * @param annee
	 *            annee du jour
	 * @param mois
	 *            mois du jour
	 * @param jour
	 *            valeur du jour
	 * @return une liste des relev�s
	 * @throws Exception
	 *             non trait�e.
	 */
	public List<Releve> getDataDay(String stationName, int annee, int mois, int jour) throws Exception {
		String mm = (mois < 10) ? "0" + mois : "" + mois;
		String jj = (jour < 10) ? "0" + jour : "" + jour;
		String date = annee + mm + jj;
		// v�rifier si la station si elle existe d�j� dans la liste des stations
		// si elle existe
		if (isStationExists(stationName) == false) {
			getDataDayAndMonthFromCSV(stationName, date);
		}
		if (isErreur() == false) {
			for (Station station : this.getListStations()) {
				// r�cup�rer la station recherch� dans la liste
				if (station.getNom().equals(stationName)) {
					if (station.getListeReleveJour(annee, mois, jour) == null) {
						getDataDayAndMonthFromCSV(stationName, date);
						return station.getListeReleveJour(annee, mois, jour);
					} else {
						return station.getListeReleveJour(annee, mois, jour);
					}
				}
			}
		}
		return null;
	}

	/**
	 * Cette m�thode permet de de r�cuperer la liste des relv�s d'un mois au
	 * choix pour une station particuli�re : si la station existe on v�rifie si
	 * la liste des relev�s du mois existe on les r�cup�res directement sinon on
	 * lit les donn�es � partir du csv du mois puis on r�cup�re les donn�es �
	 * partir de la liste des stations. la m�thode qui permet de lire le fichier
	 * csv est : {@link #getDataDayAndMonthFromCSV(String, String)}.
	 * 
	 * 
	 * @see {@link Releve}
	 * @param stationName
	 *            nom de la station
	 * @param annee
	 *            annee du mois
	 * @param mois
	 *            valeur du mois
	 * @param param
	 *            si c'est 1 alors on r�cup�re la liste des relev�s, sinon on
	 *            r�cup�re la liste des ecart-types.
	 * @return une liste des relev�s
	 * @throws Exception
	 *             non trait�e.
	 */
	public List<Releve> getDataMonth(String stationName, int annee, int mois, int param) throws Exception {
		String mm = (mois < 10) ? "0" + mois : "" + mois;
		String date = annee + mm;
		// v�rifier si la station si elle existe d�j� dans la liste des stations
		// si elle existe
		if (isStationExists(stationName) == false) {
			// r�cuperer les donn�es de la station � partir du fichier csv
			getDataDayAndMonthFromCSV(stationName, date);
		}
		if (isErreur() == false) {
			for (Station station : this.getListStations()) {
				// r�cup�rer la station recherch� dans la liste
				if (station.getNom().equals(stationName)) {
					// sis la liste des relev�s du mois n'existe pas on les
					// r�cup�re dans le fichier csv
					if (station.getListeReleveMois(annee, mois, param) == null) {
						getDataDayAndMonthFromCSV(stationName, date);
						return station.getListeReleveMois(annee, mois, param);
					} else {
						return station.getListeReleveMois(annee, mois, param);
					}
				}
			}
		}

		return null;
	}

	/**
	 ** Cette m�thode permet de de r�cuperer la liste des relv�s d'une ann�e au
	 * choix pour une station particuli�re : si la station existe on v�rifie si
	 * la liste des relev�s de l'ann�e existe on les r�cup�res directement sinon
	 * on lit les donn�es � partir des fichiers csv des mois correspendant �
	 * cette ann�e puis on r�cup�re les donn�es � partir de la liste des
	 * stations. la m�thode qui permet de lire les fichiers csv est :
	 * {@link #getDataYearFromCSV(String, String)}.
	 * 
	 * @see {@link Releve}
	 * @param stationName
	 *            nom de la station
	 * @param annee
	 *            valeur de l'ann�e
	 * @param param
	 *            si c'est 1 alors on r�cup�re la liste des relev�s, sinon on
	 *            r�cup�re la liste des ecart-types.
	 * @return une liste des relev�s
	 * @throws Exception
	 *             non trait�e.
	 */
	public List<Releve> getDataYear(String stationName, int annee, int param) throws Exception {
		List<Releve> listeRels = null;
		String date = "" + annee;
		// v�rifier si la station si elle existe d�j� dans la liste des stations
		// si elle existe
		if (isStationExists(stationName) == false) {
			// r�cuperer les donn�es de la station � partir du fichier csv
			getDataYearFromCSV(stationName, date);
		}
		if (isErreur() == false) {
			for (Station station : this.getListStations()) {
				// r�cup�rer la station recherch� dans la liste
				if (station.getNom().equals(stationName)) {
					// sis la liste des relev�s de l'ann�e n'existe pas on les
					// r�cup�re dans le fichier csv
					if (station.getListeReleveAnnee(annee, param) == null) {
						getDataYearFromCSV(stationName, date);
						return station.getListeReleveAnnee(annee, param);
					} else {
						return station.getListeReleveAnnee(annee, param);
					}
				}
			}
		}

		return listeRels;
	}

	/**
	 * Elle permet de calculer la diff�rence entre deux liste de relev�s: entre
	 * deux jour, deux mois ou deux ann�es.
	 * 
	 * @param list1
	 *            liste des relv�s de la date 1.
	 * @param list2
	 *            liste des relv�s de la date 2.
	 * @return elle renvoie une liste des relv�s (la diff�rence entre list1 et
	 *         list2).
	 * @see Releve
	 */
	public List<Releve> getDifference(List<Releve> list1, List<Releve> list2) {
		List<Releve> listeRels = null;
		float temp = 0, hum = 0, neb = 0;
		if (list1 != null && list2 != null) {
			if (!list1.isEmpty() && !list2.isEmpty()) {
				listeRels = new ArrayList<Releve>();
				// si la taille des deux liste sont �gales, on fait un calcule
				// normal
				if (list1.size() == list2.size()) {
					for (int i = 0; i < list1.size(); i++) {
						// Temperature
						// si toute les deux sont m�sur�e (!=mq dans le csv)
						if (list1.get(i).getTemperature() != -1 && list2.get(i).getTemperature() != -1) {
							temp = Math.abs(list1.get(i).getTemperature() - list2.get(i).getTemperature());
						} else {
							temp = -1;
						}

						// Humidit�
						// si toute les deux sont m�sur�e (!=mq dans le csv)
						if (list1.get(i).getHumidite() != -1 && list2.get(i).getHumidite() != -1) {
							hum = Math.abs(list1.get(i).getHumidite() - list2.get(i).getHumidite());
						} else {
							// sinon on les marque -1 (non mesur� : c � dire
							// impossible de faire la diff�rence si l'une des
							// deux et non m�sur�e)
							hum = -1;
						}

						// Temperature
						// si toute les deux sont m�sur�e (!=mq dans le csv)
						if (list1.get(i).getNebulosite() != -1 && list2.get(i).getNebulosite() != -1) {
							neb = Math.abs(list1.get(i).getNebulosite() - list2.get(i).getNebulosite());
						} else {
							neb = -1;
						}

						Releve releve = new Releve(list1.get(i).getValue());
						releve.setTemperature(temp);
						releve.setHumidite(hum);
						releve.setNebulosite(neb);
						listeRels.add(releve);
					}
				} else {
					// si list1.size() > list2.size() : on parcoure la liste de
					// petite taille (Liste2) et on cherche le relev�
					// correspondant dans la liste de grande taille; si l'elts
					// existe donc on fait la diff�rence, sinon c'est impossible
					// de faire la diff�rence on met la valeur de l'humidit� ou
					// n�bulosit� ou temp�rature � -1 si l'un des donn�es n'est
					// pas m�sur�e et calculer la diff�rence sinon
					if (list1.size() > list2.size()) {
						for (Releve r2 : list2) {
							for (Releve r1 : list1) {
								if (r1.getValue() == r2.getValue()) {
									// Temperature
									// si toute les deux sont m�sur�e (!=mq dans
									// le csv)
									if (r1.getTemperature() != -1 && r2.getTemperature() != -1) {
										temp = Math.abs(r1.getTemperature() - r2.getTemperature());
									} else {
										temp = -1;
									}

									// Humidit�
									// si toute les deux sont m�sur�e (!=mq dans
									// le csv)
									if (r1.getHumidite() != -1 && r2.getHumidite() != -1) {
										hum = Math.abs(r1.getHumidite() - r2.getHumidite());
									} else {
										hum = -1;
									}

									// Temperature
									// si toute les deux sont m�sur�e (!=mq dans
									// le csv)
									if (r1.getNebulosite() != -1 && r2.getNebulosite() != -1) {
										neb = Math.abs(r1.getNebulosite() - r2.getNebulosite());
									} else {
										neb = -1;
									}

									Releve releve = new Releve(r2.getValue());
									releve.setTemperature(temp);
									releve.setHumidite(hum);
									releve.setNebulosite(neb);
									listeRels.add(releve);
								}
							}
						}
					} else {
						listeRels = getDifference(list2, list1);
					}
				}
			}
		}
		return listeRels;
	}

	/**
	 * Cette m�thode permet de r�cup�rer les donn�es d'un mois ou d'un jour a
	 * partir du fichier csv correspedant. Au d�but on v�rifie si le fichier csv
	 * existe on r�cup�re directement les donn�es sinon on appel la m�thode
	 * {@link app.com.utils.Utils#gUnzipFile(String)} qui permet de t�l�charger
	 * et d�zipper le fichier csv correspendant au mois de la date, si y a pas
	 * d'erreur : connexion existe on t�l�charge puis on d�zippe et on proc�de a
	 * la r�cup�ration des donn�es sinon aucune donn�es ne sera remplis dans
	 * listStation (null).
	 * 
	 * @param stationName
	 *            nom de la station
	 * @param date
	 *            la valeur de la date (yyyyMM si il s'agit d'un mois, yyyyMMjj
	 *            si il s'agit d'un jour)
	 * @throws Exception
	 *             non trait�e car elle est couverte par un boolean
	 */
	public void getDataDayAndMonthFromCSV(String stationName, String date) throws Exception {
		erreur = false;
		LocalDate nowDay = LocalDate.now();
		String SaveDirName = savePath + File.separator + date.substring(0, 4);
		String fileName = date.substring(4, 6) + ".csv";
		File file = new File(SaveDirName + File.separator + fileName);
		boolean isfileExist = false;

		// si le fichier correspondant au mois de la date demand�
		if (file.exists()) {
			// si le mois demand� est �gale au mois courant on le re-t�l�charge
			// et on le d�compresse car � chaque fois on mis � jour le fichier
			// jusqu'� la fin du mois
			if (Integer.parseInt(date.substring(4, 6)) == nowDay.getMonthValue()
					&& Integer.parseInt(date.substring(0, 4)) == nowDay.getYear()) {
				file.delete();
				isfileExist = util.gUnzipFile(date);
			} else {
				isfileExist = true;
			}
		} else {
			isfileExist = util.gUnzipFile(date);
		}
		// si le fichier d�compress� existe
		if (isfileExist) {
			String numStation = util.getStationNum(stationName);
			String line = null;
			String[] tab = null;
			BufferedReader br = new BufferedReader(new FileReader(SaveDirName + File.separator + fileName));
			// lecture des lignes du CSV
			while ((line = br.readLine()) != null) {
				tab = line.split(";");
				if (tab[0].equals(numStation)) {
					if (tab[1].startsWith(date)) {
						Releve releve = new Releve(Integer.parseInt(tab[1].substring(8, 10)));
						try {
							releve.setTemperature(Math.round(Float.parseFloat(tab[7])));
						} catch (NumberFormatException e) {
							// si c'est la valeur 'mq' dans le CSV on remet a -1
							releve.setTemperature(-1);
						}
						try {
							releve.setNebulosite(Math.round(Float.parseFloat(tab[14])));
						} catch (NumberFormatException e) {
							// si c'est la valeur 'mq' dans le CSV on remet a -1
							releve.setNebulosite(-1);
						}
						try {
							releve.setHumidite(Math.round(Float.parseFloat(tab[9])));
						} catch (NumberFormatException e) {
							// si c'est la valeur 'mq' dans le CSV on remet a -1
							releve.setHumidite(-1);
						}
						Station station = this.createStation(numStation, stationName);
						Annee annee = station.creatAnnee(Integer.parseInt(tab[1].substring(0, 4)));
						Mois mois = annee.createMois(Integer.parseInt(tab[1].substring(4, 6)));
						Jour jour = mois.createJour(Integer.parseInt(tab[1].substring(6, 8)));
						jour.addReleves(releve);
					}
				}
			}
			br.close();
			erreur = false;
		} else {
			// si le fichier d�compress� n'existe pas : il n'est pas t�l�charg�
			// et d�compress� : pas de connexion internet donc on signale une
			// erreur
			erreur = true;
		}
	}

	/**
	 * Cette m�thode permet de r�cup�rer les donn�es d'une Ann�e partir des
	 * fichiers csv correspedant � cette ann�e. Au d�but on v�rifie si les
	 * fichiers csv existent on r�cup�re directement les donn�es sinon on appel
	 * la m�thode {@link app.com.utils.Utils#gUnzipFile(String)} qui permet de
	 * t�l�charger et d�zipper les fichier csv correspendant a cette ann�e, si y
	 * a pas d'erreur : connexion existe on t�l�charge puis on d�zippe et on
	 * proc�de a la r�cup�ration des donn�es sinon aucune donn�es ne sera
	 * remplis dans listStation (null).
	 * 
	 * @param stationName
	 *            nom de la station
	 * @param annee
	 *            valeur de l'ann�e.
	 * @throws Exception
	 *             non trait�e car elle est couverte par un boolean
	 */
	public void getDataYearFromCSV(String stationName, String annee) throws Exception {
		erreur = false;
		int nbrFichierManquant = 0;
		// r�cup�ration des dates correspondantes au mois de l'ann�e demand�e
		// sous
		// format yyyyMM
		List<String> listMois = util.getMonthsYear(annee);
		// parcourir la liste des dates correspondant aux mois de l'ann�e et
		// v�rifier si les fichier existes : si ils existent on fait la lecture
		// sinon on signale une erreur et on retourne une station nulle comme
		// initialiser au debut de cette m�thode
		for (String str : listMois) {
			LocalDate nowDay = LocalDate.now();
			String SaveDirName = savePath + File.separator + str.substring(0, 4);
			String fileName = str.substring(4, 6) + ".csv";
			File file = new File(SaveDirName + File.separator + fileName);
			boolean isfileExist = false;
			if (file.exists()) {
				if (Integer.parseInt(str.substring(4, 6)) == nowDay.getMonthValue()
						&& Integer.parseInt(str.substring(0, 4)) == nowDay.getYear()) {
					file.delete();
					isfileExist = util.gUnzipFile(str);
				} else {
					isfileExist = true;
				}
			} else {
				isfileExist = util.gUnzipFile(str);
			}

			if (isfileExist) {
				String numStation = util.getStationNum(stationName);
				String line = null;
				String[] tab = null;
				BufferedReader br = new BufferedReader(new FileReader(SaveDirName + File.separator + fileName));
				while ((line = br.readLine()) != null) {
					tab = line.split(";");
					if (tab[0].equals(numStation)) {
						if (tab[1].startsWith(str)) {
							Releve releve = new Releve(Integer.parseInt(tab[1].substring(8, 10)));
							try {
								releve.setTemperature(Math.round(Float.parseFloat(tab[7])));
							} catch (NumberFormatException e) {
								// si c'est la valeur 'mq' dans le CSV on remet
								// a -1
								releve.setTemperature(-1);
							}
							try {
								releve.setNebulosite(Math.round(Float.parseFloat(tab[14])));
							} catch (NumberFormatException e) {
								// si c'est la valeur 'mq' dans le CSV on remet
								// a -1
								releve.setNebulosite(-1);
							}
							try {
								releve.setHumidite(Math.round(Float.parseFloat(tab[9])));
							} catch (NumberFormatException e) {
								// si c'est la valeur 'mq' dans le CSV on remet
								// a -1
								releve.setHumidite(-1);
							}
							Station station = this.createStation(numStation, stationName);
							Annee ann = station.creatAnnee(Integer.parseInt(tab[1].substring(0, 4)));
							Mois mois = ann.createMois(Integer.parseInt(tab[1].substring(4, 6)));
							Jour jour = mois.createJour(Integer.parseInt(tab[1].substring(6, 8)));
							jour.addReleves(releve);
						}
					}
				}
				br.close();
			} else {
				nbrFichierManquant++;
			}
		}
		if (nbrFichierManquant != 0) {
			// si au moins un seule fichier manquant (n'est pas t�l�charg� =>
			// pas de connexion) on signale une erreur
			erreur = true;
		} else {
			erreur = false;
		}
	}

	/**
	 * Cette m�thode renvoie l'attribut priv� listStations.
	 * 
	 * @see Station
	 * @return liste des stations.
	 */
	public List<Station> getListStations() {
		return listStations;
	}

	/**
	 * Elle permet de r�cup�rer la valeur de la variable static "erreur".
	 * 
	 * @return la variable erreur.
	 */
	public boolean isErreur() {
		return erreur;
	}
}
