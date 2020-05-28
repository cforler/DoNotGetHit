package dngh;

public class BigAsteroid extends BoardObject {

    public BigAsteroid(int x, int y) {
        super(x,y,"dngh/images/big_asteroid.png", Board.Tile.STONE);
    }

    int tileX() { return super.x / Board.SQUARE_LEN; }
    int tileY() { return super.y / Board.SQUARE_LEN; }
    
}
