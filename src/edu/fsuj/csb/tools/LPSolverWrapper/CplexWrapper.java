package edu.fsuj.csb.tools.LPSolverWrapper;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Iterator;
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
	
	/* (non-Javadoc)
	 * @see edu.fsuj.csb.tools.LPSolverWrapper.LPSolveWrapper#writeConditions(java.io.BufferedWriter)
	 */
	protected void writeConditions(BufferedWriter bw) throws IOException {
		bw.write("Subject To\n");
 		for (Iterator<LPCondition> it = conditions.iterator(); it.hasNext();) bw.write(" c"+(i++)+": "+it.next().toString() + "\n");
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

	protected static String key="Actual values of the variables";
	
	/**
	 * @return a description of the current LP problem, this wrapper is holding
	 */
	public String describe(){
		StringBuffer sb=new StringBuffer(this.toString());
		sb.append("\nConditions:\n");
		i=1;
 		for (Iterator<LPCondition> it = conditions.iterator(); it.hasNext();) sb.append(" c"+(i++)+": "+it.next().toString() + "\n");
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
