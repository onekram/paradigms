(load-file "object.clj")
(load-file "parser.clj")
(def alphabet (apply str (map char (concat (range (int \a) (inc (int \z)))
                                           (range (int \A) (inc (int \Z)))))))
(def *letter (+char alphabet))
(def *digit (+char "0123456789"))
(def *int (+str (+plus *digit)))
(def *double (+str (+seq *int (+char ".") *int)))
(def *number (+map read-string (+str (+seq (+opt (+char "-")) (+or *double *int)))))
(def *const (+map (partial Constant) *number))
(defn +word [s] (apply +seqf (constantly (str s)) (map (partial +char) (map str (seq s)))))

(def func-map {"+" Add
               "-" Subtract
               "*" Multiply
               "/" Divide
               "negate" Negate
               "arithMean" ArithMean
               "geomMean" GeomMean
               "harmMean" HarmMean})

(defn +get-func [m]
  (+map (partial m) (apply +or (map (comp +word first) m))))
(def *func (+get-func func-map))
(def *arg (+str (+plus (+or (+char "xyz") (+char "XYZ")))))
;(println (*arg "Xxyx"))
(def *var (+map (partial Variable) *arg))
(def *space (+char " \t\r\n"))
(def *ws (+ignore (+star *space)))

(defn +i-char [c]
  (+ignore (+char c)))
(declare *postfix)
(def *postfix-scope (+map (fn [[args f]] (apply f args))
                          (+seq
                   (+i-char "(")
                   (+plus (delay *postfix))
                   *func
                   *ws
                   (+i-char ")"))))
(def *postfix (+seqn 0 *ws (+or *const *postfix-scope *var) *ws))
(defn parseObjectPostfix [expr]
  (-value (*postfix expr)))



;------------------------------------------Infix -------------------------------------------------------------------

(def binary (fn [[a f b]] (f a b)))
(def unary-prefix (fn [[f a]] (f a)))
(def *base (+seqn 0 *ws (+or *var *const) *ws) )
(defn +right-assoc [func base]
  (+or (+map binary (+seq *ws base *ws func *ws (delay (+or (+right-assoc func base) base)) *ws)) base))
(defn parse-left [[acc last]]
  (if (empty? last) acc
                  (let [[op arg] (first last)] (parse-left [(op acc arg) (rest last)]))))
(defn +left-assoc [func base]
  (+map parse-left (+seq base *ws (+star (+seq func *ws base *ws)))))

(defn +unary-prefix [func base]
  (+or (+map unary-prefix (+seq *ws func *ws (delay (+unary-prefix func base)) *ws)) base))

(defn unary-postfix [[acc last]]
  (if (empty? last) acc
                    (let [op (first last)] (unary-postfix [(op acc) (rest last)]))))
(defn +unary-postfix [func base]
  (+map unary-postfix (+seq *ws base *ws (+star (+seqn 0 *ws func *ws)) *ws)))
(declare *add-subtract)
(def *infix-scope (+or
                    (+seqn 0 *ws (+i-char "(") *ws (delay *add-subtract) *ws (+i-char ")") *ws)
                    *base))
(def *unary-prefix (+unary-prefix (+get-func {"negate" Negate
                                               "cos" Cos
                                               "sin" Sin}) *infix-scope))
(def *unary-postfix (+unary-postfix (+get-func {"sinp" SinP
                                        "cosp" CosP}) *unary-prefix))
(def *sinc-cosc (+right-assoc (+get-func {"sinc" SinC
                                          "cosc" CosC}) *unary-postfix))
(def *multiply-subtract (+left-assoc (+get-func {"*" Multiply
                                                 "/" Divide}) *sinc-cosc))
(def *add-subtract (+left-assoc (+get-func {"+" Add
                                            "-" Subtract}) *multiply-subtract))

(defn parseObjectInfix [expr]
  (-value (*add-subtract expr)))
