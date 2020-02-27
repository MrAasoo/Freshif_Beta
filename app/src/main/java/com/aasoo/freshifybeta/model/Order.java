package com.aasoo.freshifybeta.model;

public class Order {
    private String order_no;
    private String order_by_user;
    private String order_model_no;
    private String order_quantity;
    private String order_item_price;
    private String order_date;
    private String order_time;
    private String order_total_price;
    private String order_address;
    private String order_name;
    private String order_mobile;
    private String order_due;
    private String order_advance;

    public Order() {
        this.order_no = order_no;
        this.order_by_user = order_by_user;
        this.order_model_no = order_model_no;
        this.order_quantity = order_quantity;
        this.order_item_price = order_item_price;
        this.order_date = order_date;
        this.order_time = order_time;
        this.order_total_price = order_total_price;
        this.order_address = order_address;
        this.order_name = order_name;
        this.order_mobile = order_mobile;
        this.order_due = order_due;
        this.order_advance = order_advance;
    }


    public String getOrder_due() {
        return order_due;
    }

    public void setOrder_due(String order_due) {
        this.order_due = order_due;
    }

    public String getOrder_advance() {
        return order_advance;
    }

    public void setOrder_advance(String order_advance) {
        this.order_advance = order_advance;
    }

    public String getOrder_no() {
        return order_no;
    }

    public void setOrder_no(String order_no) {
        this.order_no = order_no;
    }

    public String getOrder_by_user() {
        return order_by_user;
    }

    public void setOrder_by_user(String order_by_user) {
        this.order_by_user = order_by_user;
    }

    public String getOrder_model_no() {
        return order_model_no;
    }

    public void setOrder_model_no(String order_model_no) {
        this.order_model_no = order_model_no;
    }

    public String getOrder_quantity() {
        return order_quantity;
    }

    public void setOrder_quantity(String order_quantity) {
        this.order_quantity = order_quantity;
    }

    public String getOrder_item_price() {
        return order_item_price;
    }

    public void setOrder_item_price(String order_item_price) {
        this.order_item_price = order_item_price;
    }
    public String getOrder_date() {
        return order_date;
    }

    public void setOrder_date(String order_date) {
        this.order_date = order_date;
    }

    public String getOrder_time() {
        return order_time;
    }

    public void setOrder_time(String order_time) {
        this.order_time = order_time;
    }

    public String getOrder_total_price() {
        return order_total_price;
    }

    public void setOrder_total_price(String order_total_price) {
        this.order_total_price = order_total_price;
    }

    public String getOrder_address() {
        return order_address;
    }

    public void setOrder_address(String order_address) {
        this.order_address = order_address;
    }

    public String getOrder_name() {
        return order_name;
    }

    public void setOrder_name(String order_name) {
        this.order_name = order_name;
    }

    public String getOrder_mobile() {
        return order_mobile;
    }

    public void setOrder_mobile(String order_mobile) {
        this.order_mobile = order_mobile;
    }
}
