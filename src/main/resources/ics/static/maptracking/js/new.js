class Maptrack {
    constructor(opt) {
        this.opt = opt;
        var def = {
            mapType: '',
            dom: '',
            mapconfig: {
                gps: [116.404, 39.915],
                zoom: 10,
            },
            mapTrack: false,
            trackconfig: {
                trackApi: '',
                trackParam: { pageNum: 1, pageSize: 10 }
            }
        };
        this.def = extend(def, this.opt, true);
        this.Ajax = {
            get: function (url, opt, fn) {
                var xhr = new XMLHttpRequest();
                let date = new Date().getTime();
                url += '?';
                for (let key in opt) {
                    url += `${key}=${opt[key]}&`;
                }
                url += `timestamp=${date}`;
                xhr.open('GET', url, true);
                xhr.onreadystatechange = function () {
                    if (xhr.readyState == 4 && xhr.status == 200 || xhr.status == 304) {
                        fn.call(this, JSON.parse(xhr.responseText));
                    }
                };
                xhr.send();
            },
            post: function (url, data, fn) {
                var xhr = new XMLHttpRequest();
                xhr.open("POST", url, true);
                xhr.setRequestHeader("Content-Type", "application/x-www-form-urlencoded");
                xhr.onreadystatechange = function () {
                    if (xhr.readyState == 4 && (xhr.status == 200 || xhr.status == 304)) {
                        fn.call(this, JSON.parse(xhr.responseText));
                    }
                };
                xhr.send(data);
            }
        };
    }
    setBmap(obj) {
        let myMap = new BMap.Map(obj.dom);
        let point = new BMap.Point(obj.mapconfig.gps[0], obj.mapconfig.gps[1]);
        myMap.centerAndZoom(point, obj.mapconfig.zoom);
        myMap.enableScrollWheelZoom();
    }
    setTrack(data) {
        let map = new BMap.Map(this.def.dom);
        let point = new BMap.Point(data.lng, data.lat);
        map.centerAndZoom(point, this.def.mapconfig.zoom);
        map.enableScrollWheelZoom();
        let marker = new BMap.Marker(point);
        map.addOverlay(marker);
    }
    init() {
        console.log(this.def);
        if (!this.def.dom)
            return;
        if (this.def.mapType) {
            if (this.def.mapType === 'bmap') {
                loadJScript().then(() => {
                    let trackconfig = this.def.trackconfig;
                    if (this.def.mapTrack) {
                        this.Ajax.get(`${trackconfig.trackApi}/ics/gps/page`, trackconfig.trackParam, (data) => {
                            if (data.dataList.length)
                                this.setTrack(data.dataList[1]);
                            else
                                this.setBmap(this.def);
                        });
                    }
                    else {
                        this.setBmap(this.def);
                    }
                });
            }
        }
        else
            throw new Error('maptrack requires a mapType');
    }
}
function extend(o, n, override) {
    for (var key in n) {
        if (n.hasOwnProperty(key) && (!o.hasOwnProperty(key) || override)) {
            if (typeof n[key] !== "object")
                o[key] = n[key];
            else
                Object.assign(o[key], n[key]);
        }
    }
    return o;
}
function loadJScript() {
    if (!window.BMap) {
        window.BMap = {};
        window.BMap._preloader = new Promise((resolve, reject) => {
            window._initBaiduMap = function () {
                resolve(window.BMap);
                window.document.body.removeChild($script);
                window.BMap._preloader = null;
                window._initBaiduMap = null;
            };
            const $script = document.createElement('script');
            window.document.body.appendChild($script);
            $script.src = `//api.map.baidu.com/api?v=2.0&ak=ehokpezgpQESNRi1ld0fQmRSgAoO6YAG&callback=_initBaiduMap`;
        });
        return window.BMap._preloader;
    }
    else if (!window.BMap._preloader) {
        return Promise.resolve(window.BMap);
    }
    else {
        return window.BMap._preloader;
    }
}
