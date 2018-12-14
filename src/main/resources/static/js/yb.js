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