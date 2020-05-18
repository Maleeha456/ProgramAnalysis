package IntervalAnalysis;
import java.util.Scanner;
public class Medium3 {
	
	

	        public static void main(String[] args) {
	          Scanner sc=new Scanner(System.in);
	          int input=sc.nextInt();
	            int y = 0;
	            int x = 7;
	            x = x + 1;
	            while (input > y)
	            {
	                x = 7;
	                x = x + 1;
	                y = y + 1;
	            }

	            if(x>5){
	                y*=-2;
	                x=x-2;
	            }
	            else {
	                x=8;
	                x--;
	                x++;
	            }
	           
	        }
	    }



