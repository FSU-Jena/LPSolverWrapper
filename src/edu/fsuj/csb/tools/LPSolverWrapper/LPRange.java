package edu.fsuj.csb.tools.LPSolverWrapper;

/**
 * implements a range construct for linear programs
 * @author Stephan Richter
 *
 */
public class LPRange {
	protected double min,max;
	protected LPTerm t;
	public String term;
	private String comment;
	
	/**
	 * creates a new lp range object
	 * @param min the minimum value the term is allowed to have
	 * @param term the term, which shall be limited
	 * @param max the maximum value the term is allowed to have
	 */
	public LPRange(double min, LPTerm term, double max) {
//		System.out.println("new Range");
		this.min=min;
		this.max=max;
		this.t=term;
  }
	
	/**
	 * creates a new, commented lp range object
	 * @param min the minimum value the term is allowed to have
	 * @param term the term, which shall be limited
	 * @param max the maximum value the term is allowed to have
	 * @param comment the comment, which shall be written into linear programs using this term
	 */
	public LPRange(double min, LPTerm term, double max, String comment) {
		this(min,term,max);
		this.comment=comment;
  }
	
	/**
	 * returns the comment of this term, if given
	 * @return null or the comment of this term, if given
	 */
	public String comment(){
		return comment;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
//		System.out.println("Range");
	  return min+" <= "+t+" <= "+max+";";
	}
}
