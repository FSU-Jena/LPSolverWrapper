package edu.fsuj.csb.tools.LPSolverWrapper;

public class LPConditionEqualOrLess extends LPCondition {

	public LPConditionEqualOrLess(LPTerm term, Double value) {
	  super(term, value);
  }
	
	@Override
	public String toString() {	 
	  return term+" <= "+value;
	}

}
