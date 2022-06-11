package utilities;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintStream;
import java.text.DecimalFormat;
import java.util.Locale;
import java.util.Scanner;
import java.util.prefs.Preferences;
import model.Facet;
import model.Nest;
import model.SampleSizeTree;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.DialogPane;
import javafx.scene.control.Label;
import javafx.scene.control.Spinner;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

public class Filer {

	/*
	 * Process reading and writing of control and data files the 'read' command
	 * takes the following arguments: - file: name of the file to be read -
	 * sModus: a control argument with the following values: 'Control': reads
	 * and processes existing control file for 'doOver'. 'Data': reads, strips
	 * and stores stripped data file (with previously established format
	 * parameters. 'Scan': reads, scans and displays data to establish format
	 * parameters and sample sizes (auto-index)
	 */

	private Nest myNest = null;
	private String fileName = null;
	private String sDataFileName = null;
	private Integer iFieldWidth = 0;
	private Integer iHilight = 0;
	private String sHTML = null;
	private Integer iMaxColumns = 0;
	private Preferences prefs = null;
	private WebEngine webEngine = null;
	private String[] sHeaders = null;
	private Filer self;
	private Integer iMaxColCount = null; // tentative limit for number of
											// data-in columns
	private Double[] dSums = null; // for calculating grand means
	private Integer[] iCounts = null; // for calculating grand means
	private Integer iOutputPointer = 0;
	private Double dGrandMeans = 0.0;
	private Integer iHeader = 0;
	private Integer iMissedItems = 0;
	private String[][] sRawData = null;
	private Double dMin = 100.0;
	private Double dMax = -100.0;
	private Popup popup;
	private Stage myStage = null;

	public Filer(Nest _nest, Preferences _prefs, Popup _popup, Stage _stage) {
		iMaxColCount = 100;
		myNest = _nest;
		prefs = _prefs;
		myStage = _stage;
		//iFieldWidth = myNest.getFieldWidth();
		iFieldWidth = 8;
		self = this;
		sDataFileName = myNest.getDataFileName();
		dSums = new Double[iMaxColCount];
		iCounts = new Integer[iMaxColCount];
		for (Integer i = 0; i < iMaxColCount; i++) {
			iCounts[i] = 0;
			dSums[i] = 0.0;
		}
		popup = _popup;
		popup.setClass("Filer");
	}

	public void readFile(File file) {
		// reads already existing control file
		String sLine = null;
		try {
			fileName = file.getCanonicalPath().toString();
			myNest.setFileName(fileName);
		} catch (IOException e) {
			popup.tell("readFile_a", e);
		}

		try (Scanner scanner = new Scanner(file)) {
			while (scanner.hasNextLine()) {
				sLine = scanner.nextLine();
				processControlLine(sLine); // process control file
			}
		} catch (IOException e) {
			popup.tell("readFile_b", e);
		}
	}

	private void processControlLine(String line) {
		String[] words;
		String key;
		Integer length;
		words = line.split("\\s+", 100);
		key = words[0];
		char cFacet;
		length = words.length;
		String value = join(words, length);
		String sMark = prefs.get("Facet mark", "%");
		sMark = sMark.trim().substring(0, 1);
		switch (key) {
		case "GSTUDY":
			myNest.setTitle(value);
			break;
		case "COMMENT&":
		case "COMMENT*":
		case "COMMENT%":
			Facet nullFacet = new Facet(myNest);
			words = value.split("\\s+");
			nullFacet.setName(words[0]);
			cFacet = words[1].toCharArray()[1];
			nullFacet.setDesignation(cFacet);
			nullFacet.setNested(false);
			myNest.addFacet(nullFacet);
			break;
		case "COMMENT":
			myNest.addComment(value);
			break;
		case "OPTIONS":
			prefs.put("OPTIONS", value);
			break;
		case "EFFECT":
			myNest.addEffect(value);
			break;
		case "FORMAT":
			myNest.addFormat(value);
			break;
		case "PROCESS":
			myNest.addProcess(value);
			break;
		case "ANCHORS":
			myNest.addAnchors(value);
			break;
		case "VARIANCES":
			myNest.addVariances(value);
			break;
		default:
			break;
		}
	}

	private String join(String[] words, Integer length) {
		StringBuilder sb = new StringBuilder();
		for (Integer i = 1; i < length; i++)
			sb.append(" " + words[i]);
		String result = (sb.toString()).toString();
		return result.trim();
	}

	private String HTML_join(String[] _sColumns) {
		StringBuilder sbLine = new StringBuilder("<tr>");
		Integer iCount = 0;
		for (String sTemp : _sColumns) {
			if (iCount++ >= iHilight)
				sbLine.append("<td align=\"right\"><strong>" + sTemp + "</strong></td>");
			else
				sbLine.append("<td align=\"right\"><font color = \"#CCCCCC\">" + sTemp + "</td>");
		}
		sbLine.append("</tr>\n");
		return sbLine.toString();
	}

	public Group showTableNew() {
		/**
		 * This method displays the experimental scores, as read in from the data
		 * file. It allows to exclude index columns from being fed to urGenova.
		 * In contrast to all the other display scenes ('groups'), this one 
		 * is not pure javafx, but it displays the data as html within a webview.
		 * It is more efficient than having a factory generate a javafx object for each score.
		 */
		StringBuilder sb = new StringBuilder("<html><body contentEditable=\"true\"><table border = \"1\">\n");
		Integer iCounter = 0;
		for (int i = 0; i < iMaxColumns; i++)
			sb.append("<col width=\"50\">\n");
		if (sHeaders != null) {
			sb.append("<tr>");
			for (String s : sHeaders)
				if ((s != null) && !s.equals(""))
					sb.append("<th>" + s + "</th>");
			sb.append("</tr>");
		}
		for (String[] sRow : sRawData) {
			if (iCounter++ > 50)
				break;
			try {
				sb.append(HTML_join(sRow));
			} catch (Exception e) {
				popup.tell("showTableNew_a", e);
			}
		}
		sb.append("</table></body></html>");
		sHTML = sb.toString(); // content of browser
		// System.out.print(sHTML);
		Group tableGroup = new Group(); // overall container
		VBox mainLayout = new VBox(0.0); // overall layout
		mainLayout.setAlignment(Pos.TOP_CENTER);
		HBox titleBox = new HBox(); // container for title
		titleBox.setPrefWidth(800);
		titleBox.setStyle(prefs.get("Format_20", null));
		titleBox.setAlignment(Pos.CENTER);
		HBox parameterBox = new HBox(); // container for spinner etc
		parameterBox.setPrefHeight(70.0);
		parameterBox.setAlignment(Pos.CENTER);
		HBox browserBox = new HBox(); // container for browser
		browserBox.setStyle(
				"-fx-padding: 10; -fx-border-style: solid inside; -fx-border-width: 2; -fx-border-insets: 5; -fx-border-radius: 5; -fx-border-color: brown;");
		browserBox.setAlignment(Pos.TOP_CENTER);
		Label lbTitle = new Label("Select skip and field format");
		lbTitle.setStyle(
				"-fx-font-size: 20px; -fx-font-family: \"ARIAL\"; -fx-padding: 10; -fx-background-color: #801515; -fx-text-fill: #FFFFFF;");
		lbTitle.setPrefWidth(800.0);
		lbTitle.setAlignment(Pos.CENTER);
		titleBox.getChildren().add(lbTitle);
		mainLayout.getChildren().add(titleBox);
		Label lbSkip = new Label("Skip: ");
		lbSkip.setPadding(new Insets(0, 0, 0, 10));
		Label lbWidth = new Label("     Width: ");
		Spinner<Integer> intSpinner = new Spinner<Integer>(0, iMaxColumns - 1, 0, 1);
		intSpinner.getStyleClass().add(Spinner.STYLE_CLASS_SPLIT_ARROWS_HORIZONTAL);
		intSpinner.setPrefWidth(80.0);
		intSpinner.getValueFactory().setValue(iHilight);
		intSpinner.getEditor().textProperty().addListener((obs1, oldValue1, newValue1) -> {
			if (!oldValue1.equals(newValue1)) {
				iHilight = Integer.parseInt(newValue1);
				myNest.show(self.showTableNew());
			}
		});
		TextField format = new TextField();
		format.setPrefWidth(55);
		format.setText(iFieldWidth.toString());
		format.textProperty().addListener((obs2, oldValue2, newValue2) -> {
			if (newValue2 != oldValue2)
				iFieldWidth = Integer.parseInt(newValue2);
		});
		parameterBox.getChildren().addAll(lbSkip, intSpinner, lbWidth, format);
		mainLayout.getChildren().add(parameterBox);
		// setup of browser

		WebView browser = new WebView();
		browser.setMaxWidth(iMaxColumns * 70);
		browser.setMaxHeight(400.0);
		webEngine = browser.getEngine();
		webEngine.loadContent(sHTML);
		browserBox.getChildren().add(browser);
		mainLayout.getChildren().add(browserBox);
		tableGroup.getChildren().add(mainLayout);
		return tableGroup;
	}

	public void readDataFileNew(File _inFile) {
		/*
		 * Reads and parses datafile, stores possible header in 'headers',
		 * stores data in 'sRawData' structure', i.e. an array of arrays
		 * String[][] in several passes: Pass 1: counts valid lines and counts
		 * lines with Double content Pass 2: creates outer array with
		 * appropriate number of lines, then chops lines, creates inner arrays
		 * and stores Double numbers Also, initialize summing for means and
		 * counting for N's
		 */
		Integer iLineCount = 0;
		Integer iFalseLineCount = 0;
		Integer iNumberFields;
		Integer iLineIndex = 0;
		String[] sChoppedLine = null;
		String sLine = null;
		// check if file exists
		if (_inFile == null) {
			popup.tell("1109a", "Data file missing in readDataFile.");
			System.exit(0);
		}
		// Pass 1:
		try (Scanner scanner = new Scanner(_inFile)) {
			while (scanner.hasNextLine()) {
				sLine = scanner.nextLine();
				if (sLine != null && sLine.replaceAll("\\s+", "") != "" && !sLine.matches("^[a-zA-Z]*$"))
					iLineCount++;
				else
					iFalseLineCount++;
			}
		} catch (IOException eio) {
			popup.tell("1109b", eio);
			System.exit(0);
		}
		// Pass 2:
		sRawData = new String[iLineCount][];
		try (Scanner scanner = new Scanner(_inFile)) {
			while (scanner.hasNextLine()) {
				sLine = scanner.nextLine();
				if (sLine != null && sLine.replaceAll("\\s+", "") != "" && !sLine.matches("^[a-zA-Z]*$")) {
					if (sLine.indexOf(',') >= 0) {
						// split on comma
						sChoppedLine = sLine.split(",");
					} else if (sLine.indexOf("\t") >= 0) {
						// split on tab
						sChoppedLine = sLine.split("\t");
					} else {
						// split on whitespace	// not recommended if there are missing data
						sChoppedLine = sLine.split("\\s+");
					}
					iNumberFields = sChoppedLine.length;
					sRawData[iLineIndex++] = sChoppedLine;

					if (iNumberFields > iMaxColumns)
						iMaxColumns = iNumberFields;
				}
			}
		} catch (IOException eio) {
			popup.tell("1109b", eio);
			System.exit(0);
		}
	}

	public void writeDataFileNew() {
		/*
		 * writes stripped datafile to a special file (~data.txt) in the special
		 * Brennan directory (home of urGenova) for running urGenova on.
		 */

		// First calculate grand means
		StringBuilder sb = null;
		Double sum = 0.0;
		Double dItem = 0.0;
		Double dValue = 0.0;
		int DataCount = 0;
		int iHeadCount = 0;

		for (String[] sRow1 : sRawData) {
			for (int j = iHilight; j < sRow1.length; j++) {
				if (!sRow1[j].trim().isEmpty()) {
					dItem = Double.parseDouble(sRow1[j]);
					sum += dItem;
					if (dItem < dMin)
						dMin = dItem;
					else if (dItem > dMax)
						dMax = dItem;
					DataCount++;
				} else {
					sRow1[j] = "x";
					//iBlankCount++;
				}
			}
		}
		dGrandMeans = sum / DataCount;
		myNest.setGrandMeans(dGrandMeans);

		String sDataPath = prefs.get("Working Directory", null) + File.separator + sDataFileName;
		File fData = new File(sDataPath);
		fData.setReadable(true, false);
		PrintStream ps = null;
		try {
			ps = new PrintStream(fData);
		} catch (IOException e) {
			popup.tell("writeDataFileNew_a", e);
		}
	    new DecimalFormat("####.##");
	    DecimalFormat df = (DecimalFormat) DecimalFormat.getInstance(Locale.US);
		for (String[] dRow : sRawData) {
			sb = new StringBuilder("");
			iHeadCount = iHilight;
			for (String s : dRow) {
				if (iHeadCount-- > 0)
					sb.append(padLeft(s.toString(), iFieldWidth));
				else if (!s.equals("x")) {
					dValue = Double.parseDouble(s) - dGrandMeans;
					sb.append(padLeft(df.format(dValue), iFieldWidth));
				} else
					sb.append(padLeft(df.format(0.0), iFieldWidth));
			}
			ps.println(sb.toString());
		}
		ps.close();
		testSignature();
	}

	public String testSignature() {
		Double dItem = 0.0;

		Lehmer Signer = new Lehmer(dMin, dMax);
		for (String[] sRow1 : sRawData) {
			for (int j = iHilight; j < sRow1.length; j++) {
				if (!sRow1[j].equals("x")) {
					dItem = Double.parseDouble(sRow1[j]);
					Signer.Test(dItem);
				}
			}
		}
		return "\nSig: " + Signer.Summarize();
	}

	public static String padLeft(String s, int n) {
		return String.format("%1$" + n + "s", s);
	}

	public void getUrGenova() {
		/*
		 * Reads in the results of urGenova and stores them in n
		 * 'VarianceComponent array (in Nest)
		 */
		String sLine = null;
		String sUrOutput = prefs.get("Working Directory", null) + "/~control.txt.lis";
		File file = new File(sUrOutput);
		iOutputPointer = 0;
		try (Scanner scanner = new Scanner(file)) {
			while (scanner.hasNextLine()) {
				sLine = scanner.nextLine();
				processResultlLine(sLine); // process control file
			}
		} catch (IOException e) {
			popup.tell("getUrGenova_a", e);
		}
	}

	private void processResultlLine(String _line) {
		switch (iOutputPointer) {
		case 0:
			if (_line.indexOf("--------------------------------") >= 0)
				iOutputPointer = 1;
			break;
		case 1:
			if (_line.indexOf("--------------------------------") >= 0)
				iOutputPointer = 2;
			break;
		case 2:
			if (_line.indexOf("--------------------------------") >= 0) {
				iOutputPointer = 3;
				break;
			}
			// Now we can read the variance components
			myNest.setVariance(_line);
			break;
		default:
			break;
		}
	}

	public Integer getHighlight() {
		return iHilight;
	}

	public Integer getHeader() {
		return iHeader;
	}

	public Integer missingItems() {
		return iMissedItems;
	}

	public File getFile(Boolean bRead, String sTitle) {
		String sInitial = prefs.get("Home Directory", System.getProperty("user.home"));
		File fInitial = new File(sInitial);
		File f = null;
		String sHome = null;
		FileChooser fc = new FileChooser();
		fc.setInitialDirectory(fInitial);
		fc.setTitle(sTitle);
		if (bRead)
			f = fc.showOpenDialog(myStage);
		else
			f = fc.showSaveDialog(myStage);
		sHome = f.getParent();

		if (!sHome.equals(sInitial))
			prefs.put("Home Directory", sHome);
		return f;
	}

	public void saveParametersDialog(String sType, String sQuestion) {
		// sType either 'Analysis' or 'Synthesis'
		Alert alert = new Alert(AlertType.CONFIRMATION);
		Double dX = myStage.getX() + 200.0;
		Double dY = myStage.getY() + 75.0;
		File fOutputFile = null;
		alert.setX(dX);
		alert.setY(dY);
		alert.initOwner(myStage);
		alert.setTitle("Decide!");
		alert.setHeaderText(null);
		alert.setContentText(sQuestion);
		DialogPane dialogPane = alert.getDialogPane();
		dialogPane.getStylesheets().add("resources/myDialog.css");
		dialogPane.getStyleClass().add("myDialog");
		ButtonBar buttonBar = (ButtonBar) dialogPane.lookup(".button-bar");
		buttonBar.getButtons().forEach(b -> b.setStyle(
				"-fx-font-size: 16;-fx-background-color: #551200;-fx-text-fill: #ffffff;-fx-font-weight: bold;"));
		if (alert.showAndWait().get() != ButtonType.OK)
			return;
		switch (sType) {
		case "Analysis":
			fOutputFile = getFile(false, "Save Analysis Control File to:");
			writeAnalysisControlFile(fOutputFile);
			break;
		case "Synthesis":
			fOutputFile = getFile(false, "Save Synthesis Control File to:");
			writeSynthesisControlFile(fOutputFile);
			break;
		}
	}

	public void writeAnalysisControlFile(File file) {
		// writes new control file
		// StringBuilder sbComments = new StringBuilder(); // informal comments
		// StringBuilder sbFComments = new StringBuilder(); // facet comments
		String sTemp = null;
		SampleSizeTree myTree = myNest.getTree();
		PrintStream writer = null;
		try {
			writer = new PrintStream(file);
		} catch (FileNotFoundException e) {
			popup.tell("writeAnalysisControlFile_a", e);
		}
		// Title
		writer.println("GSTUDY    " + myNest.getTitle());
		// Comments
		for (String s : myNest.getComments()) {
			writer.println("COMMENT   " + s);
		}
		// Comments*
		for (Facet f : myNest.getFacets()) {
			writer.println("COMMENT*  " + f.getName() + "    (" + f.getDesignationString() + ")");
		}

		char[] cFacet = myNest.getDictionary().toCharArray();
		Integer iLevels = cFacet.length;
		// Options
		writer.println("OPTIONS   " + prefs.get("OPTIONS", "NREC 5 \"*.lis\" TIME NOBANNER"));
		// Effects
		for (Integer i = 0; i < iLevels; i++) {
			sTemp = myTree.getEffect(i);
			writer.println(sTemp);
		}
		writer.println("FORMAT       " + iFieldWidth * iHilight + "  " + iFieldWidth);
		// Process
		writer.println("PROCESS      \"" + sDataFileName + "\"");
		writer.close();
	}

	public void writeSynthesisControlFile(File fOutput) {
		// writes new control file
		String sTemp = null;
		Integer iNumberVarianceCoeffients = 0;
		SampleSizeTree myTree = myNest.getTree();
		PrintStream writer = null;
		try {
			writer = new PrintStream(fOutput);
		} catch (FileNotFoundException e) {
			popup.tell("writeSynthesisControlFile_a", e);
		}

		// Comments
		for (String s : myNest.getComments()) {
			writer.println("COMMENT   " + s);
		}

		// Facets
		for (Facet f : myNest.getFacets()) {
			writer.println("COMMENT* " + f.getName() + "    (" + f.getDesignationString() + ")");
		}
		char[] cFacet = myNest.getDictionary().toCharArray();
		Integer iLevels = cFacet.length;
		// Effects
		for (Integer i = 0; i < iLevels; i++) {
			sTemp = myTree.getEffect(i);
			writer.println(sTemp);
		}

		// Anchors
		writer.println("ANCHORS    " + myNest.getFloor() + "    " + myNest.getMean() + "     " + myNest.getCeiling());

		// Variances
		iNumberVarianceCoeffients = myNest.getVcDim();
		StringBuilder sb = new StringBuilder("VARIANCES");
		for (int i = 0; i < iNumberVarianceCoeffients; i++)
			sb.append("    " + myNest.getVarianceCoefficient(i));
		writer.println(sb.toString());
		writer.close();
	};
}
