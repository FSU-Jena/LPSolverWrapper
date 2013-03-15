package edu.fsuj.csb.tools.LPSolverWrapper;

import java.util.TreeSet;

import org.omg.CORBA.DynAnyPackage.InvalidValue;

import edu.fsuj.csb.tools.xml.ObjectComparator;

/**
 * implements a linear term condition for usage in linear programing optimization
 * @author Stephan Richter
 *
 */
public class LPCondition {
	public final static int EQUAL=0;
	public final static int LESS_THEN=1;
	public final static int LESS_OR_EQUAL=2;
	public final static int GREATER_THEN=3;
	public final static int GREATER_OR_EQUAL=4;

	private LPTerm term;
	private Double value=null;
	private String comment;
	private int relation;	
	
	/**
	 * require left smaller than/equal to right
	 * @param left
	 * @param right
	 * @throws InvalidValue 
	 */
	public LPCondition (LPTerm term,int relation, Double value) {
		if (relation<0 || relation>4) throw new IndexOutOfBoundsException("Invalid value given for relation!");
		this.term=term;
		this.relation=relation;
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
	
	private String base(){
		switch (relation){
		case EQUAL: return term+" = "+value;
		case LESS_THEN: return term+" < "+value;
		case LESS_OR_EQUAL: return term+" <= "+value;
		case GREATER_THEN: return term+" > "+value;
		case GREATER_OR_EQUAL: return term+" >= "+value;
	}
  return null;
}
	
	public String toString() {
		if (comment!=null) return base()+" /* "+comment+" */";
		return base();
	}
}
