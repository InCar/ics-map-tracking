package com.incarcloud.base.handler.dynamicrequest.component;

import com.incarcloud.base.config.Config;
import com.incarcloud.base.context.Context;
import com.incarcloud.base.context.Initializable;
import com.incarcloud.base.dao.DataAccess;

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
