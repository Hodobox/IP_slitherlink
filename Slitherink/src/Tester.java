import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintStream;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;

public class Tester {
	
	private static final SimpleDateFormat sdf = new SimpleDateFormat("yyyy.MM.dd.HH.mm.ss");
	
	public static void test(Class<? extends Slitherlink> modelClass, long timeLimit, String dir) throws IOException
	{	
		System.out.println("Using model: " + modelClass.getSimpleName());
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
		logWriter.write("Model: " + modelClass.getSimpleName() + "\n");
		logWriter.write("Timelimit: " + timeLimit + "\n");
		
		File[] files = new File(dir).listFiles();
		
		Constructor constructor = null;
		try {
			constructor = modelClass.getConstructor(String.class);
		} catch (NoSuchMethodException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SecurityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		for(File input : files)
		{
			Slitherlink slitherlink = null; 
			String fname = input.getPath();
			try {
				slitherlink = (Slitherlink) constructor.newInstance(fname);
			} catch (InstantiationException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IllegalArgumentException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (InvocationTargetException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			slitherlink.verbose = false;
			if (timeLimit >= 0) slitherlink.solver.limitTime(timeLimit*1000);

			System.out.println("Solving " + fname + " (n=" + slitherlink.n + ")");
			slitherlink.solve();
		    
		    logWriter.write(fname + " (n=" + slitherlink.n + ")\n");
			
		    Validator val = new Validator(slitherlink);
		    
		    if(val.validate(false))
		    {
		    	System.out.println("Solved\n");
		    	String saveName = "data/out/" + input.getName().substring(0,input.getName().length()-2) + modelClass.getSimpleName() + ".out";
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
