/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controller.converter;

import jakarta.faces.component.UIComponent;
import jakarta.faces.context.FacesContext;
import jakarta.faces.convert.Converter;
import jakarta.faces.convert.FacesConverter;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 *
 * @author Everton
 */
@FacesConverter("dateTimeConverter")
public class DateTimeConverter implements Converter {

    @Override
    public Object getAsObject(FacesContext fc, UIComponent uic, String string) {
    SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");    
        try {
            return sdf.parse(string);
        } catch (ParseException ex) {
            throw new RuntimeException("Não foi possível converter a data.");
        }
    }

    @Override
    public String getAsString(FacesContext fc, UIComponent uic, Object o) {
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss"); 
        return sdf.format((Date) o);
    }       
}
