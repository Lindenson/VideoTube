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
            file: null,
            tagOptions: ['sport', 'home', 'friends'],
            selectedTag: '',
            fileTypeError: false
        };
    },
    methods: {
        selectTag(tag) {
            this.selectedTag = tag;
        },
        checkFileType(event) {
            const file = event.target.files[0];
            this.fileTypeError = file.type !== 'video/mp4';
        },
        async submitForm() {
            if (this.fileTypeError) {
                alert('Only MPEG4 files are allowed.');
                return;
            }
            const file = this.$refs.file.files[0];

            console.log(file, this.fileName, this.selectTag)

                if (file && this.fileName && this.selectTag) {
                const formData = new FormData();
                formData.append('file', file);
                formData.append('fileName', this.fileName);
                formData.append('tag', this.selectedTag);

                try {
                    let token = localStorage.getItem('jwt');
                    const response = await axios.post('/upload', formData, {
                        headers: {
                            'Content-Type': 'multipart/form-data',
                            Authorization: `Bearer ${token}`
                        }
                    });

                    if (file.size === response.data) {
                        renewVideoTags();
                        this.$emit('upload', response.data);
                    }

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


function renewVideoTags() {
    const videos = document.getElementsByTagName('video');
    for (let i = 0; i < videos.length; i++) {
        const currentTime = new Date().getTime(); // Get current timestamp
        const source = videos[i].getElementsByTagName('source')[0]
        const originalSrc = source.src
        const newSrc = `${originalSrc}?t=${currentTime}`;
        source.setAttribute('src', newSrc);
        videos[i].load();
    }
}