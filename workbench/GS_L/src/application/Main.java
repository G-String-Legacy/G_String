package application;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.URL;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.prefs.Preferences;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import model.Nest;
import steps.AnaGroups;
import steps.SynthGroups;
import steps.gSetup;
import utilities.Filer;
import view.TextStack;
import view.rootLayoutController;

/**
 * <strong>Main entry point</strong> to G_String_L, stands for 'Legacy'.
 * Uses standard structure of standard Javafx applications:
 * main(), start(), and initRootLayout();
 * This ensures tight integration with rootLayoutController and rootLayout.fxml in the view package.
 * Main also steers the operations according to menu selections to the actual work routines.
 * All traffic between the UID (rootLayout) and the working code is funneled through the 'Main' class.
 *
 * @see <a href="https://github.com/G-String-Legacy/G_String/blob/main/workbench/GS_L/src/application/Main.java">application.Main</a>
 * @author Ralph Bloch
 * @version %v..%
 */
public class Main extends Application {

	/**
	 * The primary stage of javaFX used as main window for the G_String GUI.
	 */
	private Stage primaryStage;

	/**
	 * the rootLayout of the mainStage is formated as a <code>BorderPane</code>
	 */
	private BorderPane rootLayout;

	/**
	 * Object <code>myNest</code> - encapsulates all experimental model descriptors (excepts
	 * sample sizes, and methods to generate logical derivatives.
	 */
	private static Nest myNest;

	/**
	 * <code>mySteps</code> - guides the user through all the input steps for performing
	 * a Generalizability Analysis.
	 */
	private AnaGroups mySteps;

	/**
	 * <code>mySynthSteps</code> - guides the user through all the input steps for generating
	 * a synthetic dataset, on which Generalizability Analysis can be practiced.
	 */
	private SynthGroups mySynthSteps;

	/**
	 * <code> scene0</code> acts a container to send the javaFX code <code>group</code>
	 * to <code>primaryStage</code> for display.
	 */
	private Scene scene0;

	/**
	 * controller - Object that controls the GUI.
	 *
	 */
	private rootLayoutController controller;

	/**
	 * <code>group</code> - encapsulates the various components of a javaFX,
	 * specific for each step and condition.
	 */
	private Group group;

	/**
	 * Exception logger;
	 */
	private Logger logger;

	/**
	 * <code>storedScene</code> location to park current scene, when a
	 * temporary scene has to be overlaid.
	 */
	private Scene storedScene = null;

	/**
	 * <code>root</code> - serves a root for current Preferences.
	 */
	private Preferences root = Preferences.userRoot();

	/**
	 * <code>prefs</code> - Preference API.
	 */
	final private Preferences prefs = root.node("/com/papaworx/gstring");

	/**
	 * <code>newScene</code> - used to generate a fresh display.
	 */
	private Scene newScene = null;

	/**
	 * Object <code>flr</code> handles most file input/output.
	 */
	private Filer flr;

	/**
	 * String containing current location of log file output.
	 */
	private String sLogPath = null;
	
	/**
	 * flag indicating replication mode.
	 */
	private Boolean bReplicate = false;

	/**
	 * Obligatory javafx <code>main</code> - constructor
	 *
	 * @param args; Default for javaFX
	 */
	public static void main(String[] args) {
		launch(args);
	}

	/**
	 * Initializes the root layout. Standard javafx.
	 * This system method is somewhat cryptic. It gets called by the system,
	 * and hands a handle for the primary stage to the program.
	 * First, setting up objects to be used throughout.
	 * Overrides default.
	 *
	 * @param primaryStage;
	 */
	@Override
	public void start(Stage primaryStage) {
		String sUser = System.getProperty("user.home");
		String sHomeDir = prefs.get("Home Directory","");
		File fHome = new File (sHomeDir);
		if (!fHome.exists()) {
			sHomeDir = sUser;
			prefs.put("Home Directory", sHomeDir);
		}
		sLogPath = sHomeDir + File.separator + "com.papaworx.gstring.Log";
		FileHandler fh = null;							// just for initialization
		try {
			fh = new FileHandler(sLogPath, true);		// log handler, creates append logs, rather than new ones
		} catch (IOException e1) {
			e1.printStackTrace(); 						// emergency exit
		}
		logger = Logger.getLogger(Main.class.getName());
		logger.addHandler(fh);
		myNest = new Nest(logger, this, prefs);
		flr = new Filer(myNest, prefs, logger, primaryStage);
		this.primaryStage = primaryStage;
		myNest.setStage(primaryStage);
		this.primaryStage.setTitle("G_String_L (Java)");
		group = null;
		initRootLayout();								// initializes standard GUI layout
		mySteps = new AnaGroups(this, myNest, logger, controller, primaryStage, prefs, flr); 	// object for analysis
		mySynthSteps = new SynthGroups(this, myNest, logger, controller, prefs, flr);	// object for synthesis
		try {
			stepUp();	// now ready for work
		} catch (Throwable e) {
			logger.warning(e.getMessage());
		}
	}

	/**
	 * Standard javafx; loads initial root layout stage
	 */
	public void initRootLayout() {

		try {
			URL fxmlLocation = getClass().getResource("/view/rootLayout.fxml");
			FXMLLoader loader = new FXMLLoader(fxmlLocation);
			rootLayout = (BorderPane) loader.load();

			// Show the scene containing the root layout.
			scene0 = new Scene(rootLayout);
			myNest.setScene(scene0);
			controller = loader.getController();
			controller.setMainApp(this, logger, prefs);
		} catch (IOException e) {
			logger.warning(e.getMessage());
			e.printStackTrace();
		}
	}


	/**
	 * getter for primary stage available by a call to 'main'
	 *
	 * @return primaryStage;
	 */
	public Stage getPrimaryStage() {
		return primaryStage;
	}

	/**
	 * Except for very localized messages, the graphical user interface (GUI) is standardized.
	 * Throughout G_String, the objects create javafx scenes called 'groups', which are handed to the 'show' subroutine.
	 * User interactions with the GUI are directly fed back to appropriate methods of Main.
	 * Responds to GUI 'Next' button.
	 */
	public void stepUp() {
		
		try {
			if (!myNest.getDawdle() && !myNest.getVarianceDawdle())
				myNest.incrementSteps();
			int iStep = myNest.getStep();
			controller.setStep(iStep);
			if (!myNest.getSimulate()) // if in analysis mode
			{
				/**
				 * This is the default analysis path
				 */
					group = mySteps.getGroup();
				if (group == null)
					switch (iStep) {
					case 1:
						freshStart();
						break;
					case 9:
						myNest.setStep(7);
						break;
					case 10:
						break;
					default:
						break;
					}
				else {
					show(group);
				}
			} else {
				/**
				 * Otherwise we got for synthesis
				 */
				group = mySynthSteps.getGroup();
				show(group);
			}
		} catch (Throwable e) {
			StringWriter sw = new StringWriter();
			PrintWriter pw = new PrintWriter(sw);
			e.printStackTrace(pw);			
			logger.warning(e.toString());
		}
	}

	/**
	 * Central display king pin. Receives scene (group), and passes it on to the GUI.
	 *
	 * @param _display;
	 */
	public void show(Group _display) {

		BorderPane frame = (BorderPane) scene0.getRoot();
		frame.setCenter(_display);
		newScene = scene0;
		newScene.setRoot(frame);
		primaryStage.setScene(newScene);
		primaryStage.show();
	}

	/**
	 * In response to GUI sets switches for manual analysis
	 */
	public void startFresh() {
		bReplicate = false;
		myNest.setReplicate(false);
		myNest.setDoOver(false);
		mySteps.setReplicate(false);
		try {
			stepUp();
		} catch (Throwable e) {
			logger.warning(e.getMessage());
		}
	}

	/**
	 * In response to GUI sets switches for script driven analysis
	 */
	public void startOver() {
		bReplicate = false;
		myNest.setDoOver(true);			// sets the 'doOver' Boolean in 'Nest'
		myNest.setReplicate(false);
		mySteps.setReplicate(false);
		try {
			stepUp();					// and then tries going to 'stepUp'
		} catch (Throwable e) {
			logger.warning(e.getMessage());
		}
	}

	/**
	 * In response to GUI set switches for manual simulation
	 */
	public void Simulate() {

		myNest.setDoOver(false); 		// only manual input
		myNest.setSimulate(true); 		// simulate
		myNest.setReplicate(false);
		try {
			stepUp();
		} catch (Throwable e) {
			logger.warning(e.getMessage());
		}
	}

	/**
	 * In response to GUI sets switches for script driven simulation.
	 */
	public void Resimulate() {

		myNest.setDoOver(true); 		// to read script input file
		myNest.setSimulate(true);		// to force simulation
		myNest.setReplicate(false);
		try {
			stepUp();
		} catch (Throwable e) {
			logger.warning(e.getMessage());
		}
	}

	/**
	 * In response to GUI, initiates setup; to be done on first use.
	 */
	public void doSetup() {

		gSetup setup = new gSetup(primaryStage, logger, prefs);
		try {
			setup.ask();
		} catch (IOException e) {
			logger.warning(e.getMessage());
		}
	}

	/**
	 * In response to GUI, exits program
	 */
	public void cleanExit() {

		Platform.exit();
	}

	/**
	 * Sets up the standard 'help' scene
	 *
	 * @param _sTitle  string	the Help screen title;
	 * @param _sSource string	the help text file location;
	 * @return Scene to be displayed;
	 */
	private Scene helpScene(String _sTitle, String _sSource) {

		BorderPane helpLayout = new BorderPane();
		helpLayout.setPrefSize(800.0, 500.0);
		Label lbTitle = new Label(_sTitle);
		lbTitle.setStyle(prefs.get("StandardFormat", null));
		HBox topBox = new HBox();
		topBox.setAlignment(Pos.CENTER);
		topBox.setStyle(prefs.get("StandardFormat", null));
		topBox.setPrefHeight(30.0);
		topBox.setStyle("-fx-border-color:chocolate;-fx-border-width:1px;");
		topBox.getChildren().add(lbTitle);
		helpLayout.setTop(topBox);
		Button closeButton = new Button("Close");
		closeButton.setOnAction((event) -> {
			helpSwitch("return");
		});
		HBox bottomBox = new HBox();
		bottomBox.setStyle(prefs.get("StandardFormat", null));
		bottomBox.setAlignment(Pos.BOTTOM_RIGHT);
		bottomBox.setPadding(new Insets(5.0, 5.0, 5.0, 5.0));
		bottomBox.setStyle("-fx-border-color:chocolate;-fx-border-width:1px;");
		bottomBox.getChildren().add(closeButton);
		helpLayout.setBottom(bottomBox);
		//
		String sLocation = _sSource;
		TextStack t = new TextStack(sLocation, prefs, logger);
		VBox vb = t.vStack();
		vb.setStyle("-fx-background-color:beige;");
		helpLayout.setCenter(vb);
		Scene helpScene = new Scene(helpLayout);
		return helpScene;
	}

	/**
	 * In response to GUI initiates 'help' action.
	 * This method allows context switching to help any time. The current screen
	 * content (scene) is stored (only one level), and returned at end of help
	 * screen watching. If necessary, this method could be expanded to use a stack to save screens,
	 *
	 * @param sCommand;
	 */
	public void helpSwitch(String sCommand) {

		switch (sCommand) {
		case "help":										// context specific help
			Boolean bSimulate = myNest.getSimulate();
			bReplicate = myNest.getReplicate();
			String sLocation = null;
			Integer iStep = myNest.getStep();
			if (storedScene == null)
				storedScene = primaryStage.getScene();
			if (bSimulate) {		// get prose from simulation help files
				if (iStep == 1)
					sLocation = "HelpRep_1" + ".tf";
				else
					sLocation = "HelpSim_" + iStep.toString() + ".tf";
			} else {
				if ((iStep == 1) && bReplicate)
					sLocation = "HelpRep_1" + ".tf";
				else
					sLocation = "Help_" + iStep.toString() + ".tf";   // get prose from default (analysis) help files
			}
			primaryStage.setScene(helpScene("Contextual Help", sLocation));
			primaryStage.show();
			break;
		case "intro":										// serves background prose
			if (storedScene == null)
				storedScene = primaryStage.getScene();
			primaryStage.setScene(helpScene("Background", "Background.tf"));
			primaryStage.show();
			break;
		case "return":										// restores pre-help scene
			primaryStage.setScene(storedScene);
			primaryStage.show();
			break;
		case "replicate":
			if (storedScene == null)
				storedScene = primaryStage.getScene();
			primaryStage.setScene(helpScene("replicate", "Replicate.tf"));
			primaryStage.show();
			break;
		default:
			break;
		}
	}


	/**
	 * starts G_String all over again. Resets all switches and Nest to default
	 */
	public void freshStart() {

		myNest = null;
		mySteps = null;
		myNest = new Nest(logger, this, prefs);
		myNest.setStage(primaryStage);
		group = null;
		mySteps = new AnaGroups(this, myNest, logger, controller, primaryStage, prefs, flr);
		controller.callForAction(true);
		try {
			stepUp();
		} catch (Throwable e) {
			logger.warning(e.getMessage());
		}
	}

	/**
	 * In response GUI activates the analysis branch to save the collected results.
	 */
	public void saveAll() {

		try {
			mySteps.saveAll();
		} catch (IOException e) {
			logger.warning(e.getMessage());
		}
	}

	/**
	 * In response to GUI, allows user to set program preferences that will be stored.
	 *
	 * @return, Scene for preferences control;
	 */
	public Scene prefChanger() {

		BorderPane pcLayout = new BorderPane();
		pcLayout.setPrefSize(800.0, 500.0);
		Label lbTitle = new Label("Change Preferences");
		lbTitle.setStyle(prefs.get("StandardFormat", null));
		HBox topBox = new HBox();
		topBox.setAlignment(Pos.CENTER);
		topBox.setStyle(prefs.get("StandardFormat", null));
		topBox.getChildren().add(lbTitle);
		pcLayout.setTop(topBox);
		Button closeButton = new Button("Close");
		closeButton.setOnAction((event) -> {
			switchChangePreferences(false);
		});
		HBox bottomBox = new HBox();
		bottomBox.setAlignment(Pos.BOTTOM_RIGHT);
		bottomBox.setPadding(new Insets(5.0, 5.0, 5.0, 5.0));
		bottomBox.getChildren().add(closeButton);
		pcLayout.setBottom(bottomBox);
		VBox vbPrefs = new VBox();
		// now compile preferences
		List<String> sarKeys = null;
		String sValue = null;
		try {
			sarKeys = Arrays.asList(prefs.keys());
		} catch (Exception e) {
			logger.warning(e.getMessage());
		}
		Collections.sort(sarKeys);

		for (String sKey : sarKeys) {
			sValue = prefs.get(sKey, null);
			vbPrefs.getChildren().add(hbKeyValue(sKey, sValue));
		}
		vbPrefs.setPadding(new Insets(40, 50, 20, 50));
		pcLayout.setCenter(vbPrefs);
		Scene pcScene = new Scene(pcLayout);
		return pcScene;
	}

	/**
	 * primitive helper for 'ChangePreference',
	 * saves or restores previous scene.
	 *
	 * @param bPrefs;
	 */
	public void switchChangePreferences(Boolean bPrefs) {

		if (bPrefs) {
			storedScene = primaryStage.getScene();
			primaryStage.setScene(prefChanger());
			primaryStage.show();
		} else {
			primaryStage.setScene(storedScene);
			primaryStage.show();
		}
	}

	/**
	 * helper in Preferences, displays individual preference.
	 *
	 * @param _sKey		string	the key for a particular preference item
	 * @param _sValue	string	the new value for that preference item
	 * @return HBox, to be displayed in <code>prefChanger</code>;
	 */
	private HBox hbKeyValue(String _sKey, String _sValue) {

		HBox hbReturn = new HBox();
		Label lbKey = new Label(_sKey);
		lbKey.setPrefWidth(150.0);
		TextField tfValue = new TextField(_sValue);
		tfValue.setPrefWidth(500.0);
		tfValue.textProperty().addListener((obs, oldText, newText) -> {
			if (_sKey.equals("Default Log") && !newText.equals(oldText)) {
				if (Level.parse(newText.toUpperCase()) != null)
					prefs.put(_sKey, newText.toUpperCase());
			}
			else
				if ((newText != null) && (!newText.trim().equals("")) && (newText != oldText))
					prefs.put(_sKey, newText);
		});
		hbReturn.getChildren().addAll(lbKey, tfValue);
		return hbReturn;
	}

	/**
	 * https://stackoverflow.com/questions/941754/how-to-get-a-path-to-a-resource-in-a-java-jar-file
	 * to display Brennan's original uRGENOVA manual pdf.
	 * <a href="http://java-buddy.blogspot.com">see also</a>;
	 *
	 * @param sName;
	 * @return File;
	 */
	public File showPDF(String sName) {

		String sResource = sName;
		File docFile = null;
		InputStream input = getClass().getResourceAsStream(sResource);
		try {
			docFile = File.createTempFile("urGenova", ".pdf");
			OutputStream out = new FileOutputStream(docFile);
			int read;
			byte[] bytes = new byte[8192];

			while ((read = input.read(bytes)) != -1)
				out.write(bytes, 0, read);
			out.close();
		} catch (IOException e) {
			logger.warning(e.getMessage());
		}
		return docFile;
	}
	
	/**
	 * User has selected replication analysis.
	 */
	public void replicate() {
		myNest.setDoOver(false);
		bReplicate = true;
		myNest.setReplicate(true);
		mySteps.setReplicate(true);
		try {
			stepUp();					// and then tries going to 'stepUp'
		} catch (Throwable e) {
			logger.warning(e.getMessage());
		}
	}
	
	/**
	 * User has selected replication synthesis
	 */
	public void replicateAgain() {
		myNest.setDoOver(true);
		bReplicate = true;
		myNest.setReplicate(true);
		mySteps.setReplicate(true);
		try {
			stepUp();					// and then tries going to 'stepUp'
		} catch (Throwable e) {
			logger.warning(e.getMessage());
		}
	}
}
