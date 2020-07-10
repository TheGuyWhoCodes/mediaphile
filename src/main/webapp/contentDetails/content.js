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
        console.log(info.volumeInfo);
    });
}

function getMovieDetails(id) {
    fetch('/movies/details?id=' + id)
    .then(response => response.json()).then((info) => {
        const infoElement = document.getElementById('details-element');
        infoElement.appendChild(movieDetailElement(info));
    });
}

function movieDetailElement(info) {
    const detailElement = document.createElement('div');
    detailElement.className = 'info-box';

    const title = document.createElement('div');
    title.setAttribute("id", "title");
    title.innerText = info.title;

    const bar = document.createElement('div');
    bar.className = 'details-bar';

    const date = document.createElement('div');
    date.className = 'details';
    date.innerText = "release:" + info.releaseDate;

    const runtime = document.createElement('div');
    runtime.className = 'details';
    runtime.innerText = info.runtime + "min";

    const rating = document.createElement('div');
    rating.className = 'details';
    rating.innerText = "rating:" + info.voteAverage;

    const origLang = document.createElement('div');
    origLang.setAttribute("id", "orig-lang");
    origLang.innerText = "rating:" + info.voteAverage;

    const overview = document.createElement('div');
    overview.setAttribute("id", "description");
    overview.innerText = info.overview;

    bar.appendChild(date);
    bar.appendChild(runtime);
    bar.appendChild(rating);
    bar.appendChild(origLang);

    detailElement.appendChild(title);
    detailElement.appendChild(bar);
    detailElement.appendChild(overview);

    return detailElement;
}