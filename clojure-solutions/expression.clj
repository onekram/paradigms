(load-file "functional.clj")
(load-file "object.clj")
(defn parse-rec [funcs constant-func variable-func]
  (letfn [(rec [arg]
            (cond (number? arg) (constant-func arg)
                  (symbol? arg) (variable-func (str arg))
                  (list? arg) (apply (funcs (first arg)) (map rec (rest arg)))))]
    #(rec (read-string %))))

(def parseFunction
  (parse-rec func-map constant variable))


(def parseObject
  (parse-rec {'+ Add
              '- Subtract
              '* Multiply
              'negate Negate
              '/ Divide
              'arithMean ArithMean
              'geomMean GeomMean
              'harmMean HarmMean} Constant Variable))
