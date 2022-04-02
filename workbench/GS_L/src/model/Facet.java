package model;

public class Facet {
	/*
	 *  the class 'facet' encapsulates the various parameters for each facet;
	 *  as well as the various methods required
	 */

	private String sName = null;
	private char cDesignation = ' ';
	private Boolean bIsNested = false;
	private Boolean bAsterisk = false;
	private Integer iColumnOffset = null;			// for auto-indexing, indicates column, where that facet index occurs
	private Integer iOrder = -1;					// hierarchical order of facet in sHDictionary
	
	private char cFacetType = 'x';					/**
													 * 'x' - not specified
													 * 'd' - Differentiation
													 * 's' - Stratification
													 * 'g' - Generalization
													 **/
	private Character cNestor = '$';				/**
													 * the character for the facet in which 
													 * this facet is nested
													 **/
	private int iNestingRank = 0;					// depth of nesting 
	private String sNestees = null;
	private Double dFacetLevel;						// for D - Studies
	private Nest myNest;
	private Boolean bFixed = false;					/**
	 												 * differentiates between random and
	 												 * fixed facets in D Study context.
	 												 **/
	private SampleSizeTree myTree  = null;



	public Facet(Nest _nest)
	/**
	 * Constructor for empty facet
	 */
	
	{
		myNest = _nest;
		myTree = myNest.getTree();
	}

	public Facet (Nest _nest, String _sName, char _cDesignation, Boolean _bIsNested, Integer _iColumnOffset)
	/**
	 * Constructor for defined facet
	 */

	{
		sName = _sName;
		cDesignation = _cDesignation;
		bIsNested = _bIsNested;
		iColumnOffset = _iColumnOffset;
		myNest = _nest;
		iOrder = myNest.getDictionary().indexOf(cDesignation);
		myTree = myNest.getTree();
	}

	public void setName( String _sName)
	/**
	 * Sets facet name, descriptive single word with upper case initial.
	 */
	
	{
		sName = _sName;
	}

	public void setDesignation (char _cDesignation)
	/**
	 * Sets symbolic character designating the facet, usually the lower case initial of the name
	 */
	
	{
		cDesignation = _cDesignation;
	}

	public void setNested( Boolean _bIsNested)
	/**
	 * Boolean value, 'true' if facet nested in another.
	 */
	
	{
		bIsNested = _bIsNested;
	}

	public void setOffset ( Integer _iColumnOffset)
	/**
	 * Optional, if the values are specied in a columnar document
	 */
	
	{
		iColumnOffset = _iColumnOffset;
	}

	public void setID (Integer _id)
	/**
	 * The order in which the facets increment in the data file.
	 */
	
	{
		iOrder = _id;			// sets the hierarchical order of facets
	}

	public void setNestor (char cFacet)
	{
		cNestor = cFacet;
		bIsNested = true;
	}

	public void setFacetType(char _cFacetType)	{

	/**
	 * As defined above in the variable list. The facet type is essential for
	 * the calculation of variance components and G-coefiients.
	 */
	
		cFacetType = _cFacetType;
	}

	public void setAsterisk (Boolean _bAsterisk)
	/**
	 * When the value of the starred facet (boolean true) changes
	 * the data file starts ba new line. !! only one starred facet
	 * per project.
	 */
	
	{
		bAsterisk = _bAsterisk;
	}

	public void setFixed(Boolean _bFixed)
	/**
	 * While facets usually are considered random, they can be held fixed for D-Studies.
	 */
	
	{
		bFixed = _bFixed;
	}

	// Getters
	public String getName()
	/**
	 * Returns facet name
	 */
	
	{
		return sName;
	}

	public char getDesignation()
	/**
	 * Returns facet designation.
	 */
	
	{
		return cDesignation;
	}
	
	public String getDiagDesignation() {
		/**
		 * Returns face designation and Level (mean sample size)
		 */
		
		StringBuilder sb = new StringBuilder("Facet " + cDesignation);
		if(bFixed)
			sb.append(", fixed; level = " + dFacetLevel + ".\n");
		else
			sb.append(", random; level = " + dFacetLevel + ".\n");
		
		return sb.toString();
	}

	public String getDesignationString()
	{
		return String.valueOf(cDesignation);
	}

	public Boolean getNested()
	{
		return bIsNested;
	}

	public Character getNestor()
	{
		return cNestor;
	}

	public Integer getOffset()
	{
		return iColumnOffset;
	}

	public Boolean getFixed()
	{
		return bFixed;
	}

	public Integer getOrder()
	{
		return iOrder;
	}

	public char getFacetType()
	{
		return cFacetType;
	}

	public Double getFacetLevel()
	{
		return dFacetLevel;
	}

	public Boolean starred()
	{
		return bAsterisk;
	}

	public void setFacetLevel()
	{
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
	
	public void setdFacetLevel (Double _dLevel) {
		dFacetLevel = _dLevel;
	}
	
	public Double dGetLevel() {
		return dFacetLevel;
	}
	
 	public void doNesting(String sComponent) {
 		iNestingRank = (sComponent.length() - 1)/2;
		if (iNestingRank > 0) {
			bIsNested = true;
			cNestor = sComponent.toCharArray()[2];
			Facet fNestor = myNest.getFacet(cNestor);
			fNestor.addNestee(cDesignation);
		}
	}
 	
	public Integer getRank()
	{
		return iNestingRank;
	}

	public Boolean isCrossed(){
		return !bIsNested;
	}

	public Boolean isFixed(){
		return bFixed;
	}
	
	public void addNestee(char cNestee) {
		if (sNestees == null)
			sNestees = String.valueOf(cNestee);
		else {
			String s = sNestees;
			sNestees = s + String.valueOf(cNestee);
		}		
	}
	
	public void setDownPointer (int iPointer) {
		/*int iCounter = 0;
		iDownPointer = iPointer;
		Integer[] iSampleSizes = myTree.getSizes(cDesignation);
		for (int i = 0; i < iDownPointer; i++)
			iCounter += iSampleSizes[i];
		iCounter += myTree.getIndex(cDesignation);
		for (char c : sNestees.toCharArray())
			myNest.getFacet(c).setDownPointer(iCounter);*/
		
	}
	
	public char[] getcNestees() {
		if (sNestees == null)
			return null;
		else
			return sNestees.toCharArray();
	}
	
	public String getsNestees() {
		return sNestees;
	}
}
