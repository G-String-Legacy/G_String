package utilities;

import java.util.ArrayList;

import model.Nest;
import model.aNode;

public class constructSimulation {
	private Nest myNest = null;
	private Integer iCompCount = 0;
	private Integer iDim = 0;
	private Double[] darSE = null;
	private aNode[] nodes = null;
	private Double[] darOutput = null;
	private int iAsterisk = 0;

	private ArrayList<String> sarCarriageReturn = new ArrayList<String>();

	public constructSimulation(Nest _nest) {
		StringBuilder sb = new StringBuilder();
		String sOut = null;
		myNest = _nest;
		String sHDictionary = myNest.getHDictionary();
		iCompCount = myNest.getCompCount();
		nodes = new aNode[iCompCount];
		iAsterisk = myNest.getHAsterisk();
		for (int iNode = 0; iNode < iCompCount; iNode++)
			nodes[iNode] = myNest.getNode(iNode);
		darSE = new Double[iCompCount];
		for (int i = 0; i < iCompCount; i++)
			darSE[i] = myNest.getVarianceCoefficient(i);
		// set of normally distributed variables x number of nodes
		Double[][] darVector = new Double[iCompCount][];
		// first construct component vectors
		for (int iComp = 0; iComp < iCompCount; iComp++) {
			//System.out.println("Loop: " + iComp + " < " + iCompCount);
			aNode n = myNest.getNode(iComp);
			iDim = n.getDepth();
			darVector[iComp] = C_Vector(iDim, darSE[iComp]);
		}

		// now assemble the simulation data, first as double array
		int iBase = iCompCount - 1;
		darOutput = darVector[iBase];
		Boolean bContinue = true;
		aNode incrementer = nodes[iBase];
		myNest.resetIndices();
		Integer iRange = 0;
		Integer iTemp = 0;
		Integer[] indices = new Integer [sHDictionary.length()];
		myNest.resetIndices();
		Integer iChange = 0;
		sb = new StringBuilder("0");
		for (int i = 1; i <= iAsterisk; i++)
			sb.append("\t" + "0");
		sOut = sb.toString();
		do { // increment indices
			indices = myNest.getIndices();
			//System.out.println("Indices: " + dumpArray(indices));
			if (indices[iAsterisk] != iChange) { // new line
				sb = new StringBuilder(sOut);
				sb.append("|" + iRange.toString());
				sarCarriageReturn.add(sb.toString());
				sb = new StringBuilder(indices[0].toString());
				for (int i = 1; i <= iAsterisk; i++)
					sb.append("\t" + indices[i]);
				sOut = sb.toString();
				iChange = indices[iAsterisk];
			}
			iRange = nodes[iBase].getCount(0);
			for (Integer iNode = iBase - 1; iNode >= 0; iNode--) {
					iTemp = nodes[iNode].getCount(0);
					darOutput[iRange] += darVector[iNode][iTemp];
			}
			bContinue = !incrementer.increment(0);
		} while (bContinue);
		sb = new StringBuilder(sOut);
		sb.append("|" + iRange.toString());
		sarCarriageReturn.add(sb.toString());
		//System.out.println(sOut);
	}

	private Double[] C_Vector(Integer l, Double vc) {
		// Stub for synthesizing G_String data file

		Double stdDev = 0.0;
		Double dS = 0.0;
		Double[] vector = new Double[l];
		Double dFudgeFactor = 1.00;		// to match synthetic to reverse
		Double dSum = 0.0;
		Double dSum2 = 0.0;
		Double dMean = 0.0;
		Double dStd = 0.0;
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

	private static double spare;
	private static boolean hasSpare = false;

	public static synchronized double generateGaussian(double mean, double stdDev) {
		// Marsaglia polar method
		// Marsaglia, G.; Bray, T. A. (1964). "A Convenient Method for
		// Generating Normal Variables". SIAM Review. 6 (3): 260–264

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

	public Double[] getData() {
		return darOutput;
	}

	public ArrayList<String> getCarriageReturn() {
		return sarCarriageReturn;
	}

	public String dumpArray (Integer[] array){
		int l = array.length;
		StringBuilder sb = new StringBuilder(array[0]);
		for (int i = 1; i < l; i++)
			sb.append("\t" + array[i]);
		return sb.toString();
	}
}
