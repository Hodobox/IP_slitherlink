import java.util.ArrayList;

import org.chocosolver.solver.search.strategy.Search;
import org.chocosolver.solver.variables.*;
import org.chocosolver.util.tools.ArrayUtils;

public class PatModel extends Slitherlink{

	boolean solve()
	{
		solver.setSearch(Search.minDomLBSearch(solution));
		return solver.solve();
	}
	
	public String getName() 
	{
		return PatModel.class.getSimpleName();
	}
	
	IntVar[][] v;
    IntVar[][] A;
    IntVar solutionLength;
    
	public PatModel(String fname)
	{
		super(fname);
		v = new IntVar[n][n];
	    A = new IntVar[n*n][n*n];
		for (int i=0;i<n;i++)
	        for (int j=0;j<n;j++){
	        ArrayList<Integer> dom = new ArrayList<Integer>();
	        if (i > 0) dom.add((i-1)*n + j);   // above
	        if (j > 0) dom.add(i*n + j - 1);   // left
	        dom.add(i*n + j);                  // itself
	        if (j < n-1) dom.add(i*n + j + 1); // right
	        if (i < n-1) dom.add((i+1)*n + j); // below;
	        int[] domain = new int[dom.size()];
	        int k = 0;
	        for (int x : dom) domain[k++] = x;
	        v[i][j] = model.intVar("v["+ i +"]["+ j +"]",domain);
	        }
		
		solutionLength = model.intVar("solution length",4,n*n);
	    solution = ArrayUtils.flatten(v);
	    model.subCircuit(solution,0,solutionLength).post();
	    
	    //
	    // Link the solution variables with the adjaceny matrix A such that
	    // A[i][j] = 1 <-> solution[i] = j
	    //
	    for (int i=0;i<n*n;i++){
	        int ub = solution[i].getUB();
	        for (int j = solution[i].getLB();j<=ub;j=solution[i].nextValue(j))
	        if (i != j){
	            A[i][j] = model.intVar("A["+ i +"]["+ j +"]",0,1);
	            model.ifOnlyIf(model.arithm(A[i][j],"=",1),model.arithm(solution[i],"=",j));
	        }
	    }
	    
	    //
	    // given an n-1 by n-1 integer array of counts of edges around a square constrain
	    // the sum of edges in A accordingly ... non-trivial
	    //
	    
	    int k = 0;
	    for (int i=0;i<n-1;i++){
	        for (int j=0;j<n-1;j++){
	        if (board[i][j] != -1){
	            IntVar[] edge = new IntVar[8];
	            // clockwise
	            edge[0] = A[k][k+1];
	            edge[1] = A[k+1][k+1+n];
	            edge[2] = A[k+1+n][k+n];
	            edge[3] = A[k+n][k];
	            // anticlockwise
	            edge[4] = A[k][k+n];
	            edge[5] = A[k+n][k+n+1];
	            edge[6] = A[k+n+1][k+1];
	            edge[7] = A[k+1][k];
	            model.sum(edge,"=",board[i][j]).post();
	        }
	        k++;
	        }
	        k++;
	    }   
		
	}

}
