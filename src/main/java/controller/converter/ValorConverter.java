/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controller.converter;

import controller.Util;
import jakarta.faces.component.UIComponent;
import jakarta.faces.context.FacesContext;
import jakarta.faces.convert.Converter;
import jakarta.faces.convert.FacesConverter;

import java.math.BigDecimal;

/**
 *
 * @author Everton
 */
@FacesConverter("valorConverter")
public class ValorConverter implements Converter {

    @Override
    public Object getAsObject(FacesContext fc, UIComponent uic, String string) {
        if (string != null) {            
            return new BigDecimal(string.replace(",", "."));        
        } else {
            return null;
        }
    }

    @Override
    public String getAsString(FacesContext fc, UIComponent uic, Object o) {        
        if (o != null) {            
            return Util.BigDecimalToReal((BigDecimal)o).replace("R$", "");
        } else {
            return null;
        }
    }       
}
