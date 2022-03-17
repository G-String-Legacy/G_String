package utilities;

import java.io.UnsupportedEncodingException;
import model.Facet;
import model.Nest;

public class VarianceComponent {
	private String sPattern; // nesting pattern
	private char[] cPattern = null; // char array of pattern
	private Boolean b_tau = false; // contributes to tau
	private Boolean b_delta = false; // contributes to delta
	private Boolean b_Delta = false; // contributes to Delta
	private Double dVC; // variance component
	private String sSignature; // interpretation of pattern
	private Nest myNest;
	private String sDictionary = null;
	private Facet[] farFacets = null;
	private Double dCoefficient = 1.0;
	private String sCoefficient = null;
	private Popup popup = null;
	private String sPlatform = null;

	public VarianceComponent(Nest _nest, String _line, String _sPlatform, Popup _popup) {
		/*
		 * constructor for G-Analysis
		 */
		myNest = _nest;
		sDictionary = myNest.getDictionary();
		farFacets = myNest.getFacets();
		String[] sWords = _line.split("\\s+");
		dVC = Double.parseDouble(sWords[5]);
		sPattern = sWords[0];
		cPattern = sPattern.toCharArray();
		sPlatform = _sPlatform;
		popup = _popup;
		popup.setObject(12, 70);
	}

	public VarianceComponent(Nest _nest, Boolean bMarker, Double _dVC, String sComponent, Popup _popup) {
		/*
		 * Constructor for data synthesis. (note the Boolean bMarker, which only
		 * serves to differentiate.)
		 */
		myNest = _nest;
		sDictionary = myNest.getDictionary();
		farFacets = myNest.getFacets();
		dVC = _dVC;
		sPattern = sComponent;
		cPattern = sPattern.toCharArray();
		popup = _popup;
		popup.setObject(12, 70);
	}

	public void doCoefficient(StringBuilder sbOut) {
		StringBuilder sb = new StringBuilder();
		Double dFactor = 0.0;
		Boolean bFirst = true;
		dCoefficient = 1.0;
		sSignature = sign(sPattern);
		for (char c : cPattern) {
			if (c != ':') {
				Facet f = farFacets[sDictionary.indexOf(c)];
				// f.setFacetLevel();
				if (f.getFacetType() == 'd') // no factor from level of
												// differentiation
					dFactor = 1.00;
				else
					dFactor = f.dGetLevel();
				dCoefficient *= dFactor;
				if (bFirst)
					try {
						sb.append(reCode(String.format("%.2f", dFactor)));
					} catch (UnsupportedEncodingException e) {
						popup.tell("1185a", e);
					}
				else
					try {
						sb.append(reCode(" x " + String.format("%.2f", dFactor)));
					} catch (UnsupportedEncodingException e) {
						popup.tell("1185b", e);
					}
				bFirst = false;
			}
		}
		// now analyze contribution to tau, delta and Delta.
		// in words:
		// tau: always has to contain d-type, may also contain fixed facet, but
		// no random facets
		b_tau = has('d') && !has('g'); // Norman's rule 5.1.1*
		b_delta = has('d') && (has('g'));
		b_Delta = (has('g') || has('s') && !has('d'));
		String sTarget = null;
		if (b_tau)
			sTarget = "τ only";
		else if (b_delta && !b_Delta)
			sTarget = "δ only";
		else if (!b_delta && b_Delta)
			sTarget = "Δ only";
		else if (b_delta && b_Delta)
			sTarget = "both δ and Δ";
		sCoefficient = sb.toString();
		if (sbOut != null) {
			try {
				sbOut.append("Variance component '" + sPattern + "' (" + sSignature + ") is " + dVC
						+ "; denominator is " + sCoefficient + ";  " + reCode(sTarget) + "\n");
			} catch (Exception ioe) {
				popup.tell("1185a", "Result file writer", ioe);
			}
		}
	}

	private Boolean has(char _c) {
		/**
		 * simplified 'Signature contains' function
		 */
		return (sSignature.indexOf(_c) >= 0);
	}

	public Double getCoefficient() {
		return dCoefficient;
	}

	public String getPattern() {
		return sPattern;
	}

	public Double getVarianceComponent() {
		return dVC;
	}

	public String getSignature() {
		return sSignature;
	}

	private String sign(String _pattern) {
		char[] cPattern = _pattern.toCharArray();
		char cTemp = 'x';
		StringBuilder sb = new StringBuilder();
		for (char c : cPattern) {
			if (c == ':')
				sb.append(c);
			else {
				Integer iFacet = myNest.getDictionary().indexOf(c);
				Facet f = farFacets[iFacet];
				cTemp = f.getFacetType();
				if (f.getFixed())
					cTemp = 'f';
				sb.append(cTemp);
			}
		}
		return sb.toString();
	}

	public Boolean b_tau() {
		return b_tau;
	}

	public Boolean b_delta() {
		return b_delta;
	}

	public Boolean b_Delta() {
		return b_Delta;
	}

	private String reCode(String sInput) throws UnsupportedEncodingException {
		String sOutput = sInput;
		if (sPlatform.equals("Mac")) {
			byte[] buffer = sInput.getBytes("MacGreek");
			sOutput = new String(buffer);
			return sOutput;
		}
		return sOutput;
	}
}
