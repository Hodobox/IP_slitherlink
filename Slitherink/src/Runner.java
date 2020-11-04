import java.io.IOException;

public class Runner {

	public static void main(String[] args) throws IOException {
		if (args.length == 0 || args[0].equals("-h")){
		    System.out.println("Slitherlink version 0 \n" +
				       "FILE          Input problem instance (required)\n" +
				       "MODEL		  Model type (required)\n" +
	                   "--time INT     Set CPU time limit in seconds (default infinity)\n" +
				       "--brief        Not verbose\n"+
				       "--models		  Print list of supported models\n" +
				       "-h            Print this help message\n"
		    );
		    return;
		}
		
		if(args[0].equals("--models"))
		{
			System.out.println(
				"Naive\n" +
			    "Pat\n" +
				"SetNeighbor\n" +
			    "NeighEnum\n" + 
				"SAT"
			);
			return;
		}
		
		String fname = args[0];
		String modelName = args[1];
		boolean brief = false, trace = false;
		long timeLimit = -1;
		for (int i=1;i<args.length;i++){
		    if (args[i].equals("--time") || args[i].equals("-t")) timeLimit = 1000 * (long)Integer.parseInt(args[i+1]);
		    if (args[i].equals("--brief")) brief = true;
		}
		
		Slitherlink slitherlink = null; 
		
		if(modelName.equals("Naive")) slitherlink = new NaiveModel(fname);
		else if (modelName.equals("Pat")) slitherlink = new PatModel(fname);
		else if (modelName.equals("SetNeighbor")) slitherlink = new SetNeighborModel(fname);
		else if (modelName.equals("NeighEnum")) slitherlink = new NeighEnumModel(fname);
		else if (modelName.equals("SAT")) slitherlink = new SATModel(fname);
		else
		{
			System.out.println("Unsupported model: " + modelName);
			return;
		}
		slitherlink.verbose = !brief;
		if (timeLimit > 0) slitherlink.solver.limitTime(timeLimit);
		if (trace) slitherlink.solver.showDecisions();
		System.out.println("Solving...\n");
		slitherlink.solve();
		slitherlink.show();
	    slitherlink.stats();
	    
	    //String saveName = fname.substring(0,fname.length()-2) + modelName + ".out";
	    //slitherlink.save(saveName);
	    
	    Validator val = new Validator(slitherlink);
	    val.validate(true);
	    val.show();
	}

}
