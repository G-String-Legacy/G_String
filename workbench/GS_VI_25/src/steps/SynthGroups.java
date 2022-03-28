package steps;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.prefs.Preferences;

import model.Components; // false diagnostic
import model.Facet;
import model.Nest;
import model.SampleSizeTree;
import model.aNode;
import utilities.Filer;
import utilities.Lehmer;
import utilities.Popup;
import utilities.constructSimulation;
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
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.util.Callback;

public class SynthGroups {

	// similar to dGroups, but for purpose of synthesizing
	// provides step specific display groups in central pane
	// it will bind display field controls to properties within process
	// private vault myVault;

	private rootLayoutController myController;
	private String customBorder;
	private static ListView<String> lvCrossed = null;
	private static ListView<String> lvNested = null;
	private static ListView<String> lvFacets = null;
	private ObservableList<String> nestedData = null;
	private ObservableList<String> crossedData = null;
	private Boolean bAutoindex = false;
	private Integer iFrom = null;
	private Integer iTo = null;
	private Integer iStep = 0;
	private char cStarred;
	private Integer iPointer = null;
	private Nest myNest = null;
	private SampleSizeTree myTree = null;
	private Stage myStage = null;
	private Facet oldFacet = null;
	private Integer iSample = 0;
	private String sDictionary = null;
	private String sHDictionary = null;
	private Filer flr = null;
	private String sStyle_20 = null;
	private String sStyle_18 = null;
	private String sStyle_14 = null;
	private Preferences prefs = null;
	private String sOutputPath2 = null;
	private TextArea taOutput = null;
	private Boolean bDownStep = false;
	private Boolean[] VarianceDadleCheck = null;
	private constructSimulation CS = null;
	private String sText = null;
	private String sInitial = null;
	private Integer iTFonPage = 0;
	private Integer iMinCut = 0;
	private Integer iMaxCut = 0;
	private Integer iNoCut = 0;
	private Popup popup = null;
	@SuppressWarnings("unused")
	private Components comps = null; // attention: comps generates nodes and
										// feeds them to the Nest
										// false diagnostic

	public SynthGroups(Nest _nest,Popup _popup, rootLayoutController _controller, Stage _stage, Preferences _prefs) {
		// Constructor
		myNest = _nest;
		myController = _controller;
		myStage = _stage;
		popup = _popup;
		popup.setObject(11,  53);
		nestedData = FXCollections.observableArrayList();
		crossedData = FXCollections.observableArrayList();
		iFrom = -1;
		iTo = -1;
		iPointer = 0;
		prefs = _prefs;
		flr = new Filer(myNest, prefs, popup);
		customBorder = prefs.get("Border", null);
		sStyle_18 = prefs.get("Style_18",
				"-fx-font-size: 18px; -fx-font-family: \"ARIAL\"; -fx-padding: 10; -fx-background-color: #805015; -fx-text-fill: #FFFFFF;");
		sStyle_20 = prefs.get("Style_20",
				"-fx-font-size: 30px; -fx-font-family: \"ARIALSerif\"; -fx-padding: 10; -fx-background-color: #805015; -fx-text-fill: #FFFFFF;");
		if (myNest.getDoOver()) {
			File selectedFile = flr.getFile(true, "Select Control File", myStage);
			if (selectedFile != null) {
				sInitial = selectedFile.getParent();
				popup.tell("819a", "setTitle: Initial directory, actual: " + sInitial);
				prefs.put("Home Directory", sInitial);
				String sFileName = selectedFile.getName();
				prefs.put("Control", sFileName);
				flr.readFile(selectedFile);
			}
		}
	}

	public Group getGroup(Boolean _bDownStep) throws Throwable {
		// returns the display group to steppers in main
		bDownStep = _bDownStep;
		checkVarianceDawdle();
		if (iStep == 0)
			myController.callForAction(true);
		else
			myController.callForAction(false);
		iStep = myNest.getStep();
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
			popup.tell("820a", "Step 1 done; main subject specified.");
			return subjectsGroup();
		case 4:
			return orderFacets();
		case 5:
			popup.tell("820b", "Step 3 done; Facets ordered and starred.");
			return setNestingGroup();
		case 6:
			popup.tell("820c", "Step 4 done; facets nested successfully.");
			myNest.setOrder();
			myNest.G_setFacets();
			myNest.setDawdle(true);
			return setSampleSize();
		case 7:
			iTFonPage = 0;
			return baseScaleGroup();
		case 8:
			comps = new Components(myNest);
			//setComponentStructure();
			myNest.setVarianceDawdle(true);
			return VarianceComponentsGroup();
		case 9:
			flr.saveParametersDialog("Synthesis", "ready for saving synthetic parameters", myStage);
			CS = new constructSimulation(myNest);
			return saveSynthetics(CS.getData(), CS.getCarriageReturn());
		default:
			System.exit(99);
			return null;

		}
	}

	private Group addComments() throws IOException {
		/*
		 * Step 2: Specifies comments to describe the project
		 */
		Group group = new Group();
		VBox vb = new VBox(20);
		Label lb = new Label("Edit or add comment describing details of this analysis.");
		lb.setStyle(sStyle_20);
		lb.setPrefWidth(800.0);
		popup.tell("821a", "Preferences: " + prefs.get(("Format_18"), null));
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
		 * Step 3: Specifies the main subject facet and number of facets
		 */
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

	private Group subjectsGroup() {
		/*
		 * Step 4: Specifies the features of each additional facet
		 */
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

	private Group facetGroup(String sCue, Integer iFacetID) {
		/*
		 * generates bound GUI addition to specify one specific facet. -
		 * iFacetID provides an index for the specific facet. - bAutoIndex makes
		 * the column selector field visible (true)
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
				popup.tell("883a", "Facet '" + iFacetID + "' name: " + newValue);
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
				popup.tell("883b", "Facet designation '" + newValue + "'");
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
				if (newToggle)
					popup.tell("883c", "Nested");
			}
		});
		Spinner<Integer> colSelect = null;
		/*if (myNest.getAutoIndex()) {
			colSelect = new Spinner<Integer>();
			Integer iOffset = currentFacet.getOffset();
			SpinnerValueFactory<Integer> valueFactory = new SpinnerValueFactory.IntegerSpinnerValueFactory(-1, 10,
					iOffset, 1);
			colSelect.setValueFactory(valueFactory);
			colSelect.setPrefWidth(75);
			colSelect.valueProperty().addListener((observable, oldValue, newValue) -> {
				if (newValue != oldValue) {
					currentFacet.setOffset(newValue - 1);
					logger.fine("Index offset: " + (newValue - 1));
				}
			});
		}*/
		layout.getChildren().add(facetName);
		layout.getChildren().add(facetChar);
		layout.getChildren().add(lbSpacer);
		layout.getChildren().add(butCrossed);
		layout.getChildren().add(butNested);
		layout.getChildren().add(lbSpacer2);
		if (bAutoindex)
			layout.getChildren().add(colSelect);
		facetGroup.getChildren().add(layout);
		return facetGroup;
	}

	private Group facetCountGroup() {
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
				popup.tell("825a", "Facet count: " + newValue);
			}
		});
		hb.getChildren().add(lFc);
		hb.getChildren().add(facetCount);
		fc.getChildren().add(hb);
		return fc;
	}

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
		if (bAutoindex)
			hb.getChildren().add(lbIndex);
		header.getChildren().add(hb);
		return header;
	}

	private Group orderFacets() {
		/*
		 * Step 5 Group to set facet order and line change position.
		 */

		myNest.createDictionaries();
		lvFacets = new ListView<String>();
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
		popup.tell("827a", "Ordering - sDictionary: " + sDictionary);
		sHDictionary = myNest.getHDictionary();
		if ((sHDictionary == null) || bDownStep) {
			sHDictionary = sDictionary;
			myNest.setHDictionary(sDictionary);
		}
		popup.tell("827b", "Ordering - sHDictionary: " + sHDictionary);
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
							popup.tell("827c", "Order CellFactory: " + sTemp);
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
								popup.tell("827c", "Ordered dictionary: " + sHDictionary);
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
						// System.out.println("-- clicked on " + sText);
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

	private Group setNestingGroup() {
		/*
		 * Step 6 arranges nesting details
		 */
		// final
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
		title.setStyle(sStyle_18);
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

	private Group simpleVariableGroup(String _sType, String _sName, String _sTarget) {
		/*
		 * generates bound GUI addition to specify one specific name - value
		 * pair and deposits the integer value in a designated target location.
		 * sType has to be either "Integer" or "Double". The result of
		 * appropriate type, however, is passed as a string to myNest.
		 */
		Group simpleGroup = new Group();
		HBox layout = new HBox(20);
		layout.setStyle("-fx-padding: 10;-fx-border-color: silver;-fx-border-width: 1;");
		layout.setAlignment(Pos.BASELINE_LEFT);
		;
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
			if (newVal == false) {
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

	private Group simpleVariableGroup(String _sType, String _sName, String _sTarget, String sValue) {
		/*
		 * generates bound GUI addition to specify one specific name - value
		 * pair and deposits the integer value in a designated target location.
		 * sType has to be either "Integer" or "Double". The result of
		 * appropriate type, however, is passed as a string to myNest.
		 */
		Group simpleGroup = new Group();
		HBox layout = new HBox(20);
		layout.setStyle("-fx-padding: 10;-fx-border-color: silver;-fx-border-width: 1;");
		layout.setAlignment(Pos.BASELINE_LEFT);
		;
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
			if (newVal == false) {
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

	private Group VarianceComponentsGroup() {
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
		Integer iComps = myNest.getCompCount();
		VarianceDadleCheck = new Boolean[iComps];
		Boolean bRe = myNest.getDoOver();
		Integer iUpper = iComps;
		if (bRe)
			iUpper = myNest.getVcDim();
		for (int i = 0; i < iUpper; i++)
			VarianceDadleCheck[i] = false;
		iTFonPage = 0;
		for (Integer i = 0; i < iUpper; i++) {
			if (bRe) {
				String sVc = myNest.getVarianceCoefficient(i).toString();
				vb.getChildren().add(vcGroup(i, sVc));
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

	private Group vcGroup(Integer iPos) {
		aNode node = myNest.getNode(iPos);
		Group group = new Group();
		String sCompDesig = node.getConfiguration();
		String sLevel = node.getDepth().toString();
		String sVC = "";
		if (myNest.getDoOver())
			sVC = myNest.getVarianceCoefficient(iPos).toString();
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
			if (newVal == false) {
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

	private Group vcGroup(Integer iPos, String sVC) {
		aNode node = myNest.getNode(iPos);
		Group group = new Group();
		String sCompDesig = node.getConfiguration();
		String sLevel = node.getDepth().toString();
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
			if (newVal == true) {
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

	// until here________________________________________________________

	private ObservableList<String> filteredFacetList(Boolean isNested) {
		// set up observable lists for ordering
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

	private void saveNested(ObservableList<String> _crossed) {
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
		Group group = new Group();
		// construct samples page
		group.getChildren().add(myTree.getPage(iSample));
		myNest.setDawdle(iSample++ < myNest.getNestCount() - 1);
		return group;
	}

	private Group baseScaleGroup() {
		/*
		 * Step 6: Specifies score limits and mean
		 */
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

	private void repeatFocus(TextField facetName) {
		if (iTFonPage < 1)
			Platform.runLater(() -> {
				if (!facetName.isFocused()) {
					facetName.requestFocus();
					repeatFocus(facetName);
				}
			});
	}

	public void reset() {
		nestedData.clear();
		crossedData.clear();
		sDictionary = null;
		sHDictionary = null;
	}

	public void saveAll() {
		if ((sOutputPath2 != null) && (taOutput != null)) {
			File outFile = new File(sOutputPath2);
			if (outFile != null) {
				String filePath = outFile.getAbsolutePath();
				try (BufferedWriter writer = Files.newBufferedWriter(Paths.get(filePath))) {
					writer.write(taOutput.getText());
				} catch (IOException e) {
					popup.tell("840a","Print Failure:", e);
				}
			}
		}
	}

	private void readOld() {
		String sInitial = prefs.get("Home Directory", File.separator);

		if (myNest.getDoOver()) {
			File selectedFile = flr.getFile(true, "Select Synthesis Control File", myStage);
			if (selectedFile != null) {
				sInitial = selectedFile.getParent();
				popup.tell("841a", "setTitle: Initial directory, actual: " + sInitial);
				prefs.put("Home Directory", sInitial);
				String sFileName = selectedFile.getName();
				popup.tell("841b", "setTitle: selected Filename: " + sFileName);
				prefs.put("Control", sFileName);
				flr.readFile(selectedFile);
			}
		}
	}

	private void checkVarianceDawdle() {
		if (VarianceDadleCheck != null) {
			Boolean bTotal = true;
			for (int i = 0; i < VarianceDadleCheck.length; i++)
				bTotal = bTotal && VarianceDadleCheck[i];
			if (!bTotal)
				myNest.setVarianceDawdle(bTotal);
			// System.out.println(bTotal);
		}
	}

	private Group saveSynthetics(Double[] _darData, ArrayList<String> _salCarriageReturn) {
		String sInitial = prefs.get("Home Directory", File.separator);
		Group group = new Group();
		File selectedFile = null;
		FileChooser fc = new FileChooser();
		fc.setTitle("Select data file to be saved");
		File f = new File(sInitial);
		if (f != null)
			fc.setInitialDirectory(f);
		try {
			selectedFile = fc.showSaveDialog(myStage);
		} catch (Exception e) {
			popup.tell("843a", "File directory not found");
			System.exit(13);
		}
		if (selectedFile != null) {
			sInitial = selectedFile.getParent();
			popup.tell("843b", "setTitle: Initial directory, actual: " + sInitial);
			prefs.put("Home Directory", sInitial);
			String sFileName = selectedFile.getPath();
			popup.tell("843c", "setTitle: selected Filename: " + sFileName);
			saveDataFile(sFileName, _darData, _salCarriageReturn);
		}
		return group;
	}

	private void saveDataFile(String _sFileName, Double[] _darData, ArrayList<String> _salCarriageReturn) {
		File fout = new File(_sFileName);
		PrintStream writer = null;
		Double dTemp = 0.0;
		try {
			writer = new PrintStream(fout);
		} catch (FileNotFoundException e) {
			popup.tell("844a", "Control file writer, invalid path.", e);
		}
		int iCeiling = myNest.getCeiling();
		int iFloor = myNest.getFloor();
		StringBuilder sb = new StringBuilder();
		Double dMean = myNest.getMean();
		int iMaxLine = _salCarriageReturn.size();
		int iPointer = 0;
		Integer iNext = 0;
		String sDelim = "\t";
		int iItem = 0;
		String[] sLeaders = null;
		String sHeader = null;
		Lehmer Signer = new Lehmer(iFloor, iCeiling);
		for (int iLine = 0; iLine < iMaxLine; iLine++) {
			if (iLine < iMaxLine) {
				sLeaders = _salCarriageReturn.get(iLine).split ("\\|");
				sHeader = sLeaders[0];
				iNext = Integer.parseInt(sLeaders[1]);
			} else
				iNext = _darData.length - 1;
			sb = new StringBuilder(sHeader);
			while ( iPointer <= iNext){
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
				iItem = Signer.adjust(iItem);		// signing step for synthetic data
				sb.append(sDelim + iItem);
				iPointer++;
			}
			writer.println(sb.toString());
		}
		writer.close();
		Integer iPercentage = (100 * (iMinCut + iMaxCut)) / iNext;
		String sFeedback = iNoCut.toString() + " were in range. " + iMinCut.toString()
				+ " had to be restrained at bottom. " + iMaxCut.toString() + " had to be restrained at top. "
				+ "A total of " + iPercentage + "% out of bounds.";
		flr.Warning(true, "Info", sFeedback, myStage);
	}

	private void repeatFocus(Node node) {
		Platform.runLater(() -> {
			if (!node.isFocused()) {
				node.requestFocus();
				repeatFocus(node);
			}
		});
	}

}
