goog.provide('noderepl');
goog.require('cljs.core');
goog.require('cljs.repl');
noderepl.pep = (function pep(text){
cljs.core.println.call(null,[cljs.core.str(cljs.repl.prompt.call(null)),cljs.core.str(text)].join(''));
cljs.repl.eval_print.call(null,text);
return cljs.core.println.call(null);
});
/**
* @param {...*} var_args
*/
noderepl._main = (function() { 
var _main__delegate = function (args){
cljs.repl.init.call(null);
cljs.core._STAR_out_STAR_ = (function (p1__3234_SHARP_){
return process.stdout.write(p1__3234_SHARP_);
});
cljs.core._STAR_rtn_STAR_ = (function (p1__3235_SHARP_){
return process.stdout.write(p1__3235_SHARP_);
});
cljs.core._STAR_err_STAR_ = (function (p1__3236_SHARP_){
return process.stderr.write(p1__3236_SHARP_);
});
cljs.core._STAR_print_fn_STAR_ = (function (p1__3237_SHARP_){
return cljs.core._STAR_out_STAR_.call(null,p1__3237_SHARP_);
});
cljs.core.println.call(null,";; ClojureScript");
cljs.core.println.call(null,";;   - http://github.com/kanaka/clojurescript");
cljs.core.println.call(null,";;   - A port of the ClojureScript compiler to ClojureScript");
noderepl.pep.call(null,"(+ 1 2)");
noderepl.pep.call(null,"(defn sqr [x] (* x x))");
noderepl.pep.call(null,"(sqr 8)");
noderepl.pep.call(null,"(defmacro unless [pred a b] `(if (not ~pred) ~a ~b))");
noderepl.pep.call(null,"(unless false :yep :nope)");
var readline = require("readline");
var rl = readline.createInterface(process.stdin,process.stdout);
rl.setPrompt(cljs.repl.prompt.call(null));
rl.prompt();
rl.on("line",(function (line){
if(cljs.core.seq.call(null,cljs.core.filter.call(null,(function (p1__3238_SHARP_){
return cljs.core.not_EQ_.call(null," ",p1__3238_SHARP_);
}),line)))
{cljs.repl.eval_print.call(null,line);
cljs.core.println.call(null);
} else
{}
rl.setPrompt(cljs.repl.prompt.call(null));
return rl.prompt();
}));
return rl.on("close",(function (){
return process.exit(0);
}));
};
var _main = function (var_args){
var args = null;
if (goog.isDef(var_args)) {
  args = cljs.core.array_seq(Array.prototype.slice.call(arguments, 0),0);
} 
return _main__delegate.call(this, args);
};
_main.cljs$lang$maxFixedArity = 0;
_main.cljs$lang$applyTo = (function (arglist__3239){
var args = cljs.core.seq(arglist__3239);;
return _main__delegate(args);
});
_main.cljs$lang$arity$variadic = _main__delegate;
return _main;
})()
;
cljs.core._STAR_main_cli_fn_STAR_ = noderepl._main;
