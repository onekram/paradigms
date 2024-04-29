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
