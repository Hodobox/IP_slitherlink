import java.io.IOException;
import java.util.EnumMap;

public class Main {

	private enum ModelEnum
	{
		Naive("Naive"),
		Pat("Pat"),
		SetNeighbor("SetNeighbor"),
		NeighEnum("NeighEnum"),
		SAT("SAT");
		
		private String name;
		
		ModelEnum(String envName)
		{
			this.name = envName;
		}
		
		public String getName()
		{
			return name;
		}
	}
	
	private static EnumMap<ModelEnum,Class<? extends Slitherlink>> modelMap = new EnumMap<ModelEnum,Class<? extends Slitherlink>>(ModelEnum.class);
	
	private static void initModels(boolean verbose)
	{
		modelMap.put(ModelEnum.Naive, NaiveModel.class);
		modelMap.put(ModelEnum.Pat, PatModel.class);
		modelMap.put(ModelEnum.SetNeighbor, SetNeighborModel.class);
		modelMap.put(ModelEnum.NeighEnum, NeighEnumModel.class);
		modelMap.put(ModelEnum.SAT, SATModel.class);
		
		if(verbose)
		{
			System.out.println("Supported models:");
			for(ModelEnum e : modelMap.keySet())
			{
				System.out.println(e.getName());
			}
		}
		
	}
	
	private static void parseOptions(String[] args)
	{
		if(args.length == 0 || args[0].equals("-h"))
		{
			System.out.println("IP Slitherlink version 0.1");
			
			System.out.println("Options:\n"
					+ "--models: displays supported models\n"
					+ "-h: displays this message\n"
					+ "CLASS (required as first argument for normal run): the class to run. Currently supported: Tester, Runner, Analyzer, Displayer.\n"
					+ "\n"
					+ "CLASS specific options:\n"
					+ "-----------------------\n"
					+ "Runner (runs a model on a problem instance):\n"
					+ "--file STRING (required): Input problem instance (ending in .in)\n"
					+ "--model STRING (required): Model to run. See --models for list of supported models\n"
					+ "--time INT: Set CPU time limit in seconds (default infinity)\n"
				    + "--brief: Not verbose\n"
					+ "--save: Save solution (same directory as file, with model in its name and .out extension)\n"
				    + "-----------------------\n"
				    + "Tester (runs a model on a set of instances):\n"
				    + "--model STRING (required): Model to run. See --models for list of supported models\n"
				    + "--time INT: Set CPU time limit in seconds (default infinity)\n" 
					+ "--dir STRING: Specify directory to read inputs from (default data/in/manual)\n"
				    + "== Note that Tester always saves to data/out/ ==\n"
					+ "-----------------------\n"
				    + "Analyzer (gathers properties of instances/solutions):\n"
					+ "--dir STRING: Specificy directory to read inputs from (default data/in/manual)\n"
				    + "== Note that Analyzer always logs to data/analysis/ ==\n"
					+ "Displayer (makes a GUI with an instance/solution):\n"
				    + "--file STRING (required): Input problem instance (ending in .in)\n"
					+ "--sol STRING: Appropriate solution file to the problem instance (ending in .out)"
					+ "-----------------------\n"
					+ "Generator (generates puzzle input):\n"
					+ "--dir STRING: Specify directory to write inputs to (default data/in/generated)\n"
					+ "-----------------------\n");
			
			return;
		}
		
		if(args[0].equals("--models"))
		{
			initModels(true);
			return;
		}
		else initModels(false);
		
		
		// parse all supported arguments
		String fname = null, model = null, sol = null;
		long time = -1;
		boolean brief = false;
		String dir = null;
		boolean save = false;
		
		String classToRun = args[0];
		
		int argidx = 1;
		while(argidx < args.length)
		{
			String a = args[argidx];
			if(a.equals("--file")) { fname = args[argidx+1]; argidx++;}
			else if (a.equals("--model")) { model = args[argidx+1]; argidx++;}
			else if (a.equals("--time")) { time = Integer.parseInt(args[argidx+1]); argidx++; }
			else if (a.equals("--brief")) { brief = true; }
			else if (a.equals("--dir")) { dir = args[argidx+1]; argidx++; }
			else if (a.equals("--save")) { save = true; }
			else if (a.equals("--sol")) { sol = args[argidx+1]; argidx++; }
			else System.out.println("Error: unknown option '" + args[argidx] + "'");
			
			argidx++;
		}
		
		ModelEnum modelNum = null;
		if(model != null) modelNum = ModelEnum.valueOf(model);
		
		if(classToRun.equals("Tester"))
		{
			if(dir == null) dir = "data/in/manual/";
			try {
				Tester.test(modelMap.get(modelNum), time, dir);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		else if (classToRun.equals("Runner"))
		{
			try {
				Runner.run(modelMap.get(modelNum), fname, time, brief, save);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		else if (classToRun.equals("Analyzer"))
		{
			if(dir == null) dir = "data/in/manual/";
			Analyzer.analyze(dir);
		}
		else if (classToRun.equals("Displayer"))
		{
			Displayer.display(fname, sol);
		}
		else if (classToRun.equals("Generator"))
		{
			if(dir == null) dir = "data/in/generated/";
			Generator.generate(dir);
		}
		else System.out.println("Unsupported CLASS '" + classToRun + "'");
		
	}
	
	public static void main(String[] args)
	{
		parseOptions(args);
	}
	
}
