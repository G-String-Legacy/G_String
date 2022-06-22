package utilities;

import model.Nest;
import model.SampleSizeTree;

/**
 *  <code>CombConstr</code> takes the original nesting constructs ('nests', also called 'Components' and builds
 *  all possible facet combinations that have to be considered for
 *  constructing the variance components ('Effects' called here 'Configurations').
 *     To create all possible combinations a trick is used: consider an integer
 *  represented as a binary number. As we count from 0 to maximum, 
 *  every digit toggles between 0 and 1. In fact, since the count 
 *  goes through all possible numbers, all possible combinations 
 *  of  0 and 1 (or false/true) are covered. If each position (power of 2) 
 *  stands for a particular facet character, this generates all possible
 *  combinations of facets.
 *     The resulting strings of 0 and 1 can then be explored and operated 
 *  upon with Java's bitwise logic operations.
 *     All that is left to do, is to filter out combinations, where there are 
 *  nested facets, but not the facet, in which they are nested.
 *  
 *  The inner class <code>Component</code> is defined within this class, further down.
 * 
 * @see <a href="https://github.com/G-String-Legacy/G_String/blob/main/workbench/GS_L/src/utilities/CompConstrct.java">utilities.CompConstrct</a>
 * @author ralph
 * @version %v..%
 */
public class CombConstrct {
	
	/**
	 * array of <ode>Component</code> see below
	 */
	private Component[] components;
	
	/**
	 * pointer to <code>Nest</code>
	 */
	private Nest myNest;
	
	/**
	 * pointer to <code>SampleSizeTree</code>
	 */
	private SampleSizeTree tree;
	
	/**
	 * string defining configuration of Facet chars and 'colons'
	 */
	private String sConfig;

	/**
	 * constructor
	 * 
	 * @param _nest  pointer to <code>Nest</code>
	 */
	public CombConstrct (Nest _nest){
		myNest = _nest;
		tree = myNest.getTree();
		String sConstruct = null;
		/**
		 * Step 1: read the nesting constructs from the 'NestedNames' in 'nest,'
		 * and set up the required parameters.
		 */
		
		int iCount = 1;
		int iReq = 0;
		int iMask = 0;
		int iLast = -1;
		int iD = 0;
		StringBuilder sb;
		Boolean bChange = false;
		Component tempComponent;
		
		int iNests = myNest.getNestCount();
		String sDictionary = myNest.getSynthDictionary();
			/*
			 * A specially ordered facet string, by depth of nesting first and
			 * and data sequence second.
			 */
		
		int iSize= sDictionary.length();
		int iRange = (int) Math.pow(2, iSize);
		components = new Component[iSize];
		for (int j = 0; j < iNests; j++) {
			sConstruct = myNest.getNestedName(j);	// = 'primary Effect'
			tempComponent = new Component(sConstruct, sDictionary);
			components[tempComponent.getOrder()] = tempComponent;
		}
		
		/*
		 * Step 2: now start counting up the binary integer iCount
		 * until it reaches 2 to the power 'iSize', the number of facets in
		 * a given configuration of facets.
		 * Note: we call the resulting set of nested and crossed facets 'Configurations'
		 * This should be kept in mind. In fact, in urGENOVA terms, they are also called 'EFFECT'.
		 * This could otherwise lead to confusions, since the original or primary  'EFFECTs' in the 
		 * control file are only the original facets, where appropriate in their nested context.
		 * The term 'Configuration' stands for the more general 'EFFECT'.
		 */
		
		while (iCount < iRange) {
			sb = new StringBuilder();
			iReq = 0;
			iMask = 0;
			bChange = false;
			for(Component cmp : components) {
				if ((iCount & cmp.getWeight()) != 0) {
					/**
					 * According to Brennan's convention: starting from left to right,
					 * every time the nesting level increases, a colon (':') is intercalated.
					 */

					iD = cmp.getDepth();
					if (bChange & (iD > iLast))
						sb.append(":");
					iLast = iD;
					bChange = true;
					sb.append(cmp.getFacet());
					iReq |= cmp.getPrerequisite();
					iMask |= cmp.getWeight();
				}
			}
			sb = sb.reverse();
			if ((~iMask & iReq) == 0) {
				sConfig = sb.toString();
				tree.addConfiguration(sConfig);
			}
			iCount++;
		}
		
		for (int i = 0; i <tree.getConfigurationCount(); i++) {
			tree.factorConfigurations(i);	
		}
		tree.consolidateSplits();
		tree.initCounter();
		tree.resetIndices();
	}
	
	public int getComp() {
		return tree.getConfigurationCount();	// could also be called 'Effect count'
	}
}

	/**
	 * An auxiliary class to handle each of the constructs.
	 * 
	 * @author ralph
	 * @version %v..%
	 */
	class Component{
		
		/**
		 * String consisting of ordered facet chars and (possibly) colons, describing the component composition.
		 * also called 'Effect' in Brennan's control file (primary Effects).
		 */
		private String sDescription;
		
		/**
		 * the leading Facet in the combination
		 */
		private char cFacet;	
		
		/**
		 * the order number of leading Facet in the original Facet dictionary <code>sDictionary</code>
		 */
		private int iOrder;
		
		/**
		 * level of nesting
		 */
		private int iDepth;				// the total nesting depth of the component
		
		/**
		 * = 2 ^^ iOrder; power of 2 according to position of cFacet in sDictionary
		 */
		private int iWeight;
		
		/**
		 * the power of 2 for the encompassing facet
		 */
		private int iPrerequisite;
		
		/**
		 * constructor
		 * 
		 * @param _sDescription  structural description of Component
		 * @param _sDictionary  standard, original Facet dictionary
		 */
		public Component(String _sDescription, String _sDictionary) {
			sDescription = _sDescription;
			char[] cFacets = sDescription.toCharArray();
			cFacet = cFacets[0];
			iDepth = 1;
			for (char c : cFacets)
				if (c == ':')
					iDepth ++;
			iOrder = _sDictionary.indexOf(cFacet);
			iWeight = (int) Math.pow(2,  iOrder);
			iPrerequisite = 0;
			if (iDepth > 1)
				iPrerequisite = (int) Math.pow(2,  _sDictionary.indexOf(cFacets[2]));
		}
			
		/**
		 * getter
		 * 
		 * @return iWeight
		 */
		public int getWeight() {
			return iWeight;
		}
		
		/**
		 * getter
		 * 
		 * @return sDescription
		 */
		public String getDescription() {
			return sDescription;
		}
		
		/**
		 * getter
		 * 
		 * @return iDepth
		 */
		public int getDepth() {
			return iDepth;
		}
		
		/**
		 * getter
		 * 
		 * @return iPrerequisite
		 */
		public int getPrerequisite() {
			return iPrerequisite;
		}
		
		/**
		 * getter
		 * 
		 * @return cFacet as String
		 */
		public String getFacet() {
			return String.valueOf(cFacet);
		}
		
		/**
		 * getter
		 * 
		 * @return iOrder
		 */
		public int getOrder() {
			return iOrder;
		}
}


