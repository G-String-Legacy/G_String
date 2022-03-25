package utilities;

import java.util.ArrayList;

/*
 *  Checks is an auxiliary variable of the type [true, true, false, true,...]
 *  for comparing nested names independent independent of facet order
 */

public class Checks {
	Boolean[] bChecks = null;
	Integer iLength = 0;

	public Checks(Boolean[] _bChecks) {						// formal constructor
		bChecks = _bChecks;
		iLength = _bChecks.length;
	}

	public Checks (String _sDictionary, String _sComponent){	// practical constructor

		iLength = _sDictionary.length();
		bChecks = new Boolean[iLength];

		for (Integer i = 0; i < iLength; i++)
			bChecks[i] = (_sComponent.indexOf(_sDictionary.toCharArray()[i]) >= 0);

	}

	public Boolean[] get() {							// return Boolean array of checks
		return bChecks;
	}

	public Integer length()								// returns number of Booleans in array
	{
		return iLength;
	}

	public Boolean query(Integer iPos) {				// returns Boolean value at position iPos
		return bChecks[iPos];
	}

	public Boolean Complete()							// checks if all facets are contained
	{
		Boolean bReturn = true;
		for (Boolean b : bChecks) {
			if (!b) {
				//System.out.print("false ");
				//System.out.println();
				bReturn = false;
			} //else
				//System.out.print("true ");
		}
		return bReturn;
	}

	public Boolean equals( String _sDictionary, Checks xOther) {		// compares itself with another Checks
		if (_sDictionary.length() != iLength) {
			System.out.println("Unequal Dictionary length");
			System.exit(200);
		}
		Boolean bReturn = true;
			for (Integer i = 0; i< iLength; i++)
				if (bChecks[i] != xOther.query(i))
					bReturn = false;
		return bReturn;
	}

	public Boolean isContained (String _sDictionary, ArrayList<Checks> _balChecks){
		Boolean bReturn = false;
			for (Checks bc : _balChecks){
				if (this.equals(_sDictionary, bc))
					bReturn = true;
			}
		return bReturn;
	}

	public String print(){
		StringBuilder sb = new StringBuilder();
		for (Boolean b:bChecks)
			if(b)
				sb.append('+');
			else
				sb.append('_');
		return sb.toString();
	}

}
