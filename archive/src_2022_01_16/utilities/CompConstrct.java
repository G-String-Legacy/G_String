package utilities;

import java.util.ArrayList;

import model.Facet;
import model.Nest;

public class CompConstrct {
	/*
	 *  'CompConstr' replaces old 'Binary'. Its purpose is to construct
	 *  a String array of all possible crossed products of nested facets.
	 */
	private ArrayList<String> sarConfigs = null;
	private Nest myNest = null;
	private ArrayList<vcNode> vcNodes = new ArrayList<vcNode>();

	public CompConstrct (Nest _nest){
		myNest = _nest;
		String sCrossed = null;		// string of crossed facets
		StringBuilder sb = new StringBuilder();
		sarConfigs = new ArrayList<String>();

		Facet[] far = myNest.getFacets();
		int iNests = myNest.getNestCount();
		String sNests[] = new String[iNests];

		for (int i = 0; i < iNests; i++)
			sNests[i] = myNest.getNestedName(i);
		for (Facet f : far) {
			if (f.isCrossed())
					sb.append(f.getDesignation());
		}
		sCrossed = sb.toString();
		char[] cCrossed = sCrossed.toCharArray();
		int iLast = 0;
		vcNode vcNewNode = null;
		for (char c : cCrossed){
			//System.out.println(c);
			for (String vNest: sNests){
				iLast = vNest.length() - 1;
				if (vNest.charAt(iLast) == c){
					vcNewNode = new vcNode(vNest, c);
					vcNodes.add(vcNewNode);
				}
			}
			vcNode vn = null;
			int nodeCount = vcNodes.size();
			for (int i = 0; i < nodeCount; i++) {
				vn = vcNodes.get(i);
				if (!vn.contains(c)) {
					vcNode vnn = vn.clone();
					vnn.addFactor(vcNewNode.getStructure(), c);
					vcNodes.add(vnn);
				}
			}
		}

	}

	public ArrayList<String> getComponents(){
		for (vcNode vn:vcNodes)
			sarConfigs.add(vn.getStructure());

		return sarConfigs;
	}

}
