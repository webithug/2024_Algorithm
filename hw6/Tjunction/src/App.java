import java.util.Arrays;

class RoadStatus {
    // Number of cars on each road
    private int[] carsCount;
    // Current time
    private int currentTime;
    // The index of the road with green light
    private int currentGreen;
    // The duration of the green light
    private int greenTimer;

    RoadStatus() {
        // Initially there is no car on each road.
        carsCount = new int[]{0, 0, 0};
        // Initial time is 0.
        currentTime = 0;
        // Traffic lights are all red in the beginning.
        currentGreen = -1;
        // The duration will be zero initially.
        greenTimer = 0;
    }

    public int[] roadStatus(int time) {
        // If input time is equal to currentTime, return carsCount
        if (time == currentTime) {
            return carsCount;
        }

        // If time > currentTime, calculate how many cars exit (not in place)
        int[] tempCarsCount = Arrays.copyOf(carsCount, carsCount.length);
        int tempCurrentGreen = currentGreen;
        int tempGreenTimer = greenTimer;

        for (int i = currentTime + 1; i <= time; i++) {
            if (tempGreenTimer == 0) {
                // Equivalent to updateLights()
                int maxCars = -1;
                int selectedRoad = -1;

                for (int j = 0; j < tempCarsCount.length; j++) {
                    if (tempCarsCount[j] > maxCars) {
                        maxCars = tempCarsCount[j];
                        selectedRoad = j;
                    }
                }

                if (maxCars > 0) {
                    tempCurrentGreen = selectedRoad;
                    tempGreenTimer = tempCarsCount[selectedRoad];
                } else {
                    tempCurrentGreen = -1;
                }
            }

            if (tempGreenTimer > 0 && tempCurrentGreen != -1) {
                tempCarsCount[tempCurrentGreen] = Math.max(0, tempCarsCount[tempCurrentGreen] - 1);
                tempGreenTimer -= 1;
            }
        }

        return tempCarsCount;
    }

    public void addCar(int time, int id, int num_of_cars) {
        // If time doesn't elapse, cars will be directly added to the road.
        if (time == currentTime) {
            if (greenTimer == 0) {
                updateLights();
            }
            carsCount[id] += num_of_cars;
        }

        // If time elapses, cars will exit, lights will be updated, and time will be replaced. Finally, new cars will be added.
        if (time > currentTime) {
            // Cars exit
            for (int i = currentTime + 1; i <= time; i++) {
                if (greenTimer == 0) {
                    updateLights();
                }

                if (greenTimer > 0 && currentGreen != -1) {
                    carsCount[currentGreen] = Math.max(0, carsCount[currentGreen] - 1);
                    greenTimer -= 1;
                }
            }

            // Time elapses
            currentTime = time;

            // Cars are added
            carsCount[id] += num_of_cars;
        }
    }

    private void updateLights() {
        int maxCars = -1;
        int selectedRoad = -1;

        // Find the road occupied by the most cars
        for (int i = 0; i < carsCount.length; i++) {
            if (carsCount[i] > maxCars) {
                maxCars = carsCount[i];
                selectedRoad = i;
            }
        }

        // Update lights
        if (maxCars > 0) {
            currentGreen = selectedRoad;
            greenTimer = carsCount[selectedRoad]; // Duration of the green light
        } else {
            currentGreen = -1; // No cars on any road
        }
    }

    public static void main(String[] args) {
        // Example 1
        System.out.println("Example 1: ");
        RoadStatus sol1 = new RoadStatus(); // create a T-junction; all traffic lights are Red at the beginning
        sol1.addCar(0, 0, 2);
        System.out.println("0: " + Arrays.toString(sol1.roadStatus(0)));
        sol1.addCar(0, 1, 3);
        System.out.println("0: " + Arrays.toString(sol1.roadStatus(0)));
        System.out.println("1: " + Arrays.toString(sol1.roadStatus(1)));
        sol1.addCar(2, 0, 4);
        for (int i = 2; i < 12; ++i)
            System.out.println(i + ": " + Arrays.toString(sol1.roadStatus(i)));

        // Example 2
        RoadStatus sol2 = new RoadStatus(); // create a T-junction; all traffic lights are Red at the beginning
        System.out.println("Example 2: ");
        sol2.addCar(0, 0, 2);
        System.out.println("0: " + Arrays.toString(sol2.roadStatus(0)));
        sol2.addCar(0, 0, 1);
        System.out.println("0: " + Arrays.toString(sol2.roadStatus(0)));
        System.out.println("1: " + Arrays.toString(sol2.roadStatus(1)));
        sol2.addCar(2, 1, 2);
        for (int i = 2; i < 7; ++i)
            System.out.println(i + ": " + Arrays.toString(sol2.roadStatus(i)));

        // Example 3
        RoadStatus sol3 = new RoadStatus(); // create a T-junction; all traffic lights are Red at the beginning
        System.out.println("Example 3: ");
        sol3.addCar(0, 0, 1);
        System.out.println("0: " + Arrays.toString(sol3.roadStatus(0)));
        System.out.println("1: " + Arrays.toString(sol3.roadStatus(1)));
        System.out.println("2: " + Arrays.toString(sol3.roadStatus(2)));
        sol3.addCar(3, 1, 1);
        System.out.println("3: " + Arrays.toString(sol3.roadStatus(3)));
        sol3.addCar(3, 1, 1);
        System.out.println("3: " + Arrays.toString(sol3.roadStatus(3)));
        sol3.addCar(4, 0, 2);
        for (int i = 4; i < 10; i++) {
            System.out.println(i + ": " + Arrays.toString(sol3.roadStatus(i)));
        }
    }
}



// import java.util.ArrayList;
// import java.util.Arrays;
// import java.io.FileNotFoundException;
// import java.io.FileReader;
// import java.util.*;

// import com.google.gson.*;

// class RoadStatus {
//     ArrayList<int[]> StatusQueue = new ArrayList<>();
//     int[] greenlight_memo = {-1, 0};
//     int add_car_time = -1 ;
//     int add_car_count = 0;

//     public RoadStatus() {
//         completeStatusQueue(0, 0, false);
//     }

//     public int[] roadStatus(int time) {
//         int final_time_of_queue = StatusQueue.size() - 1;
//         completeStatusQueue(final_time_of_queue, time, false);
//         return Arrays.copyOfRange(StatusQueue.get(time), 0, 3);
//     }

//     public void addCar(int time, int id, int num_of_cars) {
//         int final_time_of_queue = StatusQueue.size() - 1;
//         completeStatusQueue(final_time_of_queue, time, true);
//         StatusQueue.get(time)[id] += num_of_cars;
//         // StatusQueue.get(time)[5] += 1;

//         if (time == add_car_time){
//             add_car_count ++;
//         }
//         else{ // add car firs time
//             add_car_time = time;
//             add_car_count = 1;
//         }
        

//     }

//     private void completeStatusQueue(int start_time, int end_time, boolean isAddCar) {
//         int original_SQ_size = StatusQueue.size();
//         if (original_SQ_size == 0) {
//             StatusQueue.add(new int[]{0, 0, 0, -1, 0});
//         }

//         int[] last_status_array = StatusQueue.get(start_time);

//         for (int current_time = start_time + 1; current_time <= end_time; current_time++) {
//             int[] new_status_array = Arrays.copyOf(last_status_array, last_status_array.length);
//             // new_status_array[5] = 0;

//             int[] green_decision = decide_green_road(last_status_array, current_time - 1);
//             int green_road = green_decision[0];
//             int green_duration = green_decision[1];

//             if (green_road >= 0) {
//                 new_status_array[green_road] -= 1;
//             }
//             new_status_array[3] = green_road;
//             new_status_array[4] = green_duration;

//             if (current_time >= original_SQ_size) {
//                 StatusQueue.add(new_status_array);
//             } else {
//                 StatusQueue.set(current_time, new_status_array);
//             }

//             last_status_array = new_status_array;
//         }

//         if (isAddCar) {
//             greenlight_memo = decide_green_road( StatusQueue.get(end_time), end_time );
//         }

//         // for (int[] array : StatusQueue) {
//         //     System.out.println(Arrays.toString(array));
//         // }
//         // System.out.println("end");
//     }

//     private int[] decide_green_road(int[] last_status, int last_time) {
//         int decision = -1;
//         int duration = 0;

//         if (last_status[4] > 0) {
//             decision = last_status[3];
//             duration = last_status[4] - 1;
//         } 
//         // else if ((last_status[5] == 0) || (last_status[5] == 1)) {
//         //     decision = max_car_road(last_status);

//         //     if (decision >= 0) {
//         //         duration = last_status[decision] - 1;
//         //     } else {
//         //         duration = 0;
//         //     }

//         //     greenlight_memo[0] = decision;
//         //     greenlight_memo[1] = duration;
//         // } 
        
//         else if (add_car_count>1 && add_car_time==last_time) {
//             decision = greenlight_memo[0];
//             duration = greenlight_memo[1];
//         }
//         else {
//             decision = max_car_road(last_status);

//             if (decision >= 0) {
//                 duration = last_status[decision] - 1;
//             } else {
//                 duration = 0;
//             }

//             greenlight_memo[0] = decision;
//             greenlight_memo[1] = duration;
//         }

//         return new int[]{decision, duration};
//     }

//     private int max_car_road(int[] array) {
//         int maxIndex = 0;
//         int maxElement = array[0];

//         for (int i = 1; i < 3; i++) {
//             if (array[i] > maxElement) {
//                 maxElement = array[i];
//                 maxIndex = i;
//             }
//         }

//         if (maxElement == 0) {
//             maxIndex = -1;
//         }

//         return maxIndex;
//     }

//     public static void main(String[] args)
//     {
//         // // Example 1
//         // System.out.println("Example 1: ");
//         // RoadStatus sol1 = new RoadStatus(); // create a T-junction; all traffic lights are Red at the beginning
//         // sol1.addCar(6, 1, 5);
//         // for (int i = 2; i < 10; ++i)
//         //     System.out.println(i + ": " + Arrays.toString(sol1.roadStatus(i)));
       
//         // sol1.addCar(9, 0, 8);
//         // // System.out.println("0: " + Arrays.toString(sol1.roadStatus(0)));
//         // sol1.addCar(10, 0, 5);
//         // // System.out.println("0: " + Arrays.toString(sol1.roadStatus(0)));
//         // // System.out.println("1: " + Arrays.toString(sol1.roadStatus(1)));
//         // sol1.addCar(10, 1, 2);
//         // for (int i = 2; i < 30; ++i)
//         //     System.out.println(i + ": " + Arrays.toString(sol1.roadStatus(i)));
//         //______________________________________________________________________
//         // Example 2
//         RoadStatus sol2 = new RoadStatus(); // create a T-junction; all traffic lights are Red at the beginning
//         System.out.println("Example 2: ");
//         sol2.addCar(0, 0, 2);
//         System.out.println("0: " + Arrays.toString(sol2.roadStatus(0)));
//         sol2.addCar(0, 0, 1);
//         System.out.println("0: " + Arrays.toString(sol2.roadStatus(0)));
//         System.out.println("1: " + Arrays.toString(sol2.roadStatus(1)));
//         sol2.addCar(2, 1, 2);
//         for (int i = 2; i < 7; ++i)
//             System.out.println(i + ": " + Arrays.toString(sol2.roadStatus(i)));
//         //______________________________________________________________________
//         // Example 3
//         RoadStatus sol3 = new RoadStatus(); // create a T-junction; all traffic lights are Red at the beginning
//         System.out.println("Example 3: ");
//         sol3.addCar(0, 0, 1);
//         System.out.println("0: " + Arrays.toString(sol3.roadStatus(0)));
//         System.out.println("1: " + Arrays.toString(sol3.roadStatus(1)));
//         System.out.println("2: " + Arrays.toString(sol3.roadStatus(2)));
//         sol3.addCar(3, 1, 1);
//         System.out.println("3: " + Arrays.toString(sol3.roadStatus(3))); 
//         sol3.addCar(3, 1, 1);
//         System.out.println("3: " + Arrays.toString(sol3.roadStatus(3)));
//         sol3.addCar(4, 0, 2);
//         for (int i = 4; i < 10; i++) {
//             System.out.println(i + ": " + Arrays.toString(sol3.roadStatus(i)));
//         }
//         // check below for full output explaination
//     }
// }




// // 看 PQ 怎用
// // 看上課影片
// import java.io.FileNotFoundException;
// import java.io.FileReader;
// import java.util.*;

// import com.google.gson.*;

// // import edu.princeton.cs.algs4.In;

// class RoadStatus
// {
//     // index 0,1,2: number of cars at each road at the begining of the second.
//     // index 3: which road is green light at the begining of the second (上一秒決定的).
//     // index 4: duration left for this green light -> if > 1, 下一秒還是 選一樣的 green road
//     // index 5: 這秒內 做過幾次 add_car()
//     // always has the last array as [0,0,0, x, 0]
//     ArrayList<int[]> StatusQueue = new ArrayList<>();
//     int[] greenlight_memo = {-1,0};
    
    
//     public RoadStatus()
//     {
//         completeStatusQueue(0, 0, false);   
        
//     }

//     public int[] roadStatus(int time)
//     {
//         int final_time_of_queue = StatusQueue.size() - 1;
//         completeStatusQueue(final_time_of_queue, time, false );
//         return Arrays.copyOfRange(StatusQueue.get(time), 0, 3);
 
//     }

//     public void addCar(int time, int id, int num_of_cars)
//     {
//         //add a car to the queue of a specific id.

//         int final_time_of_queue = StatusQueue.size() - 1;

//         completeStatusQueue(final_time_of_queue, time, true);

//         // car number increase
//         StatusQueue.get(time)[id] += num_of_cars;
        
//         // add_car count ++ 
//         StatusQueue.get(time)[5] += 1;



        

            
        
//     }

//     // update the status queue 
//     private void completeStatusQueue(int start_time, int end_time, boolean isAddCar)
//     {
//         // System.out.println(" complete called " + start_time +" ," + end_time );
//         int original_SQ_size = StatusQueue.size();

//         // if empty, initiall a statusqueue
//         if (original_SQ_size == 0){
//             StatusQueue.add(new int[]{0,0,0, -1, 0, 0});
//         }


//         // get the first status_array
//         int[] last_status_array = StatusQueue.get(start_time);

//         int[] green_decision;
//         int green_road;
//         int green_duration;
//         int last_time;

//         // build the StatusQueue unti endtime
//         for ( int current_time = start_time+1 ; current_time <= end_time; current_time ++ ){

//             last_time = current_time - 1;

//             int [] new_status_array = Arrays.copyOf(last_status_array, last_status_array.length);
//             new_status_array[5] = 0;

//             // use last_status_array to decide the gree road for new_status_array
//             // System.out.println("last status " + Arrays.toString(last_status_array));
//             green_decision = decide_green_road(last_status_array, last_time );
//             green_road = green_decision[0];
//             green_duration = green_decision[1];
//             // System.out.println(" decision " + green_road );

//             // update the new_status_array according to green_decision
//             if (green_road >= 0){
//                 // System.out.println("road" + green_road);
//                 new_status_array[green_road] -= 1;
//             }
//             new_status_array[3] = green_road;
//             new_status_array[4] = green_duration;

//             if (current_time >= original_SQ_size){ // 超過原長度 用 arraylist add
//                 StatusQueue.add(new_status_array);
//                 // System.out.println(" add " );
//             }
//             else{ // 在原長內 用蓋掉的
//                 StatusQueue.set(current_time, new_status_array);
//                 // System.out.println(" set " );
//             }

//             last_status_array = new_status_array;

//         }

//         if (isAddCar){ // if it's addcar, save the greenlight decision
//             greenlight_memo = decide_green_road( StatusQueue.get(end_time), end_time );
//         }

//         // for (int[] array : StatusQueue) {
//         //     System.out.println(Arrays.toString(array));
//         // }
//         // System.out.println("end");


        
//     }

//     // decide the green road index for 下秒初
//     private int[] decide_green_road(int[] last_status, int last_time){
        
//         // if 目前的 green light duration 還有剩 就繼續
//         // if add_car_count == 0 or 1: 找最多車的
//         // if add_car_count >= 2: 照原本的 (next_status[3])

//         int decision = -1;
//         int duration = 0;

//         if (last_status[4] > 0){ // duration 還有 直接一樣 road
//             decision = last_status[3];
//             duration = last_status[4] - 1;
//         }
//         else if ((last_status[5] == 0) || (last_status[5] == 1) ) { // add_car_count = 0, find max 
//             decision = max_car_road(last_status);
            
//             if (decision >= 0){ // duration is number of cars
//                 duration = last_status[ decision ]-1;
//             }
//             else{
//                 duration = 0;
//             }

//             greenlight_memo[0] = decision;
//             greenlight_memo[1] = duration;
//         }
//         else{
//             decision = greenlight_memo[0];
//             duration = greenlight_memo[1];
//         }

//         return new int[]{decision, duration};
//     }

//     private int max_car_road(int[] array) {

//         int maxIndex = 0; // Initialize the index of the maximum element
//         int maxElement = array[0]; // Initialize the maximum element

//         // Iterate through the first three elements of the array
//         for (int i = 1; i < 3; i++) {
//             if (array[i] > maxElement) {
//                 maxElement = array[i];
//                 maxIndex = i;
//             }
//         }
//         // System.out.println(maxIndex);
//         if (maxElement == 0){
//             maxIndex = -1;
//         }

//         return maxIndex;
//     }
    
//     public static void main(String[] args)
//     {
//         // // Example 1
//         // System.out.println("Example 1: ");
//         // RoadStatus sol1 = new RoadStatus(); // create a T-junction; all traffic lights are Red at the beginning
//         // sol1.addCar(6, 1, 5);
//         // for (int i = 2; i < 10; ++i)
//         //     System.out.println(i + ": " + Arrays.toString(sol1.roadStatus(i)));
       
//         // sol1.addCar(9, 0, 8);
//         // // System.out.println("0: " + Arrays.toString(sol1.roadStatus(0)));
//         // sol1.addCar(10, 0, 5);
//         // // System.out.println("0: " + Arrays.toString(sol1.roadStatus(0)));
//         // // System.out.println("1: " + Arrays.toString(sol1.roadStatus(1)));
//         // sol1.addCar(10, 1, 2);
//         // for (int i = 2; i < 30; ++i)
//         //     System.out.println(i + ": " + Arrays.toString(sol1.roadStatus(i)));
//         //______________________________________________________________________
//         // Example 2
//         RoadStatus sol2 = new RoadStatus(); // create a T-junction; all traffic lights are Red at the beginning
//         System.out.println("Example 2: ");
//         sol2.addCar(0, 0, 2);
//         System.out.println("0: " + Arrays.toString(sol2.roadStatus(0)));
//         sol2.addCar(0, 0, 1);
//         System.out.println("0: " + Arrays.toString(sol2.roadStatus(0)));
//         System.out.println("1: " + Arrays.toString(sol2.roadStatus(1)));
//         sol2.addCar(2, 1, 2);
//         for (int i = 2; i < 7; ++i)
//             System.out.println(i + ": " + Arrays.toString(sol2.roadStatus(i)));
//         //______________________________________________________________________
//         // Example 3
//         RoadStatus sol3 = new RoadStatus(); // create a T-junction; all traffic lights are Red at the beginning
//         System.out.println("Example 3: ");
//         sol3.addCar(0, 0, 1);
//         System.out.println("0: " + Arrays.toString(sol3.roadStatus(0)));
//         System.out.println("1: " + Arrays.toString(sol3.roadStatus(1)));
//         System.out.println("2: " + Arrays.toString(sol3.roadStatus(2)));
//         sol3.addCar(3, 1, 1);
//         System.out.println("3: " + Arrays.toString(sol3.roadStatus(3))); 
//         sol3.addCar(3, 1, 1);
//         System.out.println("3: " + Arrays.toString(sol3.roadStatus(3)));
//         sol3.addCar(4, 0, 2);
//         for (int i = 4; i < 10; i++) {
//             System.out.println(i + ": " + Arrays.toString(sol3.roadStatus(i)));
//         }
//         // check below for full output explaination
//     }
// }

// class OutputFormat{
//     int[] answer;
//     String func;
//     int[] args;
// }

// class test{
//     static boolean run_and_check(OutputFormat[] data, RoadStatus roadStat)
//     {
//         for(OutputFormat cmd : data)
//         {
//             if(cmd.func.equals("addCar"))
//             {
//                 roadStat.addCar(cmd.args[0], cmd.args[1], cmd.args[2]);
//             }
//             else if(cmd.func.equals("roadStatus"))
//             {
//                 int[] arr = roadStat.roadStatus(cmd.args[0]);
//                 System.out.println(cmd.args[0] + "my answer: " + Arrays.toString(arr));
//                 System.out.println("correct answer: " + Arrays.toString(cmd.answer));
//                 if(!Arrays.equals(arr,cmd.answer)){
//                     for (int i = 526; i < 550; ++i)
//                         System.out.println(i + ": " + Arrays.toString(roadStat.roadStatus(i)));
//                     return false;
//                 }
                
                    
//             }
//         }
//         return true;
//     }
//     public static void main(String[] args)
//     {
//         Gson gson = new Gson();
//         OutputFormat[][] datas;
//         OutputFormat[] data;
//         int num_ac = 0;

//         try {
//             datas = gson.fromJson(new FileReader(args[0]), OutputFormat[][].class);
//             for(int i = 0; i<datas.length;++i)
//             {
//                 // if (i !=2) continue;

//                 data = datas[i];
                
//                 System.out.print("Sample"+i+": ");
//                 if(run_and_check(data, new RoadStatus()))
//                 {
//                     System.out.println("AC");
//                     num_ac++;
//                 }
//                 else
//                 {
//                     System.out.println("WA");
//                     System.out.println("");
//                 }
//             }
//             System.out.println("Score: "+num_ac+"/"+datas.length);
//         } catch (JsonSyntaxException e) {
//             e.printStackTrace();
//         } catch (JsonIOException e) {
//             e.printStackTrace();
//         } catch (FileNotFoundException e) {
//             e.printStackTrace();
//         }
//     }
// }