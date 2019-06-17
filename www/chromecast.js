const cordova = require('cordova')

function ChromecastPlugin () {}

ChromecastPlugin.prototype.echo = function (msg, callback) {
  cordova.exec(callback, function (err) {
    callback(err)
  }, 'ChromecastPlugin', 'echo', [msg])
}

ChromecastPlugin.prototype.castBtnClick = function (callback) {
  cordova.exec(callback, function (err) {
    callback(err)
  }, 'ChromecastPlugin', 'castBtnClick', [])
}

module.exports = new ChromecastPlugin()
