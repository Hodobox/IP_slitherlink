import java.io.File;
import java.io.FileNotFoundException;
import java.util.HashSet;
import java.util.Scanner;
import java.util.Set;

import org.jgrapht.alg.util.Pair;

public class Validator {
	
	int n;
	int[][] board;
	Set<Pair<Integer, Integer>> edges;
	
	private boolean validate(boolean verbose)
	{
		return false;
	}
	
	public boolean validate(String input, String output, boolean verbose)
	{
		Scanner sc;
		try {
			sc = new java.util.Scanner(new File(input));
		n = sc.nextInt();
		board = new int[n-1][n-1];
    	for (int i=0;i<n-1;i++)
        {
    	    for(int k=0;k<n-1;++k)
                board[i][k] = sc.nextInt();
    	}
		sc.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		Set<Pair<Integer, Integer>> edges = new HashSet<>();
		
		try {
			sc = new java.util.Scanner(new File(output));
			int m = sc.nextInt();
			for(int i=0;i<m;++i)
			{
				int a = sc.nextInt(), b = sc.nextInt();
				edges.add(Pair.of(a, b));
			}
			sc.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return validate(verbose);
	}
	
	public boolean validate(Slitherlink model, boolean verbose)
	{
		n = model.n;
		edges = model.getEdges();
		board = model.board;
		return validate(verbose);
	}
	
}
