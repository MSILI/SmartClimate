package app.com.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.zip.GZIPInputStream;

import org.apache.commons.io.FileUtils;

/**
 * Elle est une classe utilitaire, elle contient l'ensembles des m�thode que la
 * Classe Facade utilise, parmis ces m�thodes le t�l�chargement du fichier csv
 * compress� � partir du site france m�t�o, la decompression de ce dernier vers
 * un fichier csv ordinaire.
 * 
 * @version 1.4
 * @author M'SILI Fatah.
 */
public class Utils {

	private static final String savePath = "C:\\smartClimateDepot";

	/**
	 * Elle permet de t�l�charger un fichier CSV d'un mois d'une date demand�e
	 * par l'utilisateur � partir du serveur de France M�t�o. Si le fichier est
	 * t�l�charg� elle renvoie vrai, si y a pas de connexion (une IOException)
	 * elle renvoie faux.
	 * 
	 * @param fileNameToDownload
	 *            nom de fichier � t�l�charger0.
	 * @param saveDir
	 *            dossier de sauvegarde de fichier t�l�charg�.
	 * @return le type de retour est un boolean
	 */
	private boolean downloadFileMonth(String fileNameToDownload, String saveDir) {
		boolean isDownloaded = true;
		URL URLfile;
		try {
			URLfile = new URL(
					"https://donneespubliques.meteofrance.fr/donnees_libres/Txt/Synop/Archive/" + fileNameToDownload);
			FileUtils.copyURLToFile(URLfile, new File(saveDir + File.separator + fileNameToDownload));
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			isDownloaded = false;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			isDownloaded = false;
		}
		return isDownloaded;
	}

	/**
	 * Elle a pour r�le de d�compresser les fichier csv.gz en csv.
	 * 
	 * @param fileName
	 *            nom de fichier a d�compresser.
	 * @return le type de retour est un boolean
	 * @see {@link #downloadFileMonth(String, String)}
	 */
	public boolean gUnzipFile(String fileName) {
		boolean isUnzipped = true;
		String saveDir = savePath + File.separator + fileName.substring(0, 4);
		String compressedFileName = "synop." + fileName.substring(0, 6) + ".csv.gz";
		String decompressedFileName = fileName.substring(4, 6) + ".csv";
		byte[] buffer = new byte[1024];
		int len;
		GZIPInputStream gzis = null;
		FileOutputStream out = null;
		try {
			gzis = new GZIPInputStream(new FileInputStream(saveDir + File.separator + compressedFileName));
			out = new FileOutputStream(saveDir + File.separator + decompressedFileName);
			while ((len = gzis.read(buffer)) > 0) {
				out.write(buffer, 0, len);
			}
			gzis.close();
			out.close();
			File compressedFile = new File(saveDir + File.separator + compressedFileName);
			if (compressedFile.exists()) {
				compressedFile.delete();
			}
		} catch (FileNotFoundException e) {
			// si le fichier n'existe pas on le t�l�charges
			boolean isdownloaded = downloadFileMonth(compressedFileName, saveDir);
			if (!isdownloaded) {
				// si le fichier n'est pas t�l�charg� (a cause de la connexion
				// internet) on return false
				isUnzipped = false;
			} else {
				// si le fichier existe on le d�compresse
				isUnzipped = gUnzipFile(fileName);
			}
		} catch (IOException e) {
			boolean isdownloaded = downloadFileMonth(compressedFileName, saveDir);
			if (!isdownloaded) {
				isUnzipped = false;
			} else {
				isUnzipped = gUnzipFile(fileName);
			}
		}

		return isUnzipped;
	}

	/**
	 * Elle � pour r�le de r�cup�rer la liste des stations sous forme de hashMap
	 * : la cl� c'est le num�ro, la valeur c'est le nom de la station.
	 * 
	 * @return le type de retour est une Map<String,String>.
	 */
	public Map<String, String> getAllStations() {
		Map<String, String> listStations = new HashMap<String, String>();
		String line = null;
		String[] tab = null;
		BufferedReader br;
		try {
			// lecture du fichier postesSynop.csv
			br = new BufferedReader(new InputStreamReader(getClass().getResourceAsStream("/files/postesSynop.csv")));
			while ((line = br.readLine()) != null) {
				tab = line.split(";");
				// remplir la hash map avec la cl� = num�ro, valeur = nom de la
				// station
				listStations.put(tab[0], tab[1]);
			}
			br.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		return listStations;
	}

	/**
	 * Elle permet de r�cup�rer le num�ro correspondant a un nom de station.
	 * 
	 * @param stationName
	 *            nom de la station.
	 * @return le type de retour est un String.
	 */
	public String getStationNum(String stationName) {
		Map<String, String> listStations = getAllStations();
		String stationNum = null;
		for (Map.Entry<String, String> entry : listStations.entrySet()) {
			if (entry.getValue().equals(stationName)) {
				stationNum = entry.getKey();
			}
		}
		return stationNum;
	}

	/**
	 * Elle renvoie une liste de chaine de caract�res qui correspond aux dates
	 * de mois d'une ann�e demand�e sous format yyyyMM.
	 * 
	 * @param annee
	 *            valeur de l'ann�e.
	 * @return le type renvoy� c'est une liste de dates sous forme annee+mois
	 *         (200702)
	 */
	public List<String> getMonthsYear(String annee) {
		List<String> listMois = new ArrayList<String>();
		LocalDate nowday = LocalDate.now();
		String month = null;
		// si l'ann�e pass� en param�tre est �gale a l'ann�e de la date courante
		// on ajoute que les dates des mois existants.
		if (Integer.parseInt(annee) == nowday.getYear()) {
			for (int i = 1; i <= nowday.getMonthValue(); i++) {
				if (i < 10) {
					month = annee + "0" + i;
				} else {
					month = annee + i;
				}
				listMois.add(month);
			}
		} else {
			// sinon on ajoute les 12 mois de l'ann�e
			for (int i = 1; i <= 12; i++) {
				if (i < 10) {
					month = annee + "0" + i;
				} else {
					month = annee + i;
				}
				listMois.add(month);
			}
		}
		return listMois;
	}

	/**
	 * Elle renvoie la liste des ann�es disponibles dans le depot de
	 * l'application.
	 * 
	 * @return le type de retour est une liste de String
	 */
	public static List<String> getAnneeDispo() {
		List<String> listFolder = null;
		File saveFolder = new File(savePath);
		String[] names = saveFolder.list();
		if (saveFolder.exists()) {
			listFolder = new ArrayList<String>();
			for (String name : names) {
				if (new File(savePath + File.separator + name).isDirectory()) {
					listFolder.add(name);
				}
			}
		}
		return listFolder;
	}

	/**
	 * Elle renvoie la liste des mois disponibles dans une ann�e.
	 * 
	 * @param annee
	 * @return List<String>
	 */
	public static List<String> getMoisDispo(String annee) {
		List<String> listMonth = null;
		File yearFolder = new File(savePath + File.separator + annee);
		if (yearFolder.exists()) {
			listMonth = new ArrayList<String>();
			File[] listOfFile = yearFolder.listFiles();
			for (File f : listOfFile) {
				listMonth.add(f.getName().substring(0, f.getName().lastIndexOf(".")) + "/" + annee);
			}
		}
		return listMonth;
	}

}
