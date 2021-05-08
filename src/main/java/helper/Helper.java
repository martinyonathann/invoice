package helper;

import kong.unirest.HttpResponse;
import kong.unirest.Unirest;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Helper {
    public Timestamp getDate(String format) {
        // "yyyy-MM-dd HH:mm:ss"
        Date now = new Date();
        SimpleDateFormat sdf;
        sdf = new SimpleDateFormat(format);
        Timestamp strDate = Timestamp.valueOf(sdf.format(now));

        sdf = null;
        return strDate;
    }

    public JSONObject unirest (String Url) {
        HttpResponse<String> response = null;
        JSONObject jObjResponse = new JSONObject();
        response = Unirest.get(Url)
                .asString();
        try {
            JSONParser parser = new JSONParser();
            jObjResponse = (JSONObject) parser.parse(response.getBody());
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return jObjResponse;
    }
}
