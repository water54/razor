package com.wbtech.ums.objects;

import java.io.Serializable;

public class AbstractReturnObject implements Serializable {

    private static final long serialVersionUID = 4049503783357712439L;
    public int code = 0;
    public String message = "";
    public String data;
    public String detail;

    public AbstractReturnObject(int code, String message, String data, String detail) {
        this.code = code;
        this.message = message;
        this.data = data;
        this.detail = detail;
    }

    public AbstractReturnObject(int code, String message) {
        this.code = code;
        this.message = message;

    }

    public AbstractReturnObject() {

    }

}
