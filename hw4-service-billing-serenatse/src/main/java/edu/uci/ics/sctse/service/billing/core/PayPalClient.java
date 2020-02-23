package edu.uci.ics.sctse.service.billing.core;

import com.paypal.api.payments.*;

import javax.ws.rs.core.UriBuilder;
import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.paypal.api.payments.RedirectUrls;
import com.paypal.base.rest.APIContext;
import com.paypal.base.rest.PayPalRESTException;
import edu.uci.ics.sctse.service.billing.BillingService;
import edu.uci.ics.sctse.service.billing.logger.ServiceLogger;

public class PayPalClient
{
    private String clientID = "ATy1yxn16fuiVM_5vs7FgZZgIYKeSU2Nuq3iiiac6UEuEVbUSUiqMk6uCdCdGSSvNwGBlcg_Ko634Rb_";
    private String clientSecret = "EFAMXKolzYMTEOS8TCKistY3irk0xdp-up0dHzja7wpNvXDCF1HMzgjNHMrK2sCLRckLOtJc26EaPi1O";

    public Map<String, Object> createPayment(String sum)
    {
        Map<String, Object> response = new HashMap<>();

        ServiceLogger.LOGGER.info("Creating new amount");
        Amount amount = new Amount();
        amount.setCurrency("USD");
        amount.setTotal(sum);

        ServiceLogger.LOGGER.info("Creating new transaction");
        Transaction transaction = new Transaction();
        transaction.setAmount(amount);
        List<Transaction> transactions = new ArrayList<>();
        transactions.add(transaction);

        ServiceLogger.LOGGER.info("Creating new Payer");
        Payer payer = new Payer();
        payer.setPaymentMethod("paypal");

        ServiceLogger.LOGGER.info("Creating new Payment");
        Payment payment = new Payment();
        payment.setIntent("sale");
        payment.setPayer(payer);
        payment.setTransactions(transactions);

        ServiceLogger.LOGGER.info("Getting RedirectURLs");
        String scheme = BillingService.getConfigs().getScheme();
        String hostName = BillingService.getConfigs().getHostName();
        String path = BillingService.getConfigs().getPath();
        int port = BillingService.getConfigs().getPort();

        RedirectUrls redirectUrls = new RedirectUrls();
        URI uri = UriBuilder.fromUri(scheme + hostName + path).port(port).build();
        redirectUrls.setCancelUrl(uri.toString() + "/order/cancel");
        redirectUrls.setReturnUrl(uri.toString() + "/order/complete");
        ServiceLogger.LOGGER.info("RETURN URL: " + uri.toString() + "/order/complete");
        payment.setRedirectUrls(redirectUrls);

        try
        {
            ServiceLogger.LOGGER.info("getting api context");
            APIContext apiContext = new APIContext(clientID, clientSecret, "sandbox");
            Payment createdPayment = payment.create(apiContext);

            String redirectUrl = "";

            List<Links> links = createdPayment.getLinks();
//            ServiceLogger.LOGGER.info("LINKS: " + links.toString());
            for (Links link: links)
            {
                if (link.getRel().equals("approval_url"))
                {
                    ServiceLogger.LOGGER.info("Approved redirect URL");
                    redirectUrl = link.getHref();
                    break;
                }
            }

            response.put("status", "success");
            response.put("redirectUrl", redirectUrl);

        }
        catch (PayPalRESTException e)
        {
            ServiceLogger.LOGGER.info("Exception in making payment");
            response.put("status", "fail");
            response.put("redirectUrl", null);
        }

        return response;
    }

    public Map<String, Object> completePayment(String paymentId, String PayerID)
    {
        ServiceLogger.LOGGER.info("Completing payment...");
        Map<String, Object> response = new HashMap<>();
        Payment payment = new Payment();
        payment.setId(paymentId);

        ServiceLogger.LOGGER.info("Making payment Execution");
        PaymentExecution paymentExecution = new PaymentExecution();
        paymentExecution.setPayerId(PayerID);

        try
        {
            ServiceLogger.LOGGER.info("Making api context");
            APIContext apiContext = new APIContext(clientID, clientSecret, "sandbox");
            Payment createdPayment = payment.execute(apiContext, paymentExecution);

            ServiceLogger.LOGGER.info("Payment success");
            response.put("status", "success");
            response.put("payment", createdPayment);
            response.put("transactionID", createdPayment.getTransactions().get(0).getRelatedResources().get(0).getSale().getId());
        }
        catch (PayPalRESTException e)
        {
            ServiceLogger.LOGGER.info("Payment execution failed.");
            response.put("status", "fail");
        }

        return response;
    }

    public Sale retrievePayment(String tID)
    {
        ServiceLogger.LOGGER.info("Retrieving Payment...");
        try
        {
            APIContext apiContext = new APIContext(clientID, clientSecret, "sandbox");
            Sale sale = Sale.get(apiContext, tID);
            ServiceLogger.LOGGER.info("sale made.");
            return sale;
        }
        catch (PayPalRESTException e)
        {
            ServiceLogger.LOGGER.info("Payment retrieval failed.");
        }

        return new Sale();
    }
}
