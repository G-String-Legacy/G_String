package steps;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.prefs.BackingStoreException;
import java.util.prefs.InvalidPreferencesFormatException;
import java.util.prefs.Preferences;
import application.Main;

import javafx.scene.control.Alert;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.DialogPane;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.layout.Region;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;
import utilities.Popup;

public class gSetup
{
	/*
	 * Provides the screen for program setup
	 */
	private Stage myStage = null;
	private Popup popup = null;
	private Preferences prefs = null;
	//private Long iBytes;


	public gSetup(Stage _stage, Popup _popup, Preferences _prefs)
	{
		//  dummy constructor
		myStage = _stage;
		popup = _popup;
		popup.setObject(11,51);
		prefs = _prefs;
		try {
			prefs.clear();
		} catch (BackingStoreException e1) {
			popup.tell("775a", "Unable to clear preferences.", e1);
		}
	}

	public void ask() throws IOException
	{
		// Initialize preferences
		String sResource = "/resources/Prefs_Default.xml";
		InputStream stIn = Main.class.getResourceAsStream(sResource);
		try {
			Preferences.importPreferences(stIn);
		} catch (InvalidPreferencesFormatException e1) {
			popup.tell("776a", e1);
		}

		DirectoryChooser dc = new DirectoryChooser();
		File fDir = new File(prefs.get("Working Directory", File.separator));
		if (fDir.exists())
			dc.setInitialDirectory(fDir);
		dc.setTitle("Choose location and create new working directory");
		File dir = dc.showDialog(myStage);
		try
		{
			dir.mkdir();
		} catch (Exception e)
		{
			popup.tell("776b", e);
		}
		dir.setWritable(true, false);
		String sWork = dir.getPath();
		prefs.put("Working Directory", sWork);
		prefs.put("Home Directory", System.getProperty("user.home"));
		prefs.put("Default Log", "OFF");
		String sOS_Full = System.getProperty("os.name");
		popup.tell("776b", "Operating System: " + sOS_Full);
		String sTarget = null;
		String sName = null;
		String sOS = null;
		if (sOS_Full.indexOf("Windows") >=0)
		{
			sOS = "Windows";
			sName = "urGenova_W.exe";
			sTarget = sWork + File.separator + "urGenova.exe";
		}
		else if (sOS_Full.indexOf("Linux") >= 0)
		{
			sOS = "Linux";
			sName = "urGenova_L";
			sTarget = sWork + File.separator + "urGenova";
		}
		else if (sOS_Full.indexOf("Mac") >= 0)
		{
			sOS = "Mac";
			sName = "urGenova_M";
			sTarget = sWork + File.separator + "urGenova";
		}
		else
		{
			Warning("Operating system " + sOS + " not recognized.");
			System.exit(1);
		}
		File fTarget = new File(sTarget);
		sResource = "/resources/" + sName;
		popup.tell("776c", "setup parameters: " + sOS + ", " + sName + ", " + sTarget + ", " + sResource);
		popup.tell("776d", "Out path: " + fTarget.getPath());
		byte[] b = new byte[1024];
		int len;

		FileOutputStream out = new FileOutputStream(sTarget);
		popup.tell("776e", "OutputStream created.");
		try {
				InputStream is = Main.class.getResourceAsStream(sResource);
				if (is == null)
					popup.tell("776f", "urGenova Input stream null.");
				popup.tell("776g", "InputStream created, bytes available: " + is.available());
				while((len = is.read(b, 0, 1024)) > 0){
				    out.write(b, 0, len);
				    popup.tell("776h", "One Buffer read." + len + " bytes.");
				}
				is.close();
				out.close();
		} catch (IOException e) {
		    popup.tell("776i", "Copying failed:", e);
		}
	    fTarget.setExecutable(true, false);

	    // load default preferences
	    String sPreferences = "resources/" + "Prefs_Default.xml";
		try {
			InputStream is = Main.class.getResourceAsStream(sPreferences);
			if (is == null)
				popup.tell("776j", "Default Preferences Input stream null.");
			try {
				Preferences.importPreferences(is);
			} catch (InvalidPreferencesFormatException e) {
				popup.tell("776k", e);
			}
			is.close();
			out.close();
		} catch (IOException e) {
		    popup.tell("776k", "Copying preferences failed:", e);
		}
		prefs.put("OS", sOS);
	}
	private void Warning(String message) {
		Alert alert = new Alert(AlertType.WARNING);
		alert.getDialogPane().setMinHeight(Region.USE_PREF_SIZE);
		Double dX = myStage.getX() + 200.0;
		Double dY = myStage.getY() + 75.0;
		alert.setX(dX);
		alert.setY(dY);
		alert.initOwner(myStage);
		alert.setTitle("Log");
		alert.setHeaderText(null);
		alert.setContentText(message);
		DialogPane dialogPane = alert.getDialogPane();
		dialogPane.getStylesheets().add("com/papaworx/gstring/resources/myDialog.css");
		dialogPane.getStyleClass().add("myDialog");
		ButtonBar buttonBar = (ButtonBar) dialogPane.lookup(".button-bar");
		buttonBar.getButtons().forEach(b -> b.setStyle(
				"-fx-font-size: 16;-fx-background-color: #551200;-fx-text-fill: #ffffff;-fx-font-weight: bold;"));
		alert.showAndWait();
	}

}

