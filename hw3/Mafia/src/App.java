import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Arrays; // Used to print the arrays

import com.google.gson.*;

class test{
    public test(String[] args){
        Mafia sol = new Mafia();
        Gson gson = new Gson();
        try (FileReader reader = new FileReader(args[0])){
            JsonArray all = gson.fromJson(reader, JsonArray.class);
            for(JsonElement caseInList : all){
                JsonArray a = caseInList.getAsJsonArray();
                int q_cnt = 0, wa = 0,ac = 0;
                for (JsonElement o : a) {
                    q_cnt++;
                    JsonObject person = o.getAsJsonObject();
                    JsonArray arg_lvl = person.getAsJsonArray("level");
                    JsonArray arg_rng = person.getAsJsonArray("range");
                    JsonArray arg_ans = person.getAsJsonArray("answer");
                    int LVL[] = new int[arg_lvl.size()];
                    int RNG[] = new int[arg_lvl.size()];
                    int Answer[] = new int[arg_ans.size()];
                    int Answer_W[] = new int[arg_ans.size()];
                    for(int i=0;i<arg_ans.size();i++){
                        Answer[i]=(arg_ans.get(i).getAsInt());
                        if(i<arg_lvl.size()){
                            LVL[i]=(arg_lvl.get(i).getAsInt());
                            RNG[i]=(arg_rng.get(i).getAsInt());
                        }
                    }
                    Answer_W = sol.result(LVL,RNG);
                    for(int i=0;i<arg_ans.size();i++){
                        if(Answer_W[i]==Answer[i]){
                            if(i==arg_ans.size()-1){
                                System.out.println(q_cnt+": AC");
                            }
                        }else {
                            wa++;
                            System.out.println(q_cnt+": WA");
                            break;
                        }
                    }

                }
                System.out.println("Score: "+(q_cnt-wa)+"/"+q_cnt);

            }
        }catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

class member{
    int Level;
    int Range;
    int Index;
    member(int _level,int _range, int i){
        Level=_level;
        Range=_range;
        Index=i;
    }
}

// class Mafia {
//     public int[] result(int[] levels, int[] ranges) {
//           // Given the traits of each member and output 
//           // the leftmost and rightmost index of member
//           // can be attacked by each member.
          
//           int[] final_output = new int[ 2*levels.length ];
//           // loop over levels
//           for(int i=0;i<levels.length;i++){
//             int a = i;
//             int b = i;
//             //   
//             // Look for left neighbor within the range
//             for (int j = 1; j <= ranges[i]; j++) {
//                 // Look back
//                 if (i - j >= 0 && levels[i - j] < levels[i]) {
//                     a = i - j;
//                 } else {
//                     break;
//                 }
//             }

//             // Look for right neighbor within the range
//             for (int j = 1; j <= ranges[i]; j++) {
//                 // Look forward
//                 if (i + j < levels.length && levels[i + j] < levels[i]) {
//                     b = i + j;
//                 } else {
//                     break;
//                 }
//             }
//                 final_output[2*i] = a;
//                 final_output[2*i+1] = b;
//             }
//         return final_output; 
//         // complete the code by returning an int[]
//         // flatten the results since we only need an 1-dimentional array.
//     }

//     public static void main(String[] args) {
//         Mafia sol = new Mafia();
//         System.out.println(Arrays.toString(
//             sol.result(new int[] {11, 13, 11, 7, 15},
//                          new int[] { 1,  8,  1, 7,  2})));
//         // Output: [0, 0, 0, 3, 2, 3, 3, 3, 2, 4]
//         //      => [a0, b0, a1, b1, a2, b2, a3, b3, a4, b4]
        
//         // int[] real_ans = {0, 0, 0, 3, 2, 3, 3, 3, 2, 4};
//         // System.out.println(Arrays.toString(real_ans));
            
//     }
// }
// public class App {
//     public static void main(String[] args) throws Exception {
//         System.out.println("Hello, World!");
//     }
// }

// use stack
class MyStack {
    private int maxSize;
    private int[] stackArray;
    private int top; // Index of the top element

    public MyStack(int size) {
        maxSize = size;
        stackArray = new int[maxSize];
        top = -1;
    }

    public void push(int value) {
        if (top < maxSize - 1) {
            stackArray[++top] = value;
        } else {
            System.out.println("Stack Overflow");
        }
    }

    public int pop() {
        if (top >= 0) {
            return stackArray[top--];
        } else {
            System.out.println("Stack Underflow");
            return -1; // Return a default value indicating underflow
        }
    }

    public int peek() {
        if (top >= 0) {
            return stackArray[top];
        } else {
            System.out.println("Stack is empty");
            return -1; // Return a default value indicating an empty stack
        }
    }

    public boolean isEmpty() {
        return top == -1;
    }

    public boolean isFull() {
        return top == maxSize - 1;
    }

    public void clear() {
        top = -1;
    }

    public void printStack() {
        for (int i = 0; i <= top; i++) {
            System.out.print(stackArray[i] + " ");
        }
        System.out.println();
    }
}

class Mafia {
    public int[] result(int[] levels, int[] ranges) {
        // Implement with stack
        int n = levels.length;
        int[] nextGreaterLeft = new int[n];
        int[] nextGreaterRight = new int[n];
        int[] attackRanges = new int[2 * n];
        MyStack stack = new MyStack(n);

        // Find next greater element on the left
        Arrays.fill(nextGreaterLeft, -1); // Initialize with -1 (no greater element)
        for (int i = 0; i < n; i++) {
            while (!stack.isEmpty() && levels[stack.peek()] < levels[i]) {
                stack.pop();
            }
            if (!stack.isEmpty()) {
                nextGreaterLeft[i] = stack.peek();
            }
            stack.push(i);
        }

        // Clear the stack for the next operation
        stack.clear();

        // Find next greater element on the right
        Arrays.fill(nextGreaterRight, n); // Initialize with n (no greater element)
        for (int i = n - 1; i >= 0; i--) {
            while (!stack.isEmpty() && levels[stack.peek()] < levels[i]) {
                stack.pop();
            }
            if (!stack.isEmpty()) {
                nextGreaterRight[i] = stack.peek();
            }
            stack.push(i);
        }

        // Calculate attackRanges based on the next greater elements and given ranges
        for (int i = 0; i < n; i++) {
            int minIndex = Math.max(0, i - ranges[i]); // the furthest left index with the given range
            int maxIndex = Math.min(n - 1, i + ranges[i]);

            if (nextGreaterLeft[i] != -1) {
                minIndex = Math.max(minIndex, nextGreaterLeft[i] + 1);
            }
            if (nextGreaterRight[i] != n) {
                maxIndex = Math.min(maxIndex, nextGreaterRight[i] - 1);
            }

            attackRanges[2 * i] = minIndex;
            attackRanges[2 * i + 1] = maxIndex;
        }

        return attackRanges;
    }

    public static void main(String[] args) {
        Mafia sol = new Mafia();
        System.out.println(Arrays.toString(
            sol.result(new int[] {11, 13, 11, 7, 15},
                         new int[] { 1,  8,  1, 7,  2})));
        // Output: [0, 0, 0, 3, 2, 3, 3, 3, 2, 4]
        //      => [a0, b0, a1, b1, a2, b2, a3, b3, a4, b4]
        
        // int[] real_ans = {0, 0, 0, 3, 2, 3, 3, 3, 2, 4};
        // System.out.println(Arrays.toString(real_ans));
            
    }
}


