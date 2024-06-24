import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.*;

import com.google.gson.*;

class Exam {
    public static List<int[]> getPassedList(Integer[][] scores)
    {
        //input:
        //    scores: int[subject][id] 
        //    eg. scores[0][0] -> subject: 0, ID: 0
        //        scores[1][5] -> subject: 1, ID: 5

        //return:
        //    return a List of {ID, totalScore} 
        //    sorted in descending order of the total score
        
        List<int[]> passedList = new ArrayList<>();
        
        int totalStudents = scores[0].length;
        int numSubjects = scores.length;
        int numTopStudents = (int) Math.ceil(totalStudents * 0.2);
        
        // Iterate over each subject
        for (int subject = 0; subject < numSubjects; subject++) {
            Integer[] subjectScores = scores[subject];
            Integer[] subjectScores_copy = new Integer[subjectScores.length];
            for (int i = 0; i < subjectScores.length; i++) {
                subjectScores_copy[i] = subjectScores[i];
            }
            
            // Use Quick Select to find the score threshold for the top 20% student
            int topScore = quickSelect(subjectScores_copy, numTopStudents - 1);

            // include every top 20% student for subject0
            if (subject == 0){
                // Add the top 20% students' scores to passedList
                for (int id = 0; id < totalStudents; id++) {
                    if (subjectScores[id] >= topScore) {
                        passedList.add(new int[]{id, subjectScores[id]});
                        
                    }
                }
            }
            // only check the students with top 20% in subject0
            else{
                Iterator<int[]> iterator = passedList.iterator();
                while (iterator.hasNext()) {
                    int[] student = iterator.next();
                    int id = student[0];
                    if (subjectScores[id] >= topScore) {
                        student[1] += subjectScores[id];
                    }
                    else {
                        iterator.remove();
                    }
                
                }
            }
            
            
        }
        
        // Sort passedList based on total score in descending order
        insertionSort(passedList);
        
        return passedList;
    }

    // Quick Select algorithm to find the kth largest element
    private static int quickSelect(Integer[] arr, int k) {
        int left = 0;
        int right = arr.length - 1;
        
        while (left <= right) {
            int pivotIndex = partition(arr, left, right);
            if (pivotIndex == k) {
                return arr[pivotIndex];
            } else if (pivotIndex < k) {
                left = pivotIndex + 1;
            } else {
                right = pivotIndex - 1;
            }
        }
        
        return -1; // This should never happen
    }
    
    // Helper function to partition the array
    private static int partition(Integer[] arr, int left, int right) {
        int pivot = arr[right];
        int i = left - 1;
        
        for (int j = left; j < right; j++) {
            if (arr[j] >= pivot) {
                i++;
                swap(arr, i, j);
            }
        }
        
        swap(arr, i + 1, right);
        return i + 1;
    }
    
    // Helper function to swap two elements in an array
    private static void swap(Integer[] arr, int i, int j) {
        int temp = arr[i];
        arr[i] = arr[j];
        arr[j] = temp;
    }

    private static void insertionSort(List<int[]> list) {
        int n = list.size();
        for (int i = 1; i < n; ++i) {
            int[] key = list.get(i);
            int j = i - 1;

            // Move elements of list[0..i-1], that are less than key, to one position ahead
            // of their current position
            while (j >= 0 && list.get(j)[1] < key[1]) {
                list.set(j + 1, list.get(j));
                j = j - 1;
            }
            list.set(j + 1, key);
        }
    }



    public static void main(String[] args) {
        List<int[]> ans = getPassedList(new Integer[][]
            {
                // ID:[0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10]
                {67,82,98,32,65,76,87,12,43,75,25},
                {42,90,80,12,76,58,95,30,67,78,10}
            }
        );
        for(int[] student : ans)
            System.out.print(Arrays.toString(student));
            // 11 students * 0.2 = 2.2 -> Top 3 students 
            // Output -> [6, 182][2, 178][1, 172]
        
        System.out.println(); // For typesetting
        
        ans = getPassedList(new Integer[][]
            {
                // ID:[0, 1, 2, 3, 4, 5]
                {67,82,64,32,65,76},
                {42,90,80,12,76,58}
            }
        );
        for(int[] student : ans)
            System.out.print(Arrays.toString(student));
            // 6 students * 0.2 = 1.2 -> Top 2 students 
            // Output -> [1, 172]
    } 
}

class OutputFormat{
    Integer[][] scores;
    List<int[]> answer;
}

class test_Exam{
    static boolean deepEquals(List<int[]> answer,List<int[]> answer2)
    {
        if(answer.size() != answer2.size())
            return false;
        for(int i = 0; i< answer.size(); ++i)
        {
            int[] a = answer.get(i);
            int[] b = answer2.get(i);
            if(!Arrays.equals(a, b))
            {
                return false;
            }
        }
        return true;
    }
    public static void main(String[] args)
    {
        Gson gson = new Gson();
        OutputFormat[] datas;
        int num_ac = 0;
        List<int[]> user_ans;
        OutputFormat data;

        try {
            datas = gson.fromJson(new FileReader(args[0]), OutputFormat[].class);
            for(int i = 0; i<datas.length;++i)
            {
                data = datas[i];
                user_ans = Exam.getPassedList(data.scores);
                System.out.print("Sample"+i+": ");

                if(deepEquals(user_ans, data.answer))
                {
                    System.out.println("AC");
                    num_ac++;
                }
                else
                {
                    System.out.println("WA");
                    System.out.println("Data:      " + Arrays.deepToString(data.scores));
                    System.out.println("Test_ans:  " + Arrays.deepToString(data.answer.toArray()));
                    System.out.println("User_ans:  " + Arrays.deepToString(user_ans.toArray()));
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
