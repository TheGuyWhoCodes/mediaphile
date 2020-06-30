function loadMovies() {
    const search = document.getElementById("search").value;

    fetch('/movies/search?query=' + search + "&pageNumber=1").then(response => response.json()).then((data) => {
        const contentList = document.getElementById("movie-list");
        data.results.forEach((info) => {
            contentList.appendChild(createContentElement(info));
        })
    });
}

//Creates an li to display the title of the movies
function createContentElement(info) {
    const contentElement = document.createElement('li');
    contentElement.innerText = info.title;
    return contentElement;
}