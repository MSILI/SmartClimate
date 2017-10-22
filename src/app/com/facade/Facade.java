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
 * Elle contient l'ensemble des traitements échangés entre le model (domaine) et
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
	 * Elle permet de créer et ajouter une station dans listStations si elle
	 * n'existe pas ou de la retourner si elle existe. Cette méthode permet
	 * d'éviter de répéter la création d'une station si elle existe déjà.
	 * 
	 * @param sNum
	 *            c'est le numéro de la station.
	 * @param sNom
	 *            c'est le nom de la station
	 * @return le type retourné de cette méthode est une Station.
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
	 * Cette methode permet de vérifier si une station existe dans la liste des
	 * stations.
	 * 
	 * @param stationName
	 *            nom de la station.
	 * @return un booléen
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
	 * Cette méthode permet de de récuperer la liste des relvés d'un jour au
	 * choix pour une station particulière : si la station existe on cherche
	 * vérifie si la liste des relevés du jour existe on les récupères
	 * directement sinon on lit les données à partir du csv du mois puis on
	 * récupère les données à partir de la liste des stations. la méthode qui
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
	 * @return une liste des relevés
	 * @throws Exception
	 *             non traitée.
	 */
	public List<Releve> getDataDay(String stationName, int annee, int mois, int jour) throws Exception {
		String mm = (mois < 10) ? "0" + mois : "" + mois;
		String jj = (jour < 10) ? "0" + jour : "" + jour;
		String date = annee + mm + jj;
		// vérifier si la station si elle existe déjà dans la liste des stations
		// si elle existe
		if (isStationExists(stationName) == false) {
			getDataDayAndMonthFromCSV(stationName, date);
		}
		if (isErreur() == false) {
			for (Station station : this.getListStations()) {
				// récupérer la station recherché dans la liste
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
	 * Cette méthode permet de de récuperer la liste des relvés d'un mois au
	 * choix pour une station particulière : si la station existe on vérifie si
	 * la liste des relevés du mois existe on les récupères directement sinon on
	 * lit les données à partir du csv du mois puis on récupère les données à
	 * partir de la liste des stations. la méthode qui permet de lire le fichier
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
	 *            si c'est 1 alors on récupère la liste des relevés, sinon on
	 *            récupère la liste des ecart-types.
	 * @return une liste des relevés
	 * @throws Exception
	 *             non traitée.
	 */
	public List<Releve> getDataMonth(String stationName, int annee, int mois, int param) throws Exception {
		String mm = (mois < 10) ? "0" + mois : "" + mois;
		String date = annee + mm;
		// vérifier si la station si elle existe déjà dans la liste des stations
		// si elle existe
		if (isStationExists(stationName) == false) {
			// récuperer les données de la station à partir du fichier csv
			getDataDayAndMonthFromCSV(stationName, date);
		}
		if (isErreur() == false) {
			for (Station station : this.getListStations()) {
				// récupérer la station recherché dans la liste
				if (station.getNom().equals(stationName)) {
					// sis la liste des relevés du mois n'existe pas on les
					// récupère dans le fichier csv
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
	 ** Cette méthode permet de de récuperer la liste des relvés d'une année au
	 * choix pour une station particulière : si la station existe on vérifie si
	 * la liste des relevés de l'année existe on les récupères directement sinon
	 * on lit les données à partir des fichiers csv des mois correspendant à
	 * cette année puis on récupère les données à partir de la liste des
	 * stations. la méthode qui permet de lire les fichiers csv est :
	 * {@link #getDataYearFromCSV(String, String)}.
	 * 
	 * @see {@link Releve}
	 * @param stationName
	 *            nom de la station
	 * @param annee
	 *            valeur de l'année
	 * @param param
	 *            si c'est 1 alors on récupère la liste des relevés, sinon on
	 *            récupère la liste des ecart-types.
	 * @return une liste des relevés
	 * @throws Exception
	 *             non traitée.
	 */
	public List<Releve> getDataYear(String stationName, int annee, int param) throws Exception {
		List<Releve> listeRels = null;
		String date = "" + annee;
		// vérifier si la station si elle existe déjà dans la liste des stations
		// si elle existe
		if (isStationExists(stationName) == false) {
			// récuperer les données de la station à partir du fichier csv
			getDataYearFromCSV(stationName, date);
		}
		if (isErreur() == false) {
			for (Station station : this.getListStations()) {
				// récupérer la station recherché dans la liste
				if (station.getNom().equals(stationName)) {
					// sis la liste des relevés de l'année n'existe pas on les
					// récupère dans le fichier csv
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
	 * Elle permet de calculer la différence entre deux liste de relevés: entre
	 * deux jour, deux mois ou deux années.
	 * 
	 * @param list1
	 *            liste des relvés de la date 1.
	 * @param list2
	 *            liste des relvés de la date 2.
	 * @return elle renvoie une liste des relvés (la différence entre list1 et
	 *         list2).
	 * @see Releve
	 */
	public List<Releve> getDifference(List<Releve> list1, List<Releve> list2) {
		List<Releve> listeRels = null;
		float temp = 0, hum = 0, neb = 0;
		if (list1 != null && list2 != null) {
			if (!list1.isEmpty() && !list2.isEmpty()) {
				listeRels = new ArrayList<Releve>();
				// si la taille des deux liste sont égales, on fait un calcule
				// normal
				if (list1.size() == list2.size()) {
					for (int i = 0; i < list1.size(); i++) {
						// Temperature
						// si toute les deux sont mésurée (!=mq dans le csv)
						if (list1.get(i).getTemperature() != -1 && list2.get(i).getTemperature() != -1) {
							temp = Math.abs(list1.get(i).getTemperature() - list2.get(i).getTemperature());
						} else {
							temp = -1;
						}

						// Humidité
						// si toute les deux sont mésurée (!=mq dans le csv)
						if (list1.get(i).getHumidite() != -1 && list2.get(i).getHumidite() != -1) {
							hum = Math.abs(list1.get(i).getHumidite() - list2.get(i).getHumidite());
						} else {
							// sinon on les marque -1 (non mesuré : c à dire
							// impossible de faire la différence si l'une des
							// deux et non mésurée)
							hum = -1;
						}

						// Temperature
						// si toute les deux sont mésurée (!=mq dans le csv)
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
					// petite taille (Liste2) et on cherche le relevé
					// correspondant dans la liste de grande taille; si l'elts
					// existe donc on fait la différence, sinon c'est impossible
					// de faire la différence on met la valeur de l'humidité ou
					// nébulosité ou température à -1 si l'un des données n'est
					// pas mésurée et calculer la différence sinon
					if (list1.size() > list2.size()) {
						for (Releve r2 : list2) {
							for (Releve r1 : list1) {
								if (r1.getValue() == r2.getValue()) {
									// Temperature
									// si toute les deux sont mésurée (!=mq dans
									// le csv)
									if (r1.getTemperature() != -1 && r2.getTemperature() != -1) {
										temp = Math.abs(r1.getTemperature() - r2.getTemperature());
									} else {
										temp = -1;
									}

									// Humidité
									// si toute les deux sont mésurée (!=mq dans
									// le csv)
									if (r1.getHumidite() != -1 && r2.getHumidite() != -1) {
										hum = Math.abs(r1.getHumidite() - r2.getHumidite());
									} else {
										hum = -1;
									}

									// Temperature
									// si toute les deux sont mésurée (!=mq dans
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
	 * Cette méthode permet de récupèrer les données d'un mois ou d'un jour a
	 * partir du fichier csv correspedant. Au début on vérifie si le fichier csv
	 * existe on récupère directement les données sinon on appel la méthode
	 * {@link app.com.utils.Utils#gUnzipFile(String)} qui permet de télécharger
	 * et dézipper le fichier csv correspendant au mois de la date, si y a pas
	 * d'erreur : connexion existe on télécharge puis on dézippe et on procède a
	 * la récupération des données sinon aucune données ne sera remplis dans
	 * listStation (null).
	 * 
	 * @param stationName
	 *            nom de la station
	 * @param date
	 *            la valeur de la date (yyyyMM si il s'agit d'un mois, yyyyMMjj
	 *            si il s'agit d'un jour)
	 * @throws Exception
	 *             non traitée car elle est couverte par un boolean
	 */
	public void getDataDayAndMonthFromCSV(String stationName, String date) throws Exception {
		erreur = false;
		LocalDate nowDay = LocalDate.now();
		String SaveDirName = savePath + File.separator + date.substring(0, 4);
		String fileName = date.substring(4, 6) + ".csv";
		File file = new File(SaveDirName + File.separator + fileName);
		boolean isfileExist = false;

		// si le fichier correspondant au mois de la date demandé
		if (file.exists()) {
			// si le mois demandé est égale au mois courant on le re-télécharge
			// et on le décompresse car à chaque fois on mis à jour le fichier
			// jusqu'à la fin du mois
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
		// si le fichier décompressé existe
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
			// si le fichier décompressé n'existe pas : il n'est pas téléchargé
			// et décompressé : pas de connexion internet donc on signale une
			// erreur
			erreur = true;
		}
	}

	/**
	 * Cette méthode permet de récupèrer les données d'une Année partir des
	 * fichiers csv correspedant à cette année. Au début on vérifie si les
	 * fichiers csv existent on récupère directement les données sinon on appel
	 * la méthode {@link app.com.utils.Utils#gUnzipFile(String)} qui permet de
	 * télécharger et dézipper les fichier csv correspendant a cette année, si y
	 * a pas d'erreur : connexion existe on télécharge puis on dézippe et on
	 * procède a la récupération des données sinon aucune données ne sera
	 * remplis dans listStation (null).
	 * 
	 * @param stationName
	 *            nom de la station
	 * @param annee
	 *            valeur de l'année.
	 * @throws Exception
	 *             non traitée car elle est couverte par un boolean
	 */
	public void getDataYearFromCSV(String stationName, String annee) throws Exception {
		erreur = false;
		int nbrFichierManquant = 0;
		// récupération des dates correspondantes au mois de l'année demandée
		// sous
		// format yyyyMM
		List<String> listMois = util.getMonthsYear(annee);
		// parcourir la liste des dates correspondant aux mois de l'année et
		// vérifier si les fichier existes : si ils existent on fait la lecture
		// sinon on signale une erreur et on retourne une station nulle comme
		// initialiser au debut de cette méthode
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
			// si au moins un seule fichier manquant (n'est pas téléchargé =>
			// pas de connexion) on signale une erreur
			erreur = true;
		} else {
			erreur = false;
		}
	}

	/**
	 * Cette méthode renvoie l'attribut privé listStations.
	 * 
	 * @see Station
	 * @return liste des stations.
	 */
	public List<Station> getListStations() {
		return listStations;
	}

	/**
	 * Elle permet de récupérer la valeur de la variable static "erreur".
	 * 
	 * @return la variable erreur.
	 */
	public boolean isErreur() {
		return erreur;
	}
}
