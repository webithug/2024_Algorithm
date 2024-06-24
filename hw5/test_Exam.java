import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.Arrays;
import java.util.List;

import com.google.gson.*;

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
