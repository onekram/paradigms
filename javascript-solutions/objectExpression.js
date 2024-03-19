'use strict'

function Operation(...args) {
    this.args = args;
}
Operation.prototype.toString = function() {
    return `${this.args.map((el) => el.toString()).join(' ')} ${this.sign}`;
}
Operation.prototype.evaluate = function(...v) {
    return this.evaluateImpl(...this.args.map((el) => el.evaluate(...v)));
}

function Negate(arg) {
    Operation.call(this, arg);
    this.sign = 'negate';
    this.evaluateImpl = function(v) {
        return -v;
    }
    this.diff = function(v) {
        return new Negate(arg.diff(v));
    }
}
Negate.prototype = Object.create(Operation.prototype);

function Add(left, right) {
    Operation.call(this, left, right);
    this.sign = '+';
    this.evaluateImpl = function(v1, v2) {
        return v1 + v2;
    }
    this.diff = function(v) {
        return new Add(left.diff(v), right.diff(v));
    }
}
Add.prototype = Object.create(Operation.prototype);

function Subtract(left, right) {
    Operation.call(this, left, right);
    this.sign = '-';
    this.evaluateImpl = function(v1, v2) {
        return v1 - v2;
    }
    this.diff = function(v) {
        return new Subtract(left.diff(v), right.diff(v));
    }
}
Subtract.prototype = Object.create(Operation.prototype);

function Multiply(left, right) {
    Operation.call(this, left, right);
    this.sign = '*';
    this.evaluateImpl = function(v1, v2) {
        return v1 * v2;
    }
    this.diff = function(v) {
        return new Add(new Multiply(left.diff(v), right), new Multiply(right.diff(v), left));
    }
}
Multiply.prototype = Object.create(Operation.prototype);

function Divide(left, right) {
    Operation.call(this, left, right);
    this.sign = '/';
    this.evaluateImpl = function(v1, v2) {
        return v1 / v2;
    }
    this.diff = function(v) {
        return new Divide(
            new Subtract(
                new Multiply(left.diff(v), right),
                new Multiply(right.diff(v), left)
            ),
            new Multiply(right, right)
        );
    }
}
Divide.prototype = Object.create(Operation.prototype);

function Hypot(left, right) {
    Operation.call(this, left, right);
    this.sign = 'hypot';
    this.evaluateImpl = function(v1, v2) {
        return v1 * v1 + v2 * v2;
    }
    this.diff = function(v) {
        return new Add(
            new Multiply(left, left).diff(v),
            new Multiply(right, right).diff(v)
        );
    }
}

Hypot.prototype = Object.create(Operation.prototype);

function HMean(left, right) {
    Operation.call(this, left, right);
    this.sign = 'hmean';
    this.evaluateImpl = function(v1, v2) {
        return 2 / (1 / v1 + 1 / v2);
    }

    this.diff = function(v) {
        return new Divide(
            new Const(2),
            new Add(
                new Divide(new Const(1), left),
                new Divide(new Const(1), right)
            )
        ).diff(v);
    }
}

HMean.prototype = Object.create(Operation.prototype);

function Const(value) {
    this.evaluate = function (_) {
        return value;
    }
    this.toString = function() {
        return value.toString();
    }
    this.diff = function(v) {
        return new Const(0);
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
        return v === name ? new Const(1) : new Const(0);
    }
}

let cnsts = {
    'pi' : new Const(Math.PI),
    'e' : new Const(Math.E)
}

let operations = {
    '+': Add,
    '-': Subtract,
    '*': Multiply,
    '/': Divide,
    'negate': Negate,
    'hypot' : Hypot,
    'hmean' : HMean
};
function dump(obj) {
    for (const name in obj) {
        console.log(name + ': ' + obj[name]);
    }
}
function test() {
    let expr = new HMean(new Const(2), new Const(3)).diff('x');
    console.log(expr.toString());
    console.log(expr.evaluate(2, 2, 2));
}

function parse(expression) {
    let tokens = expression.trim().split(/\s+/)[Symbol.iterator]();
    let stack = [];
    let el;
    while (!(el = tokens.next()).done) {
        if (el.value in operations) {
            let op = operations[el.value];
            stack.push(new op(...stack.splice(-op.length)));
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
