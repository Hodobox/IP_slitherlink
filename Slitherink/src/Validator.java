import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Scanner;
import java.util.Set;

import javax.swing.JFrame;

import org.jgrapht.alg.util.Pair;
import java.awt.*;

public class Validator {
	
	int n;
	int[][] board;
	Set<Pair<Integer, Integer>> edges;
	boolean VALID;
	String fname;
	
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
	
	private class GUI extends JFrame
	{
		
		final int BORDER = 30;
		final int SQUARESIZE = 50;
		final int OFFSET = 10;
		final int FONTSIZE = 24;
		final int MARKSIZE = 6;
		
		public GUI(String title)
		{
			super(title);
			setSize(new Dimension(Validator.this.n*SQUARESIZE+2*OFFSET, Validator.this.n*SQUARESIZE+BORDER+2*OFFSET));
			setVisible(true);
			setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		}
		
		void drawPuzzle(Graphics g) {
			  //create new Graphics2D instance using Graphics parent
			  Graphics2D g2 = (Graphics2D) g;
			  //set color
			  g2.setColor(Color.BLACK);
			  g2.setFont(new Font("TimesRoman", Font.BOLD, FONTSIZE));
			 
			  // draw the grid
			  for(int i=0;i<Validator.this.n;++i)
			  {
				  for(int j=0;j<Validator.this.n;++j)
				  {
					  g.drawOval(i*SQUARESIZE+OFFSET, j*SQUARESIZE+BORDER+OFFSET, MARKSIZE, MARKSIZE);
					  g.fillOval(i*SQUARESIZE+OFFSET, j*SQUARESIZE+BORDER+OFFSET, MARKSIZE, MARKSIZE);
				  }
			  }
			  
			  final int FONTWIDTH = g.getFontMetrics().stringWidth("3");
			  
			  // draw the clues
			  for(int i=0;i<Validator.this.n-1;++i)
			  {
				  for(int j=0;j<Validator.this.n-1;++j)
				  {
					  int clue = Validator.this.board[j][i];
					  if(clue != -1)
					  {
						  g.drawChars(new char[] {(char) (clue+'0')}, 0, 1, i*SQUARESIZE + SQUARESIZE/2 - FONTWIDTH/2 + OFFSET, j*SQUARESIZE+ SQUARESIZE/2 + FONTSIZE/2 + BORDER + OFFSET);
					  }
				  }
			  }
			  
			  // draw the edges
			  for(Pair<Integer,Integer> e : edges)
			  {
				  int x = e.getFirst() % n, y = e.getFirst() / n;
				  int X = e.getSecond() % n, Y = e.getSecond() / n;
				  g.drawLine(x*SQUARESIZE + MARKSIZE/2 + OFFSET, y*SQUARESIZE + MARKSIZE/2 + OFFSET + BORDER, X*SQUARESIZE + MARKSIZE/2 + OFFSET, Y*SQUARESIZE + MARKSIZE/2 + OFFSET + BORDER);
			  }
			  
			}
		
	    public void paint(Graphics g) {
	        super.paint(g);
	        drawPuzzle(g);
	      }
		
	}
	
	public void show()
	{
		GUI g = new GUI(fname);
	}
	
	public Validator(String input, String output)
	{
		fname = output;
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
		fname = model.fname;
		n = model.n;
		edges = model.getEdges();
		board = model.board;
	}
	
}
