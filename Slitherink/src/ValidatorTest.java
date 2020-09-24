import static org.junit.jupiter.api.Assertions.*;

import java.io.*;

import org.junit.After;
import org.junit.Before;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class ValidatorTest {
	
	private final ByteArrayOutputStream outContent = new ByteArrayOutputStream();
	private final ByteArrayOutputStream errContent = new ByteArrayOutputStream();
	private final PrintStream originalOut = System.out;
	private final PrintStream originalErr = System.err;

	@BeforeEach
	public void setUpStreams() {
	    System.setOut(new PrintStream(outContent));
	    System.setErr(new PrintStream(errContent));
	}

	@After
	public void restoreStreams() {
	    System.setOut(originalOut);
	    System.setErr(originalErr);
	}

	@Test
	void noEdges() throws IOException {
		File in = new File("tests/noEdges.in");
		in.createNewFile();
		FileWriter fw = new FileWriter(in,false);
		fw.write("2\n4\n");
		fw.close();
		
		File out = new File("tests/noEdges.out");
		out.createNewFile();
		fw = new FileWriter(out,false);
		fw.write("0\n");
		fw.close();
		
		Validator val = new Validator("tests/noEdges.in","tests/noEdges.out");
		assertFalse(val.validate(false));
		assertEquals("No solution is present!", outContent.toString().trim());
	}
	
	@Test
	void invalidEdges() throws IOException {
		File in = new File("tests/invalidEdges.in");
		in.createNewFile();
		FileWriter fw = new FileWriter(in,false);
		fw.write("3\n-1 -1\n-1 -1\n");
		fw.close();
		
		File out = new File("tests/invalidEdges.out");
		out.createNewFile();
		fw = new FileWriter(out,false);
		
		int nEdges = 8;
		int[][] edges = {
				{7,7},
				{0,2},
				{1,5},
				{2,3},
				{4,8},
				{1,6},
				{5,6},
				{2,8}
		};
		fw.write(nEdges + "\n");
		for(int i=0;i<nEdges;++i)
			fw.write(edges[i][0] + " " + edges[i][1] + "\n");
		fw.close();
		
		Validator val = new Validator("tests/invalidEdges.in","tests/invalidEdges.out");
		assertFalse(val.validate(true));
		
		String[] lines = outContent.toString().split("\\r?\\n");
		
		for(int i=0;i<nEdges;++i)
		{
			boolean found = false;
			for(int k=0;k<nEdges;++k)
			{
				found |= lines[k].trim().equals("Invalid edge from " + edges[i][0] + " to " + edges[i][1]);
			}
			assertTrue(found,edges[i][0] + " to " + edges[i][1]);
		}
	}
	
	@Test
	void invalidEdgeDegrees() throws IOException {
		File in = new File("tests/invalidEdgeDegrees.in");
		in.createNewFile();
		FileWriter fw = new FileWriter(in,false);
		fw.write("3\n-1 -1\n-1 -1\n");
		fw.close();
		
		File out = new File("tests/invalidEdgeDegrees.out");
		out.createNewFile();
		fw = new FileWriter(out,false);
		
		int nEdges = 8;
		int[][] edges = {
				{1,4},
				{3,4},
				{4,5},
				{4,7},
				{0,3},
				{0,1},
				{3,6},
				{6,7}
		};
		fw.write(nEdges + "\n");
		for(int i=0;i<nEdges;++i)
			fw.write(edges[i][0] + " " + edges[i][1] + "\n");
		fw.close();
		
		Validator val = new Validator("tests/invalidEdgeDegrees.in","tests/invalidEdgeDegrees.out");
		assertFalse(val.validate(true));
		
		String[] lines = outContent.toString().split("\\r?\\n");
		
		int[] adjEdges = new int[9];
		
		for(int i=0;i<nEdges;++i)
		{
			adjEdges[ edges[i][0] ]++;
			adjEdges[ edges[i][1] ]++;
		}
		
		int errCount = 0;
		for(int i=0;i<nEdges;++i)
		{
			if(adjEdges[i]!=0 && adjEdges[i] != 2)
			{
				assertEquals("Vertex " + i + " is adjacent to " + adjEdges[i] + " edges", lines[errCount].trim());
				errCount++;
			}
		}
	}
	
	@Test
	void twoLoops() throws IOException {
		File in = new File("tests/twoLoops.in");
		in.createNewFile();
		FileWriter fw = new FileWriter(in,false);
		fw.write("4\n-1 -1 -1\n-1 -1 -1\n-1 -1 -1\n");
		fw.close();
		
		File out = new File("tests/twoLoops.out");
		out.createNewFile();
		fw = new FileWriter(out,false);
		
		int nEdges = 16;
		int[][] edges = {
				{0,1},
				{1,2},
				{2,3},
				{3,7},
				{7,11},
				{11,15},
				{14,15},
				{13,14},
				{12,13},
				{8,12},
				{4,8},
				{0,4},
				
				{5,6},
				{6,10},
				{9,10},
				{5,9}
		};
		fw.write(nEdges + "\n");
		for(int i=0;i<nEdges;++i)
			fw.write(edges[i][0] + " " + edges[i][1] + "\n");
		fw.close();
		
		Validator val = new Validator("tests/twoLoops.in","tests/twoLoops.out");
		assertFalse(val.validate(true));
		
		String[] lines = outContent.toString().split("\\r?\\n");
		
		assertEquals("Graph is more than 1 component", lines[0].trim());
	}
	
	@Test
	void incorrectSolution() throws IOException {
		File in = new File("tests/incorrectSolution.in");
		in.createNewFile();
		FileWriter fw = new FileWriter(in,false);
		fw.write("6\n3 -1 3 3 -1\n-1 2 0 -1 2\n1 -1 2 -1 1\n2 -1 1 -1 2\n-1 -1 -1 2 -1\n");
		fw.close();
		
		File out = new File("tests/incorrectSolution.out");
		out.createNewFile();
		fw = new FileWriter(out,false);
		
		int nEdges = 30;
		int[][] edges = {
				{0,1},  {1,2},  {2,3},   {4,10}, {4,5},
				{0,6},
				{6,7},{7,13}, {3,9},  {9,10},  {5,11},
				        {13,19},{15,16},{16,22}, {11,17},
				        {19,25},{15,21},{22,28}, {17,23},
		  {24,30},      {24,25},{21,27},{28,34}, {23,29},   
		  {30,31},{31,32},{32,33},{27,33},{34,35},{29,35},
							  				   
		};
		fw.write(nEdges + "\n");
		for(int i=0;i<nEdges;++i)
			fw.write(edges[i][0] + " " + edges[i][1] + "\n");
		fw.close();
		
		Validator val = new Validator("tests/incorrectSolution.in","tests/incorrectSolution.out");
		assertFalse(val.validate(true));
		
		String[] lines = outContent.toString().split("\\r?\\n");
		
		assertEquals("Slitherink requires 3 at 0,2 but 2 instead",lines[0]);
		assertEquals("Slitherink requires 2 at 1,1 but 1 instead",lines[1]);
		assertEquals("Slitherink requires 2 at 1,4 but 1 instead",lines[2]);
		assertEquals("Slitherink requires 2 at 2,2 but 1 instead",lines[3]);
		assertEquals("Slitherink requires 1 at 2,4 but 2 instead",lines[4]);
		assertEquals("Solution valid: false",lines[5]);	
	}
	
	@Test
	void correctSolution() {
		Validator val = new Validator("tests/correctSolution.in", "tests/correctSolution.out");
		assertTrue(val.validate(true));
		assertEquals("Solution valid: true", outContent.toString().trim());
	}

}
