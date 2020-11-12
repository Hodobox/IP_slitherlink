import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

public class Runner {

	public static void run(Class <? extends Slitherlink> modelClass, String fname, long timeLimit, boolean brief, boolean save) throws IOException {
		
		Constructor<? extends Slitherlink> constructor = null;
		try {
			constructor = modelClass.getConstructor(String.class);
		} catch (NoSuchMethodException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SecurityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		Slitherlink slitherlink = null; 
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
		
		slitherlink.verbose = !brief;
		if (timeLimit > 0) slitherlink.solver.limitTime(timeLimit*1000);
		System.out.println("Solving...\n");
		slitherlink.solve();
		slitherlink.show();
	    slitherlink.stats();
	    
	    if(save)
	    {
	    	String saveName = fname.substring(0,fname.length()-2) + modelClass.getSimpleName() + ".out";
	    	slitherlink.save(saveName);
	   	}
	    
	    Validator val = new Validator(slitherlink);
	    val.validate(true);
	    val.show();
	}

}
