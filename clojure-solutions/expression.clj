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
(deffields sign args impl)
(defmethod diff-impl)
(def toString (method :toString))
(def evaluate (method :evaluate))
(def diff (method :diff))
(def OperationProto
  {:toString (fn [this] (str "(" (__sign this) " " (clojure.string/join " " (map toString (__args this))) ")"))
   :evaluate (fn [this arg] (apply (__impl this) (map #(evaluate % arg) (__args this))))})

(def funcs {})
(defn CreateOperation [impl sign diff]
  (let [op-proto (assoc OperationProto
             :impl impl
             :sign sign
             :diff diff)
        op-cons (fn [this & args] (assoc this :args args))
        op (constructor op-cons op-proto)]
    (do (def funcs (assoc funcs (symbol sign) op))
        op)))

(declare ZERO)
(declare ONE)
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

(def Add
  (CreateOperation
           +
           "+"
           (fn [this arg] (apply Add (map #(diff % arg) (__args this)))))) ; :NOTE:  ; + diff на уровне Operation
(def Subtract
  (CreateOperation
    -
    "-"
    (fn [this arg] (apply Subtract (map #(diff % arg) (__args this))))))

(def Multiply
  (CreateOperation
    *
    "*"
    (fn [this arg]
      (cond
        (== (count (__args this)) 1) (diff (first (__args this)) arg)
        :else (Add (Multiply (diff (first (__args this)) arg) (apply Multiply (rest (__args this))))
                   (Multiply (first (__args this)) (diff (apply Multiply (rest (__args this))) arg)))))
      )
    )
(def Negate
  (CreateOperation
    (fn [arg] (- arg))
    "negate"
    (fn [this arg] (Negate (diff (first (__args this)) arg)))))
(def Divide
  (CreateOperation
    div
    "/"
    (fn [this arg]
      (cond (== (count (__args this)) 1) (Divide
                                          (Negate (diff (first (__args this)) arg))
                                          (Multiply (first (__args this)) (first (__args this))))
            :else (Divide
                    (Subtract
                      (Multiply (diff (first (__args this)) arg) (apply Multiply (rest (__args this))))
                      (Multiply (first (__args this)) (diff (apply Multiply (rest (__args this))) arg)))
                    (Multiply (apply Multiply (rest (__args this))) (apply Multiply (rest (__args this)))))))))

(def ArithMean
  (CreateOperation
    arith-mean-impl
    "arithMean"
    (fn [this arg] (diff
                     (Divide (apply Add (__args this)) (Constant (count (__args this))))
                     arg))))
(def GeomMean
  (CreateOperation
    geom-mean-impl
    "geomMean"
    (fn [this arg] (let [mult (apply Multiply (__args this))]
                     (Divide
                       (Multiply (diff mult arg) (apply GeomMean (__args this)))
                       (Multiply (Constant (count (__args this))) mult))))))
(def HarmMean
  (CreateOperation
    harm-mean-impl
    "harmMean"
    (fn [this arg] (let [sum-inverses (apply Add (map #(Divide ONE %) (__args this)))]
                     (Divide
                       (Negate (Multiply (diff sum-inverses arg) (Constant (count (__args this)))))
                       (Multiply sum-inverses sum-inverses))))))

(def parseObject
  (parse-rec funcs Constant Variable))

(def expr (Divide (Variable "x") (Variable "y") (Constant 2)))
(def expr1 (diff expr "x"))
(println (evaluate expr1 {"x" 1 "y" 100}))
