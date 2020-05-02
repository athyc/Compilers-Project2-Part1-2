import syntaxtree.*;
import visitor.GJDepthFirst;

import java.util.ArrayList;
import java.util.List;


public class Pass1 extends GJDepthFirst<String,SymbolTable> {
    int position=0;
    String posId = null;
    List<Variable> variablesCollector = new ArrayList<>();
    List<Variable> formalParameterCollector = new ArrayList<>();
    List<Function> methods = new ArrayList<>();
    private boolean existsInList(Variable temp, List<Variable> formalParameterCollector) {
        for (Variable variable : formalParameterCollector) {
            if (temp.name.equals(variable.name)) {
                return true;
            }
        }
        return false;
    }

    private boolean existsInMethods(Function temp, List<Function> formalParameterCollector) {
        Variable variable = new Variable();
        boolean argChecker ;
        int i;
        for (Function function : formalParameterCollector) {
            argChecker = true;
            if (temp.ID.equals(function.ID)) {

                if(temp.arguments.size() != function.arguments.size()) continue;

                for (i=0 ;i < function.arguments.size();i++ ){
                    argChecker = argChecker & function.arguments.get(i).type.equals(temp.arguments.get(i).type);
                }
                if(argChecker)return true;
            }
        }
        return false;
    }
    void print(String str){
        System.out.println(str);
    }

    /**
     mainclass
     * f1 -> Identifier()
     * f11 -> Identifier()
     * f14 -> ( VarDeclaration() )*
     * f15 -> ( Statement() )*
     */

    public String visit(MainClass n, SymbolTable argu)throws Exception {
        String _ret=null;
        List<Variable> variables = new ArrayList<>();
        Variable variable = new Variable();
        List<Function> functions;
        Function function = new Function();
        Class aClass = new Class();
        aClass.ID = n.f1.accept(this, argu);

        function.ID = "main";
        function.returnType = "void";
        variable.type = "StringArray";
        variable.name = n.f11.accept(this, argu);
        function.arguments = new ArrayList<>();
        function.arguments.add(variable);

        n.f14.accept(this, argu);
        function.declarations = new ArrayList<>(this.variablesCollector);
        if(existsInList(function.arguments.get(0), variablesCollector)){
            throw new Exception("Variable in main has same name as argument");
        }
        this.variablesCollector.clear();
        aClass.Functions.add(function);
        argu.aClasses.add(aClass);

        return null;
    }
    //throws Exception
    /**
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
    public String visit(MethodDeclaration n, SymbolTable argu)  throws Exception{
        String _ret=null;
        Function temp = new Function();
        temp.returnType = n.f1.accept(this, argu);
        temp.ID = n.f2.accept(this, argu);
        n.f4.accept(this, argu);
        temp.arguments = new ArrayList<>(formalParameterCollector);

        n.f7.accept(this, argu);
        temp.declarations = new ArrayList<>(variablesCollector);
        variablesCollector.clear();
        formalParameterCollector.clear();
        //todo if function exists!
        if(existsInMethods(temp, methods)){
            throw new Exception("Function declared twice in scope");
        }
        methods.add(temp);
        return _ret;
    }
    /**
     * f0 -> "class"
     * f1 -> Identifier()
     * f2 -> "{"
     * f3 -> ( VarDeclaration() )*
     * f4 -> ( MethodDeclaration() )*
     * f5 -> "}"
     */
    public String visit(ClassDeclaration n, SymbolTable argu)throws Exception {
        String _ret=null;
        Class aClass = new Class();
        aClass.ID = n.f1.accept(this, argu);

        n.f3.accept(this, argu);
        aClass.Variables = new ArrayList<>(this.variablesCollector);
        this.variablesCollector.clear();
        n.f4.accept(this, argu);
        aClass.Functions = new ArrayList<>(this.methods);
        methods.clear();
        for(Class class1 :argu.aClasses){
            if(aClass.ID.equals(class1.ID)){
                throw new Exception("Two classes have the same name " + aClass.ID);
            }
        }
        argu.aClasses.add(aClass);
        return _ret;
    }



    /**
     * f0 -> "class"
     * f1 -> Identifier()
     * f2 -> "extends"
     * f3 -> Identifier()
     * f4 -> "{"
     * f5 -> ( VarDeclaration() )*
     * f6 -> ( MethodDeclaration() )*
     * f7 -> "}"
     */
    public String visit(ClassExtendsDeclaration n, SymbolTable argu)throws Exception {
        String _ret=null;
        Class aClass = new Class();
        aClass.ID = n.f1.accept(this, argu);

        aClass.Inheritance = n.f3.accept(this, argu);

        n.f5.accept(this, argu);
        aClass.Variables.addAll(variablesCollector);
        variablesCollector.clear();
        n.f6.accept(this, argu);
        aClass.Functions.addAll(methods);
        methods.clear();
        if(aClass.ID.equals(aClass.Inheritance)){
            throw new Exception(" cyclic inheritance involving " + aClass.ID);
        }
        for(Class class1 :argu.aClasses){
            if(aClass.ID.equals(class1.ID)){
                throw new Exception("Two classes have the same name " + aClass.ID);
            }
        }
        argu.aClasses.add(aClass);

        return _ret;
    }

    public String visit(FormalParameter n, SymbolTable argu)throws Exception{
        /*
         * f0 -> Type()
         * f1 -> Identifier()
         */
        String _ret=null;
        Variable temp = new Variable();
        temp.type = n.f0.accept(this, argu);
        temp.name = n.f1.accept(this, argu);
        //throw error  todo
        if(existsInList(temp, formalParameterCollector)){
            throw new Exception("Formal parameter " + temp.name + " declared twice");
        }
        formalParameterCollector.add(temp);
        return _ret;
    }




    /**
     * f0 -> Type()
     * f1 -> Identifier()
     * f2 -> ";"
     *
     * variable declaration
     */
    public String visit(VarDeclaration n, SymbolTable argu) throws Exception{
        String _ret=null;
        Variable temp = new Variable();
        temp.type = n.f0.accept(this, argu);
        temp.name = n.f1.accept(this, argu);
        //throw error todo
        if(existsInList(temp, variablesCollector) || existsInList(temp, formalParameterCollector)){
            throw new Exception("Variable " + temp.name +" declared twice");
        }
        variablesCollector.add(temp);
        return _ret;
    }

    //stationary typing
    public String visit(Identifier n, SymbolTable argu) {
        return n.f0.toString();
    }
    /**
     * f0 -> "boolean"
     * f1 -> "["
     * f2 -> "]"
     */
    public String visit(BooleanArrayType n, SymbolTable argu) {
        return "boolean[]";
    }
    /**
     * f0 -> "int"
     * f1 -> "["
     * f2 -> "]"
     */
    public String visit(IntegerArrayType n, SymbolTable argu) {
        return "int[]";
    }
    /**
     * f0 -> "boolean"
     */
    public String visit(BooleanType n, SymbolTable argu) {
        return "boolean";
    }
    /**
     * f0 -> "int"
     */
    public String visit(IntegerType n, SymbolTable argu) {
        return "int";
    }
    public String visit(Goal n, SymbolTable argu) throws Exception {
        n.f0.accept(this, argu);
        n.f1.accept(this, argu);
        return "Symbol Table filled";
    }

}
