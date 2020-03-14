package SerialBroadcast;

import java.awt.*;
import java.awt.event.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.ImageIcon;

public class SystemTrayWebSocket
{
    static TrayIcon trayIcon=null;
    public SystemTrayWebSocket()
    {
        
        /*El objeto trayIcon representa el tray icon valga la redundancia
          a este objeto se le pueden asigna imágenes, popups, tooltips y
          una serie de listeners asociados a el*/
        

        /*Se verifica si el sistema soporta los try icons*/
        if (SystemTray.isSupported()) {

            SystemTray tray = SystemTray.getSystemTray();
           
            //Se asigna la imagen que del tray icon
            ImageIcon im = new ImageIcon(SystemTrayWebSocket.class.getResource("tray.png"));
            Image image = Toolkit.getDefaultToolkit().getImage("tray.png");
        
            //Este listener permite salir de la aplicacion
            ActionListener exitListener = new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    System.out.println("Exiting...");
                    System.exit(0);
                }
            };
            ActionListener closeListener = new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    ManejadorSerie.removeTodosPuertosAbiertos();
                }
            };
            //Aquí se crea un popup menu
            PopupMenu popup = new PopupMenu();
            
            MenuItem closetItem = new MenuItem("Close all ports");

            //Se le asigna al item del popup el listener para salir de la app
            closetItem.addActionListener(closeListener);
            popup.add(closetItem);
               
            //Se agrega la opción de salir
            MenuItem defaultItem = new MenuItem("Exit");

            //Se le asigna al item del popup el listener para salir de la app
            defaultItem.addActionListener(exitListener);
            popup.add(defaultItem);

            /*Aqui creamos una instancia del tray icon y asignamos
            La imagen, el nombre del tray icon y el popup*/
            trayIcon = new TrayIcon(im.getImage(), "Tray Icon", popup);



            trayIcon.setImageAutoSize(true);
           

            try {

                tray.add(trayIcon);
               
            } catch (AWTException ex) {
                ex.printStackTrace();
            }
           
        } else {
            System.err.println("System tray is currently not supported.");
        }
    }
    
    /**
     * @param args the command line arguments
     */
  
}