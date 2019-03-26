package server.controller;

import shared.RemoteCommands;
import shared.TopData;

import javax.ejb.Stateless;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.List;

@Stateless
@Path("/")
public class MonitoringController {

    @GET
    @Path("{path:.*}")
    public InputStream index(@Context HttpServletRequest req, @PathParam("path") String path) {
        try {
            String base = req.getServletContext().getRealPath("");
            path = path.equals("") ? "index.jsp" : path;
            File f = new File(String.format("%s/%s", base, path));
            return new FileInputStream(f);
        } catch (FileNotFoundException e) {
            System.out.println(e.getMessage());
            return null;
        }
    }

    @POST
    @Path("top")
    public Response top(@FormParam("ip") String ip) {
        List<TopData> response;
        try {
            Registry registry = LocateRegistry.getRegistry(ip, 12122);
            RemoteCommands stub = (RemoteCommands) registry.lookup("RemoteCommands");
            response = stub.getTopData();
        } catch (RemoteException | NotBoundException e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(e.getMessage())
                    .build();
        }
        return Response.status(Response.Status.OK)
                .entity(response.toString())
                .build();
    }
}
