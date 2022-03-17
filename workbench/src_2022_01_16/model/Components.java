package model;

import java.util.ArrayList;
import utilities.CompConstrct;

public class Components {
	/**
	 * encapsulates facets and facet combinations responsible for variance
	 * components
	 */

	private Integer iCompCount = 0; // number of components
	private ArrayList<String> salNestedNames = null; // designation of													// components array list
	private Nest myNest = null;

	public Components(Nest _Nest) {
		// iFacet here in original, not hierarchical order
		aNode node = null;
		myNest = _Nest;
		myNest.resetIndices();
		CompConstrct cc = new CompConstrct(myNest);
		salNestedNames = cc.getComponents();
		myNest.setSarNestedNames(salNestedNames);
		iCompCount = salNestedNames.size();
		for (int i = 0; i < iCompCount; i++) {
			node = new aNode(myNest, salNestedNames.get(i));
			myNest.addNode(node);
		}
		myNest.setCompCount(iCompCount);
		myNest.setSarNestedNames(salNestedNames);
	}

	public void checkVarianceCollectionComplete() {
		Boolean bComplete = true;
		for (Integer i = 0; i < iCompCount; i++)
			if (myNest.get_dVC(i) == null)
				bComplete = false;
		if (bComplete)
			myNest.setVarianceDawdle(false); // variance components complete, OK
												// to continue
	}
}
