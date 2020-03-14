/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package SerialBroadcast;

import java.awt.TrayIcon;
import java.util.Timer;

/**
 *
 * @author Zippo
 */

public class SerialBroadcast {
//"c:\Program Files\gs\gs9.14\bin\gswin64.exe" -dBATCH -dNOSAFER -dNOPAUSE -q -dNumCopies=1 -sDEVICE=mswinpr2 -sOutputFile="%printer%80mm" "d:\Zippo\Descargas\a.pdf"
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args)
    {
        
        SystemTrayWebSocket main = new SystemTrayWebSocket();
        SystemTrayWebSocket.trayIcon.setToolTip("Serial Broadcast PC");
        TareaCompruebaSerie tarea = new TareaCompruebaSerie();
        Timer temporizador = new Timer();
        Integer segundos = 10;
        temporizador.scheduleAtFixedRate(tarea, 0, 1000*segundos);
        
        new WebsocketServer().start();
        
    
           
    } 
    
}
