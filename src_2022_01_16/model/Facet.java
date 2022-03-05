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
	private Integer[] iNestors = null;				// hierarchical index to parent facet (in ialSample ...
	private Integer[] iNestees = null;				// hierarchical indices to child facets
	private Integer iNestingRank = null;			// for g-type facets: max level of nesting
	private char cFacetType = 'x';					// type of facet:
													/**
													 * 'x' - not specified
													 * 'd' - Differentiation
													 * 's' - Stratification
													 * 'g' - Generalization
													 **/
	private Double dFacetLevel;					// for D - Studies
	private Nest myNest = null;
	private Boolean bFixed = false;					// differentiates between random and
													// fixed facets in D Study context
	private SampleSizeTree myTree  = null;


	//Constructors
	public Facet(Nest _nest)
	{
		// Dummy constructor
		myNest = _nest;
	}

	public Facet (Nest _nest, String _sName, char _cDesignation, Boolean _bIsNested, Integer _iColumnOffset)
	{
		sName = _sName;
		cDesignation = _cDesignation;
		bIsNested = _bIsNested;
		iColumnOffset = _iColumnOffset;
		myNest = _nest;
		iOrder = myNest.getDictionary().indexOf(cDesignation);
		myTree = myNest.getTree();
	}

	//Setters
	public void setName( String _sName)
	{
		sName = _sName;
	}

	public void setDesignation (char _cDesignation)
	{
		cDesignation = _cDesignation;
	}

	public void setNested( Boolean _bIsNested)
	{
		bIsNested = _bIsNested;
	}

	public void setOffset ( Integer _iColumnOffset)
	{
		iColumnOffset = _iColumnOffset;
	}

	public void setID (Integer _id)
	{
		iOrder = _id;			// sets the hierarchical order of facets
	}

	public void setNestors (Integer[] _iNestors)
	{
		iNestors = _iNestors;
	}

	public void setNestees(Integer[] _iNestees)
	{
		iNestees = _iNestees;
	}

	public void setFacetType(char _cFacetType)
	{
		String cTypes = "xdsgrf";
		if (cTypes.indexOf(_cFacetType) >= 0)
			cFacetType = _cFacetType;
		else
		{
			System.out.println("Facet type out of bounds.");
			System.exit(30);
		}
	}

	public void setAsterisk (Boolean _bAsterisk)
	{
		bAsterisk = _bAsterisk;
	}

	public void setFixed(Boolean _bFixed)
	{
		bFixed = _bFixed;
	}

	// Getters
	public String getName()
	{
		return sName;
	}

	public char getDesignation()
	{
		return cDesignation;
	}

	public String getDesignationString()
	{
		return String.valueOf(cDesignation);
	}

	public Boolean getNested()
	{
		return bIsNested;
	}

	public Integer[] getNestors()
	{
		return iNestors;
	}

	public char[] getCNestors() {
		if (iNestors == null)
			return null;
		int L = iNestors.length;
		char[] cNestors = new char[L];
		char[] cDictionary = myNest.getDictionary().toCharArray();
		for (int i = 0; i < L; i++)
			cNestors[i] = cDictionary[iNestors[i]];
		return cNestors;
	}

	public Integer getOffset()
	{
		return iColumnOffset;
	}

	public Boolean getFixed()
	{
		return bFixed;
	}

	public Integer[] getNestees()
	{
		return iNestees;
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
		doNestingRank();
		switch (iNestingRank) {
			case -1:		// not g-type facet
				dFacetLevel = 1.0;
				myNest.log("Facet '" + cDesignation +"'", "non g-type");
				break;
			case 0:			// simple, un-nested g-type facet
			case 1:			// first order, nested g-type facet
				dFacetLevel = myTree.getLevel(iOrder);
				myNest.log("Facet '" + cDesignation +"'", "simple average");
				break;
			default:		// higher order, nested g-type facet
				dFacetLevel = myTree.getHarmonic(iOrder);
				myNest.log("Facet '" + cDesignation +"'", "harmonic mean");
				break;
		}
	}

	public void setFacetLevel (Double _dLevel)
	{
		dFacetLevel = _dLevel;
	}

	public Double dGetLevel()
	{
		return dFacetLevel;
	}

	public void doNestingRank()
	{
		// for determining appropriate averaging method
		iNestingRank = -1;
		if (cFacetType == 'g')		// applies only to g-type facets
		{
			if (iNestors == null)
				iNestingRank = 0;
			else switch (iNestors.length)
			{
				case 1:			// there is only one g-type Nestor > rank = g-type Nestor.rank
					iNestingRank = myNest.getHFacet(iNestors[0]).getRank() + 1;
					//if ()
					break;
				default:		// max rank of all g-type Nestors + 1
					iNestingRank = 0;
					for (Integer iN : iNestors)
					{
						Integer iNR = myNest.getHFacet(iN).getRank() + 1;
						if ( iNR > iNestingRank)
							iNestingRank = iNR;
					}
					break;
			}
			myNest.log("Facet '" + cDesignation +"'", "Rank: " + iNestingRank);
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
}
