package steps;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.logging.Logger;
import java.util.prefs.Preferences;

import application.Main;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.RadioButton;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;
import javafx.scene.control.SplitPane;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.DragEvent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import javafx.util.Callback;
import model.Facet;
import model.Nest;
import model.SampleSizeTree;
import utilities.FacetModView;
import utilities.Filer;
import view.rootLayoutController;

/**
 * Manages G-Analysis by taking the user step-by-step
 * through the whole procedure from entering the project title to
 * performing an arbitrary number of D-Studies. Altogether there are 10 steps,
 * and proceeding to the next step is only possible after 'grammatically'
 * correct input. Obviously, users can still enter parameters that do not correspond to
 * their actual study. At each step a context specific help screen is available.
 * As result of the user responses at a given step, the entered data are stored, and the GUI
 * scene for the next step is generated, and handed via 'main' back to the GUI.
 * Users can step through the analysis either manually, by entering the design information
 * via keyboard and mouse, or they can pick the 'do-over' mode, where the program
 * reads a control file (script), prepared in a previous analysis run, and the user just has
 * to enter changes.
 *
 * @see <a href="https://github.com/G-String-Legacy/G_String/blob/main/workbench/GS_L/src/steps/AnaGroups.java">steps.AnaGroups</a>
 * @author ralph
 * @version %v..%
 */
public class AnaGroups {

	/**
	 * JavaFX class managing GUI
	 */
	private rootLayoutController myController;

	/**
	 * stores graphical data
	 */
	private String customBorder;

	/**
	 * graphical element for displaying crossed facet designations
	 */
	private static ListView<String> lvCrossed = null;

	/**
	 * graphical element for displaying nested facet designations
	 */
	private static ListView<String> lvNested = null;

	/**
	 * graphical element for displaying all facet designations
	 */
	private static ListView<String> lvFacets = null;

	/**
	 * A list that allows listeners to track changes of nested Data when they occur.
	 */
	private ObservableList<String> nestedData;

	/**
	 * A list that allows listeners to track changes of crossed Data when they occur.
	 */
	private ObservableList<String> crossedData;

	/**
	 * lower limit for list (drag and drop)
	 */
	private Integer iFrom;
	
	/**
	 * upper limit for list (drag and drop)
	 */
	private Integer iTo;
	
	/**
	 * Facet designation char for starred Facet
	 */
	private char cAsterisk = ' ';
	
	/**
	 * char designation of replicating facet.
	 */
	private char cReplicate = '-';

	/**
	 * integer location of element in list
	 */
	private Integer iPointer;

	/**
	 * <code>Nest</code> parameter repository defining whole assessment
	 */
	private Nest myNest;

	/**
	 * <code>SampleSizeTree</code> sample size repository for facets and nesting
	 */
	private SampleSizeTree myTree = null;

	/**
	 * sample size intermediary result
	 */
	private Integer iSample = 0;

	/**
	 * string containing all Facet designation chars in original order
	 */
	private String sDictionary = null;

	/**
	 * string containing all Facet designation chars in hierarchical order
	 */
	private String sHDictionary;

	/**
	 * pointer to <code>Filer</code>
	 */
	private Filer flr = null;

	/**
	 * descriptor of text display style
	 */
	private String sStyle_20 = null;

	/**
	 * descriptor of text display style
	 */
	private String sStyle_18 = null;

	/**
	 * pointer to <code>Preferences</code>
	 */
	private Preferences prefs = null;

	/**
	 * pointer to input file
	 */
	private File selectedFile = null;

	/**
	 * name of control file ('script')
	 */
	private String sControlFileName = null;

	/**
	 * javafx element for text display
	 */
	private TextArea taOutput = null;

	/**
	 * Flag indicating Replication mode
	 */
	private Boolean bReplicate = false;

	/**
	 * Flag for first analysis
	 */
	private Boolean bFirstAnalysis = true;

	/**
	 * boolean used for setup
	 */
	private StringBuilder sbResult; // accumulates results

	/**
	 * pointer to Main
	 */
	private Main myMain;
	
	/**
	 * pointer to Logger
	 */
	private Logger logger;

	/**
	 * constructor of <code>AnaGroups</code>
	 *
	 * @param _main  pointer to <code>main</code> method of class <code>Main</code>
	 * @param _nest  <code>Nest</code>
	 * @param _logger  pointer to application logger
	 * @param _controller  <code>rootLayoutController</code>
	 * @param _stage  <code>primaryStage</code> of GUI
	 * @param _prefs  <code>Preferences</code>
	 * @param _flr  <code>Filer</code>
	 */
	public AnaGroups(Main _main, Nest _nest, Logger _logger, rootLayoutController _controller, Stage _stage, Preferences _prefs, Filer _flr) {

		myMain = _main;
		myNest = _nest;
		flr = _flr;
		logger = _logger;
		
		sControlFileName = myNest.getControlFileName();
		myController = _controller;
		nestedData = FXCollections.observableArrayList();
		crossedData = FXCollections.observableArrayList();
		iFrom = -1;
		iTo = -1;
		iPointer = 0;
		prefs = _prefs;
		sStyle_18 = prefs.get("Style_18",
				"-fx-font-size: 18px; -fx-font-family: \"ARIAL\"; -fx-padding: 10; -fx-background-color: #805015; -fx-text-fill: #FFFFFF;");
		sStyle_20 = prefs.get("Style_20",
				"-fx-font-size: 30px; -fx-font-family: \"ARIALSerif\"; -fx-padding: 10; -fx-background-color: #805015; -fx-text-fill: #FFFFFF;");
		customBorder = prefs.get("Border", null);
	}

	/**
	 * This method uses the java 'switch/case' construct to
	 * make sure that it constructs the correct scene for a given step,
	 * based on the data entered previously. This scene is then
	 * handed back to 'main' that passes it to the GUI.
	 * The 'popup' call at each step collects ongoing status
	 * information, that can be optionally fed to a log file
	 * for diagnostic use.
	 * At each step 'getGroup' returns a JavaFX Group object, constructed
	 * as result of the the cumulative information entered. The 'Group'
	 * goes to 'Main', where it is packaged into a JavaFX 'Scene',
	 * which is then handed to the 'rootLayoutController'.
	 * Some steps can generate their 'Group' directly, others
	 * need the assistance of further methods contained in this package
	 *
	 * @return <code>Group</code> essentially the 'Scene' to be sent to the GUI
	 * @throws Throwable IOException
	 */
	public Group getGroup() throws Throwable {
		Integer iStep = myNest.getStep();
		if (iStep == 0)
			myController.callForAction(true);
		else
			myController.callForAction(false);
		myController.setStep(iStep);
	 	switch (iStep) {
		// step 0  initialize AnaGroups
	 	case 0:
			try {
				myController.buttonsEnabled(false);
				myController.disableSave(true);
				myNest.setDoOver(false);
				return startUp();
			} catch (Exception e) {
				myLogger(0, logger, e);
			}
		// step 1  enter script title
		case 1:
			bReplicate = myNest.getReplicate();
			myController.buttonsEnabled(true);
			myController.enableStepUp(true);
			if (myNest.getDoOver())
				readOld();
			try {
				cReplicate = myNest.get_cRep();
					
				return setTitle();
			} catch (Exception e) {
				myLogger(1, logger, e);
			}
		// step 2  add comments
		case 2:
			try {
				return addComments();
			} catch (Exception e) {
				myLogger(2, logger, e);
			}
		// step 3  specify Facet of differentiation  and number of additional Facets
		case 3:
			try {
				return mainSubjectGroup();
			} catch (Exception e) {
				myLogger(2, logger, e);
			}
		// step 4  add additional Facets
		case 4:
			try {
				return subjectsGroup();
			} catch (Exception e) {
				myLogger(4, logger, e);
			}
		// step 5  set the desired order of Facets
		case 5:
			try {
				myTree = myNest.getTree();
				return orderFacets();
			} catch (Exception e) {
				myLogger(5, logger, e);
			}
		// step 6  arrange nesting of Facets
		case 6:
			try {
				return setNestingGroup();
			} catch (Exception e) {
				myLogger(6, logger, e);
			}
		// step 7  select data file
		case 7:
			try {
				return selectDataFile();
			} catch (Exception e) {
				myLogger(7, logger, e);
			}
		// step 8  enter sample sizes (repeat until done)
		case 8:
			try {
				myNest.setOrder();
				myNest.G_setFacets();
				return setSampleSize();
			} catch (Exception e) {
				myLogger(8, logger, e);
			}
		// step 9  program prepares files for, and runs urGENOVA
		case 9:
			try {
				// figure out reps
				if (bReplicate) {
					myTree.setDepths();
					doReplications();
				}
				flr.writeDataFileNew();
				myController.disableSave(false);
				return runBrennan();
			} catch (Exception e) {
				myLogger(9, logger, e);
			}
		// step 10  calculates coefficients for G-Study and D_Stuies (repeat)
		case 10:
			try {
				if (bFirstAnalysis)
					flr.getUrGenova();
				bFirstAnalysis = false;
				myNest.setDawdle(true);
				return Analysis();
			} catch (Exception e) {
				myLogger(10, logger, e);
			}
		case 100:
			return Explain();
		default:
			System.exit(99);
			return null;

		}
	}

	/**
	 * initializes Groups for action selection.
	 *
	 * @return <code>Group</code> essentially the 'Scene' for action selection to be sent to the GUI
	 */
	private Group startUp() {
		myController.setStep(0);
		Group group = new Group();
		VBox vb = new VBox();
		vb.setAlignment(Pos.CENTER);
		Label label = new Label("Choose your Action!");
		label.setStyle(sStyle_20);
		label.setPrefWidth(800.0);
		label.setAlignment(Pos.CENTER);
		vb.getChildren().add(label);
		group.getChildren().add(vb);
		return group;
	}

	/**
	 * If in 'Do over' mode, prompts for previous control file and
	 * If in 'Do over' mode, prompts for previous control file and
	 * loads it. Then it allows specification of a new project title,
	 * or changing a previously entered one.
	 *
	 * @return <code>Group</code> essentially the 'Scene' for title entry to be sent to the GUI
	 */
	private Group setTitle() {
		testSetup();							// just for security, the program checks if
												// it had been properly set up, and provides feedback
												// otherwise.
		Group group = new Group();
		VBox vb = new VBox(100);
		vb.setAlignment(Pos.TOP_CENTER);
		String projectTitle = myNest.getTitle();;

		/*
		 * In both Analysis and Synthesis users have the choice to
		 * either enter all the design variables manually, or by
		 * recalling them from the control file, prepared earlier,
		 * as one of the two input files for urGENOVA. This is set
		 * in the menu as the boolean 'bDoOver'.
		 * But throughout, users can always change parameters along
		 * the way, which then get recorded again in the resulting
		 * new control file.
		 */

		/*if (myNest.getDoOver()) {
			selectedFile = flr.getFile(true, "Select Analysis Control File");
			if (selectedFile != null) {
				String sFileName = selectedFile.getName();
				prefs.put("Control", sFileName);
				flr.readFile(selectedFile);
				projectTitle = myNest.getTitle();
			} else {
				return null;
			}
		}*/
		// proceeds normally with manual edit
		TextField tf = new TextField(projectTitle);
		tf.setPrefWidth(800.0);
		repeatFocus(tf);
		tf.setEditable(true);
		tf.setPromptText("Project Title");
		tf.setFont(Font.font("ARIAL", 20));

		/**
		 * The following construct appears over and over in the code,
		 * but we will only explain it here once:
		 * A text field 'tf' has been created. It gets a so called 'Listener' added,
		 * that only springs into action, when the user has changed the text (or number) in the field.
		 * in that case, the new value is stored in the appropriate repository in the 'Nest'
		 */

		tf.textProperty().addListener((observable, oldValue, newValue) -> {
			if (newValue != oldValue) {
				myNest.setTitle(newValue);
			}
		});
		Label lb = new Label("Give your project a unique name, possibly with versions.");
		lb.setAlignment(Pos.TOP_CENTER);
		lb.setPrefWidth(800.0);
		lb.setStyle(sStyle_18);
		Label lbRep = new Label("You may request contextual help in regard to replication.");
		lbRep.setStyle(sStyle_18);
		if (bReplicate)
			vb.getChildren().addAll(lb, tf, lbRep);
		else
			vb.getChildren().addAll(lb, tf);
		group.getChildren().add(vb);
		return group;
	}

	/**
	 * Prompts for comments to describe the project. These comments
	 * form the leading lines of the 'COMMENT' section in the control file.
	 * Thes lines appear in the control file with the header 'COMMENT '.
	 * G_String then adds the facet names and their 1 char designations
	 * in the original facet order. These lines appear in the control file
	 * with the header 'COMMENT*'.
	 *
	 * @return <code>Group</code> essentially the 'Scene' for comment entry to be sent to the GUI
	 */
	private Group addComments() {
		Group group = new Group();
		VBox vb = new VBox(20);
		Label lb = new Label("Edit or add comment describing details of this analysis.");
		lb.setStyle(sStyle_20);
		lb.setPrefWidth(800.0);
		lb.setPrefWidth(800);
		lb.setAlignment(Pos.TOP_CENTER);
		lb.setStyle(sStyle_18);
		TextArea ta = new TextArea();
		repeatFocus(ta);
		ta.setPrefWidth(800.0);
		ta.setMinHeight(300.0);
		ta.setFont(Font.font("Monospaced", 14));
		ta.setPromptText("Comments on the project.");
		if (myNest.getDoOver()) {
			for (String s : myNest.getComments())
				ta.appendText(s + "\n");
		}
		ta.textProperty().addListener((observable, oldValue, newValue) -> {
			if (newValue != oldValue)
				myNest.setComments(newValue);
		});
		vb.getChildren().add(lb);
		vb.getChildren().add(ta);
		group.getChildren().add(vb);
		return group;
	}

	/**
	 *  Prompts for the main subject facet name, designation, and a choice
	 *  between crossed and nested status. This method uses one 'facetSubForm' and one
	 * 'facetCountSubForm'.
	 *
	 *  @return  <code>Group</code> essentially the 'Scene' for main Facet entry to be sent to the GUI
	 */
	private Group mainSubjectGroup() {
		Group content = new Group();
		VBox vb = new VBox(20);
		// vb.setPrefHeight(600);
		Label title = new Label("Specify subject and number of facets.");
		title.setPrefWidth(800);
		title.setStyle(sStyle_18);
		title.setAlignment(Pos.TOP_CENTER);
		vb.getChildren().add(title);
		vb.getChildren().add(headerSubForm("Subject"));
		vb.getChildren().add(facetGroup("Subject Name", 0));
		vb.getChildren().add(facetCountSubForm());
		content.getChildren().add(vb);
		return content;
	}

	/**
	 * Prompts for the features of each additional facet,
	 * again in original facet order. This method uses as many facetSubForms
	 * as necessary.
	 *
	 * @return <code>Group</code> essentially the 'Scene' for other Facets entry to be sent to the GUI
	 */
	private Group subjectsGroup() {
		Group content = new Group();
		int iFCount = myNest.getFacetCount();
		if (!myNest.getDoOver()) {
			myNest.createFacets();
		}
		VBox vb = new VBox(20);
		// vb.setPrefHeight(600);
		Label lb = new Label("Now specify each of the remaining facets.");
		lb.setStyle(sStyle_18);
		lb.setAlignment(Pos.TOP_CENTER);
		lb.setPrefWidth(800);
		vb.getChildren().add(lb);
		vb.getChildren().add(headerSubForm("Facets"));
		for (Integer i = 1; i < iFCount; i++)
			vb.getChildren().add(facetGroup("Facet Name", i));
		content.getChildren().add(vb);
		return content;
	}

	/**
	 * generates bound GUI sub form to specify each specific facet.
	 * iFacetID provides an index for the specific facet.
	 * It is used in both 'mainSubjectGroup' (x 1), and 'subjectsGroup' (x1 to many).
	 * It assigns full facet name, facet char designation, and the facts whether a facet
	 * is crossed or nested, and if the latter, is replicated.
	 *
	 * @param sCue  header string
	 * @param iFacetID original order of new Facet
	 * @return <code>Group</code> essentially the sub -'Scene' for Facet details entry to be sent to the GUI
	 */
	/**
	 * generates bound GUI sub form to specify each specific facet.
	 * iFacetID provides an index for the specific facet.
	 * It is used in both 'mainSubjectGroup' (x 1), and 'subjectsGroup' (x1 to many).
	 * It assigns full facet name, facet char designation, and the facts whether a facet
	 * is crossed or nested, and if the latter, is replicated.
	 *
	 * @param sCue  header string
	 * @param iFacetID original order of new Facet
	 * @return <code>Group</code> essentially the sub -'Scene' for Facet details entry to be sent to the GUI
	 */
	private Group facetGroup(String sCue, Integer iFacetID) {
		Facet tempFacet = null;
		Boolean isNested = false;
		Group facetGroup = new Group();
		HBox layout = new HBox(20);
		layout.setStyle("-fx-padding: 10;-fx-border-color: silver;-fx-border-width: 1;");
		String sFacet = "";
		char[] cFacet = new char[1];
		Boolean bRep = false;
		if (myNest.getDoOver()) {
			tempFacet = myNest.getFacet(iFacetID);
			sFacet = tempFacet.getName();
			cFacet[0] = tempFacet.getDesignation();
			isNested = tempFacet.getNested();
			bRep = (cFacet[0] == cReplicate);
		}
		else
			tempFacet = new Facet(myNest);
		
		if (iFacetID == 0) {
			tempFacet.setAsterisk(true);
			myNest.setSubject(tempFacet);
		}
		
		layout.setAlignment(Pos.BASELINE_LEFT);

		TextField facetName = new TextField(sFacet);
		if (iFacetID < 2)
			repeatFocus(facetName);
		facetName.setPromptText(sCue);
		facetName.setPrefWidth(260);
		facetName.setStyle(customBorder);
		facetName.textProperty().addListener((observable, oldValue, newValue) -> {
			if (newValue != oldValue) {
				myNest.setFacetName(iFacetID, newValue.trim());
			}
		});
		TextField facetChar = new TextField(String.valueOf(cFacet));

		facetChar.setPrefWidth(30);
		facetChar.setStyle(customBorder);
		facetChar.textProperty().addListener((observable, oldValue, newValue) -> {
			String sTemp = newValue.trim();
			if (sTemp.length() > 1) {
				facetChar.setText(oldValue);
				return;
			}
			if (sTemp != oldValue) {
				cFacet[0] = (char)newValue.trim().toCharArray()[0];
				myNest.setFacetDesignation(iFacetID, cFacet[0]);
			}
		});
		Label lbSpacer = new Label(null);
		lbSpacer.setPrefWidth(15);
		Label lbSpacer2 = new Label(null);
		lbSpacer2.setPrefWidth(0.8);
		Label lbSpacer3 = new Label(null);
		lbSpacer3.setPrefWidth(0.8);
		ToggleGroup nestGroup = new ToggleGroup();
		RadioButton butCrossed = new RadioButton();
		butCrossed.setSelected(true);
		butCrossed.setToggleGroup(nestGroup);
		RadioButton butNested = new RadioButton();
		butNested.setToggleGroup(nestGroup);
		butNested.setSelected(isNested);
		butNested.setToggleGroup(nestGroup);
		//nestGroup.selectToggle(isNested ? butNested : butCrossed);
		RadioButton butReplication = new RadioButton();
		butNested.selectedProperty().addListener((observable, oldToggle, newToggle) -> {
			if (newToggle != oldToggle) {
				myNest.setFacetNested(iFacetID, newToggle);
				butReplication.setSelected(false);
				if (newToggle.equals(true) && bReplicate) {
					butReplication.setVisible(true);
				} else {
					butReplication.setVisible(false);
				}
			}
		});
		if (isNested && bReplicate) {
			butReplication.setVisible(true);
			if (cFacet[0] == cReplicate)
				butReplication.setSelected(true);
		}
		else
			butReplication.setVisible(false);
		if (bRep) {
			butReplication.setVisible(true);
			butReplication.setSelected(true);
		}
		butReplication.setOnAction(event -> {
			if (butReplication.isSelected() && (cReplicate == '-')) {
				butReplication.setSelected(true);
				cReplicate = cFacet[0];
			} else {
				butReplication.setSelected(false);
				cReplicate = '-';
				myNest.set_cRep('-');
			}
		});
		layout.getChildren().add(facetName);
		layout.getChildren().add(facetChar);
		layout.getChildren().add(lbSpacer);
		layout.getChildren().add(butCrossed);
		layout.getChildren().add(lbSpacer2);
		layout.getChildren().add(butNested);
		layout.getChildren().add(lbSpacer3);
		layout.getChildren().add(butReplication);
		facetGroup.getChildren().add(layout);
		return facetGroup;
	}

	/**
	 * Generates subform for spinner to enter number of
	 * additional facets.
	 * Is used once in 'subjectsGroup'.
	 *
	 * @return <code>Group</code> essentially the 'Scene' for facet count spinner entry to be sent to the GUI
	 */
	private Group facetCountSubForm() {
		Group fc = new Group();
		HBox hb = new HBox(50);
		hb.setLayoutY(50);
		hb.setPadding(new Insets(30, 12, 15, 0));
		Label lFc = new Label("  Select number of facets (excl. subject):    ");
		lFc.setFont(Font.font("ARIAL", 20));
		Integer facCount = 1;
		if (myNest.getDoOver())
			facCount = myNest.getFacetCount();
		final Spinner<Integer> facetCount = new Spinner<>();
		if (!myNest.getDoOver())
			myNest.setFacetCount(2);
		SpinnerValueFactory<Integer> valueFactory = new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 10,
				facCount - 1, 1);
		facetCount.setValueFactory(valueFactory);
		facetCount.setStyle(customBorder);
		facetCount.setPrefWidth(75);
		facetCount.valueProperty().addListener((observable, oldValue, newValue) -> {
			if (newValue != oldValue) {
				myNest.setFacetCount(newValue + 1);
			}
		});
		hb.getChildren().add(lFc);
		hb.getChildren().add(facetCount);
		fc.getChildren().add(hb);
		return fc;
	}

	/**
	 * Generates the subform for the header line in all subject forms.
	 *
	 * @param _sColumnHeader string of header
	 * @return <code>Group</code> essentially the 'Scene' for facet form header entry to be sent to the GUI
	 */
	private Group headerSubForm(String _sColumnHeader) {
		Group header = new Group();
		HBox hb = new HBox();
		VBox vb = new VBox();
		vb.setAlignment(Pos.BOTTOM_CENTER);
		HBox hb2 = new HBox(10);
		Label lbSubject = new Label(_sColumnHeader);
		lbSubject.setFont(new Font("Arial", 20));
		lbSubject.setPadding(new Insets(0,0,0,10));
		lbSubject.setMinWidth(275);
		Label lbLabel = new Label("Label");
		lbLabel.setFont(new Font("Arial", 20));
		lbLabel.setMinWidth(100);
		Label lbNesting = new Label("Nesting");
		lbNesting.setFont(new Font("Arial", 20));
		Label lbCrossed = new Label("crossed");
		Label lbNested = new Label("nested");
		Label lbReplicate = new Label("replicate");
		lbReplicate.setVisible(bReplicate==true);
		hb.getChildren().add(lbSubject);
		hb.getChildren().add(lbLabel);
		vb.getChildren().add(lbNesting);
		hb2.getChildren().addAll(lbCrossed, lbNested);
		if (bReplicate == true)
			hb2.getChildren().add(lbReplicate);
		vb.getChildren().add(hb2);
		hb.getChildren().add(vb);
		header.getChildren().add(hb);
		return header;
	}

	/**
	 * to set facet order and line change position.
	 * This group uses another standard javafx construct that we will use again:
	 * 'Grab and Drop'. A visual item can be 'grabbed' by clicking with the mouse
	 * button on it. The item then follows the mouse movement, and is then
	 * finally dropped, where the mouse button is released.
	 * see e.g. http://tutorials.jenkov.com/javafx/drag-and-drop.html
	 * In this, and the following group, items are moved from one cell in a list
	 * to another.
	 * Based on the new facet order, G_String creates a new dictionary 'sHdictionary',
	 * which lists the facet characters in hierarchical order, in contrast to 'sDictionary',
	 * which lists the facets in the original order, as they have been entered.
	 * The distinction is important for GS. The original order helps calling up facet
	 * properties, while the hierachical order is used for the algorithmic sequences!
	 *
	 * @return <code>Group</code> essentially the 'Scene' for Facets order entry to be sent to the GUI
	 */
	private Group orderFacets() {
		lvFacets = new ListView<>();
		ObservableList<String> orderedData = FXCollections.observableArrayList();
		// final ToggleGroup tg = new ToggleGroup();

		Group returnGroup = new Group();

		VBox vbOuter = new VBox(10);
		vbOuter.setAlignment(Pos.CENTER);
		Label lbTitle = new Label(
				"Set order of facets and identify the main \neffect indicated by change of record (Star).");
		lbTitle.setPrefWidth(800);
		lbTitle.setStyle(sStyle_18);
		lbTitle.setAlignment(Pos.TOP_CENTER);
		vbOuter.getChildren().add(lbTitle);
		myNest.createDictionary();
		sDictionary = myNest.getDictionary();
		sHDictionary = sDictionary;
		cAsterisk = myNest.getAsterisk();
		for (Integer i = 0; i < sHDictionary.length(); i++)
			orderedData.add(sHDictionary.substring(i, i + 1));
		lvFacets.setItems(orderedData);
		lvFacets.setMaxWidth(150);
		lvFacets.setMaxHeight(300);
		lvFacets.setCellFactory(new Callback<ListView<String>, ListCell<String>>() {
			@Override
			public ListCell<String> call(ListView<String> lv) {
				final ListCell<String> cell = new ListCell<String>() {

					@Override
					protected void updateItem(String t, boolean bln) {
						super.updateItem(t, bln);
						if (t == null)
							setText(null);
						else {
							char c = t.toCharArray()[0];
							String sTemp = t;
							this.setPrefWidth(50.0);
							this.setAlignment(Pos.CENTER);
							if (c == cAsterisk)
								sTemp += "*";
							setText(sTemp);
							setFont(Font.font(20));
						}
					}
				};

				cell.setOnDragDetected(new EventHandler<MouseEvent>() {
					@Override
					public void handle(MouseEvent event) {
						/* drag was detected, start a drag-and-drop gesture */
						/* allow any transfer mode */
						String item = null;
						if (cell != null) {
							/* Put cell content on a dragboard */
							// listArray(orderedData);
							Dragboard dragboard = cell.startDragAndDrop(TransferMode.MOVE);
							ClipboardContent content = new ClipboardContent();
							item = lvFacets.getSelectionModel().getSelectedItem().toString();
							content.putString(item);
							iFrom = cell.getIndex();
							dragboard.setContent(content);
							event.setDragDetect(true);
							event.consume();
						}
					}
				});

				cell.setOnDragDropped(new EventHandler<DragEvent>() {
					@Override
					public void handle(DragEvent event) {
						iTo = cell.getIndex();
						event.setDropCompleted(true);
						event.consume();
					}
				});

				cell.setOnDragExited(new EventHandler<DragEvent>() {
					@Override
					public void handle(DragEvent event) {
						cell.setStyle("-fx-background-color: WHITE;");
						event.consume();
					}
				});

				cell.setOnDragOver(new EventHandler<DragEvent>() {
					@Override
					public void handle(DragEvent event) {
						/* data is dragged over the target */
						/*
						 * accept it only if it is not dragged from the same
						 * node and if it has a string data
						 */
						if (event.getGestureSource() != cell && event.getDragboard().hasString()) {
							/*
							 * allow for both copying and moving, whatever user
							 * chooses
							 */
							event.acceptTransferModes(TransferMode.COPY_OR_MOVE);
						}
						event.consume();
					}
				});

				cell.setOnDragDone(new EventHandler<DragEvent>() {
					@Override
					public void handle(DragEvent event) {
						/* the drag and drop gesture ended */
						/* if the data was successfully moved, clear it */
						if (event.getTransferMode() == TransferMode.MOVE) {
							if (iTo < 0)
								iTo = lvFacets.getItems().size();
							Dragboard db = event.getDragboard();
							if (db.hasString()) {
								String s = orderedData.get(iFrom);
								orderedData.remove(s);
								if (iTo >= orderedData.size())
									orderedData.add(s);
								else
									orderedData.add(iTo, s);
								cell.updateListView(lvFacets);
								lvFacets.setItems(null);
								lvFacets.setItems(orderedData);
								Integer L = orderedData.size();
								StringBuilder sb = new StringBuilder();
								for (Integer i = 0; i < L; i++)
									sb.append(orderedData.get(i).charAt(0));
								sHDictionary = sb.toString();
								myNest.setHDictionary(sHDictionary);
								lv.refresh();
							}
						}
						event.consume();
					}
				});

				cell.setOnDragEntered(new EventHandler<DragEvent>() {
					@Override
					public void handle(DragEvent event) {
						cell.setStyle("-fx-background-color: BLANCHEDALMOND;");
						event.consume();
					}
				});

				cell.setOnMouseClicked(new EventHandler<MouseEvent>() {
					@Override
					public void handle(MouseEvent event) {
						String sText = cell.getText();
						if (sText == cAsterisk + "*") {
							cell.setText(sText);
							cell.setVisible(true);
							event.consume();
							return;
						} else {
							cAsterisk = cell.getText().toCharArray()[0];
							myNest.setAsterisk(cAsterisk);
							cell.setText(cAsterisk + "*");
							myNest.setAsterisk(cAsterisk);
							lv.refresh();
							cell.setVisible(true);
							event.consume();
						}
					}

				});

				return cell;
			}
		});

		lvFacets.refresh();
		vbOuter.getChildren().add(lvFacets);
		returnGroup.getChildren().add(vbOuter);

		return returnGroup;
	}

	/**
	 * arranges nesting details, again using 'drag and drop'.
	 * But while in step 5 items were moved within the same list, in
	 * step 6 items are moved from the list on the left (nested facets)
	 * to the list on the right (crossed effects).
	 * However, the basic mechanism stays the same.
	 *
	 * @return <code>Group</code> essentially the 'Scene' for Facet nesting entry to be sent to the GUI
	 */
	private Group setNestingGroup() {
		String dataFormat = "-fx-font-size: 1.5em ;";
		nestedData.clear();
		nestedData.addAll(filteredFacetList(true));
		crossedData.clear();
		crossedData.addAll(filteredFacetList(false));
		saveNested(crossedData);
		ArrayList<String> tempNested = new ArrayList<String>();
		Group group = new Group();
		VBox vb = new VBox(20);
		Label title = new Label("Arrange Nesting");
		vb.setAlignment(Pos.TOP_CENTER);
		title.setPrefWidth(800);
		title.setStyle(sStyle_20);
		title.setAlignment(Pos.TOP_CENTER);
		vb.getChildren().add(title);
		HBox hb = new HBox(5);
		hb.setAlignment(Pos.CENTER);
		lvCrossed = new ListView<>();
		lvCrossed.setStyle(dataFormat);
		lvCrossed.setCellFactory(new Callback<ListView<String>, ListCell<String>>() {
			@Override
			public ListCell<String> call(ListView<String> lv) {
				final ListCell<String> cell = new ListCell<String>() {

					@Override
					protected void updateItem(String t, boolean bln) {
						super.updateItem(t, bln);
						if (t == null)
							setText(null);
						else
							setText(t);
					}
				};

				cell.setOnDragEntered(new EventHandler<DragEvent>() {
					@Override
					public void handle(DragEvent event) {
						if (event.getGestureSource() != cell && event.getDragboard().hasString())
							cell.setStyle("-fx-background-color: BLANCHEDALMOND;");
						event.consume();
					}
				});

				cell.setOnDragExited(new EventHandler<DragEvent>() {
					@Override
					public void handle(DragEvent event) {
						/* mouse moved away, remove the graphical cues */
						cell.setStyle(null);
						event.consume();
					}
				});

				cell.setOnDragOver(new EventHandler<DragEvent>() {
					@Override
					public void handle(DragEvent event) {
						if (event.getGestureSource() != lvCrossed && event.getDragboard().hasString()) {
							/*
							 * allow for both copying and moving, whatever user
							 * chooses
							 */
							event.acceptTransferModes(TransferMode.MOVE);
						}
						event.consume();
					}
				});

				cell.setOnDragDropped(new EventHandler<DragEvent>() {
					@Override
					public void handle(DragEvent event) {
						/* data dropped */
						/*
						 * if there is a string data on dragboard, read it and
						 * use it
						 */
						Dragboard db = event.getDragboard();
						boolean success = false;
						if (db.hasString()) {
							String sCarried = db.getString();
							iTo = cell.getIndex();
							if (bReplicate) {
								char cN = sDictionary.toCharArray()[iTo];
								if (cN == cReplicate) {
									myNest.setProblem(1);
									return;
								}
							}
							if (iTo >= iPointer)
								iPointer = iTo + 1;
							success = true;
							String sNest = sCarried + ":" + lvCrossed.getItems().get(iTo).toString();
							crossedData.add(iPointer++, sNest);
							lvNested.getItems().remove(sCarried);
							lvNested.refresh();
							String[] ss = crossedData.toArray(new String[crossedData.size()]);
							StringBuilder sb = new StringBuilder();
							for (String s : ss) {
								sb.append(s + "; ");
							}
						}
						/*
						 * let the source know whether the string was
						 * successfully transferred and used
						 */
						event.setDropCompleted(success);
						for (int i = 0; i < crossedData.size(); i++)
							tempNested.add(crossedData.get(i));
						saveNested(crossedData);
						event.consume();
					}
				});

				cell.setOnMouseClicked(new EventHandler<MouseEvent>() {
					@Override
					public void handle(MouseEvent event) {
						String sNest = cell.getItem();
						Integer iColon = sNest.indexOf(':');
						if (iColon > 0) // nested item (colon detected
						{
							String sNested = sNest.substring(0, iColon);
							nestedData.add(sNested);
							lvNested.refresh();
							crossedData.remove(sNest);
							lvCrossed.refresh();
							iPointer--;
						}
						event.consume();
					}
				});

				return cell;
			}
		});

		lvCrossed.setItems(crossedData);
		lvNested = new ListView<>();
		lvNested.setStyle(dataFormat);
		lvNested.setCellFactory(new Callback<ListView<String>, ListCell<String>>() {
			@Override
			public ListCell<String> call(ListView<String> lv) {
				final ListCell<String> cell = new ListCell<String>() {
					@Override
					protected void updateItem(String t, boolean bln) {
						super.updateItem(t, bln);
						if (t == null)
							setText(null);
						else
							setText(t);
					}

				};
				// drag from left to right
				cell.setOnDragDetected(new EventHandler<MouseEvent>() {
					@Override
					public void handle(MouseEvent event) {
						/* drag was detected, start a drag-and-drop gesture */
						/* allow any transfer mode */
						Dragboard db = lvNested.startDragAndDrop(TransferMode.MOVE);
						/* Put a string on a dragboard */
						ClipboardContent content = new ClipboardContent();
						content.putString(cell.getText());
						iFrom = lvNested.getSelectionModel().getSelectedIndex();
						db.setContent(content);
						event.consume();
					}
				});

				cell.setOnDragEntered(new EventHandler<DragEvent>() {
					@Override
					public void handle(DragEvent event) {
						/* the drag-and-drop gesture entered the target */
						/*
						 * show to the user that it is an actual gesture target
						 */
						if (event.getGestureSource() != lvCrossed && event.getDragboard().hasString()) {
						}
						event.consume();
					}
				});

				return cell;
			}
		});

		lvNested.setItems(nestedData);
		Label lb = new Label("Arrange the appropriate nesting of your design");
		lb.setStyle(sStyle_18);
		lb.setPrefWidth(800.0);
		lb.setAlignment(Pos.CENTER);
		Label arrow = new Label(" \u21D4 ");
		arrow.setFont(new Font("Arial", 30));
		vb.getChildren().add(lb);
		VBox vbN = new VBox(5);
		vbN.setMaxHeight(300.0);
		vbN.setAlignment(Pos.TOP_CENTER);
		Label lbN = new Label("Nested");
		lbN.setStyle(dataFormat);
		VBox vbC = new VBox(5);
		vbC.setMaxHeight(300.0);
		vbC.setAlignment(Pos.TOP_CENTER);
		Label lbC = new Label("Crossed");
		lbC.setStyle(dataFormat);
		vbN.getChildren().add(lbN);
		vbN.getChildren().add(lvNested);
		hb.getChildren().add(vbN);
		hb.getChildren().add(arrow);
		vbC.getChildren().add(lbC);
		vbC.getChildren().add(lvCrossed);
		hb.getChildren().add(vbC);
		vb.getChildren().add(hb);
		group.getChildren().add(vb);
		return group;
	}

	/**
	 * Saves the nested list to the 'Nest' repository, and returns it for further use
	 * in AnaGroups as a formal tree structure.
	 *
	 * @param _crossed observable list of crossed Effect descriptions
	 */
	private void saveNested(ObservableList<String> _crossed) {
		String[] sNests = null;
		ArrayList<String> sarNests = new ArrayList<>();
		Integer iLength = _crossed.size();
		for (Integer i = 0; i < iLength; i++) {
			String sNest = _crossed.get(i);
			if ((sNest.length() == 1) || (sNest.indexOf(':') >= 0))
				sarNests.add(sNest);
		}
		sNests = sarNests.toArray(new String[sarNests.size()]);
		myNest.setNests(sNests);
	}


	/**
	 * An 'ObservableList' is a Javafx construct that enables listeners
	 * to track changes in the list, when they occur. A ListChangeListener is
	 * an interface that receives notifications of changes to an ObservableList.
	 * This construct is used in the method 'setNestingGroup' for both the list
	 * of crossed and nested facets. That makes the visual arranging
	 * of facet nesting possible. Identical to the one in 'AnaGroups'.
	 *
	 * @param isNested Boolean
	 * @return filteredFacetList  ObservableList
	 */
	private ObservableList<String> filteredFacetList(Boolean isNested) {
		ArrayList<String> result = new ArrayList<String>();
		Integer iMax = 0;
		String sTemp = null;
		if (myNest.getNests() != null)
			if (!isNested)
				return myNest.getNests();
			else
				return FXCollections.observableArrayList(new ArrayList<String>());
		else {
			sHDictionary = myNest.getHDictionary();
			char[] cOrder = sHDictionary.toCharArray();
			for (char c : cOrder) {
				if (myNest.getFacet(c).getNested() == isNested) {
					result.add(Character.toString(c));
					iMax = result.size();
					if (!isNested) {
						for (Integer i = 0; i < iMax; i++)
							if ((sTemp = result.get(i)).indexOf(c) < 0)
								result.add(sTemp + c);
					}
				}
			}
		}
		return FXCollections.observableArrayList(result);
	}

	/**
 	 * Prompts for the data file; if no valid file is selected,
	 * an error message pops up, and the program will exit.
	 *
	 * @return <code>Group</code> essentially the 'Scene' for data file selection entry to be sent to the GUI
	 */
	private Group selectDataFile() {
		selectedFile = flr.getFile(true, "Select Analysis Data File");
		prefs.put("Home Directory", selectedFile.getParent());
		prefs.put("Data Raw", "Data.txt");
		flr.readDataFileNew(selectedFile);
			// reads data file into filer table 'sRawData'
		return flr.showTableNew();
			// displays 'sRawData' and allows index suppressing
	}

	/**
	 * G_String cycles through 'setSampleSize' until all the sample sizes
	 * for all facets (in hierarchical order) have been collected, when the variable 'bDawdle'
	 * turns to 'false'. The actual form is constructed as 'getPage' within 'SampleSizeTree',
	 * where the sample sizes are being stored as well.
	 *
	 * @return <code>Group</code> essentially the 'Scene' for setting sample sizes entry to be sent to the GUI
	 */
	public Group setSampleSize() {
		char cFacet = sHDictionary.toCharArray()[iSample];
		if ((cFacet == cReplicate) && bReplicate)
			iSample++;
		myTree.collectSampleSizes();
		Group group = new Group();
		// construct samples page
		group.getChildren().add(myTree.getPage(iSample));
		myNest.setDawdle(iSample++ < myNest.getFacetCount()- 1);			
		return group;
	}

	/**
	 * this is the crucial step 9 where urGENOVA is tasked with calculating the estimates
	 * of variance coefficients for all facets and their appropriate calculations.
	 *
	 * @return <code>Group</code> essentially the 'Scene' for returning urGENOVA results to be sent to the GUI
	 */
	public Group runBrennan() {
		String tLine = null;

		String slash = File.separator;
		String sControl = prefs.get("Working Directory", null) + slash + sControlFileName;
		File brennanControl = new File(sControl);
		if (brennanControl != null) {
			flr.writeAnalysisControlFile(brennanControl,true);
			flr.saveParametersDialog("Analysis", "Save Analysis Control File?");
		}
		myNest.setLevels();
		brennanControl.setReadable(true, false);
		brennanControl.setWritable(true, false);

		// now run urGenova
		Group group = new Group();
		String homeDirectory = prefs.get("Working Directory", null);
		ProcessBuilder builder = new ProcessBuilder();
		switch (prefs.get("OS", null)) {
		case "Linux":
			builder.command("sh", "-c", "./urGenova ~control.txt");
			break;
		case "Mac":
			builder.command("sh", "-c", "./urGenova ~control.txt");
			break;
		case "Windows":
			// here DoUrGenova has to be built in
			builder.command("cmd.exe", "/c", "urGenova.exe", "~control.txt");
			break;
		default:
			break;
		}
		builder.directory(new File(homeDirectory));
		Process process = null;
		try {
			process = builder.start();
		} catch (Exception e) {
			logger.warning( e.getMessage());
		}
		String line;
		sbResult = new StringBuilder();
		BufferedReader input = new BufferedReader(new InputStreamReader(process.getInputStream()));
		try {
			while ((line = input.readLine()) != null) {
				sbResult.append(line);
			}
		} catch (IOException e1) {
			logger.warning(e1.getMessage());
		}
		line = sbResult.toString();
		Label lbGenova = new Label("urGenova said: " + line);
		if (line.indexOf("Successful") < 0)
			lbGenova.setTextFill(Color.RED);
		try {
			process.waitFor();
		} catch (InterruptedException e) {
			logger.warning(e.getMessage());
		}
		TextArea taOutput = new TextArea();

		InputStream is = null;
		try {
			is = new FileInputStream(prefs.get("Working Directory", null) + "/~control.txt.lis");
		} catch (FileNotFoundException e) {
			logger.warning(e.getMessage());
		}

		/*
		 * ...... urGenova has finished.
		 * The program now reads the urGenova output file and formats it into stringBuilder
		 * 'sbResult'.
		 */

		BufferedReader buf = new BufferedReader(new InputStreamReader(is));


		try {
			while ((tLine = buf.readLine()) != null) {
				sbResult.append(tLine + "\n");
			}
			tLine = "\n";
			sbResult.append(tLine + "\n");
			Integer iMissed = flr.missingItems();
			String sMissed = null;
			if (iMissed == 0)
				sMissed = "There were no missing items.\n";
			else if (iMissed == 1)
				sMissed = "One item was missing.\n";
			else
				sMissed = "There were " + iMissed.toString() + " missing items.\n";
			sbResult.append(sMissed);
			sbResult.append(tLine + "\n");
			tLine = "The calculated grand mean = " + String.format("%.5f", myNest.getGreatMeans()) + "\n";
			sbResult.append(tLine);
			tLine = "This value has been subtracted from the actual scores for the calculations.\n";
			sbResult.append(tLine);
			tLine = "While this improves the accuracy, it does not affect the calculated variances.\n\n";
			sbResult.append(tLine);
		} catch (IOException e) {
			logger.warning(e.getMessage());
		}
		VBox vbOuter = new VBox();
		vbOuter.setPrefWidth(800);
		vbOuter.setMinHeight(300.0);
		vbOuter.setAlignment(Pos.TOP_CENTER);
		vbOuter.setSpacing(5);
		Label lbTitle = new Label("urGenova Output");
		lbTitle.setAlignment(Pos.TOP_CENTER);
		lbTitle.setPrefWidth(800);
		lbTitle.setStyle(sStyle_20);
		taOutput.setFont(Font.font("Monospaced", 12));
		taOutput.setText(sbResult.toString());
		taOutput.setPrefHeight(500);
		vbOuter.getChildren().addAll(lbTitle, lbGenova, taOutput);
		group.getChildren().add(vbOuter);
		return group;
	}

	/**
	 * Special display group to explain specific problem.
	 * Depending on the severity of the issue, it might force end of program,
	 * or it may bring you back to the step, where you can fix it.
	 *  
	 *  @return  <code>Group</code> essentially the 'Scene' for main Facet entry to be sent to the GUI
	 */
	private Group Explain() {
		int iProblem = myNest.getProblem();
		String[] sExplanations = new String[10];
		sExplanations[1] = "You can not nest a facet within a replicating facet!";
		sExplanations[2] = "Only one facet can be replicating," +
				"\n - it has to be nested under the facet of differentiation," +
				"\n - it has to also be the 'starred' facet, or immediately follow it.";
		sExplanations[3] = "The facet, you set the asterisk on, " +
				"\nthe index column number for that facet," +
				"\nand the organisation of the data file have to correspond!";
		int[] iResumeSteps = new int[10];
		iResumeSteps[1] = 3;
		iResumeSteps[2] = 5;
		iResumeSteps[3] = 4;
		Group content = new Group();
		VBox vb = new VBox(20);
		Label lTitle = new Label("Knowledge is Power!");
		lTitle.setPrefWidth(800);
		lTitle.setStyle(sStyle_20);
		lTitle.setAlignment(Pos.TOP_CENTER);
		HBox hbExplain = new HBox();
		hbExplain.setStyle(sStyle_18);
		Label lExplanation = new Label(sExplanations[iProblem]);
		lExplanation.setStyle(sStyle_18);
		hbExplain.getChildren().add(lExplanation);
		vb.getChildren().addAll(lTitle, hbExplain);
		myNest.setResume(iResumeSteps[iProblem]);
		content.getChildren().add(vb);
		return content;
	}

	/**
	 * reads existing script
	 */
	private void readOld() {
		String sInitial = prefs.get("Home Directory", File.separator);

		if (myNest.getDoOver()) {
			File selectedFile = flr.getFile(true, "Select Synthesis Control File");
			if (selectedFile != null) {
				sInitial = selectedFile.getParent();
				prefs.put("Home Directory", sInitial);
				String sFileName = selectedFile.getName();
				prefs.put("Control", sFileName);
				flr.readFile(selectedFile);
			}
		}
	}

	/**
	 * 'Analysis' extracts the means and estimated variance coefficients
	 * from the urGenova output file, and uses them to calculate G-Study and D-Study results
	 * see 'VarianceComponents' in the 'Algorithm' section of the explanations.
	 *
	 * @return <code>Group</code> essentially the 'Scene' for returning G-Study abd D-Study results to be sent to the GUI
	 */
	public Group Analysis() {
		final ImageView imvErho = new ImageView();
		final Image imErho = new Image(Main.class.getResourceAsStream("/resources/E_rho2.png"), 60, 60, true, true);
		imvErho.setImage(imErho);
		final ImageView imvPhi = new ImageView();
		final Image imPhi = new Image(Main.class.getResourceAsStream("/resources/Phi.png"), 50, 50, true, true);
		imvPhi.setImage(imPhi);
		// First calculate the results for this cycle and add it to the result
		// file
		try {
			myNest.formatResults(sbResult);
		} catch (Exception ioe) {
			myLogger(11, logger, ioe);
		}
		Group group = new Group();
		taOutput = new TextArea();
		VBox vbOuter = new VBox();
		vbOuter.setPrefWidth(800);
		vbOuter.setMinHeight(300);
		vbOuter.setAlignment(Pos.TOP_CENTER);
		vbOuter.setSpacing(20);
		Label lbTitle = new Label("Analysis");
		lbTitle.setAlignment(Pos.TOP_CENTER);
		lbTitle.setPrefWidth(800);
		lbTitle.setStyle(sStyle_20);
		SplitPane sp = new SplitPane();
		VBox rightBox = new VBox();
		HBox titleBox = new HBox();
		Label lbFacet = new Label("Facet:");
		lbFacet.setPadding(new Insets(0, 40, 10, 3));
		lbFacet.setStyle("-fx-font-weight: bold;");
		Label lbLevel = new Label("Level:");
		lbLevel.setStyle("-fx-font-weight: bold;");
		lbLevel.setPadding(new Insets(0, 40, 10, 0));
		Label lbType = new Label("Type");
		lbType.setStyle("-fx-font-weight: bold;");
		lbType.setPadding(new Insets(0, 0, 10, 0));
		titleBox.getChildren().addAll(lbFacet, lbLevel, lbType);
		rightBox.getChildren().add(titleBox);
		for (Facet f : myNest.getFacets()) {
			char cT = f.getFacetType();
			if (cT == 'g') {
				FacetModView fmv = new FacetModView(f);
				rightBox.getChildren().add(fmv);
			}
		}
		Double dRho = myNest.getRho();
		Double dPhi = myNest.getPhi();
		HBox rhoBox = new HBox();
		Label lbRhoRes = new Label(formatDouble(dRho));
		lbRhoRes.setStyle("-fx-font-size: 18");
		lbRhoRes.setPadding(new Insets(10, 0, 0, 10));
		rhoBox.setPadding(new Insets(10, 0, 0, 80));
		rhoBox.getChildren().addAll(imvErho, lbRhoRes);
		HBox phiBox = new HBox();
		Label lbPhiRes = new Label(formatDouble(dPhi));
		lbPhiRes.setStyle("-fx-font-size: 18");
		lbPhiRes.setPadding(new Insets(2, 0, 0, 10));
		phiBox.setPadding(new Insets(10, 0, 0, 90));
		phiBox.getChildren().addAll(imvPhi, lbPhiRes);
		if (dPhi.equals(0.0))
			rightBox.getChildren().addAll(rhoBox);
		else
			rightBox.getChildren().addAll(rhoBox, phiBox);
		taOutput.setText(sbResult.toString());
		taOutput.setPrefWidth(400);
		taOutput.setPadding(new Insets(20, 20, 20, 20));
		taOutput.setMinHeight(300.0);
		taOutput.appendText("");
		sp.getItems().addAll(taOutput, rightBox);
		sp.setDividerPositions(0.7f);
		vbOuter.getChildren().addAll(lbTitle, sp);
		group.getChildren().add(vbOuter);

		return group;
	}

	/**
	 * thread utility for javafx
	 *
	 * @see <a href="https://docs.oracle.com/javase/8/javafx/api/index.html?javafx/application/Platform.html">JavaFX platform</a>
	 * @param node  javafx generic scene graph
	 */
	private void repeatFocus(Node node) {
		Platform.runLater(() -> {
			if (!node.isFocused()) {
				node.requestFocus();
				repeatFocus(node);
			}
		});
	}

	/**
	 * special formatting request for Double in text
	 * @param _d Double value
	 * @return formatted string
	 */
	private String formatDouble(Double _d) {
		if (_d % 1.0 != 0)
			return String.format("%.2f", _d);
			//return String.format("%.4f", _d);	// Frithjoff change
		else
			return String.format("%.0f", _d);
	}

	/**
	 * in response to menu item 'Start Over'
	 * resets essential variables for new start.
	 */
	public void reset() {
		nestedData.clear();
		crossedData.clear();
		sDictionary = null;
		sHDictionary = null;
	}

	/**
	 * Checks if a working directory has been specified previously,
	 * and the operating system specific urGENOVA code has been installed.
	 */
	private void testSetup() {
		String sWorkingDirectory = prefs.get("Working Directory", null);
		String sOS_Full = System.getProperty("os.name");
		String sUrGenova = null;
		if (sOS_Full.indexOf("Windows") >=0)
			sUrGenova = "urgenova.exe";
		else
			sUrGenova = "urGenova";
		File f = new File(sWorkingDirectory, sUrGenova);
		if (!f.isFile()){
			Alert alert = new Alert(AlertType.CONFIRMATION);
			alert.setContentText("You did not go properly through setup\n(see manual)!");
			alert.showAndWait();
			myMain.doSetup();
		}
	}

	/**
	 * In response to GUI saves all the analysis results in a text file
	 * according to user's instructions.
	 *
	 * @throws IOException  problem in file i/o
	 */
	public void saveAll() throws IOException {
		OutputStream outputStream = null;
		OutputStreamWriter outputStreamWriter = null;
		File outFile = flr.getFile(false, "Save Analysis Results");
		if (outFile != null) {
			outputStream = new FileOutputStream(outFile);
			outputStreamWriter = new OutputStreamWriter(outputStream, "UTF-8");
			outputStreamWriter.write(sbResult.toString());
			outputStreamWriter.write(flr.testSignature());
			outputStreamWriter.flush();
			outputStreamWriter.close();
		}
	}
	
	/**
	 * Setter for replication flag
	 * 
	 * @param _bRep  replication flag
	 */
	public void setReplicate(Boolean _bRep) {
		bReplicate = _bRep;
	}
	
	/**
	 * Handles replications in analysis. In contrast to the method with the same
	 * name in SynthGroups, this method extracts the replication sample sizes
	 * from the data file, interpreting the leading index columns.
	 * 
	 * @return true if successful false otherwise
	 */
	private Boolean doReplications() {
		String[][] sRawData = flr.getRawData();
		Boolean bCondition = (cAsterisk == cReplicate);
		char cNestor = myNest.getFacet(cReplicate).getNestor();
		if (bCondition && (myNest.getFacet(cNestor).getFacetType() != 'd'))
			myNest.setProblem(4);
		int[] iarNestor =myTree.getSizes(cNestor);
		int iDimRep = 0;
		for (int i : iarNestor)
			iDimRep +=i;
		int[] iarReps = new int[iDimRep];
		int iCount = 0;
		int iHighLight = myNest.getHighLight();
		int iFactor = myTree.iGetRepFactor(cReplicate);
		int iDiffCol = sHDictionary.indexOf(cNestor);
		int iDiff = 0;
		int iRep = 0;
		try {
			for(String[] sLine : sRawData) {
				if (bCondition) {		// a new data line for each replicate value
					int iSubject = Integer.parseInt(sLine[iDiffCol]);
					if (iSubject != iDiff){
						iarReps[iCount] = iRep;
						iRep = 0;
						iDiff = iSubject;
						iCount++;
					} else
						iRep++;
				
				} else {				// all replicates and dependents on same line
					if (iCount >= iarReps.length) {
						myNest.setProblem(3);
						return false;
					}
					iarReps[iCount] = (sLine.length - iHighLight)/iFactor;
					iCount++;
				}
			}
			if (bCondition && (iCount >= iDimRep))
				myNest.setProblem(5);
		} catch(Exception e) {
			 e.printStackTrace();
		}
		
		Integer iR = sDictionary.indexOf(cReplicate);
		myTree.addSampleSize(iR, iarReps);
		myNest.setDawdle(iSample++ < myNest.getNestCount() - 1);
		return true;
	}
	
	/**
	 * auxiliary method in logger handling
	 * 
	 * @param _iStep  current step in AnaGroups
	 * @param _logger  pointer to logger API
	 * @param _e  error to be logged
	 */
	private void myLogger(int _iStep, Logger _logger, Exception _e) {
		if (myNest.getStackTraceMode())
			_e.printStackTrace();
		else {
			String sMessage = "\n Step: " + _iStep + "\n " + _e.getLocalizedMessage();
			logger.warning(sMessage);
		}
	}
}
