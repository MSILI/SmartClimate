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
 * Elle correspond au controlleur de la vue GraphVisionParJour.fxml, Elle permet
 * de d'afficher les courbes des donn�es un jour au choix.
 *
 * @version 1.1
 * @author M'SILI Fatah.
 */
public class GraphicVisionParJourController implements Initializable {

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
	private LineChart<String, Number> lineChart;
	@FXML
	private Label anneLabel;
	@FXML
	private ProgressBar progBarre;
	@FXML
	private ProgressIndicator progIndicator;
	@FXML
	private Label progLabel;
	private List<Releve> listRelevesJour = null;
	private String dateAffich = null;

	/**
	 * C'est la m�thode FXML onAction du boutton visualiser, elle permet de
	 * r�cup�rer la liste des relev�s du jour et de les afficher sous forme de
	 * graphe et cel� en faisant appel � {@link #barreProgressing()}.
	 * 
	 * @throws Exception
	 *             non trait�e.
	 * @see {@link #barreProgressing()}
	 */
	@FXML
	private void visualiserAction() throws Exception {
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
	 * progressbarre et la r�cup�ration des donn�es a � afficher � partir de la
	 * classe Fa�ade (liste des relev�s du jour en faisant appel �
	 * {@link app.com.facade.Facade#getDataDay(String, int, int, int)}.
	 * 
	 * @return null
	 */
	public Task<?> barreProgressing() {
		return new Task<String>() {
			@Override
			protected String call() throws Exception {
				LocalDate localDate = datePicker.getValue();
				String stationName = comboStations.getValue();
				DateTimeFormatter dateFormatAffich = DateTimeFormatter.ofPattern("dd/MM/yyyy");
				dateAffich = localDate.format(dateFormatAffich);
				Facade facade = new Facade();
				updateMessage("T�l�chargement...");
				listRelevesJour = facade.getDataDay(stationName, localDate.getYear(), localDate.getMonthValue(),
						localDate.getDayOfMonth());
				Platform.runLater(new Runnable() {
					@Override
					public void run() {
						lineChart.getData().clear();
						lineChart.setTitle("MOYENNE DES TEMPERATURES (�K)");
						lineChart.getYAxis().setLabel("Temp�rature (�K)");
						int i = 0;
						if (listRelevesJour != null) {
							List<XYChart.Series<String, Number>> listSeries = GraphicUtils.createGrapheTemperature(
									listRelevesJour, null, null, null, null, dateAffich, "", i, 1);
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
	 * d'afficher le grahe des temp�ratures du jour en degr�s Kelvin en
	 * faisant appel �
	 * {@link app.com.utils.GraphicUtils#createGrapheTemperature(List, List, List, List, List, String, String, int, int)}.
	 */
	@FXML
	private void displayTemperature() {
		lineChart.getData().clear();
		lineChart.setTitle("TEMPERATURE (�K)");
		lineChart.getYAxis().setLabel("Temp�rature (�K)");
		InitGraphiCompenents.enableCelsiusKelvin(radioKelvin, radioCelsius);
		if (listRelevesJour != null) {
			List<XYChart.Series<String, Number>> listSeries = GraphicUtils.createGrapheTemperature(listRelevesJour,
					null, null, null, null, dateAffich, "", 0, 1);
			for (XYChart.Series<String, Number> serie : listSeries) {
				lineChart.getData().add(serie);
			}
		}
	}

	/**
	 * C'est une m�thode FXML onAction du radio button humidit�, qui permet
	 * d'afficher le grahes des humidit�s du jour en % en faisant appel �
	 * faisant appel �
	 * {@link app.com.utils.GraphicUtils#createGrapheHumidite(List, List, List, List, List, String, String)}.
	 */
	@FXML
	private void displayHumidite() {
		lineChart.getData().clear();
		lineChart.setTitle("HUMIDITE (%)");
		lineChart.getYAxis().setLabel("Humidit� (%)");
		InitGraphiCompenents.disableCelsiusKelvin(radioKelvin, radioCelsius);
		if (listRelevesJour != null) {
			if (listRelevesJour != null) {
				List<XYChart.Series<String, Number>> listSeries = GraphicUtils.createGrapheHumidite(listRelevesJour,
						null, null, null, null, dateAffich, "");
				for (XYChart.Series<String, Number> serie : listSeries) {
					lineChart.getData().add(serie);
				}
			}
		}
	}

	/**
	 * C'est une m�thode FXML onAction du radio button nebulosit�, qui permet
	 * d'afficher les n�bulosit�s du jour en % en faisant
	 * appel �
	 * {@link app.com.utils.GraphicUtils#createGrapheNebulosite(List, List, List, List, List, String, String)}.
	 */
	@FXML
	private void displayNebulosite() {
		lineChart.getData().clear();
		lineChart.setTitle("NEBULOSITE (%)");
		lineChart.getYAxis().setLabel("Nebulosit� (%)");
		XYChart.Series<String, Number> nebSeries = new XYChart.Series<String, Number>();
		InitGraphiCompenents.disableCelsiusKelvin(radioKelvin, radioCelsius);
		nebSeries.setName("N�bulosit� " + dateAffich);
		if (listRelevesJour != null) {
			List<XYChart.Series<String, Number>> listSeries = GraphicUtils.createGrapheNebulosite(listRelevesJour, null,
					null, null, null, dateAffich, "");
			for (XYChart.Series<String, Number> serie : listSeries) {
				lineChart.getData().add(serie);
			}
		}
	}

	/**
	 * c'est une m�thode FXML onAction du radio button temp�rature, qui permet
	 * d'afficher le grahe des temp�ratures du jour en degr�s Celcius en
	 * faisant appel �
	 * {@link app.com.utils.GraphicUtils#createGrapheTemperature(List, List, List, List, List, String, String, int, int)}.
	 */
	@FXML
	private void setToCelcius() {
		lineChart.getData().clear();
		lineChart.setTitle("TEMPERATURE (�C)");
		lineChart.getYAxis().setLabel("Temp�rature (�C)");
		XYChart.Series<String, Number> tempSeries = new XYChart.Series<String, Number>();
		tempSeries.setName("Temp�rature " + dateAffich);
		if (listRelevesJour != null) {
			List<XYChart.Series<String, Number>> listSeries = GraphicUtils.createGrapheTemperature(listRelevesJour,
					null, null, null, null, dateAffich, "", 0, 2);
			for (XYChart.Series<String, Number> serie : listSeries) {
				lineChart.getData().add(serie);
			}
		}
	}

	/**
	 * c'est une m�thode FXML onAction du radio button temp�rature, qui permet
	 * d'afficher le grahe des temp�ratures du jour en degr�s Kelvin en
	 * faisant appel �
	 * {@link app.com.utils.GraphicUtils#createGrapheTemperature(List, List, List, List, List, String, String, int, int)}.
	 */
	@FXML
	private void setToKelvin() {
		lineChart.getData().clear();
		lineChart.setTitle("TEMPERATURE (�K)");
		lineChart.getYAxis().setLabel("Temp�rature (�K)");
		XYChart.Series<String, Number> tempSeries = new XYChart.Series<String, Number>();
		tempSeries.setName("Temp�rature " + dateAffich);
		if (listRelevesJour != null) {
			List<XYChart.Series<String, Number>> listSeries = GraphicUtils.createGrapheTemperature(listRelevesJour,
					null, null, null, null, dateAffich, "", 0, 1);
			for (XYChart.Series<String, Number> serie : listSeries) {
				lineChart.getData().add(serie);
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
		InitGraphiCompenents.disableDates(datePicker);
		InitGraphiCompenents.initComboboxStations(comboStations);
		InitGraphiCompenents.hideProgressingEemets(progBarre, progIndicator, progLabel);
		InitGraphiCompenents.disableCelsiusKelvin(radioKelvin, radioCelsius);
		InitGraphiCompenents.disableTempNebHum(btnTemp, btnHum, btnNeb);
		lineChart.getXAxis().setLabel("Relev�(h)");
	}

}
