class Factorial{
    public static void main(String[] a){
        Fac b;
        b = new Fac1();
        System.out.println(new Fac().ComputeFac(10));

    }
}

class Fac {
    boolean s;
    public int ComputeFac(int num){
        int num_aux ;

        if (num < 1)
            num_aux = 1 ;
        else
            num_aux = num * (this.ComputeFac(num-1)) ;
        return num_aux ;
    }
    public int Else(boolean b){
        return 0;
    }
}
class Fac1 extends Fac {
    int s;
    public int Else(int a){
        return true;
    }
    public int ComputeFac(int num){
        int num_aux ;

        if (num < 1)
            num_aux = 1 ;
        else
            num_aux = num * (this.ComputeFac(num-1)) ;
        s = this.Else();
        return num_aux ;
    }
}