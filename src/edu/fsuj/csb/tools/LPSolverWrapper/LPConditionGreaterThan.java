package edu.fsuj.csb.tools.LPSolverWrapper;

public class LPConditionGreaterThan extends LPCondition {

	public LPConditionGreaterThan(LPTerm term, Double value) {
	  super(term, value);
  }
	
	@Override
	public String toString() {	 
	  return term+" > "+value;
	}

}
