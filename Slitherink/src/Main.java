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
					+ "FILE (required): Input problem instance (ending in .in)\n"
					+ "MODEL (required): Model to run. See --models for list of supported models\n"
					+ "--time INT: Set CPU time limit in seconds (default infinity)\n"
				    + "--brief: Not verbose\n"
				    + "-----------------------\n"
				    + "Tester:\n"
				    + "MODEL (required): Model to run. See --models for list of supported models\n"
				    + "--time INT: Set CPU time limit in seconds (default infinity)\n" 
					+ "--dir STRING: Specify directory to read inputs from (default data/in/manual)\n"
					+ "-----------------------\n");
			
			return;
		}
		
		if(args[0].equals("--models"))
		{
			initModels(true);
			return;
		}
		else initModels(false);
		
		ModelEnum modelNum = ModelEnum.valueOf(args[0]);
		
		// parse all supported arguments
		
		String fname = null, model = null;
		long time = -1;
		Boolean brief = null;
		String dir = "data/in/manual/";
		
		/*for (int i=1;i<args.length;i++){
		    if (args[i].equals("--time") || args[i].equals("-t")) timeLimit = 1000 * (long)Integer.parseInt(args[i+1]);
		    if(args[i].equals("--dir") || args[i].equals("-d")) dir = args[i+1];
		}*/
		
		
	}
	
	public static void main(String[] args)
	{
		parseOptions(args);
	}
	
}
