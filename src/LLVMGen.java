import syntaxtree.*;
import visitor.GJDepthFirst;

import java.util.ArrayList;
import java.util.ListIterator;
import java.util.Objects;

public class LLVMGen extends GJDepthFirst<String,LLVMRedux> {
    private LLVMRedux ST;
    //functions
    private Utils u = new Utils();
    String FPAllocs;
    private int argStackIndex = -1;
    ArrayList<ArrayList<String>> argStack = new ArrayList<>();
    private LLVMClass scopeSearch(String scopeID) {
        for(LLVMClass aClass : ST.aClasses){
            if(aClass.ID.equals(scopeID)){
                return aClass;
            }
        }
        return null;
    }
    public LLVMGen(LLVMRedux ST){
        this.ST = ST;
        //start of vtable creation
        System.out.println("@."+ST.aClasses.get(0).ID+"_vtable = global [0 x i8*] []");
        for (int i=1;i<ST.aClasses.size();i++){
            LLVMClass curr = ST.aClasses.get(i);
            System.out.print("@."+curr.ID+"_vtable = global ["+curr.VtableTemplate.size()+" x i8*] [");
            int j=0;
            for (Function f: curr.VtableTemplate){
                String args = u.argsToString(f.arguments);
                System.out.print("i8* bitcast ( " + u.javaTypeToLLVMType(f.returnType)+" (i8*"+args+")* @"+f.InheritedFrom+"."+f.ID+" to i8*)");
                if(j==curr.VtableTemplate.size()-1){
                    System.out.println("]\n");
                }else{
                    System.out.println(",");
                }
                j++;
            }
            if(j==0)System.out.println("]");
        }

        //end of vtable creation
        u.declare();
        //todo add offsets.
    }
    public String visit(MainClass n, LLVMRedux argu) throws Exception {
        /*
         * f0 -> "class"
         * f1 -> Identifier()
         * f2 -> "{"
         * f3 -> "public"
         * f4 -> "static"
         * f5 -> "void"
         * f6 -> "main"
         * f7 -> "("
         * f8 -> "String"
         * f9 -> "["
         * f10 -> "]"
         * f11 -> Identifier()
         * f12 -> ")"
         * f13 -> "{"
         * f14 -> ( VarDeclaration() )*
         * f15 -> ( Statement() )*
         * f16 -> "}"
         * f17 -> "}"
         */
        System.out.println("define i32 @main() {");
        u.increaseIndentation();
        u.currClass=argu.aClasses.get(0);
        u.currFunction=u.currClass.VtableTemplate.get(0);

        n.f14.accept(this, argu);
        n.f15.accept(this, argu);

        u.decreaseIndentation();
        System.out.println("\tret i32 0\n" +"}");
        return null;
    }
    public String visit(ClassDeclaration n, LLVMRedux argu) throws Exception {
        /*
         * f0 -> "class"
         * f1 -> Identifier()
         * f2 -> "{"
         * f3 -> ( VarDeclaration() )*
         * f4 -> ( MethodDeclaration() )*
         * f5 -> "}"
         */
        u.setCurrClass(ST.findClass(n.f1.accept(this, argu)));


        n.f4.accept(this, argu);

        return null;
    }
    public String visit(ClassExtendsDeclaration n, LLVMRedux argu) throws Exception {


        u.setCurrClass(ST.findClass(n.f1.accept(this, argu)));
        n.f6.accept(this, argu);

        return null;
    }
    public String visit(VarDeclaration n, LLVMRedux argu) throws Exception {
        /*
         * f0 -> Type()
         * f1 -> Identifier()
         * f2 -> ";"
         */

        u.println("%"+n.f1.accept(this, argu)+" = alloca "+n.f0.accept(this,argu));
        u.println("");
        return null;
    }
    public String visit(MethodDeclaration n, LLVMRedux argu) throws Exception {
        /*
         * f0 -> "public"
         * f1 -> Type()
         * f2 -> Identifier()
         * f3 -> "("
         * f4 -> ( FormalParameterList() )?
         * f5 -> ")"
         * f6 -> "{"
         * f7 -> ( VarDeclaration() )*
         * f8 -> ( Statement() )*
         * f9 -> "return"
         * f10 -> Expression()
         * f11 -> ";"
         * f12 -> "}"
         */
        String _ret=null;
        u.setCurrFunction(u.currClass.getFunction(n.f2.accept(this, argu)));
        u.print("define " + n.f1.accept(this, argu)+" @" + u.getCurrClass().ID+"."+u.currFunction.ID+"(");
        n.f4.accept(this, argu);
        u.println(") {");
        u.increaseIndentation();
        u.simplePrint(u.getFPallocs());

        n.f8.accept(this, argu);
        u.println("ret "+u.javaTypeToLLVMType(u.currFunction.returnType)+" "+n.f10.accept(this, argu));

        u.decreaseIndentation();
        u.println("}");
        u.regReset();
        return _ret;
    }
    public String visit(FormalParameterList n, LLVMRedux argu) throws Exception {
        /*
         * f0 -> FormalParameter()
         * f1 -> FormalParameterTail()
         */
        String _ret=null;
        u.print("i8* %this, ");
        n.f0.accept(this, argu);
        n.f1.accept(this, argu);
        return _ret;
    }
    public String visit(FormalParameter n, LLVMRedux argu) throws Exception {
        /*
         * f0 -> Type()
         * f1 -> Identifier()
         */
        u.printFP(n.f0.accept(this, argu)," %.",n.f1.accept(this, argu));
        return null;

    }
    public String visit(FormalParameterTail n, LLVMRedux argu) throws Exception {
        /*
         * f0 -> ( FormalParameterTerm() )*
         */

        return n.f0.accept(this, argu);
    }
    public String visit(FormalParameterTerm n, LLVMRedux argu) throws Exception {
        /*
         * f0 -> ","
         * f1 -> FormalParameter()
         */
        String _ret=null;
        u.print(", ");
        n.f1.accept(this, argu);
        return _ret;
    }
    public String visit(AssignmentStatement n, LLVMRedux argu) throws Exception {
        /*
         * f0 -> Identifier()
         * f1 -> "="
         * f2 -> Expression()
         * f3 -> ";"
         */
        String Identifier = n.f0.accept(this, argu);
        String storeArg1 = u.decodeIdentifier(Identifier);
        String type = u.getIdentifierType(Identifier);
        String storeArg2 =  n.f2.accept(this, argu);
        u.println("store "+u.javaTypeToLLVMType(type)+" "+storeArg2+", "+u.pointer(u.javaTypeToLLVMType(type))+" "+storeArg1);
        return storeArg1;
    }
    public String visit(ArrayAssignmentStatement n, LLVMRedux argu) throws Exception {
        /*
         * f0 -> Identifier()
         * f1 -> "["
         * f2 -> Expression()
         * f3 -> "]"
         * f4 -> "="
         * f5 -> Expression()
         * f6 -> ";"
         */
        String storeArg1 = u.arrayAssignment(n.f0.accept(this,argu),n.f2.accept(this,argu));
        u.println("store "+u.javaTypeToLLVMType(u.getRegType(storeArg1))+" "+n.f5.accept(this, argu)+", "+u.pointer(u.javaTypeToLLVMType(u.getRegType(storeArg1)))+" "+storeArg1);



        return null;
    }
    public String visit(IfStatement n, LLVMRedux argu) throws Exception {
        /*
         * f0 -> "if"
         * f1 -> "("
         * f2 -> Expression()
         * f3 -> ")"
         * f4 -> Statement()
         * f5 -> "else"
         * f6 -> Statement()
         */
        String[] array =u.getConditionTags();
        u.println("br i1 "+ n.f2.accept(this, argu)+" label "+array[0]+", "+array[1]);
        u.increaseIndentation();
        u.println(array[0]+":");
        n.f4.accept(this, argu);
        u.println(array[1]+":");
        n.f6.accept(this, argu);
        u.decreaseIndentation();
        return null;
    }
    public String visit(PrintStatement n, LLVMRedux argu) throws Exception {
        /*
         * f0 -> "System.out.println"
         * f1 -> "("
         * f2 -> Expression()
         * f3 -> ")"
         * f4 -> ";"
         */

        u.println("call void (i32) @print_int(i32 "+n.f2.accept(this, argu)+")");
        return null;
    }
    public String visit(WhileStatement n, LLVMRedux argu) throws Exception {
        /*
         * f0 -> "while"
         * f1 -> "("
         * f2 -> Expression()
         * f3 -> ")"
         * f4 -> Statement()
         */


        //first loop tag goes here
        String[]  tag = u.getWhileTags();
        u.increaseIndentation();
        u.println(tag[0]+":");
        u.println("br i1 "+ n.f2.accept(this, argu)+", label "+tag[1]+", label "+tag[2]);
        u.println(tag[1]+":");
        n.f4.accept(this, argu);
        u.println("br label "+tag[0]);
        u.println(tag[2]+":");
        u.decreaseIndentation();
        return null;
    }

    public String visit(PrimaryExpression n, LLVMRedux argu) throws Exception {
        String result =  n.f0.accept(this, argu);
        return u.getArg(result);

    }
    public String visit(AndExpression n, LLVMRedux argu) throws Exception {
        /*
         * f0 -> Clause()
         * f1 -> "&&"
         * f2 -> Clause()
         */

        String arg1 = n.f0.accept(this, argu), arg2 = n.f2.accept(this, argu);
        u.println(u.getReg()+" = and i1 "+arg1+", "+arg2);
        return u.getLastReg();
    }
    public String visit(CompareExpression n, LLVMRedux argu) throws Exception {
        /*
         * f0 -> PrimaryExpression()
         * f1 -> "<"
         * f2 -> PrimaryExpression()
         */

        String arg1 = n.f0.accept(this, argu);
        String arg2 = n.f2.accept(this, argu);
        u.println(u.getReg()+" = icmp slt i32 "+arg1+", "+arg2);
        return u.getLastReg();
    }
    public String visit(PlusExpression n, LLVMRedux argu) throws Exception {
        /*
         * f0 -> PrimaryExpression()
         * f1 -> "+"
         * f2 -> PrimaryExpression()
         */

        String arg1 = n.f0.accept(this, argu);
        String arg2 = n.f2.accept(this, argu);
        u.println(u.getReg()+" = add i32 "+arg1+", "+arg2);
        return u.getLastReg();
    }
    public String visit(MinusExpression n, LLVMRedux argu) throws Exception {
        /*
         * f0 -> PrimaryExpression()
         * f1 -> "-"
         * f2 -> PrimaryExpression()
         */

        String arg1 = n.f0.accept(this, argu);
        String arg2 = n.f2.accept(this, argu);
        u.println(u.getReg()+" = sub i32 "+arg1+", "+arg2);
        return u.getLastReg();
    }
    public String visit(TimesExpression n, LLVMRedux argu) throws Exception {
        /*
         * f0 -> PrimaryExpression()
         * f1 -> "*"
         * f2 -> PrimaryExpression()
         */

        String arg1 = n.f0.accept(this, argu);
        String arg2 = n.f2.accept(this, argu);
        u.println(u.getReg()+" = sub i32 "+arg1+", "+arg2);
        return u.getLastReg();
    }
    public String visit(IntegerArrayAllocationExpression n, LLVMRedux argu) throws Exception {
        /*
         * f0 -> "new"
         * f1 -> "int"
         * f2 -> "["
         * f3 -> Expression()
         * f4 -> "]"
         */
        String tempReg;
        String[] array = u.getNSZTags();
        String exprReg = u.getArg(n.f3.accept(this, argu));
        String alocSize = u.getReg();
        u.allocationSegment(alocSize,exprReg,array,1);
        tempReg=u.getReg();
        u.println(tempReg+" = call i8* @calloc(i32 "+alocSize+", i32 4)");
        u.println(u.getReg("int[]")+" = bitcast i8* "+tempReg+" to i32*");
        u.println("store i32 "+exprReg+", i32* "+u.getLastReg());


        return u.getLastReg();
    }
    public String visit(BooleanArrayAllocationExpression n, LLVMRedux argu) throws Exception {
        /*
         * f0 -> "new"
         * f1 -> "boolean"
         * f2 -> "["
         * f3 -> Expression()
         * f4 -> "]"
         */

        String tempReg;
        String[] array = u.getNSZTags();
        String exprReg = u.getArg(n.f3.accept(this, argu));
        String alocSize = u.getReg();
        u.allocationSegment(alocSize,exprReg,array,4);
        tempReg=u.getReg();
        u.println(tempReg+" = call i8* @calloc(i32 "+alocSize+", i32 "+1+")");
        u.println(u.getReg("boolean*")+" = bitcast i8* "+tempReg+" to i32*");
        u.println("store i32 "+exprReg+", i32* "+u.getLastReg());


        return u.getLastReg();
    }
    public String visit(AllocationExpression n, LLVMRedux argu) throws Exception {
        /*
         * f0 -> "new"
         * f1 -> Identifier()
         * f2 -> "("
         * f3 -> ")"
         */

        String ID = n.f1.accept(this, argu), reg1 = u.getReg(ID), reg2;
        LLVMClass Class = argu.findClass(ID);
        u.println(reg1+" = call i8* @calloc(i32 1, i32 "+Class.getSize()+")");
        reg2 = u.getReg();
        u.println(reg2+" = bitcast i8* "+reg1+" to i8***");
        u.println(u.getReg()+ "  = getelementptr "+Class.getLLVMVtableFormat()+", "+Class.getLLVMVtableFormat()+"* @."+Class.ID+"_vtable, i32 0, i32 0");
        u.println("store i8** "+u.getLastReg()+", i8*** "+reg2);

        return reg1;
    }


    public String visit(ArrayLookup n, LLVMRedux argu) throws Exception {
        /*
         * f0 -> PrimaryExpression()
         * f1 -> "["
         * f2 -> PrimaryExpression()
         * f3 -> "]"
         */

        String final_pointer = u.arrayLookup(n.f0.accept(this, argu),n.f2.accept(this, argu));
        u.println(u.getReg()+" = load "+u.dereference(u.javaTypeToLLVMType(u.getRegType(final_pointer)))+", "+u.javaTypeToLLVMType(u.getRegType(final_pointer))+" "+final_pointer);//todo get type
        return u.getLastReg();
    }
    public String visit(ArrayLength n, LLVMRedux argu) throws Exception {
        /*
         * f0 -> PrimaryExpression()
         * f1 -> "."
         * f2 -> "length"
         */

        String reg = n.f0.accept(this, argu);
        return u.getLengthSegment(reg, u.getRegType(reg));
    }
    public String visit(MessageSend n, LLVMRedux argu) throws Exception {
        /*
         * f0 -> PrimaryExpression()
         * f1 -> "."
         * f2 -> Identifier()
         * f3 -> "("
         * f4 -> ( ExpressionList() )?
         * f5 -> ")"
         */
        int i=0;
        String primex_res = n.f0.accept(this, argu);
        Function f = scopeSearch(u.getRegType(primex_res)).getFunction(n.f2.accept(this, argu));

        ArrayList<String> localArgList;
        //bitcast
        String bitcast = u.getReg();
        u.println(bitcast+" = bitcast i8* "+primex_res+" to i8***");
        //load bitcasted to new reg (vtable)
        String load = u.getReg();
        u.println(load+" = load i8**, i8*** "+bitcast);
        //getelemntptr from above reg and the functoffset
        String gepr = u.getReg();
        u.println(gepr+" = getelementptr i8*, i8** "+load+", i32 "+f.offset);
        //get actual funct ptr load new ptr i8* above ptr
        String loader2 = u.getReg();
        u.println(loader2+" = load i8*, i8** "+gepr);
        //String functId = ;
        String bitcast2 = u.getReg();
        //if has only one argument copy paste command below without comma and closed parentheses
        if(f.arguments.size()==0){
            u.println(bitcast2+" = bitcast i8* "+loader2+" to "+u.javaTypeToLLVMType(f.returnType)+"(i8*)");
        }else{
            u.print(bitcast2+" = bitcast i8* "+loader2+" to "+u.javaTypeToLLVMType(f.returnType)+"(i8*, ");
            //else c&c c&v comm above & loop
            for (Variable v: f.arguments){
                u.simpleInlinePrint(u.javaTypeToLLVMType(v.type));
                if (!(i==f.arguments.size()-1))u.simpleInlinePrint(", ");
                i++;
            }
            u.simpleInlinePrint(")*");
        }

        argStack.add(new ArrayList<>());
        argStackIndex++;
        n.f4.accept(this, argu);
        String rvalue= u.getReg(f.returnType);

        u.print(rvalue+" = call "+u.javaTypeToLLVMType(f.returnType)+" "+bitcast2+"(i8* "+primex_res+", ");
        localArgList = argStack.get(argStackIndex);
        for (i=0;i<f.arguments.size();i++){
            u.simpleInlinePrint(u.javaTypeToLLVMType(f.arguments.get(i).type)+" "+localArgList.get(i));
            if (!(i==f.arguments.size()-1))u.simpleInlinePrint(", ");
        }
        u.simplePrint(")");

        argStackIndex--;
        argStack.remove(argStack.size()-1);
        return rvalue;
    }
    public String visit(ExpressionList n, LLVMRedux argu) throws Exception {
        /*
         * f0 -> Expression()
         * f1 -> ExpressionTail()
         */
        argStack.get(argStackIndex).add(n.f0.accept(this, argu));
        n.f1.accept(this, argu);
        return null;
    }
    public String visit(ExpressionTerm n, LLVMRedux argu) throws Exception {
        /*
         * f0 -> ","
         * f1 -> Expression()
         */
        argStack.get(argStackIndex).add(n.f1.accept(this, argu));
        return null;
    }
    public String visit(NotExpression n, LLVMRedux argu) throws Exception {
        /*
         * f0 -> "!"
         * f1 -> Clause()
         */
        String reg =u.getReg();
        u.println(reg+" = xor i1 1, "+n.f1.accept(this, argu));
        return reg;
    }


    public String visit(Type n, LLVMRedux argu) throws Exception {
        String type = n.f0.accept(this, argu);
        //if(!argu.primaryTypes.contains(type))
        return (argu.primaryTypes.contains(type))?type:"i8*";

    }
    public String visit(Identifier n, LLVMRedux argu) {
        return n.f0.toString();
    }
    public String visit(BooleanArrayType n, LLVMRedux argu) {
        /*
         * f0 -> "boolean"
         * f1 -> "["
         * f2 -> "]"
         */
        return "i8*";
    }
    public String visit(IntegerArrayType n, LLVMRedux argu) {
        /*
         * f0 -> "int"
         * f1 -> "["
         * f2 -> "]"
         */
        return "i32*";
    }
    public String visit(BooleanType n, LLVMRedux argu) {
        /*
         * f0 -> "boolean"
         */
        return "i1";
    }
    public String visit(IntegerType n, LLVMRedux argu) {
        /*
         * f0 -> "int"
         */
        return "i32";
    }
    public String visit(IntegerLiteral n, LLVMRedux argu) throws Exception {
        /*
         * f0 -> <INTEGER_LITERAL>
         */
        return n.f0.toString();
    }
    public String visit(TrueLiteral n, LLVMRedux argu) throws Exception {
        /*
         * f0 -> "true"
         */
        return "true";
    }
    public String visit(FalseLiteral n, LLVMRedux argu) throws Exception {
        /*
         * f0 -> "false"
         */
        return "false";
    }
    public String visit(ThisExpression n, LLVMRedux argu) throws Exception {
        /*
         * f0 -> "this"
         */
        return "this";
    }
    public String visit(BracketExpression n, LLVMRedux argu) throws Exception {
        /*
         * f0 -> "("
         * f1 -> Expression()
         * f2 -> ")"
         */


        return n.f1.accept(this,argu);
    }

}
