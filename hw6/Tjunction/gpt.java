import java.util.ArrayList;
import java.util.Arrays;

class RoadStatus {
    ArrayList<int[]> StatusQueue = new ArrayList<>();

    public RoadStatus() {
        completeStatusQueue(0);
    }

    public int[] roadStatus(int time) {
        if (time >= StatusQueue.size()) {
            return new int[]{0, 0, 0};
        } else {
            return Arrays.copyOfRange(StatusQueue.get(time), 0, 3);
        }
    }

    public void addCar(int time, int id, int num_of_cars) {
        int final_time_of_queue = StatusQueue.size() - 1;

        if (time <= final_time_of_queue) {
            StatusQueue.get(time)[id] += num_of_cars;
            StatusQueue.get(time)[5] += 1;
            completeStatusQueue(time);
        } else {
            for (int i = final_time_of_queue + 1; i < time; i++) {
                StatusQueue.add(new int[]{0, 0, 0, -1, 0, 1});
            }
            int[] newStatus = {0, 0, 0, -1, 0, 1};
            newStatus[id] += num_of_cars;
            StatusQueue.add(newStatus);
            completeStatusQueue(time);
        }
    }

    private void completeStatusQueue(int start_time) {
        int original_SQ_size = StatusQueue.size();

        if (original_SQ_size == 0) {
            StatusQueue.add(new int[]{0, 0, 0, -1, 0, 0});
        }

        int[] last_status_array = StatusQueue.get(start_time);
        int current_time = start_time;

        while ((last_status_array[0] != 0) || (last_status_array[1] != 0) || (last_status_array[2] != 0)) {
            int[] new_status_array = Arrays.copyOf(last_status_array, last_status_array.length);
            new_status_array[5] = 0;
            current_time += 1;

            int[] green_decision = decide_green_road(last_status_array, current_time - 1);
            int green_road = green_decision[0];
            int green_duration = green_decision[1];

            if (green_road >= 0) {
                new_status_array[green_road] -= 1;
            }
            new_status_array[3] = green_road;
            new_status_array[4] = green_duration;

            if (current_time >= original_SQ_size) {
                StatusQueue.add(new_status_array);
            } else {
                StatusQueue.set(current_time, new_status_array);
            }

            last_status_array = new_status_array;
        }
    }

    private int[] decide_green_road(int[] last_status, int last_time) {
        int decision = -1;
        int duration = 0;

        if (last_status[4] > 0) {
            decision = last_status[3];
            duration = last_status[4] - 1;
        } else if ((last_status[5] == 0) || (last_status[5] == 1)) {
            decision = max_car_road(last_status);
            duration = last_status[decision] - 1;
        } else {
            decision = StatusQueue.get(last_time + 1)[3];
            duration = StatusQueue.get(last_time + 1)[4];
        }

        return new int[]{decision, duration};
    }

    private int max_car_road(int[] array) {
        int maxIndex = 0;
        int maxElement = array[0];

        for (int i = 1; i < 3; i++) {
            if (array[i] > maxElement) {
                maxElement = array[i];
                maxIndex = i;
            }
        }

        return maxIndex;
    }

    public static void main(String[] args) {
        RoadStatus sol = new RoadStatus();
        sol.addCar(0, 0, 2);
        System.out.println("0: " + Arrays.toString(sol.roadStatus(0)));
        sol.addCar(0, 0, 1);
        System.out.println("0: " + Arrays.toString(sol.roadStatus(0)));
        System.out.println("1: " + Arrays.toString(sol.roadStatus(1)));
        sol.addCar(2, 1, 2);
        for (int i = 2; i < 7; i++) {
            System.out.println(i + ": " + Arrays.toString(sol.roadStatus(i)));
        }
    }
}