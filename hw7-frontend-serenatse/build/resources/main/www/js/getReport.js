function getReportResponse(resultCode, responseModel)
{
    resultCode = parseInt(resultCode);

    switch (resultCode) {
        // REGISTER USER //
        case USER_REGISTERED:
            console.log("User registered successfully");
            userRegistered();
            break;
        case INVALID_PASSWORD_LEN:
            console.log("User did not register successfully.");
            console.log("Password invalid length");
            invalidPassLen();
            break;
        case INVALID_PASSWORD:
            console.log("User did not register successfully.");
            console.log("Invalid password");
            invalidPass();
            break;
        case EMAIL_ALREADY_USED:
            console.log("User did not register successfully.");
            console.log("Email already in use");
            emailAlreadyUsed();
            break;
        // LOGIN USER //
        case LOGIN_SUCCESS:
            console.log("Login Success");
            let sessionID = responseModel["sessionID"];
            loginSuccess(sessionID);
            break;
        case PASSWORD_MISMATCH:
            console.log("Passwords don't match");
            passMismatch();
            break;
        case USER_NOT_FOUND:
            console.log("User not found");
            userNotFound();
            break;

        // MOVIE SEARCH //
        case MOVIES_FOUND:
            console.log("Movies found");
            if (QUICK_SEARCH) {
                movieTable(responseModel);
            }
            else if (DETAIL_GET) {
                showDetails(responseModel);
            }
            else if (LETTER_SEARCH) {
                movieTable(responseModel);
            }
            else if (CART_GET) {
                console.log("get titles");
                addTitle(responseModel);
            }
            else if (ITEM_GET) {
                console.log("get titles for items");
                addItemTitle(responseModel);
            }
            break;
        case NO_MOVIES_FOUND:
            console.log("No movies found");
            if (QUICK_SEARCH) {
                noMovies();
            }
            break;

        // RATING //
        case RATING_SUCCESS:
            console.log("rating succeeded");
            rateSuccess();
            break;
        case RATING_FAIL:
            console.log("rating failed");
            rateFail();
            break;

        // INSERT CART //
        case INVALID_QUANTITY:
            console.log("invalid quantity");
            invalidQty();
            break;
        case DUPLICATE_INSERTION:
            console.log("duplicate");
            dupAdd();
            break;
        case ITEM_INSERTED:
            console.log("item inserted");
            addSuccess();
            break;

        // RETRIEVE CART //
        case CART_RETRIEVED:
            console.log("cart retrieved.");
            cartTable(responseModel);
            break;
        case ITEM_DOES_NOT_EXIST:
            console.log("item does not exist");
            showEmpty();
            break;
        case CART_PRICE_GET:
            console.log("prices got.");
            addPrice(responseModel);
            break;

        // DELETE FROM CART //
        case CART_DELETE_SUCCESS:
            console.log("delete success");
            retrieveCart();
            break;
        case CART_CLEARED:
            console.log("cart cleared");
            showEmpty();
            // $("#cartText").html("Your Cart is Empty!");
            // retrieveCart();
            break;

        // UPDATE CART
        case CART_UPDATED:
            console.log("cart updated");
            retrieveCart();
            break;

        // ORDER PLACE
        case CREATE_PAYMENT_FAIL:
            console.log("payment failed");
            break;
        case ORDER_PLACED:
            console.log("order placed");
            orderPlaced(responseModel);
            break;

        case PAYMENT_COMPLETE:
            console.log("payment completed");
            break;
        case PAYMENT_INCOMPLETE:
            console.log("payment incomplete.");
            break;

        // INSERT CC
        case CC_INSERT_SUCCESS:
        case DUP_CC:
            console.log("card inserted.");
            $("#ckout-loading").html("Loading...");
            insertCustomer();
            break;
        case CUSTOMER_INSERT_SUCCESS:
        case DUP_CUSTOMER:
            console.log("customer inserted.");
            $("#ckout-loading").html("Loading...");
            placeOrder();
            break;

        // RETRIEVE ORDER
        case ORDERS_RETRIEVED:
            console.log("orders retrieved.");
            displayOrders(responseModel);
            break;

        // GENRES
        case GOT_GENRES:
            console.log("genres gotten");
            showGenres(responseModel);
            break;

    }

    LETTER_SEARCH = false;
    QUICK_SEARCH = false;
    DETAIL_GET = false;
}
