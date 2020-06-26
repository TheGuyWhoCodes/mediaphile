function loadContent() {
    document.getElementById("movie-tab").style.display = "none";
    document.getElementById("book-tab").style.display = "none";
    const option = document.getElementById('selection').value;

    if(option === "movie") {
        document.getElementById("movie-tab").style.display = "block";
        loadMovies();
    } else if(option === "book"){
        document.getElementById("book-tab").style.display = "block"
        loadBooks();
    } else {
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
            contentList.appendChild(createContentElement(info));
        })
    });
}

function loadBooks() {
    console.log("book-search is unavailable at the moment");
    /*const search = document.getElementById("search").value;

    fetch('/books/search?query=' + search + "&pageNumber=1")
    .then(response => response.json()).then((data) => {
        const contentList = document.getElementById("book-list");
        data.results.forEach((info) => {
            contentList.appendChild(createContentElement(info));
        })
    });*/
}

//Creates an li to display the title of the movies
function createContentElement(info) {
    const contentElement = document.createElement('li');
    contentElement.innerText = info.title;
    return contentElement;
}

function displayContent(content) {
    document.getElementById("movie-list").style.display = "none";
    document.getElementById("book-list").style.display = "none";

    document.getElementById(content).style.display = "block";
}