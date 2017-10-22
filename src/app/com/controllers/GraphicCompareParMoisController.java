package app.com.controllers;

import java.net.URL;
import java.time.LocalDate;
import java.util.List;
import java.util.ResourceBundle;

import app.com.facade.Facade;
import app.com.model.Releve;
import app.com.utils.GraphicUtils;
import app.com.utils.InitGraphiCompenents;
import app.com.utils.MessageUtil;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.RadioButton;
import javafx.scene.control.ToggleGroup;

/**
 *
 * Cette classe correspond au controlleur de la vue GraphCompareParJour.fxml.
 * Elle permet de d'afficher les courbes des donn�es a comparer pour deux Mois
 * au choix.
 *
 * @version 1.6
 * @author M'SILI Fatah.
 */
public class GraphicCompareParMoisController implements Initializable {

	@FXML
	private RadioButton btnTemp;
	@FXML
	private RadioButton btnHum;
	@FXML
	private RadioButton btnNeb;
	@FXML
	private ToggleGroup groupeR;
	@FXML
	private RadioButton radioKelvin;
	@FXML
	private RadioButton radioCelsius;
	@FXML
	private ToggleGroup radioGroup;
	@FXML
	private ComboBox<String> comboStations;
	@FXML
	private ComboBox<Integer> comboMois;
	@FXML
	private ComboBox<Integer> comboAnnee2;
	@FXML
	private ComboBox<Integer> comboAnnee1;
	@FXML
	private Label anneLabel;
	@FXML
	private ProgressBar progBarre;
	@FXML
	private ProgressIndicator progIndicator;
	@FXML
	private Label progLabel;
	@FXML
	private LineChart<String, Number> lineChart;
	private Facade facade = new Facade();

	private List<Releve> listRelevesMois1 = null;
	private List<Releve> listRelevesMois2 = null;
	private List<Releve> listRelevesEcartypeMois1 = null;
	private List<Releve> listRelevesEcartypeMois2 = null;
	private List<Releve> listRelevesDiff = null;
	private String dateAffich1 = null;
	private String dateAffich2 = null;
	LocalDate ld = LocalDate.now();

	/**
	 * C'est la m�thode FXML onAction du boutton visualiser, elle permet de
	 * r�cup�rer la liste des relev�s des deux mois � comparer et de les
	 * afficher sous forme de graphe et cel� en faisant appel �
	 * {@link #barreProgressing()}.
	 * 
	 * @throws Exception
	 *             non trait�e.
	 * @see {@link #barreProgressing()}
	 */
	@FXML
	private void visualiser() throws Exception {
		lineChart.getData().clear();
		if (validChamps()) {
			anneLabel.setText(comboStations.getValue());
			Task<?> barreProgressingTask = barreProgressing();
			progIndicator.progressProperty().unbind();
			progIndicator.progressProperty().bind(barreProgressingTask.progressProperty());
			progBarre.progressProperty().unbind();
			progBarre.progressProperty().bind(barreProgressingTask.progressProperty());
			InitGraphiCompenents.showProgressingEemets(progBarre, progIndicator, progLabel);
			barreProgressingTask.messageProperty().addListener(new ChangeListener<String>() {
				public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
					progLabel.setText(newValue);
				}
			});
			new Thread(barreProgressingTask).start();
		} else {
			MessageUtil.errorDialog("Veuillez choisir un mois de deux ann�es diff�rentes !");
		}

	}

	/**
	 * C'est une tache JavaFX qui synchronise entre la progression de la
	 * progressbarre et la r�cup�ration des donn�es a � afficher � partir de la
	 * classe Fa�ade (liste des relev�s des deux Mois en faisant appel �
	 * {@link app.com.facade.Facade#getDataMonth(String, int, int)}, liste des
	 * relev�s ecart-type des deux Mois en faisant appel �
	 * {@link app.com.facade.Facade#getDataEcarTypeMonth(String, int, int)}, et
	 * liste des relev�s diff�rence entre les deux Mois en faisant appel �
	 * {@link app.com.facade.Facade#getDifference(List, List)}.
	 * 
	 * @return null
	 */
	public Task<?> barreProgressing() {
		return new Task<String>() {
			@Override
			protected String call() throws Exception {
				String month = null;
				if (comboMois.getValue() < 10) {
					month = "0" + comboMois.getValue();
				} else {
					month = String.valueOf(comboMois.getValue());
				}
				dateAffich1 = month + "/" + comboAnnee1.getValue();
				dateAffich2 = month + "/" + comboAnnee2.getValue();
				String stationName = comboStations.getValue();
				updateMessage("T�l�chargement...");
				listRelevesMois1 = facade.getDataMonth(stationName, comboAnnee1.getValue(), comboMois.getValue(), 1);
				listRelevesMois2 = facade.getDataMonth(stationName, comboAnnee2.getValue(), comboMois.getValue(), 1);
				listRelevesDiff = facade.getDifference(listRelevesMois1, listRelevesMois2);
				listRelevesEcartypeMois1 = facade.getDataMonth(stationName, comboAnnee1.getValue(),
						comboMois.getValue(), 2);
				listRelevesEcartypeMois2 = facade.getDataMonth(stationName, comboAnnee2.getValue(),
						comboMois.getValue(), 2);
				int i = 0;
				Platform.runLater(new Runnable() {
					@Override
					public void run() {
						if (listRelevesMois1 != null && listRelevesMois2 != null) {
							List<XYChart.Series<String, Number>> listSeries = GraphicUtils.createGrapheTemperature(
									listRelevesMois1, listRelevesMois2, listRelevesEcartypeMois1,
									listRelevesEcartypeMois2, listRelevesDiff, dateAffich1, dateAffich2, i, 1);
							updateMessage("Chargement...");
							updateProgress(i + 1, 1);
							if (!listSeries.isEmpty()) {
								for (XYChart.Series<String, Number> serie : listSeries) {
									updateProgress(i + 1, 1);
									lineChart.getData().add(serie);
								}
								InitGraphiCompenents.enableCelsiusKelvin(radioKelvin, radioCelsius);
								InitGraphiCompenents.enableTempNebHum(btnTemp, btnHum, btnNeb);
								updateMessage("Succ�s !");
							}

						} else {
							updateProgress(0, 0);
							InitGraphiCompenents.hideProgressingEemets(progBarre, progIndicator, progLabel);
							InitGraphiCompenents.disableCelsiusKelvin(radioKelvin, radioCelsius);
							InitGraphiCompenents.disableTempNebHum(btnTemp, btnHum, btnNeb);
							MessageUtil
									.errorDialog("Erreur de connexion au serveur ! v�rifiez votre connexion internet.");
						}

					}
				});
				return null;
			}
		};
	}

	/**
	 * c'est une m�thode FXML onAction du radio button temp�rature, qui permet
	 * d'afficher le grahes des temp�ratures des deux Mois degr�s Kelvin en
	 * faisant appel �
	 * {@link app.com.utils.GraphicUtils#createGrapheTemperature(List, List, List, List, List, String, String, int, int)}.
	 */
	@FXML
	private void displayTemperature() {
		InitGraphiCompenents.enableCelsiusKelvin(radioKelvin, radioCelsius);
		lineChart.getData().clear();
		lineChart.setTitle("MOYENNE DES TEMPERATURES (�K)");
		lineChart.getYAxis().setLabel("Temp�rature (�K)");
		if (listRelevesMois1 != null && listRelevesMois2 != null) {
			List<XYChart.Series<String, Number>> listSeries = GraphicUtils.createGrapheTemperature(listRelevesMois1,
					listRelevesMois2, listRelevesEcartypeMois1, listRelevesEcartypeMois2, listRelevesDiff, dateAffich1,
					dateAffich2, 0, 1);
			for (XYChart.Series<String, Number> serie : listSeries) {
				lineChart.getData().add(serie);
			}
		}
	}

	/**
	 * C'est une m�thode FXML onAction du radio button humidit�, qui permet
	 * d'afficher le grahes des humidit�s des deux Mois en % en faisant appel �
	 * faisant appel �
	 * {@link app.com.utils.GraphicUtils#createGrapheHumidite(List, List, List, List, List, String, String)}.
	 */
	@FXML
	private void displayHumidite() {
		InitGraphiCompenents.disableCelsiusKelvin(radioKelvin, radioCelsius);
		lineChart.getData().clear();
		lineChart.setTitle("MOYENNE DES HUMIDITES (%)");
		lineChart.getYAxis().setLabel("Humidit� (%)");
		if (listRelevesMois1 != null && listRelevesMois2 != null) {
			List<XYChart.Series<String, Number>> listSeries = GraphicUtils.createGrapheHumidite(listRelevesMois1,
					listRelevesMois2, listRelevesEcartypeMois1, listRelevesEcartypeMois2, listRelevesDiff, dateAffich1,
					dateAffich2);
			for (XYChart.Series<String, Number> serie : listSeries) {
				lineChart.getData().add(serie);
			}
		}
	}

	/**
	 * C'est une m�thode FXML onAction du radio button nebulosit�, qui permet
	 * d'afficher les n�bulosit�s des deux Mois � comparer en % en faisant appel
	 * �
	 * {@link app.com.utils.GraphicUtils#createGrapheNebulosite(List, List, List, List, List, String, String)}.
	 */
	@FXML
	private void displayNebulosite() {
		InitGraphiCompenents.disableCelsiusKelvin(radioKelvin, radioCelsius);
		lineChart.getData().clear();
		lineChart.setTitle("MOYENNE DES NEBULOSITES (%)");
		lineChart.getYAxis().setLabel("Nebulosit� (%)");

		if (listRelevesMois1 != null && listRelevesMois2 != null) {
			List<XYChart.Series<String, Number>> listSeries = GraphicUtils.createGrapheNebulosite(listRelevesMois1,
					listRelevesMois2, listRelevesEcartypeMois1, listRelevesEcartypeMois2, listRelevesDiff, dateAffich1,
					dateAffich2);
			for (XYChart.Series<String, Number> serie : listSeries) {
				lineChart.getData().add(serie);
			}
		}
	}

	/**
	 * C'est une m�thode FXML onAction du radio button Celcius,qui permet
	 * d'afficher le grahes des temp�ratures des deux Mois degr�s Celcius en
	 * faisant appel �
	 * {@link app.com.utils.GraphicUtils#createGrapheTemperature(List, List, List, List, List, String, String, int, int)}.
	 *
	 */
	@FXML
	private void setToCelcius() {
		lineChart.getData().clear();
		lineChart.setTitle("MOYENNE DES TEMPERATURES (�C)");
		lineChart.getYAxis().setLabel("Temp�rature (�C)");
		if (listRelevesMois1 != null && listRelevesMois2 != null) {
			List<XYChart.Series<String, Number>> listSeries = GraphicUtils.createGrapheTemperature(listRelevesMois1,
					listRelevesMois2, listRelevesEcartypeMois1, listRelevesEcartypeMois2, listRelevesDiff, dateAffich1,
					dateAffich2, 0, 2);
			for (XYChart.Series<String, Number> serie : listSeries) {
				lineChart.getData().add(serie);
			}
		}

	}

	/**
	 * C'est une m�thode FXML onAction du radio button Kelvin,qui permet
	 * d'afficher le grahes des temp�ratures des deux Mois degr�s Kelvin en
	 * faisant appel �
	 * {@link app.com.utils.GraphicUtils#createGrapheTemperature(List, List, List, List, List, String, String, int, int)}.
	 *
	 */
	@FXML
	private void setToKelvin() {
		lineChart.getData().clear();
		lineChart.setTitle("MOYENNE DES TEMPERATURES (�K)");
		lineChart.getYAxis().setLabel("Temp�rature (�K)");
		if (listRelevesMois1 != null && listRelevesMois2 != null) {
			if (listRelevesMois1 != null && listRelevesMois2 != null) {
				List<XYChart.Series<String, Number>> listSeries = GraphicUtils.createGrapheTemperature(listRelevesMois1,
						listRelevesMois2, listRelevesEcartypeMois1, listRelevesEcartypeMois2, listRelevesDiff,
						dateAffich1, dateAffich2, 0, 1);
				for (XYChart.Series<String, Number> serie : listSeries) {
					lineChart.getData().add(serie);
				}
			}
		}

	}

	/**
	 * Cette m�thode permet de initialiser la comboMois selon l'ann�e choisi (si
	 * c'est l'ann�e actuelle c'est jusqu'au mois courant).
	 */
	@FXML
	private void popilateComboboxMois() {
		int curDate = ld.getYear();
		ObservableList<Integer> list = FXCollections.observableArrayList();
		if (comboAnnee1.getValue() == curDate || comboAnnee2.getValue() == curDate) {
			for (int i = 1; i <= ld.getMonthValue(); i++) {
				list.add(i);
			}
		} else {
			for (int i = 1; i <= 12; i++) {
				list.add(i);
			}
		}
		comboMois.setItems(list);
		comboMois.setValue(1);
	}

	/**
	 * Cette m�thode permet de tester la validation des champs du formulaire :
	 * l'utilisateur doit choisir deux ann�es diff�rentes et un mois a comparer.
	 *
	 * @return un boolean
	 */
	public boolean validChamps() {
		boolean isValide = true;
		if (comboAnnee1.getValue() != comboAnnee2.getValue()) {
			isValide = true;
		} else {
			isValide = false;
		}
		return isValide;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javafx.fxml.Initializable#initialize(java.net.URL,
	 * java.util.ResourceBundle)
	 */
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		InitGraphiCompenents.initComboboxStations(comboStations);
		InitGraphiCompenents.initComboboxAnnee(comboAnnee1);
		InitGraphiCompenents.initComboboxAnnee(comboAnnee2);
		InitGraphiCompenents.initComboboxMois(comboMois);
		InitGraphiCompenents.hideProgressingEemets(progBarre, progIndicator, progLabel);
		InitGraphiCompenents.disableCelsiusKelvin(radioKelvin, radioCelsius);
		InitGraphiCompenents.disableTempNebHum(btnTemp, btnHum, btnNeb);
		lineChart.getXAxis().setLabel("Jour");
	}

}
