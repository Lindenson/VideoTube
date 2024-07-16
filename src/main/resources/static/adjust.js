
let pauseDebounceTimer;

function resizeHeight() {
    const vh = window.innerHeight * 0.01;
    document.getElementById('app').style.height = `${vh*100}px`;
    document.getElementById('footer').style.maxHeight = '3em';
}

function setupVideoControls() {
    let videos = document.getElementsByTagName("video");
    for (let i = 0; i < videos.length; i++) {
        videos[i].addEventListener("click", this.handleVideoClick);
        videos[i].addEventListener("playing", this.handleVideoPlaying);
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
        }
    }, 50);
}

function renewVideoTags() {
    const videos = document.getElementsByTagName('video');
    for (let i = 0; i < videos.length; i++) {
        const currentTime = new Date().getTime(); // Get current timestamp
        const source = videos[i].getElementsByTagName('source')[0]
        const originalSrc = source.src
        const newSrc = `${originalSrc}?t=${currentTime}`;
        source.setAttribute('src', newSrc);
        videos[i].load();
        videos[i].pause();
    }
}

window.addEventListener('load', resizeHeight);
window.addEventListener('resize', resizeHeight);
window.addEventListener('orientationchange', resizeHeight);

resizeHeight()

