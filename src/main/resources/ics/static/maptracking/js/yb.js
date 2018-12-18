// 构造函数
// function SetData(obj) {
//     for (let key in obj) {
//         console.log(key)
//         this[key] = obj.key
//     }
//      this.initBmap = initBmap
// }

// init 方法
maptrackInit = function(obj) {
  if (!obj.dom) return
  if (obj.mapType) {
    if (obj.mapType === 'bmap') {
      addLoadEvent(loadJScript(`http://api.map.baidu.com/api?v=2.0&ak=ehokpezgpQESNRi1ld0fQmRSgAoO6YAG&callback=initBmap(${obj.dom})`))
    }
  } else throw new Error('maptrack requires a mapType')
  // new SetData({
  //     dom: obj.dom,
  //     bmap: obj.bmap
  // });
}
// window.maptrack = maptrack;
//百度地图API功能
function loadJScript(url) {
  var script = document.createElement('script')
  script.type = 'text/javascript'
  script.src = url
  document.head.appendChild(script)
}
// window.onload
function addLoadEvent(func) {
  let oldonload = window.onload
  if (typeof window.onload != 'function') {
    window.onload = func
  } else {
    window.onload = function() {
      oldonload()
      func()
    }
  }
}
// 初始化百度地图
function initBmap(dom) {
  let map = new BMap.Map(dom) // 创建Map实例
  console.log(map)
  let point = new BMap.Point(116.404, 39.915) // 创建点坐标
  map.centerAndZoom(point, 15)
  map.enableScrollWheelZoom() //启用滚轮放大缩小
}
// }));
