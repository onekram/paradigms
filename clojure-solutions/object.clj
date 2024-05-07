(load-file "proto.clj")
(load-file "functional.clj")
(declare ZERO ONE)

(defclass Constant _ [arg]
          (public toString [] (str (__arg this)))
          (public toStringPostfix [] (str (__arg this)))
          (public toStringInfix [] (str (__arg this)))
          (public evaluate [_] (__arg this))
          (public diff [_] ZERO))

(defclass Variable _ [var]
          (public toString [] (__var this))
          (public toStringPostfix [] (__var this))
          (public toStringInfix [] (__var this))
          (public evaluate [arg] (arg ((comp clojure.string/lower-case first) (__var this))))
          (public diff [arg] (if (= arg (__var this)) ONE ZERO)))

(def ZERO (Constant 0))
(def ONE (Constant 1))
(def MINUS-ONE (Constant -1))

(declare Add Multiply)
(defclass BaseOperation _ [& args]
          (public toString []
                  (str "("
                       (_get-sign this) " "
                       (clojure.string/join " " (map toString (__args this)))
                       ")"))
          (public toStringPostfix []
                  (str "("
                       (clojure.string/join " " (map toStringPostfix (__args this))) " "
                       (_get-sign this)
                       ")"))
          (public toStringInfix []
                  (let [[l r] (__args this)] (if (nil? r) (if (_is-postfix this)
                                                            (str "(" (toStringInfix l) " " (_get-sign this) ")")
                                                            (str (_get-sign this) " " (toStringInfix l) ))
                                               (str "(" (toStringInfix l) " " (_get-sign this) " " (toStringInfix r) ")"))))
          (public evaluate [arg] (apply (_impl this) (map #(evaluate % arg) (__args this))))
          (public diff [var] (apply Add (map-indexed
                                           (fn [index arg] (Multiply (_diff-impl this index) (diff arg var)))
                                           (__args this))))
          (private nth [n] (nth (__args this) n))
          (private count [] (count (__args this)))
          (private without-nth [n] (map-indexed #(if (= n %1) ONE %2) (__args this)))
          (abstract impl)
          (abstract get-sign)
          (abstract diff-impl)
          (abstract is-postfix))

(defclass Add BaseOperation []
          (private impl [] +)
          (private get-sign [] "+")
          (private diff-impl [index] ONE))

(defclass Subtract BaseOperation []
          (private impl [] -)
          (private get-sign [] "-")
          (private diff-impl [index] (if (= (_count this) 1) MINUS-ONE (if (= index 0) ONE MINUS-ONE))))

(defclass Multiply BaseOperation []
          (private impl [] *)
          (private get-sign [] "*")
          (private diff-impl [index] (apply Multiply (_without-nth this index))))

(defclass Negate BaseOperation []
          (private impl [] -)
          (private get-sign [] "negate")
          (private diff-impl [index] MINUS-ONE)
          (private is-postfix [] false))

(defclass Divide BaseOperation []
          (private impl [] div)
          (private get-sign [] "/")
          (private diff-impl [index]
                   (if (= (_count this) 1)
                     (Divide MINUS-ONE (Multiply (first (__args this)) (first (__args this))))
                     (let [den (apply Multiply (rest (__args this)))]
                       (if (= index 0) (Divide den)
                                       (Divide (Negate (first (__args this)))
                                               (Multiply den (_nth this index))))))))

(defclass ArithMean BaseOperation []
          (private impl [] arith-mean-impl)
          (private get-sign [] "arithMean")
          (private diff-impl [index] (Divide (Constant (_count this)))))

(defclass GeomMean BaseOperation []
          (private impl [] geom-mean-impl)
          (private get-sign [] "geomMean")
          (private diff-impl [index] (Divide
                                       (apply GeomMean (__args this))
                                       (Multiply (Constant (_count this)) (_nth this index)))))

(defclass HarmMean BaseOperation []
          (private impl [] harm-mean-impl)
          (private get-sign [] "harmMean")
          (private diff-impl [index] (let [sum-inverses (apply Add (map Divide (__args this)))
                                           current (Multiply (_nth this index) (_nth this index))]
                                       (Divide
                                         (Constant (_count this))
                                         (Multiply sum-inverses sum-inverses current)))))

(defn sin [& args]
  (Math/sin (first args)))
(defn cos [& args]
  (Math/cos (first args)))
(defclass Sin BaseOperation []
          (private impl [] sin)
          (private get-sign [] "sin")
          (private is-postfix [] false))
(defclass Cos BaseOperation []
          (private impl [] cos)
          (private get-sign [] "cos")
          (private is-postfix [] false))

(defn sinc [& args]
  (let [[b a] args
        s (Math/sin a)
        ch (Math/cosh b)]
    (* s ch)))
(defn cosc [& args]
  (let [[b a] args
        c (Math/cos a)
        ch (Math/cosh b)]
    (* c ch)))

(defn sinp [& args] 0)
(defn cosp [& args]
  (let [a (first args)]
    (Math/cosh a)))
(defclass SinC BaseOperation []
          (private impl [] sinc)
          (private get-sign [] "sinc"))
(defclass CosC BaseOperation []
          (private impl [] cosc)
          (private get-sign [] "cosc"))
(defclass SinP BaseOperation []
          (private impl [] sinp)
          (private get-sign [] "sinp")
          (private is-postfix [] true))
(defclass CosP BaseOperation []
          (private impl [] cosp)
          (private get-sign [] "cosp")
          (private is-postfix [] true))
