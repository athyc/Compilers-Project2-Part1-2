import java.util.ArrayList;
import java.util.List;

public class Scope {
    String ID, Inheritance = null;
    boolean InheritanceFound = true;
    List<Function> Functions = new ArrayList<>();
    List<Variable> Variables = new ArrayList<>();
    List<Function> InheritedFunctions = new ArrayList<>();
    List<Variable> InheritedVariables = new ArrayList<>();
    private Variable findInScopeVariables(String varID){
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
    public Variable findInScope(String varID, Function function){
        Variable var = null;
        for (Function funct: Functions){
            if(funct.ID.equals(function.ID)) var = funct.lookForVariableInFunction(varID);
        }

        if(var == null)var = findInScopeVariables(varID);
        return var;
    }
}
