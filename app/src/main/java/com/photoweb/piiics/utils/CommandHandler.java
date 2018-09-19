package com.photoweb.piiics.utils;

import android.util.Log;

import com.photoweb.piiics.PriceSecurityException;
import com.photoweb.piiics.model.AddressData;
import com.photoweb.piiics.model.Command;
import com.photoweb.piiics.model.DeliveryMethod;
import com.photoweb.piiics.model.EditorPic;
import com.photoweb.piiics.model.SummaryCategory;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.StringWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by dnizard on 29/08/2017.
 */

public class CommandHandler {
    private static final String TAG = "CommandHandler";

    private static CommandHandler instance;

    public Command currentCommand;

    //public int DeliveryPrice = 0;
    //public String DeliveryMode = "";
    public DeliveryMethod delivery;

    private XMLWriter xmlWriter;

    public HashMap<String, SummaryCategory> articles;
    private String productRange;

    private AddressData deliveryDict = new AddressData();
    private AddressData billingDict = new AddressData();
    private String relayId;

    private String email;

    public static CommandHandler get()
    {
        if (instance == null) instance = new CommandHandler();
        return instance;
    }

    //Initialization
    public void init(String commandID, String product)
    {
        currentCommand = new Command(commandID, product);
        relayId = "";

        if(currentCommand.getProduct().equals("PRINT")){
            productRange = "Eco";
        }else{
            productRange = "Style";
        }

        articles = new HashMap<String, SummaryCategory>();

        //xmlWriter = new XMLWriter(new StringWriter());

        /*try {
            xmlWriter.startXML();
        } catch (IOException e) {
            e.printStackTrace();
        }*/
    }

    public int getTotalPrice()
    {
        int deliveryPrice = 0;
        if(delivery != null){
            deliveryPrice = delivery.getPrice();
        }

        try {
            if (currentCommand.getProduct().equals("ALBUM")) {
                return currentCommand.getAllPicsPrice() + currentCommand.getAlbumOptions().getOptionsTotalPrice() + deliveryPrice;
            } else {
                return currentCommand.getAllPicsPrice() + deliveryPrice;
            }
        } catch (PriceSecurityException e) {
            e.printStackTrace();
            return 0;
        }
    }

    public AddressData getDeliveryDict() {
        return deliveryDict;
    }

    public AddressData getBillingDict() {
        return billingDict;
    }

    public void setDeliveryDict(AddressData data)
    {
        this.deliveryDict = data;
    }

    public void setBillingDict(AddressData data)
    {
        this.billingDict = data;
    }

    public String getProductRange() {
        return productRange;
    }

    public void setProductRange(String productRange) {
        this.productRange = productRange;
    }

    public String getRelayId() {
        return relayId;
    }

    public void setRelayId(String relayId) {
        this.relayId = relayId;
    }

    public String getCountryCodeWithDOM(String Code, String zip)
    {
        String cCode = Code;

        if(cCode.equals("FR")){
            if(zip.startsWith("972")){
                cCode = "MQ";
            }
            if(zip.startsWith("973")){
                cCode = "GY";
            }
            if(zip.startsWith("974")){
                cCode = "RE";
            }
            if(zip.startsWith("975")){
                cCode = "PM";
            }
            if(zip.startsWith("976")){
                cCode = "YT";
            }
            if(zip.startsWith("984")){
                cCode = "TF";
            }
            if(zip.startsWith("986")){
                cCode = "WF";
            }
            if(zip.startsWith("987")){
                cCode = "PF";
            }
            if(zip.startsWith("988")){
                cCode = "NC";
            }
        }

        return cCode;
    }

    public String getSaleAddress(String type)
    {
        String address = "";
        AddressData addressData = new AddressData();

        if(type.equals("billing")){
            addressData = billingDict;
        }else{
            addressData = deliveryDict;
        }

        address = addressData.getFirstName() + " " + addressData.getLastName();
        address = address + "<br>";
        address = address + addressData.getAddress();
        address = address + "<br>";
        address = address + addressData.getPostalCode() + " " + addressData.getCity();
        address = address + "<br>";
        address = address + addressData.getCountryCode();

        return address;
    }

    public void initUserAddress() throws IOException {
        xmlWriter = new XMLWriter(new StringWriter());
        xmlWriter.startXML();

        SimpleDateFormat sdfDate = new SimpleDateFormat("yyyy-MM-dd");
        String strDate = sdfDate.format(new Date());

        String billingStreet1 = "";
        String billingStreet2 = "";
        String deliveryStreet1 = "";
        String deliveryStreet2 = "";

        String[] billingAddress = billingDict.getAddress().split(" ");
        String[] deliveryAddres = deliveryDict.getAddress().split(" ");

        Boolean switchOn2 = false;

        for (String sub : billingAddress) {
            if(switchOn2){
                billingStreet2 = billingStreet2 + " " + sub;
            }else{
                if(billingStreet1.length() + sub.length() < 35){
                    billingStreet1 = billingStreet1 + " " + sub;
                }else{
                    switchOn2 = true;

                    billingStreet2 = billingStreet2 + " " + sub;
                }
            }
        }

        switchOn2 = false;

        for (String sub : deliveryAddres) {
            if(switchOn2){
                deliveryStreet2 = deliveryStreet2 + " " + sub;
            }else{
                if(deliveryStreet1.length() + sub.length() < 35){
                    deliveryStreet1 = deliveryStreet1 + " " + sub;
                }else{
                    switchOn2 = true;

                    deliveryStreet2 = deliveryStreet2 + " " + sub;
                }
            }
        }

        xmlWriter.element("pwb:order")
                    .attribute("version", "1.0")
                    .attribute("orderReference", currentCommand.getCommandID())
                    .attribute("orderDate", strDate)
                    .attribute("xmlns:pwb", "http://atelier.photoweb.fr/api/orders/v1")
                    .attribute("xmlns:xsi", "http://www.w3.org/2001/XMLSchema-instance")
                    .attribute("xsi:schemaLocation", "http://atelier.photoweb.fr/api/orders/v1 order.xsd");

        xmlWriter.element("pwb:customer")
                    .attribute("lastName", billingDict.getLastName())
                    .attribute("firstName", billingDict.getFirstName())
                    .attribute("emailAddress", UserInfo.getUserEmail())
                    .attribute("civility", billingDict.getCivility())
                    .pop();

        xmlWriter.element("pwb:billingAddress")
                    .attribute("city", billingDict.getCity())
                    .attribute("postCode", billingDict.getPostalCode())
                    .attribute("street1", billingStreet1)
                    .attribute("countryCode", getCountryCodeWithDOM(billingDict.getCountryCode(), billingDict.getPostalCode()))
                    .attribute("lastName", billingDict.getLastName())
                    .attribute("firstName", billingDict.getFirstName())
                    .attribute("emailAddress", UserInfo.getUserEmail())
                    .attribute("phoneNumber", "0600000000");

        if(!billingStreet2.equals(""))
            xmlWriter.attribute("street2", billingStreet2);

        xmlWriter.pop();

        xmlWriter.element("pwb:shipments");

        xmlWriter.element("pwb:shipment")
                    .attribute("carrier", delivery.getIdentifier());

        xmlWriter.element("pwb:shippingAddress");

        if(delivery.getIdentifier().equals("MRPRL"))
        {
            xmlWriter.attribute("deliveryPointId", relayId);
        }

        xmlWriter.attribute("city", deliveryDict.getCity())
                .attribute("postCode", deliveryDict.getPostalCode())
                .attribute("street1", deliveryStreet1)
                .attribute("countryCode", getCountryCodeWithDOM(deliveryDict.getCountryCode(), deliveryDict.getPostalCode()))
                .attribute("lastName", deliveryDict.getLastName())
                .attribute("firstName", deliveryDict.getFirstName())
                .attribute("emailAddress", UserInfo.getUserEmail())
                .attribute("phoneNumber", "0600000000");

        if(!deliveryStreet2.equals(""))
            xmlWriter.attribute("street2", deliveryStreet2);

        xmlWriter.pop();

    }

    public String getCommandFile()
    {
        JSONObject jsonObject = new JSONObject();

        ArrayList<EditorPic> list = new ArrayList<>();

        list.addAll(currentCommand.getEditorPics());

        if(currentCommand.getProduct().equals("ALBUM")){
            list.add(currentCommand.getAlbumFrontCover());
            list.add(currentCommand.getAlbumBackCover());
        }

        Collections.sort(list, new Comparator<EditorPic>() {
            @Override
            public int compare(EditorPic t0, EditorPic t1) {
                return t0.getIndex() - t1.getIndex();
            }

        });

        for (EditorPic pic:list) {
            try {
                File f = new File(pic.getFinalBitmapPath());
                jsonObject.put(f.getName(), pic.getBackgroundReference().getBackgroundFile().getName());
            } catch (JSONException e) {
                e.printStackTrace();
            } catch (NullPointerException e){
                e.printStackTrace();
            }
        }

        Log.d(TAG, jsonObject.toString());

        String fileName = currentCommand.getCommandRootDirectoryPath() + "/" + currentCommand.getCommandID() + ".txt";

        FileWriter fileWriter = null;
        File file = new File(fileName);

        // if file doesn't exists, then create it
        if (file.exists()) {
            file.delete();
        }

        try {
            file.createNewFile();

            fileWriter = new FileWriter(file);
            fileWriter.write(jsonObject.toString());
            fileWriter.close();

        } catch (IOException e) {
            e.printStackTrace();
        }

        return fileName;
    }

    private void deleteRecursive(File fileOrDirectory) {

        if (fileOrDirectory.isDirectory()) {
            for (File child : fileOrDirectory.listFiles()) {
                deleteRecursive(child);
            }
        }

        fileOrDirectory.delete();
    }

    public void deleteCurrentCommand()
    {
        File f = new File(DraftsUtils.getPrintDirectoryPath(), currentCommand.getCommandID());
        deleteRecursive(f);
    }

    public void initArticles() throws IOException {

        xmlWriter.element("pwb:articles");

        for(Map.Entry<String, SummaryCategory> entry : articles.entrySet()) {
            String key = entry.getKey();
            SummaryCategory value = entry.getValue();

            if(value.pics.size() > 0){
                String totalPrice = "1.0";
                if(currentCommand.getProduct().equals("PRINT")){

                    int count = 0;

                    for (EditorPic pics:value.pics) {
                        if(pics.getCopy() == 1){
                            count++;
                        }
                    }

                    totalPrice = count + ".0";
                }else if(currentCommand.getAlbumOptions().isHasStrongCover()){
                    totalPrice = "1.2";
                    productRange = "Prestige";
                }

                xmlWriter.element("pwb:article")
                        .attribute("quantity", (currentCommand.getProduct().equals("PRINT")) ? "1" : currentCommand.getAlbumOptions().getBookQuantity())
                        .attribute("unitPrice", "1.0")
                        .attribute("totalPrice", totalPrice)
                        .attribute("productReference", (currentCommand.getProduct().equals("PRINT")) ? "Print" : "Book")
                        .attribute("productRange", productRange)
                        .attribute("productFormat", (currentCommand.getProduct().equals("PRINT")) ? key : "M");

                if(currentCommand.getProduct().equals("ALBUM")){
                    xmlWriter.element("pwb:finishes");

                    xmlWriter.element("pwb:finish")
                            .attribute("type", "BlocFinish")
                            .attribute("value", currentCommand.getAlbumOptions().isHasVarnishedPages() ? "Varnished" : "NotVarnished")
                            .pop();
                    xmlWriter.element("pwb:finish")
                            .attribute("type", "CoverFinish")
                            .attribute("value", currentCommand.getAlbumOptions().isHasMateCover() ? "NotShiny" : "Shiny")
                            .pop();

                    if(currentCommand.getAlbumOptions().isHasStrongCover()){
                        xmlWriter.element("pwb:finish")
                                .attribute("type", "SpineFinish")
                                .attribute("value", "9mmBack")
                                .pop();
                    }

                    //Close pwb:finishes
                    xmlWriter.pop();

                }

                xmlWriter.element("pwb:attachments");

                int count = 1;

                for (EditorPic pic:value.pics) {

                    if(pic.getCopy() == 1){
                        String elementName = "";
                        String elementComponent = "";

                        if(currentCommand.getProduct().equals("PRINT")){
                            elementName = pic.getPhotoID() + ".jpg";
                            elementComponent = "Print";
                        }else{
                            elementName = "page" + count + ".pdf";
                            elementComponent = "Bloc";
                        }

                        xmlWriter.element("pwb:attachment")
                                .attribute("name", elementName)
                                .attribute("component", elementComponent)
                                .pop();

                        count = count + 1;
                    }


                }

                if(currentCommand.getProduct().equals("ALBUM")){
                    if(count%2 == 0){
                        xmlWriter.element("pwb:attachment")
                                .attribute("name", "page" + count + ".pdf")
                                .attribute("component", "Bloc")
                                .pop();
                    }

                    if(currentCommand.getAlbumOptions().isHasStrongCover()){
                        xmlWriter.element("pwb:attachment")
                                .attribute("name", "cover.jpg")
                                .attribute("component", "Cover")
                                .pop();
                    }else{
                        xmlWriter.element("pwb:attachment")
                                .attribute("name", "cover1.pdf")
                                .attribute("component", "Cover Face")
                                .pop();

                        xmlWriter.element("pwb:attachment")
                                .attribute("name", "cover2.pdf")
                                .attribute("component", "Cover Back")
                                .pop();

                        xmlWriter.element("pwb:attachment")
                                .attribute("name", "tranche.pdf")
                                .attribute("component", "Cover Spine")
                                .pop();
                    }


                }

                //Close pwb:attachments
                xmlWriter.pop();

                //Close pwb:article
                xmlWriter.pop();

                if(currentCommand.getProduct().equals("PRINT"))
                {
                    for (EditorPic pic:value.pics){
                        if(pic.getCopy() > 1){
                            xmlWriter.element("pwb:article")
                                    .attribute("quantity", pic.getCopy())
                                    .attribute("unitPrice", "1.0")
                                    .attribute("totalPrice", pic.getCopy()+".0")
                                    .attribute("productReference", "Print")
                                    .attribute("productRange", productRange)
                                    .attribute("productFormat", key);

                            xmlWriter.element("pwb:attachments");

                            xmlWriter.element("pwb:attachment")
                                    .attribute("name", pic.getPhotoID() + ".jpg")
                                    .attribute("component", "Print")
                                    .pop();

                            //Close pwb:attachments
                            xmlWriter.pop();

                            //Close pwb:article
                            xmlWriter.pop();
                        }
                    }
                }
            }



        }

    }

    public void finishXML() throws IOException {
        //Close pwb:articles
        xmlWriter.pop();

        //Close pwb:shipment
        xmlWriter.pop();

        //Close pwb:shipments
        xmlWriter.pop();

        //Close pwb:order
        xmlWriter.pop();

    }

    public String getFinalXMl()
    {
        System.out.println(xmlWriter.getWriter().toString());
        String fileName = currentCommand.getCommandRootDirectoryPath() + "/order.xml";

        FileWriter fileWriter = null;
        File file = new File(fileName);

        // if file doesn't exists, then create it
        if (file.exists()) {
            file.delete();
        }

        try {
            file.createNewFile();

            fileWriter = new FileWriter(file);
            fileWriter.write(xmlWriter.getWriter().toString());
            fileWriter.close();

        } catch (IOException e) {
            e.printStackTrace();
        }

        return fileName;

    }
}
