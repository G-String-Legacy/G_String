package steps;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.prefs.BackingStoreException;
import java.util.prefs.InvalidPreferencesFormatException;
import java.util.prefs.Preferences;

import application.Main;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;
import utilities.Popup;

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
	 * pointer to <code>Popup</code>
	 */
	private Popup popup = null;

	/**
	 * pointer to Preferences API
	 */
	private Preferences prefs = null;
	//private Long iBytes;

	/**
	 * constructor for <code>gSetup</code>.
	 *
	 * @param _stage  display screen
	 * @param _popup  exception handler
	 * @param _prefs  Preferences
	 */
	public gSetup(Stage _stage, Popup _popup, Preferences _prefs)
	{
		//  dummy constructor
		myStage = _stage;
		popup = _popup;
		popup.setClass("gSetup");
		prefs = _prefs;
		try {
			prefs.clear();
		} catch (BackingStoreException e1) {
			popup.tell("gSetup_a", e1);
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
			popup.tell("ask_b", e);
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
			popup.tell("ask_a","Operating system " + sOS + " not recognized.");
			System.exit(1);
		}
		File fTarget = new File(sTarget);
		sResource = "/resources/" + sName;
		byte[] b = new byte[1024];
		int len;

		FileOutputStream out = new FileOutputStream(sTarget);
		try {
				InputStream is = Main.class.getResourceAsStream(sResource);
				if (is == null)
					popup.tell("ask_bf", "urGenova Input stream null.");
				while((len = is.read(b, 0, 1024)) > 0){
				    out.write(b, 0, len);
				}
				is.close();
				out.close();
		} catch (IOException e) {
		    popup.tell("ask_c", e);
		}
	    fTarget.setExecutable(true, false);

	    // load default preferences
	    String sPreferences = "resources/" + "Prefs_Default.xml";
		try {
			InputStream is = Main.class.getResourceAsStream(sPreferences);
			if (is == null)
				popup.tell("ask_d", "Default Preferences Input stream null.");
			try {
				Preferences.importPreferences(is);
			} catch (InvalidPreferencesFormatException e) {
				popup.tell("ask_e", e);
			}
			is.close();
			out.close();
		} catch (IOException e) {
		    popup.tell("ask_f", e);
		}
		prefs.put("OS", sOS);
	}
}

