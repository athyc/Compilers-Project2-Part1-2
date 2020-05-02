import syntaxtree.Goal;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

class Main {
    public static void main (String [] args) throws IOException {

	FileInputStream fis = null;
	for(String arg:args){
		System.out.println(arg);
		try{
			fis=null;
			SymbolTable st = new SymbolTable();
			fis = new FileInputStream(arg);
			MiniJavaParser parser = new MiniJavaParser(fis);
			System.err.println("Program parsed successfully.");
			Pass1 eval = new Pass1();
			Goal root = parser.Goal();
			System.out.println(root.accept(eval, st));
			st.validateST();
			Pass3 secondEval = new Pass3(st);
			System.out.println(root.accept(secondEval, st));
			System.err.println("done");
		}
		catch(ParseException ex){
			System.out.println(ex.getMessage());
		}
		catch(FileNotFoundException ex){
			System.err.println("File not found");
			System.err.println(ex.getMessage());
		}
		catch (Exception ex){
			System.err.println("Error in parsing");
			System.err.println(ex.getMessage());
		}
		finally{
			try{
				if(fis != null) fis.close();
			}
			catch(IOException ex){
				System.err.println(ex.getMessage());
			}
		}
		//System.in.read();

	}

    }
}
