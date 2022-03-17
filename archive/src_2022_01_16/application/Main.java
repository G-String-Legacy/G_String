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
import steps.dGroups;
import steps.gSetup;
import utilities.Filer;
import utilities.Popup;
import utilities.TextStack;
import utilities.process;
import view.rootLayoutController;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ChoiceDialog;
import javafx.scene.control.DialogPane;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;


public class Main extends Application {
	private Stage primaryStage;
	private BorderPane rootLayout;
	private Boolean bSynthesize = false; // flag for synthesizing mode;
	private Boolean bDawdle = false;
	private static Nest myNest;
	private process myProcess;
	private dGroups mySteps;
	private SynthGroups mySynthSteps;
	private Scene scene0;
	private rootLayoutController controller;
	private Group group;
	private Logger logger;
	private Scene storedScene = null;
	private Preferences root = Preferences.userRoot();
	final private Preferences prefs = root.node("/com/papaworx/gstring");
	private Scene newScene = null;
	private Integer infoPage = 0;
	private Filer flr;
	private Popup popup;
	private String sLogPath = null;

	public static void main(String[] args) {
		launch(args);
	}
	
	@Override
	public void start(Stage primaryStage) {

		/**
		 * Initializes the root layout.
		 */
		String homeDir = System.getProperty("user.home");
		sLogPath = prefs.get("Home Directory", homeDir) + File.separator + "com.papaworx.gstring.Log";
		File fLog = new File(sLogPath);
		if (fLog.exists())
			fLog.delete();
		FileHandler fh = null;		// just for initialization
		try {
			fh = new FileHandler(sLogPath, true);
		} catch (SecurityException | IOException e1) {
			// unanounced emergency exit
			e1.printStackTrace();
		}
		String sLogLevel = prefs.get("Default Log", "Warning");
		Level lCurrent = Level.parse(sLogLevel);
		logger = Logger.getLogger(Main.class.getName());
		logger.addHandler(fh);
		logger.setLevel(lCurrent);
		Popup popup = new Popup(logger, primaryStage);
		popup.setObject(2, 14);
		myNest = new Nest(popup, this, prefs);
		flr = new Filer(myNest, prefs, popup);
		popup.setFiler(flr);
		this.primaryStage = primaryStage;
		myNest.setStage(primaryStage);
		this.primaryStage.setTitle("G_String_VI (Java)");
		group = null;
		myProcess = new process(myNest);
		initRootLayout();
		mySteps = new dGroups(myNest, logger, controller, primaryStage, prefs, flr, popup);
		mySynthSteps = new SynthGroups(myNest, popup, controller, primaryStage, prefs);
		try {
			stepUp();
		} catch (Throwable e) {
			popup.tell("211a", e);
		}
	}
		public void initRootLayout() {
		/// This routine sets up the container display (Root Layout) which will
		/// house the individual step windows
		try {
			// Load root layout from fxml file.
			URL fxmlLocation = getClass().getResource("/view/rootLayout.fxml");
			FXMLLoader loader = new FXMLLoader(fxmlLocation);
			//loader.setRoot(this);
			rootLayout = loader.load();

			// Show the scene containing the root layout.
			scene0 = new Scene(rootLayout);
			myNest.setScene(scene0);
			controller = loader.getController();
			controller.setMainApp(this, logger, prefs);
		} catch (IOException e) {
			popup.tell("212a", e);
			e.printStackTrace();
		}
	}

	public Stage getPrimaryStage() {
		// Returns primary Stage
		return primaryStage;
	}

	public void stepUp() {
		// initiates 'next step, every time the 'Next' button is pressed.
		Integer iStep = myNest.getStep();
		if (!myNest.getSimulate()) // if in analysis mode
		{
			myProcess.getStep(false);
			controller.setStep(iStep);
			try {
				group = mySteps.getGroup(false);
			} catch (Throwable e) {
				popup.tell("214a", e);
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
		} else { // if in synthesize mode
			// we only do stepup at this time
			controller.setStep(iStep);
			if (!bDawdle)
				// only if all variance components have been selected
				myNest.incrementSteps();
			try {
				group = mySynthSteps.getGroup(false);
			} catch (Throwable e) {
				popup.tell("214b", e);
			}
			// }
			show(group);
		}
	}

	public void stepDown() {
		// Initiates a roll back, every time the 'Previous' button is pressed.
		Integer iStep = myNest.getStep();
		myProcess.getStep(true);
		controller.setStep(iStep);
		if (iStep == 1)
			freshStart();
		else {
			try {
				group = mySteps.getGroup(true);
			} catch (Throwable e) {
				popup.tell("215a", e);
			}
			if (group == null)
				freshStart();
			else
				show(group);
		}
	}

	public void show(Group _display) {
		BorderPane frame = (BorderPane) scene0.getRoot();
		frame.setCenter(_display);
		newScene = scene0;
		newScene.setRoot(frame);
		primaryStage.setScene(newScene);
		primaryStage.show();
		//System.out.println("Hold!");
	}

	public void startFresh() {
		// Action to start from scratch
		myNest.setDoOver(false);
		try {
			stepUp();
		} catch (Throwable e) {
			popup.tell("218a", e);
		}
	}

	public void startOver() {
		// Action to start with existing control file
		myNest.setDoOver(true);
		try {
			stepUp();
		} catch (Throwable e) {
			popup.tell("219a", e);
		}
	}

	public void doSetup() {
		gSetup setup = new gSetup(primaryStage, popup, prefs);
		try {
			setup.ask();
		} catch (IOException e) {
			popup.tell("220a", e);
		}
	}

	public void cleanExit() {
		Platform.exit();
	}

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
		TextStack t = new TextStack(sLocation, prefs, popup);
		VBox vb = t.vStack();
		vb.setStyle("-fx-background-color:beige;");
		helpLayout.setCenter(vb);
		Scene helpScene = new Scene(helpLayout);
		return helpScene;
	}

	public void helpSwitch(String _sCommand) {
		helpSwitch(_sCommand, null);
	}

	public void helpSwitch(String sCommand, Integer iDetail) {
		switch (sCommand) {
		case "help":
			Boolean bSimulate = myNest.getSimulate();
			String sLocation = null;
			if (storedScene == null)
				storedScene = primaryStage.getScene();
			if (bSimulate) {
				sLocation = "HelpSim_" + myNest.getStep().toString() + ".tf";
			} else {
				sLocation = "Help_" + myNest.getStep().toString() + ".tf";
			}
			primaryStage.setScene(helpScene("Contextual Help", sLocation));
			primaryStage.show();
			break;
		case "info":
			if (storedScene == null)
				storedScene = primaryStage.getScene();
			primaryStage.setScene(infoScene(iDetail, "urGenova"));
			primaryStage.show();
			break;
		case "intro":
			if (storedScene == null)
				storedScene = primaryStage.getScene();
			primaryStage.setScene(helpScene("Background", "Background.tf"));
			primaryStage.show();
			break;
		case "return":
			primaryStage.setScene(storedScene);
			primaryStage.show();
			break;
		default:
			break;
		}
	}

	public void SetLogLevel() {
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

		// Traditional way to get the response value.
		Optional<Level> result = dialog.showAndWait();
			result.ifPresent(selection -> logger.setLevel(selection));
	}

	public void freshStart() {
		/**
		 * starts G_String all over again
		 */
		myNest = null;
		mySteps = null;
		myNest = new Nest(popup, this, prefs);
		myNest.setStage(primaryStage);
		group = null;
		myProcess = new process(myNest);
		mySteps = new dGroups(myNest, logger, controller, primaryStage, prefs, flr, popup);
		controller.callForAction(true);
		try {
			stepUp();
		} catch (Throwable e) {
			popup.tell("226a", e);
		}
	}

	public void saveAll() {
		try {
			mySteps.saveAll();
		} catch (IOException e) {
			popup.tell("227a", e);
		}
	}

	public Scene prefChanger() {
		// Preference Changer Scene
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

	private Scene infoScene(Integer _iPage, String _sTitle) {
		final String[] sIDs = { "i", "ii", "iii", "iv", "v", "1", "2", "3", "4", "5", "6", "7", "8" };
		final ImageView imvInfo = new ImageView();
		if (_iPage != null)
			infoPage = _iPage;
		else
			infoPage = 0;
		String sButtonStyle = "-fx-background-color:saddlebrown;-fx-text-fill:WHITE;";
		String sPage = "Resources/help/urGenova_" + sIDs[infoPage] + ".png";
		final Image imInfo = new Image(Main.class.getResourceAsStream(sPage));
		imvInfo.setImage(imInfo);
		BorderPane infoLayout = new BorderPane();
		infoLayout.setPrefSize(800.0, 500.0);
		Integer iMax = sIDs.length;
		Button bLeft = new Button("<");
		bLeft.setStyle(sButtonStyle);
		bLeft.setOnAction((event) -> {
			if (infoPage > 0) {
				infoPage--;
				helpSwitch("info", infoPage);
			}
		});
		Button bRight = new Button(">");
		bRight.setStyle(sButtonStyle);
		bRight.setOnAction((event) -> {
			if (infoPage < iMax - 1) {
				infoPage++;
				helpSwitch("info", infoPage);
			}
		});
		Label lbTitle = new Label(_sTitle);
		lbTitle.setStyle(sButtonStyle);
		lbTitle.setPadding(new Insets(0, 200, 0, 200));
		HBox topBox = new HBox();
		topBox.setAlignment(Pos.CENTER);
		// topBox.setStyle(prefs.get("StandardFormat", null));
		topBox.setStyle(sButtonStyle);
		topBox.setPrefHeight(30.0);
		// topBox.setStyle("-fx-border-color:chocolate;-fx-border-width:1px;");
		topBox.getChildren().addAll(bLeft, lbTitle, bRight);
		infoLayout.setTop(topBox);
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
		infoLayout.setBottom(bottomBox);
		VBox vb = new VBox();
		vb.setStyle("-fx-background-color:beige;");
		vb.getChildren().add(imvInfo);
		infoLayout.setCenter(vb);
		Scene helpScene = new Scene(infoLayout);
		return helpScene;
	}

	public File showPDF(String sName) {
		/**
		 * @web http://java-buddy.blogspot.com/
		 *      https://stackoverflow.com/questions/941754/how-to-get-a-path-to-a-resource-in-a-java-jar-file
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
			popup.tell("232a", e);
		}
		return docFile;
	}

	public void Simulate() {
		myNest.setDoOver(false); // only manual input
		myNest.setSimulate(true);
		try {
			stepUp();
		} catch (Throwable e) {
			popup.tell("232a", e);
		}
	}

	public void Resimulate() {
		// Action to start with existing control file
		myNest.setDoOver(true); // to read input file
		myNest.setSimulate(true);
		try {
			stepUp();
		} catch (Throwable e) {
			popup.tell("234a", e);
		}
	}

	public boolean getSynth() {
		return bSynthesize; // to access mode
	}

	public void setSynthesize(Boolean _bSynth) {
		bSynthesize = _bSynth; // reset mode
	}
}
