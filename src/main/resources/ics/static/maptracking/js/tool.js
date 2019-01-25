  // 工具函数
  // 对象合并
  const extend = (o,n,override) => {
    for(var key in n){
        if(n.hasOwnProperty(key) && (!o.hasOwnProperty(key) || override)){
          if (typeof n[key] !== "object" || n[key] instanceof Array) o[key]=n[key];
          else {
            extend(o[key],n[key],override)
          }
        }
    }
    return o;
}

 // deepCopy
 const deepCopy = (obj, cache = []) => {
  if (obj === null || typeof obj !== 'object') {
    return obj;
  }
  const hit = find(cache, c => c.original === obj);
  if (hit) {
    return hit.copy;
  }

  const copy = Array.isArray(obj)
    ? []
    : {};
  cache.push({
    original: obj,
    copy
  });

  Object.keys(obj).forEach(key => {
    copy[key] = deepCopy(obj[key], cache);
  });
  return copy;
};
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
// 动态加载script 
const loadJScript = (obj) => {
  if (!window.BMap) {
    window.BMap = {}
    window.BMap._preloader = new Promise((resolve, reject) => {
      window._initBaiduMap = function () {
        resolve(window.BMap)
        window.document.head.removeChild($script)
        window.BMap._preloader = null
        window._initBaiduMap = null
      }
      const $script = document.createElement('script')
      window.document.head.appendChild($script)
      $script.src = `//api.map.baidu.com/api?v=2.0&ak=${obj.mapKey}&callback=_initBaiduMap`
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
const Ajax = {
  get: function(url, opt, fn) {
    let date = new Date().getTime();
    var xhr = new XMLHttpRequest();
    url += '?' 
   for(let key in opt) {
     url += `${key}=${opt[key]}&`
   }  
    url += `timestamp=${date}`;
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
// 转具体时间
const MillisecondToDate = (msd) => {
  var time = parseFloat(msd) / 1000;
  if (time !== null && time !== '') {
      if (time > 60 && time < 60 * 60) {
          let m = parseInt(time / 60.0);
          let s = parseInt((parseFloat(time / 60.0) - parseInt(time / 60.0)) * 60);
          if (s === 0) {
             time = m + '分';
          } else {
            time = m + '分';
          }
      } else if (time >= 60 * 60 && time < 60 * 60 * 24) {
          let h = parseInt(time / 3600.0);
          let s = parseInt((parseFloat((parseFloat(time / 3600.0) - parseInt(time / 3600.0)) * 60) -
                  parseInt((parseFloat(time / 3600.0) - parseInt(time / 3600.0)) * 60)) * 60);
          let m = parseInt((parseFloat(time / 3600.0) - parseInt(time / 3600.0)) * 60);
          if (s === 0 && m === 0) {
             time = h + '时';
          } else if (s === 0) {
             time = h + '时' + m + '分';
          } else {
            time = h + '时' + m + '分';
          }
      } else if (time >= 60 * 60 * 24) {
         let d = parseInt(time / 3600.0 / 24);
         let h = parseInt((parseFloat(time / 3600.0 / 24) - parseInt(time / 3600.0 / 24)) * 24);
         let m = parseInt((parseFloat(time / 3600.0) - parseInt(time / 3600.0)) * 60);
         let s = parseInt((parseFloat((parseFloat(time / 3600.0) - parseInt(time / 3600.0)) * 60) -
                 parseInt((parseFloat(time / 3600.0) - parseInt(time / 3600.0)) * 60)) * 60);
        if (s === 0 && m === 0 && h === 0) {
             time = d + '天';
          } else if (s === 0 && m === 0) {
             time = d + '天' + h + '时';
          } else if (s === 0) {
            time = d + '天' + h + '时' + m + '分';
          } else {
            time = d + '天' + h + '时' + m + '分';
          }
      } else {
        time = parseInt(time) + '秒';
      };
  } else {
      time = '0 时 0 分';
  };
  return time;
};
//移动车辆，count两点间要移动的次数，timer，每次移动的时间，毫秒
const moveCar = (map, prvePoint, newPoint, timer, marker, count) => {
  var _prvePoint = new BMap.Pixel(0,0);
  var _newPoint = new BMap.Pixel(0,0);
  //当前帧数
  var currentCount = 0;
  //初始坐标
  _prvePoint = map.getMapType().getProjection().lngLatToPoint(prvePoint);
  //获取结束点的(x,y)坐标
  _newPoint = map.getMapType().getProjection().lngLatToPoint(newPoint);
  
  //两点之间匀速移动
  var intervalFlag = setInterval(function() {
    //两点之间当前帧数大于总帧数的时候，则说明已经完成移动
    if (currentCount >= count) {
      clearInterval(intervalFlag);
    } else {
      //动画移动
      currentCount++;//计数
      //console.log(currentCount);
      var x = linear(_prvePoint.x, _newPoint.x, currentCount,
          count);
      var y = linear(_prvePoint.y, _newPoint.y, currentCount,
          count);
      //根据平面坐标转化为球面坐标
      var pos = map.getMapType().getProjection().pointToLngLat(new BMap.Pixel(x, y));
      //console.log(pos);
      
      marker.setPosition(pos);
      //调整方向
      setRotation(map, prvePoint, newPoint, marker);
    }
  }, timer);
  //marker.removeOverlay(marker);
}
//
function linear(initPos, targetPos, currentCount, count) {
  var b = initPos, c = targetPos - initPos, t = currentCount, d = count;
  return c * t / d + b;
}
//设置方向
function setRotation(map, curPos, targetPos, marker) {
  var deg = 0;
  curPos = map.pointToPixel(curPos);
  targetPos = map.pointToPixel(targetPos);


  if (targetPos.x != curPos.x) {
    var tan = (targetPos.y - curPos.y) / (targetPos.x - curPos.x), atan = Math
        .atan(tan);
    deg = atan * 360 / (2 * Math.PI);
    if (targetPos.x < curPos.x) {
      deg = -deg + 90 + 90;
    } else {
      deg = -deg;
    }
    marker.setRotation(-deg);
  } else {
    
    var disy = targetPos.y - curPos.y;
    var bias = 0;
    if (disy > 0){
    bias = -1;
    }
    else{
    bias = 1;
    }
    marker.setRotation(-bias * 90);
  }
  return;
}




export {
  loadJScript,
  webSocket,
  extend,
  moveCar,
  run,
  MillisecondToDate,
  deepCopy,
  Ajax,
  getElementsByClassName,
  DateFormat
}