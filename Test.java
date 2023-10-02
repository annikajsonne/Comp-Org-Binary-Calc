
public class Test {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		 BitField b = new BitField("1001"); //1010
		 BitField a = new BitField("0010"); //0010
		 BitField expected = new BitField("0101"); //0101
		 BitField[] p = BinaryCalculator.divide(a, b);
		 
		 
		 System.out.println(p[0] + " q");
		 System.out.println(p[1] + " r");
		 
	}

}
