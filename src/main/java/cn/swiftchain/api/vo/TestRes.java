package cn.swiftchain.api.vo;

import lombok.Data;

@Data
public class TestRes {
    private long id;
    private String content;

    public TestRes(long id, String content) {
        this.id = id;
        this.content = content;
    }
}
