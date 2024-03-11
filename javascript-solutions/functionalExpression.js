'use strict'

function getOperation(apply) {
    let func =  (...v) => (x, y, z) => apply(...v.map((el) => el(x, y, z)));
    func.count = apply.length;
    return func;
}

let add = getOperation((a, b) => a + b);
let subtract = getOperation((a, b) => a - b);
let multiply = getOperation((a, b) => a * b);
let divide = getOperation((a, b) => a / b);
let negate = getOperation((a) => -a);


function cnst(value) {
    return (x, y, z) => value;
}

function variable(name) {
    return (x, y, z) => {
        switch (name) {
            case 'x':
                return x
            case 'y':
                return y
            case 'z':
                return z
            default:
                throw "Error: Unexpected variable name " + name
        }
    };
}
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
        console.log(expr(x, 0, 0));
    }
}

function parse(expression) {
    let operations = {
        '+': add,
        '-': subtract,
        '*': multiply,
        '/': divide,
        'negate': negate
    };
    let tokens = expression.trim().split(/\s+/)[Symbol.iterator]();
    let stack = [];
    let el;
    while (!(el = tokens.next()).done) {
        if (el.value in operations) {
            let func = operations[el.value]
            let count = func.count;
            let arr = [];
            for (let i = 0; i < count; i++) {
                arr.unshift(stack.pop())
            }
            stack.push(func(...arr))
        } else {
            if (!isNaN(Number(el.value))) {
                stack.push(cnst(Number(el.value)))
            } else {
                stack.push(variable(el.value))
            }
        }
    }
    return stack.pop()

}

// test();
console.log(parse("x x 2 - * x * 1 +")(5, 0, 0))
