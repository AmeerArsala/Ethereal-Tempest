/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package etherealtempest;

/**
 *
 * @author night
 */
public class GameProtocols {
    private static Runnable OpenPostActionMenu = () -> {};
    
    public static void setOpenPostActionMenu(Runnable open) {
        OpenPostActionMenu = open;
    }
    
    public static void OpenPostActionMenu() {
        OpenPostActionMenu.run();
    }
}
