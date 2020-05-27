import java.util.List;

public class Function {

    String ID;
    String returnType;
    String InheritedFrom = null;
    List<Variable> arguments = null;
    List<Variable> declarations = null;
    int offset=0;

    public Function(String ID, String returnType, String inheritedFrom, List<Variable> arguments, List<Variable> declarations, int offset) {
        this.ID = ID;
        this.returnType = returnType;
        InheritedFrom = inheritedFrom;
        this.arguments = arguments;
        this.declarations = declarations;
        this.offset = offset;
    }

    public Function() {
    }

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
