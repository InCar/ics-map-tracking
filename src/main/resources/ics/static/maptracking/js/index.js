 import "../css/index.scss";

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
            else {
              extend(o[key],n[key],override)
            }
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
                trackParam: {pageNum: 1, pageSize: 100},
                soketUrl: '',
                vinCode: '',
                iconUrl: '',
                startIcon: "",
                endIcon: "",
                iconSize: [28, 28],
                trackControl: {
                  startButton: '开始',
                  endButton: '暂停',
                  stopButton: '停止',
                  reduceButton: '减速',
                  addButton: '加速',
                  currentPoint: 0, // 记录当前坐标点index
                  isPlay: false,
                  markerIsStart: false,
                  speed: '1.0',
                }
              }
          };
          this.def = extend(def,opt,true);
          this.hasDom = false;
          this.Bmap = {},
          this.Amap = {},
          this.listeners = []; //自定义事件，用于监听插件的用户交互
          this.handlers = {};
          this.newData = []; // 转换后的轨迹数据
          this.init();
        //   Object.defineProperty(this.def, 'changeButton',{
        //     get() {
        //         console.log('get');
        //         return this.startButton;
        //     },
        //     set(newVal)  {
        //         console.log('set');
        //          this.startButton = newVal;
        //     }
        //  })
      },
     setTrack: function(map, data, config) {
        data.map(item => {
          let a = bd_encrypt(item);
          this.newData.push(new BMap.Point(a.lng, a.lat))
        })
        // 创建起、终点
        let startIcon = new BMap.Icon(config.startIcon, new BMap.Size(config.iconSize[0],config.iconSize[1]));
        let endIcon = new BMap.Icon(config.endIcon, new BMap.Size(config.iconSize[0],config.iconSize[1]));
        if(config.startIcon) map.addOverlay(new BMap.Marker(this.newData[0],{icon:startIcon})); // 创建点
        else map.addOverlay(new BMap.Marker(this.newData[0]));
        if(config.endIcon) map.addOverlay(new BMap.Marker(this.newData[this.newData.length - 1],{icon:endIcon}));
        else map.addOverlay(new BMap.Marker(this.newData[this.newData.length - 1]));
        // 目标点
        let icon = new BMap.Icon(config.iconUrl, new BMap.Size(config.iconSize[0], config.iconSize[1]));
        let target = null;
        if (config.iconUrl) target = new BMap.Marker(this.newData[0],{icon:icon}); // 创建点
        else target = new BMap.Marker(this.newData[0]);
        map.addOverlay(target); // 创建点
         this.setPolyline(map, this.newData)
         map.setViewport(this.newData);
         this.creatDom(config, target)
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
         marker.setPosition(point); // 改变点的位置
         marker.setRotation(data.direction); // 改变点的方向
         this.def.points.push(point);
         this.setZoom(map, this.def.points)
         this.setPolyline(map, this.def.points);
        // map.centerAndZoom(point, this.def.config.zoom)
      },
      creatDom (config, target) {
         let domId = document.getElementById(this.def.dom);
         let control = config.trackControl;
         let str = `<ul class="trackControl clearfix">
           <li>
               <span class="play"></span>
               <div class="tooltip">${control.startButton}</div>
           </li>
           <li class="stop">
              <div class="tooltip">${control.stopButton}</div>
           </li>
           <li class="reduce">
              <div class="tooltip">${control.reduceButton}</div>
           </li>
         <li class="noClick">
           x
           <span>${control.speed}</span>
         </li>
           <li class="add">
               <div class="tooltip">${control.addButton}</div>
           </li>
       </ul>`
       let div = document.createElement('div');
      //  div.setAttribute('class', 'trackControl');
       div.innerHTML = str;
       domId.appendChild(div)
       let trackControl = document.getElementsByClassName("trackControl")[0].children;
       this.play(target, trackControl, control);
        trackControl[1].onclick = function() {
          control.markerIsStart = true;
          control.isPlay = false;
          control.currentPoint = 0;
          trackControl[0].children[1].innerHTML = control.startButton
          trackControl[0].children[0].className = 'play';
        }
        trackControl[4].onclick = function() {
          if (control.speed >= 3.0) return;
          control.speed = (+control.speed + 0.5).toFixed(1);
          trackControl[3].children[0].innerText = control.speed
        }
        trackControl[2].onclick = function() {
          if (control.speed <= 1.0) return;
          control.speed = (+control.speed - 0.5).toFixed(1);
          trackControl[3].children[0].innerText = control.speed
        }
      },
      play: function(target, trackControl, control) {
        let _this = this;
        trackControl[0].onclick = function() {
          control.isPlay = !control.isPlay;
          if (!control.isPlay) {
            this.children[1].innerHTML = control.startButton
            this.children[0].className = 'play';
          }
          else {
            this.children[1].innerHTML = control.endButton;
            this.children[0].className = 'parse';
          }
          _this.resetMkPoint(target, control, control.currentPoint)
        }
      },
      resetMkPoint: function(target, control, i) {
        if (!control.isPlay) {
          control.markerIsStart = true;
          return;
        }; // 停止播放
        control.markerIsStart = false;
        let time = 200 - (50 * control.speed);
        target.setPosition(this.newData[i]);// 车辆位置
        target.setRotation(this.newData[i].direction);// 车辆方向
        if (i < this.newData.length - 1) {
          setTimeout(() => {
            i++;
            this.resetMkPoint(target, control, i);
          }, time);
        }
        control.currentPoint = i;
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
              this.Bmap = map;
              let point = new BMap.Point(config.gps[0], config.gps[1])
              map.centerAndZoom(point, config.zoom)
              map.enableScrollWheelZoom(); 
              map.clearOverlays()
                  if (this.def.mapTrack) {  // 轨迹回放
                  Ajax.get(`${config.trackApi}/ics/gps/page`, config.trackParam, (data) => {
                    console.log(data)
                    if (data.data.dataList.length)  this.setTrack(map, data.data.dataList, config)
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
  }

  var Ajax = {
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
  // 毫秒转年月日
  const DateFormat = (str, fmt) => {
    let o = {
      'M+': str.getMonth() + 1,
      'd+': str.getDate(),
      'h+': str.getHours(),
      'm+': str.getMinutes(),
      's+': str.getSeconds(),
      'q+': Math.floor((str.getMonth() + 3) / 3),
      'S': str.getMilliseconds()
    };
    if (/(y+)/.test(fmt)) fmt = fmt.replace(RegExp.$1, (str.getFullYear() + '').substr(4 - RegExp.$1.length));
    for (let k in o) {
      if (new RegExp('(' + k + ')').test(fmt)) {
        fmt = fmt.replace(RegExp.$1, (RegExp.$1.length === 1) ? (o[k]) : (('00' + o[k]).substr(('' + o[k]).length)));
      }
    }
    return fmt;
  };
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
