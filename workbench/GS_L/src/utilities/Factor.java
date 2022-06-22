package utilities;

import model.Facet;
import model.SampleSizeTree;

/**
 * This class encapsulates design factors ('factors')
 * within a configuration. It  calculates the absolute size, i.e. the number of states
 * of this factor. That makes it easier to calculate size for the whole configuration.
 * Factor is used extensively in SampleSizeTree.
 *
 * @see <a href="https://github.com/G-String-Legacy/G_String/blob/main/workbench/GS_L/src/utilities/Factor.java">utilities.Factor</a>
 * @author ralph
 * @version %v..%
 */
public class Factor {
	/**
	 * pointer to <code>SampleSizeTree</code>
	 */
	private SampleSizeTree tree;

	/**
	 * String Description of <code>Factor</code> in terms of Facet chars and 'colons'
	 */
	private String sFactor;

	/**
	 * Factor section split by colons expressed as string of Facet chars
	 */
	private String sFloor;

	/**
	 * String array of <code>sFloor</code>s
	 */
	private String[] sarFloors;

	/**
	 * index to <code>sarFloors</code>
	 */
	private int iFloors = 0;

	/**
	 * array of sample sizes
	 */
	private int[][][] iarSizes;

	/**
	 * pointer to original Facet dictionary
	 */
	private String sDictionary;

	/**
	 * <code>Facet</code> variable
	 */
	private Facet facet;

	/**
	 * This class encapsulates design factors ('factors')
	 * within a configuration. It  calculates the absolute size, i.e. the number of states
	 * of this factor. That makes it easier to calculate size for the whole configuration.
	 * Factor is used extensively in SampleSizeTree.
	 *
	 * @param _tree  pointer to the SampleSizeTree
	 * @param _sFactor string showing the configuration of the factor
	 */
	 public Factor (SampleSizeTree _tree, String _sFactor) {
		tree = _tree;
		sDictionary = tree.getDictionary();
		sFactor = new StringBuilder(_sFactor).reverse().toString();
		sarFloors = sFactor.split(":");
		iFloors = sarFloors.length;
	}

	/**
	 * Critical method:
	 * It is trivial, if a factor corresponds to a 'primary Effect',
	 * then it is simply equal to the cumulative sample size of the deepest facet.
	 * However, if facets, nested under the same 'Nestor', are crossed, it
	 * becomes somewhat more involved.
	 *
	 * @return int cumulative smple size
	 */
	public int getSize() {
		int iF = -1;
		int[] iProducts = null;
		int iProduct = 1;
		int iSum = 0;
		int iL = 0;
		char cN0 = '$';			// previous Nestor designation
		char cN = '@';			// current Nestor designation
		int iFloor = iFloors - 1;
		sFloor = sarFloors[iFloor];
		iProduct = 1;
		for (char cF : sFloor.toCharArray()) {
			iF = sDictionary.indexOf(cF);
			facet = tree.getFacet(cF);
			cN = facet.getNestor();
			if (iFloor == 0) {
				iarSizes[iFloor][iF] = tree.getSizes(cF).clone();
				iProduct = iarSizes[iFloor][iF][0];
			} else if(cN == cN0) {
				int[] iarFactors = tree.getSizes(cF);
				for (int i = 0; i < iL; i++)
					iProducts[i] *= iarFactors[i];
			} else {
				if ((cN != '$') && (iProducts != null)) {
					iSum = 0;
					for (int i : iProducts)
						iSum += i;
					iProduct *= iSum;
				}
				iL = tree.getDim(cN);
				iProducts = (tree.getSizes(cF)).clone();
				cN0 = cN;
			}
		}
		iSum = 0;
		if (iProducts != null) {
			for (int i : iProducts)
				iSum += i;
			iProduct *= iSum;
		}
		return iProduct;
	}
}

