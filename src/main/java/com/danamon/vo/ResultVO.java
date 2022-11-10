package com.danamon.vo;

public class ResultVO {

    private String message;
    private Object result;

    public ResultVO() {
    }

    public ResultVO(String message) {
        this.message = message;
    }

    public ResultVO(String message, Object result) {
        this.message = message;
        this.result = result;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Object getResult() {
        return result;
    }

    public void setResult(Object result) {
        this.result = result;
    }

    @Override
    public String toString() {
        return "ResultVO{" +
                "message='" + message + '\'' +
                ", result=" + result +
                '}';
    }
}
