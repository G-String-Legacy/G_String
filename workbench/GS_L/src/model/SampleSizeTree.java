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
	
	Boolean bFirst = true;
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
	private String sHDictionary = null;

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
	//private int[] iParentIndices = null;

	/**
	 * Flag if parameters are entered from script, rather than manually
	 */
	private Boolean bDoOver = false;

	/**
	 * array list of <code>SampleSizeView</code> for sample size input
	 */
	private ArrayList<SampleSizeView> ssvAL = null;

	/**
	 * sample sizes for each facet below a particular index of the nesting facet.
	 */
	private int[][] iarSizes = null;

	/**
	 * Analysis specific sums of sample sizes below a particular index of the nesting facet.
	 */
	private int[][] iarASums = null;

	/**
	 * index offsets below a particular index of the nesting facet.
	 */
	private int[][] iarOffsets = null;

	/**
	 * ragged matrix - rows by Effect, cols by Facet within Effect in hierarchical order
	 */
	//private char[][] conKeySet = null;

	/**
	 * ragged matrix - rows by Effect, cols: Facet index in order corresponding to <code>conKeySet</code>
	 */
	//private int[][] iarCurrentIndexSet = null;

	/**
	 * array containing the current state for each Effect
	 */
	private int[] iarCounts = null;

	/**
	 * array containing the total state count for each effect
	 */
	private int[] iarDepths = null;

	/**
	 * structured 2D array encapsulating the overall project design
	 */
	private String[][] sarProducts = null;
	
	/**
	 * Configurations (Effects), within the context of GS_L are strings defining specific facet combinations
	 * that describe the permitted 'Effects' and cross terms in Brennan's terminology
	 */
	private String[] sarConfigurations;

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
	 * @param _logger  pointer to application logger
	 * @param _prefs pointer to <code>Preferences</code>
	 */
	public SampleSizeTree(Nest _nest, Logger _logger, Preferences _prefs) {
		myNest = _nest;
		logger = _logger;
		prefs = _prefs;
		bDoOver = myNest.getDoOver();
		sStyle_16 = prefs.get("Style_16",
				"-fx-font-size: 16px; -fx-font-family: \"ARIAL\"; -fx-padding: 5; -fx-background-color: #805015; -fx-text-fill: #FFFFFF;");
		sStyle_18 = prefs.get("Style_18",
				"-fx-font-size: 18px; -fx-font-family: \"ARIAL\"; -fx-padding: 10; -fx-background-color: #805015; -fx-text-fill: #FFFFFF;");
		sStyle_20 = prefs.get("Style_20",
				"-fx-font-size: 30px; -fx-font-family: \"ARIALSerif\"; -fx-padding: 10; -fx-background-color: #805015; -fx-text-fill: #FFFFFF;");
	}
	/**
	 * setter for sDictionary
	 * @param _sDictionary  String of all Facet designation chars in original order.
	 */
	public void setDictionary(String _sDictionary) {
		sDictionary = _sDictionary;
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
	public void addSampleSize(int _iFacet, int[] sss) {

		int iSize = sss.length;
		int iCount = 0;
		int iLast = 0;
		iarSizes[_iFacet] = new int[iSize];
		iarASums[_iFacet] = new int[iSize + 1];
		iarOffsets[_iFacet] = new int[iSize + 1];
		for (Integer iS : sss) {
			iarOffsets[_iFacet][iCount] = iLast;
			iarSizes[_iFacet][iCount] = iS;
			iarASums[_iFacet][iCount++] = iLast;
			iLast += iS;
		}
		iarASums[_iFacet][iCount] = iLast;
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
		int iCount = 0;
		int iLast = 0;
		int iS = 0;
		if (iarSizes == null) {
			iFacetCount = myNest.getFacetCount();
			sDictionary = myNest.getDictionary();
			iarSizes = new int[iFacetCount][];
			iarASums = new int[iFacetCount][];
			iarOffsets = new int[iFacetCount][];
			iLengths = new int[iFacetCount];
			iarDepths = new int[iFacetCount]; 
		}
		int iFacet = sDictionary.indexOf(_c);
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
	 * When an arbitrary sample size changes during manual data entry, this method update the whole dependent
	 * arrays based only on the iarSizes array.
	 * desi
	 * @param _iF  original order of currently updating facet
	 */
	private void updateSampleSizes(int _iF) {
		int iFacet = _iF;
		int iLast = 0;
		int iCount = 0;
		int L = iarSizes[iFacet].length;
		iarOffsets[iFacet] = new int[L + 1];
		iarASums[iFacet] = new int[L + 1];
		for (int iS : iarSizes[iFacet]) {
			iarOffsets[iFacet][iCount] = iLast;
			iarASums[iFacet][iCount++] = iLast;
			iLast += iS;
		}
		iarOffsets[iFacet][iCount] = iLast;
		iarASums[iFacet][iCount] = iLast;
		iLengths[iFacet]= L;

		
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
		String sNest = myNest.getNestedName(_iSample);
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
			if (iarSizes == null) {
				iarSizes = new int[iFacetCount][];
				iarASums = new int[iFacetCount][];
				iarOffsets = new int[iFacetCount][];
				iLengths = new int[iFacetCount];
			}
			if (iarSizes[iFacet] == null) {
				iarSizes[iFacet] = new int[iSize];
				iarASums[iFacet] = new int[iSize + 1];
				iarASums[iFacet][0] = 0;
			}
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
		char cFacet = sNest.toCharArray()[0];
		Integer iFacet = sDictionary.indexOf(cFacet);
		for (Integer i : iarSizes[iFacet]) {
			sb.append("\t" + i.toString());
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
		updateSampleSizes(iLevel);
/*		if(!myNest.getSimulate() || !bDoOver)				// for analysis only
			iarASums[iLevel][_iPointer + 1] = iarASums[iLevel][_iPointer] + _value;*/
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
	
	private int getCurrentIndex(char cF) {
		return iIndices[sHDictionary.indexOf(cF)];
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
	 * Total number of states for Facet cF
	 * 
	 * @param _cF  designation char for facet
	 * @return  total number of possible states for facet
	 */
	public int getMaxSum(char _cF) {
		int i = sDictionary.indexOf(_cF);
		int L = iarASums[i].length - 1;
		return iarASums[i][L];
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
		try {
			if (farFacets == null) {
				farFacets = myNest.getFacets();
				iFacetCount = farFacets.length;
			}
			cAsterisk = myNest.getAsterisk();
		} catch(Exception e) {
			myLogger(logger, e);
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
	 * Incrementer for simulation for each call the Facet indices get incremented
	 * according to the nesting order, such that the count of the last Effect
	 * goes up by one count. The 'Effect' corresponding to the residual variance
	 * component gets split into its factors. The indices are then incremented
	 * separately for each factor in the appropriate order by the recursive
	 * method 'bClimb'.
	 *
	 * @return <code>false</code> when all Facet indices have reached their maximum, <code>true</code> otherwise
	 */
	public Boolean increment() {
		int iP = iFacetCount - 1;
		Boolean bReturn = bClimb (iP);
		updateCounts();
		return bReturn;
	}
	
	/**
	 * recursive routine climbs facet hierarchy to orderly increment indices.
	 * 
	 * @param _iPH  facet ID for currently incrementing index
	 * @return  true if successful, false if not.
	 */
	private Boolean bClimb (int _iPH) {
		int iPH = _iPH;
		char cF = sHDictionary.toCharArray()[iPH];
		int iPO = sDictionary.indexOf(cF);
		int iComplexity = iarSizes[iPO].length;
		int iPointer = 0;
		int iCeiling = 0;
		try {
			if (iComplexity > 1)
				iPointer = iIndices[iPH - 1];
			iCeiling = iarSizes[iPO][iPointer] - 1;
			if (iIndices[iPH] < iCeiling) {
				iIndices[iPH]++;
				return true;
			} else if (iPH == 0)
				return false;
			else {
				iIndices[iPH] = 0;
				return bClimb(iPH - 1);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
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
		//iParentIndices = new int[iFacetCount];
		for (int i = 0; i < iFacetCount; i++) {
			iIndices[i] = 0;
		}
/*		for (int i = 0; i < iConfigurationCount; i++) {
			iarCounts[i]= 0;
			L = conKeySet[i].length;
			int[] iIndSet = new int[L];
			for (int j = 0; j < L; j++)
				iIndSet[j] = 0;
			iarCurrentIndexSet[i] = iIndSet;
		}*/
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
		return sarConfigurations[_iConfig];
	}
	
	/**
	 * 1D String array with syntax od design
	 * 
	 * @param _sarConfigs  design syntax
	 */
	public void setConfigurations(String[] _sarConfigs) {
		sarConfigurations = _sarConfigs;
	}
	
	/**
	 * Setter for 2D String array containing project design
	 * 
	 * @param _sarProducts  2D String array containing project design
	 */
	public void setProducts(String[][] _sarProducts) {
		sarProducts = _sarProducts;
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
			iConfigurationCount = sarConfigurations.length;
		
		return iConfigurationCount;
	}

	/**
	 * initializes the variables used for stepping (incrementing) through Facet indices.
	 */
	public void initCounter() {
		iarCounts = new int[iConfigurationCount];
		//conKeySet = new char[iConfigurationCount][];
		//iarCurrentIndexSet = new int[iConfigurationCount][];
	}

	/**
	 * breaks down Effects into <code>Factor</code>s.
	 *
	 * @param _iConf  index pointer to Effect
	 * @return int number of Factors comprising Effect
	 */
	/*public int factorConfigurations(int _iConf) {
		int iConf = _iConf;
		int iCount = 0;
		/*
		 * First, break down configuration into crossed components.
		 */
		/*String sConfiguration = salConfigurations.get(iConf);
		String sReverse = new StringBuilder(sConfiguration).reverse().toString();
		char[] cHead = sReverse.split(":")[0].toCharArray();
		String [] sCrosseds = new String[cHead.length];
		String s;
		for (char c : cHead)
			if ((s = sTree(iConf, String.valueOf(c))) != null)
				sCrosseds[iCount++] = new StringBuilder(s).reverse().toString();
				//sCrosseds[iCount++] = new StringBuilder(s).toString();
		salFactors.add(sCrosseds);
		return iCount;
	}*/

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
	 * utility converts Factor string array lists to string arrays
	 */
	public void consolidateSplits() {
		sarFactors = salFactors.toArray(new String[0][]);
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
	 * @param _cFacet Facet designation char
	 * @param _iPointer  index of 'Nestor'
	 * @return sum of sample sizes less than iPointer
	 */
	public int getOffset(char _cFacet, int _iPointer) {
		int iOffset = 0;
		int iF = 0;
		try {
			iF = sDictionary.indexOf(_cFacet);
			iOffset = iarOffsets[iF][_iPointer];
		} catch (Exception e) {
			e.printStackTrace();
		}
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
	/*private Boolean compare(int[] _iArray1, int[] _iArray2) {
		int L = _iArray1.length;
		if (L != _iArray2.length)
			return false;
		for (int i = 0; i < L; i++)
			if (_iArray1[i] != _iArray2[i])
				return false;
		return true;
	}*/

	/**
	 * getter of <code>iarDepth</code> by "Effect' pointer.
	 *
	 * @param _iC  pointer to 'Effect' (Configuration)
	 * @return int  maximal number of states per 'Effect'
	 */
	public int getDepth(int _iC) {
		return iarDepths[_iC];
	}
	
	public int getDepth(char cF) {
		int i = sHDictionary.indexOf(cF);
		return iarDepths[i];
	}
	
	/**
	 * calculates total number of possible states for each Facet
	 */
	public void setDepths() {
		try {
			if (iarDepths == null)
				iarDepths = new int[iFacetCount];
			for (int i = 0; i < iFacetCount; i++) {		
				if (iarASums[i] != null) {
					int L = iarASums[i].length - 1;
					iarDepths[i] = iarASums[i][L];
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Setter of facet number of states
	 * 
	 * @param _iarDepths  facet number of states
	 */
	public void setDepths(int[] _iarDepths) {
		iarDepths = _iarDepths;
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
	
	/**
	 * Setter for facet array.
	 * 
	 * @param _farFacets  facet array
	 */
	public void setFacets(Facet[] _farFacets) {
		farFacets = _farFacets;
	}
	
	/**
	 * Establishes the structural arrays.
	 * 
	 * @param _sDictionary  string of facets
	 */
	public void setArrays(String _sDictionary) {
		sDictionary = _sDictionary;
		int L = sDictionary.length();
		if (iarSizes.length == 0) {
			iarSizes = new int[L][];
			iarASums = new int[L][];
			iarCounts = new int[L];
			//iarCurrentIndexSet = new int[L][];
			iarDepths = new int[L];
			iarOffsets = new int[L][];
			//iParentIndices = new int[L];
			iLengths = new int[L];
		}
		iLengths = new int[L];
	}
	
	/**
	 * For each indices-incrementation-step, this method calculates
	 * the current count for every permitted facet configuration
	 */
	private void updateCounts() {
		int iFactor = 1;
		int iSum = 0;
		int iCounter = 0;
		try {
			char[] cElement = null;
	 	 	if (iarCounts == null) {
				int iConfigs = sarProducts.length;
				iarCounts = new int[iConfigs];
			}
			for (String[] sarStageProduct : sarProducts) { 
				iSum = 0;
				iFactor = 1;
				for (String sFactor : sarStageProduct) {
					int iStep = 0;
					cElement = sFactor.toCharArray();
					for (char cF :cElement) {
						if ("(:),".indexOf(cF) >= 0)
							continue;
						else {
							iStep = getOffset(cF, iStep) + getCurrentIndex(cF);
						}
						iFactor = getDepth(cF);
					}
					iSum = iSum * iFactor + iStep;
				}
				iarCounts[iCounter++] = iSum;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Calculates the number of fixed scores presenting for each replication
	 * 
	 * @param _cReplicate  designation of replicating facet
	 * @return  number of fixed scores per replication
	 */
	public int iGetRepFactor (char _cReplicate) {
		int iFactor = 1;
		int L = sHDictionary.length();
		int iRepP = sHDictionary.indexOf(_cReplicate) + 1;
		char c = '-';
		while (iRepP < L) {
			c = sarNests[iRepP++].toCharArray()[0];
			iFactor *= getDepth(c);
		}
		return iFactor;
	}
	
	/**
	 * utility to convert int array to text string
	 *
	 * @param array arbitrary int array
	 * @return formatted text
	 */
	public String dumpArray (int[] array){
		int l = array.length;
		StringBuilder sb = new StringBuilder(array[0]);
		for (int i = 0; i < l; i++)
			sb.append("\t" + array[i]);
		return sb.toString();
	}
	
	/**
	 * Logging utility
	 * 
	 * @param _logger  pointer to logging API
	 * @param _e  Exception
	 */	
	private void myLogger(Logger _logger, Exception _e) {
		if (myNest.getStackTraceMode())
			_e.printStackTrace();
		else {
			logger.warning(_e.getMessage());
		}
	}
}
