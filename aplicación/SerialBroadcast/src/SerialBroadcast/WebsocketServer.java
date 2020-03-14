/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package SerialBroadcast;


import org.java_websocket.WebSocket;
import org.java_websocket.handshake.ClientHandshake;
import org.java_websocket.server.WebSocketServer;

import java.net.InetSocketAddress;
import java.util.HashSet;
import java.util.Set;
import com.google.gson.Gson;
import java.awt.TrayIcon;


public class WebsocketServer extends WebSocketServer {
    Gson gson = new Gson();
    private static int TCP_PORT = 4444;
    TrayIcon trayIcon;
    private static Set<WebSocket> conns;

    public WebsocketServer() {
        super(new InetSocketAddress(TCP_PORT));
        conns = new HashSet<>();
        
    }

    @Override
    public void onOpen(WebSocket conn, ClientHandshake handshake) {
        conns.add(conn);
        /*
        System.out.println("New connection from " + conn.getRemoteSocketAddress().getAddress().getHostAddress());
        SDTMensaje impresoras=obtieneImpresoras();
        conn.send(gson.toJson(impresoras, SDTMensaje.class));*/
    }

    @Override
    public void onClose(WebSocket conn, int code, String reason, boolean remote) {
        conns.remove(conn);
        System.out.println("Closed connection to " + conn.getRemoteSocketAddress().getAddress().getHostAddress());
        ManejadorSerie.removeTodosPuertosAbiertos();
    }

    @Override
    public void onMessage(WebSocket conn, String message) {
        

        SDTMensaje smensaje= gson.fromJson(message, SDTMensaje.class);
        System.out.println("Message from client: " + message);

        if (smensaje.getTipo().compareTo("CONECTAR")==0){
            String puertoNombre;
            puertoNombre = smensaje.getMensaje().split(";")[0];
            int baudrate  ;
            baudrate = Integer.parseInt(smensaje.getMensaje().split(";")[1].trim());
            ManejadorSerie.conectar(puertoNombre,baudrate);
            SDTMensaje mensCon=new SDTMensaje();
            mensCon.setTipo("CONECTADO");
            conn.send(gson.toJson(mensCon, SDTMensaje.class));
        }
        if (smensaje.getTipo().compareTo("SPRIT")==0){

           ManejadorSerie.enviar(smensaje.getMensaje());
        }

    }

    @Override
    public void onError(WebSocket conn, Exception ex) {
        //ex.printStackTrace();
         SystemTrayWebSocket.trayIcon.displayMessage("Serial Broadcast", "NO SE PUDO INICIAR\nERROR " +ex.getClass().getSimpleName()+' '+ex.getLocalizedMessage(),TrayIcon.MessageType.ERROR);
        if (conn != null) {
            conns.remove(conn);
            // do some thing if required
            System.out.println("ERROR from " + conn.getRemoteSocketAddress().getAddress().getHostAddress());
        }
        else
        {
           
            System.out.println("ERROR " +ex.getClass().getSimpleName()+' '+ex.getLocalizedMessage());
            System.out.println("Exiting...");
            System.exit(0);
        }
        ManejadorSerie.removeTodosPuertosAbiertos();
        
    }

    @Override
	public void onStart() {
		System.out.println("Server started!");
		setConnectionLostTimeout(0);
		setConnectionLostTimeout(100);
                SystemTrayWebSocket.trayIcon.displayMessage("Serial Broadcast", 
                        "Iniciado!",
                        TrayIcon.MessageType.INFO);
	}


 

  
         public static Set<WebSocket> obtieneConexiones()
         {
             return conns;
         }    
}

