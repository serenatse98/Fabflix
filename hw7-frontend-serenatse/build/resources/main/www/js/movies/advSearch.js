$("#advSearchForm").submit( function (event) {
    event.preventDefault();

    console.log("making advanced search...");

    let title = $("#search-title").val();
    let director = $("#search-director").val();
    let year = $("#search-year").val();
    let genre = $("#search-genre").val();

    setMostRecentSearch("advSearch");
    setAdvSearchCookie(title, director, year, genre);
    setLimitCookie(10);
    setOffsetCookie(0);
    setOrderbyCookie("rating");
    setDirectionCookie("desc");
    console.log("COOKIES: " + document.cookie);

    console.log("adv. title: " + title);
    console.log("adv. director: " + director);
    console.log("adv. year: " + year);
    console.log("adv. genre: " + genre);

    let searchURL = url + "/movies/search?";

    if (title !== "") {
        searchURL += "title=" + title;
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
});