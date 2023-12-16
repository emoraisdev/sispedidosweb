/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bean;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 *
 * @author Everton
 */
public class ItemPedidoAdicional implements Serializable {
    private BigDecimal valor;
    private ItemPedido itemPedido;
    private ItemAdicional itemAdicional;

    public BigDecimal getValor() {
        return valor;
    }

    public void setValor(BigDecimal valor) {
        this.valor = valor;
    }

    public ItemPedido getItemPedido() {
        return itemPedido;
    }

    public void setItemPedido(ItemPedido itemPedido) {
        this.itemPedido = itemPedido;
    }

    public ItemAdicional getItemAdicional() {
        return itemAdicional;
    }

    public void setItemAdicional(ItemAdicional itemAdicional) {
        this.itemAdicional = itemAdicional;
    }
        
}
