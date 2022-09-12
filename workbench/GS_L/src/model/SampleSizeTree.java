package model;

import java.util.ArrayList;
import java.util.logging.Logger;
import java.util.prefs.Preferences;

import javafx.application.Platform;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.SplitPane;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import utilities.Factor;
import utilities.SampleSizeView;

/**
 * <h1>Class SampleSizeTree</h1>
 * The SampleSizeTree object contains the whole structure of the sample
 * sizes for both crossed and nested facets, as well management of indices,
 * incrementing indices (stepping), and calculation of of range for each
 * configuration ('Effect').
 *
 * @see <a href="https://github.com/G-String-Legacy/G_String/blob/main/workbench/GS_L/src/model/SampleSizeTree.java">model.SampleSizeTree</a>
 * @author ralph
 * @version %v..%
 */
public class SampleSizeTree {

	/**
	 * <code>Nest</code>  the various design parameters of the assessment.
	 */
	private Nest myNest = null;

	/**
	 * <code>sDictionary</code> the facet dictionary in the original order.
	 */
	private String sDictionary = null;

	/**
	 * <code>sHDictionary</code>  the facet dictionary in hierarchical order.
	 */
	private String sHDictionary;

	/**
	 * <code>sarNests</code>  contains a nest string for each facet in the original order.
	 */
	private String[] sarNests = null;

	/**
	 * <code>barCrossed</code>  a simple array of booleans in original facet order,
	 *  (true) - crossed; (false) - nested.
	 */
	private Boolean[] barCrossed = null;
	
	/**
	 * <code>iFacet</code>  number of facets.
	 */
	private Integer iLength = 0;

	/**
	 * <code>int</code> array of number of sample sizes per facet
	 */
	private int[] iLengths = null;

	/**
	 * 	int array of indices for each Facet for orderly stepping through the
	 *  whole range of sample sizes according to nesting.
	 */
	private int[] iIndices = null;

	/**
	 * location to keep track of previous Facet designation char
	 * when stepping through them.
	 */
	private char cPrevious = '-';
	
	/**
	 * Carrie current cumulative parent index
	 */
	private int[] iParentIndices = null;

	/**
	 * Flag if parameters are entered from script, rather than manually
	 */
	private Boolean bDoOver = false;

	/**
	 * Pointer to array of primary nested Facet configurations.
	 */
	//private int iConfiguration;

	/**
	 * array list of <code>SampleSizeView</code> for sample size input
	 */
	private ArrayList<SampleSizeView> ssvAL = null;

	/**
	 * sample sizes for each facet below a particular index of the nesting facet.
	 */
	private int[][] iarSizes;

	/**
	 * configuration specific sums of sample sizes below a particular index of the nesting facet.
	 */
	//private int[][][] iarSums;

	/**
	 * Analysis specific sums of sample sizes below a particular index of the nesting facet.
	 */
	private int[][] iarASums;

	/**
	 * index offsets below a particular index of the nesting facet.
	 */
	private int[][] iarOffsets;

	/**
	 * ragged matrix - rows by Effect, cols by Facet within Effect in hierarchical order
	 */
	private char[][] conKeySet = null;

	/**
	 * ragged matrix - rows by Effect, cols: Facet index in order corresponding to <code>conKeySet</code>
	 */
	private int[][] iarCurrentIndexSet = null;

	/**
	 * array containing the current state for each Effect
	 */
	private int[] iarCounts = null;

	/**
	 * array containing the total state count for each effect
	 */
	private int[] iarDepths = null;

	/**
	 * Configurations (Effects), within the context of GS_L are strings defining specific facet combinations
	 * that describe the permitted 'Effects' and cross terms in Brennan's terminology
	 */
	private ArrayList<String> salConfigurations;

	/**
	 * total number of configurations (Effects)
	 */
	private int iConfigurationCount = 0;

	/**
	 * pointer to application logger
	 */
	private Logger logger;

	/**
	 * array of <code>Facets</code> in basic order
	 */
	private Facet[] farFacets = null;

	/**
	 * number of Facets
	 */
	private Integer iFacetCount = 0;

	/**
	 * css style 20
	 */
	private String sStyle_20 = null;

	/**
	 * css style 16
	 */
	private String sStyle_18 = null;

	/**
	 * css style 16
	 */
	private String sStyle_16 = null;

	/**
	 * pointer to Preference API
	 */
	private Preferences prefs = null;

	/**
	 * char designation of starred Facet
	 */
	private char cAsterisk;

	/**
	 * array list of <code>Factor</code>
	 *
	 * @see <a href="https://github.com/G-String-Legacy/G_String/blob/main/workbench/GS_L/src/utilities/Factor.java">Factor</a>
	 */
	private ArrayList<String[]> salFactors = new ArrayList<>();

	/**
	 * ragged matrix <code>Factor</code> per <code>sConfig</code> (Effect)
	 */
	private String[][] sarFactors = null;
	
	/**
	 * Constructor for <code>SampleSizeTree</code>
	 *
	 * @param _nest  pointer to <code>Nest</code>
	 * @param _Dictionary  <code>sDictionary</code>
	 * @param _logger  pointer to application logger
	 * @param _prefs pointer to <code>Preferences</code>
	 */
	public SampleSizeTree(Nest _nest, String _Dictionary, Logger _logger, Preferences _prefs) {
		sDictionary = _Dictionary;
		Integer iFacets = sDictionary.length();
		iarSizes = new int[iFacets][];
		//iarTotals = new int[100][iFacets][];
		iarASums = new int[iFacets][];
		iarOffsets = new int[iFacets][];
		barCrossed = new Boolean[iFacets];
		iLengths = new int[iFacets];
		myNest = _nest;
		farFacets = myNest.getFacets();
		iIndices = new int[iFacets];
		iParentIndices = new int[iFacets];
		logger = _logger;
		prefs = _prefs;
		salConfigurations = new ArrayList<>();
		bDoOver = myNest.getDoOver();
		sStyle_16 = prefs.get("Style_16",
				"-fx-font-size: 16px; -fx-font-family: \"ARIAL\"; -fx-padding: 5; -fx-background-color: #805015; -fx-text-fill: #FFFFFF;");
		sStyle_18 = prefs.get("Style_18",
				"-fx-font-size: 18px; -fx-font-family: \"ARIAL\"; -fx-padding: 10; -fx-background-color: #805015; -fx-text-fill: #FFFFFF;");
		sStyle_20 = prefs.get("Style_20",
				"-fx-font-size: 30px; -fx-font-family: \"ARIALSerif\"; -fx-padding: 10; -fx-background-color: #805015; -fx-text-fill: #FFFFFF;");
	}

	/**
	 * setter for sHDictionary
	 * @param _sHDictionary  String of all Facet designation chars in hierarchic order.
	 */
	public void setHDictionary(String _sHDictionary) {
		sHDictionary = _sHDictionary;
	}

	/**
	 * This method runs when sample sizes are entered.
	 * sums and cumuls are calculated when a facet is added
	 * by facet number (original).
	 *
	 * @param _iFacet  basic index to facet
	 * @param sss  array of integer sample sizes for a specifies facet
	 */
	public void addSampleSize(Integer _iFacet, Integer[] sss) {

		int iSize = sss.length;
		int iCount = 0;
		int iLast = 0;
		iarSizes[_iFacet] = new int[iSize];
		iarASums[_iFacet] = new int[iSize];
		iarOffsets[_iFacet] = new int[iSize + 1];
		for (Integer iS : sss) {
			iarOffsets[_iFacet][iCount] = iLast;
			iarSizes[_iFacet][iCount] = iS;
			iarASums[_iFacet][iCount++] = iLast;
			iLast += iS;
		}
		iarOffsets[_iFacet][iCount] = iLast;
		iLengths[_iFacet] = iSize;
	}

	/**
	 * This method runs when sample sizes are entered.
	 * sums and cumuls are calculated when a facet is added
	 * by facet Designation char.
	 *
	 * @param _c  Facet char designation
	 * @param sss  string array of sample sizes as text
	 */
	public void addSampleSize(char _c, String[] sss) {
		if (_c == cPrevious)
			return;
		cPrevious = _c;
		int iSize = sss.length;
		int iFacet = sDictionary.indexOf(_c);
		int iCount = 0;
		int iLast = 0;
		int iS = 0;
		iarSizes[iFacet] = new int[iSize];
		iarASums[iFacet] = new int[iSize + 1];
		iarOffsets[iFacet] = new int[iSize + 1];
		for (String s : sss) {
			iS = Integer.parseInt(s);
			iarOffsets[iFacet][iCount] = iLast;
			iarSizes[iFacet][iCount] = iS;
			iarASums[iFacet][iCount++] = iLast;
			iLast += iS;
		}
		iarOffsets[iFacet][iCount] = iLast;
		iarASums[iFacet][iCount] = iLast;
		iLengths[iFacet]= iSize;
	}

	/**
	 * Setter for <code>Facet</code> 'bCrossed' status (vs bNested).
	 *
	 * @param _c  Facet designation char
	 * @param _bCrossed  flag that Facet is crossed (vs nested)
	 */
	public void setCrossed(char _c, Boolean _bCrossed) {
		char c = _c;
		Integer index = sDictionary.indexOf(c);
		barCrossed[index] = _bCrossed;
	}

	/**
	 * assign appropriate array of primary nests to <code>sarNests</code>
	 *
	 * @param _sarNested  external string array
	 */
	public void setNests(String[] _sarNested) {
		// sets Nests
		sarNests = _sarNested;
	}

	/**
	 * composes JavaFX code page (<code>vBox</code> to collect
	 * sample sizes for a nested Facet.
	 *
	 * @param _iSample  index to nested Facet (corresponds to order in script)
	 * @return vBox
	 */
	public VBox getPage(Integer _iSample) {
		VBox vOuter = new VBox();
		vOuter.setMinHeight(800);
		vOuter.setAlignment(Pos.TOP_CENTER);
		HBox vPara = new HBox();
		VBox hbLeft = null;
		VBox hbRight = null;
		FlowPane fp = null;
		SplitPane sp = null;
		vPara.setPrefHeight(20);
		String sNest = sarNests[_iSample];
		String sAugment = "";
		char cFacet = sNest.toCharArray()[0];
		String ssNest = null;
		StringBuilder sb = new StringBuilder("Set sample sizes for facet '" + cFacet + "'");
		switch (sNest.length()) {
		case 1:
			ssNest = "";
			break;
		case 3:
			sAugment = "               <- " + sNest.substring(2) + " ->";
			ssNest = "";
			sb.append(" in " + sNest.substring(2));
			break;
		}
		sb.append(".");
		Label lbTitle = new Label(sb.toString());
		lbTitle.setPrefWidth(800);
		lbTitle.setStyle(sStyle_20);
		lbTitle.setAlignment(Pos.TOP_CENTER);
		vPara.getChildren().add(lbTitle);
		vOuter.getChildren().add(vPara);
		char[] cFacets = sNest.replaceAll(":", "").toCharArray();
		iLength = cFacets.length;
		iIndices = new int[iLength];
		// add sub title
		sp = new SplitPane();
		sp.setDividerPositions(new double[] { .15 });
		hbLeft = new VBox();
		hbLeft.setPrefHeight(0.3);
		hbRight = new VBox();
		hbRight.setPrefHeight(0.3);
		hbLeft.setStyle(sStyle_16);
		hbRight.setStyle(sStyle_16);
		Label lblInfo = new Label(ssNest);
		lblInfo.setStyle(sStyle_16);
		hbLeft.getChildren().add(lblInfo);
		sp.getItems().add(hbLeft);
		Label lbSubtitle = new Label("Sample Sizes" + sAugment);
		lbSubtitle.setStyle(sStyle_16);
		hbRight.getChildren().add(lbSubtitle);
		sp.getItems().add(hbRight);
		vOuter.getChildren().add(sp);
		sp = null;
		for (Integer i = 0; i < iLength; i++) {
			iIndices[i] = 0;
		}
		getSamples(cFacet);
		boolean bFirstSample = true;
		String sLineLabel = "-";
		String sNewLineLabel = "";
		for (SampleSizeView ssv : ssvAL) {
			sNewLineLabel = ssv.getIndexString();
			if (!sNewLineLabel.equals(sLineLabel)) {
				sLineLabel = sNewLineLabel;
				if (sp != null) {
					hbRight.getChildren().add(fp);
					sp.getItems().add(hbRight);
					vOuter.getChildren().add(sp);
				}
				sp = new SplitPane();
				sp.setDividerPositions(new double[] { .15 });
				hbLeft = new VBox();
				hbRight = new VBox();
				Label lblIndices = new Label(sNewLineLabel);
				lblIndices.setStyle(sStyle_18);
				hbLeft.getChildren().add(lblIndices);
				sp.getItems().add(hbLeft);
				fp = new FlowPane();
			}
			fp.getChildren().add(ssv);
			if (bFirstSample)
				repeatFocus(ssv);
			bFirstSample = false;
		}
		hbRight.getChildren().add(fp);
		sp.getItems().add(hbRight);
		vOuter.getChildren().add(sp);
		ScrollPane scrollPane = new ScrollPane();
		scrollPane.setContent(vOuter);
		scrollPane.setFitToWidth(true);

		VBox vBox = new VBox();
		vBox.getChildren().add(scrollPane);
		return vBox;
	}

	/**
	 * intializes <code>Facet</code>
	 *
	 * @param _cFacet facet designation char
	 */
	public void getSamples(char _cFacet) {
		char cFacet = _cFacet;
		Integer iSize;
		Character cNestor = null;
		Integer iFacet = sDictionary.indexOf(cFacet);
		if (farFacets == null) {
			farFacets = myNest.getFacets();
			iFacetCount = farFacets.length;
		}

		Facet f = farFacets[iFacet];
		ssvAL = new ArrayList<>();
		if (!bDoOver) {
			if ((cNestor = f.getNestor()) == '$')
				iSize = 1;
			else {
				iSize = 0;
				for (int i : getSizes(cNestor))
					iSize += i;
			}
			iarSizes[iFacet] = new int[iSize];
			iarASums[iFacet] = new int[iSize + 1];
			iarASums[iFacet][0] = 0;
		}

		String sIndices = null;
		int iCounter = 0;
		Integer iValue;
		for (Integer i = 0; i < iarSizes[iFacet].length; i++) {
			sIndices = getIndex(iFacet, i);
			iValue = iarSizes[iFacet][i];
			ssvAL.add(new SampleSizeView(this, cFacet, iFacet, iCounter++, iValue, stringToArray(sIndices)));
		}
	}

	/**
	 * getter of 'Effect Size', i.e. the sample sizes for a Facet according
	 * of its 'Nestor', if any. 'Crossed' Facets only have one sample size.
	 *
	 * @param iRow  Facet position in original order.
	 * @return String containing one or more sample size values
	 */
	public String getEffect(Integer iRow) {
		// returns effect line for control file
		StringBuilder sb = new StringBuilder("EFFECT       ");
		String sNest = sarNests[iRow];
		if (sNest.toCharArray()[0] == cAsterisk)
			sb.append("* ");
		sb.append(padRight(sNest, 12));
		Integer iFacet = sDictionary.indexOf(sNest.substring(0, 1));
		for (Integer i : iarSizes[iFacet]) {
			if (i == null)
				sb.append(" ");
			else
				sb.append(i.toString() + " ");
		}
		return sb.toString();
	}

	/**
	 * getter of primary nested configuration of Facets (as in script).
	 *
	 * @param _c  Facet designation char
	 * @return nested configuration of Facet
	 */
	public String getNest(char _c) {
		return sarNests[sHDictionary.indexOf(_c)];
	}

	/**
	 *
	 * @param s  original text
	 * @param n number of spaces to pad
	 * @return  padded text
	 */
	public static String padRight(String s, int n) {
		return String.format("%1$-" + n + "s", s);
	}

	/**
	 * updater of sample size arrays accumulations
	 *
	 * @param _iLevel  Facet position in original order
	 * @param _iPointer  pointer to each of the individual sample sizes for this Facet
	 * @param _value  actual value of the sample size
	 */
	public void hasChanged(Integer _iLevel, Integer _iPointer, Integer _value) {
		Integer iLevel = _iLevel;
		iarSizes[iLevel][_iPointer] = _value;	// for both analysis and synthesis
		if(!myNest.getSimulate())				// for analysis only
			iarASums[iLevel][_iPointer + 1] = iarASums[iLevel][_iPointer] + _value;
	}

	/**
	 * setter of 'doOver' flag (use script)
	 *
	 * @param _bDoOver  boolean flag
	 */
	public void DoOver(Boolean _bDoOver) {
		bDoOver = _bDoOver;
	}

	/**
	 * getter of total score count.
	 *
	 * @return iRecordCount  total number of scores
	 */
	public Integer numRecords() {
		int iRecordCount = 1;
		Boolean[] bDone = new Boolean[iFacetCount];
		for (Integer i = 0; i < iFacetCount; i++) // initialize flags to false
			bDone[i] = false;
		Integer iFacet = -1;
		int iSum = 0;
		Integer[] iNestees = null;
		Integer iSize = 0;
		Integer iRoot = -1;
		Boolean bAsterisk = false;
		for (char c : sHDictionary.toCharArray()) {
			Facet f = myNest.getFacet(c);
			bAsterisk = f.starred();
			iFacet = f.getOrder();
			iRoot = iFacet;
			if (bDone[iRoot])
				continue; // this facet has already been processed
			bDone[iRoot] = true;
			iFacet = iRoot;
			if ((iNestees == null) || (iNestees.length == 0)) {
				iRecordCount *= iarASums[iFacet][iarSizes[iFacet].length];
				if (bAsterisk)
					break;
				continue;
			} else if (iNestees.length == 2) {
				Integer iN0 = iNestees[0];
				Integer iN1 = iNestees[1];
				iSize = iarASums[iFacet][iarSizes[iFacet].length];
				bDone[iN0] = true;
				bDone[iN1] = true;
				for (Integer i = 0; i < iSize; i++)
					iSum += iarSizes[iN0][i] * iarSizes[iN1][i];
				iRecordCount *= iSum;
				if (bAsterisk)
					break;
				continue;
			}		}
		return iRecordCount;
	}

	/**
	 * special kluge for JavaFX @see <a href="https://docs.oracle.com/javase/8/javafx/api/javafx/application/Platform.html">Application platform support class</a>
	 *
	 * @param node  item of JavaFX
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
	 * current Facet index in nested environment
	 *
	 * @param _iFacet integer facet pointer (original order)
	 * @param _iPointer  offset according to 'Nestor'
	 * @return Facet index
	 */
	public String getIndex(Integer _iFacet, Integer _iPointer) {
		char cN;
		int iBase = 0;
		int iCount = 0;
		int iNestor = 0;
		Facet f = farFacets[_iFacet];
		cN  = f.getNestor();
		if (cN == '$')
			return "0";			// facet f is crossed
		else {
			// facet f is nested
			iNestor = sDictionary.indexOf(cN);
			for (Integer i = iarASums[iNestor].length - 1; i >= 0; i--) {
				if ((iBase = iarASums[iNestor][i]) <= _iPointer) {
					iCount = i;
				}
			}
		}
		String sReturn = getIndex(iNestor, iCount) + ", " + String.valueOf(_iPointer - iBase);
		return (sReturn);
	}


	/**
	 * utility to convert csv text to Integer array
	 *
	 * @param _sInput  text string
	 * @return Integer array
	 */
	private Integer[] stringToArray(String _sInput) {
		String[] tokens = _sInput.split(",");
		Integer L = tokens.length;
		Integer[] iResult = new Integer[L];
		String sTemp = null;
		if (_sInput.length() < 1)
			return new Integer[0];
		for (Integer i = 0; i < L; i++) {
			sTemp = tokens[i].toString().trim();
			try {
				iResult[i] = Integer.parseUnsignedInt(sTemp);
			} catch (Exception e) {
				logger.warning(e.getMessage());
			}
		}
		return iResult;
	}

	/**
	 * cumulative sample size per Facet.
	 *
	 * @param _iPointer  Facet as Integer in original order
	 * @return total sample size for Facet
	 */
	public Integer size(Integer _iPointer) {
		return iarASums[_iPointer][iarSizes[_iPointer].length];
	}

	/**
	 * returns sample size array by Facet designation char
	 *
	 * @param c  Facet designation char
	 * @return sample size array
	 */
	public int[] getSizes(char c) {
		if (c == '$') {					// unassigned Facets
			int[] iRet = new int[1];
			iRet[0] = 0;
			return iRet;
		}
		else {							// regular Facets
			Integer i = sDictionary.indexOf(c);
			return iarSizes[i];
		}
	}

	/**
	 * returns sample size array according to the position of the Facet in the original order
	 *
	 * @param iOrder  position of Facet in original order
	 * @return sample size array
	 */
	public int[] getOSizes(Integer iOrder) {
		return iarSizes[iOrder];
	}

	/**
	 * returns sample size array according to the position of the Facet in the hierarchic order
	 *
	 * @param iHOrd  Facet position in hierarchic order
	 * @return sample size array
	 */
	public int[] getHSizes(Integer iHOrd) {
		if (iHOrd < iFacetCount)
			iHOrd = sDictionary.indexOf(sHDictionary.toCharArray()[iHOrd]);
		return iarSizes[iHOrd];
	}

	/**
	 * getter of sample size sums per Facet by Facet designation char for Analysis.
	 *
	 * @param c  Facet designation char
	 * @return cumulative sample size per Facet
	 */
	public int[] getSums(char c) {
		Integer i = sDictionary.indexOf(c);
		return iarASums[i];
	}

	/**
	 * getter of sample size sums per Facet by position in original order for Analysis.
	 *
	 * @param _iOrd  Facet position in original order
	 * @return cumulative sample size per Facet
	 */
	public int[] getOSums(Integer _iOrd) {
		return iarASums[_iOrd];
	}

	/**
	 * getter of sample size sums per Facet by position in hierarchic order for Analysis.
	 *
	 * @param _iHOrd Facet position in hierarchic order
	 * @return cumulative sample size per Facet
	 */
	public int[] getHSums(Integer _iHOrd) {
		if (_iHOrd < iFacetCount)
			_iHOrd = sDictionary.indexOf(sHDictionary.toCharArray()[_iHOrd]);
		return iarASums[_iHOrd];
	}

	/**
	 * Arithmetic mean of sample size per Facet in nested context,
	 * according to Brennan for calculation of G coefficients.
	 *
	 * @param _iFacet  position of Facet in original order
	 * @return  arithmetic mean of Sample Size per Facet
	 */
	public Double getLevel(Integer _iFacet) {
		Integer iSize = iarSizes[_iFacet].length;
		double dNum = (double)iarASums[_iFacet][iSize];
		double dDenom = (double)iSize.doubleValue();
		return dNum/dDenom;
	}

	/**
	 * Harmonic mean of sample sizes per Facet in nested context,
	 * according to Brennan for calculation of G coefficients.
	 *
	 * @param _iFacet   position of Facet in original order
	 * @return harmonic mean of Sample Size per Facet
	 */
	public Double getHarmonic(Integer _iFacet) {
		double dDenom = 0.0;
		int iCount = 0;
		for (Integer iSize : iarSizes[_iFacet]) {
			if (!iSize.equals(0)) {
				iCount++;
				dDenom += 1.0 / iSize;
			}
		}
		return (iCount / dDenom);
	}

	/**
	 * utility for setting up Sample Size Page
	 */
	public void collectSampleSizes() {
		if (farFacets == null) {
			farFacets = myNest.getFacets();
			iFacetCount = farFacets.length;
		}
	}

	/**
	 * sets the char for the starred Facet
	 *
	 * @param _cAsterisk char of Facet carrying Asterisk
	 */
	public void setAsterisk (char _cAsterisk){
		cAsterisk = _cAsterisk;
	}

	/**
	 * incrementer for simulation for each call the Facet indices get incremented
	 * according to the nesting order, such that the count of the last Effect
	 * goes up by one count. The 'Effect' corresponding to the residual variance
	 * component gets split into its factors. The indices are then incremented
	 * separately for each factor in the appropriate order by the recursive
	 * method 'bClimb'.
	 *
	 * @return <code>false</code> when all Facet indices have reached their maximum, <code>true</code> otherwise
	 */
	public Boolean increment() {
		
		int L = sarFactors.length -1;
		Boolean bReturn = true;
		String[] sarFacComp = sarFactors[L];
		int K = sarFacComp.length;
		for (int i = K - 1; i >= 0; i--) {
			String sFac = sarFacComp[i];
			bReturn = bClimb(sFac.toCharArray()[0]);
			if (bReturn)
				break;
		}

		//now update counts
		int[] indexSet = null;
		for ( int j = 0; j < iConfigurationCount; j++) {
			indexSet = getIndexSet(j);
			if (!compare(indexSet, iarCurrentIndexSet[j])){
				if (indexSet[0] < iarCurrentIndexSet[j][0]) {
					iarCounts[j] = 0;
				} else if (iarCounts[j] < iarDepths[j] - 1)
					iarCounts[j]++;
				iarCurrentIndexSet[j] = indexSet.clone();
			}
		}
		return bReturn;
	}
	
	/**
	 * recursive routine climbs facet hierarchy to orderly increment indices.
	 * 
	 * @param _cFacet  facet for incrementing index
	 * @return  true if successful, false if not.
	 */
	private Boolean bClimb (char _cFacet) {
		Boolean bReturn = true;
		char cF = _cFacet;
		char cN = getFacet(cF).getNestor();
		int iNIndex = 0;
		int iF = sDictionary.indexOf(cF);	
		if (cN != '$')
			iNIndex = getHIndex(cN);
		/*else
			return false;*/
		int iFIndex = getHIndex(cF);
		int iLimit = 0;
		try {
			if (iNIndex <= iarSizes[iF].length)
				iLimit = iarSizes[iF][iNIndex];
			else  
				return true;
			
			if (iFIndex++ < iLimit -1) {
				setHIndex(cF, iFIndex);					// the facet index is incremented
				bReturn = true;
			} else {
				setHIndex(cF, 0);
				if (cN == '$') 						// the indices of the factor reached maximum
					bReturn = false;
				else {
					bReturn = bClimb(cN);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			System.exit(0);
		}
		return bReturn;
	}

	/**
	 * getter of Facet indices by Facet designation char
	 * (the order in which the Facet indices step through the score data file)
	 *
	 * @param c  Facet designation char
	 * @return Facet index for that Facet
	 */
	public int getHIndex(char c) {
		int i = sHDictionary.indexOf(c);
		return iIndices[i];
	}

	/**
	 * setter of Facet indices by Facet designation char
	 * (the order in which the Facet indices step through the score data file)
	 *
	 * @param c  Facet designation char
	 * @param iIndex  Facet index for that Facet
	 */
	public void setHIndex(char c, int iIndex) {
		int i = sHDictionary.indexOf(c);
		iIndices[i] = iIndex;
	}

	/**
	 * reset all Facet indices and iarCurrentIndexSet.
	 */
	public void resetIndices() {
		int L;
		for (int i = 0; i < iFacetCount; i++) {
			iIndices[i] = 0;
			iParentIndices[i] = 0;
		}
		for (int i = 0; i < iConfigurationCount; i++) {
			iarCounts[i]= 0;
			L = conKeySet[i].length;
			int[] iIndSet = new int[L];
			for (int j = 0; j < L; j++)
				iIndSet[j] = 0;
			iarCurrentIndexSet[i] = iIndSet;
		}
	}

	/**
	 * getter of all Facet indices
	 *
	 * @return int array of Facet indices
	 */
	public int[] getIndices() {
		return iIndices;
	}

	/**
	 * setter of FacetCount and indices array establishment.
	 *
	 * @param _iFacetCount  number of facets
	 */
	public void setFacetCount(int _iFacetCount) {
		iFacetCount = _iFacetCount;
		iIndices = new int[iFacetCount];
	}

	/**
	 * getter of Effects, both primary (per Facet) and cross-interactions.
	 *
	 * @param _iConfig  index of Effect
	 * @return Effect string  (ordered, nested Facet chars)
	 */
	public String getConfiguration(int _iConfig) {
		return salConfigurations.get(_iConfig);
	}

	/**
	 * This method is the major simulation work horse.
	 * It receives a configuration string from CompConstrct,
	 * stores it in 'salConfigurations' and then calculates
	 * first the parameters. For this purpose we go from
	 * bottom to top. The configuration string is actually
	 * already in this order.
	 *
	 * @param _sConfiguration  Effect string  (ordered, nested Facet chars)
	 */
	public void addConfiguration(String _sConfiguration) {
		String sConfiguration = _sConfiguration;
		salConfigurations.add(sConfiguration);
	}

	/**
	 * utility to format string array for text output.
	 *
	 * @param sar generic String array
	 * @return formatted text String
	 */
	public String sDump(String[] sar) {
		StringBuilder sb = new StringBuilder();
		if(sar != null) {
			boolean bFirst = true;
			for (String s : sar)
				if(bFirst) {
					sb.append(s);
					bFirst = false;
				} else
					sb.append(", " + s);
		} else
			sb.append("null");
		return sb.toString();
	}

	/**
	 * getter of Effect count (primary and cross-interactions).
	 *
	 * @return  int count
	 */
	public int getConfigurationCount() {
		if (iConfigurationCount == 0)
			iConfigurationCount = salConfigurations.size();
		return iConfigurationCount;
	}

	/**
	 * initializes the variables used for stepping (incrementing) through Facet indices.
	 */
	public void initCounter() {
		String sConfiguration;
		char[] cConfiguration = null;
		iarCounts = new int[iConfigurationCount];
		iarDepths = new int[iConfigurationCount];
		conKeySet = new char[iConfigurationCount][];
		iarCurrentIndexSet = new int[iConfigurationCount][];
		for (int i = 0; i < iConfigurationCount; i++) {
			sConfiguration = salConfigurations.get(i);
			cConfiguration = new StringBuilder(sConfiguration.replace(":",  "")).reverse().toString().toCharArray();
			conKeySet[i] = cConfiguration;
			iarDepths[i] = getSize(i);
		}
	}

	/**
	 * breaks down Effects into <code>Factor</code>s.
	 *
	 * @param _iConf  index pointer to Effect
	 * @return int number of Factors comprising Effect
	 */
	public int factorConfigurations(int _iConf) {
		int iConf = _iConf;
		int iCount = 0;
		/*
		 * First, break down configuration into crossed components.
		 */
		String sConfiguration = salConfigurations.get(iConf);
		String sReverse = new StringBuilder(sConfiguration).reverse().toString();
		char[] cHead = sReverse.split(":")[0].toCharArray();
		String [] sCrosseds = new String[cHead.length];
		String s;
		for (char c : cHead)
			if ((s = sTree(iConf, String.valueOf(c))) != null)
				sCrosseds[iCount++] = new StringBuilder(s).reverse().toString();
		salFactors.add(sCrosseds);
		return iCount;
	}

	/**
	 * boolean test indicates whether String sContainer contains char cContent.
	 *
	 * @param cContent Facet designation char
	 * @param sContainer  typically an 'Effect'
	 * @return true if contained, false otherwise
	 */
	private Boolean isContained(char cContent, String sContainer) {
		boolean bContains = false;
		bContains = (sContainer.indexOf(cContent) >= 0);
		return bContains;
	}

	/**
	 * getter of Facet by designation char.
	 *
	 * @param _c Facet designation char
	 * @return  Facet corresponding to  _c
	 */
	public Facet getFacet(char _c) {
		int i = sDictionary.indexOf(_c);
		Facet f = farFacets[i];
		return f;
	}

	/**
	 * utility used for factoring Effects, checks relationships (nesting)
	 * of Facets and identifies the totality of a nested family
	 *
	 * @param _iConf  int pointer to Effect
	 * @param sSibs probes of Facet
	 * @return String containing a nested family.
	 */
	private String sTree (int _iConf, String sSibs) {
		if (sSibs.length() == 0)
			return null;
		String sKids;
		StringBuilder sbCurrent = new StringBuilder();
		StringBuilder sbDescendants = new StringBuilder();
		String sConfiguration = salConfigurations.get(_iConf);
		char[] cSibs = sSibs.toCharArray();
		for (char c : cSibs) {
			if (isContained(c, sConfiguration)) {
				sbCurrent.append(c);
				sKids = getFacet(c).getsNestees();
				if (sKids != null) {
					char[] cKids = sKids.toCharArray();
					for (char d : cKids)
						if (isContained(d, sConfiguration))
							sbDescendants.append(d);
				}
			}
		}
		String sReturn = null;
		String s = null;
		if (sbCurrent.length() > 0)
			sReturn = sbCurrent.toString();
		s = sbDescendants.toString().trim();
		if ((s = sTree(_iConf, s)) != null)
			sReturn += ":" + s;
		return sReturn;
	}

	/**
	 * utility converts Factor string array lists to string arrays
	 */
	public void consolidateSplits() {
		sarFactors = salFactors.toArray(new String[0][]);
	}

	/**
	 * getter of position of a Factor in Factor string array list.
	 *
	 * @param sFactor  String representation of <code>Factor</code>
	 * @return int position
	 */
	public int getFactorConf(String sFactor) {
		return salConfigurations.indexOf(sFactor);
	}

	/**
	 * getter of current state in 'Effect'.
	 *
	 * @param iConf  pointer to 'Effect'
	 * @return int  current state of 'Effect', less than <code>iarDepth</code>
	 */
	public int getCount (int iConf) {
		return iarCounts[iConf];
	}

	/**
	 * getter of maximal state count of 'Effect' as product of <code>Factor</code>s.
	 *
	 * @param iConf  int pointer to 'Effect'
	 * @return int  maximal number of states for an 'Effect', when  Facet indices go through all combinations.
	 */
	public int getSize (int iConf) {
		int iSize = 1;
		Factor factor = null;
		String[] sFactors = sarFactors[iConf];
		for (String s : sFactors) {
			factor = new Factor(this, s);
			iSize = iSize * factor.getSize();
		}
		return iSize;
	}

	/**
	 * getter of index offset in nested Facets resulting from the 'Nestor's' index.
	 *
	 * @param cFacet Facet designation char
	 * @param iPointer  index of 'Nestor'
	 * @return sum of sample sizes less than iPointer
	 */
	public int getOffset(char cFacet, int iPointer) {
		int iF = sDictionary.indexOf(cFacet);
		int iOffset = iarOffsets[iF][iPointer];
		return iOffset;
	}

	/**
	 * getter of Facet dictionary in original order
	 *
	 * @return sDictionary  String
	 *
	 */
	public String getDictionary() {
		return sDictionary;
	}

	/**
	 * total number of states per Facet (Analysis)
	 *
	 * @param _c  Facet designation char
	 * @return  total number of states
	 */
	public int getDim(char _c) {
		int iF = sDictionary.indexOf(_c);
		int L = iarOffsets[iF].length;
		return iarOffsets[iF][L - 1];
	}

	/**
	 * compares 2 int arrays for identity.
	 *
	 * @param _iArray1  generic int array
	 * @param _iArray2  generic int array
	 * @return true  if identical, false otherwise
	 */
	private Boolean compare(int[] _iArray1, int[] _iArray2) {
		int L = _iArray1.length;
		if (L != _iArray2.length)
			return false;
		for (int i = 0; i < L; i++)
			if (_iArray1[i] != _iArray2[i])
				return false;
		return true;
	}

	/**
	 * getter for array of hierarchic Facet order by 'Effect' pointer
	 *
	 * @param iConf  int pointer to 'Effect'
	 * @return int array of hierarchic Facet order
	 */
	private int[] getIndexSet( int iConf) {
		char[] carConKey = conKeySet[iConf];
		int[] iReturn = new int[carConKey.length];
		int i = 0;
		for (char c : carConKey)
			iReturn[i++] = getHIndex(c);
		return iReturn;
	}

	/**
	 * getter of <code>iarDepth</code> by "Effect' pointer.
	 *
	 * @param _iC  pointer to 'Effect' (Configuration)
	 * @return int  maximal number of states per 'Effect'
	 */
	public int getDepth(int _iC) {
		return iarDepths[_iC];
	}
	
	/**
	 * Accumulates sample size sums during simulation
	 */
	public void ProcessSizes() {
		int[][][] iarSums = new int[iConfigurationCount][iFacetCount][];
		for (int i = 0; i < iFacetCount; i++)
			for (int j = 0; j < iConfigurationCount; j++)
				iarSums[j][i] = new int[iarSizes[i].length];
		for (int j = 0; j < iConfigurationCount; j++) {
			String sConfig = salConfigurations.get(j);
			char[] carConf = new StringBuilder(sConfig).reverse().toString().toCharArray();
			Boolean bTail = true;
			for (char cF : carConf) {
				if (cF==':') {
					bTail = false;		// switch to no longer in tail of configuration
					continue;
				} else if (bTail) {  	// facet in tail
					
				} else {				// facet not in tail
					
				}
			}
		}
	}
	
	/**
	 * completes sizeArray offsets from size array
	 */
	public void completeOffsets() {
		for (int i = 0; i < iFacetCount; i++) {
			int iSampleCount = iarSizes[i].length;
			int iOffset = 0;
			iarOffsets[i] = new int[iSampleCount + 1];
			for (int j = 0; j < iSampleCount; j++) {
				iOffset += iarSizes[i][j];
				iarOffsets[i][j + 1] = iOffset;
			}				
		}
	}
}
