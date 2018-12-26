
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
// 先转成中国正常坐标系GCJ02协议的坐标
  var GPS = {
    PI : 3.14159265358979324,
    x_pi : 3.14159265358979324 * 3000.0 / 180.0,
    delta : function (lat, lon) {
        // Krasovsky 1940
        //
        // a = 6378245.0, 1/f = 298.3
        // b = a * (1 - f)
        // ee = (a^2 - b^2) / a^2;
        var a = 6378245.0; //  a: 卫星椭球坐标投影到平面地图坐标系的投影因子。
        var ee = 0.00669342162296594323; //  ee: 椭球的偏心率。
        var dLat = this.transformLat(lon - 105.0, lat - 35.0);
        var dLon = this.transformLon(lon - 105.0, lat - 35.0);
        var radLat = lat / 180.0 * this.PI;
        var magic = Math.sin(radLat);
        magic = 1 - ee * magic * magic;
        var sqrtMagic = Math.sqrt(magic);
        dLat = (dLat * 180.0) / ((a * (1 - ee)) / (magic * sqrtMagic) * this.PI);
        dLon = (dLon * 180.0) / (a / sqrtMagic * Math.cos(radLat) * this.PI);
        return {'lat': dLat, 'lon': dLon};
    },

    //GPS---高德
    gcj_encrypt : function ( wgsLat , wgsLon ) {
        if (this.outOfChina(wgsLat, wgsLon))
            return {'lat': wgsLat, 'lon': wgsLon};

        var d = this.delta(wgsLat, wgsLon);
        return {'lat' : wgsLat + d.lat,'lon' : wgsLon + d.lon};
    },
    outOfChina : function (lat, lon) {
        if (lon < 72.004 || lon > 137.8347)
            return true;
        if (lat < 0.8293 || lat > 55.8271)
            return true;
        return false;
    },
    transformLat : function (x, y) {
        var ret = -100.0 + 2.0 * x + 3.0 * y + 0.2 * y * y + 0.1 * x * y + 0.2 * Math.sqrt(Math.abs(x));
        ret += (20.0 * Math.sin(6.0 * x * this.PI) + 20.0 * Math.sin(2.0 * x * this.PI)) * 2.0 / 3.0;
        ret += (20.0 * Math.sin(y * this.PI) + 40.0 * Math.sin(y / 3.0 * this.PI)) * 2.0 / 3.0;
        ret += (160.0 * Math.sin(y / 12.0 * this.PI) + 320 * Math.sin(y * this.PI / 30.0)) * 2.0 / 3.0;
        return ret;
    },
    transformLon : function (x, y) {
        var ret = 300.0 + x + 2.0 * y + 0.1 * x * x + 0.1 * x * y + 0.1 * Math.sqrt(Math.abs(x));
        ret += (20.0 * Math.sin(6.0 * x * this.PI) + 20.0 * Math.sin(2.0 * x * this.PI)) * 2.0 / 3.0;
        ret += (20.0 * Math.sin(x * this.PI) + 40.0 * Math.sin(x / 3.0 * this.PI)) * 2.0 / 3.0;
        ret += (150.0 * Math.sin(x / 12.0 * this.PI) + 300.0 * Math.sin(x / 30.0 * this.PI)) * 2.0 / 3.0;
        return ret;
    }
  }
  //将 GCJ-02 坐标转换成 BD-09 坐标  
  function bd_encrypt(point) {
    let gps = GPS.gcj_encrypt(point.lat, point.lng); // 这里顺序不同
    const  x_pi = 3.14159265358979324 * 3000.0 / 180.0;  
    var x = gps.lon, y = gps.lat;
    var z = Math.sqrt(x * x + y * y) + 0.00002 * Math.sin(y * x_pi);
    var theta = Math.atan2(y, x) + 0.000003 * Math.cos(x * x_pi);
    point.lng = z * Math.cos(theta) + 0.0065;
    point.lat = z * Math.sin(theta) + 0.006;
    return point
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
        $script.src = `//api.map.baidu.com/api?v=2.0&ak=Z387qRaNG1dZvs0xrpNDMWTVh2ZhWRkW&callback=_initBaiduMap`
      })
      return window.BMap._preloader
    } else if (!window.BMap._preloader) {
      return Promise.resolve(window.BMap)
    } else {
      return window.BMap._preloader
    }
  }
  // socket
 const webSocket = (api, params, fnSuccess, fnFail) => {
    let responseInfo = {type: '', msg: ''};            // 请求信息
    let apiUrl = api + '?vins=' + params.vinCode
    //  + '&userId=' + params.userId;
      let ws;
      // 开启webSocket
      function doOpen () {
        responseInfo.type = 'open';
        responseInfo.msg = 'websocket已开启';
        // console.log('开启websocket');
      }
  
      // 错误接收
      function doError () {
        this.close();
        responseInfo.type = 'error';
        responseInfo.msg = '您已经掉线，无法与服务器通信!';
        fnFail && fnFail(responseInfo);
      }
  
      // 接收新消息
      function doMessage (message) {
        var event = JSON.parse(message.data);
        fnSuccess && fnSuccess(event);
      }
  
    // 断开
    function doClose () {
      responseInfo.type = 'close';
      responseInfo.msg = '您已经掉线，无法与服务器通信!';
      fnFail && fnFail(responseInfo);
    }
  
      // 初始话 WebSocket
      function initWebSocket (apiUrl) {
        if (window.WebSocket) {
          ws = new WebSocket(encodeURI(apiUrl));
          ws.onopen = doOpen;
          ws.onerror = doError;
          ws.onclose = doClose;
          ws.onmessage = doMessage;
        } else {
          responseInfo.type = 'error';
          responseInfo.msg = '您的设备不支持 webSocket!';
          fnFail && fnFail(responseInfo);
        }
      }
      // 初始化webSocket
      initWebSocket(apiUrl);
      return ws;
  };

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
              mapTrack: false,
              mapMointer: false,
              points: [], // 监控点
              config: {
                gps: [116.404, 39.915], // 经纬度
                zoom: 16,                // 层级
                trackApi: '',
                trackParam: {pageNum: 1, pageSize: 10},
                soketUrl: 'ws://192.168.75.1:8889/api/ws/gpsWebSocket',
                vinCode: '',
                iconUrl: '',
                iconSize: [28, 28]
              }
          };
          this.def = extend(def,opt,true);
          this.hasDom = false;
          this.listeners = []; //自定义事件，用于监听插件的用户交互
          this.handlers = {};
          this.init();
      },
     setTrack: function(map, data) {
       let newData = [];
        data.map(item => {
          let a = bd_encrypt(item);
          newData.push(new BMap.Point(a.lng, a.lat))
        })
         this.setPolyline(map, newData)
        map.centerAndZoom(newData[0], this.def.config.zoom)
      },
      setPolyline: function(map, lineData) {
        map.addOverlay(new BMap.Polyline(lineData, {strokeColor:"blue", strokeWeight:6, strokeOpacity:0.8}));  //增加折线
      },
      //根据点信息实时更新地图显示范围，让轨迹完整显示。设置新的中心点和显示级别
      setZoom: function (map, bPoints) {
      var view = map.getViewport(eval(bPoints));
      // var mapZoom = view.zoom;
      var centerPoint = view.center;
      map.centerAndZoom(centerPoint, 16);
    },
      setMoniter: function(map, data, marker) {
         let newData = bd_encrypt(data)
         let point = new BMap.Point(newData.lng, newData.lat);
         marker.setPosition(point);
         marker.setRotation(data.direction);
         this.def.points.push(point);
         this.setZoom(map, this.def.points)
         this.setPolyline(map, this.def.points);
        // map.centerAndZoom(point, this.def.config.zoom)
      },
      init: function() {
        if (!this.def.dom) return
        if (this.def.mapType) {
          if (this.def.mapType === 'bmap') {  
            loadJScript().then(() => {
              let config = this.def.config
              let map = new BMap.Map(this.def.dom, {
                enableMapClick: false
              })
              console.log(map)
              let point = new BMap.Point(config.gps[0], config.gps[1])
              map.centerAndZoom(point, config.zoom)
              map.enableScrollWheelZoom(); 
              map.clearOverlays()
                  if (this.def.mapTrack) {  // 轨迹回放
                  this.Ajax.get(`${config.trackApi}/ics/gps/page`, config.trackParam, (data) => {
                    if (data.dataList.length)  this.setTrack(map, data.dataList)
                  }) 
                 } else if (this.def.mapMointer) {  // 监控点
                  // let data = [
                  //   {lat:30.4824,lng:114.397257, direction: 90},
                  //   {lat:30.482563,lng:114.396676, direction: 30},
                  //   {lat:30.482374,lng:114.395602, direction: 40},
                  //   {lat:30.482343,lng:114.394226, direction: 20},
                  //   {lat:30.482326,lng:114.393722, direction: 120},
                  //   {lat:30.482331,lng:114.393278, direction: 150}
                  // ]
                  // let i = 0;
                  let icon = new BMap.Icon(config.iconUrl, new BMap.Size(config.iconSize[0], config.iconSize[1]));
                  let marker = null;
                  if (config.iconUrl) marker = new BMap.Marker(point,{icon:icon}); // 创建点
                  else marker = new BMap.Marker(point);
                  map.addOverlay(marker);  // 标点
                  // let circle = setInterval(() => {
                  //   this.setMoniter(map, data[i], marker)
                  //   i++
                  //   if(i === data.length) clearInterval(circle)
                  // }, 2000)
                  webSocket(config.soketUrl, {vinCode: config.vinCode}, (data) => {
                   this.setMoniter(map, data, marker)
                  }) 
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
