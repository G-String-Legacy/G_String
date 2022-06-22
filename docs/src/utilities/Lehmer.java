package utilities;

/**
 *  Method to generate deterministic, "non"-repetitive sequence of digits using the Lehmer Random Number generator,
 *  (https://en.wikipedia.org/wiki/Lehmer_random_number_generator, ZX81)
 *  The purpose is to introduce a reliable one-digit signature to synthetic data sets at the least significant digit.
 *  Depending on the Lehmer result, it will either add or subtract one unit, so that the data is even, when the Lehmer number
 *  is even, resp. vice verso. Lehmer is used in SynthGroups.saveDataFile and AnaGroups.saveAll.
 *
 *  There are five methods:
 *  	1. Generation and initialization, when the class is first called,
 *  	2. an Iteration step adjusting each generated simulated data item,
 *  	3. Test, counts the number of times the signature is violated,
 *  	4. Summarize, exports the total number of items and signature violations.
 *  	5. Even, a boolean based on Lehmer's method
 * 
 * @see <a href="https://github.com/G-String-Legacy/G_String/blob/main/workbench/GS_L/src/utilities/Lehmer.java">utilities.Lehmer</a>
 * @author ralph
 * @version %v..%
 */
public class Lehmer {
/*
 *  Method to generate deterministic, "non"-repetitive sequence of digits using the Lehmer Random Number generator,
 *  (https://en.wikipedia.org/wiki/Lehmer_random_number_generator, ZX81)
 *  The purpose is to introduce a reliable one-digit signature to synthetic data sets at the least significant digit.
 *  Depending on the Lehmer result, it will either add or subtract one unit, so that the data is even, when the Lehmer number
 *  is even, and vice verso. Lehmer is used in SynthGroups.saveDataFile (~line 1226) and AnaGroups.saveAll
 *  (~line 1250).
 *
 *  There are five methods:
 *  	1. Generation and initialization, when the class is first called,
 *  	2. an Iteration step adjusting each generated simulated data item,
 *  	3. Test, counts the number of times the signature is violated,
 *  	4. Summarize, exports the total number of items and signature violations.
 *  	5. Even, a boolean based on Lehmer's method
 */
	private Integer m = 65537;	// ZX81
	private Integer a = 16807;	// ZX81
	private Integer	X = 1;
	private Integer iMin = 0;
	private Integer iMax = 0;
	private Double dMin = 0.0;
	private Double dMax = 0.0;
	private Integer iCount = 0;
	private Boolean bSign = false;

	public Lehmer(int _min, int _max){
		// null generator/initializer
		iMin = _min;
		iMax = _max;
		bSign = (iMax - iMin > 4);	// don't sign data sets with low discrimination
		// to prevent signing, comment out above
	}

	public Lehmer(Double _dMin, Double _dMax){
		dMin = _dMin;
		dMax = _dMax;
	}

	public int adjust(int data_raw){
		Integer data_adjusted = data_raw;
		Boolean dataEven = (data_raw % 2 == 0);
		Boolean bLehmer = Even();
		if (bSign && (dataEven != bLehmer))
			if (bLehmer && (data_raw > iMin))
				data_adjusted -= 1;
			else if (data_raw < iMax)
				data_adjusted += 1;
		return data_adjusted;
	}

	public Integer Summarize(){
		return iCount;
	}

	public void Test(Double data){
		Boolean bEven = Even();
		Double dMod = data % 2.0;
		if (data.equals(dMin) || data.equals(dMax))
			return;
		if (dMod.equals(0.0) != bEven)
			iCount++;
	}

	private Boolean Even(){
		// Lehmer calculation returning even/odd binary
		X = (X * a) % m;
		return ((X % 2) == 0);
	}
}
