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
import java.util.prefs.Preferences;
import application.Main;
import model.Facet;
import model.Nest;
import model.SampleSizeTree;
import utilities.FacetModView;
import utilities.Filer;
import utilities.Popup;
import view.rootLayoutController;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.Node;
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
//import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.util.Callback;

public class AnaGroups {

	private rootLayoutController myController;
	private String customBorder;
	private static ListView<String> lvCrossed = null;
	private static ListView<String> lvNested = null;
	private static ListView<String> lvFacets = null;
	private ObservableList<String> nestedData;
	private ObservableList<String> crossedData;
	private Integer iFrom;
	private Integer iTo;
	private char cStarred;
	private Integer iPointer;
	private Nest myNest;
	private SampleSizeTree myTree = null;
	private Facet oldFacet = null;
	private Integer iSample = 0;
	private String sDictionary = null;
	private String sHDictionary;
	private Filer flr = null;
	private String sStyle_20 = null;
	private String sStyle_18 = null;
	private Preferences prefs = null;
	private File selectedFile = null;
	private String sControlFileName = null;
	private TextArea taOutput = null;
	private Boolean bDownStep = false;
	private Boolean bFirstAnalysis = true;
	private StringBuilder sbResult; // accumulates results
	private Popup popup;

	public AnaGroups(Nest _nest, Popup _popup, rootLayoutController _controller, Stage _stage, Preferences _prefs, Filer _flr) {
		/**
		 * 'AnaGroups' manages the G-Analysis by taking the user step-by-step
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
		 */
		
		myNest = _nest;
		flr = _flr;
		sControlFileName = myNest.getControlFileName();
		myController = _controller;
		popup = _popup;
		popup.setClass("AnaGroups");
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
		popup = _popup;
	}

	public Group getGroup(Boolean _bDownStep) throws Throwable {
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
		 */
		
		bDownStep = _bDownStep;
		Integer iStep = myNest.getStep();
		if (iStep == 0)
			myController.callForAction(true);
		else
			myController.callForAction(false);
		myController.setStep(iStep);
	 	switch (iStep) {
		case 0:
			myController.buttonsEnabled(false);
			myController.disableSave(true);
			myNest.setDoOver(false);
			return startUp();
		case 1:
			myController.buttonsEnabled(true);
			return setTitle();
		case 2:
			return addComments();
		case 3:
			return mainSubjectGroup();
		case 4:
			return subjectsGroup();
		case 5:
			return orderFacets();
		case 6:
			return setNestingGroup();
		case 7:
			myNest.setOrder();
			myNest.G_setFacets();
			return selectDataFile();
		case 8:
			myTree.collectSampleSizes(flr);
			return setSampleSize();
		case 9:
			flr.writeDataFileNew();
			myController.disableSave(false);
			return runBrennan();
		case 10:
			if (bFirstAnalysis)
				flr.getUrGenova();
			bFirstAnalysis = false;
			myNest.setDawdle(true);
			return Analysis();
		default:
			System.exit(99);
			return null;

		}
	}

	private Group startUp() {
		/*
		 * Step 0: Constructs scene_0, inviting user to choose her/his action.
		 */
		
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

	private Group setTitle() {
		/*
		 * Step 1: If in 'Do over' mode, prompts for previous control file and
		 * loads it. Then it allows specification of a new project title,
		 * or changing a previously entered one.
		 */
		
		testSetup();							// just for security, the program checks if
												// it had been properly set up, and provides feedback
												// otherwise.
		Group group = new Group();
		VBox vb = new VBox(100);
		vb.setAlignment(Pos.TOP_CENTER);
		String projectTitle = null;
		if (bDownStep)							// in case of back stepping
			projectTitle = myNest.getTitle();
		
		/**
		 * In both Analysis and Synthesis users have the choice to
		 * either enter all the design variables manually, or by
		 * recalling them from the control file, prepared earlier,
		 * as one of the two input files for urGENOVA. This is set
		 * in the menu as the boolean 'bDoOver'.
		 * But throughout, users can always change parameters along
		 * the way, which then get recorded again in the resulting
		 * new control file.
		 */
		
		if (myNest.getDoOver()) {
			selectedFile = flr.getFile(true, "Select Analysis Control File");
			if (selectedFile != null) {
				String sFileName = selectedFile.getName();
				prefs.put("Control", sFileName);
				flr.readFile(selectedFile);
				projectTitle = myNest.getTitle();
			} else {
				return null;
			}
		}
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
		vb.getChildren().addAll(lb, tf);
		group.getChildren().add(vb);
		return group;
	}

	private Group addComments() throws IOException {
		/*
		 * Step 2: Prompts for comments to describe the project. These comments
		 * form the leading lines of the 'COMMENT' section in the control file.
		 * Thes lines appear in the control file with the header 'COMMENT '.
		 * G_String then adds the facet names and their 1 char designations
		 * in the original facet order. These lines appear in the control file
		 * with the header 'COMMENT*'.
		 */
		
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
		if (myNest.getDoOver() || bDownStep) {
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

	private Group mainSubjectGroup() {
		/*
		 * Step 3: Prompts for the main subject facet name, designation, and a choice
		 * between crossed and nested status. This method uses one 'facetSubForm' and one 
		 * 'facetCountSubForm.
		 */
		
		Group content = new Group();
		VBox vb = new VBox(20);
		// vb.setPrefHeight(600);
		Label title = new Label("Specify subject and number of facets.");
		title.setPrefWidth(800);
		title.setStyle(sStyle_18);
		title.setAlignment(Pos.TOP_CENTER);
		vb.getChildren().add(title);
		vb.getChildren().add(headerSubForm("Subject"));
		vb.getChildren().add(facetSubForm("Subject Name", 0));
		vb.getChildren().add(facetCountSubForm());
		content.getChildren().add(vb);
		return content;
	}

	private Group subjectsGroup() {
		/*
		 * Step 4: Prompts for the features of each additional facet,
		 * again in original facet order. This method uses as many facetSubForms
		 * as necessary.
		 */
		
		Group content = new Group();
		VBox vb = new VBox(20);
		// vb.setPrefHeight(600);
		Label lb = new Label("Now specify each of the remaining facets.");
		lb.setStyle(sStyle_18);
		lb.setAlignment(Pos.TOP_CENTER);
		lb.setPrefWidth(800);
		vb.getChildren().add(lb);
		vb.getChildren().add(headerSubForm("Facets"));
		for (Integer i = 1; i < myNest.getFacetCount(); i++)
			vb.getChildren().add(facetSubForm("Facet Name", i));
		content.getChildren().add(vb);
		return content;
	}

	private Group facetSubForm(String sCue, Integer iFacetID) {
		/*
		 * generates bound GUI sub form to specify each specific facet.
		 * iFacetID provides an index for the specific facet.
		 * It is used in both 'mainSubjectGroup' (x 1), and 'subjectsGroup' (x1 to many).
		 */
		
		Facet currentFacet = myNest.getNewFacet();
		currentFacet.setOffset(-1);
		Boolean isNested = false;
		Group facetGroup = new Group();
		HBox layout = new HBox(20);
		layout.setStyle("-fx-padding: 10;-fx-border-color: silver;-fx-border-width: 1;");
		String sFacet = "";
		char cFacet = ' ';
		if (myNest.getDoOver() || bDownStep) {
			oldFacet = myNest.getFacet(iFacetID);
			sFacet = oldFacet.getName();
			cFacet = oldFacet.getDesignation();
			isNested = (Boolean) oldFacet.getNested();
		}
		layout.setAlignment(Pos.BASELINE_LEFT);
		;
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

	private Group facetCountSubForm() {
		/**
		 * Generates subform for spinner to enter number of
		 * additional facets.
		 * Is used once in 'subjectsGroup'.
		 */
		
		Group fc = new Group();
		HBox hb = new HBox(50);
		hb.setLayoutY(50);
		hb.setPadding(new Insets(30, 12, 15, 0));
		Label lFc = new Label("  Select number of facets (excl. subject):    ");
		lFc.setFont(Font.font("ARIAL", 20));
		Integer facCount = myNest.getFacetCount();
		final Spinner<Integer> facetCount = new Spinner<Integer>();
		if (!myNest.getDoOver() && !bDownStep)
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

	private Group headerSubForm(String _sColumnHeader) {
		/**
		 * Generates the subform for the header line in all subject forms.
		 */
		
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

	private Group orderFacets() {
		/*
		 * Step 5 Group to set facet order and line change position.
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
		 */

		myNest.createDictionaries();
		lvFacets = new ListView<String>();
		ObservableList<String> orderedData = FXCollections.observableArrayList();
				// see a short discussion of 'ObservableList' in the 'FilteredFacetList' method below.

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
		if ((sHDictionary == null) || bDownStep) {
			sHDictionary = sDictionary;
			myNest.setHDictionary(sDictionary);
		}
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
							if (c == cStarred){
								sTemp += "*";
								myNest.setAsterisk(cStarred);
							}
							setText(sTemp);
							setFont(Font.font(20));
						}
					}
				};

				cell.setOnDragDetected(new EventHandler<MouseEvent>() {
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
							myNest.setAsterisk(cStarred);
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

	private Group setNestingGroup() {
		/*
		 * Step 6 arranges nesting details, again using 'drag and drop'.
		 * But while in step 5 items were moved within the same list, in
		 * step 6 items are moved from the list on the left (nested facets) 
		 * to the list on the right (crossed effects). 
		 * However, the basic mechanism stays the same.
		 */

		if (bDownStep)
			myNest.setNests(null);
		
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
		title.setStyle(sStyle_20);
		title.setAlignment(Pos.TOP_CENTER);
		vb.getChildren().add(title);
		HBox hb = new HBox(5);
		hb.setAlignment(Pos.CENTER);
		lvCrossed = new ListView<String>();
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
							if (iTo >= iPointer)
								iPointer = iTo + 1;
							success = true;
							crossedData.add(iPointer++, sCarried + ":" + lvCrossed.getItems().get(iTo).toString());
							// testList(crossedData);
							saveNested(crossedData);
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
		lvNested = new ListView<String>();
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

	private ObservableList<String> filteredFacetList(Boolean isNested) {
		/**
		 * An 'ObservableList' is a Javafx construct that enables listeners 
		 * to track changes in the list, when they occur. A ListChangeListener is 
		 * an interface that receives notifications of changes to an ObservableList.
		 * This construct is used in the method 'setNestingGroup' for both the list 
		 * of crossed and nested facets. That makes the visual arranging
		 * of facet nesting possible.
		 */
		
		Integer iMax = 0;
		String sTemp = null;
		if (myNest.getDoOver() && !bDownStep)
			if (!isNested)
				return myNest.getNests();
			else
				return FXCollections.observableArrayList(new ArrayList<String>());
		else {
			sHDictionary = myNest.getHDictionary();
			ArrayList<String> result = new ArrayList<String>();
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

	private Group selectDataFile() {
		/**
		 * Prompts for the data file; if no valid file is selected,
		 * an error message pops up, and the program will exit.
		 */
		
		selectedFile = flr.getFile(true, "Select Analysis Data File");
		if (selectedFile == null) {
			popup.tell("selectDataFile_a", "Diagnostic: File not found!");
			System.exit(10);
			return null;
		}
		 else {
			prefs.put("Home Directory", selectedFile.getParent());
			prefs.put("Data Raw", "Data.txt");
			flr.readDataFileNew(selectedFile);
				// reads data file into filer table 'sRawData'
			return flr.showTableNew();
				// displays 'sRawData' and allows index suppressing
		}
	}

	private void saveNested(ObservableList<String> _crossed) {
		/**
		 * Saves the nested list to the 'Nest' repository, and returns it for further use
		 * in AnaGroups as a formal tree structure.
		 */
		
		String[] sNests = null;
		ArrayList<String> sarNests = new ArrayList<String>();
		Integer iLength = _crossed.size();
		for (Integer i = 0; i < iLength; i++) {
			String sNest = _crossed.get(i);
			if ((sNest.length() == 1) || (sNest.indexOf(':') >= 0))
				sarNests.add(sNest);
		}
		sNests = sarNests.toArray(new String[sarNests.size()]);
		myNest.setNests(sNests);
		myTree = myNest.getTree(); // to be available for the next step
	}

	public Group setSampleSize() {
		/**
		 * G_String cycles through 'setSampleSize' until all the sample sizes
		 * for all facets (in hierarchical order) have been collected, when the variable 'bDawdle'
		 * turns to 'false'. The actual form is constructed as 'getPage' within 'SampleSizeTree',
		 * where the sample sizes are being stored as well.
		*/
		
		Group group = new Group();
		// construct samples page
		group.getChildren().add(myTree.getPage(iSample));
		myNest.setDawdle(iSample++ < myNest.getNestCount() - 1); // iSample ==
																	// iNest in
																	// hierarchical
																	// order
		return group;
	}

	public Group runBrennan() {
		/**
		 * this is the crucial step 9 where urGENOVA is tasked with calculating the estimates
		 * of variance coefficients for all facets and their appropriate calculations.
		 */

		String tLine = null;
		myNest.setLevels();

		String slash = File.separator;
		String sControl = prefs.get("Working Directory", null) + slash + sControlFileName;
		File brennanControl = new File(sControl);
		if (brennanControl != null) {
			flr.writeAnalysisControlFile(brennanControl);
			flr.saveParametersDialog("Analysis", "Save Analysis Control File?");
		}
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
		} catch (IOException e) {
			popup.tell("runBrennan_a", e);
		}

		String line;
		sbResult = new StringBuilder();
		BufferedReader input = new BufferedReader(new InputStreamReader(process.getInputStream()));
		try {
			while ((line = input.readLine()) != null) {
				sbResult.append(line);
			}
		} catch (IOException e1) {
			popup.tell("runBrennan_b", e1);
		}
		line = sbResult.toString();
		Label lbGenova = new Label("urGenova said: " + line);
		if (line.indexOf("Successful") < 0)
			lbGenova.setTextFill(Color.RED);
		try {
			process.waitFor();
		} catch (InterruptedException e) {
			popup.tell("runBrennan_c", e);
		}
		TextArea taOutput = new TextArea();

		InputStream is = null;
		try {
			is = new FileInputStream(prefs.get("Working Directory", null) + "/~control.txt.lis");
		} catch (FileNotFoundException e) {
			popup.tell("runBrennan_d", e);
		}
		
		/**
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
			popup.tell("runBrennan_e", e);
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

	public Group Analysis() {
		/**
		 * This is step 10. 'Analysis' extracts the means and estimated variance coefficients
		 * from the urGenova output file, and uses them to calculate G-Study and D-Study results
		 * see 'VarianceComponents' in the 'Algorithm' section of the explanations.
		 */
		
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
			popup.tell("Analysis_a", ioe);
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
		;
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

	private void repeatFocus(Node node) {
		Platform.runLater(() -> {
			if (!node.isFocused()) {
				node.requestFocus();
				repeatFocus(node);
			}
		});
	}

	private String formatDouble(Double _d) {
		if (_d % 1.0 != 0)
			return String.format("%.2f", _d);
			//return String.format("%.4f", _d);	// Frithjoff change
		else
			return String.format("%.0f", _d);
	}

	public void reset() {
		nestedData.clear();
		crossedData.clear();
		sDictionary = null;
		sHDictionary = null;
	}

	private void testSetup() {
		/**
		 * Checks if a working directory has been specified previously,
		 * and the operating system specific urGENOVA code has been installed.
		 */
		
		String sWorkingDirectory = prefs.get("Working Directory", null);
		String sOS_Full = System.getProperty("os.name");
		String sUrGenova = null;
		if (sOS_Full.indexOf("Windows") >=0)
			sUrGenova = "urgenova.exe";
		else
			sUrGenova = "urGenova";
		File f = new File(sWorkingDirectory, sUrGenova);
		if (!f.isFile()){
			popup.tell("testSetup_a", "You did not go properly through setup (see manual)!");
			System.exit(10);
		}
	}

	public void saveAll() throws IOException {
		/**
		 * In response to GUI saves all the analysis results in a text file
		 * according to user's instructions.
		 */
		
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
}
