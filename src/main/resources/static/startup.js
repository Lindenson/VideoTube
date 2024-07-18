if (window.location.hostname !== 'localhost') {
    Vue.config.productionTip = false;
}

new Vue({
    el: '#app',
    data: {
        videos: Array(6).fill().map((_, i) => ({
            id: i + 1,
            src: '',
            tag: '',
            name: '',
            exists: true,
        })),
        toasts: [],
        toastType: true,
        uploadDisabled: true,
        currentPage: 0,
        existsMore: true,
        refreshMarker: ''
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
            this.showToast('Logout', 'Good bye');
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
            this.showToast('Upload', result);
            this.needRefresh();
            this.fetchVideos();
        },
        showToast(title, body, good = true) {
            this.toastType = good;
            this.toasts.push({title, body});
            this.$nextTick(() => {
                const toastElements = this.$refs.toasts;
                const lastToast = toastElements[toastElements.length - 1];
                $(lastToast).toast('show');
                setTimeout(() => {
                    this.toasts.shift();
                }, 2000); // Hide after 2 seconds
            });
        },
        removeToast(index) {
            this.toasts.splice(index, 1);
        },
        pageUp() {
            if (!this.existsMore) return;
            this.currentPage++;
            this.fetchVideos();
        },
        pageDown() {
            if (this.currentPage === 0) return;
            this.currentPage--;
            this.fetchVideos();
        },
        pageFirst() {
            if (this.currentPage === 0) return;
            this.currentPage = 0;
            this.fetchVideos();
        },
        updateVideoTags(updatedVideos) {
            this.videos.forEach((video, index) => {
                if (updatedVideos[index]) {
                    video.name = updatedVideos[index].name;
                    video.tag = updatedVideos[index].tag;
                    video.exists = true;
                    const newId = index + 1 + this.currentPage * 6;
                    video.src = `files/${newId}#t=0.1?v=${this.refreshMarker}`;
                } else {
                    video.exists = false;
                    video.src = '';
                }
            });
        }, fetchVideos() {
            setSpinner();
            axios.get(`/names/${this.currentPage}`)
                .then(response => {
                    const updatedVideos = response.data[0];
                    this.existsMore = response.data[1];
                    if (updatedVideos.length === 0) return;
                    this.updateVideoTags(updatedVideos);
                    renewVideoTags();
                })
                .catch(error => {
                    console.error("Error fetching videos:", error);
                    this.showToast('Sever error', 'Failed to load videos', false);
                });
        },
        needRefresh(){
            this.refreshMarker = new Date().getTime();
        },
    },
    mounted() {
        this.needRefresh();
        this.fetchVideos();
        setupVideoControls();
    }
});