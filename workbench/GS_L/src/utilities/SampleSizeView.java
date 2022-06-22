package utilities;

import javafx.scene.control.TextField;
import javafx.scene.text.Font;
import model.SampleSizeTree;

/**
 * 'SampleSizeView' (ssv) is a specialized text field for the display and modification
 * of sample size values in the 'SampleSizeTree', both for the construction of
 * the SampleSize page (Called by both AnaGroups (step 8) and SynthGroups (step 6).
 *
 * @see <a href="https://github.com/G-String-Legacy/G_String/blob/main/workbench/GS_L/src/utilities/SampleSizeView.java">utilities.SampleSizeView</a>
 * @author ralph
 * @version %v..%
 */
public class SampleSizeView extends TextField {
	private Integer iLevel = 0;
	private Integer iSampleSize = 0;
	private SampleSizeTree myTree = null;
	private Boolean bChanged = false;
	private char cFacet = ' ';
	private Integer[] iIndices = null;

	/**
	 * constructor
	 *
	 * @param _myTree  pointer to <code>Tree</code>
	 * @param _cFacet  Facet designation char
	 * @param _iFacet  order of Facet in original dictionary <code>sDictionary</code>
	 * @param _iCounter Integer indicating which value in sample size series for Facet
	 * @param _iSampleSize previous value
	 * @param _indices Integer array template to localize an effect specific mean square in the calculation
	 */
	public SampleSizeView(SampleSizeTree _myTree, char _cFacet, Integer _iFacet, Integer _iCounter,
		Integer _iSampleSize, Integer[] _indices) {
		this.setMinWidth(80);
		this.setPrefHeight(30);
		this.autosize();
		this.setFont(Font.font("SansSerif", 16));
		Integer iCounter = _iCounter;
		Integer iFacet = _iFacet;
		myTree = _myTree;
		iSampleSize = _iSampleSize;
		iLevel = _indices.length;
		iIndices = new Integer[iLevel];
		// char cFacet = _cFacet;
		for (Integer i = 0; i < iLevel; i++)
			iIndices[i] = _indices[i];
		this.setPrefWidth(50);
		if (iSampleSize == null)
			this.setText(null);
		else
			this.setText(iSampleSize.toString());
		this.textProperty().addListener((obs, oldText, newText) -> {
			if ((newText == null) || (newText.trim().equals(""))) {
				bChanged = true;
			} else if (!newText.matches("\\d{0,6}")) // is not 6 digit integer
				this.setText(oldText);
			else // correct format
				myTree.hasChanged(iFacet, iCounter, Integer.parseInt(newText));
		});
	}

	/**
	 * setter of sample size
	 *
	 * @param _value value of sample size
	 */
	public void setValue(String _value) {
		if ((_value == null) || _value.trim().equals("")) {
			this.setText("");
			iSampleSize = null;
		} else if (_value.matches("\\d{0,3}")) {
			this.setText(_value);
			iSampleSize = Integer.parseInt(_value);
		}
	}

	/**
	 * getter of sample size
	 *
	 * @return
	 */
	public Integer getValue() {
		return iSampleSize;
	}

	/**
	 * boolean flag if associated sample size has changed value
	 *
	 * @return true/false - changed/unchanged
	 */
	public Boolean hasChanged() {
		return bChanged;
	}

	/**
	 * getter of array template to localize an effect specific mean square in the calculation
	 *
	 * @return Integer[]
	 */
	public Integer[] getIndices() {
		return iIndices;
	}

	/**
	 * getter of IndexString, i.e. index array formatted as text String.
	 * @return
	 */
	public String getIndexString() {
		StringBuilder sb = new StringBuilder();
		Integer iLength = iIndices.length;
		for (Integer i = iLength - 2; i > 0; i--)
			if (sb.length() == 0)
				sb.append(iIndices[i]);
			else
				sb.append(", " + iIndices[i] + 1);
		return sb.toString();
	}


	/**
	 * getter of <code>Nest</code>
	 * @return
	 */
	public String getNest() {
		return myTree.getNest(cFacet);
	}
}
