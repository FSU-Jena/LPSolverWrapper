package edu.fsuj.csb.tools.LPSolverWrapper;

import java.util.TreeSet;

import edu.fsuj.csb.tools.xml.ObjectComparator;

/**
 * implements a linear term condition for usage in linear programing optimization
 * @author Stephan Richter
 *
 */
public abstract class LPCondition {
	LPTerm term;
	Double value=null;
	private String comment;	
	
	/**
	 * require left smaller than/equal to right
	 * @param left
	 * @param right
	 */
	public LPCondition (LPTerm term, Double value) {
		this.term=term;
		this.value=value;
  }
	
	/**
	 * adds/replaces the comment, which will be written into the lp file
	 * @param comment
	 */
	public void setComment(String comment){
		this.comment=comment;
	}

	/**
	 * returns the comment linked to this condition
	 * @return the comment string
	 */
	public String comment() {
	  return comment;
  }

	public static TreeSet<LPCondition> set() {
	  return new TreeSet<LPCondition>(ObjectComparator.get());
  }
}
