import java.util.Arrays;

public class Variables{
    public static void showPlayingGrid(int[][] playingGrid, int rows, int columns)
    {
        String[] letters = {"a", "b", "c", "d", "e", "f", "g", "h", "i", "j", "k", "l", "m", "n", "o", "p", "q", "r", "s", "t", "u", "v", "w", "x", "y", "z"};

        System.out.print(" ");
        for (int i = 0; i < columns; i++)
        {
            System.out.print(" " + letters[i]);
        }

        int currentX = 1; int currentY = 0;
        for (int i = 0; i < playingGrid.length; i++)
        {
            currentX = playingGrid[i][0];
            if (playingGrid[i][1] != currentY)
            {
                //System.out.println(" |");
                System.out.println(" ");
                System.out.print(letters[playingGrid[i][1] - 1]);
            }
            currentY = playingGrid[i][1];

            //System.out.print(" | ");
            System.out.print(" ");
            if (playingGrid[i][2] == 0)
            {
                System.out.print("#");
            }
            else if (playingGrid[i][2] == 1)
            {
                //System.out.print("!");
                System.out.print("#");
            }
            else if (playingGrid[i][2] == 2)
            {
                int minesInProximity = getMinesInProximity(playingGrid, i);
                if (minesInProximity > 0)
                {
                    System.out.print(minesInProximity);
                }
                else
                {
                    System.out.print(" ");
                }
            }
            else if (playingGrid[i][2] == 3 || playingGrid[i][2] == 4)
            {
                System.out.print("!");
            }
            else
            {

            }
        }
    }

    public static int getMinesInProximity(int[][] playingGrid, int cellIndex)
    {
        int minesInProximity = 0;
        for (int i = 0; i < playingGrid.length; i++)
        {
            if ((playingGrid[i][2] == 1 || playingGrid[i][2] == 4) && playingGrid[i][1] == playingGrid[cellIndex][1] && (playingGrid[i][0] == playingGrid[cellIndex][0] + 1 || playingGrid[i][0] == playingGrid[cellIndex][0] - 1))
            //if cell is a mine and if cell is on same column and if the cell is left or right
            {
                minesInProximity += 1;
            }
            else if((playingGrid[i][2] == 1 || playingGrid[i][2] == 4) && playingGrid[i][0] == playingGrid[cellIndex][0] && (playingGrid[i][1] == playingGrid[cellIndex][1] + 1 || playingGrid[i][1] == playingGrid[cellIndex][1] - 1))
            //if cell is a mine and if cell is on same row and if the cell is above or below
            {
                minesInProximity += 1;
            }
            else if ((playingGrid[i][2] == 1 || playingGrid[i][2] == 4) && (playingGrid[i][0] == playingGrid[cellIndex][0] + 1 && playingGrid[i][1] == playingGrid[cellIndex][1] + 1))
            // if cell is a mine and cell is top right
            {
                minesInProximity += 1;
            }
            else if ((playingGrid[i][2] == 1 || playingGrid[i][2] == 4) && (playingGrid[i][0] == playingGrid[cellIndex][0] - 1 && playingGrid[i][1] == playingGrid[cellIndex][1] + 1))
            // if cell is a mine and cell is top left
            {
                minesInProximity += 1;
            }
            else if ((playingGrid[i][2] == 1 || playingGrid[i][2] == 4) && (playingGrid[i][0] == playingGrid[cellIndex][0] - 1 && playingGrid[i][1] == playingGrid[cellIndex][1] - 1))
            // if cell is a mine and cell is bottom left
            {
                minesInProximity += 1;
            }
            else if ((playingGrid[i][2] == 1 || playingGrid[i][2] == 4) && (playingGrid[i][0] == playingGrid[cellIndex][0] + 1 && playingGrid[i][1] == playingGrid[cellIndex][1] - 1))
            // if cell is a mine and cell is bottom right
            {
                minesInProximity += 1;
            }
            else
            {

            }
        }

        return minesInProximity;
    }

    public static int getRandom(int lowest, int highest){
        return (int)(Math.random()*(highest - lowest + 1) + lowest);
    }

    public static boolean inArray(int[][] numberList, int []coords){
        for (int i = 0; i < numberList.length; i++)
        {
            if (numberList[i][0] == coords[0] && numberList[i][1] == coords[1])
            {
                return true;
            }
        }
        return false;
    }

    public static int[] getActionFromText(String input)//do input check later
    {
        String[] letters = {"a", "b", "c", "d", "e", "f", "g", "h", "i", "j", "k", "l", "m", "n", "o", "p", "q", "r", "s", "t", "u", "v", "w", "x", "y", "z"};
        int selectedX = 0, selectedY = 0, action = 0;
        
        for (int i = 0; i < letters.length; i++)
        {
            if (letters[i].equals(String.valueOf(input.charAt(0))))
            {
                selectedX = i + 1;
            }
            if (letters[i].equals(String.valueOf(input.charAt(1))))
            {
                selectedY = i + 1;
            }
        }

        if (String.valueOf((input.charAt(2))).toLowerCase().equals("o"))
        {
            action = 1;
        }
        else if (String.valueOf((input.charAt(2))).toLowerCase().equals("f"))
        {
            action = 2;
        }
        else
        {

        }

        int[] toReturn = {selectedX, selectedY, action};

        return toReturn;
    } 

    public static int[][] generateMines(int row, int column, int mines)
    {
        int[][] minesCoordinates = {};

        for (int i = 0; i < mines; i++)
        {
            int[] randomMine = {getRandom(1, row), getRandom(1, column)};

            while (inArray(minesCoordinates, randomMine)) //keeps looping until generates one that isnt in the thing already
            {
                randomMine[0] = getRandom(1, row);
                randomMine[1] = getRandom(1, column);
            }

            minesCoordinates = Arrays.copyOf(minesCoordinates, minesCoordinates.length + 1);
            minesCoordinates[minesCoordinates.length - 1] = randomMine;
        }

        return minesCoordinates;
    }

    public static int[][] createPlayingGrid(int rows, int columns, int mines)
    {
        int[][] minesGenerated = generateMines(rows, columns, mines);
        int[][] playingGrid = new int[rows*columns][4];

        //loop through playing grid and set up each cell's value
        int currentX = 1; int currentY = 1;
        for (int i = 0; i < rows*columns; i++)
        {
            currentX = i % columns + 1;
            currentY = i / columns + 1;

            playingGrid[i][0] = currentX; //x coord
            playingGrid[i][1] = currentY; //y coord
            playingGrid[i][2] = 0; //state of the cell 0 - covered nothing 1 - covered mine 2 - uncovered nothing 3 - flagged nothing 4 - flagged mine

            for (int m = 0; m < minesGenerated.length; m++)
            {
                if (minesGenerated[m][0] == currentX && minesGenerated[m][1] == currentY)
                {
                    playingGrid[i][2] = 1;
                }
            }
        }

        return playingGrid;
    }
    

    public static int getCellState(int[][] playingGrid, int cellX, int cellY)
    {
        for (int i = 0; i < playingGrid.length; i++)
        {
            if (playingGrid[i][0] == cellX && playingGrid[i][1] == cellY)
            {
                return playingGrid[i][2];
            }
        }

        return 0;
    }

    public static void modifyCellState(int[][] playingGrid, int cellX, int cellY, int newState)
    {
        for (int i = 0; i < playingGrid.length; i++)
        {
            if (playingGrid[i][0] == cellX && playingGrid[i][1] == cellY)
            {
                playingGrid[i][2] = newState;
                break;
            }
        }
    }

    public static void chainOpen(int[][] playingGrid, int cellX, int cellY)
    {
        for (int i = 0; i < playingGrid.length; i++)
        {
            if (playingGrid[i][0] == cellX && playingGrid[i][1] == cellY)
            {
                modifyCellState(playingGrid, cellX, cellY, 2);
                if (getMinesInProximity(playingGrid, i) == 0)
                {
                    for (int n = 0; n < playingGrid.length; n++)
                    {
                        if (playingGrid[n][2] == 0 && playingGrid[n][1] == cellY && (playingGrid[n][0] == cellX + 1 || playingGrid[n][0] == cellX - 1))
                        //if cell is covered nothing and if cell is on same column and if the cell is left or right
                        {
                            modifyCellState(playingGrid, playingGrid[n][0], playingGrid[n][1], 2);
                            chainOpen(playingGrid, playingGrid[n][0], playingGrid[n][1]);
                        }
                        else if(playingGrid[n][2] == 0 && playingGrid[n][0] == cellX && (playingGrid[n][1] == cellY + 1 || playingGrid[n][1] == cellY - 1))
                        //if cell is covered nothing and if cell is on same row and if the cell is above or below
                        {
                            modifyCellState(playingGrid, playingGrid[n][0], playingGrid[n][1], 2);
                            chainOpen(playingGrid, playingGrid[n][0], playingGrid[n][1]);
                        }
                        else if ((playingGrid[n][2] == 0) && (playingGrid[n][0] == cellX + 1 && playingGrid[n][1] == cellY + 1))
                        // if cell is a covered nothing and cell is top right
                        {
                            modifyCellState(playingGrid, playingGrid[n][0], playingGrid[n][1], 2);
                            chainOpen(playingGrid, playingGrid[n][0], playingGrid[n][1]);
                        }
                        else if ((playingGrid[n][2] == 0) && (playingGrid[n][0] == cellX - 1 && playingGrid[n][1] == cellY + 1))
                        // if cell is a covered nothing and cell is top left
                        {
                            modifyCellState(playingGrid, playingGrid[n][0], playingGrid[n][1], 2);
                            chainOpen(playingGrid, playingGrid[n][0], playingGrid[n][1]);
                        }
                        else if ((playingGrid[n][2] == 0) && (playingGrid[n][0] == cellX - 1 && playingGrid[n][1] == cellY - 1))
                        // if cell is a covered nothing and cell is bottom left
                        {
                            modifyCellState(playingGrid, playingGrid[n][0], playingGrid[n][1], 2);
                            chainOpen(playingGrid, playingGrid[n][0], playingGrid[n][1]);
                        }
                        else if ((playingGrid[n][2] == 0) && (playingGrid[n][0] == cellX + 1 && playingGrid[n][1] == cellY - 1))
                        // if cell is a covered nothing and cell is bottom right
                        {
                            modifyCellState(playingGrid, playingGrid[n][0], playingGrid[n][1], 2);
                            chainOpen(playingGrid, playingGrid[n][0], playingGrid[n][1]);
                        }
                    }
                }
            }
        }
    }
}
