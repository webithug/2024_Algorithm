import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.Arrays;

import com.google.gson.*;

class OutputFormat{
    int[] defence;
    int[] attack;
    int k;
    int answer;
}

class test_RPG{
    public static void main(String[] args)
    {
        Gson gson = new Gson();
        OutputFormat[] datas;
        int num_ac = 0;
        int user_ans;
        OutputFormat data;

        // check if input is not empty
        if (args.length == 0) {
            System.out.println("no input");
            return;
        }

        try {
            datas = gson.fromJson(new FileReader(args[0]), OutputFormat[].class);
            for(int i = 0; i<datas.length;++i)
            {
                data = datas[i];
                user_ans = new RPG(data.defence, data.attack).maxDamage(data.k);
                System.out.print("Sample"+i+": ");
                if(data.answer == user_ans)
                {
                    System.out.println("AC");
                    num_ac++;
                }
                else
                {
                    System.out.println("WA");
                    System.out.println("Data_atk:  " + Arrays.toString(data.attack));
                    System.out.println("Data_dfc:  " + Arrays.toString(data.defence));
                    System.out.println("Test_ans:  " + data.answer);
                    System.out.println("User_ans:  " + user_ans);
                    System.out.println("");
                }
            }
            System.out.println("Score: "+num_ac+"/"+datas.length);
        } catch (JsonSyntaxException e) {
            e.printStackTrace();
        } catch (JsonIOException e) {
            e.printStackTrace();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }
}

// paste your own RPG class here :)
class RPG {
    private int[] defence;
    private int[] attack;
    private int[] damage;

    public RPG(int[] defence, int[] attack) {
        this.defence = defence;
        this.attack = attack;
        
    }


    // public int maxDamage(int k) {

    //     this.damage = new int[k];
        
    //     // create the damage array
    //     for (int i = 0; i < k; i++) {
    //         damage[i] = attack[i] - defence[i];
    //     }
    //     // System.err.println( Arrays.toString(damage) );

    //     // if only one round
    //     if (damage.length == 1){
    //         return damage[0];
    //     }

    //     for (int boost_number = 0; boost_number <= k/2; boost_number++){

    //     // find the biggest number in damage[] (skip the first element)
    //     int maxElement_index = -1;
    //     int maxElement = 0;
        
    //     for ( int i = 1; i < damage.length; i++ ){

    //         // if it's boosted or its last element is boosted, we don't want to consider it anymore
    //         if( (damage[i-1] == 0 || ( i>=2 && damage[i-2] == 0) )){
    //             continue;
    //         }
    //         if (damage[i] > maxElement) {
    //             maxElement_index = i;
    //             maxElement = damage[i];
    //         }
    //         // System.out.println("hi ");
    //     }

    //     // update the damage array with boost (skip if it's been boosted already)
    //     if( (maxElement_index < 0)   ){
    //         continue;
    //     }
    //     damage[maxElement_index-1] = 0;
    //     damage[maxElement_index] = 2*maxElement + defence[maxElement_index];
    //     // System.out.println( Arrays.toString(damage) );
    //     }

    //     // accumulate the sum of damage[]
    //     int damage_sum = 0;
    //     for (int element : damage) {
    //         damage_sum += element;
    //     }

    //     return damage_sum; 
    // }

    public int maxDamage(int k) {

        this.damage = new int[k];
        
        // create the damage array
        for (int i = 0; i < k; i++) {
            damage[i] = attack[i] - defence[i];
        }
        int[] dp = new int[damage.length];

        // Initialize dp array with the first element of the input array
        dp[0] = damage[0];

        for (int i = 1; i < damage.length; i++) {
            // Calculate the score for attacking the current element
            int attackScore = dp[i - 1] + damage[i];

            // Calculate the score for boosting the current element and skipping the next
            int boostScore = (i >= 2 ? dp[i - 2] : 0) + damage[i] * 2 + defence[i];

            // Choose the maximum score between attacking and boosting
            dp[i] = Math.max(attackScore, boostScore);
        }

        // Return the maximum score after considering the first 'k' elements
        return dp[Math.min(k, damage.length) - 1];
    }


}

// class MaxSumCalculator {

//     public static int maxSum(int[] array, int n) {
//         int[] dp = new int[array.length];

//         // Initialize dp array with the first element of the input array
//         dp[0] = array[0];

//         for (int i = 1; i < array.length; i++) {
//             // Calculate the score for attacking the current element
//             int attackScore = dp[i - 1] + array[i];

//             // Calculate the score for boosting the current element and skipping the next
//             int boostScore = (i >= 2 ? dp[i - 2] : 0) + array[i] * 2;

//             // Choose the maximum score between attacking and boosting
//             dp[i] = Math.max(attackScore, boostScore);
//         }

//         // Return the maximum score after considering the first 'n' elements
//         return dp[Math.min(n, array.length) - 1];
//     }

//     public static void main(String[] args) {
//         // Example usage
//         int[] array = {1, 20, 3, 4, 5};
//         int n = 4;

//         int result = maxSum(array, n);
//         System.out.println("Maximum Sum: " + result);
//     }
// }



// public class App {
//     public static void main(String[] args) throws Exception {
//         System.out.println("Hello, World!");
//     }
// }
