package com.zergatul.cheatutils.scripting.modules;

import com.zergatul.cheatutils.configs.Config;
import com.zergatul.cheatutils.configs.ConfigStore;
import com.zergatul.cheatutils.scripting.ApiType;
import com.zergatul.cheatutils.scripting.ApiVisibility;
import com.zergatul.scripting.MethodDescription;

public class EspApi {

    @MethodDescription("""
            Checks if ESPs rendering is enabled
            """)
    public boolean isEnabled() {
        return ConfigStore.instance.getConfig().esp;
    }

    @MethodDescription("""
            Enables/disables rendering of all ESP modules
            """)
    @ApiVisibility(ApiType.UPDATE)
    public void toggle() {
        ConfigStore store = ConfigStore.instance;
        Config config = store.getConfig();
        config.esp = !config.esp;
        store.requestWrite();
    }
}