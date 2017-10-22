package app.com.controllers;

import java.net.URL;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
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
 * Cette classe correspond au controlleur de la vue GraphCompareParJour.fxml.
 * Elle permet de d'afficher les courbes des données a comparer pour deux jours
 * au choix.
 * 
 * @version 1.5
 * @author M'SILI Fatah.
 */
public class GraphicCompareParJourController implements Initializable {

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
	private DatePicker datePicker1;
	@FXML
	private DatePicker datePicker2;
	@FXML
	private ComboBox<String> comboStations;
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

	private List<Releve> listRelevesJour1 = null;
	private List<Releve> listRelevesJour2 = null;
	private List<Releve> listRelevesDiff = null;
	private String dateAffich1 = null;
	private String dateAffich2 = null;
	private Facade facade = new Facade();

	/**
	 * C'est la méthode FXML onAction du boutton visualiser, elle permet de
	 * récupérer la liste des relevés des deux jours à comparer et de les
	 * afficher sous forme de graphe et celà en faisant appel à
	 * {@link #barreProgressing()}.
	 * 
	 * @throws Exception
	 *             non traitée.
	 * @see {@link #barreProgressing()}
	 */
	@FXML
	private void visualiserAction() throws Exception {
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
			MessageUtil.errorDialog("Veuillez choisir un jour avec un même jour d'une autre année !");
		}

	}

	/**
	 * C'est une tache JavaFX qui synchronise entre la progression de la
	 * progressbarre et la récupération des données a à afficher à partir de la
	 * classe Façade (liste des relevés des deux années en faisant appel à
	 * {@link app.com.facade.Facade#getDataDay(String, int, int, int)}, liste
	 * des relevés différence entre les deux jours en faisant appel à
	 * {@link app.com.facade.Facade#getDifference(List, List)}.
	 * 
	 * @return null
	 */
	public Task<?> barreProgressing() {
		return new Task<String>() {
			@Override
			protected String call() throws Exception {
				LocalDate localDate1 = datePicker1.getValue();
				LocalDate localDate2 = datePicker2.getValue();
				DateTimeFormatter dateFormat2 = DateTimeFormatter.ofPattern("dd/MM/yyyy");
				dateAffich1 = localDate1.format(dateFormat2);
				dateAffich2 = localDate2.format(dateFormat2);
				String stationName = comboStations.getValue();
				updateMessage("Téléchargement...");
				listRelevesJour1 = facade.getDataDay(stationName, localDate1.getYear(), localDate1.getMonthValue(),
						localDate1.getDayOfMonth());
				listRelevesJour2 = facade.getDataDay(stationName, localDate2.getYear(), localDate2.getMonthValue(),
						localDate2.getDayOfMonth());
				listRelevesDiff = facade.getDifference(listRelevesJour1, listRelevesJour2);
				int i = 0;
				Platform.runLater(new Runnable() {
					@Override
					public void run() {

						lineChart.getData().clear();
						lineChart.setTitle("MOYENNE DES TEMPERATURES (°K)");
						lineChart.getYAxis().setLabel("Température (°K)");

						if (listRelevesJour2 != null && listRelevesJour2 != null) {
							List<XYChart.Series<String, Number>> listSeries = GraphicUtils.createGrapheTemperature(
									listRelevesJour1, listRelevesJour2, null, null, listRelevesDiff, dateAffich1,
									dateAffich2, i, 1);
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
	 * d'afficher le grahes des températures des deux jours en degrés Kelvin en
	 * faisant appel à
	 * {@link app.com.utils.GraphicUtils#createGrapheTemperature(List, List, List, List, List, String, String, int, int)}.
	 */
	@FXML
	private void displayTemperature() {
		InitGraphiCompenents.enableCelsiusKelvin(radioKelvin, radioCelsius);
		lineChart.getData().clear();
		lineChart.setTitle("MOYENNE DES TEMPERATURES (°K)");
		lineChart.getYAxis().setLabel("Température (°K)");

		if (listRelevesJour1 != null && listRelevesJour2 != null) {
			List<XYChart.Series<String, Number>> listSeries = GraphicUtils.createGrapheTemperature(listRelevesJour1,
					listRelevesJour2, null, null, listRelevesDiff, dateAffich1, dateAffich2, 0, 1);
			for (XYChart.Series<String, Number> serie : listSeries) {
				lineChart.getData().add(serie);
			}
		}
	}

	/**
	 * C'est une méthode FXML onAction du radio button humidité, qui permet
	 * d'afficher le grahes des humidités des deux jours en % en faisant appel à
	 * faisant appel à
	 * {@link app.com.utils.GraphicUtils#createGrapheHumidite(List, List, List, List, List, String, String)}.
	 */
	@FXML
	private void displayHumidite() {
		InitGraphiCompenents.disableCelsiusKelvin(radioKelvin, radioCelsius);
		lineChart.getData().clear();
		lineChart.setTitle("MOYENNE DES HUMIDITES (%)");
		lineChart.getYAxis().setLabel("Humidité (%)");

		if (listRelevesJour1 != null && listRelevesJour2 != null) {
			List<XYChart.Series<String, Number>> listSeries = GraphicUtils.createGrapheHumidite(listRelevesJour1,
					listRelevesJour2, null, null, listRelevesDiff, dateAffich1, dateAffich2);
			for (XYChart.Series<String, Number> serie : listSeries) {
				lineChart.getData().add(serie);
			}
		}

	}

	/**
	 * C'est une méthode FXML onAction du radio button nebulosité, qui permet
	 * d'afficher les nébulosités des deux jours à comparer en % en faisant
	 * appel à
	 * {@link app.com.utils.GraphicUtils#createGrapheNebulosite(List, List, List, List, List, String, String)}.
	 */
	@FXML
	private void displayNebulosite() {
		InitGraphiCompenents.disableCelsiusKelvin(radioKelvin, radioCelsius);
		lineChart.getData().clear();
		lineChart.setTitle("MOYENNE DES NEBULOSITES (%)");
		lineChart.getYAxis().setLabel("Nebulosité (%)");
		if (listRelevesJour1 != null && listRelevesJour2 != null) {
			List<XYChart.Series<String, Number>> listSeries = GraphicUtils.createGrapheNebulosite(listRelevesJour1,
					listRelevesJour2, null, null, listRelevesDiff, dateAffich1, dateAffich2);
			for (XYChart.Series<String, Number> serie : listSeries) {
				lineChart.getData().add(serie);
			}
		}

	}

	/**
	 * C'est une méthode FXML onAction du radio button Celcius,qui permet
	 * d'afficher le grahes des températures des deux jours degrés Celcius en
	 * faisant appel à
	 * {@link app.com.utils.GraphicUtils#createGrapheTemperature(List, List, List, List, List, String, String, int, int)}.
	 *
	 */
	@FXML
	private void setToCelcius() {
		lineChart.getData().clear();
		lineChart.setTitle("MOYENNE DES TEMPERATURES (°C)");
		lineChart.getYAxis().setLabel("Température (°C)");
		if (listRelevesJour1 != null && listRelevesJour2 != null) {
			if (listRelevesJour1 != null && listRelevesJour2 != null) {
				List<XYChart.Series<String, Number>> listSeries = GraphicUtils.createGrapheTemperature(listRelevesJour1,
						listRelevesJour2, null, null, listRelevesDiff, dateAffich1, dateAffich2, 0, 2);
				for (XYChart.Series<String, Number> serie : listSeries) {
					lineChart.getData().add(serie);
				}
			}
		}
	}

	/**
	 * C'est une méthode FXML onAction du radio button Kelvin,qui permet
	 * d'afficher le grahes des températures des deux jours degrés Kelvin en
	 * faisant appel à
	 * {@link app.com.utils.GraphicUtils#createGrapheTemperature(List, List, List, List, List, String, String, int, int)}.
	 *
	 */
	@FXML
	private void setToKelvin() {
		lineChart.getData().clear();
		lineChart.setTitle("MOYENNE DES TEMPERATURES (°K)");
		lineChart.getYAxis().setLabel("Température (°K)");
		if (listRelevesJour1 != null && listRelevesJour2 != null) {
			List<XYChart.Series<String, Number>> listSeries = GraphicUtils.createGrapheTemperature(listRelevesJour1,
					listRelevesJour2, null, null, listRelevesDiff, dateAffich1, dateAffich2, 0, 2);
			for (XYChart.Series<String, Number> serie : listSeries) {
				lineChart.getData().add(serie);
			}
		}
	}

	/**
	 * Cette méthode permet de tester la validation des champs du formulaire :
	 * l'utilisateur doit choisir deux jours et mois pereils et deux années
	 * différentes a comparer.
	 *
	 * @return
	 */
	public boolean validChamps() {
		boolean isValide = true;
		LocalDate localDate1 = datePicker1.getValue();
		LocalDate localDate2 = datePicker2.getValue();
		if (localDate1.getYear() != localDate2.getYear()) {
			if (localDate1.getMonthValue() == localDate2.getMonthValue()) {
				if (localDate1.getDayOfMonth() == localDate2.getDayOfMonth()) {
					isValide = true;
				} else {
					isValide = false;
				}
			} else {
				isValide = false;
			}
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
		InitGraphiCompenents.disableDates(datePicker1);
		InitGraphiCompenents.disableDates(datePicker2);
		InitGraphiCompenents.initComboboxStations(comboStations);
		InitGraphiCompenents.hideProgressingEemets(progBarre, progIndicator, progLabel);
		InitGraphiCompenents.disableCelsiusKelvin(radioKelvin, radioCelsius);
		InitGraphiCompenents.disableTempNebHum(btnTemp, btnHum, btnNeb);
		lineChart.getXAxis().setLabel("Relevé(h)");
	}

}
