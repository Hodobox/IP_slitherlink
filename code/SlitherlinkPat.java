import java.util.*;
import org.chocosolver.solver.Model;
import org.chocosolver.solver.Solver;
import org.chocosolver.solver.variables.IntVar;
import org.chocosolver.util.tools.ArrayUtils;
import org.chocosolver.solver.search.strategy.Search;

public class SlitherlinkPat {

    int n;
    int[][] count;
    Model model;
    Solver solver;
    IntVar[][] v;
    IntVar[][] A;
    IntVar[] tour;
    IntVar tourLength;
    boolean trace;

    public SlitherlinkPat(int n,int[][] count){
    this.n = n;
    this.count = count;
    model  = new Model("Slither Link");
    solver = model.getSolver();
    v      = new IntVar[n][n];
    A      = new IntVar[n*n][n*n];
    trace  = false;
    
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
    
    if (trace)
        for (int i=0;i<n;i++){
        for (int j=0;j<n;j++)
            System.out.print(v[i][j] +" ");
        System.out.println();
        }

    tourLength = model.intVar("tour length",4,n*n);
    tour = ArrayUtils.flatten(v);
    model.subCircuit(tour,0,tourLength).post();

    //
    // Link the tour variables with the adjaceny matrix A such that
    // A[i]j] = 1 <-> tour[i] = j
    //
    for (int i=0;i<n*n;i++){
        int ub = tour[i].getUB();
        for (int j = tour[i].getLB();j<=ub;j=tour[i].nextValue(j))
        if (i != j){
            A[i][j] = model.intVar("A["+ i +"]["+ j +"]",0,1);
            model.ifOnlyIf(model.arithm(A[i][j],"=",1),model.arithm(tour[i],"=",j));
            if (trace) System.out.println("A["+ i +"]["+ j +"] = 1 <-> tour["+ i +"] = "+ j);
        }
    }

    //
    // given an n-1 by n-1 integer array of counts of edges around a square constrain
    // the sum of edges in A accordingly ... non-trivial
    //
    
    int k = 0;
    for (int i=0;i<n-1;i++){
        for (int j=0;j<n-1;j++){
        if (count[i][j] != -1){
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
            model.sum(edge,"=",count[i][j]).post();
        }
        k++;
        }
        k++;
    }   
    }

    boolean solve(){
    solver.setSearch(Search.minDomLBSearch(tour)); // fail-first
    return solver.solve();
    }

    int minimize(){
    int smallest = n*n;
    while (solver.solve()){
        smallest = tourLength.getValue();
        model.arithm(tourLength,"<",smallest).post();
    }
    return smallest;
    }

    int maximize(){
    int largest = 4;
    while (solver.solve()){
        largest = tourLength.getValue();
        model.arithm(tourLength,">",largest).post();
    }
    return largest;
    }

    int count(){
    int solutions = 0;
    while (solver.solve()) solutions++;
    return solutions;                
    }

    int[] getResult(){
    int[] result = new int[n*n];
    for (int i=0;i<n*n;i++)
        result[i] = tour[i].getValue();
    return result;
    }

    void stats(){
    System.out.println(solver.getMeasures());
    }
}
