/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package etherealtempest.info;

/**
 *
 * @author night
 * @param <D> data holder type
 */
public abstract class Request<D extends Conveyor> {
    public enum RequestType {
        Ordinal, //linear
        ASAP; //happens ASAP
    }
    
    private final RequestType requestType;
    
    //meaning that this request will not let everything else around it happen until it is done; otherwise, happens at the same time as other requests
    //walls cannot out ASAP requests
    private final boolean isWall; 
    
    public Request(RequestType requestType, boolean isWall) {
        this.requestType = requestType;
        this.isWall = isWall;
    }
    
    public boolean update(float tpf, D data) { //returns if the update is done or not
        boolean finished = update(data, tpf);
        
        if (finished) {
            onFinish(data);
            return true;
        }
        
        return false;
    }
    
    protected abstract boolean update(D data, float tpf);
    protected abstract void onFinish(D data);
    
    public RequestType getRequestType() { return requestType; }
    public boolean getIsWall() { return isWall; }
    
}
