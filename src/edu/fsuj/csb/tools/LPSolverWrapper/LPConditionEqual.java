package edu.fsuj.csb.tools.LPSolverWrapper;

public class LPConditionEqual extends LPCondition {

	public LPConditionEqual(LPTerm term, Double value) {
	  super(term, value);
  }
	
	@Override
	public String toString() {
	  return term+" = "+value;
	}
}
