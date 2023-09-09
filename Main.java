import java.util.concurrent.Executors;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;
public class Main {
    private int[][] sudokuPuzzle;
    private boolean isValid;
    public Main(int[][] sudokuPuzzle) {
        this.sudokuPuzzle = sudokuPuzzle;
        this.isValid = true;
    }
    public boolean isValid() {
        return isValid;
    }
    public void validate() {
        ExecutorService executorService = Executors.newFixedThreadPool(11);
        //This will validate the columns
        for (int column = 0; column < 9; column++) {
            executorService.execute(new ValidateForColumns(column));
        }
        //This will validate the rows
        for (int row = 0; row < 9; row++) {
            executorService.execute(new ValidateForRows(row));
        }
        //This will validate the subPuzzles
        for (int startForRow = 0; startForRow < 9; startForRow += 3) {
            for (int startForColumn = 0; startForColumn < 9; startForColumn += 3) {
                executorService.execute(new ValidateForSubPuzzle(startForRow, startForColumn));
            }
        }
        executorService.shutdown();
        try {
            executorService.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        // This will check if any validation for columns, subPuzzle, or rows fails and if it does then the solution will be not valid
        isValid = !ValidateForColumns.validationFailed &&
                  !ValidateForSubPuzzle.validationFailed &&
                  !ValidateForRows.validationFailed;
        
        if (isValid) {
            System.out.println("The solution to the Sudoku puzzle is valid.");
        } else {
            System.out.println("The solution to the Sudoku puzzle is not valid.");
        }
    }
    //This is responsible for validating a column in the puzzle
    private class ValidateForColumns implements Runnable {
        private int column;
        private static volatile boolean validationFailed = false;
        public ValidateForColumns(int column) {
            this.column = column;
        }
        @Override
        public void run() {
            boolean[] visited = new boolean[9];
            for (int row = 0; row < 9; row++) {
                int number = sudokuPuzzle[row][column];
                if (number < 1 || number > 9 || visited[number - 1]) {
                    validationFailed = true;
                    return;
                }
                visited[number - 1] = true;
            }
        }
    }
  //This is responsible for validating a row in the puzzle
    private class ValidateForRows implements Runnable {
        private int row;
        private static volatile boolean validationFailed = false;
        public ValidateForRows(int row) {
            this.row = row;
        }
        @Override
        public void run() {
            boolean[] visited = new boolean[9];
            for (int column = 0; column < 9; column++) {
                int number = sudokuPuzzle[row][column];
                if (number < 1 || number > 9 || visited[number - 1]) {
                    validationFailed = true;
                    return;
                }
                visited[number - 1] = true;
            }
        }
    }
  //This is responsible for validating a subPuzzle
    private class ValidateForSubPuzzle implements Runnable {
        private int startForRow;
        private int startForColumn;
        private static volatile boolean validationFailed = false;
        public ValidateForSubPuzzle(int startForRow, int startForColumn) {
            this.startForRow = startForRow;
            this.startForColumn = startForColumn;
        }
        @Override
        public void run() {
            boolean[] visited = new boolean[9];
            for (int row = startForRow; row < startForRow + 3; row++) {
                for (int column = startForColumn; column < startForColumn + 3; column++) {
                    int number = sudokuPuzzle[row][column];
                    if (number < 1 || number > 9 || visited[number - 1]) {
                        validationFailed = true;
                        return;
                    }
                    visited[number - 1] = true;
                }
            }
        }
    }
    public static void main(String[] args) {
        int[][] sudokuPuzzle = {
            {6, 2, 4, 5, 3, 9, 1, 8, 7},
            {5, 1, 9, 7, 2, 8, 6, 3, 4},
            {8, 3, 7, 6, 1, 4, 2, 9, 5},
            {1, 4, 3, 8, 6, 5, 7, 2, 9},
            {9, 5, 8, 2, 4, 7, 3, 6, 1},
            {7, 6, 2, 3, 9, 1, 4, 5, 8},
            {3, 7, 1, 9, 5, 6, 8, 4, 2},
            {4, 9, 6, 1, 8, 2, 5, 7, 3},
            {2, 8, 5, 4, 7, 3, 9, 1, 6}
        };
        Main sudokuValidator = new Main(sudokuPuzzle);
        sudokuValidator.validate();
    }
}




