class Factorial{
    public static void main(String[] a){
		int sth;

		boolean sthelse;
    	System.out.println(new Fac().ComputeFac(10));

    }
}
class Fac2 extends Fac1 {

	public int ComputeFac(int num){

		int num_aux ;
		if (num < 1)
			num_aux = 1 ;
		else
			num_aux = num * (this.ComputeFac(num-1)) ;
		return num_aux ;
	}


}
class Fac {
	int here;

    public int ComputeFac(int num){
		int num_aux ;
		if (num < 1)
			num_aux = 1 ;
		else
			num_aux = num * (this.ComputeFac(num-1)) ;
		return num_aux ;
    }
    public int Else(){
		return 0;
	}

}
class Fac1 extends Fac {
	boolean here;

	public int ComputerFac(int num){
		int num_aux ;
		if (num < 1)
			num_aux = 1 ;
		else
			num_aux = num * (this.ComputeFac(num-1)) ;
		return num_aux ;
	}

}
