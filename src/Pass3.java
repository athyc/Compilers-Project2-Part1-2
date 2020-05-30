import syntaxtree.*;
import visitor.GJDepthFirst;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static java.lang.Thread.sleep;

public class Pass3 extends GJDepthFirst <String,SymbolTable>{
    private ArrayList<String> formalParameterCollector = new ArrayList<>();
    ArrayList<ArrayList<String>> argStack = new ArrayList<>();
    private Function currFunction = new Function();
    List<String> debugger = new ArrayList<>();
    private Class currClass = new Class();
    private int argStackIndex = -1;
    SymbolTable ST;
    private Function functionSignatureSearch(String classid, String functid, ArrayList<String> localArgList)throws Exception {
//        if(classid.equals("Tree") && functid.equals("accept")){
//            sleep(1);
//        }
        Class c = scopeSearch(classid);
        Variable v = new Variable();
        int i;
        for (Function f: c.Functions){
            if(f.ID.equals(functid)){
                if(f.arguments.size()!=localArgList.size())continue;
                for ( i = 0 ; i<f.arguments.size(); i++){
                    v = f.arguments.get(i);
                    if(!v.type.equals(localArgList.get(i)) && !inInheritanceLine(v.type,localArgList.get(i))){
                        break;
                    }
                }
                if(i == f.arguments.size())return f;
            }
        }
        for (Function f: c.InheritedFunctions){
            if(f.ID.equals(functid)){
                if(f.arguments.size()!=localArgList.size())continue;
                for ( i = 0 ; i<f.arguments.size(); i++){
                    v = f.arguments.get(i);
                    if(!v.type.equals(localArgList.get(i))&& !inInheritanceLine(v.type,localArgList.get(i))){
                        break;
                    }
                }
                if(i == f.arguments.size())return f;
            }
        }
        throw new Exception("actual and formal argument lists differ in function call "+functid+" of class "+classid+" in "  + currClass.ID +' '+ currFunction.ID );
    }
    private void inappropriateTypeCheck(String type, String wantedType1) throws Exception {

        if(! wantedType1.equals(type)){
            throw new Exception("inappropriate typing in " + currClass.ID +' '+ currFunction.ID);
        }

    }
    private ArrayList<String> decodeArgList(ArrayList<String> argList)throws Exception{
        List<String> types = new ArrayList<>(Arrays.asList("int", "boolean", "boolean[]", "int[]", "String []"));
        Variable variable;
        ArrayList<String> rval = new ArrayList<>();
        for (String arg:argList){
            if(!ST.inPrimaryTypes(arg)){
                variable = currClass.findInScope(arg, currFunction, currClass.ID, currClass);
                rval.add(variable.type);
            }else {
                rval.add(arg);
            }
        }
        return rval;
    }
    private String getArrayType(String type) {return type.substring(0,type.length()-2);}
    private Class scopeSearch(String scopeID) throws Exception{
        for(Class aClass : ST.aClasses){
            if(aClass.ID.equals(scopeID)){
                return aClass;
            }
        }
        throw new Exception("Symbol " + scopeID + " not found in " + currClass.ID +' '+ currFunction.ID);
    }
    public String decodePrimEx(String a) throws Exception{
        a = cleanupPrimex(a);
        if(!ST.inPrimaryTypes(a)){
            a = currClass.getType(a,currFunction, debugger);
        }
        if(a==null) throw new Exception("cannot find symbol "+ a+" in " + currClass.ID + currFunction.ID);
        return a;
    }
    private String cleanupPrimex(String primexRes){
        if(primexRes.startsWith("new ")){
            return primexRes.substring(4);
        }
        return primexRes;
    }
    public Pass3(SymbolTable st) {ST = st;}


//classes
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

        currClass = scopeSearch(n.f1.accept(this, argu));

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

        currClass = scopeSearch(n.f1.accept(this, argu));

        n.f6.accept(this, argu);

        return _ret;
    }

//methods
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
        currFunction = functionSignatureSearch(currClass.ID, functID, formalParameterCollector);
        formalParameterCollector.clear();

        n.f8.accept(this, argu);
        inappropriateTypeCheck(n.f10.accept(this, argu),type);
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




//Statements
    public String visit(Statement n, SymbolTable argu) throws Exception {
        debugger.clear();
        return n.f0.accept(this, argu);
    }

    public String visit(AssignmentStatement n, SymbolTable argu) throws Exception {
        /*
         * f0 -> Identifier()
         * f1 -> "="
         * f2 -> Expression()
         * f3 -> ";"
         */
        String _ret=null;
        String var1ID = n.f0.accept(this, argu);
        debugger.add("=");

        Variable variable3 = currClass.findInScope(var1ID, currFunction, currClass.ID, currClass);
        if(variable3 == null){
            throw new Exception("Variable "+var1ID+" not found in " + currClass.ID+" function " + currFunction.ID+" in statement "+ Arrays.toString(debugger.toArray()));
        }
        if(variable3.type.equals("String")||variable3.type.equals("String[]")){
            throw new Exception("Operations between the arguments of main are henceforth not allowed."+ Arrays.toString(debugger.toArray()));
        }

        String result = n.f2.accept(this, argu);
        //look for both values

        if(!result.equals(variable3.type)){
            //if not variable 3 type in inheritance line of class type
            if((ST.primaryTypes.contains(variable3.type) && !ST.primaryTypes.contains(result)) || (!ST.primaryTypes.contains(variable3.type) && ST.primaryTypes.contains(result))){
                throw new Exception("Variables not matching in assignment statement in " + currClass.ID+' ' + currFunction.ID+ " one is "+variable3.type +" other is "+result+" in statement "+ Arrays.toString(debugger.toArray()));
            }
            if(!inInheritanceLine(variable3.type,result)){
                throw new Exception("Variables not matching in assignment statement in " + currClass.ID+' ' + currFunction.ID+ " one is "+variable3.type +" other is "+result+" in statement "+ Arrays.toString(debugger.toArray()));
            }
        }
//        debugger.clear();
        return _ret;
    }

    private boolean inInheritanceLine(String type, String result) throws Exception {
        if(ST.primaryTypes.contains(type) ||ST.primaryTypes.contains(result)){
            return false;
        }
        Class a = scopeSearch(type);
        Class b = scopeSearch(result);
        for(String classID:b.InheritanceLine){
            if(classID.equals(type)){
                return true;
            }
        }
        return false;
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
        String arrayType = currClass.getType(n.f0.accept(this, argu),currFunction, debugger);
        if(arrayType.equals("String")||arrayType.equals("String[]")){
            throw new Exception("Operations between the arguments of main are henceforth not allowed."+ Arrays.toString(debugger.toArray()));
        }
        debugger.add("[");
        String result = n.f2.accept(this, argu);
        debugger.add("]");
        inappropriateTypeCheck(result,"int");

        debugger.add("=");
        String result1 = n.f5.accept(this, argu);
        inappropriateTypeCheck(getArrayType(arrayType),result1);
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
        debugger.add("if(");
        inappropriateTypeCheck(n.f2.accept(this, argu),"boolean");
        debugger.add(")");
        n.f4.accept(this, argu);
        debugger.add("else {");
        n.f6.accept(this, argu);
        debugger.add("}");
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
        inappropriateTypeCheck(n.f2.accept(this, argu),"boolean");
        n.f4.accept(this, argu);
        return _ret;
    }
    public String visit(PrintStatement n, SymbolTable argu) throws Exception {
        String _ret=null;

        inappropriateTypeCheck(n.f2.accept(this, argu),"int");
        return _ret;
    }




//expressions
    public void Expressions(String a, String b, String wantedType1, String wantedType2)throws Exception{

        a = cleanupPrimex(a);
        b = cleanupPrimex(b);
        if(!ST.inPrimaryTypes(a)){
            a = currClass.getType(a,currFunction, debugger);
        }
        if (!a.equals(wantedType1)) {
            throw new Exception("inappropriate typing in " + currClass.ID +' '+ currFunction.ID);
        }
        if(!ST.inPrimaryTypes(b)){
            b = currClass.getType(b,currFunction, debugger);
        }
        if (!b.equals(wantedType2)) {
            throw new Exception("inappropriate typing in " + currClass.ID +' '+ currFunction.ID);
        }



    }

    public String visit(Expression n, SymbolTable argu) throws Exception {


        String temp = cleanupPrimex(n.f0.accept(this, argu));
        return decodePrimEx(temp);
    }

    public String visit(AndExpression n, SymbolTable argu) throws Exception {
        /*
         * f0 -> Clause()
         * f1 -> "&&"
         * f2 -> Clause()
         */
        String a =n.f0.accept(this, argu);
        debugger.add("&&");
        String b = n.f2.accept(this, argu);
        Expressions(a,b,"boolean","boolean");

        return "boolean";
    }

    public String visit(CompareExpression n, SymbolTable argu) throws Exception {
        /*
         * f0 -> PrimaryExpression()
         * f1 -> "<"
         * f2 -> PrimaryExpression()
         */
        String a =n.f0.accept(this, argu);
        debugger.add("<");
        String b = n.f2.accept(this, argu);
        Expressions(a,b,"int","int");
        return "boolean";
    }

    public String visit(PlusExpression n, SymbolTable argu) throws Exception {
        /*
         * f0 -> PrimaryExpression()
         * f1 -> "+"
         * f2 -> PrimaryExpression()
         */
        String a =n.f0.accept(this, argu);
        debugger.add("+");
        String b = n.f2.accept(this, argu);
        Expressions(a,b,"int","int");
        return "int";
    }

    public String visit(MinusExpression n, SymbolTable argu) throws Exception {
        /*
         * f0 -> PrimaryExpression()
         * f1 -> "-"
         * f2 -> PrimaryExpression()
         */
        String a =n.f0.accept(this, argu);
        debugger.add("-");
        String b = n.f2.accept(this, argu);
        Expressions(a,b,"int","int");
        return "int";
    }

    public String visit(TimesExpression n, SymbolTable argu) throws Exception {
        /*
         * f0 -> PrimaryExpression()
         * f1 -> "*"
         * f2 -> PrimaryExpression()
         */
        String a =n.f0.accept(this, argu);
        debugger.add("*");
        String b = n.f2.accept(this, argu);
        Expressions(a,b,"int","int");
        return "int";

    }

    public String visit(ArrayLookup n, SymbolTable argu) throws Exception {
        /*
         * f0 -> PrimaryExpression() array
         * f1 -> "["
         * f2 -> PrimaryExpression() int
         * f3 -> "]"
         */
        String _ret = null;
        String a = n.f0.accept(this, argu), anew;
        anew = cleanupPrimex(a);
        if(!anew.equals(a)){
            throw new Exception("cannot use array lookup on a newly allocated expression in " + currClass.ID + currFunction.ID);
        }
        debugger.add("[");
        anew=decodePrimEx(anew);
        if(!anew.endsWith("[]")){
            throw new Exception("cannot use array lookup on non-array variable in " + currClass.ID + currFunction.ID);
        }
        anew = getArrayType(anew);
        String b = n.f2.accept(this, argu), bnew;
        bnew = decodePrimEx(cleanupPrimex(b));
        inappropriateTypeCheck(bnew,"int");
        debugger.add("]");

        return anew;

    }

    public String visit(ArrayLength n, SymbolTable argu) throws Exception {
        /*
         * f0 -> PrimaryExpression()
         * f1 -> "."
         * f2 -> "length"
         */

        String a = n.f0.accept(this, argu), anew;
        anew = decodePrimEx(cleanupPrimex(a));
        debugger.add(".length");
        if(!anew.endsWith("[]")){
            throw new Exception("Length operator cannot be used on something that's not array.");
        }
        return getArrayType(anew);
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

        String classID, oldID = n.f0.accept(this, argu),type;
        Class aClass;
        ArrayList<String> localArgList;

        classID = cleanupPrimex(oldID);

        debugger.add(".");
        type = decodePrimEx(classID);
        if(ST.primaryTypes.contains(type)){ //if is int bool etc
            throw new Exception("Cannot be dereferenced in " + currClass.ID+' ' + currFunction.ID);
        }
        aClass = scopeSearch(type);
        String functID = n.f2.accept(this, argu);
        debugger.add("(");
        if(!aClass.findFunctionInScope(functID)){
            throw new Exception("Can't find symbol " + classID +" in class " + aClass.ID);
        }
//        if(aClass.ID.equals("Tree") && functID.equals("accept")){
//            sleep(1);
//        }
        argStack.add(new ArrayList<>());
        argStackIndex++;
        n.f4.accept(this, argu);
        localArgList = decodeArgList(argStack.get(argStackIndex));
        argStackIndex--;
        argStack.remove(argStack.size()-1);
        debugger.add(")");
        Function funct = functionSignatureSearch(aClass.ID,functID, localArgList);
        return funct.returnType;

    }

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
        debugger.add(",");
        argStack.get(argStackIndex).add(n.f1.accept(this, argu));
        return _ret;
    }










//primary expressions
    public String visit(Identifier n, SymbolTable argu) throws Exception {
        debugger.add(n.f0.toString());
        return n.f0.toString();
    }
    public String visit(IntegerLiteral n, SymbolTable argu) throws Exception {
        debugger.add(n.f0.toString());
        return  "int";
    }
    public String visit(TrueLiteral n, SymbolTable argu) throws Exception {
        debugger.add(n.f0.toString());
        return  "boolean";
    }
    public String visit(FalseLiteral n, SymbolTable argu) throws Exception {
        debugger.add(n.f0.toString());
        return "boolean";
    }
    public String visit(ThisExpression n, SymbolTable argu) throws Exception {
        debugger.add(n.f0.toString());
        return n.f0.toString();
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
        debugger.add(n.f0.toString());
        debugger.add(n.f1.toString() + '[');
        String type = n.f3.accept(this, argu);
        inappropriateTypeCheck(type,"int");
        debugger.add("]");
        return "new boolean[]";
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
        debugger.add(n.f0.toString());
        debugger.add(n.f1.toString() + '[');
        String type = n.f3.accept(this, argu);
        inappropriateTypeCheck(type,"int");
        debugger.add("]");
        return "new int[]";
    }
    public String visit(AllocationExpression n, SymbolTable argu) throws Exception {
        /*
         * f0 -> "new"
         * f1 -> Identifier()
         * f2 -> "("
         * f3 -> ")"
         */



        String type = n.f1.accept(this, argu);
        debugger.add("new " + type +"()");
        return "new "+type;
    }


//clauses
    public String visit(NotExpression n, SymbolTable argu) throws Exception {
        String _ret=null;
        /*
         * f0 -> "!"
         * f1 -> Clause()
         */
        debugger.add("!");
        String type =n.f1.accept(this, argu);
        //inappropriateTypeCheck(type,"boolean");
        return type;
    }
    public String visit(BracketExpression n, SymbolTable argu) throws Exception {
        /*
         * f0 -> "("
         * f1 -> Expression()
         * f2 -> ")"
         */
        String _ret;
        debugger.add("(");
        _ret= n.f1.accept(this, argu);
        debugger.add(")");
        return _ret;
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
