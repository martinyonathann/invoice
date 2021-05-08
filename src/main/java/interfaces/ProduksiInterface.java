package interfaces;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

public interface ProduksiInterface {

    JSONObject createInvoice(JSONObject dataInvoice);

    JSONObject getDataInvoice(String invoiceCode);

    JSONObject getDataPaymentInvoice (String invoiceCode);

    void paymentInvoice(JSONObject dataPayment);

    void createProduct (String invoiceCode);

    JSONObject getDataProduct(String invoiceCode);

    void updateProductStatus(JSONObject jObjData);

    JSONArray getStatusDetail ();

    JSONArray getPaymentMethod ();

    JSONArray getDataBarang();

    JSONArray listProduksi();

}
