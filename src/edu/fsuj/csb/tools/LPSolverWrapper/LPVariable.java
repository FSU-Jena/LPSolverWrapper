package edu.fsuj.csb.tools.LPSolverWrapper;

/**
 * a named token, which can be used in LPTerms
 * @author Stephan Richter
 *
 */
public class LPVariable extends LPTerm {
	private String name;
	/**
	 * create a new LPVariable
	 * @param name teh name, which the variable shall have
	 */
	public LPVariable(String name) {
	  this.name=name;
  }
	
	/**
	 * returns the LPVariable, multiplied with d if applicable
	 * @return &lt;d&gt;&nbsp;&lt;name-of-variable&gt; if d ist not one, &lt;name-of-variable&gt; otherwise
	 */
	public String toString(Double d) {
	  return ((d!=1)?d.toString()+" ":"")+name;
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
	  return name;
	}
}
