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
		Double k1=this.k1*f;
		Double k2=this.k2*f;
		
		if (right==null) return (left==null)?"0":left.toString(k1);		
		return ((left==null)?"":(left.toString(k1)+" "))+"- "+right.toString(k2);
		
//		return ((left==null)?k1.toString():left.toString(k1))+" - "+((right==null)?k2.toString():right.toString(k2));
	}

}
