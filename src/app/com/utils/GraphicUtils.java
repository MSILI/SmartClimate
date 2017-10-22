package app.com.utils;

import java.util.ArrayList;
import java.util.List;

import app.com.model.Releve;
import javafx.scene.chart.XYChart;

/**
 * Cette classe contient les méthodes qui permettent de créer des graphes pour
 * chacune des données météo (température, humidité, nébulosité).
 * 
 * @author M'SILI Fatah
 * @version 1.0
 */
public class GraphicUtils {

	/**
	 * Cette méthode permet de créer un graphe des température pour des listes
	 * de relevés particulieres.
	 * 
	 * @param listDate1
	 *            liste de relevés du 1er jour ou mois ou année.
	 * @param listDate2
	 *            liste de relevés du 2ieme jour ou mois ou année.
	 * @param listDateEcart1
	 *            liste de relevés d'ecart-type du 1er jour ou mois ou année.
	 * @param listDateEcart2
	 *            liste de relevés d'ecart-type du 2ieme jour ou mois ou année.
	 * @param listDateDiff
	 *            liste de relevés de la différence entre les deux jours ou mois
	 *            ou années.
	 * @param dateSerie1
	 *            date à afficher pour les séries du 1er jour ou mois ou année.
	 * @param dateSerie2
	 *            date à afficher pour les séries du 2ieme jour ou mois ou
	 *            année.
	 * @param cpt
	 *            un compteur qui va servir dans la progresse barre de la
	 *            méthode progressingBarre() des classes controlleurs.
	 * @param tempType
	 *            si c'est 1 donc on affiche le graphe de la température en
	 *            degrés Kelvin sinon si c'est 2 on affiche le graphe de la
	 *            température en degrès celcius.
	 * @return une Map de XYChart.Series<String, Number> de l'objet JavaFX
	 *         LineChart.
	 */
	public static List<XYChart.Series<String, Number>> createGrapheTemperature(List<Releve> listDate1,
			List<Releve> listDate2, List<Releve> listDateEcart1, List<Releve> listDateEcart2, List<Releve> listDateDiff,
			String dateSerie1, String dateSerie2, int cpt, int tempType) {

		List<XYChart.Series<String, Number>> listSeries = new ArrayList<XYChart.Series<String, Number>>();
		XYChart.Series<String, Number> tempSeries1 = new XYChart.Series<String, Number>();
		tempSeries1.setName("Température " + dateSerie1);
		XYChart.Series<String, Number> tempSeries2 = new XYChart.Series<String, Number>();
		tempSeries2.setName("Température " + dateSerie2);
		XYChart.Series<String, Number> tempSeriesEcart1 = new XYChart.Series<String, Number>();
		tempSeriesEcart1.setName("Ecart-type Température " + dateSerie1);
		XYChart.Series<String, Number> tempSeriesEcart2 = new XYChart.Series<String, Number>();
		tempSeriesEcart2.setName("Ecart-type Température " + dateSerie2);
		XYChart.Series<String, Number> tempSeriesDiff = new XYChart.Series<String, Number>();
		tempSeriesDiff.setName("Différence");
		if (tempType == 1) {
			if (listDate1 != null) {
				for (Releve r : listDate1) {
					if (r.getTemperature() != -1) {
						cpt++;
						tempSeries1.getData().add(
								new XYChart.Data<String, Number>(String.valueOf(r.getValue()), r.getTemperature()));
					}
				}
				if (!tempSeries1.getData().isEmpty()) {
					listSeries.add(tempSeries1);
				} else {
					MessageUtil.informationDialog(
							"Données introuvables pour cette station pour la date " + dateSerie1 + " !");
				}
			}
			if (listDate2 != null) {
				for (Releve r : listDate2) {
					if (r.getTemperature() != -1) {
						cpt++;
						tempSeries2.getData().add(
								new XYChart.Data<String, Number>(String.valueOf(r.getValue()), r.getTemperature()));
					}
				}
				if (!tempSeries2.getData().isEmpty()) {
					listSeries.add(tempSeries2);
				} else {
					MessageUtil.informationDialog(
							"Données introuvables pour cette station pour la date " + dateSerie2 + " !");
				}
			}

		} else {
			if (tempType == 2) {
				if (listDate1 != null) {
					for (Releve r : listDate1) {
						if (r.getTemperature() != -1) {
							cpt++;
							float celciusValue = (float) (r.getTemperature() - 273.15);
							tempSeries1.getData()
									.add(new XYChart.Data<String, Number>(String.valueOf(r.getValue()), celciusValue));
						}
					}
					if (!tempSeries1.getData().isEmpty()) {
						listSeries.add(tempSeries1);
					} else {
						MessageUtil.informationDialog(
								"Données introuvables pour cette station pour la date " + dateSerie1 + " !");
					}

				}
				if (listDate2 != null) {
					for (Releve r : listDate2) {
						if (r.getTemperature() != -1) {
							cpt++;
							float celciusValue = (float) (r.getTemperature() - 273.15);
							tempSeries2.getData()
									.add(new XYChart.Data<String, Number>(String.valueOf(r.getValue()), celciusValue));
						}
					}
					if (!tempSeries2.getData().isEmpty()) {
						listSeries.add(tempSeries2);
					} else {
						MessageUtil.informationDialog(
								"Données introuvables pour cette station pour la date " + dateSerie2 + " !");
					}
				}
			}
		}

		if (listDateEcart1 != null) {
			for (Releve r : listDateEcart1) {
				if (r.getTemperature() != -1) {
					cpt++;
					tempSeriesEcart1.getData()
							.add(new XYChart.Data<String, Number>(String.valueOf(r.getValue()), r.getTemperature()));
				}
			}

			if (!tempSeriesEcart1.getData().isEmpty()) {
				listSeries.add(tempSeriesEcart1);
			}
		}

		if (listDateEcart2 != null) {
			for (Releve r : listDateEcart2) {
				if (r.getTemperature() != -1) {
					cpt++;
					tempSeriesEcart2.getData()
							.add(new XYChart.Data<String, Number>(String.valueOf(r.getValue()), r.getTemperature()));
				}
			}
			if (!tempSeriesEcart2.getData().isEmpty()) {
				listSeries.add(tempSeriesEcart2);
			}
		}

		if (listDateDiff != null) {
			for (Releve r : listDateDiff) {
				if (r.getTemperature() != -1) {
					cpt++;
					tempSeriesDiff.getData()
							.add(new XYChart.Data<String, Number>(String.valueOf(r.getValue()), r.getTemperature()));
				}
			}
			if (!tempSeriesDiff.getData().isEmpty()) {
				listSeries.add(tempSeriesDiff);
			}
		}

		return listSeries;
	}

	/**
	 * Cette méthode permet de créer un graphe des Humidités pour des listes de
	 * relevés particulieres.
	 * 
	 * @param listDate1
	 *            liste de relevés du 1er jour ou mois ou année.
	 * @param listDate2
	 *            liste de relevés du 2ieme jour ou mois ou année.
	 * @param listDateEcart1
	 *            liste de relevés d'ecart-type du 1er jour ou mois ou année.
	 * @param listDateEcart2
	 *            liste de relevés d'ecart-type du 2ieme jour ou mois ou année.
	 * @param listDateDiff
	 *            liste de relevés de la différence entre les deux jours ou mois
	 *            ou années.
	 * @param dateSerie1
	 *            date à afficher pour les séries du 1er jour ou mois ou année.
	 * @param dateSerie2
	 *            date à afficher pour les séries du 2ieme jour ou mois ou
	 *            année.
	 * @return une Map de XYChart.Series<String, Number> de l'objet JavaFX
	 *         LineChart.
	 */
	public static List<XYChart.Series<String, Number>> createGrapheHumidite(List<Releve> listDate1,
			List<Releve> listDate2, List<Releve> listDateEcart1, List<Releve> listDateEcart2, List<Releve> listDateDiff,
			String dateSerie1, String dateSerie2) {
		List<XYChart.Series<String, Number>> listSeries = new ArrayList<XYChart.Series<String, Number>>();
		XYChart.Series<String, Number> humSeries1 = new XYChart.Series<String, Number>();
		humSeries1.setName("Humidité " + dateSerie1);
		XYChart.Series<String, Number> humSeries2 = new XYChart.Series<String, Number>();
		humSeries2.setName("Humidité " + dateSerie2);
		XYChart.Series<String, Number> humSeriesEcart1 = new XYChart.Series<String, Number>();
		humSeriesEcart1.setName("Ecart-type Humidité " + dateSerie1);
		XYChart.Series<String, Number> humSeriesEcart2 = new XYChart.Series<String, Number>();
		humSeriesEcart2.setName("Ecart-type Humidité " + dateSerie2);
		XYChart.Series<String, Number> humSeriesDeff = new XYChart.Series<String, Number>();
		humSeriesDeff.setName("Différence ");

		if (listDate1 != null) {
			for (Releve r : listDate1) {
				if (r.getHumidite() != -1) {
					humSeries1.getData()
							.add(new XYChart.Data<String, Number>(String.valueOf(r.getValue()), r.getHumidite()));
				}
			}
			if (!humSeries1.getData().isEmpty()) {
				listSeries.add(humSeries1);
			} else {
				MessageUtil
						.informationDialog("Données introuvables pour cette station pour la date " + dateSerie1 + " !");
			}
		}
		if (listDate2 != null) {
			for (Releve r : listDate2) {
				if (r.getHumidite() != -1) {
					humSeries2.getData()
							.add(new XYChart.Data<String, Number>(String.valueOf(r.getValue()), r.getHumidite()));
				}
			}
			if (!humSeries2.getData().isEmpty()) {
				listSeries.add(humSeries2);
			} else {
				MessageUtil
						.informationDialog("Données introuvables pour cette station pour la date " + dateSerie2 + " !");
			}
		}

		if (listDateEcart1 != null) {
			for (Releve r : listDateEcart1) {
				if (r.getHumidite() != -1) {
					humSeriesEcart1.getData()
							.add(new XYChart.Data<String, Number>(String.valueOf(r.getValue()), r.getHumidite()));
				}
			}

			if (!humSeriesEcart1.getData().isEmpty()) {
				listSeries.add(humSeriesEcart1);
			}
		}

		if (listDateEcart2 != null) {
			for (Releve r : listDateEcart2) {
				if (r.getHumidite() != -1) {
					humSeriesEcart2.getData()
							.add(new XYChart.Data<String, Number>(String.valueOf(r.getValue()), r.getHumidite()));
				}
			}
			if (!humSeriesEcart2.getData().isEmpty()) {
				listSeries.add(humSeriesEcart2);
			}
		}

		if (listDateDiff != null) {
			for (Releve r : listDateDiff) {
				if (r.getHumidite() != -1) {
					humSeriesDeff.getData()
							.add(new XYChart.Data<String, Number>(String.valueOf(r.getValue()), r.getHumidite()));
				}
			}
			if (!humSeriesDeff.getData().isEmpty()) {
				listSeries.add(humSeriesDeff);
			}
		}

		return listSeries;
	}

	/**
	 * Cette méthode permet de créer un graphe des Nébulosités pour des listes
	 * de relevés particulieres.
	 * 
	 * @param listDate1
	 *            liste de relevés du 1er jour ou mois ou année.
	 * @param listDate2
	 *            liste de relevés du 2ieme jour ou mois ou année.
	 * @param listDateEcart1
	 *            liste de relevés d'ecart-type du 1er jour ou mois ou année.
	 * @param listDateEcart2
	 *            liste de relevés d'ecart-type du 2ieme jour ou mois ou année.
	 * @param listDateDiff
	 *            liste de relevés de la différence entre les deux jours ou mois
	 *            ou années.
	 * @param dateSerie1
	 *            date à afficher pour les séries du 1er jour ou mois ou année.
	 * @param dateSerie2
	 *            date à afficher pour les séries du 2ieme jour ou mois ou
	 *            année.
	 * @return une Map de XYChart.Series<String, Number> de l'objet JavaFX
	 *         LineChart.
	 */
	public static List<XYChart.Series<String, Number>> createGrapheNebulosite(List<Releve> listDate1,
			List<Releve> listDate2, List<Releve> listDateEcart1, List<Releve> listDateEcart2, List<Releve> listDateDiff,
			String dateSerie1, String dateSerie2) {
		List<XYChart.Series<String, Number>> listSeries = new ArrayList<XYChart.Series<String, Number>>();
		XYChart.Series<String, Number> nebSeries1 = new XYChart.Series<String, Number>();
		nebSeries1.setName("Nébulosité " + dateSerie1);
		XYChart.Series<String, Number> nebSeries2 = new XYChart.Series<String, Number>();
		nebSeries2.setName("Nébulosité " + dateSerie2);
		XYChart.Series<String, Number> nebSeriesEcart1 = new XYChart.Series<String, Number>();
		nebSeriesEcart1.setName("Ecart-type Nébulosité " + dateSerie1);
		XYChart.Series<String, Number> nebSeriesEcart2 = new XYChart.Series<String, Number>();
		nebSeriesEcart2.setName("Ecart-type Nébulosité " + dateSerie2);
		XYChart.Series<String, Number> nebSeriesDiff = new XYChart.Series<String, Number>();
		nebSeriesDiff.setName("Différence ");

		if (listDate1 != null) {
			for (Releve r : listDate1) {
				if (r.getNebulosite() != -1) {
					nebSeries1.getData()
							.add(new XYChart.Data<String, Number>(String.valueOf(r.getValue()), r.getNebulosite()));
				}
			}
			if (!nebSeries1.getData().isEmpty()) {
				listSeries.add(nebSeries1);
			} else {
				MessageUtil
						.informationDialog("Données introuvables pour cette station pour la date " + dateSerie1 + " !");
			}
		}
		if (listDate2 != null) {
			for (Releve r : listDate2) {
				if (r.getNebulosite() != -1) {
					nebSeries2.getData()
							.add(new XYChart.Data<String, Number>(String.valueOf(r.getValue()), r.getNebulosite()));
				}
			}
			if (!nebSeries2.getData().isEmpty()) {
				listSeries.add(nebSeries2);
			} else {
				MessageUtil
						.informationDialog("Données introuvables pour cette station pour la date " + dateSerie2 + " !");
			}
		}

		if (listDateEcart1 != null) {
			for (Releve r : listDateEcart1) {
				if (r.getNebulosite() != -1) {
					nebSeriesEcart1.getData()
							.add(new XYChart.Data<String, Number>(String.valueOf(r.getValue()), r.getNebulosite()));
				}
			}

			if (!nebSeriesEcart1.getData().isEmpty()) {
				listSeries.add(nebSeriesEcart1);
			}
		}

		if (listDateEcart2 != null) {
			for (Releve r : listDateEcart2) {
				if (r.getNebulosite() != -1) {
					nebSeriesEcart2.getData()
							.add(new XYChart.Data<String, Number>(String.valueOf(r.getValue()), r.getNebulosite()));
				}
			}
			if (!nebSeriesEcart2.getData().isEmpty()) {
				listSeries.add(nebSeriesEcart2);
			}
		}

		if (listDateDiff != null) {
			for (Releve r : listDateDiff) {
				if (r.getNebulosite() != -1) {
					nebSeriesDiff.getData()
							.add(new XYChart.Data<String, Number>(String.valueOf(r.getValue()), r.getNebulosite()));
				}
			}
			if (!nebSeriesDiff.getData().isEmpty()) {
				listSeries.add(nebSeriesDiff);
			}
		}

		return listSeries;
	}

}
