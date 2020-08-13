/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package misc;
import java.io.File;
/**
 *
 * @author asus
 */
public class DirFileExplorer {
    private String dir;
    private File directory;
    public DirFileExplorer(String dir) {
        //directory, file extension
        this.dir = dir;
        directory = new File(dir);
    }
    
    //use this for animations
    public int getFileCount(String ext) {
        int count = 0;
        for (String fl : directory.list()) {
            if (fl.substring(fl.length() - ext.length()).equals(ext)) { count++; }
        }
        return count;
    }
    
    public static int FileCount(File directory, String ext) {
        int count = 0;
        for (String fl : directory.list()) {
            if (fl.substring(fl.length() - ext.length()).equals(ext)) { count++; }
        }
        return count;
    }
    
}
