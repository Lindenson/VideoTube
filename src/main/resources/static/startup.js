if (window.location.hostname !== 'localhost') {
    Vue.config.productionTip = false;
}

new Vue({
    el: '#app',
    data: {
        videos: [
            {id: 1, src: 'files/1#t=0.1'},
            {id: 2, src: 'files/2#t=0.1'},
            {id: 3, src: 'files/3#t=0.1'},
            {id: 4, src: 'files/4#t=0.1'},
            {id: 5, src: 'files/5#t=0.1'},
            {id: 6, src: 'files/6#t=0.1'}
        ],
        toasts: [],
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
            this.showToast('Login', result);
            if (result.includes('Success')) this.uploadDisabled = false;
        },
        handleFileUpload(result) {
            if (result.includes('Not authorized')) this.uploadDisabled = true;
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