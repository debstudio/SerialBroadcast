/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package SerialBroadcast;

/**
 *
 * @author Zippo
 */


import com.fazecast.jSerialComm.SerialPort;
import com.google.gson.Gson;
import java.awt.TrayIcon;

import java.awt.event.ActionEvent;
import java.io.IOException;
import java.io.OutputStream;
import static java.lang.Thread.sleep;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Enumeration;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.java_websocket.WebSocket;

/**
*
* @author ricardo
*/
public class ManejadorSerie {
    private static Enumeration puertos_libres =null;
    private static ArrayList<SerialPort> puertosAbiertos=new ArrayList<SerialPort>();
    
    public synchronized static void conectar( String portName, int baudrate)
    {
            boolean encontrado=false;
                 ArrayList<SerialPort> auxpuertosAbiertos=(ArrayList<SerialPort>) puertosAbiertos.clone();
                for(SerialPort sp:auxpuertosAbiertos)
                {
                    if(sp.getSystemPortName().compareTo(portName)==0) 
                        if(!sp.isOpen()){

                            
                              removePuertosAbiertos(sp);
                        }
                         else
                         {
                                encontrado=true;

                         }
                }
                
                        
                if(!encontrado){
                    SerialPort commPort =SerialPort.getCommPort(portName);
                    commPort.setBaudRate​(baudrate);
                    commPort.openPort();
                    if(commPort.isOpen())
                    {
                        try {
                            
                            commPort.addDataListener(new SerialPortListener(commPort));
                            Thread.sleep(2000);
                            SystemTrayWebSocket.trayIcon.displayMessage("Serial Broadcast",
                                    "PUERO SERIE " +portName+" CONECTADO",
                                    TrayIcon.MessageType.INFO);
                            puertosAbiertos.add(commPort);
                        } catch (InterruptedException ex) {
                            Logger.getLogger(ManejadorSerie.class.getName()).log(Level.SEVERE, null, ex);
                        }
                        
                    }
                    
                    else{

                        SystemTrayWebSocket.trayIcon.displayMessage("Serial Broadcast", "NO SE PUDO ABRIR EL PUERTO "+portName,TrayIcon.MessageType.ERROR);

                    }
                }

    }
     public static void enviar( String mensaje )
    {
            byte[] decodedBytes = Base64.getDecoder().decode(mensaje);    
                    System.out.println(decodedBytes);
                    StringBuilder sb = new StringBuilder();
                    for (byte b : decodedBytes) {
                        sb.append(String.format("%02X ", b));
                    }
                    System.out.println(sb.toString());
                    ArrayList<SerialPort> auxpuertosAbiertos=(ArrayList<SerialPort>) puertosAbiertos.clone();
            for(SerialPort sp:auxpuertosAbiertos)
            {
                try {
                    sp.getOutputStream().write(decodedBytes);
                    sp.getOutputStream().write('\n');
                    sp.getOutputStream().write('\r');
                } catch (IOException ex) {
                    Logger.getLogger(ManejadorSerie.class.getName()).log(Level.SEVERE, null, ex);
                    SystemTrayWebSocket.trayIcon.displayMessage("Serial Broadcast", "Error"+" "+sp.getSystemPortName()+": " + ex.getMessage(), TrayIcon.MessageType.ERROR);
                    puertosAbiertos.remove(sp);
                }
          
            }
    }
    public static void recibir( byte[] mensaje)
    {
        
        String sMensaje=Base64.getEncoder().encodeToString(mensaje);
        System.out.println(sMensaje);
        System.out.println(sMensaje);
        for(WebSocket conn:WebsocketServer.obtieneConexiones())
        {
                SDTMensaje mensCon=new SDTMensaje();
                mensCon.setTipo("SERIE");
                mensCon.setMensaje(sMensaje);
                Gson gson = new Gson();
                conn.send(gson.toJson(mensCon, SDTMensaje.class));
        }

    }
    public synchronized static void removePuertosAbiertos(SerialPort sp)
    {
         for (SerialPort auxSp:puertosAbiertos)
         {    
             if (auxSp.getSystemPortName().compareTo(sp.getSystemPortName())==0)
             {
                    if(sp.isOpen())
                    {
                        sp.removeDataListener();
                        sp.closePort();
                        
                        
                    }
                    puertosAbiertos.remove(auxSp);
                    return;
             }
         }
    }
    public static void compruebaPuertos()
    {
        ArrayList<SerialPort> auxpuertosAbiertos=(ArrayList<SerialPort>) puertosAbiertos.clone();
        System.out.println(auxpuertosAbiertos);
         for(SerialPort sp:auxpuertosAbiertos)
            {
               if(!sp.isOpen()){
            
                    SystemTrayWebSocket.trayIcon.displayMessage("Serial Broadcast", "DESCONECTADO "+sp.getSystemPortName(),TrayIcon.MessageType.ERROR);

                    removePuertosAbiertos(sp);
                }
            }
         
    }
    public static void removeTodosPuertosAbiertos()
    {
         ArrayList<SerialPort> auxpuertosAbiertos=(ArrayList<SerialPort>) puertosAbiertos.clone();
         for(SerialPort sp:auxpuertosAbiertos)
            {
                removePuertosAbiertos(sp);
                SystemTrayWebSocket.trayIcon.displayMessage("Serial Broadcast", "DESCONECTADO "+sp.getSystemPortName(),TrayIcon.MessageType.INFO);
            }
         
    }
    
}

