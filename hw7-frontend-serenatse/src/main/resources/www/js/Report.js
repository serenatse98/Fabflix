function getReport(response) {
    let transID = response.getResponseHeader("transactionID");

    $.ajax({
        url: url + "/report",
        type: "GET",
        contentType: "application/json",
        headers: {
            "transactionID": transID
        },
        statusCode: {
            200: function (responseModel) {
                console.log("Got 200 response...");
                let resultCode = responseModel["resultCode"];
                let message = responseModel["message"];
                console.log("ResultCode: " + resultCode);
                console.log("Message: " + message);

                getReportResponse(resultCode, responseModel);

            },

            204: function (responseModel) {
                console.log("Got 204 response (Report.js)...");

            },

            400: function (responseModel) {
                console.log("Got 400 bad request (Report.js)...");
                let resultCode = responseModel["resultCode"];
                let message = responseModel["message"];
                console.log("ResultCode: " + resultCode);
                console.log("Message: " + message);


            },

            500: function (responseModel) {
                console.log("Got 500 error (Report.js)...")
                let resultCode = responseModel["resultCode"];
                let message = responseModel["message"];
                console.log("ResultCode: " + resultCode);
                console.log("Message: " + message);
            }
        }
    })
}