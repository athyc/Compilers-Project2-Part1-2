import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class SymbolTable {
    List<Class> aClasses = new ArrayList<>() ;
    void print(String str){
        System.out.println(str);
    }


    public void validateST() throws Exception{

        List<String> types = new ArrayList<>(Arrays.asList("int", "boolean", "boolean[]", "int[]", "StringArray"));
        for (Class aClass : aClasses) {
            types.add(aClass.ID);
        }
        for (Class aClass : aClasses){

            for (Variable variable: aClass.Variables){
                if(!types.contains(variable.type)){
                    throw new Exception("Error unknown type: " + variable.type + " in class " + aClass.ID);

                }
            }
            for (Function function: aClass.Functions){
                if(!types.contains(function.returnType) && ! function.returnType.equals("void")){
                    throw new Exception("Error unknown type: " + function.returnType + " in class " + aClass.ID);

                }
                for (Variable variable:function.arguments){
                    if(!types.contains(variable.type)){
                        throw new Exception("Error in function arguments "+function.ID+" in class " + aClass.ID);
                    }
                }
                for (Variable variable:function.declarations){
                    if(!types.contains(variable.type)){
                        throw new Exception("Error in function declarations "+function.ID+" in class " + aClass.ID);
                    }
                }
            }

        }
        for (Class aClass : aClasses) {
            if (aClass.Inheritance!=null){
                findInheritance(aClass, aClass);
            }
            //print("Validation done " + scope.ID);
        }
        //print("Validation done");

    }

    private void findInheritance(Class classSearch, Class superClass) throws Exception {
        if(superClass.Inheritance == null){
            return;
        }
        boolean found = false;
        for (Class aClass : aClasses) {
            if(superClass.Inheritance.equals(aClass.ID)){
                classSearch.InheritedVariables.addAll(aClass.Variables);
                classSearch.InheritedFunctions.addAll(aClass.Functions);
                findInheritance(classSearch, aClass);
                found = true;
                break;
            }
        }
        if(!found){
            throw new Exception("Error! Unknown class " + superClass.Inheritance);
        }

    }


}
