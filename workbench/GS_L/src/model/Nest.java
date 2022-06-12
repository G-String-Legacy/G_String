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
	 * <code>falFacets</code> array list of all the facets in the design.
	 */
	private ArrayList<Facet> falFacets = null;
	
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
		falFacets = new ArrayList<Facet>();
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
		return falFacets.get(iOrder);
	}

	/**
	 * facet from char designation
	 * 
	 * @param _cDesignation - char designation
	 * @return facets.get(index);
	 */
	public Facet getFacet(char _cDesignation) {
		Integer index = sDictionary.indexOf(_cDesignation);
		return falFacets.get(index);
	}

	/**
	 * getter for <code>bSimulate</code>.
	 * 
	 * @return bSimulate
	 */
	public Boolean getSimulate() {
		return bSimulate;
	}

	/**
	 * getter for <code>sDictionary</code>.
	 * 
	 * @return sDictionary
	 */
	public String getDictionary()	{
		return sDictionary;
	}

	/**
	 * getter for <code>sHDictionary</code>.
	 * 
	 * @return sHDictionary
	 */
	public String getHDictionary()	{
		if (sHDictionary == null)
			sHDictionary = sDictionary;
		return sHDictionary;
	}

	/**
	 * setter for <code>sHDictionary</code>.
	 * 
	 * @param _sHDictionary hierarchic facet dictionary
	 */
	public void setHDictionary(String _sHDictionary) {
		sHDictionary = _sHDictionary;
		if (myTree == null)
			myTree = new SampleSizeTree(this, sDictionary, popup, prefs);
		myTree.setHDictionary(_sHDictionary);
	}

	/**
	 * setter for <code>iAsterisk</code> as hierarchical facet order. 
	 * 
	 * @param _Asterisk integer value
	 */
	public void setAsterisk(Integer _Asterisk) {
		iAsterisk = _Asterisk;
	}

	/**
	 * setter of <code>iAsterisk</code> by facet char Designation.
	 * @param cAsterisk char facet designation
	 */
	public void setAsterisk(char cAsterisk){
		myTree.setAsterisk(cAsterisk);
	}

	/**
	 * getter of <code>iAsterisk</code> as hierarchical facet order.
	 * @return iAsterisk  facet index of asterisk
	 */
	public Integer getAsterisk() {
		return iAsterisk;
	}

	/**
	 * getter for hierarchical <code>iAsterisk</code> position.
	 * 
	 * @return   hierarchical facet index of asterisk
	 */
	public Integer getHAsterisk() {
		char c = sDictionary.toCharArray()[iAsterisk];
		return sHDictionary.indexOf(c);
	}

	/**
	 * conditional <code>iStep</code> incrementer.
	 */
	public void incrementSteps() {
		if (!bDawdle)
			iStep++;
	}

	/**
	 * getter for <code>iStep</code>, i.e. position in data entry sequence.
	 * 
	 * @return iStep
	 */
	public Integer getStep() {
		return iStep;
	}

	/**
	 * setter for <code>bDoOver</code>, i.e. 'script' vs 'manual' parameter entry.
	 * 
	 * @param _bDoOver  flag to get input parameters from script
	 */
	public void setDoOver(Boolean _bDoOver) {
		bDoOver = _bDoOver;
	}

	/**
	 * getter for <code>bDoOver</code>, i.e. 'script' vs 'manual' parameter entry.
	 * 
	 * @return bDoOver
	 */
	public Boolean getDoOver() {
		return bDoOver;
	}

	/**
	 * setter for <code>bSimulate</code> - flag 'Simulate' vs 'Analyze'.
	 * 
	 * @param _bSimulate  flag to do synthesis
	 */
	public void setSimulate(Boolean _bSimulate) {
		bSimulate = _bSimulate;
	}

	public void setTitle(String _sTitle) {
		sScriptTitle = _sTitle;
	}

	/**
	 * adds <code>Facet</code> to <code>falFacets</code>.
	 * 
	 * @param _facet Facet object
	 */
	public void addFacet(Facet _facet) {
		falFacets.add(_facet);
		sbFO.append(_facet.getDesignation());
		sDictionary = sbFO.toString();
		iFacetCount++;
	}

	/**
	 * getter for <code>iFacetCount</code>.
	 * 
	 * @return iFacetCount
	 */
	public Integer getFacetCount() {
		return iFacetCount;
	}

	/**
	 * setter for <code>iFacetCount</code>.
	 * 
	 * @param _iFacetCount count of facets
	 */
	public void setFacetCount(Integer _iFacetCount) {
		iFacetCount = _iFacetCount;
	}

	/**
	 * getter for <code>sScriptTitle</code>.
	 * 
	 * @return sScriptTitle
	 */
	public String getTitle() {
		return sScriptTitle;
	}

	/**
	 * getter for script comments.
	 * 
	 * @return string array of comments
	 */
	public String[] getComments() {
		return salComments.toArray(new String[salComments.size()]);
	}

	/**
	 * setter for script comments.
	 * 
	 * @param _comments  flowing text with carriage returns.
	 */
	public void setComments(String _comments) {
		salComments = new ArrayList<String>();
		String[] lines = _comments.split("\n");
		for (String line : lines)
			salComments.add(line);
	}

	/**
	 * adds new, default <code>Facet</code> to facet array list, and returns pointer.
	 * 
	 * @return newFacet
	 */
	public Facet getNewFacet() {
		Facet newFacet = new Facet(this);
		falFacets.add(newFacet);
		return newFacet;
	}

	/**
	 * returns designation character of starred facet.
	 * 
	 * @return facet char
	 */
	public char getCStarred() {
		return sDictionary.toCharArray()[iAsterisk];
	}

	/**
	 * setter of <code>iAsterisk</code> via facet char designation.
	 * 
	 * @param cStarred facet designation char of starred facet
	 */
	public void setStarred(char cStarred) {
		iAsterisk = sDictionary.indexOf(cStarred);
	}

	/**
	 * add commentLine to <code>salComments</code>.
	 * 
	 * @param _sCommentLine  line of comment to be included in script
	 */
	public void addComment(String _sCommentLine) {
		salComments.add(_sCommentLine);
	}

	/**
	 * setter for <code>sOptions</code>.
	 * 
	 * @param _sOptions  string of options to be included in script
	 */
	public void setOptions(String _sOptions) {
		sOptions = _sOptions;
	}

	/**
	 * parser for 'Effect' line in analyze script
	 * 
	 * @param _sEffect EFFECT describes nested or unnested configuration of Facets
	 */
	public void addEffect(String _sEffect) {
		/*
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

	/**
	 * setter for <code>sFormat</code>.
	 * 
	 * @param _sFormat  to be included in script
	 */
	public void addFormat(String _sFormat) {
		sFormat = _sFormat;
	}

	/**
	 * setter for <code>sProcess</code>
	 * 
	 * @param _sProcess to be included in script
	 */
	public void addProcess(String _sProcess) {
		sProcess = _sProcess;
	}

	/**
	 * creates observable list of primary nesting from facet input
	 * for use in 'nesting grab and drop' (Step 7)
	 * 
	 * @return ObservableList
	 */
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

	/**
	 * translates string array from AnaGroups step 7 into <code>Nest</code>, 
	 * and <code>SampleSizeTree</code> data structures
	 * 
	 * @param _nests  primary nested facet combinations
	 */
	public void setNests(String[] _nests) {
		/*
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

	/**
	 * setter of <code>Scene</code>.
	 * 
	 * @param _scene  <code>Scene</code> to be displayed
	 */
	public void setScene(Scene _scene) {
		scene = _scene;
	}

	/**
	 * getter of <code>Scene</code>.
	 * 
	 * @return scene  to be displayed
	 */
	public Scene getScene() {
		return scene;
	}

	/**
	 * setter of primary <code>Stage</code>.
	 * 
	 * @param _stage  display stage
	 */
	public void setStage(Stage _stage) {
		primaryStage = _stage;
	}

	/**
	 * getter of primary  of <code>Stage</code>.
	 * 
	 * @return primaryStage
	 */
	public Stage getStage() {
		return primaryStage;
	}

	/**
	 * setter of <code>sFileName</code>.
	 * 
	 * @param _fName  file name
	 */
	public void setFileName(String _fName) {
		sFileName = _fName;
	}

	/**
	 * getter of <code>sFileName</code>.
	 * 
	 * @return sFileName
	 */
	public String getFileName() {
		return sFileName;
	}

	/**
	 * getter of <code>bDawdle</code>.
	 * 
	 * @return bDawdle flag to stepper
	 */
	public Boolean getDawdle() {
		return bDawdle;
	}

	/**
	 * getter of <code>bVarianceDawdle</code>.
	 * 
	 * @return bVarianceDawdle
	 */
	public Boolean getVarianceDawdle() {
		return bVarianceDawdle;
	}

	/**
	 * setter of <code>bDawdle</code>.
	 * 
	 * @param _dawdle  flag to stepper
	 */
	public void setDawdle(Boolean _dawdle) {
		bDawdle = _dawdle;
	}

	/**
	 * setter of <code>bVarianceDawdle</code>.
	 * 
	 * @param _Dawdle flag to stepper
	 */
	public void setVarianceDawdle(Boolean _Dawdle) {
		bVarianceDawdle = _Dawdle;
	}

	/**
	 * returns primary nesting configuration by hierarchical facet index.
	 * 
	 * @param i flag index
	 * @return <code>sarNestedNames[i]</code>
	 */
	public String getHNest(Integer i) {
		char c = sDictionary.toCharArray()[i];
		Integer iReg = sHDictionary.indexOf(c);  //translation from hierarchical to basic order
		return sarNestedNames[iReg];
	}

	/**
	 * getter of <code>sOptions</code> for urGENOVA script.
	 * 
	 * @return <code>sOptions</code>
	 */
	public String getOptions() {
		return sOptions;
	}

	/**
	 * getter of <code>sFormat</code> for urGENOVA script.
	 * 
	 * @return sFormat
	 */
	public String getFormat() {
		return sFormat;
	}

	/**
	 * getter of <code>sProcess</code> for urGENOVA script.
	 * 
	 * @return <code>sProcess</code>
	 */
	public String getProcess() {
		return sProcess;
	}

	/**
	 * passes <code>_group</code> via <code>myMain</code> to <code>controller</code>, i.e. GUI.
	 * 
	 * @param _group display group
	 */
	public void show(Group _group) {
		myMain.show(_group);
	}

	/**
	 * getter of <code>sControlFileName</code>.
	 * 
	 * @return sControlFileName
	 */
	public String getControlFileName() {
		return sControlFileName;
	}

	/**
	 * getter of <code>sDataFileName</code>.
	 * 
	 * @return sDataFileName
	 */
	public String getDataFileName() {
		return sDataFileName;
	}

	/**
	 * formatting method.
	 * 
	 * @param s <code>string s</code>
	 * @param n <code>int n</code>
	 * @return formatted n
	 */
	public static String padLeft(String s, int n) {
		return String.format("%1$" + n + "s", s);
	}

	/**
	 * setter of <code>dGrandMeans</code>.
	 * 
	 * @param _means  value of grand mean
	 */
	public void setGrandMeans(Double _means) {
		dGrandMeans = _means;
	}

	/**
	 * getter of <code>dGrandMeans</code>.
	 * 
	 * @return dGrandMeans
	 */
	public Double getGreatMeans() {
		return dGrandMeans;
	}

	/**
	 * adds new <code>VarianceComponent</code> to <code>salVarianceComponents</code> by parsing
	 * ANOVA line from urGENOVA output.
	 * 
	 * @param _line  string from ANOVA table
	 */
	public void setVariance(String _line) {
		salVarianceComponents.add(new VarianceComponent(this, _line, sPlatform, popup));
	}

	/**
	 * Essential routine to establish the logic of facet organization. Is
	 * called from AnaGroups and SynthGroups after Nesting step
	 */
	public void setOrder() {
		Integer iFacetCount = sHDictionary.length();
		//Boolean bNotFound = true;
		@SuppressWarnings("unchecked")
		ArrayList<Integer>[] ialNestees = new ArrayList[iFacetCount];
		for (Integer i = 0; i < iFacetCount; i++)
			ialNestees[i] = new ArrayList<Integer>();
		farFacets = new Facet[iFacetCount];
		for (Integer i = 0; i < iFacetCount; i++) {
			Facet f = falFacets.get(i);
			char c = f.getDesignation();
			Integer j = sDictionary.indexOf(c);
			f.setID(j);
			farFacets[j] = f;
		}
	}

	/**
	 * getter for array of all facets.
	 * 
	 * @return farFacets  all facets of the study
	 */
	public Facet[] getFacets() {
		Facet[] farFacet = new Facet[iFacetCount];
		for (int i = 0; i < iFacetCount; i++)
			farFacet[i] = falFacets.get(i);
		return farFacet;
	}

	/**
	 * getter of specific facet by hierarchical order
	 * 
	 * @param iFacet Integer hierarchical order of facet
	 * @return Facet target
	 */
	public Facet getHFacet(Integer iFacet) {
		return farFacets[iFacet];
	}

	/**
	 * getter for number of primary Effects (facet nestings)
	 * @return iNestCount  number of primary Effects
	 */
	public Integer getNestCount() {
		return iNestCount;
	}

	/**
	 * sets up facet types for generalizability calculations
	 */
	public void G_setFacets() {
		
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

	/**
	 * Create basic dictionary (in original order of facets)
	 */
	public void createDictionary() {
		StringBuilder sb = new StringBuilder();
		farFacets = new Facet[iFacetCount];
		Integer iCount = 0;
		for (Integer i = 0; i < iFacetCount; i++) {
			Facet f = falFacets.get(i);
			f.setID(i);
			sb.append(f.getDesignation());
			farFacets[iCount++] = f;
		}
		sDictionary = sb.toString();
	}

	/**
	 * Sets facet levels of all facets (for D-Studies
	 */
	public void setLevels() {
		for (Facet f : farFacets)
			f.setFacetLevel();
	}

	/**
	 * Handles integer conversion for the floor and ceiling score values
	 * in the synthesis, and saves them appropriately (Synthesis).
	 * 
	 * @param _sTarget  <code>floor</code> or <code>ceiling</code> respectively
	 * @param _sValue  value as text
	 */
	private void saveInteger(String _sTarget, String _sValue) {
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

	/**
	 * saves 'score' mean value from text to Double, (Synthesis)
	 * @param _sTarget  usually 'cMean'
	 * @param _sValue  value of intended mean score in text form
	 */
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

