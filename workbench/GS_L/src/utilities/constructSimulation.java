package utilities;

import java.util.ArrayList;

import model.Nest;
import model.SampleSizeTree;

/**
 * 'constructSimulation' (cS) builds the synthetic data according to
 * the ANOVA random model.
 *   On the basis of the specified facets and their nesting, the program
 * has identified the potential sources of variability (variance components)
 * in terms of crossed and nested facets, as well as due to their interactions
 * (see CompConstrct), expressed as strings, designated 'Configurations'
 * - the general 'Effects' of the model.
 * For each configuration (i.e.'Effect') it then gets the number of possible states
 * (from 'SampleSizeTree.getSize), over the allowed range of facet indices.
 * For each configuration it generates 'synthetic error' values, distributed
 * according to the variance component corresponding to its configuration. It then
 * steps through the indices in the correct order, adding all the component error values
 * according to the appropriate indices. The resulting array of double numbers (darOutput)
 * is distributed symmetrically around 0.0 with the appropriate compound variance.
 * On the side, 'constructSimulation also prepares a stringArrayList (salCarriageReturn),
 * providing the formated indices for each line of data values. Both, darOutput and
 * salCarriageReturn are being passed to SynthGroups.saveSynthetics.
 *
 * @see <a href="https://github.com/G-String-Legacy/G_String/blob/main/workbench/GS_L/src/utilities/constructSimulation.java">utilities.constructSimulation</a>
 * @author ralph
 * @version %v..%
 */
public class constructSimulation {

	/**
	 * pointer to <code>Nest</code>
	 */
	private Nest myNest = null;

	/**
	 * pointer to <code>SampleSizeTree</code>
	 */
	private SampleSizeTree myTree = null;

	/**
	 * counts configurations (Effects), as they are created
	 */
	private int iConfCount = 0;

	/**
	 * number of individual states an Effect can assume, as the facets step through their sample sizes
	 */
	private int iDim = 0;

	/**
	 * Double array of variance components per Effect
	 */
	private Double[] darSE = null;

	/**
	 * Double Array of final score values minus grand mean
	 */
	private Double[][] DarScores = null;

	/**
	 * order of cStarred in original dictionary
	 */
	private char cAsterisk = 0;

	/**
	 * String array list of row indices of final output
	 */
	private String[] sarCarriageReturns = null;
	
	/**
	 * Counter of data lines
	 */
	private int iLineCount = 0;

	/**
	 * constructor
	 *
	 * @param _nest  pointer to <code>Nest</code>
	 */
	public constructSimulation(Nest _nest) {
		StringBuilder sb = new StringBuilder();
		myNest = _nest;
		myTree = myNest.getTree();
		cAsterisk = myNest.getAsterisk();
		int L = myTree.getDepth(cAsterisk) + 2;
		DarScores = new Double[L][];
		sarCarriageReturns = new String[L];
		String sHDictionary = myNest.getHDictionary();
		iConfCount = myTree.getConfigurationCount();
		int iAsterisk = sHDictionary.indexOf(cAsterisk);
		darSE = new Double[iConfCount];
		for (int i = 0; i < iConfCount; i++)
			darSE[i] = myNest.getVarianceCoefficient(i);
		// set of normally distributed variables x number of nodes
		Double[][] darVector = new Double[iConfCount][];
		// first construct component vectors
		myTree.initCounter();
		int[] iDepths = new int[iConfCount];
		for (int iConf = 0; iConf < iConfCount; iConf++) {
			iDim = myTree.getDepth(iConf)+ 1;
			iDepths[iConf] = iDim;
			Normal norm = new Normal(iDim, darSE[iConf]);
			darVector[iConf] = norm.getDistribution();
		}
		//System.out.println(dumpArray(iDepths));
		// now assemble the simulation data, first as double array
		int iBase = iConfCount - 1;
		Double DarOutput = 0.0;
		myTree.resetIndices();
		int iTemp = 0;
		int oLineCount = 0;
		ArrayList<Double> DalScoreLine = new ArrayList<Double>(0);
		int[] indices = myTree.getIndices().clone();
		int[] oIndices = myTree.getIndices().clone();
		int iPrevious  = 0;
		/**
		 * This is the main working loop. As it assembles the output 'darOutput',
		 * it increments both Facet index sets, and linear Effect indices, using
		 * the method 'increment' in 'SampleSizeTree.
		 */
		try {
			do { 									// increment indices
				indices = myTree.getIndices();
				if ((indices[iAsterisk] != iPrevious)) { // new line
					try {
						iPrevious = indices[iAsterisk];
						sb = new StringBuilder(0);
						sb.append(String.valueOf(oLineCount) + '|');
						for (int i = 0; i <= iAsterisk; i++)
							sb.append("\t" + oIndices[i]);
						sarCarriageReturns[iLineCount] = sb.toString();
						DarScores[iLineCount] = DalScoreLine.toArray(new Double[0]);
						DalScoreLine = new ArrayList<Double>(0);
						iLineCount ++;
						oIndices = indices.clone();
					} catch(Exception e) {
						e.printStackTrace();
					}
				}
				try {
					DarOutput = 0.0;
					for (int i = iBase - 1; i >= 0; i--) {
						iTemp = myTree.getCount(i);
						DarOutput += darVector[i][iTemp];
					}
				} catch(Exception e) {
					e.printStackTrace();
				}
				if (DarOutput == null)
					break;
				DalScoreLine.add(DarOutput);
			}	while((iPrevious < L) && myTree.increment());
		} catch(Exception e) {
			e.printStackTrace();
		}
		sb = new StringBuilder(0);
		sb.append(String.valueOf(oLineCount) + '|');
		for (int i = 0; i <= iAsterisk; i++)
			sb.append("\t" + oIndices[i]);
		sarCarriageReturns[iLineCount] = sb.toString();
		DarScores[iLineCount] = DalScoreLine.toArray(new Double[0]);
	}

	/**
	 * getter of final <code>darOutput</code>
	 *
	 * @return darOutput
	 */
	public Double[][] getData() {
		return DarScores;
	}

	/**
	 * getter of row indices
	 *
	 * @return salCarriageReturn
	 */
	public String[] getCarriageReturn() {
		return sarCarriageReturns;
	}
	
	/**
	 * Getter for lineCount.
	 * 
	 * @return  total number of lines
	 */
	public int getlineCount() {
		return iLineCount;
	}

	/**
	 * utility to convert int array to text string
	 *
	 * @param array arbitrary int array
	 * @return formatted text
	 */
	public String dumpArray (int[] array){
		int l = array.length;
		StringBuilder sb = new StringBuilder(array[0]);
		for (int i = 0; i < l; i++)
			sb.append("\t" + array[i]);
		return sb.toString();
	}

	/**
	 * utility to convert double array to text string
	 *
	 * @param array arbitrary double array
	 * @return formatted text
	 */
	public String dumpArray (Double[] array){
		int l = array.length;
		StringBuilder sb = new StringBuilder(0);
		sb.append(array[0]);
		for (int i = 0; i < l; i++)
			sb.append("\t" + array[i]);
		return sb.toString();
	}

	
}
