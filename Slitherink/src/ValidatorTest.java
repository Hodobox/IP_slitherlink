import static org.junit.jupiter.api.Assertions.*;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import org.junit.jupiter.api.Test;

class ValidatorTest {

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
	}

}
