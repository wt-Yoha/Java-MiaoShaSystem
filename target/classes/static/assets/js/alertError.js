function alertErrorMsg() {
    var code = parseInt($("#codeMsg").text());
    if (!isNaN(code) && code !== 0) {
        alert($("#codeMsg").attr("value"));
    }
}
