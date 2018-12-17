(function( global, factory ) {
    if ( typeof module === "object" && typeof module.exports === "object" ) {
        module.exports = global.document ?
            factory( global, true ) :
            function( w ) {
                if ( !w.document ) {
                    throw new Error( "jQuery requires a window with a document" );
                }
                return factory( w );
            };
    } else {
        factory( global );
    }
}(typeof window !== "undefined" ? window : this, function( window, noGlobal ) {
     // 构造函数
    function Incar(dom) {
        this._dom = dom;
        this._dom.innerText = "经纬度"
    }

    // init 方法
    var maptrack = {}
    maptrack.init = function (dom) {
        let initDom = document.getElementById(dom);
        let track = new Incar(initDom);
        return track;
    }
    window.maptrack = maptrack;
}));

