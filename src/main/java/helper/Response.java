package helper;

import org.json.simple.JSONObject;

public class Response {
    public enum Pesan {
        CODE200SUCCESS,
        CODE400BADREQUEST,
        CODE500INTERNALSERVEREROR,
        CODE404NOTFOUND,
        UNKNOWN;
        public JSONObject resp(JSONObject dataResponse){
            JSONObject jObjData = new JSONObject();
            switch (this){
                case CODE200SUCCESS:
                    jObjData.put("code",200);
                    jObjData.put("message", "Success");
                    jObjData.put("detail", "process successfully");
                    jObjData.put("data",dataResponse);
                    return jObjData;
                case CODE400BADREQUEST:
                    jObjData.put("code", 400);
                    jObjData.put("message", "Failed");
                    jObjData.put("detail", "Missing Parameter");
                    return  jObjData;
                case CODE500INTERNALSERVEREROR:
                    jObjData.put("code", 500);
                    jObjData.put("message", "Failed");
                    jObjData.put("detail", "INTERNAL_SERVER_ERROR");
                    return jObjData;
                case CODE404NOTFOUND:
                    jObjData.put("code", 404);
                    jObjData.put("message", "Failed");
                    jObjData.put("detail", "DATA NOT FOUND");
                    return jObjData;
                default:
                    throw  new AssertionError("unknown operations "+this);
            }
        }
    }
}
