// Event handler, the callback function to handle the API response
function handleResult(res) {
    console.log(res);
    debugger;
    let movieDom = $('.movies');
    movieDom.empty(); // Clear the previous results

    // Manually build the HTML Table with the response
    let rowHTML = "<table border=\"1\"><tr><td>Movie ID</td><td>Title</td><td>Director</td>";
    let movieList = res.movieList;
    
    for (let i = 0; i < movieList.length; ++i) {
        rowHTML += "<tr>";
        let movieObject = movieList[i];

        rowHTML += "<td>" + movieObject["movieId"] + "</td>";
        rowHTML += "<td>" + movieObject["title"] + "</td>";
        rowHTML += "<td>" + movieObject["director"] + "</td>";
        rowHTML += "</tr>";
    }
    rowHTML += "</table>";
    debugger;
    movieDom.append(rowHTML);
}

// Overwrite the default submit behaviour of the HTML Form
$("form").submit(function (event) {
        event.preventDefault(); // Prevent the default form submit event, using ajax instead
        
        let title = $(".title").val() // Extract data from search input box to be the title argument
        debugger;
        console.log("Search title: " + title);

        $.ajax({
            method: "GET", // Declare request type
            url: "http://35.235.98.249:8080/api/movies/search?limit=10&offset=10&title=" + title,
            success: handleResult, // Bind event handler as a success callback
        });
    }
);
