package edu.fsuj.csb.tools.LPSolverWrapper;

/**
 * implements a sum representation for lp terms
 * @author Stephan Richter
 *
 */
public class LPDiff extends LPSum {
	
	/**
	 * represents (left - right)
	 * @param left
	 * @param right
	 */
	public LPDiff(LPTerm left, LPTerm right) {
	  super(left, right);
  }
	
	/**
	 * represents (left - k2*right)
	 * @param left
	 * @param k2
	 * @param right
	 */
	public LPDiff(LPTerm left, double k2, LPTerm right) {
		super(left,k2,right);
	}
	
	/* (non-Javadoc)
	 * @see edu.fsuj.csb.tools.LPSolverWrapper.LPSum#toString(java.lang.Double)
	 */
	public String toString(Double f) {
		Double factor=k1*f;
		
		if (right==null) {
			if (left==null) return "0";
			return left.toString(factor);		
		}
		if (left==null){
			return "- "+parenthesis(f);
		}
		return left.toString(factor)+" - "+parenthesis(f);
	}

	private String parenthesis(Double f) {
		Double factor=this.k2*f;
		if (right instanceof LPVariable) {
			return right.toString(factor);	    
    }
	  return "( "+right.toString(factor)+" )";
  }

}
