/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package etherealtempest;

import java.util.List;

/**
 *
 * @author night
 */
public interface Requestable {
    public List<Request> getRequests();
    public void attemptToResolveCurrentRequest(float tpf, DataStructure data);
}
