function handle(event) {
    if(event.keyCode === 13) {
        console.log(event.keyCode);
        event.preventDefault();
        loadContent();
    }
}

function loadContent() {
    document.getElementById("movie-tab").style.display = "none"
    document.getElementById("book-tab").style.display = "none"
    document.getElementById("movie-list").innerHTML = "";
    document.getElementById("book-list").innerHTML = "";
    const option = document.getElementById('selection').value;

    //display's which tabs appear according to the selection of the user
    if(option === "movie") {
        document.getElementById("movie-tab").style.display = "block";
        displayContent("movie-list");
        loadMovies();
    } else if(option === "book"){
        document.getElementById("book-tab").style.display = "block"
        displayContent("book-list");
        loadBooks();
    } else {
        displayContent("movie-list")
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
            contentList.appendChild(createContentElement(info, null));
        })
    });
}

function loadBooks() {
    const search = document.getElementById("search").value;

    fetch('/books/search?query=' + search + "&pageNumber=0")
    .then(response => response.json()).then((data) => {
        const contentList = document.getElementById("book-list");
        data.results.forEach((info) => {
            contentList.appendChild(createContentElement(info.volumeInfo, info));
        })
    });
}

//Creates a box to display the title and description of movies or book.
function createContentElement(infoMain, infoSide) {
    const contentElement = document.createElement('div');
    contentElement.className = 'content-box';
    contentElement.value = infoMain.id ? infoMain.id : infoSide.id.toString();

    const title = document.createElement('div');
    title.className = 'title-box';
    title.innerText = infoMain.title;
    
    const description = document.createElement('div');
    description.className = 'description-box';
    description.innerText = infoMain.description ? infoMain.description : infoMain.overview;

    contentElement.appendChild(title);
    contentElement.appendChild(description);

    contentElement.setAttribute("onclick", "contentDetails(\""+ contentElement.value + "\")");
    
    return contentElement;
}

//Display's whether the movies or books list appears
function displayContent(content) {
    document.getElementById("movie-list").style.display = "none";
    document.getElementById("book-list").style.display = "none";

    document.getElementById(content).style.display = "block";
}

function contentDetails(id) {
    let type = "book"
    if(document.getElementById("movie-list").style.display === "block") {
        type = "movie";
    }
    localStorage.setItem('ident', id);
    localStorage.setItem('type', type);
    window.document.location = 'contentDetails/content.html';
}