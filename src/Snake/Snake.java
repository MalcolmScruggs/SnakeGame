package Snake;

/**
 * Created by mgscr on 5/18/2017.
 */

import java.util.ArrayList;
import java.util.List;

/**
 * Represents a snake.
 */
public class Snake {
  private int x;
  private int y;
  private Direction direction;
  private ArrayList<BodyCell> tail;
  private ArrayList<BodyCell> toAdd;


  public Snake(int x, int y) {
    this.setX(x);
    this.setY(y);
    this.direction = Direction.Right;
    this.tail = new ArrayList<BodyCell>();
    this.toAdd = new ArrayList<BodyCell>();
  }

  /**
   * Updates the direction of this snake according to a string input
   * Only responds to the following chars:
   * - "w"
   * - "a"
   * - "s"
   * - "d"
   *
   * @param ke - the string that determine the change of direction
   */
  public void moveSnake(char ke) {
      if (ke == ('w')) {
        this.direction = Direction.Up;
      }
      else if (ke == 'd') {
        this.direction = Direction.Right;
      }
      else if (ke == 's') {
        this.direction = Direction.Down;
      }
      else if (ke == 'a') {
        this.direction = Direction.Left;
      }
  }


  /**
   * Moves the snake according to one tick of time having passed.
   * Movement is determine by the local value this.direction.
   *
   * @throws RuntimeException if direction is not one the expected values
   */
  private void moveSnake() {
    int oldx = x;
    int oldy = y;
    if (direction == Direction.Up) {
      y--;
    }
    else if (direction == Direction.Right) {
      x++;
    }
    else if (direction == Direction.Down) {
      y++;
    }
    else if (direction == Direction.Left) {
      x--;
    }
    else {
      throw new RuntimeException("direction is illegal");
    }

    if (tail.size() > 0) {
      tail.remove(tail.size() - 1);
      tail.add(0, new BodyCell(oldx, oldy));
    }

  }


  /**
   *    * Updates the snake based of one tick of time having passed.
   * This consists of:
   *  - eating food snake is touching
   *  - adding any eaten foods onto the tail of the snake if at tail
   *  - moving the snake
   *  - determining if snake has hit itself
   *
   * @param loFood a list of all the food that the snake can e
   * @param width width of the grid snake is in
   * @param height height of grid snake is in
   * @param doWallsKill true if walls kill, false if they don't
   * @return true if game snake is alive, false if dead
   */
  public boolean handleOnTick(List<Food> loFood, int width, int height, boolean doWallsKill) {
    boolean snakeIsAlive;

    foodCollision(loFood, width, height);

    // handles adding on cells that have been eaten
    processAddedBodyCells();

    // moves the snake
    this.moveSnake();

    snakeIsAlive = handleBoundries(width, height, doWallsKill);

    if (snakeIsAlive) {
      snakeIsAlive = snakeCollision();
    }

    return snakeIsAlive;
  }

  /**
   * Adds already eatten body cells to the tail if they are not colliding with the head of the tail.
   */
  private void processAddedBodyCells() {
    for (int i = 0; i < toAdd.size(); i++) {
      BodyCell bcAdd = toAdd.get(i);
      boolean validAddition = true;
      // check collision with head
      if (bcAdd.getX() == this.x && bcAdd.getY() == this.y) {
        validAddition = false;
      }

      //check no collision with tail
      if (validAddition) {
        for (BodyCell bcTail : tail) {
          if (bcAdd.getX() == bcTail.getX() && bcAdd.getX() == bcTail.getY()) {
            validAddition = false;
          }
        }
      }

      if (validAddition) {
        this.tail.add(bcAdd);
        toAdd.remove(i);
      }
    }
  }

  /**
   * Checks if the snake has hit food.
   * If it has creates another food and adds a Snake.BodyCell to the snake
   * @param loFood list of food in snake game
   * @param width width of grid
   * @param height height of grid
   */
  private void foodCollision(List<Food> loFood, int width, int height) {
    for (int i = 0; i < loFood.size(); i++) {
      Food f = loFood.get(i);
      if (f.getX() == x && f.getY() == y) {
        loFood.remove(i);
        loFood.add(new Food(this, width, height));
        addBodyCell(new BodyCell(f.getX(), f.getY()));
      }
    }
  }

  /**
   * Determines if the snake has hit itself
   *
   * @return true if collision, false otherwise
   */
  private boolean snakeCollision() {
    for (BodyCell bodyCell : tail) {
      if (bodyCell.getX() == x && bodyCell.getY() == y) {
        return false;
      }
    }
    return true;
  }

  /**
   * Checks the snakes boundry behavior. If walls kill and snake hits wall it dies. Otherwise it is
   * reset to continue moving from the wall opposite to the one it hit.
   *
   * @param width width of grid
   * @param height height of grid
   * @param doWallsKill setting of whether the snake should die of wall hit
   * @return true if snake lives, false if it dies
   */
  private boolean handleBoundries(int width, int height, boolean doWallsKill) {
    if (doWallsKill) {
      return !(x >= width || x < 0 || y >= height || y < 0);
    }
    else {
      if (x >= width) {
        x = 0;
      }
      else if (x < 0){
        x = width - 1;
      }
      else if (y >= height) {
        y = 0;
      }
      else if (y < 0) {
        y = height - 1;
      }
      return true;
    }
  }




  /**
   * Takes the body cell and stores it until it is appropriate to add it to
   * the snake
   * @param bc - the Snake.BodyCell to be added onto the snake
   */
  private void addBodyCell(BodyCell bc) {
    this.toAdd.add(bc);
  }

  public void setX(int x) {
    this.x = x;
  }

  public void setY(int y) {
    this.y = y;
  }

  public int getX() {
    return this.x;
  }

  public int getY() {
    return this.y;
  }

  public ArrayList<BodyCell> getTail() {
    return tail;
  }
}
