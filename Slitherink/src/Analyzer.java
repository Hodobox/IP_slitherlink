import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintStream;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;

public class Analyzer {
	
	private static final SimpleDateFormat sdf = new SimpleDateFormat("yyyy.MM.dd.HH.mm.ss");
	
	private static String getInstanceName(File f)
	{
		String[] fnameParts = f.getPath().split("/");
		fnameParts = fnameParts[fnameParts.length-1].split("\\.");
		String instanceName = fnameParts[0];
		return instanceName;
	}
	
	private static Validator getSolution(File in)
	{
		String fname = in.getPath();
		String instanceName = getInstanceName(in);
		File[] solutions = new File("data/out").listFiles();
		
		for(File f : solutions)
		{
			String solInstanceName = getInstanceName(f);
			
			if(solInstanceName.equals(instanceName))
			{
				Validator val = new Validator(fname, f.getPath());
				if(val.validate(false))
					return val;
			}
		}
		
		return null;
	}
	
	private static void analyze_inner(Validator instance, FileWriter log) throws IOException
	{
		// n
		log.write("n " + instance.n + "\n");
		
		// counts of numbers (-1,0,1,2,3)
		int[] counts = {0,0,0,0,0};
		for(int i=0;i<instance.n-1;++i)
		{
			for(int k=0;k<instance.n-1;++k)
			{
				counts[ instance.board[i][k]+1 ] += 1;
			}
		}
		
		log.write("counts");
		for(int i=0;i<5;++i)
			log.write(" " + counts[i]);
		log.write("\n");
		
		// length of path (if we have solution)
		if(instance.VALID)
		{
			log.write("length " + instance.edges.size() + "\n");
		}
	}
	
	public static void analyze(String dir)
	{
		System.out.println("Analyzing files in " + dir);
		
		File[] files = new File(dir).listFiles();
		
		for(File input : files)
		{ 
			// attempt to find a solved instance
			Validator instance = getSolution(input);
			if(instance == null)
			{
				// failed to find solution, load unsolved instance
				Slitherlink tmp = new NaiveModel(input.getPath());
				instance = new Validator(tmp);
				instance.validate(false); // will set VALID to false
			}
			
			File logFile = new File("data/analysis/" + getInstanceName(input) + ".txt");
			FileWriter logWriter = null;
			try {
				logWriter = new FileWriter(logFile);
				analyze_inner(instance, logWriter);
				logWriter.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			
		}
		
	}

}
