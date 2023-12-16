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

import java.util.Map;

/**
 *
 * @author Everton
 */
@FacesConverter("grupoCardConverter")
public class GrupoCardConverter implements Converter {

    public Object getAsObject(FacesContext fc, UIComponent uic, String string) {
        if (string != null) {
            return this.getAttributesFrom(uic).get(string);
        }
        return null;
    }

    @Override
    public String getAsString(FacesContext facesContext, UIComponent uiComponent, Object o) {
        return ((String) o);
    }


    protected Map<String, Object> getAttributesFrom(UIComponent component) {
        return component.getAttributes();
    }

}