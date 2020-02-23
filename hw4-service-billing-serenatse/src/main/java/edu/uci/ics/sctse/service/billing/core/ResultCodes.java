package edu.uci.ics.sctse.service.billing.core;

public class ResultCodes
{
    public static String getResponse(int c)
    {
        String result = "";
        switch (c)
        {
            case -11:
            case -10:
            case -3:
            case -2:
                result = "400 Bad Request";
                break;

            case -1:
                result = "500 Internal Server Error";
                break;

            case 33:
            case 311:
            case 312:
            case 321:
            case 322:
            case 323:
            case 324:
            case 325:
            case 331:
            case 332:
            case 333:
            case 341:
            case 342:
            case 3100:
            case 3110:
            case 3120:
            case 3130:
            case 3140:
            case 3200:
            case 3210:
            case 3220:
            case 3230:
            case 3300:
            case 3310:
            case 3320:
            case 3400:
            case 3420:
            case 3421:
            case 3422:
                result = "200 OK";
                break;
        }

        return result;
    }

    public static String resultMessage(int c)
    {
        String message = "";
        switch (c)
        {
            // 400 BAD REQUEST
            case -11:
                message = " Email address has invalid format.";
                break;
            case -10:
                message = "Email address has invalid length.";
                break;
            case -3:
                message = "JSON Parse Exception.";
                break;
            case -2:
                message = "JSON Mapping Exception.";
                break;

            // 500 INTERNAL SERVER ERROR
            case -1:
                message = "Internal Server Error.";
                break;

            // 200 OK
            case 33:
                message = "Quantity has invalid value.";
                break;
            case 311:
            case 325:
            case 333:
                message = "Duplicate insertion.";
                break;
            case 312:
                message = "Shopping item does not exist.";
                break;
            case 321:
                message = " Credit card ID has invalid length.";
                break;
            case 322:
                message = "Credit card ID has invalid value.";
                break;
            case 323:
                message = "expiration has invalid value.";
                break;
            case 324:
                message = "Credit card does not exist.";
                break;
            case 331:
                message = "Credit card ID not found.";
                break;
            case 332:
                message = "Customer does not exist.";
                break;
            case 341:
                message = "Shopping cart for this customer not found.";
                break;
            case 342:
                message = "Create payment failed.";
                break;
            case 3100:
                message = "Shopping cart item inserted successfully.";
                break;
            case 3110:
                message = "Shopping cart item updated successfully.";
                break;
            case 3120:
                message = "Shopping cart item deleted successfully.";
                break;
            case 3130:
                message = "Shopping cart retrieved successfully.";
                break;
            case 3140:
                message = "Shopping cart cleared successfully.";
                break;
            case 3200:
                message = "Credit card inserted successfully.";
                break;
            case 3210:
                message = "Credit card updated successfully.";
                break;
            case 3220:
                message = "Credit card deleted successfully.";
                break;
            case 3230:
                message = "Credit card retrieved successfully.";
                break;
            case 3300:
                message = "Customer inserted successfully.";
                break;
            case 3310:
                message = "Customer updated successfully.";
                break;
            case 3320:
                message = "Customer retrieved successfully.";
                break;
            case 3400:
                message = "Order placed successfully.";
                break;
            case 3410:
                message = "Orders retrieved successfully.";
                break;
            case 3420:
                message = "Payment is completed successfully.";
                break;
            case 3421:
                message = "Token not found.";
                break;
            case 3422:
                message = "Payment can not be completed.";
                break;
        }

        return message;
    }
}
