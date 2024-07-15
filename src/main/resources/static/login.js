Vue.component('login-modal', {
    template: `
            <div class="modal fade" id="loginModal" tabindex="-1" role="dialog" aria-labelledby="loginModalLabel" aria-hidden="true">
                <div class="modal-dialog" role="document">
                    <div class="modal-content modal-dark">
                        <div class="modal-header">
                            <h5 class="modal-title" id="loginModalLabel">Login</h5>
                            <button type="button" class="close" data-dismiss="modal" aria-label="Close">
                                <span aria-hidden="true">&times;</span>
                            </button>
                        </div>
                        <div class="modal-body">
                            <form @submit.prevent="submitForm">
                                <div class="form-group">
                                    <label for="username">Username</label>
                                    <input type="text" id="username" v-model="username" class="form-control" required>
                                </div>
                                <div class="form-group">
                                    <label for="password">Password</label>
                                    <input type="password" id="password" v-model="password" class="form-control" required>
                                </div>
                                <button type="submit" class="btn btn-primary">Login</button>
                                <button type="button" class="btn btn-secondary" data-dismiss="modal">Cancel</button>
                            </form>
                        </div>
                    </div>
                </div>
            </div>
        `,
    data() {
        return {
            username: '',
            password: ''
        };
    },
    methods: {
        submitForm() {
            this.$emit('login', { username: this.username, password: this.password });
            $('#loginModal').modal('hide');
        }
    }
});