/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controller.converter;

import controller.Util;
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
@FacesConverter("cpfConverter")
public class CPFConverter implements Converter {

    /**
     * Creates a new instance of cpfConverter
     */
    public CPFConverter() {
    }

    @Override
    public Object getAsObject(FacesContext fc, UIComponent uic, String string) {

        if (string != null) {
        
            String cpf = string.replace(".", "").replace("-", "");
            if (Util.isCPF(cpf)) {
                return cpf;
            } else {
                throw new ConverterException(
                            new FacesMessage(FacesMessage.SEVERITY_ERROR, "CPF inválido.", null));
            }     
        } else {
            return null;  
        }
    }

    @Override
    public String getAsString(FacesContext fc, UIComponent uic, Object o) {
        return ((String) o);
    }
    
}
