package com.dathuynh.kishop.Model;


public class userBuyingEntity{
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getPhonenumber() {
        return phonenumber;
    }

    public void setPhonenumber(String phonenumber) {
        this.phonenumber = phonenumber;
    }

    public double getAccountBalance() {
        return accountBalance;
    }

    public void setAccountBalance(double accountBalance) {
        this.accountBalance = accountBalance;
    }

    public String getBuyinglocation() {
        return buyinglocation;
    }

    public void setBuyinglocation(String buyinglocation) {
        this.buyinglocation = buyinglocation;
    }

    public double getTotalorder() {
        return totalorder;
    }

    public void setTotalorder(double totalorder) {
        this.totalorder = totalorder;
    }

    public int id;
    public String token;
    public String phonenumber;
    public double accountBalance;
    public String buyinglocation;
    public double totalorder;
}