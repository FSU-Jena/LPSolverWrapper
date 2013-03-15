package edu.fsuj.csb.tools.LPSolverWrapper;

/**
 * implements a sum representation for lp terms
 * @author Stephan Richter
 *
 */
public class LPDiff extends LPSum {
	
	public LPDiff(Double k1, LPTerm left, Double k2, LPTerm right) {
	  super(k1, left, -k2, right);
  }

	public LPDiff(Double k1, LPTerm left, LPTerm right) {
	  super(k1, left, -1.0, right);
  }

	public LPDiff(LPTerm left, Double k2, LPTerm right) {
	  super(left, -k2, right);
  }
	public LPDiff(LPTerm left, LPTerm right) {
	  super(left, -1.0, right);
  }	
}
