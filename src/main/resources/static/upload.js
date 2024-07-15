Vue.component('upload-file-modal', {
    template: `
      <div class="modal fade" id="uploadFileModal" tabindex="-1" role="dialog" aria-labelledby="uploadFileModalLabel" aria-hidden="true">
        <div class="modal-dialog" role="document">
          <div class="modal-content modal-dark">
            <div class="modal-header">
              <h5 class="modal-title" id="uploadFileModalLabel">Upload File</h5>
              <button type="button" class="close" data-dismiss="modal" aria-label="Close">
                <span aria-hidden="true">&times;</span>
              </button>
            </div>
            <div class="modal-body">
              <form @submit.prevent="submitForm">
                <div class="form-group">
                  <label for="fileName">File Name</label>
                  <input type="text" id="fileName" v-model="fileName" class="form-control" required maxlength="20">
                </div>
                <div class="form-group">
                  <label>Tag</label>
                  <div>
                    <button
                      v-for="tagOption in tagOptions"
                      :key="tagOption"
                      type="button"
                      class="btn btn-outline-primary"
                      @click="selectTag(tagOption)"
                      :class="{ active: tag === tagOption }"
                    >
                      {{ tagOption }}
                    </button>
                  </div>
                </div>
                <div class="form-group">
                  <label for="file">Choose file</label>
                  <input type="file" id="file" ref="file" class="form-control" required>
                </div>
                <button type="submit" class="btn btn-primary">Upload</button>
                <button type="button" class="btn btn-secondary" data-dismiss="modal">Cancel</button>
              </form>
            </div>
          </div>
        </div>
      </div>
        `,
    data() {
        return {
            fileName: '',
            tag: '',
            file: null,
            tagOptions: ['sport', 'home', 'friends']
        };
    },
    methods: {
        selectTag(selectedTag) {
            this.tag = selectedTag;
        },
        async submitForm() {
            const file = this.$refs.file.files[0];
            if (file && this.fileName && this.tag) {
                const formData = new FormData();
                formData.append('file', file);
                formData.append('fileName', this.fileName);
                formData.append('tag', this.tag);

                try {
                    const response = await axios.post('YOUR_BACKEND_URL_HERE', formData, {
                        headers: {
                            'Content-Type': 'multipart/form-data'
                        }
                    });
                    this.$emit('upload', response.data);
                    $('#uploadFileModal').modal('hide');
                } catch (error) {
                    console.error('File upload failed:', error);
                    alert('File upload failed. Please try again.');
                }
            } else {
                alert('Please complete all fields.');
            }
        }
    }
});
