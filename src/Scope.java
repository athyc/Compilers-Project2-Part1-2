import java.util.ArrayList;
import java.util.List;

public class Scope {
    String ID, Inheritance = null;
    boolean InheritanceFound = true;
    List<Function> Functions = new ArrayList<>();
    List<Variable> Variables = new ArrayList<>();
    List<Function> InheritedFunctions = new ArrayList<>();
    List<Variable> InheritedVariables = new ArrayList<>();
    public void addVariable(String accept, String stringArray) {
        Variable temp = new Variable();
        temp.name = accept;
        temp.type = stringArray;
        Variables.add(temp);
        temp = null;
    }
}
