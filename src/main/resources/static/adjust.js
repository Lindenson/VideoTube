let pauseDebounceTimer;
let videos;


function setupVideoControls() {
    for (let i = 0; i < videos.length; i++) {
        videos[i].addEventListener("click", this.handleVideoClick);
        videos[i].addEventListener("playing", this.handleVideoPlaying);
        videos[i].addEventListener("canplay", this.stopSpinnerEndResize);
        videos[i].addEventListener("pause", (event) => this.handleVideoPause(event, pauseDebounceTimer));
    }
}

function handleVideoClick(event) {
    if (event.target.paused) {
        event.target.play();
    }
}

function handleVideoPlaying(event) {
    if (!event.target.hasAttribute("controls")) {
        event.target.setAttribute("controls", "controls");
    }
}

function handleVideoPause(event, pauseDebounceTimer) {
    if (pauseDebounceTimer) {
        clearTimeout(pauseDebounceTimer);
    }
    pauseDebounceTimer = setTimeout(() => {
        if (event.target.hasAttribute("controls")) {
            event.target.removeAttribute("controls");
            event.target.pause();
            this.stopSpinnerEndResize()
        }
    }, 70);
}

function renewVideoTags() {
    for (let i = 0; i < videos.length; i++) {
        videos[i].load();
    }
}

function setSpinner() {
    videos = document.getElementsByTagName("video");
    for (let i = 0; i < videos.length; i++) {
        let parent = videos[i].parentNode;
        let spinner = parent.querySelector('.spinner');
        spinner.style.display = 'block';
    }
}

function resize(){
    const vh = window.innerHeight * 0.01;
    document.getElementById('app').style.height = `${vh * 100}px`;
    document.getElementById('footer').style.maxHeight = '3em';
}

function stopSpinnerEndResize(event) {
    let parent = event.target.parentNode;
    let spinner = parent.querySelector('.spinner');
    spinner.style.display = 'none';
    resize();
}

window.addEventListener('load', stopSpinnerEndResize);
window.addEventListener('resize', stopSpinnerEndResize);
window.addEventListener('orientationchange', stopSpinnerEndResize);
resize();


