function loadProfile() {
    var queryParams = new URLSearchParams(window.location.search);
    let id = queryParams.get('id');
    loadUser(id);
    loadQuery(id);
}

function loadUser(id) {
    fetch('/user?id=' + id)
    .then(response => response.json())
    .then((user) => {
        document.getElementById("username").innerText = user.username;
        document.getElementById("profile-pic").innerText = user.username.charAt(0);
    });
}

function loadQuery(id) {
    
    fetch('/list/entity?userId=' + id + '&listType=queue')
    .then(response => response.json())
    .then((queue) => {
        const queueList = document.getElementById('queue-list');
        queue.forEach((info) => {
            viewedList.appendChild(createList(info));
        })
    });

    
    fetch('/list/entity?userId=' + id + '&listType=viewed')
    .then(response => response.json())
    .then((viewed) => {
        const viewedList = document.getElementById('watched-read');
        viewed.forEach((info) => {
            viewedList.appendChild(createList(info));
        })
    });
}

function displayContent(content) {
    document.getElementById("queue-list").style.display = "none";
    document.getElementById("watched-read").style.display = "none";

    document.getElementById(content).style.display = "block";
}

function createList(info) {
    const box = document.createElement('div');
    box.className = 'box-info'
    box.value = info.mediaId;

    const title = document.createElement('div');
    title.className = 'title';
    title.innerText = info.title;

    const type = document.createElement('div');
    type.className = 'type';
    type.innerText = info.mediaType;

    box.appendChild(title);
    box.appendChild(type);
    box.setAttribute("onclick", "contentDetails(\""+ box.value + "\", " + type.innerText +")");
    return box;
}

function contentDetails(id, type) {
    window.document.location = 'contentDetails/content.html?ident=' + id + '&type=' + type;
}

function goBack() {
    window.history.back();
}