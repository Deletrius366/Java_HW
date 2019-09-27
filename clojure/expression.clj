(defn operator [func]
  (fn [& operands]
    (fn [vars] (apply func (map (fn [x] (x vars)) operands)))))

(def constant constantly)
(defn variable [name] (fn [vars] (get vars name)))

(def add (operator +))
(def subtract (operator -))
(def multiply (operator *))
(def divide (operator (fn [x y] (/ (double x) (double y)))))
(def negate (operator -))
;(defn reduce-op [func] (operator (fn [& args] (reduce func args))))
(def min (operator clojure.core/min))
(def max (operator clojure.core/max))

(def ops {'+ add, '- subtract, '* multiply, '/ divide, 'negate negate, 'min min, 'max max})

(defn parser [expression]
  (cond
    (seq? expression) (apply (ops (first expression)) (map parser (rest expression)))
    (number? expression) (constant expression)
    (symbol? expression) (variable (str expression))))

(def parseFunction (comp parser read-string))


(defn get-property [this key]
  (if (contains? this key) (this key) (get-property (:prototype this) key)))

(defn field [key] (fn [this] (get-property this key)))

(defn function [this key & args]
  (apply (get-property this key) this args))

(defn method [key]
  (fn [this & args] (apply function this key args)))

(def toString (method :toString))
(def toStringInfix (method :toStringInfix))
(def evaluate (method :evaluate))
(def diff (method :diff))

(def operands (field :operands))

(def zero 0)
(def ConstantPrototype
  (let [number (field :value)]
    {:toString      (fn [this] (let [num (number this)] (format "%.1f" num)))
     :toStringInfix toString
     :evaluate      (fn [this _] (number this))
     :diff          (fn [this _] zero)}))

(defn Constant [number]
  {:prototype ConstantPrototype
   :value     number})

(def zero (Constant 0))

(def VariablePrototype
  (let [name (field :value)]
    {:toString      (fn [this] (name this))
     :toStringInfix toString
     :evaluate      (fn [this vars] (vars (name this)))
     :diff          (fn [this var] (if (= (name this) var) (Constant 1) zero))}))

(defn Variable [name]
  {:prototype VariablePrototype
   :value     name})

(def diffF (field :diffF))

(let [operands (field :operands)
      symbol (field :symbol)
      function (field :function)
      diff (method :diff)]
  (def OperationPrototype
    {:toString      (fn [this] (str "(" (symbol this) " " (clojure.string/join " " (mapv toString (operands this))) ")"))
     :toStringInfix (fn [this] (str "(" (toStringInfix (first (operands this))) (str " " (symbol this)) " " (clojure.string/join (str " " (symbol this) " ") (map toStringInfix (rest (operands this)))) ")"))
     :evaluate      (fn [this vars] (apply (function this) (mapv (fn [operand] (evaluate operand vars)) (operands this))))
     :diff          (fn [this var] ((diffF this) var (operands this) (mapv (fn [x] (diff x var)) (operands this)) ))
     }))
(let [operands (field :operands)
      symbol (field :symbol)]
  (def UnaryOperationPrototype
    {:prototype     OperationPrototype
     :toStringInfix (fn [this] (str (symbol this) "(" (toStringInfix (first (operands this))) ")"))
     }))

(defn Operator [proto symbol function diff]
  (let [protot {:prototype proto
               :function function
               :diffF     diff
               :symbol    symbol}]
    (fn [& args] {:prototype protot
                  :operands  (vec args) })))

(defn count=1? [vec] (== (count vec) 1))

(def Add (Operator OperationPrototype "+" + (fn [_ _ da] (apply Add da))))
(def Subtract (Operator OperationPrototype "-" - (fn [_ _ da] (apply Subtract da))))
(def Negate (Operator UnaryOperationPrototype "negate" - (fn [_ _ da] (apply Negate da))))
(def Multiply (Operator OperationPrototype "*" * (fn [var operands [& doperands]]
                                                   (last (reduce (fn [[a da] [b db]]
                                                                   [(Multiply a b) (Add (Multiply a db) (Multiply b da))])
                                                                 [operands doperands]
                                                                 ;(apply map vector [operands doperands])
                                                                     )))))
                                                     ;(reduce (fn [x y] (Add (Multiply (diff x var) y)(Multiply (diff y var) x))) operands)))))
                                                     ;(cond (count=1? operands) (Add (Multiply dfir sec) (Multiply fir dsec))
                                                     ;      (not (count=1? operands)) (diff (Multiply fir (apply Multiply operands)) var))))))
(def Divide (Operator OperationPrototype "/" (fn [& operands] (reduce (fn [x y] (/ x (double y))) operands))
                      (fn [_ [fir & operands] [dfir & doperands]]
                        (let [diff-ops (diff (apply Multiply doperands))]
                        ;(if empty? operands) (Negate (Divide (diff fir var) (Multiply fir fir)))
                        (Divide (Subtract (apply Multiply dfir operands) (Multiply diff-ops fir))
                                (apply Multiply (map #(Multiply % %) operands)))))))
                          ;(cond (count=1? operands) (Divide (Subtract (Multiply dfir sec) (Multiply dsec fir))
                          ;                                (Multiply sec sec))
                          ;      (not (count=1? operands)) (diff (Divide fir (apply Multiply operands)) var))))))
(def Pow (Operator OperationPrototype "**" (fn [a b] (Math/pow (double a) (double b))) (fn [a b] (Math/pow (double a) (double b)))))
(def Log (Operator OperationPrototype "//" (fn [a b] (/ (Math/log (double (Math/abs b))) (Math/log (double (Math/abs a))))) (fn [a] (Math/log (double a)))))
(def Sign (Operator UnaryOperationPrototype "sign" (fn [this] (Math/signum this)) (fn [& _] (Constant 0))))
(def Sqrt (Operator UnaryOperationPrototype "sqrt" (fn [x] (Math/sqrt (Math/abs x))) (fn [_ [fir & _] [dfir & _]]
                                                                                       (Multiply (Sign fir) (Divide dfir (Multiply (Constant 2) (Sqrt fir)))))))
(def Square (Operator UnaryOperationPrototype "square" (fn [x] (* x x)) (fn [_ [fir & _] [dfir & _]] (Multiply (Constant 2) dfir fir))))



(def object-ops {'+ Add '- Subtract '* Multiply '/ Divide 'negate Negate 'sqrt Sqrt 'square Square 'log Log 'pow Pow})
(defn object-parser [expression]
  (cond
    (seq? expression) (apply (object-ops (first expression)) (map object-parser (rest expression)))
    (number? expression) (Constant expression)
    (symbol? expression) (Variable (str expression))))

(def parseObject (comp object-parser read-string))

(defn -return [value tail] {:value value :tail tail})
(def -valid? boolean)
(def -value :value)
(def -tail :tail)

(defn _show [result]
  (if (-valid? result) (str "-> " (pr-str (-value result)) " | " (pr-str (apply str (-tail result))))
                       "!"))
(defn tabulate [parser inputs]
  (run! (fn [input] (printf "    %-10s %s\n" input (_show (parser input)))) inputs))

(defn _empty [value] (partial -return value))

(defn _char [p]
  (fn [[c & cs]]
    (if (and c (p c)) (-return c cs))))

(defn _map [f]
  (fn [result]
    (if (-valid? result)
      (-return (f (-value result)) (-tail result)))))

(defn _combine [f a b]
  (fn [str]
    (let [ar ((force a) str)]
      (if (-valid? ar)
        ((_map (partial f (-value ar)))
          ((force b) (-tail ar)))))))

(defn _either [a b]
  (fn [str]
    (let [ar ((force a) str)]
      (if (-valid? ar) ar ((force b) str)))))

(defn _parser [p]
  (fn [input]
    (-value ((_combine (fn [v _] v) p (_char #{\u0000})) (str input \u0000)))))

(defn +char [chars] (_char (set chars)))

(defn +char-not [chars] (_char (comp not (set chars))))

(defn +map [f parser] (comp (_map f) parser))

(def +ignore (partial +map (constantly 'ignore)))

(defn iconj [coll value]
  (if (= value 'ignore) coll (conj coll value)))

(defn +seq [& ps]
  (reduce (partial _combine iconj) (_empty []) ps))

(defn +seqf [f & ps] (+map (partial apply f) (apply +seq ps)))

(defn +seqn [n & ps] (apply +seqf (fn [& vs] (nth vs n)) ps))

(defn +or [p & ps]
  (reduce (partial _either) p ps))

(defn +opt [p]
  (+or p (_empty nil)))

(defn +star [p]
  (letfn [(rec [] (+or (+seqf cons p (delay (rec))) (_empty ())))] (rec)))

(defn +plus [p] (+seqf cons p (+star p)))

(defn +str [p] (+map (partial apply str) p))

(def *all-chars (mapv char (range 32 128)))

(def *digit (+char (apply str (filter #(Character/isDigit %) *all-chars))))

;(def *number (+map (comp Constant read-string) (+str (+plus *digit))))

(def *number (+map read-string (+str (+map flatten (+seq (+opt (+char "-")) (+plus *digit) (+opt (+seqf cons (+char ".") (+plus *digit))))))))

(def *string
  (+seqn 1 (+char "\"") (+str (+star (+char-not "\""))) (+char "\"")))

(def spaces " \t\n\r")

(def *space (+char spaces))

(def *ws (+ignore (+star *space)))

(def *null (+seqf (constantly 'null) (+char "n") (+char "u") (+char "l") (+char "l")))

(def *all-chars (mapv char (range 32 128)))

(apply str *all-chars)

(def *letter (+char (apply str (filter #(Character/isLetter %) *all-chars))))

(def *ops (+char "+-*/"))
;(tabulate *letter ["a" "A" "1" ""])

(def *identifier (+str (+seqf cons *letter (+star (+or *letter *digit)))))

;(tabulate *identifier ["a" "A" "1" "A1" "a1~" "a a"])

(def *variable (+map Variable *identifier))

(def *args (delay (+or *number *variable)))

;(def +oper (object-ops *identifier))

;(def *expr (+seqn 1 (+char "(") (*identifier) (+char ")")))

;(tabulate *expr ["(2 x)" "(2 x +)"])

(defn *array [p]
  (+seqn 1 (+char "[") p (+char "]")))

(defn *array [p]
  (+seqn 1 (+char "[") (+opt p) (+char "]")))

(defn *array [p]
  (+seqn 1 (+char "[") (+opt (+seq p (+star (+seqn 1 (+char ",") p)))) (+char "]")))

(defn *array [p]
  (+seqn 1 (+char "[") (+opt (+seqf cons p (+star (+seqn 1 (+char ",") p)))) (+char "]")))

(defn *array [p]
  (+seqn 1 (+char "[") (+opt (+seqf cons *ws p (+star (+seqn 1 *ws (+char ",") *ws p)))) *ws (+char "]")))

(defn *seq [begin p end]
  (+seqn 1 (+char begin) (+opt (+seqf cons *ws p (+star (+seqn 1 *ws (+char ",") *ws p)))) *ws (+char end)))

(defn *array [p] (*seq "[" p "]"))

(defn *member [p] (+seq *identifier *ws (+ignore (+char ":")) *ws p))

(defn *object [p] (*seq "{" (*member p) "}"))

(defn *object [p] (+map (partial reduce #(apply assoc %1 %2) {}) (*seq "{" (*member p) "}")))

(defn +parser [p]
  (fn [input]
    (-value ((_combine (fn [v _] v) p (_char #{\u0000})) (str input \u0000)))))

(def +parser _parser)

(def *symbol (+map symbol (+str (+or (+map list (+char "+-/*"))
                                     (+seqf cons (+char-not (str spaces \u0000 "()+-/*.1234567890")) (+star (+char-not (str spaces "()+-/*." \u0000))))))))

;(declare *value)

; ===== INFIX =====
(declare *add-sub)
(def *constant (+map Constant (+seqn 0 *ws *number)))
(def *variable (+map Variable (+seqn 0 *ws (+map str (+char "xyz")))))
(def *infix-expr (+seqn 1 (+char "(") *ws (delay *add-sub) *ws (+char ")")))
(defn *identifier [id real] (+map symbol (apply +seqf (constantly real) (map (fn [x] (+char x)) (clojure.string/split id #"")))))
(def unary-syms (*identifier "negate" "negate"))
;(def real {"+" "+"  "-" "-" "*" "*" "/" "/" "negate" "negate" "//" "log" "**" "pow" })
(def add-sub-syms (+or (*identifier "+" "+") (*identifier "-" "-")))
(def mul-div-syms (+or (*identifier "*" "*") (*identifier "/" "/")))
(def log-pow-syms (+or (*identifier "**" "pow") (*identifier "//" "log")))

(declare *unary)
(def *function (+seqf
                 (fn [f arg] ((object-ops f) arg))
                 unary-syms *ws
                 (+or (delay *unary) *variable *constant *infix-expr)))
(def *unary (+or *variable *constant *infix-expr *function))
(defn to-object [is-left]
  (fn [a] (let [ra (if is-left a (reverse a))]
            (reduce #(apply (object-ops (first %2))
                            (if is-left [%1 (second %2)]
                                        [(second %2) %1]))
                    (first ra) (partition 2 (rest ra))))))
(defn binary [p symb is-left]
  ;(let [symb (+or (*identifier (first sym) (real (first sym)))) (*identifier (second sym) (str (real (first sym))))]
  (+map (to-object is-left) (+seqf cons *ws p
                                   (+map (partial apply concat)
                                         (+star (+seq *ws symb *ws p))) *ws)))
(def *log-pow (binary *unary log-pow-syms false))
(def *mul-div (binary *log-pow mul-div-syms true))
(def *add-sub (binary *mul-div add-sub-syms true))
;(def *log-pow (binary *unary ["**", "//"] false))
;(def *mul-div (binary *log-pow ["*", "/"] true))
;(def *add-sub (binary *mul-div ["+", "-"] true))
(def *infix (+seqn 0 *ws *add-sub *ws))
(defn parseObjectInfix [input]
  (+parser *add-sub) input)



;(parseObjectInfix "      x+2.0    ")

;(println (toString (parseObjectInfix ("x"))))

;(println (toStringInfix (parseObjectInfix "x ** z ** y")))


;(println (evaluate (parseObjectSuffix "x   ") {"z" 1.0, "x" 1.0, "y" 0.0}))

;(def parseObjectInfix
;  (comp parseObjectInfixExpression read-string (fn [string] (str "(" string ")"))))

;(println (diff (parseObject "(+ (square (- x 3.0)) (* z (* y (sqrt -1.0))))") "z"))\
;(println (diff (Divide (Constant 1) (Constant 2) (Constant 3) (Constant 4)) "x"))
;(println (apply lazy-seq (lazy-seq [[1 2] [3 4] [5 6]])))
;(println (evaluate (diff (Multiply (Variable "x") (Constant 4) (Constant 2)) "x") {"x" 1, "y" 1, "z" 2}))