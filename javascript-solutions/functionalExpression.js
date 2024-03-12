'use strict'

function getOperation(apply, count = apply.length) {
    let func =  (...args) => (...v) => apply(...args.map((el) => el(...v)));
    func.count = count;
    return func;
}

function cnst(value) {
    return () => value;
}

function variable(name) {
    return (...v) => v[variables[name]];
}

let add = getOperation((a, b) => a + b);
let subtract = getOperation((a, b) => a - b);
let multiply = getOperation((a, b) => a * b);
let divide = getOperation((a, b) => a / b);
let negate = getOperation((a) => -a);
let max3 = getOperation(Math.max, 3);
let min5 = getOperation(Math.min, 5);


let pi = cnst(Math.PI)
let e = cnst(Math.E)


let cnsts = {
    'pi' : pi,
    'e' : e
}

let variables = {
    'x': 0,
    'y': 1,
    'z': 2,
};

let operations = {
    '+': add,
    '-': subtract,
    '*': multiply,
    '/': divide,
    'negate': negate,
    'min5': min5,
    'max3' : max3
};
function test() {
    let expr = add(
        subtract(
            multiply(
                variable('x'),
                variable('x')
            ),
            multiply(
                cnst(2),
                variable('x')
            )
        ),
        cnst(1)
    );

    for (let x= 0; x < 11; x++) {
        console.log(expr(x));
    }
}

function parse(expression) {
    let tokens = expression.trim().split(/\s+/)[Symbol.iterator]();
    let stack = [];
    let el;
    while (!(el = tokens.next()).done) {
        if (el.value in operations) {
            let func = operations[el.value];
            stack.push(func(...stack.splice(-func.count)));
        } else if (el.value in variables) {
            stack.push(variable(el.value));
        } else if (el.value in cnsts) {
            stack.push(cnsts[el.value]);
        } else if (!isNaN(Number(el.value))) {
            stack.push(cnst(Number(el.value)));
        } else {
            throw 'Unexpected token ' + el.value;
        }
    }
    return stack.pop()
}
// test()
