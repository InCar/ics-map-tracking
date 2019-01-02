  // 工具函数
  // 对象合并
  const extend = (o,n,override) => {
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
// 动态加载script 
const loadJScript = () => {
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
export {
  loadJScript,
  webSocket,
  extend,
  Ajax,
  DateFormat
}