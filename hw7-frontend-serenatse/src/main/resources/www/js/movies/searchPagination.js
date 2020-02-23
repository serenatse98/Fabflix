function getAdvMovies(searchURL) {
    $.ajax({
        method: "GET",
        url: searchURL,
        contentType: "application/json",
        dataType: "json",
        headers: {
            "email": getCookie("email"),
            "sessionID": getCookie("sessionID")
        },
        success: function (data, textStatus, response) {
            console.log("tranID: " + response.getResponseHeader("transactionID"));
            QUICK_SEARCH = true;
            getReport(response);
        }
    });
}

function getQuickMovies(offset, limit, orderby, direction) {
    let movieTitle = getCookie("title");
    $.ajax({
        method: "GET",
        url: url + "/movies/search?title=" + movieTitle + "&offset=" + offset + "&limit=" + limit +
            "&orderby=" + orderby + "&direction=" + direction,
        contentType: "application/json",
        dataType: "json",
        headers: {
            "email": getCookie("email"),
            "sessionID": getCookie("sessionID")
        },
        success: function (data, textStatus, response) {
            console.log("tranID: " + response.getResponseHeader("transactionID"));
            QUICK_SEARCH = true;
            getReport(response);
        }
    })
}

function getMoviesByLetter(offset, limit, orderby, direction) {
    let letter = getCookie("title");
    $.ajax({
        method: "GET",
        url: url + "/movies/search/" + letter + "?offset=" + offset + "&limit=" + limit  +
            "&orderby=" + orderby + "&direction=" + direction,
        contentType: "application/json",
        dataType: "json",
        headers: {
            "email": getCookie("email"),
            "sessionID": getCookie("sessionID")
        },
        success: function (data, textStatus, response) {
            console.log("tranID: " + response.getResponseHeader("transactionID"));
            LETTER_SEARCH = true;
            getReport(response);
        }
    });
}

function getMoviesByGenre(offset, limit, orderby, direction) {
    let genre = getCookie("genre");
    $.ajax({
        method: "GET",
        url: url + "/movies/search?genre=" + genre + "&offset=" + offset + "&limit=" + limit +
            "&orderby=" + orderby + "&direction=" + direction,
        contentType: "application/json",
        dataType: "json",
        headers: {
            "email": getCookie("email"),
            "sessionID": getCookie("sessionID")
        },
        success: function (data, textStatus, response) {
            console.log("tranID: " + response.getResponseHeader("transactionID"));
            QUICK_SEARCH = true;
            getReport(response);
        }
    })
}


$(document).on('click', '#nextPage', function () {
    if (getCookie("recentSearch") === "advSearch") {
        let title = getCookie("title");
        let director = getCookie("director");
        let year = getCookie("year");
        let genre = getCookie("genre");
        let orderby = getCookie("orderby");
        let direction = getCookie("direction");

        setOffsetCookie(Number(getCookie("offset"))+Number(getCookie("limit")));
        let offset = Number(getCookie("offset"));
        let limit = Number(getCookie("limit"));
        console.log("COOKIES: " + document.cookie);

        let searchURL = url + "/movies/search?offset=" + offset + "&limit=" + limit +
            "&orderby=" + orderby + "&direction=" + direction;

        if (title !== "") {
            searchURL += "&title=" + title;
        }

        if (director !== "") {
            searchURL += "&director=" + director;
        }

        if (year !== "") {
            searchURL += "&year=" + year;
        }

        if (genre !== "") {
            searchURL += "&genre=" + genre;
        }

        console.log("URL: " + searchURL);
        getAdvMovies(searchURL);

    }

    else if (getCookie("recentSearch") === "quickSearch") {
        setOffsetCookie(Number(getCookie("offset"))+Number(getCookie("limit")));
        let offset = Number(getCookie("offset"));
        let limit = Number(getCookie("limit"));
        let orderby = getCookie("orderby");
        let direction = getCookie("direction");
        getQuickMovies(offset, limit, orderby, direction);
    }

    else if (getCookie("recentSearch") === "letterSearch") {
        setOffsetCookie(Number(getCookie("offset"))+Number(getCookie("limit")));
        let offset = Number(getCookie("offset"));
        let limit = Number(getCookie("limit"));
        let orderby = getCookie("orderby");
        let direction = getCookie("direction");
        getMoviesByLetter(offset, limit, orderby, direction);
    }

    else if (getCookie("recentSearch") === "genreSearch") {
        setOffsetCookie(Number(getCookie("offset"))+Number(getCookie("limit")));
        let offset = Number(getCookie("offset"));
        let limit = Number(getCookie("limit"));
        let orderby = getCookie("orderby");
        let direction = getCookie("direction");
        getMoviesByGenre(offset, limit, orderby, direction);
    }
    $("#change-offset").val(getCookie("offset"));
});

$(document).on('click', '#backPage', function () {
    if (getCookie("recentSearch") === "advSearch") {
        let title = getCookie("title");
        let director = getCookie("director");
        let year = getCookie("year");
        let genre = getCookie("genre");
        let orderby = getCookie("orderby");
        let direction = getCookie("direction");

        setOffsetCookie(Number(getCookie("offset"))-Number(getCookie("limit")));
        let offset = Number(getCookie("offset"));
        let limit = Number(getCookie("limit"));
        console.log("COOKIES: " + document.cookie);

        if (offset < 0) {
            setOffsetCookie(0);
        }

        let searchURL = url + "/movies/search?offset=" + offset + "&limit=" + limit +
            "&orderby=" + orderby + "&direction=" + direction;

        if (title !== "") {
            searchURL += "&title=" + title;
        }

        if (director !== "") {
            searchURL += "&director=" + director;
        }

        if (year !== "") {
            searchURL += "&year=" + year;
        }

        if (genre !== "") {
            searchURL += "&genre=" + genre;
        }

        console.log("URL: " + searchURL);
        getAdvMovies(searchURL);

    }

    else if (getCookie("recentSearch") === "quickSearch") {
        setOffsetCookie(Number(getCookie("offset"))-Number(getCookie("limit")));
        let offset = Number(getCookie("offset"));
        let limit = Number(getCookie("limit"));
        let orderby = getCookie("orderby");
        let direction = getCookie("direction");
        if (offset < 0) {
            setOffsetCookie(0);
        }
        getQuickMovies(offset, limit, orderby, direction);
    }

    else if (getCookie("recentSearch") === "letterSearch") {
        setOffsetCookie(Number(getCookie("offset"))-Number(getCookie("limit")));
        let offset = Number(getCookie("offset"));
        let limit = Number(getCookie("limit"));
        let orderby = getCookie("orderby");
        let direction = getCookie("direction");
        if (offset < 0) {
            setOffsetCookie(0);
        }
        getMoviesByLetter(offset, limit, orderby, direction);
    }

    else if (getCookie("recentSearch") === "genreSearch") {
        setOffsetCookie(Number(getCookie("offset"))-Number(getCookie("limit")));
        let offset = Number(getCookie("offset"));
        let limit = Number(getCookie("limit"));
        let orderby = getCookie("orderby");
        let direction = getCookie("direction");
        if (offset < 0) {
            setOffsetCookie(0);
        }
        getMoviesByGenre(offset, limit, orderby, direction);
    }
    $("#change-offset").val(getCookie("offset"));

});
