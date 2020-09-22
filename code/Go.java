import java.io.*;
import java.util.*;
import org.chocosolver.solver.Model;
import org.chocosolver.solver.Solver;
import org.chocosolver.solver.variables.IntVar;

public class Go {

    static int puzzleSize;
    static int[][] board;
    static Scanner sc;

    static void readSlitherlink(String fname) throws IOException {
	sc = new Scanner(new File(fname));
	puzzleSize = sc.nextInt();
	readBoard();
	sc.close();
    }

    static void readBoard() throws IOException {
    	board = new int[puzzleSize-1][puzzleSize-1];
    	for (int i=0;i<puzzleSize-1;i++)
        {
    	    for(int k=0;k<puzzleSize-1;++k)
                board[i][k] = sc.nextInt();
    	}
    
    }

    public static void main(String[] args)  throws IOException {
	if (args.length == 0 || args[0].equals("-h")){
	    System.out.println("Slitherlink version 0 \n" +
			       "FILE          Input problem instance (required)\n" +
                   "-time INT     Set CPU time limit in seconds (default infinity) \n" +
			       "-solve        Find a first solution (default) \n"+
			       "-opt          Optimize  \n" +
			       "-brief        Not verbose \n"+
			       "-trace        trace \n"+
			       "-h            Print this help message");
	    return;
	}
	String fname = "unknown";
	boolean solve = false, optimize = false, brief = false, trace = false;
	long timeLimit = -1;
	fname = args[0];
	for (int i=1;i<args.length;i++){
	    if (args[i].equals("-time") || args[i].equals("-t")) timeLimit = 1000 * (long)Integer.parseInt(args[i+1]);
	    if (args[i].equals("-solve")) solve = true;
	    if (args[i].equals("-opt")) optimize = true;
	    if (args[i].equals("-brief")) brief = true;
	    if (args[i].equals("-trace")) trace = true;
	}
	readSlitherlink(fname);
	Slitherlink slitherlink = new Slitherlink(puzzleSize, board);
	//slitherlink.verbose = !brief;
	if (timeLimit > 0) slitherlink.solver.limitTime(timeLimit);
	if (trace) slitherlink.solver.showDecisions();
	//if (optimize) slitherlink.optimize();
	else if (solve) slitherlink.solve();
	slitherlink.show();
    //slitherlink.stats();
    }
}
	
	
