import java.util.Arrays;


class RoadStatus
{

    // Number of cars on each road
    private int[] carsCount;

    // Current time
    private int currentTime;

    // The index of the road with green light
    private int currentGreen;

    // The duration of the green light
    private int greenTimer;



    RoadStatus()
    {
        // Initially there is no car on each road.
        carsCount = new int[]{0, 0, 0};

        // Initial time is 0.
        currentTime = 0;

        // Traffic lights are all red in the begining.
        currentGreen = -1;

        // The duration will be zero initially.
        greenTimer = 0;
    }



    
    public int[] roadStatus(int time)
    {

        // If input time is equal to currentTime, return carsCount
        if(time == currentTime){
            return carsCount;
        }

        
        // If time > currentTime, calculate how many cars exit (not in place) 
        int[] tempCarsCount = Arrays.copyOf(carsCount, carsCount.length);
        int tempCurrentGreen = currentGreen;
        int tempGreenTimer = greenTimer;

        for (int i = currentTime+1 ; i <= time ; i++){

            if(tempGreenTimer == 0){

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

            if(tempGreenTimer > 0 && tempCurrentGreen != -1){
                tempCarsCount[tempCurrentGreen] = Math.max(0, tempCarsCount[tempCurrentGreen]-1);
                tempGreenTimer -= 1;
            }

            
        }

        return tempCarsCount;
    }



    public void addCar(int time, int id, int num_of_cars)
    {

        // If time doesn't elapse, cars will be directly add to road. 
        if (time == currentTime){

            if(greenTimer == 0){
                updateLights();
            }

            carsCount[id] += num_of_cars;
        }

        // If time elapses, cars will exit, light will be updated, and time will be replaced. Finally new cars will be added.
        if (time > currentTime){

            // Cars exit
            for (int i = currentTime+1 ; i <= time ; i++){

                if(greenTimer == 0){
                    updateLights();
                }


                if(greenTimer > 0 && currentGreen != -1){
                    carsCount[currentGreen] = Math.max(0, carsCount[currentGreen]-1);
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



    public static void main(String[] args)
    {


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
        //______________________________________________________________________


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
        //______________________________________________________________________


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
        // check below for full output explaination
    }
}
