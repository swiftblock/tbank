package cn.swiftchain.dao.po;import java.math.BigDecimal;import java.util.Date;public class OfferPO extends BaseEntity<OfferPO> {	private static final long serialVersionUID = 1L;			private String offerNo;//   报价编号	private String status;//   状态码	private BigDecimal priceTrx;//   TRX单价	private BigDecimal priceVena;//   VENA单价	private Date startTime;//   报价起始时间	private Date endTime;//   报价截止时间	public String getOfferNo() {	    return this.offerNo;	}	public void setOfferNo(String offerNo) {	    this.offerNo = offerNo;	}	public String getStatus() {	    return this.status;	}	public void setStatus(String status) {	    this.status = status;	}	public BigDecimal getPriceTrx() {	    return this.priceTrx;	}	public void setPriceTrx(BigDecimal priceTrx) {	    this.priceTrx = priceTrx;	}	public BigDecimal getPriceVena() {	    return this.priceVena;	}	public void setPriceVena(BigDecimal priceVena) {	    this.priceVena = priceVena;	}	public Date getStartTime() {	    return this.startTime;	}	public void setStartTime(Date startTime) {	    this.startTime = startTime;	}	public Date getEndTime() {	    return this.endTime;	}	public void setEndTime(Date endTime) {	    this.endTime = endTime;	}    @Override    public String toString() {        StringBuilder builder = new StringBuilder();        builder.append("OfferPO [");           builder.append("offerNo=");        builder.append(offerNo);        builder.append(", status=");        builder.append(status);        builder.append(", priceTrx=");        builder.append(priceTrx);        builder.append(", priceVena=");        builder.append(priceVena);        builder.append(", startTime=");        builder.append(startTime);        builder.append(", endTime=");        builder.append(endTime);        builder.append("]");        return builder.toString();    }}