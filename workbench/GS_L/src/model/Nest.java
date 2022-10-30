package model;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.logging.Logger;
import java.util.prefs.Preferences;

import application.Main;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.stage.Stage;
import utilities.VarianceComponent;

/**
 * <h1>class Nest</h1>
 * Encapsulates the major parameters of the assessment design, and the central variables required for analysis.
 * <code>Nest</code> is used in most other classes as common repository to access central variables and parameters.
 *
 * @see <a href="https://github.com/G-String-Legacy/G_String/blob/main/workbench/GS_L/src/model/Nest.java">model.Nest</a>
 * @author ralph
 * @version %v..%
 * */
public class Nest {

	/**
	 * This is a VERY IMPORTANT SWITCH! It can only be changed by directly
	 * entering the value here. It controls, whether exceptions get logged, or
	 * cause a stack trace printout in debugging mode.
	 */
	private Boolean bStackTrace = true;
	
	/**
	 * <code>cAsterisk</code> the designating char designation of the facet 
	 * carrying the asterisk, i.e. the facet associated with a carriage
	 * return in the data input file.
	 */
	private char cAsterisk = '-';
	
	/**
	 * Designation char for replicating facet. '-' for none
	 */
	private char cReplicate = '-';

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
	 * <code>bReplicate</code> flag to operate with replicating measurements
	 */
	private Boolean bReplicate;
	
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
	private ArrayList<String> salComments = new ArrayList<>();

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
	 * <code>sbHFO</code> stringbuilder to build hierarchic facet directory <code>sHDictionary</code>,
	 * the order they were arranged subsequently.
	 */
	private StringBuilder sbHFO;

	/**
	 * arra list of facets; temporary if facets come in before number established
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
	 * provides instant translation from index order (sHdictionary) to original
	 * Facet order (sDictionary).
	 */
	private int[] iarCeilings = null;

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
	 * <code>iRepMinimum</code> lowest permitted number of replications.
	 */
	private Integer iRepMinimum = 0;
	
	/**
	 * <code>dRepRange</code> excess over replication minimum as variance.
	 * This serves to calculate the actual number of replications as random process.
	 */
	private Double dRepRange = 0.0;

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
	 * <code>logger</code> pointer to application logger.
	 */
	private Logger logger = null;

	/**
	 * <code>salNestedNames</code> array list of nested configurations from AnaGroups step 6.
	 */
	private ArrayList<String> salNestedNames = new ArrayList<>();
	
	/**
	 * Flag forcing non-regular program exit.	
	 */
	private int iProblem = 0;
	
	/**
	 * number of index columns in data file.
	 */
	private int iHighLight = 0;

	/**
	 * step, at which stepup is to resume after an 'Explain'.
	 * i.e. a setback.
	 */
	private int iResume = -1;
	
	/**
	 * Place holder for subjet facet.
	 */
	private Facet fSubject = null;

	/**
	 * Constructor for class <code>Nest</code>
	 *
	 * @param _logger - pointer application logger
	 * @param _myMain - pointer to Main class
	 * @param _prefs - pointer to Preferences API
	 */
	public Nest(Logger _logger, Main _myMain, Preferences _prefs) {

		cAsterisk = '-';
		sbHFO = new StringBuilder();
		logger = _logger;
		myMain = _myMain;
		prefs = _prefs;
		salVarianceComponents = new ArrayList<>();
		sPlatform = prefs.get("OS", null);
		myTree = new SampleSizeTree(this, logger, prefs);
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
		return farFacets[iOrder];
	}

	/**
	 * facet from char designation
	 *
	 * @param _cDesignation - char designation
	 * @return facets.get(index);
	 */
	public Facet getFacet(char _cDesignation) {
		Integer index = sDictionary.indexOf(_cDesignation);
		return farFacets[index];
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
	 * @param _sHDictionary  hierarchic facet dictionary
	 */
	public void setHDictionary(String _sHDictionary) {
		sHDictionary = _sHDictionary;
		int L = sDictionary.length();
		iarCeilings = new int[L];
		char[] cHDictionary = new char[L];
		cHDictionary = sHDictionary.toCharArray();
		for (int i = 0; i < L; i++)
			iarCeilings[i] = sDictionary.indexOf(cHDictionary[i]);
		}

	/**
	 * For incrementing through the indices, SampleSizeTree needs to easily
	 * translate between the 'sDictionary' and 'sHDictionary' order.
	 * 
	 * @return order array sHDictionary to sDictionary
	 */
	public int[] getCeilings() {
		return iarCeilings;
	}

	/**
	 * setter of <code>iAsterisk</code> by facet char Designation.
	 * @param _cAsterisk  char facet designation
	 */
	public void setAsterisk(char _cAsterisk){
		cAsterisk = _cAsterisk;
		myTree.setAsterisk(cAsterisk);
	}
	
	/**
	 * Setter for starred facet after change of asterisk position
	 * 
	 * @param _iAsterisk  new position of starred facet in sHDictionary
	 */
	public void setAsterisk(int _iAsterisk) {
		char[] cFacets = sDictionary.toCharArray();
		cAsterisk = cFacets[_iAsterisk];
		for (char c : cFacets)
			getFacet(c).setAsterisk(c == cAsterisk);
	}

	/**
	 * getter of <code>iAsterisk</code> as hierarchical facet order.
	 * @return iAsterisk  facet index of asterisk
	 */
	public char getAsterisk() {
		return cAsterisk;
	}

	/**
	 * getter for hierarchical <code>iAsterisk</code> position.
	 *
	 * @return   hierarchical facet index of asterisk
	 */
	/*public char get_cAsterisk() {
		return cAsterisk;
	}*/
	
	/**
	 * getter for minimal number of replications.
	 * 
	 * @return  iRepMinimum
	 */
	public int get_iMinRep() {
		return iRepMinimum;
	}
	
	/**
	 * getter for Replication range (variance)
	 * 
	 * @return  dRepRange
	 */
	public Double getRepRange() {
		return dRepRange;
	}

	/**
	 * conditional <code>iStep</code> incrementer.
	 * Depending on the <code>iProblem</code> switch:
	 * 		-1:	exit the program
	 * 		 0:	go to the next step
	 * 		greater than 0: go and explain problem
	 */
	public void incrementSteps() {
		if (iStep < -1)
			System.exit(iProblem);
		switch( iProblem) {
			case -1:
				System.exit(iProblem);
				break;
			case 0:
				if (!bDawdle)
					iStep++;
				break;
			default:
				iStep = iResume;
				iProblem = 0;
				break;
		}
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
	
	public void setSubject(Facet _fSubject) {
		fSubject = _fSubject;
	}
	
	/**
	 * Sets designation char of current facet
	 * 
	 * @param iFacetID  ID of current facet
	 * @param cDesignation  new facet designation char
	 */
	public void setFacetDesignation(int iFacetID, char cDesignation) {
		if (iFacetID == 0)
			fSubject.setDesignation(cDesignation);
		else
			farFacets[iFacetID].setDesignation(cDesignation);
	}
	
	/**
	 * Sets name of current facet
	 * 
	 * @param iFacetID  iFacetID  ID of current facet
	 * @param sFacetName  new facet name
	 */
	public void setFacetName(int iFacetID, String sFacetName) {
		if (iFacetID == 0)
			fSubject.setName(sFacetName);
		else			
			farFacets[iFacetID].setName(sFacetName);
	}
	
	/**
	 * Sets nested status of current facet
	 * 
	 * @param iFacetID   iFacetID  ID of current facet
	 * @param bFacetNested new facet status
	 */
	public void setFacetNested(int iFacetID, Boolean bFacetNested) {
		if (iFacetID == 0)
			fSubject.setNested(bFacetNested);
		else
			farFacets[iFacetID].setNested(bFacetNested);
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
		salComments = new ArrayList<>();
		String[] lines = _comments.split("\n");
		for (String line : lines)
			salComments.add(line);
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
	 * method to collect facet information from scripts
	 * 
	 * @param _f  facet to be added
	 */
	public void addFacet(Facet _f) {
		if (falFacets == null)
			falFacets = new ArrayList<Facet>();
		_f.setAsterisk(false);
		falFacets.add(_f);
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

		if ((farFacets == null) || (farFacets.length == 0)) {
			StringBuilder sb = new StringBuilder();
			iFacetCount = falFacets.size();
			farFacets = new Facet[iFacetCount];
			for (int i = 0; i < iFacetCount; i++) {
				Facet f = falFacets.get(i);
				farFacets[i] = f;
				sb.append(f.getDesignation());
			}
			sDictionary = sb.toString();
			falFacets = null;
		}
		Boolean bAsterisk = false;
		char cTarget;
		char cNestor = '$';
		String[] words = _sEffect.trim().split("\\s+");
		String sNest = words[0];
		String[] sss = null;
		int iFirst = 1;
		Integer iLength = words[0].length();
		boolean bPrimary = true;
		if (sNest.indexOf("*") == 0) // indicates starred face;
		{
			bAsterisk = true;
			if (iLength.equals(1)) {
				sNest = words[1];
				iFirst = 2;
			} else {
				sNest = words[0].substring(1, iLength);
			}
			cAsterisk = sNest.toCharArray()[0];
			getFacet(cAsterisk).setAsterisk(true);
		}
		String[] sFacets = sNest.split(":");
		cTarget = sFacets[0].charAt(0);
		if (bAsterisk)
			cAsterisk = cTarget;
		if (sNest.length() > 1) {
			bPrimary = false;
			cNestor = sFacets[1].charAt(0);
		}
		this.getFacet(cTarget).setNested(!bPrimary);
		this.getFacet(cTarget).setNestor(cNestor);
		salNestedNames.add(sNest);
		sbHFO.append(cTarget);
		sHDictionary = sbHFO.toString();
		sss = new String[words.length - iFirst];
		for (Integer i = 0; i < words.length - iFirst; i++)
			sss[i] = words[i + iFirst];
		if (cTarget != cReplicate)
			myTree.addSampleSize(cTarget, sss);
		myTree.setDictionary(sDictionary);
		sHDictionary = sDictionary;
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
		if (sarNestedNames == null || sarNestedNames.length == 0) {
			if ((salNestedNames== null) || (salNestedNames.isEmpty()))
				return null;
			iNestCount = salNestedNames.size();
			sarNestedNames = new String[iNestCount];
			for (int i = 0; i < iNestCount; i++)
				sarNestedNames[i] = salNestedNames.get(i);
		}
		ArrayList<String> result = new ArrayList<String>();
		for (char c : sHDictionary.toCharArray())
			result.add(sarNestedNames[sHDictionary.indexOf(c)]);
		return FXCollections.observableArrayList(result);
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
			myTree.setHDictionary(sHDictionary);
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
		salVarianceComponents.add(new VarianceComponent(this, _line, sPlatform, logger));
	}

	/**
	 * Essential routine to establish the logic of facet organization. Is
	 * called from AnaGroups and SynthGroups after Nesting step
	 */
	public void setOrder() {
		Integer iFacetCount = sHDictionary.length();
		for (Integer i = 0; i < iFacetCount; i++) {
			Facet f = farFacets[i];
			char c = f.getDesignation();
			Integer j = sDictionary.indexOf(c);
			f.setID(j);
			farFacets[j] = f;
		}
		myTree.setDictionary(sDictionary);
		myTree.setFacets(farFacets);
		myTree.setFacetCount(iFacetCount);
		myTree.setNests(sarNestedNames);
	}

	/**
	 * getter for array of all facets.
	 *
	 * @return farFacets  all facets of the study
	 */
	public Facet[] getFacets() {
		return farFacets;
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
		if ((sarNestedNames == null) || (sarNestedNames.length ==0)) {
			int L = salNestedNames.size();
			sarNestedNames = new String[L];
			for (int i = 0; i < L; i++)
			sarNestedNames[i] = salNestedNames.get(i);
		}
		iNestCount = sarNestedNames.length;
		
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
	
	public void createFacets() {
		farFacets = new Facet[iFacetCount];
		farFacets[0] = fSubject;
		for (int i = 1; i < iFacetCount; i++)
			farFacets[i] = new Facet(this);
	}

	/**
	 * Create basic dictionary (in original order of facets)
	 */
	public void createDictionary() {
		StringBuilder sb = new StringBuilder();
		for (Facet f : farFacets)
			sb.append(f.getDesignation());
		sDictionary = sb.toString();
		sHDictionary = sDictionary;
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
		case "cRepMin":
			iRepMinimum = iValue;
		}
	}

	/**
	 * saves intended 'score' mean value from text to Double, (Synthesis)
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
		case "cRepRange":
			dRepRange = dValue;
		default:
			break;
		}
	}

	/**
	 * General purpose variable saving method.
	 *
	 * @param _sType  variable type
	 * @param _sTarget  variable name
	 * @param _sValue  variable value
	 */
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

	/**
	 * This method should probably be in utilities.filer from a logic point of view
	 * but was placed in nests for convenience.
	 * It redacts and formats the prose for G- and D-Studies into the StringBuilder 'sbResult'.
	 *
	 * @param sbResult  StringBuilder, to which the prose has to be added
	 * @throws UnsupportedEncodingException  this is a heuristic for unspecified Exceptions
	 */
	public void formatResults(StringBuilder sbResult) throws UnsupportedEncodingException {

		StringBuilder sb = new StringBuilder();
		//double dAbsolute = 0.0; // total sum of absolute values of weighted
								// components
		//double dFactual = 0.0; // total sum of weighted components > 0.0
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
			logger.warning(e1.getMessage());
		}
		sTitle = " D-Study.";
		for (VarianceComponent vc : salVarianceComponents) {
			vc.doCoefficient(sbResult);
			dTemp = vc.getVarianceComponent() / vc.getDenominator();
			//dAbsolute += Math.abs(dTemp);
			if (dTemp > 0.0) {
				//dFactual += dTemp;
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
			sbResult.append(reCode("Eρ\u00B2      	= " + String.format("%.2f", dRel) + "\n"));
			sbResult.append(reCode("Φ           = " + String.format("%.2f", dAbs) + "\n"));
		} catch (Exception e) {
			logger.warning(e.getMessage());
		}
	}

	/**
	 * getter for Generalizability Coefficient.
	 *
	 * @return dRel  Double value of Generalizability Coefficient
	 */
	public Double getRho() {
		return dRel;
	}

	/**
	 * getter for Index of Reliability.
	 *
	 * @return dAbs  Double value of Index of Reliability
	 */
	public Double getPhi() {
		return dAbs;
	}

	/**
	 * setter for step used in AnaGroups and SynthGroups respectively.
	 *
	 * @param _iStep  step in data entry sequence
	 */
	public void setStep(Integer _iStep) {
		iProblem = 0;
		iStep = _iStep;
	}

	/**
	 * getter of primary Effect name.
	 *
	 * @param iSAR index to string array
	 * @return sarNestedNames[iSAR]  primary effect name
	 */
	public String getNestedName(Integer iSAR) {
		return sarNestedNames[iSAR];
	}
	
	/**
	 * Getter of nested names array
	 * 
	 * @return nested names arry
	 */
	public String[] getNestedNames() {
		return sarNestedNames;
	}

	/**
	 * setter of primary Effect name.
	 *
	 * @param sComp  name
	 * @param iComp  index
	 */
	public void setComponent(String sComp, Integer iComp) {
		sarNestedNames[iComp] = sComp;
	}

	/**
	 * creates a one-dimensional and a two-dimensional, Double array
	 * for the calculation of variance components.
	 *
	 * @param iDim  dimension of array
	 */
	public void createVectors(Integer iDim) {
		dVectors = new Double[iDim][];
		dVC = new Double[iDim];
	}

	/**
	 * setter for variance component.
	 *
	 * @param iPos  index to position in array
	 * @param _dValue of variance component
	 */
	public void set_dVC(Integer iPos, Double _dValue) {
		dVC[iPos] = _dValue;
		bReplicate = true;
	}

	/**
	 * getter of variance component.
	 *
	 * @param iPos   index to position in array
	 * @return value of variance component
	 */
	public Double get_dVC(Integer iPos) {
		return dVC[iPos];
	}

	/**
	 * setter to complete the two dimensional array <code>dVectors</code> by
	 * placing <code>dVector</code> into position <code>iPos</code> of <code>dVectors</code>.
	 *
	 * @param iPos  position in <code>dVectors</code> of <code>dVector</code>
	 * @param dVector  one-dimensional array to be placed
	 */
	public void setVector(Integer iPos, Double[] dVector) {
		dVectors[iPos] = dVector;
	}

	/**
	 * getter of a facet's 'Nestor' facet.
	 *
	 * @param cF  char designation of facet
	 * @return char designation of Nestor facet
	 */
	public Character getNestor(char cF) {
		return getFacet(cF).getNestor();
	}

	/**
	 * getter of variance coefficient array's length.
	 * @return array length
	 */
	public Integer vCCount() {
		return darVarianceCoefficients.length;
	}

	/**
	 * setter of individual variance coefficient in array.
	 *
	 * @param i  position in array
	 * @param _dVC  value of variance coefficient.
	 */
	public void setVariancecoefficient(int i, Double _dVC) {
		if (_dVC == null)
			return;
		if (darVarianceCoefficients == null) {
			darVarianceCoefficients = new Double[myTree.getConfigurationCount()];
		}
		darVarianceCoefficients[i] = _dVC;
	}

	/**
	 * getter of variance coefficient from array.
	 *
	 * @param i position in array
	 * @return Double value of variance coeffient
	 */
	public Double getVarianceCoefficient(int i) {
		if ((darVarianceCoefficients != null) && (i < darVarianceCoefficients.length))
			return darVarianceCoefficients[i];
		else
			return 0.0;
	}

	/**
	 * getter of score ceiling value
	 *
	 * @return iCeiling
	 */
	public Integer getCeiling() {
		return iCeiling;
	}

	/**
	 * getter of score floor value.
	 *
	 * @return iFloor
	 */
	public Integer getFloor() {
		return iFloor;
	}

	/**
	 * getter of intended score mean
	 *
	 * @return Double value
	 */
	public Double getMean() {
		return dMean;
	}

	/**
	 * converts String array list to String array.
	 *
	 * @param _salNestedNames[i++]
	 */
	public void setSarNestedNames(ArrayList<String> _salNestedNames) {
		int i = 0;
		sarNestedNames = new String[_salNestedNames.size()];
		for (String s : _salNestedNames) {
			if ((s == null) | (s.trim() == ""))
				break;
			sarNestedNames[i++] = s;
		}
	}

	/**
	 * getter of <code>sarNestedNames</code>.
	 *
	 * @return sarNestedNames
	 */
	public String[]  getSarNestedNames(){
		return sarNestedNames;
	}

	/**
	 * parser for reading score anchors from script (SynthGroups).
	 *
	 * @param sValue  text line from script
	 */
	public void addAnchors(String sValue) {
		String[] sAnchors = sValue.split("\\s+");
		int iAnchors = sAnchors.length;
		iFloor = Integer.parseInt(sAnchors[0]);
		dMean = Double.parseDouble(sAnchors[1]);
		iCeiling = Integer.parseInt(sAnchors[2]);
		if (iAnchors > 3) {
			iRepMinimum = Integer.parseInt(sAnchors[3]);
			dRepRange = Double.parseDouble(sAnchors[4]);
		}
	}

	/**
	 * parser for reading variance components from script (SynthGroups).
	 *
	 * @param sValue  text line from script
	 */
	public void addVariances(String sValue) {
		String[] sVariances = sValue.split("\\s+");
		int ivCount = sVariances.length;
		darVarianceCoefficients = new Double[ivCount];
		for (int i = 0; i < ivCount; i++)
			darVarianceCoefficients[i] = Double.parseDouble(sVariances[i]);
	}

	/**
	 * creates Double array for variance coefficients.
	 *
	 * @param ivCount  dimension of array
	 */
	public void createVarianceCoefficients(Integer ivCount) {
		darVarianceCoefficients = new Double[ivCount];
	}

	/**
	 * getter of variance coefficient array size.
	 *
	 * @return array size
	 */
	public Integer getVcDim() {
		Integer x = darVarianceCoefficients.length;
		return x;
	}

	/**
	 * kluge pipe to deal with 'Mac' coding idiosyncracy.
	 *
	 * @param sInput input string
	 * @return sOutput  output string
	 * @throws UnsupportedEncodingException  heuristic for undefined exceptions
	 */
	private String reCode(String sInput) throws UnsupportedEncodingException {
		String sOutput = sInput;
		if (sPlatform.equals("Mac")) {
			byte[] buffer = sInput.getBytes("MacGreek");
			sOutput = new String(buffer);
		}
		return sOutput;
	}

	/**
	 * getter for SampleSizeTree.
	 *
	 * @return current SampleSizeTree
	 */
	public SampleSizeTree getTree() {
		return myTree;
	}

	/*
	 * Returns dictionary ordered for purpose of synthesis:
	 * first from crossed to most nested, then according
	 * to data order.
	 *
	 * @return specially ordered sDictionary
	 */
	public String getSynthDictionary() {
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

	/**
	 * Generates facet combinations responsible for variance
	 * components, including their appropriate syntax.
	 * The object is somewhat occult. It gets only called once in
	 * steps.SynthGroups.VarianceComponentsGroup line 887.
	 * It sets up the string array sarNestedNames in Nest and the
	 * 'aNode' array 'nodes' in 'nest'.
	 */
	public void doComponents() {
		myTree.setFacetCount(sDictionary.length());
	}

	/**
	 * tests if String 'sTest' occurs among primary Effects
	 *
	 * @param sTest  String to be tested
	 * @return Boolean test result
	 */
	public Boolean isComponent(String sTest) {
		boolean bContained = false;
			for (String s : sarNestedNames)
				if (s.equals(sTest))
					bContained = true;
		return bContained;
	}

	/**
	 * reorders array of primary nested names (primary Effects).
	 *
	 * @param _sOrder required facet order
	 * @return reordered array
	 */
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
	
	/**
	 * Setter for replication flag.
	 * @param _bReplicate replication flag
	 */
	public void setReplicate(Boolean _bReplicate) {
		bReplicate = _bReplicate;
	}

	/**
	 * Getter for replication flag.
	 * 
	 * @return  replication flag.
	 */
	public Boolean getReplicate() {
		return bReplicate;
	}
	
	/**
	 * processes raw Facets to include nesting logic.
	 */
	public void doNesting() {
		for (String s : sarNestedNames) {
			char cFacet = s.toCharArray()[0];
			Facet f = getFacet(cFacet);
			f.doNesting(s);
		}
	}
	
	/**
	 * Getter for cReplicate
	 * 
	 * @return cReplicate
	 */
	public char get_cRep() {
		return cReplicate;
	}
	
	/**
	 * Setter of termination flag (<code>bAbandon</code>).
	 * 
	 * @param _iProblem  problem identifier in replication
	 */
	public void setProblem(int _iProblem) {
		iProblem = _iProblem;
		bDawdle = false;
	}
	
	/**
	 * Getter of termination flag (<code>iProblem</code>).
	 * 
	 * @return <code>bAbandon</code>
	 */
	public int getProblem() {
		return iProblem;
	}
	
	/**
	 * method adding a facet nest to the list
	 * 
	 * @param _sNest  name of facet nest to be addedd
	 */
	public void addNestedName(String _sNest) {
		salNestedNames.add(_sNest);
	}
	
	/**
	 * Setter for cReplicate
	 * 
	 * @param _cRep  cReplicate
	 */
	public void set_cRep(char _cRep) {
		if(bReplicate)
			cReplicate = _cRep;
	}
	
	/**
	 * Returns designation of replicating facet, or '-' if none.
	 * 
	 * @return  <code>cReplicate</code>, or '-'
	 */
	public char getRepChar() {
		if (bReplicate)
			return cReplicate;
		else
			return '-';
	}
	
	/**
	 * Getter for lowest posible value for number of replications.
	 * 
	 * @return  iRepMinimum
	 */
	public Integer getRepMin() {
		return iRepMinimum;
	};
	
	/**
	 * Setter for number of index columns in data file.
	 * 
	 * @param  _iHighLight  offset afyer index columns
	 */
	public void setHighlight(int _iHighLight) {
		iHighLight = _iHighLight;
	}
	
	/**
	 * Getter  for number of index columns in data file.
	 * 
	 * @return  iHighLight  offset after index columns;
	 */
	public int getHighLight() {
		return iHighLight;
	}
	
	/**
	 * Sets step, at which AnaGroups has to resume after a design problem
	 * 
	 * @param _iResume  set number to resume at.
	 */
	public void setResume(int _iResume) {
		iResume = _iResume;
	}

	/**
	 * Getter for iResume after Problem.
	 * 
	 * @return iResume  Step, at which SynthGroups is to return to after problem explanation
	 */
	public int getResume() {
		return iResume;
	}
	
	/**
	 * Setter for bReNest (whether nesting info is reused)
	 * 
	 * @param _bReNest  boolean flag
	 */
	public void setReNest(Boolean _bReNest) {
		if (!_bReNest) {
			iNestCount = 0;
			sarNestedNames = null;

		}
	}
	
	/**
	 * Debug utility, prints out an int[] array.
	 * @param iArray  any integer array to be printed
	 */
	public void printIntArray(int[] iArray) {
		StringBuilder sb = new StringBuilder(iArray[0]);
		for (int i =1; i < iArray.length; i++)
			sb.append(", " + iArray[i]);
		System.out.println(sb.toString());
	}
	
	public Boolean getStackTraceMode() {
		return bStackTrace;
	}
}

