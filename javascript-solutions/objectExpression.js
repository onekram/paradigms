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

Operation.prototype.prefix = function() {
    return `(${this.sign} ${this.args.map((el) => el.prefix()).join(' ')})`;
}

Operation.prototype.postfix = function() {
    return `(${this.args.map((el) => el.postfix()).join(' ')} ${this.sign})`;
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

const ArithMean = CreateOperation(
    (...args) => (args.reduce((init, arg) => init + arg, 0)) / args.length,
    'arithMean',
    function(v) {
        return new Divide(
            this.args.reduce((init, arg) => new Add(init, arg)),
            new Const(this.args.length)
        ).diff(v);
    }
);

const GeomMean = CreateOperation(
    (...args) => {
        return Math.pow(Math.abs(args.reduce((init, arg) => init * arg, 1)), 1/args.length);
    },
    'geomMean',
    function(v) {
        let multiply = this.args.reduce((init, arg) => new Multiply(init, arg));
        return new Divide(
            new Multiply(
                multiply.diff(v),
                new GeomMean(...this.args)
            ),
            new Multiply(
                new Const(this.args.length),
                multiply
            )
        );
    }
);

const HarmMean = CreateOperation(
    (...args) => args.length / (args.reduce((init, arg) => init + 1 / arg, 0)),
    'harmMean',
    function(v) {
        let sumInverse = this.args.reduce(
            (init, arg) => new Add(
                init,
                new Divide(new Const(1), arg)
            ),
            new Const(0)
        );
        return new Negate(
            new Divide(
                new Multiply(
                    sumInverse.diff(v),
                    new Const(this.args.length)
                ),
                new Multiply(
                    sumInverse,
                    sumInverse
                )
            )
        );
    }
);
function Const(value) {
    this.evaluate = function (_) {
        return value;
    }
    this.toString = function() {
        return value.toString();
    }
    this.prefix = this.toString;

    this.postfix = this.toString;

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

    this.prefix = this.toString;

    this.postfix = this.toString;

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
class ParsingError extends Error {
    constructor(message) {
        super(message);
        this.name = 'ParsingError';
    }
}
class UnknownTokenError extends ParsingError {
    constructor(token, pos) {
        super(`Unknown token '${token}', at the position ${pos}`);
    }
}

class UnexpectedTokenError extends ParsingError {
    constructor(unexpected, pos, expected) {
        super(
            `Unexpected '${unexpected}'` +
            (pos ? ` at the position ${pos}` : '') +
            (expected ? ` (Expected '${expected}')` : '')
        );
    }
}
class Parser {
    #tokens = [];
    #index = 0;
    #open = '(';
    #close = ')';
    #prefix = true;

    constructor(expression, prefix = true) {
        this.#prefix = prefix;
        this.#tokens = prefix ? this.#makeTokens(expression) : this.#makeTokens(expression).reverse();
        if (!prefix) {
            [this.#open, this.#close] = [this.#close, this.#open];
        }
    }

    #makeTokens(expression) {
        return expression.trim().split(/\s+|(?=[()])|(?<=[()])/);
    }

    parse() {
        let ex = this.#factor();
        if (!this.#isEnd()) {
            throw new UnexpectedTokenError(this.#cur(), this.#realIndex());
        }
        return ex;
    }

    #isEnd() {
        return this.#index >= this.#tokens.length;
    }
    #next() {
        return this.#tokens[this.#index++];
    }

    #cur() {
        return this.#isEnd() ? 'end of expression' : this.#tokens[this.#index];
    }

    #realIndex() {
        return this.#prefix ? this.#index : this.#tokens.length - this.#index + 1;
    }
    #generateOp(op, count) {
        let cond;
        let i = 0;
        let args = [];
        if (count === 0) {
            cond = () => this.#cur() !== this.#close && !this.#isEnd();
        } else {
            cond = () => i++ < count;
        }
        while (cond()){
            args.push(this.#factor());
        }
        if (!this.#prefix) {
            return new op(...args.reverse());
        }
        return new op(...args);
    }

    #expression() {
        let tk = this.#next();
        let op = operations[tk];
        if (op === undefined) {
            throw new UnknownTokenError(tk, this.#realIndex());
        }
        return this.#generateOp(op, op.prototype.count);
    }
    #factor() {
        if(this.#cur() === this.#open) {
            this.#next();
            let result = this.#expression();
            if (this.#cur() !== this.#close) {
                throw new UnexpectedTokenError(this.#cur(), this.#realIndex(), this.#close);
            }
            this.#next();
            return result;
        }
        if (this.#isEnd()) {
            throw new UnexpectedTokenError('end of expression');
        }
        let tk = this.#next();
        if (tk in variables) {
            return new Variable(tk);
        } else if (tk in cnsts) {
            return cnsts[tk];
        } else if (tk.length > 0 && !isNaN(Number(tk))) {
            return new Const(Number(tk));
        } else {
            throw new UnexpectedTokenError(tk, this.#realIndex(), "const or var");
        }
    }
}

function parsePrefix(expression) {
    return new Parser(expression, true).parse();
}

function parsePostfix(expression) {
    return new Parser(expression, false).parse();
}

function test() {
    try {
        let expr1 = parsePrefix('( / (negate x) 2)');
        console.log(expr1.postfix());
    } catch (ex) {
        console.log(ex.name);
        console.log(ex.message);
        console.log(ex);
    }
}
test()
