package filecatalog.server.startup;

import filecatalog.server.controller.Controller;

import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public class ServerMain {
    public static void main(String args[]){
        try{
            new ServerMain().startRMIServant();
            Naming.rebind(Controller.FILE_CATALOG_NAME_IN_REGISTRY,new Controller());
            System.out.println("File catalog server is running.");
        }
        catch(RemoteException | MalformedURLException e) {
            System.out.println("Failed to start file catalog server.");
        }
    }

    private void startRMIServant() throws RemoteException{
        try {
            LocateRegistry.getRegistry().list();
        } catch (RemoteException e) {
            LocateRegistry.createRegistry(Registry.REGISTRY_PORT);
        }
    }
}
