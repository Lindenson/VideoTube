if (window.location.hostname !== 'localhost') {
    Vue.config.productionTip = false;
}

new Vue({
    el: '#app',
    data: {
        videos: [
            {id: 1, src: 'files/1#t=0.1', tag: 'home', name: 'My video'},
            {id: 2, src: 'files/2#t=0.1', tag: 'home', name: 'My video'},
            {id: 3, src: 'files/3#t=0.1', tag: 'home', name: 'My video'},
            {id: 4, src: 'files/4#t=0.1', tag: 'home', name: 'My video'},
            {id: 5, src: 'files/5#t=0.1', tag: 'home', name: 'My video'},
            {id: 6, src: 'files/6#t=0.1', tag: 'home', name: 'My video'}
        ],
        toasts: [],
        toastType: true,
        uploadDisabled: true
    },
    methods: {
        showLoginForm() {
            $('#loginModal').modal('show');
        },
        showUploadFileForm() {
            if (this.uploadDisabled) return;
            $('#uploadFileModal').modal('show');
        },
        logout() {
            localStorage.removeItem('jwt');
            this.uploadDisabled = true;
            this.showToast('Logout', 'Good bye')
        },
        handleLogin(result) {
            if (result.includes('Success')) {
                this.uploadDisabled = false;
                this.showToast('Login', result);
                return;
            }
            this.showToast('Login', result, false);
        },
        handleFileUpload(result) {
            if (result.includes('Not authorized')) {
                this.uploadDisabled = true;
                this.showToast('Upload', result, false);
                return;
            }
            this.showToast('Upload', result)
        },
        showToast(title, body, good= true) {
            this.toastType=good
            console.log(this.toastType)
            this.toasts.push({title, body});
            this.$nextTick(() => {
                const toastElements = this.$refs.toasts;
                const lastToast = toastElements[toastElements.length - 1];
                $(lastToast).toast('show');
                setTimeout(() => {
                    this.toasts.shift();
                }, 2000); // Hide after 5 seconds
            });
        },
        removeToast(index) {
            this.toasts.splice(index, 1);
        },
        setupVideoControls() {
            var pauseDebounceTimer;
            var videos = document.getElementsByTagName("video");
            for (var i = 0; i < videos.length; i++) {
                videos[i].addEventListener("click", this.handleVideoClick);
                videos[i].addEventListener("playing", this.handleVideoPlaying);
                videos[i].addEventListener("pause", (event) => this.handleVideoPause(event, pauseDebounceTimer));
            }
        },
        handleVideoClick(event) {
            if (event.target.paused) {
                event.target.play();
            }
        },
        handleVideoPlaying(event) {
            if (!event.target.hasAttribute("controls")) {
                event.target.setAttribute("controls", "controls");
            }
        },
        handleVideoPause(event, pauseDebounceTimer) {
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
    },
    mounted() {
        this.setupVideoControls();
    }
});