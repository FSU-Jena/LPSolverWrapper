package edu.fsuj.csb.tools.LPSolverWrapper;

public class LPConditionEqualOrGreater extends LPCondition {

	public LPConditionEqualOrGreater(LPTerm term, Double value) {
	  super(term, value);
  }
	
	@Override
	public String toString() {	 
	  return term+" >= "+value;
	}

}
