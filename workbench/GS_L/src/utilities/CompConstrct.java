package utilities;

import model.Nest;
import model.SampleSizeTree;

/**
 *  <code>CompConstr</code> takes the original nesting constructs and builds
 *  all possible facet combinations that have to be considered for
 *  constructing the variance components ('Effects').
 *     To create all possible combination a trick is used: consider an integer
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
public class CompConstrct {
	
	private Component[] components;
	private Nest myNest;
	private SampleSizeTree tree;
	private String sConfig;

	public CompConstrct (Nest _nest){
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
	
	class Component{
		/**
		 * An auxiliary object to handle each of the constructs
		 */
		
		private String sDescription;		// a descriptive string
		private char facet;				// a char for the innermost facet
		private int order;				// the final order of components by depth and data sequence
		private int depth;				// the total nesting depth of the component
		private int weight;				// the power of 2 for the innermost facet
		private int prerequisite;		// the power of 2 for the encompassing facet
		
		public Component(String _sDescription, String _sDictionary) {
			sDescription = _sDescription;
			char[] facets = sDescription.toCharArray();
			facet = facets[0];
			depth = 1;
			for (char c : facets)
				if (c == ':')
					depth ++;
			order = _sDictionary.indexOf(facet);
			weight = (int) Math.pow(2,  order);
			prerequisite = 0;
			if (depth > 1)
				prerequisite = (int) Math.pow(2,  _sDictionary.indexOf(facets[2]));
		}
		
		public int getWeight() {
			return weight;
		}
		
		public String getDescription() {
			return sDescription;
		}
		
		public int getDepth() {
			return depth;
		}
		
		public int getPrerequisite() {
			return prerequisite;
		}
		
		public String getFacet() {
			return String.valueOf(facet);
		}
		
		public int getOrder() {
			return order;
		}
}


