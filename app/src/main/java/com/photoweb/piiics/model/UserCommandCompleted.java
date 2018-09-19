package com.photoweb.piiics.model;

import android.util.Log;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.photoweb.piiics.PriceSecurityException;
import com.photoweb.piiics.R;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;


/**
 * Created by thomas on 06/09/2017.
 */

public class UserCommandCompleted {
    private static final String LOG_TAG = "UserCommandCompleted";
    @SerializedName("id")
    @Expose
    private int id;

    @SerializedName("identifier")
    @Expose
    private String identifier;

    @SerializedName("date")
    @Expose
    private String date;

    @SerializedName("userid")
    @Expose
    private int userid;

    @SerializedName("product")
    @Expose
    private String product;

    @SerializedName("amount")
    @Expose
    private String amount;

    @SerializedName("free")
    @Expose
    private int free;

    @SerializedName("status")
    @Expose
    private String status;

    @SerializedName("acceptance")
    @Expose
    private String acceptance;

    @SerializedName("promocode")
    @Expose
    private String promocode;

    @SerializedName("deliveryAddress")
    @Expose
    private String deliveryAddress;

    @SerializedName("shipmentTrackURL")
    @Expose
    private String shipmentTrackURL;

    @SerializedName("items")
    @Expose
    private ArrayList<Item> items;

    private String commandTitle;

    public String getCommandTitle() {
        return commandTitle;
    }

    public void setCommandTitle(String commandTitle) {
        this.commandTitle = commandTitle;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getIdentifier() {
        return identifier;
    }

    public void setIdentifier(String identifier) {
        this.identifier = identifier;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public int getUserid() {
        return userid;
    }

    public void setUserid(int userid) {
        this.userid = userid;
    }

    public String getProduct() {
        return product;
    }

    public void setProduct(String product) {
        this.product = product;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public int getFree() {
        return free;
    }

    public void setFree(int free) {
        this.free = free;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getAcceptance() {
        return acceptance;
    }

    public void setAcceptance(String acceptance) {
        this.acceptance = acceptance;
    }

    public String getPromocode() {
        return promocode;
    }

    public void setPromocode(String promocode) {
        this.promocode = promocode;
    }

    public ArrayList<Item> getItems() {
        return items;
    }

    public void setItems(ArrayList<Item> items) {
        this.items = items;
    }

    public String getFormattedDate() {
        String formattedDate = null;
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'hh:mm:ss");
            Date dateToFormat = sdf.parse(this.date);

            SimpleDateFormat outputFormat = new SimpleDateFormat("dd MMMM yyyy");
            formattedDate = outputFormat.format(dateToFormat);

            Log.d(LOG_TAG, "Got the date: " + formattedDate);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return formattedDate;
    }

    public Date getRealDate() {
        Date realDate = null;
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'hh:mm:ss");
            realDate = sdf.parse(this.date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return realDate;
    }

    public String getFormattedPrice() {
        int priceInCts = 0;

        String[] strings = this.amount.split("[.]");

        String euros = strings[0];
        String cents = strings[1];

        if (cents.length() == 1) {
            cents = cents + "0";
        }

        return euros + "." + cents + "€";
    }

    public int getFormattedStatus() {
        if (status.equals("payed")) {
            return R.string.PENDING;
        } else if (status.equals("send")) {
            return R.string.SENT;
        } else {
            return R.string.PENDING;
        }
    }

    public String getDeliveryAddress() {
        return deliveryAddress;
    }

    public void setDeliveryAddress(String deliveryAddress) {
        this.deliveryAddress = deliveryAddress;
    }

    public String getShipmentTrackURL() {
        return shipmentTrackURL;
    }

    public void setShipmentTrackURL(String shipmentTrackURL) {
        this.shipmentTrackURL = shipmentTrackURL;
    }

    public class Item {

        @SerializedName("id")
        @Expose
        private int id;

        @SerializedName("name")
        @Expose
        private String name;

        @SerializedName("price")
        @Expose
        private String price;

        @SerializedName("sale_id")
        @Expose
        private int sale_id;

        @SerializedName("quantity")
        @Expose
        private int quantity;

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getPrice() {
            return price;
        }

        public void setPrice(String price) {
            this.price = price;
        }

        public int getSale_id() {
            return sale_id;
        }

        public void setSale_id(int sale_id) {
            this.sale_id = sale_id;
        }

        public int getQuantity() {
            return quantity;
        }

        public void setQuantity(int quantity) {
            this.quantity = quantity;
        }

        @Override
        public String toString() {
            return "Item{" +
                    "id=" + id +
                    ", name='" + name + '\'' +
                    ", price=" + price +
                    ", sale_id=" + sale_id +
                    ", quantity=" + quantity +
                    '}';
        }
    }

    @Override
    public String toString() {
        String str = "UserCommandCompleted{" +
                "id=" + id +
                ", identifier='" + identifier + '\'' +
                ", date='" + date + '\'' +
                ", userid=" + userid +
                ", product='" + product + '\'' +
                ", amount='" + amount + '\'' +
                ", free=" + free +
                ", status='" + status + '\'' +
                ", acceptance='" + acceptance + '\'' +
                ", promocode='" + promocode + '\'' +
                ", items : \n";
                for (Item item : items) {
                    str += "    " + item.toString() + "\n";
                }
                str += '}';
        return str;
    }
}
