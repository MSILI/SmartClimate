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
 * Elle permet de d'afficher les courbes des donn�es a comparer pour deux jours
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
	 * C'est la m�thode FXML onAction du boutton visualiser, elle permet de
	 * r�cup�rer la liste des relev�s des deux jours � comparer et de les
	 * afficher sous forme de graphe et cel� en faisant appel �
	 * {@link #barreProgressing()}.
	 * 
	 * @throws Exception
	 *             non trait�e.
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
			MessageUtil.errorDialog("Veuillez choisir un jour avec un m�me jour d'une autre ann�e !");
		}

	}

	/**
	 * C'est une tache JavaFX qui synchronise entre la progression de la
	 * progressbarre et la r�cup�ration des donn�es a � afficher � partir de la
	 * classe Fa�ade (liste des relev�s des deux ann�es en faisant appel �
	 * {@link app.com.facade.Facade#getDataDay(String, int, int, int)}, liste
	 * des relev�s diff�rence entre les deux jours en faisant appel �
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
				updateMessage("T�l�chargement...");
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
						lineChart.setTitle("MOYENNE DES TEMPERATURES (�K)");
						lineChart.getYAxis().setLabel("Temp�rature (�K)");

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
	 * d'afficher le grahes des temp�ratures des deux jours en degr�s Kelvin en
	 * faisant appel �
	 * {@link app.com.utils.GraphicUtils#createGrapheTemperature(List, List, List, List, List, String, String, int, int)}.
	 */
	@FXML
	private void displayTemperature() {
		InitGraphiCompenents.enableCelsiusKelvin(radioKelvin, radioCelsius);
		lineChart.getData().clear();
		lineChart.setTitle("MOYENNE DES TEMPERATURES (�K)");
		lineChart.getYAxis().setLabel("Temp�rature (�K)");

		if (listRelevesJour1 != null && listRelevesJour2 != null) {
			List<XYChart.Series<String, Number>> listSeries = GraphicUtils.createGrapheTemperature(listRelevesJour1,
					listRelevesJour2, null, null, listRelevesDiff, dateAffich1, dateAffich2, 0, 1);
			for (XYChart.Series<String, Number> serie : listSeries) {
				lineChart.getData().add(serie);
			}
		}
	}

	/**
	 * C'est une m�thode FXML onAction du radio button humidit�, qui permet
	 * d'afficher le grahes des humidit�s des deux jours en % en faisant appel �
	 * faisant appel �
	 * {@link app.com.utils.GraphicUtils#createGrapheHumidite(List, List, List, List, List, String, String)}.
	 */
	@FXML
	private void displayHumidite() {
		InitGraphiCompenents.disableCelsiusKelvin(radioKelvin, radioCelsius);
		lineChart.getData().clear();
		lineChart.setTitle("MOYENNE DES HUMIDITES (%)");
		lineChart.getYAxis().setLabel("Humidit� (%)");

		if (listRelevesJour1 != null && listRelevesJour2 != null) {
			List<XYChart.Series<String, Number>> listSeries = GraphicUtils.createGrapheHumidite(listRelevesJour1,
					listRelevesJour2, null, null, listRelevesDiff, dateAffich1, dateAffich2);
			for (XYChart.Series<String, Number> serie : listSeries) {
				lineChart.getData().add(serie);
			}
		}

	}

	/**
	 * C'est une m�thode FXML onAction du radio button nebulosit�, qui permet
	 * d'afficher les n�bulosit�s des deux jours � comparer en % en faisant
	 * appel �
	 * {@link app.com.utils.GraphicUtils#createGrapheNebulosite(List, List, List, List, List, String, String)}.
	 */
	@FXML
	private void displayNebulosite() {
		InitGraphiCompenents.disableCelsiusKelvin(radioKelvin, radioCelsius);
		lineChart.getData().clear();
		lineChart.setTitle("MOYENNE DES NEBULOSITES (%)");
		lineChart.getYAxis().setLabel("Nebulosit� (%)");
		if (listRelevesJour1 != null && listRelevesJour2 != null) {
			List<XYChart.Series<String, Number>> listSeries = GraphicUtils.createGrapheNebulosite(listRelevesJour1,
					listRelevesJour2, null, null, listRelevesDiff, dateAffich1, dateAffich2);
			for (XYChart.Series<String, Number> serie : listSeries) {
				lineChart.getData().add(serie);
			}
		}

	}

	/**
	 * C'est une m�thode FXML onAction du radio button Celcius,qui permet
	 * d'afficher le grahes des temp�ratures des deux jours degr�s Celcius en
	 * faisant appel �
	 * {@link app.com.utils.GraphicUtils#createGrapheTemperature(List, List, List, List, List, String, String, int, int)}.
	 *
	 */
	@FXML
	private void setToCelcius() {
		lineChart.getData().clear();
		lineChart.setTitle("MOYENNE DES TEMPERATURES (�C)");
		lineChart.getYAxis().setLabel("Temp�rature (�C)");
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
	 * C'est une m�thode FXML onAction du radio button Kelvin,qui permet
	 * d'afficher le grahes des temp�ratures des deux jours degr�s Kelvin en
	 * faisant appel �
	 * {@link app.com.utils.GraphicUtils#createGrapheTemperature(List, List, List, List, List, String, String, int, int)}.
	 *
	 */
	@FXML
	private void setToKelvin() {
		lineChart.getData().clear();
		lineChart.setTitle("MOYENNE DES TEMPERATURES (�K)");
		lineChart.getYAxis().setLabel("Temp�rature (�K)");
		if (listRelevesJour1 != null && listRelevesJour2 != null) {
			List<XYChart.Series<String, Number>> listSeries = GraphicUtils.createGrapheTemperature(listRelevesJour1,
					listRelevesJour2, null, null, listRelevesDiff, dateAffich1, dateAffich2, 0, 2);
			for (XYChart.Series<String, Number> serie : listSeries) {
				lineChart.getData().add(serie);
			}
		}
	}

	/**
	 * Cette m�thode permet de tester la validation des champs du formulaire :
	 * l'utilisateur doit choisir deux jours et mois pereils et deux ann�es
	 * diff�rentes a comparer.
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
		lineChart.getXAxis().setLabel("Relev�(h)");
	}

}
