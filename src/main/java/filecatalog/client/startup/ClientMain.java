package filecatalog.client.startup;

import filecatalog.client.view.NonBlockingInterpreter;
import filecatalog.common.FileCatalogServer;

import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;

public class ClientMain {

    public static void main(String args[]){
        try{
            FileCatalogServer server = (FileCatalogServer) Naming.lookup(FileCatalogServer.FILE_CATALOG_NAME_IN_REGISTRY);
            new NonBlockingInterpreter().start(server);
            System.out.println("done");
        }
        catch (RemoteException | MalformedURLException | NotBoundException e) {
            e.printStackTrace();
        }
    }
}
