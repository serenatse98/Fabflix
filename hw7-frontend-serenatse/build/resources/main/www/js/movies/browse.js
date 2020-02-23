// SEARCH BY FIRST LETTER //
$(document).on('click', '.letterLink', function () {
    $(".main").load("../../html/movies/searchMovie.html");
});

function getMovies(letter) {
    setMostRecentSearch("letterSearch");
    setQuickSearchCookie(letter);
    setLimitCookie(10);
    setOffsetCookie(0);
    setOrderbyCookie("rating");
    setDirectionCookie("desc");
    console.log("COOKIES: " + document.cookie);

    $.ajax({
        method: "GET",
        url: url + "/movies/search/" + letter,
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

// SEARCH BY GENRE //
$(document).on('click', '.genreLink', function () {
    $(".main").load("../../html/movies/searchMovie.html");
});

function getMoviesByGenre(genre) {
    console.log("getting movies by genre search");

    setMostRecentSearch("genreSearch");
    setGenreCookie(genre);
    setLimitCookie(10);
    setOffsetCookie(0);
    setOrderbyCookie("rating");
    setDirectionCookie("desc");

    $.ajax({
        method: "GET",
        url: url + "/movies/search?genre=" + genre,
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

function getGenres() {
    console.log("getting genres...");

    $.ajax({
        method: "GET",
        url: url + "/movies/genre",
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
    })

}

function showGenres(res) {
    console.log(res);

    let genresDom = $("#genres");
    genresDom.empty();

    let genreList = res["genres"];
    let len = genreList.length;
    for (let i = 0; i < len; ++i)
    {
        let a = document.createElement("a");
        a.setAttribute("class", "genreLink");
        a.setAttribute("id", genreList[i]["name"]);
        a.onclick = function () { getMoviesByGenre(genreList[i]["name"]); };
        let genre = document.createTextNode(genreList[i]["name"]);

        let p = document.createElement("p");
        p.setAttribute("class", "line");
        let line = document.createTextNode(" | ");

        let br = document.createElement("br");

        a.appendChild(genre);
        p.appendChild(line);

        if (i % 8 === 0 && i !== 0)
        {
            genresDom.append(br);
        }

        genresDom.append(a);
        if (i !== len-1 || i % 8 !== 0) {
            genresDom.append(p);
        }
    }

}