package com.dathuynh.kishop.Model;

import java.util.ArrayList;
import java.util.List;

public class OrderModelList {

    boolean success;
    ArrayList<OrderModel> result;
    String status;

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

    public ArrayList<OrderModel> getResult() {
        return result;
    }

    public void setResult(ArrayList<OrderModel> result) {
        this.result = result;
    }
}
