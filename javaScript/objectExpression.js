"use strict"


/*function Exception (message) {this.message = message}

Exception.prototype = Object.create(Error.prototype);
Exception.prototype.name = "Exception";

function exceptionFactory(name, make) {
    let res = function (...args) {
        this.message = make(...args);
    }
    res.prototype = Object.create(Error.prototype);
    res.prototype.constructor = Exception;
    return res;
}

let OddClosingBracketException = exceptionFactory(
    "OddClosingBracketException",
    (expr, ind) => "Odd closing bracket at position: " + ind
);

let MissingOperationException = exceptionFactory(
    "MissingOperationException",
    (expr, ind) => "Missed operation at position: " + ind
);

let MissingOperationBracketException = exceptionFactory(
    "MissingOperationBracketException",
    (expr, ind) => "Missed operation bracket at position: " + ind
);

let WrongOperandsNumberException = exceptionFactory(
    "WrongOperandsNumberException",
    (expr, ind) => "Wrong number of arguments at position: " + ind
);

let EmptyExpressionException = exceptionFactory(
    "EmptyExpressionException",
    (expr, ind) => "Empty expresion: " + ind
);

let UnknownIdentifierException = exceptionFactory(
    "UnknownIdentifierException",
    (expr, ind) => "Unknown identifier at position " + ind
);

let OddSuffixException = exceptionFactory(
    "OddSuffixException",
    (expr, ind) => "Odd suffix started at position " + ind
);*/

let primitive = {
    simplify: function () {
        return this
    }
};

function Const(x) {
    this.value = x;
}

Const.prototype = Object.create(primitive);
Const.prototype.toString = function () {
    return this.value.toString();
}
Const.prototype.prefix = Const.prototype.toString;
Const.prototype.postfix = Const.prototype.toString;
Const.prototype.evaluate = function () {
    return this.value;
}
const ZERO = new Const(0);
const ONE = new Const(1);
Const.prototype.diff = () => ZERO;


function isNum(value, num) {
    return value instanceof Const && value.value === num;
}

function Variable(name) {
    this.name = name;
}

const VARS = {"x": 0, "y": 1, "z": 2};

Variable.prototype = Object.create(primitive);
Variable.prototype.toString = function () {
    return this.name;
};
Variable.prototype.prefix = Variable.prototype.toString;
Variable.prototype.postfix = Variable.prototype.toString;
Variable.prototype.evaluate = function (...args) {
    const id = VARS[this.name];
    return args[id];
};
Variable.prototype.diff = function (name) {
    if (this.name === name) {
        return ONE;
    } else {
        return ZERO;
    }
};
const VAR = {};
for (let v in VARS) {
    VAR[v] = new Variable(v);
}

function Operation(...args) {
    this.operands = args;
}

Operation.prototype.toString = function () {
    return this.operands.join(" ") + " " + this.symbol;
}

Operation.prototype.prefix = function () {
    return "(" + this.symbol + " " + this.operands.map((value) => value.prefix()).join(" ") + ")";
}

Operation.prototype.postfix = function () {
    return "(" + this.operands.map(function (value) {
        return value.postfix()
    }).join(" ") + " " + this.symbol + ")";
}

Operation.prototype.evaluate = function (...args) {
    let res = this.operands.map((value) => value.evaluate.apply(value, args));
    return this._ops(...res);
}
Operation.prototype.diff = function (v) {
    let ops = this.operands;
    return this._diff.apply(this, ops.concat(ops.map(function (value) { return value.diff(v) })));

}
Operation.prototype.simplify = function () {
    let sOps = this.operands.map((value) => value.simplify());
    let allConst = true;
    sOps.forEach(function (value) {
        if (!(value instanceof Const)) {
            allConst = false;
        }
    });
    let res = newOps(this.constructor, sOps);
    if (allConst) {
        return new Const(res.evaluate());
    }
    if (this._simpl !== undefined) {
        return this._simpl.apply(this, sOps);
    }
    return res;
}

function DefineOperation(constr, ops, symbol, diff, simpl) {
    this.constructor = constr;
    this.symbol = symbol;
    this._ops = ops;
    this._diff = diff;
    this._simpl = simpl;
}

DefineOperation.prototype = Operation.prototype;

function OperationFactory(ops, symbol, diff, simpl) {
    let res = function (...args) {
        Operation.apply(this, args);
    };
    res.prototype = new DefineOperation(res, ops, symbol, diff, simpl);
    return res;
}

let Add = OperationFactory((a, b) => a + b, "+", (a, b, da, db) => new Add(da, db),
    function (a, b) {
        if (isNum(a, 0)) {
            return b;
        }
        if (isNum(b, 0)) {
            return a;
        }
        return new Add(a, b);
    }
);
let Subtract = OperationFactory((a, b) => a - b, "-", (a, b, da, db) => new Subtract(da, db),
    function (a, b) {
        if (isNum(b, 0)) {
            return a;
        }
        return new Subtract(a, b);
    });
let Multiply = OperationFactory((a, b) => a * b, "*", (a, b, da, db) => new Add(new Multiply(da, b), new Multiply(a, db)),
    function (a, b) {
        if (isNum(a, 0) || isNum(b, 0)) {
            return ZERO;
        }
        if (isNum(a, 1)) {
            return b;
        }
        if (isNum(b, 1)) {
            return a;
        }
        return new Multiply(a, b);
    });

let Divide = OperationFactory((a, b) => a / b, "/", (a, b, da, db) => new Divide(new Subtract(new Multiply(da, b), new Multiply(a, db)), new Multiply(b, b)),
    function (a, b) {
        if (isNum(a, 0)) {
            return ZERO;
        }
        if (isNum(b, 1)) {
            return a;
        }
        return new Divide(a, b);
    });
let Negate = OperationFactory((a) => -a, "negate", (a, da) => new Negate(da))
let ArcTan = OperationFactory((a) => Math.atan(a), "atan", (a, da) => new Divide(da, new Add(ONE, new Multiply(a, a))))
let ArcTan2 = OperationFactory((a, b) => Math.atan2(a, b), "atan2",
    function (a, b, da, db) {
        return new Divide(new Subtract(new Multiply(da, b), new Multiply(db, a)), new Add(new Multiply(a, a), new Multiply(b, b)));
    });
let Sumexp = OperationFactory((...args) => args.map((value) => Math.exp(value)).reduce((a, b) => (a + b), 0), "sumexp",
    function (...args) {
        let ans = ZERO;
        for (let i = 0; i < args.length/2;i++) {
            ans = new Add(ans, new Multiply(new Sumexp(args[i]), args[i+args.length/2]));
        }
        return ans;
    });
let Softmax = OperationFactory((...args) => Math.exp(args[0]) / (args.map((value) => Math.exp(value)).reduce((a, b) => (a + b), 0)), "softmax", 
    function (...args) {
        let sumE = new Sumexp(...args.slice(0, args.length/2));
        let dsumE = ZERO;
        for (let i = 0; i < args.length/2;i++) {
            dsumE = new Add(dsumE, new Multiply(new Sumexp(args[i]), args[i+args.length/2]));
        }
        return new Divide(new Subtract(new Multiply(new Multiply(new Sumexp(args[0]), args[args.length/2]), sumE), new Multiply(dsumE, new Sumexp(args[0]))), new Multiply(sumE, sumE));

    });
let OPS = {
    "+": Add,
    "-": Subtract,
    "*": Multiply,
    "/": Divide,
    "negate": Negate,
    "atan": ArcTan,
    "atan2": ArcTan2,
    "sumexp": Sumexp,
    "softmax": Softmax
}

let ARGS = {
    "+": 2,
    "-": 2,
    "*": 2,
    "/": 2,
    "atan2": 2,
    "atan": 1,
    "negate": 1
};

let newOps = function (ops, args) {
    let res = Object.create(ops.prototype);
    ops.apply(res, args);
    return res;
}

let parse = (s) => {
    let tokens = s.split(" ").filter((t) => t.length > 0);
    let stack = [];
    tokens.forEach(token => {
        if (token in OPS) {
            let args = stack.splice(stack.length - ARGS[token]);
            stack.push(newOps(OPS[token], args));
        } else if (token in VAR) {
            stack.push(VAR[token]);
        } else {
            stack.push(new Const(Number(token)));
        }
    });
    return stack.pop();
};

function ParseException (message) {this.message = message}
ParseException.prototype = Object.create(Error.prototype);
ParseException.prototype.name = "ParseException";
ParseException.prototype.constructor = ParseException;

const error = (expected, found, ind, expression) => {
    let point = "";
    for (let i = 0; i < ind; i++) {
        point += " ";
    }
    point += "^";
    throw new ParseException("expected: " + expected + " , found: " + found + "\n" + expression + "\n" + point);
};

let ind = 0;
let expr1 = "";
let stack = [];
let index = [];
let skipWhites = () => {
    while (ind < expr1.length && /\s/.test(expr1.charAt(ind))) {
        ind++;
    }
}

let doOps = (mode) => {
    if (mode === 0) {
        let operands = [];
        while (stack[stack.length - 1] !== "(" && !(stack[stack.length - 1] in OPS)) {
            operands.push(stack.pop());
            index.pop();
        }
        if (stack[stack.length - 1] === "(") {
            error("operation", "bracket", ind, expr1);
        }
        let op = stack.pop();
        let brack = stack.pop();
        if (brack !== "(") {
            error("operation bracket", brack, ind, expr1);
        }
        if (ARGS[op] !== undefined && operands.length !== ARGS[op]) {
            error(ARGS[op] + " arguments", operands.length + " arguments", ind, expr1);
        }
        stack.push(newOps(OPS[op], operands.reverse()));
    } else {
        if (!(stack[stack.length - 1] in OPS)) {
            error("operation", stack[stack.length-1], ind-1, expr1);
        }
        let op = stack.pop();
        let oper = [];
        let tmp = stack.pop();
        while (tmp !== '(') {
            if (!checkOp(tmp)) {
                if (stack.length === 0) {
                    error("open bracket for this close", "no open bracket", ind, expr1);
                }
                error(ARGS[op] + " arguments", oper.length + " arguments", ind-1, expr1);
            }
            oper.push(tmp);
            tmp = stack.pop();
        }
        if (tmp !== '(') {
            error("opening bracket", "not found it", ind-1, expr1);
        }
        if (ARGS[op] !== undefined && oper.length !== ARGS[op]) {
            error(ARGS[op] + " arguments", oper.length + " arguments", ind-1, expr1);
        }
        stack.push(newOps(OPS[op], oper.reverse()));
    }
}
let checkOp = (a) => {
    return a instanceof Const || a instanceof Variable || a instanceof Operation;
}

let getNum = () => {
    let num = "";
    if (expr1.charAt(ind) === "-") {
        num += "-";
        ind++;
    }
    while (ind < expr1.length && /\d/.test(expr1.charAt(ind))) {
        num += expr1.charAt(ind++);
    }
    if (num !== "" && num !== "-") {
        return parseInt(num);
    }
    if (num === "-") {
        ind--;
    }
    return undefined;
}

let getOps = () => {
    let res = "";
    while (ind < expr1.length && /\w/.test(expr1.charAt(ind))) {
        res += expr1.charAt(ind++);
    }
    return res;
}

let parsePrefixPostfix = (s, mode) => {
    ind = 0;
    expr1 = s;
    stack = [];
    skipWhites();
    if (ind === expr1.length) {
        error("expression", "nothing", ind + 1, expr1);;
    }
    while (true) {
        skipWhites();
        if (ind >= expr1.length) {
            break;
        }
        if (expr1.charAt(ind) === ")") {
            doOps(mode);
            ind++;
            continue;
        }
        index.push(ind);
        if (expr1.charAt(ind) === "(") {
            stack.push("(");
            ind++;
            continue;
        }
        let curNum = getNum();
        if (curNum !== undefined) {
            stack.push(new Const(curNum));
            continue;
        }
        let curOp = undefined;
        let curId = "";
        if (expr1.charAt(ind) in OPS) {
            curOp = expr1.charAt(ind);
            ind++;
        } else {
            curId = getOps();
            if (curId in OPS) {
                curOp = curId;
            }
        }
        if (curOp !== undefined) {
            stack.push(curOp);
        } else if (curId in VARS) {
            stack.push(VAR[curId]);
        } else {
            error("identifier", "unknown identifier", ind-1, expr1);
        }
    }
    skipWhites();
    if (ind !== expr1.length) {
        error("end of expression", "odd suffix", ind, expr1);
    } else if (stack.length > 1) {
        error("no odd identifiers(s)", "odd identifier(s)", ind-1, expr1);
    }
    return stack.pop();
}

let parsePrefix = (s) => parsePrefixPostfix(s, 0);
let parsePostfix = (s) => parsePrefixPostfix(s, 1);

//console.log(parsePostfix("z (x y +) *)"))
