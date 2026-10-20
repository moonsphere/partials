(ns partials.render
  "lein render op00 [op01 ...] — render pieces to out/*.wav, then quit."
  (:require [overtone.core :as o]
            [partials.studio :as studio]))

(defn -main [& ids]
  (studio/boot!)
  (try
    (doseq [id ids]
      (println "-->" id "rendered to" (studio/render! id)))
    (finally
      ;; Only shut down a server we booted ourselves.
      (when-not (System/getenv "SC_PORT")
        (o/kill-server))
      (shutdown-agents)
      (System/exit 0))))
