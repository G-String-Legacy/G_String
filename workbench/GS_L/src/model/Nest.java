package model;

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

/**
 * <h1>class Nest</h1>
 * Encapsulates the major parameters of the assessment design, and the central variables required for analysis.
 * <code>Nest</code> is used in most other classes as common repository to access central variables and parameters.
 * 
 * @author ralph
 * @version %v..%
*/
public class Nest {
	
	/**
	 * <code>iAsterisk</code> the hierarchical position (sHDirectory) of
	 * the facet carrying the asterisk, i.e. the facet associated with a carriage
	 * return in the data input file.
	 */
	private Integer iAsterisk = -1;

	/**
	 * <code>iFacetCount</code> the number of facets in the design.
	 */
	private Integer iFacetCount = 0;
	
	/**
	 * <code>iStep</code> keeps count of the current step in <code>AnaGroups</code> and <code>SynthGroups</code>.
	 */
	private Integer iStep = -1;
	
	/**
	 * <code>bDoOver</code> <code>true</code>: parameter input from script, <code>false</code>: manual entry.
	 */
	private Boolean bDoOver = false;

	/**
	 * <code>bSimulate</code> <code>false</code> (default): perform analysis, <code>true</code>: perform analysis.
	 */
	private Boolean bSimulate = false;

	/**
	 * <code>sFileName</code> path of control file.
	 */
	private String sFileName;

	/**
	 * <code>sScriptTitle</code> title in script; default 'gstudy'.
	 */
	private String sScriptTitle;

	/**
	 * <code>salComments</code> array list containing comments for script.
	 */
	private ArrayList<String> salComments = new ArrayList<String>();
	
	/**
	 * <code>sOptions</code> official optionm specifications for urGenova (see urGenova manual).
	 */
	private String sOptions;
	
	/**
	 * <code>sFormat</code> official format specifications  for urGenova (see urGenova manual).
	 */
	private String sFormat;
	
	/**
	 * <code>sFormat</code> official process specifications for urGenova (see urGenova manual).
	 */
	private String sProcess;
	
	/**
	 * <code>sbFO</code> stringbuilder to build basic facet directory <code>sDictionary</code>, 
	 * the order in which the facets were entered initially.
	 */
	private StringBuilder sbFO; 

	/**
	 * <code>sbHFO</code> stringbuilder to build hierarchic facet directory <code>sHDictionary</code>,
	 * the order they were arranged subsequently.
	 */
	private StringBuilder sbHFO;

	/**
	 * <code>facets</code> array list of all the facets in the design.
	 */
	private ArrayList<Facet> facets = null;
	
	/**
	 * <code>farFacets</code> array of all facets
	 */
	private Facet[] farFacets = null;
	
	/**
	 * <code>myTree</code> pointer to <code>SampleSizeTree</code>.
	 */
	private SampleSizeTree myTree = null;
	
	/**
	 * <code>sarNestedNames</code> array of final, nested arrays in hierarchical order.
	 */
	private String[] sarNestedNames;
	
	/**
	 * <code>sDictionary</code> simple simulation of one character dictionary
	 * as concatenation of member characters in the original order,
	 * the facets were entered. 
	 */
	private String sDictionary;

	/**
	 * <code>sHDictionary</code> the hierarchical dictionary orders the
	 * facets in the order the data appear in the data file.
	 */
	private String sHDictionary; 
	
	/**
	 * <code>iNestCount</code> number of nested facets.
	 */
	private Integer iNestCount = 0;

	/**
	 * <code>scene</code> standard empty display <code>Scene</code> for use in objects.
	 */
	private Scene scene = null; 

	/**
	 * <code>primaryStage</code> display stage of GUI.
	 */
	private Stage primaryStage = null;

	/**
	 * <code>bDawdle</code> boolean <code>false</code> (default): proceed to normal next step; <code>true</code>: instead steps through sample size collection.
	 */
	private Boolean bDawdle = false; 

	/**
	 * <code>bVarianceDawdle</code> boolean <code>false</code> (default): proceed to normal next step; <code>true</code>: instead steps through variance collection.
	 */
	private Boolean bVarianceDawdle = false;
	
	/**
	 * <code>sControlFileName</code> default name for urGENOVA control file.
	 */
	private String sControlFileName = "~control.txt";
	
	/**
	 * <code>sDataFileName</code> default name for urGENOVA data file.
	 */
	private String sDataFileName = "~data.txt"; 
	
	/**
	 * <code>myMain</code> pointer to class <code>Main</code>.
	 */
	private Main myMain;

	/**
	 * <code>dGrandMeans</code> grand means of all scores in data file, as Double.
	 */
	private Double dGrandMeans = 0.0;
	
	/**
	 * <code>salVarianceComponents</code> array list of variance components.
	 */
	private ArrayList<VarianceComponent> salVarianceComponents = null;
	
	/**
	 * <code>sTitle</code> default header in analysis output.
	 */
	private String sTitle = " G Study.";
	
	/**
	 * <code>dRel</code> Generalizability Coefficient.
	 */
	private Double dRel = 0.0;
	
	/**
	 * <code>dAbs</code> Index of Dependability.
	 */
	private Double dAbs = 0.0;
	
	/**
	 * <code>prefs</code> pointer to <code>Preferences</code> API.
	 */
	private Preferences prefs = null;
	
	/**
	 * <code>iFloor</code> lowest permitted score value in synthesis.
	 */
	private Integer iFloor = 0;

	/**
	 * <code>dMean</code> target value for mean in synthetic data output.
	 */
	private Double dMean = 0.0;

	/**
	 * <code>iCeiling</code> highest permitted score value in synthesis.
	 */
	private Integer iCeiling = 0;

	/**
	 * <code>dVectors</code> intermediate double matrix for calculation of variance components.
	 */
	private Double[][] dVectors = null;
	
	/**
	 * <code>dVC</code> intermediate double vector for calculation of variance components.
	 */
	private Double[] dVC = null;
	
	/**
	 * <code>darVarianceCoefficients</code> double array of Variance Coefficients.
	 */
	private Double[] darVarianceCoefficients = null;

	/**
	 * <code>sPlatform</code> name of current OS platform ('Linux', 'Mac', or 'Windows').
	 */
	private String sPlatform = null;
	
	/**
	 * <code>popup</code> pointer to <code>Popup</code>, exception handler.
	 */
	private Popup popup = null;

	/**
	 * <code>salNestedNames</code> array list of nested configurations from AnaGroups step 6.
	 */
	private ArrayList<String> salNestedNames = new ArrayList<String>();

	
	/**
	 * Constructor for class <code>Nest</code>
	 * 
	 * @param _popup - pointer to exception handler
	 * @param _myMain - pointer to Main class
	 * @param _prefs - pointer to Preferences API
	 */
	public Nest(Popup _popup, Main _myMain, Preferences _prefs) {
		
		iAsterisk = 0;
		facets = new ArrayList<Facet>();
		sbFO = new StringBuilder();
		sbHFO = new StringBuilder();
		popup = _popup;
		popup.setClass("Nest");
		myMain = _myMain;
		prefs = _prefs;
		salVarianceComponents = new ArrayList<VarianceComponent>();
		sPlatform = prefs.get("OS", null);
	}

	/**
	 * facet name from char designation
	 * 
	 * @param c - char designation
	 * @return - full facet name
	 */
	public String getName(char c) {
		// return facet name according to cDesignation
		return this.getFacet(c).getName();
	}

	/**
	 * facet from order number
	 * 
	 * @param iOrder - order in sDictionary
	 * @return facets.get(iOrder)- Facet object
	 */
	public Facet getFacet(int iOrder) {
		return facets.get(iOrder);
	}

	/**
	 * facet from char designation
	 * 
	 * @param _cDesignation - char designation
	 * @return facets.get(index);
	 */
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

	public void setTitle(String _sTitle) {
		sScriptTitle = _sTitle;
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
		return sScriptTitle;
	}

	public String[] getComments() {
		return salComments.toArray(new String[salComments.size()]);
	}

	public void setComments(String _comments) {
		salComments = new ArrayList<String>();
		String[] lines = _comments.split("\n");
		for (String line : lines)
			salComments.add(line);
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
		salComments.add(commentLine);
	}

	public void setOptions(String _sOptions) {
		sOptions = _sOptions;
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
		sFormat = _sFormat;
	}

	public void addProcess(String _sProcess) {
		sProcess = _sProcess;
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
		sarNestedNames = new String[iNestCount];
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
		return sOptions;
	}

	public String getFormat() {
		return sFormat;
	}

	public String getProcess() {
		return sProcess;
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
		salVarianceComponents.add(new VarianceComponent(this, _line, sPlatform, popup));
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
		for (VarianceComponent vc : salVarianceComponents) {
			vc.doCoefficient(sbResult);
			dTemp = vc.getVarianceComponent() / vc.getDenominator();
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
		try {
			sbResult.append(reCode("\nGENERALIZABILITY COEFFICIENTS:\n\n"));
			sbResult.append(reCode("Eρ\u00B2      	  = " + String.format("%.2f", dRel) + "\n"));
			sbResult.append(reCode("Φ           = " + String.format("%.2f", dAbs) + "\n"));
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

	public void setStep(Integer _iStep) {
		iStep = _iStep;
	}

	public String getNestedName(Integer iSAR) {
		return sarNestedNames[iSAR];
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
		return darVarianceCoefficients.length;
	}

	public void setVariancecoefficient(int i, Double _dVC) {
		darVarianceCoefficients[i] = _dVC;
	}

	public Double getVarianceCoefficient(int i) {
		return darVarianceCoefficients[i];
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
		darVarianceCoefficients = new Double[ivCount];
		for (int i = 0; i < ivCount; i++)
			darVarianceCoefficients[i] = Double.parseDouble(sVariances[i]);
	}
	
	public void createVarianceCoefficients(Integer ivCount) {
		darVarianceCoefficients = new Double[ivCount];	
	}

	public Integer getVcDim() {
		Integer x = darVarianceCoefficients.length;
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
