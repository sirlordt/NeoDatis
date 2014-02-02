/**
 * 
 */
package org.neodatis.odb.test.fromusers.gyowanny_queiroz;

import java.math.BigDecimal;

/**
 * @author olivier
 *
 */
public class ItemVenda {
	private Long id;
	private Long vendaId;
	private BigDecimal value;
	private String description;
	public ItemVenda(Long id, Long vendaId, BigDecimal value, String description) {
		super();
		this.id = id;
		this.vendaId = vendaId;
		this.value = value;
		this.description = description;
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
	public BigDecimal getValue() {
		return value;
	}
	public void setValue(BigDecimal value) {
		this.value = value;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	
	

}
