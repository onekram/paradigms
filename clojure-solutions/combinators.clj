(load-file "object.clj")
(load-file "parser.clj")

(defn -show [result]
  (if (-valid? result)
    (str "-> " (pr-str (-value result)) " | " (pr-str (apply str (-tail result))))
    "!"))

(defn tabulate [parser inputs]
  (run! (fn [input] (printf "    %-10s %s\n" (pr-str input) (-show (parser input)))) inputs))

(def alphabet (apply str (map char (concat (range (int \a) (inc (int \z)))
                                           (range (int \A) (inc (int \Z)))))))

(def *letter (+char alphabet))
(def *digit (+char "0123456789"))
(def *number (+map read-string (+str (+plus *digit))))
(def *constant (+map #(Constant %) *number))


(defn +word [s] (apply +seqf (constantly (str s)) (map (partial +char) (map str (seq s)))))

(def func-map {"+" Add
               "-" Subtract
               "*" Multiply
               "/" Divide
               "negate" Negate
               "arithMean" ArithMean
               "geomMean" GeomMean
               "harmMean" HarmMean})

;(def *func (apply +or (map (comp +word first) func-map)))
(def *func (+map #(func-map %) (apply +or (map (comp +word first) func-map))) )

;(def *arg (+seq *letter (+star (+or *digit *letter))))
;(defn +str-star [& parser] (+map str +star))

(def *arg (+char "xyz"))

;(def *arg (+map #(apply str %) (+seq *letter (+str (+or *digit *letter)))))


(def *var (+map (comp str #(Variable %))  *arg))

(def *space (+char " \t\r\n"))
(def *ws (+ignore (+star *space)))

(declare *element)


(def *scope (+map (fn [[args func]] (apply func args))
                  (+seq
                    (+ignore (+char "("))
                    *ws
                    (+plus (delay *element))
                    *ws
                    *func
                    *ws
                    (+char ")")
                    )))

(def *element (+seq *ws (+or *constant *scope *var) *ws))

;(defn parseObjectInfix [expression] (str expression))

;(tabulate (+word "papa") ["papa" "mama" "p"])
;(tabulate *func ["+ " "- " "/   er" "arithMean  "])
; "])
;
;(tabulate *scope ["(10 20 +)"])
;
;(println (-show (*scope "(10 20 +)")))

;(*scope "(10 20 +)")


;(tabulate
;  *var
;  ["x"])

;(tabulate )
