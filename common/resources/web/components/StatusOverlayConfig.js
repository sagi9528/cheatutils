import { addComponent } from '/components/Loader.js'
import { handleCodeSave } from '/components/MonacoEditor.js'
import * as http from '/http.js';

function createComponent(template) {
    let args = {
        template: template,
        created() {
            this.refresh();
        },
        data() {
            return {
                config: null,
                refs: null,
                showRefs: false
            };
        },
        methods: {
            refresh() {
                http.get('/api/status-overlay').then(response => {
                    this.config = response;
                });
            },
            save() {
                handleCodeSave('/api/status-overlay-code', this.config.code);
            },
            showApiRef() {
                if (this.showRefs) {
                    this.showRefs = false;
                } else {
                    if (this.refs) {
                        this.showRefs = true;
                    } else {
                        http.get('/api/scripts-doc/OVERLAY').then(response => {
                            this.showRefs = true;
                            this.refs = response;
                        });
                    }
                }
            },
            update() {
                http.post('/api/status-overlay', this.config).then(response => {
                    this.config = response;
                });
            }
        }
    };
    addComponent(args, 'ScriptEditor');
    return args;
}

export { createComponent }