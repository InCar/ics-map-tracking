
// plugin.js
;(function(undefined) {
  "use strict"
  var _global;

  // 工具函数
  // 对象合并
  function extend(o,n,override) {
      for(var key in n){
          if(n.hasOwnProperty(key) && (!o.hasOwnProperty(key) || override)){
            if (typeof n[key] !== "object") o[key]=n[key];
            else Object.assign(o[key], n[key])
          }
      }
      return o;
  }
  // window.onload
// function addLoadEvent(func) {
//   let oldonload = window.onload
//   if (typeof window.onload != 'function') {
//     window.onload = func
//   } else {
//     window.onload = function() {
//       oldonload()
//       func()
//     }
//   }
// }
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

  // 通过class查找dom
  if(!('getElementsByClass' in HTMLElement)){
      HTMLElement.prototype.getElementsByClass = function(n){
          var el = [],
              _el = this.getElementsByTagName('*');
          for (var i=0; i<_el.length; i++ ) {
              if (!!_el[i].className && (typeof _el[i].className == 'string') && _el[i].className.indexOf(n) > -1 ) {
                  el[el.length] = _el[i];
              }
          }
          return el;
      };
      ((typeof HTMLDocument !== 'undefined') ? HTMLDocument : Document).prototype.getElementsByClass = HTMLElement.prototype.getElementsByClass;
  }

  // 构造函数 - 返回数组结构
  function Maptrack(opt){
      this._initial(opt);
  }
  Maptrack.prototype = {
      constructor: this,
      _initial: function(opt) {
          // 默认参数
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
          this.def = extend(def,opt,true);
          this.hasDom = false;
          this.listeners = []; //自定义事件，用于监听插件的用户交互
          this.handlers = {};
      },
      setBmap: function(obj) {
        let myMap = new BMap.Map(obj.dom)
        let point = new BMap.Point(obj.mapconfig.gps[0], obj.mapconfig.gps[1])
        myMap.centerAndZoom(point, obj.mapconfig.zoom)
        myMap.enableScrollWheelZoom();   
      },
      setTrack: function(data) {
        let map = new BMap.Map(this.def.dom)
        let point = new BMap.Point(data.lng, data.lat);
        map.centerAndZoom(point, this.def.mapconfig.zoom)
        map.enableScrollWheelZoom();  
        let marker = new BMap.Marker(point); // 创建点
        map.addOverlay(marker);
      },
      init: function() {
        if (!this.def.dom) return
        if (this.def.mapType) {
          if (this.def.mapType === 'bmap') { 
            loadJScript().then(() => {
                 let trackconfig = this.def.trackconfig
                  if (this.def.mapTrack) {
                  this.Ajax.get(`${trackconfig.trackApi}/ics/gps/page`, trackconfig.trackParam, (data) => {
                    if (data.dataList.length) this.setTrack(data.dataList[1])
                    else this.setBmap(this.def)
                  }) 
                 } else {
                  this.setBmap(this.def)
                 }
                })
          }
        } else throw new Error('maptrack requires a mapType')
      },
      Ajax: {
        get: function(url, opt, fn) {
          var xhr = new XMLHttpRequest();
          url += '?' 
         for(let key in opt) {
           url += `${key}=${opt[key]}&`
         }  
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
        // datat应为'a=a1&b=b1'这种字符串格式，在jq里如果data为对象会自动将对象转成这种字符串格式
        post: function (url, data, fn) {
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
      }
  }

  // 将插件对象暴露给全局对象
  _global = (function(){ return this || (0, eval)('this'); }());
  if (typeof module !== "undefined" && module.exports) {
      module.exports = Maptrack;
  } else if (typeof define === "function" && define.amd) {
      define(function(){return Maptrack;});
  } else {
      !('Maptrack' in _global) && (_global.Maptrack = Maptrack);
  }
}());
