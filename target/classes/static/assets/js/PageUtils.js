let afterLoginHocks = {};
// 用于保存地址栏状态
let urlState="";

// 加载header和footer
function loadHeaderAndFooter() {
    loadHeader();
    loadFooter();
}

// 加载header
function loadHeader() {
    $("#myHeader").load("header.html", function () {
        getLoginUser();
    });
}

// 加载footer
function loadFooter() {
    $("#myFooter").load("footer.html", function () {
        $("#submitAdvice").attr("onclick", "submitAdvice()");
    });
}

// logout
function logout() {
    $.ajax({
        url: contentUrl("/user/logout"),
        type: "GET",
        success: function (result) {
            resultProcessing(
                result,
                function (result) {
                    alert(result.data);
                    window.location.href = contentUrl("/shop.html");
                }
            );
        }
    })
}

// header 获取登陆用户信息
function getLoginUser() {
    var tag = $(document.getElementById("welcome"));
    $.ajax({
        url: contentUrl("/user/loginMsg"),
        type: "GET",
        success: function (result) {
            if (parseInt(result.code) === 0 && result.data !== null) {
                tag.text("欢迎回来，" + result.data.nickname);
                tag.attr("href", contentUrl("/orderList.html"));
                let logout = $("#logout");
                logout.attr("style", "visibility: visible");
                afterLogin();
            } else {
                tag.text("您还未登录");
                tag.attr("title", "前往登陆");
                tag.attr("href", contentUrl("/login.html"));
            }
        },
        error: function () {
            tag.text("您还未登录");
            tag.attr("title", "前往登陆");
            tag.attr("href", contentUrl("/login.html"));
        }
    });
}

function afterLogin() {
    for (let key in afterLoginHocks) {
        let func = afterLoginHocks[key];
        func();
    }
}

function setAfterLogin(name, hock) {
    afterLoginHocks[name] = hock;
}

// footer 提交建议
function submitAdvice() {
    var text = $("#adviceText").val();
    $.ajax({
        url: contentUrl("/user/adviceSubmit"),
        data: {msg: text},
        type: "POST",
        success: function (result) {
            resultProcessing(
                result,
                function (result) {
                    alert(result.data);
                },
                alertErrorMsg
            );
        }
    })
}

// 配置项目contentPath
function contentUrl(url) {
    var contentPath = "/breezewind"
    return contentPath + url;
}

// 跳转页面错误提示信息
function showErrorPage(error) {
    var container = $("#myContainer");
    container.load("ErrorSegment.html", function () {
        $("#errorCode").text(error.code);
        $("#errorMsg").text(error.msg);
    });
}

// alert错误信息
function alertErrorMsg(error) {
    alert(error.msg);
}

// alert 页面错误提示
function alertErrorPage(error) {
    alert(error.code + ": " + error.msg);
}

// 获取result后的通用处理流程
function resultProcessing(result, successCallback, errorCallback) {
    if (parseInt(result.code) === 0) {
        // 服务器返回了正确的结果
        if (successCallback !== undefined) {
            successCallback(result);
        }
    } else {
        if (errorCallback !== undefined) {
            errorCallback(result);
        } else {
            showErrorPage(result);
        }
    }
}

// 获取地址栏参数
function getUrlParam(key) {
    let map = parseUrlParam();
    if (map === undefined) {
        return undefined;
    }
    return map[key];
}

// 保持地址栏状态并设置参数保存在urlState
function setUrlParam(key, value, url) {
    if (url === undefined) {
        url = urlState;
    }
    let map = parseParam(url), resUrl="?", count = 0;
    map = map === undefined ? {} : map;
    map[key] = value;
    for (let k in map) {
        if (count === 0) {
            resUrl += k + "=" + map[k];
            count++;
        } else {
            resUrl += "&"+ k + "=" + map[k];
        }
    }
    urlState = resUrl;
    return resUrl;
}

// 获取urlState
function getUrlState() {
    return urlState;
}

// 解析地址栏参数
function parseUrlParam() {
    let url = location.href;
    return parseParam(url);
}

// 解析一个字符串url
function parseParam(url){
    let map = {};
    try {
        let variables = url.split("?")[1];
        let strings = variables.split("&");
        for (let i = 0; i < strings.length; i++) {
            let group = strings[i].split("=");
            map[group[0]] = group[1];
        }
    }catch (e) {
        return undefined;
    }
    return map;
}

// 拓展Date实现格式化时间
Date.prototype.Format = function (fmt) {
    var o = {
        "M+": this.getMonth() + 1, //月份
        "d+": this.getDate(), //日
        "H+": this.getHours(), //小时
        "m+": this.getMinutes(), //分
        "s+": this.getSeconds(), //秒
        "q+": Math.floor((this.getMonth() + 3) / 3), //季度
        "S": this.getMilliseconds() //毫秒
    };
    if (/(y+)/.test(fmt)) fmt = fmt.replace(RegExp.$1, (this.getFullYear() + "").substr(4 - RegExp.$1.length));
    for (var k in o)
        if (new RegExp("(" + k + ")").test(fmt)) fmt = fmt.replace(RegExp.$1, (RegExp.$1.length == 1) ? (o[k]) : (("00" + o[k]).substr(("" + o[k]).length)));
    return fmt;
}

// Header 搜索框
function searchGoods() {
    let keys = $(".searchfield.txt-livesearch.input").val();
    setUrlParam("currentPage", 1);
    let param = setUrlParam("searchKeys", keys);
    window.location.href = contentUrl("/shop-list.html" + param);
}