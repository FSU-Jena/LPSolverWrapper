package edu.fsuj.csb.tools.LPSolverWrapper;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Iterator;
import java.util.TreeMap;
import java.util.TreeSet;

import edu.fsuj.csb.tools.programwrapper.OutputHandler;
import edu.fsuj.csb.tools.xml.ObjectComparator;


/**
 * extend the capabilities of the LPSolverWrapper class for useage with the ibm cplex solver
 * @author Stephan Richter
 *
 */
public class CplexWrapper extends LPSolveWrapper implements OutputHandler {
	private int i=0;
	private String taskfilename="cplex.batch";
	protected TreeSet<LPVariable> binVars;
		
	/**
	 * creates a new Instance of this class
	 */
	public CplexWrapper() {
		super("cplex");
	}
	
	/* (non-Javadoc)
	 * @see edu.fsuj.csb.tools.LPSolverWrapper.LPSolveWrapper#writeLpFile()
	 */
	public void writeLpFile() throws IOException {
		BufferedWriter bw = new BufferedWriter(new FileWriter(lpFile));
		bw.write(((minimize)?"Minimize":"Maximize")+"\n obj: " + (objective!=null?objective:"") + ";\n");
		i=1;
		writeConditions(bw);
		writeRanges(bw);
		if (binVars != null && !binVars.isEmpty()) {
			bw.write("Binaries\n");
			for (Iterator<LPVariable> it = binVars.iterator(); it.hasNext();) {
				bw.write(it.next().toString()+"\n");
			}
		}
		if (intVars != null && !intVars.isEmpty()) {
			bw.write("Generals\n");
			for (Iterator<LPVariable> it = intVars.iterator(); it.hasNext();) {
				bw.write(it.next().toString()+"\n");
			}
		}
		bw.write("end\noptimize\n");
		bw.close();
/*		bw = new BufferedWriter(new FileWriter(taskfilename+".batch"));
		bw.write("set logfile "+taskfilename+".cplex.log\n"); 
		bw.write("read "+taskfilename+"\noptimize\ndisplay solution variables bI*\nquit\n");
		bw.close();*/
	}
	
	/**
	 * converts a LPCondition object into a string for use in lp files for the cplex program
	 * @param lpc the condition to be converted
	 * @return the condition's string representation
	 */
	private String lpConditionToString(LPCondition lpc){
		if (lpc.left!=null){ // left term given:
			if (lpc.right ==null)	{ // but no right term 
				
//				  		left coeff * term          <=       right coeff 
				return lpc.left.toString(lpc.k1)+" <= "+lpc.k2.toString() + ((lpc.comment()==null)?"":("\t\\ "+lpc.comment()));
			}
//			right term given:
//									coeff * term         -      right term * coeff        <= 0
			return lpc.left.toString(lpc.k1)+" - "+lpc.right.toString(lpc.k2)+" <= 0" + ((lpc.comment()==null)?"":("\t\\ "+lpc.comment()));
		}

		// no left term (but coefficient)
// 															right coeff		 :  right coeff * term          >=      left coeff    
		return ((lpc.right==null)?lpc.k2.toString():lpc.right.toString(lpc.k2))+" >= "+lpc.k1.toString() + ((lpc.comment()==null)?"":("\t\\ "+lpc.comment()));			
	}
	
	/* (non-Javadoc)
	 * @see edu.fsuj.csb.tools.LPSolverWrapper.LPSolveWrapper#writeConditions(java.io.BufferedWriter)
	 */
	protected void writeConditions(BufferedWriter bw) throws IOException {
		bw.write("Subject To\n");
 		for (Iterator<LPCondition> it = conditions.iterator(); it.hasNext();) bw.write(" c"+(i++)+": "+lpConditionToString(it.next()) + "\n");
  }
	
	/**
	 * converts a LPRange object into a string for usage in lp files for the cplex program
	 * @param r the range object to be converted
	 * @return the string representation of the range object
	 */
	private String lpRangeToString(LPRange r){
		String comment="";
		if (r.comment()!=null) comment=" \\ "+r.comment();

		if (r.min == r.max) return r.t+" = "+r.max+comment;
		return r.t+" >= "+r.min+"\n"+r.t+" <= "+r.max+comment;
	}
	
	/* (non-Javadoc)
	 * @see edu.fsuj.csb.tools.LPSolverWrapper.LPSolveWrapper#writeRanges(java.io.BufferedWriter)
	 */
	protected void writeRanges(BufferedWriter bw) throws IOException {
		if (ranges!=null)	for (Iterator<LPRange> it = ranges.iterator(); it.hasNext();) bw.write(" c"+(i++)+": "+lpRangeToString(it.next()) + "\n");
	}
	
	
	
	
	/* (non-Javadoc)
	 * @see edu.fsuj.csb.tools.LPSolverWrapper.LPSolveWrapper#setTaskfileName(java.lang.String)
	 */
	public void setTaskfileName(String taskFilename) {
		this.taskfilename=taskFilename;
		lpFile = new File(taskfilename);
  }
	
	/*public String getOutput(){
		StringBuilder sb=new StringBuilder();
		try {
			BufferedReader br=new BufferedReader(new FileReader(taskfilename+".cplex.log"));
	    while (br.ready()) sb.append(br.readLine());
	    br.close();
    } catch (IOException e) {
	    e.printStackTrace();
    }
		return sb.toString();
	}*/
	
	/**
	 * writes the linear program file, sets cplex parameters and starts optimization
	 * @param solutionVariables the solution variables to be loaded after computation (passed to cplex, may contain wildcards)
	 * @throws IOException
	 * @throws InterruptedException
	 */
	public void start(String solutionVariables) throws IOException, InterruptedException {
		this.writeLpFile();
		//addOutputHandler(this);
//  String [] params={"set logfile "+taskfilename+".cplex.log","read "+taskfilename,										 																											"optimize","display solution variables "+solutionVariables,"quit"}; 
  	String [] params={"set logfile "+taskfilename+".cplex.log","read "+taskfilename,"set mip cuts all 2",																											"optimize","display solution variables "+solutionVariables,"quit"}; 
//	String [] params={"set logfile "+taskfilename+".cplex.log","read "+taskfilename,"set mip cuts all 2","set mip cuts covers 3","set mip cuts disjunctive 3","optimize","display solution variables "+solutionVariables,"quit"}; 
		super.startAndWait(params);
  }

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
		while (!output[lineNumber].startsWith("Variable Name")){ // search for beginning of variable listing
			lineNumber++;
			if (lineNumber>=output.length){
				System.err.println(outputString);
				return null;
			}
		}
		
		while (true) {			
			Double value=0.0;
			try{
				String[] dummy=output[++lineNumber].split(" ");
				value=Double.parseDouble(dummy[dummy.length-1].trim());
				result.put(new LPVariable(dummy[0]),value);
			} catch (IndexOutOfBoundsException iobe){
				break;
			} catch (NumberFormatException nfe){
				break;
			}
		}
		return result;
	}
	
	/**
	 * @return a description of the current LP problem, this wrapper is holding
	 */
	public String describe(){
		StringBuffer sb=new StringBuffer(this.toString());
		sb.append("\nConditions:\n");
		i=1;
 		for (Iterator<LPCondition> it = conditions.iterator(); it.hasNext();) sb.append(" c"+(i++)+": "+lpConditionToString(it.next()) + "\n");
		if (ranges!=null)	for (Iterator<LPRange> it = ranges.iterator(); it.hasNext();) sb.append(" c"+(i++)+": "+lpRangeToString(it.next()) + "\n");
		if (intVars != null && !intVars.isEmpty()) {
			sb.append("Integers:\n");
			for (Iterator<LPVariable> it = intVars.iterator(); it.hasNext();) {
				sb.append(it.next().toString()+"\n");
			}
		}
		sb.append(((minimize)?"Minimize":"Maximize")+"\n obj: " + (objective!=null?objective:"") + ";\n");
 		return sb.toString();
	}

	/**
	 * declares a LPVariable to be binary (boolean)
	 * @param v
	 */
	public void addBinVar(LPVariable v) {
		if (binVars == null) binVars = new TreeSet<LPVariable>(ObjectComparator.get());
		binVars.add(v);
  }

	
	/* (non-Javadoc)
	 * @see edu.fsuj.csb.tools.programwrapper.OutputHandler#alert(java.lang.String, java.io.BufferedWriter)
	 */
	@Override
  public void alert(String message,BufferedWriter processWriter) {
		System.out.println(message);
  }

	/**
	 * passes a message to the cplex program
	 * @param msg the message to be submitted
	 * @param processWriter the process input handle
	 * @throws IOException
	 */
	@Deprecated
	protected void sendMessage(String msg, BufferedWriter processWriter) throws IOException {
		System.out.println("sending '"+msg+"'");
		processWriter.write(msg+'\n');
		processWriter.flush();
  }
}
