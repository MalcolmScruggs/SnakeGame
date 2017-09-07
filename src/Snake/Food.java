package Snake; /**
 * Created by mgscr on 5/18/2017.
 */

import java.util.ArrayList;
import java.util.Random;

/**
 * Represents a piece of food in a game of snake
 */
public class Food extends Cell{

  /**
   * Creates a Snake.Food object on a cell not currently occupied by the given snake
   *
   * @param s the snake which the food will not be placed on top of
   * @param width width of the grid
   * @param height height of the grid
   */
  public Food(Snake s, int width, int height) {
    super(0, 0);
    Random rand = new Random();
    ArrayList<BodyCell> invalidPlacements = s.getTail();
    invalidPlacements.add(new BodyCell(s.getX(), s.getY()));


    int foodX = rand.nextInt(width);
    int foodY = rand.nextInt(height);

    // to prevent an infinite loop
    for (int i = 0; i < invalidPlacements.size(); i++) {
      BodyCell bc = invalidPlacements.get(i);
      if (bc.getX() == foodX && bc.getY() == foodY) {
        foodX = rand.nextInt(width);
        foodY = rand.nextInt(height);
        i = 0;
      }
    }
    this.setX(foodX);
    this.setY(foodY);
  }

}
