function loadProfile() {
    fetch('/login/status').then(response => response.json())
    .then((json) => {
        if(json.loggedIn) {
            isLoggedIn = true;
            loadUser(json.id);
            loadQuery(json.id);
        }
    });
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
    fetch('/list/entity?userid=' + id + 'entityType=queue')
    .then(response = response.json())
    .then((queue) => {
        const queueList = document.getElementById('queue-list');
        queue.forEach((content) => {
            queueList.appendChild(content);
        })
    });

    fetch('/list/entity?userid=' + id + 'entityType=viewed')
    .then(response = response.json())
    .then((viewed) => {
        const queueList = document.getElementById('queue-list');
        viewed.forEach((content) => {
            queueList.appendChild(content);
        })
    });
}

function displayContent(content) {
    document.getElementById("queue-list").style.display = "none";
    document.getElementById("watched-read").style.display = "none";

    document.getElementById(content).style.display = "block";
}

function goBack() {
    window.history.back();
}