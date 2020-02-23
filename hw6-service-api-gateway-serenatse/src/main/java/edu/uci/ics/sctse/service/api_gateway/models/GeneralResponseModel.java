package edu.uci.ics.sctse.service.api_gateway.models;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import edu.uci.ics.sctse.service.api_gateway.utilities.ResultCodes;

@JsonIgnoreProperties(value = { "dataValid" })
public class GeneralResponseModel extends Model {
    private int resultCode;
    private String message;

    @JsonCreator
    public GeneralResponseModel(@JsonProperty(value = "resultCode", required = true) int resultCode) {
        this.resultCode = resultCode;
        this.message = ResultCodes.setMessage(resultCode);
    }

    @Override
    public String toString() {
        return this.getClass().getName() + "[resultCode: " + resultCode + ", message: " + message + "]";
    }

    @JsonProperty(value = "resultCode", required = true)
    public int getResultCode() {
        return resultCode;
    }

    @JsonProperty(value = "message", required = true)
    public String getMessage() {
        return message;
    }
}
