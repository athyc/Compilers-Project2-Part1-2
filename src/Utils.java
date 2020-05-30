import java.io.BufferedWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Utils {
    private int currReg=0, arrayTag=0, oobTag =0,ifTag=0, loopTag = 0;
    LLVMClass currClass;
    Function currFunction;
    List<Variable> essRegs = new ArrayList<>();

    public Utils(BufferedWriter bw) {
        this.bw = bw;
    }
    public Utils() {
        this.bw = bw;
    }
    public Function getCurrFunction() {
        return currFunction;
    }
    BufferedWriter bw;

    public void setCurrFunction(Function currFunction) {
        this.currFunction = currFunction;
    }
    String []getNSZTags(){
        String [] array = new String[2];
        array[0] = "nsz_ok_"+arrayTag;
        array[1] = "nsz_err_"+(arrayTag++);
        return array;
    }
    String []getOOBTags(){
        String [] array = new String[2];
        array[0] = "oob_ok_"+oobTag;
        array[1] = "oob_err_"+(oobTag++);
        return array;
    }
    String []getConditionTags(){
        String [] array = new String[3];
        array[0] = "if_then"+(ifTag);
        array[1] = "if_else"+(ifTag);
        array[2] = "if_end"+(ifTag++);
        return array;
    }
    String []getWhileTags(){
        String [] array = new String[3];
        array[0] = "loop"+(loopTag++);
        array[1] = "loop"+(loopTag++);
        array[2] = "loop"+(loopTag++);
        return array;
    }

    String getReg(){
        return "%_"+(currReg++);
    }
    String getReg(String type){
        essRegs.add(new Variable(("%_"+currReg),type));
        return "%_"+(currReg++);
    }
    String getLastReg(){
        return "%_"+(currReg-1);
    }
    void regReset(){
        currReg=0;
        essRegs.clear();
    }

    public void simplePrint(String s) throws IOException {
        bw.write(s+"\n");
        //System.out.println(s);
    }
    public void simpleInlinePrint(String s) throws IOException {
        bw.write(s);//System.out.print(s);
    }
    void print(String s) throws IOException {
        bw.write(Indentation()+s);
        //System.out.print(Indentation()+s);
    }
    String printString(String s){
        return Indentation()+s;
    }
    void println(String s) throws IOException {
        bw.write(Indentation()+s+"\n");
        //System.out.println();
    }
    private int Indentation=0;
    StringBuilder FPAlloc= new StringBuilder();
    void increaseIndentation(){
        Indentation++;
    }
    void decreaseIndentation(){
        Indentation--;
    }
    String Indentation(){
        StringBuilder rv = new StringBuilder();
        rv.append("\t".repeat(Math.max(0, Indentation)));
        return rv.toString();
    }
    protected void declare() throws IOException {
        bw.write("declare i8* @calloc(i32, i32)\n" +
                "declare i32 @printf(i8*, ...)\n" +
                "declare void @exit(i32)\n" +
                "\n" +
                "@_cint = constant [4 x i8] c\"%d\\0a\\00\"\n" +
                "@_cOOB = constant [15 x i8] c\"Out of bounds\\0a\\00\"\n" +"@_cNSZ = constant [15 x i8] c\"Negative size\\0a\\00\"\n"+
                "define void @print_int(i32 %i) {\n" +
                "    %_str = bitcast [4 x i8]* @_cint to i8*\n" +
                "    call i32 (i8*, ...) @printf(i8* %_str, i32 %i)\n" +
                "    ret void\n" +
                "}\n" +
                "\n" +
                "define void @throw_oob() {\n" +
                "    %_str = bitcast [15 x i8]* @_cOOB to i8*\n" +
                "    call i32 (i8*, ...) @printf(i8* %_str)\n" +
                "    call void @exit(i32 1)\n" +
                "    ret void\n" +
                "}\n\n");
        //System.out.print();
        bw.write("define void @throw_nsz() {\n" +
                "    %_str = bitcast [15 x i8]* @_cNSZ to i8*\n" +
                "    call i32 (i8*, ...) @printf(i8* %_str)\n" +
                "    call void @exit(i32 1)\n" +
                "    ret void\n" +
                "}\n\n");
        //System.out.print();
    }
    String NumberedIndentation(int x){
        return "\t".repeat(Math.max(0, x));
    }

    public LLVMClass getCurrClass() {
        return currClass;
    }
    public void setCurrClass(LLVMClass currClass) {
        this.currClass = currClass;
    }

    protected int javaTypeToOffset(String returnType) {
        switch (returnType){
            case "int":
                return 4;
            case "boolean":
                return 1;
            case "int[]":
                return 8;
            default:
                return 8;
        }

    }

    public void printFP(String type, String s, String ID) throws IOException {
        FPAlloc.append(NumberedIndentation(1)).append("%").append(ID).append(" = alloca ").append(type).append("\n");
        FPAlloc.append(NumberedIndentation(1)).append("store ").append(type).append(" ").append(s).append(ID).append(", ").append(type).append("*").append(" %").append(ID).append("\n");
        print(type+s+ID);
    }

    String getFPallocs(){
        String r=FPAlloc.toString();
        FPAlloc.setLength(0);
        return r;
    }
    public String decodeIdentifier(String Identifier) throws IOException {
        for (Variable v : currFunction.declarations){
            if(v.name.equals(Identifier)){
                return "%"+v.name;
            }
        }
        for (Variable v : currFunction.arguments){
            if(v.name.equals(Identifier)){
                return "%"+v.name;
            }
        }
        List<Variable> temp = new ArrayList<>(currClass.VariablesTemplate);
        Collections.reverse(temp);
        for (Variable v : temp){
            if(v.name.equals(Identifier)){
                return getClassVariable(v);
            }
        }
        return "decode identifier not found "+Identifier;
    }

    private String getClassVariable(Variable v) throws IOException {
        String reg = getReg();
        print((reg + " = getelementptr i8, i8* %this, i32 " + (v.offset+8) + "\n") + printString(getReg() + " = bitcast i8* " + reg + " to " + javaTypeToLLVMType(v.type)+ "*\n\n"));
        return getLastReg();
    }

    public boolean isReg(String result) {
        return result.startsWith("%_")&&result.substring(2).matches("[0-9]+");
    }

    public boolean isLiteral(String result) {
        return result.equals("true") || result.equals("false") ||result.matches("[0-9]+");
    }

    public boolean isIdentifier(String result) {
        return result.matches("[A-Za-z][A-Za-z0-9_]*")&&!isLiteral(result)&&!result.equals("this");
    }


    public String getIdentifierType(String Identifier) {
        for (Variable v : currFunction.declarations){
            if(v.name.equals(Identifier)){
                return v.type;
            }
        }
        for (Variable v : currFunction.arguments){
            if(v.name.equals(Identifier)){
                return v.type;
            }
        }
        List<Variable> temp = new ArrayList<>(currClass.VariablesTemplate);
        Collections.reverse(temp);
        for (Variable v : temp){
            if(v.name.equals(Identifier)){
                return v.type;
            }
        }

        return "get identifier type not found "+Identifier;
    }

    public String pointer(String type) {
        return type+"*";
    }

    public String decodeLiteral(String literal) {
        if(literal.equals("true")){
            return "1";
        }else if(literal.equals("false")){
            return "0";
        }else{
            return literal;
        }
    }



    public String arrayDifferenciation(String reg1, String type) throws IOException {
        if(type.equals("int[]")){
            return reg1;
        }else if(type.equals("boolean[]")){
            String rv =getReg();
            println(rv +" = bitcast i8* "+reg1+" to i32*");
            return rv;
        }else{
            return "arraydifferenciaition not found "+reg1;
        }
    }

    public void oobCheckSegment(String condFinal, String[] array) throws IOException {
        println("br i1 "+condFinal+", label %"+array[0]+", label %"+array[1]);

        println(array[1]+":\n" +Indentation()+
                "call void @throw_oob()\n" +Indentation()+
                "br label %"+array[0]+"\n");
        println(array[0]+":");
    }
    private int minorTypeConversion(String type){
        switch (type){
            case "boolean[]":
                return 4;
            case "int[]":
                return 1;
            default:
                return 0;
        }

    }
    String getLengthSegment(String reg1, String type) throws IOException {
        String sizeLoader = arrayDifferenciation(reg1, type);
        String size = getReg();
        println(size+" = load i32, i32* "+sizeLoader);
        return size;
    }
    String arrayAccess( String reg1, String type, String betweenBrackets) throws IOException {
        String size = getLengthSegment(reg1, type);
        String cond1 =getReg(), cond2 = getReg(), condFinal = getReg();
        println(cond1+" = icmp sge i32 "+ betweenBrackets +", 0");
        println(cond2+" = icmp slt i32 "+ betweenBrackets +", "+size);
        println(condFinal+" = and i1 "+cond1+", "+cond2);
        oobCheckSegment(condFinal,getOOBTags());
        String finalIndex = getReg();
        println(finalIndex+" = add i32 "+ minorTypeConversion(type)+", "+ betweenBrackets);
        String pointer = getReg(type);
        println(pointer+" = getelementptr "+dereference(javaTypeToLLVMType(type))+", "+javaTypeToLLVMType(type)+" "+reg1+", i32 "+finalIndex);
        return pointer;
    }
    String arrayAssignment(String arrayID, String betweenBrackets) throws IOException {
        String storeArg1 = decodeIdentifier(arrayID);
        String type = getIdentifierType(arrayID);
        String reg1 = getReg();
        println(reg1+" = load "+javaTypeToLLVMType(type)+", "+javaTypeToLLVMType(type)+"* "+storeArg1);
        return arrayAccess(reg1,type,betweenBrackets);

    }
    String arrayLookup(String primexResult, String betweenBrackets) throws IOException {
        String type = getRegType(primexResult);
        return arrayAccess(primexResult,type,betweenBrackets);
    }
    public String dereference(String regType) {
        return regType.substring(0,regType.length()-1);
    }

    public String decodePrimexIdentifier(String Identifier) throws IOException {
        String reg,reg1;
        for (Variable v : currFunction.arguments){
            if(v.name.equals(Identifier)){
                reg = getReg(v.type);
                println(reg+" = load "+javaTypeToLLVMType(v.type)+", "+javaTypeToLLVMType(v.type)+"* %"+Identifier);
                println("");
                return reg;
            }
        }
        for (Variable v : currFunction.declarations){
            if(v.name.equals(Identifier)){
                reg = getReg(v.type);
                println(reg+" = load "+javaTypeToLLVMType(v.type)+", "+javaTypeToLLVMType(v.type)+"* %"+Identifier);
                println("");
                return reg;
            }
        }
        List<Variable> temp = new ArrayList<>(currClass.VariablesTemplate);
        Collections.reverse(temp);
        for (Variable v : temp){
            if(v.name.equals(Identifier)){
                reg = getClassVariable(v);
                reg1 = getReg(v.type);
                println(reg1+" = load "+javaTypeToLLVMType(v.type)+", "+javaTypeToLLVMType(v.type)+"* "+reg);
                println("");
                return reg1;
            }
        }
        return " decode primex identifier not found "+Identifier;
    }
    public String getArg(String result) throws IOException {
        String temp;
        if(isReg(result)){
            return result;
        }else if(isIdentifier(result)){
            temp = decodePrimexIdentifier(result);
            return temp;
        }else if(isLiteral(result)){
            return decodeLiteral(result);
        }else if(result.equals("this")){
            return "%this";
        }
        return "getarg not found "+result;
    }
    public void allocationSegment(String allocSize, String exprReg, String[] array,int typeFactor) throws IOException {
        println(allocSize+" = add i32 "+typeFactor+", "+exprReg);
        println(getReg()+" = icmp sge i32 "+allocSize+", 1");
        println("br i1 "+getLastReg()+", label %"+array[0]+", label %"+array[1]);

        println(array[1]+":\n" +Indentation()+
                "call void @throw_nsz()\n" +Indentation()+
                "br label %"+array[0]+"\n");
        println(array[0]+":");
    }
    public String getRegType(String primexResult) {
        if(primexResult.equals("%this"))return currClass.ID;
        for (Variable v: essRegs){
            if (v.name.equals(primexResult)){
                return v.type;
            }
        }

        return "getReg Type not found "+primexResult;
    }
    protected String argsToString(List<Variable> arguments) {
        StringBuilder rv = new StringBuilder();
        for (Variable v : arguments){
            rv.append(",");
            rv.append(javaTypeToLLVMType(v.type));
        }
        return rv.toString();
    }
    protected String javaTypeToLLVMType(String returnType) {
        switch (returnType){
            case "int":
                return "i32";
            case "boolean":
                return "i1";
            case "int[]":
                return "i32*";
            default:
                return "i8*";
        }

    }
}
