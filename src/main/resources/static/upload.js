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
                      :class="{ active: tagOption === selectedTag }"                 
                    >
                      {{ tagOption }}
                        </button>
                  </div>
                </div>
                <div class="form-group">
                  <label for="file">Choose file</label>
                  <input type="file" id="file" ref="file" class="form-control" @change="checkFileType" required>
                </div>
                <button type="submit" class="btn btn-primary" :disabled="isLoading">Upload</button>
                <button type="button" class="btn btn-secondary" data-dismiss="modal" :disabled="isLoading">Cancel</button>
                <div v-if="isLoading" class="loading-spinner"></div>
              </form>
            </div>
          </div>
        </div>
      </div>
        `,
    data() {
        return {
            fileName: '',
            file: null,
            tagOptions: ['sport', 'home', 'friends'],
            selectedTag: '',
            fileTypeError: false,
            fileSizeError: false,
            isLoading: false,
        };
    },
    methods: {
        selectTag(tag) {
            this.selectedTag = tag;
        },
        checkFileType(event) {
            const file = event.target.files[0];
            this.fileTypeError = file.type !== 'video/mp4';
            this.fileSizeError = file.size > 20000000; // 20 MB limit
        },
        async submitForm() {
            if (this.fileTypeError) {
                this.$emit('upload', 'Only MPEG4 files are allowed.');
                return;
            }
            if (this.fileSizeError) {
                this.$emit('upload', 'File size should not exceed 20MB.');
                return;
            }
            const file = this.$refs.file.files[0];

                if (file && this.fileName && this.selectTag) {
                const formData = new FormData();
                formData.append('file', file);
                formData.append('fileName', this.fileName);
                formData.append('tag', this.selectedTag);

                try {
                    this.isLoading = true;
                    let token = localStorage.getItem('jwt');
                    const response = await axios.post('/upload', formData, {
                        headers: {
                            'Content-Type': 'multipart/form-data',
                            Authorization: `Bearer ${token}`
                        }
                    });

                    if (file.size === response.data) {
                        this.$emit('upload', `Uploaded ${response.data} bites`);
                    }

                } catch (error) {
                    if(error.response.status === 401) {
                        this.$emit('upload', `Not authorized for upload`);
                    }
                    else {
                        this.$emit('upload', `File upload failed.`);
                    }
                }
                finally {
                    this.isLoading = false;
                    $('#uploadFileModal').modal('hide');
                }
            } else {
                    this.$emit('upload', `Please complete all fields.`);
            }
        }
    }
});