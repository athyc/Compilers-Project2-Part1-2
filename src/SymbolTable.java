import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class SymbolTable {
    List<Class> aClasses = new ArrayList<>() ;
    List<String> types = new ArrayList<>(Arrays.asList("int", "boolean", "boolean[]", "int[]", "String []"));
    List<String> primaryTypes = new ArrayList<>(Arrays.asList("int", "boolean", "boolean[]", "int[]", "String []"));
    List<String> classNames = new ArrayList<>();
    Utils u = new Utils();
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
            // verify all identifiers exist (aka you dont have undeclared A a;)
            for (Variable variable: aClass.Variables){
                if(!types.contains(variable.type)){
                    throw new Exception("Error unknown type: " + variable.type + " in class " + aClass.ID);

                }

            }

            for (Function function: aClass.Functions){
                //verify return type
                if(!types.contains(function.returnType) && ! function.returnType.equals("void")){
                    throw new Exception("Error unknown type: " + function.returnType + " in class " + aClass.ID);

                }
                //an args
                for (Variable variable:function.arguments){
                    if(!types.contains(variable.type)){
                        throw new Exception("Error in function arguments "+function.ID+" in class " + aClass.ID);
                    }
                }
                //and declarations
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
                variable.offset = aClass.varOffset;
                aClass.varOffset+=getVarSize(variable.type);
            }

            for (Function function: aClass.Functions){
                if(!aClass.isOverriden(function)){
                    //System.out.println(aClass.ID + "."+aClass.varOffset);
                    function.offset = aClass.functOffset;
                    aClass.functOffset+=8;
                }
            }
            aClass.varLocalOffsett = aClass.varOffset;
            aClass.functLocalOffset=aClass.functOffset;
            aClass.functOffset=0;
            aClass.varOffset=0;

        }


//        for (Class aClass : aClasses) {
//            for (Variable variable: aClass.Variables){
//                System.out.println(aClass.ID + "."+variable.name+" : " + (aClass.varOffset+aClass.varInheritedOffset ));
//                aClass.varOffset+=getVarSize(variable.type);
//            }
//
//            for (Function function: aClass.Functions){
//                if(!aClass.isOverriden(function)){
//                    System.out.println(aClass.ID + "."+function.ID+" : " +(aClass.functOffset+aClass.functInheritedOffset  ));
//                    aClass.functOffset+=8;
//                }
//
//            }
//
//            aClass.classSize=aClass.varOffset+aClass.functOffset;
//        }

        for (Class c : aClasses){

            if(c.Inheritance==null){
                c.VtableTemplate.addAll(c.Functions);
                c.VariablesTemplate.addAll(c.Variables);
                continue;
            }
            VTableMaker[] Array = new VTableMaker[c.InheritanceLine.size()+1];
            List<String> temp = new ArrayList<>(c.InheritanceLine);
            Collections.reverse(temp);
            for (int i=0;i<c.InheritanceLine.size()+1;i++){
                Array[i] = new VTableMaker();
            }
            int i=0;
            for (String str:temp){
                Array[i].ID=str;
                i++;
            }
            Array[i].ID=c.ID;
            Array[i].funcGroup.addAll(c.Functions);
            Array[i].varGroup.addAll(c.Variables);

            for (Function f :c.InheritedFunctions ){
                for ( i=0;i<c.InheritanceLine.size();i++){
                    if(Array[i].ID.equals(f.InheritedFrom)){
                        Array[i].funcGroup.add(new Function(f.ID,f.returnType,f.InheritedFrom,f.arguments,f.declarations,0));
                    }
                }
            }

            //for(Variable v:c.InheritedVariables)

            List<Function> VTableTemplate = new ArrayList<>(Array[0].funcGroup);
            boolean found = false;

            for (i=1;i<c.InheritanceLine.size()+1;i++){

                for (Function f1:Array[i].funcGroup){

                    for (Function f2:VTableTemplate){
                        if(f1.ID.equals(f2.ID)){
                            f2.InheritedFrom=Array[i].ID;
                            found=true;
                            break;
                        }
                    }
                    if(!found){
                        VTableTemplate.add(f1);

                    }
                    found=false;
                }
            }
            c.VtableTemplate.addAll(VTableTemplate);
            //print("done");
            Collections.reverse(c.InheritedVariables);
            c.VariablesTemplate.addAll(c.InheritedVariables);
            c.VariablesTemplate.addAll(c.Variables);
        }

    }

    private void findInheritanceOffset(Class classSearch, Class superClass) {
        if(superClass.Inheritance == null){
            return;
        }
        for (Class aClass : aClasses) {
            if(superClass.Inheritance.equals(aClass.ID)){
                classSearch.functInheritedOffset+=aClass.functLocalOffset;
                classSearch.varInheritedOffset+=aClass.varLocalOffsett;
                //give inherited functions their offset.
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

                List<Variable> temp = new ArrayList<>(aClass.Variables);
                for(Variable v:temp){
                    v.InheritedFrom = aClass.ID;
                }
                Collections.reverse(temp);
                classSearch.InheritedVariables.addAll(temp);
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
                    scfunct.InheritedFrom =aClass.ID;
                    classSearch.InheritedFunctions.add(scfunct);
                }

                classSearch.InheritanceLine.add(aClass.ID);
                findInheritance(classSearch, aClass);
                found = true;
                break;
            }
        }
        //print("done");
        if(!found){
            throw new Exception("Error! Unknown class " + superClass.Inheritance);
        }

    }
    void Reduct(LLVMRedux newST){
        for (Class c : aClasses){
            LLVMClass temp=new LLVMClass(c);
            int offset=0;
            for (Variable v:temp.VariablesTemplate){
                v.offset=offset;
                offset+=u.javaTypeToOffset(v.type);
            }
            offset=0;
            for (Function f: temp.VtableTemplate){
                f.offset=offset;
                offset+=1;
            }
            newST.aClasses.add(temp);

        }
    }


}
