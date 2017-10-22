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
 * Cette classe correspond au controlleur de la vue GraphCompareParAnnee.fxml.
 * Elle permet de d'afficher les courbes des donn�es a comparer pour deux ann�es
 * au choix.
 * 
 * @version 1.4
 * @author M'SILI Fatah.
 */
public class GraphicCompareParAnneeController implements Initializable {

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
	private ComboBox<Integer> comboAnnee1;
	@FXML
	private ComboBox<Integer> comboAnnee2;
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

	private List<Releve> listReleveAnnee1 = null;
	private List<Releve> listReleveAnnee2 = null;
	private List<Releve> listReleveAnneeEcart1 = null;
	private List<Releve> listReleveAnneeEcart2 = null;
	private List<Releve> listRelevesDiff = null;
	private String annee1 = null;
	private String annee2 = null;
	private Facade facade = new Facade();

	/**
	 * C'est la m�thode FXML onAction du boutton visualiser, elle permet de
	 * r�cup�rer la liste des relev�s deux ann�e � comparer et de les afficher
	 * sous forme de graphe et cel� en faisant appel �
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
			MessageUtil.errorDialog("Veuillez choisir deux ann�es diff�rentes !");
		}

	}

	/**
	 * C'est une tache JavaFX qui synchronise entre la progression de la
	 * progressbarre et la r�cup�ration des donn�es a � afficher � partir de la
	 * classe Fa�ade (liste des relev�s des deux ann�es en faisant appel �
	 * {@link app.com.facade.Facade#getDataYear(String, int)}, liste des relev�s
	 * ecart-type des deux annn�e en faisant appel �
	 * {@link app.com.facade.Facade#getDataEcarTypeYear(String, int)}, et liste
	 * des relev�s diff�rence entre les deux ann�es en faisant appel �
	 * {@link app.com.facade.Facade#getDifference(List, List)}.
	 * 
	 * @return null
	 */
	public Task<?> barreProgressing() {
		return new Task<String>() {
			@Override
			protected String call() throws Exception {
				String stationName = comboStations.getValue();
				annee1 = String.valueOf(comboAnnee1.getValue());
				annee2 = String.valueOf(comboAnnee2.getValue());
				updateMessage("T�l�chargement...");
				listReleveAnnee1 = facade.getDataYear(stationName, comboAnnee1.getValue(),1);
				listReleveAnnee2 = facade.getDataYear(stationName, comboAnnee2.getValue(),1);
				listReleveAnneeEcart1 = facade.getDataYear(stationName, comboAnnee1.getValue(),2);
				listReleveAnneeEcart2 = facade.getDataYear(stationName, comboAnnee2.getValue(),2);
				listRelevesDiff = facade.getDifference(listReleveAnnee1, listReleveAnnee2);
				int i = 0;
				Platform.runLater(new Runnable() {
					@Override
					public void run() {
						lineChart.getData().clear();
						lineChart.setTitle("MOYENNE DES TEMPERATURES (�K)");
						lineChart.getYAxis().setLabel("Temp�rature (�K)");

						if (listReleveAnnee1 != null && listReleveAnnee2 != null) {
							List<XYChart.Series<String, Number>> listSeries = GraphicUtils.createGrapheTemperature(
									listReleveAnnee1, listReleveAnnee2, listReleveAnneeEcart1, listReleveAnneeEcart2,
									listRelevesDiff, annee1, annee2, i, 1);
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
	 * d'afficher le grahes des temp�ratures des deux ann�es degr�s Kelvin en
	 * faisant appel �
	 * {@link app.com.utils.GraphicUtils#createGrapheTemperature(List, List, List, List, List, String, String, int, int)}.
	 */
	@FXML
	private void displayTemperature() {
		InitGraphiCompenents.enableCelsiusKelvin(radioKelvin, radioCelsius);
		lineChart.getData().clear();
		lineChart.setTitle("MOYENNE DES TEMPERATURES (�K)");
		lineChart.getYAxis().setLabel("Temp�rature (�K)");
		if (listReleveAnnee1 != null && listReleveAnnee2 != null) {
			List<XYChart.Series<String, Number>> listSeries = GraphicUtils.createGrapheTemperature(listReleveAnnee1,
					listReleveAnnee2, listReleveAnneeEcart1, listReleveAnneeEcart2, listRelevesDiff, annee1, annee2, 0,
					1);
			for (XYChart.Series<String, Number> serie : listSeries) {
				lineChart.getData().add(serie);
			}
		}
	}

	/**
	 * C'est une m�thode FXML onAction du radio button humidit�, qui permet
	 * d'afficher le grahes des humidit�s des deux ann�es en % en faisant appel
	 * � faisant appel �
	 * {@link app.com.utils.GraphicUtils#createGrapheHumidite(List, List, List, List, List, String, String)}.
	 */
	@FXML
	private void displayHumidite() {
		InitGraphiCompenents.disableCelsiusKelvin(radioKelvin, radioCelsius);
		lineChart.getData().clear();
		lineChart.setTitle("MOYENNE DES HUMIDITES (%)");
		lineChart.getYAxis().setLabel("Humidit� (%)");
		if (listReleveAnnee1 != null && listReleveAnnee2 != null) {
			if (listReleveAnnee1 != null && listReleveAnnee2 != null) {
				List<XYChart.Series<String, Number>> listSeries = GraphicUtils.createGrapheHumidite(listReleveAnnee1,
						listReleveAnnee2, listReleveAnneeEcart1, listReleveAnneeEcart2, listRelevesDiff, annee1,
						annee2);
				for (XYChart.Series<String, Number> serie : listSeries) {
					lineChart.getData().add(serie);
				}
			}
		}
	}

	/**
	 * C'est une m�thode FXML onAction du radio button nebulosit�, qui permet
	 * d'afficher les n�bulosit�s des deux ann�es � comparer en % en faisant
	 * appel �
	 * {@link app.com.utils.GraphicUtils#createGrapheNebulosite(List, List, List, List, List, String, String)}.
	 */
	@FXML
	private void displayNebulosite() {
		InitGraphiCompenents.disableCelsiusKelvin(radioKelvin, radioCelsius);
		lineChart.getData().clear();
		lineChart.setTitle("MOYENNE DES NEBULOSITES (%)");
		lineChart.getYAxis().setLabel("Nebulosit� (%)");

		if (listReleveAnnee1 != null && listReleveAnnee2 != null) {
			if (listReleveAnnee1 != null && listReleveAnnee2 != null) {
				List<XYChart.Series<String, Number>> listSeries = GraphicUtils.createGrapheNebulosite(listReleveAnnee1,
						listReleveAnnee2, listReleveAnneeEcart1, listReleveAnneeEcart2, listRelevesDiff, annee1,
						annee2);
				for (XYChart.Series<String, Number> serie : listSeries) {
					lineChart.getData().add(serie);
				}
			}
		}
	}

	/**
	 * C'est une m�thode FXML onAction du radio button Celcius,qui permet
	 * d'afficher le grahes des temp�ratures des deux ann�es degr�s Celcius en
	 * faisant appel �
	 * {@link app.com.utils.GraphicUtils#createGrapheTemperature(List, List, List, List, List, String, String, int, int)}.
	 *
	 */
	@FXML
	private void setToCelcius() {
		lineChart.getData().clear();
		lineChart.setTitle("MOYENNE DES TEMPERATURES (�C)");
		lineChart.getYAxis().setLabel("Temp�rature (�C)");
		if (listReleveAnnee1 != null && listReleveAnnee2 != null) {
			if (listReleveAnnee1 != null && listReleveAnnee2 != null) {
				List<XYChart.Series<String, Number>> listSeries = GraphicUtils.createGrapheTemperature(listReleveAnnee1,
						listReleveAnnee2, listReleveAnneeEcart1, listReleveAnneeEcart2, listRelevesDiff, annee1, annee2,
						0, 2);
				for (XYChart.Series<String, Number> serie : listSeries) {
					lineChart.getData().add(serie);
				}
			}
		}
	}

	/**
	 * C'est une m�thode FXML onAction du radio button Kelvin,qui permet
	 * d'afficher le grahes des temp�ratures des deux ann�es degr�s Kelvin en
	 * faisant appel �
	 * {@link app.com.utils.GraphicUtils#createGrapheTemperature(List, List, List, List, List, String, String, int, int)}.
	 *
	 */
	@FXML
	private void setToKelvin() {
		lineChart.getData().clear();
		lineChart.setTitle("MOYENNE DES TEMPERATURES (�K)");
		lineChart.getYAxis().setLabel("Temp�rature (�K)");

		if (listReleveAnnee1 != null && listReleveAnnee2 != null) {
			if (listReleveAnnee1 != null && listReleveAnnee2 != null) {
				List<XYChart.Series<String, Number>> listSeries = GraphicUtils.createGrapheTemperature(listReleveAnnee1,
						listReleveAnnee2, listReleveAnneeEcart1, listReleveAnneeEcart2, listRelevesDiff, annee1, annee2,
						0, 1);
				for (XYChart.Series<String, Number> serie : listSeries) {
					lineChart.getData().add(serie);
				}
			}
		}

	}

	/**
	 * Cette m�thode permet de tester la validation des champs du formulaire :
	 * l'utilisateur doit choisir deux ann�es diff�rentes a comparer.
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
		InitGraphiCompenents.hideProgressingEemets(progBarre, progIndicator, progLabel);
		InitGraphiCompenents.disableCelsiusKelvin(radioKelvin, radioCelsius);
		InitGraphiCompenents.disableTempNebHum(btnTemp, btnHum, btnNeb);
		lineChart.getXAxis().setLabel("Mois");
	}

}
