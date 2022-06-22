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
	private Double[] darOutput = null;

	/**
	 * order of cStarred in original dictionary
	 */
	private int iAsterisk = 0;

	/**
	 * String array list of row indices of final output
	 */
	private ArrayList<String> salCarriageReturn = new ArrayList<>();

	/**
	 * constructor
	 *
	 * @param _nest  pointer to <code>Nest</code>
	 */
	public constructSimulation(Nest _nest) {
		StringBuilder sb = new StringBuilder();
		String sOut = null;
		myNest = _nest;
		myTree = myNest.getTree();
		String sHDictionary = myNest.getHDictionary();
		iConfCount = myTree.getConfigurationCount();
		iAsterisk = myNest.getHAsterisk();
		darSE = new Double[iConfCount];
		for (int i = 0; i < iConfCount; i++)
			darSE[i] = myNest.getVarianceCoefficient(i);
		// set of normally distributed variables x number of nodes
		Double[][] darVector = new Double[iConfCount][];
		// first construct component vectors
		myTree.initCounter();
		for (int iConf = 0; iConf < iConfCount; iConf++) {
			iDim = myTree.getSize(iConf);
			darVector[iConf] = C_Vector(iDim, darSE[iConf]);
		}
		// now assemble the simulation data, first as double array
		int iBase = iConfCount - 1;
		darOutput = darVector[iBase];
		myTree.resetIndices();
		int iRange = 0;
		int iTemp = 0;
		int[] indices = new int [sHDictionary.length()];
		Integer iChange = 0;
		sb = new StringBuilder("0");
		for (int i = 1; i <= iAsterisk; i++)
			sb.append("\t" + "0");
		sOut = sb.toString();
		/**
		 * This is the main working loop. As it assembles the output 'darOutput',
		 * it increments both Facet index sets, and linear Effect indices, using
		 * the method 'increment' in 'SampleSizeTree.
		 */

		do { // increment indices
			indices = myTree.getIndices();
			if (indices[iAsterisk] != iChange) { // new line
				sb = new StringBuilder(sOut);
				sb.append("|" + String.valueOf(iRange));
				salCarriageReturn.add(sb.toString());
				sb = new StringBuilder(String.valueOf(indices[0]));
				for (int i = 1; i <= iAsterisk; i++)
					sb.append("\t" + indices[i]);
				sOut = sb.toString();
				iChange = indices[iAsterisk];
			}
			for (int i = iBase; i >= 0; i--) {
				iTemp = myTree.getCount(i);
				darOutput[iRange] += darVector[i][iTemp];
			}
			myTree.increment();
			iRange = myTree.getCount(iBase);
		} while (iRange > 0);

		sb = new StringBuilder(sOut);
		sb.append("|" + String.valueOf(iRange));
		salCarriageReturn.add(sb.toString());
	}

	/**
	 * Generates a set of 'l' randomly distributed Double values with
	 * a nominal standard distribution of 'vc'. The method returns an
	 * array of 'l' double values with a mean of 0.0 for each 'Effect'.
	 *
	 * @param l  number of states for Effect
	 * @param vc  variance component for Effect
	 * @return  Double array (dim l) of Gaussian pseudo random numbers, mean 0.0, variance vc.
	 */
	private Double[] C_Vector(Integer l, Double vc) {
		Double stdDev = 0.0;
		Double dS = 0.0;
		Double[] vector = new Double[l];
		double dFudgeFactor = 1.00;		// to match synthetic to reverse
		double dSum = 0.0;
		double dSum2 = 0.0;
		double dMean = 0.0;
		double dStd = 0.0;
		// generate vector and sums
		stdDev = Math.sqrt(vc) * dFudgeFactor;
		for (int i = 0; i < l; i++) {
			dS = generateGaussian(0.0, stdDev);
			vector[i] = dS;
			dSum += dS;
			dSum2 += dS * dS;
		}
		if (stdDev == 0.0)
			return vector;

		// Normalization
		dMean = dSum / l;
		dStd = Math.sqrt(dSum2 / l - dMean * dMean) / stdDev;
		for (int i = 0; i < l; i++)
			vector[i] = (vector[i] - dMean) / dStd;
		return vector;
		// create random sample vector
	}

	/**
	 * variable required for random number generator (Marsaglia)
	 */
	private static double spare;

	/**
	 * variable required for random number generator (Marsaglia)
	 */
	private static boolean hasSpare = false;

	/**
	 * Marsaglia polar method
	 * Marsaglia, G.; Bray, T. A. (1964). "A Convenient Method for
	 *   generating Normal Variables". SIAM Review. 6 (3): 260–264
	 *
	 * @param mean  required mean value
	 * @param stdDev  standard deviation parameter
	 * @return  Double random value
	 */
	public static synchronized double generateGaussian(double mean, double stdDev) {
		if (stdDev == 0.0)
			return 0.0;

		if (hasSpare) {
			hasSpare = false;
			return spare * stdDev + mean;
		} else {
			double u, v, s;
			do {
				u = Math.random() * 2 - 1;
				v = Math.random() * 2 - 1;
				s = u * u + v * v;
			} while (s >= 1 || s == 0);
			s = Math.sqrt(-2.0 * Math.log(s) / s);
			spare = v * s;
			hasSpare = true;
			return mean + stdDev * u * s;
		}
	}

	/**
	 * getter of final <code>darOutput</code>
	 *
	 * @return darOutput
	 */
	public Double[] getData() {
		return darOutput;
	}

	/**
	 * getter of row indices
	 *
	 * @return salCarriageReturn
	 */
	public ArrayList<String> getCarriageReturn() {
		return salCarriageReturn;
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
		for (int i = 1; i < l; i++)
			sb.append("\t" + array[i]);
		return sb.toString();
	}
}
