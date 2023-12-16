/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bean;

import bean.enums.SituacaoItemPedido;
import controller.Util;

import java.io.Serializable;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Date;
import java.util.List;

/**
 *
 * @author Everton
 */
public class ItemPedido implements Serializable {
    private int codigo;
    private BigDecimal valor;
    private String observacao ;
    private int quantidade;
    private ItemCardapio item;
    private Pedido pedido;
    private SituacaoItemPedido situacao;
    private Date inicioPreparo, terminoPreparo;
    private Funcionario funcionario;
    private List<ItemPedidoAdicional> itensAdicionais;
    
    
    public ItemPedido(int codigo){
        this.codigo = codigo;
    }
    
    public BigDecimal getValorTotal(){
        BigDecimal valorTotal = BigDecimal.ZERO;
        
        for (ItemPedidoAdicional itemPedidoAdicional: itensAdicionais) {
            valorTotal = valorTotal.add(itemPedidoAdicional.getItemAdicional().getValor());
        }
        return valorTotal.add(this.valor).multiply(new BigDecimal(this.quantidade));    
    }
    
    public String getValorTotalEmReais(){
        return Util.BigDecimalToReal(getValorTotal());
    }

    public BigDecimal getValorTotalItensAdicionais(){
        BigDecimal valorTotal = BigDecimal.ZERO;
        
        for (ItemPedidoAdicional itemPedidoAdicional: itensAdicionais) {
            valorTotal = valorTotal.add(itemPedidoAdicional.getValor());
        }
        return valorTotal;    
    }    
    
    public String getValorTotalItensAdicionaisEmReais(){    
        return Util.BigDecimalToReal(getValorTotalItensAdicionais());
    }
    
    public String getResumoItensAdicionais(){
        
        String resumo = "";
        
        boolean bPrimeiro = true;
        for (ItemPedidoAdicional itemPedidoAdicional: this.itensAdicionais) {
            if (!bPrimeiro){
                resumo = resumo+", ";
            }
            resumo = resumo+itemPedidoAdicional.getItemAdicional().getNome();            
            bPrimeiro = false;
        }
        
        return resumo;
    }
    
    public boolean isPermiteDefinirComoPronto(){
        return this.item.isPermitePreparo() && 
                (this.situacao == SituacaoItemPedido.PENDENTE || this.situacao == SituacaoItemPedido.NA_FILA);
    }
    
    public boolean isPermiteSolicitarPreparo(){
        return this.item.isPermitePreparo() && (this.situacao == SituacaoItemPedido.PENDENTE);
    }

    public String getTempoPreparo(){
        BigDecimal difSegundos = new BigDecimal((terminoPreparo.getTime() - inicioPreparo.getTime())/1000);
                     
        Integer horas = difSegundos.divideToIntegralValue(new BigDecimal(3600)).intValue();
        difSegundos = difSegundos.remainder(new BigDecimal(3600));

        Integer minutos = difSegundos.divideToIntegralValue(new BigDecimal(60)).intValue();
        difSegundos = difSegundos.remainder(new BigDecimal(60));
        
        Integer segundos = difSegundos.setScale(0,RoundingMode.FLOOR).intValue();
                
        return String.format("%02d:%02d:%02d", horas, minutos, segundos);
    }
    
    public Date getInicioPreparo() {
        return inicioPreparo;
    }

    public void setInicioPreparo(Date inicioPreparo) {
        this.inicioPreparo = inicioPreparo;
    }

    public Date getTerminoPreparo() {
        return terminoPreparo;
    }

    public void setTerminoPreparo(Date terminoPreparo) {
        this.terminoPreparo = terminoPreparo;
    }        
    
    public List<ItemPedidoAdicional> getItensAdicionais() {
        return itensAdicionais;
    }

    public void setItensAdicionais(List<ItemPedidoAdicional> itensAdicionais) {
        this.itensAdicionais = itensAdicionais;
    }

    public SituacaoItemPedido getSituacao() {
        return situacao;
    }

    public void setSituacao(SituacaoItemPedido situacao) {
        this.situacao = situacao;
    }

    public Funcionario getFuncionario() {
        return funcionario;
    }

    public void setFuncionario(Funcionario funcionario) {
        this.funcionario = funcionario;
    }        

    public ItemPedido() {
        this.quantidade = 1;
    }        
        
    public BigDecimal getValor() {
        return valor;
    }
    
    public String getValorEmReais(){
        return Util.BigDecimalToReal(valor);
    }

    public int getCodigo() {
        return codigo;
    }

    public void setCodigo(int codigo) {
        this.codigo = codigo;
    }

    public void setValor(BigDecimal valor) {
        this.valor = valor;
    }

    public String getObservacao() {
        return observacao;
    }

    public void setObservacao(String observacao) {
        this.observacao = observacao;
    }

    public int getQuantidade() {
        return quantidade;
    }

    public void setQuantidade(int quantidade) {
        this.quantidade = quantidade;
    }

    public ItemCardapio getItem() {
        return item;
    }

    public void setItem(ItemCardapio item) {
        this.item = item;
    }

    public Pedido getPedido() {
        return pedido;
    }

    public void setPedido(Pedido pedido) {
        this.pedido = pedido;
    }
    
    
}
