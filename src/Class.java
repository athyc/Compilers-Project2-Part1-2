import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Class {
    String ID, Inheritance = null;
    int classSize=0, varOffset = 0, functOffset = 0, classInheritedOffset=0,varLocalOffsett =0,functLocalOffset=0,varInheritedOffset=0,functInheritedOffset=0;
    List<Function> Functions = new ArrayList<>();
    List<Variable> Variables = new ArrayList<>();
    List<Function> InheritedFunctions = new ArrayList<>();
    List<Variable> InheritedVariables = new ArrayList<>();
    List<String> InheritanceLine = new ArrayList<>();

    public Variable findInScopeVariables(String varID){
        for (Variable var:Variables) {
            if(var.name.equals(varID))return var;
        }
        for (Variable var:InheritedVariables) {
            if(var.name.equals(varID))return var;
        }
        return null;
    }
    public void addVariable(String accept, String stringArray) {
        Variable temp = new Variable();
        temp.name = accept;
        temp.type = stringArray;
        Variables.add(temp);
        temp = null;
    }



    public boolean findFunctionInScope(String function) {
        for (Function funct: Functions){
            if(funct.ID.equals(function)) return true;
        }
        for (Function funct: InheritedFunctions){
            if(funct.ID.equals(function)) return true;
        }
        return false;
    }
    public Variable findInScope(String varID, Function function, String ScopeID, Class currScope)throws Exception{
        Variable var = null;
        if(varID.equals("this")){
            var = new Variable();
            var.type = currScope.ID;
            return var;
        }
         var = function.lookForVariableInFunction(varID);


        if(var == null)var = findInScopeVariables(varID);

        return var;
    }
    public String getType(String varID, Function function, List<String> debugger) throws Exception {
        if(varID.equals("this")){
            return this.ID;
        }
        Variable var = function.lookForVariableInFunction(varID);


        if(var == null)var = findInScopeVariables(varID);
        if(var==null){
            throw new Exception("Variable" + varID+" not found " + " in function "+function.ID+ Arrays.toString(debugger.toArray()));
        }
        return var.type;
    }



    public boolean isOverriden(Function function) throws Exception {
        int i;
        for (Function f :InheritedFunctions){
            if(function.ID.equals(f.ID) && function.arguments.size()==f.arguments.size()){
                for (i=0;i<f.arguments.size();i++){
                    if(!f.arguments.get(i).type.equals(function.arguments.get(i).type)){
                        break;
                    }
                }
                if(i==f.arguments.size()){
                    if(!function.returnType.equals(f.returnType)){
                        throw new Exception("cannot override " + f.ID + " in " + this.ID + " from " + f.returnType + " to " + function.returnType);
                    }
                    return true;
                }


            }
        }
        return false;
    }
}
