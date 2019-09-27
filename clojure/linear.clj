(defn equalLen? [& vs] (apply == (mapv count vs)))

(defn equalRow? [& vs] (apply equalLen? (mapv first vs)))

;(defn equalRow?
;  ([] true)
;  ([mat1 & mat]
;    (let [cnt (count (first mat1))] (every? (fn [x] (== (count (first x)) cnt)) mat))))

(defn shape [a]
  (if (number? a)
    ()
    (cons (count a) (shape (first a)))))


(defn vec? [vec] (and (vector? vec) (every? number? vec)))

(defn matrix? [matrix] (and (vector? matrix) (every? vec? matrix) (apply equalLen? matrix)))


(defn tensor? [t] (or (number? t) (vec? t)
                      (and
                        (every? vector? t)
                        (equalLen? t)
                        (tensor? ((fn [tens] (reduce (partial reduce conj) tens)) t)))))

(defn can-format? [t1 t2] (let [shape1 (shape t1)]
                            (= (nthrest shape1 (- (count (shape t2)) (count shape1))) shape1)))

(defn operatorV [f]
  (fn [& args]
    {:pre [(and (every? vec? args) (apply equalLen? args)) ]}
    (apply mapv f args)))

(defn operatorM [f]
  (fn [& args]
    {:pre [(and (every? matrix? args) (apply equalLen? args) (apply equalRow? args))]}
    (apply mapv f args)))

(defn operate-s [f]
  (fn
    ([v] {:pre (vec? v)} v)
    ([v & s]
     {:pre [(and (vec? v) (every? number? s))]}
     (mapv (fn [x] (apply f x s)) v))))

(defn operate-sM [f]
  (fn
    ([v] {:pre [(matrix? v)]} v)
    ([v & s]
     {:pre [(and (matrix? v) (every? number? s))]}
     (mapv (fn [x] (apply f x s)) v))))

(def v+ (operatorV +))                                       ;
(def v- (operatorV -))                                       ;
(def v* (operatorV *))                                       ;
(defn scalar [& args] (apply + (apply v* args)))
(defn vect
  ([v] v)
  ([v1 v2]
   {:pre [(and (vector? v1) (vector? v2) (equalLen? v1 v2) (= (count v1) 3))]}
   [(- (* (v1 1) (v2 2)) (* (v1 2) (v2 1)))
    (- (* (v1 2) (v2 0)) (* (v1 0) (v2 2)))
    (- (* (v1 0) (v2 1)) (* (v1 1) (v2 0)))])
  ([v1 v2 & v]
   (apply vect (vect v1 v2) v)))
(def v*s (operate-s *))

(def m+ (operatorM v+))
(def m- (operatorM v-))
(def m* (operatorM v*))
(def m*s (operate-sM v*s))
(defn m*v [m v]
  {:pre [(and (matrix? m) (vec? v) (== (count (first m)) (count v)))]}
  (mapv (fn [row] (scalar row v)) m))
(defn transpose [mat] {:pre [(matrix? mat)]} (apply mapv vector mat))
(defn m*m
  ([mat] {:pre [(matrix? mat)]} mat)
  ([mat1 mat2]
   {:pre [(and (matrix? mat1) (matrix? mat2) (== (count (first mat1)) (count mat2)))]}
   (mapv (fn [row]
           (mapv (partial scalar row) (transpose mat2))) mat1))
  ([mat1 mat2 & mat]
   (apply m*m (m*m mat1 mat2) mat)))

(defn format-shape [t s]
  ;{:pre [(= (nthrest s (- (count s) (count (shape t)))) (shape t))]}
  (if (equalLen? (shape t) s)
    t
    (vec (repeat (first s) (format-shape t (rest s))))))

(defn operate-t [f]
  (fn
    ([t]
     {:pre [(tensor? t)]}
     (if (number? t)
       (f t)
       (mapv (operate-t f) t))
      )
    ([t1 t2]
     {:pre [(and (tensor? t1) (tensor? t2) (or (can-format? t1 t2) (can-format? t2 t1)))]}
     (let [max-shape (max-key count (shape t1) (shape t2)) t1 (format-shape t1 max-shape) t2 (format-shape t2 max-shape)]
       (if (number? t1)
         (f t1 t2)
         (mapv (operate-t f) t1 t2))
       ))
    ([t1 t2 & t]
     (apply (operate-t f) ((operate-t f) t1 t2) t))))

(def b+ (operate-t +))
(def b* (operate-t *))
(def b- (operate-t -))