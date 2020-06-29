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
            contentList.appendChild(createContentElement(info));
        })
    });
}

function loadBooks() {
    const search = document.getElementById("search").value;

    fetch('/books/search?query=' + search + "&pageNumber=1")
    .then(response => response.json()).then((data) => {
        const contentList = document.getElementById("book-list");
        data.results.forEach((info) => {
            contentList.appendChild(createContentElement(info.volumeInfo));
        })
    });
}

//Creates a box to display the title and description of movies or book.
function createContentElement(info) {
    const contentElement = document.createElement('div');
    contentElement.className = 'content-box';

    const title = document.createElement('div');
    title.className = 'title-box';
    title.innerText = info.title;
    
    const description = document.createElement('div');
    description.className = 'description-box';
    description.innerText = info.description ? info.description : info.overview;

    contentElement.appendChild(title);
    contentElement.appendChild(description);
    return contentElement;
}

//Display's wether the movies or books list appears
function displayContent(content) {
    document.getElementById("movie-list").style.display = "none";
    document.getElementById("book-list").style.display = "none";

    document.getElementById(content).style.display = "block";
}