package steps;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintStream;
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
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.DragEvent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.util.Callback;
import model.Facet;
import model.Nest;
import model.SampleSizeTree;
import utilities.CombConstrct;
import utilities.Filer;
import utilities.Lehmer;
import utilities.constructSimulation;
import view.rootLayoutController;

/**
 * SynthGroup manages the generation of synthetic data sets by taking the user step-by-step
 * through the whole procedure from entering the project summary to
 * writing the reulting data file. Altogether there are 9 steps,
 * and proceeding to the next step is only possible after 'grammatically'
 * correct input. At each step a context specific help screen is available.
 * As result of the user responses at a given step, the entered data are stored, and the GUI scene
 * for the next step is generated, and handed via 'main' back to the GUI.
 * Users can step through the synthesis either manually, by entering the design information
 * via keyboard and mouse, or they can pick the 'do-over' mode, where the program
 * reads a control file, prepared in a previous synthesis run.  The user just has to enter changes.
 *
 * @see <a href="https://github.com/G-String-Legacy/G_String/blob/main/workbench/GS_L/src/steps/SynthGroups.java">steps.SynthGroup</a>
 * @author ralph
 * @version %v..%
 * */
public class SynthGroups {

	/**
	 * <code>myController</code> pointer to <code>view.rootLayoutController</code>
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
	private ObservableList<String> nestedData = null;

	/**
	 * A list that allows listeners to track changes of crossed Data when they occur.
	 */
	private ObservableList<String> crossedData = null;

	/**
	 * lower limit for list (drag and drop)
	 */
	private Integer iFrom = null;

	/**
	 * upper limit for list (drag and drop)
	 */
	private Integer iTo = null;

	/**
	 * current step in workflow
	 */
	private Integer iStep = 0;

	/**
	 * Facet designation char for starred Facet
	 */
	private char cStarred;

	/**
	 * integer location of element in list
	 */
	private Integer iPointer = null;

	/**
	 * <code>Nest</code> parameter repository defining whole assessment
	 */
	private Nest myNest = null;

	/**
	 * <code>SampleSizeTree</code> sample size repository for facets and nesting
	 */
	private SampleSizeTree myTree = null;

	/**
	 * storage for previous element in series
	 */
	private Facet oldFacet = null;

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
	private String sHDictionary = null;

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
	 * descriptor of text display style
	 */
	private String sStyle_14 = null;

	/**
	 * pointer to <code>Preferences</code>
	 */
	private Preferences prefs = null;

	/**
	 * Boolean array to checkk completeness of required variance entry steps.
	 */
	private Boolean[] VarianceDadleCheck = null;

	/**
	 * pointer to <code>constructSimulation</code>
	 */
	private constructSimulation CS = null;

	/**
	 * text multi-purpose text field
	 */
	private String sText = null;

	/**
	 * counter of concurrent scenes
	 */
	private Integer iTFonPage = 0;

	/**
	 * counting low threshold cutoffs
	 */
	private Integer iMinCut = 0;

	/**
	 * counting high threshold cutoffs
	 */
	private Integer iMaxCut = 0;

	/**
	 *  counting remaining scores
	 */
	private Integer iNoCut = 0;

	/**
	 * pointer to application logger
	 */
	private Logger logger = null;
	
	/**
	 * pointer to Main
	 */
	private Main myMain;

	/**
	 * Constructor
	 *
	 * @param _main  pointer to Main class
	 * @param _nest  pointer to Nest
	 * @param _logger  application logger
	 * @param _controller  pointer to rootLayoutController
	 * @param _prefs pointer to Preferences
	 * @param _flr pointer to Filer
	 */
	public SynthGroups(Main _main, Nest _nest, Logger _logger, rootLayoutController _controller, Preferences _prefs, Filer _flr) {
		myMain = _main;
		myNest = _nest;
		myController = _controller;
		logger = _logger;
		flr = _flr;
		nestedData = FXCollections.observableArrayList();
		crossedData = FXCollections.observableArrayList();
		iFrom = -1;
		iTo = -1;
		iPointer = 0;
		prefs = _prefs;
		customBorder = prefs.get("Border", null);
		sStyle_18 = prefs.get("Style_18",
				"-fx-font-size: 18px; -fx-font-family: \"ARIAL\"; -fx-padding: 10; -fx-background-color: #805015; -fx-text-fill: #FFFFFF;");
		sStyle_20 = prefs.get("Style_20",
				"-fx-font-size: 30px; -fx-font-family: \"ARIALSerif\"; -fx-padding: 10; -fx-background-color: #805015; -fx-text-fill: #FFFFFF;");
		if (myNest.getDoOver()) {
			File selectedFile = flr.getFile(true, "Select Control File");
			if (selectedFile != null) {
				String sInitial = selectedFile.getParent();
				prefs.put("Home Directory", sInitial);
				String sFileName = selectedFile.getName();
				prefs.put("Control", sFileName);
				flr.readFile(selectedFile);
			}
		}
		//myTree = myNest.getTree(); // to be available for the next step
		//myTree.setSimulate(true);
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
	 * @throws Throwable  IOException
	 */
	public Group getGroup() throws Throwable {
		testSetup();
		checkVarianceDawdle();
		iStep = myNest.getStep();
		if (iStep == 0)
			myController.callForAction(true);
		else
			myController.callForAction(false);
		myController.setStep(iStep);
		switch (iStep) {
		case 1:
			myController.enableStepUp(true);
			if (myNest.getDoOver())
				readOld();
			return addComments();
		case 2:
			return mainSubjectGroup();
		case 3:
			return subjectsGroup();
		case 4:
			return orderFacets();
		case 5:
			return setNestingGroup();
		case 6:
			myNest.setOrder();
			myNest.G_setFacets();
			myNest.setDawdle(true);
			return setSampleSize();
		case 7:
			iTFonPage = 0;
			return baseScaleGroup();
		case 8:
			if (!myNest.getDoOver())
				myNest.setVarianceDawdle(true);
			return VarianceComponentsGroup();
		case 9:
			flr.saveParametersDialog("Synthesis", "ready for saving synthetic parameters");
			CS = new constructSimulation(myNest);
			return saveSynthetics(CS.getData(), CS.getCarriageReturn());
		default:
			System.exit(99);
			return null;

		}
	}

	/**
	 * Prompts for comments to describe the project. These comments
	 * form the leading lines of the 'COMMENT' section in the control file.
	 * These lines appear in the control file with the header 'COMMENT '.
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
		vb.getChildren().add(headerGroup("Subject"));
		vb.getChildren().add(facetGroup("Subject Name", 0));
		vb.getChildren().add(facetCountGroup());
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
		VBox vb = new VBox(20);
		Label lb = new Label("Now specify each of the remaining facets.");
		lb.setStyle(sStyle_18);
		lb.setAlignment(Pos.TOP_CENTER);
		lb.setPrefWidth(800);
		vb.getChildren().add(lb);
		vb.getChildren().add(headerGroup("Facets"));
		for (Integer i = 1; i < myNest.getFacetCount(); i++)
			vb.getChildren().add(facetGroup("Facet Name", i));
		content.getChildren().add(vb);
		return content;
	}

	/**
	 * generates bound GUI sub form to specify each specific facet.
	 * iFacetID provides an index for the specific facet.
	 * It is used in both 'mainSubjectGroup' (x 1), and 'subjectsGroup' (x1 to many).
	 *
	 * @param sCue  header string
	 * @param iFacetID original order of new Facet
	 * @return <code>Group</code> essentially the sub -'Scene' for Facet details entry to be sent to the GUI
	 */
	private Group facetGroup(String sCue, Integer iFacetID) {
		Facet currentFacet = myNest.getNewFacet();
		Boolean isNested = false;
		Group facetGroup = new Group();
		HBox layout = new HBox(20);
		layout.setStyle("-fx-padding: 10;-fx-border-color: silver;-fx-border-width: 1;");
		String sFacet = "";
		char cFacet = ' ';
		if (myNest.getDoOver()) {
			oldFacet = myNest.getFacet(iFacetID);
			sFacet = oldFacet.getName();
			cFacet = oldFacet.getDesignation();
			isNested = oldFacet.getNested();
		}
		layout.setAlignment(Pos.BASELINE_LEFT);

		TextField facetName = new TextField(sFacet);
		if (iFacetID < 2)
			repeatFocus(facetName);
		facetName.setPromptText(sCue);
		facetName.setPrefWidth(300);
		facetName.setStyle(customBorder);
		facetName.textProperty().addListener((observable, oldValue, newValue) -> {
			if (newValue != oldValue) {
				currentFacet.setName(newValue.trim());
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
				currentFacet.setDesignation(newValue.trim().toCharArray()[0]);
			}
		});
		Label lbSpacer = new Label(null);
		lbSpacer.setPrefWidth(5);
		Label lbSpacer2 = new Label(null);
		lbSpacer2.setPrefWidth(35);
		ToggleGroup nestGroup = new ToggleGroup();
		RadioButton butCrossed = new RadioButton();
		butCrossed.setSelected(true);
		butCrossed.setToggleGroup(nestGroup);
		RadioButton butNested = new RadioButton();
		butNested.setToggleGroup(nestGroup);
		butNested.setSelected(isNested);
		butNested.setToggleGroup(nestGroup);
		nestGroup.selectToggle(isNested ? butNested : butCrossed);
		butNested.selectedProperty().addListener((observable, oldToggle, newToggle) -> {
			if (newToggle != oldToggle) {
				currentFacet.setNested(newToggle);
			}
		});
		layout.getChildren().add(facetName);
		layout.getChildren().add(facetChar);
		layout.getChildren().add(lbSpacer);
		layout.getChildren().add(butCrossed);
		layout.getChildren().add(butNested);
		layout.getChildren().add(lbSpacer2);
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
	private Group facetCountGroup() {
		Group fc = new Group();
		HBox hb = new HBox(50);
		hb.setLayoutY(50);
		hb.setPadding(new Insets(30, 12, 15, 0));
		Label lFc = new Label("  Select number of facets (excl. subject):    ");
		lFc.setFont(Font.font("ARIAL", 20));
		Integer facCount = myNest.getFacetCount();
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
	 * @param _sColumnHeader  String containing column header text
	 * @return <code>Group</code> essentially the 'Scene' for facet form header entry to be sent to the GUI
	 */
	private Group headerGroup(String _sColumnHeader) {
		Group header = new Group();
		HBox hb = new HBox(30);
		VBox vb = new VBox();
		vb.setAlignment(Pos.BOTTOM_CENTER);
		HBox hb2 = new HBox(10);
		Label lbSubject = new Label(_sColumnHeader);
		lbSubject.setFont(new Font("Arial", 20));
		lbSubject.setPrefWidth(300);
		lbSubject.setAlignment(Pos.BASELINE_CENTER);
		Label lbLabel = new Label("Label");
		lbLabel.setFont(new Font("Arial", 20));
		Label lbNesting = new Label("Nesting");
		lbNesting.setFont(new Font("Arial", 20));
		Label lbCrossed = new Label("crossed");
		Label lbNested = new Label("nested");
		Label lbIndex = new Label("Column Index");
		lbIndex.setFont(new Font("Arial", 20));
		hb.getChildren().add(lbSubject);
		hb.getChildren().add(lbLabel);
		vb.getChildren().add(lbNesting);
		hb2.getChildren().add(lbCrossed);
		hb2.getChildren().add(lbNested);
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
		myNest.createDictionary();
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
		sDictionary = myNest.getDictionary();
		sHDictionary = sDictionary;
		cStarred = myNest.getCStarred();
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
							if (c == cStarred)
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
								myTree = myNest.getTree();
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
						if (sText == cStarred + "*") {
							cell.setText(sText);
							cell.setVisible(true);
							event.consume();
							return;
						} else {
							Integer iStarred = cell.getIndex();
							myNest.setAsterisk(iStarred);
							cStarred = sText.toCharArray()[0];
							cell.setText(sHDictionary.toCharArray()[iStarred] + "*");
							myNest.setAsterisk(iStarred);
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
		Group group = new Group();
		VBox vb = new VBox(20);
		Label title = new Label("Arrange Nesting");
		vb.setAlignment(Pos.TOP_CENTER);
		title.setPrefWidth(800);
		title.setStyle(sStyle_18);
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
							if (iTo >= iPointer)
								iPointer = iTo + 1;
							success = true;
							crossedData.add(iPointer++, sCarried + ":" + lvCrossed.getItems().get(iTo).toString());
							// testList(crossedData);
							saveNested(crossedData);
							lvNested.getItems().remove(sCarried);
							lvNested.refresh();
						}
						/*
						 * let the source know whether the string was
						 * successfully transferred and used
						 */
						event.setDropCompleted(success);
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
		saveNested(crossedData);
		return group;
	}

	/**
	 * generates bound GUI addition to specify one specific name - value
	 * pair and deposits the integer value in a designated target location.
	 * sType has to be either "Integer" or "Double". The result of
	 * appropriate type, however, is passed as a string to myNest.
	 *
	 * @param _sType variable type as String
	 * @param _sName variable name as String
	 * @param _sTarget name as String, where variable is to be stored
	 * @return JavaFX graph element to be used in GUI form
	 */
	private Group simpleVariableGroup(String _sType, String _sName, String _sTarget) {
		Group simpleGroup = new Group();
		HBox layout = new HBox(20);
		layout.setStyle("-fx-padding: 10;-fx-border-color: silver;-fx-border-width: 1;");
		layout.setAlignment(Pos.BASELINE_LEFT);

		Label lbName = new Label(_sName);
		lbName.setFont(new Font("Arial", 20));
		lbName.setPrefWidth(300);
		lbName.setAlignment(Pos.BASELINE_CENTER);
		layout.getChildren().add(lbName);
		TextField compValue = new TextField("");
		repeatFocus(compValue);
		sText = null;
		switch (_sType) {
		case "Integer":
			compValue.setPromptText("Integer");
			break;
		case "Double":
			compValue.setPromptText("Decimal");
			break;
		}
		compValue.textProperty().addListener((observable, oldValue, newValue) -> {
			if (newValue != oldValue)
				sText = newValue;
		});
		compValue.focusedProperty().addListener((obs, oldVal, newVal) -> {
			if (!newVal) {
				myNest.saveVariable(_sType, _sTarget, sText);
				sText = null;
			}
		});
		compValue.setPrefWidth(300);
		compValue.setStyle(customBorder);
		layout.getChildren().add(compValue);
		simpleGroup.getChildren().add(layout);
		return simpleGroup;
	}

	/**
	 * generates bound GUI addition to specify one specific name - value
	 * pair and deposits the integer value in a designated target location.
	 * sType has to be either "Integer" or "Double". The result of
	 * appropriate type, however, is passed as a string to myNest.
	 *
	 * @param _sType  variable type as String
	 * @param _sName  variable name as string
	 * @param _sTarget  name as String, where variable is to be stored
	 * @param sValue  variable value as String
	 * @return  JavaFX graph element to be used in GUI form
	 */
	private Group simpleVariableGroup(String _sType, String _sName, String _sTarget, String sValue) {
		Group simpleGroup = new Group();
		HBox layout = new HBox(20);
		layout.setStyle("-fx-padding: 10;-fx-border-color: silver;-fx-border-width: 1;");
		layout.setAlignment(Pos.BASELINE_LEFT);

		Label lbName = new Label(_sName);
		lbName.setFont(new Font("Arial", 20));
		lbName.setPrefWidth(300);
		lbName.setAlignment(Pos.BASELINE_CENTER);
		layout.getChildren().add(lbName);
		TextField compValue = new TextField(sValue);
		repeatFocus(compValue);
		sText = null;
		switch (_sType) {
		case "Integer":
			compValue.setPromptText("Integer");
			break;
		case "Double":
			compValue.setPromptText("Decimal");
			break;
		}
		compValue.textProperty().addListener((observable, oldValue, newValue) -> {
			if (newValue != oldValue)
				sText = newValue;
		});
		compValue.focusedProperty().addListener((obs, oldVal, newVal) -> {
			if (!newVal) {
				myNest.saveVariable(_sType, _sTarget, sText);
				sText = null;
			}
		});
		compValue.setPrefWidth(300);
		compValue.setStyle(customBorder);
		layout.getChildren().add(compValue);
		simpleGroup.getChildren().add(layout);
		return simpleGroup;
	}

	/**
	 * this group collects and/or displays the user specified
	 * variance components. But firstly, it has to determine
	 * the number of variance components needed.
	 *
	 * @return Group  VarianceComponentsGroup
	 */
	private Group VarianceComponentsGroup() {
		myNest.doComponents();
		Group group = new Group();
		VBox vb = new VBox();
		Label lbHeader = new Label("Now specify variance component values for each component!");
		lbHeader.setStyle(sStyle_18);
		lbHeader.setAlignment(Pos.TOP_CENTER);
		lbHeader.setPrefWidth(800);
		vb.getChildren().add(lbHeader);
		HBox hTitles = new HBox(3);
		Label lbDesig = new Label("Configuration");
		lbDesig.setAlignment(Pos.BASELINE_CENTER);
		lbDesig.setPrefWidth(200);
		lbDesig.setStyle(sStyle_14);
		lbDesig.setTranslateX(110.0);
		Label lbLevel = new Label("Levels");
		lbLevel.setPrefWidth(100);
		lbLevel.setStyle(sStyle_14);
		lbLevel.setTranslateX(80.0);
		Label lbVar = new Label("Variance");
		lbVar.setPrefWidth(110);
		lbVar.setStyle(sStyle_14);
		lbVar.setTranslateX(140.0);
		hTitles.getChildren().addAll(lbDesig, lbLevel, lbVar);
		vb.getChildren().add(hTitles);
		/*
		 * Next we have to construct all allowed configurations
		 * ('Effects' in Brennan's terminology). The total number
		 * of these configurations ('iComps') provides the number
		 * of variance components to be entered.
		 */
		CombConstrct cc = new CombConstrct(myNest);
		int iComps = cc.getComp();
		/*
		 * 'Dawdles' are booleans that represent, whether all the necessary components
		 * of an array have been collected, or whether the collection process
		 * has to go on.
		 */
		VarianceDadleCheck = new Boolean[iComps];
		Boolean bRe = myNest.getDoOver();
		int iUpper = iComps;
		if (bRe)
			iUpper = iComps;
		else
			myNest.createVarianceCoefficients(iComps);
		for (int i = 0; i < iUpper; i++)
			VarianceDadleCheck[i] = false;
		iTFonPage = 0;
		for (Integer i = 0; i < iUpper; i++) {
			if (bRe) {
				String sVc = myNest.getVarianceCoefficient(i).toString();
				Group vc = vcGroup(i, sVc);
				vb.getChildren().add(vc);
				VarianceDadleCheck[i] = true;
			} else
				vb.getChildren().add(vcGroup(i));
		}
		vb.getChildren().get(0).requestFocus();
		ScrollPane sP = new ScrollPane();
		sP.setContent(vb);
		sP.setFitToWidth(true);
		group.getChildren().add(sP);
		return group;
	}

	/**
	 * Subform to handle variance components in VarianceComponentsGroup
	 * for empty fields.
	 *
	 * @param iPos  Integer
	 * @return Group  vcGroup
	 */
	private Group vcGroup(Integer iPos) {
		Group group = new Group();
		String sVC = "";
		if (myNest.getDoOver())
			sVC = myNest.getVarianceCoefficient(iPos).toString();
		HBox hb = new HBox(3);
		// hb.setMaxHeight(0.5);
		String sConfDesig = myTree.getConfiguration(iPos);
		Label lbDesig = new Label(sConfDesig);
		lbDesig.setAlignment(Pos.BASELINE_CENTER);
		lbDesig.setPrefWidth(200);
		lbDesig.setStyle(sStyle_14);
		lbDesig.setTranslateX(100.0);
		String sLevel = ((Integer)myTree.getSize(iPos)).toString();
		Label lbLevel = new Label(sLevel);
		lbLevel.setPrefWidth(100);
		lbLevel.setStyle(sStyle_14);
		lbLevel.setTranslateX(100.0);
		TextField tfVC = new TextField(sVC);
		repeatFocus(tfVC);
		tfVC.setPromptText("   decimal value");
		tfVC.setTranslateX(150.0);
		tfVC.setPrefWidth(80.0);
		sText = null;
		tfVC.textProperty().addListener((observable, oldValue, newValue) -> {
			if (newValue != oldValue)
				sText = newValue;
		});
		tfVC.focusedProperty().addListener((obs, oldVal, newVal) -> {
			if (!newVal) {
				myNest.setVariancecoefficient(iPos, Double.parseDouble(sText));
				VarianceDadleCheck[iPos] = true;
				checkVarianceDawdle();
			}
		});
		hb.getChildren().addAll(lbDesig, lbLevel, tfVC);
		group.getChildren().add(hb);
		iTFonPage++;
		return group;
	}

	/**
	 * Subform to handle variance components in VarianceComponentsGroup
	 * for script driven data entry.
	 *
	 * @param iPos integer
	 * @param sVC string
	 * @return Group vcGroup
	 */
	private Group vcGroup(Integer iPos, String sVC) {
		Group group = new Group();
		String sCompDesig = myTree.getConfiguration(iPos);
		String sLevel = String.valueOf(myTree.getDepth(iPos));
		HBox hb = new HBox(3);
		// hb.setMaxHeight(0.5);
		Label lbDesig = new Label(sCompDesig);
		lbDesig.setAlignment(Pos.BASELINE_CENTER);
		lbDesig.setPrefWidth(200);
		lbDesig.setStyle(sStyle_14);
		lbDesig.setTranslateX(100.0);
		Label lbLevel = new Label(sLevel);
		lbLevel.setPrefWidth(100);
		lbLevel.setStyle(sStyle_14);
		lbLevel.setTranslateX(100.0);
		TextField tfVC = new TextField(sVC);
		VarianceDadleCheck[iPos] = true;
		repeatFocus(tfVC);
		tfVC.setPromptText("   decimal value");
		tfVC.setTranslateX(150.0);
		tfVC.setPrefWidth(80.0);
		sText = null;
		tfVC.textProperty().addListener((observable, oldValue, newValue) -> {
			if (newValue != oldValue) {
				sText = newValue;
				myNest.setVariancecoefficient(iPos, Double.parseDouble(sText));
			}
		});
		tfVC.focusedProperty().addListener((obs, oldVal, newVal) -> {
			if (newVal) {
				// myNest.setVariancecoefficient(iPos,
				// Double.parseDouble(sText));
				VarianceDadleCheck[iPos] = true;
				checkVarianceDawdle();
			}
		});
		hb.getChildren().addAll(lbDesig, lbLevel, tfVC);
		group.getChildren().add(hb);
		iTFonPage++;
		return group;
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
		Integer iMax = 0;
		String sTemp = null;
		if (myNest.getDoOver())
			if (!isNested)
				return myNest.getNests();
			else
				return FXCollections.observableArrayList(new ArrayList<String>());
		else {
			sHDictionary = myNest.getHDictionary();
			ArrayList<String> result = new ArrayList<>();
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
			return FXCollections.observableArrayList(result);
		}
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
	 * G_String cycles through 'setSampleSize' until all the sample sizes
	 * for all facets (in hierarchical order) have been collected, when the variable 'bDawdle'
	 * turns to 'false'. The actual form is constructed as 'getPage' within 'SampleSizeTree',
	 * where the sample sizes are being stored as well.
	 *
	 * @return <code>Group</code> essentially the 'Scene' for setting sample sizes entry to be sent to the GUI
	 */
	public Group setSampleSize() {
		myTree.collectSampleSizes();
		Group group = new Group();
		// construct samples page
		group.getChildren().add(myTree.getPage(iSample));
		myNest.setDawdle(iSample++ < myNest.getNestCount() - 1);
		return group;
	}

	/**
	 * Collects lower and upper score cutoff and targeted mean score
	 *
	 * @return <code>Group</code> essentially the 'Scene' for score range entry to be sent to the GUI
	 */
	private Group baseScaleGroup() {
		Group content = new Group();
		iTFonPage = 0;
		VBox vb = new VBox(20);
		// vb.setPrefHeight(600);
		Label title = new Label("Specify score limits (as integers) and score mean (as decimal value).");
		title.setPrefWidth(800);
		title.setStyle(sStyle_18);
		title.setAlignment(Pos.TOP_CENTER);
		vb.getChildren().add(title);
		if (myNest.getDoOver()) {
			vb.getChildren().add(simpleVariableGroup("Integer", "Floor", "cFloor", myNest.getFloor().toString()));
			iTFonPage++;
			vb.getChildren().add(simpleVariableGroup("Double", "Mean", "cMean", myNest.getMean().toString()));
			iTFonPage++;
			vb.getChildren().add(simpleVariableGroup("Integer", "Ceiling", "cCeiling", myNest.getCeiling().toString()));
		} else {
			vb.getChildren().add(simpleVariableGroup("Integer", "Floor", "cFloor"));
			iTFonPage++;
			vb.getChildren().add(simpleVariableGroup("Double", "Mean", "cMean"));
			iTFonPage++;
			vb.getChildren().add(simpleVariableGroup("Integer", "Ceiling", "cCeiling"));
		}
		content.getChildren().add(vb);
		vb.getChildren().get(1).requestFocus();
		return content;
	}

	/**
	 * thread utility for javafx
	 *
	 * @see <a href="https://docs.oracle.com/javase/8/javafx/api/index.html?javafx/application/Platform.html">JavaFX platform</a>
	 * @param facetName  Text field for response entry
	 */
	private void repeatFocus(TextField facetName) {
		if (iTFonPage < 1)
			Platform.runLater(() -> {
				if (!facetName.isFocused()) {
					facetName.requestFocus();
					repeatFocus(facetName);
				}
			});
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
	 * checks if all required variances have been entered.
	 */
	private void checkVarianceDawdle() {
		if (VarianceDadleCheck != null) {
			Boolean bTotal = true;
			for (Boolean element : VarianceDadleCheck)
				bTotal = bTotal && element;
			if (!bTotal)
				myNest.setVarianceDawdle(bTotal);
		}
	}

	/**
	 * Sets up data file to save the synthetic data.
	 *
	 * @param _darData Double[]
	 * @param _salCarriageReturn ArrayList
	 * @return saveSynthetics  Group
	 */
	private Group saveSynthetics(Double[] _darData, ArrayList<String> _salCarriageReturn) {
		Group group = new Group();
		File selectedFile = flr.getFile(false, "Select data file to be saved");
		if (selectedFile != null) {
			String sInitial = selectedFile.getParent();
			prefs.put("Home Directory", sInitial);
			String sFileName = selectedFile.getPath();
			saveDataFile(sFileName, _darData, _salCarriageReturn);
		}
		return group;
	}

	/**
	 * Takes the abstract synthetic data set 'darData', adds the mean value, and constrains the output
	 * between maximum and minimum boundaries. Values get formatted and bundled into lines,
	 * each with an appropriate leader (that later will be ignored again in the analysis), and, finally,
	 * saved in a file with the chosen file name. A summary of the file will be displayed on the screen.
	 *
	 * @param _sFileName String
	 * @param _darData  Double[]
	 * @param _salCarriageReturn  ArrayList
	 */
	private void saveDataFile(String _sFileName, Double[] _darData, ArrayList<String> _salCarriageReturn) {
		File fout = new File(_sFileName);
		PrintStream writer = null;
		Double dTemp = 0.0;
		try {
			writer = new PrintStream(fout);
		} catch (FileNotFoundException e) {
			logger.warning(e.getMessage());
		}
		int iCeiling = myNest.getCeiling();
		int iFloor = myNest.getFloor();
		StringBuilder sb = new StringBuilder();
		Double dMean = myNest.getMean();
		int iMaxLine = _salCarriageReturn.size();
		int iPointer = 0;
		int iNext = 0;

		String sDelim = "\t";
		int iItem = 0;
		String[] sLeaders = null;
		String sHeader = null;
		/*
		 * the 'Lehmer signer' is initialized (see utilities.Lehmer), to make
		 * the synthetic data file distinguishable from empiric data.
		 */
		Lehmer Signer = new Lehmer(iFloor, iCeiling);
		for (int iLine = 0; iLine < iMaxLine; iLine++) {
			sLeaders = _salCarriageReturn.get(iLine).split ("\\|");
			sHeader = sLeaders[0];
			if (iLine < iMaxLine - 1) {
				iNext = Integer.parseInt(sLeaders[1]);
			} else
				iNext = _darData.length;
			sb = new StringBuilder(sHeader);
			while ( iPointer < iNext){
				dTemp = dMean +_darData[iPointer];
				if (dTemp > iCeiling)
					iMaxCut++;
				else if (dTemp < iFloor)
					iMinCut++;
				else
					iNoCut++;
				// now fit result between minimum and maximum
				iItem = (int) Math.round(dTemp);
				iItem = Math.min(iItem, iCeiling);
				iItem = Math.max(iItem, iFloor);
				iItem = Signer.adjust(iItem);		// applies signature to each score
				sb.append(sDelim + iItem);
				iPointer++;
			}
			String sLine = sb.toString();
			writer.println(sLine);
		}
		writer.close();
		/**
		 * Here comes the summary part.
		 */

		int iPercentage = (100 * (iMinCut + iMaxCut)) / (iPointer + iNext);
		String sFeedback = iNoCut.toString() + " scores were in range.\n" + iMinCut.toString()
				+ " had to be restrained at bottom. " + iMaxCut.toString() + " had to be restrained at top.\n "
				+ "Otherwise, a total of " + iPercentage + "% would have been out of bounds.";
		Alert alert = new Alert(AlertType.INFORMATION);
		alert.setHeaderText("Secondary score adjustments:");
		alert.setContentText(sFeedback);
		alert.showAndWait();
		
		System.exit(0);
	}

	/**
	 * A special javafx construct to defer operation until ready.
	 * See: https://riptutorial.com/javafx/example/7291/updating-the-ui-using-platform-runlater
	 *
	 * @param node Node
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
			Alert alert = new Alert(AlertType.WARNING);
			alert.setContentText("You did not go properly through setup \n  (see manual)!");
			alert.showAndWait();
			myMain.doSetup();
		}
	}
}
