 import "../css/index.scss";
 import * as tool from './tool'
// plugin.js
;(function(undefined) {
  "use strict"
  var _global;
 
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
          this.def = tool.extend(def,opt,true);
          this.Bmap = {},
          this.Amap = {},
          this.listeners = []; //自定义事件，用于监听插件的用户交互
          this.handlers = {};
          this.newData = []; // 转换后的轨迹数据
          this.trackPoint = {}; // 原始轨迹数据
          this.currentData = {}; // 当前轨迹数据
          this.domId = document.getElementById(this.def.dom);
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
          let a = this.translateToBmap(item);
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
        let target = null;
        if (config.iconUrl) {
          let icon = new BMap.Icon(config.iconUrl, new BMap.Size(config.iconSize[0], config.iconSize[1]));
          target = new BMap.Marker(this.newData[0],{icon:icon}); // 创建点
        }
        else target = new BMap.Marker(this.newData[0]);
        map.addOverlay(target); // 创建点
         this.setPolyline(map, this.newData)
         map.setViewport(this.newData);
         this.creatDom(config, target)
         this.creatBorder(config, target)
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
         let newData = this.translateToBmap(data)
         let point = new BMap.Point(newData.lng, newData.lat);
         marker.setPosition(point); // 改变点的位置
         marker.setRotation(data.direction); // 改变点的方向
         this.def.points.push(point);
         this.setZoom(map, this.def.points)
         this.setPolyline(map, this.def.points);
        // map.centerAndZoom(point, this.def.config.zoom)
      },
      creatDom: function(config, target) {
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
      this.domId.appendChild(div)
      let trackControl = document.getElementsByClassName("trackControl")[0].children;
      let _this = this;
      trackControl[0].onclick = function() {
        control.isPlay = !control.isPlay;
        if (!control.isPlay) {
          this.children[1].innerHTML = control.startButton
          this.children[0].className = 'play';
          if(_this.listeners.indexOf('pause') > -1) {
            _this.emit({type:'pause',target: this})
        }
        }
        else {
          this.children[1].innerHTML = control.endButton;
          this.children[0].className = 'parse';
          if(_this.listeners.indexOf('play') > -1) {
            _this.emit({type:'play',target: this})
        }
        }
        _this.resetMkPoint(target, control, control.currentPoint)
      }
       trackControl[1].onclick = function() {
         control.markerIsStart = true;
         control.isPlay = false;
         control.currentPoint = 0;
         trackControl[0].children[1].innerHTML = control.startButton
         trackControl[0].children[0].className = 'play';
         if(_this.listeners.indexOf('stop') > -1) {
           _this.emit({type:'stop',target: this})
       }
       }
       trackControl[4].onclick = function() {
         if (control.speed >= 3.0) return;
         control.speed = (+control.speed + 0.5).toFixed(1);
         trackControl[3].children[0].innerText = control.speed
         if(_this.listeners.indexOf('add') > -1) {
           _this.emit({type:'add',target: this})
       }
       }
       trackControl[2].onclick = function() {
         if (control.speed <= 1.0) return;
         control.speed = (+control.speed - 0.5).toFixed(1);
         trackControl[3].children[0].innerText = control.speed
         if(_this.listeners.indexOf('reduce') > -1) {
           _this.emit({type:'reduce',target: this})
       }
       }
     },
      creatBorder: function(config, target) {
         let control = config.trackControl;
         let str = `
         <p style="font-size:16px;color:#000000;">轨迹信息</p>
         <p>VIN码：ewq</p>
         <p>速度：${this.currentData.direction} km/h</p>
         <p>时间：32</p>
         <div>
               <span>大萨达</span>
               <span class="circleSpan" >大萨达撒</span>
               <span>大萨达撒</span>
         </div>
         `
       let div = document.createElement('div');
       div.setAttribute('class', 'trackBorder');
       div.innerHTML = str;
       this.domId.appendChild(div)
      },
      on: function(type, handler){
        // type: play, pause, stop, add, reduce
        if(typeof this.handlers[type] === 'undefined') {
            this.handlers[type] = [];
        }
        this.listeners.push(type);
        this.handlers[type].push(handler);
        return this;
      },
      emit: function(event){
        if(!event.target) {
            event.target = this;
        }
        if(this.handlers[event.type] instanceof Array) {
            var handlers = this.handlers[event.type];
            for(var i = 0, len = handlers.length; i < len; i++) {
                handlers[i](event);
                return true;
            }
        }
        return false;
    },
      resetMkPoint: function(target, control, i) {
        if (!control.isPlay) {
          control.markerIsStart = true;
          return;
        }; // 停止播放
        control.markerIsStart = false;
        let time = 200 - (50 * control.speed);
        let dataList = this.trackPoint.data.dataList;
        target.setPosition(this.newData[i]);// 车辆位置
        target.setRotation(dataList[i].direction);// 车辆方向
        if (i < this.newData.length - 1) {
          setTimeout(() => {
            i++;
            this.resetMkPoint(target, control, i);
            this.currentData = dataList[i];
          }, time);
        }
        control.currentPoint = i;
      },
       //将 GCJ-02 坐标转换成 BD-09 坐标  
       translateToBmap: function (point) {
        let gps = this.GPS.translateToAmap(point.lat, point.lng); // 这里顺序不同
        const  x_pi = 3.14159265358979324 * 3000.0 / 180.0;  
        var x = gps.lon, y = gps.lat;
        var z = Math.sqrt(x * x + y * y) + 0.00002 * Math.sin(y * x_pi);
        var theta = Math.atan2(y, x) + 0.000003 * Math.cos(x * x_pi);
        point.lng = z * Math.cos(theta) + 0.0065;
        point.lat = z * Math.sin(theta) + 0.006;
        return point
      },
      // 先转成中国正常坐标系GCJ02协议的坐标
      GPS : {
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
        translateToAmap : function ( wgsLat , wgsLon ) {
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
      },
      init: function(fn) {
        if (!this.def.dom) return
        if (this.def.mapType) {
          if (this.def.mapType === 'bmap') {  
            tool.loadJScript().then(() => {
              let config = this.def.config
              let map = new BMap.Map(this.def.dom, {
                enableMapClick: false
              })
              this.Bmap = map;
              this.BMap = BMap;
              let point = new BMap.Point(config.gps[0], config.gps[1])
              map.centerAndZoom(point, config.zoom)
              map.enableScrollWheelZoom(); 
              map.clearOverlays()
              if (this.def.mapTrack) {  // 轨迹回放
                tool.Ajax.get(`${config.trackApi}/ics/gps/page`, config.trackParam, (data) => {
                  this.trackPoint = data;
                  if (data.data && data.data.dataList.length)  this.setTrack(map, data.data.dataList, config)
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
              let marker = null;
              if (config.iconUrl) {
                let icon = new BMap.Icon(config.iconUrl, new BMap.Size(config.iconSize[0], config.iconSize[1]));
                marker = new BMap.Marker(point,{icon:icon}); // 创建点
              } else marker = new BMap.Marker(point);
              map.addOverlay(marker);  // 标点
              // let circle = setInterval(() => {
              //   this.setMoniter(map, data[i], marker)
              //   i++
              //   if(i === data.length) clearInterval(circle)
              // }, 2000)
              tool.webSocket(config.soketUrl, {vinCode: config.vinCode}, (data) => {
                this.setMoniter(map, data, marker)
              }) 
              }
              return fn(BMap, map);
            })
          }
        } else throw new Error('maptrack requires a mapType')
      },
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
