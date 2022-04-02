package utilities;

import model.SampleSizeTree;

//import javafx.application.Platform;
//import javafx.scene.control.Control;
import javafx.scene.control.TextField;
import javafx.scene.text.Font;

public class SampleSizeView extends TextField {
	/*
	 * Clever TextField Checks for proper values and transfers changed values
	 * int SampleSizeTree
	 */

	private Integer iLevel = 0;
	private Integer iSampleSize = 0;
	private SampleSizeTree myTree = null;
	private Boolean bChanged = false;
	private char cFacet = ' ';
	private Integer iPointer = -1;
	private Integer[] iIndices = null;

	public SampleSizeView(SampleSizeTree _myTree, char _cFacet, Integer _iFacet, Integer _iCounter,
			Integer _iSampleSize, Integer[] _indices) {
		this.setMinWidth(80);
		this.setPrefHeight(30);
		this.autosize();
		this.setFont(Font.font("SanSerif", 16));
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
				myTree.setChanged(true); // flag change
				bChanged = true;
			} else if (!newText.matches("\\d{0,6}")) // is not 6 digit integer
				this.setText(oldText);
			else // correct format
				myTree.hasChanged(iFacet, iCounter, Integer.parseInt(newText));
		});
	}

	public void setValue(String _value) {
		if ((_value == null) || _value.trim().equals("")) {
			this.setText("");
			iSampleSize = null;
		} else if (_value.matches("\\d{0,3}")) {
			this.setText(_value);
			iSampleSize = Integer.parseInt(_value);
		}
	}

	public void setPointer(Integer _iPointer) {
		iPointer = _iPointer;
	}

	public Integer getPointer() {
		return iPointer;
	}

	public Integer getValue() {
		return iSampleSize;
	}

	public Boolean hasChanged() {
		return bChanged;
	}

	public Integer[] getIndices() {
		return iIndices;
	}

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

	public String getNest() {
		return myTree.getNest(cFacet);
	}
}
