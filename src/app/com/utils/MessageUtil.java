package app.com.utils;

import java.util.Optional;

import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;

/**
 * Elle regroupe des méthodes personalisés correspondante à des alert javaFX.
 *
 */
public class MessageUtil {

	/**
	 * Elle permet d'afficher une alert d'information dans l'interface
	 * graphique.
	 * 
	 * @param msg
	 *            message à afficher dans la fenêtre de dialogue
	 */
	public static void informationDialog(String msg) {
		Alert alert = new Alert(AlertType.INFORMATION);
		alert.setTitle("Information");
		alert.setHeaderText(null);
		alert.setContentText(msg);
		alert.showAndWait();
	}

	/**
	 * Elle permet d'afficher une alert d'erreur dans l'interface graphique.
	 * 
	 * @param msg
	 *            message à afficher dans la fenêtre de dialogue.
	 */
	public static void errorDialog(String msg) {
		Alert alert = new Alert(AlertType.ERROR);
		alert.setTitle("Erreur");
		alert.setHeaderText(null);
		alert.setContentText(msg);
		alert.showAndWait();
	}

	/**
	 * Elle permet d'afficher une alert de confirmation donnant un choix a
	 * l'utilisateur de confirmer ou non choix. Si on clique sur le bouton OK la
	 * fonction renvoie vrai, sinon faux.
	 * 
	 * @param msg
	 *            message à afficher dans la fenetre de dialogue.
	 * @return elle retourne le choix de l'utilisateur (soit ok : vrai soit
	 *         Annuler ou fermer : false)
	 */
	public static boolean yesNoDialog(String msg) {
		boolean bool = false;
		Alert alert = new Alert(AlertType.CONFIRMATION);
		alert.setTitle("Confirmation");
		alert.setHeaderText(null);
		alert.setContentText(msg);
		Optional<ButtonType> result = alert.showAndWait();
		if (result.get() == ButtonType.OK) {
			bool = true;
		}
		return bool;
	}

}
