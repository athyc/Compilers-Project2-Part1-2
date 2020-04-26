import java.util.List;

public class Function {
    String ID;
    String returnType;
    String Inherited = null;
    List<Variable> arguments = null;
    List<Variable> declarations = null;
    public Variable lookForVariableInFunction(String varID){
        for (Variable var:arguments){
            if(var.name.equals(varID))return var;

        }
        for (Variable var:declarations){
            if(var.name.equals(varID))return var;

        }
        return null;
    }
}
