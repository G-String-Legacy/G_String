package steps;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.logging.Logger;
import java.util.prefs.BackingStoreException;
import java.util.prefs.InvalidPreferencesFormatException;
import java.util.prefs.Preferences;
import application.Main;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;

/**
 * Provides the screen for program setup
 *
 * @see <a href="https://github.com/G-String-Legacy/G_String/blob/main/workbench/GS_L/src/steps/gSetup.java">steps.gSetup</a>
 * @author ralph
 * @version %v..%
 */
public class gSetup
{
	/**
	 * JavaFX stage for GUI
	 */
	private Stage myStage = null;

	/**
	 * pointer to <code>logger</code>
	 */
	private Logger logger = null;

	/**
	 * pointer to Preferences API
	 */
	private Preferences prefs = null;
	//private Long iBytes;

	/**
	 * constructor for <code>gSetup</code>.
	 *
	 * @param _stage  display screen
	 * @param _logger  pointer to application logger
	 * @param _prefs  Preferences
	 */
	public gSetup(Stage _stage, Logger _logger, Preferences _prefs)
	{
		//  dummy constructor
		myStage = _stage;
		logger = _logger;
		prefs = _prefs;
		try {
			prefs.clear();
		} catch (BackingStoreException e) {
			logger.warning(e.getMessage());
		}
	}

	/**
	 * Initializes preferences
	 *
	 * @throws IOException input errors
	 */
	public void ask() throws IOException
	{
		String sResource = "/resources/Prefs_Default.xml";
		InputStream stIn = Main.class.getResourceAsStream(sResource);
		try {
			Preferences.importPreferences(stIn);
		} catch (InvalidPreferencesFormatException e) {
			logger.warning(e.getMessage());
		}

		DirectoryChooser dc = new DirectoryChooser();
		dc.setInitialDirectory(null);
		File fDir = new File(System.getProperty("user.home"));
		if (fDir.exists())
			dc.setInitialDirectory(fDir);
		dc.setTitle("Choose location and create new working directory");
		File dir = dc.showDialog(myStage);
		try
		{
			dir.mkdir();
		} catch (Exception e)
		{
			logger.warning(e.getMessage());
		}
		dir.setWritable(true, false);
		String sWork = dir.getPath();
		prefs.put("Working Directory", sWork);
		prefs.put("Home Directory", System.getProperty("user.home"));
		String sOS_Full = System.getProperty("os.name");
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
			Alert alert = new Alert(AlertType.WARNING);
			alert.setContentText("Operating system " + sOS + " not recognized.");
			alert.showAndWait();
			System.exit(1);
		}
		File fTarget = new File(sTarget);
		sResource = "/resources/" + sName;
		byte[] b = new byte[1024];
		int len;

		FileOutputStream out = new FileOutputStream(sTarget);
		try {
				InputStream is = Main.class.getResourceAsStream(sResource);
				if (is == null) {
					Alert alert = new Alert(AlertType.WARNING);
					alert.setContentText("urGenova Input stream null.");
					alert.showAndWait();
				}
				while((len = is.read(b, 0, 1024)) > 0){
				    out.write(b, 0, len);
				}
				is.close();
				out.close();
		} catch (IOException e) {
		    logger.warning(e.getMessage());
		}
	    fTarget.setExecutable(true, false);

	    // load default preferences
	    String sPreferences = "../resources/" + "Prefs_Default.xml";
		try {
			InputStream is = Main.class.getResourceAsStream(sPreferences);
			if (is == null) {
				Alert alert = new Alert(AlertType.WARNING);
				alert.setContentText("Default Preferences Input stream null.");
				alert.showAndWait();
			}
			try {
				Preferences.importPreferences(is);
			} catch (InvalidPreferencesFormatException e) {
				logger.warning(e.getMessage());
			}
			is.close();
			out.close();
		} catch (IOException e) {
			logger.warning(e.getMessage());
		}
		prefs.put("OS", sOS);
	}
}

