(ns ^{:opus 0 :title "Tuning" :duration 15}
  partials.pieces.op00-tuning
  "Op. 0 — Tuning. A sound check. The first eight partials of A1 enter one
  per second; then the fundamental falls silent, and is still heard."
  (:refer-clojure :exclude [abs])
  (:use overtone.core))

(def fundamental 55)

(defsynth partial-tone [freq 55 amp 0.1 pan 0 dur 10]
  (let [env (env-gen (envelope [0 1 1 0] [0.5 (- dur 2) 1.5]) :action FREE)]
    (out 0 (pan2 (* amp env (sin-osc freq)) pan))))

(defn play []
  (let [t (now)]
    (doseq [n (range 1 9)
            :let [enter (dec n)
                  leave (if (= n 1) 10 15)]]
      (at (+ t (* 1000 enter))
        (partial-tone :freq (* n fundamental)
                      :amp  (/ 0.15 n)
                      :pan  (if (odd? n) -0.3 0.3)
                      :dur  (- leave enter))))))
