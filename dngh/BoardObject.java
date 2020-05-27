
package dngh;
import java.awt.image.BufferedImage;
import java.awt.Point;
import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import javax.imageio.ImageIO;
import java.security.InvalidParameterException;

    
public class BoardObject {
    int x;
    int y;
    final Board.Tile tile;
    BufferedImage img;


    public BoardObject(int x, int y, String filename, Board.Tile tile) {
        if (Board.isOnBoard(x,y)==false) {
            String msg = "" + this.getClass().getSimpleName() +
                ": Invalid coordinate: " + "(" + x +","+y+")" ;
            throw new InvalidParameterException(msg);
        }
        this.x = x;
        this.y = y;
        this.tile = tile;
        img = null;
        try {
            img = ImageIO.read(new File(filename));
        } catch(IOException e) {  System.err.println(filename + ": " + e);  }
    }


    public int getWidth() {
        return (img.getWidth() + Board.SQUARE_LEN - 1) /Board.SQUARE_LEN ;
    }

    
    public int getHeight() {
        return (img.getHeight() +  Board.SQUARE_LEN - 1) / Board.SQUARE_LEN;
    }

    
    public String toString() {
        StringBuffer sb = new StringBuffer("");
        sb.append(this.getClass().getSimpleName());
        sb.append(": (");
        sb.append(x);
        sb.append(",");
        sb.append(y);
        sb.append(") , Dimension : (");
        sb.append(getWidth());
        sb.append(",");
        sb.append(getHeight());
        sb.append(")");
        return sb.toString();
    } 
}
