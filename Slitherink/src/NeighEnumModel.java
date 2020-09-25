import java.util.ArrayList;

import org.chocosolver.solver.constraints.Constraint;
import org.chocosolver.solver.search.strategy.Search;
import org.chocosolver.solver.variables.BoolVar;
import org.chocosolver.solver.variables.IntVar;

public class NeighEnumModel extends Slitherlink {

	boolean solve()
	{
	    solver.setSearch(Search.minDomLBSearch(solution));
		return solver.solve();
	}
	
	IntVar circuitSize;
	BoolVar[][] edgeMatrix;
	
	public NeighEnumModel(String fname) {
		super(fname);
		
		solution = new IntVar[n*n];
		
		for(int i=0;i<n*n;++i)
		{
			ArrayList<Integer> dom = new ArrayList<Integer>();
	        if (i>=n) dom.add(i-n);     // above
	        if (i%n>0) dom.add(i-1);    // left
	        dom.add(i);                 // itself
	        if (i%n<n-1) dom.add(i+1);  // right
	        if (i+n < n*n) dom.add(i+n);// below;
	        int[] domain = new int[dom.size()];
	        int k = 0;
	        for (int x : dom) domain[k++] = x;
	        solution[i] = model.intVar("sol[" + i + "]",domain);
		}
		
		circuitSize = model.intVar("circuitSize", 4, n*n);
        model.subCircuit(solution, 0, circuitSize).post();
        
        edgeMatrix = model.boolVarMatrix(n*n, n*n);
        
        for (int i=0;i<n*n;i++)
        {
	        int ub = solution[i].getUB();
	        for (int k = solution[i].getLB();k<=ub;k=solution[i].nextValue(k))
	        	model.ifOnlyIf(model.arithm(edgeMatrix[i][k],"=",1), model.arithm(solution[i], "=", k));
		
        }
        
        // enumerate all possible neighbors for each given square
        
        for(int i=0;i<n-1;++i)
        for(int k=0;k<n-1;++k)
        {
        	if(board[i][k] == -1) continue;
         	
        	int UL = i*n + k;
        	int UR = i*n + k + 1;
        	int DL = (i+1)*n + k;
        	int DR = (i+1)*n + k + 1;
        	
        	Constraint UL_UR = model.arithm(solution[UL], "=", UR);
        	Constraint UR_UL = model.arithm(solution[UR], "=", UL);
        	Constraint UL_DL = model.arithm(solution[UL], "=", DL);
        	Constraint DL_UL = model.arithm(solution[DL], "=", UL);
        	Constraint DL_DR = model.arithm(solution[DL], "=", DR);
        	Constraint DR_DL = model.arithm(solution[DR], "=", DL);
        	Constraint UR_DR = model.arithm(solution[UR], "=", DR);
        	Constraint DR_UR = model.arithm(solution[DR], "=", UR);
        	
        	if(board[i][k] == 0)
        	{
        		model.not(UL_UR).post();
        		model.not(UR_UL).post();
        		model.not(UL_DL).post();
        		model.not(DL_UL).post();
        		model.not(DL_DR).post();
        		model.not(DR_DL).post();
        		model.not(UR_DR).post();
        		model.not(DR_UR).post();
        	}
        	else if (board[i][k]==1)
        	{      		
        		Constraint ul_ur = model.and(UL_UR, model.not(UR_DR),model.not(DL_UL),model.not(DL_DR),model.not(DR_DL));
        		Constraint ur_ul = model.and(UR_UL, model.not(UL_DL),model.not(DR_UR),model.not(DL_DR),model.not(DR_DL));
        		Constraint ur_dr = model.and(UR_DR, model.not(DR_DL),model.not(UL_UR),model.not(UL_DL),model.not(DL_UL));
        		Constraint dr_ur = model.and(DR_UR, model.not(UR_UL),model.not(DL_DR),model.not(DL_UL),model.not(UL_DL));
        		Constraint dl_dr = model.and(DL_DR, model.not(DR_UR),model.not(UL_DL),model.not(UL_UR),model.not(UR_UL));
        		Constraint dr_dl = model.and(DR_DL, model.not(DL_UL),model.not(UR_DR),model.not(UL_UR),model.not(UR_UL));
        		Constraint ul_dl = model.and(UL_DL, model.not(DL_DR),model.not(UR_UL),model.not(DR_UR),model.not(UR_DR));
        		Constraint dl_ul = model.and(DL_UL, model.not(UL_UR),model.not(DR_DL),model.not(DR_UR),model.not(UR_DR));
        		
        		model.or(ul_ur, ur_ul,ur_dr,dr_ur,dl_dr,dr_dl,ul_dl,dl_ul).post();
        		
        	}
        	else if (board[i][k]==2)
        	{
        		// up and down
        		Constraint ul_ur_dl_dr = model.and(UL_UR, DL_DR);
        		Constraint ul_ur_dr_dl = model.and(UL_UR, DR_DL, model.not(DL_UL), model.not(UR_DR));
        		Constraint ur_ul_dl_dr = model.and(UR_UL, DL_DR, model.not(UL_DL), model.not(DR_UR));
        		Constraint ur_ul_dr_dl = model.and(UR_UL, DR_DL);
        		
        		// left and right
        		Constraint dl_ul_dr_ur = model.and(DL_UL, DR_UR);
        		Constraint dl_ul_ur_dr = model.and(DL_UL, UR_DR, model.not(UL_UR), model.not(DR_DL));
        		Constraint ul_dl_dr_ur = model.and(UL_DL, DR_UR, model.not(UR_UL), model.not(DL_DR));
        		Constraint ul_dl_ur_dr = model.and(UL_DL, UR_DR);
        		
        		// bottom left corner
        		Constraint ul_dl_dr = model.and(UL_DL, DL_DR, model.not(DR_UR), model.not(UR_UL));
        		Constraint dr_dl_ul = model.and(DR_DL, DL_UL, model.not(UR_DR), model.not(UL_UR));
        		
        		// top right corner
        		Constraint ul_ur_dr = model.and(UL_UR, UR_DR, model.not(DR_DL), model.not(DL_UL));
        		Constraint dr_ur_ul = model.and(DR_UR, UR_UL, model.not(DL_DR), model.not(UL_DL));
        		
        		// bottom right corner
        		Constraint dl_dr_ur = model.and(DL_DR, DR_UR, model.not(UR_UL), model.not(UL_DL));
        		Constraint ur_dr_dl = model.and(UR_DR, DR_DL, model.not(UL_UR), model.not(DL_UL));
        		
        		// top left corner
        		Constraint ur_ul_dl = model.and(UR_UL, UL_DL, model.not(DL_DR), model.not(DR_UR));
        		Constraint dl_ul_ur = model.and(DL_UL, UL_UR, model.not(DR_DL), model.not(UR_DR));
        		
        		model.or(ul_ur_dl_dr,ul_ur_dr_dl,ur_ul_dl_dr,ur_ul_dr_dl, dl_ul_dr_ur, dl_ul_ur_dr, ul_dl_dr_ur, ul_dl_ur_dr, ul_dl_dr, dr_dl_ul, ul_ur_dr, dr_ur_ul, dl_dr_ur, ur_dr_dl, ur_ul_dl, dl_ul_ur).post();
        	}
        	else if (board[i][k]==3)
        	{
        		Constraint ul_dl_dr_ur = model.and(UL_DL, DL_DR, DR_UR, model.not(UL_UR));
        		Constraint ur_dr_dl_ul = model.and(UR_DR, DR_DL, DL_UL, model.not(UR_UL));
        		Constraint dl_ul_ur_dr = model.and(DL_UL, UL_UR, UR_DR, model.not(DL_DR));
        		Constraint dr_ur_ul_dl = model.and(DR_UR, UR_UL, UL_DL, model.not(DR_DL));
        		Constraint dl_dr_ur_ul = model.and(DL_DR, DR_UR, UR_UL, model.not(DL_UL));
        		Constraint ul_ur_dr_dl = model.and(UL_UR, UR_DR, DR_DL, model.not(UL_DL));
        		Constraint ur_ul_dl_dr = model.and(UR_UL, UL_DL, DL_DR, model.not(UR_DR));
        		Constraint dr_dl_ul_ur = model.and(DR_DL, DL_UL, UL_UR, model.not(DR_UR));
        		
        		model.or(ul_dl_dr_ur,ur_dr_dl_ul,dl_ul_ur_dr, dr_ur_ul_dl,dl_dr_ur_ul,ul_ur_dr_dl, ur_ul_dl_dr, dr_dl_ul_ur).post();
        	}
        	
        }
        
	}
}
