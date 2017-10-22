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
 * Elle correspond au controlleur de la vue GraphVisionParMois, Elle permet de
 * d'afficher les courbes des données du Mois au choix.
 * 
 * @version 1.5
 * @author M'SILI Fatah.
 */
public class GraphicVisionParMoisController implements Initializable {

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
	private ComboBox<Integer> comboAnnee;
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

	private List<Releve> listRelevesMois = null;
	private List<Releve> listRelevesEcartypeMois = null;
	private String dateAffich = null;
	private LocalDate ld = LocalDate.now();
	private Facade facade = new Facade();

	/**
	 * C'est la méthode FXML onAction du boutton visualiser, elle permet de
	 * récupérer la liste des relevés du mois et de les afficher sous forme de
	 * graphe et celà en faisant appel à {@link #barreProgressing()}.
	 * 
	 * @throws Exception
	 *             non traitée.
	 * @see {@link #barreProgressing()}
	 */
	@FXML
	private void visualiser() throws Exception {
		lineChart.getData().clear();
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

	}

	/**
	 * C'est une tache JavaFX qui synchronise entre la progression de la
	 * progressbarre et la récupération des données a à afficher à partir de la
	 * classe Façade (liste des relevés du Mois en faisant appel à
	 * {@link app.com.facade.Facade#getDataMonth(String, int, int)}, liste des
	 * relevés ecart-type du Mois en faisant appel à
	 * {@link app.com.facade.Facade#getDataEcarTypeMonth(String, int, int)}.
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
				dateAffich = month + "/" + comboAnnee.getValue();
				String stationName = comboStations.getValue();
				updateMessage("Téléchargement...");
				listRelevesMois = facade.getDataMonth(stationName, comboAnnee.getValue(), comboMois.getValue(),1);
				listRelevesEcartypeMois = facade.getDataMonth(stationName, comboAnnee.getValue(), comboMois.getValue(),2);
				Platform.runLater(new Runnable() {
					@Override
					public void run() {
						lineChart.getData().clear();
						lineChart.setTitle("MOYENNE DES TEMPERATURES (°K)");
						lineChart.getYAxis().setLabel("Température (°K)");
						int i = 0;
						if (listRelevesMois != null) {
							List<XYChart.Series<String, Number>> listSeries = GraphicUtils.createGrapheTemperature(
									listRelevesMois, null, listRelevesEcartypeMois, null, null, dateAffich, "", i, 1);
							if (!listSeries.isEmpty()) {
								for (XYChart.Series<String, Number> serie : listSeries) {
									updateProgress(i + 1, 1);
									lineChart.getData().add(serie);
								}
								InitGraphiCompenents.enableCelsiusKelvin(radioKelvin, radioCelsius);
								InitGraphiCompenents.enableTempNebHum(btnTemp, btnHum, btnNeb);
								updateMessage("Succès !");
							}
						} else {
							updateProgress(0, 0);
							InitGraphiCompenents.hideProgressingEemets(progBarre, progIndicator, progLabel);
							InitGraphiCompenents.disableCelsiusKelvin(radioKelvin, radioCelsius);
							InitGraphiCompenents.disableTempNebHum(btnTemp, btnHum, btnNeb);
							MessageUtil
									.errorDialog("Erreur de connexion au serveur ! vérifiez votre connexion internet.");
						}
					}
				});
				return null;
			}
		};
	}

	/**
	 * c'est une méthode FXML onAction du radio button température, qui permet
	 * d'afficher le grahes des températures du Mois degrés Kelvin en faisant
	 * appel à
	 * {@link app.com.utils.GraphicUtils#createGrapheTemperature(List, List, List, List, List, String, String, int, int)}.
	 */
	@FXML
	private void displayTemperature() {
		lineChart.getData().clear();
		lineChart.setTitle("MOYENNE DES TEMPERATURES (°K)");
		lineChart.getYAxis().setLabel("Température (°K)");
		InitGraphiCompenents.enableCelsiusKelvin(radioKelvin, radioCelsius);
		if (listRelevesMois != null) {
			List<XYChart.Series<String, Number>> listSeries = GraphicUtils.createGrapheTemperature(listRelevesMois,
					null, listRelevesEcartypeMois, null, null, dateAffich, "", 0, 1);
			for (XYChart.Series<String, Number> serie : listSeries) {
				lineChart.getData().add(serie);
			}
		}
	}

	/**
	 * C'est une méthode FXML onAction du radio button humidité, qui permet
	 * d'afficher le grahe des humidités du Mois en % en à faisant appel à
	 * {@link app.com.utils.GraphicUtils#createGrapheHumidite(List, List, List, List, List, String, String)}.
	 */
	@FXML
	private void displayHumidite() {
		lineChart.getData().clear();
		lineChart.setTitle("MOYENNE DES HUMIDITES (%)");
		lineChart.getYAxis().setLabel("Humidité (%)");
		InitGraphiCompenents.disableCelsiusKelvin(radioKelvin, radioCelsius);
		if (listRelevesMois != null) {
			List<XYChart.Series<String, Number>> listSeries = GraphicUtils.createGrapheHumidite(listRelevesMois, null,
					listRelevesEcartypeMois, null, null, dateAffich, "");
			for (XYChart.Series<String, Number> serie : listSeries) {
				lineChart.getData().add(serie);
			}

		}
	}

	/**
	 * C'est une méthode FXML onAction du radio button nebulosité, qui permet
	 * d'afficher les nébulosités du Mois à comparer en % en faisant appel à
	 * {@link app.com.utils.GraphicUtils#createGrapheNebulosite(List, List, List, List, List, String, String)}.
	 */
	@FXML
	private void displayNebulosite() {
		lineChart.getData().clear();
		lineChart.setTitle("MOYENNE DES NEBULOSITES (%)");
		lineChart.getYAxis().setLabel("Nebulosité (%)");
		InitGraphiCompenents.disableCelsiusKelvin(radioKelvin, radioCelsius);
		if (listRelevesMois != null) {
			if (listRelevesMois != null) {
				List<XYChart.Series<String, Number>> listSeries = GraphicUtils.createGrapheNebulosite(listRelevesMois,
						null, listRelevesEcartypeMois, null, null, dateAffich, "");
				for (XYChart.Series<String, Number> serie : listSeries) {
					lineChart.getData().add(serie);
				}

			}
		}
	}

	/**
	 * c'est une méthode FXML onAction du radio button température, qui permet
	 * d'afficher le grahes des températures du Mois en degrés Celcius en
	 * faisant appel à
	 * {@link app.com.utils.GraphicUtils#createGrapheTemperature(List, List, List, List, List, String, String, int, int)}.
	 */
	@FXML
	private void setToCelcius() {
		lineChart.getData().clear();
		lineChart.setTitle("MOYENNE DES TEMPERATURES (°C)");
		lineChart.getYAxis().setLabel("Température (°C)");

		if (listRelevesMois != null) {
			List<XYChart.Series<String, Number>> listSeries = GraphicUtils.createGrapheTemperature(listRelevesMois,
					null, listRelevesEcartypeMois, null, null, dateAffich, "", 0, 2);
			for (XYChart.Series<String, Number> serie : listSeries) {
				lineChart.getData().add(serie);
			}
		}
	}

	/**
	 * C'est une méthode FXML onAction du radio button Kelvin,qui permet
	 * d'afficher le grahes des températures du Mois en degrés Kelvin en faisant
	 * appel à
	 * {@link app.com.utils.GraphicUtils#createGrapheTemperature(List, List, List, List, List, String, String, int, int)}.
	 *
	 */
	@FXML
	private void setToKelvin() {
		lineChart.getData().clear();
		lineChart.setTitle("MOYENNE DES TEMPERATURES (°K)");
		lineChart.getYAxis().setLabel("Température (°K)");

		if (listRelevesMois != null) {
			List<XYChart.Series<String, Number>> listSeries = GraphicUtils.createGrapheTemperature(listRelevesMois,
					null, listRelevesEcartypeMois, null, null, dateAffich, "", 0, 1);
			for (XYChart.Series<String, Number> serie : listSeries) {
				lineChart.getData().add(serie);
			}
		}
	}

	/**
	 * Cette méthode permet de initialiser la comboMois selon l'année choisi (si
	 * c'est l'année actuelle c'est jusqu'au mois courant).
	 */
	@FXML
	private void popilateComboboxMois() {
		int curDate = ld.getYear();
		ObservableList<Integer> list = FXCollections.observableArrayList();
		if (comboAnnee.getValue() == curDate) {
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

	/*
	 * (non-Javadoc)
	 * 
	 * @see javafx.fxml.Initializable#initialize(java.net.URL,
	 * java.util.ResourceBundle)
	 */
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		InitGraphiCompenents.initComboboxStations(comboStations);
		InitGraphiCompenents.initComboboxAnnee(comboAnnee);
		InitGraphiCompenents.initComboboxMois(comboMois);
		InitGraphiCompenents.hideProgressingEemets(progBarre, progIndicator, progLabel);
		InitGraphiCompenents.disableCelsiusKelvin(radioKelvin, radioCelsius);
		InitGraphiCompenents.disableTempNebHum(btnTemp, btnHum, btnNeb);
		lineChart.getXAxis().setLabel("Jour");
	}

}
