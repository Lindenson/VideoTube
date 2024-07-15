function resizeHeight() {
    const vh = window.innerHeight * 0.01;
    document.getElementById('app').style.height = `${vh*100}px`;
    document.getElementById('footer').style.maxHeight = '3em';
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

resizeHeight()
window.addEventListener('load', resizeHeight);
window.addEventListener('resize', resizeHeight);
window.addEventListener('orientationchange', resizeHeight);