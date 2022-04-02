package application;
	
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.prefs.Preferences;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.stage.Stage;
import model.Nest;
import steps.SynthGroups;
import steps.AnaGroups;
import steps.gSetup;
import utilities.Filer;
import utilities.Popup;
import utilities.process;
import view.TextStack;
import view.rootLayoutController;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ChoiceDialog;
import javafx.scene.control.DialogPane;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

public class Main extends Application {
	/**
	 * Main entry point to G_String_L, stands for 'Legacy'.
	 * Uses standard structure of standard Javafx applications:
	 * main(), start(), and initRootLayout();
	 * This ensures tight integration with rootLayoutController and rootLayout.fxml in the view package.
	 * Main also steers the operations according to menu selections to the actual work routines.
	 * 
	 * All traffic between the UID (rootLayout) and the working code is funneled through the 'Main' class.
	 */
	private Stage primaryStage;
	private BorderPane rootLayout;
	private Boolean bSynthesize = false; // flag for synthesizing mode;
	private Boolean bDawdle = false;
	private static Nest myNest;
	private process myProcess;
	private AnaGroups mySteps;
	private SynthGroups mySynthSteps;
	private Scene scene0;
	private rootLayoutController controller;
	private Group group;
	private Logger logger;
	private Scene storedScene = null;
	private Preferences root = Preferences.userRoot();
	final private Preferences prefs = root.node("/com/papaworx/gstring");
	private Scene newScene = null;
	private Filer flr;
	private Popup popup;
	private String sLogPath = null;

	public static void main(String[] args) {
		/**
		 * Obligatory javafx main
		 */
		
		launch(args);
	}
	
	@Override
	public void start(Stage primaryStage) {

		/**
		 * Initializes the root layout. Standard javafx.
		 * This system method is somewhat cryptic. It gets called by the system,
		 * and hands a handle for the primary stage to the program.
		 * First, setting up objects to be used throughout.
		 */
		
		String homeDir = System.getProperty("user.home");
		sLogPath = prefs.get("Home Directory", homeDir) + File.separator + "com.papaworx.gstring.Log";
				// logger targets log output to file "com.papaworx.gstring.Log" in the user's current directory
		File fLog = new File(sLogPath);
		if (fLog.exists())								// cleans up existing log files
			fLog.delete();
		FileHandler fh = null;							// just for initialization
		try {
			fh = new FileHandler(sLogPath, true);		// log handler, creates append logs, rather than new ones
		} catch (SecurityException | IOException e1) {
			e1.printStackTrace(); 						// emergency exit
		}
		String sLogLevel = prefs.get("Default Log", "Warning");		// sets log level to preferred
		Level lCurrent = Level.parse(sLogLevel);
		logger = Logger.getLogger(Main.class.getName());
		logger.addHandler(fh);
		logger.setLevel(lCurrent);						// that's the logger, used throughout
		popup = new Popup(logger, primaryStage);		// standard diagnostic message handler
		popup.setClass("Main");
		myNest = new Nest(popup, this, prefs);
		flr = new Filer(myNest, prefs, popup, primaryStage);
		this.primaryStage = primaryStage;
		myNest.setStage(primaryStage);
		this.primaryStage.setTitle("G_String_L (Java)");
		group = null;
		myProcess = new process(myNest);
		initRootLayout();								// initializes standard GUI layout
		mySteps = new AnaGroups(myNest, popup, controller, primaryStage, prefs, flr); 	// object for analysis
		mySynthSteps = new SynthGroups(myNest, popup, controller, prefs, flr);	// object for synthesis
		try {
			stepUp();	// now ready for work
		} catch (Throwable e) {
			popup.tell("Main_a", e);
		}
	}
	
		public void initRootLayout() {
		/**
		 * Standard javafx; loads initial root layout stage
		 */
			
		try {
			URL fxmlLocation = getClass().getResource("/view/rootLayout.fxml");
			FXMLLoader loader = new FXMLLoader(fxmlLocation);
			//loader.setRoot(this);
			rootLayout = loader.load();

			// Show the scene containing the root layout.
			scene0 = new Scene(rootLayout);
			myNest.setScene(scene0);
			controller = loader.getController();
			controller.setMainApp(this, popup, prefs);
		} catch (IOException e) {
			popup.tell("initRootLayout_a", e);
			e.printStackTrace();
		}
	}

	public Stage getPrimaryStage() {
	/**
	 * Makes the primary stage available by a call to 'main'
	 * 
	 */
		return primaryStage;
	}

	public void stepUp() {
		/**
		 * Except for very localized messages, the graphical user interface (GUI) is standardized.
		 * Throughout G_String, the objects create javafx scenes called 'groups', which are handed to the 'show' subroutine.
		 * User interactions with the GUI are directly fed back to appropriate methods of Main.
		 * Responds to GUI 'Next' button.
		 */
		
		Integer iStep = myNest.getStep();
		if (!myNest.getSimulate()) // if in analysis mode
		{
			/**
			 * This is the default analysis path
			 */
			myProcess.getStep(false);
			controller.setStep(iStep);
			try {
				group = mySteps.getGroup(false);
			} catch (Throwable e) {
				popup.tell("stepUp_a", e);
			}
			if (group == null)
				switch (iStep) {
				case 1:
					freshStart();
					break;
				case 7:
					stepDown();
					break;
				case 9:
					myNest.setStep(7);
					stepDown();
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
			controller.setStep(iStep);
			mySynthSteps.setDownStep(false);
			if (!bDawdle)
				// only if all variance components have been selected
				myNest.incrementSteps();
			try {
				group = mySynthSteps.getGroup(false);
			} catch (Throwable e) {
				popup.tell("stepUp_b", e);
			}
			show(group);
		}
	}

	public void stepDown() {
		/**
		 * provides limited roll back.
		 */
		
		Integer iStep = myNest.getStep();
		myProcess.getStep(true);
		controller.setStep(iStep);
		if (iStep == 1)
			freshStart();
		else {
			try {
				group = mySteps.getGroup(true);
			} catch (Throwable e) {
				popup.tell("stepDown_a", e);
			}
			if (group == null)
				freshStart();
			else
				show(group);
		}
	}

	public void show(Group _display) {
		/**
		 * Central display king pin. Receives scene (group), and passes it on to the GUI.
		 */
		
		BorderPane frame = (BorderPane) scene0.getRoot();
		frame.setCenter(_display);
		newScene = scene0;
		newScene.setRoot(frame);
		primaryStage.setScene(newScene);
		primaryStage.show();
	}

	public void startFresh() {
		/**
		 * In response to GUI sets switches for manual analysis
		 */
		
		myNest.setDoOver(false);
		try {
			stepUp();
		} catch (Throwable e) {
			popup.tell("setDoOver_a", e);
		}
	}

	public void startOver() {
		/**
		 * In response to GUI sets switches for script driven analysis
		 */
		
		myNest.setDoOver(true);			// sets the 'doOver' Boolean in 'Nest'
		try {
			stepUp();					// and then tries going to 'stepUp'
		} catch (Throwable e) {
			popup.tell("startOver", e);
		}
	}
	public void Simulate() {
		/**
		 * In response to GUI set switches for manual simulation
		 */
		
		myNest.setDoOver(false); 		// only manual input
		myNest.setSimulate(true); 		// simulate
		try {
			stepUp();
		} catch (Throwable e) {
			popup.tell("Simulate_a", e);
		}
	}

	public void Resimulate() {
		/**
		 * In response to GUI sets switches for script driven simulation.
		 */
		
		myNest.setDoOver(true); 		// to read input file
		myNest.setSimulate(true);
		try {
			stepUp();
		} catch (Throwable e) {
			popup.tell("reSimulate_a", e);
		}
	}

	public boolean getSynth() {
		/**
		 * returns synthesize switch
		 */
		
		return bSynthesize; 
	}

	public void setSynthesize(Boolean _bSynth) {
		/**
		 * Sets synthesize switch
		 */
		
		bSynthesize = _bSynth; 
	}


	public void doSetup() {
		/**
		 * In response to GUI, initiates setup; to be done on first use.
		 */
		
		gSetup setup = new gSetup(primaryStage, popup, prefs);
		try {
			setup.ask();
		} catch (IOException e) {
			popup.tell("doSetUp_a", e);
		}
	}

	public void cleanExit() {
		/**
		 * In response to GUI, exits program
		 */
		
		Platform.exit();
	}

	private Scene helpScene(String _sTitle, String _sSource) {
		/**
		 * Set up the standard 'help' scene
		 */
		
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
		TextStack t = new TextStack(sLocation, prefs, popup);
		VBox vb = t.vStack();
		vb.setStyle("-fx-background-color:beige;");
		helpLayout.setCenter(vb);
		Scene helpScene = new Scene(helpLayout);
		return helpScene;
	}

	public void helpSwitch(String _sCommand) {
		/**
		 * For 'help' actions that don't require an explicit pointer to a context help screen.
		 */
		helpSwitch(_sCommand, null);
	}
	

	public void helpSwitch(String sCommand, Integer iDetail) {
		/**
		 * In response to GUI initiates 'help' action.
		 * This method allows context switching to help any time. The current screen
		 * content (scene) is stored (only one level), and returned at end of help 
		 * screen watching. If necessary, this method could be expanded to use a stack to save screens,
		 */
		
		switch (sCommand) {
		case "help":										// context specific help
			Boolean bSimulate = myNest.getSimulate();
			String sLocation = null;
			if (storedScene == null)
				storedScene = primaryStage.getScene();
			if (bSimulate) {								// get prose from simulation help files
				sLocation = "HelpSim_" + myNest.getStep().toString() + ".tf";
			} else {										// get prose from default (analysis) help files
				sLocation = "Help_" + myNest.getStep().toString() + ".tf";
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
		default:
			break;
		}
	}

	public void SetLogLevel() {
		/**
		 * allows user to select logging level for current operation only.
		 * After every new start the log level reverts to to the level set in preferences.
		 */
		
		List<Level> choices = new ArrayList<>();
		choices.add(Level.OFF);
		choices.add(Level.SEVERE);
		choices.add(Level.WARNING);
		choices.add(Level.INFO);
		choices.add(Level.FINEST);
		choices.add(Level.ALL);
		String sInitialLevel = prefs.get("Default Log", "OFF");
		Level lDefault = Level.parse(sInitialLevel);
		ChoiceDialog<Level> dialog = new ChoiceDialog<>(lDefault, choices);
		dialog.setTitle("Choice Dialog");
		dialog.setHeaderText("Pick your Log Level");
		DialogPane dp = dialog.getDialogPane();
		dp.getStylesheets().add("/resources/myDialog.css");
		dp.getStyleClass().add("myDialog");
		ButtonBar buttonBar = (ButtonBar) dp.lookup(".button-bar");
		buttonBar.getButtons()
				.forEach(b -> b.setStyle("-fx-font-size: 16;-fx-background-color: #551200;-fx-text-fill: #ffffff;"));

		Optional<Level> result = dialog.showAndWait();			// Traditional way to get the response value.
			result.ifPresent(selection -> logger.setLevel(selection));
	}

	public void freshStart() {
		/**
		 * starts G_String all over again. Resets all switches and Nest to default
		 */
		
		myNest = null;
		mySteps = null;
		myNest = new Nest(popup, this, prefs);
		myNest.setStage(primaryStage);
		group = null;
		myProcess = new process(myNest);
		mySteps = new AnaGroups(myNest, popup, controller, primaryStage, prefs, flr);
		controller.callForAction(true);
		try {
			stepUp();
		} catch (Throwable e) {
			popup.tell("freshStart_a", e);
		}
	}

	public void saveAll() {
		/**
		 * In response GUI activates the analysis branch to save the collected results.
		 */
		try {
			mySteps.saveAll();
		} catch (IOException e) {
			popup.tell("saveAll_a", e);
		}
	}

	public Scene prefChanger() {
		/**
		 * In response to GUI, allows user to set program preferences that will be stored.
		 */
		
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
			popup.tell("228a", e);
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

	public void switchChangePreferences(Boolean bPrefs) {
		/**
		 * primitive helper for 'ChangePreference',
		 * saves or restores previous scene.
		 */
		
		if (bPrefs) {
			storedScene = primaryStage.getScene();
			primaryStage.setScene(prefChanger());
			primaryStage.show();
		} else {
			primaryStage.setScene(storedScene);
			primaryStage.show();
		}
	}

	private HBox hbKeyValue(String _sKey, String _sValue) {
		/**
		 * helper in Preferences
		 */
		
		HBox hbReturn = new HBox();
		Label lbKey = new Label(_sKey);
		lbKey.setPrefWidth(150.0);
		TextField tfValue = new TextField(_sValue);
		tfValue.setPrefWidth(500.0);
		tfValue.textProperty().addListener((obs, oldText, newText) -> {
			if ((newText != null) && (!newText.trim().equals("")) && (newText != oldText))
				prefs.put(_sKey, newText);
		});
		hbReturn.getChildren().addAll(lbKey, tfValue);
		return hbReturn;
	}

	public File showPDF(String sName) {
		/**
		 * @web http://java-buddy.blogspot.com/
		 * https://stackoverflow.com/questions/941754/how-to-get-a-path-to-a-resource-in-a-java-jar-file
		 * to display Brennan's original uRGENOVA manual pdf.
		 */
		
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
			popup.tell("showPDF_a", e);
		}
		return docFile;
	}

}