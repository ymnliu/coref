package edu.mayo.bsi.nlp.coref.CrScorer.helpers;

import edu.mayo.bsi.nlp.coref.CrScorer.helpers.CEAFType;

public enum MatType {
	ABSOLUTE, RELATIVE;
	public static MatType matchingType (CEAFType t) {
		return t==CEAFType.MENTION ? ABSOLUTE : RELATIVE;
	}
}
