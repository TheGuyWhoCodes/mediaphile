function goBack() {
    window.history.back();
}

function getBookDetails(id) {
    fetch('/books/details?id=' + id)
    .then(response => response.json()).then((info) => {
        console.log(info);
    });
}