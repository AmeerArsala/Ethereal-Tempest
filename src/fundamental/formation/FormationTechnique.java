/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fundamental.formation;

import com.jme3.math.Vector2f;
import com.jme3.math.Vector3f;
import etherealtempest.fsm.MasterFsmState;
import etherealtempest.info.Conveyor;
import general.procedure.Request;
import fundamental.Attribute;
import fundamental.tool.Tool.ToolType;
import fundamental.unit.UnitAllegiance;
import java.util.Arrays;
import java.util.List;
import maps.layout.Coords;
import maps.layout.MapLevel;
import maps.layout.MapCoords;
import maps.layout.occupant.VenturePeek;
import maps.layout.occupant.character.TangibleUnit;
import maps.layout.tile.Tile;

/**
 *
 * @author night
 */
public abstract class FormationTechnique extends Attribute {
    private final List<Integer> ranges;
    private final ToolType toolType;
    private final int desirability;
    
    public FormationTechnique(String name, String description, List<Integer> ranges, ToolType toolType, int desirability) {
        super(name, description);
        this.ranges = ranges;
        this.toolType = toolType;
        this.desirability = desirability;
    }
    
    public abstract boolean getCondition(Conveyor data);
    public abstract void useTechnique(Conveyor data);
    
    public List<Integer> getRanges() { return ranges; }
    public ToolType getToolType() { return toolType; }
    public int getDesirability() { return desirability; }
    
    public boolean isAvailableAt(MapCoords pos, UnitAllegiance allegiance) { 
        MapLevel currentMap = MasterFsmState.getCurrentMap();
        for (Integer range : ranges) {
            for (MapCoords point : VenturePeek.coordsForTilesOfRange(range, pos)) {
                TangibleUnit occupier = currentMap.getTileAt(point).getOccupier();
                if (occupier != null && toolType.isSupportive() == allegiance.alliedWith(occupier.getAllegiance())) {
                    return true;
                }
            }
        }
        
        return false;
    }
    
    
    public static final FormationTechnique FallBack() {
        return new FormationTechnique
            (
                "Fall Back", 
                "Both the user and its targeted ally, who is adjacent to the user, move one space backwards (in other words, one space in the direction of the user)",
                Arrays.asList(1), //1 range
                ToolType.SupportAlly,
                230 //priority for AI
            ) 
        {                 
            @Override
            public void useTechnique(Conveyor data) {
                TangibleUnit unit = data.getUnit(), ally = data.getOtherUnit();
                
                Coords xyDirection = unit.getPos().getCoords().subtract(ally.getPos().getCoords()).signsOf();
                int frames = 5;
                float speed = Tile.SIDE_LENGTH / frames;
                Vector2f deltaDistance = xyDirection.toVector2f().divideLocal(frames);
                Vector3f deltaTranslation = xyDirection.toVector3fZX().multLocal(speed);
                
                data.getMapFlowRequestTaker().getRequests().add(
                    new Request<Conveyor>(Request.RequestType.Ordinal, false) {
                        private final Vector2f distance = new Vector2f(0f, 0f);
                        
                        @Override
                        protected boolean update(Conveyor data, float tpf) {
                            data.getUnit().getNode().move(deltaTranslation);
                            data.getOtherUnit().getNode().move(deltaTranslation);
                            
                            distance.addLocal(deltaDistance);
                            
                            return distance.x == xyDirection.x && distance.y == xyDirection.y;
                        }

                        @Override
                        protected void onFinish(Conveyor data) {
                            TangibleUnit user = data.getUnit(), target = data.getOtherUnit();
                            user.remapPosition(user.getPos().add(xyDirection));
                            target.remapPosition(target.getPos().add(xyDirection));
                        }
                    }
                );
            }
            
            @Override
            public boolean getCondition(Conveyor data) {
                TangibleUnit user = data.getUnit(), target = data.getOtherUnit();
                if (!user.isAlliedWith(target) || user.getPos().getLayer() != target.getPos().getLayer()) { return false; }
                
                return user.getPos().getCoords().nonDiagonalDistanceFrom(target.getPos().getCoords()) == 1; // must be adjacent
            }
        };
    }
}
