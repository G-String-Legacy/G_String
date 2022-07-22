package model;

/**
 *  the class 'Facet' encapsulates the various parameters for each facet;
 *  as well as the various methods required
 *
 * @see <a href="https://github.com/G-String-Legacy/G_String/blob/main/workbench/GS_L/src/model/Facet.java">model.Facet</a>
 * @author ralph
 * @version %v..%
 */
public class Facet {
	/**
	 * <code>sName</code> descriptive Name of facet.
	 */
	private String sName = null;

	/**
	 * <code>cDesignation</code> formal, symbolic name of facet
	 */
	private char cDesignation = ' ';

	/**
	 * Boolean, indicating whether facet is nested or crossed
	 */
	private Boolean bIsNested = false;

	/**
	 * Boolean exclusive, marking the facet for which a carriage return
	 * in the data file occurs.
	 */
	private Boolean bAsterisk = false;

	/**
	 * Hierarchical order of facet in sHDictionary
	 */
	private Integer iOrder = -1;

	/**
	 * 'x' - not specified
	 * 'd' - Differentiation
	 * 's' - Stratification
	 * 'g' - Generalization
	 * 'f' - Fixed
	 */
	private char cFacetType = 'x';

	/**
	 * the designation char for the facet in which
	 * this facet is nested. The default is '$'
	 * which indicates a crossed facets, i.e.
	 * facets without a 'Nestor'
	 */
	private Character cNestor = '$';

	/**
	 * depth of nesting
	 */
	private int iNestingRank = 0;

	/**
	 * 'Nestees' are the facets nested in the current facet.
	 * Since multiple facets can be nested within the current
	 * facet, they form a string of facet designations (char)
	 */
	private String sNestees = null;

	/**
	 * 'Average' sample size for variance coefficients calculation.
	 */
	private Double dFacetLevel;

	/**
	 * Pointer to 'Nest' for access to design parameters stored.
	 */
	private Nest myNest;

	/**
	 * For D-Studies, differentiates between 'Fixed' and 'Random' facets.
	 */
	private Boolean bFixed = false;

	/**
	 * Pointer to 'SampleSizeTree' for access to sample size data.
	 */
	private SampleSizeTree myTree  = null;

	/**
	 * Constructor for empty facet
	 *
	 * @param _nest;
	 */
	public Facet(Nest _nest){
		myNest = _nest;
		myTree = myNest.getTree();
	}

	/**
	 * Constructor for defined facet.
	 *
	 * @param _nest;
	 * @param _sName;
	 * @param _cDesignation;
	 * @param _bIsNested;
	 */
	public Facet (Nest _nest, String _sName, char _cDesignation, Boolean _bIsNested) {
		sName = _sName;
		cDesignation = _cDesignation;
		bIsNested = _bIsNested;
		myNest = _nest;
		iOrder = myNest.getDictionary().indexOf(cDesignation);
		myTree = myNest.getTree();
	}

	/**
	 * Setter for facet name, descriptive single word with upper case initial.
	 *
	 * @param _sName;
	 */
	public void setName( String _sName) {
		sName = _sName;
	}

	/**
	 * Setter for symbolic character designating the facet, usually the lower case initial of the name.
	 *
	 * @param _cDesignation;
	 */
	public void setDesignation (char _cDesignation) {
		cDesignation = _cDesignation;
	}

	/**
	 * Setter for 'isNested', 'true' if facet nested in another.
	 *
	 * @param _bIsNested;
	 */
	public void setNested( Boolean _bIsNested) {
		bIsNested = _bIsNested;
	}

	/**
	 * Setter for the hierarchical order in which the facets increment
	 * in the data file, and it is in 'sHDictionary'.
	 *
	 * @param _id;
	 */
	public void setID (Integer _id) {
		iOrder = _id;			// sets the hierarchical order of facets
	}

	/**
	 * Setter of 'Nestor', i.e. the facet in which the current
	 * facet is nested.
	 *
	 * @param cFacet;
	 */
	public void setNestor (char cFacet) {
		cNestor = cFacet;
		bIsNested = true;
	}

	/**
	 * Setter of 'facetType', defined above in the variable list. The facet type is essential for
	 * the calculation of variance components and G-coefficients.
	 *
	 * @param _cFacetType;
	 */
	public void setFacetType(char _cFacetType) {
		cFacetType = _cFacetType;
	}

	/**
	 * Setter of bAsterisk.
	 *
	 * @param _bAsterisk;
	 */
	public void setAsterisk (Boolean _bAsterisk) {
		bAsterisk = _bAsterisk;
	}

	/**
	 * Setter for bFixed.
	 *
	 * @param _bFixed;
	 */
	public void setFixed(Boolean _bFixed) {
		bFixed = _bFixed;
	}

	/**
	 * Getter for facet name.
	 *
	 * @return sName;
	 */
	public String getName() {
		return sName;
	}

	/**
	 * Returns facet designation.
	 *
	 * @return cDesignation;
	 */
	public char getDesignation() {
		return cDesignation;
	}

	/**
	 * Getter for facet designation, whether it is fixed or random,
	 * and Level (average sample size).
	 *
	 * @return sb.toString;
	 */
	public String getDiagDesignation() {
		StringBuilder sb = new StringBuilder("Facet " + cDesignation);
		if(bFixed)
			sb.append(", fixed; level = " + dFacetLevel + ".\n");
		else
			sb.append(", random; level = " + dFacetLevel + ".\n");

		return sb.toString();
	}

	/**
	 * Getter of cDesignation as String.
	 *
	 * @return String.valueOf(cDesignation);
	 */
	public String getDesignationString() {
		return String.valueOf(cDesignation);
	}

	/**
	 * Getter of 'nested status'.
	 *
	 * @return bIsNested;
	 */
	public Boolean getNested()

	{
		return bIsNested;
	}

	/**
	 * Getter of 'Nestor'.
	 *
	 * @return cNestor;
	 */
	public Character getNestor()

	{
		return cNestor;
	}

	/**
	 * Getter of 'Fixed' status.
	 *
	 * @return bFixed;
	 */
	public Boolean getFixed() {
		return bFixed;
	}

	/**
	 * Getter for facet order within sHDirectory.
	 *
	 * @return iOrder;
	 */
	public Integer getOrder() {
		return iOrder;
	}

	/**
	 * Getter for facet type.

	 * @return cFacetType;
	 */
	public char getFacetType() {
		return cFacetType;
	}

	public Double getFacetLevel()
	{
		return dFacetLevel;
	}

	/**
	 * Getter for presence of facet asterisk.
	 *
	 * @return bAsterisk;
	 */
	public Boolean starred() {
		return bAsterisk;
	}

	/**
	 * Calculates levels for G studies for facets of generalization:
	 * simple mean of sample sizes for 1 level of nesting, harmoniv mean
	 * for higher levels of nesting.
	 */
	public void setFacetLevel() {
		if (myTree == null)
			myTree = myNest.getTree();
		switch (iNestingRank) {
			case -1:		// not g-type facet
				dFacetLevel = 1.0;
				break;
			case 0:			// simple, un-nested g-type facet
			case 1:			// first order, nested g-type facet
				dFacetLevel = myTree.getLevel(iOrder);
				break;
			default:		// higher order, nested g-type facet
				dFacetLevel = myTree.getHarmonic(iOrder);
				break;
		}
	}

	/**
	 * Setter of FacetLevel (Double).
	 *
	 * @param _dLevel;
	 */
	public void setdFacetLevel (Double _dLevel) {
		dFacetLevel = _dLevel;
	}

	/**
	 * Getter of FacetLevel (Double).
	 *
	 * @return dFacetLevel;
	 */
	public Double dGetLevel() {
		return dFacetLevel;
	}

	/**
	 * Sets up nesting configuration based on the nesting input,
	 * this method determines the nestor/nestees relationships
	 *
	 * @param sComponent;
	 */
 	public void doNesting(String sComponent) {
 		/**
 		 * based on the nesting input, this method determines the
 		 * nestor/nestees relationships
 		 */

 		iNestingRank = (sComponent.length() - 1)/2;
		if (iNestingRank > 0) {
			bIsNested = true;
			cNestor = sComponent.toCharArray()[2];
			Facet fNestor = myNest.getFacet(cNestor);
			fNestor.addNestee(cDesignation);
		}
	}

 	/**
 	 * Getter for rank in nesting hierarchy.
 	 *
 	 * @return iNestingRank;
 	 */
	public Integer getRank()
	{
		return iNestingRank;
	}

	/**
	 * Getter for 'crossed' status.
	 *
	 * @return !bIsNested;
	 */
	public Boolean isCrossed(){
		return !bIsNested;
	}

	/**
	 * Getter for 'Fixed' status.
	 *
	 * @return bFixed;
	 */
	public Boolean isFixed(){
		return bFixed;
	}

	/**
	 * adds 'Nestee', facets nested in the current facet.
	 *
	 * @param cNestee;
	 */
	public void addNestee(char cNestee) {
		if (sNestees == null)
			sNestees = String.valueOf(cNestee);
		else if (sNestees.indexOf(cNestee) > -1)
			return;
		else {
			String s = sNestees;
			sNestees = s + String.valueOf(cNestee);
		}
	}

	/**
	 * getter for 'Nestees' as char[].
	 *
	 * @return sNestees.toCharArray();
	 */
	public char[] getcNestees() {
		if (sNestees == null)
			return null;
		else
			return sNestees.toCharArray();
	}

	/**
	 * getter for 'Nestees' as String.
	 *
	 * @return sNestees;
	 */

	public String getsNestees() {
		return sNestees;
	}
}
