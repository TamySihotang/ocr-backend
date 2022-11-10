package com.danamon.vo;


import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class ResultPageVO extends ResultVO {

    private String pages;
    private String elements;

    public String getPages() {
        return pages;
    }

    public void setPages(String pages) {
        this.pages = pages;
    }

    public String getElements() {
        return elements;
    }

    public void setElements(String elements) {
        this.elements = elements;
    }

    public ResultPageVO() {
    }

    public ResultPageVO(String pages, String elements) {
        this.pages = pages;
        this.elements = elements;
    }

}
