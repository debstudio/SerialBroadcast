/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package SerialBroadcast;

import java.util.TimerTask;

/**
 *
 * @author Zippo
 * **/
public class TareaCompruebaSerie extends TimerTask {
 
    public TareaCompruebaSerie() {
        
    }
 
    @Override
    public void run() {
        ManejadorSerie.compruebaPuertos();
    }
 
}