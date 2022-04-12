package model;

import java.util.ArrayList;
import java.util.prefs.Preferences;

import utilities.Factor;
import utilities.Filer;
import utilities.Popup;
import utilities.SampleSizeView;
import javafx.application.Platform;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.SplitPane;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

public class SampleSizeTree {

	/*
	 * The SampleSizeTree object contains the whole structure of the sample
	 * sizes for both crossed and nested facets, as well management of indices, 
	 * incrementing indices (stepping), and calculation of of range for each 
	 * configuration.
	 */

	private Nest myNest = null;
	private String sDictionary = null;
	// contains the facet dictionary in the original order
	private String sHDictionary;
	// contains the facet dictionary in hierarchical order
	private String[] sarNests = null;
	// contains a nest string for each facet in the original order
	private Boolean[] barCrossed = null;
	// contains a simple array of booleans in original facet order (crossed =
	// true)
	// contains index of starred facet in hierarchical order
	private Integer iLength = 0;
	private int[] iLengths = null;
	private Integer[] iPointers = null;
	private Integer[] iRows = null;
	// array arranging ial's in hierarchical order
	private int[] iIndices = null;
	private Facet[] facets = null;
	private char cPrevious = '-';
	private Boolean bChanged = false;
	private Boolean bDoOver = false;
	private int iConfiguration;			// pointer to the permitted configurations
										// section related to updating sample sizes 
										//on processing the ssv page
	private ArrayList<SampleSizeView> ssvAL = null;
	
	private int[][] iarSizes;			/** 
										*	sample sizes for each facet below a particular
										*	index of the nesting facet.
										**/
	private int[][][] iarSums;			/**
	 									*	configuration specific sums of sample sizes
	 									*	below a particular index of the nesting facet.
	 									**/
	private int[][] iarASums;			/**
	 									*	Analysis specific sums of sample sizes
	 									*	below a particular index of the nesting facet.
	 									**/
	private int[][] iarOffsets;			/**
										*	index offsets below a particular index of 
										*	the nesting facet. 
										**/
	
	/**
	 * These 5 variable arrays serve the efficient calculation of configuration specific progression counts.
	 */
	private char[][] conKeySet = null;			// array of facet content of all configurations
	private int[][] iarCurrentIndexSet = null;	// array of current index sets for all configurations
	private int[] iarCounts = null;				// array of counts for each configuration
	private int[] iarDepths = null;				// array of total number of states for each configurations
	
	/**
	 * Configurations, within the context of GS_L are strings defining specific facet combinations
	 * that describe the permitted 'effects' and cross terms in Brennan's terminology
	 */
	private ArrayList<String> salConfigurations;	// array list of permitted configurations
	private int iConfigurationCount = 0;
	
	private utilities.Popup popup;

	// variables for index calculation
	private Facet[] farFacets = null;
	private Integer iFacetCount = 0;
	private String sStyle_20 = null;
	private String sStyle_18 = null;
	private String sStyle_16 = null;
	private Preferences prefs = null;
	private char cAsterisk;
	private ArrayList<String[]> salFactors = new ArrayList<String[]>();
	private String[][] sarFactors = null;
	
	// Constructor
	public SampleSizeTree(Nest _nest, String _Dictionary, Popup _popup, Preferences _prefs) {
		// Constructor initializes SampleSizeTree according to original
		// dictionary
		sDictionary = _Dictionary;
		Integer iFacets = sDictionary.length();
		iarSizes = new int[iFacets][];
		iarSums = new int[100][iFacets][];
		//iarTotals = new int[100][iFacets][];
		iarASums = new int[iFacets][];
		iarOffsets = new int[iFacets][];
		barCrossed = new Boolean[iFacets];
		iLengths = new int[iFacets];
		facets = new Facet[iFacets];
		myNest = _nest;
		farFacets = myNest.getFacets();
		iIndices = new int[iFacets];
		popup = _popup;
		popup.setClass("SampleSizeTree");
		prefs = _prefs;
		salConfigurations = new ArrayList<String>();
		bDoOver = myNest.getDoOver();
		sStyle_16 = prefs.get("Style_16",
				"-fx-font-size: 16px; -fx-font-family: \"ARIAL\"; -fx-padding: 5; -fx-background-color: #805015; -fx-text-fill: #FFFFFF;");
		sStyle_18 = prefs.get("Style_18",
				"-fx-font-size: 18px; -fx-font-family: \"ARIAL\"; -fx-padding: 10; -fx-background-color: #805015; -fx-text-fill: #FFFFFF;");
		sStyle_20 = prefs.get("Style_20",
				"-fx-font-size: 30px; -fx-font-family: \"ARIALSerif\"; -fx-padding: 10; -fx-background-color: #805015; -fx-text-fill: #FFFFFF;");
	}

	// Setters (public)
	public void setHDictionary(String _HDictionary) {
		sHDictionary = _HDictionary;
	}

	public void addSampleSize(Integer _iFacet, Integer[] sss) {
		/**
		 * This method runs when sample sizes are entered. 
		 * sums and cumuls are calculated when each
		 * configuration is added.
		 */
		
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

	public void addSampleSize(char _c, String[] sss) {
		/**
		 * Alternative method to add sample sizes by facet character
		 * sums are calculated when each configuration is added.
		 */
		
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

	public void setFacet(Facet _facet) {
		char c = _facet.getDesignation();
		Integer index = sDictionary.indexOf(c);
		if (index >= 0) {
			facets[index] = _facet;
			barCrossed[index] = !_facet.getNested();
		}
	}

	public void setCrossed(char _c, Boolean _crossed) {
		// sets balCrossed element for facet _c
		char c = _c;
		Integer index = sDictionary.indexOf(c);
		barCrossed[index] = _crossed;
	}

	public void setNests(String[] nested) {
		// sets Nests
		sarNests = nested;
	}

	public void setChanged(Boolean _bChanged) {
		bChanged = _bChanged;
	}

	// Getters (public)

	public VBox getPage(Integer _iSample) {
		/*
		 * composes sample size page for a facet within a nest. In contrast to
		 * other pages for the GUI. this page also uses html formating, 
		 * rather than exclusive javafx.
		 */

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
		iPointers = new Integer[iLength];
		iRows = new Integer[iLength];
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
			iRows[i] = sHDictionary.indexOf(cFacets[i]);
			iIndices[i] = 0;
			iPointers[i] = 0;
		}
		getSamples(cFacet);
		Boolean bFirstSample = true;
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
		ssvAL = new ArrayList<SampleSizeView>();
		if (!bDoOver) {
			if ((cNestor = f.getNestor()) == '$')
				iSize = 1;
			else {
				iSize = 0;
				for (int i : getSizes(cNestor))
					iSize += i;
			}			
			iarSizes[iFacet] = new int[iSize];
			iarSums[iConfiguration][iFacet] = new int[iSize + 1];
			iarSums[iConfiguration][iFacet][0] = 0;
			iarASums[iFacet] = new int[iSize + 1];
			iarASums[iFacet][0] = 0;
		}

		String sIndices = null;
		Integer iCounter = 0;
		Integer iValue;
		for (Integer i = 0; i < iarSizes[iFacet].length; i++) {
			sIndices = getIndex(iFacet, i);
			iValue = iarSizes[iFacet][i];
			ssvAL.add(new SampleSizeView(this, cFacet, iFacet, iCounter++, iValue, stringToArray(sIndices)));
		}
	}

	public Boolean getChanged() {
		return bChanged;
	}

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

	public String getNest(char _c) {
		return sarNests[sHDictionary.indexOf(_c)];
	}

	public static String padRight(String s, int n) {
		return String.format("%1$-" + n + "s", s);
	}

	public void hasChanged(Integer _iLevel, Integer _iPointer, Integer _value) {
		Integer iLevel = _iLevel;
		iarSizes[iLevel][_iPointer] = _value;
		if(!myNest.getSimulate())
			iarASums[iLevel][_iPointer + 1] = iarASums[iLevel][_iPointer] + _value;
	}

	public void DoOver(Boolean _bDoOver) {
		bDoOver = _bDoOver;
	}

	public Integer numRecords() {
		Integer iRecordCount = 1;
		Boolean[] bDone = new Boolean[iFacetCount];
		for (Integer i = 0; i < iFacetCount; i++) // initialize flags to false
			bDone[i] = false;
		Integer iFacet = -1;
		Integer iSum = 0;
		Integer[] iNestees = null;
		Integer iSize = 0;
		Integer iRoot = -1;
		Integer iLoopCount = 0;
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
				else
					iLoopCount++;
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
				else
					iLoopCount++;
				continue;
			}		}
		return iRecordCount;
	}

	private void repeatFocus(Node node) {
		Platform.runLater(() -> {
			if (!node.isFocused()) {
				node.requestFocus();
				repeatFocus(node);
			}
		});
	}

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
				popup.tell("stringToArray_a", e);
			}
		}
		return iResult;
	}

	public Integer size(Integer _iPointer) {
		return iarASums[_iPointer][iarSizes[_iPointer].length];
	}

	public int[] getSizes(char c) {
		if (c == '$') {
			int[] iRet = new int[1];
			iRet[0] = 0;
			return iRet;
		}
		else {
			Integer i = sDictionary.indexOf(c);
			return iarSizes[i];
		}
	}

	public int[] getOSizes(Integer iComp) {
		return iarSizes[iComp];
	}

	public int[] getHSizes(Integer iComp) {
		if (iComp < iFacetCount)
			iComp = sDictionary.indexOf(sHDictionary.toCharArray()[iComp]);
		return iarSizes[iComp];
	}

	public int[] getSums(char c) {		
		Integer i = sDictionary.indexOf(c);
		return iarASums[i];
	}

	public int[] getOSums(Integer iComp) {
		return iarASums[iComp];
	}

	public int[] getHSums(Integer iComp) {
		if (iComp < iFacetCount)
			iComp = sDictionary.indexOf(sHDictionary.toCharArray()[iComp]);
		return iarASums[iComp];
	}

	public Double getLevel(Integer _iFacet) {
		Integer iSize = iarSizes[_iFacet].length;
		Double dNum = (double)iarASums[_iFacet][iSize];
		Double dDenom = (double)iSize.doubleValue();
		return dNum/dDenom;
	}

	public Integer getNestedSize(Integer _iFacet, Integer _iLevel) {
		/*
		 * here the _iFacet refers to the size of the target facet the _iLevel
		 * to the specific value of the Nestor's (actual) index, that means the
		 * nested index, if Nestor itself is nested!
		 */

		return iarSizes[_iFacet][_iLevel];
	}

	public Double getHarmonic(Integer _iFacet) {
		Double dDenom = 0.0;
		Integer iCount = 0;
		for (Integer iSize : iarSizes[_iFacet]) {
			if (!iSize.equals(0)) {
				iCount++;
				dDenom += 1.0 / iSize;
			}
		}
		return (iCount / dDenom);
	}

	public void collectSampleSizes(Filer flr) {
		// set iTranslate
		if (farFacets == null) {
			farFacets = myNest.getFacets();
			iFacetCount = farFacets.length;
		}
	}

	public Integer getEffectCount() {
		return sarNests.length;
	}

	public void setAsterisk (char _cAsterisk){
		cAsterisk = _cAsterisk;
	}
	
	public Boolean increment() {
		char[] cHFacets = sHDictionary.toCharArray();
		int iIndex = 0;
		int iLimit = 0;
		char c0 = '-';
		Character c1 = '-';
		int iPointer = 0;
		int iIncrementer = 0;
		Boolean bReturn = true;
		
		iIncrementer = iConfigurationCount - 1;
		for (int i = sHDictionary.length() - 1; i >= 0; i--) {
			c0 = cHFacets[i];
			c1 = myNest.getFacet(c0).getNestor();
			iIndex = getHIndex(c0);
			if (c1 == '$')
				iPointer = 0;
			else
				iPointer = getHIndex(c1);
			iLimit = iarSizes[sDictionary.indexOf(c0)][iPointer];
			iIndex++;
			if (iIndex >= iLimit)
				setHIndex(c0, 0);
			else {
				setHIndex(c0, iIndex);
				break;
			}
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
		bReturn = (getCount(iIncrementer) >= iLimit);
		return bReturn;
	}
	
	public int getHIndex(char c) {
		int i = sHDictionary.indexOf(c);
		return iIndices[i];
	}
	
	public void setHIndex(char c, int iIndex) {
		int i = sHDictionary.indexOf(c);
		iIndices[i] = iIndex;
	}
	
	public void resetIndices() {
		int L;
		for (int i = 0; i < iFacetCount; i++)
			iIndices[i] = 0;
		for (int i = 0; i < iConfigurationCount; i++) {
			iarCounts[i]= 0;
			L = conKeySet[i].length;
			int[] iIndSet = new int[L];
			for (int j = 0; j < L; j++)
				iIndSet[j] = 0;
			iarCurrentIndexSet[i] = iIndSet;
		}
	}

	public void setIndex(char c, Integer index) {
		int i = sDictionary.indexOf(c);
		iIndices[i] = index;
	}

	public Integer getIndex(char c) {
		if (c == '$')
			return 0;
		else {
			int i = sDictionary.indexOf(c);
			return iIndices[i];
		}
	}

	public int[] getIndices() {
		return iIndices;
	}
	
	public void setFacetCount(int _iFacetCount) {
		iFacetCount = _iFacetCount;
		iIndices = new int[iFacetCount];
	}

	public String getConfiguration(int _iConfig) {
		return salConfigurations.get(_iConfig);
	}
	
	public void addConfiguration(String _sConfiguration) {
		/**
		 * This method is the major simulation work horse.
		 * It receives a configuration string from CompConstrct,
		 * stores it in 'salConfigurations' and then calculates
		 * first the parameters. For this purpose we go from
		 * bottom to top. The configuration string is actually
		 * already in this order.
		 */
		
		String sConfiguration = _sConfiguration;
		salConfigurations.add(sConfiguration);
		iConfiguration = salConfigurations.size();
	}
	
	public String sDump(String[] sar) {
		StringBuilder sb = new StringBuilder();
		if(sar != null) {
			Boolean bFirst = true;
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
	
	public int getConfigurationCount() {
		if (iConfigurationCount == 0)
			iConfigurationCount = salConfigurations.size();
		return iConfigurationCount;
	}
	
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
	
	public int factorConfigurations(int _iConf) {
		/**
		 * Returns the current count for Configuration 'iConf', given indices.
		 */
		int iConf = _iConf;
		int iCount = 0;
		/**
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
		
	private Boolean isContained(char cContent, String sContainer) {
		Boolean bContains = false;
		bContains = (sContainer.indexOf(cContent) >= 0);
		return bContains;
	}
	
	public Facet getFacet(char _c) {
		int i = sDictionary.indexOf(_c);
		Facet f = farFacets[i]; 
		return f; 
	}
	
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
	public void consolidateSplits() {
		int L = salFactors.size();
		sarFactors = new String[L][];
		for (int i = 0; i < L; i++)
			sarFactors[i] = salFactors.get(i);
	}
	
	public int getFactorConf(String sFactor) {
		return salConfigurations.indexOf(sFactor);
	}
	
	public int getCount (int iConf) {
		return iarCounts[iConf];
	}
	
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
	
	public int getOffset(char cFacet, int iPointer) {
		int iF = sDictionary.indexOf(cFacet);
		int iOffset = iarOffsets[iF][iPointer];
		return iOffset;
	}
	
	public String getDictionary() {
		return sDictionary;
	}
	
	public int getDim(char c) {
		int iF = sDictionary.indexOf(c);
		int L = iarOffsets[iF].length;
		return iarOffsets[iF][L - 1];
	}
	
	private Boolean compare(int[] array1, int[] array2) {
		int L = array1.length;
		if (L != array2.length)
			return false;
		for (int i = 0; i < L; i++)
			if (array1[i] != array2[i])
				return false;
		return true;
	}
	
	private int[] getIndexSet( int iConf) {
		char[] carConKey = conKeySet[iConf];
		int[] iReturn = new int[carConKey.length];
		int i = 0;
		for (char c : carConKey)
			iReturn[i++] = getHIndex(c);
		return iReturn;
	}
	
	public int getDepth(int _iC) {
		return iarDepths[_iC];
	}
}
