import java.io.*;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Scanner;
import java.util.Set;

import org.chocosolver.solver.Model;
import org.chocosolver.solver.Solver;
import org.chocosolver.solver.search.strategy.Search;
import org.chocosolver.solver.variables.*;
import org.jgrapht.alg.util.Pair;

public abstract class Slitherlink {

	Model model;
	Solver solver;
	int n = 0;
	int[][] board;
	IntVar[] solution;
	boolean verbose;
	
	boolean solve()
	{ 
		//solver.setSearch(Search.minDomLBSearch(solution));
		return solver.solve(); 
	}
	
	void stats() {solver.printShortStatistics();}
	void show() 
	{
		Set<Pair<Integer, Integer>> edges = new HashSet<>();

        boolean found = false;
        for(int i=0;i<n*n;++i)
            if(solution[i].getValue() != i)
            {
                found = true;
                int idx = i;
                while(solution[idx].getValue() != i)
                {
                    //System.out.println(idx + " " + solution[idx].getValue());
                    int a = idx;
                    int b = solution[idx].getValue();
                    if(a>b)
                    {
                        a = a + b;
                        b = -(b - a);
                        a = a - b;
                    }
                    if(verbose) System.out.println(a+" "+b);
                    edges.add( Pair.of(a,b));
                    idx = solution[idx].getValue();
                }

                int a = idx;
                int b = solution[idx].getValue();
                if(a>b)
                {
                    a = a + b;
                    b = -(b - a);
                    a = a - b;
                }
                if(verbose) System.out.println(a+" "+b);
                edges.add(Pair.of(a,b));

                break;
            }

        if(found == false)
            System.out.println("No circuit");

        for(int r=0;r<n;++r)
        {
            for(int c=0;c<n;++c)
            {
                System.out.print(".");
                if(c<n-1)
                {
                    if( edges.contains(Pair.of(r*n+c,r*n+c+1) ) )
                        System.out.print("-");
                    else System.out.print(" ");
                }
            } System.out.println();

            if(r+1 != n)
            {
                for(int c=0;c<n;++c)
                {
                    
                    if( edges.contains(Pair.of(r*n+c, (r+1)*n+c) ))
                        System.out.print("|");
                    else System.out.print(" ");

                    if(c<n-1 && board[r][c] != -1)
                        System.out.print(board[r][c]);
                    else System.out.print(" ");

                } System.out.println();
            }
        }
	}
	void read(String fname) throws IOException
	{
		Scanner sc = new java.util.Scanner(new File(fname));
		n = sc.nextInt();
		board = new int[n-1][n-1];
    	for (int i=0;i<n-1;i++)
        {
    	    for(int k=0;k<n-1;++k)
                board[i][k] = sc.nextInt();
    	}
		sc.close();
	}
	
	int[] getResult()
	{
		int[] result = new int[n*n];
	    for (int i=0;i<n*n;i++)
	        result[i] = solution[i].getValue();
	    return result;
	}
}