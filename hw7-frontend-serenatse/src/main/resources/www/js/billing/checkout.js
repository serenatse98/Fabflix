function orderPlaced(res) {
    console.log("order placed response: " + res["redirectURL"]);
    let url = res["redirectURL"];
    $(".main").load("../../html/billing/payment.html");
    $(document).on('click', '#paypalBtn', function() {
        window.open(url);
        showThankYou();
    })
}

function showThankYou() {
    $(".main").load("../../html/billing/thankYou.html");
}

$(".checkout-form").submit( function (event) {
    event.preventDefault();

    let firstName = $("#firstName").val();
    let lastName = $("#lastName").val();
    let ccId = "0000000000000000000";

    $.ajax({
        method: "POST",
        url: url + "/billing/creditcard/insert",
        contentType: "application/json",
        dataType: "json",
        data: JSON.stringify({
            "id": ccId,
            "firstName": firstName,
            "lastName": lastName,
            "expiration": "2222-02-02"
        }),
        headers: {
            "email": getCookie("email"),
            "sessionID": getCookie("sessionID")
        },
        success: function(data, textStatus, res)
        {
            if (res["resultCode"] === 131) {
                $(".main").load("../../html/login.html");
            }
        },
        statusCode:{
            204: function(data, textStatus, response) {
                console.log("Got 204 response (checkout.js - insert cc)...")
                console.log("tranID: " + response.getResponseHeader("transactionID"));
                getReport(response);
            }
        }
    })

});

function insertCustomer() {
    console.log("inserting customer");

    $.ajax({
        method: "POST",
        url: url + "/billing/customer/insert",
        contentType: "application/json",
        dataType: "json",
        data: JSON.stringify({
            "email": getCookie("email"),
            "firstName": "First",
            "lastName": "Last",
            "ccId": "0000000000000000000",
            "address": "N/A"
        }),
        headers: {
            "email": getCookie("email"),
            "sessionID": getCookie("sessionID")
        },
        success: function(data, textStatus, res)
        {
            if (res["resultCode"] === 131) {
                $(".main").load("../../html/login.html");
            }
        },
        statusCode:{
            204: function(data, textStatus, response) {
                console.log("Got 204 response (checkout.js - insert cc)...")
                console.log("tranID: " + response.getResponseHeader("transactionID"));
                getReport(response);
            }
        }
    })
}

function placeOrder() {
    console.log("placing order...")
    $.ajax({
        method: "POST",
        url: url + "/billing/order/place",
        contentType: "application/json",
        dataType: "json",
        data: JSON.stringify({
            "email": getCookie("email")
        }),
        headers: {
            "email": getCookie("email"),
            "sessionID": getCookie("sessionID")
        },
        success: function(data, textStatus, res)
        {
            if (res["resultCode"] === 131) {
                $(".main").load("../../html/login.html");
            }
        },
        statusCode:{
            204: function(data, textStatus, response) {
                console.log("Got 204 response (cartView.js - place order)...")
                console.log("tranID: " + response.getResponseHeader("transactionID"));
                getReport(response);
            }
        }
    })
}