/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package SerialBroadcast;

import com.fazecast.jSerialComm.SerialPort;
import com.fazecast.jSerialComm.SerialPortDataListenerWithExceptions;
import com.fazecast.jSerialComm.SerialPortEvent;


import java.awt.TrayIcon;
import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Zippo
 */
public class SerialPortListener implements SerialPortDataListenerWithExceptions
{
    BufferedReader input=null;
    byte[] buffer = new byte[1024];
    int pos=0;
    byte ch=0;
    byte antCh=0;
    boolean isMensaje=false;
    DataInputStream dataInput; 
    byte [] cComienzo;
    SerialPort serial;
    
    @Override
   
    public void serialEvent(SerialPortEvent spe) {

         if (spe.getEventType() == SerialPort.LISTENING_EVENT_DATA_AVAILABLE) {

             try {
                 while(dataInput.available()>0){
                     ch=(byte)dataInput.readUnsignedByte();
                     if (isMensaje)
                     {
                         
                         if((ch == 10 || pos>1020)){ //In ASCII code: 13 = Carriage return, 10 = line feed. When the GPS receiver sends those characters, the while loop must be broken to avoid an IOException
                             
                             if (pos >0){
                                 buffer[pos]=ch;
                                 pos++;
                                 byte [] unmensaje =new byte[pos];
                                 
                                 System.arraycopy(buffer, 0, unmensaje, 0, pos);
                                 ManejadorSerie.recibir(unmensaje);
                                 pos=0;
                                 isMensaje=false;
                                 
                             }
                             
                         }else{
                             buffer[pos]=ch;
                             pos++;
                         }
                     }
                     
                     
                     else{
                         if (ch==cComienzo[1]&&antCh==cComienzo[0]){
                             buffer[0]=antCh;
                             buffer[1]=ch;
                             pos=2;
                             isMensaje=true;
                         }
                     }
                     antCh=ch;
                 }
                 
                 /*
                 String inputLine;
                 inputLine = input.readLine();
                 System.out.println(inputLine);*/
             } catch (IOException ex) {
                 Logger.getLogger(SerialPortListener.class.getName()).log(Level.SEVERE, null, ex);
                SystemTrayWebSocket.trayIcon.displayMessage("Serial Broadcast", "Error "+serial.getSystemPortName()+": " + ex.getMessage(), TrayIcon.MessageType.ERROR);
                ManejadorSerie.removePuertosAbiertos(serial);
             }
 
         }
   

    }

    public SerialPortListener(SerialPort sp) {
         serial=sp;
         InputStream ie=sp.getInputStream();
         input = new BufferedReader(new InputStreamReader(ie));
         dataInput= new DataInputStream(ie);
         cComienzo=new byte[2];
         cComienzo[0]=(byte) 0xFF;
         cComienzo[1]=0x55;
    }

    @Override
    public void catchException(Exception ex) {
        Logger.getLogger(SerialPortListener.class.getName()).log(Level.SEVERE, null, ex);
        SystemTrayWebSocket.trayIcon.displayMessage("Serial Broadcast", "Error "+serial.getSystemPortName()+": " + ex.getMessage(), TrayIcon.MessageType.ERROR);
        ManejadorSerie.removePuertosAbiertos(serial);
    }

    @Override
    public int getListeningEvents() {
         return SerialPort.LISTENING_EVENT_DATA_AVAILABLE;
    }
    
    
}