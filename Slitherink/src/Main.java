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
					+ "CLASS (required as first argument for normal run): the class to run. Currently supported: Tester, Runner.\n"
					+ "\n"
					+ "CLASS specific options:\n"
					+ "-----------------------\n"
					+ "Runner:\n"
					+ "--file STRING (required): Input problem instance (ending in .in)\n"
					+ "--model STRING (required): Model to run. See --models for list of supported models\n"
					+ "--time INT: Set CPU time limit in seconds (default infinity)\n"
				    + "--brief: Not verbose\n"
					+ "--save: Save solution (same directory as file, with model in its name and .out extension)\n"
				    + "-----------------------\n"
				    + "Tester:\n"
				    + "--model STRING (required): Model to run. See --models for list of supported models\n"
				    + "--time INT: Set CPU time limit in seconds (default infinity)\n" 
					+ "--dir STRING: Specify directory to read inputs from (default data/in/manual)\n"
				    + "== Note that Tester always saves to data/out/ =="
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
		String fname = null, model = null;
		long time = -1;
		boolean brief = false;
		String dir = "data/in/manual/";
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
			else System.out.println("Error: unknown option '" + args[argidx] + "'");
			
			argidx++;
		}
		
		ModelEnum modelNum = null;
		if(model != null) modelNum = ModelEnum.valueOf(model);
		
		if(classToRun.equals("Tester"))
		{
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
		else System.out.println("Unsupported CLASS '" + classToRun + "'");
		
		
	}
	
	public static void main(String[] args)
	{
		parseOptions(args);
	}
	
}
