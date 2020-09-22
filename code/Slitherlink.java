import java.io.*;
import java.util.*;
import org.chocosolver.solver.Model;
import org.chocosolver.solver.Solver;
import org.chocosolver.solver.variables.IntVar;
import org.chocosolver.solver.search.strategy.Search;
import org.chocosolver.solver.variables.*;

class Pair
{
    // Return a map entry (key-value pair) from the specified values
    public static <T, U> Map.Entry<T, U> of(T first, U second)
    {
        return new AbstractMap.SimpleEntry<>(first, second);
    }
}

public class Slitherlink {

    Model model;
    Solver solver;
    
    int puzzleSize; // length of side of puzzle
    int nodeN; // number of nodes in the puzzle 
    IntVar puzzleSizeIntVar;
    int[][] board; // the puzzle

    IntVar[] indices, iQuotients, iModulos;
    IntVar[] solQuotients, solModulos;
    IntVar[] diffQuotients, diffModulos;
    IntVar[] solution; // a subcircuit of this will be the solution

    IntVar circuitSize; // size of the subcircuit
    IntVar[][] squares;

    BoolVar[][] edgeMatrix;

    boolean verbose;
    
    public Slitherlink(int puzzleSize, int[][] board){

    // save all the parameters I need
	this.puzzleSize = puzzleSize;
    this.board = board;

    nodeN = puzzleSize * puzzleSize;

	model  = new Model();
	solver = model.getSolver();

    puzzleSizeIntVar = model.intVar("nodeN", puzzleSize);

    solution = model.intVarArray("solution", nodeN, 0, nodeN-1);
    indices = model.intVarArray("indices", nodeN, 0, nodeN-1);
    for(int i=0;i<nodeN;++i)
        model.arithm(indices[i],"=",i).post();

    iQuotients = model.intVarArray("iQuotients", nodeN, 0, puzzleSize-1);
    for(int i=0;i<nodeN;++i)
        model.arithm(iQuotients[i],"=",indices[i],"/",puzzleSize).post();
    iModulos = model.intVarArray("iModulos", nodeN, 0, puzzleSize-1);
    for(int i=0;i<nodeN;++i)
        model.mod(indices[i],puzzleSizeIntVar,iModulos[i]).post();

    solQuotients = model.intVarArray("solQuotients", nodeN, 0, puzzleSize-1);
    for(int i=0;i<nodeN;++i)
        model.arithm(solQuotients[i],"=",solution[i],"/",puzzleSize).post();
    solModulos = model.intVarArray("solModulos", nodeN, 0, puzzleSize-1);
    for(int i=0;i<nodeN;++i)
        model.mod(solution[i],puzzleSizeIntVar,solModulos[i]).post();


    diffModulos = model.intVarArray("diffModulos",nodeN,0,puzzleSize-1);
    for(int i=0;i<nodeN;++i)
        model.distance(solModulos[i],iModulos[i],"=",diffModulos[i]).post();
    diffQuotients = model.intVarArray("diffQuotients",nodeN,0,puzzleSize-1);
    for(int i=0;i<nodeN;++i)
        model.distance(solQuotients[i],iQuotients[i],"=",diffQuotients[i]).post();

    for(int i=0;i<nodeN;++i)
        model.arithm(diffModulos[i],"+",diffQuotients[i],"<",2).post();

    circuitSize = model.intVar("circuitSize", 4, nodeN);
    model.subCircuit(solution, 0, circuitSize).post();

    squares = model.intVarMatrix("squares", puzzleSize-1, puzzleSize-1, 0, 4);

    for(int i=0;i<puzzleSize-1;++i)
    for(int k=0;k<puzzleSize-1;++k)
        if(board[i][k] != -1)
            model.arithm(squares[i][k],"=",board[i][k]).post();

    edgeMatrix = model.boolVarMatrix("edgeMatrix",nodeN,nodeN);

    for(int i=0;i<nodeN;++i)
    for(int k=0;k<nodeN;++k)
        edgeMatrix[i][k] = model.or( model.arithm(solution[i],"=",k), model.arithm(solution[k],"=",i) ).reify();

    for(int i=0;i<puzzleSize-1;++i)
    for(int k=0;k<puzzleSize-1;++k)
    {
        int ulcorner = i*puzzleSize + k;
        model.sum(new BoolVar[] {edgeMatrix[ulcorner][ulcorner+1],edgeMatrix[ulcorner][ulcorner+puzzleSize],edgeMatrix[ulcorner+1][ulcorner+puzzleSize+1],edgeMatrix[ulcorner+puzzleSize][ulcorner+puzzleSize+1]},"=",squares[i][k]).post();
    }

    //minCreditsInAllPeriods = model.intVar("minCredits",minCredits,maxCredits);
    //model.arithm(assigned[i],">",assigned[ prereq[i].get(k) ]).post();
    //this.creditload = model.intVarArray("creditload",nPeriods,minCredits,maxCredits);
    //model.min(minCreditsInAllPeriods,creditload).post();
    //model.max(maxCreditsInAllPeriods,creditload).post();

    }

    void optimize(){
	//model.setObjective(Model.MINIMIZE,imbalance);

	/* while (solver.solve()){
        for(int i=0;i<name.length;i++)
            this.period[i] = this.assigned[i].getValue();
	    
        // Force the solver to find a solution with a smaller imbalance than it just did
        model.arithm(imbalance,"<",imbalance.getValue()).post();
    } */
	}
    

    void solve(){
	solver.solve();
    //for(int i=0;i<name.length;i++)
    //    this.period[i] = this.assigned[i].getValue();
    }

    void show(){

        
        Set<Map.Entry<Integer, Integer>> edges = new HashSet<>();

        boolean found = false;
        for(int i=0;i<nodeN;++i)
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
                    edges.add(Pair.of(a,b));
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

        for(int r=0;r<puzzleSize;++r)
        {
            for(int c=0;c<puzzleSize;++c)
            {
                System.out.print(".");
                if(c<puzzleSize-1)
                {
                    if( edges.contains(Pair.of(r*puzzleSize+c,r*puzzleSize+c+1) ) )
                        System.out.print("-");
                    else System.out.print(" ");
                }
            } System.out.println();

            if(r+1 != puzzleSize)
            {
                for(int c=0;c<puzzleSize;++c)
                {
                    
                    if( edges.contains(Pair.of(r*puzzleSize+c, (r+1)*puzzleSize+c) ))
                        System.out.print("|");
                    else System.out.print(" ");

                    if(c<puzzleSize-1 && board[r][c] != -1)
                        System.out.print(board[r][c]);
                    else System.out.print(" ");

                } System.out.println();
            }
        }

        /*if(verbose)
        {
            for(int i=0;i<nodeN;++i)
            {
                for(int k=0;k<nodeN;++k)
                {
                    if(edgeMatrix[i][k].getValue()==1) 
                        System.out.print("#");
                    else System.out.print(".");
                } System.out.println();
            }
        }*/
    
	   solver.printShortStatistics();
    }
}	
	
