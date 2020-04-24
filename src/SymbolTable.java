import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class SymbolTable {
    List<Scope> Scopes = new ArrayList<>() ;
    void print(String str){
        System.out.println(str);
    }


    public void validateST() throws Exception{

        List<String> types = new ArrayList<>(Arrays.asList("int", "boolean", "booleanArray", "intArray", "StringArray"));
        for (Scope scope : Scopes) {
            types.add(scope.ID);
        }
        for (Scope scope : Scopes){

            for (Variable variable: scope.Variables){
                if(!types.contains(variable.type)){
                    throw new Exception("Error unknown type: " + variable.type + " in class " + scope.ID);

                }
            }
            for (Function function: scope.Functions){
                if(!types.contains(function.returnType) && ! function.returnType.equals("void")){
                    throw new Exception("Error unknown type: " + function.returnType + " in class " + scope.ID);

                }
                for (Variable variable:function.arguments){
                    if(!types.contains(variable.type)){
                        throw new Exception("Error in function arguments "+function.ID+" in class " + scope.ID);
                    }
                }
                for (Variable variable:function.declarations){
                    if(!types.contains(variable.type)){
                        throw new Exception("Error in function declarations "+function.ID+" in class " + scope.ID);
                    }
                }
            }

        }
        for (Scope scope:Scopes) {
            if (scope.Inheritance!=null){
                findInheritance(scope, scope);
            }
            //print("Validation done " + scope.ID);
        }
        //print("Validation done");

    }

    private void findInheritance(Scope scopeSearch, Scope superScope) throws Exception {
        if(superScope.Inheritance == null){
            return;
        }
        boolean found = false;
        for (Scope scope:Scopes) {
            if(superScope.Inheritance.equals(scope.ID)){
                scopeSearch.InheritedVariables.addAll(scope.Variables);
                scopeSearch.InheritedFunctions.addAll(scope.Functions);
                findInheritance(scopeSearch, scope);
                found = true;
                break;
            }
        }
        if(!found){
            throw new Exception("Error! Unknown class " + superScope.Inheritance);
        }

    }


}
