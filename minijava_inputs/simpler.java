class Factorial{
    public static void main(String[] a){
        int sth;
        boolean sthelse;
        System.out.println(new Fac().ComputeFac(10));

    }
}

class Fac2 extends Fac1 {

    public int ComputeFac(int num){
        return 0;
    }

}

class Fac {
    int here;

    public int ComputeFac(int num){
        int num_aux=0 ;
        return num_aux ;
    }
    public int ComputeFac(boolean num){
        int num_aux=0 ;
        return num_aux ;
    }
    public int Else(){
        return 0;
    }

}

class Fac1 extends Fac {
    boolean here;
    String[] there;
    public int ComputerFac(int num){
        int num_aux =0;

        return num_aux ;
    }

}