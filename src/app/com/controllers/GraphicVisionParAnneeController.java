package app.com.controllers;

import java.net.URL;
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
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.RadioButton;
import javafx.scene.control.ToggleGroup;

/**
 * Cette classe correspond au controlleur de la vue GraphVisionParAnnee.fxml,
 * Elle permet de d'afficher les courbes des données d'une année au choix.
 * 
 * @version 1.6
 * @author M'SILI Fatah.
 */
public class GraphicVisionParAnneeController implements Initializable {

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
	private DatePicker datePicker;
	@FXML
	private ComboBox<String> comboStations;
	@FXML
	private ComboBox<Integer> comboAnnee;
	@FXML
	private LineChart<String, Number> lineChart;
	@FXML
	private Label anneLabel;
	@FXML
	private ProgressBar progBarre;
	@FXML
	private ProgressIndicator progIndicator;
	@FXML
	private Label progLabel;
	private List<Releve> listReleveAnnee = null;
	private List<Releve> listReleveAnneeEcart = null;
	private String annee = null;
	private Facade facade = new Facade();

	/**
	 * C'est la méthode FXML onAction du boutton visualiser, elle permet de
	 * récupérer la liste des relevés de l'année et de les afficher sous forme
	 * de graphe et celà en faisant appel à {@link #barreProgressing()}.
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
	 * classe Façade (liste des relevés des de l'année en faisant appel à
	 * {@link app.com.facade.Facade#getDataYear(String, int)}, liste des relevés
	 * ecart-type de l'année en faisant appel à
	 * {@link app.com.facade.Facade#getDataEcarTypeYear(String, int)}.
	 * 
	 * @return null
	 */
	public Task<?> barreProgressing() {
		return new Task<String>() {
			@Override
			protected String call() throws Exception {
				String stationName = comboStations.getValue();
				annee = String.valueOf(comboAnnee.getValue());
				updateMessage("Téléchargement...");
				listReleveAnnee = facade.getDataYear(stationName, comboAnnee.getValue(), 1);
				listReleveAnneeEcart = facade.getDataYear(stationName, comboAnnee.getValue(), 2);
				Platform.runLater(new Runnable() {
					@Override
					public void run() {
						int i = 0;
						lineChart.getData().clear();
						lineChart.setTitle("MOYENNE DES TEMPERATURES (°K)");
						lineChart.getYAxis().setLabel("Température (°K)");

						if (listReleveAnnee != null) {
							List<XYChart.Series<String, Number>> listSeries = GraphicUtils.createGrapheTemperature(
									listReleveAnnee, null, listReleveAnneeEcart, null, null, annee, "", i, 1);
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
	 * d'afficher le grahes des températures de l'année en degrés Kelvin en
	 * faisant appel à
	 * {@link app.com.utils.GraphicUtils#createGrapheTemperature(List, List, List, List, List, String, String, int, int)}.
	 */
	@FXML
	private void displayTemperature() {
		InitGraphiCompenents.enableCelsiusKelvin(radioKelvin, radioCelsius);
		lineChart.getData().clear();
		lineChart.setTitle("MOYENNE DES TEMPERATURES (°K)");
		lineChart.getYAxis().setLabel("Température (°K)");

		if (listReleveAnnee != null) {
			List<XYChart.Series<String, Number>> listSeries = GraphicUtils.createGrapheTemperature(listReleveAnnee,
					null, listReleveAnneeEcart, null, null, annee, "", 0, 1);
			for (XYChart.Series<String, Number> serie : listSeries) {
				lineChart.getData().add(serie);
			}
		}
	}

	/**
	 * C'est une méthode FXML onAction du radio button humidité, qui permet
	 * d'afficher le grahes des humidités de l'année en % en faisant appel à
	 * faisant appel à
	 * {@link app.com.utils.GraphicUtils#createGrapheHumidite(List, List, List, List, List, String, String)}.
	 */
	@FXML
	private void displayHumidite() {
		lineChart.getData().clear();
		lineChart.setTitle("MOYENNE DES HUMIDITES (%)");
		lineChart.getYAxis().setLabel("Humidité (%)");
		InitGraphiCompenents.disableCelsiusKelvin(radioKelvin, radioCelsius);
		if (listReleveAnnee != null) {
			List<XYChart.Series<String, Number>> listSeries = GraphicUtils.createGrapheHumidite(listReleveAnnee, null,
					listReleveAnneeEcart, null, null, annee, "");
			for (XYChart.Series<String, Number> serie : listSeries) {
				lineChart.getData().add(serie);
			}
		}
	}

	/**
	 * C'est une méthode FXML onAction du radio button nebulosité, qui permet
	 * d'afficher les nébulosités de l'année en % en faisant appel à
	 * {@link app.com.utils.GraphicUtils#createGrapheNebulosite(List, List, List, List, List, String, String)}.
	 */
	@FXML
	private void displayNebulosite() {
		lineChart.getData().clear();
		lineChart.setTitle("MOYENNE DES NEBULOSITES (%)");
		lineChart.getYAxis().setLabel("Nebulosité (%)");
		InitGraphiCompenents.disableCelsiusKelvin(radioKelvin, radioCelsius);
		if (listReleveAnnee != null) {
			if (listReleveAnnee != null) {
				List<XYChart.Series<String, Number>> listSeries = GraphicUtils.createGrapheNebulosite(listReleveAnnee,
						null, listReleveAnneeEcart, null, null, annee, "");
				for (XYChart.Series<String, Number> serie : listSeries) {
					lineChart.getData().add(serie);
				}
			}
		}
	}

	/**
	 * C'est une méthode FXML onAction du radio button Celcius,qui permet
	 * d'afficher le grahes des températures de l'année en degrés Celcius en
	 * faisant appel à
	 * {@link app.com.utils.GraphicUtils#createGrapheTemperature(List, List, List, List, List, String, String, int, int)}.
	 *
	 */
	@FXML
	private void setToCelcius() {
		lineChart.getData().clear();
		lineChart.setTitle("MOYENNE DES TEMPERATURES (°C)");
		lineChart.getYAxis().setLabel("Température (°C)");

		if (listReleveAnnee != null) {
			if (listReleveAnnee != null) {
				List<XYChart.Series<String, Number>> listSeries = GraphicUtils.createGrapheTemperature(listReleveAnnee,
						null, listReleveAnneeEcart, null, null, annee, "", 0, 2);
				for (XYChart.Series<String, Number> serie : listSeries) {
					lineChart.getData().add(serie);
				}
			}
		}
	}

	/**
	 * c'est une méthode FXML onAction du radio button température, qui permet
	 * d'afficher le grahes des températures de l'année en degrés Kelvin en
	 * faisant appel à
	 * {@link app.com.utils.GraphicUtils#createGrapheTemperature(List, List, List, List, List, String, String, int, int)}.
	 */
	@FXML
	private void setToKelvin() {
		lineChart.getData().clear();
		lineChart.setTitle("MOYENNE DES TEMPERATURES (°K)");
		lineChart.getYAxis().setLabel("Température (°K)");

		if (listReleveAnnee != null) {
			if (listReleveAnnee != null) {
				List<XYChart.Series<String, Number>> listSeries = GraphicUtils.createGrapheTemperature(listReleveAnnee,
						null, listReleveAnneeEcart, null, null, annee, "", 0, 1);
				for (XYChart.Series<String, Number> serie : listSeries) {
					lineChart.getData().add(serie);
				}
			}
		}
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
		InitGraphiCompenents.hideProgressingEemets(progBarre, progIndicator, progLabel);
		InitGraphiCompenents.disableCelsiusKelvin(radioKelvin, radioCelsius);
		InitGraphiCompenents.disableTempNebHum(btnTemp, btnHum, btnNeb);
		lineChart.getXAxis().setLabel("Mois");
	}

}
