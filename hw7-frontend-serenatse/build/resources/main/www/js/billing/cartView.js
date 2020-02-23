function cartTable(res) {
    console.log(res);

    let cartDom = $(".cart");
    cartDom.empty();
    let table = document.createElement("table");
    table.setAttribute("id", "cartTable");


    // TABLE HEADERS
    let heads = document.createElement('tr');
    let th = document.createElement('th');
    let th2 = document.createElement('th');
    let th3 = document.createElement('th');

    th.appendChild(document.createTextNode(""));
    th2.appendChild(document.createTextNode("Price"));
    th3.appendChild(document.createTextNode("Quantity"));
    heads.appendChild(th);
    heads.appendChild(th2);
    heads.appendChild(th3);
    table.appendChild(heads);

    let itemList = res["items"];
    for (let i = 0; i < itemList.length; ++i) {
        console.log("IN FOR: " + itemList[i]["movieId"]);
        reqTitle(itemList[i]["movieId"]);
        reqPrice(itemList[i]["movieId"]);
    }
    CART_GET = false;

    for (let i = 0; i < itemList.length; ++i) {
        let tr1 = document.createElement("tr");
        let td1 = document.createElement('td'); // movie title
        td1.setAttribute("id", "cartTitle");
        td1.setAttribute("name", itemList[i]["movieId"]);
        let td2 = document.createElement('td'); // price
        td2.setAttribute("id", "cartPrice");
        td2.setAttribute("name", itemList[i]["movieId"]);
        let td3 = document.createElement('td'); // quantity
        let text = document.createElement("input");
        text.setAttribute("id", itemList[i]["movieId"]);
        text.setAttribute("type", "number");
        text.setAttribute("value", itemList[i]["quantity"]);
        text.setAttribute("class", "qty-text");
        td3.setAttribute("id", "cartQty");

        let tr2 = document.createElement("tr");
        tr2.setAttribute("id", "delete-update-tr");
        let td4 = document.createElement('td'); // delete
        let a1 = document.createElement("a");
        a1.setAttribute("id", "cartDelete");
        a1.setAttribute("movieId", itemList[i]["movieId"]);
        let td5 = document.createElement('td'); // update
        let a2 = document.createElement("a");
        a2.setAttribute("id", "cartUpdate");
        a2.setAttribute("movieId", itemList[i]["movieId"]);
        let td6 = document.createElement("td");
        td6.setAttribute("id", "cartEmpty");

        let title = document.createTextNode(itemList[i]["movieId"]);
        let price = document.createTextNode("-");

        let del = document.createTextNode("Delete");
        let update = document.createTextNode("Update");

        td1.appendChild(title);
        td2.appendChild(price);
        td3.appendChild(text);
        tr1.appendChild(td1);
        tr1.appendChild(td2);
        tr1.appendChild(td3);

        a1.appendChild(del);
        td4.appendChild(a1);
        a2.appendChild(update);
        td5.appendChild(a2);
        tr2.appendChild(td6);
        tr2.appendChild(td4);
        tr2.appendChild(td5);

        table.appendChild(tr1);
        table.appendChild(tr2);
    }
    cartDom.append(table);
}

function reqTitle(movieId) {
    console.log("getting title...");

    $.ajax({
        method: "GET",
        url: url + "/movies/get/" + movieId,
        contentType: "application/json",
        dataType: "json",
        headers: {
            "email": getCookie("email"),
            "sessionID": getCookie("sessionID")
        },
        success: function (data, textStatus, response) {
            console.log("tranID: " + response.getResponseHeader("transactionID"));
            CART_GET = true;
            getReport(response);
        }
    });
}

function addTitle(res) {
    let movieTitle = res["movie"]["title"];
    let movieId = res["movie"]["movieId"];
    $("td[id='cartTitle'][name='"+movieId+"']").html(movieTitle);
}


function reqPrice(movieId) {
    console.log("getting price...");

    $.ajax({
        method: "GET",
        url: url + "/billing/price/" + movieId,
        contentType: "application/json",
        dataType: "json",
        headers: {
            "email": getCookie("email"),
            "sessionID": getCookie("sessionID")
        },
        success: function (data, textStatus, response) {
            console.log("tranID: " + response.getResponseHeader("transactionID"));
            getReport(response);
        }
    });
}


function addPrice(res) {
    console.log("getting price...");
    let price = res["price"];
    let discount = res["discount"];
    let movieId = res["movieId"];
    let final_price = price * discount;
    $("td[id='cartPrice'][name='"+movieId+"']").html("$"+final_price.toFixed(2));
}

function retrieveCart() {
    console.log("retrieving cart...");
    $("#cartText").html("Your Cart is Empty!");

    $.ajax({
        method: "POST",
        url: url + "/billing/cart/retrieve",
        contentType: "application/json",
        dataType: "json",
        data: JSON.stringify({
            "email": getCookie("email")
        }),
        headers: {
            "email": getCookie("email"),
            "sessionID": getCookie("sessionID")
        },
        statusCode: {
            204: function(data, textStatus, response) {
                console.log("Got 204 response (cartView.js)...");
                console.log("tranID: " + response.getResponseHeader("transactionID"));
                getReport(response);
            },
            200: function () {
                console.log("Got 200 response...");
                $(".main").load("../../html/login.html");

            }
        }
    });
}

$(document).on('click', '#goToCart', function () {
    $("#cartText").html("Your Cart is Empty!");
    retrieveCart();
});

$(document).on('click', '#cartBtn', function() {
    $("#cartText").html("Your Cart is Empty!");
    retrieveCart();
});

$(document).on('click', '#cartButton', function() {
    $("#cartText").html("Your Cart is Empty!");
    retrieveCart();
});


// DELETE  //
$(document).on('click', '#cartDelete', function () {
    let movieId = $(this).attr("movieId");

    console.log("movie to delete: " + movieId);
    $.ajax({
        method: "POST",
        url: url + "/billing/cart/delete",
        contentType: "application/json",
        dataType: "json",
        data: JSON.stringify({
            "email": getCookie("email"),
            "movieId": movieId
        }),
        headers: {
            "email": getCookie("email"),
            "sessionID": getCookie("sessionID")
        },
        success: function()
        {
            console.log("Got 200 response(cartView.js)...")
        },
        statusCode:{
            204: function(data, textStatus, response) {
                console.log("Got 204 response (cartView.js)...");
                console.log("tranID: " + response.getResponseHeader("transactionID"));
                getReport(response);
            }
        }

    })
});


// UPDATE //
$(document).on('click', '#cartUpdate', function () {
    let movieId = $(this).attr("movieId");
    let quantity = $("input[id="+movieId+"][class='qty-text']").val();

    console.log("quantity: " + quantity);

    $.ajax({
        method: "POST",
        url: url + "/billing/cart/update",
        contentType: "application/json",
        dataType: "json",
        data: JSON.stringify({
            "email": getCookie("email"),
            "movieId": movieId,
            "quantity": quantity
        }),
        headers: {
            "email": getCookie("email"),
            "sessionID": getCookie("sessionID")
        },
        success: function () {
            console.log("Got 200 response(cartView.js)...")
        },
        statusCode: {
            204: function (data, textStatus, response) {
                console.log("Got 204 response (cartView.js)...");
                console.log("tranID: " + response.getResponseHeader("transactionID"));
                getReport(response);
            }
        }
    });

    $( "#popUp" ).show();
    setTimeout(function() {
        $( "#popUp" ).hide();
    }, 2000);
});


// EMPTY CART //
$(document).on('click', '#emptyBtn', function () {
    console.log("emptying cart...");

    $.ajax({
        method: "POST",
        url: url + "/billing/cart/clear",
        contentType: "application/json",
        dataType: "json",
        data: JSON.stringify({
            "email": getCookie("email")
        }),
        headers: {
            "email": getCookie("email"),
            "sessionID": getCookie("sessionID")
        },
        success: function()
        {
            console.log("Got 200 response(cartView.js - empty cart)...")
        },
        statusCode: {
            204: function (data, textStatus, response) {
                console.log("Got 204 response (cartView.js - empty cart)...");
                console.log("tranID: " + response.getResponseHeader("transactionID"));
                getReport(response);
            }
        }
    })
});

function showEmpty() {
    // $("#cartText").html("Your Cart is Empty!");
    // $(".main").load("../../html/billing/shoppingCart.html");
    let cartDom = $(".cart");
    cartDom.empty();

    let h3 = document.createElement("h3");
    let text = document.createTextNode("Your Cart is Empty!");
    h3.setAttribute("id", "cartText");
    h3.appendChild(text);

    cartDom.append(h3);

    // $(document).ready(function () {
    //     $("#cartText").html("Your Cart is Empty!");
    // });
}

// CHECKOUT //
$(document).on('click', '#checkoutBtn', function () {
    console.log("checking out...");

    $(".main").load("../../html/billing/checkout.html");

});

// ORDER HISTORY //
$(document).on('click', '#orderHist', function () {
    $(".main").load("../../html/billing/orderHistory.html");
    retrieveOrders();
});