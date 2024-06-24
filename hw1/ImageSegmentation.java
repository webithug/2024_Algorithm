class ImageSegmentation {
    
    private int segmentCount;
    private int largestColor;
    private int largestSize;
    private int[] parent;
    private int[] size;
    private int[] color;

    public ImageSegmentation(int N, int[][] inputImage) {
        // Initialize a N-by-N image
        int totalPixels = N * N;

        // create arrays of size as totalPixels
        parent = new int[totalPixels];
        size = new int[totalPixels];
        color = new int[totalPixels];

        // loop over each pixel and set the arrays
        for (int i = 0; i < totalPixels; i++) {
            parent[i] = i;
            size[i] = 1;
            color[i] = inputImage[i / N][i % N];
        }

        // initialize the values
        segmentCount = countNonZeroElements(inputImage);
        largestColor = -1;
        largestSize = 0;
        
        // loop over all pixels and make the unions
        for (int row = 0; row < N ; row++ ){
            for ( int col = 0; col < N; col++ ){
                if (inputImage[row][col] != 0){
                    // check down
                    if ( (row+1 < N) && (inputImage[row][col] == inputImage[row+1][col]) ){
                        union( N*row+col, N*(row+1)+col );
                        // System.out.println("union:" + N*row+col+ ", " + N*(row+1)+col);
                    }

                    // check right
                    if ( col+1 < N && inputImage[row][col] == inputImage[row][col+1] ){
                        union( N*row+col, N*row+(col+1) );
                    }
                }
            }
        }
    }

    // funtion to find the root node of x
    private int find(int x) {
        int root = x;
        while( root != parent[root] ){
            root = parent[root];
        }

        // path compression
        while( x != root ){
            int next = parent[x];
            parent[x] = root;
            x = next;
        }

        return root;

    }

    private void union(int x, int y) {
        int rootX = find(x);
        int rootY = find(y);

        if (rootX != rootY) {
            // Union by size
            if (size[rootX] < size[rootY]) {
                parent[rootX] = rootY;
                size[rootY] += size[rootX];
                updateLargestSize(rootY);
            } else {
                parent[rootY] = rootX;
                size[rootX] += size[rootY];
                updateLargestSize(rootX);
            }
            segmentCount--;
        }
    }

    public void updateLargestSize( int root ){
        if (size[root] > largestSize ) {
            largestSize = size[root];
            largestColor = color[root];
        } else if( (size[root] == largestSize) && (color[root] < largestColor) ){
            largestSize = size[root];
            largestColor = color[root];
        }
        
        // System.out.println(largestColor);
    }

    public int countDistinctSegments() {
        // Count the number of distinct segments in the image.
        return segmentCount;
    }

    public int[] findLargestSegment() {
        // Find the largest connected segment and return an array
        // containing the number of pixels and the color of the segment.
        return new int[]{largestSize, largestColor};
    }

    // private object mergeSegment (object XXX, ...){ 
        // Maybe you can use user-defined function to
        // facilitate you implement mergeSegment method. 
    // }
    public int countNonZeroElements(int[][] matrix) {
        int count = 0;

        for (int[] row : matrix) {
            for (int element : row) {
                if (element != 0) {
                    count++;
                }
            }
        }

        return count;
    }

    

    public static void main(String args[]) {

        // Example 1:
        int[][] inputImage1 = {
            {0, 0, 0},
            {0, 1, 1},
            {0, 0, 1}
        };

        System.out.println("Example 1:");

        ImageSegmentation s = new ImageSegmentation(3, inputImage1);
        System.out.println("Number of Distinct Segments: " + s.countDistinctSegments());

        int[] largest = s.findLargestSegment();
        System.out.println("Size of the Largest Segment: " + largest[0]);
        System.out.println("Color of the Largest Segment: " + largest[1]);


        // Example 2:
        int[][] inputImage2 = {
               {0, 0, 0, 3, 0},
               {0, 2, 3, 3, 0},
               {1, 2, 2, 0, 0},
               {1, 2, 2, 1, 1},
               {0, 0, 1, 1, 1}
        };

        System.out.println("\nExample 2:");

        s = new ImageSegmentation(5, inputImage2);
        System.out.println("Number of Distinct Segments: " + s.countDistinctSegments());

        largest = s.findLargestSegment();
        System.out.println("Size of the Largest Segment: " + largest[0]);
        System.out.println("Color of the Largest Segment: " + largest[1]);

    }

}