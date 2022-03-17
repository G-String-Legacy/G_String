package model;

import java.util.ArrayList;

public class aNode {
/*
 *  All in one structure and method for nested component.
 *  An aNode may have zero or more SubFactor aNoded and zero or one Descendant aNodes.
 */

	private Nest myNest = null;
	private SampleSizeTree tree = null;
	private String sComponent = null;
	private aNode aSubFactors[] = null;
	private aNode aDescendant = null;
	private Facet aFacet = null;
	private String[] sFactors;
	private int iFactors = 0;
	private String sActive = null;
	private Integer[]   iSubLevels = null; // number of children at next level
	private Integer[]   iSubSums = null; // iterative sum of children on a level
	private Integer iDepth = 0;
	private Integer iIndex = null;
	private Integer iPrevious = -1;		// previous index
	private char cFacet;
	private int iComplexity;

	public aNode(Nest _nest, String _sComponent){
		myNest = _nest;
		tree = myNest.getTree();
		//sHDictionary = myNest.getHDictionary();
		sComponent = _sComponent;
		alert("Start");
		// first we analyse the component
		sFactors = sComponent.split(" x ");
		iFactors = sFactors.length;
		sActive = sFactors[iFactors - 1];	// structure of active node
		int iSubFactors = iFactors - 2;
		ArrayList<aNode> sarFactors = new ArrayList<aNode>();
		if (iFactors > 1){
			alert("   " + "Subfactors start");
			// this is not simply a cascading nest, create factor nodes
			aSubFactors = new aNode[iSubFactors];
			for (int i = 0; i < iFactors - 1; i++ ){
				sarFactors.add( new aNode(myNest, sFactors[i]));
			}
			aSubFactors = sarFactors.toArray(aSubFactors);
			alert("    " + "End of Subfactors");
		}
		String sFacets[] = sActive.split(":");
		iDepth = sFacets.length;
		cFacet = sFacets[iDepth - 1].charAt(0);
		aFacet = myNest.getFacet(cFacet);
		iIndex = myNest.getIndex(cFacet);
		Integer[] iSampleSizes = tree.getSizes(aFacet.getDesignation());
		int ls = iSampleSizes.length;
		iSubLevels = new Integer[ls + 1];
		iSubSums = new Integer[ls + 1];
		int iCumulator = 0;
		for (int i = 0; i < ls; i++){
			iSubLevels[i] = iSampleSizes[i];
			iSubSums[i] = iCumulator;
			iCumulator += iSubLevels[i];
		}
		iSubSums[ls] = iCumulator;
		if (iDepth > 1)		// if more than one facet in this factor, create subnode
		{
			StringBuilder sb = new StringBuilder(sFacets[0]);
			for (int i = 1; i < iDepth - 1; i++)
				sb.append(":" + sFacets[i]);
			alert("     Descendant");
			aDescendant = new aNode(myNest, sb.toString());
		}
		iComplexity = 1;
		if (aDescendant != null)
			iComplexity += aDescendant.getComplexity();
		if (aSubFactors != null)
			for (aNode aSF : aSubFactors)
				iComplexity+= aSF.getComplexity();

	}

	public String getConfiguration(){
		return sComponent;
	}

	public Integer getDepth(){
		// gives total number of items in component distribution
		Integer iDepth = 0;
		if (aDescendant == null) {
			int l = iSubSums.length - 1;
			iDepth = iSubSums[l];
		}else
			iDepth = aDescendant.getDepth();
		if (aSubFactors != null){
			int l = aSubFactors.length;
			for  (int i = 0; i < l; i++)
				iDepth *= aSubFactors[i].getDepth();
		}
		return iDepth;
	}

	public Integer getCount(Integer iGroup) {
		// returns position in respective distribution for a given combination of indices
		// iGroup pass on the current Nestor index
		Integer iReturn = 0;
		Integer iMultiplier = 1;
		Integer iSubCount = 0;
		iIndex = myNest.getIndex(cFacet);

		// Step 0: get subFactors
		if (aSubFactors != null){
			for (aNode aSF : aSubFactors){
				iSubCount += iMultiplier * aSF.getCount(0);
				iMultiplier *= aSF.getDepth();
			}
		}

		// Step 1 : get sDescendant
		if (iIndex > 0){
			//dumpArray("Get Count: ", myNest.getIndices());
			//System.out.println("Facet: " + Character.toString(cFacet));

		}

		Integer iPointer = iSubSums[iGroup] + iIndex;
		if (aDescendant != null)
			iReturn = aDescendant.getCount(iPointer);
		else {
			iReturn = iPointer;
			//System.out.println("iReturn = " + iReturn.toString());
		}

		// Step 3 final
		iReturn = iReturn * iMultiplier + iSubCount;
		if (iReturn > 3959)
			System.out.println("Stop");
		return iReturn;
	}

	public Boolean increment(Integer _iPointer)	{
		// handles iterative incrementing by the "rule of 3"
		Boolean bIncrement = true;
		Integer Index0 = iIndex;
		Integer iPointer = 0;
		// step 0: aSubFactors
		iPointer = iSubSums[_iPointer] + iIndex;
		if (aSubFactors != null){
			for (aNode aSF : aSubFactors)
				if (bIncrement)
					bIncrement = aSF.increment(0);
		}
		// step 1: aDescendant
		if (aDescendant != null){
			if (bIncrement)
				bIncrement = aDescendant.increment(iPointer);
		}
		// step 2: Node proper
		if (bIncrement){
			if (iPointer.equals(0)){
				if (iIndex >= iSubLevels[0] -1){
					iIndex = 0;
					bIncrement = true;
				}
				else {
					iIndex++;
					//System.out.println(aFacet.toString() + ":   " + iIndex);
					bIncrement = false;
				}
			} else {
				if (iIndex >= iSubLevels[_iPointer] - 1){
					iIndex = 0;
					bIncrement = true;
				}
				else {
					iIndex++;
					bIncrement = false;
				}
			}

		}
		if (!iIndex.equals(Index0)){
			myNest.setIndex(cFacet, iIndex);
		}
		return bIncrement;
	}

	private void alert(String sMessage){
		//System.out.println(sComponent + ": " + sMessage);
	}

	public Integer[] getIndices(){
		// assembles indices for component using "Rule of 3 steps"
		Integer iarWorking[] = null;
		int iLength = 0;
		// first calculate length of indices array
		// Step 0
		if (aSubFactors != null)
			for (aNode aSF : aSubFactors)
				iLength += aSF.getComplexity();

		// Step 1
		if (aDescendant != null)
			iLength += aDescendant.getComplexity();

		// Step 3
		iLength++;

		// now we assemble indices again by "Rule of 3 steps"
		iarWorking = new Integer[iLength];
		//Step 0
		int iCounter = 0;
		if (aSubFactors != null)
			for (aNode aSF : aSubFactors)
				for (int i : aSF.getIndices())
					iarWorking[iCounter++] = i;

		//Step 1
		if (aDescendant != null)
			for (int i : aDescendant.getIndices())
				iarWorking[iCounter++] = i;

		//Step 2
		iarWorking[iCounter] = iIndex;
		//dumpArray(sComponent, iarWorking);
		return iarWorking;
	}

	/*private void dumpArray(String sTitle, Integer[] iIndices){
		StringBuilder sb = new StringBuilder(sTitle + ": " + iIndices[0].toString());
		for (int i = 1; i < iIndices.length; i++)
			sb.append(", " + iIndices[i].toString());
		System.out.println(sb.toString());
	}*/

	public int getComplexity(){
		return iComplexity;
	}

	public Boolean hasChanged() {
		// checks if an index has changed in component
		Boolean bChanged = false;
		if (aSubFactors != null)
			for (aNode aSF : aSubFactors)
				bChanged = bChanged || aSF.hasChanged();
		if (iIndex != iPrevious) {
			iPrevious = iIndex;
			bChanged = true;
		}
		if (aDescendant != null)
			bChanged = bChanged || aDescendant.hasChanged();
		return bChanged;
	}
}
