package edu.fsuj.csb.tools.LPSolverWrapper;

/**
 * implements a linear term condition for usage in linear programing optimization
 * @author Stephan Richter
 *
 */
public class LPCondition {
	LPTerm left;
	LPTerm right;
	Double k1=null;
	Double k2=null;
	private String comment;	
	
	/**
	 * require left smaller than/equal to right
	 * @param left
	 * @param right
	 */
	public LPCondition (LPTerm left, LPTerm right) {
		this(1.0,left,1.0,right);
  }

	/**
	 * require k1*left smaller than/equal to right
	 * @param k1
	 * @param left
	 * @param right
	 */
	public LPCondition (Double k1,LPTerm left, LPTerm right) {
		this(k1,left,1.0,right);
  }

	/**
	 * require left smaller than/equal to k2*right
	 * @param left
	 * @param k2
	 * @param right
	 */
	public LPCondition (LPTerm left, Double k2,LPTerm right) {
		this(1.0,left,k2,right);
  }

	/**
	 * require k1*left smaller than/equal to k2*right
	 * @param k1
	 * @param left
	 * @param k2
	 * @param right
	 */
	public LPCondition (Double k1,LPTerm left, Double k2,LPTerm right) {
			this.k1=k1;
			this.left=left;
			this.k2=k2;
			this.right=right;
  }
	
	/**
	 * require d smaller than/equal to t
	 * @param d
	 * @param t
	 */
	public LPCondition(double d, LPTerm t) {
		this(d,null,t);
  }

	/**
	 * require term smaller than/equal to value
	 * @param term
	 * @param value
	 */
	public LPCondition(LPTerm term,double value) {
		this(term,value,null);
  }
	
	/**
	 * adds/replaces the comment, which will be written into the lp file
	 * @param comment
	 */
	public void setComment(String comment){
		this.comment=comment;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	public String toString() {		
		return ((left==null)?k1.toString():left.toString(k1))+" <= "+((right==null)?k2.toString():right.toString(k2))+";";			
	}

	/**
	 * returns the comment linked to this condition
	 * @return the comment string
	 */
	public String comment() {
	  return comment;
  }
}
