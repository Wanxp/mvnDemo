package com.wanxp.App;

import com.wanxp.Interface.Filter;
import com.wanxp.Interface.Source;
import com.wanxp.Interface.Target;

public class PipLine {
    private Source source;
    private Target target;
    private Filter filter;
    public PipLine(){

    }

    public PipLine(Source source, Target target, Filter filter) {
        this.source = source;
        this.target = target;
        this.filter = filter;
    }
}
