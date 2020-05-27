import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class LLVMClass {
    String ID;
    List<Function> VtableTemplate = new ArrayList<>();
    List<Variable> VariablesTemplate = new ArrayList<>();
    List<String> InheritanceLine = new ArrayList<>();
    String getLLVMVtableFormat(){
        return "["+VtableTemplate.size()+" x i8*]";
    }
    private int size =8;
    private int javaTypeToOffset(String returnType) {
        switch (returnType){
            case "int":
                return 4;
            case "boolean":
                return 1;
            case "int[]":
                return 8;
            default:
                return 8;
        }

    }
    public LLVMClass(Class a){
        ID=a.ID;
        VtableTemplate.addAll(a.VtableTemplate);
        VariablesTemplate.addAll(a.VariablesTemplate);
        InheritanceLine.addAll(a.InheritanceLine);
        for (Variable v: VariablesTemplate){
            size+=javaTypeToOffset(v.type);
        }
    }
    Function getFunction(String functiID){
        for (Function f: VtableTemplate){
            if(f.ID.equals(functiID)){
                return f;
            }
        }
        return null;
    }

    public int getSize() {
        return size;
    }


}
