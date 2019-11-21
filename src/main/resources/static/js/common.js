// microyum站点共通JS库
var currentDate = function (symbol) {
    var systemDate = new Date();

    return systemDate.getFullYear() + symbol + systemDate.getMonth() + 1 + symbol + systemDate.getDate();
}