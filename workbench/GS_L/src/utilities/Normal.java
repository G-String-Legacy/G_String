package utilities;

/**
 * Utility attempts to generate set of iDim Double values with a variance of dVariance and a mean of 0.0.
 * It achieves these goals asymptotically, as iDim goes towards infinity
 * 
 * @author ralph
 *
 */
public class Normal {
	
	/*
	 * Number of Double values to generate
	 */
	int iDim = 0;
	
	/**
	 * Target variance of distribution
	 */
	Double dVariance = 0.0;
	
	/**
	 * Target set of iDim pseudo random values
	 */
	Double[] dDistribution = null;
	
	/**
	 * Constructor
	 * 
	 * @param _L  desired number of values
	 * @param _dVar  target variance
	 */
	public Normal(int _L, Double _dVar) {
		iDim = _L;
		dVariance = _dVar;
		
		dDistribution = C_Vector(iDim, dVariance);
	}
	
	/**
	 * Getter of distribution
	 * 
	 * @return  array of iDim pseudo-random Double values
	 */
	public Double[] getDistribution() {
		return dDistribution;
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
	private Double[] C_Vector(int l, Double vc) {
		Double stdDev = 0.0;
		Double dS = 0.0;
		Double[] vector = new Double[l];
		double dSum = 0.0;
		double dSum2 = 0.0;
		double dMean = 0.0;
		double dStd = 0.0;
		// generate vector and sums
		stdDev = Math.sqrt(vc);
		for (int i = 0; i < l; i++) {
			dS = generateGaussian(0.0, stdDev);
			vector[i] = dS;
			dSum += dS;
			dSum2 += dS * dS;
		}
		if (stdDev == 0.0)
			return vector;
		else if (l == 1) {
			vector[0] = 0.0;
			return vector;
		}

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

}
