import java.util.ArrayList;

import org.chocosolver.solver.constraints.Constraint;
import org.chocosolver.solver.constraints.nary.cnf.LogOp;
import org.chocosolver.solver.search.strategy.Search;
import org.chocosolver.solver.variables.BoolVar;
import org.chocosolver.solver.variables.IntVar;

public class SATModel extends Slitherlink {

	boolean solve()
	{
	    solver.setSearch(Search.minDomLBSearch(solution));
		return solver.solve();
	}
	
	IntVar circuitSize;
	BoolVar[][] edgeMatrix;
	
	public SATModel(String fname) {
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
        	
        	Constraint UL_URc = model.arithm(solution[UL], "=", UR);
        	Constraint UR_ULc = model.arithm(solution[UR], "=", UL);
        	Constraint UL_DLc = model.arithm(solution[UL], "=", DL);
        	Constraint DL_ULc = model.arithm(solution[DL], "=", UL);
        	Constraint DL_DRc = model.arithm(solution[DL], "=", DR);
        	Constraint DR_DLc = model.arithm(solution[DR], "=", DL);
        	Constraint UR_DRc = model.arithm(solution[UR], "=", DR);
        	Constraint DR_URc = model.arithm(solution[DR], "=", UR);
        	
        	BoolVar UL_UR = UL_URc.reify();
        	BoolVar UR_UL = UR_ULc.reify();
        	BoolVar UL_DL = UL_DLc.reify();
        	BoolVar DL_UL = DL_ULc.reify();
        	BoolVar DL_DR = DL_DRc.reify();
        	BoolVar DR_DL = DR_DLc.reify();
        	BoolVar DR_UR = DR_URc.reify();
        	BoolVar UR_DR = UR_DRc.reify();
        	
        	if(board[i][k] == 0)
        	{
 
        		model.addClauseFalse(UL_UR);
        		model.addClauseFalse(UR_UL);
        		model.addClauseFalse(UL_DL);
        		model.addClauseFalse(DL_UL);
        		model.addClauseFalse(DL_DR);
        		model.addClauseFalse(DR_DL);
        		model.addClauseFalse(DR_UR);
        		model.addClauseFalse(UR_DR);
        		
        		// DOES NOT WORK - WHY?
        		//model.addClauses(new BoolVar[] {}, new BoolVar[] {UL_UR,UR_UL,UL_DL,DL_UL,DL_DR,DR_DL,DR_UR,UR_DR});
        		
        	}
        	else if (board[i][k]==1)
        	{      		
        		Constraint ul_urc = model.and(UL_UR, UR_DR.not(),DL_UL.not(),DL_DR.not(),DR_DL.not());
        		Constraint ur_ulc = model.and(UR_UL, UL_DL.not(),DR_UR.not(),DL_DR.not(),DR_DL.not());
        		Constraint ur_drc = model.and(UR_DR, DR_DL.not(),UL_UR.not(),UL_DL.not(),DL_UL.not());
        		Constraint dr_urc = model.and(DR_UR, UR_UL.not(),DL_DR.not(),DL_UL.not(),UL_DL.not());
        		Constraint dl_drc = model.and(DL_DR, DR_UR.not(),UL_DL.not(),UL_UR.not(),UR_UL.not());
        		Constraint dr_dlc = model.and(DR_DL, DL_UL.not(),UR_DR.not(),UL_UR.not(),UR_UL.not());
        		Constraint ul_dlc = model.and(UL_DL, DL_DR.not(),UR_UL.not(),DR_UR.not(),UR_DR.not());
        		Constraint dl_ulc = model.and(DL_UL, UL_UR.not(),DR_DL.not(),DR_UR.not(),UR_DR.not());
        		
        		BoolVar[] reified = new BoolVar[8];
        		reified[0] = ul_urc.reify();
        		reified[1] = ur_ulc.reify();
        		reified[2] = ur_drc.reify();
        		reified[3] = dr_urc.reify();
        		reified[4] = dl_drc.reify();
        		reified[5] = dr_dlc.reify();
        		reified[6] = ul_dlc.reify();
        		reified[7] = dl_ulc.reify();
        		
        		model.addClausesBoolOrArrayEqualTrue(reified);
        		
        	}
        	else if (board[i][k]==2)
        	{
        		BoolVar[] reified = new BoolVar[16];
        		
        		// up and down
        		Constraint ul_ur_dl_dr = model.and(UL_UR, DL_DR);
                Constraint ul_ur_dr_dl = model.and(UL_UR, DR_DL, DL_UL.not(), UR_DR.not());
                Constraint ur_ul_dl_dr = model.and(UR_UL, DL_DR, UL_DL.not(), DR_UR.not());
                Constraint ur_ul_dr_dl = model.and(UR_UL, DR_DL);
                reified[0] = ul_ur_dl_dr.reify();
                reified[1] = ul_ur_dr_dl.reify();
                reified[2] = ur_ul_dl_dr.reify();
                reified[3] = ur_ul_dr_dl.reify();
                
                // left and right
                Constraint dl_ul_dr_ur = model.and(DL_UL, DR_UR);
                Constraint dl_ul_ur_dr = model.and(DL_UL, UR_DR, UL_UR.not(), DR_DL.not());
                Constraint ul_dl_dr_ur = model.and(UL_DL, DR_UR, UR_UL.not(), DL_DR.not());
                Constraint ul_dl_ur_dr = model.and(UL_DL, UR_DR);
                reified[4] = dl_ul_dr_ur.reify();
                reified[5] = dl_ul_ur_dr.reify();
                reified[6] = ul_dl_dr_ur.reify();
                reified[7] = ul_dl_ur_dr.reify();
                
                // bottom left corner
                Constraint ul_dl_dr = model.and(UL_DL, DL_DR, DR_UR.not(), UR_UL.not());
                Constraint dr_dl_ul = model.and(DR_DL, DL_UL, UR_DR.not(), UL_UR.not());
                reified[8] = ul_dl_dr.reify();
                reified[9] = dr_dl_ul.reify();
                
                // top right corner
                Constraint ul_ur_dr = model.and(UL_UR, UR_DR, DR_DL.not(), DL_UL.not());
                Constraint dr_ur_ul = model.and(DR_UR, UR_UL, DL_DR.not(), UL_DL.not());
                reified[10] = ul_ur_dr.reify();
                reified[11] = dr_ur_ul.reify();
                
                // bottom right corner
                Constraint dl_dr_ur = model.and(DL_DR, DR_UR, UR_UL.not(), UL_DL.not());
                Constraint ur_dr_dl = model.and(UR_DR, DR_DL, UL_UR.not(), DL_UL.not());  
                reified[12] = dl_dr_ur.reify();
                reified[13] = ur_dr_dl.reify();
        		
        		// top left corner
        		Constraint ur_ul_dl = model.and(UR_UL, UL_DL, DL_DR.not(), DR_UR.not());
        		Constraint dl_ul_ur = model.and(DL_UL, UL_UR, DR_DL.not(), UR_DR.not());
        		reified[14] = ur_ul_dl.reify();
        		reified[15] = dl_ul_ur.reify();
        		
        		
        		model.addClausesBoolOrArrayEqualTrue(reified);
        	}
        	else if (board[i][k]==3)
        	{
        		
        		
        		//model.addClauses(LogOp.and(LogOp.and(LogOp.and(DR_UR,UL_UR.not()), DL_DR),UL_DL));
        		
        		Constraint ul_dl_dr_urc = model.and(UL_DL, DL_DR, DR_UR, UL_UR.not());
        		Constraint ur_dr_dl_ulc = model.and(UR_DR, DR_DL, DL_UL, UR_UL.not());
        		Constraint dl_ul_ur_drc = model.and(DL_UL, UL_UR, UR_DR, DL_DR.not());
        		Constraint dr_ur_ul_dlc = model.and(DR_UR, UR_UL, UL_DL, DR_DL.not());
        		Constraint dl_dr_ur_ulc = model.and(DL_DR, DR_UR, UR_UL, DL_UL.not());
        		Constraint ul_ur_dr_dlc = model.and(UL_UR, UR_DR, DR_DL, UL_DL.not());
        		Constraint ur_ul_dl_drc = model.and(UR_UL, UL_DL, DL_DR, UR_DR.not());
        		Constraint dr_dl_ul_urc = model.and(DR_DL, DL_UL, UL_UR, DR_UR.not());
        		
        		BoolVar[] reified = new BoolVar[8];
        		reified[0] = ul_dl_dr_urc.reify();
        		reified[1] = ur_dr_dl_ulc.reify();
        		reified[2] = dl_ul_ur_drc.reify();
        		reified[3] = dr_ur_ul_dlc.reify();
        		reified[4] = dl_dr_ur_ulc.reify();
        		reified[5] = ul_ur_dr_dlc.reify();
        		reified[6] = ur_ul_dl_drc.reify();
        		reified[7] = dr_dl_ul_urc.reify();
        		
        		model.addClausesBoolOrArrayEqualTrue(reified);
        		
        	}
        	
        }
        
	}
}
