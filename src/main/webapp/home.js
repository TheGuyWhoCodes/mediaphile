/*document.getElementsByName('search')
.addEventListener('keyup', function(event) {
    if (event.code == 'Enter') {
        event.preventDefault();
        document.querySelector('form').submit();
    }
}); */

function loadMovies() {
    const search = document.getElementById("search").value;

    fetch('/movies/search?query=' + search +'&pageNumber=' + 1).then(response => response.json())
    .then((content) => {
        const contentList = document.getElementById("movie-list");
        content.forEach((info) => {
            contentList.appendChild(createContentElement(info));
        })
    });
}

function createContentElement(info) {
    const contentElement = document.createElement('li');
    contentElement.className = 'content';
    contentElement.innerText = info;
}

