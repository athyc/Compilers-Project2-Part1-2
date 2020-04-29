import syntaxtree.*;
import visitor.GJDepthFirst;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

public class Pass2 extends GJDepthFirst<String,SymbolTable> {
    void print(String str){
        System.out.println(str);
    }

    SymbolTable ST;
    ArrayList<String> formalParameterCollector = new ArrayList<>();
    Class currClass = new Class(), MSClass = null;
    Function currFunction = new Function();
    List<String> clausesCollector = new ArrayList<>();
    List<Variable> callArgCollector = new ArrayList<>();
    ArrayList<ArrayList<String>> argStack = new ArrayList<>();
    int argStackIndex = -1;
    Variable variable1 = new Variable(), variable2 = new Variable(), variable3 = new Variable();
    String wantedType;
    //String[] primaryTypes =
    boolean isClassAllocation = false, isMessageSend = false, isFuncCall = false, callArgCollecting = false, isArrayAllocation = false, fromArrayOp = false;
    String resultType = null;
    public Pass2(SymbolTable st) {
        ST = st;
    }
    public void validatePrimEx(String str1, String str2){
        if(Pattern.matches("[0-9]*", str1)){
            variable1.type = "int";
            variable1.name = str1;
        }
        else if(str1.equals("true") || str1.equals("false")){
            variable1.type = "boolean";
            variable1.name = str1;
        }else if(str1.equals("this")) {
            variable1.type = "this";
            variable1.name = str1;
        }
        else{
            variable1 = currFunction.lookForVariableInFunction(str1);

        }

        if(Pattern.matches("[0-9]*", str2)){
            variable2.type = "int";
            variable2.name = str2;
        }
        else if(str2.equals("true") || str2.equals("false")){
            variable2.type = "boolean";
            variable2.name = str2;
        }else if(str2.equals("this")) {
            variable2.type = "this";
            variable2.name = str2;
        }else if(str2.equals("-")) {
            variable2.type = "null";
            variable2.name = str2;
        }else{
            variable2 = currFunction.lookForVariableInFunction(str2);
        }



    }
    ArrayList<String> decodeArgList(ArrayList<String> argList)throws Exception{
        Variable variable;
        ArrayList<String> rval = new ArrayList<String>();
        for (String arg:argList){
            if(arg.endsWith("id")){
                variable = currClass.findInScope(getID(arg), currFunction, currClass.ID, currClass);
                rval.add(variable.type);
            }else {
                rval.add(arg);
            }
        }
        return rval;
    }
    private Function functionSignatureSearch(String classid, String functid, ArrayList<String> localArgList)throws Exception {
        Class c = scopeSearch(classid);
        Variable v = new Variable();
        int i;
        for (Function f: c.Functions){
            if(f.ID.equals(functid)){
                if(f.arguments.size()!=localArgList.size())continue;
                for ( i = 0 ; i<f.arguments.size(); i++){
                    v = f.arguments.get(i);
                    if(!v.type.equals(localArgList.get(i))){
                        break;
                    }
                }
                if(i == f.arguments.size())return f;
            }
        }
        throw new Exception("actual and formal argument lists differ in function call "+functid+" of class "+classid+" in "  + currClass.ID + currFunction.ID );
    }

    private boolean inappropriateTyping(String a, String b, String wantedType1, String wantedType2) throws Exception {
        if (a.endsWith("id")) {
            Variable av =  currClass.findInScope(getID(a),currFunction, currClass.ID, currClass);
            if(av == null){
                throw new Exception("Variable not found in " + currClass.ID + currFunction.ID);
            }
            if(! wantedType1.equals(av.type)){
                throw new Exception("inappropriate typing in " + currClass.ID + currFunction.ID);
            }
        } else {
            if (!a.endsWith(wantedType1)) {
                throw new Exception("inappropriate typing in " + currClass.ID + currFunction.ID);
            }
        }
        if (b.endsWith("id")) {
            Variable bv =  currClass.findInScope(getID(b),currFunction, currClass.ID,currClass );
            if(bv == null){
                throw new Exception("Variable not found in " + currClass.ID + currFunction.ID);
            }
            if(! wantedType1.equals(bv.type)){
                throw new Exception("inappropriate typing in " + currClass.ID + currFunction.ID);
            }
        } else {
            if (!b.endsWith(wantedType2)) {
                throw new Exception("inappropriate typing in " + currClass.ID + currFunction.ID);
            }
        }
        return false;
    }
    private boolean inappropriateTyping(String a, String wantedType1) throws Exception {
        if (a.endsWith("id")) {
            Variable av =  currClass.findInScope(getID(a),currFunction, currClass.ID, currClass);
            if(av == null){
                throw new Exception("Variable not found in " + currClass.ID + currFunction.ID);
            }else {
                if(!wantedType1.equals(av.type)){
                    throw new Exception("inappropriate typing in " + currClass.ID + currFunction.ID);
                }
            }
        } else {
            if (!a.endsWith(wantedType1)) {
                throw new Exception("inappropriate typing in " + currClass.ID + currFunction.ID);
            }
        }

        return false;
    }
    private String getID(String a) {
        return a.substring(0,a.length()-2);
    }
    public String cleanupPrimex(String primexRes){
        if(primexRes.startsWith("new")){
            return primexRes.substring(3);
        }
        return primexRes;
    }
    public Class scopeSearch(String scopeID) throws Exception{
        for(Class aClass : ST.aClasses){
            if(aClass.ID.equals(scopeID)){
                return aClass;
            }
        }
        throw new Exception("Symbol " + scopeID + " not found in " + currClass.ID + currFunction.ID);
    }

    public String visit(MainClass n, SymbolTable argu)throws Exception {
        /*
         mainclass
         * f1 -> Identifier()
         * f15 -> ( Statement() )*
         */
        String _ret=null;

        currClass = ST.aClasses.get(0);

        currFunction = ST.aClasses.get(0).Functions.get(0);

        n.f0.accept(this, argu);

        n.f15.accept(this, argu);

        return _ret;
    }

    public String visit(ClassDeclaration n, SymbolTable argu) throws Exception {
        /*
         * f0 -> "class"
         * f1 -> Identifier()
         * f2 -> "{"
         * f3 -> ( VarDeclaration() )*
         * f4 -> ( MethodDeclaration() )*
         * f5 -> "}"
         */
        String _ret=null;

        currClass = scopeSearch(getID(n.f1.accept(this, argu)));

        n.f4.accept(this, argu);

        return _ret;
    }

    public String visit(ClassExtendsDeclaration n, SymbolTable argu) throws Exception {
        /*
         * f0 -> "class"
         * f1 -> Identifier()
         * f2 -> "extends"
         * f3 -> Identifier()
         * f4 -> "{"
         * f5 -> ( VarDeclaration() )*
         * f6 -> ( MethodDeclaration() )*
         * f7 -> "}"
         */
        String _ret=null;

        currClass = scopeSearch(getID(n.f1.accept(this, argu)));

        n.f6.accept(this, argu);

        return _ret;
    }
    public String visit(MethodDeclaration n, SymbolTable argu) throws Exception {
        /*
         * f0 -> "public"
         * f1 -> Type()
         * f2 -> Identifier()
         * f3 -> "("
         * f4 -> ( FormalParameterList() )?
         * f5 -> ")"
         * f6 -> "{"
         * f7 -> ( VarDeclaration() )*
         * f8 -> ( Statement() )*
         * f9 -> "return"
         * f10 -> Expression()
         * f11 -> ";"
         * f12 -> "}"
         */
        String _ret=null;

        String type = n.f1.accept(this, argu);
        String functID = n.f2.accept(this, argu);

        n.f4.accept(this, argu);
        currFunction = functionSignatureSearch(currClass.ID, getID(functID), formalParameterCollector);
        formalParameterCollector.clear();

        n.f8.accept(this, argu);
        inappropriateTyping(n.f10.accept(this, argu),type);
        return _ret;
    }


    public String visit(FormalParameter n, SymbolTable argu)throws Exception{
        /*
         * f0 -> Type()
         * f1 -> Identifier()
         */
        String _ret=null;
        formalParameterCollector.add(n.f0.accept(this, argu));
        return _ret;
    }


    //statements
    public String visit(AssignmentStatement n, SymbolTable argu) throws Exception {
        /*
         * f0 -> Identifier()
         * f1 -> "="
         * f2 -> Expression()
         * f3 -> ";"
         */
        String _ret=null;
        String var1ID = n.f0.accept(this, argu);

        Variable variable3 = currClass.findInScope(getID(var1ID), currFunction, currClass.ID, currClass);
        if(variable3 == null){
            throw new Exception("Variable not found in " + currClass.ID + currFunction.ID);
        }

        String result = n.f2.accept(this, argu);
        //look for both values
        if(!result.equals(variable3.type)){
            throw new Exception("Variables not matching in assignment statement in " + currClass.ID + currFunction.ID);
        }
        return _ret;
    }

    public String visit(ArrayAssignmentStatement n, SymbolTable argu) throws Exception {
        /*
         * f0 -> Identifier()
         * f1 -> "["
         * f2 -> Expression()
         * f3 -> "]"
         * f4 -> "="
         * f5 -> Expression()
         * f6 -> ";"
         */
        String _ret=null;
        String var1ID = n.f0.accept(this, argu);
        Variable variable3 = currClass.findInScope(getID(var1ID), currFunction, currClass.ID, currClass);
        if(variable3 == null){
            throw new Exception("Variable not found in " + currClass.ID + currFunction.ID);
        }

        String result = n.f2.accept(this, argu);
        if(!result.equals("int")){
            throw new Exception("incompatible types: int cannot be converted to"+ result + "in " + currClass.ID + currFunction.ID);
        }
        Variable var;
        result = n.f5.accept(this, argu);
        if(result.endsWith("id")){
             var = currClass.findInScope(getID(result), currFunction, currClass.ID, currClass);
            result = var.type;
        }

        if(!result.equals(variable3.type.substring(0,variable3.type.length()-2))){
            throw new Exception("Variables not matching in assignment statement in " + currClass.ID + currFunction.ID);
        }

        return _ret;
    }

    public String visit(IfStatement n, SymbolTable argu) throws Exception {
        /*
         * f0 -> "if"
         * f1 -> "("
         * f2 -> Expression()
         * f3 -> ")"
         * f4 -> Statement()
         * f5 -> "else"
         * f6 -> Statement()
         */
        String _ret=null;
        inappropriateTyping(n.f2.accept(this, argu),"boolean");
        n.f4.accept(this, argu);
        n.f6.accept(this, argu);
        return _ret;
    }

    public String visit(WhileStatement n, SymbolTable argu) throws Exception {
        /*
         * f0 -> "while"
         * f1 -> "("
         * f2 -> Expression()
         * f3 -> ")"
         * f4 -> Statement()
         */
        String _ret=null;
        inappropriateTyping(n.f2.accept(this, argu),"boolean");
        n.f4.accept(this, argu);
        return _ret;
    }





    //expressions

    public String visit(Expression n, SymbolTable argu) throws Exception {

        return cleanupPrimex(n.f0.accept(this, argu));
    }

    public String visit(AndExpression n, SymbolTable argu) throws Exception {
        /*
         * f0 -> Clause()
         * f1 -> "&&"
         * f2 -> Clause()
         */
        String _ret=null;
        String a = n.f0.accept(this, argu);
        String b = n.f2.accept(this, argu);
        a = cleanupPrimex(a);
        b = cleanupPrimex(b);

        //todo if either ends with id get type.

        if(inappropriateTyping(a ,b ,"boolean", "boolean")){
            print("either value not a boolean");
            //todo throw exception
        }
        return "boolean";
    }

    public String visit(CompareExpression n, SymbolTable argu) throws Exception {
        /*
         * f0 -> PrimaryExpression()
         * f1 -> "<"
         * f2 -> PrimaryExpression()
         */
        String _ret=null;
        String a = n.f0.accept(this, argu);
        String b = n.f2.accept(this, argu);
        a = cleanupPrimex(a);
        b = cleanupPrimex(b);

        if(inappropriateTyping(a ,b ,"int","int" )){
            print("either value not an int");
            //todo throw exception
        }
        return "boolean";
    }

    public String visit(PlusExpression n, SymbolTable argu) throws Exception {
        /*
         * f0 -> PrimaryExpression()
         * f1 -> "+"
         * f2 -> PrimaryExpression()
         */
        String a = n.f0.accept(this, argu);
        String b = n.f2.accept(this, argu);
        a = cleanupPrimex(a);
        b = cleanupPrimex(b);

        if(inappropriateTyping(a ,b ,"int", "int")) {
            print("either value not an int");
            //todo throw exception
        }
        return "int";
    }

    public String visit(MinusExpression n, SymbolTable argu) throws Exception {
        /*
         * f0 -> PrimaryExpression()
         * f1 -> "-"
         * f2 -> PrimaryExpression()
         */
        String a = n.f0.accept(this, argu);
        String b = n.f2.accept(this, argu);
        a = cleanupPrimex(a);
        b = cleanupPrimex(b);

        if(inappropriateTyping(a ,b ,"int", "int")) {
            print("either value not an int");
            //todo throw exception
        }
        return "int";
    }

    public String visit(TimesExpression n, SymbolTable argu) throws Exception {
        /*
         * f0 -> PrimaryExpression()
         * f1 -> "*"
         * f2 -> PrimaryExpression()
         */
        String a = n.f0.accept(this, argu);
        String b = n.f2.accept(this, argu);
        a = cleanupPrimex(a);
        b = cleanupPrimex(b);

        if(inappropriateTyping(a ,b ,"int", "int")) {
            print("either value not an int");
            //todo throw exception
        }
        return "int";

    }

    public String visit(ArrayLookup n, SymbolTable argu) throws Exception {
        /*
         * f0 -> PrimaryExpression() array
         * f1 -> "["
         * f2 -> PrimaryExpression() int
         * f3 -> "]"
         */
        String _ret=null;
        String a = n.f0.accept(this, argu), anew;
        anew = cleanupPrimex(a);
        if(!anew.equals(a)){
            throw new Exception("cannot use array lookup on an allocated expression");
        }
        if(a.endsWith("id")){
            anew = getID(a);
        }else{
            throw new Exception("cannot use array lookup on literal");
        }
        Variable var = currClass.findInScope(anew,currFunction, currClass.ID, currClass);
        if(var == null){
            throw new Exception("cannot find symbol "+ anew+" in \" + currScope.ID + currFunction.ID");
        }
        if(!var.type.endsWith("[]")){
            throw new Exception("cannot use array lookup on non-array variable");
        }



        String b = n.f2.accept(this, argu), bnew;
        bnew = cleanupPrimex(b);
        if(bnew.endsWith("id")){
            bnew = getID(bnew);
            inappropriateTyping(currClass.findInScope(bnew,currFunction, currClass.ID, currClass).type,"int");
        }else {
            inappropriateTyping(bnew,"int");
        }


        return var.type.substring(0,var.type.length() -2);
    }

    public String visit(ArrayLength n, SymbolTable argu) throws Exception {
        /*
         * f0 -> PrimaryExpression()
         * f1 -> "."
         * f2 -> "length"
         */
        String _ret=null;
        String string = n.f0.accept(this, argu),type;
        Variable var;
        cleanupPrimex(string);
        if(string.endsWith("id")){
            var = currClass.findInScope(getID(string),currFunction, currClass.ID, currClass);
            type=var.type;
        }else{
            type = string;
        }
        if(!type.endsWith("[]")){
            throw new Exception("Length operator cannot be used on something that's not array.");
        }
        return type.substring(0,type.length()-2);
    }

    public String visit(MessageSend n, SymbolTable argu) throws Exception {
        /*
         * f0 -> PrimaryExpression()
         * f1 -> "."
         * f2 -> Identifier()
         * f3 -> "("
         * f4 -> ( ExpressionList() )?
         * f5 -> ")"
         */
        String _ret=null;
        String classID = n.f0.accept(this, argu),type;
        Variable var;
        classID = cleanupPrimex(classID);
        ArrayList<String> localArgList = new ArrayList<String>();
        if(!classID.endsWith("id")){
            throw new Exception(classID+" cannot be dereferenced");
        }
        Variable variable = currClass.findInScope(getID(classID),currFunction, currClass.ID, currClass);
        Class aClass = scopeSearch(variable.type);
        String functID = n.f2.accept(this, argu);
        if(!classID.endsWith("id")){
            throw new Exception(classID+" cannot be dereferenced");
        }
        if(!aClass.findFunctionInScope(getID(functID))){
            throw new Exception("Can't find symbol " + classID +" in class " + aClass.ID);
        }
        argStack.add(new ArrayList<>());
        argStackIndex++;
        n.f4.accept(this, argu);
        localArgList = decodeArgList(argStack.get(argStackIndex));
        argStackIndex--;
        argStack.remove(argStack.size()-1);
        Function funct = functionSignatureSearch(aClass.ID,getID(functID) , localArgList);
        //return funct type
        return funct.returnType;
    }




    //for messageSend

    public String visit(ExpressionList n, SymbolTable argu) throws Exception {
        /*
         * f0 -> Expression()
         * f1 -> ExpressionTail()
         */
        String _ret=null;
        argStack.get(argStackIndex).add(n.f0.accept(this, argu));

        n.f1.accept(this, argu);
        return _ret;
    }


    public String visit(ExpressionTerm n, SymbolTable argu) throws Exception {
        /*
         * f0 -> ","
         * f1 -> Expression()
         */
        String _ret=null;
        n.f0.accept(this, argu);
        argStack.get(argStackIndex).add(n.f1.accept(this, argu));
        return _ret;
    }
















    //stationary typing
    public String visit(Identifier n, SymbolTable argu) throws Exception {
        return n.f0.toString() + "id";
    }
    public String visit(IntegerLiteral n, SymbolTable argu) throws Exception {
        return  "int";
    }
    public String visit(TrueLiteral n, SymbolTable argu) throws Exception {
        return  "boolean";
    }
    public String visit(FalseLiteral n, SymbolTable argu) throws Exception {
        return "boolean";
    }
    public String visit(ThisExpression n, SymbolTable argu) throws Exception {
        return n.f0.toString()+"id";
    }
    public String visit(ArrayAllocationExpression n, SymbolTable argu) throws Exception {
        /*
         * f0 -> BooleanArrayAllocationExpression()
         *       | IntegerArrayAllocationExpression()
         */
        return n.f0.accept(this, argu);

    }
    public String visit(BooleanArrayAllocationExpression n, SymbolTable argu) throws Exception {
        String _ret=null;
        /*
         * f0 -> "new"
         * f1 -> "boolean"
         * f2 -> "["
         * f3 -> Expression()
         * f4 -> "]"
         */

        String type = n.f3.accept(this, argu);
        if(inappropriateTyping(type,"int"));
        return "newboolean[]";
    }
    public String visit(IntegerArrayAllocationExpression n, SymbolTable argu) throws Exception {
        String _ret=null;
        /*
         * f0 -> "new"
         * f1 -> "int"
         * f2 -> "["
         * f3 -> Expression()
         * f4 -> "]"
         */
        String type = n.f3.accept(this, argu);
        if(inappropriateTyping(type,"int"));
        return "newint[]";
    }
    public String visit(AllocationExpression n, SymbolTable argu) throws Exception {
        /*
         * f0 -> "new"
         * f1 -> Identifier()
         * f2 -> "("
         * f3 -> ")"
         */

        String _ret=null;
       String type = n.f0.accept(this, argu);

        n.f1.accept(this, argu);
        n.f2.accept(this, argu);
        n.f3.accept(this, argu);
        return type;
    }


    //clauses
    public String visit(NotExpression n, SymbolTable argu) throws Exception {
        String _ret=null;
        /*
         * f0 -> "!"
         * f1 -> Clause()
         */
        String type =n.f1.accept(this, argu);
        if(inappropriateTyping(type,"boolean")){
            print("here");
        }
        return type;
    }
    public String visit(BracketExpression n, SymbolTable argu) throws Exception {
        String _ret=null;
        /*
         * f0 -> "("
         * f1 -> Expression()
         * f2 -> ")"
         */
        return n.f1.accept(this, argu);
    }
    //types (for currFunctionSearch)

    public String visit(BooleanType n, SymbolTable argu) throws Exception {
        /*
         * f0 -> "boolean"
         */
        return "boolean";
    }
    public String visit(IntegerType n, SymbolTable argu) throws Exception {
        /*
         * f0 -> "int"
         */
        return "int";
    }
    public String visit(BooleanArrayType n, SymbolTable argu) throws Exception {

        return "boolean[]";

    }
    public String visit(IntegerArrayType n, SymbolTable argu) throws Exception {
        return "int[]";
    }



}
