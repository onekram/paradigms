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

(def add (get-op +))
(def subtract (get-op -))
(def divide (get-op div))
(def multiply (get-op *))
(def negate subtract)

(def func-map
  {'+ add
   '- subtract
   '* multiply
   '/ divide
   'negate negate})

(defn parse-rec [arg]
  (cond (number? arg) (constant arg)
        (symbol? arg) (variable (str arg))
        (list? arg) (apply (func-map (first arg)) (map parse-rec (rest arg)))))

(defn parseFunction [expr]
  (parse-rec (read-string expr)))
