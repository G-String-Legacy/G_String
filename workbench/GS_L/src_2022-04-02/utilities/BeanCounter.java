package utilities;

import java.util.ArrayList;

public class BeanCounter {
	private ArrayList<inTuple> rawData = null;
	private ArrayList<inTuple> tempData = null;
	private ArrayList<inTuple> croppedLong = null;
	private ArrayList<inTuple> croppedShort = null;
	private Integer[] iMask = null;
	private int iMaskLength = 0;
	private int[] iCount = null;
	private int iPointer = 0;
	private int iHeaders = 0;

	public BeanCounter (ArrayList<inTuple> _tData)
	{
		rawData = _tData;
	}

	public void setMask (Integer[] _iMask)
	{
		iMask = _iMask;
		iMaskLength = iMask.length;
		tempData = new ArrayList<inTuple>();

		// trim data to mask
		int itHeader = iHeaders;
		for (inTuple it : rawData)
		{
			if (itHeader-- > 0)
				continue;
			String[] s1 = it.get();
			String[] s2 = new String[iMaskLength];
			for (int i = 0; i < iMaskLength; i++)
				s2[i] = s1[iMask[i]];
			inTuple nT = new inTuple(s2);
			if (!tempData.contains(nT))
				tempData.add(nT);
		}

		// crop data by one intuple element
		croppedLong = new ArrayList<inTuple>();
		croppedShort = new ArrayList<inTuple>();
		for (inTuple it : tempData)
		{
			String[] s1 = it.get();
			String[] s2 = new String[iMaskLength -1];
			for (int i = 0; i < iMaskLength -1; i++)
				s2[i] = s1[i];
			inTuple nT = new inTuple(s2);
			croppedLong.add(nT);
			if (!croppedShort.contains(nT))
				croppedShort.add(nT);
		}

		// now we do histogram
		iCount = new int[croppedShort.size()];
		int iNdex = -1;
		for (int i = 0; i < iCount.length; i++)
			iCount[i] = 0;
		for (inTuple nT : croppedLong)
		{
			iNdex = croppedShort.indexOf(nT);
			iCount[iNdex]++;
		}
	}

	public Integer Dim()
	{
		return iMaskLength;
	}

	public Integer Level()
	{
		return (Integer)iCount[iPointer];
	}

	public String Indices()
	{
		Boolean bFirst = true;
		StringBuilder sb = new StringBuilder();
		String[] sInd = croppedShort.get(iPointer).get();
		if (iMaskLength <3)
			return new String("");
		for (int i = 0; i < iMaskLength - 2; i++)
		{
			if (bFirst)
				sb.append(sInd[i]);
			else
				sb.append(", " + sInd[i]);
			bFirst = false;
		}
		return sb.toString();
	}

	public Boolean next()
	{
		return (iPointer++ < iMaskLength);
	}

	public void setHeaderLines(Integer _iHeaders)
	{
		iHeaders = _iHeaders;
	}

	public Integer getCount()
	{
		return croppedShort.size();
	}

}
