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
                code: null,
                refs: null,
                showRefs: false
            };
        },
        methods: {
            refresh() {
                http.get('/api/events-scripting').then(response => {
                    this.config = response;
                    this.code = this.config.code;
                    delete this.config.code;
                });
            },
            save() {
                handleCodeSave('/api/events-scripting-code', this.code);
            },
            showApiRef() {
                if (this.showRefs) {
                    this.showRefs = false;
                } else {
                    if (this.refs) {
                        this.showRefs = true;
                    } else {
                        http.get('/api/scripts-doc/EVENTS').then(response => {
                            this.showRefs = true;
                            this.refs = response;
                        });
                    }
                }
            },
            update() {
                http.post('/api/events-scripting', this.config);
            }
        }
    };
    addComponent(args, 'ScriptEditor');
    return args;
}

export { createComponent }