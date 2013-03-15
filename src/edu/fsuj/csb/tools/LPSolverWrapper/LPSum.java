package edu.fsuj.csb.tools.LPSolverWrapper;

/**
 * 
 * represents the (weighted) sum of two lp terms
 * @author Stephan Richter
 *
 */
public class LPSum extends LPTerm {

	
	Double k1,k2;
	
	/**
	 * creates a new weighted sum of two lp terms
	 * @param k1 the weight for the left hand side
	 * @param left the left hand term of the sum
	 * @param k2 the wight of the right hand side
	 * @param right the right hand term of the sum
	 */
	public LPSum(Double k1,LPTerm left,Double k2, LPTerm right) {
		this.k1=(left!=null)?k1:0;
		this.left=left;		
		
		this.k2=(right!=null)?k2:0;
		this.right=right;
  }

	/**
	 * creates a new, left-weighted sum of two lp terms
	 * @param k1 the weight for the left hand term
	 * @param left the left hand term of the sum
	 * @param right the right hand term of the sum
	 */
	public LPSum(Double k1,LPTerm left, LPTerm right) {
		this(k1,left,1.0,right);
  }

	/**
	 * creates a new, right-weighted sum of two lp terms
	 * @param left the left hand term of the sum
	 * @param k2 the weight of the right hand term of the sum
	 * @param right the right hand term of the sum
	 */
	public LPSum(LPTerm left, Double k2, LPTerm right) {
		this(1.0,left,k2,right);
  }

	/**
	 * creates a new unweighted sum of two lp terms
	 * @param left the left hand term
	 * @param right the right hand term of the sum
	 */
	public LPSum(LPTerm left, LPTerm right) {
		this(1.0,left,1.0,right);
  }
	
	/* (non-Javadoc)
	 * @see edu.fsuj.csb.tools.LPSolverWrapper.LPTerm#toString(java.lang.Double)
	 */
	public String toString(Double f) {
		Double k1=this.k1*f;
		Double k2=this.k2*f;
		
		if (left==null){
			return (right==null)?"0":right.toString(k2);
		} else {
			return left.toString(k1)+((right==null)?"":(" + "+right.toString(k2)));
		}
		
		//return ((left==null)?k1.toString():left.toString(k1))+" + "+((right==null)?k2.toString():right.toString(k2));
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		return toString(1.0).replace("+ -", "- ");
	}

}
