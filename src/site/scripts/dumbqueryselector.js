(function (global, factory) {
  if (typeof define === "function" && define.amd) {
    define("DumbQuerySelector", ["exports"], factory);
  } else if (typeof exports !== "undefined") {
    factory(exports);
  } else {
    var mod = {
      exports: {}
    };
    factory(mod.exports);
    global.DumbQuerySelector = mod.exports;
  }
})(this, function (exports) {
  "use strict";

  Object.defineProperty(exports, "__esModule", {
    value: true
  });
  exports.$ = $;
  exports.$$ = $$;

  function $(query) {
    var scope = arguments.length <= 1 || arguments[1] === undefined ? document : arguments[1];
    return scope.querySelector(query);
  }

  function $$(query) {
    var scope = arguments.length <= 1 || arguments[1] === undefined ? document : arguments[1];
    return Array.prototype.slice.call(scope.querySelectorAll(query));
  }
});
