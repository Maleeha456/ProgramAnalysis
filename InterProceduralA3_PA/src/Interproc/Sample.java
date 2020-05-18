package Interproc;
/*Assignment 4 Test Cases

Part 1

Test Case 1:
*/
public class Sample
{
    public static int func (int x, int y)
    {
      int temp = x;
      x = y;
      y = temp;
      return (x + temp + y);
    }
  
    public static int foo (int play)
    {
      int beta = 0;
      int alpha = beta + func(1,play);
      int gamma = 3;
      if(alpha == 2)
      { 
        return gamma;
      } 
      else
      { 
        gamma = beta + func(gamma,beta);
      }
      return gamma;
    }
  
    public static void main (String[] args)
    {
      int play = Integer.parseInt(args[0]);
      int x = foo(play);
      System.out.println(x);
    }
}

