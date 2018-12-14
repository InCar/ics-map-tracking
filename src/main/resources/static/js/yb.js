 // 构造函数
function Incar(dom) {
    this._dom = dom;
    console.log(this._dom)
    this._dom.innerText = "经纬度"
}

// init 方法
var maptrack = {}
maptrack.init = function (dom) {
    let track = new Incar(dom);
    console.log(track)
    return track;
}