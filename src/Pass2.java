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
    Scope currScope = new Scope(), MSScope = null;
    Function currFunction = new Function();
    List<String> clausesCollector = new ArrayList<>();
    List<Variable> callArgCollector = new ArrayList<>();
    List<List<String>> argStack = new ArrayList<>();
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



    private boolean inappropriateTyping(String a, String b, String wantedType1, String wantedType2) throws Exception {
        if (a.endsWith("id")) {
            Variable av =  currScope.findInScope(getID(a),currFunction,currScope.ID  );
            if(av == null){
                throw new Exception("Variable not found in " + currScope.ID + currFunction.ID);
            }
            if(! wantedType1.equals(av.type)){
                throw new Exception("inappropriate typing in " + currScope.ID + currFunction.ID);
            }
        } else {
            if (!a.endsWith(wantedType1)) {
                throw new Exception("inappropriate typing in " + currScope.ID + currFunction.ID);
            }
        }
        if (b.endsWith("id")) {
            Variable bv =  currScope.findInScope(getID(b),currFunction,currScope.ID );
            if(bv == null){
                throw new Exception("Variable not found in " + currScope.ID + currFunction.ID);
            }
            if(! wantedType1.equals(bv.type)){
                throw new Exception("inappropriate typing in " + currScope.ID + currFunction.ID);
            }
        } else {
            if (!b.endsWith(wantedType2)) {
                throw new Exception("inappropriate typing in " + currScope.ID + currFunction.ID);
            }
        }
        return false;
    }
    private boolean inappropriateTyping(String a, String wantedType1) throws Exception {
        if (a.endsWith("id")) {
            Variable av =  currScope.findInScope(getID(a),currFunction, currScope.ID );
            if(av == null){
                throw new Exception("Variable not found in " + currScope.ID + currFunction.ID);
            }else {
                if(!wantedType1.equals(av.type)){
                    throw new Exception("inappropriate typing in " + currScope.ID + currFunction.ID);
                }
            }
        } else {
            if (!a.endsWith(wantedType1)) {
                throw new Exception("inappropriate typing in " + currScope.ID + currFunction.ID);
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
    public Scope scopeSearch(String scopeID){
        for(Scope scope: ST.Scopes){
            if(scope.ID.equals(scopeID)){
                return scope;
            }
        }
        return null;
    }

    public String visit(MainClass n, SymbolTable argu)throws Exception {
        /*
         mainclass
         * f1 -> Identifier()
         * f15 -> ( Statement() )*
         */
        String _ret=null;

        currScope = ST.Scopes.get(0);

        currFunction = ST.Scopes.get(0).Functions.get(0);

        n.f0.accept(this, argu);

        n.f15.accept(this, argu);

        return _ret;
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

        variable3 = currScope.findInScope(getID(var1ID), currFunction, currScope.ID );
        if(variable3 == null){
            throw new Exception("Variable not found in " + currScope.ID + currFunction.ID);
        }

        String result = n.f2.accept(this, argu);
        //look for both values
        if(!result.equals(variable3.type)){
            throw new Exception("Variables not matching in assignment statement in " + currScope.ID + currFunction.ID);
        }
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
        Variable var = currScope.findInScope(anew,currFunction,currScope.ID );
        if(var == null){
            throw new Exception("cannot find symbol "+ anew+" in \" + currScope.ID + currFunction.ID");
        }
        if(!var.type.endsWith("Array")){
            throw new Exception("cannot use array lookup on non-array variable");
        }



        String b = n.f2.accept(this, argu), bnew;
        bnew = cleanupPrimex(b);
        if(bnew.endsWith("id")){
            bnew = getID(bnew);
            inappropriateTyping(currScope.findInScope(bnew,currFunction,currScope.ID  ).type,"int");
        }else {
            inappropriateTyping(bnew,"int");
        }


        return var.type.substring(0,var.type.length() -5);
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
            var = currScope.findInScope(getID(string),currFunction, currScope.ID );
            type=var.type;
        }else{
            type = string;
        }
        if(!type.endsWith("Array")){
            throw new Exception("Length operator cannot be used on something that's not array.");
        }
        return type.substring(0,type.length()-5);
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
        String string = n.f0.accept(this, argu),type;
        Variable var;
        string = cleanupPrimex(string);
        if(!string.endsWith("id")){
            throw new Exception(string+" cannot be dereferenced");
        }
        Variable variable = currScope.findInScope(getID(string),currFunction,currScope.ID);
        Scope scope = scopeSearch(variable.type);
        string = n.f2.accept(this, argu);
        if(!string.endsWith("id")){
            throw new Exception(string+" cannot be dereferenced");
        }
        if(!scope.findFunctionInScope(getID(string))){
            throw new Exception("Can't find symbol " + string +" in class " + scope.ID);
        }
        n.f4.accept(this, argu);

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
        return n.f0.toString();
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
        return "newbooleanArray";
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
        return "newintArray";
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
}
