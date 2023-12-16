/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controller.converter;

import jakarta.faces.application.FacesMessage;
import jakarta.faces.component.UIComponent;
import jakarta.faces.context.FacesContext;
import jakarta.faces.convert.Converter;
import jakarta.faces.convert.ConverterException;
import jakarta.faces.convert.FacesConverter;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 *
 * @author Everton
 */
 
@FacesConverter("dataConverter")
public class DataConverter implements Converter {

    @Override
    public Object getAsObject(FacesContext fc, UIComponent uic, String string) {
    SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
    sdf.setLenient(false);
        try {
            return (string != null) ? sdf.parse(string) : null;
        } catch (ParseException ex) {
            throw new ConverterException(
                            new FacesMessage(FacesMessage.SEVERITY_ERROR, "Data inválida.", null));
        }
    }

    @Override
    public String getAsString(FacesContext fc, UIComponent uic, Object o) {
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        if (o != null) {
            return sdf.format((Date) o);
        }
        return null;
    }   
}
