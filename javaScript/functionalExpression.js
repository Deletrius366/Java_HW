"use strict";

function operation(f) {
    return (...args) => (...values) => f(...args.map(arg => arg(...values)))
}

let cnst = a => () => a;
let VARS = ["x", "y", "z"];
let variable = function(name) {
    const index = VARS.indexOf(name);
    return (...args) => args[index];
}
const VAR = {};
for (const v of VARS) {
    VAR[v] = variable(v);
}
let add = operation((a, b) => a + b);
let subtract = operation((a, b) => a - b);
let multiply = operation((a, b) => a * b);
let divide = operation((a, b) => a / b);
let negate = operation(a => -a);
let avg5 = operation((...args) => args.reduce((a, b) => a + b) / args.length);
let med3 = operation((...args) => args.sort((a, b) => a - b)[Math.floor(args.length / 2)]);
const pi = cnst(Math.PI);
const e = cnst(Math.E);
let CONST = {
    "pi": pi,
    "e": e
};

let OPS = {
    "+": add,
    "-": subtract,
    "*": multiply,
    "/": divide,
    "negate": negate,
    "avg5": avg5,
    "med3": med3
};

let ARGS = {
    "+": 2,
    "-": 2,
    "*": 2,
    "/": 2,
    "negate": 1,
    "avg5": 5,
    "med3": 3
};
function parse(s) {
    let tokens = s.split(" ").filter((t) => t.length > 0);
    let stack = [];
    tokens.forEach(token => {
        if (token in OPS) {
            let args = stack.splice(-ARGS[token]);
            stack.push(OPS[token](...args));
        } else if (token in VAR) {
            stack.push(VAR[token]);
        } else if (token in CONST) {
            stack.push(CONST[token]);
        } else {
            stack.push(cnst(Number(token)));
        }
    });
    return stack.pop();
};
