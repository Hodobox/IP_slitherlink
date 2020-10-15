import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintStream;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;

public class Tester {
	
	private static final SimpleDateFormat sdf = new SimpleDateFormat("yyyy.MM.dd.HH.mm.ss");
	
	public static void main(String[] args) throws IOException
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
		long timeLimit = -1;
		for (int i=1;i<args.length;i++){
		    if (args[i].equals("--time") || args[i].equals("-t")) timeLimit = 1000 * (long)Integer.parseInt(args[i+1]);
		    if(args[i].equals("--dir") || args[i].equals("-d")) dir = args[i+1];
		}
		
		System.out.println("Using model: " + modelName);
		System.out.println("Timelimit: " + timeLimit);
		
		Timestamp timestamp = new Timestamp(System.currentTimeMillis());
		String logName = "logs/" + sdf.format(timestamp).replace('.', '-') + ".txt";
		System.out.println("Logging to " + logName);
		File logFile = new File(logName);
		try {
			logFile.createNewFile();
		} catch (IOException e) {
			System.out.println("ERROR: UNABLE TO GENERATE LOG\n");
			e.printStackTrace();
		}
		FileWriter logWriter = new FileWriter(logFile);
		logWriter.write("Model: " + modelName + "\n");
		logWriter.write("Timelimit: " + timeLimit + "\n");
		
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

			System.out.println("Solving " + fname + " (n=" + slitherlink.n + ")");
			slitherlink.solve();
		    
		    logWriter.write(fname + " (n=" + slitherlink.n + ")\n");
			
		    Validator val = new Validator(slitherlink);
		    
		    if(val.validate(false))
		    {
		    	System.out.println("Solved\n");
		    	String saveName = "data/out/" + input.getName().substring(0,input.getName().length()-2) + modelName + ".out";
			    slitherlink.save(saveName);
			    slitherlink.stats();
			    
			    logWriter.write("Solved\n");
			    // little bit of hacking to get solver statistics into log file
			    ByteArrayOutputStream baos = new ByteArrayOutputStream();
			    PrintStream ps = new PrintStream(baos);
			    slitherlink.solver.setOut(ps);
			    slitherlink.stats();
			    String[] stats = baos.toString().split(" ");
			    String resolutionTime = stats[5];
			    String nodes = stats[6];
			    // write resolution time and # of nodes
			    logWriter.write("Resolution time " + resolutionTime + " " + nodes + " Nodes\n");
		    }
		    else
		    {
		    	logWriter.write("Unsolved\n");
		    	System.out.println("Failed to solve " + fname + " in " + timeLimit + "ms");
		    }
		    
		    logWriter.write("\n");
		    logWriter.flush();
		    
		}
		
		logWriter.close();
	}
}
