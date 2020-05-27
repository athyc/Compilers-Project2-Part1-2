public class Variable {
    String name, type;
    int offset=0;
    String InheritedFrom;

    public Variable() {
        type = null;
        name = null;
        InheritedFrom=null;
    }
    public Variable(String name ,String type) {
        this.type = type;
        this.name = name;
        InheritedFrom=null;
    }
}
