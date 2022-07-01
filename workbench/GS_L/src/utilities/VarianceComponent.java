package utilities;

import java.io.UnsupportedEncodingException;

import model.Facet;
import model.Nest;

/**
 * 'VarianceComponent' (vc) is a class of objects for the calculation of
 * Generalizability Coefficients used in 'Nest', method 'formatResults'.
 * There is one one vc object for each actual variance component value.
 * The object calculates the contribution of this variance component
 * to the final Generalization Coefficient.
 * VC also handles Brennan's rules for calculating sigma2(tau), sigma2(delta),
 * and sigma2(Delta) [Brennan, Generalizability Theory, pp 144/5]
 * For each variance component it thus determines three booleans (b_tau, b_delta,
 * and b_Delta to signify that this variance component gets added to the corresponding
 * sigma squares. This step then occurs in Nest.formatResults.
 * During D-Studies it changes the signature item of fixed facets to 'f', thus
 * excluding them from contributing to the error terms delta and Delta.
 *
 * @see <a href="https://github.com/G-String-Legacy/G_String/blob/main/workbench/GS_L/src/utilities/VarianceComponent.java">utilities.VarianceComponent</a>
 * @author ralph
 * @version %v..%
 *
 */
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
	private Double dDenominator = 1.0;
	private String sDenominator = null;
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
		if (dVC < 0.0)			// ignore negative variance components
			dVC = 0.0;
		sPattern = sWords[0];
		cPattern = sPattern.toCharArray();
		sPlatform = _sPlatform;
		popup = _popup;
		popup.setClass("VarianceComponent");
	}

	public void doCoefficient(StringBuilder sbOut) {
		StringBuilder sb = new StringBuilder();
		Double dFactor = 0.0;
		boolean bFirst = true;
		dDenominator = 1.0;
		sSignature = sign(sPattern);
		for (char c : cPattern) {
			if (c != ':') {
				Facet f = farFacets[sDictionary.indexOf(c)];
				if (f.getFacetType() == 'd') 		// no factor from level of differentiation
					dFactor = 1.00;
				else if (f.getFacetType() == 's')	// no factor from level of stratification
					dFactor = 1.00;
				else
					dFactor = f.dGetLevel();
				dDenominator *= dFactor;
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
		/**
		 * now analyze contribution to tau, delta and Delta.
		 * in words:
		 *   tau: always has to contain d-type,  but no random facets;
		 *   delta: at least one 'g', but no 'd';
		 *   Delta: at least one 'g' facet
		 */
		b_tau = !has('g') && has('d');
		b_delta = has('g') && has('d');
		b_Delta = has('g');
		String sTarget = null;
		if (b_tau)
			sTarget = "τ only";
		else if (b_delta && !b_Delta)
			sTarget = "δ only";
		else if (!b_delta && b_Delta)
			sTarget = "Δ only";
		else if (b_delta && b_Delta)
			sTarget = "both δ and Δ";
		sDenominator = sb.toString();
		if (sbOut != null) {
			try {
				sbOut.append("Variance component '" + sPattern + "' (" + sSignature + ") is " + dVC
						+ "; denominator is " + sDenominator + ";  " + reCode(sTarget) + "\n");
			} catch (Exception ioe) {
				popup.tell("doCoefficient_a", ioe);
			}
		}
	}

	private Boolean has(char _c) {
		/**
		 * simplified 'Signature contains' function
		 */
		return (sSignature.indexOf(_c) >= 0);
	}

	public Double getDenominator() {
		return dDenominator;
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
				if (f.getFixed())		// that excludes them from error terms
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
