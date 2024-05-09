; This file should be placed in clojure-solutions
; You may use it via (load-file "proto.clj")

(defn- proto-get
  "Returns object property respecting the prototype chain"
  ([obj key] (proto-get obj key nil))
  ([obj key default]
   (cond
     (contains? obj key) (obj key)
     (contains? obj :prototype) (proto-get (obj :prototype) key default)
     :else default)))

(defn- proto-call
  "Calls object method respecting the prototype chain"
  [this key & args]
  (apply (proto-get this key) this args))

(defn- field
  "Creates field"
  [key] (fn
          ([this] (proto-get this key))
          ([this def] (proto-get this key def))))

(defn- method
  "Creates method"
  [key] (fn [this & args] (apply proto-call this key args)))

(defn- constructor
  "Defines constructor"
  [ctor prototype]
  (fn [& args] (apply ctor {:prototype prototype} args)))


; Macros

(defn- to-symbol [& parts] (symbol (apply str parts)))

(defmacro deffield
  "Defines field"
  [name]
  `(def ~(to-symbol "__" name) (field ~(keyword name))))

(defmacro deffields
  "Defines multiple fields"
  [& names]
  `(do ~@(map (fn [name] `(deffield ~name)) names)))

(defmacro defmethod
  "Defines method"
  [name]
  `(def ~(to-symbol "_" name) (method ~(keyword name))))

(defmacro defpublicmethod
  "Defines method"
  [name]
  `(def ~name (method ~(keyword name))))

(defmacro defmethods
  "Defines multiple methods"
  [& names]
  `(do ~@(map (fn [name] `(defmethod ~name)) names)))

(defmacro defpublicmethods
  "Defines multiple methods"
  [& names]
  `(do ~@(map (fn [name] `(defpublicmethod ~name)) names)))

(defmacro defconstructor
  "Defines constructor"
  [name fields prototype]
  (let [parsed-fields (filterv (comp not #(= '& %)) fields)]
  `(do
     (deffields ~@parsed-fields)
     (defn ~name ~fields
       (assoc {:prototype ~prototype}
         ~@(mapcat (fn [f] [(keyword f) f]) parsed-fields))))))

(defmacro defclass
  "Defines class"
  [name super fields & methods]
  (let [_name (fn [suffix] (fn [class] (to-symbol class "_" suffix)))
        add-proto (_name "proto")
        add-fields (_name "fields")
        add-arguments (_name "arguments")
        abstract-methods (filterv #(= 'abstract (first %)) methods)
        non-abstract-methods (filterv (comp not #(= 'abstract (first %))) methods)
        method (fn [[_ name args body]] [(keyword name) `(fn [~'this ~@args] ~body)])
        super-proto (if (= '_ super) {} {:prototype (add-proto super)})
        prototype (apply assoc super-proto (mapcat method non-abstract-methods))
        prototype-name (add-proto name)
        fields-name (add-fields name)
        arguments-name (add-arguments name)
        super-fields (if (= '_ super) [] (eval (add-fields super)))
        super-arguments (if (= '_ super) [] (eval (add-arguments super)))
        all-arguments (into super-arguments fields)
        parsed-fields (filterv (comp not #(= '& %)) fields)
        all-fields (into super-fields parsed-fields)
        filter-access-mod (fn [type] (map rest (filter #(= type (first %)) methods)))
        private-methods (filter-access-mod 'private)
        public-methods (filter-access-mod 'public)]
    `(do
       (declare ~name)
       (declare ~@(map #(to-symbol "_" (nth % 1)) abstract-methods))
       (defmethods ~@(map first private-methods))
       (defpublicmethods ~@(map first public-methods))
       (deffields ~@parsed-fields)
       (def ~prototype-name ~prototype)
       (def ~fields-name '~all-fields)
       (def ~arguments-name '~all-arguments)
       (defconstructor ~name ~all-arguments ~prototype-name))))
