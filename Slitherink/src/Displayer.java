
public class Displayer {
	private static void display_inner(String input)
	{
		Slitherlink tmp = new NaiveModel(input);
		Validator val = new Validator(tmp);
		val.edges.clear();
		val.show();
	}
	
	private static void display_inner(String input, String output)
	{
		Validator val = new Validator(input, output);
		val.show();
	}
	
	public static void display(String input, String output)
	{
		if(output == null) display_inner(input);
		else display_inner(input,output);
	}
	
}
