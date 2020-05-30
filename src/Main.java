import syntaxtree.Goal;

import java.io.*;

class Main {
    public static void main (String [] args) throws IOException {

		FileInputStream fis = null;
		if(args.length < 1){
			System.err.println("Usage: java Driver <inputFile1> <inputFile2> ... <inputFilek>");
			System.exit(1);
		}

		for(String arg:args){
			System.out.println(arg);
			BufferedWriter bw = new BufferedWriter(new FileWriter(getOutputFileName(arg)));
			try{

				fis=null;
				SymbolTable st = new SymbolTable();
				LLVMRedux nST = new LLVMRedux();
				fis = new FileInputStream(arg);
				MiniJavaParser parser = new MiniJavaParser(fis);
				//System.err.println("Program parsed successfully.");
				Pass1 eval = new Pass1();
				Goal root = parser.Goal();
				root.accept(eval, st);
				st.validateST();
				Pass3 secondEval = new Pass3(st);
				root.accept(secondEval, st);
				st.Reduct(nST);
				LLVMGen llvmGen = new LLVMGen(nST, bw);
				root.accept(llvmGen,nST);
				System.out.println("done");
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
				bw.close();
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
			bw.close();
		}

    }

	private static String getOutputFileName(String arg) {
    	StringBuilder rv = new StringBuilder();
		for (int i=arg.length()-1;i>=0;i--){
			if(arg.charAt(i)=='\\'){
				return rv.reverse().toString().substring(0,rv.length()-5)+".ll";
			}
			rv.append(arg.charAt(i));
		}
		return rv.reverse().toString();
	}
}
