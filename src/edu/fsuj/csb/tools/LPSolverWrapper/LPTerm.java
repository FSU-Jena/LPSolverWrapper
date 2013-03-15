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
	
	public static void main(String[] args) {
	  LPTerm a=new LPVariable("A");
	  LPTerm b=new LPVariable("B");
	  LPTerm c=new LPVariable("C");
	  LPTerm d=new LPVariable("D");
	  LPTerm t1=new LPSum(1.0,a,2.0,b);
	  LPTerm t2=new LPDiff(3.0,c,4.0,d);
	  System.out.println(new LPDiff(1.5,t1,.5,t2).toString());
  }
}
