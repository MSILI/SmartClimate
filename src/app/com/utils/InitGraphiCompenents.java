package app.com.utils;

import java.time.LocalDate;
import java.util.Map;

import app.com.model.Releve;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DateCell;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TableColumn;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.util.Callback;

/**
 * Cette classe contient l'ensemble des méthodes qui permettent d'initialiser et
 * manipuler les élements des interfaces graphiques.
 * 
 * @author M'SILI Fatah
 * @version 1.0
 */
public class InitGraphiCompenents {

	/**
	 * Cette méthode permet de peupler et initialiser la comboBox des stations.
	 * 
	 * @param comboStations
	 *            composant JavaFX (comboBox).
	 */
	public static void initComboboxStations(ComboBox<String> comboStations) {
		Utils utils = new Utils();
		Map<String, String> listStations = utils.getAllStations();
		ObservableList<String> list = FXCollections.observableArrayList();
		for (Map.Entry<String, String> entry : listStations.entrySet()) {
			list.add(entry.getValue());
		}
		comboStations.setItems(list);
		comboStations.setValue("CAYENNEMATOURY");
	}

	/**
	 * cette méthode permet d'inialiser les colones des tableaux des interfaces
	 * graphiques
	 * 
	 * @param dateColumn
	 *            colone de la date des tableaux des interfaces graphiques
	 * @param tempColumn
	 *            colone de la température des tableaux des interfaces
	 *            graphiques
	 * @param humColumn
	 *            colone de la humidité des tableaux des interfaces graphiques
	 * @param nubColumn
	 *            colone de la Nébulisité des tableaux des interfaces graphiques
	 */
	public static void initTabColumn(TableColumn<Releve, Integer> dateColumn, TableColumn<Releve, Float> tempColumn,
			TableColumn<Releve, Float> humColumn, TableColumn<Releve, Float> nubColumn) {
		dateColumn.setCellValueFactory(new PropertyValueFactory<>("value"));
		tempColumn.setCellValueFactory(new PropertyValueFactory<>("temperature"));
		humColumn.setCellValueFactory(new PropertyValueFactory<>("humidite"));
		nubColumn.setCellValueFactory(new PropertyValueFactory<>("nebulosite"));
	}

	/**
	 * Cette méthode permet de désactiver les deux RadioButton celcius et kelvin
	 * de l'inerface graphique
	 * 
	 * @param radioKelvin
	 *            composant javaFX RadioButton
	 * @param radioCelsius
	 *            composant javaFX RadioButton
	 */
	public static void disableCelsiusKelvin(RadioButton radioKelvin, RadioButton radioCelsius) {
		radioKelvin.setSelected(true);
		radioCelsius.setDisable(true);
		radioKelvin.setDisable(true);
	}

	/**
	 * Cette méthode permet de réactiver les deux RadioButton celcius et kelvin
	 * de l'inerface graphique
	 * 
	 * @param radioKelvin
	 *            composant javaFX RadioButton
	 * @param radioCelsius
	 *            composant javaFX RadioButton
	 */
	public static void enableCelsiusKelvin(RadioButton radioKelvin, RadioButton radioCelsius) {
		radioKelvin.setSelected(true);
		radioCelsius.setDisable(false);
		radioKelvin.setDisable(false);
	}

	/**
	 * Cette méthode permet de désactiver les RadioButton btnTemp, btnHum,
	 * btnNeb.
	 * 
	 * @param btnTemp
	 *            composant javaFX RadioButton
	 * @param btnHum
	 *            composant javaFX RadioButton
	 * @param btnNeb
	 *            composant javaFX RadioButton
	 */
	public static void disableTempNebHum(RadioButton btnTemp, RadioButton btnHum, RadioButton btnNeb) {
		btnTemp.setSelected(true);
		btnTemp.setDisable(true);
		btnHum.setDisable(true);
		btnNeb.setDisable(true);
	}

	/**
	 * Cette méthode permet de réactiver les RadioButton btnTemp, btnHum,
	 * btnNeb.
	 * 
	 * @param btnTemp
	 *            composant javaFX RadioButton
	 * @param btnHum
	 *            composant javaFX RadioButton
	 * @param btnNeb
	 *            composant javaFX RadioButton
	 */
	public static void enableTempNebHum(RadioButton btnTemp, RadioButton btnHum, RadioButton btnNeb) {
		btnTemp.setSelected(true);
		btnTemp.setDisable(false);
		btnHum.setDisable(false);
		btnNeb.setDisable(false);
	}

	/**
	 * Cette méthode permet de désactiver les date supérieur à la date courante
	 * et celle inférieur à 1996.
	 * 
	 * @param datePicker
	 *            composant javaFX DatePicker
	 */
	public static void disableDates(DatePicker datePicker) {
		datePicker.setValue(LocalDate.now());
		LocalDate localDate = LocalDate.of(1996, 1, 1);
		final Callback<DatePicker, DateCell> dayCellFactory = new Callback<DatePicker, DateCell>() {
			@Override
			public DateCell call(final DatePicker datePicker) {
				return new DateCell() {
					@Override
					public void updateItem(LocalDate item, boolean empty) {
						super.updateItem(item, empty);
						if (item.isAfter(LocalDate.now().plusDays(0))) {
							setDisable(true);
							setStyle("-fx-background-color: #ffc0cb;");
						}
						if (item.isBefore(localDate.plusDays(0))) {
							setDisable(true);
							setStyle("-fx-background-color: #ffc0cb;");
						}
					}
				};
			}
		};
		datePicker.setDayCellFactory(dayCellFactory);
	}

	/**
	 * Cette méthode permet de peupler et d'initialiser la ComboBox de l'année
	 * des interfaces graphiques.
	 * 
	 * @param comboAnnee
	 *            comosant javaFX ComboBox
	 */
	public static void initComboboxAnnee(ComboBox<Integer> comboAnnee) {
		LocalDate ld = LocalDate.now();
		ObservableList<Integer> list = FXCollections.observableArrayList();
		int curDate = ld.getYear();
		for (int i = 1996; i <= curDate; i++) {
			list.add(i);
		}
		comboAnnee.setItems(list);
		comboAnnee.setValue(1996);
	}

	/**
	 * Cette méthode permet de peupler et d'initialiser la ComboBox du mois des
	 * interfaces graphiques.
	 * 
	 * @param comboMois
	 *            comosant javaFX ComboBox
	 */
	public static void initComboboxMois(ComboBox<Integer> comboMois) {
		ObservableList<Integer> list = FXCollections.observableArrayList();
		for (int i = 1; i <= 12; i++) {
			list.add(i);
		}
		comboMois.setItems(list);
		comboMois.setValue(1);
	}

	/**
	 * Cette méthode permet de cacher les élements de la progression des
	 * intérfaces graphiques.
	 * 
	 * @param progBarre
	 *            comosant javaFX ProgressBar
	 * @param progIndicator
	 *            comosant javaFX progIndicator
	 * @param progLabel
	 *            comosant javaFX progLabel
	 */
	public static void hideProgressingEemets(ProgressBar progBarre, ProgressIndicator progIndicator, Label progLabel) {
		progBarre.setVisible(false);
		progIndicator.setVisible(false);
		progLabel.setVisible(false);
	}

	/**
	 * Cette méthode permet de faire apparaitre les élements de la progression
	 * des intérfaces graphiques.
	 * 
	 * @param progBarre
	 *            comosant javaFX ProgressBar
	 * @param progIndicator
	 *            comosant javaFX progIndicator
	 * @param progLabel
	 *            comosant javaFX progLabel
	 */
	public static void showProgressingEemets(ProgressBar progBarre, ProgressIndicator progIndicator, Label progLabel) {
		progBarre.setVisible(true);
		progIndicator.setVisible(true);
		progLabel.setVisible(true);
	}

}
