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

/**
 *
 * @author Everton
 */
@FacesConverter("telefoneConverter")
public class TelefoneConverter implements Converter {

    /**
     * Creates a new instance of cpfConverter
     */   

    public TelefoneConverter() {
    }

    @Override
    public Object getAsObject(FacesContext fc, UIComponent uic, String string) {
        
        if (string != null) {
            
            String telefone = string.replace("(", "")
                    .replace(")", "")
                    .replace(" ", "")
                    .replace("_", "")
                    .replace("-", "");
            if (telefone.length() < 10) {
                throw new ConverterException(
                        new FacesMessage(FacesMessage.SEVERITY_ERROR, "Telefone inválido.", null));
            }
            
            return telefone;
        } else {
            return null;
        }
    }

    @Override
    public String getAsString(FacesContext fc, UIComponent uic, Object o) {
        return ((String) o);
    } 
}
