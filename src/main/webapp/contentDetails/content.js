function goBack() {
    window.history.back();
}

function loadDetails() {
    let id = localStorage.getItem('ident');
    let type = localStorage.getItem('type');
    console.log(id);
    console.log(type);

}

function getBookDetails(id) {
    fetch('/books/details?id=' + id)
    .then(response => response.json()).then((info) => {
        console.log(info);
    });
}