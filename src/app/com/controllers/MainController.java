package app.com.controllers;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import org.apache.commons.io.FileUtils;

import app.com.utils.MessageUtil;
import javafx.animation.FadeTransition;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.ListView;
import javafx.scene.layout.AnchorPane;
import javafx.util.Duration;

/**
 * Cette classe correspond au controlleur de la vue Main, cette classe contient
 * les diff�rentes m�thodes qu'utilisent les diff�rents composant de cette
 * derni�re.
 * 
 * @version 1.2
 * @author M'SILI Fatah.
 *
 */
public class MainController implements Initializable { 

	@FXML
	private ListView<String> numListview;
	@FXML
	private ListView<String> graphListview;
	@FXML
	private ListView<String> compListview;
	@FXML
	private AnchorPane dataPane;
	@FXML
	private AnchorPane rootPane;

	/**
	 * Cette m�thode permet de supprimer le dossier des fichiers T�l�charg�s
	 * stock�s dans disque local C: .
	 * 
	 * @throws Exception
	 */
	@FXML
	public void viderLeCache() throws Exception {
		File file = new File("C:\\smartClimateDepot");
		if (file.exists()) {
			boolean confirmation = MessageUtil.yesNoDialog("Voulez-vous vraiment vider le cache ?");
			if (confirmation) {
				FileUtils.deleteDirectory(file);
				MessageUtil.informationDialog("Le cache a �t� vid� !");
				initTab();
			}
		} else {
			MessageUtil.informationDialog("Aucun cache existant pour le moment !");
		}
	}

	/**
	 * Cette m�thode permet de d'interoger l'utilisateur si il veux vraiment
	 * quitter l'application.
	 */
	@FXML
	public void exitWindow() {
		boolean confirmation = MessageUtil.yesNoDialog("Voulez-vous vraiment quitter ?");
		if (confirmation) {
			Platform.exit();
		}
	}

	/**
	 * Cette m�thode permet de d'afficher la vue Apropos.
	 * 
	 * @throws Exception
	 */
	@FXML
	public void affichApropos() throws Exception {
		setDataPane(fadeAnimate("/app/com/views/Apropos.fxml"));
	}

	/**
	 * Cette m�thode permet d'affichier la vue DonneeDisponible.
	 * 
	 * @throws Exception
	 */
	@FXML
	public void affichDonneeDispo() throws Exception {
		setDataPane(fadeAnimate("/app/com/views/DonneeDisponible.fxml"));
	}

	/**
	 * Cette m�thode permet de modifier le contenu de l'anchorPane (espace de
	 * travail).
	 * 
	 * @param node
	 */
	private void setDataPane(Node node) {
		dataPane.getChildren().setAll(node);
	}

	/**
	 * Cette m�thode permet d'affichier la synchronisation entre la vue actuelle
	 * et l'affichage de la vue demand�e par l'utilisateur, et cel� en rajoutant
	 * un effet d'animation (FadeTransition de javaFX).
	 * 
	 * @param url
	 * @return AnchorPane
	 * @throws IOException
	 */
	public AnchorPane fadeAnimate(String url) throws IOException {
		AnchorPane anchorPane = FXMLLoader.load(getClass().getResource(url));
		FadeTransition transition = new FadeTransition(Duration.millis(1000));
		transition.setNode(anchorPane);
		transition.setFromValue(0.1);
		transition.setToValue(1);
		transition.setCycleCount(1);
		transition.setAutoReverse(false);
		transition.play();
		return anchorPane;

	}

	/**
	 * Cette m�thode permet d'initialiser les textes � afficher pour chacune des
	 * ListeView qui se trouve � gauche de la vue principale.
	 */
	public void loadLisViews() {
		ObservableList<String> ol1 = FXCollections.observableArrayList();
		ol1.add("Par Jour");
		ol1.add("Par Mois");
		ol1.add("Par Ann�e");
		numListview.setItems(ol1);
		graphListview.setItems(ol1);

		ObservableList<String> ol2 = FXCollections.observableArrayList();
		ol2.add("Entre deux jours");
		ol2.add("Entre deux mois");
		ol2.add("Entre deux ann�es");
		compListview.setItems(ol2);

	}

	/**
	 * Cette m�thode permet d'afficher des vues diff�rentes en cliquant dans
	 * chacun des items de la listView Afficher tableur.
	 */
	public void selectedNumeriqueMenu() {
		numListview.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<String>() {
			@Override
			public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
				// throw new UnsupportedOperationException("Not supported
				// yet."); //To change body of generated methods, choose Tools |
				// Templates.
				int i = numListview.getSelectionModel().getSelectedIndex();
				switch (i) {
				case 0:
					try {
						setDataPane(fadeAnimate("/app/com/views/NumVisionParJour.fxml"));
					} catch (IOException ex) {
						ex.printStackTrace();
					}
					break;
				case 1:
					try {
						setDataPane(fadeAnimate("/app/com/views/NumVisionParMois.fxml"));
					} catch (IOException ex) {
						ex.printStackTrace();
					}

					break;
				case 2:
					try {
						setDataPane(fadeAnimate("/app/com/views/NumVisionParAnnee.fxml"));
					} catch (IOException ex) {
						ex.printStackTrace();
					}
					break;
				case 3:

					break;
				}
			}
		});
	}

	/**
	 * Cette m�thode permet d'afficher des vues diff�rentes en cliquant dans
	 * chacun des items de la listView Visualiser Graphes.
	 */
	public void selectedGraphicMenu() {
		graphListview.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<String>() {
			@Override
			public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
				// throw new UnsupportedOperationException("Not supported
				// yet."); //To change body of generated methods, choose Tools |
				// Templates.
				int i = graphListview.getSelectionModel().getSelectedIndex();
				switch (i) {
				case 0:
					try {
						setDataPane(fadeAnimate("/app/com/views/GraphVisionParJour.fxml"));
					} catch (IOException ex) {
						ex.printStackTrace();
					}
					break;
				case 1:
					try {
						setDataPane(fadeAnimate("/app/com/views/GraphVisionParMois.fxml"));
					} catch (IOException ex) {
						ex.printStackTrace();
					}

					break;
				case 2:
					try {
						setDataPane(fadeAnimate("/app/com/views/GraphVisionParAnnee.fxml"));
					} catch (IOException ex) {
						ex.printStackTrace();
					}
					break;
				case 3:

					break;
				}
			}
		});
	}

	/**
	 * Cette m�thode permet d'afficher des vues diff�rentes en cliquant dans
	 * chacun des items de la listView Comparer donn�es.
	 */
	public void selectedGraphicCompareMenu() {
		compListview.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<String>() {
			@Override
			public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
				// throw new UnsupportedOperationException("Not supported
				// yet."); //To change body of generated methods, choose Tools |
				// Templates.
				int i = compListview.getSelectionModel().getSelectedIndex();
				switch (i) {
				case 0:
					try {
						setDataPane(fadeAnimate("/app/com/views/GraphCompareParJour.fxml"));
					} catch (IOException ex) {
						ex.printStackTrace();
					}
					break;
				case 1:
					try {
						setDataPane(fadeAnimate("/app/com/views/GraphCompareParMois.fxml"));
					} catch (IOException ex) {
						ex.printStackTrace();
					}

					break;
				case 2:
					try {
						setDataPane(fadeAnimate("/app/com/views/GraphCompareParAnnee.fxml"));
					} catch (IOException ex) {
						ex.printStackTrace();
					}
					break;
				case 3:

					break;
				}
			}
		});
	}

	/**
	 * cette m�thode permet d'initialiser la premi�re vue de l'espace de travail
	 * (le anchorPane du millieu) par la vue Bienvenu.
	 */
	public void initTab() {
		try {
			setDataPane(fadeAnimate("/app/com/views/Bienvenu.fxml"));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
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
		loadLisViews();
		initTab();
		selectedNumeriqueMenu();
		selectedGraphicMenu();
		selectedGraphicCompareMenu();
	}

}
