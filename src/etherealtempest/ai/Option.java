/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package etherealtempest.ai;

import etherealtempest.info.ActionInfo.PostMoveAction;
import fundamental.Attribute;
import fundamental.Entity;
import maps.layout.MapCoords;
import maps.layout.occupant.control.CursorFSM.Purpose;
import maps.layout.occupant.MapEntity;

/**
 *
 * @author night
 */
public class Option {
        public final PostMoveAction action;
        public final Purpose purpose;
        public final Attribute option;
        public final MapCoords targetTile;
        public final Entity target; //must be one of the subclasses
        public final int fromRange;
        
        public int priority = -1;
        
        public Option(PostMoveAction accion, Purpose pur, Attribute opt, MapCoords cds, int range, Entity targetEntity) {
            action = accion;
            option = opt;
            purpose = pur;
            fromRange = range;
            targetTile = cds;
            target = targetEntity;
        }
        
        public Option(MapCoords cds) { //for waiting on a tile and doing nothing else
            targetTile = cds;
            fromRange = 0;
            purpose = null;
            option = null;
            action = null;
            target = null;
        }
        
        public Option(MapCoords cds, PostMoveAction accion) { //for waiting on a tile and doing nothing else
            targetTile = cds;
            action = accion;
            fromRange = 0;
            purpose = null;
            option = null;
            target = null;
        }
        
        public Option(MapCoords cds, PostMoveAction accion, MapEntity targetEntity, int range) { //for waiting on a tile and doing nothing else
            targetTile = cds;
            action = accion;
            target = targetEntity;
            fromRange = range;
            purpose = null;
            option = null;
        }
        
        public Option setPriority(int p) {
            priority = p;
            return this;
        }
    }
