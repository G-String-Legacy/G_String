package utilities;

public class inTuple {
	/**
	 * 'inTuple' is a helper object. It packages long data strings,
	 * being read in by 'Filer'.
	 */
	
	private Integer iDim = 0;
	private String[] tuple = null;

	public inTuple(String[] sColumns) {
		iDim = sColumns.length;
		tuple = new String[iDim];
		Integer i = 0;
		for (String s : sColumns) {
			if ((sColumns[i] == "") || (sColumns[i] == null))
				tuple[i] = null;
			else {
				tuple[i] = s.trim();
			}
			i++;
		}
	}
}