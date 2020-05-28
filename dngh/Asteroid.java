package dngh;

public class Asteroid extends BoardObject {

    public Asteroid(int x, int y) {
        super(x,y,"dngh/images/asteroid.png", Board.Tile.STONE);
    }


    int tileX() { return super.x / Board.SQUARE_LEN; }
    int tileY() { return super.y / Board.SQUARE_LEN; }
    
}
