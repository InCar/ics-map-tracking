package com.incar.base.context;

import com.incar.base.anno.ICSComponent;
import com.incar.base.anno.ICSConditionalOnMissingBean;
import com.incar.base.config.Config;
import com.incar.base.request.RequestData;

@ICSComponent("dispatcher")
@ICSConditionalOnMissingBean(name="dispatcher")
public class DefaultDispatcher implements Dispatcher,Initializable {
    private Context context;
    private Config config;
    @Override
    public void dispatch(RequestData requestData) {
        String staticMappingPre=config.getRequestStaticMappingPre();
        String subPath=requestData.getSubPath();
        if(subPath.startsWith(staticMappingPre)){
            context.handleResource(requestData);
        }else{
            context. handleRequest(requestData);
        }
    }

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
