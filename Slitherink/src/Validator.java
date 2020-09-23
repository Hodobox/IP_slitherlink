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
	
	private int validateLoop(ArrayList<Integer>[] graph, boolean verbose)
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
		
		if(cur != root)
		{
			VALID = false;
			if(verbose) System.out.println("Edge from " + root + " but cycle reached at " + cur);
		}
		
		for(int i=0;i<n*n;++i)
			if(!visited[i] && graph[i].size() != 0)
			{
				VALID = false;
				if(verbose) System.out.println("Graph is more than 1 component");
				break;
			}
		
		return root;
	}
	
	private void validateBoard(int root, ArrayList<Integer>[] graph, boolean verbose)
	{
		// check whether slitherlink board is satisfied
	}
	
	public boolean validate(boolean verbose)
	{
		VALID = true;
		
		if(edges.isEmpty())
		{
			System.out.println("No solution is present!");
			return false;
		}
		
		int root = -1;
		ArrayList<Integer>[] graph = validateEdges(verbose);
		if(VALID) root = validateLoop(graph, verbose);
		if(VALID) validateBoard(root, graph, verbose);
		
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
	}
	
	public Validator(Slitherlink model)
	{
		n = model.n;
		edges = model.getEdges();
		board = model.board;
	}
	
}
