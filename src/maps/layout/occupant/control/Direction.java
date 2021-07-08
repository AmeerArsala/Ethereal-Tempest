/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package maps.layout.occupant.control;

/**
 *
 * @author night
 */
public enum Direction {
    Up("move up"),
    Down("move down"),
    Left("move left"),
    Right("move right");
    
    private Direction conflicter;
    private final String correspondingInput;
        
    private Direction(String input) {
        correspondingInput = input;
    }
        
    private void setConflicter(Direction conflict) {
        conflicter = conflict;
    }
        
    static {
        Up.setConflicter(Down);
        Down.setConflicter(Up);
            
        Left.setConflicter(Right);
        Right.setConflicter(Left);
    }
        
    public Direction getConflicter() {
        return conflicter;
    }
        
    public String getCorrespondingInput() {
        return correspondingInput;
    }
        
    public static Direction getDirection(String input) {
        Direction[] vals = Direction.values();
        for (Direction dir : vals) {
            if (dir.getCorrespondingInput().equals(input)) {
                return dir;
            }
        }
            
        return null;
    }
        
    public static String getConflicter(String input) {
        return getDirection(input).getConflicter().getCorrespondingInput();
    }
}