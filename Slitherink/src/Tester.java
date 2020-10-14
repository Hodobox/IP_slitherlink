import java.io.File;

public class Tester {
	public static void main(String[] args)
	{
		if (args.length == 0 || args[0].equals("-h")){
		    System.out.println("Slitherlink version 0 \n" +
				       "MODEL		  Model type (required)\n" +
	                   "--time INT     Set CPU time limit in seconds (default infinity)\n" +
				       "--dir STRING   Specify directory to read inputs from (default data/in/manual)\n" +
				       "-h            Print this help message\n"
		    );
		    return;
		}
		
		String modelName = args[0];
		String dir = "data/in/manual/";
		boolean brief = false, trace = false;
		long timeLimit = -1;
		for (int i=1;i<args.length;i++){
		    if (args[i].equals("--time") || args[i].equals("-t")) timeLimit = 1000 * (long)Integer.parseInt(args[i+1]);
		    if(args[i].equals("--dir") || args[i].equals("-d")) dir = args[i+1];
		}
		
		System.out.println("Using model: " + modelName);
		System.out.println("Timelimit: " + timeLimit);
		
		File[] files = new File(dir).listFiles();
		
		for(File input : files)
		{
			Slitherlink slitherlink = null; 
			String fname = input.getPath();
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
			slitherlink.verbose = false;
			if (timeLimit > 0) slitherlink.solver.limitTime(timeLimit);

			System.out.println("Solving " + fname + " (" + slitherlink.n + ")");
			slitherlink.solve();
			
		    Validator val = new Validator(slitherlink);
		    
		    if(val.validate(false))
		    {
		    	String saveName = "data/out/" + input.getName().substring(0,input.getName().length()-2) + modelName + ".out";
			    slitherlink.save(saveName);
			    System.out.println("Solved\n");
			    slitherlink.stats();
		    }
		    else
		    {
		    	System.out.println("Failed to solve " + fname + " in " + timeLimit + "ms");
		    }
		    
		}
	}
}
