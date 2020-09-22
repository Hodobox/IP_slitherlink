import java.io.IOException;

public class Runner {

	public static void main(String[] args) throws IOException {
		if (args.length == 0 || args[0].equals("-h")){
		    System.out.println("Slitherlink version 0 \n" +
				       "FILE          Input problem instance (required)\n" +
	                   "-time INT     Set CPU time limit in seconds (default infinity)\n" +
				       //"-opt          Optimize instead of find first solution\n" +
				       "-brief        Not verbose\n"+
				       "-trace        trace\n"+
				       "-h            Print this help message");
		    return;
		}
		
		String fname = "unknown";
		boolean solve = false, optimize = false, brief = false, trace = false;
		long timeLimit = -1;
		fname = args[0];
		for (int i=1;i<args.length;i++){
		    if (args[i].equals("-time") || args[i].equals("-t")) timeLimit = 1000 * (long)Integer.parseInt(args[i+1]);
		    if (args[i].equals("-solve")) solve = true;
		    if (args[i].equals("-opt")) optimize = true;
		    if (args[i].equals("-brief")) brief = true;
		    if (args[i].equals("-trace")) trace = true;
		}
		
		Slitherlink slitherlink = new SetNeighborModel(fname);
		slitherlink.verbose = !brief;
		if (timeLimit > 0) slitherlink.solver.limitTime(timeLimit);
		if (trace) slitherlink.solver.showDecisions();
		//if (optimize) slitherlink.optimize();
		//else 
		slitherlink.solve();
		slitherlink.show();
	    slitherlink.stats();

	}

}
