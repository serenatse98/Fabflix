function limitChange(self) {
    let limit = self.value;
    console.log("new limit: " + limit);
    setLimitCookie(limit);

    let orderby = getCookie("orderby");
    let direction = getCookie("direction");

    let offset = $("#change-offset").val();
    offset = (offset < 0 || offset % limit !== 0) ? 0 : offset;
    setOffsetCookie(offset);

    console.log("COOKIES: " + document.cookie);

    if (getCookie("recentSearch") === "advSearch") {
        let title = getCookie("title");
        let director = getCookie("director");
        let year = getCookie("year");
        let genre = getCookie("genre");

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
        getQuickMovies(offset, limit, orderby, direction);
    }

    else if (getCookie("recentSearch") === "letterSearch") {
        getMoviesByLetter(offset, limit, orderby, direction);
    }

    else if (getCookie("recentSearch") === "genreSearch") {
        getMoviesByGenre(offset, limit, orderby, direction);
    }
}

$(document).on('click', '#changeOffsetBtn', function () {
    let offset = $("#change-offset").val();
    let limit = $("#change-limit").val();
    let orderby = getCookie("orderby");
    let direction = getCookie("direction");

    offset = (offset < 0 || offset % limit !== 0) ? 0 : offset;

    console.log("new offset: " + offset);
    setOffsetCookie(offset);
    console.log("COOKIES: " + document.cookie);

    if (getCookie("recentSearch") === "advSearch") {
        let title = getCookie("title");
        let director = getCookie("director");
        let year = getCookie("year");
        let genre = getCookie("genre");


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
        getQuickMovies(offset, limit, orderby, direction);
    }

    else if (getCookie("recentSearch") === "letterSearch") {
        getMoviesByLetter(offset, limit, orderby, direction);
    }

    else if (getCookie("recentSearch") === "genreSearch") {
        getMoviesByGenre(offset, limit, orderby, direction);
    }
});



// CHANGE SORTING //
$(document).on('click', '#ratingSort-desc', function () {
    console.log("sorting by best rating");
    setOrderbyCookie("rating");
    setDirectionCookie("desc");

    let orderby = getCookie("orderby");
    let direction = getCookie("direction");
    let offset = getCookie("offset");
    let limit = getCookie("limit");

    reGetMovies(offset, limit, orderby, direction)
});

$(document).on('click', '#ratingSort-asc', function () {
    console.log("sorting by worst rating");
    setOrderbyCookie("rating");
    setDirectionCookie("asc");

    let orderby = getCookie("orderby");
    let direction = getCookie("direction");
    let offset = getCookie("offset");
    let limit = getCookie("limit");

    reGetMovies(offset, limit, orderby, direction)
});

$(document).on('click', '#titleSort-asc', function () {
    console.log("sorting by title a-z");
    setOrderbyCookie("title");
    setDirectionCookie("asc");

    let orderby = getCookie("orderby");
    let direction = getCookie("direction");
    let offset = getCookie("offset");
    let limit = getCookie("limit");

    reGetMovies(offset, limit, orderby, direction)
});

$(document).on('click', '#titleSort-desc', function () {
    console.log("sorting by title z-a");
    setOrderbyCookie("title");
    setDirectionCookie("desc");

    let orderby = getCookie("orderby");
    let direction = getCookie("direction");
    let offset = getCookie("offset");
    let limit = getCookie("limit");

    reGetMovies(offset, limit, orderby, direction)
});

function reGetMovies(offset, limit, orderby, direction) {
    if (getCookie("recentSearch") === "advSearch") {
        let title = getCookie("title");
        let director = getCookie("director");
        let year = getCookie("year");
        let genre = getCookie("genre");


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
        getQuickMovies(offset, limit, orderby, direction);
    }

    else if (getCookie("recentSearch") === "letterSearch") {
        getMoviesByLetter(offset, limit, orderby, direction);
    }

    else if (getCookie("recentSearch") === "genreSearch") {
        getMoviesByGenre(offset, limit, orderby, direction);
    }
}


