import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class LLVMRedux {
    List<LLVMClass> aClasses = new ArrayList<>() ;
    List<String> primaryTypes = new ArrayList<>(Arrays.asList("i32", "i1", "i8*", "i32*"));
    LLVMClass findClass(String classID){
        for(LLVMClass llvmc: aClasses){
            if(llvmc.ID.equals(classID))return llvmc;
        }
        return null;
    }
}
