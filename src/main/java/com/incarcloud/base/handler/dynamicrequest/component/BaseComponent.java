package com.incar.base.handler.dynamicrequest.component;

import com.incar.base.config.Config;
import com.incar.base.context.Context;
import com.incar.base.context.Initializable;

public class BaseComponent implements Initializable {
    protected Context context;
    protected Config config;

    @Override
    public void init(Context context) {
        this.context=context;
        this.config=context.getConfig();
    }

    public Context getContext() {
        return context;
    }

    public Config getConfig() {
        return config;
    }
}
