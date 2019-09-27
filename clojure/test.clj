(ns test)

(gen-and-load-class 'user.UserException :extends Exception)

(defn sum [& a]
  (try
     (reduce + a)
     (catch Exception e (println "rofl")
       )))
  ;{:pre [(every? number? a)] (throw (Exception. "my exception message"))}

(println (sum 2 "a"))