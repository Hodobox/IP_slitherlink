import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;

import org.jgrapht.alg.util.Pair;

public class Generator {
	
	private int n;
	private int seed;
	private int[][] board;
	private boolean verbose = false;
	private int length_limit = -1;
	private int remove_limit = -1;
	
	public Generator(int n,int seed) 
	{
		this.n = n;
		this.seed = seed;
	}
	
	public void save(String fname)
	{
		try {
	      	File file = new File(fname);
	      	file.createNewFile();
	      	
	      	if(this.verbose)
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
		if(this.seed != -1) rand.setSeed(this.seed);
		
		Set<Pair<Integer, Integer>> avail = new HashSet<>(), expand = new HashSet<>(), dead = new HashSet<>();
		ArrayList<Pair<Integer,Integer>> expand_list = new ArrayList<>();
		
		for(int x=0;x<n-1;++x)
			for(int y=0;y<n-1;++y)
			{
				avail.add(Pair.of(x,y));
			}
		
		int sx = rand.nextInt(n-1), sy = rand.nextInt(n-1);
		avail.remove(Pair.of(sx, sy));
		//System.out.println(sx + " " + sy);
		expand.add(Pair.of(sx, sy));
		expand_list.add(Pair.of(sx, sy));
		
		int loop_length = 4;
		if(length == -1) length = 1000000;
		
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
				
				// many neighbors
				int neighbors = 0;
				for(int ndir=0; ndir<4;++ndir)
				{
					int neix = nx + dx[ndir];
					int neiy = ny + dy[ndir];
					if(neix<0 || neiy < 0 || neix >= this.n-1 || neiy >= this.n-1 || avail.contains(Pair.of(neix, neiy))) continue;
					neighbors += 1;
				}
				
				if(neighbors >= 3) continue;
				if(neighbors == 2 && rand.nextDouble() > 0.3) continue;
				
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
			
			//after: (4 - before) - before (edges that exist -> won't, edges that don't -> will)
			int after = 4 - before;
			loop_length += after - before;
			
			// expand to neighbor
			avail.remove(chosen);
			expand.add(chosen);
			expand_list.add(chosen);

		}
		
		while(!expand_list.isEmpty())
		{
			dead.add(expand_list.get(expand_list.size()-1));
			expand_list.remove(expand_list.size()-1);
		}
		
		//System.out.println(loop_length);
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
	
	// returns name of solution file if save = true, otherwise null
	private String remove_clues(String fname,boolean save)
	{
		Random rand = new Random();
		if(this.seed != -1) rand.setSeed(this.seed);
		
		int removed_clues = 0;
		int removed_limit = this.remove_limit;
		if(removed_limit == -1) removed_limit = this.n*this.n;
		int failed_attempts = 0;
		String solfname = null;
		Set<Pair<Integer,Integer>> unremovable = new HashSet<>();
		while(failed_attempts < 5 && removed_clues < removed_limit)
		{
			// pick random clue to remove
			int clues_present = 0, remove_x = -1, remove_y = -1;
			for(int i=0;i<this.n-1;i++)
			for(int j=0;j<this.n-1;j++)
			{
				if(this.board[i][j] != -1 )
				{
					if(unremovable.contains(Pair.of(i, j))) continue;
					clues_present += 1;
					if(rand.nextDouble() <= 1.0 / clues_present)
					{
						remove_x = i;
						remove_y = j;
					}
				}
			}
			
			if(clues_present == 0)
				break;
			
			
			// remove it, check if we have more than 1 solution
			int val = this.board[remove_x][remove_y];
			this.board[remove_x][remove_y] = -1;
			
			this.save(fname + ".gen");
			Slitherlink solver = new SetNeighborModel(fname + ".gen");
			File tmpfile = new File(fname + ".gen");
			tmpfile.delete();
			
			solver.solver.limitTime(20000);
			solver.verbose = false;
			
			// cant even solve, put it back
			if(!solver.solve())
			{
				if(this.verbose)
					System.out.println("cant solve");
				failed_attempts += 1;
				this.board[remove_x][remove_y] = val;
				unremovable.add(Pair.of(remove_x, remove_y));
				continue;
			}
			else if (save)
			{
				// save the solution first time we get one
				solfname = fname + "." + solver.getName() + ".out";
				solver.save(solfname);
				save = false;
			}
			
			if(!solver.solve()) // opposite direction
			{
				if(this.verbose)
					System.out.println("cant solve");
				failed_attempts += 1;
				this.board[remove_x][remove_y] = val;
				unremovable.add(Pair.of(remove_x, remove_y));
				continue;
			}
			
			// look for second solution
			if(solver.solve())
			{
				if(this.verbose)
					System.out.println("two solutions");
				// found one, put it back
				failed_attempts += 1;
				this.board[remove_x][remove_y] = val;
				unremovable.add(Pair.of(remove_x, remove_y));
				continue;
			}
			
			if(this.verbose)
				System.out.println("ok removed " + remove_x + "," + remove_y);
			// didn't find one, good!
			failed_attempts = 0;
			removed_clues += 1;
			
		}
		
		return solfname;
	}
	
	public void generate_single(String fname, boolean save)
	{		
		// generate loop
		Set<Pair<Integer,Integer>> loop = make_loop(this.length_limit);
		
		// generate board from loop
		this.boardify_loop(loop);
		
		// remove clues
		String solfname = this.remove_clues(fname,save);
		
		// save board
		if(save)
			this.save(fname + ".in");
		
		if(solfname != null && this.verbose)
		{
			Validator v = new Validator(fname + ".in", solfname);
			v.show();
		}
	}
	
	public static void generate(String dir,int n,int num,String suffix, int length, double clues)
	{
		for(int i=0;i<num;++i)
		{
			Generator g = new Generator(n,i);
			g.length_limit = length;
			if(clues != -1.0)
				g.remove_limit = (int) Math.floor((n-1)*(n-1) * clues);
			else
				g.remove_limit = -1;
			String fname = dir + n + "_" + i;
			if(suffix != null) fname += "_" + suffix;
			g.generate_single(fname, true);
			System.out.println( (i+1) + "/" + num);
		}
	}
}
