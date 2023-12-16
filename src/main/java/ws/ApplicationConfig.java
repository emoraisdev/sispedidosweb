/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ws;

import javax.ws.rs.core.Application;
import java.util.Set;

/**
 *
 * @author Avell B155 MAX
 */
@javax.ws.rs.ApplicationPath("webresources")
public class ApplicationConfig extends Application {

    @Override
    public Set<Class<?>> getClasses() {
        Set<Class<?>> resources = new java.util.HashSet<>();
        addRestResourceClasses(resources);
        return resources;
    }

    /**
     * Do not modify addRestResourceClasses() method.
     * It is automatically populated with
     * all resources defined in the project.
     * If required, comment out calling this method in getClasses().
     */
    private void addRestResourceClasses(Set<Class<?>> resources) {
        resources.add(ws.AvaliacaoWS.class);
        resources.add(ws.CardapioWS.class);
        resources.add(ws.ClienteWS.class);
        resources.add(ws.EntradaWS.class);
        resources.add(ws.NotificacaoWS.class);
        resources.add(ws.PedidoWS.class);
        resources.add(ws.SugestaoWS.class);
        resources.add(ws.UtilWS.class);
    }
    
}
