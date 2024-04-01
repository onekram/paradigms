(defn v? [v]
  (and (vector? v) (or (empty? v) (every? number? v))))
(defn m? [m]
  (and (vector? m) (not (empty? m)) (every? v? m) (apply = (mapv count m))))

(defn same-size? [& args]
  {:pre [every? vector? args]}
  (apply (fn test [& args] (or (some number? args)
                               (some empty? args)
                               (and
                                 (apply = (mapv count args))
                                 (every? true? (apply mapv test args))))) args))

(defn get-op-of [cond?]
  (fn [f]
     (fn [& args]
       {:pre [(and (every? cond? args) (apply same-size? args))]}
       (apply (fn inner [& args] (if
                                   (every? number? args)
                                   (apply f args)
                                   (apply mapv inner args))) args))))
(defn cross2 [v1 v2]
  (vector
    (- (* (v1 1) (v2 2)) (* (v1 2) (v2 1)))
    (- (* (v1 2) (v2 0)) (* (v1 0) (v2 2)))
    (- (* (v1 0) (v2 1)) (* (v1 1) (v2 0)))
    ))
(def v (get-op-of v?))
(def v+ (v +))
(def v- (v -))
(def v* (v *))
(def vd (v /))
(defn v*s [v & s]
  {:pre [(and (v? v) (every? number? s))]}
  (mapv #(* % (apply * s)) v))
(defn vect [& args]
  {:pre [(and (every? v? args) (every? #(== 3 (count %)) args))]}
  (reduce
    (fn [v1 v2] (vector
                  (- (* (v1 1) (v2 2)) (* (v1 2) (v2 1)))
                  (- (* (v1 2) (v2 0)) (* (v1 0) (v2 2)))
                  (- (* (v1 0) (v2 1)) (* (v1 1) (v2 0)))
                           )) args))
(defn scalar [& args]
  {:pre [(and (every? v? args) (apply same-size? args))]}
  (apply + (apply v* args)))
(def m (get-op-of m?))
(def m+ (m +))
(def m- (m -))
(def m* (m *))
(def md (m /))
(defn m*s [m & s]
  {:pre [(and (m? m) (every? number? s))]}
  (mapv #(v*s % (apply * s)) m))
(defn m*v [m v]
  {:pre [(and (m? m) (v? v))]}
  (mapv #(scalar % v) m))
(defn transpose [m]
  {:pre [(m? m)]}
  (apply mapv vector m))
(defn m*m [& args]
  {:pre [(every? m? args)]}
  (reduce (fn [m1 m2]
            (let [btr (transpose m2)]
              (mapv #(m*v btr %) m1))) args))