import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Scanner;
import java.util.Set;

import org.jgrapht.alg.util.Pair;

public class Validator {
	
	int n;
	int[][] board;
	Set<Pair<Integer, Integer>> edges;
	boolean VALID;
	
	private ArrayList<Integer>[] validateEdges(boolean verbose)
	{
		// check whether edges are valid
		for(Pair<Integer,Integer> e : edges)
		{
			int diff = e.getSecond() - e.getFirst();
			
			if(diff == 1)
			{
				if(e.getFirst() % n == n-1)
				{
					VALID = false;
					if(verbose)
						System.out.println("Invalid edge from " + e.getFirst() + " to " + e.getSecond());
				}
			}
			else if (diff != n)
			{
				VALID = false;
				if(verbose)
					System.out.println("Invalid edge from " + e.getFirst() + " to " + e.getSecond());
			}
			
		}
		
		// build graph, check every vertex has 0 or 2 edges
		ArrayList<Integer>[] graph = new ArrayList[n*n];
		for(int i=0;i<n*n;++i) graph[i] = new ArrayList<>();
		for(Pair<Integer,Integer> e : edges)
		{
			graph[e.getFirst()].add(e.getSecond());
			graph[e.getSecond()].add(e.getFirst());
		}
		
		for(int i=0;i<n*n;++i)
		{
			if(graph[i].size() != 0 && graph[i].size() != 2)
			{
				VALID = false;
				if(verbose)
					System.out.println("Vertex " + i + " is adjacent to " + graph[i].size() + " edges");
			}
		}
		
		return graph;
	}
	
	private void validateLoop(ArrayList<Integer>[] graph, boolean verbose)
	{
		// check whether a single loop is present
		int root = 0;
		while(graph[root].size() == 0) root++;
		
		boolean[] visited = new boolean[n*n];
		for(int i=0;i<n*n;++i)
			visited[i] = false;
		
		int cur = root, last = -1;
		while(!visited[cur])
		{
			visited[cur] = true;
			if(graph[cur].get(0) == last)
			{
				last = cur;
				cur = graph[cur].get(1);
			}
			else
			{
				last = cur;
				cur = graph[cur].get(0);
			}
		}
		
		// since validateEdges passed, each non-trivial component is a cycle. Thus we definitely covered a valid loop.
		
		for(int i=0;i<n*n;++i)
			if(!visited[i] && graph[i].size() != 0)
			{
				VALID = false;
				if(verbose) System.out.println("Graph is more than 1 component");
				break;
			}
	}
	
	private void validateBoard(boolean verbose)
	{
		// check whether slitherlink board is satisfied
		int[][] values = new int[n-1][n-1];
		for(int i=0;i<n*n-2*n+1;++i) values[i/(n-1)][i%(n-1)] = 0;
		for(Pair<Integer,Integer> e : edges)
		{
			int a = e.getFirst(), b = e.getSecond();
			
			if(b-a==1) // horizontal
			{
				if(a>n-1) values[a/n-1][a%n]++;
				if(a+n<n*n) values[a/n][a%n]++;
			}
			else // vertical
			{
				if(a%n>0) values[a/n][a%n-1]++;
				if(a%n<n-1) values[a/n][a%n]++;
			}
			
		}
		
		for(int i=0;i<n-1;++i)
		for(int k=0;k<n-1;++k)
			if(board[i][k] != -1)
			{
				if(values[i][k] != board[i][k])
				{
					VALID = false;
					if(verbose) System.out.println("Slitherink requires " + board[i][k] + " at " + i + "," + k + " but " + values[i][k] + " instead");
				}
			}

	}
	
	public boolean validate(boolean verbose)
	{
		VALID = true;
		
		if(edges.isEmpty())
		{
			System.out.println("No solution is present!");
			return false;
		}
		
		ArrayList<Integer>[] graph = validateEdges(verbose);
		if(VALID) validateLoop(graph, verbose);
		if(VALID) validateBoard(verbose);
		
		if(verbose) System.out.print("Solution valid: " + VALID);
		return VALID;
	}
	
	public Validator(String input, String output)
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
		
		edges = new HashSet<>();
		
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
	}
	
	public Validator(Slitherlink model)
	{
		n = model.n;
		edges = model.getEdges();
		board = model.board;
	}
	
}
