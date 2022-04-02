package model;

/**
 * This class encapsulates the nesting logic.
 * It serves as a repository of all system relevant variables.
 * Thus it makes it more convenient to pass the totality of up-to-date variables
 * (including complex data structures) to an object/method that requires 
 * them for its functionality
 */

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.prefs.Preferences;
import application.Main;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.stage.Stage;
import utilities.Popup;
import utilities.VarianceComponent;

public class Nest {
	private Integer iAsterisk = -1; // position of asterisk
	// which level is associated with individual lines/records
	// of the data file.
	private Integer iFacetCount = 0;
	private Integer iCompCount = 0; // number of Components established
	private Integer iStep = -1; // step counter in algorithm
	private Boolean bDoOver = false; // boolean indicating 'do over' mode
	private Boolean bSimulate = false; // boolean indicating simulating mode

	private String sFileName; // path of control file
	private String title; // called 'gstudy' in urGenova
	private ArrayList<String> comments = new ArrayList<String>();
	private String options;
	private String format;
	private String process;
	private StringBuilder sbFO; // to build basic dictionary
	private StringBuilder sbHFO; // to build hierarchical dictionary
	private ArrayList<Facet> facets = null;
	private Facet[] farFacets = null;
	private SampleSizeTree myTree = null;
	private String[] sarNestedNames;
								// array of final, nested arrays in hierarchical order
	private String sDictionary; // simple simulation of one character dictionary
								// as concatenation of member characters
	private String sHDictionary; 	// the hierarchical dictionary orders the
									// facets in the
									// sequence they have to be processed 
									// according to the nesting hierarchy.
	private Integer iNestCount = 0; // number of nested facets
	private Scene scene = null; 	// standard empty display scene for use in
									// objects
	private Stage primaryStage = null;		// stage of UID
	private Integer iFieldWidth = 0;		// data input field width
	private Boolean bDawdle = false; 		// flag, if true, inactivates normal
											// procedural stepping, and instead
											// steps through
											// sample size collection.
	private Boolean bVarianceDawdle = false;
	private String sControlFileName = "~control.txt"; 	// name for internal use
														// with urGenova
	private String sDataFileName = "~data.txt"; 		// name for internal use with
														// urGenova
	private Main myMain;								// pointer to main class
	private Double dGrandMeans = 0.0;
	private ArrayList<VarianceComponent> VarianceComponents = null;
	private String sTitle = " G Study.";
	private Double dRel = 0.0;
	private Double dAbs = 0.0;
	private Double dConsistency = 0.0;
	private Preferences prefs = null;
	private Integer iFloor = 0; // lowest value of Score scale
	private Double dMean = 0.0; // mean value of score
	private Integer iCeiling = 0; // maximum value of score
	private Double[][] dVectors = null;
	private Double[] dVC = null;
	private Double[] varianceCoefficients = null;	// array for Variance coefficients
	private String sPlatform = null;				// operating system on which G_String is running
	private Popup popup = null;						// collects and displays diagnostic information.
	private ArrayList<String> salNestedNames = new ArrayList<String>();

	public Nest(Popup _popup, Main _myMain, Preferences _prefs) {
		/*
		 * Constructor
		 */
		
		iAsterisk = 0;
		facets = new ArrayList<Facet>();
		sbFO = new StringBuilder();
		sbHFO = new StringBuilder();
		popup = _popup;
		popup.setClass("Nest");
		myMain = _myMain;
		prefs = _prefs;
		VarianceComponents = new ArrayList<VarianceComponent>();
		sPlatform = prefs.get("OS", null);
	}

	public String getName(char c) {
		// return facet name according to cDesignation
		return this.getFacet(c).getName();
	}

	public Facet getFacet(int iOrder) {
		return facets.get(iOrder);
	}

	public Facet getFacet(char _cDesignation) {
		Integer index = sDictionary.indexOf(_cDesignation);
		return facets.get(index);
	}

	public Boolean getSimulate() {
		return bSimulate;
	}

	public String getDictionary() // return dictionary according to storage
									// order
	{
		return sDictionary;
	}

	public String getHDictionary() // return dictionary according to logical
									// order
	{
		if (sHDictionary == null)
			sHDictionary = sDictionary;
		return sHDictionary;
	}

	public void setHDictionary(String _sHDictionary) {
		sHDictionary = _sHDictionary;
		if (myTree == null)
			myTree = new SampleSizeTree(this, sDictionary, popup, prefs);
		myTree.setHDictionary(_sHDictionary);
	}

	public void setAsterisk(Integer _Asterisk) {
		iAsterisk = _Asterisk;
	}

	public void setAsterisk(char cAsterisk){
		myTree.setAsterisk(cAsterisk);
	}

	public Integer getAsterisk() {		// index of facet with asterisk
		return iAsterisk;				// i.e. the facet associated data line change.
	}

	public Integer getHAsterisk() {		// hierachic index of facet with asterisk
		char c = sDictionary.toCharArray()[iAsterisk];
		return sHDictionary.indexOf(c);
	}

	public void incrementSteps() {
		if (!bDawdle)
			iStep++;
	}

	public void decrementSteps() {
		if (iStep > 0)
			iStep--;
	}

	public Integer getStep() {
		return iStep;
	}

	public void setDoOver(Boolean _bDoOver) {
		bDoOver = _bDoOver;
	}

	public Boolean getDoOver() {
		return bDoOver;
	}

	public void setSimulate(Boolean _bSimulate) {
		bSimulate = _bSimulate;
	}

	public void setTitle(String _title) {
		title = _title;
	}

	public void addFacet(Facet _facet) {
		facets.add(_facet);
		sbFO.append(_facet.getDesignation());
		sDictionary = sbFO.toString();
		iFacetCount++;
	}

	public Integer getFacetCount() {
		return iFacetCount;
	}

	public void setFacetCount(Integer _iFacetCount) {
		/*
		 * Indices are in hierachical order
		 */
		iFacetCount = _iFacetCount;
	}

	public String getTitle() {
		return title;
	}

	public String[] getComments() {
		return comments.toArray(new String[comments.size()]);
	}

	public void setComments(String _comments) {
		comments = new ArrayList<String>();
		String[] lines = _comments.split("\n");
		for (String line : lines)
			comments.add(line);
	}

	public Facet getNewFacet() {
		Facet newFacet = new Facet(this);
		facets.add(newFacet);
		return newFacet;
	}

	public char getCStarred() {
		return sDictionary.toCharArray()[iAsterisk];
	}

	public void setStarred(char cStarred) {
		iAsterisk = sDictionary.indexOf(cStarred);
	}

	public void addComment(String commentLine) {
		comments.add(commentLine);
	}

	public void setOptions(String _sOptions) {
		options = _sOptions;
	}

	public void addEffect(String _sEffect) {
		/**
		 * Builds up 'SampleSizeTree' as design elements are added.
		 * Basically it is a simple lexical analyzer.
		 */
		
		char cTarget;
		if (myTree == null) {
			myTree = new SampleSizeTree(this, sDictionary, popup, prefs);

		}
		String[] words = _sEffect.trim().split("\\s+");
		String sNest = words[0];
		String[] sss = null;
		Integer iFirst = 1;
		Integer iLength = words[0].length();
		Boolean bPrimary = true;
		Boolean bAsterisk = false;
		if (words[0].indexOf("*") == 0) // indicates starred face;
		{
			bAsterisk = true;
			if (iLength.equals(1)) {
				sNest = words[1];
				iFirst = 2;
			} else {
				sNest = words[0].substring(1, iLength);
			}
		}
		cTarget = sNest.trim().toCharArray()[0];
		Integer iFacet = sDictionary.indexOf(cTarget);
		if (sNest.length() > 1)
			bPrimary = false;
		if (bAsterisk)
			iAsterisk = iFacet;
		this.getFacet(cTarget).setNested(!bPrimary);
		salNestedNames.add(sNest);
		sbHFO.append(cTarget);
		sHDictionary = sbHFO.toString();
		sss = new String[words.length - iFirst];
		for (Integer i = 0; i < words.length - iFirst; i++)
			sss[i] = words[i + iFirst];
		myTree.addSampleSize(cTarget, sss);
		myTree.setHDictionary(sHDictionary);
		iNestCount++;
	}

	public void addFormat(String _sFormat) {
		format = _sFormat;
	}

	public void addProcess(String _sProcess) {
		process = _sProcess;
	}

	public ObservableList<String> getNests() {
		if (sarNestedNames == null) {
			iNestCount = salNestedNames.size();
			sarNestedNames = new String[iNestCount];
			for (int i = 0; i < iNestCount; i++)
				sarNestedNames[i] = salNestedNames.get(i);
		}
		ArrayList<String> result = new ArrayList<String>();
		for (char c : sHDictionary.toCharArray())
			result.add(sarNestedNames[sHDictionary.indexOf(c)]);
		return (ObservableList<String>) FXCollections.observableArrayList(result);
	}

	public void setNests(String[] _nests) {
		/**
		 * The term 'nest' here is confusing. It has nothing to do with the similar class name.
		 * It is a historic relic for 'nested arrangements', and needs to be distinguished
		 * from the 'Nest'. The author apologizes.
		 */
		// convert observable list back to string array
		iNestCount = _nests.length;
		if (myTree == null) {
			myTree = new SampleSizeTree(this, sDictionary, popup, prefs);
			myTree.setHDictionary(sHDictionary);
		}
		String sNest = null;
		for (Integer i = 0; i < iNestCount; i++) {
			sNest = _nests[i];
			sarNestedNames[i] = sNest;
			char cFacet = sNest.toCharArray()[0];
			Facet f = getFacet(cFacet);
			f.doNesting(sNest);
		}
		myTree.setNests(_nests);
		iNestCount = _nests.length;
	}

	public void setScene(Scene _scene) {
		scene = _scene;
	}

	public Scene getScene() {
		return scene;
	}

	public void setStage(Stage _stage) {
		primaryStage = _stage;
	}

	public Stage getStage() {
		return primaryStage;
	}

	public void setFieldWidth(Integer _width) {
		iFieldWidth = _width;
	}

	public Integer getFieldWidth() {
		return iFieldWidth;
	}

	public void setFileName(String _fName) {
		sFileName = _fName;
	}

	public String getFileName() {
		return sFileName;
	}

	public Boolean getDawdle() {
		return bDawdle;
	}

	public Boolean getVarianceDawdle() {
		return bVarianceDawdle;
	}

	public void setDawdle(Boolean _dawdle) {
		bDawdle = _dawdle;
	}

	public void setVarianceDawdle(Boolean _Dawdle) {
		bVarianceDawdle = _Dawdle;
	}

	public String getHNest(Integer i) {
		char c = sDictionary.toCharArray()[i];
		Integer iReg = sHDictionary.indexOf(c);
		return sarNestedNames[iReg];
	}

	public String getOptions() {
		return options;
	}

	public String getFormat() {
		return format;
	}

	public String getProcess() {
		return process;
	}

	public void show(Group _group) {
		myMain.show(_group);
	}

	public String getControlFileName() {
		return sControlFileName;
	}

	public String getDataFileName() {
		return sDataFileName;
	}

	public static String padLeft(String s, int n) {
		return String.format("%1$" + n + "s", s);
	}

	public void setGrandMeans(Double _means) {
		dGrandMeans = _means;
	}

	public Double getGreatMeans() {
		return dGrandMeans;
	}

	public void setVariance(String _line) {
		VarianceComponents.add(new VarianceComponent(this, _line, sPlatform, popup));
	}

	public void setOrder() {
		/**
		 * Essential routine to establish the logic of facet organization. Is
		 * called from AnaGroups and SynthGroups after Nesting step
		 */
		
		Integer iFacetCount = sHDictionary.length();
		//Boolean bNotFound = true;
		@SuppressWarnings("unchecked")
		ArrayList<Integer>[] ialNestees = new ArrayList[iFacetCount];
		for (Integer i = 0; i < iFacetCount; i++)
			ialNestees[i] = new ArrayList<Integer>();
		farFacets = new Facet[iFacetCount];
		for (Integer i = 0; i < iFacetCount; i++) {
			Facet f = facets.get(i);
			char c = f.getDesignation();
			Integer j = sDictionary.indexOf(c);
			f.setID(j);
			farFacets[j] = f;
		}
	}
	
	public Facet[] getFacets() {
		Facet[] farFacet = new Facet[iFacetCount];
		for (int i = 0; i < iFacetCount; i++)
			farFacet[i] = facets.get(i);
		return farFacet;
	}

	public Facet getHFacet(Integer iFacet) {
		return farFacets[iFacet];
	}

	public Integer getNestCount() {
		return iNestCount;
	}

	public void G_setFacets() {
		/**
		 * sets up facet types for generalizability calculations
		 */
		
		char cDiff = 'x';
		
		/**
		 * First, identify facet of differentiation
		 */
		for (Facet f : farFacets) {
			if (f.getOrder() == 0) {
				f.setFacetType('d');
				cDiff = f.getDesignation();
			}
			else
				f.setFacetType('g');
		}
		
		/**
		 * Then identify facets of stratification
		 */
		
		String sNest = null;
		for (int i = 0; i < iNestCount; i++) {
			sNest = sarNestedNames[i];
			if (sNest.indexOf(cDiff) == 0) {
				/**
				 * That's a nest with the stratified facet of differentiation
				 */
				char[] cFacets = sNest.replace(":",  "").toCharArray();
				for (char c : cFacets)
					if (c != cDiff)
						getFacet(c).setFacetType('s');
				
			}
		}
	}

	public void createDictionaries() {
		StringBuilder sb = new StringBuilder();
		farFacets = new Facet[iFacetCount];
		Integer iCount = 0;
		for (Integer i = 0; i < iFacetCount; i++) {
			Facet f = facets.get(i);
			f.setID(i);
			sb.append(f.getDesignation());
			farFacets[iCount++] = f;
		}
		sDictionary = sb.toString();
	}

	public void setLevels() {
		for (Facet f : farFacets)
			f.setFacetLevel();
	}

	private void saveInteger(String _sTarget, String _sValue) {
		/**
		 * Handles integer conversion for the floor and ceiling score values
		 * in the synthesis
		 */
		
		Integer iValue = 0;
		if (_sValue != null)
			iValue = Integer.parseInt(_sValue);
		switch (_sTarget) {
		case "cFloor":
			iFloor = iValue;
			break;
		case "cCeiling":
			iCeiling = iValue;
			break;
		}
	}

	private void saveDouble(String _sTarget, String _sValue) {
		Double dValue = 0.0;
		if (_sValue != null)
			dValue = Double.parseDouble(_sValue);
		switch (_sTarget) {
		case "cMean":
			dMean = dValue;
			break;
		default:
			break;
		}
	}

	public void saveVariable(String _sType, String _sTarget, String _sValue) {
		switch (_sType) {
		case "Integer":
			saveInteger(_sTarget, _sValue);
			break;
		case "Double":
			saveDouble(_sTarget, _sValue);
			break;
		}
	}

	public void formatResults(StringBuilder sbResult) throws UnsupportedEncodingException {
		/**
		 * This method should probably be in utilities.filer from a logic point of view
		 * but was placed in nests for convenience.
		 * It redacts the prose for G- and D-Studies into a StringBuilder 'sbResult'.
		 */
		
		StringBuilder sb = new StringBuilder();
		Double dAbsolute = 0.0; // total sum of absolute values of weighted
								// components
		Double dFactual = 0.0; // total sum of weighted components > 0.0
		Double dTemp = 0.0; // temporary variable;
		Double dS2_t = 0.0; // sigma2(tau)
		Double dS2_d = 0.0; // sigma2(delta)
		Double dS2_D = 0.0; // sigma2(Delta)

 
		for (Facet f : farFacets) {
			if (f.getFacetType() == 'g'){
					sb.append(f.getDiagDesignation());
			}
		}
		sb.append("\n");

		try {
			sbResult.append("\n\n-----------------------------------\n");
			sbResult.append("Results " + sTitle + "\n\n");
			sbResult.append(sb.toString());
		} catch (Exception e1) {
			popup.tell("formatResults_a", e1);
		}
		sTitle = " D-Study.";
		for (VarianceComponent vc : VarianceComponents) {
			vc.doCoefficient(sbResult);
			dTemp = vc.getVarianceComponent() / vc.getCoefficient();
			dAbsolute += Math.abs(dTemp);
			if (dTemp > 0.0) {
				dFactual += dTemp;
				if (vc.b_tau())
					dS2_t += dTemp;
				if (vc.b_delta())
					dS2_d += dTemp;
				if (vc.b_Delta())
					dS2_D += dTemp;
			}
		}
		sbResult.append("\n");

		sbResult.append(reCode("\u03C3\u00B2(\u03C4) = " + String.format("%.4f", dS2_t) + "\n"));
		sbResult.append(reCode("\u03C3\u00B2(\u03B4) = " + String.format("%.4f", dS2_d) + "\n"));
		sbResult.append(reCode("\u03C3\u00B2(\u0394) = " + String.format("%.4f", dS2_D) + "\n"));
		dRel = dS2_t / (dS2_t + dS2_d);
		dAbs = dS2_t / (dS2_t + dS2_D);
		dConsistency = dFactual / dAbsolute;
		try {
			sbResult.append(reCode("\nGENERALIZABILITY COEFFICIENTS:\n\n"));
			sbResult.append(reCode("Consistency   = " + String.format("%.2f", dConsistency) + "\n"));
			sbResult.append(reCode("Eρ\u00B2      	  = " + String.format("%.2f", dRel) + "\n"));
			sbResult.append(reCode("Φ             = " + String.format("%.2f", dAbs) + "\n"));
		} catch (Exception e) {
			popup.tell("formatResults_b", e);
		}
	}

	public Double getRho() {
		return dRel;
	}

	public Double getPhi() {
		return dAbs;
	}

	public Double getCons() {
		return dConsistency;
	}

	public Boolean checkColOffset_1() {
		/**
		 * Checks if each column is only mentioned once
		 */
		ArrayList<Integer> ialCols = new ArrayList<Integer>();
		Integer iFac = null;
		for (Facet f : facets) {
			iFac = f.getOffset();
			if ((iFac > 0) && ialCols.contains(iFac))
				return false;
			else
				ialCols.add(iFac);
		}
		return true;
	}

	public Integer checkColOffset_2(Integer _iHilight) {
		/**
		 * Checks if each column up to and including the starred facet, but only
		 * those, is mentioned exactly once and in proper order. return: 0: OK;
		 * 1: iAsterisk != iHilight; 2: facet offsets out of order
		 */

		if (iAsterisk != _iHilight)
			return 1;
		char[] cHDictionary = sHDictionary.toCharArray();
		for (Integer i = 0; i <= _iHilight; i++) {
			Facet f = getFacet(cHDictionary[i]);
			if (f.getOffset() != i)
				return 2;
		}
		return 0;
	}

	public void setStep(Integer _iStep) {
		iStep = _iStep;
	}

	public String getNestedName(Integer iSAR) {
		return sarNestedNames[iSAR];
	}

	public void setCompCount(Integer _cc) {
		if (iCompCount == 0) {
			iCompCount = _cc;
			varianceCoefficients = new Double[iCompCount];
			for  (int i = 0; i < iCompCount; i++)
				varianceCoefficients[i] = 0.0;
		}
	}

	public Integer getCompCount() {
		return iCompCount;
	}

	public Integer getCompIndex(String sComp) {
		Integer iResult = null;
		for (Integer i = 0; i < iCompCount; i++)
			if (sComp.equals(sarNestedNames[i])) {
				iResult = i;
				break;
			}
		if (iResult < iFacetCount)
			iResult = sDictionary.indexOf(sHDictionary.toCharArray()[iResult]);
		return iResult;
	}

	public void setComponent(String sComp, Integer iComp) {
		sarNestedNames[iComp] = sComp;
	}

	public void createVectors(Integer iDim) {
		dVectors = new Double[iDim][];
		dVC = new Double[iDim];
	}

	public void set_dVC(Integer iPos, Double value) {
		dVC[iPos] = value;
	}

	public Double get_dVC(Integer iPos) {
		return dVC[iPos];
	}

	public void setVector(Integer iPos, Double[] dVector) {
		dVectors[iPos] = dVector;
	}

	public Character getNestor(char cF) {
		return getFacet(cF).getNestor();
	}

	public Integer vCCount() {
		return varianceCoefficients.length;
	}

	public void setVariancecoefficient(int i, Double _dVC) {
		varianceCoefficients[i] = _dVC;
	}

	public Double getVarianceCoefficient(int i) {
		return varianceCoefficients[i];
	}

	public Integer getCeiling() {
		return iCeiling;
	}

	public Integer getFloor() {
		return iFloor;
	}

	public Double getMean() {
		return dMean;
	}

	/*public Double getGF() {
		return dGF;
	}*/

	public void setSarNestedNames(ArrayList<String> _sarNestedNames) {
		int i = 0;
		sarNestedNames = new String[_sarNestedNames.size()];
		for (String s : _sarNestedNames) {
			if ((s == null) | (s.trim() == ""))
				break;
			sarNestedNames[i++] = s;
		}
	}
	
	public String[]  getSarNestedNames(){
		return sarNestedNames;
	}

	public void addAnchors(String value) {
		String[] sAnchors = value.split("\\s+");
		int iAnchors = sAnchors.length;
		if (iAnchors != 3)
		iFloor = Integer.parseInt(sAnchors[0]);
		dMean = Double.parseDouble(sAnchors[1]);
		iCeiling = Integer.parseInt(sAnchors[2]);
	}

	public void addVariances(String value) {
		String[] sVariances = value.split("\\s+");
		int ivCount = sVariances.length;
		varianceCoefficients = new Double[ivCount];
		for (int i = 0; i < ivCount; i++)
			varianceCoefficients[i] = Double.parseDouble(sVariances[i]);
	}
	
	public void createVarianceCoefficients(Integer ivCount) {
		varianceCoefficients = new Double[ivCount];	
	}

	public Integer getVcDim() {
		Integer x = varianceCoefficients.length;
		return x;
	}

	private String reCode(String sInput) throws UnsupportedEncodingException {
		String sOutput = sInput;
		if (sPlatform.equals("Mac")) {
			byte[] buffer = sInput.getBytes("MacGreek");
			sOutput = new String(buffer);
		}
		return sOutput;
	}

	public SampleSizeTree getTree() {
		return myTree;
	}
	
	public Popup getPopup() {
		return popup;
	}
	
	public String getSynthDictionary() {
		/**
		 * Returns dictionary ordered for purpose of synthesis:
		 * first from crossed to most nested, then according
		 * to data order.
		 */
		
		StringBuilder sb = new StringBuilder();
		String dict = sHDictionary;
		int L= dict.length();
		int iDepth = 1;
		int iCount = 1;
		String ss = null;
		while (iCount <= L) {
			for (String s : sarNestedNames) {
				if(s.equals(null) | s.equals(" "))
						break;
				ss = s.replace(":", "");
				if (ss.length() == iDepth) {
					sb.append(ss.substring(0, 1));
					iCount++;
				}
			}
			iDepth++;
		}
		return sb.toString();
	}
	
	public void doComponents() {
		/**
		 * Generates facet combinations responsible for variance
		 * components, including their appropriate syntax.
		 * The object is somewhat occult. It gets only called once in
		 * steps.SynthGroups.VarianceComponentsGroup line 887.
		 * It sets up the string array sarNestedNames in Nest and the
		 * 'aNode' array 'nodes' in 'nest'.
		 */

		myTree.setFacetCount(sDictionary.length());
	}
	
	public Boolean isComponent(String sTest) {
		Boolean bContained = false;
			for (String s : sarNestedNames)
				if (s.equals(sTest))
					bContained = true;
		return bContained;
	}
	
	public String[] getOrderedNestedNames(String _sOrder) {
		int iSize = getNestCount();
		String s;
		String sOrder = _sOrder;
		String[] sarOrderedNames = new String[iSize];
		for (int i = 0; i < iSize; i++) {
			s = sarNestedNames[i];
			sarOrderedNames[sOrder.indexOf(s.toCharArray()[0])] = s;
		}
		return sarOrderedNames;
	}
	
	public void doNesting() {
		for (String s : sarNestedNames) {
			char cFacet = s.toCharArray()[0];
			Facet f = getFacet(cFacet);
			f.doNesting(s);
		}
	}
}
