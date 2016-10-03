package Poker;

/**
 * based on code Retrieved at: 
 * http://algokoder.blogspot.com/2008/09/sum-of-subsets-find-how-many-pairs-in.html
* This will find how many pairs of numbers in the given array sum
* up to the given number.
*
* @param array - array of integers
* @param sum - The sum
* @return int - number of pairs.
*/
import java.util.Arrays;

public class test{
    public static int sumOfSubset(int[] array, int sum)
    {
            // This has a complexity of O ( n lg n )
            Arrays.sort(array);

            int pairCount = 0;
            int leftIndex = 0;
            int rightIndex = array.length - 1;

            // The portion below has a complextiy of
            //  O ( n ) in the worst case.
            while (array[rightIndex] >= sum && rightIndex > 0)
            {
                rightIndex--;    
            }

            while (leftIndex < rightIndex)
            {
                if (array[leftIndex] + array[rightIndex] == sum)
                {
                    pairCount++;
                    leftIndex++;
                    rightIndex--;
                }
                else if(array[leftIndex] + array[rightIndex]  < sum)
                {
                    leftIndex++;
                }
                else
                {
                    rightIndex--;   
                }
            }

            return pairCount;
    }// end sumOfSubset

    public static void main(String[] args){
        int result;
        int[] array1 = {9,7,32,5,9,77,88,101,500,4,8,1,7,13};
        
        for (int i = 1; i < 1000; i++){
            result = sumOfSubset(array1,i);
            String s = String.format("Sum: %d  Count: %d %n", i, result);
            if (result > 0) System.out.print(s);
        }
    }// end main
}// end test.java
