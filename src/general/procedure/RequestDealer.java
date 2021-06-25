/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package general.procedure;

import general.procedure.Request.RequestType;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author night
 * @param <D> data holder type
 */
public class RequestDealer<D> {
    private final List<Request<D>> requests = new ArrayList<>();
    
    public RequestDealer() {}
    
    public List<Request<D>> getRequests() {
        return requests;
    }
    
    public void attemptToResolveCurrentRequest(float tpf, D data) {
        boolean wallActive = false;
        boolean finished = requests.get(0).update(tpf, data);
        for (int i = 1; i < requests.size(); i++) {
            if (requests.get(i).getRequestType() == RequestType.ASAP || !wallActive) {
                requests.get(i).update(tpf, data);
            }
            
            if (requests.get(i).getIsWall()) {
                wallActive = true;
            }
        }
        
        if (finished) {
            requests.remove(0);
        }
    }
    
    public boolean hasNext() {
        return !requests.isEmpty();
    }
    
}
