
import java.util.ArrayList;
import java.util.List;

import Snake.BodyCell;
import Snake.Snake;
import Snake.Food;

import processing.core.*;

public class SnakeGame extends PApplet {

  public static void main(String args[]) {
    PApplet.main("SnakeGame");
  }

  @Override
  public void settings() {
    size(600, 600);
  }

  //Variables for snake
  private Snake snek;
  private ArrayList<Food> loFood;

  // Ints
  private static int frameCount;
  private static int WIDTH;
  private static int HEIGHT;
  private static final int CELL_SIZE = 20;
  private static final int HEADER_TEXT_SIZE = CELL_SIZE * 2;

  // Booleans
  private static boolean WALLS_KILL = false;
  private static boolean gameIsActive = false;
  private static boolean snakeIsAlive = true;
  private static boolean showHeatMap = false;
  private static boolean showDeadSnakes = false;
  private static boolean showGUI = true;

  // Colors
  private final int SNAKE_COLOR = color(255);
  private final int SNAKE_PSHAPE_COLOR_DEFAULT = SNAKE_COLOR;
  private final int FONT_COLOR = color(255);
  private final int BACKGROUND_COLOR = color(0);


  //HeatMap array
  private int[][] timesVisited;
  //List of crated PShapes (last idx is most recent)
  private List<PShape> deadSnakeShapes;
  // gui object
  private final SnakeGUI gui = new SnakeGUI();


  @Override
  public void setup() {
    clear();

    WIDTH = width / CELL_SIZE;
    HEIGHT = height / CELL_SIZE;
    timesVisited = new int[WIDTH][HEIGHT];
    deadSnakeShapes = new ArrayList<>();

    resetGameValues();

  }

  /**
   * Set the values for a new game of snake.
   */
  public void startGame() {
    resetGameValues();
    showGUI = false;
  }

  /**
   * Reset values for a new game that continues after method call.
   */
  private void resetGameValues() {
    gameIsActive = true;
    snakeIsAlive = true;
    snek = new Snake(WIDTH / 2, HEIGHT / 2);
    loFood = new ArrayList<Food>();
    loFood.add(new Food(snek, WIDTH, HEIGHT));
  }

  @Override
  public void draw() {
    if (showGUI) {
      gui.render();
      return;
    }

    // This will pause the game when the user clicks off window
    if (!focused) {
      gameIsActive = false;
    }

    frameCount++;
    //only move and render the snake every 5 frames
    if (gameIsActive && snakeIsAlive && frameCount % 5 == 0) {
      snakeIsAlive = snek.handleOnTick(loFood, WIDTH, HEIGHT, WALLS_KILL);
      if (!snakeIsAlive) {
        createSnakePShape();
      }
      updateHeatMap();
      renderSnake();
    }
    // show death / pause screen if snake is dead
    else if (!gameIsActive || !snakeIsAlive) {
      renderGameNotActive();
    }

    if (showHeatMap) {
      renderHeatMap();
    }

    if (showDeadSnakes) {
      renderDeadSnakes(true);
    }
  }

  /**
   * Renders the snake and food. Draws a background.
   */
  private void renderSnake() {
    background(BACKGROUND_COLOR);
    noStroke();
    fill(SNAKE_COLOR);
    rect(snek.getX() * CELL_SIZE, snek.getY() * CELL_SIZE, CELL_SIZE, CELL_SIZE);
    List<BodyCell> tail = snek.getTail();
    for (BodyCell bodyCell : tail) {
      rect(bodyCell.getX() * CELL_SIZE, bodyCell.getY() * CELL_SIZE, CELL_SIZE, CELL_SIZE);
    }
    for (Food food : loFood) {
      rect(food.getX() * CELL_SIZE, food.getY() * CELL_SIZE, CELL_SIZE, CELL_SIZE);
    }
  }

  /**
   * Adds the current location of the snake to the heatmap representation.
   */
  private void updateHeatMap() {
    if (snek.getX() < timesVisited.length && snek.getY() < timesVisited[0].length
            && snek.getX() >= 0 && snek.getY() >= 0) {
      timesVisited[snek.getX()][snek.getY()]++;
    }
  }

  /**
   * Creates the death / pause game. Shows option to reset and unpause if game was paused, but only
   * the reset option if snake has died.
   * Does not draw background.
   */
  private void renderGameNotActive() {
    // No background reset so previous view will show
    textAlign(CENTER);
    textSize(CELL_SIZE);
    fill(FONT_COLOR);
    text("Press R to restart", width / 2, height /2);
    if (snakeIsAlive) {
      text("Press P to unpause", width / 2, height / 2 + CELL_SIZE);
    }
  }

  /**
   * Renders the heatmap using the colors from HeatMatColorUtils. Draws a background.
   */
  private void renderHeatMap() {
    background(BACKGROUND_COLOR);
    HeatMapColorUtils heatMapColorUtils = new HeatMapColorUtils(timesVisited);

    for (int row = 0; row < timesVisited.length; row++) {
      for (int cell = 0; cell < timesVisited[row].length; cell++) {
        fill(heatMapColorUtils.getColor(row, cell));
        rect(row * CELL_SIZE, cell * CELL_SIZE, CELL_SIZE, CELL_SIZE);
      }
    }
  }

  /**
   * Renders the dead snake PShapes. If onlyMostRecent is true it will only render the more recent
   * snake to have died. If false it will render all dead snakes.
   * Renders nothing if no snakes have died yet. Does not draw background.
   *
   * @param onlyMostRecent true to draw one, false to draw all
   */
  private void renderDeadSnakes(boolean onlyMostRecent) {
    fill(SNAKE_PSHAPE_COLOR_DEFAULT);
    if (onlyMostRecent) {
      if (deadSnakeShapes.size() > 0) {
        shape(deadSnakeShapes.get(deadSnakeShapes.size() - 1));
      }
    }
    else {
      for (PShape pShape : deadSnakeShapes) {
        shape(pShape);
      }
    }
  }

  /**
   * Creates and stores a PShape for a snake in the deadSnakesShapes list. PShape is greated as
   * a group with each cell of the snake being its own square PShape.
   */
  private void createSnakePShape() {
    PShape wholeSnake = createShape(GROUP);
    wholeSnake.beginShape();
    wholeSnake.noStroke();
    wholeSnake.fill(SNAKE_PSHAPE_COLOR_DEFAULT);

    PShape head = createShape(RECT, snek.getX() * CELL_SIZE, snek.getY() * CELL_SIZE,
            CELL_SIZE, CELL_SIZE);
    wholeSnake.addChild(head);

    for (BodyCell bodyCell : snek.getTail()) {
      PShape cellPShape = createShape(RECT, bodyCell.getX() * CELL_SIZE, bodyCell.getY() * CELL_SIZE, CELL_SIZE, CELL_SIZE);
      wholeSnake.addChild(cellPShape);
    }
    deadSnakeShapes.add(wholeSnake);
  }

  @Override
  public void keyPressed() {
    checkGameKeys(key);
  }

  @Override
  public void mousePressed() {
    if (showGUI) {
      gui.mousePressed();
    }
  }

  /**
   * Changes state based off the input key. Actions are as follows:
   *
   <table>
   <caption>Actions by key</caption>
     <tr>
       <th>Key</th>
       <th>action</th>
     </tr>
     <tr>
       <td>r or R</td>
       <td>resets the snake. Game stays active</td>
     </tr>
     <tr>
       <td>p or P</td>
       <td>flips state of game pause</td>
     </tr>
     <tr>
       <td>h or H</td>
       <td>toggle showing of heatmap</td>
     </tr>
     <tr>
       <td>j or J</td>
       <td>toggle showing of dead snakes</td>
     </tr>
     <tr>
       <td>g or G</td>
       <td>switch to displaying GUI start</td>
     </tr>
     <tr>
       <td>any</td>
       <td>sends key to snake's move</td>
     </tr>
   </table>

   * @param key the character to be processed
   */
  private void checkGameKeys(char key) {
    switch (key) {
      case 'r' :
      case 'R' :
        resetGameValues();
        break;
      case 'p' :
      case 'P' :
        gameIsActive = !gameIsActive;
        break;
      case 'h' :
      case 'H' :
        showHeatMap = !showHeatMap;
        break;
      case 'j' :
      case 'J' :
        showDeadSnakes = !showDeadSnakes;
        break;
      case 'g' :
      case 'G' :
        resetGameValues();
        gameIsActive = false;
        showGUI = true;
        gui.render();
        break;
      case 'C' :
      case 'c' :
        timesVisited = new int[timesVisited.length][timesVisited[0].length];
        break;
      default:
        snek.moveSnake(Character.toLowerCase(key));
    }
  }

  /**
   * Class to generate the colors for a heatmap. Only supports 2d arrays of ints.
   */
  public class HeatMapColorUtils {
    public final int RED = color(186, 31, 31);
    public final int ORANGE = color(232, 134, 30);
    public final int YELLOW = color(198, 206, 51);
    public final int GREEN = color(45, 173, 48);
    public final int TEAL = color(51, 206, 193);
    public final int LIGHT_BLUE = color(48, 162, 255);
    public final int BLUE = color(51, 48, 255);

    public double max;
    public double min;
    public int[][]arr;

    /**
     * Stores the array and figures out min and max of arr.
     * @param arr the array to base colors off of
     */
    HeatMapColorUtils(int[][] arr) {
      this.arr = arr;
      setMinMax();
    }

    /**
     * Sets the vaues of min and max based of this.arr.
     */
    private void setMinMax() {
      int min = Integer.MAX_VALUE;
      int max = Integer.MIN_VALUE;
      for (int[] row : arr) {
        for (int i : row) {
          min = Math.min(i, min);
          max = Math.max(i, max);
        }
      }
      this.min = min * 1.0;
      this.max = max * 1.0;
    }

    /**
     * Determines the color of the cell in the given row. Rows and cells are zero index and correspord
     * to indices in this.arr.
     *
     * @param row index of the row
     * @param cell index of the cell
     * @return an int representing an RGB color.
     * @throws IllegalArgumentException if row or cell is not a valid index
     */
    public int getColor(int row, int cell) {
      if (row < 0 || row >= arr.length || cell < 0 || cell>= arr[0].length) {
        throw new IllegalArgumentException("Give valid index");
      }

      int num = arr[row][cell];
      double normalizedVal = (num - min) / (max - min);

      if (normalizedVal > .8)  {
        return RED;
      }
      else if (normalizedVal > .6) {
        return ORANGE;
      }
      else if (normalizedVal > .4) {
        return YELLOW;
      }
      else if (normalizedVal > .3) {
        return GREEN;
      }
      else if (normalizedVal > .2) {
        return TEAL;
      }
      else if (normalizedVal > .1) {
        return LIGHT_BLUE;
      }
      else if (normalizedVal > 0) {
        return BLUE;
      }
      else {
        return BACKGROUND_COLOR;
      }
    }
  }

  /**
   * Class used to create the GUI for snake game
   */
  public class  SnakeGUI {
    private final int buttonColor = color(255);
    private final int buttonHighlight = color (150);

    private static final String SNAKE_TITLE_TEXT = "SNAKE";

    //instruction button
    public static final String INSTRUCTIONS_TEXT = "KEYS: \n w a s d to move \n r to reset " +
            "\n p to pause \n h to show heatmap \n j to show dead snakes \n g to show gui \n" +
            " c to clear heatmap";
    private static final String INTRUCTIONS_BUTTON_TEXT = "Instructions";
    private static final int instrX = 15;
    private static final int instrY = 15;
    private static final int instrW = 200;
    private static final int instrH = 60;
    private Button instructionsButton = new Button(instrX, instrY, instrW, instrH, buttonColor, buttonHighlight, INTRUCTIONS_BUTTON_TEXT);

    //start game button
    private static final String START_GAME_BUTTON_TEXT = "Start Game";
    private static final int gameX = 225;
    private static final int gameY = 500;
    private static final int gameW = 150;
    private static final int gameH = 75;
    private Button startGameButton = new Button(gameX, gameY, gameW, gameH, buttonColor, buttonHighlight, START_GAME_BUTTON_TEXT);

    //walls kill button
    private static final String WALLS_KILL_YES = "yes";
    private static final String WALLS_KILL_NO = "no";
    private static final String WALLS_KILL_BUTTON_TEXT = "Walls kill: ";
    private static final int wallsX = gameX + gameW + 20;
    private static final int wallsY = gameY;
    private static final int wallsW = gameW + 20;
    private static final int wallsH = gameH;
    private Button wallsKillButton = new Button(wallsX, wallsY, wallsW, wallsH, buttonColor, buttonHighlight, WALLS_KILL_BUTTON_TEXT);


    /**
     * Renders the gui, drawing a background. It draws the following elements:
     * <br> - a heatmap over the background
     * <br> - a title text block
     * <br> - a instruction button that toggles to display the keys
     * <br> - a button for displaying whether walls kill, toggling the state of walls kill
     * <br> - a button to start the game
     * <br>
     *  Also triggers the buttons to change color if they are being hovered.
     */
    public void render() {
      update();
      background(BACKGROUND_COLOR);
      renderHeatMap();

      fill(FONT_COLOR);
      textSize(HEADER_TEXT_SIZE);
      textAlign(CENTER);
      text(SNAKE_TITLE_TEXT, width / 2, height / 2);

      instructionsButton.render();
      startGameButton.render();
      wallsKillButton.render();

      if (instructionsButton.clicked()) {
        fill(FONT_COLOR);
        textSize(CELL_SIZE);
        textAlign(LEFT);
        text(INSTRUCTIONS_TEXT, instrX, instrY + instrH + CELL_SIZE);
      }

      if (WALLS_KILL) {
        fill(FONT_COLOR);
        textSize(CELL_SIZE);
        textAlign(CENTER);
        text(WALLS_KILL_YES, wallsX + 145, wallsY + wallsH / 2);
      }
      else {
        fill(FONT_COLOR);
        textSize(CELL_SIZE);
        textAlign(CENTER);
        text(WALLS_KILL_NO, wallsX + 145, wallsY + wallsH / 2);
      }
    }

    /**
     * Calls the update method for every button. Most often used to handle hovering effect of button.
     */
    private void update() {
      instructionsButton.update();
      startGameButton.update();
      wallsKillButton.update();
    }

    /**
     * Handles mouse presses and checks if button have been clicked. Initiates following actions:
     * <br> - checks instructionsButton
     * <br> - checks startGameButton and starts game if it is clicked
     * <br> - checks wallsKillButton and updates walls setting.
     */
    void mousePressed() {
      instructionsButton.mousePressed();

      startGameButton.mousePressed();
      if (startGameButton.clicked()) {
        startGame();
        startGameButton.reset();
      }

      wallsKillButton.mousePressed();
      if (wallsKillButton.clicked()) {
        WALLS_KILL = !WALLS_KILL;
        wallsKillButton.reset();
      }
    }
  }

  /**
   * Represents a rectangle button
   */
  public class Button {
    private int x;
    private int y;
    private int width;
    private int height;
    private int buttonColor;
    private int buttonHighlight;
    private boolean buttonOver;
    private boolean hasBeenClicked;
    private String text;


    /**
     * Creates a button object.
     *
     * @param x x cord of button
     * @param y y cord of button
     * @param width width of the button
     * @param height height of the button
     * @param buttonColor outline color of button
     * @param buttonHighlight color to display when button is hovered
     * @param text text to display inside button
     */
    public Button(int x, int y, int width, int height, int buttonColor, int buttonHighlight, String text) {
      this.x = x;
      this.y = y;
      this.width = width;
      this.height = height;
      this.buttonColor = buttonColor;
      this.buttonHighlight = buttonHighlight;
      this.buttonOver = false;
      this.hasBeenClicked = false;
      this.text = text;
    }

    /**
     * Renders the button. Color is changed if button is being hovered by mouse.
     */
    public void render() {
      if (buttonOver) {
        stroke(buttonHighlight);
      }
      else {
        stroke(buttonColor);
      }

      fill(BACKGROUND_COLOR);
      rect(x, y, width, height);


      textSize(CELL_SIZE);
      fill(buttonColor);
      noStroke();
      textAlign(CENTER);
      text(text, x + width / 2, y + height / 2);
    }

    /**
     * Sets the boolean state for being hovered.
     */
    public void update() {
      if (overButton()) {
        buttonOver = true;
      }
      else {
        buttonOver = false;
      }
    }

    /**
     * Toggles the state of being clicked if the button has been clicked. All buttons are configured
     * as a toggle button ie. starts off, turns on with click, turns back off on second click. If
     * button should not behave as a toggle, the button must be reset after being pressed.
     */
    public void mousePressed() {
      if (buttonOver) {
        hasBeenClicked = !hasBeenClicked;
      }
    }

    /**
     * Determines if the mouse if over the button.
     *
     * @return true if mouse is over button, false otherwise
     */
    private boolean overButton()  {
      if (mouseX >= x && mouseX <= x+width &&
              mouseY >= y && mouseY <= y+height) {
        return true;
      } else {
        return false;
      }
    }

    /**
     * The state of the button having being clicked. All buttons are configured
     * as a toggle button ie. starts off, turns on with click, turns back off on second click. If
     * button should not behave as a toggle, the button must be reset after being pressed.
     *
     * @return true if button is in clicked state, false if in unclicked state
     */
    public boolean clicked() {
      return hasBeenClicked;
    }

    /**
     * Resets the button to its starting/default state.
     */
    public void reset() {
      buttonOver = false;
      hasBeenClicked = false;
    }
  }
}