import org.chocosolver.solver.search.strategy.Search;
import org.chocosolver.solver.variables.*;

public class SetNeighborModel extends Slitherlink
{
	boolean solve()
	{
	    solver.setSearch(Search.minDomLBSearch(solution));
		return solver.solve();
	}
	
	IntVar[] indices;
	IntVar[] diff;
	IntVar circuitSize;
	BoolVar[][] edgeMatrix;
    SetVar neiLeft, neiRight, neiInner, neiUp, neiDown;
    IntVar[][] squares;

    public SetNeighborModel(String fname)
    {
    	super(fname);
    	
    	solution = model.intVarArray("solution", n*n, 0, n*n-1);
        indices = model.intVarArray("indices", n*n, 0, n*n-1);
        for(int i=0;i<n*n;++i)
            model.arithm(indices[i],"=",i).post();

        diff = model.intVarArray("diff",n*n,-n,n);

        for(int i=0;i<n*n;++i)
            model.arithm(solution[i],"-",indices[i],"=",diff[i]).post();

        circuitSize = model.intVar("circuitSize", 4, n*n);
        model.subCircuit(solution, 0, circuitSize).post();

        neiLeft = model.setVar("neiLeft", new int[]{0,1,n,-n});
        neiRight = model.setVar("neiRight", new int[]{0,-1,n,-n});
        neiInner = model.setVar("neiInner", new int[]{0,-1,1,n,-n});
        neiUp = model.setVar("neiUp", new int[]{0,-1,1,n});
        neiDown = model.setVar("neiDown", new int[]{0,-1,1,-n});

        for(int i=0;i<n*n;++i)
            if(i%n==0)
                model.member(diff[i],neiLeft).post();
            else if (i%n == n-1)
                model.member(diff[i],neiRight).post();
            else if (i<n)
                model.member(diff[i],neiUp).post();
            else if (i>=n*n-n)
                model.member(diff[i],neiDown).post();
            else
                model.member(diff[i],neiInner).post();
            

        squares = model.intVarMatrix("squares", n-1, n-1, 0, 3);

        for(int i=0;i<n-1;++i)
        for(int k=0;k<n-1;++k)
            if(board[i][k] != -1)
                model.arithm(squares[i][k],"=",board[i][k]).post();

        edgeMatrix = model.boolVarMatrix("edgeMatrix",n*n,n*n);

        for(int i=0;i<n*n;++i)
        for(int k=0;k<n*n;++k)
            edgeMatrix[i][k] = model.or( model.arithm(solution[i],"=",k), model.arithm(solution[k],"=",i) ).reify();

        for(int i=0;i<n-1;++i)
        for(int k=0;k<n-1;++k)
        {
            int ulcorner = i*n + k;
            if(board[i][k] != -1)
            model.sum(new BoolVar[] {edgeMatrix[ulcorner][ulcorner+1],edgeMatrix[ulcorner][ulcorner+n],edgeMatrix[ulcorner+1][ulcorner+n+1],edgeMatrix[ulcorner+n][ulcorner+n+1]},"=",squares[i][k]).post();
        }
        
    }
    
}
