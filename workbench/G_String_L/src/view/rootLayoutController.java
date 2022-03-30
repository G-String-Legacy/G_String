package view;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.prefs.BackingStoreException;
import java.util.prefs.InvalidPreferencesFormatException;
import java.util.prefs.Preferences;

import utilities.About;
import utilities.Popup;
import javafx.application.HostServices;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.BorderPane;
import javafx.stage.FileChooser;

public class rootLayoutController {

	private application.Main myMain;
	private Popup popup = null;
	private Preferences prefs;
	private String homeDir = null;

	@FXML
	private BorderPane bpScreen;
	@FXML
	private MenuItem mnuExit;
	@FXML
	private MenuItem mnuActionFresh;
	@FXML
	private MenuItem mnuActionStartOver;
	@FXML
	private MenuItem mnuSimulate;
	@FXML
	private MenuItem mnuResimulate;
	@FXML
	private MenuItem mnuLogLevel;
	@FXML
	private MenuItem mnuSetup;
	@FXML
	private MenuItem mnuPreferences;
	@FXML
	private MenuItem mnuCHelp;
	@FXML
	private MenuItem mnuIntro;
	@FXML
	private MenuItem mnuAbout;
	@FXML
	private MenuItem mnuUHelp;
	@FXML
	private MenuItem mnuAboutB;
	@FXML
	private MenuItem mnuStart;
	@FXML
	private MenuItem mnuSaveAll;
	@FXML
	private MenuItem mnuChangePrefs;
	@FXML
	private MenuItem mnuLoadPrefs;
	@FXML
	private MenuItem mnuSavePrefs;
	@FXML
	private Button btnStepUp;
	@FXML
	private Label lblStep;
	@FXML
	private Menu mnuAction;

	@FXML
	void typedBS(KeyEvent event) {
	    if (event.getCode() == KeyCode.BACK_SPACE) {
	        lblStep.setText(event.getText() + " typed.");
	    }
	}
	public void initialize() {
		mnuExit.setOnAction((event) -> {
			respond();
		});
		btnStepUp.setOnAction((event) -> myMain.stepUp());
		mnuActionStartOver.setOnAction((event) -> {
			myMain.startOver();
		});
		mnuCHelp.setOnAction((event) -> {
			myMain.helpSwitch("help");
		});
		mnuIntro.setOnAction((event) -> {
			myMain.helpSwitch("intro");
		});
		mnuUHelp.setOnAction((event) -> {
			displayResource("/resources/urGENOVA_manual.pdf");
		});
		mnuSimulate.setOnAction((event) -> {
			myMain.Simulate();
		});
		mnuResimulate.setOnAction((event) -> {
			myMain.Resimulate();
		});
		mnuLogLevel.setOnAction((event) -> {
			myMain.SetLogLevel();
		});
		mnuSetup.setOnAction((event) -> {
			myMain.doSetup();
		});
		mnuChangePrefs.setOnAction((event) -> {
			myMain.switchChangePreferences(true);
		});
		mnuLoadPrefs.setOnAction((event) -> {
			loadPreferences();
		});
		mnuSavePrefs.setOnAction((event) -> {
			savePreferences();
		});
		mnuAbout.setOnAction((event) -> {
			about();
		});
		mnuAboutB.setOnAction((event) -> {
			aboutB();
		});
		mnuStart.setOnAction((event) -> {
			myMain.freshStart();
		});
		mnuSaveAll.setOnAction((event) -> {
			myMain.saveAll();
		});
		lblStep.setAlignment(Pos.CENTER);
		lblStep.setText("Step 0");
		mnuActionFresh.setOnAction((event) -> {
			myMain.startFresh();
		});
	}

	public void setMainApp(application.Main _main, Popup _popup, Preferences _prefs) {
		myMain = _main;
		popup = _popup;
		popup.setClass("rootLayoutController");
		prefs = _prefs;
		homeDir = System.getProperty("user.home");
	}

	public void buttonsEnabled(boolean b) {
		btnStepUp.setDisable(!b);
		mnuActionFresh.setDisable(b);
		mnuActionStartOver.setDisable(b);
	}

	private void respond() {
		System.exit(0);
	}

	public void setStep(Integer _iStep) {
		lblStep.setText("Step " + _iStep);
		if (_iStep == 0)
			buttonsEnabled(false);
		else
			buttonsEnabled(true);
	}

	public void lockSetup() {
		mnuSetup.setDisable(true);
	}

	private void about() {
		About myAbout = new About(myMain.getPrimaryStage(), popup, "/resources/About.txt", "About G_String_L");
		myAbout.show();
	}

	private void aboutB() {
		About myAbout = new About(myMain.getPrimaryStage(), popup, "/resources/AboutB.txt", "About urGenova");
		myAbout.show();
	}

	public void disableSave(Boolean bDisable) {
		mnuSaveAll.setDisable(bDisable);
	}

	private void loadPreferences() {
		InputStream sIn = null;
		File selectedFile = null;
		FileChooser fc = new FileChooser();
		fc.setTitle("Select Preferences File to load");
		FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("XML files (*.xml)", "*.xml");
		fc.getExtensionFilters().add(extFilter);
		fc.setSelectedExtensionFilter(extFilter);
		fc.setInitialDirectory(new File(homeDir));
		try {
			selectedFile = fc.showOpenDialog(myMain.getPrimaryStage());
		} catch (Exception e) {
			popup.tell("1266a", e);
		}
		if (selectedFile != null) {
			try {
				sIn = new FileInputStream(selectedFile);
			} catch (FileNotFoundException e) {
				popup.tell("1266b", e);
			}
			try {
				Preferences.importPreferences(sIn);
			} catch (IOException e) {
				popup.tell("1266c", e);
			} catch (InvalidPreferencesFormatException e) {
				popup.tell("1266d", e);
			}
		}

	}

	private void savePreferences() {
		OutputStream sOut = null;
		File selectedFile = null;
		FileChooser fc = new FileChooser();
		fc.setTitle("Select Preferences File to save");
		FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("XML files (*.xml)", "*.xml");
		fc.getExtensionFilters().add(extFilter);
		fc.setSelectedExtensionFilter(extFilter);
		fc.setInitialDirectory(new File(homeDir));
		try {
			selectedFile = fc.showSaveDialog(myMain.getPrimaryStage());
		} catch (Exception e) {
			popup.tell("1267a", e);
		}
		if (selectedFile != null) {
			try {
				sOut = new FileOutputStream(selectedFile);
			} catch (FileNotFoundException e) {
				popup.tell("1267b", e.getMessage());
			}
			try {
				prefs.exportNode(sOut);
			} catch (IOException e) {
				popup.tell("1267c", e.getMessage());
			} catch (BackingStoreException e) {
				popup.tell("1267d", e.getMessage());
			}
		}
	}

	public void enableStepUp(Boolean bEnable) {
		btnStepUp.setDisable(!bEnable);
	}

	public void callForAction(Boolean bCall) {
		if (bCall) {
			mnuAction.setStyle("-fx-border-color:chocolate;");
			mnuAction.setDisable(false);
		} else {
			mnuAction.setStyle(null);
			mnuAction.setDisable(true);
		}
	}

	private void displayResource(String _sName) {
		File docFile = myMain.showPDF(_sName);
		HostServices hostServices = myMain.getHostServices();
		hostServices.showDocument(docFile.toURI().toString());
		docFile.deleteOnExit();
	}
}
