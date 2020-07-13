function loadProfile() {
    /*fetch('/login/status').then(response => response.json())
    .then((json) => {
        if(json.loggedIn) {
            isLoggedIn = true;
            loadUser(json.id);
            loadQuery(json.id);
        }
    });*/
    let id = localStorage.getItem('user-id');
    postContent(id);
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
    
    fetch('/list/entity?userId=' + id + '&entityType=queue')
    .then(response => response.json())
    .then((queue) => {
        console.log(queue);
        console.log("queue loaded");
    });

    
    fetch('/list/entity?userId=' + id + '&entityType=viewed')
    .then(response => response.json())
    .then((viewed) => {
        console.log("Watched loaded ===========================" + viewed);
            console.log(viewed);
    });
    console.log("step 3");
}

function postContent(ident) {
    const theFile = {id: '4541', title: 'afdsafdsafdsa cool', type:'book',
     entityType: 'queue',artUrl: 'hey.com/coolimage.png', userID: ident};

    fetch('/list/entity', {
        method: 'POST',
        headers: {
            'content-Type': 'applicatoin/json',
        },
        body: JSON.stringify(theFile),
        })
        .then(response => response.json())
        .then(data => {
            console.log('success', data);
        })
        .catch((error) => {
            console.error('Error:', error);
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