package edu.fsuj.csb.tools.LPSolverWrapper;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Iterator;
import java.util.TreeMap;
import java.util.TreeSet;

import edu.fsuj.csb.tools.programwrapper.ProgramWrapper;
import edu.fsuj.csb.tools.xml.ObjectComparator;

/**
 * extends the program Wrapper for the linux command line program lpsolve
 * @author Stephan Richter
 *
 */
public class LPSolveWrapper extends ProgramWrapper {
	protected File lpFile = new File("input.lp");
	protected TreeSet<LPCondition> conditions;
	protected LPTerm objective;
	protected TreeSet<LPVariable> intVars;
	protected TreeSet<LPVariable> binVars;
	protected TreeSet<LPRange> ranges;
	protected boolean minimize=false;
	
	/**
	 * adds a condition to the current linear problem
	 * @param lpc the condition that shall be added
	 */
	public void addCondition(LPCondition lpc) {
		if (conditions == null) conditions = new TreeSet<LPCondition>(ObjectComparator.get());
		conditions.add(lpc);
	}

	/**
	 * changes the linear programming term to be optimized, but does not alter the minimization mode (minimization/maximization)
	 * @param t the term, that shall be optimized
	 */
	public void setObjective(LPTerm t) {
		objective = t;
	}
	
	/**
	 * sets a linear programming term to be minimized
	 * @param t the term, that shall be minimized
	 */
	public void minimize(LPTerm t){
		setObjective(t);
		minimize=true;
	}

	/**
	 * sets a linear programming term to be maximized
	 * @param t the term, that shall be maximized
	 */
	public void maximize(LPTerm t){
		setObjective(t);
		minimize=false;
	}

	/**
	 * creates a new LPSolver object
	 */
	public LPSolveWrapper() {
		super("lp_solve input.lp");
	}

	/**
	 * creates a new LPSolver object with altered execution command
	 * @param command the alternative execution command
	 */
	public LPSolveWrapper(String command) {
		super(command);
  }

	/**
	 * writes the optimization program file, starts the optimization process and waits for it to continue
	 * @throws IOException
	 * @throws InterruptedException
	 */
	public void start() throws IOException, InterruptedException {
		//System.out.println("LPSolverWrapper.start()");
		writeLpFile();
		startBlocking();
	}

	/**
	 * writes the optimization program file
	 * @throws IOException
	 */
	protected void writeLpFile() throws IOException {
		BufferedWriter bw = new BufferedWriter(new FileWriter(lpFile));
		bw.write(((minimize)?"min":"max")+": " + (objective!=null?objective:"") + ";\n");
		writeRanges(bw);
		writeConditions(bw);
		if (intVars != null && !intVars.isEmpty()) {
			bw.write("int\n");
			for (Iterator<LPVariable> it = intVars.iterator(); it.hasNext();) {
				bw.write(it.next().toString());
				if (it.hasNext()) bw.write(", "); else
				bw.write(";\n");
			}
		}
		if (binVars != null && !binVars.isEmpty()) {
			bw.write("binary\n");
			for (Iterator<LPVariable> it = binVars.iterator(); it.hasNext();) {
				bw.write(it.next().toString());
				if (it.hasNext()) bw.write(", "); else
				bw.write(";\n");
			}
		}
		bw.close();
	}

	/**
	 * writes desired variable ranges into the optimization program file
	 * @param bw the writer for the lp file
	 * @throws IOException
	 */
	protected void writeRanges(BufferedWriter bw) throws IOException {
		if (ranges!=null)	for (Iterator<LPRange> it = ranges.iterator(); it.hasNext();) bw.write(it.next() + "\n");
	}

	/**
	 * writes the desired conditions into the optimization program file
	 * @param bw the writer for the lp file
	 * @throws IOException
	 */
	protected void writeConditions(BufferedWriter bw) throws IOException {
 		for (Iterator<LPCondition> it = conditions.iterator(); it.hasNext();) bw.write(it.next() + ";\n");
  }

	/**
	 * removes the lp file
	 */
	public void clean(){
		lpFile.delete();
	}

	/**
	 * adds a term=value assignment to the current linear program
	 * @param term
	 * @param value
	 */
	public void setEqual(LPTerm term, double value) {
	  this.addRange(new LPRange(value, term, value));
	  
  }
	
	/**
	 * adds a term=value assignment to the current linear program and also adds a comment to it
	 * @param term
	 * @param value
	 * @param comment
	 */
	public void setEqual(LPTerm term, double value, String comment) {
	  this.addRange(new LPRange(value, term, value,comment));	  
  }


	/**
	 * adds a range definition to the current linear program
	 * @param range
	 */
	private void addRange(LPRange range) {
	  if (ranges==null) ranges = new TreeSet<LPRange>(ObjectComparator.get());
	  ranges.add(range);
  }

	/**
	 * reads the value of a certain lp variable, should be used after program execution
	 * @param v the variable, for which the value shall be returned
	 * @return the value of the variable or null, if <ul><li>the variable is not part of the linear program</li><li>notpart of the solution</li><li> the program has not been executed (optimized), yet</li><ul>
	 */
	public Double get(LPVariable v) {
		String [] oouLines=getOutput().split("\n");
		for (int i=oouLines.length-1; i>=0; i--) if (oouLines[i].startsWith(v.toString()+" ")) return Double.parseDouble(oouLines[i].substring(oouLines[i].lastIndexOf(" ")+1));
		return null;
  }

	/**
	 * set a LPVariable to be of type int
	 * @param v
	 */
	public void addIntVar(LPVariable v) {
		if (intVars == null) intVars = new TreeSet<LPVariable>(ObjectComparator.get());
		intVars.add(v);
	}
	
	/**
	 * declares a LPVariable to be binary (boolean)
	 * @param v
	 */
	public void addBinVar(LPVariable v) {
		if (binVars == null) binVars = new TreeSet<LPVariable>(ObjectComparator.get());
		binVars.add(v);
  }
	
	/**
	 * set the filename, into which the linear program shall be written before optimization
	 * @param taskFilename
	 */
	public void setTaskfileName(String taskFilename) {
		lpFile = new File(taskFilename);
		System.out.println(lpFile.getAbsolutePath());
		setCommand("lp_solve "+taskFilename);
  }
	
	protected static String key="Actual values of the variables";
	
	/**
	 * read the output of the cplex program and parse it into solution values
	 * @return a mapping from LPVariables to their respective values
	 */
	public TreeMap<LPVariable,Double> getSolution(){
		String outputString=getOutput();
		if (outputString.contains("No integer feasible solution exists")) return null; 
		TreeMap<LPVariable, Double> result=new TreeMap<LPVariable, Double>(ObjectComparator.get());
		String [] output=outputString.split("\n");		
		int lineNumber=0;
		while (!output[lineNumber].startsWith(key)){ // search for beginning of variable listing
			if (output[lineNumber].startsWith("Value of objective function")) System.out.println(output[lineNumber]);
			lineNumber++;
			if (lineNumber>=output.length){
				System.err.println(outputString);
				return null;
			}
		}
		lineNumber++;
		while (lineNumber<output.length) {			
			Double value=0.0;
			try{
				String[] dummy=output[lineNumber].split(" ");
				value=Double.parseDouble(dummy[dummy.length-1].trim());
				result.put(new LPVariable(dummy[0]),value);
				lineNumber++;
			} catch (IndexOutOfBoundsException iobe){
				iobe.printStackTrace();
				break;
			} catch (NumberFormatException nfe){
				nfe.printStackTrace();
				break;
			}
		}
		return result;
	}

	
}
