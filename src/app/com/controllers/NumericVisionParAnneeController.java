package app.com.controllers;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

import app.com.facade.Facade;
import app.com.model.Releve;
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
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.ToggleGroup;

/**
 * cette classe correspond au controlleur de la vue NumVisionParAnnee, cette
 * classe contient les différentes méthodes qu'utilisent les différents
 * composant de cette dernière.
 * 
 * @version 1.3
 * @author M'SILI Fatah.
 *
 */
public class NumericVisionParAnneeController implements Initializable {

	@FXML
	private DatePicker datePicker;
	@FXML
	private ComboBox<String> comboStations;
	@FXML
	private TableColumn<Releve, Integer> dateColumn;
	@FXML
	private TableColumn<Releve, Float> tempColumn;
	@FXML
	private TableColumn<Releve, Float> humColumn;
	@FXML
	private TableColumn<Releve, Float> nubColumn;
	@FXML
	private TableView<Releve> tableView;
	@FXML
	private ComboBox<Integer> comboAnnee;
	@FXML
	private ToggleGroup groupeR;
	@FXML
	private RadioButton radioKelvin;
	@FXML
	private RadioButton radioCelsius;
	@FXML
	private Label anneLabel;
	@FXML
	private ProgressBar progBarre;
	@FXML
	private ProgressIndicator progIndicator;
	@FXML
	private Label progLabel;
	@FXML
	private Label dateLabel;

	ObservableList<Releve> listObsReleve = FXCollections.observableArrayList();
	private List<Releve> listReleveAnnee = null;
	private String annee = null;
	private Facade facade = new Facade();

	/**
	 * Cette méthode c'est la méthode FXML onAction du boutton visualiser.
	 * 
	 * @see {@link #barreProgressing()}
	 * @throws Exception
	 */
	@FXML
	private void visualiser() throws Exception {
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
	 * barreProgressing() est une tache JavaFX qui synchronise entre la
	 * progression de la progressbarre et la récupération des données a à
	 * afficher à partir de la classe Façade.
	 * 
	 * @see {@link app.com.facade.Facade#getDataYear(String, String)}
	 * @see {@link app.com.utils.Utils#isErreur()}
	 */
	public Task<?> barreProgressing() {
		return new Task<String>() {
			@Override
			protected String call() throws Exception {
				String stationName = comboStations.getValue();
				annee = String.valueOf(comboAnnee.getValue());
				updateMessage("Téléchargement...");
				listReleveAnnee = facade.getDataYear(stationName, comboAnnee.getValue(), 1);
				Platform.runLater(new Runnable() {
					@Override
					public void run() {
						if (!tableView.getItems().isEmpty()) {
							tableView.getItems().clear();
						}
						anneLabel.setText(comboStations.getValue());
						int i = 0;
						if (listReleveAnnee != null) {
							if (!listReleveAnnee.isEmpty()) {
								for (Releve r : listReleveAnnee) {
									listObsReleve.add(r);
									updateMessage("Chargement...");
									updateProgress(i + 1, 1);
								}
								tableView.setItems(listObsReleve);
								dateLabel.setText("Année de : " + annee);
								updateMessage("Succès !");
								InitGraphiCompenents.enableCelsiusKelvin(radioKelvin, radioCelsius);
							} else {
								updateProgress(0, 0);
								InitGraphiCompenents.hideProgressingEemets(progBarre, progIndicator, progLabel);
								InitGraphiCompenents.disableCelsiusKelvin(radioKelvin, radioCelsius);
								MessageUtil.informationDialog(
										"Données introuvable pour cette station avec l'année " + annee);
							}
						} else {
							updateProgress(0, 0);
							InitGraphiCompenents.hideProgressingEemets(progBarre, progIndicator, progLabel);
							InitGraphiCompenents.disableCelsiusKelvin(radioKelvin, radioCelsius);
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
	 * setToCelcius() est une méthode FXML onAction du radio button Celcius, qui
	 * permet d'afficher la température en degrés Celcius.
	 *
	 */
	@FXML
	private void setToCelcius() {
		if (!tableView.getItems().isEmpty()) {
			tableView.getItems().clear();
		}
		if (listReleveAnnee != null) {
			if (!listReleveAnnee.isEmpty()) {
				for (Releve r : listReleveAnnee) {
					if (r.getTemperature() != -1) {
						r.setTemperature((float) (r.getTemperature() - 273.15));
					}
					listObsReleve.add(r);

				}
				tempColumn.setText("Moyennes des Températures(°C)");
				tableView.setItems(listObsReleve);
			} else {
				MessageUtil.informationDialog("Données introuvable pour cette station avec l'année " + annee);
			}

		}
	}

	/**
	 * setToKelvin() est une méthode FXML onAction du radio button Kelvin, qui
	 * permet d'afficher la température en degrés Kelvin.
	 *
	 */
	@FXML
	private void setToKelvin() {
		if (!tableView.getItems().isEmpty()) {
			tableView.getItems().clear();
		}
		if (listReleveAnnee != null) {
			if (!listReleveAnnee.isEmpty()) {
				for (Releve r : listReleveAnnee) {
					if (r.getTemperature() != -1) {
						r.setTemperature((float) (r.getTemperature() + 273.15));
					}
					listObsReleve.add(r);
				}
				tempColumn.setText("Moyennes des Températures(°K)");
				tableView.setItems(listObsReleve);
			} else {
				MessageUtil.informationDialog("Données introuvable pour cette station avec l'année " + annee);
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
		InitGraphiCompenents.initTabColumn(dateColumn, tempColumn, humColumn, nubColumn);
		InitGraphiCompenents.hideProgressingEemets(progBarre, progIndicator, progLabel);
		InitGraphiCompenents.disableCelsiusKelvin(radioKelvin, radioCelsius);
		tempColumn.setText("Moyenne des Températures(°K)");
	}

}
