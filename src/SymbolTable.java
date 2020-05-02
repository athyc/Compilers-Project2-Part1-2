import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class SymbolTable {
    List<Class> aClasses = new ArrayList<>() ;
    List<String> types = new ArrayList<>(Arrays.asList("int", "boolean", "boolean[]", "int[]", "StringArray"));
    List<String> primaryTypes = new ArrayList<>(Arrays.asList("int", "boolean", "boolean[]", "int[]", "StringArray"));
    List<String> classNames = new ArrayList<>();
    void print(String str){
        System.out.println(str);
    }

    public boolean inPrimaryTypes(String type){
        return types.contains(type);

    }
    public int getVarSize(String varType){
        if(varType.equals("boolean")){
            return 1;
        }
        if(varType.equals("int")  ){
            return 4;
        }
        if(varType.equals("int[]") || varType.equals("boolean[]") || classNames.contains(varType)){
            return 8;
        }
        return 0;
    }
    public void validateST() throws Exception{


        for (Class aClass : aClasses) {
            types.add(aClass.ID);
            classNames.add(aClass.ID);
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

        for (Class aClass : aClasses) {
            for (Variable variable: aClass.Variables){
                //System.out.println(aClass.ID + "."+variable.name+" : " + aClass.varOffset);
                aClass.varOffset+=getVarSize(variable.type);
            }

            for (Function function: aClass.Functions){
                if(!aClass.isOverriden(function)){
                    //System.out.println(aClass.ID + "."+aClass.varOffset);
                    aClass.functOffset+=8;
                }
            }
            aClass.varLocalOffsett = aClass.varOffset;
            aClass.functLocalOffset=aClass.functOffset;
            aClass.functOffset=0;
            aClass.varOffset=0;

        }
        for (Class aClass : aClasses) {
            if (aClass.Inheritance!=null){
                findInheritanceOffset(aClass, aClass);
            }
            //print("Validation done " + scope.ID);
        }

        for (Class aClass : aClasses) {
            for (Variable variable: aClass.Variables){
                System.out.println(aClass.ID + "."+variable.name+" : " + (aClass.varOffset+aClass.varInheritedOffset ));
                aClass.varOffset+=getVarSize(variable.type);
            }

            for (Function function: aClass.Functions){
                if(!aClass.isOverriden(function)){
                    System.out.println(aClass.ID + "."+function.ID+" : " +(aClass.functOffset+aClass.functInheritedOffset  ));
                    aClass.functOffset+=8;
                }

            }

            aClass.classSize=aClass.varOffset+aClass.functOffset;
        }

        //print("Validation done");

    }

    private void findInheritanceOffset(Class classSearch, Class superClass) {
        if(superClass.Inheritance == null){
            return;
        }
        for (Class aClass : aClasses) {
            if(superClass.Inheritance.equals(aClass.ID)){
                classSearch.functInheritedOffset+=aClass.functLocalOffset;
                classSearch.varInheritedOffset+=aClass.varLocalOffsett;
                findInheritanceOffset(classSearch, aClass);
                break;
            }
        }
    }

    private void findInheritance(Class classSearch, Class superClass) throws Exception {
        if(superClass.Inheritance == null){
            return;
        }
        int i=0;
        boolean found = false;
        for (Class aClass : aClasses) {
            if(superClass.Inheritance.equals(aClass.ID)){
                classSearch.InheritedVariables.addAll(aClass.Variables);
                for(Function scfunct : aClass.Functions){
                    for (Function csfunct: classSearch.Functions){

                        if(scfunct.ID.equals(csfunct.ID)){
                            if(scfunct.arguments.size()!=csfunct.arguments.size() || !scfunct.returnType.equals(csfunct.returnType)){
                                throw new Exception("Function " + scfunct.ID+" cannot be overloaded must have arguments as inherited function");
                            }
                            for(i=0;i<scfunct.arguments.size();i++){
                                if(!scfunct.arguments.get(i).type.equals(csfunct.arguments.get(i).type)){
                                    throw new Exception("Function " + scfunct.ID+" cannot be overloaded must have arguments as inherited function!");
                                }
                            }
                        }

                    }
                    classSearch.InheritedFunctions.add(scfunct);
                }

                classSearch.InheritanceLine.add(aClass.ID);
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
