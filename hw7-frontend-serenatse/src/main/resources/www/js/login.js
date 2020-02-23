function loginSuccess(sessionID)
{
    let email = $("#login-email").val();
    setCookie(email, sessionID);
    // console.log("COOKIE EMAIL: " + getCookie("email"));
    // console.log("COOKIE SESSIONID: " + getCookie("sessionID"));
    window.location = '/index.html';
}

function passMismatch()
{
    let modal = document.getElementById("register-modal");
    let span = document.getElementsByClassName("close")[0];
    $("#modal-title").html("Invalid Password! :(");
    $("#modal-body").html("Password is incorrect! Please try again.");
    modal.style.display = "block";

    span.onclick = function() {
        modal.style.display = "none";
    };

    window.onclick = function(event) {
        if (event.target === modal) {
            modal.style.display = "none";
        }
    }
}

function userNotFound()
{
    let modal = document.getElementById("register-modal");
    let span = document.getElementsByClassName("close")[0];
    $("#modal-title").html("Email Not Found! :(");
    $("#modal-body").html("Please enter a valid email.");
    modal.style.display = "block";

    span.onclick = function() {
        modal.style.display = "none";
    };

    window.onclick = function(event) {
        if (event.target === modal) {
            modal.style.display = "none";
        }
    }
}

$(".login-form").submit( function (event) {
    event.preventDefault();

    let email = $("#login-email").val();
    let password = $("#login-password").val().split("");

    console.log("Email: " + email);
    console.log("Password: " + password);

    $.ajax({
        url: url + "/idm/login",
        type: "POST",
        data: JSON.stringify({
            "email": email,
            "password": password
        }),
        contentType: "application/json",
        success: function(data, textStatus, request)
        {
            console.log("Got 200 response(login.js)...")
        },
        statusCode:{
            204: function(data, textStatus, response) {
                console.log("Got 204 response (login.js)...")
                console.log("tranID: " + response.getResponseHeader("transactionID"));
                getReport(response);

            }
        }

    });

});