'use strict'

function Operation(...args) {
    this.args = args;
}
Operation.prototype.toString = function() {
    return `${this.args.map((el) => el.toString()).join(' ')} ${this.sign}`;
}
Operation.prototype.evaluate = function(...v) {
    return this.impl(...this.args.map((el) => el.evaluate(...v)));
}

const operations = {};
function CreateOperation(impl, sign, diff) {
    const operation = function(...args) {
        Operation.call(this, ...args);
    }
    operation.prototype = Object.create(Operation.prototype);
    operation.prototype.impl = impl;
    operation.prototype.sign = sign;
    operation.prototype.count = impl.length;
    operation.prototype.diff = diff;
    operations[sign] = operation;
    return operation;
}

const Negate = CreateOperation(
    (v1) => -v1,
    'negate',
    function(v) { return new Negate(this.args[0].diff(v));}
);

const Add = CreateOperation(
    (v1, v2) => v1 + v2,
    '+',
    function(v) { return new Add(this.args[0].diff(v), this.args[1].diff(v));}
);
const Subtract = CreateOperation(
    (v1, v2) => v1 - v2,
    '-',
    function(v) { return new Subtract(this.args[0].diff(v), this.args[1].diff(v));}
);

const Multiply = CreateOperation(
    (v1, v2) => v1 * v2,
    '*',
    function(v) {
        return new Add(new Multiply(this.args[0].diff(v), this.args[1]), new Multiply(this.args[1].diff(v), this.args[0]));
    }
);

const Divide = CreateOperation(
    (v1, v2) => v1 / v2,
    '/',
    function(v) {
        return new Divide(
            new Subtract(
                new Multiply(this.args[0].diff(v), this.args[1]),
                new Multiply(this.args[1].diff(v), this.args[0])
            ),
            new Multiply(this.args[1], this.args[1])
        );
    }
);

const Hypot = CreateOperation(
    (v1, v2) => v1 * v1 + v2 * v2,
    'hypot',
    function(v) {
        return new Add(
            new Multiply(this.args[0], this.args[0]).diff(v),
            new Multiply(this.args[1], this.args[1]).diff(v)
        );
    }
);

const HMean = CreateOperation(
    (v1, v2) => 2 / (1 / v1 + 1 / v2),
    'hmean',
    function(v) {
        return new Divide(
            new Const(2),
            new Add(
                new Divide(Const.ONE, this.args[0]),
                new Divide(Const.ONE, this.args[1])
            )
        ).diff(v);
    }
);
function Const(value) {
    this.evaluate = function (_) {
        return value;
    }
    this.toString = function() {
        return value.toString();
    }
    this.diff = function(v) {
        return Const.ZERO;
    }
}

let variables = {
    'x': 0,
    'y': 1,
    'z': 2,
};

function Variable(name) {
    this.evaluate = function(...v) {
        return v[variables[name]];
    }
    this.toString = function() {
        return name;
    }
    this.diff = function(v) {
        return v === name ? Const.ONE : Const.ZERO;
    }
}

Const.ZERO = new Const(0);
Const.ONE = new Const(1);

let cnsts = {
    'pi' : new Const(Math.PI),
    'e' : new Const(Math.E)
}

function dump(obj) {
    for (const name in obj) {
        console.log(name + ': ' + obj[name]);
    }
}
function test() {
    let expr = parse('x negate');
    console.log(expr.diff('x').evaluate(0, 0, 0 ));
}

function parse(expression) {
    let tokens = expression.trim().split(/\s+/)[Symbol.iterator]();
    let stack = [];
    let el;
    while (!(el = tokens.next()).done) {
        if (el.value in operations) {
            let op = operations[el.value];
            stack.push(new op(...stack.splice(-op.prototype.count)));
        } else if (el.value in variables) {
            stack.push(new Variable(el.value));
        } else if (el.value in cnsts) {
            stack.push(cnsts[el.value]);
        } else if (!isNaN(Number(el.value))) {
            stack.push(new Const(Number(el.value)));
        } else {
            throw 'Unexpected token ' + el.value;
        }
    }
    return stack.pop()
}
