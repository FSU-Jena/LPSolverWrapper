package edu.fsuj.csb.tools.LPSolverWrapper;

/**
 * base class for different types of terms which can occur in linear programs
 * @author Stephan Richter
 *
 */
public class LPTerm {
	protected LPTerm left,right;

	/**
	 * returns k1 as string
	 * @param k1 a double value
	 * @return k1 as string
	 */
	public String toString(Double k1) {	  
	  return k1.toString();
  }
}
