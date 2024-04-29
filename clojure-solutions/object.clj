(load-file "proto.clj")
(load-file "functional.clj")

(defclass AbstractOperation _ [& args]
          (public toString [] (_toString this))
          (public evaluate [arg] (_evaluate this arg))
          (public diff [arg] (_diff this arg))
          (abstract toString)
          (abstract evaluate)
          (abstract diff))

(declare ZERO ONE)
(defclass Constant AbstractOperation []
          (private toString [] (str (first (__args this))))
          (private evaluate [_] (first (__args this)))
          (private diff [_] ZERO))


(defclass Variable AbstractOperation []
          (private toString [] (first (__args this)))
          (private evaluate [arg] (arg (first (__args this))))
          (private diff [arg] (if (= arg (first (__args this))) ONE ZERO)))

(def ZERO (Constant 0))
(def ONE (Constant 1))
(def MINUS-ONE (Constant -1))

(declare Add Multiply)
(defclass BaseOperation AbstractOperation []
          (private toString [] (str "(" (_get-sign this) " " (clojure.string/join " " (map toString (__args this))) ")"))
          (private evaluate [arg] (apply (_impl this) (map #(_evaluate % arg) (__args this))))
          (private diff [var] (apply Add (map-indexed
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
