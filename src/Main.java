import syntaxtree.Goal;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

class Main {
    public static void main (String [] args){
	if(args.length != 1){
	    System.err.println("Usage: java Driver <inputFile>");
	    System.exit(1);
	}
	FileInputStream fis = null;
	try{
		SymbolTable st = new SymbolTable();
	    fis = new FileInputStream(args[0]);
	    MiniJavaParser parser = new MiniJavaParser(fis);
	    System.err.println("Program parsed successfully.");
	    Pass1 eval = new Pass1();
	    Goal root = parser.Goal();
	    System.out.println(root.accept(eval, st));
	    st.validateST();
	    Pass2 secondEval = new Pass2(st);
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
    }
}
