package model;

import java.util.ArrayList;
import java.util.prefs.Preferences;
import utilities.Filer;
import utilities.Popup;
import utilities.SampleSizeView;
import javafx.application.Platform;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.SplitPane;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

public class SampleSizeTree {

	/*
	 * The SampleSizeTree object contains the whole structure of the sample
	 * sizes for both crossed and nested facets
	 */

	private Nest myNest = null;
	private String sDictionary = null;
	// contains the facet dictionary in the original order
	private String sHDictionary;
	// contains the facet dictionary in hierarchical order
	private String[] sarNests = null;
	// contains a nest string for each facet in the original order
	private Boolean[] barCrossed = null;
	// contains a simple array of booleans in original facet order (crossed =
	// true)
	// contains index of starred facet in hierarchical order
	private Integer iLength = 0;
	private Integer[] iPointers = null;
	private Integer[] iRows = null;
	// array arranging ial's in hierarchical order
	private Integer[] iIndices = null;
	private Facet[] facets = null;
	private Integer iRow = 0;
	private char cPrevious = ' ';
	private Boolean bChanged = false;
	private Boolean bDoOver = false;
	// section related to updating sample sizes on processing the ssv page
	private ArrayList<SampleSizeView> ssvAL = null;
	private Integer iarSizes[][];
	private Integer iarSums[][];
	private utilities.Popup popup;

	// variables for index calculation
	private Facet[] farFacets = null;
	private Integer iFacetCount = 0;
	private String sStyle_20 = null;
	private String sStyle_18 = null;
	private String sStyle_16 = null;
	private Preferences prefs = null;
	private char cAsterisk;

	// Constructor
	public SampleSizeTree(Nest _nest, String _Dictionary, Popup _popup, Preferences _prefs) {
		// Constructor initializes SampleSizeTree according to original
		// dictionary
		sDictionary = _Dictionary;
		Integer iFacets = sDictionary.length();
		iarSizes = new Integer[100][];
		iarSums = new Integer[100][];
		barCrossed = new Boolean[100];
		facets = new Facet[iFacets];
		myNest = _nest;
		popup = _popup;
		popup.setObject(8, 19);
		prefs = _prefs;
		sStyle_16 = prefs.get("Style_16",
				"-fx-font-size: 16px; -fx-font-family: \"ARIAL\"; -fx-padding: 5; -fx-background-color: #805015; -fx-text-fill: #FFFFFF;");
		sStyle_18 = prefs.get("Style_18",
				"-fx-font-size: 18px; -fx-font-family: \"ARIAL\"; -fx-padding: 10; -fx-background-color: #805015; -fx-text-fill: #FFFFFF;");
		sStyle_20 = prefs.get("Style_20",
				"-fx-font-size: 30px; -fx-font-family: \"ARIALSerif\"; -fx-padding: 10; -fx-background-color: #805015; -fx-text-fill: #FFFFFF;");
	}

	// Setters (public)
	public void setHDictionary(String _HDictionary) {
		sHDictionary = _HDictionary;
	}

	public void addSampleSize(Integer iComp, Integer[] sss) {
		Integer iSum = 0;
		Integer iSize = sss.length;
		Integer iCount = 0;
		iRow = iComp;
		iarSizes[iRow] = new Integer[iSize];
		iarSums[iRow] = new Integer[iSize + 1];
		for (Integer iS : sss) {
			iarSizes[iRow][iCount] = iS;
			iarSums[iRow][iCount] = iSum;
			iSum += iS;
			iCount++;
		}
		iarSums[iRow][iCount] = iSum;
	}

	public void addSampleSize(char _c, String[] sss) {
		// alternative (for doOver) sample size adder
		Integer iSum = 0;
		Integer iSS = 0;
		Integer iSize = sss.length;
		Integer iCount = 0;
		iRow = sDictionary.indexOf(_c);
		if (_c == cPrevious)
			return;
		iarSizes[iRow] = new Integer[iSize];
		iarSums[iRow] = new Integer[iSize + 1];
		for (String s : sss) {
			iSS = Integer.parseInt(s);
			iarSizes[iRow][iCount] = iSS;
			iarSums[iRow][iCount] = iSum;
			iSum += iSS;
			iCount++;
		}
		iarSums[iRow][iCount] = iSum;
		cPrevious = _c;
		iRow++;
	}

	public void addSample(Integer i, Integer[] iSize, Integer[] iSum) {
		if (i < iFacetCount)
			i = sDictionary.indexOf(sHDictionary.toCharArray()[i]);
		iarSizes[i] = iSize;
		iarSums[i] = iSum;
	}

	public void setFacet(Facet _facet) {
		char c = _facet.getDesignation();
		Integer index = sDictionary.indexOf(c);
		if (index >= 0) {
			facets[index] = _facet;
			barCrossed[index] = !_facet.getNested();
		}
	}

	public void setCrossed(char _c, Boolean _crossed) {
		// sets balCrossed element for facet _c
		char c = _c;
		Integer index = sDictionary.indexOf(c);
		barCrossed[index] = _crossed;
	}

	public void setNests(String[] nested) {
		// sets Nests
		sarNests = nested;
	}

	public void setChanged(Boolean _bChanged) {
		bChanged = _bChanged;
	}

	// Getters (public)

	public VBox getPage(Integer _iSample) {
		/*
		 * composes sample size page for a facet within a nest.
		 */
		Facet fC = null;
		VBox vOuter = new VBox();
		vOuter.setMinHeight(800);
		vOuter.setAlignment(Pos.TOP_CENTER);
		HBox vPara = new HBox();
		VBox hbLeft = null;
		VBox hbRight = null;
		FlowPane fp = null;
		SplitPane sp = null;
		vPara.setPrefHeight(20);
		String sNest = sarNests[_iSample];
		String sAugment = "";
		char cFacet = sNest.toCharArray()[0];
		String ssNest = null;
		StringBuilder sb = new StringBuilder("Set sample sizes for facet '" + cFacet + "'");
		switch (sNest.length()) {
		case 1:
			ssNest = "";
			break;
		case 3:
			sAugment = "               <- " + sNest.substring(2) + " ->";
			ssNest = "";
			sb.append(" in " + sNest.substring(2));
			break;
		default:
			sAugment = "               <- " + sNest.substring(2, 3) + " ->";
			fC = farFacets[sDictionary.indexOf(cFacet)];
			if (!fC.isCrossed())
				sb.append(" in nest '" + sNest.substring(2) + "'");
			Integer[] iNestors = fC.getNestors();
			Integer L = iNestors.length;
			if (L == 1) {
				sAugment = "               <- " + sNest.substring(2, 3) + " ->";
				ssNest = sNest.substring(4);
			} else if (L == 2) {
				int i1 = iNestors[0];
				int i2 = iNestors[0] + 1;
				sAugment = "               <- " + sDictionary.substring(i1, i2) + " ->";
				i1 = iNestors[1];
				i2 = iNestors[1] + 1;
				ssNest = sDictionary.substring(i1, i2);
			}
		}
		sb.append(".");
		Label lbTitle = new Label(sb.toString());
		lbTitle.setPrefWidth(800);
		lbTitle.setStyle(sStyle_20);
		lbTitle.setAlignment(Pos.TOP_CENTER);
		vPara.getChildren().add(lbTitle);
		vOuter.getChildren().add(vPara);
		char[] cFacets = sNest.replaceAll(":", "").toCharArray();
		iLength = cFacets.length;
		iPointers = new Integer[iLength];
		iRows = new Integer[iLength];
		iIndices = new Integer[iLength];
		// add sub title
		sp = new SplitPane();
		sp.setDividerPositions(new double[] { .15 });
		hbLeft = new VBox();
		hbLeft.setPrefHeight(0.3);
		hbRight = new VBox();
		hbRight.setPrefHeight(0.3);
		hbLeft.setStyle(sStyle_16);
		hbRight.setStyle(sStyle_16);
		Label lblInfo = new Label(ssNest);
		lblInfo.setStyle(sStyle_16);
		hbLeft.getChildren().add(lblInfo);
		sp.getItems().add(hbLeft);
		Label lbSubtitle = new Label("Sample Sizes" + sAugment);
		lbSubtitle.setStyle(sStyle_16);
		hbRight.getChildren().add(lbSubtitle);
		sp.getItems().add(hbRight);
		vOuter.getChildren().add(sp);
		sp = null;
		for (Integer i = 0; i < iLength; i++) {
			iRows[i] = sHDictionary.indexOf(cFacets[i]);
			iIndices[i] = 0;
			iPointers[i] = 0;
		}
		getSamples(cFacet);
		Boolean bFirstSample = true;
		String sLineLabel = "-";
		String sNewLineLabel = "";
		for (SampleSizeView ssv : ssvAL) {
			sNewLineLabel = ssv.getIndexString();
			if (!sNewLineLabel.equals(sLineLabel)) {
				sLineLabel = sNewLineLabel;
				if (sp != null) {
					hbRight.getChildren().add(fp);
					sp.getItems().add(hbRight);
					vOuter.getChildren().add(sp);
				}
				sp = new SplitPane();
				sp.setDividerPositions(new double[] { .15 });
				hbLeft = new VBox();
				hbRight = new VBox();
				Label lblIndices = new Label(sNewLineLabel);
				lblIndices.setStyle(sStyle_18);
				hbLeft.getChildren().add(lblIndices);
				sp.getItems().add(hbLeft);
				fp = new FlowPane();
			}
			fp.getChildren().add(ssv);
			if (bFirstSample)
				repeatFocus(ssv);
			bFirstSample = false;
		}
		hbRight.getChildren().add(fp);
		sp.getItems().add(hbRight);
		vOuter.getChildren().add(sp);
		ScrollPane scrollPane = new ScrollPane();
		scrollPane.setContent(vOuter);
		scrollPane.setFitToWidth(true);

		VBox vBox = new VBox();
		vBox.getChildren().add(scrollPane);
		return vBox;
	}

	public void getSamples(char _cFacet) {
		char cFacet = _cFacet;
		Integer iFacet = sDictionary.indexOf(cFacet);
		if (farFacets == null) {
			farFacets = myNest.getFacets();
			iFacetCount = farFacets.length;
		}

		Facet f = farFacets[iFacet];
		ssvAL = new ArrayList<SampleSizeView>();
		if (!bDoOver) {
			Integer[] iNestors = f.getNestors();
			Integer iSize;
			if (iNestors == null)
				iSize = 1;
			else {
				iSize = 1;
				for (Integer i : iNestors)
					iSize *= iarSums[i][iarSizes[i].length];
			}
			iarSizes[iFacet] = new Integer[iSize];
			iarSums[iFacet] = new Integer[iSize + 1];
			iarSums[iFacet][0] = 0;
			iarSums[iFacet][iSize] = 0;
		}

		String sIndices = null;
		Integer iCounter = 0;
		Integer iValue;
		for (Integer i = 0; i < iarSizes[iFacet].length; i++) {
			sIndices = getIndex(iFacet, i);
			iValue = iarSizes[iFacet][i];
			ssvAL.add(new SampleSizeView(this, cFacet, iFacet, iCounter++, iValue, stringToArray(sIndices)));
		}
	}

	public Boolean getChanged() {
		return bChanged;
	}

	public String getEffect(Integer iRow) {
		// returns effect line for control file
		StringBuilder sb = new StringBuilder("EFFECT       ");
		String sNest = sarNests[iRow];
		if (sNest.toCharArray()[0] == cAsterisk)
			sb.append("* ");
		sb.append(padRight(sNest, 12));
		Integer iFacet = sDictionary.indexOf(sNest.substring(0, 1));
		for (Integer i : iarSizes[iFacet]) {
			if (i == null)
				sb.append(" ");
			else
				sb.append(i.toString() + " ");
		}
		return sb.toString();
	}

	public String getNest(char _c) {
		return sarNests[sHDictionary.indexOf(_c)];
	}

	public static String padRight(String s, int n) {
		return String.format("%1$-" + n + "s", s);
	}

	public void hasChanged(Integer _iLevel, Integer _iPointer, Integer _value) {
		Integer iLevel = _iLevel;
		iarSizes[iLevel][_iPointer] = _value;
		iarSums[iLevel][_iPointer + 1] = iarSums[iLevel][_iPointer] + _value;
	}

	public void DoOver(Boolean _bDoOver) {
		bDoOver = _bDoOver;
	}

	public Integer numRecords() {
		Integer iRecordCount = 1;
		Boolean[] bDone = new Boolean[iFacetCount];
		for (Integer i = 0; i < iFacetCount; i++) // initialize flags to false
			bDone[i] = false;
		Integer iFacet = -1;
		Integer iSum = 0;
		Integer[] iNestees = null;
		Integer iSize = 0;
		Integer iRoot = -1;
		Integer iLoopCount = 0;
		Boolean bAsterisk = false;
		for (char c : sHDictionary.toCharArray()) {
			Facet f = myNest.getFacet(c);
			bAsterisk = f.starred();
			iFacet = f.getOrder();
			iRoot = iFacet;
			if (bDone[iRoot])
				continue; // this facet has already been processed
			bDone[iRoot] = true;
			iFacet = iRoot;
			while (((iNestees = f.getNestees()) != null) && (iNestees.length == 1)) {
				f = farFacets[iNestees[0]];
				iFacet = f.getOrder();
				bDone[iFacet] = true;
				bAsterisk = f.starred();
			}
			if ((iNestees == null) || (iNestees.length == 0)) {
				iRecordCount *= iarSums[iFacet][iarSizes[iFacet].length];
				if (bAsterisk)
					break;
				else
					iLoopCount++;
				continue;
			} else if (iNestees.length == 2) {
				Integer iN0 = iNestees[0];
				Integer iN1 = iNestees[1];
				iSize = iarSums[iFacet][iarSizes[iFacet].length];
				bDone[iN0] = true;
				bDone[iN1] = true;
				for (Integer i = 0; i < iSize; i++)
					iSum += iarSizes[iN0][i] * iarSizes[iN1][i];
				iRecordCount *= iSum;
				if (bAsterisk)
					break;
				else
					iLoopCount++;
				continue;
			}
		}
		return iRecordCount;
	}

	private void repeatFocus(Node node) {
		Platform.runLater(() -> {
			if (!node.isFocused()) {
				node.requestFocus();
				repeatFocus(node);
			}
		});
	}

	private String getIndex(Integer _iFacet, Integer _iPointer) {
		Integer[] iNestors = null;
		Integer iCount = -1;
		Integer iBase = -1;
		if (((iNestors = farFacets[_iFacet].getNestors()) == null) || (iNestors.length == 0))
			return "0";
		else if (iNestors.length == 1) {
			Integer iNestor = iNestors[0];
			for (Integer i = iarSums[iNestor].length - 1; i >= 0; i--) {
				if ((iBase = iarSums[iNestor][i]) <= _iPointer) {
					iCount = i;
					break;
				}
			}
			String sReturn = getIndex(iNestor, iCount) + ", " + String.valueOf(_iPointer - iBase);
			return (sReturn);
		} else {
			// multiple nestors - to be completed
			// we only handle the case 'c:ba' others will have to wait according
			// to need
			int ib = (int) iarSizes[iNestors[0]][0];
			int ic = (int) _iPointer;
			return "0, " + String.valueOf(ic / ib) + " ,0";
		}
	}

	private Integer[] stringToArray(String _sInput) {
		String[] tokens = _sInput.split(",");
		Integer L = tokens.length;
		Integer[] iResult = new Integer[L];
		String sTemp = null;
		if (_sInput.length() < 1)
			return new Integer[0];
		for (Integer i = 0; i < L; i++) {
			sTemp = tokens[i].toString().trim();
			try {
				iResult[i] = Integer.parseUnsignedInt(sTemp);
			} catch (Exception e) {
				popup.tell("509a", "Indices format problem:", e);
			}
		}
		return iResult;
	}

	public Integer size(Integer _iPointer) {
		return iarSums[_iPointer][iarSizes[_iPointer].length];
	}

	public Integer[] getSizes(char c) {
		Integer i = sDictionary.indexOf(c);
		return iarSizes[i];
	}

	public Integer[] getOSizes(Integer iComp) {
		return iarSizes[iComp];
	}

	public Integer[] getHSizes(Integer iComp) {
		if (iComp < iFacetCount)
			iComp = sDictionary.indexOf(sHDictionary.toCharArray()[iComp]);
		return iarSizes[iComp];
	}

	public Integer[] getSums(char c) {
		Integer i = sDictionary.indexOf(c);
		return iarSums[i];
	}

	public Integer[] getOSums(Integer iComp) {
		return iarSums[iComp];
	}

	public Integer[] getHSums(Integer iComp) {
		if (iComp < iFacetCount)
			iComp = sDictionary.indexOf(sHDictionary.toCharArray()[iComp]);
		return iarSums[iComp];
	}

	public Double getLevel(Integer _iFacet) {
		Integer iSize = iarSizes[_iFacet].length;
		return iarSums[_iFacet][iSize].doubleValue() / iSize.doubleValue();
	}

	public Integer getNestedSize(Integer _iFacet, Integer _iLevel) {
		/*
		 * here the _iFacet refers to the size of the target facet the _iLevel
		 * to the specific value of the Nestor's (actual) index, that means the
		 * nested index, if Nestor itself is nested!
		 */
		return iarSizes[_iFacet][_iLevel];
	}

	public Double getHarmonic(Integer _iFacet) {
		Double dDenom = 0.0;
		Integer iCount = 0;
		for (Integer iSize : iarSizes[_iFacet]) {
			if (!iSize.equals(0)) {
				iCount++;
				dDenom += 1.0 / iSize;
			}
		}
		return (iCount / dDenom);
	}

	public void collectSampleSizes(Filer flr) {
		// set iTranslate
		if (farFacets == null) {
			farFacets = myNest.getFacets();
			iFacetCount = farFacets.length;
		}
	}

	public Integer getOLevels(Integer i) {
		Integer[] ia = iarSums[i];
		Integer L = ia.length - 1;
		return ia[L];
	}

	public Integer getHLevels(Integer i) {
		Integer j = i;
		if (i < iFacetCount)
			j = sDictionary.indexOf(sHDictionary.toCharArray()[i]);
		Integer[] ia = iarSums[j];
		Integer L = ia.length - 1;
		return ia[L];
	}

	public Integer getEffectCount() {
		return sarNests.length;
	}

	public void setAsterisk (char _cAsterisk){
		cAsterisk = _cAsterisk;
	}
}
