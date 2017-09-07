package Snake;

/**
 * Created by mgscr on 5/18/2017.
 */

/**
 * Represents a cell in the game of snake.
 */
public abstract class Cell {
  private int x;
  private int y;


  /**
   * Creates a cell at an x-y on grid
   * @param x x cord
   * @param y y cord
   */
  public Cell(int x, int y) {
    this.setX(x);
    this.setY(y);
  }

  public void setX(int x) {
    this.x = x;
  }

  public void setY(int y) {
    this.y = y;
  }

  public int getX() {
    return x;
  }

  public int getY() {
    return y;
  }
}
