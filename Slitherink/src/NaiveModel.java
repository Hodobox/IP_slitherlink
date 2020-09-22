import java.io.IOException;

import org.chocosolver.solver.Model;
import org.chocosolver.solver.search.strategy.Search;
import org.chocosolver.solver.variables.*;

public class NaiveModel extends Slitherlink
{
    int nodeN; // number of nodes in the puzzle 
    IntVar nIntVar;
 
    IntVar[] indices, iQuotients, iModulos;
    IntVar[] solQuotients, solModulos;
    IntVar[] diffQuotients, diffModulos;

    IntVar circuitSize; // size of the subcircuit
    IntVar[][] squares;

    BoolVar[][] edgeMatrix;

    boolean verbose;
    
    public NaiveModel(String fname)
    {
    	try {
			this.read(fname);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	    // save all the parameters I need
    	
	    nodeN = n * n;
	
		model  = new Model();
		solver = model.getSolver();
	
	    nIntVar = model.intVar("nodeN", n);
	
	    solution = model.intVarArray("solution", nodeN, 0, nodeN-1);
	    indices = model.intVarArray("indices", nodeN, 0, nodeN-1);
	    for(int i=0;i<nodeN;++i)
	        model.arithm(indices[i],"=",i).post();
	
	    iQuotients = model.intVarArray("iQuotients", nodeN, 0, n-1);
	    for(int i=0;i<nodeN;++i)
	        model.arithm(iQuotients[i],"=",indices[i],"/",n).post();
	    iModulos = model.intVarArray("iModulos", nodeN, 0, n-1);
	    for(int i=0;i<nodeN;++i)
	        model.mod(indices[i],nIntVar,iModulos[i]).post();
	
	    solQuotients = model.intVarArray("solQuotients", nodeN, 0, n-1);
	    for(int i=0;i<nodeN;++i)
	        model.arithm(solQuotients[i],"=",solution[i],"/",n).post();
	    solModulos = model.intVarArray("solModulos", nodeN, 0, n-1);
	    for(int i=0;i<nodeN;++i)
	        model.mod(solution[i],nIntVar,solModulos[i]).post();
	
	
	    diffModulos = model.intVarArray("diffModulos",nodeN,0,n-1);
	    for(int i=0;i<nodeN;++i)
	        model.distance(solModulos[i],iModulos[i],"=",diffModulos[i]).post();
	    diffQuotients = model.intVarArray("diffQuotients",nodeN,0,n-1);
	    for(int i=0;i<nodeN;++i)
	        model.distance(solQuotients[i],iQuotients[i],"=",diffQuotients[i]).post();
	
	    for(int i=0;i<nodeN;++i)
	        model.arithm(diffModulos[i],"+",diffQuotients[i],"<",2).post();
	
	    circuitSize = model.intVar("circuitSize", 4, nodeN);
	    model.subCircuit(solution, 0, circuitSize).post();
	
	    squares = model.intVarMatrix("squares", n-1, n-1, 0, 4);
	
	    for(int i=0;i<n-1;++i)
	    for(int k=0;k<n-1;++k)
	        if(board[i][k] != -1)
	            model.arithm(squares[i][k],"=",board[i][k]).post();
	
	    edgeMatrix = model.boolVarMatrix("edgeMatrix",nodeN,nodeN);
	
	    for(int i=0;i<nodeN;++i)
	    for(int k=0;k<nodeN;++k)
	        edgeMatrix[i][k] = model.or( model.arithm(solution[i],"=",k), model.arithm(solution[k],"=",i) ).reify();
	
	    for(int i=0;i<n-1;++i)
	    for(int k=0;k<n-1;++k)
	    {
	        int ulcorner = i*n + k;
	        model.sum(new BoolVar[] {edgeMatrix[ulcorner][ulcorner+1],edgeMatrix[ulcorner][ulcorner+n],edgeMatrix[ulcorner+1][ulcorner+n+1],edgeMatrix[ulcorner+n][ulcorner+n+1]},"=",squares[i][k]).post();
	    }
    }
    
}
