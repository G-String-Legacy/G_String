package utilities;

public class inTuple {
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

	public String[] get() {
		return tuple;
	}

	public Integer size() {
		return iDim;
	}

	public String get(Integer _index) {
		if (_index >= iDim)
			throw new RuntimeException();
		return tuple[_index];
	}

	@Override
	public String toString() {
		Boolean bFirst = true;
		StringBuilder sb = new StringBuilder();
		for (String s : this.tuple) {
			if (bFirst)
				sb.append(s);
			else
				sb.append(", " + s);
			bFirst = false;
		}
		return sb.toString();
	}

	@Override
	public boolean equals(Object o) {
		if (o == null)
			return false;
		String s1 = o.toString();
		String s2 = this.toString();
		if (s1.equals(s2))
			return true;
		else
			return false;
	}

}