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
        ]
    },
    methods: {
        showLoginForm() {
            $('#loginModal').modal('show');
        },
        showUploadFileForm() {
            $('#uploadFileModal').modal('show');
        },
        handleLogin(user) {
            alert(`${user} logged in`);
        },
        handleFileUpload(file) {
            alert(`Uploaded, transferred ${file} bites`);
        }
    }
});