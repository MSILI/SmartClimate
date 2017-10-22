package app.com.controllers;

import java.net.URL;
import java.util.ResourceBundle;

import app.com.utils.Utils;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.Separator;

/**
 * Cette classe contient l'ensemble des traitements li�s � la vue
 * DonneeDisponible.fxml, parmis les fonctionalit� de ce controlleur :
 * l'affichage des donn�es disponibles (fichiers csv) dans le d�pot de
 * smartclimate.
 * 
 * @version 1.0
 * @author M'SILI Fatah.
 */
public class DonneeDisponibleController implements Initializable {

	@FXML
	private ListView<String> listViewAnnee;
	@FXML
	private Label errorLabel;
	@FXML
	private ListView<String> listViewMois;
	@FXML
	private Label erreurMoisLabel;
	@FXML
	private Label titreMoisdispo;
	@FXML
	private Separator sepMoisDispo;

	/**
	 * Cette m�thode permet de charger les ann�es disponible dans le dep�t de
	 * l'application.
	 * 
	 * @see {@link app.com.utils.Utils#getAnneeDispo()}
	 */
	public void loadLisViewAnnee() {
		listViewAnnee.setVisible(false);
		listViewMois.setVisible(false);
		erreurMoisLabel.setVisible(false);
		titreMoisdispo.setVisible(false);
		sepMoisDispo.setVisible(false);
		if (Utils.getAnneeDispo() != null) {
			if (!Utils.getAnneeDispo().isEmpty()) {
				errorLabel.setVisible(false);
				ObservableList<String> observableListAnnee = FXCollections.observableArrayList();
				for (String str : Utils.getAnneeDispo()) {
					observableListAnnee.add(str);
				}
				listViewAnnee.setItems(observableListAnnee);
				listViewAnnee.setVisible(true);
			} else {
				listViewAnnee.setVisible(false);
				errorLabel.setText("Aucune ann�e disponible !");
				errorLabel.setVisible(true);
			}

		} else {
			errorLabel.setText("Aucune ann�e disponible !");
			errorLabel.setVisible(true);
		}

	}

	/**
	 * Cette m�thode permet de charger la liste des mois d'une ann�e disponible.
	 * 
	 * @param annee
	 * @see {@link app.com.utils.Utils#getMoisDispo(String)}
	 */
	public void loadListMois(String annee) {
		listViewMois.setVisible(false);
		erreurMoisLabel.setVisible(false);
		titreMoisdispo.setVisible(true);
		sepMoisDispo.setVisible(true);
		if (Utils.getMoisDispo(annee) != null) {
			if (!Utils.getMoisDispo(annee).isEmpty()) {
				ObservableList<String> observableListMois = FXCollections.observableArrayList();
				for (String str : Utils.getMoisDispo(annee)) {
					observableListMois.add(str);
				}
				listViewMois.setItems(observableListMois);
				listViewMois.setVisible(true);
			} else {
				erreurMoisLabel.setText("Aucun mois disponible !");
				erreurMoisLabel.setVisible(true);
			}
		} else {
			erreurMoisLabel.setText("Aucun mois disponible !");
			erreurMoisLabel.setVisible(true);
		}
	}

	/**
	 * Cette m�thode permet d'afficher dynamiquement la liste des mois
	 * disponibles dans une autre listeView en selectionnant un item dans la
	 * liste des ann�es disponibles.
	 */
	public void onSelectItemListViewAnnee() {
		listViewAnnee.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<String>() {
			@Override
			public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
				String selectedValue = listViewAnnee.getSelectionModel().getSelectedItem();
				loadListMois(selectedValue);
			}
		});
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javafx.fxml.Initializable#initialize(java.net.URL,
	 * java.util.ResourceBundle)
	 */
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		// TODO Auto-generated method stub
		loadLisViewAnnee();
		onSelectItemListViewAnnee();
	}

}
