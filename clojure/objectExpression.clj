(defn proto-get [obj key]
  (cond
    (contains? obj key) (obj key)
    (contains? obj :proto) (proto-get (obj :proto) key)
    :else nil))

(defn proto-call [this key & args]
  (apply (proto-get this key) (cons this args)))

(defn field [key]
  (fn [this] (proto-get this key)))

(defn method [key]
  (fn [this & args] (apply proto-call this key args)))

(def _operands (field :operands))
(def _symbol (field :symbol))
(def _operator (field :operator))
(def _diffn (field :diffn))
(def toString (method :toString))
(def toStringSuffix (method :toStringSuffix))
(def toStringInfix (method :toStringInfix))
(def evaluate (method :evaluate))
(def diff (method :diff))

(def ExpressionPrototype {
                          :toString       (fn [this] (str "(" (_symbol this) " " (clojure.string/join " " (map toString (_operands this))) ")"))
                          :toStringSuffix (fn [this] (str "(" (clojure.string/join " " (map toStringSuffix (_operands this))) " " (_symbol this) ")"))
                          :toStringInfix  (fn [this] (str "(" (toStringInfix (first (_operands this))) (str " " (_symbol this)) " " (clojure.string/join (str " " (_symbol this) " ") (map toStringInfix (rest (_operands this)))) ")"))
                          :evaluate       (fn [this args] (apply (_operator this) (map (fn [x] (evaluate x args)) (_operands this))))
                          :diff           (fn [this var] ((_diffn this) (_operands this) var))})
(def UnaryExpressionPrototype {
                               :proto         ExpressionPrototype
                               :toStringInfix (fn [this] (str (_symbol this) "(" (toStringInfix (first (_operands this))) ")"))
                               })
(defn GenerateExpr [proto sym op diffn]
  (fn [& args]
    (assoc proto
      :symbol sym
      :operator op
      :diffn diffn
      :operands (apply vector args))))

(defn Constant [value] {
                        :toString       (fn [this] (format "%.1f" (double value)))
                        :toStringSuffix toString
                        :toStringInfix  toString
                        :evaluate       (fn [this _] value)
                        :diff           (fn [this _] (Constant 0))
                        })
(def ZERO (Constant 0))
(def ONE (Constant 1))
(defn Variable [name] {
                       :toString       (fn [this] name)
                       :toStringSuffix toString
                       :toStringInfix  toString
                       :evaluate       (fn [this args] (get args name))
                       :diff           (fn [this var]
                                         (if (= var name) ONE ZERO))
                       })
(def Add (GenerateExpr ExpressionPrototype "+"
                       (fn [& args] (apply + args))
                       (fn [a var] (apply Add (map (fn [x] (diff x var)) a)))))
(def Subtract (GenerateExpr ExpressionPrototype "-"
                            (fn [& args] (apply - args))
                            (fn [a var] (apply Subtract (map (fn [x] (diff x var)) a)))))
(def Negate (GenerateExpr UnaryExpressionPrototype "negate"
                          (fn [& args] (apply - args))
                          (fn [a var] (apply Subtract (map (fn [x] (diff x var)) a)))))
(def Multiply (GenerateExpr ExpressionPrototype "*"
                            (fn [& args] (apply * args))
                            (fn [a var] (cond
                                          (== (count a) 2) (Add (Multiply (diff (nth a 0) var) (nth a 1)) (Multiply (diff (nth a 1) var) (nth a 0)))
                                          (> (count a) 2) (diff (Multiply (first a) (apply Multiply (rest a))) var)
                                          :else nil))))
(def Divide (GenerateExpr ExpressionPrototype "/"
                          (fn [x & args] (/ x (double (apply * args))))
                          (fn [a var] (cond
                                        (== (count a) 2) (Divide (Subtract (Multiply (diff (nth a 0) var) (nth a 1)) (Multiply (nth a 0) (diff (nth a 1) var))) (Multiply (nth a 1) (nth a 1)))
                                        (> (count a) 2) (diff (Divide (first a) (apply Multiply (rest a))) var)
                                        :else nil))))
(def Pow (GenerateExpr ExpressionPrototype "**"
                       (fn [x y] (Math/pow (double x) (double y)))
                       (fn [x y] (Math/pow x y))))
(def Log (GenerateExpr ExpressionPrototype "//"
                       (fn [x y] (/ (Math/log (double (Math/abs y))) (Math/log (double (Math/abs x)))))
                       (fn [x y] (Math/pow x y))))
(def Square (GenerateExpr UnaryExpressionPrototype "square"
                          (fn [x] (* x x))
                          (fn [a var] (diff (Multiply (first a) (first a)) var))))
(def Sqrt (GenerateExpr UnaryExpressionPrototype "sqrt"
                        (fn [x] (Math/sqrt (Math/abs x)))
                        (fn [a var] (Divide (Multiply (diff (first a) var) (Sqrt (first a))) (first a) (Constant 2)))))
(def Cosh)
(def Sinh (GenerateExpr UnaryExpressionPrototype "sinh"
                        (fn [x] (Math/sinh x))
                        (fn [a var] (Multiply (Cosh (first a)) (diff (first a) var)))))
(def Cosh (GenerateExpr UnaryExpressionPrototype "cosh"
                        (fn [x] (Math/cosh x))
                        (fn [a var] (Multiply (Sinh (first a)) (diff (first a) var)))))

(def OPS {'+      Add
          '-      Subtract
          '/      Divide
          '*      Multiply
          'negate Negate
          'sqrt   Sqrt
          'square Square
          'log    Log
          'pow    Pow
          'sinh   Sinh
          'cosh   Cosh})
(defn parseObject [expression]
  (cond
    (string? expression) (parseObject (read-string expression))
    (seq? expression) (apply (OPS (first expression)) (map parseObject (rest expression)))
    (number? expression) (Constant expression)
    (symbol? expression) (Variable (str expression))))

(defn -return [value tail] {:value value :tail tail})
(def -valid? boolean)
(def -value :value)
(def -tail :tail)
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
(def +parser _parser)
(def +ignore (partial +map (constantly 'ignore)))
(defn iconj [coll value] (if (= value 'ignore) coll (conj coll value)))
(defn +seq [& ps]
  (reduce (partial _combine iconj) (_empty []) ps))
(defn +seqf [f & ps]
  (+map (partial apply f) (apply +seq ps)))
(defn +seqn [n & ps]
  (apply +seqf (fn [& vs] (nth vs n)) ps))
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
(def *number (+map read-string (+str (+map flatten (+seq (+opt (+char "-")) (+plus *digit) (+opt (+seqf cons (+char ".") (+plus *digit))))))))
(def spaces " \t\n\r")
(def *space (+char spaces))
(def *symbol (+map symbol (+str (+or (+map list (+char "+-/*")) (+seqf cons (+char-not (str spaces \u0000 "()+-/*.1234567890")) (+star (+char-not (str spaces "()+-/*." \u0000))))))))
(def *ws (+ignore (+star *space)))

(def *value)
(defn *seq [begin p end]
  (+seqn 1 (+char begin) (+opt (+seqf cons *ws p (+star (+seqn 0 *ws p)))) *ws (+char end)))
(def *bracket (+map (fn [lst] (cons (last lst) (drop-last lst))) (*seq "(" (delay *value) ")")))
(def *value (+or *number *symbol *bracket))
(def *suffix (+parser (+seqn 0 *ws *value *ws)))
(defn parseObjectSuffix [input]
  (parseObject (*suffix input)))

(declare *add)
(def *infix_bracket (+seqn 1 (+char "(") *ws #'*add *ws (+char ")")))
(defn *word [wd pseudo] (+map symbol (apply +seqf (constantly pseudo) (map (fn [x] (+char x)) (clojure.string/split wd #"")))))
(def *unary_smb (*word "negate" "negate"))
(def *add_smb (+map symbol (+or (+map str (+char "+")) (+map str (+char "-")))))
(def *mul_smb (+map symbol (+or (+map str (+char "*")) (+map str (+char "/")))))
(def *highest_smb (+or (*word "**" "pow") (*word "//" "log")))
(def *unary (+or (+seqf list *unary_smb *ws (+or #'*unary (delay *infix_bracket) (delay *number) (delay *symbol))) (delay *infix_bracket) (delay *number) (delay *symbol)))
(defn left_binary [p symb]
  (+map (partial reduce (fn [op ls] (list (first ls) op (second ls)))) (+seqf cons *ws p (+star (+seq *ws symb *ws p)) *ws)))
(defn right_binary [p symb]
  (+map (partial reduce (fn [op ls] (list (second ls) (first ls) op))) (+seqf reverse (+seqf concat (+star (+seq *ws p *ws symb)) (+seq *ws p *ws)))))
(def *highest (right_binary *unary *highest_smb))
(def *mul (left_binary *highest *mul_smb))
(def *add (left_binary *mul *add_smb))
(def *infix (+parser (+seqn 0 *ws *add *ws)))
(defn parseObjectInfix [input]
  (parseObject (*infix input)))

(println (toStringInfix (parseObjectInfix "x ** z ** y")))

;(list (first (last ls)) (second (last ls)) op)
;(list (first ls) (last ls) op)

