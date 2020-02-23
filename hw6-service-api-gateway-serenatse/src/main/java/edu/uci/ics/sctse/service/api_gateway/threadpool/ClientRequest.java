package edu.uci.ics.sctse.service.api_gateway.threadpool;

import edu.uci.ics.sctse.service.api_gateway.models.RequestModel;

import java.util.Map;

public class ClientRequest {
    private String email;
    private String sessionID;
    private String transactionID;
    private RequestModel request;
    private String URI;
    private String endpoint;
    private int httpMethodType;
    private Map<String, Object> queryParams;
    private String pathParam;

    public ClientRequest() {

    }

    public ClientRequest(String email, String sessionID, String transactionID, RequestModel request, String URI, String endpoint)
    {
        this.email = email;
        this.sessionID = sessionID;
        this.transactionID = transactionID;
        this.request = request;
        this.URI = URI;
        this.endpoint = endpoint;
    }

    public String getEmail()
    {
        return email;
    }

    public void setEmail(String email)
    {
        this.email = email;
    }

    public String getSessionID()
    {
        return sessionID;
    }

    public void setSessionID(String sessionID)
    {
        this.sessionID = sessionID;
    }

    public String getTransactionID()
    {
        return transactionID;
    }

    public void setTransactionID(String transactionID)
    {
        this.transactionID = transactionID;
    }

    public RequestModel getRequest()
    {
        return request;
    }

    public void setRequest(RequestModel request)
    {
        this.request = request;
    }

    public String getURI()
    {
        return URI;
    }

    public void setURI(String URI)
    {
        this.URI = URI;
    }

    public String getEndpoint()
    {
        return endpoint;
    }

    public void setEndpoint(String endpoint)
    {
        this.endpoint = endpoint;
    }

    public int getHttpMethodType()
    {
        return httpMethodType;
    }

    public void setHttpMethodType(int httpMethodType)
    {
        this.httpMethodType = httpMethodType;
    }

    public Map<String, Object> getQueryParams()
    {
        return queryParams;
    }

    public void setQueryParams(Map<String, Object> queryParams)
    {
        this.queryParams = queryParams;
    }

    public String getPathParam()
    {
        return pathParam;
    }

    public void setPathParam(String pathParam)
    {
        this.pathParam = pathParam;
    }
}
