function userRegistered()
{
    let modal = document.getElementById("register-modal");
    let span = document.getElementsByClassName("close")[0];
    $("#modal-title").html("Thank you for Registering!");
    $("#modal-body").html("Now go buy movies! :D");
    modal.style.display = "block";

    span.onclick = function() {
        modal.style.display = "none";
        $(".main").load("../html/login.html");
    };

    window.onclick = function(event) {
        if (event.target === modal) {
            modal.style.display = "none";
            $(".main").load("../html/login.html");
        }
    }
}

function invalidPassLen()
{
    let modal = document.getElementById("register-modal");
    let span = document.getElementsByClassName("close")[0];
    $("#modal-title").html("Invalid Password Length! :(");
    $("#modal-body").html("Please enter a password between 7 - 16 characters");
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

function invalidPass()
{
    let modal = document.getElementById("register-modal");
    let span = document.getElementsByClassName("close")[0];
    $("#modal-title").html("Invalid Password! :(");
    $("#modal-body").html(
        "Please enter a password with:<br /> " +
        "      - At least one uppercase and one lowercase letter<br />" +
        "      - At least one number<br />" +
        "      - At least one special character<br />" +
        "      - 7 to 16 characters long");
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

function emailAlreadyUsed()
{
    let modal = document.getElementById("register-modal");
    let span = document.getElementsByClassName("close")[0];
    $("#modal-title").html("This email is already in use! :(");
    $("#modal-body").html("Please enter a different email or go to the login page!");
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

$(".register-form").submit( function (event) {
    event.preventDefault();

    // let firstName = $("#firstName").val();
    // let lastName = $("#lastName").val();
    let email = $("#register-email").val();
    let password = $("#register-password").val().split("");

    // console.log("Full Name: " + firstName + " " + lastName);
    console.log("Email: " + email);
    console.log("Password: " + password);

    $.ajax({
        url: url + "/idm/register",
        type: "POST",
        data: JSON.stringify({
            // "firstName": firstName,
            "email": email,
            "password": password
        }),
        contentType: "application/json",
        success: function() {
            console.log("Got 200 response...");
            // returnResponse();
            // $(".main").load("../html/login.html");
        },

        statusCode:{
            204: function(data, textStatus, response) {
                console.log("Got 204 response (register.js)...")
                console.log("tranID: " + response.getResponseHeader("transactionID"));
                getReport(response);


            }
        }

    });

});