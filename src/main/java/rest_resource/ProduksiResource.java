package rest_resource;


import controllers.ProduksiController;
import org.apache.juli.logging.Log;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(value = "/v1/product")
public class ProduksiResource {
    static final Logger logger = LogManager.getLogger(ProduksiResource.class.getName());

    @PostMapping("/order")
    public String createOrder(@RequestBody String payloadRequest){
        String result = "";
        try{
            JSONParser parser = new JSONParser();
            JSONObject jObjPayload = (JSONObject) parser.parse(payloadRequest);
            logger.log(Level.INFO, "DATA REQUEST : "+jObjPayload);

            ProduksiController produksiController = new ProduksiController();
            result = produksiController.createHandler(jObjPayload);

            logger.log(Level.INFO, "DATA RESPONSE : "+result);
        }catch (ParseException e){
            e.printStackTrace();
        }
        return result;
    }
    @PostMapping(path = "/order/payment")
    public String paymentOrder(@RequestBody String payloadRequest){
        String result = "";
        try{
            JSONParser parser = new JSONParser();
            JSONObject jObjPayload = (JSONObject) parser.parse(payloadRequest);
            logger.log(Level.INFO, "DATA REQUEST : "+jObjPayload);

            ProduksiController produksiController = new ProduksiController();
            result = produksiController.paymentHandler(jObjPayload);

            logger.log(Level.INFO, "DATA RESPONSE : "+result);
        }catch (ParseException e){
            e.printStackTrace();
        }
        return result;
    }

    @PostMapping(path = "/advice")
    public String updateOrder(@RequestBody String payloadRequest){
        String result = "";
        try{
            JSONParser parser = new JSONParser();
            JSONObject jObjPayload = (JSONObject) parser.parse(payloadRequest);
            logger.log(Level.INFO, "DATA REQUEST : "+jObjPayload);

            ProduksiController produksiController = new ProduksiController();
            result = produksiController.updateAdvice(jObjPayload);

            logger.log(Level.INFO, "DATA RESPONSE : "+result);
        }catch (ParseException e){
            e.printStackTrace();
        }
        return result;
    }

    @GetMapping(path = "/advice/{invoice_code}")
    public String adviceProduct(@PathVariable String invoice_code){
        String result = "";
        try{
            logger.log(Level.INFO, "ADVICE PRODUCT WITH INVOICE_CODE : "+invoice_code );
            ProduksiController produksiController = new ProduksiController();
            result = produksiController.adviceHandler(invoice_code);

            logger.log(Level.INFO, "DATA RESPONSE : "+result);
        }catch (Exception e){
            e.printStackTrace();
        }
        return result;
    }

    @GetMapping(path = "/status")
    public String statusProduct(){
        String result = "";
        try{
            ProduksiController produksiController = new ProduksiController();
            result = produksiController.statusHandler();

            logger.log(Level.INFO, "DATA RESPONSE : "+result);
        }catch (Exception e){
            e.printStackTrace();
        }
        return result;
    }

    @GetMapping(path = "/metodeBayar")
    public String paymentMethod(){
        String result = "";
        try{
            ProduksiController produksiController = new ProduksiController();
            result = produksiController.paymentMethod();

            logger.log(Level.INFO, "DATA RESPONSE : "+result);
        }catch (Exception e){
            e.printStackTrace();
        }
        return result;
    }

    @GetMapping(path = "/items")
    public String dataBarang(){
        String result = "";
        try{
            ProduksiController produksiController = new ProduksiController();
            result = produksiController.dataBarang();

            logger.log(Level.INFO, "DATA RESPONSE : "+result);
        }catch (Exception e){
            e.printStackTrace();
        }
        return result;
    }

    @GetMapping(path = "/orders")
    public String listOrder(){
        String result = "";
        try{
            ProduksiController produksiController = new ProduksiController();
            result = produksiController.listProduksi();

            logger.log(Level.INFO, "DATA RESPONSE : "+result);
        }catch (Exception e){
            e.printStackTrace();
        }
        return result;
    }
}
