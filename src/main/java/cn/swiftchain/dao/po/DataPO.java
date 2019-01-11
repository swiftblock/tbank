package cn.swiftchain.dao.po;

public class DataPO extends BaseEntity<DataPO> {

	private static final long serialVersionUID = 1L;	
	
	private Long id;//   
	private String content;//   

	public Long getId() {
	    return this.id;
	}

	public void setId(Long id) {
	    this.id = id;
	}

	public String getContent() {
	    return this.content;
	}

	public void setContent(String content) {
	    this.content = content;
	}

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("DataPO [");   
        builder.append("id=");
        builder.append(id);
        builder.append(", content=");
        builder.append(content);
        builder.append("]");
        return builder.toString();
    }
}

