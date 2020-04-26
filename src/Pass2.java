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
    Scope currScope = new Scope();
    Function currFunction = new Function();
    List<String> clausesCollector = new ArrayList<>();
    List<Variable> clausesCollectorValidator = new ArrayList<>();
    Variable variable1 = new Variable(), variable2 = new Variable(), variable3 = new Variable();
    String wantedType;
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
        variable3 = currScope.findInScope(var1ID,currFunction);

        n.f2.accept(this, argu);
        //look for both values
        if(!variable3.type.equals(wantedType)){throw new Exception(wantedType+" cannot be converted to "+variable3.type+" Variable "+variable3.name+"\nin class " + currScope.ID + " function " + currFunction.ID);}
        return _ret;
    }
    public String visit(AndExpression n, SymbolTable argu) throws Exception {
        /*
         * f0 -> Clause()
         * f1 -> "&&"
         * f2 -> Clause()
         */
        String _ret=null;
        wantedType = "boolean";
        String clause1 = n.f0.accept(this, argu), clause2 = n.f2.accept(this, argu);
        validatePrimEx(clause1 , clause2);
        if(!variable1.type.equals("boolean")){throw new Exception("bad operand types for binary operator && "+variable1.type+" Variable "+variable1.name+"\nin class " + currScope.ID + " function " + currFunction.ID);}
        if(!variable2.type.equals("boolean")){throw new Exception("bad operand types for binary operator && "+variable2.type+" Variable "+variable2.name+"\nin class " + currScope.ID + " function " + currFunction.ID);}
        return _ret;
    }
    public String visit(CompareExpression n, SymbolTable argu) throws Exception {
        /*
         * f0 -> PrimaryExpression()
         * f1 -> "<"
         * f2 -> PrimaryExpression()
         */
        wantedType = "boolean";
        if(!variable3.type.equals("boolean")){throw new Exception("boolean cannot be converted to "+variable3.type+" Variable "+variable3.name+"\nin class " + currScope.ID + " function " + currFunction.ID);}
        String  _ret=null;
        String primex1 = n.f0.accept(this, argu), primex2 = n.f2.accept(this, argu);
        validatePrimEx(primex1 , primex2);
        if(!variable1.type.equals("int")){throw new Exception("bad operand types for binary operator < "+variable1.type+" Variable "+variable1.name+"\nin class " + currScope.ID + " function " + currFunction.ID);}
        if(!variable2.type.equals("int")){throw new Exception("bad operand types for binary operator < "+variable2.type+" Variable "+variable2.name+"\nin class " + currScope.ID + " function " + currFunction.ID);}
        return _ret;
    }

    public String visit(PlusExpression n, SymbolTable argu) throws Exception {
        /*
         * f0 -> PrimaryExpression()
         * f1 -> "+"
         * f2 -> PrimaryExpression()
         */

        return numericals(argu, n.f0, n.f2,"+");
    }
    public String visit(MinusExpression n, SymbolTable argu) throws Exception {
        /*
         * f0 -> PrimaryExpression()
         * f1 -> "-"
         * f2 -> PrimaryExpression()
         */
        return numericals(argu, n.f0, n.f2 , "-");
    }

    public String visit(TimesExpression n, SymbolTable argu) throws Exception {
        /*
         * f0 -> PrimaryExpression()
         * f1 -> "*"
         * f2 -> PrimaryExpression()
         */

        return numericals(argu, n.f0, n.f2 , "*");
    }
    private String numericals(SymbolTable argu, PrimaryExpression f0, PrimaryExpression f2, String op) throws Exception {
        wantedType = "int";
        String _ret=null;
        String primex1 = f0.accept(this, argu), primex2 = f2.accept(this, argu);
        validatePrimEx(primex1 , primex2);
        if(!variable1.type.equals("int")){throw new Exception("bad operand types for binary operator "+op+" "+variable1.type+" Variable "+variable1.name+"\nin class " + currScope.ID + " function " + currFunction.ID);}
        if(!variable2.type.equals("int")){throw new Exception("bad operand types for binary operator "+op+" "+variable2.type+" Variable "+variable2.name+"\nin class " + currScope.ID + " function " + currFunction.ID);}
        return _ret;
    }
    public String visit(ArrayLookup n, SymbolTable argu) throws Exception {
        String _ret=null;
        /*
         * f0 -> PrimaryExpression()
         * f1 -> "["
         * f2 -> PrimaryExpression()
         * f3 -> "]"
         */
        String primex1 = n.f0.accept(this, argu), primex2 = n.f2.accept(this, argu);
        validatePrimEx(primex1 , primex2);
        if(!(variable1.type.equals("booleanArray") || variable1.type.equals("intArray"))){throw new Exception("array required, but " +variable1.type +" found "+variable1.name+"\nin class " + currScope.ID + " function " + currFunction.ID);}
        wantedType = variable1.type.substring(0, variable1.type.length() - 5);
        if(!variable2.type.equals("int")){throw new Exception(variable2.type+" "+variable2.name+" cannot be converted to int "+"\nin class " + currScope.ID + " function " + currFunction.ID);}
        return _ret;
    }

    public String visit(ArrayLength n, SymbolTable argu) throws Exception {
        /*
         * f0 -> PrimaryExpression()
         * f1 -> "."
         * f2 -> "length"
         */
        String _ret=null;
        String primex1 = n.f0.accept(this, argu);
        validatePrimEx(primex1 , "-");
        wantedType = "int";
        n.f1.accept(this, argu);
        n.f2.accept(this, argu);
        return _ret;
    }
    //stationary typing
    public String visit(Identifier n, SymbolTable argu) throws Exception{
        Variable var1 = currScope.findInScope(n.f0.toString(),currFunction);
        if(var1  == null)throw new Exception("Variable "+n.f0.toString()+" in scope " + currScope.ID + " in function "+ currFunction.ID+" has not been declared");return n.f0.toString();
    }
    public String visit(IntegerLiteral n, SymbolTable argu) throws Exception {return n.f0.toString();}
    public String visit(TrueLiteral n, SymbolTable argu) throws Exception {return "true";}
    public String visit(FalseLiteral n, SymbolTable argu) throws Exception {return "false";}
    public String visit(ThisExpression n, SymbolTable argu) throws Exception {return "this";}

    public String visit(Goal n, SymbolTable argu) throws Exception {
        n.f0.accept(this, argu);
        n.f1.accept(this, argu);
        return "Type checking done";
    }


}
