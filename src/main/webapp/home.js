function loadContent() {
    document.getElementById("movie-tab").style.display = "none";
    document.getElementById("book-tab").style.display = "none";
    document.getElementById("movie-list").innerHTML = "";
    document.getElementById("book-list").innerHTML = "";
    const option = document.getElementById('selection').value;

    //display's which tabs appear according to the selection of the user
    if(option === "movie") {
        document.getElementById("movie-tab").style.display = "block";
        document.getElementById("book-list").style.display = "none";
        loadMovies();
    } else if(option === "book"){
        document.getElementById("book-tab").style.display = "block"
        document.getElementById("movie-list").style.display = "none";
        loadBooks();
    } else {
        document.getElementById("book-list").style.display = "none";
        document.getElementById("movie-tab").style.display = "block"
        loadMovies();

        document.getElementById("book-tab").style.display = "block";
        loadBooks();
    }
}

function loadMovies() {
    const search = document.getElementById("search").value;

    fetch('/movies/search?query=' + search + "&pageNumber=1")
    .then(response => response.json()).then((data) => {
        const contentList = document.getElementById("movie-list");
        data.results.forEach((info) => {
            contentList.appendChild(createMovieElement(info));
        })
    });
}

function loadBooks() {
    const search = document.getElementById("search").value;

    fetch('/books/search?query=' + search + "&pageNumber=1")
    .then(response => response.json()).then((data) => {
        const contentList = document.getElementById("book-list");
        data.results.forEach((info) => {
            contentList.appendChild(createBookElement(info));
        })
    });
}

//Creates a box to display the title of the movies
function createMovieElement(info) {
    const contentElement = document.createElement('div');
    contentElement.innerText = info.title;
    return contentElement;
}

function createBookElement(info) {
    const contentElement = document.createElement('div');
    contentElement.innerText = info.volumeInfo.title;
    return contentElement;
}

//Display's wether the movies or books list appears
function displayContent(content) {
    document.getElementById("movie-list").style.display = "none";
    document.getElementById("book-list").style.display = "none";

    document.getElementById(content).style.display = "block";
}