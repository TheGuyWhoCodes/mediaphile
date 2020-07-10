function goBack() {
    window.history.back();
}

function loadDetails() {
    let id = localStorage.getItem('ident');
    let type = localStorage.getItem('type');

    if(type === "movie") {
        getMovieDetails(id);
    } else {
        getBookDetails(id);
    }
}

function getBookDetails(id) {
    fetch('/books/details?id=' + id)
    .then(response => response.json()).then((info) => {
        console.log(info);
    });
}

function getMovieDetails(id) {
    fetch('/movies/details?id=' + id)
    .then(response => response.json()).then((info) => {
        document.getElementById('title').innerText = info.title;
        //document.getElementById('image').src = info.backdropPath;
        document.getElementById('date').innerText = "release: " + info.releaseDate;
        document.getElementById('runtime').innerText = info.runtime + "min";
        document.getElementById('rating').innerText = "rating:" + info.voteAverage;
        document.getElementById('orig-lang').innerText = info.originalLanguage;
        document.getElementById('description').innerText = info.overview;

        console.log(info);
    });
}