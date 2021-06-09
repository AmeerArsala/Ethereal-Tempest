/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package maps.layout.occupant;

/**
 *
 * @author night
 */
public class GlobalProtocols {
    private static Runnable OpenPostActionMenu = () -> {};
    
    public static void setOpenPostActionMenu(Runnable open) {
        OpenPostActionMenu = open;
    }
    
    public static void OpenPostActionMenu() {
        OpenPostActionMenu.run();
    }
}
