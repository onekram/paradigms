(defn v? [v]
  {:pre [true]
   :post [(boolean? %)]}
  (and (vector? v) (or (empty? v) (every? number? v))))
(defn m? [m]
  {:pre [true]
   :post [(boolean? %)]}
  (and (vector? m) (not (empty? m)) (every? v? m) (apply = (mapv count m))))
(defn same-size? [arg]
  {:pre [(every? #(or (vector? %) (number? %))  arg)]
   :post [(boolean? %)]}
  (and (apply (fn test [& args] (cond (every? number? args) true
                                      (every? vector? args) (and (apply = (mapv count args))
                                                                 (every? true? (apply mapv test args)))
                                      :else false)) arg)
  ((fn test [args] (cond (every? number? args) true
                          (every? vector? args) (and (apply = (mapv count args))
                                                     (every? true? (mapv test args)))
                          :else false)) arg)))
(defn b? [b]
  {:pre [(or (number? b) (vector? b))]
   :post [(boolean? %)]}
  (if (number? b)
    true
    (same-size? b)))
(defn size-b [b]
  {:pre [(b? b)]
   :post [(vector? %)]}
  (loop [res []
         cur b]
    (cond (number? cur) res
          (empty? cur) (conj res 0)
          :else (recur (conj res (count cur)) (cur 0)))))
(defn subvec? [v1 v2]
  {:pre [(and (vector? v1) (vector? v2))]
   :post [(boolean? %)]}
  (let [size (- (count v2) (count v1))]
    (cond (< size 0) false
    :else (= v1 (subvec v2 size)))))
(defn broad-cast [b to]
  {:pre [(and (b? b) (vector? to))]
   :post [(b? %)]}
  (loop [index (- (count to) (count (size-b b)) 1)
         cur b]
    (cond (== -1 index) cur
          :else (recur (dec index) (into [] (repeat (to index) cur))))))
(defn b [f]
  (fn [& args]
    {:pre [(and (every? b? args)
                (every? #(subvec? % (apply max-key count (mapv size-b args))) (mapv size-b args)))]
     :post [(b? %)]}
    (let [mx (apply max-key count (mapv size-b args))
          new-b (mapv #(broad-cast % mx) args)]
      (apply (fn inner [& args] (if (every? number? args)
                                                       (apply f args)
                                                       (apply mapv inner args))) new-b))))
(def b+ (b +))
(def b- (b -))
(def b* (b *))
(def bd (b /))
(defn get-op-of [cond?]
  (fn [f]
    (fn [& args]
      {:pre [(and (every? cond? args) (same-size? args))]
       :post [(cond? %)]}
      (apply (fn inner [& args] (if (every? number? args)
                                  (apply f args)
                                  (apply mapv inner args))) args))))
(def v (get-op-of v?))
(def v+ (v +))
(def v- (v -))
(def v* (v *))
(def vd (v /))
(defn v*s [v & s]
  {:pre [(and (v? v) (every? number? s))]
   :post [(v? %)]}
  (mapv #(* % (apply * s)) v))
(defn vect [& args]
  {:pre [(and (every? v? args) (every? #(== 3 (count %)) args))]
   :post [(and (v? %) (== 3 (count %)))]}
  (reduce
    (fn [v1 v2] (vector
                  (- (* (v1 1) (v2 2)) (* (v1 2) (v2 1)))
                  (- (* (v1 2) (v2 0)) (* (v1 0) (v2 2)))
                  (- (* (v1 0) (v2 1)) (* (v1 1) (v2 0))))) args))
(defn scalar [& args]
  {:pre [(and (every? v? args) (same-size? args))]
   :post [(number? %)]}
  (apply + (apply v* args)))
(def m (get-op-of m?))
(def m+ (m +))
(def m- (m -))
(def m* (m *))
(def md (m /))
(defn m*s [m & s]
  {:pre [(and (m? m) (every? number? s))]
   :post [(m? %)]}
  (mapv #(v*s % (apply * s)) m))
(defn m*v [m v]
  {:pre [(and (m? m) (v? v))]
   :post [(v? %)]}
  (mapv #(scalar % v) m))
(defn transpose [m]
  {:pre [(m? m)]
   :post [(m? m)]}
  (apply mapv vector m))
(defn m*m [& args]
  {:pre [(every? m? args)]
   :post [(m? %)]}
  (reduce (fn [m1 m2]
            (let [btr (transpose m2)]
              (mapv #(m*v btr %) m1))) args))

