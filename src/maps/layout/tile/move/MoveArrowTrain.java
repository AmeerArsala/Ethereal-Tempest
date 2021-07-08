/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package maps.layout.tile.move;

import com.jme3.asset.AssetManager;
import com.jme3.math.ColorRGBA;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import maps.layout.MapCoords;
import maps.layout.tile.TileFoundation;

/**
 *
 * @author night
 */
public class MoveArrowTrain { //head must move with the Cursor
    protected static final ColorRGBA VISIBLE_COLOR = new ColorRGBA(1f, 1f, 1f, 0.9f);
    protected static final ColorRGBA INVISIBLE_COLOR = new ColorRGBA(1f, 1f, 1f, 0f);
    
    private final HashMap<MapCoords, MoveArrowTile> loadedArrowTiles = new HashMap<>();
    private final LinkedList<MoveArrowTile> train = new LinkedList<>(); //last index is destination
    private final AssetManager assetManager;
    
    private int capacity; //determines train size; if the size becomes higher than this OR a conflict occurs, train becomes autogenerated
    private boolean isVisible = true;
    private MoveArrowTile root;
    
    public MoveArrowTrain(AssetManager assetManager, int capacity) {
        this.assetManager = assetManager;
        this.capacity = capacity;
    }
    
    public MoveArrowTrain(AssetManager assetManager) {
        this.assetManager = assetManager;
        capacity = 0;
    }
    
    public int getCapacity() {
        return capacity;
    }
    
    public boolean isVisible() {
        return isVisible;
    }
    
    public void setCapacity(int nextCapacity) {
        capacity = nextCapacity;
    }
    
    public void setRoot(MapCoords rootCoords) {
        root = createSegment(rootCoords);
    }
    
    public boolean hasTile(MapCoords coords) {
        return train.stream().anyMatch((tile) -> (tile.getPos().equals(coords)));
    }
    
    public void append(MapCoords coords) {
        addRootIfAbsent();
        
        int secondToLast = train.size() - 2;
        if (secondToLast >= 0 && coords.equals(train.get(secondToLast).getPos())) {
            train.removeLast().getNode().removeFromParent();
        } else if (train.size() + 1 > capacity + 1 || hasTile(coords)) {
            pathTo(coords);
        } else {
            push(coords);
        }
    }
    
    protected void pathTo(MapCoords coords) {
        Path path = new Path(root.getPos(), coords, capacity);
        List<MapCoords> sequence = path.getSequence();
        clear();
        for (MapCoords pos : sequence) {
            push(pos);
        }
    }
    
    protected void push(MapCoords coords) {
        MoveArrowTile moveArrowTile = createSegment(coords);
        add(moveArrowTile);
    }
    
    protected void add(MoveArrowTile next) {
        next.attachToCurrentMapIfNotAlready();
        train.addLast(next);
    }
    
    private void addRootIfAbsent() {
        if (train.isEmpty() || (!train.isEmpty() && !train.getFirst().getPos().equals(root.getPos()))) {
            train.addFirst(root);
        }
    }
    
    private MoveArrowTile createSegment(MapCoords coords) {
        MoveArrowTile moveArrowTile = loadedArrowTiles.get(coords);
        if (moveArrowTile == null) {
            moveArrowTile = new MoveArrowTile(coords, assetManager);
            loadedArrowTiles.put(coords, moveArrowTile);
        }
        
        return moveArrowTile;
    }
    
    public void clear() {
        for (MoveArrowTile moveIndicator : train) {
            moveIndicator.getNode().removeFromParent();
        }
        
        train.clear();
    }
    
    public void setVisibility(boolean visible) {
        ColorRGBA color = visible ? VISIBLE_COLOR : INVISIBLE_COLOR;
        for (int i = 0; i < train.size(); i++) {
            train.get(i).setColor(color);
        }
        
        isVisible = visible;
    }
    
    public void tick() {
        if (!isVisible) {
            return;
        }
        
        addRootIfAbsent();
        
        MoveArrowTile head = train.getLast();
        head.setIndex(MoveArrowTile.HEAD);
        head.setColor(VISIBLE_COLOR); //reveal it
        
        int secondToLast = train.size() - 2;
        if (secondToLast >= 0) {
            head.adjust(train.get(secondToLast), null);
        }
        
        //train.getFirst().setColor(INVISIBLE_COLOR); //hide the first one
        /*if (train.size() > 1) {
            train.getFirst().adjust(null, train.get(1));
        }*/
        
        for (int i = 1; i < train.size() - 1; i++) { //everything in between is either STEM or TURN
            MoveArrowTile currentPart = train.get(i);
            currentPart.setColor(VISIBLE_COLOR);
            currentPart.adjust(train.get(i - 1), train.get(i + 1));
        }
    }
    
    public MapCoords[] asArrayWithoutRoot() {
        MapCoords[] tilePath = new MapCoords[train.size() - 1];
        for (int i = 1; i < train.size(); i++) {
            tilePath[i - 1] = train.get(i).getPos();
        }
        
        return tilePath;
    }
    
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder(train.getFirst().getPos().toString());
        for (int i = 1; i < train.size(); i++) {
            sb.append(", ").append(train.get(i).getPos().toString());
        }
        
        return sb.toString();
    }
}