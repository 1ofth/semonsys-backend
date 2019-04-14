package shared;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;

public interface RemoteCommands extends Remote {
    List<TopData> getTopData() throws RemoteException;
}
