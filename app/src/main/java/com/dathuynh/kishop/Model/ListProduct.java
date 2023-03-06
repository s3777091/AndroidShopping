package com.dathuynh.kishop.Model;


import java.util.ArrayList;
import java.util.List;


public class ListProduct {

    public ArrayList<ProductDetail> getResults() {
        return results;
    }
    public void setResults(ArrayList<ProductDetail> results) {
        this.results = results;
    }


    public ArrayList<ProductDetail> results;

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    boolean success;
    public String status;
}

