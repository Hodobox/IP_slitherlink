import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;

import org.jgrapht.alg.util.Pair;

public class Generator {
	
	private int n;
	private int[][] board;
	
	public Generator(int n) 
	{
		this.n = n;
	}
	
	public void save(String fname)
	{
		System.out.println(fname);
		try {
	      	File file = new File(fname);
	      	file.createNewFile();
	      
	        System.out.println("File created: " + file.getName());
	        FileWriter myWriter = new FileWriter(fname);
	        myWriter.write(this.n + "\n");
	        for(int i=0;i<this.n-1;++i)
	        {
	        	for(int k=0;k<this.n-1;++k)
	        	{
	        		if(k>0) myWriter.write(" ");
	        		myWriter.write(this.board[i][k] + "");
	        	}
	        	myWriter.write("\n");
	        }
	        myWriter.close();
	    } catch (IOException e) {
	      e.printStackTrace();
	    }
	}
	
	private static int[] dx = {-1,1,0,0}, dy = {0,0,-1,1};
	
	// returns the set of squares which are inside the loop
	private Set<Pair<Integer,Integer>> make_loop(int length)
	{
		
		Random rand = new Random();
		rand.setSeed(47);
		
		Set<Pair<Integer, Integer>> avail = new HashSet<>(), expand = new HashSet<>(), dead = new HashSet<>();
		ArrayList<Pair<Integer,Integer>> expand_list = new ArrayList<>();
		
		for(int x=0;x<n-1;++x)
			for(int y=0;y<n-1;++y)
			{
				avail.add(Pair.of(x,y));
			}
		
		int sx = rand.nextInt(n-1), sy = rand.nextInt(n-1);
		avail.remove(Pair.of(sx, sy));
		expand.add(Pair.of(sx, sy));
		expand_list.add(Pair.of(sx, sy));
		System.out.println(sx + "," + sy + " = 4");
		
		int loop_length = 4;
		
		while(!expand_list.isEmpty() && loop_length < length)
		{
			// take a random expandable square
			int idx = rand.nextInt(expand_list.size());
			
			int x = expand_list.get(idx).getFirst(), y = expand_list.get(idx).getSecond();
			
			ArrayList<Pair<Integer,Integer>> valid = new ArrayList<>();
			
			// try all neighbors
			for(int dir=0;dir<4;++dir)
			{
				int nx = x + dx[dir];
				int ny = y + dy[dir];
				
				if(nx < 0 || ny < 0 || nx >= this.n-1 || ny >= this.n-1 || !avail.contains(Pair.of(nx, ny)) ) continue;
				
				// not valid if 'opposite' or 'kitty-corner' square is not available
				
				// opposite: repeat step in direction
				int oppx = nx + dx[dir], oppy = ny + dy[dir];
				if(oppx >= 0 && oppy >= 0 && oppx < this.n-1 && oppy < this.n-1 && !avail.contains(Pair.of(oppx, oppy))) continue;
				
				// kitty-corners: opposite + parallel step in either direction
				boolean bad = false;
				for(int step=-1;step<=1;++step)
				{
					if(step==0) continue;
					
					int kcx = oppx, kcy = oppy;
					if(kcx==nx) kcx += step;
					else kcy += step;
					
					boolean corner_exists = (kcx >= 0 && kcy >= 0 && kcx < this.n-1 && kcy < this.n-1 && !avail.contains(Pair.of(kcx, kcy)));
					
					int holex = nx, holey = ny;
					if(holex==x) holex += step;
					else holey += step;
					
					boolean hole_exists = (holex >= 0 && holey >= 0 && holex < this.n-1 && holey < this.n-1 && avail.contains(Pair.of(holex, holey)));
					
					//System.out.println(x + "," + y + " -> " + nx + "," + ny + ": " + kcx + "," + kcy + "=" + corner_exists + "; " + holex + "," + holey + "=" + hole_exists);
					
					bad |= (corner_exists && hole_exists);
				}
				
				if(bad) continue;
				
				// three neighbors
				int neighbors = 0;
				for(int ndir=0; ndir<4;++ndir)
				{
					int neix = nx + dx[ndir];
					int neiy = ny + dy[ndir];
					if(neix<0 || neiy < 0 || neix >= this.n-1 || neiy >= this.n-1 || avail.contains(Pair.of(neix, neiy))) continue;
					neighbors += 1;
				}
				
				if(neighbors >= 3) continue;
				
				valid.add(Pair.of(nx, ny));
			}
			
			// expandable vertex is dead
			if(valid.isEmpty())
			{
				dead.add(expand_list.get(idx));
				expand_list.set(idx, expand_list.get(expand_list.size()-1));
				expand_list.remove(expand_list.size()-1);
				continue;
			}
			
			// pick a random neighbor
			Pair<Integer,Integer> chosen = valid.get(rand.nextInt(valid.size()));
			int cx = chosen.getFirst(), cy = chosen.getSecond();
			
			// calculate difference in length
			// before: number of expandable/dead neighbors of the chosen square
			int before = 0;
			for(int dir=0;dir<4;++dir)
			{
				int nx = cx + dx[dir];
				int ny = cy + dy[dir];
				if(nx<0 || ny < 0 || nx >= this.n-1 || ny >= this.n-1 || avail.contains(Pair.of(nx, ny))) continue;
				before += 1;
			}
			
			//after: 4 - before (no edges between new square and squares that were taken, the rest were available and so have an edge)
			int after = 4 - before;
			loop_length += after - before;
			
			// expand to neighbor
			avail.remove(chosen);
			expand.add(chosen);
			expand_list.add(chosen);
			
			System.out.println(chosen.getFirst() + "," + chosen.getSecond() + " = " + loop_length);
		}
		
		System.out.println(loop_length);
		return dead;
	}
	
	private void boardify_loop(Set<Pair<Integer,Integer>> loop)
	{
		this.board = new int[this.n-1][this.n-1];
		for(int x=0;x<this.n-1;++x)
		for(int y=0;y<this.n-1;++y)
		{
			this.board[x][y] = 0;
			
			boolean is_inner = loop.contains(Pair.of(x, y));
			for(int dir=0;dir<4;++dir)
			{
				int nx = x + dx[dir];
				int ny = y + dy[dir];
				
				// there is an edge between (x,y) and (nx,ny) if (nx,ny) is not inside the loop
				boolean neighbor_is_inner = loop.contains(Pair.of(nx, ny));
				
				if(is_inner ^ neighbor_is_inner) 
					this.board[x][y] += 1;
			}
		}
	}
	
	public void generate_single(String fname, boolean save)
	{		
		// generate loop
		Set<Pair<Integer,Integer>> loop = make_loop(this.n*this.n*2);
		
		// generate board from loop
		this.boardify_loop(loop);
		
		// save board
		if(save)
		{
			this.save(fname);
		}
		
	}
	
	public static void generate(String dir)
	{
		Generator g = new Generator(10);
		g.generate_single(dir + "10.in", true);
	}
}
