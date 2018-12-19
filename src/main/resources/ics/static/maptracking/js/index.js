
// plugin.js
;(function(undefined) {
  "use strict"
  var _global;

  // 工具函数
  // 对象合并
  function extend(o,n,override) {
      for(var key in n){
          if(n.hasOwnProperty(key) && (!o.hasOwnProperty(key) || override)){
              o[key]=n[key];
          }
      }
      return o;
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
// 动态加载script
function loadJScript(url) {
  var script = document.createElement('script')
  script.type = 'text/javascript'
  script.src = url
  document.head.appendChild(script)
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
          };
          this.def = extend(def,opt,true);
          this.hasDom = false;
          this.listeners = []; //自定义事件，用于监听插件的用户交互
          this.handlers = {};
      },
      initBmap: function() {
        let map = new BMap.Map("apiId");
        let point = new BMap.Point(113.310815, 23.066644);
        map.centerAndZoom(point, 9);
        map.enableScrollWheelZoom();
      },
      init: function() {
        if (!this.def.dom) return
        if (this.def.mapType) {
          if (this.def.mapType === 'bmap') {
            window.initBmap = this.initBmap
            addLoadEvent(loadJScript(`http://api.map.baidu.com/api?v=2.0&ak=ehokpezgpQESNRi1ld0fQmRSgAoO6YAG&callback=initBmap`))
            this.Ajax.get('/ics/gps/page?pageNum=1&pageSize=5', function (data) {
               console.log(JSON.parse(data))
            })
          }
        } else throw new Error('maptrack requires a mapType')
      },
      Ajax: {
        get: function(url, fn) {
          var xhr = new XMLHttpRequest();            
          xhr.open('GET', url, true);
          xhr.onreadystatechange = function() {
            // readyState == 4说明请求已完成
            if (xhr.readyState == 4 && xhr.status == 200 || xhr.status == 304) { 
              // 从服务器获得数据 
              fn.call(this, xhr.responseText);  
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
              fn.call(this, xhr.responseText);
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


// plugin.js
;(function(undefined) {
  "use strict"
  var _global;

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
// 动态加载script
function loadJScript(url) {
  var script = document.createElement('script')
  script.type = 'text/javascript'
  script.src = url
  document.head.appendChild(script)
}

  // 构造函数 - 返回数组结构
  // function maptrack(opt){
  //     this._initial(opt);
  // }
  window.initBmap = function initBmap() {
          let map = new BMap.Map("apiId");
          let point = new BMap.Point(113.310815, 23.066644);
          var marker = new BMap.Marker(point);  // 创建标注
	        map.addOverlay(marker);              // 将标注添加到地图中
          map.centerAndZoom(point, 9);
          map.enableScrollWheelZoom();
        }
  // function setBmap(obj) {
  //   let setMap = new Maptrack(obj)
  //   setMap.
  // }      
  var maptrack = {
    init: function(obj) {
      if (!obj.dom) return
      if (obj.mapType) {
        if (obj.mapType === 'bmap') {
          addLoadEvent(loadJScript(`http://api.map.baidu.com/api?v=2.0&ak=ehokpezgpQESNRi1ld0fQmRSgAoO6YAG&callback=initBmap`))
          // this.Ajax.get('/ics/gps/page?pageNum=1&pageSize=5', function (data) {
          //    console.log(JSON.parse(data))
          // })
          // setBmap(obj)
          console.log()
        }
      } else throw new Error('maptrack requires a mapType')
    },
  }

  // 将插件对象暴露给全局对象
  _global = (function(){ return this || (0, eval)('this'); }());
  if (typeof module !== "undefined" && module.exports) {
      module.exports = maptrack;
  } else if (typeof define === "function" && define.amd) {
      define(function(){return maptrack;});
  } else {
      !('maptrack' in _global) && (_global.maptrack = maptrack);
  }
}());