package org.neodatis.odb.test.fromusers.gyowanny_queiroz;

import java.io.Serializable;
import java.math.BigDecimal;

public class ItemVenda2 implements Serializable {

	private Long id;

	private Long vendaId;

	private Long produtoId;

	private BigDecimal valorUnitario;

	private BigDecimal quantidade;

	private String observacao;

	private transient String produtoDescricao;

	public ItemVenda2() {
		super();
		// TODO Auto-generated constructor stub
	}

	public ItemVenda2(Long id, Long vendaId, Long produtoId, BigDecimal valorUnitario, BigDecimal quantidade, String observacao,
			String produtoDescricao) {
		super();
		this.id = id;
		this.vendaId = vendaId;
		this.produtoId = produtoId;
		this.valorUnitario = valorUnitario;
		this.quantidade = quantidade;
		this.observacao = observacao;
		this.produtoDescricao = produtoDescricao;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Long getVendaId() {
		return vendaId;
	}

	public void setVendaId(Long vendaId) {
		this.vendaId = vendaId;
	}

	public Long getProdutoId() {
		return produtoId;
	}

	public void setProdutoId(Long produtoId) {
		this.produtoId = produtoId;
	}

	public BigDecimal getValorUnitario() {
		return valorUnitario;
	}

	public void setValorUnitario(BigDecimal valorUnitario) {
		this.valorUnitario = valorUnitario;
	}

	public BigDecimal getQuantidade() {
		return quantidade;
	}

	public void setQuantidade(BigDecimal quantidade) {
		this.quantidade = quantidade;
	}

	public String getObservacao() {
		return observacao;
	}

	public void setObservacao(String observacao) {
		this.observacao = observacao;
	}

	public String getProdutoDescricao() {
		return produtoDescricao;
	}

	public void setProdutoDescricao(String produtoDescricao) {
		this.produtoDescricao = produtoDescricao;
	}

}
