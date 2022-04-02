package utilities;

import model.Facet;
import model.SampleSizeTree;

public class Factor {
	private SampleSizeTree tree;
	private String sFactor;
	private String[] sarFloors;
	private int iFloors = 0;
	private int[][][] iarSizes;
	private String sDictionary;
	private Facet facet;
	private int iDL;
	private String sFloor;

			
	
	/**
	 * This class encapsulates design factors - factors
	 * within a configuration. It  calculates the absolute size
	 * of this factor, and the indices dependent count for
	 * this factor. That makes it easy to calculate size and count 
	 * for the configuration.
	 */
	
	public Factor (SampleSizeTree _tree, String _sFactor) {
		tree = _tree;
		sDictionary = tree.getDictionary();
		iDL = sDictionary.length();
		sFactor = new StringBuilder(_sFactor).reverse().toString();
		sarFloors = sFactor.split(":");
		iFloors = sarFloors.length;
		iarSizes = new int[iFloors][][];
	}
	
	public int getSize() {
		int iF = -1;
		int[] iProducts = null;
		int iProduct = 1;
		int iSum = 0;
		int iL = 0;
		char cN0 = '$';			// previous Nestor designation
		char cN = '@';				// current Nestor designation
		int iFloor = iFloors - 1;
		sFloor = sarFloors[iFloor];
		iarSizes[iFloor] = new int [iDL][];
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
	
	public int getCount() {
		int iF = -1;
		int[] iProducts = null;
		int iProduct = 1;
		int iSum = 0;
		int iL = 0;
		char cN0 = '$';			// previous Nestor designation
		char cN = '@';				// current Nestor designation
		for (int iFloor = 0; iFloor < iFloors; iFloor++) {
			sFloor = sarFloors[iFloor];
			iarSizes[iFloor] = new int [iDL][];
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
		}
		return iProduct;
	}
}

