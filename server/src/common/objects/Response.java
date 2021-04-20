package common.objects;

import java.io.Serializable;

public class Response implements Serializable {
    private String responseString;

    public Response(String responseString) {
        this.responseString = responseString;
    }

    public String getResponseString() {
        return responseString;
    }

    @Override
    public String toString() {
        return "Response["  + responseString + "]";
    }

}
