import { addComponent } from '/components/Loader.js'
import * as http from '/http.js';

function createComponent(template) {
    let args = {
        template: template,
        created() {
            http.get('/api/auto-drop').then(response => {
                this.config = response;
            });
        },
        data() {
            return {
                config: null
            };
        },
        methods: {
            onChange() {
                http.post('/api/auto-drop', this.config).then(response => {
                    this.config = response;
                });
            }
        }
    };
    addComponent(args, 'ItemsList');
    return args;
}

export { createComponent }