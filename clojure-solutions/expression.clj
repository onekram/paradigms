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

(def _sign (field :sign))
(def _args (field :args))
(def _impl (field :impl))
(def toString (method :toString))
(def evaluate (method :evaluate))
(def diff (method :diff))

(def OperationProto
  {:toString (fn [this] (str "(" (_sign this) " " (clojure.string/join " " (map toString (_args this))) ")"))
   :evaluate (fn [this arg] (apply (_impl this) (map #(evaluate % arg) (_args this))))})

;(defn OperationCons [this & args]
;  (assoc this
;    :args args))

(defn CreateOperation [impl sign diff]
  (let [op-proto (assoc OperationProto
             :impl impl
             :sign sign
             :diff diff)
        op-cons (fn [this & args] (assoc this :args args))]
    (constructor op-cons op-proto)))

(defn Constant [value]
  {:toString (fn [this] (str value)) ; :NOTE: отдельно добавить прототип
    :evaluate (fn [this _] value)
    :diff (fn [this _] (Constant 0))}) ; :NOTE: (Constant 0) -- constants

(defn Variable [name]
   {:toString (fn [this] name) ; :NOTE: досттавать name из прототипвп
    :evaluate (fn [this arg] (arg name))
    :diff (fn [this arg] (if (= arg name) (Constant 1) (Constant 0)))})

(def Add
  (CreateOperation
           +
           "+"
           (fn [this arg] (apply Add (map #(diff % arg) (_args this)))))) ; :NOTE:  (_args this) + diff на уровне Operation
(def Subtract
  (CreateOperation
    -
    "-"
    (fn [this arg] (apply Subtract (map #(diff % arg) (_args this))))))

(def Multiply
  (CreateOperation
    *
    "*"
    (fn [this arg]
      (cond
        (== (count (_args this)) 1) (diff (first (_args this)) arg)
        :else (Add (Multiply (diff (first (_args this)) arg) (apply Multiply (rest (_args this))))
                   (Multiply (first (_args this)) (diff (apply Multiply (rest (_args this))) arg)))))
      )
    )
(def Negate
  (CreateOperation
    (fn [arg] (- arg))
    "negate"
    (fn [this arg] (Negate (diff (first (_args this)) arg)))))
(def Divide
  (CreateOperation
    div
    "/"
    (fn [this arg]
      (cond (== (count (_args this)) 1) (Divide
                                          (Negate (diff (first (_args this)) arg))
                                          (Multiply (first (_args this)) (first (_args this))))
            :else (Divide
                    (Subtract
                      (Multiply (diff (first (_args this)) arg) (apply Multiply (rest (_args this))))
                      (Multiply (first (_args this)) (diff (apply Multiply (rest (_args this))) arg)))
                    (Multiply (apply Multiply (rest (_args this))) (apply Multiply (rest (_args this)))))
            )
      )))

(def ArithMean
  (CreateOperation
    arith-mean-impl
    "arithMean"
    (fn [this arg] (diff
                     (Divide (apply Add (_args this)) (Constant (count (_args this))))
                     arg))))
(def GeomMean
  (CreateOperation
    geom-mean-impl
    "geomMean"
    (fn [this arg] (let [mult (apply Multiply (_args this))]
                     (Divide
                       (Multiply (diff mult arg) (apply GeomMean (_args this)))
                       (Multiply (Constant (count (_args this))) mult))))))
(def HarmMean
  (CreateOperation
    harm-mean-impl
    "harmMean"
    (fn [this arg] (let [sum-inverses (apply Add (map #(Divide (Constant 1) %) (_args this)))]
                     (Divide
                       (Negate (Multiply (diff sum-inverses arg) (Constant (count (_args this)))))
                       (Multiply sum-inverses sum-inverses))))))

(def obj-map
  {'+         Add
   '-         Subtract
   '*         Multiply
   '/         Divide
   'negate    Negate
   'arithMean ArithMean
   'geomMean GeomMean
   'harmMean HarmMean
   })
(def parseObject
  (parse-rec obj-map Constant Variable))

(def expr (Divide (Variable "x") (Variable "y") (Constant 2)))
(def expr1 (diff expr "x"))
(println (evaluate expr1 {"x" 1 "y" 100}))
