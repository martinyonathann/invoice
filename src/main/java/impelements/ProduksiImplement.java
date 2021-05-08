package impelements;

import connection.DatabaseConnection;
import helper.Helper;
import interfaces.ProduksiInterface;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.sql.*;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ProduksiImplement implements ProduksiInterface {
    private final Helper hlp;
    public static final int LUNAS = 1;
    public static final int PENDING = 2;
    public static final int BATAL = 3;

    public ProduksiImplement(){
        this.hlp = new Helper();
    }


    @Override
    public JSONObject createInvoice(JSONObject dataInvoice) {
        DatabaseConnection databaseConnection = new DatabaseConnection();
        Connection conn = null;
        ResultSet rs = null;
        PreparedStatement st = null;
        JSONObject jObjResult = new JSONObject();
        try {
            conn = databaseConnection.getConnection();
            String query = "WITH invoice_insert AS (\n" +
                    "    INSERT INTO public.tbl_invoice(receiver, receiver_address, \"createdAt\")" +
                    " VALUES( ?, ?, ?) returning invoice_code\n" +
                    "), detail_insert as (" +
                    "INSERT INTO public.tbl_invoicedetail (item_code, \"createdAt\", qty, total, invoice_code) \n" +
                    "VALUES(?, ?, ?, ?, (SELECT invoice_code FROM invoice_insert)))\n" +
                    "SELECT invoice_code  FROM invoice_insert ";
            st = conn.prepareStatement(query);
            st.setString(1, dataInvoice.get("pelanggan").toString());
            st.setString(2, dataInvoice.get("alamat_pelanggan").toString());
            st.setTimestamp(3, this.hlp.getDate("yyyy-MM-dd HH:mm:ss"));
            st.setString(4, dataInvoice.get("kode_barang").toString());
            st.setTimestamp(5, this.hlp.getDate("yyyy-MM-dd HH:mm:ss"));
            st.setInt(6, Integer.parseInt(dataInvoice.get("jumlah").toString()));
            st.setInt(7, Integer.parseInt(dataInvoice.get("harga_barang").toString()));
            rs = st.executeQuery();
            ResultSetMetaData metaData = rs.getMetaData();
            int count = metaData.getColumnCount();

            while(rs.next()){
                for (int i = 1; i <= count; i++){
                    if (rs.getObject(i) == null){
                        jObjResult.put(metaData.getColumnLabel(i), "");
                    }else{
                        jObjResult.put(metaData.getColumnLabel(i), rs.getString(i));
                    }
                }
            }

        }catch (Exception ex){
            Logger.getLogger(DatabaseConnection.class.getName()).log(Level.SEVERE, "Somethings Error", ex);
        } finally {
            try {
                if (st != null) {
                    st.close();
                }
                if (rs != null){
                    rs.close();
                }
            } catch (SQLException ex) {
                Logger.getLogger(DatabaseConnection.class.getName()).log(Level.SEVERE, "Error", ex);
            }
        }
        return jObjResult;
    }

    @Override
    public JSONObject getDataInvoice(String invoiceCode) {
        DatabaseConnection databaseConnection = new DatabaseConnection();
        Connection conn = null;
        ResultSet rs = null;
        PreparedStatement st = null;
        JSONObject jObjResult = new JSONObject();
        try {
            conn = databaseConnection.getConnection();
            String query = "SELECT ti.invoice_code AS \"invoice_number\", \n" +
                    "ti2.item_code AS \"kode_barang\", ti3.item_name as \"nama_barang\", \n" +
                    "ti3.item_price as \"harga_barang\", ti2.qty as \"jumlah\", \n" +
                    "ti2.total as \"total\", ti2.\"createdAt\" as \"tanggal_pesanan\" , \n" +
                    "ti.receiver as \"penerima\", ti.receiver_address as \"alamat_penerima\" \n" +
                    "FROM tbl_invoice ti \n" +
                    "JOIN tbl_invoicedetail ti2 \n" +
                    "ON  ti.invoice_code  = ti2.invoice_code\n" +
                    "JOIN tbl_item ti3  ON ti2.item_code  = ti3.item_code\n" +
                    "WHERE ti.invoice_code = ?";
            st = conn.prepareStatement(query);
            st.setString(1, invoiceCode);
            rs = st.executeQuery();
            ResultSetMetaData metaData = rs.getMetaData();
            int count = metaData.getColumnCount();

            while(rs.next()){
                for (int i = 1; i <= count; i++){
                    if (rs.getObject(i) == null){
                        jObjResult.put(metaData.getColumnLabel(i), "");
                    }else{
                        jObjResult.put(metaData.getColumnLabel(i), rs.getString(i));
                    }
                }
            }
        }catch (Exception ex){
            Logger.getLogger(DatabaseConnection.class.getName()).log(Level.SEVERE, "Somethings Error", ex);
        } finally {
            try {
                if (st != null) {
                    st.close();
                }
                if (rs != null){
                    rs.close();
                }
            } catch (SQLException ex) {
                Logger.getLogger(DatabaseConnection.class.getName()).log(Level.SEVERE, "Error", ex);
            }
        }
        return jObjResult;
    }

    @Override
    public JSONObject getDataPaymentInvoice(String invoiceCode) {
        DatabaseConnection databaseConnection = new DatabaseConnection();
        Connection conn = null;
        ResultSet rs = null;
        PreparedStatement st = null;
        JSONObject jObjResult = new JSONObject();
        try {
            conn = databaseConnection.getConnection();
            String query = "select tpi.invoice_number , tpm.method_desc as \"metode_pembayaran\", tps.status_desc as \"status_pembayaran\", tpi.tanggal_pembayaran\n" +
                    "    from tbl_payment_invoice tpi \n" +
                    "    join tbl_payment_method tpm \n" +
                    "    on tpi.payment_method_id = tpm.method_id\n" +
                    "    join tbl_payment_status tps on tpi.payment_status_id = tps.status_id \n" +
                    "    where invoice_number = ?";
            st = conn.prepareStatement(query);
            st.setString(1, invoiceCode);
            rs = st.executeQuery();
            ResultSetMetaData metaData = rs.getMetaData();
            int count = metaData.getColumnCount();

            while(rs.next()){
                for (int i = 1; i <= count; i++){
                    if (rs.getObject(i) == null){
                        jObjResult.put(metaData.getColumnLabel(i), "");
                    }else{
                        jObjResult.put(metaData.getColumnLabel(i), rs.getString(i));
                    }
                }
            }
        }catch (Exception ex){
            Logger.getLogger(DatabaseConnection.class.getName()).log(Level.SEVERE, "Somethings Error", ex);
        } finally {
            try {
                if (st != null) {
                    st.close();
                }
                if (rs != null){
                    rs.close();
                }
            } catch (SQLException ex) {
                Logger.getLogger(DatabaseConnection.class.getName()).log(Level.SEVERE, "Error", ex);
            }
        }
        return jObjResult;
    }

    @Override
    public void paymentInvoice(JSONObject dataPayment) {
        DatabaseConnection databaseConnection = new DatabaseConnection();
        Connection conn = null;
        ResultSet rs = null;
        PreparedStatement st = null;
        try {
            conn = databaseConnection.getConnection();
            String query = "INSERT INTO public.tbl_payment_invoice\n" +
                    "(invoice_number, payment_method_id, payment_status_id, tanggal_pembayaran)\n" +
                    "VALUES(?, ?, ?, ?)";
            st = conn.prepareStatement(query);
            st.setString(1, dataPayment.get("invoice_code").toString());
            st.setInt(2, Integer.parseInt(dataPayment.get("payment_method").toString()));
            st.setInt(3,LUNAS);
            st.setTimestamp(4, this.hlp.getDate("yyyy-MM-dd HH:mm:ss"));
            st.executeUpdate();
        }catch (Exception ex){
            Logger.getLogger(DatabaseConnection.class.getName()).log(Level.SEVERE, "Somethings Error", ex);
        } finally {
            try {
                if (st != null) {
                    st.close();
                }
                if (rs != null){
                    rs.close();
                }
            } catch (SQLException ex) {
                Logger.getLogger(DatabaseConnection.class.getName()).log(Level.SEVERE, "Error", ex);
            }
        }
    }

    @Override
    public void createProduct(String invoiceCode) {
        DatabaseConnection databaseConnection = new DatabaseConnection();
        Connection conn = null;
        ResultSet rs = null;
        PreparedStatement st = null;
        try {
            conn = databaseConnection.getConnection();
            String query = "INSERT INTO public.tbl_product_status\n" +
                    "(invoice_number, status_product, catatan, tanggal_masuk, tanggal_produksi, tanggal_selesai)\n" +
                    "VALUES(?, 1, 'Design Proccess', ?, null, null);";
            st = conn.prepareStatement(query);
            st.setString(1, invoiceCode);
            st.setTimestamp(2, this.hlp.getDate("yyyy-MM-dd HH:mm:ss"));
            st.executeUpdate();
        }catch (Exception ex){
            Logger.getLogger(DatabaseConnection.class.getName()).log(Level.SEVERE, "Somethings Error", ex);
        } finally {
            try {
                if (st != null) {
                    st.close();
                }
                if (rs != null){
                    rs.close();
                }
            } catch (SQLException ex) {
                Logger.getLogger(DatabaseConnection.class.getName()).log(Level.SEVERE, "Error", ex);
            }
        }
    }

    @Override
    public JSONObject getDataProduct(String invoiceCode) {
        DatabaseConnection databaseConnection = new DatabaseConnection();
        Connection conn = null;
        ResultSet rs = null;
        PreparedStatement st = null;
        JSONObject jObjResult = new JSONObject();
        try {
            conn = databaseConnection.getConnection();
            String query = "SELECT tps.invoice_number,tsp.stat_desc AS status,tps.catatan, tps.tanggal_masuk, tps.tanggal_produksi, tps.tanggal_selesai \n" +
                    "FROM tbl_product_status tps\n" +
                    "JOIN tbl_stat_product tsp on tps.status_product = tsp.stat_id \n" +
                    "WHERE invoice_number = ?;";
            st = conn.prepareStatement(query);
            st.setString(1, invoiceCode);
            rs = st.executeQuery();
            ResultSetMetaData metaData = rs.getMetaData();
            int count = metaData.getColumnCount();
            while(rs.next()){
                for (int i = 1; i <= count; i++){
                    if (rs.getObject(i) == null){
                        jObjResult.put(metaData.getColumnLabel(i), "not yet entered");
                    }else{
                        jObjResult.put(metaData.getColumnLabel(i), rs.getString(i));
                    }
                }
            }
        }catch (Exception ex){
            Logger.getLogger(DatabaseConnection.class.getName()).log(Level.SEVERE, "Somethings Error", ex);
        } finally {
            try {
                if (st != null) {
                    st.close();
                }
                if (rs != null){
                    rs.close();
                }
            } catch (SQLException ex) {
                Logger.getLogger(DatabaseConnection.class.getName()).log(Level.SEVERE, "Error", ex);
            }
        }
        return jObjResult;
    }

    @Override
    public void updateProductStatus(JSONObject jObjData) {
        DatabaseConnection databaseConnection = new DatabaseConnection();
        Connection conn = null;
        ResultSet rs = null;
        PreparedStatement st = null;
        try {
            conn = databaseConnection.getConnection();
            String query = "UPDATE public.tbl_product_status\n" +
                    "SET status_product=?, catatan= ?, tanggal_produksi= ?, tanggal_selesai= ? \n" +
                    "WHERE invoice_number = ?;";
            st = conn.prepareStatement(query);
            st.setInt(1, Integer.parseInt(jObjData.get("status_product_id").toString()));
            st.setString(2, jObjData.get("catatan").toString());
            st.setTimestamp(3, Timestamp.valueOf(jObjData.get("tanggal_produksi").toString()));
            st.setTimestamp(4, Timestamp.valueOf(jObjData.get("tanggal_selesai").toString()));
            st.setString(5, jObjData.get("invoice_number").toString());
            st.executeUpdate();
        }catch (Exception ex){
            Logger.getLogger(DatabaseConnection.class.getName()).log(Level.SEVERE, "Somethings Error", ex);
        } finally {
            try {
                if (st != null) {
                    st.close();
                }
                if (rs != null){
                    rs.close();
                }
            } catch (SQLException ex) {
                Logger.getLogger(DatabaseConnection.class.getName()).log(Level.SEVERE, "Error", ex);
            }
        }
    }

    @Override
    public JSONArray getStatusDetail() {
        DatabaseConnection databaseConnection = new DatabaseConnection();
        Connection conn = null;
        ResultSet rs = null;
        PreparedStatement st = null;
        JSONArray jArrRow = new JSONArray();
        try {
            conn = databaseConnection.getConnection();
            String query = "SELECT * FROM tbl_stat_product";
            st = conn.prepareStatement(query);
            rs = st.executeQuery();
            ResultSetMetaData metaData = rs.getMetaData();
            int colCount = metaData.getColumnCount();
            int j = 0;
            while (rs.next()) {
                JSONObject jObjColumn = new JSONObject();
                for (int i = 1; i <= colCount; i++) {
                    if (rs.getObject(i) == null) {
                        jObjColumn.put(metaData.getColumnLabel(i), "");
                    } else {
                        jObjColumn.put(metaData.getColumnLabel(i), rs.getObject(i));
                    }
                }
                jArrRow.add(j, jObjColumn);
                j = j++;
            }
        }catch (Exception ex){
            Logger.getLogger(DatabaseConnection.class.getName()).log(Level.SEVERE, "Somethings Error", ex);
        } finally {
            try {
                if (st != null) {
                    st.close();
                }
                if (rs != null){
                    rs.close();
                }
            } catch (SQLException ex) {
                Logger.getLogger(DatabaseConnection.class.getName()).log(Level.SEVERE, "Error", ex);
            }
        }
        return jArrRow;
    }

    @Override
    public JSONArray getPaymentMethod() {
        DatabaseConnection databaseConnection = new DatabaseConnection();
        Connection conn = null;
        ResultSet rs = null;
        PreparedStatement st = null;
        JSONArray jArrRow = new JSONArray();
        try {
            conn = databaseConnection.getConnection();
            String query = "SELECT * FROM tbl_payment_method";
            st = conn.prepareStatement(query);
            rs = st.executeQuery();
            ResultSetMetaData metaData = rs.getMetaData();
            int colCount = metaData.getColumnCount();
            int j = 0;
            while (rs.next()) {
                JSONObject jObjColumn = new JSONObject();
                for (int i = 1; i <= colCount; i++) {
                    if (rs.getObject(i) == null) {
                        jObjColumn.put(metaData.getColumnLabel(i), "");
                    } else {
                        jObjColumn.put(metaData.getColumnLabel(i), rs.getObject(i));
                    }
                }
                jArrRow.add(j, jObjColumn);
                j = j++;
            }
        }catch (Exception ex){
            Logger.getLogger(DatabaseConnection.class.getName()).log(Level.SEVERE, "Somethings Error", ex);
        } finally {
            try {
                if (st != null) {
                    st.close();
                }
                if (rs != null){
                    rs.close();
                }
            } catch (SQLException ex) {
                Logger.getLogger(DatabaseConnection.class.getName()).log(Level.SEVERE, "Error", ex);
            }
        }
        return jArrRow;
    }

    @Override
    public JSONArray getDataBarang() {
        DatabaseConnection databaseConnection = new DatabaseConnection();
        Connection conn = null;
        ResultSet rs = null;
        PreparedStatement st = null;
        JSONArray jArrRow = new JSONArray();
        try {
            conn = databaseConnection.getConnection();
            String query = "SELECT item_code as \"kode_barang\", item_price as \"harga_barang\", item_name as \"nama_barang\" FROM tbl_item";
            st = conn.prepareStatement(query);
            rs = st.executeQuery();
            ResultSetMetaData metaData = rs.getMetaData();
            int colCount = metaData.getColumnCount();
            int j = 0;
            while (rs.next()) {
                JSONObject jObjColumn = new JSONObject();
                for (int i = 1; i <= colCount; i++) {
                    if (rs.getObject(i) == null) {
                        jObjColumn.put(metaData.getColumnLabel(i), "");
                    } else {
                        jObjColumn.put(metaData.getColumnLabel(i), rs.getObject(i));
                    }
                }
                jArrRow.add(j, jObjColumn);
                j = j++;
            }
        }catch (Exception ex){
            Logger.getLogger(DatabaseConnection.class.getName()).log(Level.SEVERE, "Somethings Error", ex);
        } finally {
            try {
                if (st != null) {
                    st.close();
                }
                if (rs != null){
                    rs.close();
                }
            } catch (SQLException ex) {
                Logger.getLogger(DatabaseConnection.class.getName()).log(Level.SEVERE, "Error", ex);
            }
        }
        return jArrRow;
    }

    @Override
    public JSONArray listProduksi() {
        DatabaseConnection databaseConnection = new DatabaseConnection();
        Connection conn = null;
        ResultSet rs = null;
        PreparedStatement st = null;
        JSONArray jArrRow = new JSONArray();
        try {
            conn = databaseConnection.getConnection();
            String query = "select ti.invoice_code as \"Invoice Number\", ti2.item_code, ti3.item_name , ti3.item_price, ti2.qty , ti2.total , ti2.\"createdAt\" as \"tanggal_pemesanan\" , ti.receiver , ti.receiver_address,tps.status_product as \"status_produksi\"\n" +
                    "from tbl_invoice ti \n" +
                    "join tbl_invoicedetail ti2 \n" +
                    "ON  ti.invoice_code  = ti2.invoice_code\n" +
                    "join tbl_item ti3  on ti2.item_code  = ti3.item_code\n" +
                    "join tbl_product_status tps on tps.invoice_number  = ti.invoice_code \n" +
                    "order by ti2.\"createdAt\" DESC";
            st = conn.prepareStatement(query);
            rs = st.executeQuery();
            ResultSetMetaData metaData = rs.getMetaData();
            int colCount = metaData.getColumnCount();
            int j = 0;
            while (rs.next()) {
                JSONObject jObjColumn = new JSONObject();
                for (int i = 1; i <= colCount; i++) {
                    if (rs.getObject(i) == null) {
                        jObjColumn.put(metaData.getColumnLabel(i), "");
                    } else {
                        jObjColumn.put(metaData.getColumnLabel(i), rs.getObject(i));
                    }
                }
                jArrRow.add(j, jObjColumn);
                j = j++;
            }
        }catch (Exception ex){
            Logger.getLogger(DatabaseConnection.class.getName()).log(Level.SEVERE, "Somethings Error", ex);
        } finally {
            try {
                if (st != null) {
                    st.close();
                }
                if (rs != null){
                    rs.close();
                }
            } catch (SQLException ex) {
                Logger.getLogger(DatabaseConnection.class.getName()).log(Level.SEVERE, "Error", ex);
            }
        }
        return jArrRow;
    }
}
