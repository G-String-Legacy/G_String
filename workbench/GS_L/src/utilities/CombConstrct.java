package utilities;

import java.util.ArrayList;

import model.Nest;
import model.SampleSizeTree;

/**
 * Class CombConstr generates 4 structural arrays fully describing the design,
 * and passes them to the SampleSizeTree class.
 * 
 * the 4 arrays are:
 * 		iarConfigurations[]
 * 		iarDepths[]
 * 		iarCeilings[]
 * 		iarProducts[][]
 */
public class CombConstrct {

	/**
	 * pointer to <code>Nest</code>
	 */
	private Nest myNest;

	/**
	 * pointer to <code>SampleSizeTree</code>
	 */
	private SampleSizeTree myTree;

	/**
	 * Array of initial nested names
	 */
	private String[] sarNestedNames = null;
	
	/**
	 * Array list of pure facet products
	 */
	private ArrayList<String> salConfigs = null;

	/**
	 * 2-Dim string array of factored configurations
	 */
	String[][] sarProducts = null;
	
	/**
	 * 1-Dim String array of Configurations
	 */
	private String[] sarConfigs = null;
	
	/**
	 * number of configurations (combinations of products
	 */
	private int iConfigurationCount = 0;
	
	/**
	 * constructor
	 * Add all permitted products of nests to salProducts
	 * In order to get all allowed configurations in the
	 * correct sequence, we employ a binary enumeration, 
	 * where each power of 2 corresponds to a specific facet
	 * in the appropriate nesting context.
	 *
	 * @param _nest  pointer to <code>Nest</code>
	 */
	public CombConstrct (Nest _nest){
		myNest = _nest;
		myTree = myNest.getTree();
		sarNestedNames = myNest.getNestedNames();
		salConfigs = new ArrayList<String>(0);
		int iComplexity = sarNestedNames.length;
		int[] iMasks = new int[iComplexity];
		int iTop = 1 << iComplexity;
		int iTemp = 0;
		String sNN = null;
		Boolean bContained = false;
		String sProduct = null;
		String sTemp = null;
		/**
		 * initialize ArrayList salProducts with existing nests
		 */
		for (int i = 0; i < iComplexity; i++) {
			iTemp = (int)(1 << i);
			iMasks[i] = iTemp;
			sTemp = sReverse(sarNestedNames[i]);
			if (sTemp.length() > 1)
				sTemp = "(" + sTemp + ")";
			salConfigs.add(sTemp);
		}

		char cLead = ' ';
		for (int i = 1; i <= iTop; i++) {
			StringBuilder sb = new StringBuilder(0);
			for (int j = 0; j < iComplexity; j++) {
				sNN = sReverse(sarNestedNames[j]);
				cLead = sNN.toCharArray()[0];
				if (((i & iMasks[j]) == iMasks[j]) && (sb.toString().indexOf(cLead) < 0)) {
					if (sNN.length() > 1)
						sNN = "(" + sNN + ")";
					sb.append(sNN);
				}
			}
			sProduct = sb.toString();
			bContained = false;
			for (String s : salConfigs) {
				bContained = (s.indexOf(sProduct) >= 0);
				if (bContained)
					break;
			}
			if (!bContained) {
				salConfigs.add(sProduct);
			}
		}
		sarConfigs = salConfigs.toArray(new String[0]);
		/**
		 * Parse array of strings salProducts into
		 * 2-Dim array of strings sarConfigs.
		 */
		int L = sarConfigs.length;
		sarProducts = new String[L][];
		ArrayList<String> salFactors = null;
		String sP = null;
		char[] cPs = null;
		String s;
		Boolean bComplex = false;
		StringBuilder sb = null;
		for (int i = 0; i < L; i++) {
			salFactors = new ArrayList<String>(0);
			sP = sarConfigs[i];
			cPs = sP.toCharArray();
			bComplex = ((sP.indexOf(':') >= 0) && (sP.indexOf('(') < 0));
			if (bComplex)
				sb = new StringBuilder(0);
			for (char c : cPs) {
				switch (c) {
					case '(': bComplex = true;
						sb = new StringBuilder(0);
						break;
					case ':': sb.append(":");
						break;
					case ')':  bComplex = false;
						salFactors.add("(" + sb.toString() + ")");
						break;
					default: s = String.valueOf(c);
						if (bComplex)
							sb.append(s);
						else
							salFactors.add(s);
				}
			}
			if (salFactors.isEmpty())
				salFactors.add("(" + sb.toString() + ")");
			sarProducts[i] = salFactors.toArray(new String[0]);
		}
		
		iConfigurationCount = sarConfigs.length;
		myTree.setConfigurations(sarConfigs);
		int[] iarDepths = new int[iConfigurationCount];
		String[] sF = null;
		int iProduct = 0;
		for (int i = 0; i < iConfigurationCount; i++) {
			sF = sarProducts[i];
			iProduct = 1;
			for (String sS : sF) {
				iProduct *= getMaxSum (sS);
			}
			iarDepths[i] = iProduct;
		}
		myTree.setConfigurations(sarConfigs);
		myTree.setDepths(iarDepths);
		myTree.setProducts(sarProducts);
	}
	
	/**
	 * auxiliary method, returns maximal state sum of a nested facet from SampleSizeTree.
	 * 
	 * @param _sFac	 nested facet
	 * @return  maximal number of states
	 */
	private int getMaxSum(String _sFac) {
		String s = _sFac.replace("(","").replace(")","");
		char[] cArray = s.toCharArray();
		int L = cArray.length - 1;
		return myTree.getMaxSum(cArray[L]);
	}
	
	/**
	 * reverse string
	 * 
	 * @param s  input string
	 * @return s in reverse order
	 */
	private String sReverse(String s) {
		StringBuilder sb = new StringBuilder(0);
		sb.append(s);
		return sb.reverse().toString();
	}
	
	/**
	 * Getter for number of resulting configurations
	 * 
	 * @return  int number of configurations
	 */
	public int getConfigurationCount() {
		return iConfigurationCount;
	}
}

