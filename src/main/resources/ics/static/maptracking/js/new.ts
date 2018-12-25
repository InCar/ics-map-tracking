
class Maptrack {
  opt: object;
  def: object;
  Ajax: object;
  constructor(opt: object) {
      this.opt = opt;
      var def = {
        mapType: '',
          dom: '',
          mapconfig: {
            gps: [116.404, 39.915], // 经纬度
            zoom: 10,                // 层级
          },
          mapTrack: false,
          trackconfig: {
            trackApi: '',
            trackParam: {pageNum: 1, pageSize: 10}
          }
      };
      this.def = extend(def,this.opt,true);
      this.Ajax = {
        get: function(url: string, opt:object, fn:Function) {
          var xhr = new XMLHttpRequest();
          let date = new Date().getTime()
          url += '?'
         for(let key in opt) {
           url += `${key}=${(<any>opt)[key]}&`
         }  
         url += `timestamp=${date}`
          xhr.open('GET', url, true);
          xhr.onreadystatechange = function() {
            // readyState == 4说明请求已完成
            if (xhr.readyState == 4 && xhr.status == 200 || xhr.status == 304) { 
              // 从服务器获得数据 
              fn.call(this, JSON.parse(xhr.responseText));  
            }
          };
          xhr.send();
        },
        post: function (url: string, data:any, fn:Function) {
          var xhr = new XMLHttpRequest();
          xhr.open("POST", url, true);
          // 添加http头，发送信息至服务器时内容编码类型
          xhr.setRequestHeader("Content-Type", "application/x-www-form-urlencoded");  
          xhr.onreadystatechange = function() {
            if (xhr.readyState == 4 && (xhr.status == 200 || xhr.status == 304)) {
              fn.call(this, JSON.parse(xhr.responseText));
            }
          };
          xhr.send(data);
        }
      };
  }
  public setBmap (obj: object): void {
    let myMap = new BMap.Map(obj.dom)
    let point = new BMap.Point(obj.mapconfig.gps[0], obj.mapconfig.gps[1])
    myMap.centerAndZoom(point, obj.mapconfig.zoom)
    myMap.enableScrollWheelZoom();   
  }
  public setTrack(data: object): void {
    let map = new BMap.Map(this.def.dom)
    let point = new BMap.Point(data.lng, data.lat);
    map.centerAndZoom(point, this.def.mapconfig.zoom)
    map.enableScrollWheelZoom();  
    let marker = new BMap.Marker(point); // 创建点
    map.addOverlay(marker);
  }
  public init(): void {
    if (!this.def.dom) return
    if (this.def.mapType) {
      if (this.def.mapType === 'bmap') { 
        loadJScript().then(() => {
             let trackconfig = this.def.trackconfig
              if (this.def.mapTrack) {
              this.Ajax.get(`${trackconfig.trackApi}/ics/gps/page`, trackconfig.trackParam, (data:object) => {
                if (data.dataList.length) this.setTrack(data.dataList[1])
                else this.setBmap(this.def)
              }) 
             } else {
              this.setBmap(this.def)
             }
            })
      }
    } else throw new Error('maptrack requires a mapType')
  }
  
}
 // 对象合并
function extend(o:object,n:object,override:Boolean) {
  for(var key in n){
      if(n.hasOwnProperty(key) && (!o.hasOwnProperty(key) || override)){
        if (typeof  (<any>n)[key] !== "object") (<any>o)[key]=(<any>n)[key];
        else (<any>Object).assign((<any>o)[key], (<any>n)[key])
      }
  }
  return o;
}

// 动态加载script 
function loadJScript() {
  if (!window.BMap) {
    window.BMap = {}
    window.BMap._preloader = new Promise((resolve, reject) => {
      window._initBaiduMap = function () {
        resolve(window.BMap)
        window.document.body.removeChild($script)
        window.BMap._preloader = null
        window._initBaiduMap = null
      }
      const $script = document.createElement('script')
      window.document.body.appendChild($script)
      $script.src = `//api.map.baidu.com/api?v=2.0&ak=ehokpezgpQESNRi1ld0fQmRSgAoO6YAG&callback=_initBaiduMap`
    })
    return window.BMap._preloader
  } else if (!window.BMap._preloader) {
    return Promise.resolve(window.BMap)
  } else {
    return window.BMap._preloader
  }
}