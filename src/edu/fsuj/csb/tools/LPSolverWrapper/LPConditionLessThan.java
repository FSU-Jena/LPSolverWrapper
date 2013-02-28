package edu.fsuj.csb.tools.LPSolverWrapper;

public class LPConditionLessThan extends LPCondition {

	public LPConditionLessThan(LPTerm term, Double value) {
	  super(term, value);
  }
	
	@Override
	public String toString() {	 
	  return term+" < "+value;
	}

}
