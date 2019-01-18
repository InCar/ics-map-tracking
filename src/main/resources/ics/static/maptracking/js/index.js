 import "../css/index.scss";
 import * as tool from './tool'
// plugin.js
;(function(undefined) {
  "use strict"
  var _global;

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
              mapTrack: false,       // 是否开启轨迹
              splitTrack: false,       // 是否开启分段轨迹
              mapMointer: false,   // 是否开启推送
              currentData: function() { // 获取运动当前点数据，和showBorder配合使用
              },
              points: [], // 监控点
              config: {
                showBorder: true, // 是否展示面板
                gps: [116.404, 39.915], // 初始化地图经纬度
                zoom: 16,                // 初始化地图层级
                trackApi: '',           // 自定义路径
                trackParam: {vin: "LVGEN56A4JG247290"},  // 轨迹参数
                splitTrackParam: {vin: "LVGEN56A4JG247290"},  // 分段轨迹参数
                moniterParam: {vin: "LVGEN56A4JG247290"}, // 推送参数
                lineStyle: {strokeColor:"blue", strokeWeight:6, strokeOpacity:0.8},
                soketUrl: '',   // 推送地址
                iconUrl: '',    // 车辆图标
                startIcon: "",  // 轨迹开始图标
                endIcon: "",    // 轨迹结束图标
                markerSize: [28, 28],  // 图标尺寸
                startEndSize: [26, 37],  // 图标尺寸
                startEndAnchor: [10, 37],  // 图标尺寸
                trackControl: {     // 轨迹按钮自定义
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
          }
          this.originDef = tool.deepCopy(def);
          this.def = tool.extend(def,opt,true);
          this.Bmap = {},
          this.Amap = {},
          this.target = null, // 车辆
          this.listeners = []; //自定义事件，用于监听插件的用户交互
          this.handlers = {};
          this.newData = []; // 转换后的轨迹数据
          this.trackPoint = {}; // 原始轨迹数据
          this.splitTrackData = []; // 分段时间
          this.domId = document.getElementById(this.def.dom);
      },
     setTrack: function(data, timeLine, isClicktimeLine) {
       let config = this.def.config;
       this.newData = [];   // 重绘轨迹清除之前的轨迹
        data.map(item => {
          let a = this.translateToBmap(item);
          this.newData.push(new BMap.Point(a.lng, a.lat))
        })
        // 创建起、终点
        let startIcon = new BMap.Icon(config.startIcon, new BMap.Size(config.startEndSize[0],config.startEndSize[1]), {
          anchor: new BMap.Size(config.startEndAnchor[0],config.startEndAnchor[1])
        });
        let endIcon = new BMap.Icon(config.endIcon, new BMap.Size(config.startEndSize[0],config.startEndSize[1]), {
          anchor: new BMap.Size(config.startEndAnchor[0],config.startEndAnchor[1])
        });
        if(config.startIcon) this.Bmap.addOverlay(new BMap.Marker(this.newData[0],{icon:startIcon})); // 创建点
        else this.Bmap.addOverlay(new BMap.Marker(this.newData[0]));
        if(config.endIcon) this.Bmap.addOverlay(new BMap.Marker(this.newData[this.newData.length - 1],{icon:endIcon}));
        else this.Bmap.addOverlay(new BMap.Marker(this.newData[this.newData.length - 1]));
        // 目标点
        if (config.iconUrl) {
          let icon = new BMap.Icon(config.iconUrl, new BMap.Size(config.markerSize[0], config.markerSize[1]));
          this.target = new BMap.Marker(this.newData[0],{icon:icon}); // 创建点
        } else this.target = new BMap.Marker(this.newData[0]);
        this.Bmap.addOverlay(this.target); // 创建点
         this.setPolyline(this.newData)
         this.Bmap.setViewport(this.newData);
         if (!isClicktimeLine) {  // 点击分段时间线不重绘
          if (!document.querySelectorAll(".trackControl").length) this.creatDom(config, this.target); // 点击搜索已有不重绘
          if (config.showBorder) this.creatBorder(data, timeLine);
         }
          this.trackControl(this.target, config.trackControl);
      },
      setPolyline: function(lineData) {
        this.Bmap.addOverlay(new BMap.Polyline(lineData, this.def.config.lineStyle));  //增加折线
      },
      //根据点信息实时更新地图显示范围，让轨迹完整显示。设置新的中心点和显示级别
      setZoom: function (bPoints) {
        var view = this.Bmap.getViewport(eval(bPoints));
        var mapZoom = view.zoom;
        var centerPoint = view.center;
        this.Bmap.centerAndZoom(centerPoint, mapZoom);
      },
      setMoniter: function(data, marker) {
         let newData = this.translateToBmap(data)
         let point = new BMap.Point(newData.lng, newData.lat);
         marker.setPosition(point); // 改变点的位置
         marker.setRotation(data.direction); // 改变点的方向
          this.def.points.push(point);
        //  this.setZoom(map, this.def.points)
        this.Bmap.setViewport(this.def.points);
         this.setPolyline(this.def.points);
        // map.centerAndZoom(point, this.def.config.zoom)
      },
      creatDom: function(config) {
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
      this.domId.appendChild(div);
     },
     trackControl: function(target, control) {
      let trackControl = document.querySelectorAll(".trackControl li");
      let _this = this;
      trackControl[0].onclick = function() {
        control.isPlay = !control.isPlay;
        if (!control.isPlay) {
          this.children[1].innerHTML = control.startButton
          this.children[0].className = 'play';
          if(_this.listeners.indexOf('pause') > -1) {
            _this.emit({type:'pause',target: this})
        }
        } else {
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
         control.currentPoint = 0; // 重置点为新轨迹第一个点
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
     creatBorder: function(data, timeLine) {
        // console.log(data)    // <p>速度：${data[0].speed} km/h</p>
        if (!document.querySelectorAll(".trackBorder").length) {
              // 首次创建面板
              let str = `<p class="head">轨迹信息</p>
              <div class="body">
                  <p>VIN码：<span class="vin"></span></p>
                  <p >时间：<span class="trackTime"></span></p>
                  <p >起点：<span class="start"></span></p>
                  <p >终点：<span class="end"></span></p>
                  <div class="timeLine">
                    <div></div>
                  </div>
              </div>`
            let div = document.createElement('div');
            div.setAttribute('class', 'trackBorder');
            div.innerHTML = str;
            this.domId.appendChild(div);
            if (timeLine) {// 时间线区别5分钟轨迹和分段轨迹
              let timeLineDom =  document.querySelectorAll(".trackBorder .timeLine div")[0];
              timeLineDom.className = "height";
              this.creatTimeDom(data, timeLine, timeLineDom);
           }
        } else {
          if (timeLine) {// 时间线区别5分钟轨迹和分段轨迹
            let newLine = document.querySelector(".trackBorder .height");
            newLine.innerHTML = "";
            this.creatTimeDom(data, timeLine, newLine);
         }
        }
        // vin和时间初始化
        document.querySelector(".trackBorder .trackTime").innerText = data[0] ? tool.DateFormat(new Date(data[0].time), 'yyyy-MM-dd hh:mm:ss') : "";
        document.querySelector(".trackBorder .vin").innerText = this.vinCode;
        let geoc = new BMap.Geocoder();
        Promise.all([new Promise( (resolve) => {
          geoc.getLocation(this.newData[0], (lo) => {
            resolve(lo);
          });
        }),
        new Promise( (resolve) => {
          geoc.getLocation(this.newData[this.newData.length - 1], (lo) => {
            resolve(lo);
          });
        })
      ]).then((value) => {
        let [startad, endad, startC, startS, endC, endS] = [
          document.querySelector(".trackBorder .start"),
          document.querySelector(".trackBorder .end"),
          value[0].addressComponents, value[0].surroundingPois, value[1].addressComponents, value[1].surroundingPois]
        if (startC.province === endC.province && startC.city === endC.city && startC.district === endC.district) {
            startad.innerText = startC.street + startS[0].title;
            endad.innerText = endC.street + endS[0].title;
          } else if (startC.province === endC.province && startC.city === endC.city && startC.district !== endC.district){
            startad.innerText = startC.district + startC.street + startS[0].title;
            endad.innerText = endC.district + endC.street + endS[0].title;
          } else if (startC.province === endC.province && startC.city !== endC.city){
            startad.innerText = startC.city;
            endad.innerText = endC.city;
          } else if (startC.province !== endC.province){
            startad.innerText = startC.province;
            endad.innerText = endC.province;
          }
          
      })
      },
      creatTimeDom: function (data, timeLine, timeLineDom) {
        let trackTime = document.querySelector(".trackBorder .trackTime");
        trackTime.innerText = data[0] ? tool.DateFormat(new Date(data[0].time), 'yyyy-MM-dd hh:mm:ss') : "";
        let timeStr = '';
         timeLine.map(item => {
           timeStr += `<p>
           ${tool.DateFormat(new Date(item.startTime), 'hh:mm:ss')} -
           ${tool.DateFormat(new Date(item.endTime), 'hh:mm:ss')}
           </p>`
         })
         timeLineDom.innerHTML = timeStr;
        let childDom = Array.from(timeLineDom.children);
        childDom[0].className = "active";
        for(let i = 0; i < childDom.length ;i++)
        childDom[i].onclick = (e) => {
          childDom.map(item => {
            item.className = "";
          })
            e.target.className = "active";
            trackTime.innerText = tool.DateFormat(new Date(this.trackPoint.data[0].time), 'yyyy-MM-dd hh:mm:ss'); // 新轨迹段第一个时间
            // 播放按钮重置
            this.resetData();
            let obj = {startTime: timeLine[i].startTime, endTime: timeLine[i].endTime, vin: this.vinCode}
            this.getSplitData(obj, null, true);
          }
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
        let dataList = this.trackPoint.data;
        target.setPosition(this.newData[i]);// 车辆位置
        target.setRotation(dataList[i].direction);// 车辆方向
        if (i < this.newData.length - 1) {
          this.moveInter = setTimeout(() => {
            i++;
            document.querySelector(".trackBorder .trackTime").innerText = tool.DateFormat(new Date(dataList[i].time), 'yyyy-MM-dd hh:mm:ss')
             this.def.currentData(dataList[i]);
            this.resetMkPoint(target, control, i);
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
      getSplitData: function(obj, timeLine, isClicktimeLine) {
        tool.Ajax.get(`${this.def.config.trackApi}/ics/gps/list`, obj, (data) => {
          let da = JSON.parse(data);
          if (!da.data || !da.data.length)  return;
          this.trackPoint = da; // 原始数据，方向会用到
          if (isClicktimeLine) this.Bmap.clearOverlays();
          this.setTrack(da.data, timeLine, isClicktimeLine);
        }) 
      },
      init: function(fn) {
        if (!this.def.dom) return;
        if (!this.def.mapType) throw new Error('maptrack requires a mapType');
          if (this.def.mapType === 'bmap') {  
            tool.loadJScript().then(() => {
              let config = this.def.config;
              if (!Object.keys(this.Bmap).length) {
                console.log(123);
                let map = new BMap.Map(this.def.dom, {
                  enableMapClick: false
                })
                this.Bmap = map;
                this.BMap = BMap;
                map.enableScrollWheelZoom(); 
              }
              if (this.def.mapTrack) this.getTrack(config.trackParam);  // 最近5分钟轨迹回放
              else if (this.def.splitTrack) this.getSplitTime(config.splitTrackParam); // 分段轨迹
              else if (this.def.mapMointer) this.getSocket(config.moniterParam);   // 监控点
              if(fn) fn(BMap, this.Bmap);
            })
          }
      },
      // 分段轨迹时间段api
      getSplitTime: function(obj) {
        this.vinCode = obj.vin; // 存储vin
        tool.Ajax.get(`${this.def.config.trackApi}/ics/gps/listSplitSummary`, obj, (data) => {
          let da = JSON.parse(data);
          this.splitTrackData = da; // 存储分段数据
          if (!da.data || !da.data.length)  return;
            let ob = {startTime: da.data[0].startTime, endTime: da.data[0].endTime, vin: obj.vin};
            this.getSplitData(ob, da.data);
        })
      },
      // 推送监控点
      getSocket: function (obj) {
        this.vinCode = obj.vin; // 存储vin
        let config = this.def.config
        let point = new BMap.Point(config.gps[0], config.gps[1])
        this.Bmap.centerAndZoom(point, config.zoom)
        let marker = null;
        if (config.iconUrl) {
          let icon = new BMap.Icon(config.iconUrl, new BMap.Size(config.markerSize[0], config.markerSize[1]));
          marker = new BMap.Marker(point,{icon:icon}); // 创建点
        } else marker = new BMap.Marker(point);
        this.Bmap.addOverlay(marker);  // 标点
        tool.webSocket(config.soketUrl, {vinCode: obj.vin}, (data) => {
          this.setMoniter(data, marker)
        }) 
      },
      // 5分钟轨迹
      getTrack: function (obj) {
        this.vinCode = obj.vin; // 存储vin
        if (!obj.startTime && !obj.endTime) {
          let now = new Date().getTime();
          obj.endTime = now;
          obj.startTime = now - 300000;
        }
        this.getSplitData(obj);
      },
      // 搜索
      search: function(paramObj) {
         this.Bmap.clearOverlays();
         if (this.def.splitTrack) {
          this.getSplitTime(paramObj);
          this.resetData();
          this.resetBoard(paramObj.vin);
         } else if (this.def.mapTrack) {
          this.getTrack(paramObj);
          this.resetData();
          document.querySelector(".trackBorder .trackTime").innerText="";
          document.querySelector(".trackBorder .vin").innerText= paramObj.vin;
         } else if (this.def.mapMointer) {
          this.getSocket(paramObj);
         }
      },
      // 重载参数
      reload: function(obj) {
         this.Bmap.clearOverlays();
         origin = tool.deepCopy(this.originDef);
         if (this.def.splitTrack) {
          this.removeTools();
         } else if (this.def.mapTrack) {
          this.removeTools();
         } else if (this.def.mapMointer) {
         }
          this.def = tool.extend(origin,obj,true);
         this.init();
      },
      // 重设轨迹一些状态
      resetData: function () {
        let play = document.querySelectorAll(".trackControl li")[0].children;
        let control = this.def.config.trackControl;
        control.isPlay = false;
        play[1].innerHTML = control.startButton;
        play[0].className = 'play';
        control.currentPoint = 0;   // 重置点为新轨迹第一个点
      },
      // 重设面板
      resetBoard: function(vin) {
        document.querySelector(".trackBorder .trackTime").innerText="";
        document.querySelector(".trackBorder .vin").innerText= vin;
        document.querySelector(".trackBorder .height").innerText="";
      },
      // 重载清除面板工具
      removeTools: function() {
        if (this.moveInter) clearTimeout(this.moveInter)
        let trackBorder = document.querySelector(".trackBorder");
        let trackControl = document.querySelector(".trackControl");
        if (trackBorder) {
          trackBorder.innerText="";
          trackBorder.parentNode.removeChild(trackBorder);
        }
        if (trackControl) {
          trackControl.innerText="";
          trackControl.parentNode.removeChild(trackControl);
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
