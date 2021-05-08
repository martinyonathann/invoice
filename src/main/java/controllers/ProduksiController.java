package controllers;

import connection.DatabaseConnection;
import helper.Helper;
import helper.Response;
import impelements.ProduksiImplement;
import interfaces.ProduksiInterface;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.awt.font.TextHitInfo;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ProduksiController {
    private final Helper hlp;
    private final ProduksiInterface service;

    public ProduksiController(){
        this.hlp = new Helper();
        this.service = new ProduksiImplement();
    }

    public String createHandler(JSONObject dataPayload){
        String result = "";
        try{
            dataPayload.get("pelanggan").toString();
            dataPayload.get("alamat_pelanggan").toString();
            dataPayload.get("kode_barang").toString();
            dataPayload.get("jumlah").toString();
        }catch (Exception ex){
            return Response.Pesan.CODE400BADREQUEST.resp(dataPayload).toString();
        }
        try {
            JSONObject jObjResp = hlp.unirest("http://localhost:8080/v1/product/items");
            JSONObject jObjData = new JSONObject((Map) jObjResp.get("data"));
            JSONArray jObjDataStatus = (JSONArray) jObjData.get("dataBarang");

            if (jObjDataStatus.size() < 1){
                return Response.Pesan.CODE404NOTFOUND.resp(null).toString();
            }
            JSONObject jObjDataBarang = new JSONObject();
            for (int i = 0; i< jObjDataStatus.size(); i++){
                JSONObject jObjArr =(JSONObject) jObjDataStatus.get(i);
                if (jObjArr.get("kode_barang").toString().equalsIgnoreCase(dataPayload.get("kode_barang").toString())){
                    jObjDataBarang = jObjArr;
                }
            }
            int jumlah = Integer.parseInt(dataPayload.get("jumlah").toString());
            int harga_barang = Integer.parseInt(jObjDataBarang.get("harga_barang").toString());

            int totalBayar = harga_barang * jumlah;
            dataPayload.put("harga_barang", totalBayar);

           JSONObject jObjResult = this.service.createInvoice(dataPayload);
            if (jObjResult.size() < 1){
                return Response.Pesan.CODE500INTERNALSERVEREROR.resp(null).toString();
            }
            JSONObject jObjDataInvoice  = this.service.getDataInvoice(jObjResult.get("invoice_code").toString());

            result = Response.Pesan.CODE200SUCCESS.resp(jObjDataInvoice).toString();
        }catch (Exception ex){
            return Response.Pesan.CODE500INTERNALSERVEREROR.resp(null).toString();
        }
        return  result;
    }

    public String paymentHandler(JSONObject dataPayload){
        String result = "";
        try{
            //validation mandatory request
            dataPayload.get("invoice_number").toString();
            dataPayload.get("metode_bayar").toString();
            dataPayload.get("bukti_bayar").toString();
        }catch (Exception ex){
            return Response.Pesan.CODE400BADREQUEST.resp(dataPayload).toString();
        }
        try {
            JSONObject jObjDataPayment = new JSONObject();
            jObjDataPayment.put("invoice_code", dataPayload.get("invoice_number").toString());
            jObjDataPayment.put("payment_method", dataPayload.get("metode_bayar").toString());

            JSONObject jObjDataInvoice  = this.service.getDataInvoice(dataPayload.get("invoice_number").toString());
            if (jObjDataInvoice.size() < 1){
                return Response.Pesan.CODE404NOTFOUND.resp(null).toString();
            }
            this.service.paymentInvoice(jObjDataPayment); //insert data payment invoice
            JSONObject jObjInvoicePay = this.service.getDataPaymentInvoice(dataPayload.get("invoice_number").toString());
            if (jObjInvoicePay.get("status_pembayaran").toString().equalsIgnoreCase("Lunas")){
                this.service.createProduct(dataPayload.get("invoice_number").toString()); //create product queue
            }
            jObjInvoicePay.put("data_invoice", jObjDataInvoice);
            result = Response.Pesan.CODE200SUCCESS.resp(jObjInvoicePay).toString();
        }catch (Exception ex){
            Logger.getLogger(DatabaseConnection.class.getName()).log(Level.SEVERE, "Something Error", ex);
        }
        return  result;
    }

    public String adviceHandler(String invoice_code){
        String result = "";
        try {
            JSONObject jObjDataAdvice = this.service.getDataProduct(invoice_code);
            if (jObjDataAdvice.size() < 1) {
                return Response.Pesan.CODE404NOTFOUND.resp(null).toString();
            }
            result = Response.Pesan.CODE200SUCCESS.resp(jObjDataAdvice).toString();
            } catch (Exception ex){
            return Response.Pesan.CODE500INTERNALSERVEREROR.resp(null).toString();
        }
        return  result;
    }

    public String updateAdvice(JSONObject dataPayload){
        String result = "";
        try{
            dataPayload.get("invoice_number").toString();
            dataPayload.get("catatan").toString();
            dataPayload.get("tanggal_produksi").toString();
            dataPayload.get("tanggal_selesai").toString();
        }catch (Exception ex){
            return Response.Pesan.CODE400BADREQUEST.resp(dataPayload).toString();
        }
        try {
            JSONObject jObjResp = hlp.unirest("http://localhost:8080/v1/product/status");
            JSONObject jObjData = new JSONObject((Map) jObjResp.get("data"));
            JSONArray jObjDataStatus = (JSONArray) jObjData.get("dataStatus");

            if (jObjDataStatus.size() < 1){
                return Response.Pesan.CODE404NOTFOUND.resp(null).toString();
            }
            for (int i = 0; i< jObjDataStatus.size(); i++){
                JSONObject jObjArr =(JSONObject) jObjDataStatus.get(i);
                if (jObjArr.get("stat_id").toString().equalsIgnoreCase(dataPayload.get("status_product_id").toString())){
                    dataPayload.put("status_product",jObjArr.get("stat_desc").toString());
                }
            }
            this.service.updateProductStatus(dataPayload);
            dataPayload.remove("status_product_id");
            result = Response.Pesan.CODE200SUCCESS.resp(dataPayload).toString();
        }catch (Exception ex){
            return Response.Pesan.CODE500INTERNALSERVEREROR.resp(null).toString();
        }
        return  result;
    }

    public String statusHandler (){
        String result = "";
        try{
            JSONArray dataStatus =  this.service.getStatusDetail();
            if (dataStatus.size() < 1){
                return Response.Pesan.CODE404NOTFOUND.resp(null).toString();
            }
            JSONObject jObjData = new JSONObject();
            jObjData.put("dataStatus",dataStatus);
            result = Response.Pesan.CODE200SUCCESS.resp(jObjData).toString();
        }catch (Exception ex){
            return Response.Pesan.CODE500INTERNALSERVEREROR.resp(null).toString();
        }
        return result;
    }

    public String paymentMethod (){
        String result = "";
        try{
            JSONArray dataStatus =  this.service.getPaymentMethod();
            if (dataStatus.size() < 1){
                return Response.Pesan.CODE404NOTFOUND.resp(null).toString();
            }
            JSONObject jObjData = new JSONObject();
            jObjData.put("dataMetode",dataStatus);
            result = Response.Pesan.CODE200SUCCESS.resp(jObjData).toString();
        }catch (Exception ex){
            return Response.Pesan.CODE500INTERNALSERVEREROR.resp(null).toString();
        }
        return result;
    }

    public String dataBarang (){
        String result = "";
        try{
            JSONArray dataStatus =  this.service.getDataBarang();
            if (dataStatus.size() < 1){
                return Response.Pesan.CODE404NOTFOUND.resp(null).toString();
            }
            JSONObject jObjData = new JSONObject();
            jObjData.put("dataBarang",dataStatus);
            result = Response.Pesan.CODE200SUCCESS.resp(jObjData).toString();
        }catch (Exception ex){
            return Response.Pesan.CODE500INTERNALSERVEREROR.resp(null).toString();
        }
        return result;
    }

    public String listProduksi (){
        String result = "";
        try{
            JSONArray dataStatus =  this.service.listProduksi();
            if (dataStatus.size() < 1){
                return Response.Pesan.CODE404NOTFOUND.resp(null).toString();
            }
            JSONObject jObjData = new JSONObject();
            jObjData.put("dataProduksi",dataStatus);
            result = Response.Pesan.CODE200SUCCESS.resp(jObjData).toString();
        }catch (Exception ex){
            return Response.Pesan.CODE500INTERNALSERVEREROR.resp(null).toString();
        }
        return result;
    }


}
