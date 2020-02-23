

function showDetails(responseModel) {
    console.log(responseModel);
    // $(".main").load("../../html/movies/movieDetails.html");
    let modal = document.getElementById("movieDetailModal");
    let span = document.getElementsByClassName("modalDetail-close")[0];

    let movie = responseModel["movie"];

    let year = "";
    if (movie["year"] !== undefined) {
        year += " (" + movie["year"] + ")";
    }
    $("#movieDetail-title").html(movie["title"] + year);
    $("#rating").html(movie["rating"]+ "/10");
    $("#movie-overview").html(movie["overview"]);
    $("#numVotes").html("from " + movie["numVotes"] + " users");
    $("#movie-director").html("Directed by: " + movie["director"]);

    let stars = "";
    if (movie["stars"].length > 1) {
        stars += movie["stars"][0]["name"];
        let i = 1;
        for (i; i < movie["stars"].length; ++i) {
            stars += ", " + movie["stars"][i]["name"];
        }
    }
    if (stars !== "") {
        $("#movie-stars").html("Staring: " + stars);
    }

    let genres = "";
    if (movie["genres"].length > 1) {
        genres += movie["genres"][0]["name"];
        let j = 1;
        for (j; j < movie["genres"].length; ++j) {
            genres += ", " + movie["genres"][j]["name"];
        }
    }
    if (genres !== "") {
        $("#movie-genres").html("Genres: " + genres);
    }

    if (movie["revenue"] !== undefined)
    {
        $("#movie-revenue").html("Revenue: $" + movie["revenue"]);
    }
    if (movie["budget"] !== undefined)
    {
        $("#movie-budget").html("Budget: $" + movie["budget"]);
    }

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

function movieTable(res) {
    $("#movie-header").html("MOVIES FOUND");
    console.log(res);
    let movieDom = $('.movies');
    movieDom.empty(); // Clear the previous results

    let movieList = res["movies"];
    let table = document.createElement("table");

    // MAKE HEADERS //
    let heads = document.createElement('tr');
    let th = document.createElement('th');
    let th2 = document.createElement('th');

    th.appendChild(document.createTextNode("TITLE"));
    th2.appendChild(document.createTextNode("DIRECTOR"));
    heads.appendChild(th);
    heads.appendChild(th2);
    table.appendChild(heads);
    for (let i = 0; i < movieList.length; ++i) {
        let tr = document.createElement('tr');
        let td = document.createElement('td');
        let td2 = document.createElement('td');
        let link = document.createElement('a');

        let movieObject = movieList[i];
        let title = document.createTextNode(movieObject["title"]);
        let director = document.createTextNode(movieObject["director"]);
        link.setAttribute('id', 'movieTitle');
        link.setAttribute('movieId', movieObject["movieId"])

        // td.appendChild(title);
        link.appendChild(title);
        td.appendChild(link);
        td2.appendChild(director);
        tr.appendChild(td);
        tr.appendChild(td2);
        table.appendChild(tr);
    }
    movieDom.append(table);
}

function noMovies()
{
    console.log("No movies found.");
    $("#movie-header").html("No movies found :( <br/> Maybe try the <a id='advSearch'>Advanced Search</a>?");
}

$(document).on('click', "#advSearch", function () {
   $(".main").load("../../html/movies/advSearch.html")
});

$("#quick-search").submit(function (event) {
    event.preventDefault();

    console.log("making quick search...");

    let movieTitle = $(".search-text").val();
    console.log("Title: " + movieTitle);
    setMostRecentSearch("quickSearch");
    setQuickSearchCookie(movieTitle);
    setLimitCookie(10);
    setOffsetCookie(0);
    setOrderbyCookie("rating");
    setDirectionCookie("desc");
    console.log("COOKIES: " + document.cookie);

    console.log("SESSIONID: " + getCookie("sessionID"));
    // checkSession();

    $.ajax({
        method: "GET",
        url: url + "/movies/search?title=" + movieTitle,
        contentType: "application/json",
        dataType: "json",
        headers: {
            "email": getCookie("email"),
            "sessionID": getCookie("sessionID")
        },
        // success: function (data, textStatus, response) {
        //     console.log("tranID: " + response.getResponseHeader("transactionID"));
        //     QUICK_SEARCH = true;
        //     getReport(response);
        // }
        statusCode: {
            204: function(data, textStatus, response) {
                console.log("tranID: " + response.getResponseHeader("transactionID"));
                QUICK_SEARCH = true;
                getReport(response);
            },
            200: function () {
                console.log("Got 200 response...");
                $(".main").load("../../html/login.html");

            }
        }

    })
});

// function checkSession() {
//     console.log("CHECKING SESSION!");
//     $.ajax({
//         method: "POST",
//         url: url + "/idm/session",
//         contentType: "application/json",
//         dataType: "json",
//         data: JSON.stringify({
//             "email" : getCookie("email"),
//             "sessionID" : getCookie("sessionID")
//         }),
//         success: function (data, textStatus, response) {
//             console.log("tranID: " + response.getResponseHeader("transactionID"));
//             getReport(response);
//         }
//     })
// }

var movieId;

$(document).on('click', '#movieTitle', function () {
    movieId = $(this).attr("movieId");

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
            DETAIL_GET = true;
            getReport(response);
        }
    })
});

// ADD TO CART //
$("#addToCartForm").submit(function (event) {
    event.preventDefault();

    let quantity = $("#quantity").val();

    $.ajax({
        method: "POST",
        url: url + "/billing/cart/insert",
        contentType: "application/json",
        dataType: "json",
        data: JSON.stringify({
            "email" : getCookie("email"),
            "movieId" : movieId,
            "quantity" : quantity
        }),
        headers: {
            "email": getCookie("email"),
            "sessionID": getCookie("sessionID")
        },
        success: function(data, textStatus, request)
        {
            console.log("Got 200 response(search.js - add to cart)...")
        },
        statusCode:{
            204: function(data, textStatus, response) {
                console.log("Got 204 response (search.js - add to cart)...")
                console.log("tranID: " + response.getResponseHeader("transactionID"));
                getReport(response);

            }
        }

    });
});

$(document).on('click', '#goToCart', function () {
    $(".main").load("../../html/billing/shoppingCart.html");
});

function addSuccess() {
    let modal = document.getElementById("addCart-modal");
    let span = document.getElementsByClassName("close")[0];
    $("#modal-title").html("Thank you! :)");
    $("#modal-body").html("Movie added to your cart!");
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

function dupAdd()
{
    let modal = document.getElementById("addCart-modal");
    let span = document.getElementsByClassName("close")[0];
    $("#modal-title").html("Oops!");
    $("#modal-body").html("Movie is already in your cart!");
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

function invalidQty() {
    let modal = document.getElementById("addCart-modal");
    let span = document.getElementsByClassName("close")[0];
    $("#modal-title").html("Oh NO!");
    $("#modal-body").html("Please re-enter a valid quantity (greater than 1)!");
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

// RATE MOVIE //
var rating;
$("#rateMovie").submit( function (event) {

    event.preventDefault();

    rating = $("#userRating").val();
    console.log("rating: " + rating);
    console.log("movieid: " + movieId);

    $.ajax({
        method: "POST",
        url: url + "/movies/rating",
        contentType: "application/json",
        dataType: "json",
        data: JSON.stringify({
            "id": movieId,
            "rating": rating
        }),
        headers: {
            "email": getCookie("email"),
            "sessionID": getCookie("sessionID")
        },
        success: function(data, textStatus, request)
        {
            console.log("Got 200 response(search.js - rating)...")
        },
        statusCode:{
            204: function(data, textStatus, response) {
                console.log("Got 204 response (search.js - rating)...")
                console.log("tranID: " + response.getResponseHeader("transactionID"));
                getReport(response);
            }
        }
    });
});

function rateSuccess() {
    let modal = document.getElementById("rate-modal");
    let span = document.getElementsByClassName("close")[0];
    $("#rate-modal-title").html("Thanks for Rating! :)");
    $("#rate-modal-body").html("You have rated this movie with a " + rating + "!");
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

function rateFail() {
    let modal = document.getElementById("rate-modal");
    let span = document.getElementsByClassName("close")[0];
    $("#rate-modal-title").html("Oh NO! :(");
    $("#rate-modal-body").html("Something went wrong!<br>Please try again.");
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