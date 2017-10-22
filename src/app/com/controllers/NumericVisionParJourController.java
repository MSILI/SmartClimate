package app.com.controllers;

import java.net.URL;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
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
 * La classe NumericVisionParJourController correspond au controlleur de la vue
 * NumVisionParJour, cette classe contient les différentes méthodes qu'utilisent
 * les différents composant de cette dernière.
 * 
 * @version 1.2
 * @author M'SILI Fatah.
 */
public class NumericVisionParJourController implements Initializable {

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

	private ObservableList<Releve> listObsReleve = FXCollections.observableArrayList();
	private List<Releve> listRelevesJour = null;
	private String dateAffich = null;
	private Facade facade = new Facade();

	/**
	 * 
	 * La méthode visualiserAction() c'est la méthode FXML onAction du boutton
	 * visualiser.
	 * 
	 * @see {@link #barreProgressing()}
	 * @throws Exception
	 */
	@FXML
	private void visualiserAction() throws Exception {
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
	 * @see {@link app.com.facade.Facade#getDataDay(String, String)}
	 * @see {@link app.com.utils.Utils#isErreur()}
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
				updateMessage("Téléchargement...");
				listRelevesJour = facade.getDataDay(stationName, localDate.getYear(), localDate.getMonthValue(),
						localDate.getDayOfMonth());
				Platform.runLater(new Runnable() {
					@Override
					public void run() {
						if (!tableView.getItems().isEmpty()) {
							tableView.getItems().clear();
						}
						anneLabel.setText(comboStations.getValue());
						int i = 0;
						if (listRelevesJour != null) {
							if (!listRelevesJour.isEmpty()) {
								for (Releve r : listRelevesJour) {
									updateMessage("Chargement...");
									updateProgress(i + 1, 1);
									listObsReleve.add(r);
								}
								tableView.setItems(listObsReleve);
								dateLabel.setText("Jour de : " + dateAffich);
								InitGraphiCompenents.enableCelsiusKelvin(radioKelvin, radioCelsius);
								updateMessage("Succès !");
							} else {
								updateProgress(0, 0);
								InitGraphiCompenents.hideProgressingEemets(progBarre, progIndicator, progLabel);
								InitGraphiCompenents.disableCelsiusKelvin(radioKelvin, radioCelsius);
								MessageUtil.informationDialog(
										"Données introuvable pour cette station avec le jour " + dateAffich);
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
		if (listRelevesJour != null) {
			for (Releve r : listRelevesJour) {
				if (r.getTemperature() != -1) {
					r.setTemperature((float) (r.getTemperature() - 273.15));
				}
				listObsReleve.add(r);
			}
			tempColumn.setText("Températures(°C)");
			tableView.setItems(listObsReleve);
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
		if (listRelevesJour != null) {
			for (Releve r : listRelevesJour) {
				if (r.getTemperature() != -1) {
					r.setTemperature((float) (r.getTemperature() + 273.15));
				}
				listObsReleve.add(r);
			}
			tempColumn.setText("Températures(°K)");
			tableView.setItems(listObsReleve);
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
		InitGraphiCompenents.initTabColumn(dateColumn, tempColumn, humColumn, nubColumn);
		InitGraphiCompenents.hideProgressingEemets(progBarre, progIndicator, progLabel);
		InitGraphiCompenents.disableCelsiusKelvin(radioKelvin, radioCelsius);
		tempColumn.setText("Température(°K)");
	}

}
