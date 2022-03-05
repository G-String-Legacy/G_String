package utilities;

import java.util.ArrayList;
import java.util.BitSet;

import model.Nest;

public class Binary {
	private ArrayList<String> sarConfigs = null;
	private Nest myNest = null;
	private String sDictionary = null;
	char[] cDictionary = null;
	Integer lDictionarySize = 0;
	private String sNestors = null;						// string of all facets chars that are Nestors
														// dictionary position
	private char[] cNestors = null;
	private Integer lConfigurations = 0;				// all possible facet combinations
	private StringBuilder sb = null;
	private String sConfig = null;


	public Binary (Nest _nest){
		myNest = _nest;
		sDictionary = myNest.getHDictionary();
		cDictionary = sDictionary.toCharArray();
		lDictionarySize = cDictionary.length;
		sarConfigs = new ArrayList<String>();
		String sTemp = "";

		// assemble sNestors and iNestors
		sb = new StringBuilder();
		for (char cD : cDictionary){
			sTemp = myNest.getNestor(cD);
			if (sTemp != null) {
				sb.append(sTemp.toCharArray()[0]);
			}
			/*else
				sb.append('_');*/	// temporary for synthesis
		}
		sNestors = sb.toString();
		cNestors = sNestors.toCharArray();

		// now we generate all possible BitSets
		lConfigurations = Integer.valueOf((int)Math.pow( 2, (lDictionarySize - 1)));
		BitSet bsTemp = null;
		sb = new StringBuilder();
		for (long i = 0; i < (2 * lConfigurations); i++){
			bsTemp = Bits.convert(i);
			sb  = new StringBuilder();
			for (int j = 0; j < lDictionarySize; j++){
				if (bsTemp.get(j))
					sb.append(cDictionary[j]);
			}
			if (sb.length() > 0){
				sConfig = sb.toString();
				if ((sarConfigs.indexOf(sConfig) < 0) && isCorrect(sConfig)){
					sConfig = reNest(sConfig);
					System.out.println(sConfig);
					sarConfigs.add(sConfig);
				}
			}
		}
	}

	private Boolean isCorrect(String sTest){
		char[] cTest = sTest.toCharArray();
		for (char cT : cTest){
			char cN = cNestors[sDictionary.indexOf(cT)];
			if ((cN != '_') && (sTest.indexOf(cN) < 0))
				return false;
		}

		return true;
	}

	private String reNest(String sTest){
		char[] cTest = sTest.toCharArray();
		int L = cTest.length;
		sb = new StringBuilder();
		for (int i = 0; i < (L - 1); i++){
			char c = cTest[i];
			if (sNestors.indexOf(c) < 0)
				sb.append(c);
			else
				sb.append(c + ":");
		}
		sb.append(cTest[L - 1]);
		return sb.toString();
	}

	public ArrayList<String> getComponents(){
		return sarConfigs;
	}
}
