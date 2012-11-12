package edu.fsuj.csb.tools.LPSolverWrapper;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Iterator;
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
 		for (Iterator<LPCondition> it = conditions.iterator(); it.hasNext();) bw.write(it.next() + "\n");
  }

	/**
	 * removes the lp file
	 */
	public void clean(){
		lpFile.delete();
	}

	/**
	 * a testing method for the LPSolverWrapper
	 * @param args
	 * @throws IOException
	 * @throws InterruptedException
	 */
	public static void main(String[] args) throws IOException, InterruptedException {

		LPVariable A = new LPVariable("RA");
		LPVariable B = new LPVariable("RB");
		LPVariable C = new LPVariable("RC");
		LPVariable Ain = new LPVariable("RAin");
		LPVariable Bin = new LPVariable("RBin");
		LPVariable Cin = new LPVariable("RCin");
		
		LPSum sum1 = new LPSum(Ain,new LPSum(Bin,Cin));
		LPSum sum2 = new LPSum(B,C);


		LPSolveWrapper LPW = new LPSolveWrapper();
		LPW.addCondition(new LPCondition(new LPSum(Ain,C),A));
		LPW.addCondition(new LPCondition(new LPSum(Bin,C),B));
		LPW.addCondition(new LPCondition(new LPSum(new LPSum(2.0,Cin,A),B),2.0,C));
		LPW.addCondition(new LPCondition(2.0,sum2));
		LPW.setEqual(sum1,10);
		LPW.minimize(sum1);
		LPW.startBlocking();
		System.out.println(LPW.getOutput());
		ProgramWrapper pr=new ProgramWrapper("cat input.lp");
		pr.startBlocking();
		System.out.println();
		System.out.println(pr.getOutput());
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
	 * set the filename, into which the linear program shall be written before optimization
	 * @param taskFilename
	 */
	public void setTaskfileName(String taskFilename) {
		lpFile = new File(taskFilename);
		setCommand("lp_solve "+taskFilename);
  }}
