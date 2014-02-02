/**
 * 
 */
package org.neodatis.odb.test.fromusers.cetip;

/**
 * @author olivier
 * 
 */
public class Output implements java.io.Serializable {
	/**
	 * The serial version UID of this class. Needed for serialization.
	 */
	private static final long serialVersionUID = -8669839804854658428L;

	public Output() {
		this.dataPosicao = null;
		this.valorConfianca = null;
		this.prazo = 0;
		this.taxaLivreRisco = null;
		this.taxaLivreRiscoExterna = null;
		this.precoAtivoBase = null;
		this.grauLiberdade = null;
		this.premioEstimadoCall = null;
		this.rangeInferiorCall = null;
		this.rangeSuperiorCall = null;
		this.premioEstimadoPut = null;
		this.rangeInferiorPut = null;
		this.rangeSuperiorPut = null;
		this.taxaSuperior = null;
		this.taxaInferior = null;
		this.rangeAditivo = null;
		this.idDadosEntrada = null;
		this.volatilidade = null;
		this.volatilidadeInferior = null;
		this.volatilidadeSuperior = null;
		this.enquadrouCall = null;
		this.enquadrouGlobal = null;
		this.enquadrouPut = null;
		this.tamanhoAmostra = 0;
		this.desvioPadrao = null;
		this.enquadrouTaxa = null;
		this.avaliado = null;
		this.volatilidadeAtivoBase = null;
	}

	public Output(java.util.Date dataPosicao, java.math.BigDecimal valorConfianca, int prazo, java.math.BigDecimal taxaLivreRisco,
			java.math.BigDecimal taxaLivreRiscoExterna, java.math.BigDecimal precoAtivoBase, java.math.BigDecimal grauLiberdade,
			java.math.BigDecimal premioEstimadoCall, java.math.BigDecimal rangeInferiorCall, java.math.BigDecimal rangeSuperiorCall,
			java.math.BigDecimal premioEstimadoPut, java.math.BigDecimal rangeInferiorPut, java.math.BigDecimal rangeSuperiorPut,
			java.math.BigDecimal taxaSuperior, java.math.BigDecimal taxaInferior, java.math.BigDecimal rangeAditivo,
			java.lang.Long idDadosEntrada, java.math.BigDecimal volatilidade, java.math.BigDecimal volatilidadeInferior,
			java.math.BigDecimal volatilidadeSuperior, java.lang.String enquadrouCall, java.lang.String enquadrouGlobal,
			java.lang.String enquadrouPut, int tamanhoAmostra, java.math.BigDecimal desvioPadrao, java.lang.String enquadrouTaxa,
			java.lang.String avaliado, java.math.BigDecimal volatilidadeAtivoBase) {
		this.dataPosicao = dataPosicao;
		this.valorConfianca = valorConfianca;
		this.prazo = prazo;
		this.taxaLivreRisco = taxaLivreRisco;
		this.taxaLivreRiscoExterna = taxaLivreRiscoExterna;
		this.precoAtivoBase = precoAtivoBase;
		this.grauLiberdade = grauLiberdade;
		this.premioEstimadoCall = premioEstimadoCall;
		this.rangeInferiorCall = rangeInferiorCall;
		this.rangeSuperiorCall = rangeSuperiorCall;
		this.premioEstimadoPut = premioEstimadoPut;
		this.rangeInferiorPut = rangeInferiorPut;
		this.rangeSuperiorPut = rangeSuperiorPut;
		this.taxaSuperior = taxaSuperior;
		this.taxaInferior = taxaInferior;
		this.rangeAditivo = rangeAditivo;
		this.idDadosEntrada = idDadosEntrada;
		this.volatilidade = volatilidade;
		this.volatilidadeInferior = volatilidadeInferior;
		this.volatilidadeSuperior = volatilidadeSuperior;
		this.enquadrouCall = enquadrouCall;
		this.enquadrouGlobal = enquadrouGlobal;
		this.enquadrouPut = enquadrouPut;
		this.tamanhoAmostra = tamanhoAmostra;
		this.desvioPadrao = desvioPadrao;
		this.enquadrouTaxa = enquadrouTaxa;
		this.avaliado = avaliado;
		this.volatilidadeAtivoBase = volatilidadeAtivoBase;
	}

	public Output(java.lang.Long id, java.util.Date dataPosicao, java.math.BigDecimal valorConfianca, int prazo,
			java.math.BigDecimal taxaLivreRisco, java.math.BigDecimal taxaLivreRiscoExterna, java.math.BigDecimal precoAtivoBase,
			java.math.BigDecimal grauLiberdade, java.math.BigDecimal premioEstimadoCall, java.math.BigDecimal rangeInferiorCall,
			java.math.BigDecimal rangeSuperiorCall, java.math.BigDecimal premioEstimadoPut, java.math.BigDecimal rangeInferiorPut,
			java.math.BigDecimal rangeSuperiorPut, java.math.BigDecimal taxaSuperior, java.math.BigDecimal taxaInferior,
			java.math.BigDecimal rangeAditivo, java.lang.Long idDadosEntrada, java.math.BigDecimal volatilidade,
			java.math.BigDecimal volatilidadeInferior, java.math.BigDecimal volatilidadeSuperior, java.lang.String enquadrouCall,
			java.lang.String enquadrouGlobal, java.lang.String enquadrouPut, int tamanhoAmostra, java.math.BigDecimal desvioPadrao,
			java.lang.String enquadrouTaxa, java.lang.String avaliado, java.math.BigDecimal volatilidadeAtivoBase) {
		this.id = id;
		this.dataPosicao = dataPosicao;
		this.valorConfianca = valorConfianca;
		this.prazo = prazo;
		this.taxaLivreRisco = taxaLivreRisco;
		this.taxaLivreRiscoExterna = taxaLivreRiscoExterna;
		this.precoAtivoBase = precoAtivoBase;
		this.grauLiberdade = grauLiberdade;
		this.premioEstimadoCall = premioEstimadoCall;
		this.rangeInferiorCall = rangeInferiorCall;
		this.rangeSuperiorCall = rangeSuperiorCall;
		this.premioEstimadoPut = premioEstimadoPut;
		this.rangeInferiorPut = rangeInferiorPut;
		this.rangeSuperiorPut = rangeSuperiorPut;
		this.taxaSuperior = taxaSuperior;
		this.taxaInferior = taxaInferior;
		this.rangeAditivo = rangeAditivo;
		this.idDadosEntrada = idDadosEntrada;
		this.volatilidade = volatilidade;
		this.volatilidadeInferior = volatilidadeInferior;
		this.volatilidadeSuperior = volatilidadeSuperior;
		this.enquadrouCall = enquadrouCall;
		this.enquadrouGlobal = enquadrouGlobal;
		this.enquadrouPut = enquadrouPut;
		this.tamanhoAmostra = tamanhoAmostra;
		this.desvioPadrao = desvioPadrao;
		this.enquadrouTaxa = enquadrouTaxa;
		this.avaliado = avaliado;
		this.volatilidadeAtivoBase = volatilidadeAtivoBase;
	}

	private java.lang.Long id;

	/**
	    *
	    */
	public java.lang.Long getId() {
		return this.id;
	}

	public void setId(java.lang.Long id) {
		this.id = id;
	}

	private transient java.util.Date dataPosicao;

	/**
	    *
	    */
	public java.util.Date getDataPosicao() {
		return this.dataPosicao;
	}

	public void setDataPosicao(java.util.Date dataPosicao) {
		this.dataPosicao = dataPosicao;
	}

	private java.math.BigDecimal valorConfianca;

	/**
	    *
	    */
	public java.math.BigDecimal getValorConfianca() {
		return this.valorConfianca;
	}

	public void setValorConfianca(java.math.BigDecimal valorConfianca) {
		this.valorConfianca = valorConfianca;
	}

	private int prazo;

	/**
	    *
	    */
	public int getPrazo() {
		return this.prazo;
	}

	public void setPrazo(int prazo) {
		this.prazo = prazo;
	}

	private java.math.BigDecimal taxaLivreRisco;

	/**
	    *
	    */
	public java.math.BigDecimal getTaxaLivreRisco() {
		return this.taxaLivreRisco;
	}

	public void setTaxaLivreRisco(java.math.BigDecimal taxaLivreRisco) {
		this.taxaLivreRisco = taxaLivreRisco;
	}

	private java.math.BigDecimal taxaLivreRiscoExterna;

	/**
	    *
	    */
	public java.math.BigDecimal getTaxaLivreRiscoExterna() {
		return this.taxaLivreRiscoExterna;
	}

	public void setTaxaLivreRiscoExterna(java.math.BigDecimal taxaLivreRiscoExterna) {
		this.taxaLivreRiscoExterna = taxaLivreRiscoExterna;
	}

	private java.math.BigDecimal precoAtivoBase;

	/**
	    *
	    */
	public java.math.BigDecimal getPrecoAtivoBase() {
		return this.precoAtivoBase;
	}

	public void setPrecoAtivoBase(java.math.BigDecimal precoAtivoBase) {
		this.precoAtivoBase = precoAtivoBase;
	}

	private java.math.BigDecimal grauLiberdade;

	/**
	    *
	    */
	public java.math.BigDecimal getGrauLiberdade() {
		return this.grauLiberdade;
	}

	public void setGrauLiberdade(java.math.BigDecimal grauLiberdade) {
		this.grauLiberdade = grauLiberdade;
	}

	private java.math.BigDecimal premioEstimadoCall;

	/**
	    *
	    */
	public java.math.BigDecimal getPremioEstimadoCall() {
		return this.premioEstimadoCall;
	}

	public void setPremioEstimadoCall(java.math.BigDecimal premioEstimadoCall) {
		this.premioEstimadoCall = premioEstimadoCall;
	}

	private java.math.BigDecimal rangeInferiorCall;

	/**
	    *
	    */
	public java.math.BigDecimal getRangeInferiorCall() {
		return this.rangeInferiorCall;
	}

	public void setRangeInferiorCall(java.math.BigDecimal rangeInferiorCall) {
		this.rangeInferiorCall = rangeInferiorCall;
	}

	private java.math.BigDecimal rangeSuperiorCall;

	/**
	    *
	    */
	public java.math.BigDecimal getRangeSuperiorCall() {
		return this.rangeSuperiorCall;
	}

	public void setRangeSuperiorCall(java.math.BigDecimal rangeSuperiorCall) {
		this.rangeSuperiorCall = rangeSuperiorCall;
	}

	private java.math.BigDecimal premioEstimadoPut;

	/**
	    *
	    */
	public java.math.BigDecimal getPremioEstimadoPut() {
		return this.premioEstimadoPut;
	}

	public void setPremioEstimadoPut(java.math.BigDecimal premioEstimadoPut) {
		this.premioEstimadoPut = premioEstimadoPut;
	}

	private java.math.BigDecimal rangeInferiorPut;

	/**
	    *
	    */
	public java.math.BigDecimal getRangeInferiorPut() {
		return this.rangeInferiorPut;
	}

	public void setRangeInferiorPut(java.math.BigDecimal rangeInferiorPut) {
		this.rangeInferiorPut = rangeInferiorPut;
	}

	private java.math.BigDecimal rangeSuperiorPut;

	/**
	    *
	    */
	public java.math.BigDecimal getRangeSuperiorPut() {
		return this.rangeSuperiorPut;
	}

	public void setRangeSuperiorPut(java.math.BigDecimal rangeSuperiorPut) {
		this.rangeSuperiorPut = rangeSuperiorPut;
	}

	private java.math.BigDecimal taxaSuperior;

	/**
	    *
	    */
	public java.math.BigDecimal getTaxaSuperior() {
		return this.taxaSuperior;
	}

	public void setTaxaSuperior(java.math.BigDecimal taxaSuperior) {
		this.taxaSuperior = taxaSuperior;
	}

	private java.math.BigDecimal taxaInferior;

	/**
	    *
	    */
	public java.math.BigDecimal getTaxaInferior() {
		return this.taxaInferior;
	}

	public void setTaxaInferior(java.math.BigDecimal taxaInferior) {
		this.taxaInferior = taxaInferior;
	}

	private java.math.BigDecimal rangeAditivo;

	/**
	    *
	    */
	public java.math.BigDecimal getRangeAditivo() {
		return this.rangeAditivo;
	}

	public void setRangeAditivo(java.math.BigDecimal rangeAditivo) {
		this.rangeAditivo = rangeAditivo;
	}

	private java.lang.Long idDadosEntrada;

	/**
	    *
	    */
	public java.lang.Long getIdDadosEntrada() {
		return this.idDadosEntrada;
	}

	public void setIdDadosEntrada(java.lang.Long idDadosEntrada) {
		this.idDadosEntrada = idDadosEntrada;
	}

	private java.math.BigDecimal volatilidade;

	/**
	    *
	    */
	public java.math.BigDecimal getVolatilidade() {
		return this.volatilidade;
	}

	public void setVolatilidade(java.math.BigDecimal volatilidade) {
		this.volatilidade = volatilidade;
	}

	private java.math.BigDecimal volatilidadeInferior;

	/**
	    *
	    */
	public java.math.BigDecimal getVolatilidadeInferior() {
		return this.volatilidadeInferior;
	}

	public void setVolatilidadeInferior(java.math.BigDecimal volatilidadeInferior) {
		this.volatilidadeInferior = volatilidadeInferior;
	}

	private java.math.BigDecimal volatilidadeSuperior;

	/**
	    *
	    */
	public java.math.BigDecimal getVolatilidadeSuperior() {
		return this.volatilidadeSuperior;
	}

	public void setVolatilidadeSuperior(java.math.BigDecimal volatilidadeSuperior) {
		this.volatilidadeSuperior = volatilidadeSuperior;
	}

	private java.lang.String enquadrouCall;

	/**
	    *
	    */
	public java.lang.String getEnquadrouCall() {
		return this.enquadrouCall;
	}

	public void setEnquadrouCall(java.lang.String enquadrouCall) {
		this.enquadrouCall = enquadrouCall;
	}

	private java.lang.String enquadrouGlobal;

	/**
	    *
	    */
	public java.lang.String getEnquadrouGlobal() {
		return this.enquadrouGlobal;
	}

	public void setEnquadrouGlobal(java.lang.String enquadrouGlobal) {
		this.enquadrouGlobal = enquadrouGlobal;
	}

	private java.lang.String enquadrouPut;

	/**
	    *
	    */
	public java.lang.String getEnquadrouPut() {
		return this.enquadrouPut;
	}

	public void setEnquadrouPut(java.lang.String enquadrouPut) {
		this.enquadrouPut = enquadrouPut;
	}

	private int tamanhoAmostra;

	/**
	    *
	    */
	public int getTamanhoAmostra() {
		return this.tamanhoAmostra;
	}

	public void setTamanhoAmostra(int tamanhoAmostra) {
		this.tamanhoAmostra = tamanhoAmostra;
	}

	private java.math.BigDecimal desvioPadrao;

	/**
	    *
	    */
	public java.math.BigDecimal getDesvioPadrao() {
		return this.desvioPadrao;
	}

	public void setDesvioPadrao(java.math.BigDecimal desvioPadrao) {
		this.desvioPadrao = desvioPadrao;
	}

	private java.lang.String enquadrouTaxa;

	/**
	    *
	    */
	public java.lang.String getEnquadrouTaxa() {
		return this.enquadrouTaxa;
	}

	public void setEnquadrouTaxa(java.lang.String enquadrouTaxa) {
		this.enquadrouTaxa = enquadrouTaxa;
	}

	private java.lang.String avaliado;

	/**
	    *
	    */
	public java.lang.String getAvaliado() {
		return this.avaliado;
	}

	public void setAvaliado(java.lang.String avaliado) {
		this.avaliado = avaliado;
	}

	private java.math.BigDecimal volatilidadeAtivoBase;

	/**
	    *
	    */
	public java.math.BigDecimal getVolatilidadeAtivoBase() {
		return this.volatilidadeAtivoBase;
	}

	public void setVolatilidadeAtivoBase(java.math.BigDecimal volatilidadeAtivoBase) {
		this.volatilidadeAtivoBase = volatilidadeAtivoBase;
	}

}
