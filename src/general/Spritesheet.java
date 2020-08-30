/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package general;

/**
 *
 * @author night
 */
public class Spritesheet {
    private int rows;
    private int columns;
    private String fileName;
    
    public Spritesheet(int rows, int columns, String fileName) {
        this.rows = rows;
        this.columns = columns;
        this.fileName = fileName;
    }
    
    public int getRows() { return rows; }
    public int getColumns() { return columns; }
    
    public String getSheetName() { return fileName; }
}
