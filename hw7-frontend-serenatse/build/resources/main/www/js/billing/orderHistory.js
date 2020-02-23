const months = ["January", "February", "March", "April", "May", "June",
    "July", "August", "September", "October", "November", "December"];

function displayOrders(res) {
    console.log(res);

    let historyDom = $(".orderHistory");
    historyDom.empty();

    let table = document.createElement("table");
    table.setAttribute("id", cartTable);

    // table headers //
    let headers = document.createElement("tr");
    let th1 = document.createElement('th'); // Date
    th1.setAttribute("id", "saleDateHeader")
    let th2 = document.createElement('th'); // Items
    let th3 = document.createElement('th'); // Total
    th3.setAttribute("id", "totalHeader")

    th1.appendChild(document.createTextNode("Date"));
    th2.appendChild(document.createTextNode("Items"));
    th3.appendChild(document.createTextNode("Total"));

    headers.appendChild(th1);
    headers.appendChild(th2);
    headers.appendChild(th3);
    table.appendChild(headers);

    let transactions = res["transactions"];
    for (let i = 0; i < transactions.length; ++i)
    {
        let tr1 = document.createElement("tr");

        let td1 = document.createElement('td'); // Sale Date
        td1.setAttribute("id", "saleDate");

        let td2 = document.createElement('td'); // Items
        td2.setAttribute("id", "items");

        let td3 = document.createElement('td'); // Total $
        td3.setAttribute("id", "total");


        let tr2 = document.createElement("tr");
        let td4 = document.createElement('td'); // transactionID & fee
        td4.setAttribute("id", "tId-Fee");

        let td5 = document.createElement('td'); // empty
        let td6 = document.createElement('td'); // empty

        let date = new Date(transactions[i]["create_time"]);
        let saleD = months[date.getMonth()] + " " + date.getDate() + ", " + date.getFullYear();
        let minutes = date.getMinutes() < 10 ? "0" + date.getMinutes() : date.getMinutes();
        let time = date.getHours() + ":" + minutes;
        let saleDateText = saleD + "\n" + time;

        let saleDate = document.createTextNode(saleDateText);
        let total = document.createTextNode("$" + transactions[i]["amount"]["total"]);
        let tId_Fee = document.createTextNode("Transaction ID: " + transactions[i]["transactionId"] + "\nTransaction Fee: " + "$" + transactions[i]["transaction_fee"]["value"]);

        // item table
        let itemsTable = document.createElement("table");
        itemsTable.setAttribute("id", "itemsTable");
        let itemHeaders = document.createElement('tr');

        let th4 = document.createElement('th'); // Movie
        th4.setAttribute("class", "itemHeader");
        let th5 = document.createElement('th'); // Quantity
        th5.setAttribute("class", "itemHeader");
        let th6 = document.createElement('th'); // Price
        th6.setAttribute("class", "itemHeader");

        th4.appendChild(document.createTextNode("Movie"));
        th5.appendChild(document.createTextNode("Quantity"));
        th6.appendChild(document.createTextNode("Price"));

        itemHeaders.appendChild(th4);
        itemHeaders.appendChild(th5);
        itemHeaders.appendChild(th6);

        itemsTable.appendChild(itemHeaders);

        let itemList = transactions[i]["items"];
        for (let i = 0; i < itemList.length; ++i)
        {
            let tr = document.createElement("tr");
            tr.setAttribute("id", "itemHeadText")
            let td6 = document.createElement('td'); // movie title
            td6.setAttribute("class", "itemData");
            td6.setAttribute("id", "itemMovieTitle")
            td6.setAttribute("name", itemList[i]["movieId"]);
            let td7 = document.createElement('td'); // quantity
            td7.setAttribute("class", "itemData");
            let td8 = document.createElement('td'); // price
            td8.setAttribute("class", "itemData");

            for (let i = 0; i < itemList.length; ++i) {
                console.log("IN FOR: " + itemList[i]["movieId"]);
                reqItemTitle(itemList[i]["movieId"]);
            }

            let title = document.createTextNode(itemList[i]["movieId"]);
            let qty = document.createTextNode(itemList[i]["quantity"]);
            let p = String((itemList[i]["unit_price"] * itemList[i]["discount"]).toFixed(2));
            let price = document.createTextNode("$" + p);

            td6.appendChild(title);
            td7.appendChild(qty);
            td8.appendChild(price);

            tr.appendChild(td6);
            tr.appendChild(td7);
            tr.appendChild(td8);

            itemsTable.appendChild(tr);
        }
        ITEM_GET = false;

        // add into main table
        td1.appendChild(saleDate);
        td2.appendChild(itemsTable);
        td3.appendChild(total);
        td4.appendChild(tId_Fee);


        tr1.appendChild(td1);
        tr1.appendChild(td2);
        tr1.appendChild(td3);
        tr2.appendChild(td4);
        tr2.appendChild(td5);
        tr2.appendChild(td6);

        table.appendChild(tr1);
        table.appendChild(tr2);

    }


    historyDom.append(table);

}

function reqItemTitle(movieId) {
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
            ITEM_GET = true;
            CART_GET = false;
            getReport(response);
        }
    });
}

function addItemTitle(res) {
    let movieTitle = res["movie"]["title"];
    let movieId = res["movie"]["movieId"];
    $("td[id='itemMovieTitle'][name='"+movieId+"']").html(movieTitle);
}


function retrieveOrders() {
    console.log("retrieving orders...");

    console.log("email: " + getCookie("email"));

    $.ajax({
        method: "POST",
        url: url + "/billing/order/retrieve?email=" + getCookie("email"),
        contentType: "application/json",
        dataType: "json",
        data: JSON.stringify({
            // "email": getCookie("email")
        }),
        headers: {
            "email": getCookie("email"),
            "sessionID": getCookie("sessionID")
        },
        statusCode: {
            204: function(data, textStatus, response) {
                console.log("Got 204 response ([orderHistory.js] retrieveOrders)...");
                console.log("tranID: " + response.getResponseHeader("transactionID"));
                getReport(response);
            },
            200: function () {
                console.log("Got 200 response...");
                $(".main").load("../../html/login.html");

            }
        }
    })
}