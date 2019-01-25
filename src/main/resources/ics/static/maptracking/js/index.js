 import "../css/index.scss";
 import * as tool from './tool'
// plugin.js
;(function(undefined) {
  "use strict"
  var _global;
  function $(dom){
    return document.querySelector(dom)
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
              mapKey: "Z387qRaNG1dZvs0xrpNDMWTVh2ZhWRkW", //地图key
              splitTrack: false,       // 是否开启分段轨迹
              mapMointer: false,   // 是否开启推送
              trackApi: '',           // 自定义路径
              currentData: function() { // 获取运动当前点数据，和showBorder配合使用
              },
              getBmap: function(a, b) { // 获取Map和当前地图对象做外部拓展，且只加载一次
              },
              currentSoket: function(data) { //推送数据
              },
              getSoket: function(target) {
              },
              points: [], // 监控点
              config: {
                showBorder: true, // 是否展示面板
                gps: [116.404, 39.915], // 初始化地图经纬度
                zoom: 16,                // 初始化地图层级
                trackParam: {vin: "LVGEN56A4JG247290"},  // 轨迹参数
                splitTrackParam: {vin: "LVGEN56A4JG247290"},  // 分段轨迹参数
                moniterParam: {vin: "LVGEN56A4JG247290"}, // 推送参数
                lineStyle: {strokeColor:"green", strokeWeight:6, strokeOpacity:0.8, enableClicking: false},
                isSequence: false,  // 开启轨迹线箭头
                iconSequence: {
                  scale: 0.6,//图标缩放大小
                  strokeColor:'#fff',//设置矢量图标的线填充颜色
                  strokeWeight: '2',//设置线宽
                },
                speedColor: ["red", "yellow", "green"],
                speedSplit: [5, 15, 20],
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
          this.init(this.def)
      },
     setTrack: function(data) {
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
     //    if (!isClicktimeLine) {  // 点击分段时间线不重绘
          if (!document.querySelectorAll(".trackControl").length) this.creatDom(config, this.target); // 点击搜索已有不重绘
          // if (config.showBorder) 
          $(".trackInfo .trackTime").innerText = data[0] ? tool.DateFormat(new Date(data[0].time), 'yyyy-MM-dd hh:mm:ss') : "";
          $(".trackInfo .vin").innerText = this.vinCode;
          $(".trackInfo .slot").onclick = () => {
            $(".trackModel").style.display = "block"
            if (this.moveInter) clearTimeout(this.moveInter)
            this.resetData();
          };
      //   }
          this.trackControl(this.target, config.trackControl);
      },
      setFive: function(data) {
        let points = [];
        data.map(item => {
          let a = this.translateToBmap(item);
          points.push(new BMap.Point(a.lng, a.lat));
        })
        // this.setPolyline(points);
        this.fiveP = new BMap.Polyline(points, this.def.config.lineStyle);
        this.Bmap.addOverlay(this.fiveP);
      },
      setPolyline: function(lineData) {
        let c = this.def.config;
        if (Object.keys(c.iconSequence).length && c.isSequence) {
          let icons = null;
          icons = new BMap.IconSequence(
            new BMap.Symbol(BMap_Symbol_SHAPE_BACKWARD_OPEN_ARROW, c.iconSequence), '10', '30');
            c.lineStyle.icons = [icons];
        }
        let t = this.trackPoint.data
        if (t.length <= 1) this.Bmap.addOverlay(new BMap.Polyline(lineData, c.lineStyle))
        else {
          for (let i = 1; i < t.length; i++) {
            let averSpeed = (t[i].speed + t[i - 1].speed) / 2;
            for (let j = 0; j < c.speedSplit.length;j++) { 
              if (j === 0) {
                if (averSpeed <= c.speedSplit[j] && averSpeed >= 0) {
                  c.lineStyle.strokeColor = c.speedColor[j];
                  this.Bmap.addOverlay(new BMap.Polyline([lineData[i - 1], lineData[i]], c.lineStyle));  //增加折线
                  break;
                }
              } else if (j === c.speedSplit.length - 1) {
                if (averSpeed > c.speedSplit[j] || (averSpeed <= c.speedSplit[j] && averSpeed > c.speedSplit[j - 1])) {
                  c.lineStyle.strokeColor = c.speedColor[j];
                  this.Bmap.addOverlay(new BMap.Polyline([lineData[i - 1], lineData[i]], c.lineStyle));  //增加折线
                  break;
                }
              } else {
                if (averSpeed <= c.speedSplit[j] && averSpeed > c.speedSplit[j - 1]) {
                  c.lineStyle.strokeColor = c.speedColor[j];
                  this.Bmap.addOverlay(new BMap.Polyline([lineData[i - 1], lineData[i]], c.lineStyle));  //增加折线
                  break;
                }
              }
            }
          }
        }
      },
      //根据点信息实时更新地图显示范围，让轨迹完整显示。设置新的中心点和显示级别
      setZoom: function (bPoints) {
        var view = this.Bmap.getViewport(eval(bPoints));
        var mapZoom = view.zoom;
        var centerPoint = view.center;
        this.Bmap.centerAndZoom(centerPoint, mapZoom);
      },
      setMoniter: function(data, marker) {
         let newData = this.translateToBmap(data);
         let newPoint = new BMap.Point(newData.lng, newData.lat);
         this.Bmap.addOverlay(marker);  // 标点
         this.def.points.push(newPoint);
          // marker.setPosition(newPoint); // 改变点的位置
          // marker.setRotation(data.direction); // 改变点的方向
         if (this.prvePoint) tool.moveCar(this.Bmap, this.prvePoint, newPoint, 30, marker, 50);
         this.prvePoint = newPoint;
         this.Bmap.setViewport(this.def.points);
         // 只绘制最后两个点的线
          if (this.def.points.length >= 3) this.setPolyline(this.def.points.slice((this.def.points.length - 2), this.def.points.length));
          else this.setPolyline(this.def.points);
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
           _this.emit({type:'reduce',target: this});
       }
       }
     },
      // 分段轨迹
      creatTimeDom: function (timeLine) {
        let timeLineDom = $(".trackModel .timeLine");
        if (!timeLine || !timeLine.length) {
          timeLineDom.innerText = "暂无轨迹数据";
          return;
        }
        let [timeStr, promiseArray, geoc] = ['', [], new BMap.Geocoder()];
         timeLine.map(item=> {
          let a = this.translateToBmap({lng: item.startPoint[0], lat:item.startPoint[1]});
          let b = this.translateToBmap({lng: item.endPoint[0], lat: item.endPoint[1]});
          let [start, end] = [new BMap.Point(a.lng, a.lat), new BMap.Point(b.lng, b.lat)]
          promiseArray.push(this.creatAdds(geoc, start, end));
         })
         Promise.all(promiseArray).then((data) => {
              data.map((item, i) => {
                timeStr += `<div>
                  ${tool.DateFormat(new Date(timeLine[i].startTime), 'MM-dd hh:mm')}
                  <span>(${tool.MillisecondToDate(timeLine[i].endTime - timeLine[i].startTime)})<span>
                  <p><span class="start">${item.start}</span> - <span class="end">${item.end}</span>
                  </p></div>`
              })
              timeLineDom.innerHTML = timeStr
              $(".trackModel").style.display = "block";
              let childDom = Array.from(timeLineDom.children);
              // childDom[0].className = "active";
              for(let i = 0; i < childDom.length ;i++)
              childDom[i].onclick = (e) => {
                childDom.map(item => {
                  item.className = "";
                })
                  e.target.className = "active";
                  // trackTime.innerText = tool.DateFormat(new Date(this.trackPoint.data[0].time), 'yyyy-MM-dd hh:mm:ss'); // 新轨迹段第一个时间
                  // 播放按钮重置
                  $(".trackModel .slot").onclick = (e) => {
                    $(".trackModel").style.display = "none";
                    $(".trackInfo").style.display = "block";
                    $(".trackInfo .start").innerText = data[i].start;
                    $(".trackInfo .end").innerText = data[i].end;
                    if (this.moveInter) clearTimeout(this.moveInter);
                    if ($(".trackControl")) this.resetData();
                    let obj = {startTime: timeLine[i].startTime, endTime: timeLine[i].endTime, vin: this.vinCode}
                    this.Bmap.clearOverlays();
                    this.getSplitData(obj)
                  }
                }
          })
      },
      // 转化起点终点
      creatAdds: function(geoc, start, end) {
        let adPro = Promise.all([new Promise( (resolve) => {
          geoc.getLocation(start, (lo) => {
            resolve(lo);
          });
        }),
        new Promise( (resolve) => {
          geoc.getLocation(end, (lo) => {
            resolve(lo);
          });
        })
      ]).then((value) => {
        let [ad, startC, startS, endC, endS] = [{},
          value[0].addressComponents, value[0].surroundingPois, value[1].addressComponents, value[1].surroundingPois]
        if (startC.province === endC.province && startC.city === endC.city && startC.district === endC.district) {
            ad.start = startC.street + (startS.length ? startS[0].title : "");
            ad.end = endC.street + (endS.length ? endS[0].title : "");
          } else if (startC.province === endC.province && startC.city === endC.city && startC.district !== endC.district){
            ad.start = startC.district + startC.street + (startS.length ? startS[0].title : "");
            ad.end = endC.district + endC.street + (endS.length ? endS[0].title : "");
          } else if (startC.province === endC.province && startC.city !== endC.city){
            ad.start = startC.city;
            ad.end = endC.city;
          } else if (startC.province !== endC.province){
            ad.start = startC.province;
            ad.end = endC.province;
          }
          return ad;
      })
      return adPro
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
            $(".trackInfo .trackTime").innerText = tool.DateFormat(new Date(dataList[i].time), 'yyyy-MM-dd hh:mm:ss')
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
      getSplitData: function(obj, five) {
        tool.Ajax.get(`${this.def.trackApi}/ics/gps/list`, obj, (data) => {
          let da = JSON.parse(data);
          if (!da.data || !da.data.length)  return;
          this.trackPoint = da; // 原始数据，方向会用到
          if (five) this.setFive(da.data)
          else this.setTrack(da.data);
        }) 
      },
      init: function(obj) {
        if (!obj.dom) return;
        if (!obj.mapType) throw new Error('maptrack requires a mapType');
          if (obj.mapType === 'bmap') {  
            tool.loadJScript(obj).then(() => {
              let config = obj.config;
              if (!Object.keys(this.Bmap).length) {
                let map = new BMap.Map(obj.dom, {
                  enableMapClick: false
                })
                let point = new BMap.Point(config.gps[0], config.gps[1]); 
                map.centerAndZoom(point,config.zoom);
                this.Bmap = map;
                this.BMap = BMap;
                map.enableScrollWheelZoom(); 
                obj.getBmap(BMap, this.Bmap);
              }
              if (obj.splitTrack) this.getSplitTime(config.splitTrackParam); // 分段轨迹
              else if (obj.mapMointer) this.getSocket(config.moniterParam);   // 监控点
            })
          }
      },
      // 分段轨迹时间段api
      getSplitTime: function(obj) {
        this.vinCode = obj.vin; // 存储vin
        tool.Ajax.get(`${this.def.trackApi}/ics/gps/listSplitSummary`, obj, (data) => {
          let da = JSON.parse(data);
          this.splitTrackData = da; // 存储分段数据
          if (!$(".trackModel")) this.creatModel();
          this.creatTimeDom(da.data)
        })
      },
      // 分段轨迹弹层
      creatModel: function (){
        let str = `<div class="content">
          <p class="head">轨迹分段信息<span class="slot">收起</span></p>
          <div class="timeLine"></div>
        </div>`
        let topStr = `<div class="content">
        <span>VIN码：<span class="vin"></span></span>
                  <span>时间：<span class="trackTime"></span></span>
                  <span>起点：<span class="start"></span></span>
                  <span>终点：<span class="end"></span></span>
        </div>
        <span class="slot">展开</span>`
        let div = document.createElement('div');
        div.setAttribute('class', 'trackModel');
        let topDiv = document.createElement('div');
        topDiv.setAttribute('class', 'trackInfo');
        div.innerHTML = str;
        topDiv.innerHTML = topStr;
        this.domId.appendChild(div);
        this.domId.appendChild(topDiv);
      },
      // 推送监控点
      getSocket: function (obj) {   
        let data = [
          {lat:30.4824,lng:114.397257, direction: 90},
          {lat:30.482563,lng:114.396676, direction: 30},
          {lat:30.482374,lng:114.395602, direction: 40},
          {lat:30.482343,lng:114.394226, direction: 20},
          {lat:30.482326,lng:114.393722, direction: 120},
          {lat:30.482331,lng:114.393278, direction: 150}
        ]
        this.vinCode = obj.vin; // 存储vin
        this.def.points = []; //搜索清空先
        let i = 0;
        let config = this.def.config
        let point = new BMap.Point(config.gps[0], config.gps[1])
        this.Bmap.centerAndZoom(point, config.zoom)
        let marker = null;
        if (config.iconUrl) {
          let icon = new BMap.Icon(config.iconUrl, new BMap.Size(config.markerSize[0], config.markerSize[1]));
          marker = new BMap.Marker(point,{icon:icon}); // 创建点
        } else marker = new BMap.Marker(point);
          this.circle = setInterval(() => {
                this.setMoniter(data[i], marker)
                i++
                if(i === data.length) clearInterval(this.circle);
              }, 3000);
        // this.def.getSoket(marker);
        // this.soket = tool.webSocket(config.soketUrl, {vinCode: obj.vin}, (data) => {
        //   this.setMoniter(data, marker);
        //   this.def.currentSoket(data);
        // }) 
      },
      // 5分钟轨迹
      getTrack: function (obj) {
        this.vinCode = obj.vin; // 存储vin
        if (!obj.startTime && !obj.endTime) {
          let now = new Date().getTime();
          obj.endTime = now;
          obj.startTime = now - 300000;
        }
        this.getSplitData(obj, true);
      },
      // 搜索
      search: function(paramObj) {
         this.Bmap.clearOverlays();
         if (this.def.splitTrack) {
          this.getSplitTime(paramObj)
          if ($(".trackControl")) this.resetData();
          if ($(".trackInfo .vin")) $(".trackInfo .vin").innerText= paramObj.vin;
         } else if (this.def.mapMointer) {
          if (this.soket) this.soket.close();
          // if (this.circle) clearInterval(this.circle);
          // let data = [
          //   {lat:30.482343,lng:114.394226, direction: 0},
          //   {lat:30.482326,lng:114.393722, direction: 0},
          //   {lat:30.482331,lng:114.393278, direction: 0},
          //    {lat:30.482331,lng:114.393278, direction: 0},
          //    {lat:30.482314,lng:114.392932, direction: 271},
          //    {lat:30.482318,lng:114.392251, direction: 0}
          // ]
          this.getSocket(paramObj);
         }
      },
      // 重载参数
      reload: function(obj) {
         this.Bmap.clearOverlays();
         if (this.soket) this.soket.close();
         let origin = tool.deepCopy(this.originDef);
         origin.dom = this.def.dom;
         origin.mapType = this.def.mapType;
         if (origin.trackApi) origin.trackApi = this.def.trackApi;
         if (origin.mapKey) origin.mapKey = this.def.mapKey;
         if (this.def.splitTrack) {
          this.removeTools();
         } else if (this.def.mapMointer) {
         };
          this.def = tool.extend(origin,obj,true);
         this.init(this.def);
      },
      addTrack: function(params) {
        this.getTrack(params);
      },
      clearTrack: function() {
        this.Bmap.removeOverlay(this.fiveP);
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
      // 重载清除面板工具
      removeTools: function() {
        if (this.moveInter) clearTimeout(this.moveInter)
        let trackInfo = $(".trackInfo");
        let trackControl = $(".trackControl");
        let trackModel = $(".trackModel");
        if (trackInfo) {
          trackInfo.innerText="";
          trackInfo.parentNode.removeChild(trackInfo);
        }
        if (trackControl) {
          trackControl.innerText="";
          trackControl.parentNode.removeChild(trackControl);
        }
        if (trackModel) {
          trackModel.innerText="";
          trackModel.parentNode.removeChild(trackModel);
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
