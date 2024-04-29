(defn get-op [f]
  (fn [& args]
    (fn [m]
      (apply f (map #(% m) args)))))

(defn constant [arg]
  (constantly arg))

(defn variable [arg]
  (fn [m]
    (m arg)))

(defn div
  ([x] (/ 1.0 x))
  ([x & args] (/ (double x) (apply * args))))
(defn arith-mean-impl [& args]
  (div (apply + args) (count args)))
(defn geom-mean-impl [& args]
  (Math/pow (abs (apply * args)) (div 1.0 (count args))))
(defn harm-mean-impl [& args]
  (double (div (count args) (apply + (map #(div 1 %) args)))))

(def arithMean (get-op arith-mean-impl))
(def geomMean (get-op geom-mean-impl))
(def harmMean (get-op harm-mean-impl))
(def add (get-op +))
(def subtract (get-op -))
(def divide (get-op div))
(def multiply (get-op *))
(def negate subtract)

(def func-map
  {'+         add
   '-         subtract
   '*         multiply
   '/         divide
   'negate    negate
   'arithMean arithMean
   'geomMean  geomMean
   'harmMean harmMean})

(defn parse-rec [funcs constant-func variable-func]
  (letfn [(rec [arg]
            (cond (number? arg) (constant-func arg)
                  (symbol? arg) (variable-func (str arg))
                  (list? arg) (apply (funcs (first arg)) (map rec (rest arg)))))]
    #(rec (read-string %))))

(def parseFunction
  (parse-rec func-map constant variable))

(load-file "proto.clj")
(deffields sign args evaluate-impl)
(defpublicmethods toString evaluate diff)
(defmethods diff-impl nth count without-nth)
(declare Multiply)
(declare Add)
(declare ZERO)
(declare ONE)
(def OperationProto
  {:toString (fn [this] (str "(" (__sign this) " " (clojure.string/join " " (map toString (__args this))) ")"))
   :evaluate (fn [this arg] (apply (__evaluate-impl this) (map #(evaluate % arg) (__args this))))
   :diff (fn [this var] (apply Add (map-indexed
                                     (fn [index arg] (Multiply (_diff-impl this index) (diff arg var)))
                                     (__args this))))
   :nth (fn [this n] (nth (__args this) n))
   :count (fn [this] (count (__args this)))
   :without-nth (fn [this n] (map-indexed #(if (= n %1) ONE %2) (__args this)))})

(def funcs {})
(defn CreateOperation [evaluate-impl sign diff-impl]
  (let [op-proto (assoc OperationProto
                   :evaluate-impl evaluate-impl
                   :sign sign
                   :diff-impl diff-impl)
        op-cons (fn [this & args] (assoc this :args args))
        op (constructor op-cons op-proto)]
    (do (def funcs (assoc funcs (symbol sign) op))
        op)))
(def _value (field :value))
(def ConstantProto
  {:toString (fn [this] (str (_value this)))
   :evaluate (fn [this _] (_value this))})

(def VariableProto
  {:toString (fn [this] (_value this))
   :evaluate (fn [this arg] (arg (_value this)))})

(defn ConstantCons [this value]
  (assoc this
    :value value
    :diff (fn [this _] ZERO)))

(defn VariableCons [this value]
  (assoc this :value value
              :diff (fn [this arg] (if (= arg (_value this)) ONE ZERO))))

(def Constant (constructor ConstantCons ConstantProto))
(def Variable (constructor VariableCons VariableProto))
(def ZERO (Constant 0))
(def ONE (Constant 1))
(def MINUS-ONE (Constant -1))
(def Add
  (CreateOperation
    +
    "+"
    (fn [this index] ONE)))
(def Subtract
  (CreateOperation
    -
    "-"
    (fn [this index] (if (= (_count this) 1) MINUS-ONE (if (= index 0) ONE MINUS-ONE)))))
(def Multiply
  (CreateOperation
    *
    "*"
    (fn [this index] (apply Multiply (_without-nth this index)))))
(def Negate
  (CreateOperation
    (fn [arg] (- arg))
    "negate"
    (fn [this index] MINUS-ONE)))
(def Divide
  (CreateOperation
    div
    "/"
    (fn [this index] (if (= (_count this) 1) (Divide MINUS-ONE (Multiply (first (__args this)) (first (__args this))))
      (let [den (apply Multiply (rest (__args this)))]
        (if (= index 0) (Divide den)
                        (Divide (Negate (first (__args this)))
                                (Multiply den (_nth this index)))))))))

(def ArithMean
  (CreateOperation
    arith-mean-impl
    "arithMean"
    (fn [this index] (Divide (Constant (_count this))))))
(def GeomMean
  (CreateOperation
    geom-mean-impl
    "geomMean"
    (fn [this index] (Divide
                       (apply GeomMean (__args this))
                       (Multiply (Constant (_count this)) (_nth this index))))))
(def HarmMean
  (CreateOperation
    harm-mean-impl
    "harmMean"
    (fn [this index] (let [sum-inverses (apply Add (map Divide (__args this)))
                           current (Multiply (_nth this index) (_nth this index))]
                     (Divide
                       (Constant (_count this))
                       (Multiply sum-inverses sum-inverses current))))))

(def parseObject
  (parse-rec funcs Constant Variable))

(def expr (Divide (Multiply (Constant 2) (Variable "x")) (Negate (Multiply (Constant 5) (Variable "y"))) (Multiply (Constant 45) (Variable "z"))))
(def expr1 (diff expr "x"))

(def expr2 (GeomMean (Variable "x") (Constant 10)))
(def expr3 (diff expr2 "x"))

(println (toString expr2) "expr2")
(println (toString expr3) "expr3")

;(println (evaluate expr1 {"x" 1 "y" 1 "z" 1}))
