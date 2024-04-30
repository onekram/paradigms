(load-file "proto.clj")
(load-file "functional.clj")
(declare ZERO ONE)

; :NOTE: нет смысла наследвоваться от AbstractOperation
(defclass Constant _ [arg]
          (public toString [] (str (__arg this)))
          (public evaluate [_] (__arg this))
          (public diff [_] ZERO))

(defclass Variable _ [var]
          (public toString [] (__var this))
          (public evaluate [arg] (arg (__var this)))
          (public diff [arg] (if (= arg (__var this)) ONE ZERO)))

(def ZERO (Constant 0))
(def ONE (Constant 1))
(def MINUS-ONE (Constant -1))

(declare Add Multiply)
(defclass BaseOperation _ [& args]
          (public toString [] (str "(" (_get-sign this) " " (clojure.string/join " " (map toString (__args this))) ")"))
          (public evaluate [arg] (apply (_impl this) (map #(evaluate % arg) (__args this))))
          (public diff [var] (apply Add (map-indexed
                                           (fn [index arg] (Multiply (_diff-impl this index) (diff arg var)))
                                           (__args this))))
          (private nth [n] (nth (__args this) n))
          (private count [] (count (__args this)))
          (private without-nth [n] (map-indexed #(if (= n %1) ONE %2) (__args this)))
          (abstract impl)
          (abstract get-sign)
          (abstract evaluate)
          (abstract diff-impl))

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
          (private diff-impl [index] MINUS-ONE))

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
