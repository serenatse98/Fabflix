function setCookie(email, sessionID) {
    document.cookie = "sessionID=" + escape(sessionID);
    document.cookie = "email=" + escape(email);
    console.log("COOKIE: " + document.cookie);
}

function setMostRecentSearch(value) {
    document.cookie = "recentSearch=" + escape(value);
}

function setQuickSearchCookie(title) {
    document.cookie = "title=" + escape(title);
}

function setAdvSearchCookie(title, director, year, genre) {
    document.cookie = "title=" + escape(title);
    document.cookie = "director=" + escape(director);
    document.cookie = "year=" + escape(year);
    document.cookie = "genre=" + escape(genre);
}

function setGenreCookie(genre) {
    document.cookie = "genre=" + escape(genre);
}

function setOffsetCookie(offset) {
    document.cookie = "offset=" + escape(offset);
}

function setLimitCookie(limit) {
    document.cookie = "limit=" + escape(limit);
}

function setOrderbyCookie(orderby) {
    document.cookie = "orderby=" + escape(orderby);
}

function setDirectionCookie(direction) {
    document.cookie = "direction=" + escape(direction);
}

function getCookie(cname) {
    var name = cname + "=";
    var decodedCookie = decodeURIComponent(document.cookie);
    var decookie = decodedCookie.split(';');
    for(var i = 0; i < decookie.length; i++) {
        var c = decookie[i];
        while (c.charAt(0) === ' ') {
            c = c.substring(1);
        }
        if (c.indexOf(name) === 0) {
            return c.substring(name.length, c.length);
        }
    }
    return "";
}