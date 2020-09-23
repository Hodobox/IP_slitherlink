import java.io.*;
import java.util.HashSet;
import java.util.Scanner;
import java.util.Set;

import org.chocosolver.solver.Model;
import org.chocosolver.solver.Solver;
import org.chocosolver.solver.variables.*;
import org.jgrapht.alg.util.Pair;

public abstract class Slitherlink {

	Model model;
	Solver solver;
	int n = 0;
	int[][] board;
	IntVar[] solution;
	boolean verbose;
	
	public Slitherlink(String fname)
	{
		this.read(fname);
    	
		model  = new Model();
		solver = model.getSolver();
	}
	
 	boolean solve()
	{ 
		return solver.solve(); 
	}
	
	Set<Pair<Integer, Integer>> getEdges()
	{
		Set<Pair<Integer, Integer>> edges = new HashSet<>();

        for(int i=0;i<n*n;++i)
            if(solution[i].getValue() != i)
            {
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
                    //if(verbose) System.out.println(a+" "+b);
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
                //if(verbose) System.out.println(a+" "+b);
                edges.add(Pair.of(a,b));

                break;
            }

        return edges;
	}
	
	void stats() {solver.printShortStatistics();}
	void show() 
	{
		
		Set<Pair<Integer, Integer>> edges = getEdges();
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
	void read(String fname)
	{
		Scanner sc;
		try {
			sc = new java.util.Scanner(new File(fname));
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
	}
	
	void save(String fname)
	{
		Set<Pair<Integer, Integer>> edges = getEdges();
		try {
		      File file = new File(fname);
		      if (file.createNewFile()) {
		        System.out.println("File created: " + file.getName());
		        FileWriter myWriter = new FileWriter(fname);
		        myWriter.write(edges.size() + "\n");
		        for(Pair<Integer,Integer> e : edges)
		        {
		        	myWriter.write(e.getFirst() + " " + e.getSecond() + "\n");
		        }
		        myWriter.close();
		      } else {
		        System.out.println("File already exists, ignoring");
		      }
		    } catch (IOException e) {
		      e.printStackTrace();
		    }
		
	}
}
