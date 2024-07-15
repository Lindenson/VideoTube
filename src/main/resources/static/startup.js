if (window.location.hostname !== 'localhost') {
    Vue.config.productionTip = false;
}

new Vue({
    el: '#app',
    data: {
        videos: [
            {id: 1, src: 'files/1'},
            {id: 2, src: 'files/2'},
            {id: 3, src: 'files/3'},
            {id: 4, src: 'files/4'},
            {id: 5, src: 'files/5'},
            {id: 6, src: 'files/6'}
        ],
        toasts: []
    },
    methods: {
        showLoginForm() {
            $('#loginModal').modal('show');
        },
        showUploadFileForm() {
            $('#uploadFileModal').modal('show');
        },
        handleLogin(result) {
            this.showToast('Login', result)
        },
        handleFileUpload(result) {
            this.showToast('Upload', result)
        },
        showToast(title, body) {
            this.toasts.push({ title, body });
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
        }
    }
});