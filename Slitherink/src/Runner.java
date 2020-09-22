import java.io.IOException;

public class Runner {

	public static void main(String[] args) throws IOException {
		if (args.length == 0 || args[0].equals("-h")){
		    System.out.println("Slitherlink version 0 \n" +
				       "FILE          Input problem instance (required)\n" +
	                   "-time INT     Set CPU time limit in seconds (default infinity)\n" +
				       "-brief        Not verbose\n"+
				       "-h            Print this help message");
		    return;
		}
		
		String fname = "unknown";
		boolean brief = false, trace = false;
		long timeLimit = -1;
		fname = args[0];
		for (int i=1;i<args.length;i++){
		    if (args[i].equals("-time") || args[i].equals("-t")) timeLimit = 1000 * (long)Integer.parseInt(args[i+1]);
		    if (args[i].equals("-brief")) brief = true;
		}
		
		Slitherlink slitherlink = new PatModel(fname);
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
